package netrobot.crawl.xustbar.thread;

import netrobot.crawl.xustbar.CrawlSetting;

public class StartAllThread {

	public static void main(String[] args) throws Exception{

//		CrawlSetting.openCrawlSetting();
//		Thread.sleep(1000);
//		TopicNoteThread.openTopicNoteThread();
//		Thread.sleep(1000);
		NoteDetailThread.openNoteDetailThread();
	}

}
