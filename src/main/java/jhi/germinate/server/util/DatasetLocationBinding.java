package jhi.germinate.server.util;

import com.google.gson.*;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.Objects;

import jhi.germinate.resource.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetLocationBinding implements Binding<Object, DatasetLocation[]>
{
	@Override
	public Converter<Object, DatasetLocation[]> converter()
	{
		Gson gson = new Gson();
		return new Converter<>()
		{
			@Override
			public DatasetLocation[] from(Object o)
			{
				return o == null ? null : gson.fromJson(Objects.toString(o), DatasetLocation[].class);
			}

			@Override
			public Object to(DatasetLocation[] o)
			{
				return o == null ? null : gson.toJson(o);
			}

			@Override
			public Class<Object> fromType()
			{
				return Object.class;
			}

			@Override
			public Class<DatasetLocation[]> toType()
			{
				return DatasetLocation[].class;
			}
		};
	}

	@Override
	public void sql(BindingSQLContext<DatasetLocation[]> ctx)
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
	public void register(BindingRegisterContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
	}

	@Override
	public void set(BindingSetStatementContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
	}

	@Override
	public void set(BindingSetSQLOutputContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void get(BindingGetResultSetContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetStatementContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetSQLInputContext<DatasetLocation[]> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}
}
