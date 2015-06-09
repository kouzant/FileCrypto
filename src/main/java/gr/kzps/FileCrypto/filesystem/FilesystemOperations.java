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

import gr.kzps.FileCrypto.crypto.AESPrimitives;
import gr.kzps.FileCrypto.executor.Dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that implements operations to the filesystem
 * 
 * @author Antonis Kouzoupis
 *
 */
public class FilesystemOperations {
	private static final Logger log = LogManager.getLogger(FilesystemOperations.class);

	public FilesystemOperations() {
		super();
	}

	/**
	 * Identify files in the input directory
	 * 
	 * @param directory Input directory
	 * @param excludeFiles List of excluded files from encryption
	 * @return List of available files in input directory
	 * @throws FileNotFoundException
	 * @throws NotDirectoryException
	 */
	public List<File> enumerateInputFiles(String directory, List<String> excludeFiles)
			throws FileNotFoundException, NotDirectoryException {
		
		File inputDirectory = new File(directory);
		File[] fileArray;
		List<File> inputFiles = new ArrayList<File>();

		if (!inputDirectory.isDirectory()) {
			throw new NotDirectoryException(inputDirectory.getName() + " is not a directory");
		}

		if (inputDirectory.exists()) {
			fileArray = inputDirectory.listFiles();
		} else {
			throw new FileNotFoundException("Directory: " + inputDirectory + " does not exist");
		}
		
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) {
				File tmpFilename = fileArray[i];
				excludeFiles.stream().forEach(x -> {
					if (!x.equals(tmpFilename.getName())) {
						inputFiles.add(tmpFilename);
					}
				});
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
	
	/**
	 * Serialize the AES cryptographic primitives
	 * @param primitives AES cryptographic primitives object
	 * @param file The file to be serialized to
	 */
	public void serializeObject(AESPrimitives primitives, File file) {
		FileOutputStream fos;
		try {
			if (file.exists()) {
				file.delete();
			}
			log.debug("Serializing to {}", file.getAbsolutePath());
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(primitives);
			oos.close();
			fos.close();
			log.debug("Object serialized");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deserialize AES cryptographic primitives
	 * @param file Serialization file
	 * @return Primitives object
	 */
	public AESPrimitives deserializeObject(File file) {
		AESPrimitives primitives = null;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			primitives = (AESPrimitives) ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return primitives;
	}
}
