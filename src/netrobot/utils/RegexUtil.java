package netrobot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 正则工具类
 * @author qingdujun
 *
 */
public class RegexUtil {

	private static String rootUrlRegex = "(http://.*?/)";
	private static String currentUrlRegex = "(http://.*/)";
	private static String ChRegex = "([\u4e00-\u9fa5]+)";
	private static String EMOJI = "[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]";
	

	public static String getString(String dealStr, String regexStr, String splitStr, int n){
		String reStr = "";
		if (dealStr == null || regexStr == null || n < 1 || dealStr.isEmpty()){
			return reStr;
		}
		splitStr = (splitStr == null) ? "" : splitStr;
		Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(dealStr);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			stringBuffer.append(matcher.group(n).trim());
			stringBuffer.append(splitStr);
		}
		reStr = stringBuffer.toString();
		if (splitStr != "" && reStr.endsWith(splitStr)){
			reStr = reStr.substring(0, reStr.length() - splitStr.length());
		}
		return reStr;
	}

	public static String getString(String dealStr, String regexStr, int n){
		return getString(dealStr, regexStr, null, n);
	}
	
	
	public static String getFirstString(String deal, String regex, int n){
		if (null == deal || null == regex || n < 1) {
			return "";
		}
		//忽略大小写、换行符
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher matcher = pattern.matcher(deal);
		while (matcher.find()) {
			return matcher.group(n).trim();
		}
		return "";
	}
	
	public static List<String> getList(String deal, String regex, int n){
		List<String> list = new ArrayList<String>();
		if (null == deal || null == regex || n < 1) {
			return list;
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher matcher = pattern.matcher(deal);
		while (matcher.find()) {
			list.add(matcher.group(n).trim());
		}
		return list;
	}
	
	public static List<String[]> getList(String deal, String regex, int[] array){
		List<String[]> list = new ArrayList<String[]>();
		if (null == deal || null == regex || null == array) {
			return list;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] < 1) {
				return list;
			}
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher matcher = pattern.matcher(deal);
		while (matcher.find()) {
			String[] ss = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				ss[i] = (matcher.group(array[i]).trim());
			}
			list.add(ss);
		}
		return list;
	}
	public static List<String> getStringArray(String dealStr, String regexStr, int[] array) {
		List<String> reStringList = new ArrayList<String>();
		if (dealStr == null || regexStr == null || array == null) {
			return reStringList;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] < 1) {
				return reStringList;
			}
		}
		Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(dealStr);
		while (matcher.find()) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				sb.append(matcher.group(array[i]).trim());
			}
			reStringList.add(sb.toString());
		}
		return reStringList;
	}


	/**
	 * 获取第一个
	 * @param deal
	 * @param regex
	 * @param array 正则位置数组
	 * @return
	 */
	public static String[] getFirstArray(String deal, String regex, int[] array) {
		if (deal == null || regex == null || array == null) {
			return null;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] < 1) {
				return null;
			}
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(deal);
		while (matcher.find()) {
			String[] ss = new String[array.length]; 
			for (int i = 0; i < array.length; i++) {
				ss[i] = matcher.group(array[i]).trim();
			}
			return ss;
		}
		return null;
	}

	/**
	 * 组装网址，网页的url
	 * @param url
	 * @param currentUrl
	 * @return
	 */
	private static String getHttpUrl(String url, String currentUrl){
		try {
			//新增的replaceAll  转化有些地址接口中的转化地址，如： \/test\/1.html
			url = encodeUrlCh(url).replaceAll("\\\\/", "/");
		} catch (Exception e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		}
		if (url.indexOf("http") == 0){
			return url;
		}
		if  (url.indexOf("/") == 0){
			return getFirstString(currentUrl, rootUrlRegex, 1) + url.substring(1);
		} 
		if  (url.indexOf("\\/") == 0){
			return getFirstString(currentUrl, rootUrlRegex, 1) + url.substring(2);
		}
		return getFirstString(currentUrl, currentUrlRegex, 1) + url;
	}

	/**
	 * 获取和正则匹配的绝对链接地址
	 * @param deal
	 * @param regex
	 * @param currentUrl
	 * @param n
	 * @return
	 */
	public static List<String> getArrayList(String deal, String regex, String currentUrl, int n){
		List<String> reArrayList = new ArrayList<String>();
		if (deal == null || regex == null || n < 1 || deal.isEmpty()){
			return reArrayList;
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(deal);
		while (matcher.find()) {
			reArrayList.add(getHttpUrl(matcher.group(n).trim(), currentUrl));
		}
		return reArrayList;
	}

	/**
	 * 将连接地址中的中文进行编码处理
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeUrlCh (String url) throws UnsupportedEncodingException {
		while (true) {
			String s = getFirstString(url, ChRegex, 1);
			if ("".equals(s)){
				return url;
			}
			url = url.replaceAll(s, URLEncoder.encode(s, "utf-8"));
		}
	}
	
	/**
	 * 正则过滤所有emoji
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {  
        if(null != source){  
            return source.replaceAll(EMOJI, "");  
        } 
        return source;  
    }  
}
