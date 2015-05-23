package gr.kzps.executor;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import gr.kzps.crypto.CryptoOperation;
import gr.kzps.exceptions.NoCryptoKeyProvided;
import gr.kzps.filesystem.FilesystemOperations;

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
		String inputDir, outputDir, cryptoKey = null;
		
		try {
			CommandLine cmd = commandParser.parseArgs();
			
			if (cmd.hasOption(ArgumentsName.HELP_L)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("FileCrypto", commandParser.getOptions());
				
			} else if (cmd.hasOption(ArgumentsName.DECRYPT_L)) {
				System.out.println("Decrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				try {
					cryptoKey = getCryptoKey(cmd);
				} catch (NoCryptoKeyProvided ex) {
					System.err.println(ex.getMessage());
				}
				
				callDispatcher(CryptoOperation.DECRYPT, inputDir, outputDir, cryptoKey);
				
			} else if (cmd.hasOption(ArgumentsName.ENCRYPT_L)) {
				System.out.println("Encrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				try {
					cryptoKey = getCryptoKey(cmd);
				} catch (NoCryptoKeyProvided ex) {
					System.err.println(ex.getMessage());
				}
				
				callDispatcher(CryptoOperation.ENCRYPT, inputDir, outputDir, cryptoKey);
				
			} else if (cmd.hasOption(ArgumentsName.VERSION_L)) {
				System.out.println("Version");
			}
		} catch (ParseException ex) {
			System.err.println("Could not parse arguments! Reason: "
					+ ex.getStackTrace());
		}
	}

	private static void callDispatcher(CryptoOperation operation, String inputDir, String outputDir, String cryptoKey) {
		try {
			Dispatcher.dispatch(operation, inputDir, outputDir, cryptoKey);
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getMessage());
		} catch (NotDirectoryException ex) {
			System.err.println(ex.getMessage());
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (NoCryptoKeyProvided ex) {
			System.err.println(ex.getMessage());
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
	
	private static String getCryptoKey(CommandLine cmd) throws NoCryptoKeyProvided {
		if (cmd.hasOption(ArgumentsName.KEY_L)) {
			return cmd.getOptionValue(ArgumentsName.KEY_L);
		} else {
			throw new NoCryptoKeyProvided("You must provide a cryptographic key. Check available options with --help");
		}
	}
}
