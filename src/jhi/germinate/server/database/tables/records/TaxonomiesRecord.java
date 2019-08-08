/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;

import javax.annotation.*;

import jhi.germinate.server.database.tables.*;


/**
 * The species table holds information relating to the species that are deinfed 
 * within a particular Germinate instance including common names and ploidy 
 * levels.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TaxonomiesRecord extends UpdatableRecordImpl<TaxonomiesRecord> implements Record10<Integer, String, String, String, String, String, String, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 864660301;

    /**
     * Create a detached TaxonomiesRecord
     */
    public TaxonomiesRecord() {
        super(Taxonomies.TAXONOMIES);
    }

    /**
     * Create a detached, initialised TaxonomiesRecord
     */
    public TaxonomiesRecord(Integer id, String genus, String species, String subtaxa, String speciesAuthor, String subtaxaAuthor, String cropname, Integer ploidy, Timestamp createdOn, Timestamp updatedOn) {
        super(Taxonomies.TAXONOMIES);

        set(0, id);
        set(1, genus);
        set(2, species);
        set(3, subtaxa);
        set(4, speciesAuthor);
        set(5, subtaxaAuthor);
        set(6, cropname);
        set(7, ploidy);
        set(8, createdOn);
        set(9, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.genus</code>. Genus name for the species.
     */
    public String getGenus() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.genus</code>. Genus name for the species.
     */
    public void setGenus(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.species</code>. Species name in lowercase.
     */
    public String getSpecies() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.species</code>. Species name in lowercase.
     */
    public void setSpecies(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.subtaxa</code>. Subtaxa name.
     */
    public String getSubtaxa() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.subtaxa</code>. Subtaxa name.
     */
    public void setSubtaxa(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.species_author</code>. also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.
     */
    public String getSpeciesAuthor() {
        return (String) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.species_author</code>. also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.
     */
    public void setSpeciesAuthor(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.subtaxa_author</code>. also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).
     */
    public String getSubtaxaAuthor() {
        return (String) get(5);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.subtaxa_author</code>. also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).
     */
    public void setSubtaxaAuthor(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.cropname</code>. The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.
     */
    public String getCropname() {
        return (String) get(6);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.cropname</code>. The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.
     */
    public void setCropname(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.ploidy</code>. Defines the ploidy level for the species. Use numbers to reference ploidy for example diploid = 2, tetraploid = 4.
     */
    public Integer getPloidy() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.ploidy</code>. Defines the ploidy level for the species. Use numbers to reference ploidy for example diploid = 2, tetraploid = 4.
     */
    public void setPloidy(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(8);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(8, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_6_0.taxonomies.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(9);
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>germinate_template_3_6_0.taxonomies.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(9, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, String, String, String, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, String, String, String, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Taxonomies.TAXONOMIES.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Taxonomies.TAXONOMIES.GENUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Taxonomies.TAXONOMIES.SPECIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Taxonomies.TAXONOMIES.SUBTAXA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Taxonomies.TAXONOMIES.SPECIES_AUTHOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Taxonomies.TAXONOMIES.SUBTAXA_AUTHOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Taxonomies.TAXONOMIES.CROPNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return Taxonomies.TAXONOMIES.PLOIDY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field9() {
        return Taxonomies.TAXONOMIES.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return Taxonomies.TAXONOMIES.UPDATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGenus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getSpecies();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getSubtaxa();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getSpeciesAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getSubtaxaAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getCropname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getPloidy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component9() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component10() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGenus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getSpecies();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getSubtaxa();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getSpeciesAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getSubtaxaAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getCropname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getPloidy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value9() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value2(String value) {
        setGenus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value3(String value) {
        setSpecies(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value4(String value) {
        setSubtaxa(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value5(String value) {
        setSpeciesAuthor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value6(String value) {
        setSubtaxaAuthor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value7(String value) {
        setCropname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value8(Integer value) {
        setPloidy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value9(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord value10(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaxonomiesRecord values(Integer value1, String value2, String value3, String value4, String value5, String value6, String value7, Integer value8, Timestamp value9, Timestamp value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }
}
