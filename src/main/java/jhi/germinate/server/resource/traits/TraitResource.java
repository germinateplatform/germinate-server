package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableTraits;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Methods.METHODS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Scales.SCALES;
import static jhi.germinate.server.database.codegen.tables.Traits.TRAITS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;
import static jhi.germinate.server.database.codegen.tables.ViewTableTraits.VIEW_TABLE_TRAITS;

@Path("trait/{traitId:\\d+}")
public class TraitResource extends ContextResource
{
	@PathParam("traitId")
	private Integer traitId;

	@GET
	@Path("/values")
	@PermitAll
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDistinctTraitValues()
			throws SQLException
	{
		if (traitId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Integer> datasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "trials", true);

			return context.selectDistinct(PHENOTYPEDATA.PHENOTYPE_VALUE).from(PHENOTYPEDATA)
						  .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
						  .where(TRIALSETUP.DATASET_ID.in(datasets))
						  .and(PHENOTYPEDATA.VARIABLE_ID.eq(traitId))
						  .orderBy(PHENOTYPEDATA.PHENOTYPE_VALUE)
						  .fetchInto(String.class);
		}
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public ViewTableTraits patchTrait(ViewTableTraits variable)
			throws SQLException
	{
		if (variable == null || variable.getVariableId() == null || !Objects.equals(variable.getVariableId(), traitId))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			VariablesRecord record = context.selectFrom(VARIABLES).where(VARIABLES.ID.eq(traitId))
											.fetchAny();

			if (record == null)
				throw new NotFoundException();

			if (variable.getMethodId() != null)
			{
				if (!Objects.equals(variable.getMethodId(), record.getMethodId()))
				{
					// The method has been changed completely, so just update the ID reference
					record.setMethodId(variable.getMethodId());
				}
				else
				{
					MethodsRecord method = context.selectFrom(METHODS).where(METHODS.ID.eq(variable.getMethodId())).fetchAny();

					if (method != null)
					{
						method.setName(variable.getMethodName());
						method.setDescription(variable.getMethodDescription());
						method.setIsTimeseries(variable.getMethodIsTimeseries());
						method.setSetsize(variable.getMethodSetSize());
						try
						{
							method.setMethodClass(MethodsMethodClass.valueOf(variable.getMethodClass().name()));
						}
						catch (Exception e)
						{
							// Ignore
						}
						method.store();
					}
				}
			}

			if (variable.getScaleId() != null)
			{
				if (!Objects.equals(variable.getScaleId(), record.getScaleId()))
				{
					// The scale has been changed completely, so just update the ID reference
					record.setScaleId(variable.getScaleId());
				}
				else
				{
					ScalesRecord scale = context.selectFrom(SCALES).where(SCALES.ID.eq(variable.getScaleId())).fetchAny();

					if (scale != null)
					{
						scale.setName(variable.getScaleName());
						scale.setDescription(variable.getScaleDescription());
						scale.setUnit(variable.getScaleUnit());
						scale.store();
					}
				}
			}

			if (variable.getTraitId() != null)
			{
				if (!Objects.equals(variable.getTraitId(), record.getTraitId()))
				{
					// The trait has been changed completely, so just update the ID reference
					record.setTraitId(variable.getTraitId());
				}
				else
				{
					TraitsRecord trait = context.selectFrom(TRAITS).where(TRAITS.ID.eq(variable.getTraitId())).fetchAny();

					if (trait != null)
					{
						trait.setName(variable.getTraitName());
						trait.setDescription(variable.getTraitDescription());
						trait.setAbbreviation(variable.getTraitAbbreviation());
						trait.setSynonyms(variable.getTraitSynonyms());
						trait.setTraitcategoryId(variable.getTraitCategoryId());

						try
						{
							trait.setTraitClass(TraitsTraitClass.valueOf(variable.getTraitClass().name()));
						}
						catch (Exception e)
						{
							// Ignore
						}

						trait.store();
					}
				}
			}

			if (record.modified())
			{
				// Store any changes to method, trait or scale
				record.store(VARIABLES.METHOD_ID, VARIABLES.TRAIT_ID, VARIABLES.SCALE_ID);
			}

			// Return the updated record
			return context.selectFrom(VIEW_TABLE_TRAITS)
						  .where(VIEW_TABLE_TRAITS.VARIABLE_ID.eq(variable.getVariableId()))
						  .fetchAnyInto(ViewTableTraits.class);
		}
	}
}
