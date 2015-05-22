package gr.kzps.filesystem;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumeratorTest {
	private static FilesystemOperations fso;
	private static String testDirectory = "/home/antonis/Tobii/FileCrypto/input_test";
	private static List<File> enumeratedFiles;
	
	@BeforeClass
	public static void before() {
		fso = new FilesystemOperations();
		try {
			enumeratedFiles = fso.enumerateInputFiles(testDirectory);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void numberOfFiles() {
		
		assertEquals(100, enumeratedFiles.size());
	}
}
