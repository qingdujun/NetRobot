package netrobot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import netrobot.crawl.bar.db.CrawlXUSTbarDb;

public class FileUtil {

	private static String path = "E:/tmp/notedetail2.sql"; 

	//INSERT INTO `notedetail` VALUES ('767', 'http://tieba.baidu.com/p/4451568443?pn=1', '86800841854', '0', '二楼自占', '-1', '1', '0');
	//--------------------------------ID---URL---RID---PID--CONTENT---
//	private static String REGEX1 =  "\'(.*?)\',\'(.*?)\',\'(.*?)\',\'(.*?)\',\'(.*?)\',";
	//---------------------------------------------------------------------ID------URL-------RID----PID--CONTENT---
	private static String REGEX =  "INSERT INTO `notedetail` VALUES \\('(\\d+)', '(.*?)', '(\\d+)', '0', '(.*?)', ";
	private static String url;
	private static String rid;
	private static String content;
	
	private static CrawlXUSTbarDb db = new CrawlXUSTbarDb();
	
	public FileUtil() {
		
	}
	/**
	 * 读文件
	 * @param path
	 * @return
	 */
	public static void readFile(String path){

		BufferedReader bfBufferedReader = null;   
		FileInputStream fin = null;
		try{  
			fin = new FileInputStream(new File(path));  
			bfBufferedReader = new BufferedReader(new InputStreamReader(fin, "UTF-8"));  
			//String readLine()读取到文件末尾返回值为null  
			String buf = null;  

			while ((buf=bfBufferedReader.readLine().trim()) != null)  {
				
//				System.out.println(buf);
				url = subStr(RegexUtil.getFirstString(buf, REGEX, 2));
				rid = subStr(RegexUtil.getFirstString(buf, REGEX, 3));
				content = subStr(RegexUtil.getFirstString(buf, REGEX, 4));
				
//				System.out.println(url +"  "+rid+"  "+content);
				db.insertNoteDetailInfo(url, rid, content);
				
			}
			bfBufferedReader.close();
		}   
		catch (IOException e) {  
			e.printStackTrace();
		}  
	}

	/**
	 * 过滤字符串
	 * @param str
	 * @return
	 */
	private static String subStr(String str){
		if (null == str || str.length() < 2) {
			return "";
		}
		int i = str.lastIndexOf('?');
		return str.substring(0, i > 1 ? i : str.length());
	}
	
	
	public static void main(String[] args) {
		readFile(path);
	}

}
