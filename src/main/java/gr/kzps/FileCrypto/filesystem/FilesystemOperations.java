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
package gr.kzps.FileCrypto.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that implements operations to the filesystem
 * 
 * @author Antonis Kouzoupis
 *
 */
public class FilesystemOperations {
	public FilesystemOperations() {
		super();
	}

	/**
	 * Identify files in the input directory
	 * 
	 * @param directory Input directory
	 * @return List of available files in input directory
	 * @throws FileNotFoundException
	 * @throws NotDirectoryException
	 */
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
	
	/**
	 * Method that reads content of a file
	 * 
	 * @param file The file to be read
	 * @return The contents of the file
	 * @throws IOException
	 */
	public byte[] readFileContent(File file) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		
		return content;
	}
	
	/**
	 * Method that writes content to a file
	 * 
	 * @param file The path to the file for the contents to be written
	 * @param content Contents to be written
	 * @throws IOException
	 */
	public void writeBytesToFile(File file, byte[] content) throws IOException {
		Files.write(Paths.get(file.getAbsolutePath()), content);
	}
}
