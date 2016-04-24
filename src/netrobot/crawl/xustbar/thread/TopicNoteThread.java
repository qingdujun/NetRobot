package netrobot.crawl.xustbar.thread;

import java.util.List;
import netrobot.crawl.xustbar.CrawlTopicNote;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.Setting;

public class TopicNoteThread extends ThreadPool{

	//页面标记
	private int pn;
	
	private XUSTbarDb db;
	
	private String bar_url;
	private String bar_name;
	private int crawl_frequency;
	private int bar_crawl_note_count;

	
	public TopicNoteThread (){
		init();
	}
	/**
	 * 参数初始化
	 */
	private void init() {
		db = new XUSTbarDb();
		//开启
		setFlag(true);
		pn = 0;
		Setting setting = getBarSettingInfo();
		if (null == setting) {
			System.out.println("bar not exist!");
			return;
		}
		bar_url = getDealBarUrl(setting.getBar_url());
		bar_name = setting.getBar_name();
		System.out.println("we will crawl "+bar_name);
		crawl_frequency = setting.getCrawl_frequency();
		//设置爬取频率
		setSleepTime(crawl_frequency);
		bar_crawl_note_count = setting.getBar_crawl_note_count();
	}
	/**
	 * 重塑url链接
	 * @param src
	 * @return
	 */
	private String getDealBarUrl(String src){
		//构造url，采集吧名
		String barname;
		if (src.indexOf("&") > 0) {
			barname = src.substring(src.indexOf("=")+1,src.indexOf("&"));
		}else {
			barname = src.substring(src.indexOf("=")+1,src.length());
		}
		
		String barurl = "http://tieba.baidu.com/f?kw="+barname+"&ie=utf-8&tp=0&pn=";
		System.out.println("link url = "+barurl);
		return barurl;
	}
	/**
	 * 获取数据库中Setting信息
	 * @return
	 */
	private Setting getBarSettingInfo(){
		List<Setting> settings = db.getSettingTableInfo();
		if (null == settings) {
			System.out.println("Setting info get error!");
			return null;
		}
		for (Setting setting : settings) {
			if (setting.getBar_name().trim().equals("西安科技大学")) {
//				System.out.println("Exist XUST.");
				return setting;
			}
		}
		return null;
	}

	/**
	 * 爬取方法
	 */
	private synchronized void crawlAllTopicNote(){

		System.out.println("tips:crawling page "+pn+" "+Thread.currentThread().getName());
		if ( (db.getiStop() >= 150) || ( pn >= bar_crawl_note_count / 50) ) {
			//关闭线程，爬取结束
			System.out.println("crawl finish.————"+Thread.currentThread().getName());
			setFlag(false);
		}
		CrawlTopicNote ctn = new CrawlTopicNote(bar_url+(pn++)*50);
		db.saveTopicNoteCrawlInfo(ctn.getTopicNotes(), false);
	}

	/**
	 * 重写父类爬取方法（方法中保证线程同步）
	 */
	@Override
	protected void executeMethod() {
		crawlAllTopicNote();
	}
	
	public static void main(String[] args) {
		TopicNoteThread tnt = new TopicNoteThread();
		tnt.openNThreadCrawl(4);
	}
}
