package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class PaginatedPolygonRequest extends PaginatedRequest
{
	private LatLng[][] polygons;

	public LatLng[][] getPolygons()
	{
		return polygons;
	}

	public PaginatedPolygonRequest setPolygons(LatLng[][] polygons)
	{
		this.polygons = polygons;
		return this;
	}
}
