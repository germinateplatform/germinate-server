/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


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
public class ViewStatsCountry implements Serializable {

    private static final long serialVersionUID = -1630156846;

    private String country;
    private String code;
    private Long   count;

    public ViewStatsCountry() {}

    public ViewStatsCountry(ViewStatsCountry value) {
        this.country = value.country;
        this.code = value.code;
        this.count = value.count;
    }

    public ViewStatsCountry(
        String country,
        String code,
        Long   count
    ) {
        this.country = country;
        this.code = code;
        this.count = count;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ViewStatsCountry (");

        sb.append(country);
        sb.append(", ").append(code);
        sb.append(", ").append(count);

        sb.append(")");
        return sb.toString();
    }
// @formatter:on
}