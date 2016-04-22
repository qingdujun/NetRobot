package netrobot.crawl.xustbar;

import java.util.HashMap;
import netrobot.crawl.Crawl;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.Setting;
import netrobot.utils.RegexUtil;

public class CrawlSetting extends Crawl{

	private static HashMap<String, String> params;
	private String bar_url;
	private String bar_name;
	private int crawl_frequency;
	
	
	private static final String TOPIC_NOTE = "共有主题数<span class=\"red\">(\\d+)</span>个";
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlSetting(String bar_url,String bar_name) {
		readPageByGet(bar_url, params, "UTF-8");
		this.bar_url = bar_url;
		this.bar_name = bar_name;
		this.crawl_frequency = 180;
	}

	/**
	 * 获取主题帖子数目
	 * @return
	 */
	private String getTopicNoteCount(){
		return RegexUtil.getString(getPageSourceCode(), TOPIC_NOTE, 1);
	}
	/**
	 * 获取实际爬取帖子数目
	 * @return
	 */
	private String getCrawlNoteCount(){
		String count = getTopicNoteCount();
		int iCount = Integer.parseInt(count);
		if (iCount > 2000050) {
			return "2000000";
		}
		return ""+(iCount - iCount%50);
	}
	
	/**
	 * 组装Setting数据
	 * @return
	 */
	public Setting getSetting(){
		Setting setting = new Setting();
		setting.setBar_url(bar_url);
		setting.setBar_name(bar_name);
		setting.setBar_crawl_note_count(Integer.parseInt(getCrawlNoteCount()));
		setting.setCrawl_frequency(crawl_frequency);
		setting.setLast_crawl_time(""+System.currentTimeMillis());
		return setting;
	} 
	
	
	public static void main(String[] args) {
		
		CrawlSetting cs = new CrawlSetting("http://tieba.baidu.com/f?kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6","西安科技大学");
		XUSTbarDb db = new XUSTbarDb();
		db.saveSettingCrawlInfo(cs.getSetting(), false);
		
	}

}
