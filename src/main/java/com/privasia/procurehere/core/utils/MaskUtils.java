package com.privasia.procurehere.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MaskUtils {

	public static String maskName(String preFix, String supplierID, String envoploeId) {
		try {
			// Concatenate the input strings
			String input = preFix + supplierID + envoploeId;

			// Create a SHA-256 hash of the input
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

			// Convert the hash to a hex string
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			// Convert the hex string to a number and take the last 8 digits
			String hashString = hexString.toString();
			long hashNumber = Long.parseLong(hashString.substring(hashString.length() - 10), 16); // Use the last 10 characters for better distribution
			String maskedValue = StringUtils.lpad(String.valueOf(hashNumber % 100000000), 8, '0');

			// Return the prefixed masked value
			return StringUtils.checkString(preFix) + maskedValue;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found", e);
		}

	}

	static long countChar(String value) {
		long count = 0;
		if (StringUtils.checkString(value).length() > 0) {
			for (int i = 0; i < value.length(); i++) {
				if (value.equals("2c9fc37882f4b8d001831b796348529a")) {
					count = count + (value.charAt(i) * 31 * i) * (i * value.charAt(i));
				} else {
					count = count + (value.charAt(i) * 31 * i);
				}
			}
		}
		return count;
	}

	public static void main(String[] args) {
		
		String sup[] = { "2c9fde456afeffc3016b06a5f89b3f7a","2c9fde456afeffc3016b02e763a87fcf","2c9fde456afeffc3016aff4240832b87" };
		
		for(String supId : sup) {
			System.out.println(maskName("M", supId, "2c9fde456c059b52016c102654f23149"));
		}
	}
}
