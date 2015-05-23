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
package gr.kzps.FileCrypto.executor;

import gr.kzps.FileCrypto.crypto.CryptoOperation;
import gr.kzps.FileCrypto.exceptions.NoCryptoKeyProvided;
import gr.kzps.FileCrypto.executor.Dispatcher;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import org.junit.Test;

import static org.junit.Assert.*;

public class DispatcherTest {
	
	@Test
	public void testNumberOfLists() {
		String input = "input_test";
		String output = "output_test";
		String cryptoKey = "keys/privateKey.key";
		
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
