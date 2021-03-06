/*	
	Copyright (C) 2015
 	Antonis Kouzoupis <kouzoupis.ant@gmail.com>

	This file is part of FileCrypto.

    FileCrypto is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FileCrypto is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FileCrypto.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.kzps.FileCrypto.executor;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of program
 * 
 * @author Antonis Kouzoupis
 *
 */
public class Executor {
	private static final Logger log = LogManager.getLogger(Executor.class);

	private static final double VERSION = 1.0;

	public static void main(String[] args) {
		CommandParser commandParser = new CommandParser(args);
		String inputDir, outputDir, cryptoKey = null;

		try {
			CommandLine cmd = commandParser.parseArgs();

			if (cmd.hasOption(ArgumentsName.HELP_L)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("FileCrypto", commandParser.getOptions());

			} else if (cmd.hasOption(ArgumentsName.DECRYPT_L)) {
				log.info("Decrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				try {
					cryptoKey = getCryptoKey(cmd);
				} catch (NoCryptoKeyProvided ex) {
					System.err.println(ex.getMessage());
				}
				Integer threshold = getThreshold(cmd);
				callDispatcher(CryptoOperation.DECRYPT, inputDir, outputDir,
						threshold, cryptoKey);

			} else if (cmd.hasOption(ArgumentsName.ENCRYPT_L)) {
				log.info("Encrypt operation");
				inputDir = getCryptoDir(cmd, ArgumentsName.INPUTDIR_L);
				outputDir = getCryptoDir(cmd, ArgumentsName.OUTPUTDIR_L);
				try {
					cryptoKey = getCryptoKey(cmd);
				} catch (NoCryptoKeyProvided ex) {
					System.err.println(ex.getMessage());
				}
				Integer threshold = getThreshold(cmd);
				callDispatcher(CryptoOperation.ENCRYPT, inputDir, outputDir,
						threshold, cryptoKey);

			} else if (cmd.hasOption(ArgumentsName.VERSION_L)) {
				System.out.println("Version: " + VERSION);
			}
		} catch (ParseException ex) {
			log.error("Could not parse arguments! Reason: {}",
					new Object[] { ex.getStackTrace() });
		}
	}

	/**
	 * Call the dispatcher
	 * 
	 * @param operation
	 *            Encryption or decryption
	 * @param inputDir
	 *            Directory where the files to be encrypted/decrypted reside
	 * @param outputDir
	 *            Output directory of the cryptographics operation
	 * @param cryptoKey
	 *            Cryptographic key
	 */
	private static void callDispatcher(CryptoOperation operation,
			String inputDir, String outputDir, Integer threshold,
			String cryptoKey) {
		try {
			Dispatcher.dispatch(operation, inputDir, outputDir, threshold, cryptoKey);
		} catch (FileNotFoundException ex) {
			log.error("Error {}", new Object[] { ex.getMessage() });
		} catch (NotDirectoryException ex) {
			log.error("Error {}", new Object[] { ex.getMessage() });
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (NoCryptoKeyProvided ex) {
			log.error("Error {}", new Object[] { ex.getMessage() });
		}
	}

	/**
	 * Parse command line arguments and get directory to apply crypto operation
	 * Default is the current directory
	 * 
	 * @param cmd
	 *            The parsed command line arguments
	 * @param dirType
	 *            It can be either the input or the output directory
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

	/**
	 * Parse cryptographic key from command line arguments
	 * 
	 * @param cmd
	 *            Access to command line arguments
	 * @return Path to the key
	 * @throws NoCryptoKeyProvided
	 */
	private static String getCryptoKey(CommandLine cmd)
			throws NoCryptoKeyProvided {
		if (cmd.hasOption(ArgumentsName.KEY_L)) {
			return cmd.getOptionValue(ArgumentsName.KEY_L);
		} else {
			throw new NoCryptoKeyProvided(
					"You must provide a cryptographic key. Check available options with --help");
		}
	}

	private static Integer getThreshold(CommandLine cmd) {
		if (cmd.hasOption(ArgumentsName.THRESHOLD_L)) {
			return Integer.parseInt(cmd
					.getOptionValue(ArgumentsName.THRESHOLD_L));
		} else {
			return 300;
		}
	}
}
