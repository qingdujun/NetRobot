package netrobot.crawl.xustbar.thread;

import java.util.List;
import netrobot.crawl.xustbar.CrawlTopicNote;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.Setting;
import netrobot.crawl.xustbar.model.TopicNote;

public class TopicNoteThread implements Runnable{

	//页面标记
	private static int pn;
	
	private static XUSTbarDb db;
	
	private static String bar_url;
	private static String bar_name;
	private static int crawl_frequency;
	private static int bar_crawl_note_count;
	//线程池
//	private static ExecutorService newFixedThreadPool;
	private static boolean flag = false;

	/**
	 * 参数初始化
	 */
	private static void init() {
		db = new XUSTbarDb();
		//开启
		flag = true;
		//开始爬取页面
		pn = 3894;
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
		bar_crawl_note_count = setting.getBar_crawl_note_count();
	}
	/**
	 * 重塑url链接
	 * @param src
	 * @return
	 */
	private static String getDealBarUrl(String src){
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
	private static Setting getBarSettingInfo(){
		List<Setting> settings = db.getSettingTableInfo();
		if (null == settings) {
			System.out.println("Setting info get error!");
			return null;
		}
		for (Setting setting : settings) {
			if (setting.getBar_name().trim().equals("西安科技大学")) {
				System.out.println("Exist XUST.");
				return setting;
			}
		}
		return null;
	}

	/**
	 * 爬取方法
	 */
	private void crawlAllTopicNote(){

		System.out.println("crawling XUST page "+pn);
		if ( (db.getiStop() >= 150) || ( pn >= bar_crawl_note_count / 50) ) {
			//关闭线程，爬取结束
			System.out.println("crawl finish.————"+Thread.currentThread().getName());
			flag = false;
		}
		CrawlTopicNote ctn = new CrawlTopicNote(bar_url+(pn++)*50);
		
		List<TopicNote> tns = ctn.getTopicNotes();
		db.saveTopicNoteCrawlInfo(tns, false);
	}
	/**
	 * 开启N线程
	 * @param N
	 */
//	public static void openTopicNoteThread(int N) {
//		init();
//		//引入线程池
//		newFixedThreadPool = Executors.newFixedThreadPool(N);  
//		for (int i = 0; i < N; i++) {  
//			newFixedThreadPool.execute(new TopicNoteThread());
//		}  
//		newFixedThreadPool.shutdown();
//	}
	public static void openTopicNoteThread(){
		init();
		TopicNoteThread tnt = new TopicNoteThread();
		Thread t1 = new Thread(tnt);
		t1.start();
	}
	@Override
	public void run() {
		while (flag) {
			crawlAllTopicNote();
			try { 
				Thread.sleep((long)(20+Math.random()*crawl_frequency));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
