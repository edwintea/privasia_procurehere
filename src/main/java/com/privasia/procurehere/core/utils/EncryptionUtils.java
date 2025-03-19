/**
 * 
 */
package com.privasia.procurehere.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Nitin Otageri
 *
 */
abstract public class EncryptionUtils {

	private static final String keyString = "adkj@#$02#@adflkj)(*jlj@#$#@LKjasdjlkj<.,mo@#$@#kljlkdsu343";

	/**
	 * Encrypts and encodes the Object and IV for url inclusion 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String[] encryptObject(Object obj) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(stream);
		try {
			// Serialize the object			
			out.writeObject(obj);		
			byte[] serialized = stream.toByteArray();
					
			// Setup the cipher and Init Vector
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        byte[] iv = new byte[cipher.getBlockSize()];
	        new SecureRandom().nextBytes(iv);
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	
	        // Hash the key with SHA-256 and trim the output to 128-bit for the key
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        digest.update(keyString.getBytes());
	        byte[] key = new byte[16];
	        System.arraycopy(digest.digest(), 0, key, 0, key.length);
	        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
	   
	        // encrypt
	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	       
	        // Encrypt & Encode the input
	        byte[] encrypted = cipher.doFinal(serialized);       
	        byte[] base64Encoded = Base64.encodeBase64URLSafe(encrypted);
	        String base64String = new String(base64Encoded);
	        String urlEncodedData = URLEncoder.encode(base64String,"UTF-8");
	       
	        // Encode the Init Vector
	        byte[] base64IV = Base64.encodeBase64URLSafe(iv);
	        String base64IVString = new String(base64IV);
	        String urlEncodedIV = URLEncoder.encode(base64IVString, "UTF-8");

	        return new String[] {urlEncodedData, urlEncodedIV};
		}finally {
			stream.close();
			out.close();
		}
	}
	
	/**
	 * Decrypts the String and serializes the object
	 * @param base64Data
	 * @param base64IV
	 * @return
	 * @throws Exception
	 */
	public static Object decryptObject(String base64Data, String base64IV) throws Exception {
		// Decode the data
		byte[] encryptedData = Base64.decodeBase64(base64Data.getBytes()); 
		
		// Decode the Init Vector
		byte[] rawIV = Base64.decodeBase64(base64IV.getBytes()); 
		
		// Configure the Cipher
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(rawIV);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(keyString.getBytes());
        byte[] key = new byte[16];
        System.arraycopy(digest.digest(), 0, key, 0, key.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		
		// Decrypt the data..
		byte[] decrypted = cipher.doFinal(encryptedData);
		
		// Deserialize the object		
		ByteArrayInputStream stream = new ByteArrayInputStream(decrypted);
		ObjectInput in = new ObjectInputStream(stream);
		Object obj = null;
		try {
			obj = in.readObject(); 
		}finally {
			stream.close();
			in.close();
		}
		return obj;
	}
	
	public static void main(String[] args) {
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("id","Hello");
		map.put("topSecret","somesecret");
		
		try {
			{
			String[] encrypted = encryptObject(map);	
            // url may differ.. based upon project initial context
			System.out.println("d="+encrypted[0]+"\nv="+encrypted[1]);

			Object obj = decryptObject(encrypted[0], encrypted[1]);
			System.out.println("Object Decrypted: "+obj.toString());

			}
			{
			String[] encrypted = encryptObject("Data String");	
            // url may differ.. based upon project initial context
			System.out.println("d="+encrypted[0]+"\nv="+encrypted[1]);

			Object obj = decryptObject(encrypted[0], encrypted[1]);
			System.out.println("Object Decrypted: "+obj.toString());
			}

		}catch(Exception e) {
			//logger.debug("Unable to encrypt view id: "+id, e);
			System.out.println("error : " + e.getMessage());
			e.printStackTrace();
		}
	}

}
