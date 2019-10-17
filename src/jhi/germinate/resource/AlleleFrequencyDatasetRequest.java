package jhi.germinate.resource;

import jhi.germinate.server.util.async.BinningConfig;

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
