package jhi.germinate.server.resource.importers;

import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.util.List;

import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.codegen.enums.DataImportJobsDatatype;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;

/**
 * @author Sebastian Raubach
 */
public class DataImporterResource extends BaseServerResource
{
	public static final String PARAM_IS_UPDATE = "update";
	public static final String PARAM_DATA_TYPE = "type";

	private boolean                isUpdate = false;
	private DataImportJobsDatatype dataType;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		String isUpdateString = getQueryValue(PARAM_IS_UPDATE);
		if (!StringUtils.isEmpty(isUpdateString))
			this.isUpdate = Boolean.parseBoolean(isUpdateString);

		try
		{
			this.dataType = DataImportJobsDatatype.valueOf(getQueryValue(PARAM_DATA_TYPE));
		}
		catch (Exception e)
		{
			this.dataType = null;
		}
	}

	@Post
	@MinUserType(UserType.DATA_CURATOR)
	public List<AsyncExportResult> accept(Representation entity)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		return DataImportRunner.checkData(dataType, userDetails, entity, isUpdate);
	}
}
