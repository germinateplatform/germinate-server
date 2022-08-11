package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.BinningConfig;

/**
 * @author Sebastian Raubach
 */
public class AlleleFrequencyDatasetRequest extends SubsettedGenotypeDatasetRequest
{
	private BinningConfig config;

	public BinningConfig getConfig()
	{
		return config;
	}

	public AlleleFrequencyDatasetRequest setConfig(BinningConfig config)
	{
		this.config = config;
		return this;
	}
}
