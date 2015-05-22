package gr.kzps.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
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
			throw new NotDirectoryException(inputDirectory.getName());
		}

		if (inputDirectory.exists()) {
			fileArray = inputDirectory.listFiles();
		} else {
			throw new FileNotFoundException();
		}
		
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) {
				inputFiles.add(fileArray[i]);
			}
		}
		
		return inputFiles;
	}
}
