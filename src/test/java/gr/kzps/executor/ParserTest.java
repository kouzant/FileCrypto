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
		String[] args = {"-e", "-i input/test", "-o output/test", "-h", "--version"};
		
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
}
