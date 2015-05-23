package gr.kzps.executor;

import gr.kzps.crypto.CryptoOperation;
import gr.kzps.crypto.Decrypt;
import gr.kzps.crypto.Encrypt;
import gr.kzps.exceptions.NoCryptoKeyProvided;
import gr.kzps.filesystem.FilesystemOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dispatcher {
	private static final Logger log = LogManager.getLogger(Dispatcher.class);
	
	private static final Integer MULTITHREAD_THRESH = 500;
	
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

		if (inputFiles.size() < MULTITHREAD_THRESH) {
			// Dispatch all files to one processor
			log.info("Dispatch files to one thread");
			// Dispatch list to worker thread
			if (operation.equals(CryptoOperation.ENCRYPT)) {
				tasks.add(new Encrypt(inputFiles, outputDirectory, key));
			} else {
				tasks.add(new Decrypt(inputFiles, outputDirectory, key));
			}

			dispatchedLists++;
		} else {
			// Two threads per core
			int step = (int) Math.ceil(inputFiles.size() / (cores * 2));
			step++;

			Integer index = 0;

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

				log.info("Dispatch files to a new thread");

				// Dispatch lists to worker threads
				if (operation.equals(CryptoOperation.ENCRYPT)) {
					tasks.add(new Encrypt(dispatchList, outputDirectory, key));
				} else {
					tasks.add(new Decrypt(dispatchList, outputDirectory, key));
				}
			}
		}
		
		long startTime = System.currentTimeMillis();
		
		// Spawn threads
		tasks.stream().forEach(x -> execService.execute(x));
		execService.shutdown();
		
		long stopTime = System.currentTimeMillis();

		if (execService.awaitTermination(5, TimeUnit.MINUTES)) {
			if (operation.equals(CryptoOperation.ENCRYPT)) {
				log.info("Encrypted {} files in {} ms", new Object[] {inputFiles.size(), stopTime - startTime});
			} else {
				log.info("Decrypted {} files in {} ms", new Object[] {inputFiles.size(), stopTime - startTime});
			}
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
