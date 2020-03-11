package jhi.germinate.server.util;

import com.google.gson.Gson;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.Objects;

import jhi.germinate.resource.ImportResult;

/**
 * @author Sebastian Raubach
 */
public class ImportResultBinding implements Binding<Object, ImportResult[]>
{
	@Override
	public Converter<Object, ImportResult[]> converter()
	{
		Gson gson = new Gson();
		return new Converter<Object, ImportResult[]>()
		{
			@Override
			public ImportResult[] from(Object o)
			{
				return o == null ? null : gson.fromJson(Objects.toString(o), ImportResult[].class);
			}

			@Override
			public Object to(ImportResult[] o)
			{
				return o == null ? null : gson.toJson(o);
			}

			@Override
			public Class<Object> fromType()
			{
				return Object.class;
			}

			@Override
			public Class<ImportResult[]> toType()
			{
				return ImportResult[].class;
			}
		};
	}

	@Override
	public void sql(BindingSQLContext<ImportResult[]> ctx)
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
	public void register(BindingRegisterContext<ImportResult[]> ctx)
		throws SQLException
	{
		ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
	}

	@Override
	public void set(BindingSetStatementContext<ImportResult[]> ctx)
		throws SQLException
	{
		ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
	}

	@Override
	public void set(BindingSetSQLOutputContext<ImportResult[]> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void get(BindingGetResultSetContext<ImportResult[]> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetStatementContext<ImportResult[]> ctx)
		throws SQLException
	{
		ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
	}

	@Override
	public void get(BindingGetSQLInputContext<ImportResult[]> ctx)
		throws SQLException
	{
		throw new SQLFeatureNotSupportedException();
	}
}
