package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.GermplasmInstitution;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableGermplasm
{
	private String                 germplasmName;
	private String                 germplasmDisplayName;
	private Integer                germplasmId;
	private String                 germplasmGid;
	private String                 germplasmNumber;
	private String                 germplasmPuid;
	private Integer                entityTypeId;
	private String                 entityTypeName;
	private Integer                entityParentId;
	private String                 entityParentName;
	private String                 entityParentGeneralIdentifier;
	private Integer                biologicalStatusId;
	private String                 biologicalStatusName;
	private String[]               synonyms;
	private String                 collectorNumber;
	private String                 genus;
	private String                 species;
	private String                 subtaxa;
	private GermplasmInstitution[] institutions;
	private Integer                locationId;
	private String                 location;
	private BigDecimal             latitude;
	private BigDecimal             longitude;
	private BigDecimal             elevation;
	private String                 countryName;
	private String                 countryCode;
	private String                 collDate;
	private Double                 pdci;
	private Long                   imageCount;
	private String                 firstImagePath;
	private Integer                hasTrialsData;
	private Integer                hasGenotypicData;
	private Integer                hasAllelefreqData;
	private Integer                hasCompoundData;
	private Integer                hasPedigreeData;
}
