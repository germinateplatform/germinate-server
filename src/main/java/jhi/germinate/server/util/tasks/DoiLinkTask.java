package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Mcpd;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.DOI;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Links.LINKS;
import static jhi.germinate.server.database.codegen.tables.Linktypes.LINKTYPES;
import static jhi.germinate.server.database.codegen.tables.Mcpd.MCPD;

public class DoiLinkTask implements Runnable
{
	public DoiLinkTask()
	{
	}

	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Create the DOI links type if it doesn't exist
			LinktypesRecord doiLinkType = context.selectFrom(LINKTYPES)
												 .where(LINKTYPES.TARGET_TABLE.eq("germinatebase"))
												 .and(LINKTYPES.DESCRIPTION.eq("DOI"))
												 .fetchAny();
			if (doiLinkType == null)
			{
				doiLinkType = context.newRecord(LINKTYPES);
				doiLinkType.setTargetTable("germinatebase");
				doiLinkType.setDescription("DOI");
				doiLinkType.store();
			}

			// Get those MCPD entries that have a PUID and DON'T have a DOI link
			List<Mcpd> mcpds = context.selectFrom(MCPD)
									  .where(MCPD.PUID.isNotNull())
									  .andNotExists(DSL.selectOne().from(LINKS)
													   .leftJoin(LINKTYPES).on(LINKTYPES.ID.eq(LINKS.LINKTYPE_ID))
													   .where(LINKS.FOREIGN_ID.eq(MCPD.GERMINATEBASE_ID))
													   .and(LINKTYPES.TARGET_TABLE.eq("germinatebase"))
													   .and(LINKTYPES.DESCRIPTION.eq("DOI")))
									  .fetchInto(Mcpd.class);

			for (Mcpd m : mcpds)
			{
				try
				{
					// Check if this is a DOI
					DOI doi = DOI.create(m.getPuid());
					String asUrl = doi.toURI().toString();

					// Create a new links entry
					LinksRecord linksRecord = context.newRecord(LINKS);
					linksRecord.setLinktypeId(doiLinkType.getId());
					linksRecord.setHyperlink(asUrl);
					linksRecord.setDescription("DOI");
					linksRecord.store();
				}
				catch (DOI.MalformedDOIException e)
				{
					// Do nothing here. It's not a DOI.
				}
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger("").severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
