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
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FileReaderTest {
	private static FilesystemOperations fso;
	private static File firstFile;
	private static final String readDirectory = "input_test";
	
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
