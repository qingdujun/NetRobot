package netrobot.crawl.xustbar.thread;

import java.util.List;

import netrobot.crawl.xustbar.CrawlNoteDetail;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.NoteDetail;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.JsonUtil;

public class NoteDetailThread extends ThreadPool{

	private XUSTbarDb db;
	
	//需爬取url总数
	private int state_count;
	
	//每次从数据库中取的数量
	private final int count = 1000;
	
	public NoteDetailThread() {
		init();
	}
	/**
	 * 参数初始化
	 */
	private void init() {
		db = new XUSTbarDb();
		//开启
		setFlag(true);
		state_count = db.getTopicNoteCount();
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
	 * 爬取方法
	 */
	private synchronized void crawlAllNoteDetail(){
		int realCount = 0;
		for (int i = 0; i < state_count; i+=realCount) {
			//数据库分页查询
			List<TopicNote> topicNotes = getTopicNotes(i, count);
			//实际提取数量
			realCount = topicNotes.size();
			System.out.println("state_count = "+state_count);
			for (int j = 0; j < topicNotes.size(); j++) {
				//网络爬虫，入库
				TopicNote topicNote = getTopicNote(topicNotes, j);
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getNote_url());
				//主题帖被回复数目
				int reply_count = Integer.parseInt(cnd.getReplyPage());
				//帖子挖取深度不超过1000页
				if (reply_count > 1000) {
					reply_count = 1000;
				}
				System.out.println("reply_count = "+reply_count);
				for (int k = 1; k <= reply_count; k++) {
					cnd = new CrawlNoteDetail(topicNote.getNote_url()+"?pn="+k);
//					System.out.println("url = "+topicNote.getNote_url()+"?pn="+k);
//					System.out.println(JsonUtil.parseJson(cnd.getNoteDetails()));
					db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
				}
				//进行TopicNote库数据更新操作（reply_time,state）
				int size = cnd.getReply_times().size();
				db.updateTopicNoteTime(topicNote, cnd.getReply_times().get(size-1), false);
			}
		}

	}
	

	@Override
	protected void executeMethod() {
		crawlAllNoteDetail();
	}

	public static void main(String[] args) {
		NoteDetailThread ndt = new NoteDetailThread();
		
		ndt.crawlAllNoteDetail();
//		XUSTbarDb db = new XUSTbarDb();
//		int count = db.getTopicNoteCount();
//		System.out.println(count);
//		ndt.getTopicNotes(10,40);
//		ndt.openNThreadCrawl(4);
	}
}
