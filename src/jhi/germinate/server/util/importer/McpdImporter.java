package jhi.germinate.server.util.importer;

import org.dhatim.fastexcel.reader.*;
import org.jooq.DSLContext;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import jhi.germinate.resource.enums.ImportStatus;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Biologicalstatus.*;
import static jhi.germinate.server.database.tables.Collectingsources.*;
import static jhi.germinate.server.database.tables.Countries.*;
import static jhi.germinate.server.database.tables.Entitytypes.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Storage.*;

/**
 * @author Sebastian Raubach
 */
public class McpdImporter extends AbstractImporter
{
	/** Required column headers */
	private static final String[] COLUMN_HEADERS = {"PUID", "INSTCODE", "ACCENUMB", "COLLNUMB", "COLLCODE", "COLLNAME", "COLLINSTADDRESS", "COLLMISSID", "GENUS", "SPECIES", "SPAUTHOR", "SUBTAXA", "SUBTAUTHOR", "CROPNAME", "ACCENAME", "ACQDATE", "ORIGCTY", "COLLSITE", "DECLATITUDE", "LATITUDE", "DECLONGITUDE", "LONGITUDE", "COORDUNCERT", "COORDDATUM", "GEOREFMETH", "ELEVATION", "COLLDATE", "BREDCODE", "BREDNAME", "SAMPSTAT", "ANCEST", "COLLSRC", "DONORCODE", "DONORNAME", "DONORNUMB", "OTHERNUMB", "DUPLSITE", "DUPLINSTNAME", "STORAGE", "MLSSTAT", "REMARKS"};

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	private Map<String, Integer> columnNameToIndex;
	private Set<String>          foundAccenumb = new HashSet<>();
	private Map<String, Integer> gidToId;
	private Map<String, Integer> countryCodeToId;
	private List<Integer>        validCollsrc;
	private List<Integer>        validSampstat;
	private List<Integer>        validStorage;
	private Map<String, Integer> entityTypeToId;

	public McpdImporter(File input)
	{
		super(input);
	}

	@Override
	protected void prepare()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			gidToId = context.selectFrom(GERMINATEBASE)
							 .fetchMap(GERMINATEBASE.GENERAL_IDENTIFIER, GERMINATEBASE.ID);

			countryCodeToId = context.selectFrom(COUNTRIES)
									 .fetchMap(COUNTRIES.COUNTRY_CODE3, COUNTRIES.ID);

			validCollsrc = context.selectFrom(COLLECTINGSOURCES)
								  .fetch(COLLECTINGSOURCES.ID);

			validSampstat = context.selectFrom(BIOLOGICALSTATUS)
								   .fetch(BIOLOGICALSTATUS.ID);

			validStorage = context.selectFrom(STORAGE)
								  .fetch(STORAGE.ID);

