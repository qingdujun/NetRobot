package netrobot.crawl.bar.thread;

public class StartAllCrawlThread {

	/**
	 * 启动所有抓取线程
	 */
	public static void startAllCrawlThread(){
		//抓取bar主页信息
		BarThread.openCrawlBarThread();
		//抓取主题帖子
		TopicNoteThread.openTopicNoteThread();
		//抓取帖子内容
		NoteDetailThread.openNoteDetailThread();
	}
	
	public static void main(String[] args) throws Exception{
		startAllCrawlThread();
	}

}
