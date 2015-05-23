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
package gr.kzps.executor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the command line argument parser
 * 
 * @author Antonis Kouzoupis
 *
 */
public class ParserTest {
	private static CommandParser parser;
	private static CommandLine cmd;
	
	@BeforeClass
	public static void before() {
		String[] args = {"-e", "-i input/test", "-o output/test", "-h", "--version", "-k keys/key"};
				
		parser = new CommandParser(args);
		try {
			cmd = parser.parseArgs();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testValidCryptoOperation() {
		assertEquals(true, cmd.hasOption(ArgumentsName.ENCRYPT_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.ENCRYPT_S));		
	}
	
	@Test
	public void testNonValidCryptoOperation() {
		assertEquals(false, cmd.hasOption(ArgumentsName.DECRYPT_L));
		assertEquals(false, cmd.hasOption(ArgumentsName.DECRYPT_S));
	}
	
	@Test
	public void testInputDirectory() {
		assertEquals(true, cmd.hasOption(ArgumentsName.INPUTDIR_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.INPUTDIR_S));
		
		assertEquals(" input/test", cmd.getOptionValue(ArgumentsName.INPUTDIR_L));
		assertEquals(" input/test", cmd.getOptionValue(ArgumentsName.INPUTDIR_S));
	}
	
	@Test
	public void testOutputDirectory() {
		assertEquals(true, cmd.hasOption(ArgumentsName.OUTPUTDIR_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.OUTPUTDIR_S));
		
		assertEquals(" output/test", cmd.getOptionValue(ArgumentsName.OUTPUTDIR_L));
		assertEquals(" output/test", cmd.getOptionValue(ArgumentsName.OUTPUTDIR_S));
	}
	
	@Test
	public void testHelp() {
		assertEquals(true, cmd.hasOption(ArgumentsName.HELP_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.HELP_S));
	}
	
	@Test
	public void testVersion() {
		assertEquals(true, cmd.hasOption(ArgumentsName.VERSION_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.VERSION_S));
	}
	
	@Test
	public void testKey() {
		assertEquals(true, cmd.hasOption(ArgumentsName.KEY_L));
		assertEquals(true, cmd.hasOption(ArgumentsName.KEY_S));
		
		assertEquals(" keys/key", cmd.getOptionValue(ArgumentsName.KEY_L));
		assertEquals(" keys/key", cmd.getOptionValue(ArgumentsName.KEY_S));
	}
}
