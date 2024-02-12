package jhi.germinate.resource;

import java.util.List;

public class TrialCreationDetails
{
	private Integer           datasetId;
	private List<PlotDetails> plots;

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public TrialCreationDetails setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public List<PlotDetails> getPlots()
	{
		return plots;
	}

	public TrialCreationDetails setPlots(List<PlotDetails> plots)
	{
		this.plots = plots;
		return this;
	}

	public class PlotDetails {
		private Integer row;
		private Integer column;
		private String germplasm;
		private String rep;

		public Integer getRow()
		{
			return row;
		}

		public PlotDetails setRow(Integer row)
		{
			this.row = row;
			return this;
		}

		public Integer getColumn()
		{
			return column;
		}

		public PlotDetails setColumn(Integer column)
		{
			this.column = column;
			return this;
		}

		public String getGermplasm()
		{
			return germplasm;
		}

		public PlotDetails setGermplasm(String germplasm)
		{
			this.germplasm = germplasm;
			return this;
		}

		public String getRep()
		{
			return rep;
		}

		public PlotDetails setRep(String rep)
		{
			this.rep = rep;
			return this;
		}
	}
}
