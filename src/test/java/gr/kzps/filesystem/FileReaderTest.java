package gr.kzps.filesystem;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FileReaderTest {
	private static FilesystemOperations fso;
	private static File firstFile;
	private static final String readDirectory = "/home/antonis/Tobii/FileCrypto/input_test";
	
	@BeforeClass
	public static void before() {
		fso = new FilesystemOperations();
		try {
			firstFile = fso.enumerateInputFiles(readDirectory).get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void readTest() {
		String fileName = firstFile.getName();
		
		try {
			byte[] content = fso.readFileContent(firstFile);
			
			// Remove new-line character for testing
			byte[] noNewLine = new byte[content.length - 1];
			for (int i = 0; i < content.length - 1; i++)
				noNewLine[i] = content[i];
			
			// For testing, content of a file is its own filename
			assertEquals(fileName, new String(noNewLine));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
