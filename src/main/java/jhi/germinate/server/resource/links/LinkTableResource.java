package jhi.germinate.server.resource.links;

import jhi.germinate.resource.LinkRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableLinks;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Compounds.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableLinks.*;

@Path("link/table")
@Secured
@PermitAll
public class LinkTableResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewTableLinks> postLinkTable(LinkRequest request)
		throws IOException, SQLException
	{
		if (request == null || StringUtils.isEmpty(request.getTargetTable()) || request.getForeignId() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "'targetTable' and 'foreignId' must be specified.");
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
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
	}
}
