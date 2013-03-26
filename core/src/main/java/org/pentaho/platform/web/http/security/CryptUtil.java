/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.pentaho.platform.web.http.security;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptUtil {

	private static final String secKey = "7A1B3"; // TODO maybe we need to share and save these parameters in another
													// way

	private static final String cryptoKey = "12AB3ED12305"; // TODO maybe we need to share and save these parameters in
															// another way

	private CryptUtil() {
	}

	public static String base64Dencode(String str) {
		String result = "";
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] decodedBytes = decoder.decodeBuffer(str);
			result = new String(decodedBytes);
		} catch (Exception e) {
			//
		}
		return result;
	}

	public static String base64Encode(String str) {
		String result = "";
		try {
			BASE64Encoder encoder = new BASE64Encoder();
			String encodedBytes = encoder.encodeBuffer(str.getBytes());
			result = new String(encodedBytes);
		} catch (Exception e) {
			//
		}
		return result;
	}

	private static String decodeMd5(String str) {
		String result = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(cryptoKey.getBytes("utf-8"));
			Cipher rc4 = Cipher.getInstance("RC4");
			rc4.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hash, "RC4"));
			result = new String(rc4.doFinal(Hex.decodeHex(str.toCharArray())));
		} catch (Exception e) {
			//
		}
		return result;
	}

	private static String encodeMd5(String str) {
		String result = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(cryptoKey.getBytes("utf-8"));
			Cipher rc4 = Cipher.getInstance("RC4");
			rc4.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hash, "RC4"));
			result = new String(Hex.encodeHex((rc4.doFinal(str.getBytes("utf-8")))));
		} catch (Exception e) {
			//
		}
		return result;
	}

	public static String generateToken(int userId, String session) {
		String result = "";
		try {
			result = encodeMd5("" + userId + CryptUtil.secKey + session);
		} catch (Exception e) {
			//
		}
		return result;
	}

	public static boolean isTokenValid(String token, int userId, String session) {
		boolean result = false;
		try {
			result = decodeMd5(token).equalsIgnoreCase("" + userId + CryptUtil.secKey + session);
		} catch (Exception e) {
			//
		}
		return result;
	}

	public static int getUserId(String token) {
		int result = 0;
		try {
			result = Integer.parseInt(decodeMd5(token).split(CryptUtil.secKey)[0]);
		} catch (Exception e) {
			//
		}
		return result;
	}

	public static String getSession(String token) {
		String result = "";
		try {
			result = decodeMd5(token).split(CryptUtil.secKey)[1];
		} catch (Exception e) {
			//
		}
		return result;
	}
}
