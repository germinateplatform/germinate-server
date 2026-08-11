package jhi.germinate.server.resource.variables;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.Map;

import static jhi.germinate.server.database.codegen.tables.Methods.METHODS;
import static jhi.germinate.server.database.codegen.tables.Scales.SCALES;
import static jhi.germinate.server.database.codegen.tables.Traits.TRAITS;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;

@Path("variable")
@Secured
@PermitAll
public class VariableResource extends ContextResource
{
	@Path("/methodclass/count")
	@GET
	public Map<MethodsMethodClass, Integer> getVariableMethodClassCounts()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			AggregateFunction<Integer> count = DSL.count();

			return context.select(METHODS.METHOD_CLASS, count)
			              .from(VARIABLES)
			              .leftJoin(METHODS).on(VARIABLES.METHOD_ID.eq(METHODS.ID))
			              .groupBy(METHODS.METHOD_CLASS)
			              .fetchMap(METHODS.METHOD_CLASS, count);
		}
	}

	@Path("/scaledatatype/count")
	@GET
	public Map<ScalesDatatype, Integer> getVariableScaleDataTypeCounts()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			AggregateFunction<Integer> count = DSL.count();

			return context.select(SCALES.DATATYPE, count)
			              .from(VARIABLES)
			              .leftJoin(SCALES).on(VARIABLES.SCALE_ID.eq(SCALES.ID))
			              .groupBy(SCALES.DATATYPE)
			              .fetchMap(SCALES.DATATYPE, count);
		}
	}

	@Path("/traitclass/count")
	@GET
	public Map<TraitsTraitClass, Integer> getVariableTraitClassCounts()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			AggregateFunction<Integer> count = DSL.count();

			return context.select(TRAITS.TRAIT_CLASS, count)
			              .from(VARIABLES)
			              .leftJoin(TRAITS).on(VARIABLES.TRAIT_ID.eq(TRAITS.ID))
			              .groupBy(TRAITS.TRAIT_CLASS)
			              .fetchMap(TRAITS.TRAIT_CLASS, count);
		}
	}
}
