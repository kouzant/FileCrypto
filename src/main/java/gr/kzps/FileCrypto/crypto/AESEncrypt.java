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
package gr.kzps.FileCrypto.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

/**
 * Implementation of AES-256 cryptographic algorithm. Use for the encryption thread
 * @author Antonis Kouzoupis
 *
 */
public class AESEncrypt implements Runnable {
	private static final Logger log = LogManager.getLogger(AESEncrypt.class);

	private final char[] password;
	private final byte[] seed, salt;
	private FilesystemOperations fso;
	private List<File> encryptList;
	private final String outputDir;
	private SecretKeyFactory secretKeyFactory;
	private AlgorithmParameterSpec algorithmParameterSpec;
	private KeySpec keySpec;
	private SecretKey secretKey;
	private Cipher cipher;
	
	public AESEncrypt(char[] password, byte[] seed, byte[] salt, List<File> encryptList, String outputDir) {
		this.password = password;
		this.seed = seed;
		this.salt = salt;
		this.encryptList = encryptList;
		this.outputDir = outputDir;
		
		fso = new FilesystemOperations();
		Security.addProvider(new BouncyCastleProvider());
	}
	
	@Override
	public void run() {
		try {
			log.debug("Initializing cipher");
			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			algorithmParameterSpec = new IvParameterSpec(seed);
			keySpec = new PBEKeySpec(password, salt, 4000, 256);
			secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		encryptList.stream().forEach(x -> {
			String newFileName = x.getName().concat(".enc");
			try {
				String absoluteName = Paths.get(outputDir, newFileName).toString();
				byte[] ciphertext = encrypt(x);
				fso.writeBytesToFile(new File(absoluteName), ciphertext);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	/**
	 * Perform the actual encryption operation.
	 * 
	 * @param file File to be encrypted.
	 * @return Encrypted bytes of the file
	 */
	private byte[] encrypt(File file) {
		byte[] ciphertext = null;

		try {
			log.debug("Encrypting {} bytes file", file.length());
			byte[] data = fso.readFileContent(file);
			ciphertext = cipher.doFinal(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return ciphertext;
	}
}
