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
package gr.kzps.FileCrypto.crypto.Tests;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertArrayEquals;

import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.crypto.RSACipher;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

public class TestAsymmetric {
	private static RSACipher<PublicKey> encryptCipher;
	private static RSACipher<PrivateKey> decryptCipher;
	private static FilesystemOperations fso;
	private byte[] ciphertext;
	private final String plaintext = "This is a plaintext!";
	
	@BeforeClass
	public static void before() {
		fso = new FilesystemOperations();
		byte[] encryptKey = null;
		byte[] decryptKey = null;
		
		try {
			encryptKey = fso.readCryptoKey("keys_test/publicKey.crt");
			decryptKey = fso.readCryptoKey("keys_test/privateKey.key");
		} catch (NoCryptoKeyProvided e) {
			e.getMessage();
		}
		
		encryptCipher = new RSACipher<PublicKey>(CryptoOperation.ENCRYPT, encryptKey);
		decryptCipher = new RSACipher<PrivateKey>(CryptoOperation.DECRYPT, decryptKey);
	}
	
	@Test
	public void cryptoTest() {
		ciphertext = encryptCipher.encrypt(plaintext.getBytes());
		assertNotEquals(plaintext.getBytes(), ciphertext);
		
		byte[] deciphered = decryptCipher.decrypt(ciphertext);
		assertArrayEquals(plaintext.getBytes(), deciphered);
	}
}
