package jhi.germinate.server.resource.links;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.germinate.resource.LinkRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.ViewTableLinks;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Compounds.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Markers.*;
import static jhi.germinate.server.database.tables.Phenotypes.*;
import static jhi.germinate.server.database.tables.ViewTableLinks.*;

/**
 * @author Sebastian Raubach
 */
public class LinksTableResource extends BaseServerResource
{

	@Post("json")
	public List<ViewTableLinks> getJson(LinkRequest request)
	{
		if (request == null || StringUtils.isEmpty(request.getTargetTable()) || request.getForeignId() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "'targetTable' and 'foreignId' must be specified.");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			List<ViewTableLinks> result = context.selectFrom(VIEW_TABLE_LINKS)
												 .where(VIEW_TABLE_LINKS.LINKTYPE_TARGET_TABLE.eq(request.getTargetTable()))
												 .and(VIEW_TABLE_LINKS.LINK_FOREIGN_ID.eq(request.getForeignId())
																					  .or(VIEW_TABLE_LINKS.LINK_FOREIGN_ID.isNull()
																														  .and(VIEW_TABLE_LINKS.PLACEHOLDER.isNotNull())))
												 .fetchInto(ViewTableLinks.class);

			result.stream()
				  .filter(l -> l.getLinkForeignId() == null && !StringUtils.isEmpty(l.getPlaceholder()))
				  .forEach(l -> {
					  String hyperlink = l.getHyperlink();
					  String placeholder = l.getPlaceholder();
					  String targetTable = l.getLinktypeTargetTable();
					  String targetColumn = l.getLinktypeTargetColumn();

					  if (StringUtils.isEmpty(hyperlink) || StringUtils.isEmpty(placeholder) || StringUtils.isEmpty(targetTable) || StringUtils.isEmpty(targetColumn))
						  return;

					  String value = "";

					  switch (targetTable)
					  {
						  case "germinatebase":
							  value = context.select(DSL.field(targetColumn).cast(String.class))
											 .from(GERMINATEBASE)
											 .where(GERMINATEBASE.ID.eq(request.getForeignId()))
											 .fetchAnyInto(String.class);
							  break;
						  case "compounds":
							  value = context.select(DSL.field(targetColumn).cast(String.class))
											 .from(COMPOUNDS)
											 .where(COMPOUNDS.ID.eq(request.getForeignId()))
											 .fetchAnyInto(String.class);
							  break;
						  case "markers":
							  value = context.select(DSL.field(targetColumn).cast(String.class))
											 .from(MARKERS)
											 .where(MARKERS.ID.eq(request.getForeignId()))
											 .fetchAnyInto(String.class);
							  break;
						  case "phenotypes":
							  value = context.select(DSL.field(targetColumn).cast(String.class))
											 .from(PHENOTYPES)
											 .where(PHENOTYPES.ID.eq(request.getForeignId()))
											 .fetchAnyInto(String.class);
							  break;
					  }

					  if (StringUtils.isEmpty(value))
					  	l.setHyperlink(null);
					  else
					  	l.setHyperlink(hyperlink.replace(placeholder, value));
				  });

			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
