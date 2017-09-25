package netrobot.crawl.bar.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.bar.model.NoteDetail;
import netrobot.crawl.bar.model.Bar;
import netrobot.crawl.bar.model.TopicNote;
import netrobot.db.manager.DbServer;
import netrobot.utils.ParseMD5;
import netrobot.utils.TimeUtil;
/**
 * 数据库操作(import:proxool.cglib.jar)
 * @author qingdujun
 *
 */
public class CrawlXUSTbarDb {
	
	private static int iStop = 1;

	private static final String POOL_NAME = "proxool.vcrobot";

	/**
	 * 线程是否停止
	 * @return
	 */
	public int getiStop() {
		return iStop;
	}
	
	/**
	 * 从数据库中获取Bar表
	 * @return
	 */
	public List<Bar> getBarTableInfo(){
		List<Bar> bars = new ArrayList<Bar>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select * from bar";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				Bar bar = new Bar();
				bar.setUrl(rs.getString("url"));
				bar.setName(rs.getString("name"));
				bar.setCount(rs.getInt("count"));
				bar.setFrequency(rs.getInt("frequency"));
				bar.setTime(rs.getString("time"));
				bars.add(bar);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return bars;
	}
	
	/**
	 * 保存Bar信息
	 * @param bar
	 * @param isMD5
	 */
	public void saveBarCrawlInfo(Bar bar,boolean isMD5) {
		if (null == bar) {
			return;
		}

		String bar_url = ParseMD5.parseStr2MD5(bar.getUrl());
		if (hasExistBarUrl(bar_url) || hasExistBarUrl(bar.getUrl())) {
			updateBarInfo(bar, isMD5);
		}else {
			insertBarInfo(bar,isMD5);
		}
	}
	/**
	 * 对Bar表进行插入操作
	 * @param bar
	 * @param isMD5
	 */
	private void insertBarInfo(Bar bar,boolean isMD5) {
		if (null == bar) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5加密
				params.put(i++, ParseMD5.parseStr2MD5(bar.getUrl()));
				params.put(i++, ParseMD5.parseStr2MD5(bar.getName()));
				params.put(i++, bar.getCount());
				params.put(i++, bar.getFrequency());
				params.put(i++, bar.getTime());
				params.put(i, "1");
			}else {
				params.put(i++, bar.getUrl());
				params.put(i++, bar.getName());
				params.put(i++, bar.getCount());
				params.put(i++, bar.getFrequency());
				params.put(i++, bar.getTime());
				params.put(i, "1");
			}
			
