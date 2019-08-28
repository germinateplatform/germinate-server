/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ImagesRecord;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.TableImpl;


// @formatter:off
/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Images extends TableImpl<ImagesRecord> {

    private static final long serialVersionUID = -1515147971;

    /**
     * The reference instance of <code>germinate_template_3_7_0.images</code>
     */
    public static final Images IMAGES = new Images();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ImagesRecord> getRecordType() {
        return ImagesRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.images.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ImagesRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.images.imagetype_id</code>. Foreign key to imagetypes (imagetypes.id).
     */
    public final TableField<ImagesRecord, Integer> IMAGETYPE_ID = createField("imagetype_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Foreign key to imagetypes (imagetypes.id).");

    /**
     * The column <code>germinate_template_3_7_0.images.description</code>. A description of what the image shows if required.
     */
    public final TableField<ImagesRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "A description of what the image shows if required.");

    /**
     * The column <code>germinate_template_3_7_0.images.foreign_id</code>. Relates to the UID of the table to which the comment relates.
     */
    public final TableField<ImagesRecord, Integer> FOREIGN_ID = createField("foreign_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Relates to the UID of the table to which the comment relates.");

    /**
     * The column <code>germinate_template_3_7_0.images.path</code>. The file system path to the image.
     */
    public final TableField<ImagesRecord, String> PATH = createField("path", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "The file system path to the image.");

    /**
     * The column <code>germinate_template_3_7_0.images.created_on</code>. When the record was created.
     */
    public final TableField<ImagesRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_3_7_0.images.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<ImagesRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_7_0.images</code> table reference
     */
    public Images() {
        this(DSL.name("images"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.images</code> table reference
     */
    public Images(String alias) {
        this(DSL.name(alias), IMAGES);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.images</code> table reference
     */
    public Images(Name alias) {
        this(alias, IMAGES);
    }

    private Images(Name alias, Table<ImagesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Images(Name alias, Table<ImagesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
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
    public Identity<ImagesRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Images.IMAGES, jhi.germinate.server.database.tables.Images.IMAGES.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ImagesRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Images.IMAGES, "KEY_images_PRIMARY", jhi.germinate.server.database.tables.Images.IMAGES.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ImagesRecord>> getKeys() {
        return Arrays.<UniqueKey<ImagesRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Images.IMAGES, "KEY_images_PRIMARY", jhi.germinate.server.database.tables.Images.IMAGES.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Images as(String alias) {
        return new Images(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Images as(Name alias) {
        return new Images(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Images rename(String name) {
        return new Images(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Images rename(Name name) {
        return new Images(name, null);
    }
// @formatter:on
}
