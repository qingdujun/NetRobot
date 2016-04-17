package netrobot.utils;

import java.security.MessageDigest;

/**
 * º”√‹À„∑®
 * @author qingdjun
 *
 */
public abstract class Encrypt {

	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	
	public static String encryt(String str,String encrypt){
		String res = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(encrypt);
			byte[] bytes = digest.digest(str.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bytes) {
				int bt = b&0xff;
				if (bt < 16) {
					stringBuffer.append(0);
				}
				stringBuffer.append(Integer.toHexString(bt));
			}
			res = stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
