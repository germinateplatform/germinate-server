package jhi.germinate.server.util.jooq;

import org.jooq.*;
import org.jooq.impl.*;

import java.util.*;

import static org.jooq.impl.SQLDataType.VARCHAR;

public class GDSL
{
	public static Field<String> jsonSearch(String searchValue, Field<?> field) {
		return CustomField.of("json_search", VARCHAR, ctx -> ctx.visit(DSL.field("json_search(lower({0}), 'one', CONCAT('%', lower({1}), '%'))", String.class, field, searchValue)));
	}

	public static Condition jsonContains(String searchValue, Field<?> field) {
		return CustomCondition.of(ctx -> ctx.visit(DSL.field("json_contains({0}, {1})", String.class, field, searchValue)));
	}

    public static Field<String> concatWS(String separator, Field<?>... fields) {
        return CustomField.of("concat_ws", VARCHAR, ctx -> {
			String template = "concat_ws({0}";

			int counter = 1;
			for (Field<?> ignored : fields)
				template += ", {" + counter++ + "}";
			template += ")";

			List<Object> params = new ArrayList<>();
			params.add(separator);
			params.addAll(Arrays.asList(fields));

			ctx.visit(DSL.field(template, String.class, params.toArray(new Object[0])));
        });
    }
}