package se.index.utils;

import java.util.HashMap;

public class JsonUtil {

	public static String parseJson(HashMap<String, String> hashMap){
		return "{\"rid\": \""+hashMap.get("key")+"\",\"text\": \""+hashMap.get("text")+"\"}";
	}
	
	public static void main(String[] args) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		String rid = "2342342";
		String content = "啊你撒地方电话";
		hashMap.put("key", rid);
		hashMap.put("text", content);
		System.out.println(parseJson(hashMap));
	}
}
