package netrobot.crawl.bar.thread;

import java.util.List;
import netrobot.crawl.bar.CrawlTopicNote;
import netrobot.crawl.bar.db.CrawlXUSTbarDb;
import netrobot.crawl.bar.model.Bar;
import netrobot.crawl.bar.model.TopicNote;

public class TopicNoteThread implements Runnable{

	private static CrawlXUSTbarDb db;

	private static String bar_url;
	private static String bar_name;
	private static int crawl_frequency;
	private static int bar_crawl_note_count;
	//线程池
	//	private static ExecutorService newFixedThreadPool;

	/**
	 * 参数初始化
	 */
	private static boolean init() {
		db = new CrawlXUSTbarDb();
		Bar bar = getBarInfo();
		if (null == bar) {
			System.out.println("bar not exist!");
			return false;
		}
		bar_url = getDealBarUrl(bar.getUrl());
		bar_name = bar.getName();
		System.out.println("we will crawl "+bar_name);
		//设置爬取频率
		crawl_frequency = bar.getFrequency();
		//爬取数量
		bar_crawl_note_count = bar.getCount();
		return true;
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
	 * 获取数据库中Bar信息
	 * @return
	 */
	private static Bar getBarInfo(){
		List<Bar> bars = db.getBarTableInfo();
		if (null == bars) {
			System.out.println("bar info get error!");
			return null;
		}
		for (Bar bar : bars) {
			if (bar.getName().trim().equals("西安科技大学")) {
				System.out.println("Exist XUST.");
				return bar;
			}
		}
		return null;
	}

	/**
	 * 爬取方法
	 */
	private void crawlAllTopicNote(){

		System.out.println("bar_crawl_note_count = "+bar_crawl_note_count);
		int pn = 0;
		int page = bar_crawl_note_count/50;
		while (pn <= page){
			System.out.println("crawling XUST page "+pn);
			if ( db.getiStop() >= 50 * 10) {
				// 10页未爬取到新数据自动结束，爬取结束
				System.out.println("Continues 30 not need to update!crawl finish.————"+Thread.currentThread().getName());
				break;
			}
			CrawlTopicNote ctn = new CrawlTopicNote(bar_url+(pn++)*50);

			List<TopicNote> tns = ctn.getTopicNotes();
			//入库，非MD5加密
			db.saveTopicNoteCrawlInfo(tns, 0,false);

			try { 
				Thread.sleep((long)(100+Math.random()*crawl_frequency));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	@Override
	public void run() {
		crawlAllTopicNote();
	}

	public static void openTopicNoteThread(){
		if (init()){
			TopicNoteThread tnt = new TopicNoteThread();
			Thread t = new Thread(tnt);
			t.start();
		}
	}
}
