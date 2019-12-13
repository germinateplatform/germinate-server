/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

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
public class ViewTableGroups implements Serializable {

    private static final long serialVersionUID = 362133737;

    private Integer   groupId;
    private String    groupName;
    private String    groupDescription;
    private Integer   groupTypeId;
    private String    groupType;
    private String    userName;
    private Integer   userId;
    private Boolean   groupVisibility;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private Long      count;

    public ViewTableGroups() {}

    public ViewTableGroups(ViewTableGroups value) {
        this.groupId = value.groupId;
        this.groupName = value.groupName;
        this.groupDescription = value.groupDescription;
        this.groupTypeId = value.groupTypeId;
        this.groupType = value.groupType;
        this.userName = value.userName;
        this.userId = value.userId;
        this.groupVisibility = value.groupVisibility;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
        this.count = value.count;
    }

    public ViewTableGroups(
        Integer   groupId,
        String    groupName,
        String    groupDescription,
        Integer   groupTypeId,
        String    groupType,
        String    userName,
        Integer   userId,
        Boolean   groupVisibility,
        Timestamp createdOn,
        Timestamp updatedOn,
        Long      count
    ) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupTypeId = groupTypeId;
        this.groupType = groupType;
        this.userName = userName;
        this.userId = userId;
        this.groupVisibility = groupVisibility;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.count = count;
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Integer getGroupTypeId() {
        return this.groupTypeId;
    }

    public void setGroupTypeId(Integer groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getGroupVisibility() {
        return this.groupVisibility;
    }

    public void setGroupVisibility(Boolean groupVisibility) {
        this.groupVisibility = groupVisibility;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ViewTableGroups (");

        sb.append(groupId);
        sb.append(", ").append(groupName);
        sb.append(", ").append(groupDescription);
        sb.append(", ").append(groupTypeId);
        sb.append(", ").append(groupType);
        sb.append(", ").append(userName);
        sb.append(", ").append(userId);
        sb.append(", ").append(groupVisibility);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);
        sb.append(", ").append(count);

        sb.append(")");
        return sb.toString();
    }
// @formatter:on
}
