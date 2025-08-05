package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Quantiles
{
	private Integer datasetId;
	private Integer treatmentId;
	private Integer xId;
	private String  groupIds;
	private double  min;
	private double  q1;
	private double  median;
	private double  q3;
	private double  max;
	private double  avg;
	private int     count;
}
