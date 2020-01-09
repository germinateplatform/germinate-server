package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class UserGroupModificationRequest
{
	private Integer userGroupId;
	private Integer[] userIds;
	private Boolean isAddOperation;

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

	public Boolean isAddOperation()
	{
		return isAddOperation;
	}

	public UserGroupModificationRequest setAddOperation(Boolean addOperation)
	{
		isAddOperation = addOperation;
		return this;
	}
}
