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

import gr.kzps.FileCrypto.crypto.AESDecrypt;
import gr.kzps.FileCrypto.crypto.AESEncrypt;
import gr.kzps.FileCrypto.crypto.AESPrimitives;
import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.crypto.RSACipher;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

/**
 * Recognize files to be encrypted/decrypted and dispatch them to
 * encryption/decryption threads
 * 
 * @author Antonis Kouzoupis
 *
 */
public class Dispatcher {
	private static final Logger log = LogManager.getLogger(Dispatcher.class);

	private static final String META_FILENAME = "FileCrypto_AESmetadata";
	private static List<Runnable> tasks;
	private static FilesystemOperations fso;
	private static SecureRandom rand;

	/**
	 * Static method that dispaches files to encryption/decryption threads
	 * 
	 * @param operation
	 *            Encryption or Decryption operation
	 * @param inputDirectory
	 *            Directory where the files to be encrypted/decrypted reside
	 * @param outputDirectory
	 *            Output directory of the cryptographics operation
	 * @param cryptoKey
	 *            Cryptographic key
	 * @return Number of dispatched lists with files. For testing purposes
	 * @throws FileNotFoundException
	 * @throws NotDirectoryException
	 * @throws InterruptedException
	 * @throws NoCryptoKeyProvided
	 */
	public static int dispatch(CryptoOperation operation,
			String inputDirectory, String outputDirectory, Integer threshold,
			String cryptoKey) throws FileNotFoundException,
			NotDirectoryException, InterruptedException, NoCryptoKeyProvided {

		fso = new FilesystemOperations();
		// TODO Implement option for excluding files
		List<String> excludeFiles = new ArrayList<String>();
		excludeFiles.add(META_FILENAME);
		List<File> inputFiles = fso.enumerateInputFiles(inputDirectory,
				excludeFiles);
		tasks = new ArrayList<Runnable>();
		rand = new SecureRandom();
		char[] password = null;
		char[] passwordCopy = null;
		byte[] seed = null;
		byte[] salt = null;

		byte[] key = fso.readCryptoKey(cryptoKey);

		// For testing
		int dispatchedLists = 0;

		Integer cores = Runtime.getRuntime().availableProcessors();

		ExecutorService execService = Executors.newFixedThreadPool(cores * 2);

		// If output dir does not exist, create it
		File output = new File(outputDirectory);
		if (!output.exists())
			output.mkdir();

		if (operation.equals(CryptoOperation.ENCRYPT)) {
			log.debug("Construct AES primitives");
			password = generatePassword();
			// Copy in order to use it later, toBytes() clear the cache
			passwordCopy = password.clone();
			log.debug("Generating seed");
			seed = rand.generateSeed(16);
			log.debug("Generating salt");
			salt = rand.generateSeed(32);

			byte[] passBytes = toBytes(password);
			RSACipher<PublicKey> rsaCipher = new RSACipher<PublicKey>(
					CryptoOperation.ENCRYPT, key);
			byte[] encryptedPass = rsaCipher.encrypt(passBytes);
			byte[] encryptedSeed = rsaCipher.encrypt(seed);
			byte[] encryptedSalt = rsaCipher.encrypt(salt);

			AESPrimitives primitives = new AESPrimitives(encryptedPass,
					encryptedSeed, encryptedSalt);
			String absoluteName = Paths.get(outputDirectory, META_FILENAME)
					.toString();
			fso.serializeObject(primitives, new File(absoluteName));
			primitives = null;
		} else if (operation.equals(CryptoOperation.DECRYPT)) {
			log.debug("Recover AES primitives");
			String absoluteName = Paths.get(inputDirectory, META_FILENAME)
					.toString();
			AESPrimitives primitives = fso.deserializeObject(new File(
					absoluteName));
			byte[] encryptedPass = primitives.getPassword();
			byte[] encryptedSeed = primitives.getSeed();
			byte[] encryptedSalt = primitives.getSalt();
			primitives = null;

			RSACipher<PrivateKey> rsaCipher = new RSACipher<PrivateKey>(
					CryptoOperation.DECRYPT, key);
			byte[] bytePass = rsaCipher.decrypt(encryptedPass);
			password = toChar(bytePass);
			seed = rsaCipher.decrypt(encryptedSeed);
			salt = rsaCipher.decrypt(encryptedSalt);
		}

		
		log.debug("Started with threshold: {}", threshold);

		if (inputFiles.size() < threshold) {
			// Dispatch all files to one processor
			log.info("Dispatch files to one thread");
			// Dispatch list to worker thread
			if (operation.equals(CryptoOperation.ENCRYPT)) {
				tasks.add(new AESEncrypt(passwordCopy, seed, salt, inputFiles,
						outputDirectory));
			} else if (operation.equals(CryptoOperation.DECRYPT)) {
				tasks.add(new AESDecrypt(password, seed, salt, inputFiles,
						outputDirectory));
			}

			dispatchedLists++;
		} else {
			// Two threads per core
			int step = (int) Math.ceil(inputFiles.size() / (cores * 2));
			step++;

			log.info("Dispatch files to multiple threads");

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

				// Dispatch lists to worker threads
				if (operation.equals(CryptoOperation.ENCRYPT)) {
					tasks.add(new AESEncrypt(passwordCopy, seed, salt, inputFiles,
							outputDirectory));
				} else if (operation.equals(CryptoOperation.DECRYPT)) {
					tasks.add(new AESDecrypt(password, seed, salt, inputFiles,
							outputDirectory));
				}
			}
		}

		long startTime = System.currentTimeMillis();

		if (tasks.size() > 0) {
			// Spawn threads
			tasks.stream().forEach(x -> execService.execute(x));
			execService.shutdown();
			
			long stopTime = System.currentTimeMillis();

			if (execService.awaitTermination(5, TimeUnit.MINUTES)) {
				
				if (operation.equals(CryptoOperation.ENCRYPT)) {
					log.info("Encrypted {} files in {} ms", new Object[] {
							inputFiles.size(), stopTime - startTime });
				} else {
					log.info("Decrypted {} files in {} ms", new Object[] {
							inputFiles.size(), stopTime - startTime });
				}
			}
		}

		return dispatchedLists;
	}

	/**
	 * Convert byte array to char array
	 * 
	 * @param bytes
	 *            Password byte array
	 * @return Password char array
	 */
	private static char[] toChar(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		CharBuffer charBuffer = Charset.forName("UTF-8").decode(byteBuffer);
		char[] chars = Arrays.copyOfRange(charBuffer.array(), 0, charBuffer.limit());
		// Clear sensitive data
		Arrays.fill(charBuffer.array(), '\u0000');
		
		return chars;
	}

	/**
	 * Convert password to byte array
	 * 
	 * @param chars
	 *            Password
	 * @return Password in byte array
	 */
	private static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
				byteBuffer.position(), byteBuffer.limit());
		// Clear sensitive data
		Arrays.fill(charBuffer.array(), '\u0000');
		Arrays.fill(byteBuffer.array(), (byte) 0);
		
		return bytes;
	}

	/**
	 * Generate random password
	 * 
	 * @return password
	 */
	private static char[] generatePassword() {
		log.debug("Generating password");
		return new BigInteger(130, rand).toString(32).toCharArray();
	}
}
