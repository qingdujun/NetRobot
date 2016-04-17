package netrobot.crawl;

import java.util.HashMap;
import java.util.List;

import netrobot.utils.RegexUtil;

public abstract class CrawlUrlList extends Crawl{

	private String pageUrl;
	
	public CrawlUrlList(String pageUrl,String charsetName) {
		readPageByGet(pageUrl, null, charsetName);
		this.pageUrl = pageUrl;
	}
	
	public CrawlUrlList(String pageUrl,String charsetName,HashMap<String, String> params){
		readPageByGet(pageUrl, params, charsetName);
		this.pageUrl = pageUrl;
	}
	
	public List<String> getNextPageUrl(){
		return RegexUtil.getArrayList(getPageSourceCode(), getNextUrlRegex(), pageUrl, getNextUrlRegexNum());
	}
	public abstract String getNextUrlRegex();
	public abstract int getNextUrlRegexNum();
}
