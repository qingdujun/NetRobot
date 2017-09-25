package netrobot.crawl.bar.thread;

import netrobot.crawl.bar.CrawlBar;
import netrobot.crawl.bar.db.CrawlXUSTbarDb;

public class BarThread implements Runnable{

	private static CrawlXUSTbarDb db;
	
	public BarThread() {
		db = new CrawlXUSTbarDb();
	}

	private void CrawlBar() {
		CrawlBar cs = new CrawlBar("http://tieba.baidu.com/f?kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6","西安科技大学");
		db.saveBarCrawlInfo(cs.getBar(), false);
		System.out.println("Bar Setting Information acquisition is completed.");
	}
	
	@Override
	public void run() {
		CrawlBar();
	}
	
	/**
	 * 开启设置线程
	 */
	public static void openCrawlBarThread(){
		BarThread st = new BarThread();
		Thread t = new Thread(st);
		t.start();
	}
	
}
