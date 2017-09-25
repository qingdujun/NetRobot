package netrobot.crawl.bar;

import java.util.HashMap;

import netrobot.crawl.Crawl;
import netrobot.crawl.bar.model.Bar;
import netrobot.utils.RegexUtil;
import netrobot.utils.TimeUtil;

public class CrawlBar extends Crawl{

	private static HashMap<String, String> params;
	private String url;
	private String name;
	private int frequency;
	
	private static final String BAR = "共有主题数<span class=\"red_text\">(\\d+)</span>个";
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlBar(String url,String name) {
		readPageByGet(url, params, "UTF-8");
		this.url = url;
		this.name = name;
		this.frequency = 60;
	}

	/**
	 * 获取主题帖子数目
	 * @return
	 */
	private String getTopicNoteCount(){
		return RegexUtil.getFirstString(getPageSourceCode(), BAR, 1);
	}
	/**
	 * 获得贴吧贴子数目
	 * @return
	 */
	private int getBarNoteCount(){
		String count = getTopicNoteCount();
		if (null == count || count.equals("")) {
			return 0;
		}
		int iCount = Integer.parseInt(count);
		if (iCount > 2000050) {
			return 2000000;
		}
		return (iCount - iCount%50);
	}
	
	/**
	 * 组装Setting数据
	 * @return
	 */
	public Bar getBar(){
		Bar setting = new Bar();
		setting.setUrl(url);
		setting.setName(name);
		setting.setCount(getBarNoteCount());
		setting.setFrequency(frequency);
		setting.setTime(TimeUtil.getCurTime());
		return setting;
	} 
	
	public static void main(String[] args) {
		String url = "https://tieba.baidu.com/f?kw=%C0%EE%D2%E3";
		String name  = "李毅吧";
		CrawlBar cs = new CrawlBar(url, name);
		//197965
		System.out.println(cs.getTopicNoteCount());

	}

}
