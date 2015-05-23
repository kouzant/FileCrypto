package gr.kzps.executor;

import gr.kzps.crypto.CryptoOperation;
import gr.kzps.exceptions.NoCryptoKeyProvided;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import org.junit.Test;

import static org.junit.Assert.*;

public class DispatcherTest {
	
	@Test
	public void testNumberOfLists() {
		String input = "/home/antonis/Tobii/FileCrypto/input_test";
		String output = "/home/antonis/Tobii/FileCrypto/output_test";
		String cryptoKey = "/home/antonis/Tobii/FileCrypto/keys/privateKey.key";
		
		try {
			int lists = Dispatcher.dispatch(CryptoOperation.DECRYPT, input, output, cryptoKey);
			
			int threads = Runtime.getRuntime().availableProcessors() * 2;
			
			assertTrue(lists <= threads);
		} catch (NotDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (NoCryptoKeyProvided ex) {
			ex.printStackTrace();
		}
	}
}
