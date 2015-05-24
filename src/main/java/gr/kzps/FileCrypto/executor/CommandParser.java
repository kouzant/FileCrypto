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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Contruct and parse the argument list
 * 
 * @author Antonis Kouzoupis
 *
 */
public class CommandParser {
	private String[] args;
	private Options options;

	/**
	 * Constructor
	 * @param args Command line arguments list
	 */
	public CommandParser(String[] args) {
		this.args = args;
		options = new Options();
	}

	/**
	 * Parse the arguments list
	 * @return command line arguments
	 * @throws ParseException
	 * @see CommandLine
	 */
	public CommandLine parseArgs() throws ParseException {

		Option help = new Option(ArgumentsName.HELP_S, ArgumentsName.HELP_L,
				false, "Print help message");
		Option version = new Option(ArgumentsName.VERSION_S,
				ArgumentsName.VERSION_L, false, "Print version");
		Option encrypt = new Option(ArgumentsName.ENCRYPT_S,
				ArgumentsName.ENCRYPT_L, false, "Encrypt operation");
		Option decrypt = new Option(ArgumentsName.DECRYPT_S,
				ArgumentsName.DECRYPT_L, false, "Decrypt operation");

		Option inputDir = Option.builder(ArgumentsName.INPUTDIR_S)
				.longOpt(ArgumentsName.INPUTDIR_L).hasArg()
				.desc("input directory").build();
		Option outputDir = Option.builder(ArgumentsName.OUTPUTDIR_S)
				.longOpt(ArgumentsName.OUTPUTDIR_L).hasArg()
				.desc("output directory").build();
		Option cryptoKey = Option.builder(ArgumentsName.KEY_S)
				.longOpt(ArgumentsName.KEY_L).hasArg()
				.desc("encryption/decryption key").build();
		Option threshold = Option.builder(ArgumentsName.THRESHOLD_S)
				.longOpt(ArgumentsName.THRESHOLD_L).hasArg()
				.desc("Threshold to dispatch files to multiple threads. DEFAULT 300")
				.build();

		options.addOption(help);
		options.addOption(version);
		options.addOption(encrypt);
		options.addOption(decrypt);
		options.addOption(inputDir);
		options.addOption(outputDir);
		options.addOption(cryptoKey);
		options.addOption(threshold);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		return cmd;
	}

	/**
	 * Get available options
	 * @return Available options
	 * @see Options
	 */
	public Options getOptions() {
		return options;
	}
}
