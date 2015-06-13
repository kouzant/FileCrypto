package gr.kzps.FileCrypto.crypto.Tests;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

import gr.kzps.FileCrypto.crypto.AESPrimitives;
import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.crypto.RSACipher;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;
import gr.kzps.FileCrypto.filesystem.FilesystemOperations;

public class TestSerialization {
	private static RSACipher<PublicKey> encryptCipher;
	private static RSACipher<PrivateKey> decryptCipher;
	private static FilesystemOperations fso;
	private final String password = "password";
	private final String salt = "salt";
	private final String seed = "seed";
	private final String metafileName = "FileCrypto-Test-Metadata";
	
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
	public void testSerialize() {
		AESPrimitives primitives = new AESPrimitives(password.getBytes(), seed.getBytes(), salt.getBytes());

		File metafile = new File(metafileName);
		fso.serializeObject(primitives, metafile);
		
		AESPrimitives desPrim = fso.deserializeObject(metafile);
		
		assertArrayEquals(password.getBytes(), desPrim.getPassword());
		assertArrayEquals(seed.getBytes(), desPrim.getSeed());
		assertArrayEquals(salt.getBytes(), desPrim.getSalt());
		
		metafile.delete();
	}
	
	@Test
	public void testEncryptedSerialize() {
		byte[] encPass = encryptCipher.encrypt(password.getBytes());
		byte[] encSeed = encryptCipher.encrypt(seed.getBytes());
		byte[] encSalt = encryptCipher.encrypt(salt.getBytes());
		AESPrimitives primitives = new AESPrimitives(encPass, encSeed, encSalt);
		
		File metafile = new File(metafileName);
		fso.serializeObject(primitives, metafile);
		
		AESPrimitives desPrim = fso.deserializeObject(metafile);
		
		assertNotEquals(password.getBytes(), desPrim.getPassword());
		assertNotEquals(seed.getBytes(), desPrim.getSeed());
		assertNotEquals(salt.getBytes(), desPrim.getSalt());
				
		byte[] decPass = decryptCipher.decrypt(desPrim.getPassword());
		byte[] decSeed = decryptCipher.decrypt(desPrim.getSeed());
		byte[] decSalt = decryptCipher.decrypt(desPrim.getSalt());
		
		assertArrayEquals(password.getBytes(), decPass);
		assertArrayEquals(seed.getBytes(), decSeed);
		assertArrayEquals(salt.getBytes(), decSalt);
		
		metafile.delete();
	}
}
