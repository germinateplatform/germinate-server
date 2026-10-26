package jhi.germinate.server.resource.genesys;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.GermplasminstitutionsType;
import jhi.germinate.server.database.pojo.GermplasmInstitution;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.util.*;
import lombok.*;
import lombok.experimental.Accessors;
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
	@Produces(MediaType.APPLICATION_JSON)
	public boolean getGenesysStatus()
	{
		return GenesysClient.isAvailable();
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
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			if (!GenesysClient.isAvailable())
			{
				throw new ServiceUnavailableException();
			}

			DSLContext context = Database.getContext(conn);

			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, new ArrayList<>(), false, null);

			from.having(DSL.field(GERMPLASM_ID, Integer.class).in(details.getGermplasmIds()));

			List<ViewTableGermplasm> result = from.fetch()
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
				throw new NotFoundException();

			GenesysResponse response = GenesysClient.postGermplasmRequest(req);

			if (!StringUtils.isEmpty(response.getUuid()))
				return Response.ok(response.getUuid()).build();
			else
				return Response.status(Response.Status.BAD_REQUEST).entity(response.getMissingItems().stream().map(item -> mapping.get(item.doi + "|" + item.genus + "|" + item.getAcceNumb() + "|" + item.getInstCode())).toList()).build();
		}
		catch (ServiceUnavailableException e)
		{
			throw new ServiceUnavailableException();
		}
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class GenesysRequest
	{
		private List<GenesysRequestItem> items;
		private GenesysRequestUser       pii;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class GenesysRequestItem
	{
		private String instCode;
		private String acceNumb;
		private String doi;
		private String genus;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class GenesysRequestUser
	{
		private String pid;
		private String name;
		private String email;
	}
}
