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
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSACipher<T> {
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	private T key = null;
	private KeyFactory keyFactory;
	
	public RSACipher(CryptoOperation operation, byte[] cryptoKey) {
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		
		if (operation.equals(CryptoOperation.ENCRYPT)) {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cryptoKey);
			try {
				key = (T) keyFactory.generatePublic(keySpec);
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		} else if (operation.equals(CryptoOperation.DECRYPT)) {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(cryptoKey);
			try {
				key = (T) keyFactory.generatePrivate(keySpec);
			} catch (InvalidKeySpecException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public byte[] encrypt(byte[] plaintext) {
		byte[] ciphertext = null;
		
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, (PublicKey) key);
			ciphertext = cipher.doFinal(plaintext);			
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}
		
		return ciphertext;
	}
	
	public byte[] decrypt(byte[] ciphertext) {
		byte[] plaintext = null;
		
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, (PrivateKey) key);
			plaintext = cipher.doFinal(ciphertext);			
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}
		
		return plaintext;
	}
}
