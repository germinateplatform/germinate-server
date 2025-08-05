package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.Groups;
import jhi.germinate.server.database.pojo.TraitRestrictions;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewTableTrialsData
{
	private Integer            germplasmId;
	private String             germplasmGid;
	private String             germplasmName;
	private String             germplasmDisplayName;
	private String[]           germplasmSynonyms;
	private String             entityParentName;
	private String             entityParentGeneralIdentifier;
	private String             entityType;
	private Integer            datasetId;
	private String             datasetName;
	private String             datasetDescription;
	private String             locationName;
	private String             countryName;
	private String             countryCode2;
	private Integer            traitId;
	private String             traitName;
	private String             traitNameShort;
	private TraitRestrictions  traitRestrictions;
	private PhenotypesDatatype traitDataType;
	private String             unitName;
	private String             treatment;
	private Integer            trialsetupId;
	private String             rep;
	private String             block;
	private Short              trialRow;
	private Short              trialColumn;
	private List<Groups>       groups;
	private BigDecimal         latitude;
	private BigDecimal         longitude;
	private BigDecimal         elevation;
	private Timestamp          recordingDate;
	private String             traitValue;
}
