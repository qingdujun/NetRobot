package netrobot.crawl.bar.thread;

import java.util.List;
import netrobot.crawl.bar.CrawlNoteDetail;
import netrobot.crawl.bar.db.CrawlXUSTbarDb;
import netrobot.crawl.bar.model.TopicNote;

public class NoteDetailThread implements Runnable{

	private static CrawlXUSTbarDb db;

	private boolean again = true;
	//	private static ExecutorService newFixedThreadPool;

	/**
	 * 参数初始化
	 */
	private static void init() {
		db = new CrawlXUSTbarDb();
	}

	/**
	 * 分页查询TopicNote表中帖子清单
	 * @return
	 */
	private List<TopicNote> getTopicNotes(int row, int count){
		List<TopicNote> topicNotes = db.getTopicNoteCrawlList(row,count);
		return topicNotes;
	} 
	/**
	 * 提取其中一条内容进行爬取
	 * @param topicNotes
	 * @param i
	 * @return
	 */
	private TopicNote getTopicNote(List<TopicNote> topicNotes, int i){
		if ((null == topicNotes) || (topicNotes.size() <= i)) {
			return null;
		}
		return topicNotes.get(i);
	}

	/**
	 * 爬取方法
	 */
	private void crawlAllNoteDetail(){
		List<TopicNote> topicNotes = null;
		int row = 0, count = 1000;
		//获取主题帖list.size()
		int num = db.getTopicNoteCount();
		System.out.println("now,we need update note count = "+num);
		//结束线程
		if (0 == num) {
			again = false;
		}
		//批处理
		while(row < num) {
			//数据库分页查询
			topicNotes = getTopicNotes(row, count);
			row += count;
			if (null == topicNotes || 0 == topicNotes.size()) {
				continue;
			}
			for (int j = 0; j < topicNotes.size(); j++) {
				//网络爬虫，入库
				TopicNote topicNote = getTopicNote(topicNotes, j);
				if (null == topicNote) {
					continue;
				}
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getUrl());
				//主题帖被回复页数(网络访问)
				String tmp = cnd.getReplyPage();
				//网络访问成功
				//帖子被删除，无法更新
				if (null == tmp || tmp.equals("")) {
					db.updateStateValue(topicNote.getUrl(), 0);
					System.out.println("404   "+ topicNote.getUrl());
					continue;
				}
				int reply_page = Integer.parseInt(tmp);
				//帖子挖取深度不超过1000页
				if (reply_page > 1000) {
					reply_page = 1000;
				}
				for (int k = 1; k <= reply_page; k++) {
					cnd = new CrawlNoteDetail(topicNote.getUrl()+"?pn="+k);
					//数据库存储操作,非MD5（网络访问）
					db.saveNoteDetailInfo(cnd.getNoteDetails(k), false);
					try {
						//产生随机数,沉睡时间
						Thread.sleep((long)(200+Math.random()*60));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				db.updateStateValue(topicNote.getUrl(), 0);
			}
		}
	}


	//	public static void openNoteDetailThread(int N) {
	//		init();
	//		//引入线程池
	//		newFixedThreadPool = Executors.newFixedThreadPool(N);  
	//		for (int i = 0; i < N; i++) {  
	//			newFixedThreadPool.execute(new NoteDetailThread());
	//		}  
	//		newFixedThreadPool.shutdown();
	//	}

	@Override
	public void run() {
		while (again) {
			crawlAllNoteDetail();
			try {
				//暂停一分钟
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	public static void openNoteDetailThread() {
		init();
		NoteDetailThread tnt = new NoteDetailThread();
		Thread t = new Thread(tnt);
		t.start();
	}
}
