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
public class ClientAdminConfiguration extends ClientConfiguration
{
	private Integer bcryptSalt;
	private Boolean brapiEnabled;
	private String  dataDirectoryExternal;
	private String  gatekeeperUsername;
	private String  gatekeeperPassword;
	private Boolean gatekeeperRegistrationRequiresApproval;
	private Boolean pdciEnabled;
	private Integer filesDeleteAfterHoursAsync;
	private Integer filesDeleteAfterHoursTemp;
	private Boolean hiddenPagesAutodiscover;
	private Integer databaseBackupEveryDays;
	private Double databaseBackupMaxSizeGB;
}
