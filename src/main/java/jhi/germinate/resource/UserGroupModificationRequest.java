package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class UserGroupModificationRequest
{
	private Integer   userGroupId;
	private Integer[] userIds;
	private Boolean   addOperation;

	public Integer getUserGroupId()
	{
		return userGroupId;
	}

	public UserGroupModificationRequest setUserGroupId(Integer userGroupId)
	{
		this.userGroupId = userGroupId;
		return this;
	}

	public Integer[] getUserIds()
	{
		return userIds;
	}

	public UserGroupModificationRequest setUserIds(Integer[] userIds)
	{
		this.userIds = userIds;
		return this;
	}

	public Boolean getAddOperation()
	{
		return addOperation;
	}

	public UserGroupModificationRequest setAddOperation(Boolean addOperation)
	{
		this.addOperation = addOperation;
		return this;
	}
}