			dbServer.insert("bar","url,name,count,frequency,time,state",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 更新贴吧采集主题帖子数目
	 * @param topicNote
	 * @param isMD5
	 */
	private void updateBarInfo(Bar bar, boolean isMD5){
		if (null == bar) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer,Object>();
			int i = 1;

			if (isMD5) {
				//MD5加密
				params.put(i++, ParseMD5.parseStr2MD5(bar.getName()));
				params.put(i++, bar.getCount());
				params.put(i++, bar.getFrequency());
				params.put(i++, bar.getTime());
				params.put(i, "1");
			}else {
				params.put(i++, bar.getName());
				params.put(i++, bar.getCount());
				params.put(i++, bar.getFrequency());
				params.put(i++, bar.getTime());
				params.put(i, "1");
			}
			String columns = "name,count,frequency,time,state";
			String condition = "where url = '"+bar.getUrl()+"'";
			dbServer.update("bar", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 判断Bar_Url是否存在
	 * @param note_url
	 * @return
	 */
	private boolean hasExistBarUrl(String url) {
		if (null == url) {
			return false;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from bar where url = '"+url+"'";
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
		return false;
	}
	
	/**
	 * 从数据库中获取crawlTopicNote清单,分页查询
	 * @return
	 */
	public List<TopicNote> getTopicNoteCrawlList(int row, int count){
		if (row < 0 || count < 0) {
			return null;
		}
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			//SELECT * FROM `topicnote` LIMIT 1000, 1000
			String sql = "SELECT * FROM `topic` WHERE `state` = '1' LIMIT "+row+" , "+count;
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				TopicNote topicNote = new TopicNote();
				topicNote.setUrl(rs.getString("url"));
				topicNote.setContent(rs.getString("content"));
				topicNote.setCount(rs.getInt("count"));
				topicNote.setFloor(rs.getInt("floor"));
				topicNote.setTime(rs.getString("time"));
				topicNotes.add(topicNote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return topicNotes;
	}
	
	/**
	 * 获取数据库中需爬取条数
	 * @return
	 */
	public int getTopicNoteCount(){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "SELECT COUNT(*) FROM `topic` WHERE `state` = '1'";
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return 0;
	}
	/**
	 * 
	 * @param topicNotes
	 * @param isMD5  是否加密
	 */
	public void saveTopicNoteCrawlInfo(List<TopicNote> topicNotes,int floor,boolean isMD5) {
		if (null == topicNotes || 0 == topicNotes.size()) {
			return;
		}
		for (TopicNote topicNote : topicNotes) {
			//MD5加密
			String url = ParseMD5.parseStr2MD5(topicNote.getUrl());
			if (hasExistNoteUrl(url,floor) || hasExistNoteUrl(topicNote.getUrl(),floor)) {
				++iStop;
				updateStateValue(topicNote.getUrl(), 1);
				updateStateValue(url, 1);
				updateLuceneValue(topicNote.getUrl(), 1);
				updateLuceneValue(url, 1);
				updateTopicNoteCount(topicNote, isMD5);
				if (0 == iStop % 50) {
					System.out.println("Continuous "+(int)(iStop/50)+" is not need to updated!");
				}
				
			}else {
				iStop = 1;
				insertTopicNoteCrawlInfo(topicNote,1,isMD5);
			}
		}
	}
	/**
	 * 插入1楼内容
	 * @param topicNote
	 * @param floor
	 * @param isMD5
	 */
	public void saveSingleTopicNoteCrawlInfo(TopicNote topicNote,int floor,boolean isMD5) {
		if (null == topicNote) {
			return;
		}
		//MD5加密
		String url = ParseMD5.parseStr2MD5(topicNote.getUrl());
		if (hasExistNoteUrl(url,floor) || hasExistNoteUrl(topicNote.getUrl(),floor)) {
			return;
		}else {
			insertTopicNoteCrawlInfo(topicNote,0,isMD5);
		}
	}

	/**
	 * 更新state值
	 * @param note_url
	 * @param state
	 */
	public void updateStateValue(String url, int state) {
		if (null == url) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "update topic set `state` = '"+state+"' where url = '"+url+"'";
			dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 更新lucene值
	 * @param note_url
	 * @param state
	 */
	public void updateLuceneValue(String url, int lucene) {
		if (null == url) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "update topic set `state` = '"+lucene+"' where url = '"+url+"'";
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
	private boolean hasExistNoteUrl(String url,int floor) {
		if (null == url) {
			return false;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from topic where url = '"+url+"' AND floor ='"+floor+"'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return false;
	}
	
	/**
	 * 判断Note_Id是否存在
	 * @param note_url
	 * @return
	 */
	private boolean hasExistNoteId(String id) {
		if (null == id) {
			return false;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from detail where floorid = '"+id+"'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return false;
	}
	
	/**
	 * 执行主题帖插入操作
	 * @param topicNote
	 * @param isMD5
	 */
	private void insertTopicNoteCrawlInfo(TopicNote topicNote, int state, boolean isMD5) {
		if (null == topicNote) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5加密
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getUrl()));
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getContent()));
				params.put(i++, topicNote.getCount());
				params.put(i++, topicNote.getFloor());
				params.put(i++, topicNote.getTime());
				params.put(i++, ""+state);
				params.put(i, "1");
			}else {
				params.put(i++, topicNote.getUrl());
				params.put(i++, topicNote.getContent());
				params.put(i++, topicNote.getCount());
				params.put(i++, topicNote.getFloor());
				params.put(i++, topicNote.getTime());
				params.put(i++, ""+state);
				params.put(i, "1");
			}
			dbServer.insert("topic","url,content,count,floor,time,state,lucene",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	
	/**
	 * 更新主题帖子数目
	 * @param topicNote
	 * @param isMD5
	 */
	private void updateTopicNoteCount(TopicNote topicNote, boolean isMD5){
		if (null == topicNote) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer,Object>();
			int i = 1;
			if (isMD5) {
				//MD5加密
				params.put(i++, topicNote.getCount());
			}else {
				params.put(i++, topicNote.getCount());
			}
			String columns = "count";
			String condition = "where url = '"+topicNote.getUrl()+"'";
			dbServer.update("topic", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 保存帖子详情
	 * @param noteDetails
	 * @param isMD5
	 */
	public void saveNoteDetailInfo(List<NoteDetail> noteDetails,boolean isMD5) {
		if (null == noteDetails || 0 == noteDetails.size()) {
			return;
		}
		
		for (NoteDetail noteDetail : noteDetails) {
			int floor = noteDetail.getFloor();
			if (1 == floor) {
				TopicNote topicNote = new TopicNote();
				topicNote.setUrl(noteDetail.getUrl());
				topicNote.setContent(noteDetail.getContent());
				topicNote.setCount(noteDetail.getCount());
				topicNote.setFloor(1);
				topicNote.setTime(noteDetail.getTime());
				saveSingleTopicNoteCrawlInfo(topicNote, 1, isMD5);
				continue;
			}else {
				//MD5加密
				String floorid = ParseMD5.parseStr2MD5(noteDetail.getFloorid());
				if (hasExistNoteId(floorid) || hasExistNoteId(noteDetail.getFloorid())) {
					continue;
				}else {
					insertNoteDetailInfo(noteDetail,isMD5);
				}
			}
		}
	}
	
	/**
	 * 执行帖子详情插入操作
	 * @param noteDetail
	 * @param isMD5
	 */
	private void insertNoteDetailInfo(NoteDetail noteDetail, boolean isMD5) {
		if (null == noteDetail) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;

			if (isMD5) {
				//MD5加密
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getUrl()));
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getContent()));
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getFloorid()));
				params.put(i++, noteDetail.getFloor());
				params.put(i++, noteDetail.getTime());
				params.put(i++, "1");
				params.put(i, "1");
			}else {
				params.put(i++, noteDetail.getUrl());
				params.put(i++, noteDetail.getContent());
				params.put(i++, noteDetail.getFloorid());
				params.put(i++, noteDetail.getFloor());
				params.put(i++, noteDetail.getTime());
				params.put(i++, "1");
				params.put(i, "1");
			}
			dbServer.insert("detail","url,content,floorid,floor,time,state,lucene",params);

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 重置主题帖状态
	 */
	private static void managerTopicState(){
		//UPDATE topic SET state = '0' WHERE floor = '0' AND url IN (SELECT url FROM (SELECT url FROM topic GROUP BY url HAVING COUNT(url) = '2') AS tmp)
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "SELECT url FROM topic GROUP BY url HAVING COUNT(url) = '2'";
			ResultSet rs = dbServer.select(sql);

			List<String> urls = new ArrayList<String>();
			while (rs.next()) {
				String url = rs.getString("url");
				urls.add(url);
			}
			int count = 1;
			for (String url : urls) {
				sql = "UPDATE topic SET state = '0' WHERE floor = '0' AND url = '"+url+"'";
				int i = dbServer.update(sql);
				System.out.println("update "+url+" "+i+" "+(count++));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	
	private static int floor = 1;
	private static String oldUrl ="";
	/**
	 * 执行帖子详情插入操作
	 * @param noteDetail
	 * @param isMD5
	 */
	public void insertNoteDetailInfo(String url, String rid, String content) {
		if (null == url || rid == null || content == null) {
			return;
		}
		
		DbServer dbServer = new DbServer(POOL_NAME);
		
		if (null == content || content.equals("") || content.length() < 6 || content.length() > 120) {
			oldUrl = url;
			++floor;
			return;
		}

		//剔除1楼内容
		if (!oldUrl.equals(url)) {
			insertTopicNoteCrawlInfo(url,content);
			System.out.println(url +"  "+content);
			floor = 1;
			oldUrl = url;
			return;
		}
		++floor;

		if (hasExistNoteId(rid)) {
			return;
		}

		System.out.println(url +"  "+rid+"  "+content);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, url);
			params.put(i++, content);
			params.put(i++, rid);
			params.put(i++, floor);
			params.put(i++, TimeUtil.getCurTime());
			params.put(i++, "0");
			params.put(i, "1");

			dbServer.insert("detail","url,content,floorid,floor,time,state,lucene",params);

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * 执行主题帖插入操作
	 * @param topicNote
	 * @param isMD5
	 */
	private void insertTopicNoteCrawlInfo(String url, String content) {
		if (null == url || null == content) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;

				params.put(i++, url);
				params.put(i++, content);
				params.put(i++, 0);
				params.put(i++, "1");
				params.put(i++, TimeUtil.getCurTime());
				params.put(i++, "1");
				params.put(i, "1");
			dbServer.insert("topic","url,content,count,floor,time,state,lucene",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	public static void main(String[] args) {
		
		
		managerTopicState();
		
//		CrawlXUSTbarDb db = new CrawlXUSTbarDb();

//		System.out.println(JsonUtil.parseJson(db.getTopicNoteCrawlList()));

	}
}
