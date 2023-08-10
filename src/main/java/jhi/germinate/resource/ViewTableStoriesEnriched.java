package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableStories;

public class ViewTableStoriesEnriched extends ViewTableStories
{
	private boolean canAccess = false;

	public boolean isCanAccess()
	{
		return canAccess;
	}

	public ViewTableStoriesEnriched setCanAccess(boolean canAccess)
	{
		this.canAccess = canAccess;
		return this;
	}
}
