package gr.kzps.executor;

import gr.kzps.crypto.CryptoOperation;
import gr.kzps.filesystem.FilesystemOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.util.List;

public class Dispatcher {
	public static int dispatch(CryptoOperation operation,
			String inputDirectory, String OutputDirectory)
			throws FileNotFoundException, NotDirectoryException {

		FilesystemOperations fso = new FilesystemOperations();
		List<File> inputFiles = fso.enumerateInputFiles(inputDirectory);
		
		// For testing
		int dispatchedLists = 0;
		
		Integer cores = Runtime.getRuntime().availableProcessors();
		
		// TODO remove magic number
		if (inputFiles.size() < 20) {
			// Dispatch all files to one processor
			System.out.println("Dispatch to one thread");
			// TODO Dispatch list to worker thread
			
			dispatchedLists++;
		} else {
			// Two threads per core
			int step =  (int) Math.ceil(inputFiles.size() / (cores * 2));
			step++;
						
			Integer index = 0;
			
			for (File file : inputFiles) {
				System.err.println(file.getAbsolutePath());
			}
			
			while (index < inputFiles.size()) {
				List<File> dispatchList;
				if (index + step > inputFiles.size()) {
					int diff = index + step - inputFiles.size();
					dispatchList = inputFiles.subList(index, index + step - diff);
				} else {
					dispatchList = inputFiles.subList(index, index + step);

				}
				
				dispatchedLists++;
				index += step;
				
				// TODO Dispatch lists to worker threads
			}
		}
		
		return dispatchedLists;
	}
}
