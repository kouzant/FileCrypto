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
package gr.kzps.FileCrypto.filesystem.Tests;

import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumeratorTest {
	private static FilesystemOperations fso;
	private static String testDirectory = "input_test";
	private static List<File> enumeratedFiles;
	private static List<String> excludeList;

	@BeforeClass
	public static void before() {
		excludeList = new ArrayList<String>();
	}

	@Test
	public void numberOfFiles() {
		fso = new FilesystemOperations();
		try {
			enumeratedFiles = fso.enumerateInputFiles(testDirectory,
					excludeList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals(20, enumeratedFiles.size());
	}
	
	@Test
	public void numberOfFilesExclude() {
		fso = new FilesystemOperations();
		try {
			excludeList.add("test_file0");
			enumeratedFiles = fso.enumerateInputFiles(testDirectory,
					excludeList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals(19, enumeratedFiles.size());
	}
}
