package jhi.germinate.server.resource.fileresource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.records.FileresourcesRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.nio.channels.*;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Datasetfileresources.*;
import static jhi.germinate.server.database.codegen.tables.Fileresources.*;

@Path("fileresource/{fileResourceId}/stream")
public class FileResourceStreamerResource extends ContextResource
{
	private final int CHUNK_SIZE = 1024 * 1024 * 2; // 2 MB chunks

	@PathParam("fileResourceId")
	Integer fileResourceId;

	@HEAD
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	@Secured
	@PermitAll
	public Response getFileResourceStreamHead(@HeaderParam("Range") String range)
		throws IOException, SQLException
	{
		return this.stream(range, true);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	@Secured
	@PermitAll
	public Response getFileResourceStreamBody(@HeaderParam("Range") String range)
		throws IOException, SQLException
	{
		return this.stream(range, false);
	}

	private Response stream(String range, boolean isHead)
		throws SQLException, IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> datasetIds = DatasetTableResource.getDatasetIdsForUser(req, resp, userDetails, null);

		if (fileResourceId == null)
		{
			return Response.status(Response.Status.BAD_REQUEST)
				.build();
		}

		try (Connection conn = Database.getConnection())
		{
			// Check whether there isn't a dataset linked to this resource OR whether the user has access to that dataset
			Condition cond = DSL.notExists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID)))
								.or(DSL.exists(DSL.selectOne().from(DATASETFILERESOURCES).where(DATASETFILERESOURCES.FILERESOURCE_ID.eq(FILERESOURCES.ID).and(DATASETFILERESOURCES.DATASET_ID.in(datasetIds)))));
			DSLContext context = Database.getContext(conn);
			FileresourcesRecord record = context.selectFrom(FILERESOURCES)
												.where(FILERESOURCES.ID.eq(fileResourceId).and(cond))
												.fetchAny();

			if (record == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			File resultFile = ResourceUtils.getFromExternal(resp, record.getPath(), "data", "download", Integer.toString(record.getFileresourcetypeId()));

			if (!resultFile.exists() || !resultFile.isFile())
				return Response.status(Response.Status.NOT_FOUND).build();

			String filename = resultFile.getName();

			String type = java.nio.file.Files.probeContentType(resultFile.toPath());
			filename = record.getName().replaceAll("[^a-zA-Z0-9-_.]", "-") + filename.substring(filename.lastIndexOf("."));

			if (StringUtils.isEmpty(type))
				type = "*/*";


			if (isHead)
			{
				return Response.ok()
							   .status(Response.Status.PARTIAL_CONTENT)
							   .header(HttpHeaders.CONTENT_LENGTH, resultFile.length())
							   .header("Accept-Ranges", "bytes")
							   .build();
			} else {
				return this.buildStream(resultFile, range);
			}
		}
	}

	private Response buildStream(final File asset, final String range)
		throws IOException
	{
		// range not requested: firefox does not send range headers
		if (range == null)
		{
			StreamingOutput streamer = output -> {
				try (FileInputStream fis = new FileInputStream(asset);
					 FileChannel inputChannel = fis.getChannel();
					 WritableByteChannel outputChannel = Channels.newChannel(output))
				{

					inputChannel.transferTo(0, inputChannel.size(), outputChannel);
				}
				catch (IOException io)
				{
					Logger.getLogger("").info(io.getMessage());
					io.printStackTrace();
				}
			};

			return Response.ok(streamer)
						   .status(Response.Status.OK)
						   .header(HttpHeaders.CONTENT_LENGTH, asset.length())
						   .build();
		}

		String[] ranges = range.split("=")[1].split("-");

		int from = Integer.parseInt(ranges[0]);

		// Chunk media if the range upper bound is unspecified
		int to = CHUNK_SIZE + from;

		if (to >= asset.length())
		{
			to = (int) (asset.length() - 1);
		}

		// uncomment to let the client decide the upper bound
		// we want to send 2 MB chunks all the time
		//if ( ranges.length == 2 ) {
		//    to = Integer.parseInt( ranges[1] );
		//}

		final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());

		final RandomAccessFile raf = new RandomAccessFile(asset, "r");
		raf.seek(from);
		final int len = to - from + 1;
		final MediaStreamer mediaStreamer = new MediaStreamer(len, raf);
		return Response.ok(mediaStreamer)
					   .status(Response.Status.PARTIAL_CONTENT)
					   .header("Accept-Ranges", "bytes")
					   .header("Content-Range", responseRange)
					   .header(HttpHeaders.CONTENT_LENGTH, mediaStreamer.getLenth())
					   .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()))
					   .build();
	}
}
