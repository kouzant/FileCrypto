package gr.kzps.exceptions;

public class NoCryptoKeyProvided extends Exception {
	private static final long serialVersionUID = -2903756957878990352L;
	
	public NoCryptoKeyProvided() {
		
	}
	
	public NoCryptoKeyProvided(String message) {
		super(message);
	}
}
