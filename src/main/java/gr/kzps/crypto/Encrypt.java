package gr.kzps.crypto;

import gr.kzps.filesystem.FilesystemOperations;

import java.io.File;
import java.io.IOException;
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

public class Encrypt implements Runnable {
	private final List<File> encryptList;
	private final String outputDir;
	private PublicKey key = null;
	private FilesystemOperations fso;
	
	public Encrypt(List<File> encryptList, String outputDir, byte[] cryptoKey) {
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
				String absoluteName = outputDir.concat("/").concat(newFileName);
				fso.writeBytesToFile(new File(absoluteName), encrypt(x));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
	}
	
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
