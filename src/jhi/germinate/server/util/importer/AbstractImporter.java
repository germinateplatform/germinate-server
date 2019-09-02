package jhi.germinate.server.util.importer;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jhi.germinate.resource.ImportResult;
import jhi.germinate.resource.enums.ImportStatus;

/**
 * @author Sebastian Raubach
 */
public abstract class AbstractImporter
{
	private static Map<String, List<ImportResult>> CONCURRENT_STATUS = new ConcurrentHashMap<>();

	private File                            input;
	private String                          uuid     = UUID.randomUUID().toString();
	private Map<ImportStatus, ImportResult> errorMap = new HashMap<>();

	public AbstractImporter(File input)
	{
		this.input = input;
	}

	public static synchronized List<ImportResult> getStatus(String uuid)
	{
		if (CONCURRENT_STATUS.containsKey(uuid))
		{
			List<ImportResult> result = CONCURRENT_STATUS.get(uuid);
			// If the key is there return the value (null or finished result)
			return result.size() == 0 ? null : result;
		}
		else
		{
			// Else throw a not found
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}

	public String run()
	{
		// Put null to indicate that the UUID is valid, but not finished
		CONCURRENT_STATUS.put(uuid, new ArrayList<>());
		new Thread(() -> {
			try (ReadableWorkbook wb = new ReadableWorkbook(input))
			{
				prepare();

				checkFile(wb);

//				if (errorMap.size() < 1)
//					processFile(wb);
//
//				input.delete();
				// Put the result
				CONCURRENT_STATUS.put(uuid, getImportResult());
			}
			catch (IOException e)
			{
				e.printStackTrace();
				CONCURRENT_STATUS.put(uuid, Collections.singletonList(new ImportResult(ImportStatus.GENERIC_IO_ERROR, -1, e.getMessage())));
			}
		}).start();

		return uuid;
	}

	protected void addImportResult(ImportStatus status, int rowIndex, String message)
	{
		if (!errorMap.containsKey(status))
			errorMap.put(status, new ImportResult(status, rowIndex, message));
	}

	private List<ImportResult> getImportResult()
	{
		return new ArrayList<>(errorMap.values());
	}

	protected abstract void prepare();

	protected abstract void checkFile(ReadableWorkbook wb);

	protected abstract void processFile(ReadableWorkbook wb);
}
