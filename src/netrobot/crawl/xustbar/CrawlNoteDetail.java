package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import netrobot.crawl.Crawl;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.NoteDetail;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.JsonUtil;
import netrobot.utils.RegexUtil;

public class CrawlNoteDetail extends Crawl{	
	//主题帖url
	private String note_url;  //不允许重复
	//回复楼id
	private List<String> reply_floor_ids;  //不允许重复
	//父id(令，一级回复id=0)
	private List<String> reply_parent_ids;  //非空
	//一级回复(或楼中楼)内容
	private List<String> reply_contexts;
	//一级回复(或楼中楼)回复数
//	private int lzl_reply_count;
	//最后回复时间
	private List<String> reply_times;

	//一级回复ID
	private static final String ONE_REPLY_ID = "<div id=\"post_content_(\\d*?)\"";
	//一级回复内容
	private static final String ONE_REPLY_CONTEXT = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";
	//一级回复时间
	private static final String ONE_REPLY_TIME = "(\\d+-\\d+-\\d+ \\d+:\\d+)";
	
	private static final String REPLY_PAGE = "回复贴，共<span class=\"red\">(\\d+)</span>页";
	
	private static HashMap<String, String> params;

	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlNoteDetail(String note_url) {
		readPageByGet(note_url, params, "UTF-8");
		this.note_url = note_url;
	}
	
	public List<String> getReply_times() {
		return reply_times;
	}

	public void setReply_times(List<String> reply_times) {
		this.reply_times = reply_times;
	}

	/**
	 * 获取一级回复楼本身ID
	 * @return
	 */
	private List<String> getOneReplyID(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_ID, 1);
	}
	/**
	 * 获取一级回复内容
	 * @return
	 */
	private List<String> getOneReplyContext(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_CONTEXT, 1);
	}
	/**
	 * 获取一级回复时间
	 * @return
	 */
	private List<String> getOneReplyTime(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_TIME, 1);
	}
	/**
	 * 获取帖子回复页数
	 * @return
	 */
	public String getReplyPage(){
		return RegexUtil.getFirstString(getPageSourceCode(), REPLY_PAGE, 1);
	}
	/**
	 * 获取一级回复内容，过滤
	 * @param exceptLabel
	 * @return
	 */
	private List<String> getOneReplyContext(boolean exceptLabel){
		List<String> oneReplycList = getOneReplyContext();
		if (exceptLabel) {
			List<String> exceptOneReply = new ArrayList<String>();
			for (String one : oneReplycList) {
				//正则去掉所有网页html标签,emoji表情
				exceptOneReply.add(RegexUtil.filterEmoji(one.replaceAll("<[^>]*>", "")));
			}
			return exceptOneReply;
		}
		return oneReplycList;
	}
	
	/**
	 * 获取帖子详情的容量
	 * @return
	 */
	private int getNoteDetailMinSize() {
		
		reply_floor_ids = getOneReplyID();
		reply_contexts = getOneReplyContext(true);
		reply_times = getOneReplyTime();
		
		int min = reply_floor_ids.size();
		if (min > reply_contexts.size()) {
			min = reply_contexts.size();
		}
		
		if (min > reply_times.size()) {
			min = reply_times.size();
		}
		return min;
	}
	
	/**
	 * 组装帖子详细内容
	 * @return
	 */
	public List<NoteDetail> getNoteDetails() {
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();

		int minSize = getNoteDetailMinSize();
		for (int i = 0; i < minSize; i++) {
			NoteDetail noteDetail = new NoteDetail();
			
			noteDetail.setNote_url(note_url);
			noteDetail.setReply_floor_id(reply_floor_ids.get(i));
			noteDetail.setReply_context(reply_contexts.get(i));
			noteDetail.setReply_parent_id("0");
			noteDetail.setReply_time(reply_times.get(i));
			noteDetails.add(noteDetail);
		}

		return noteDetails;
	}
	
	public static void main(String[] args) {

		//XUST某一帖子url
		CrawlNoteDetail cnd = new CrawlNoteDetail("http://tieba.baidu.com/p/4481777065");

//		List<String> pid = noteDetail.getOneReplyTime();
//		
//		for (int i = 0; i < pid.size(); i++) {
//			System.out.println(i + " "+pid.get(i));
//		}
		
		
		System.out.println(cnd.getReplyPage());
//		XUSTbarDb db = new XUSTbarDb();
//		db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
	}
}
