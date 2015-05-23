package gr.kzps.filesystem;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class FileWriterTest {
	private static FilesystemOperations fso;
	private final static String newFile = "/home/antonis/Tobii/FileCrypto/output_test/writeTest";
	private final static String content = "output write test!";
	private static File file;

	@BeforeClass
	public static void before() {
		fso = new FilesystemOperations();
		file = new File(newFile);

		// Delete first any previous file
		if (file.exists())
			file.delete();

		// Write new file
		try {
			fso.writeBytesToFile(file, content.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void writeTest() {
		assertTrue(file.exists());
	}

	@Test
	public void contentTest() {
		try {
			byte[] readContent = fso.readFileContent(file);
			
			assertEquals(content, new String(readContent));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
