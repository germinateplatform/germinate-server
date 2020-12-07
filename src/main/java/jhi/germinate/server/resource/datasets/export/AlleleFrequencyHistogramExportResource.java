package jhi.germinate.server.resource.datasets.export;

import jhi.flapjack.io.binning.MakeHistogram;
import jhi.germinate.resource.SubsettedGenotypeDatasetRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class AlleleFrequencyHistogramExportResource extends BaseServerResource
{

	@Post
	public FileRepresentation postJson(SubsettedGenotypeDatasetRequest request)
	{
		if (request == null || CollectionUtils.isEmpty(request.getDatasetIds()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		List<Integer> availableDatasets = DatasetTableResource.getDatasetIdsForUser(getRequest(), getResponse());

		List<Integer> datasetIds = new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		datasetIds.retainAll(availableDatasets);

		if (datasetIds.size() < 1)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		FileRepresentation representation;
		try (DSLContext context = Database.getContext())
		{
			List<ViewTableDatasets> ds = DatasetTableResource.getDatasetForId(datasetIds.get(0), getRequest(), getResponse(), true);

			if (CollectionUtils.isEmpty(ds))
				return null;

			Set<String> germplasmNames = GenotypeExportResource.getGermplasmNames(context, request);
			Set<String> markerNames = GenotypeExportResource.getMarkerNames(context, request);

			// Get the source file
			File source = getFromExternal(ds.get(0).getSourceFile(), "data", "allelefreq");

			// Create all temporary files
			File target = createTempFile("allelefreq-" + CollectionUtils.join(datasetIds, "-"), ".txt");

			int[] counts = new TabFileSubsetter().run(source, target, germplasmNames, markerNames, null);

			File histogram = createTempFile("allelefreq-histogram-" + CollectionUtils.join(datasetIds, "-"), ".txt");
			if (counts[0] == 0 || counts[1] == 0)
			{
				// If either dimension is empty (no markers or no germplasm), then just create a dummy histogram file, because otherwise
				// the Flapjack code will fail with a "divide by zero" error.
				Files.write(histogram.toPath(), Collections.singletonList("position\tcount"));
			}
			else
			{
				new MakeHistogram(200, target.getAbsolutePath(), histogram.getAbsolutePath()).createHistogram();
			}

			representation = new FileRepresentation(histogram, MediaType.TEXT_PLAIN);
			representation.setSize(histogram.length());
			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename(histogram.getName());
			disp.setSize(histogram.length());
			representation.setDisposition(disp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
