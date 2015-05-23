package gr.kzps.executor;

import gr.kzps.crypto.CryptoOperation;
import gr.kzps.crypto.Encrypt;
import gr.kzps.exceptions.NoCryptoKeyProvided;
import gr.kzps.filesystem.FilesystemOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Dispatcher {
	private static List<Runnable> tasks;
	private static FilesystemOperations fso;

	public static int dispatch(CryptoOperation operation,
			String inputDirectory, String outputDirectory, String cryptoKey)
			throws FileNotFoundException, NotDirectoryException,
			InterruptedException, NoCryptoKeyProvided {

		fso = new FilesystemOperations();
		List<File> inputFiles = fso.enumerateInputFiles(inputDirectory);
		tasks = new ArrayList<Runnable>();
		byte[] key = readCryptoKey(cryptoKey);

		// For testing
		int dispatchedLists = 0;

		Integer cores = Runtime.getRuntime().availableProcessors();

		ExecutorService execService = Executors.newFixedThreadPool(cores * 2);

		// TODO remove magic number
		if (inputFiles.size() < 20) {
			// Dispatch all files to one processor
			System.out.println("Dispatch to one thread");
			// TODO Dispatch list to worker thread
			tasks.add(new Encrypt(inputFiles, outputDirectory, key));

			dispatchedLists++;
		} else {
			// Two threads per core
			int step = (int) Math.ceil(inputFiles.size() / (cores * 2));
			step++;

			Integer index = 0;

			for (File file : inputFiles) {
				System.err.println(file.getAbsolutePath());
			}

			while (index < inputFiles.size()) {
				List<File> dispatchList;
				if (index + step > inputFiles.size()) {
					int diff = index + step - inputFiles.size();
					dispatchList = inputFiles.subList(index, index + step
							- diff);
				} else {
					dispatchList = inputFiles.subList(index, index + step);

				}

				dispatchedLists++;
				index += step;

				// TODO Dispatch lists to worker threads
				tasks.add(new Encrypt(dispatchList, outputDirectory, key));
			}
		}

		tasks.stream().forEach(x -> execService.execute(x));

		execService.shutdown();

		if (execService.awaitTermination(5, TimeUnit.MINUTES)) {
			System.out.println("Encryption terminated!");
		}

		return dispatchedLists;
	}

	private static byte[] readCryptoKey(String key) throws NoCryptoKeyProvided {
		File keyFile = null;

		if (key != null) {
			keyFile = new File(key);

			if (keyFile.exists()) {
				try {
					return fso.readFileContent(keyFile);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		throw new NoCryptoKeyProvided("Could not read the cryptographic key");
	}
}
