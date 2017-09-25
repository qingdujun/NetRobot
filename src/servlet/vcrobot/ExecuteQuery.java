package servlet.vcrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ltp.cloud.analysis.TypeWord;
import ltp.cloud.db.LTPDb;
import netrobot.utils.TimeUtil;
import se.index.operation.bar.BarNRTSearch;
import se.index.utils.JsonUtil;
import servlet.vcrobot.db.VcrobotDb;

public class ExecuteQuery {

	/**
	 * 执行查询
	 * @param email
	 * @param keywords
	 * @return
	 */
	public static String executeQuery(String email, String keywords){
		HashMap<String, String> hashMap = new BarNRTSearch().searchInContext(email, keywords);
		return (hashMap == null) ? null : JsonUtil.parseJson(hashMap);
	}
	/**
	 * 分析内容，实时处理
	 * @param keywords
	 * @return
	 */
	public static String analyseContain(String keywords, String email){
		
//		System.out.println(keywords);
		if(keywords == null){
			return null;
		}
		
		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "10001");

		//教学  ，格式：[Q@你吃饭了没？A@我不饿。]
		if (isQA(keywords)) {
			List<String> QA = getQA(keywords);
			if (QA != null) {
				hashMap.put("text", VcrobotDb.saveQA(QA.get(0), QA.get(1)));
				return JsonUtil.parseJson(hashMap);
			}
		}
		
		if (keywords.contains("我的兴趣")) {
			hashMap.put("text", LTPDb.getWords(email));
			return JsonUtil.parseJson(hashMap);
		}
		
		if (keywords.contains("几点") || keywords.contains("时间") || keywords.contains("日期")) {
			hashMap.put("text", TimeUtil.getCurTime());
			return JsonUtil.parseJson(hashMap);
		}

		//子线程---语义分析、语法分析
		analysisInterest(email, keywords);

		return null;
	}
	/**
	 * 判断是否训练语句
	 * @param keywords
	 * @return
	 */
	private static boolean isQA(String keywords){
		if (keywords == null) {
			return false;
		}
		if (keywords.startsWith("[Q@") && keywords.endsWith("]") && keywords.contains("A@")) {
			return true;
		}
		return false;
	}
	/**
	 * 拆分QA
	 * @param keywords
	 * @return
	 */
	private static List<String> getQA(String keywords){
		if (keywords == null) {
			return null;
		}
		List<String> QA = new ArrayList<String>();
		int posA = keywords.indexOf("A@");
		String Q = keywords.substring(3, posA).trim();
		String A = keywords.substring(posA+2,keywords.length()-1).trim();
//		System.out.println(Q+"----"+A);
		QA.add(Q);
		QA.add(A);
		return QA;
	}
	
	/**
	 * 分析用户兴趣
	 * @param email
	 * @param keywords
	 */
	private static void analysisInterest(final String email, final String keywords){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				LTPDb.saveKeywords(TypeWord.analysisWordType(keywords), email);
			}
		}).start();
	}
	public static void main(String[] args) {
//		analyseContain("[Q@西科大校长是谁？A@目前是杨更社。]");
//		System.out.println(analyseContain("[Q@西科大党委书记是谁？A@目前是刘德安。]"));
	}
}
