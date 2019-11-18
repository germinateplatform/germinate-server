package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ExperimentRequest extends DatasetRequest
{
	private Integer experimentId;

	public Integer getExperimentId()
	{
		return experimentId;
	}

	public ExperimentRequest setExperimentId(Integer experimentId)
	{
		this.experimentId = experimentId;
		return this;
	}
}
