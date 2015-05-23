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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FilesystemOperations {
	public FilesystemOperations() {
		super();
	}

	public List<File> enumerateInputFiles(String directory)
			throws FileNotFoundException, NotDirectoryException {
		
		File inputDirectory = new File(directory);
		File[] fileArray;
		List<File> inputFiles = new ArrayList<File>();

		if (!inputDirectory.isDirectory()) {
			throw new NotDirectoryException(inputDirectory.getName() + "is not a directory");
		}

		if (inputDirectory.exists()) {
			fileArray = inputDirectory.listFiles();
		} else {
			throw new FileNotFoundException("Directory: " + inputDirectory + "does not exist");
		}
		
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) {
				inputFiles.add(fileArray[i]);
			}
		}
		
		return inputFiles;
	}
	
	public byte[] readFileContent(File file) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		
		return content;
	}
	
	public void writeBytesToFile(File file, byte[] content) throws IOException {
		Files.write(Paths.get(file.getAbsolutePath()), content);
	}
}
