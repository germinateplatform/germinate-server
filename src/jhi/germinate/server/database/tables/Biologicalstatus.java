/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.BiologicalstatusRecord;

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
 * Based on Multi Crop Passport Descriptors (MCPD V2 2012) - The coding scheme 
 * proposed can be used at 3 different levels of detail: either by using the
 * general codes (in boldface) such as 100, 200, 300, 400, or by using the 
 * more specific codes
 * such as 110, 120, etc.
 * 100) Wild
 * 110) Natural
 * 120) Semi-natural/wild
 * 130) Semi-natural/sown
 * 200) Weedy
 * 300) Traditional cultivar/landrace
 * 400) Breeding/research material
 *  410) Breeder's line
 *  411) Synthetic population
 *  412) Hybrid
 *  413) Founder stock/base population
 *  414) Inbred line (parent of hybrid cultivar)
 *  415) Segregating population
 *  416) Clonal selection
 *  420) Genetic stock
 *  421) Mutant (e.g. induced/insertion mutants, tilling populations)
 *  422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids,
 * amphiploids)
 *  423) Other genetic stocks (e.g. mapping populations)
 * 500) Advanced or improved cultivar (conventional breeding methods)
 * 600) GMO (by genetic engineering)
 *  999) Other 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Biologicalstatus extends TableImpl<BiologicalstatusRecord> {

    private static final long serialVersionUID = -1148184327;

    /**
     * The reference instance of <code>germinate_template_3_7_0.biologicalstatus</code>
     */
    public static final Biologicalstatus BIOLOGICALSTATUS = new Biologicalstatus();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BiologicalstatusRecord> getRecordType() {
        return BiologicalstatusRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.biologicalstatus.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<BiologicalstatusRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.biologicalstatus.sampstat</code>. Previoulsy known as sampstat.
     */
    public final TableField<BiologicalstatusRecord, String> SAMPSTAT = createField("sampstat", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "Previoulsy known as sampstat.");

    /**
     * The column <code>germinate_template_3_7_0.biologicalstatus.created_on</code>. When the record was created.
     */
    public final TableField<BiologicalstatusRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_3_7_0.biologicalstatus.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<BiologicalstatusRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_7_0.biologicalstatus</code> table reference
     */
    public Biologicalstatus() {
        this(DSL.name("biologicalstatus"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.biologicalstatus</code> table reference
     */
    public Biologicalstatus(String alias) {
        this(DSL.name(alias), BIOLOGICALSTATUS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.biologicalstatus</code> table reference
     */
    public Biologicalstatus(Name alias) {
        this(alias, BIOLOGICALSTATUS);
    }

    private Biologicalstatus(Name alias, Table<BiologicalstatusRecord> aliased) {
        this(alias, aliased, null);
    }

    private Biologicalstatus(Name alias, Table<BiologicalstatusRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Based on Multi Crop Passport Descriptors (MCPD V2 2012) - The coding scheme proposed can be used at 3 different levels of detail: either by using the\ngeneral codes (in boldface) such as 100, 200, 300, 400, or by using the more specific codes\nsuch as 110, 120, etc.\n100) Wild\n110) Natural\n120) Semi-natural/wild\n130) Semi-natural/sown\n200) Weedy\n300) Traditional cultivar/landrace\n400) Breeding/research material\n 410) Breeder's line\n 411) Synthetic population\n 412) Hybrid\n 413) Founder stock/base population\n 414) Inbred line (parent of hybrid cultivar)\n 415) Segregating population\n 416) Clonal selection\n 420) Genetic stock\n 421) Mutant (e.g. induced/insertion mutants, tilling populations)\n 422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids,\namphiploids)\n 423) Other genetic stocks (e.g. mapping populations)\n500) Advanced or improved cultivar (conventional breeding methods)\n600) GMO (by genetic engineering)\n 999) Other "));
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
    public Identity<BiologicalstatusRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS, jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BiologicalstatusRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS, "KEY_biologicalstatus_PRIMARY", jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BiologicalstatusRecord>> getKeys() {
        return Arrays.<UniqueKey<BiologicalstatusRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS, "KEY_biologicalstatus_PRIMARY", jhi.germinate.server.database.tables.Biologicalstatus.BIOLOGICALSTATUS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Biologicalstatus as(String alias) {
        return new Biologicalstatus(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Biologicalstatus as(Name alias) {
        return new Biologicalstatus(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Biologicalstatus rename(String name) {
        return new Biologicalstatus(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Biologicalstatus rename(Name name) {
        return new Biologicalstatus(name, null);
    }
// @formatter:on
}
