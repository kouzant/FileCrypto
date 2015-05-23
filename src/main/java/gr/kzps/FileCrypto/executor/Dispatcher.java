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
package gr.kzps.FileCrypto.executor;

import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.crypto.Decrypt;
import gr.kzps.FileCrypto.crypto.Encrypt;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

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

/**
 * Recognize files to be encrypted/decrypted and dispatch them to
 * encryption/decryption threads
 * 
 * @author Antonis Kouzoupis
 *
 */
public class Dispatcher {
	private static final Logger log = LogManager.getLogger(Dispatcher.class);
	
	// Under some threshold create only one thread
	private static final Integer MULTITHREAD_THRESH = 500;
	
	private static List<Runnable> tasks;
	private static FilesystemOperations fso;

	/**
	 * Static method that dispaches files to encryption/decryption threads
	 * 
	 * @param operation Encryption or Decryption operation
	 * @param inputDirectory Directory where the files to be encrypted/decrypted reside
	 * @param outputDirectory Output directory of the cryptographics operation
	 * @param cryptoKey Cryptographic key
	 * @return Number of dispatched lists with files. For testing purposes
	 * @throws FileNotFoundException
	 * @throws NotDirectoryException
	 * @throws InterruptedException
	 * @throws NoCryptoKeyProvided
	 */
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

	/**
	 * Read cryptographic key from disk
	 * 
	 * @param key Path to the key, parsed from command line argument
	 * @return Cryptographic key
	 * @throws NoCryptoKeyProvided
	 */
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