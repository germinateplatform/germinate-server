package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewMcpd
{
	private Integer    id;
	private String     puid;
	private String     instcode;
	private String     accenumb;
	private String     collnumb;
	private String     collcode;
	private String     collname;
	private String     collinstaddress;
	private String     collmissid;
	private String     genus;
	private String     species;
	private String     spauthor;
	private String     subtaxa;
	private String     subtauthor;
	private String     cropname;
	private String     accename;
	private String     acqdate;
	private String     origcty;
	private String     collsite;
	private BigDecimal declatitude;
	private byte[]     latitude;
	private BigDecimal declongitude;
	private byte[]     longitude;
	private Integer    coorduncert;
	private String     coorddatum;
	private String     georefmeth;
	private BigDecimal elevation;
	private String     colldate;
	private String     bredcode;
	private String     bredname;
	private Integer    sampstat;
	private String     ancest;
	private Integer    collsrc;
	private String     donorcode;
	private String     donorname;
	private String     donornumb;
	private String     othernumb;
	private String     duplsite;
	private String     duplinstname;
	private String     storage;
	private Integer    mlsstat;
	private String     remarks;
	private String     entitytype;
	private Long       entityparentid;
	private String     entityparentaccenumb;
}
