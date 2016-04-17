package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.CrawlUrlList;

/**
 * 爬取贴吧首页所有帖子url
 * @author qingdujun
 *
 */
public class UpdateUrlList extends CrawlUrlList{

	private static HashMap<String, String> params;
	//匹配贴吧首页帖子url正则
	private static final String NOTE_URL = "<div class=\"threadlist_text threadlist_title j_th_tit  \">            <a href=\"(.*?)\"";
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	public UpdateUrlList(String pageUrl) {
		super(pageUrl, "UTF-8", params);
	}

	@Override
	public String getNextUrlRegex() {
		
		return NOTE_URL;
	}

	@Override
	public int getNextUrlRegexNum() {
		return 1;
	}
	
	/**
	 * 排除其他杂乱网址
	 * @param exceptOther
	 * @return
	 */
	public List<String> getNextPageUrl(boolean exceptOther) {
		List<String> urls = getNextPageUrl();
		if (exceptOther) {
			List<String> exceptUrls = new ArrayList<String>();
			for (String url : urls) {
				if (url.indexOf("tieba") > 0) {
					exceptUrls.add(url);
				}
			}
			return exceptUrls;
		}
		return urls;
	}

	public static void main(String[] args) {
		//此链接为XUST首页链接
		UpdateUrlList updateList = new UpdateUrlList("http://tieba.baidu.com/f?ie=utf-8&kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6");
		int i = 1;
		for (String url : updateList.getNextPageUrl(true)) {
			System.out.println((i++)+" : "+url);
		}
	}
}
