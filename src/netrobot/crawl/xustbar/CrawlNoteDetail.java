package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import netrobot.crawl.Crawl;
import netrobot.utils.JsonUtil;
import netrobot.utils.RegexUtil;

public class CrawlNoteDetail extends Crawl{

	private String url;
	
	//标题
	private static final String TITLE = "style=\"width: 470px\">(.*?)</h1><ul class=";

	//一级回复
	private static final String REPLY_ONE = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";

	private static HashMap<String, String> params;

	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlNoteDetail(String url) {
		readPageByGet(url, params, "UTF-8");
		this.url = url;
	}
	
	private String getNoteTitle(){
		return RegexUtil.getFirstString(getPageSourceCode(), TITLE, 1);
	}
	
	private List<String> getNoteReplyOne(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_ONE, 1);
	}
	
	private List<String> getNoteReplyOne(boolean exceptLabel){
		List<String> replyOneList = getNoteReplyOne();
		if (exceptLabel) {
			List<String> exceptReplyOne = new ArrayList<String>();
			for (String one : replyOneList) {
				//正则去掉所有网页html标签
				exceptReplyOne.add(one.replaceAll("<[^>]*>", ""));
			}
			return exceptReplyOne;
		}
		return replyOneList;
	}
	
	public static void main(String[] args) {

		//XUST某一帖子url
		CrawlNoteDetail noteDetail = new CrawlNoteDetail("http://tieba.baidu.com/p/4484854674");
		String title = noteDetail.getNoteTitle();
		List<String> oneList = noteDetail.getNoteReplyOne(true);
		System.out.println("标题  "+title);
		
		for (int i = 0; i < oneList.size(); i++) {
			System.out.println((i+1)+"楼 "+" "+oneList.get(i));
		}

	}
}
