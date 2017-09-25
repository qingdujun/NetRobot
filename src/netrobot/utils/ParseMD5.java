package netrobot.utils;

/**
 * MD5º”√‹
 * @author qingdujun
 *
 */
public class ParseMD5 extends Encrypt{

	/**
	 * 32ŒªMD5
	 * @param str
	 * @return
	 */

	public static String parseStr2MD5(String str) {
		return encryt(str, MD5);
	}
	
	public static String parseStr2UpperMD5(String str){
		return parseStr2MD5(str).toUpperCase();
	}
	/**
	 * 16ŒªMD5
	 * @param str
	 * @return
	 */
	public static String parseStr216MD5 (String str) {
		return parseStr2MD5(str).substring(8, 24);
	}

	public static String parseStr2Upper16MD5 (String str) {
		return parseStr2UpperMD5(str).substring(8, 24);
	}
}
