package dk.tim.properties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

public class Encrypter {
	public static String encrypt(String text) {
		byte[] bytes;
		try {
			bytes = text.getBytes("UTF-8");
			return Base64.getEncoder().encodeToString(bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to encode: " + text + ". " + e.getMessage(), e);
		}
	}

	public static String decrypt(String encodedText) {
		byte[] decoded = Base64.getDecoder().decode(encodedText);
		return new String(decoded, Charset.forName("UTF-8"));
	}

	public static void main(String[] args) throws IOException {
		String pw = "<TYPE PW HERE>";
		String encrypted = Encrypter.encrypt(pw);
		System.out.println("This is your encrypted password: " + encrypted);
	}
}