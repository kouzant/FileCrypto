package gr.kzps.FileCrypto.crypto;

import java.io.Serializable;

public class AESPrimitives implements Serializable{
	private static final long serialVersionUID = 5380589340429089751L;

	private final char[] password;
	private final byte[] seed;
	private final byte[] salt;
	
	public AESPrimitives(char[] password, byte[] seed, byte[] salt) {
		this.password = password;
		this.seed = seed;
		this.salt = salt;
	}
	
	public char[] getPassword() {
		return password;
	}
	
	public byte[] getSeed() {
		return seed;
	}
	
	public byte[] getSalt() {
		return salt;
	}
}