			entityTypeToId = context.selectFrom(ENTITYTYPES)
									.fetchMap(ENTITYTYPES.NAME, ENTITYTYPES.ID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			// TODO
		}
	}

	@Override
	protected void checkFile(ReadableWorkbook wb)
	{
		try
		{
			wb.getSheets()
			  .filter(s -> Objects.equals(s.getName(), "DATA"))
			  .findFirst()
			  .ifPresent(s -> {
				  try
				  {
					  // Map headers to their index
					  s.openStream()
					   .findFirst()
					   .ifPresent(this::getHeaderMapping);
					  // Check the sheet
					  s.openStream()
					   .skip(1)
					   .filter(r -> r.hasCell(columnNameToIndex.get("ACCENUMB")))
					   .forEach(this::check);
					  s.openStream()
					   .skip(1)
					   .filter(r -> r.hasCell(columnNameToIndex.get("ACCENUMB")))
					   .forEach(this::checkEntityParent);
				  }
				  catch (IOException e)
				  {
					  e.printStackTrace();
					  addImportResult(ImportStatus.GENERIC_IO_ERROR, -1, e.getMessage());
				  }
			  });

			// TODO: Check Entity parent ACCENUMB
			// TODO: Check ADDITIONAL_ATTRIBUTES
		}
		catch (NullPointerException e)
		{
			addImportResult(ImportStatus.GENERIC_MISSING_EXCEL_SHEET, -1, "'DATA' sheet not found");
		}
	}

	private void getHeaderMapping(Row r)
	{
		// Map column names to their index
		columnNameToIndex = IntStream.range(0, r.getPhysicalCellCount())
									 .boxed()
									 .collect(Collectors.toMap(r::getCellText, Function.identity()));

		// Check if all columns are there
		Arrays.stream(COLUMN_HEADERS)
			  .forEach(c -> {
				  if (!columnNameToIndex.containsKey(c))
					  addImportResult(ImportStatus.GENERIC_MISSING_COLUMN, -1, c);
			  });
	}

	private void checkEntityParent(Row r)
	{
		try
		{
			String entityParentAccenumb = r.getCellText(columnNameToIndex.get("Entity parent ACCENUMB"));
			if (!StringUtils.isEmpty(entityParentAccenumb))
			{
				if (!gidToId.containsKey(entityParentAccenumb) && !foundAccenumb.contains(entityParentAccenumb))
					addImportResult(ImportStatus.MCPD_INVALID_ENTITY_PARENT_ACCENUMB, r.getRowNum(), entityParentAccenumb);
			}
		}
		catch (NullPointerException e)
		{
			// We get here if the column isn't present. This can be the case in older versions of the template. Let this slide...
		}
	}

	private void check(Row r)
	{
		// Check the accenumb isn't a duplicate
		String accenumb = r.getCellText(columnNameToIndex.get("ACCENUMB"));
		boolean alreadyFoundInFile = foundAccenumb.contains(accenumb);
		if (StringUtils.isEmpty(accenumb))
			addImportResult(ImportStatus.MCPD_MISSING_FIELD, r.getRowNum(), "ACCENUMB");
		else
			foundAccenumb.add(accenumb);
		if (gidToId.containsKey(accenumb) || alreadyFoundInFile)
			addImportResult(ImportStatus.MCPD_DUPLICATE_ACCENUMB, r.getRowNum(), accenumb);

		// Check the genus is present
		String genus = r.getCellText(columnNameToIndex.get("GENUS"));
		if (StringUtils.isEmpty(genus))
			addImportResult(ImportStatus.MCPD_MISSING_FIELD, r.getRowNum(), "GENUS");

		// Check the date is int he correct format
		String acqdate = r.getCellText(columnNameToIndex.get("ACQDATE"));
		try
		{
			// TODO: Make sure to check "-" values
			if (!StringUtils.isEmpty(acqdate))
				sdf.parse(acqdate);
		}
		catch (ParseException e)
		{
			addImportResult(ImportStatus.MCPD_INVALID_DATE, r.getRowNum(), "ACQDATE: " + acqdate);
		}

		// Check if country is a valid 3-letter code
		String countryCode = r.getCellText(columnNameToIndex.get("ORIGCTY"));
		if (!StringUtils.isEmpty(countryCode) && !countryCodeToId.containsKey(countryCode))
			addImportResult(ImportStatus.MCPD_INVALID_COUNTRY_CODE, r.getRowNum(), countryCode);

		// Check if declatitute is a number
		String declatitude = r.getCellText(columnNameToIndex.get("DECLATITUDE"));
		if (!StringUtils.isEmpty(declatitude))
		{
			try
			{
				Double.parseDouble(declatitude);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "DECLATITUDE: " + declatitude);
			}
		}

		// Check if declongitude is a number
		String declongitude = r.getCellText(columnNameToIndex.get("DECLONGITUDE"));
		if (!StringUtils.isEmpty(declongitude))
		{
			try
			{
				Double.parseDouble(declongitude);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "DECLONGITUDE: " + declongitude);
			}
		}

		// Check if elevation is a valid number
		String elevation = r.getCellText(columnNameToIndex.get("ELEVATION"));
		if (!StringUtils.isEmpty(elevation))
		{
			try
			{
				Double.parseDouble(elevation);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "ELEVATION: " + elevation);
			}
		}

		// Check the date is int he correct format
		String colldate = r.getCellText(columnNameToIndex.get("COLLDATE"));
		try
		{
			// TODO: Make sure to check "-" values
			if (!StringUtils.isEmpty(colldate))
				sdf.parse(colldate);
		}
		catch (ParseException e)
		{
			addImportResult(ImportStatus.MCPD_INVALID_DATE, r.getRowNum(), "COLLDATE: " + colldate);
		}

		// Check SAMPSTAT
		String sampstat = r.getCellText(columnNameToIndex.get("SAMPSTAT"));
		if (!StringUtils.isEmpty(sampstat))
		{
			try
			{
				if (!validSampstat.contains(Integer.parseInt(sampstat)))
					addImportResult(ImportStatus.MCPD_INVALID_SAMPSTAT, r.getRowNum(), sampstat);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "SAMPSTAT: " + sampstat);
			}
		}

		// Check COLLSRC
		String collsrc = r.getCellText(columnNameToIndex.get("COLLSRC"));
		if (!StringUtils.isEmpty(collsrc))
		{
			try
			{
				if (!validCollsrc.contains(Integer.parseInt(collsrc)))
					addImportResult(ImportStatus.MCPD_INVALID_COLLSRC, r.getRowNum(), collsrc);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "COLLSRC: " + collsrc);
			}
		}

		// Check STORAGE
		String storage = r.getCellText(columnNameToIndex.get("STORAGE"));
		if (!StringUtils.isEmpty(storage))
		{
			try
			{
				if (!validStorage.contains(Integer.parseInt(storage)))
					addImportResult(ImportStatus.MCPD_INVALID_STORAGE, r.getRowNum(), storage);
			}
			catch (NumberFormatException e)
			{
				addImportResult(ImportStatus.MCPD_INVALID_NUMBER, r.getRowNum(), "STORAGE: " + storage);
			}
		}

		try
		{
			// Check entity type
			String entityType = r.getCellText(columnNameToIndex.get("Entity type"));
			if (!StringUtils.isEmpty(entityType))
			{
				if (!entityTypeToId.containsKey(entityType))
					addImportResult(ImportStatus.MCPD_INVALID_ENTITY_TYPE, r.getRowNum(), entityType);
			}
		}
		catch (NullPointerException e)
		{
			// We get here if the column isn't present. This can be the case in older versions of the template. Let this slide...
		}
	}

	@Override
	protected void processFile(ReadableWorkbook wb)
	{

	}
}
