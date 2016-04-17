package netrobot.crawl.xustbar;

import java.util.HashMap;

import netrobot.crawl.Crawl;
import netrobot.utils.RegexUtil;

public class NodeALL extends Crawl{

	private String url;
	
	//匹配帖子标题
	private static final String NODE_TITLE = "<h1 class=\"core_title_txt  \" title=\"(.*?)\"";
	//匹配用户名
	private static final String USER_NAME = "j_user_card\" href=\"/home/main?un=(.*?)\" target=\"_blank\">(.*?)</a>";
	
	private static HashMap<String, String> params;

	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public NodeALL(String url) {
		readPageByGet(url, params, "UTF-8");
		this.url = url;
	}
	
	private String getNodeTitle(){
		return RegexUtil.getFirstString(getPageSourceCode(), NODE_TITLE, 1);
	}
	
	private String getUserName(){
		return RegexUtil.getFirstString(getPageSourceCode(), USER_NAME, 2);
	}
	
	
	public static void main(String[] args) {

		//XUST某一帖子url
		NodeALL nodeALL = new NodeALL("http://tieba.baidu.com/p/4480872615");

		System.out.println(nodeALL.getNodeTitle());
		System.out.println(nodeALL.getUserName());
	}
}
