/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.enums.ViewTableGermplasmAttributesAttributeType;
import jhi.germinate.server.database.tables.records.ViewTableGermplasmAttributesRecord;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


// @formatter:off
/**
 * VIEW
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewTableGermplasmAttributes extends TableImpl<ViewTableGermplasmAttributesRecord> {

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_germplasm_attributes</code>
     */
    public static final ViewTableGermplasmAttributes VIEW_TABLE_GERMPLASM_ATTRIBUTES = new ViewTableGermplasmAttributes();
    private static final long serialVersionUID = 1142970557;
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.germplasm_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, Integer> GERMPLASM_ID = createField("germplasm_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.germplasm_gid</code>. A unique identifier.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> GERMPLASM_GID = createField("germplasm_gid", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A unique identifier.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.germplasm_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> GERMPLASM_NAME = createField("germplasm_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A unique name which defines an entry in the germinatbase table.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.attribute_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, Integer> ATTRIBUTE_ID = createField("attribute_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.attribute_name</code>. Defines the name of the attribute.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> ATTRIBUTE_NAME = createField("attribute_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Defines the name of the attribute.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.attribute_description</code>. Describes the attribute. This should expand on the name to make it clear what the attribute actually is.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> ATTRIBUTE_DESCRIPTION = createField("attribute_description", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Describes the attribute. This should expand on the name to make it clear what the attribute actually is.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.attribute_type</code>. Describes the data type of the attribute. This can be INT, FLOAT or CHAR type.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, ViewTableGermplasmAttributesAttributeType> ATTRIBUTE_TYPE = createField("attribute_type", org.jooq.impl.SQLDataType.VARCHAR(5).defaultValue(org.jooq.impl.DSL.inline("int", org.jooq.impl.SQLDataType.VARCHAR)).asEnumDataType(jhi.germinate.server.database.enums.ViewTableGermplasmAttributesAttributeType.class), this, "Describes the data type of the attribute. This can be INT, FLOAT or CHAR type.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.target_table</code>.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> TARGET_TABLE = createField("target_table", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("germinatebase", org.jooq.impl.SQLDataType.VARCHAR)), this, "");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.foreign_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public final TableField<ViewTableGermplasmAttributesRecord, Integer> FOREIGN_ID = createField("foreign_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to germinatebase (germinatebase.id).");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_attributes.attribute_value</code>. The value of the attribute.
     */
    public final TableField<ViewTableGermplasmAttributesRecord, String> ATTRIBUTE_VALUE = createField("attribute_value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "The value of the attribute.");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_germplasm_attributes</code> table reference
     */
    public ViewTableGermplasmAttributes() {
        this(DSL.name("view_table_germplasm_attributes"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_germplasm_attributes</code> table reference
     */
    public ViewTableGermplasmAttributes(String alias) {
        this(DSL.name(alias), VIEW_TABLE_GERMPLASM_ATTRIBUTES);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_germplasm_attributes</code> table reference
     */
    public ViewTableGermplasmAttributes(Name alias) {
        this(alias, VIEW_TABLE_GERMPLASM_ATTRIBUTES);
    }

    private ViewTableGermplasmAttributes(Name alias, Table<ViewTableGermplasmAttributesRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableGermplasmAttributes(Name alias, Table<ViewTableGermplasmAttributesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableGermplasmAttributesRecord> getRecordType() {
        return ViewTableGermplasmAttributesRecord.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_3_7_0.GERMINATE_TEMPLATE_3_7_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGermplasmAttributes as(String alias) {
        return new ViewTableGermplasmAttributes(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGermplasmAttributes as(Name alias) {
        return new ViewTableGermplasmAttributes(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGermplasmAttributes rename(String name) {
        return new ViewTableGermplasmAttributes(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGermplasmAttributes rename(Name name) {
        return new ViewTableGermplasmAttributes(name, null);
    }
// @formatter:on
}
