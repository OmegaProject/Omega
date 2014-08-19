/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.commons.utilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class OmegaDataEncryptionUtility {
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm"
	        .toCharArray();
	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10,
	        (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };

	public static String encrypt(final String property)
	        throws GeneralSecurityException, UnsupportedEncodingException {
		final SecretKeyFactory keyFactory = SecretKeyFactory
		        .getInstance("PBEWithMD5AndDES");
		final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(
		        OmegaDataEncryptionUtility.PASSWORD));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(
		        OmegaDataEncryptionUtility.SALT, 20));
		return OmegaDataEncryptionUtility.base64Encode(pbeCipher.doFinal(property
		        .getBytes("UTF-8")));
	}

	private static String base64Encode(final byte[] bytes) {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Encoder().encode(bytes);
	}

	public static String decrypt(final String property)
	        throws GeneralSecurityException, IOException {
		final SecretKeyFactory keyFactory = SecretKeyFactory
		        .getInstance("PBEWithMD5AndDES");
		final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(
		        OmegaDataEncryptionUtility.PASSWORD));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(
		        OmegaDataEncryptionUtility.SALT, 20));
		return new String(pbeCipher.doFinal(OmegaDataEncryptionUtility
		        .base64Decode(property)), "UTF-8");
	}

	private static byte[] base64Decode(final String property)
	        throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Decoder().decodeBuffer(property);
	}

}
