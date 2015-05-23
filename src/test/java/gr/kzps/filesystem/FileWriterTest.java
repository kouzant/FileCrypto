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
	private final static String newFile = "output_test/writeTest";
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
