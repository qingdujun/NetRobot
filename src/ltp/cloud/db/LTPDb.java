package ltp.cloud.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ltp.cloud.bean.Word;
import netrobot.db.manager.DbServer;

public class LTPDb {

	private static final String POOL_NAME = "proxool.vcrobot";
	
	/**
	 * 储存用户关键字
	 * @param words
	 * @param email
	 * @return
	 */
	public static boolean saveKeywords(List<Word> words,String email){
		if (words == null || words.size() == 0) {
			return false;
		}
		for (Word word : words) {
			if (!insertKeyword(word, email)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 获取用户兴趣 - 前5
	 * @param email
	 * @return
	 */
	public static List<String> getUserInterest(String email){
		if (email == null) {
			return null;
		}
		List<String> interests = new ArrayList<String>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select content from interest where `email` = '"+email+"' ORDER BY count DESC LIMIT 0,5";
			System.out.println(sql);
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				interests.add(rs.getString("content"));
			}
			return interests;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return null;
	}
	
	/**
	 * 拼接List为String
	 * @param words
	 * @return
	 */
	public static String getWords(String email){
		if (email == null) {
			return null;
		}
		StringBuffer w = new StringBuffer();
		for (String word : getUserInterest(email)) {
			w.append(word+",");
		}
		return (w.length() > 0) ?w.substring(0,w.length()-1).toString() : null;
	}
	/**
	 * SQL插入操作，存在则更新
	 * @param word
	 * @param email
	 * @return
	 */
	private static boolean insertKeyword(Word word,String email){
		if (word == null || email == null) {
			return false;
		}
		String keyword = word.getCont();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			if (!isExistKeyword(keyword,email)) {
				System.out.println("INSERT");
				//INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
				String sql = "INSERT INTO interest (email, content, pos, count) VALUES ('"+email+"','"+keyword+"','"+word.getPos()+"','1')";
				dbServer.insert(sql);
			}else {
				//UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
				String sql = "UPDATE interest SET count = count+1 WHERE content = '"+keyword+"' AND email ='"+email+"'";
				dbServer.update(sql);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbServer.close();
		}
	}
	/**
	 * 判断关键字是否存在
	 * @param keyword
	 * @return
	 */
	private static boolean isExistKeyword(String keyword,String email){

		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select COUNT(*) from interest where content = '"+keyword+"' AND email = '"+email+"'";
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				return (rs.getInt(1) != 0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		
		return false;
	}
	
	public static void main(String[] args) {
//		
//		List<Word> words = new ArrayList<Word>();
//		Word word = new Word();
//		word.setCont("华山");
//		word.setPos("nh");
//		words.add(word);
//		
//		System.out.println(saveKeywords(words, "Vcrobot"));
		
		
		System.out.println(getWords("Vcrobot").toString());
	}
}
