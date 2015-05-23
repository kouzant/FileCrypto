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
package gr.kzps.crypto;

import gr.kzps.filesystem.FilesystemOperations;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Decrypt implements Runnable {	
	private final List<File> decryptList;
	private final String outputDir;
	private PrivateKey key = null;
	private FilesystemOperations fso;
	
	public Decrypt(List<File> decryptList, String outputDir, byte[] cryptoKey) {
		this.decryptList = decryptList;
		this.outputDir = outputDir;
		this.fso = new FilesystemOperations();
		
		Security.addProvider(new BouncyCastleProvider());
		
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(cryptoKey);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			key = keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
	}
	@Override
	public void run() {
		decryptList.stream().forEach(x -> {
			// Replace ".enc" with ".dec" for decrypted files
			String newFileName = x.getName().substring(0, x.getName().length() - 3).concat("dec");
			try {
				String absoluteName = outputDir.concat("/").concat(newFileName);
				fso.writeBytesToFile(new File(absoluteName), decrypt(x));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	private byte[] decrypt(File file) {
		byte[] plaintext = null;
		
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] data = fso.readFileContent(file);
			plaintext = cipher.doFinal(data);
			
			return plaintext;
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}
		
		return plaintext;
	}
}
