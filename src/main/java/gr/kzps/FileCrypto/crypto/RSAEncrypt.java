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

import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Runnable class to encrypt files
 * 
 * @author Antonis Kouzoupis
 *
 */
public class RSAEncrypt implements Runnable {
	private final List<File> encryptList;
	private final String outputDir;
	private PublicKey key = null;
	private FilesystemOperations fso;
	
	public RSAEncrypt(List<File> encryptList, String outputDir, byte[] cryptoKey) {
		this.encryptList = encryptList;
		this.outputDir = outputDir;
		this.fso = new FilesystemOperations();
		
		Security.addProvider(new BouncyCastleProvider());
		
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cryptoKey);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			key = keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		encryptList.stream().forEach(x -> {
			String newFileName = x.getName().concat(".enc");
			try {
				String absoluteName = Paths.get(outputDir, newFileName).toString();
				fso.writeBytesToFile(new File(absoluteName), encrypt(x));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}
	
	/**
	 * Do the actual encryption
	 * 
	 * @param file The file to be encrypted
	 * @return Encrypted content of the file
	 */
	private byte[] encrypt(File file) {
		byte[] ciphertext = null;
		
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] data = fso.readFileContent(file);
			ciphertext = cipher.doFinal(data);
			
			return ciphertext;
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
		
		return ciphertext;
	}

}
