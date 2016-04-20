package netrobot.crawl.xustbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.Crawl;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.RegexUtil;

/**
 * 爬取贴吧首页所有帖子url
 * @author qingdujun
 *
 */
public class CrawlTopicNote extends Crawl{

	private String bar_url;
	private static HashMap<String, String> params;
	//主题帖子url
	private static final String NOTE_URL = "<div class=\"threadlist_text threadlist_title j_th_tit  \">            <a href=\"(.*?)\"";
	//主题回复数目
	private static final String TOPIC_REPLY_COUNT = "<div class=\"threadlist_rep_num\"  title=\"回复\">(\\d+)</div>";
	//帖子标题
	private static final String NOTE_TITLE = "target=\"_blank\" class=\"j_th_tit\">(.*?)</a>";
	//最后回复时间
	private static final String LAST_REPLY_TIME = "<span class=\"threadlist_reply_date j_reply_data\" title=\"最后回复时间\">            (.*?)</span>";

	private List<String> note_urls;
	private List<String> topic_reply_counts;
	private List<String> note_titles;
	private List<String> last_reply_times;
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}

	/**
	 * 指定爬取吧url，默认编码
	 * @param bar_url
	 */
	public CrawlTopicNote(String bar_url) {
		readPageByGet(bar_url, params, "UTF-8");
		this.bar_url = bar_url;
	}
	/**
	 * 获取页面所有主题帖url
	 * @return
	 */
	private List<String> getPageNoteUrl(){
		return RegexUtil.getArrayList(getPageSourceCode(), NOTE_URL, bar_url, 1);
	}
	/**
	 * 获取主题帖回复数
	 * @return
	 */
	private List<String> getTopicReplyCount(){
		return RegexUtil.getList(getPageSourceCode(), TOPIC_REPLY_COUNT, 1);
	}
	/**
	 * 获取帖子标题
	 * @return
	 */
	private List<String> getNoteTitle(){
		return RegexUtil.getList(getPageSourceCode(), NOTE_TITLE, 1);
	}

	/**
	 * 获取主题帖最后回复时间
	 * @return
	 */
	private List<String> getLastReplyTime(){
		return RegexUtil.getList(getPageSourceCode(), LAST_REPLY_TIME, 1);
	}

	/**
	 * 排除其他杂乱网址
	 * @param exceptOther
	 * @return
	 */
	private List<String> getPageNoteUrl(boolean exceptOther) {
		List<String> urls = getPageNoteUrl();
		if (exceptOther) {
			List<String> exceptUrls = new ArrayList<String>();
			for (String url : urls) {
				if (url.indexOf("tieba") > 0) {
					exceptUrls.add(url);
				}
			}
			return exceptUrls;
		}
		return urls;
	}
	/**
	 * 时间拼接
	 * @return
	 */
	private String getLinkTime(String time) {
		long curTime = System.currentTimeMillis();

		if (time.indexOf(":") > 0) {
			//			yyyy-MM-dd HH:mm
			//			eg.20:22
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd ");
			Date date = new Date(curTime);  
			String tm = sdf.format(date)+time;  
			return tm;
		}else if(time.indexOf("-") > 0) {
			//			eg.4-20
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-");
			SimpleDateFormat sdf2 = new SimpleDateFormat(" HH:mm");
			Date date1 = new Date(curTime);  
			Date date2 = new Date(curTime); 
			String tm = sdf1.format(date1)+time+sdf2.format(date2);  
			//			System.out.println(tm);
			return tm;
		}
		return time;
	}
	/**
	 * 时间处理
	 * @param isLink
	 * @return
	 */
	private List<String> getLastReplyTime(boolean isLink) {
		List<String> lastReplyTimes = getLastReplyTime();

		if (isLink) {
			List<String> linkTimes = new ArrayList<String>();
			for (String linkTime : lastReplyTimes) {
				linkTime = getLinkTime(linkTime);
				linkTimes.add(linkTime);
			}
			return linkTimes;
		}
		return lastReplyTimes;
	}

	/**
	 * 获取主题帖的容量
	 * @return
	 */
	private int getTopicNoteMinSize() {
	
		note_urls = getPageNoteUrl(true);
		topic_reply_counts = getTopicReplyCount();
		note_titles = getNoteTitle();
		last_reply_times = getLastReplyTime(true);
		
		int min = note_urls.size();
		if (min > topic_reply_counts.size()) {
			min = topic_reply_counts.size();
		}
		
		if (min > note_titles.size()) {
			min = note_titles.size();
		}
		
		if (min > last_reply_times.size()) {
			min = last_reply_times.size();
		}
		return min;
	}
	/**
	 * 组装主题帖子所有内容
	 * @return
	 */
	public List<TopicNote> getTopicNotes() {
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();

		int minSize = getTopicNoteMinSize();
		for (int i = 0; i < minSize; i++) {
			TopicNote topicNote = new TopicNote();
			
			topicNote.setNote_url(note_urls.get(i));
			topicNote.setTopic_reply_count(Integer.parseInt(topic_reply_counts.get(i)));
			topicNote.setNote_title(note_titles.get(i));
			topicNote.setLast_reply_time(last_reply_times.get(i));
			topicNotes.add(topicNote);
		}

		return topicNotes;
	}
	
	public static void main(String[] args) {
		//此链接为XUST首页链接
		CrawlTopicNote ctn = new CrawlTopicNote("http://tieba.baidu.com/f?ie=utf-8&kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6");

//		//此链接为XUST首页链接
//		CrawlTopicNote updateUrlTime2 = new CrawlTopicNote("http://tieba.baidu.com/f?kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6&ie=utf-8&tp=0&pn=450");
//
//		List<String> count = updateUrlTime2.getLastReplyTime(true);
//
//
//		for (int i = 0; i < count.size(); i++) {
//			System.out.println((i+1)+" "+count.get(i));
//		}
//		
		

		XUSTbarDb db = new XUSTbarDb();
		db.saveTopicNoteCrawlInfo(ctn.getTopicNotes(), false);

	}
}
