package com.toraysoft.utils.encrypt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.annotation.SuppressLint;
import android.util.Log;

public class RSA {

	private static final String ALGORITHM = "RSA";

	public static PublicKey getPublicKeyFromX509(String algorithm, String bysKey)
			throws NoSuchAlgorithmException, Exception {
		byte[] decodedKey = Base64.decode(bysKey.getBytes());
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(x509);
	}

	@SuppressLint("TrulyRandom")
	public static String encrypt(String content, String key) {
		try {
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, key);

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);

			byte plaintext[] = content.getBytes("UTF-8");
			byte[] output = cipher.doFinal(plaintext);

			String s = new String(Base64.encode(output));
			Log.d("RSAManager encode", s + "");
			return s;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey.getBytes()));
			KeyFactory keyf = KeyFactory.getInstance("RSA", "BC");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] enBytes = Base64.decode(content.getBytes());
			byte[] deBytes = cipher.doFinal(enBytes);

			return new String(deBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
