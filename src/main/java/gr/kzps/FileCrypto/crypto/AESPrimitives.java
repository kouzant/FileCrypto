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

import java.io.Serializable;

public class AESPrimitives implements Serializable{
	private static final long serialVersionUID = 5380589340429089751L;

	private final byte[] password;
	private final byte[] seed;
	private final byte[] salt;
	
	public AESPrimitives(byte[] password, byte[] seed, byte[] salt) {
		this.password = password;
		this.seed = seed;
		this.salt = salt;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public byte[] getSeed() {
		return seed;
	}
	
	public byte[] getSalt() {
		return salt;
	}
}
