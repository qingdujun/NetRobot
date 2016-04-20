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
	private static final String NOTE_TITLE = "style=\"width: 470px\">(.*?)</h1><ul class=";
	//一级回复ID
	private static final String REPLY_TOPIC_ID = "<div id=\"post_content_(\\d*?)\"";
	//一级回复内容
	private static final String REPLY_TOPIC_CONTEXT = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";
	//一级回复时间
	private static final String REPLY_TOPIC_TIME = "(\\d+-\\d+-\\d+) (\\d+:\\d+)";
	//楼中楼回复ID及父ID
	
	
	private static final String REPLY_LZL_PID_SPID = "\'pid\':\'(\\d+)\',\'spid\':\'(\\d+)\'";
	//'pid':'87824397167','spid':'87824719953',
	
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
		return RegexUtil.getFirstString(getPageSourceCode(), NOTE_TITLE, 1);
	}
	
	private List<String> getNoteReplyTopicID(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_TOPIC_ID, 1);
	}
	
	private List<String> getNoteReplyTopicContext(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_TOPIC_CONTEXT, 1);
	}
	
	private List<String> getNoteReplyTopicTime(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_TOPIC_TIME, 2);
	}
	
	private List<String> getNoteReplyLzlPID(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_LZL_PID_SPID, 1);
	}
	
	private List<String> getNoteReplyLzlSPID(){
		return RegexUtil.getList(getPageSourceCode(), REPLY_LZL_PID_SPID, 2);
	}
	
	private List<String> getNoteReplyTopicContext(boolean exceptLabel){
		List<String> replyTopicList = getNoteReplyTopicContext();
		if (exceptLabel) {
			List<String> exceptReplyTopic = new ArrayList<String>();
			for (String reply : replyTopicList) {
				//正则去掉所有网页html标签
				exceptReplyTopic.add(reply.replaceAll("<[^>]*>", ""));
			}
			return exceptReplyTopic;
		}
		return replyTopicList;
	}
	
	public static void main(String[] args) {

		//XUST某一帖子url
		CrawlNoteDetail noteDetail = new CrawlNoteDetail("http://tieba.baidu.com/p/4484854674");

		List<String> pid = noteDetail.getNoteReplyLzlPID();
		
		for (int i = 0; i < pid.size(); i++) {
			System.out.println(i + " "+pid.get(i));
		}

	}
}
