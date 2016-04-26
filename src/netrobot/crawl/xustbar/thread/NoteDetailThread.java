package netrobot.crawl.xustbar.thread;

import java.util.List;

import netrobot.crawl.xustbar.CrawlNoteDetail;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.TopicNote;

public class NoteDetailThread implements Runnable{

	private static XUSTbarDb db;
	
	//需爬取url总数
	private int state_count;
	
	//每次从数据库中取的数量
	private final int count = 1000;

	//线程池
//	private static ExecutorService newFixedThreadPool;
	private static boolean flag = false;
	private static int row = 0;

	/**
	 * 参数初始化
	 */
	private static void init() {
		db = new XUSTbarDb();
		//开启
		flag = true;
		
	}
	
	/**
	 * 分页查询TopicNote表中帖子清单
	 * @return
	 */
	private List<TopicNote> getTopicNotes(int row, int count){
		List<TopicNote> topicNotes = db.getTopicNoteCrawlList(row,count);
//		System.out.println(topicNotes.size());
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
	 * 抓取完成
	 * @return
	 */
	private boolean isCrawlFinish(){
		if (state_count <= db.getTopNoteCount()) {
			try {
				Thread.sleep(2000);
				state_count = db.getTopicNoteCount();
				if (state_count <= db.getTopNoteCount()) {
					//停止线程
					flag = false;
					System.out.println("NoteDetailThread will stop.");
					return true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * 爬取方法
	 */
	private void crawlAllNoteDetail(){
		int realCount = 0;
		//获取主题帖list.size()
		state_count = db.getTopicNoteCount();
		System.out.println("now,we need update note count = "+state_count);
		if (isCrawlFinish()) {
			return;
		}
		//批处理
		for( ; row < state_count; ) {
			//数据库分页查询
			List<TopicNote> topicNotes = getTopicNotes(row, count);
			//实际提取数量
			realCount = topicNotes.size();
			row += realCount;
			System.out.println("we real get note count total "+realCount+" from row "+row+" begin.");
			for (int j = 0; j < topicNotes.size(); j++) {
				//网络爬虫，入库
				TopicNote topicNote = getTopicNote(topicNotes, j);
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getNote_url());
				//主题帖被回复页数(网络访问)
				String tmp = cnd.getReplyPage();
				//帖子被删除，无法更新
				if (null == tmp || tmp.equals("")) {
					db.updateStateValue(topicNote.getNote_url(), 0);
					System.out.println("404   "+ topicNote.getNote_url());
					continue;
				}
				int reply_page = Integer.parseInt(tmp);
				//帖子挖取深度不超过1000页
				if (reply_page > 1000) {
					reply_page = 1000;
				}
				for (int k = 1; k <= reply_page; k++) {
					cnd = new CrawlNoteDetail(topicNote.getNote_url()+"?pn="+k);
					//数据库存储操作（网络访问）
					db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
					try {
						//产生随机数,沉睡时间
						Thread.sleep((long)(50+Math.random()*180));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//帖子被删除，无法更新
				if (null == cnd.getReply_times()) {
					continue;
				}
				//进行TopicNote库数据更新操作（reply_time,state）
				int size = cnd.getReply_times().size();
				//置顶帖不更新
				if (0 != size && !topicNote.getLast_reply_time().equals("0")) {
					db.updateTopicNoteTime(topicNote, cnd.getReply_times().get(size-1), false);
				}
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
	public static void openNoteDetailThread() {
		init();
		NoteDetailThread tnt = new NoteDetailThread();
		Thread t1 = new Thread(tnt);
		t1.start();
	}
	@Override
	public void run() {
		while (flag) {
			crawlAllNoteDetail();
		}
	}
}
