package netrobot.crawl.bar;

import java.util.HashMap;

import netrobot.crawl.Crawl;

public class CrawlLzl extends Crawl{

	private static HashMap<String, String> params;
	private static final String NOTE_URL = "http://tieba.baidu.com/p/totalComment?t=1461925078809&tid=3635862582&fid=59099&pn=1&see_lz=0";
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlLzl(String note_url){
		readPageByGet(note_url, params, "UTF-8");
	}
	private String getLzlContent() {
		return getPageSourceCode();
	}
	public static void main(String[] args) {
		System.out.println(new CrawlLzl(NOTE_URL).getLzlContent());
	}
}
