package gr.kzps.executor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

/**
 * Main class of program
 * @author Antonis Kouzoupis
 *
 */
public class Executor {

	public static void main(String[] args) {
		CommandParser commandParser = new CommandParser(args);
		String inputDir, outputDir;
		
		try {
			CommandLine cmd = commandParser.parseArgs();
			
			if (cmd.hasOption(ArgumentsName.HELP_L)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("FileCrypto", commandParser.getOptions());
			} else if (cmd.hasOption(ArgumentsName.DECRYPT_L)) {
				System.out.println("Decrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				System.out.println("inputDir: " + inputDir);
				System.out.println("outputDir: " + outputDir);
			} else if (cmd.hasOption(ArgumentsName.ENCRYPT_L)) {
				System.out.println("Encrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				System.out.println("inputDir: " + inputDir);
				System.out.println("outputDir: " + outputDir);
			} else if (cmd.hasOption(ArgumentsName.VERSION_L)) {
				System.out.println("Version");
			}
		} catch (ParseException ex) {
			System.err.println("Could not parse arguments! Reason: "
					+ ex.getStackTrace());
		}
	}

	/**
	 * Parse command line arguments and get directory to apply crypto operation
	 * Default is the current directory
	 * @param cmd The parsed command line arguments
	 * @param dirType It can be either the input or the output directory
	 * @return Parsed directory
	 */
	private static String getCryptoDir(CommandLine cmd, String dirType) {
		if (cmd.hasOption(dirType)) {
			return cmd.getOptionValue(dirType);
		} else {
			// Default is current directory
			return ".";
		}
	}
}
