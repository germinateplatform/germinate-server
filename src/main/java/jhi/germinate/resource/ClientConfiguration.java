package jhi.germinate.resource;

import jhi.germinate.resource.enums.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ClientConfiguration
{
	private AuthenticationMode authMode;
	private List<String>       colorsTemplate;
	private List<String>       colorsCharts;
	private List<String>       colorsGradient;
	private String             colorPrimary;
	private Boolean            commentsEnabled;
	private List<String>       dashboardCategories;
	private List<String>       dashboardSections;
	private DataImportMode     dataImportMode;
	private String             externalLinkIdentifier;
	private String             externalLinkTemplate;
	private String             googleAnalyticsKey;
	private String             plausibleDomain;
	private Boolean            plausibleHashMode;
	private String             plausibleApiHost;
	private String             gatekeeperUrl;
	private List<String>       hiddenPages;
	private Boolean            registrationEnabled;
	private Boolean            showGdprNotification;
	private String             gridscoreUrl;
	private String             heliumUrl;
	private String             fieldhubUrl;
	private HiddenColumns      hiddenColumns;
	private Boolean            supportsFeedback;
	private String             genesysUrl;
}
