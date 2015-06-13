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
package gr.kzps.FileCrypto.executor.Tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import gr.kzps.FileCrypto.crypto.Tests.TestAsymmetric;
import gr.kzps.FileCrypto.crypto.Tests.TestSerialization;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;
import gr.kzps.FileCrypto.filesystem.Tests.EnumeratorTest;
import gr.kzps.FileCrypto.filesystem.Tests.FileReaderTest;
import gr.kzps.FileCrypto.filesystem.Tests.FileWriterTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DispatcherTest.class, ParserTest.class, EnumeratorTest.class,
		FileReaderTest.class, FileWriterTest.class, TestAsymmetric.class,
		TestSerialization.class})

public class AllUnitTests {
	private static final String input = "input_test";
	private static final String output = "output_test";
	private static final String fileName = "test_file";
	
	@BeforeClass
	public static void beforeAll() {
		FilesystemOperations fos = new FilesystemOperations();
		File inputDir = new File(input);
		File outputDir = new File(output);
		
		if (inputDir.exists()) {
			deleteFiles(inputDir);
			inputDir.delete();
		}
		inputDir.mkdir();

		if (outputDir.exists()) {
			deleteFiles(outputDir);
			outputDir.delete();
		}
		outputDir.mkdir();

		// Create 20 test files
		for (int i = 0; i < 20; i++) {
			//String testFileName = input.concat("/").concat(fileName).concat(String.valueOf(i));
			String testFileName = Paths.get(input, fileName.concat(String.valueOf(i))).toString();
			try {
				fos.writeBytesToFile(new File(testFileName), testFileName.getBytes());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static void deleteFiles(File directory) {
		File[] files = directory.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}
}
