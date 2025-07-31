package jhi.germinate;

import jhi.germinate.server.util.importer.cli.ImporterCommand;
import picocli.CommandLine;

@CommandLine.Command(
		name = "Germinate",
		description = "CLI of Germinate",
		subcommands = {
				ImporterCommand.class,
		},
		mixinStandardHelpOptions = true,
		versionProvider = jhi.germinate.GerminateCommandVersion.class
)
public class GerminateCommand implements Runnable
{
	public static void main(String[] args)
	{
		int exitCode = new CommandLine(new GerminateCommand()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public void run()
	{
		System.out.println("Germinate CLI");
	}
}
