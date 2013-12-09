package edu.umassmed.omega.commons;

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

public class OmegaDataEncryption {
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm"
	        .toCharArray();
	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10,
	        (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };

	public static String encrypt(final String property)
	        throws GeneralSecurityException, UnsupportedEncodingException {
		final SecretKeyFactory keyFactory = SecretKeyFactory
		        .getInstance("PBEWithMD5AndDES");
		final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(
		        OmegaDataEncryption.PASSWORD));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(
		        OmegaDataEncryption.SALT, 20));
		return OmegaDataEncryption.base64Encode(pbeCipher.doFinal(property
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
		        OmegaDataEncryption.PASSWORD));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(
		        OmegaDataEncryption.SALT, 20));
		return new String(pbeCipher.doFinal(OmegaDataEncryption
		        .base64Decode(property)), "UTF-8");
	}

	private static byte[] base64Decode(final String property)
	        throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Decoder().decodeBuffer(property);
	}

}
