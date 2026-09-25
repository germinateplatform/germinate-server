package jhi.germinate;

import picocli.CommandLine;

public class GerminateCommandVersion implements CommandLine.IVersionProvider
{
	@Override
	public String[] getVersion()
	{
		return new String[]{"${COMMAND-FULL-NAME} version " + getClass().getPackage().getImplementationVersion()};
	}
}