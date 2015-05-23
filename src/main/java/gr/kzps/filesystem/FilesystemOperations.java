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
	
	public byte[] readFileContent(File file) throws IOException {
		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		
		return content;
	}
	
	public void writeBytesToFile(File file, byte[] content) throws IOException {
		Files.write(Paths.get(file.getAbsolutePath()), content);
	}
}
