package jhi.germinate.server.resource.genesys;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.GermplasminstitutionsType;
import jhi.germinate.server.database.pojo.GermplasmInstitution;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("genesys/germplasm")
@Secured
@PermitAll
public class GenesysGermplasmResource extends GermplasmBaseResource
{
	@GET
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGenesysStatus()
	{
		return Response.ok(GenesysClient.isAvailable()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postGermplasmList(GenesysRequestDetails details)
			throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		ViewUserDetails user;

		if (userDetails != null && userDetails.getId() != -1000)
		{
			user = GatekeeperClient.getUser(userDetails.getId());
		}
		else
		{
			user = new ViewUserDetails();
			user.setName(details.getName());
			user.setEmailAddress(details.getEmail());
		}

		if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getEmailAddress()) || CollectionUtils.isEmpty(details.getGermplasmIds()))
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			if (!GenesysClient.isAvailable())
			{
				throw new ServiceUnavailableException();
			}

			DSLContext context = Database.getContext(conn);

			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, new ArrayList<>(), null);

			from.having(DSL.field(GERMPLASM_ID, Integer.class).in(details.getGermplasmIds()));

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableGermplasm.class);

			Map<String, Integer> mapping = new HashMap<>();

			// Build the Genesys request
			GenesysRequest req = new GenesysRequest();
			req.setPii(new GenesysRequestUser()
					.setName(user.getName())
					.setEmail(user.getEmailAddress()));
			req.setItems(result.stream().map(g -> {
				GenesysRequestItem item = new GenesysRequestItem()
						.setAcceNumb(g.getGermplasmName())
						.setGenus(g.getGenus())
						.setDoi(g.getGermplasmPuid());
				if (!CollectionUtils.isEmpty(g.getInstitutions()))
				{
					for (GermplasmInstitution inst : g.getInstitutions())
					{
						if (inst.getType() == GermplasminstitutionsType.maintenance && !StringUtils.isEmpty(inst.getCode()))
						{
							item.setInstCode(inst.getCode());
							break;
						}
					}
				}

				mapping.put(item.doi + "|" + item.genus + "|" + item.getAcceNumb() + "|" + item.getInstCode(), g.getGermplasmId());

				return item;
			}).collect(Collectors.toList()));

			if (CollectionUtils.isEmpty(req.getItems()))
				return Response.status(Response.Status.NOT_FOUND).build();

			GenesysResponse response = GenesysClient.postGermplasmRequest(req);

			if (!StringUtils.isEmpty(response.getUuid()))
				return Response.ok(response.getUuid()).build();
			else
				return Response.status(Response.Status.BAD_REQUEST).entity(response.getMissingItems().stream().map(item -> mapping.get(item.doi + "|" + item.genus + "|" + item.getAcceNumb() + "|" + item.getInstCode())).toList()).build();
		}
		catch (ServiceUnavailableException e)
		{
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	public static class GenesysRequest
	{
		private List<GenesysRequestItem> items;
		private GenesysRequestUser       pii;

		public GenesysRequest()
		{
		}

		public List<GenesysRequestItem> getItems()
		{
			return items;
		}

		public GenesysRequest setItems(List<GenesysRequestItem> items)
		{
			this.items = items;
			return this;
		}

		public GenesysRequestUser getPii()
		{
			return pii;
		}

		public GenesysRequest setPii(GenesysRequestUser pii)
		{
			this.pii = pii;
			return this;
		}

		@Override
		public String toString()
		{
			return "GenesysRequest{" +
					"items=" + items +
					", pii=" + pii +
					'}';
		}
	}

	public static class GenesysRequestItem
	{
		private String instCode;
		private String acceNumb;
		private String doi;
		private String genus;

		public GenesysRequestItem()
		{
		}

		public String getInstCode()
		{
			return instCode;
		}

		public GenesysRequestItem setInstCode(String instCode)
		{
			this.instCode = instCode;
			return this;
		}

		public String getAcceNumb()
		{
			return acceNumb;
		}

		public GenesysRequestItem setAcceNumb(String acceNumb)
		{
			this.acceNumb = acceNumb;
			return this;
		}

		public String getDoi()
		{
			return doi;
		}

		public GenesysRequestItem setDoi(String doi)
		{
			this.doi = doi;
			return this;
		}

		public String getGenus()
		{
			return genus;
		}

		public GenesysRequestItem setGenus(String genus)
		{
			this.genus = genus;
			return this;
		}

		@Override
		public String toString()
		{
			return "GenesysRequestItem{" +
					"instCode='" + instCode + '\'' +
					", acceNumb='" + acceNumb + '\'' +
					", doi='" + doi + '\'' +
					", genus='" + genus + '\'' +
					'}';
		}
	}

	public static class GenesysRequestUser
	{
		private String pid;
		private String name;
		private String email;

		public GenesysRequestUser()
		{
		}

		public String getPid()
		{
			return pid;
		}

		public GenesysRequestUser setPid(String pid)
		{
			this.pid = pid;
			return this;
		}

		public String getName()
		{
			return name;
		}

		public GenesysRequestUser setName(String name)
		{
			this.name = name;
			return this;
		}

		public String getEmail()
		{
			return email;
		}

		public GenesysRequestUser setEmail(String email)
		{
			this.email = email;
			return this;
		}

		@Override
		public String toString()
		{
			return "GenesysRequestUser{" +
					"pid='" + pid + '\'' +
					", name='" + name + '\'' +
					", email='" + email + '\'' +
					'}';
		}
	}
}
