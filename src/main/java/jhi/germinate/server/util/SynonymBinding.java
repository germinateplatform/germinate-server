package jhi.germinate.server.util;

import com.google.gson.*;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.Objects;

/**
 * @author Sebastian Raubach
 */
public class SynonymBinding implements Binding<Object, JsonArray>
{
	@Override
	public Converter<Object, JsonArray> converter()
	{
		Gson gson = new Gson();
		return new Converter<>()
		{
			@Override
			public JsonArray from(Object o)
			{
				return o == null ? null : gson.fromJson(Objects.toString(o), JsonArray.class);
			}

			@Override
			public Object to(JsonArray o)
			{
				return o == null ? null : gson.toJson(o);
			}

			@Override
			public Class<Object> fromType()
			{
				return Object.class;
			}

			@Override
			public Class<JsonArray> toType()
			{
				return JsonArray.class;
			}
		};
	}

	@Override
	public void sql(BindingSQLContext<JsonArray> ctx)
		throws SQLException
	{
		// Depending on how you generate your SQL, you may need to explicitly distinguish
		// between jOOQ generating bind variables or inlined literals.
		if (ctx.render().paramType() == ParamType.INLINED)
			ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("");
		else
			ctx.render().sql("?");
	}

	@Override
	public void register(BindingRegisterContext<JsonArray> ctx)
		throws SQLException
	{
		ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
	}

	@Override
	public void set(BindingSetStatementContext<JsonArray> ctx)
		throws SQLException
	{
		ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
	}

	@Override
	public void set(BindingSetSQLOutputContext<JsonArray> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void get(BindingGetResultSetContext<JsonArray> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetStatementContext<JsonArray> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetSQLInputContext<JsonArray> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}
}
