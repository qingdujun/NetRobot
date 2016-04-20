package netrobot.crawl.xustbar.db;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.db.manager.DbServer;
import netrobot.utils.JsonUtil;
import netrobot.utils.ParseMD5;
/**
 * 数据库操作(import:proxool.cglib.jar)
 * @author qingdujun
 *
 */
public class XUSTbarDb {

	private static final String POOL_NAME = "proxool.tiebadb";
	
	/**
	 * 从数据库中获取抓取清单
	 * @return
	 */
	public List<TopicNote> getTopicNoteCrawlList(){
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select * from topicnote where `state` = '1'";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				TopicNote topicNote = new TopicNote();
				topicNotes.add(topicNote);
				topicNote.setNote_url(rs.getString("note_url"));
				topicNote.setNote_title(rs.getString("note_title"));
				topicNote.setTopic_reply_count(rs.getInt("topic_reply_count"));
				topicNote.setLast_reply_time(rs.getString("last_reply_time"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return topicNotes;
	}
	/**
	 * 
	 * @param topicNotes
	 * @param isMD5  是否加密
	 */
	public void saveTopicNoteCrawlInfo(List<TopicNote> topicNotes,boolean isMD5) {
		if (null == topicNotes) {
			return;
		}
		for (TopicNote topicNote : topicNotes) {
			//MD5加密
			String note_url = ParseMD5.parseStr2MD5(topicNote.getNote_url());
			if (hasExistUrl(note_url)) {
				updateStateValue(note_url, 1);
			}else {
				insertTopicNoteCrawlInfo(topicNote,isMD5);
			}
		}
	}
	public void method() {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	/**
	 * 更新state值
	 * @param note_url
	 * @param state
	 */
	public void updateStateValue(String note_url, int state) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "update topicnote set `state` = '"+state+"' where note_url = '"+note_url+"'";
			dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	/**
	 * 判断Note_Url是否存在
	 * @param note_url
	 * @return
	 */
	private boolean hasExistUrl(String note_url) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from topicnote where note_url = '"+note_url+"'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * 执行数据库插入操作
	 * @param topicNote
	 * @param isMD5
	 */
	private void insertTopicNoteCrawlInfo(TopicNote topicNote, boolean isMD5) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5加密
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getNote_url()));
				params.put(i++, topicNote.getTopic_reply_count());
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getNote_title()));
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getLast_reply_time()));
				params.put(i, 1);
			}else {
				params.put(i++, topicNote.getNote_url());
				params.put(i++, topicNote.getTopic_reply_count());
				params.put(i++, topicNote.getNote_title());
				params.put(i++, topicNote.getLast_reply_time());
				params.put(i, 1);
			}
			
			dbServer.insert("topicnote","note_url,topic_reply_count,note_title,last_reply_time,state",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	
	public static void main(String[] args) {
		XUSTbarDb db = new XUSTbarDb();
		System.out.println(JsonUtil.parseJson(db.getTopicNoteCrawlList()));
	}
}
