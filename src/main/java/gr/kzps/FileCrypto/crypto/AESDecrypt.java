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

public class AESDecrypt implements Runnable {
	private static final Logger log = LogManager.getLogger(AESDecrypt.class);

	private final char[] password;
	private final byte[] seed, salt;
	private FilesystemOperations fso;
	private List<File> decryptList;
	private final String outputDir;
	private SecretKeyFactory secretKeyFactory;
	private AlgorithmParameterSpec algorithmParameterSpec;
	private KeySpec keySpec;
	private SecretKey secretKey;
	private Cipher cipher;
	
	public AESDecrypt(char[] password, byte[] seed, byte[] salt, List<File> decryptList, String outputDir) {
		this.password = password;
		this.seed = seed;
		this.salt = salt;
		this.decryptList = decryptList;
		this.outputDir = outputDir;
		
		fso = new FilesystemOperations();
		Security.addProvider(new BouncyCastleProvider());
	}
	
	@Override
	public void run() {
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			algorithmParameterSpec = new IvParameterSpec(seed);
			keySpec = new PBEKeySpec(password, salt, 4000, 256);
			secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, algorithmParameterSpec);
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
		
		decryptList.stream().forEach(x -> {
			// Replace ".enc" with ".dec" for decrypted files
			String newFileName = x.getName().substring(0, x.getName().length() - 3).concat("dec");
			try {
				String absoluteName = Paths.get(outputDir, newFileName).toString();
				fso.writeBytesToFile(new File(absoluteName), decrypt(x));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	private byte[] decrypt(File file) {
		byte[] plaintext = null;
		
		try {
			byte[] data = fso.readFileContent(file);
			plaintext = cipher.doFinal(data);			
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