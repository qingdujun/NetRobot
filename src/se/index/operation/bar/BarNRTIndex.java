package se.index.operation.bar;

import java.util.ArrayList;
import java.util.List;

import netrobot.crawl.bar.model.NoteDetail;
import netrobot.crawl.bar.model.TopicNote;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import se.index.bar.db.IndexXUSTbarDb;
import se.index.model.bar.DetailIndexBean;
import se.index.model.bar.TopicIndexBean;
import se.index.operation.NRTIndex;
import se.index.utils.Const;
import se.index.utils.ParseBean;

/**
 * 索引创建
 * @author Dolphix.J Qing
 *
 */
public class BarNRTIndex extends NRTIndex{
	
	private IndexXUSTbarDb db;
	
	public BarNRTIndex() {
		super(Const.INDEX_NAME);
		db = new IndexXUSTbarDb();
	}
	/**
	 * 添加一条主题帖子至索引
	 * @param bean
	 * @return
	 */
	public boolean addTopicIndex(TopicIndexBean bean){
		Document doc = ParseBean.parseTitle2Doc(bean);
		if (null != doc) {
			return updateDocument(new Term("url"), doc);
		}
		return false;
	} 
	
	/**
	 * 添加一条帖子详情内容至索引
	 * @param bean
	 * @return
	 */
	public boolean addDetailIndex(DetailIndexBean bean){
		Document doc = ParseBean.parseContent2Doc(bean);
		if (null != doc) {
			return updateDocument(new Term("rid"), doc);
		}
		return false;
	} 
	/**
	 * 将数据库大量主题帖添加至索引
	 * @return
	 */
	public void dbTopicImport2Index(){
		List<TopicNote> topicNotes = null;
		int count = 1000;
		boolean flag = true;
		while( flag ) {
			//分页条件查询，应该从一直从row = 0开始
			topicNotes = db.getDbTopicNote(0, count);
			if (null == topicNotes || 0 == topicNotes.size()) {
				flag = false;
				continue;
			}
			for (TopicNote topicNote : topicNotes) {
				TopicIndexBean bean = new TopicIndexBean();
				bean.setUrl(topicNote.getUrl());
				bean.setCount(topicNote.getCount());
				bean.setTitle(topicNote.getContent());
				addTopicIndex(bean);
			}
			
			commit();
			db.updateDbIndexState("topic", 0,topicNotes.get(0).getId(),topicNotes.get(topicNotes.size()-1).getId());
		}
	}

	/**
	 * 将数据库大量帖子详情添加至索引
	 * @return
	 */
	public void dbDetailImport2Index(){
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();
		int count = 1000;
		boolean flag = true;
		while( flag ) {
			//分页条件查询，应该从一直从row = 0开始
			noteDetails = db.getDbNoteDetail(0, count);
			if (null == noteDetails || 0 == noteDetails.size()) {
				flag = false;
				continue;
			}
			for (NoteDetail noteDetail: noteDetails) {
				DetailIndexBean bean = new DetailIndexBean();
				bean.setUrl2(noteDetail.getUrl());
				bean.setRid(noteDetail.getFloorid());
				bean.setContent(noteDetail.getContent());
				bean.setScore(noteDetail.getScore());
				addDetailIndex(bean);
			}
			commit();
			db.updateDbIndexState("detail", 0,noteDetails.get(0).getId(),noteDetails.get(noteDetails.size()-1).getId());
			
		}
	}
	/**
	 * 索引问题
	 */
	public void indexQ() {
		new Thread(new Runnable() {
			boolean flagQ = true;
			@Override
			public void run() {
				while(flagQ){
					System.out.println("Tips - Indexing Q");
					dbTopicImport2Index();
					System.out.println("Tips - Q index finish");
					try {
						//1分钟延迟
						Thread.sleep(60*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	/**
	 * 索引回复
	 */
	public void indexA() {
		new Thread(new Runnable() {
			boolean flagA = true;
			@Override
			public void run() {
				while(flagA){
					System.out.println("Tips - Indexing A");
					dbDetailImport2Index();
					System.out.println("Tips - A index finish");
					try {
						//一分钟延迟
						Thread.sleep(60*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}

















//NRTIndex nrtIndexTopic = null;
//NRTIndex nrtIndexDetail = null;
//private HashSet<ConfigBean> configBeans = new HashSet<ConfigBean>();
//
//private int count = 1000;
//
////索引名
//public static List<String> indexName;
////索引名
//public static List<String> indexPath;
//
//static{
//	//索引名
//	indexName = new ArrayList<String>();
//	indexName.add("topicnote");
//	indexName.add("notedetail");
//	//索引路径
//	indexPath = new ArrayList<String>();
//	indexPath.add("E:/lucene_index_bar/xust");
//	indexPath.add("E:/lucene_index_bar/xust");
//}
//
//public BarNRTIndex(){
//	setConfigBeans(setHashMap(indexName), setHashMap(indexPath));
//	db = new IndexXUSTbarDb();
//	nrtIndexTopic = new NRTIndex("topicnote");
//	nrtIndexDetail = new NRTIndex("notedetail");
//}
//
///**
// * 添加索引文件配置
// * @param indexName
// * @param indexPath
// */
//private void setConfigBeans(HashMap<Integer, String> indexName, HashMap<Integer, String> indexPath){
//	for (int i = 0; i < indexName.size(); i++) {
//		ConfigBean bean = new ConfigBean();
//		bean.setIndexPath(indexPath.get(i));
//		bean.setIndexName(indexName.get(i));
//		configBeans.add(bean);
//	}
//	IndexConfig.setConfigBeans(configBeans);
//}
///**
// * 构造HashMap
// * @param list
// * @return
// */
//public static HashMap<Integer, String> setHashMap(List<String> list) {
//	HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
//	for (int i = 0; i < list.size(); i++) {
//		hashMap.put(i, list.get(i));
//	}
//	return hashMap;
//}
///**
// * 建立索引
// */
//private void addDocument(NRTIndex nrtIndex, String url, String content){
//	Document doc = new Document();
//	//默认不分词
//	doc.add(new StringField("url",url,Store.YES));
//	//默认分词
//	doc.add(new TextField("content",content,Store.YES));
//	nrtIndex.addDocument(doc);
//}
///**
// * 拉取数据库主题帖内容
// * @param row
// * @param count
// * @return
// */
//private List<TopicNote> getDbTopicNotesContent(int row, int count){
//	List<TopicNote> topicNotes = db.getTopicNoteContent(row, count);
//	return topicNotes;
//}
///**
// * 拉取数据库帖详情内容
// * @param row
// * @param count
// * @return
// */
//private List<NoteDetail> getDbNoteDetailsContent(int row, int count){
//	List<NoteDetail> noteDetails = db.getNoteDetailContent(row, count);
//	return noteDetails;
//}
///**
// * 获得主题帖需索引数量
// * @return
// */
//private int getDbTopicNoteCount(){
//	return db.getTopicNoteCount();
//}
///**
// * 获得详情帖需索引数量
// * @return
// */
//private int getDbNoteDetailCount(){
//	return db.getNoteDetailCount();
//}
///**
// * 执行主题帖子索引
// */
//public void executeTopicNoteIndex(){
//	int index_count = getDbTopicNoteCount();
//	int row = 0;
//	List<TopicNote> topicNotes;
//	for (  ; row < index_count;  ) {
//		//分页提取主题帖内容
//		topicNotes = getDbTopicNotesContent(row, count);
//		row += topicNotes.size();
//		//对帖子标题建立索引
//		for (int i = 0; i < topicNotes.size(); i++) {
//			addDocument(nrtIndexTopic,topicNotes.get(i).getNote_url(), topicNotes.get(i).getNote_title());
//		}
//		nrtIndexTopic.commit();
//	}
//}
///**
// * 执行帖子详情索引
// */
//public void executeNoteDetailIndex(){
//	int index_count = getDbNoteDetailCount();
//	int row = 0;
//	List<NoteDetail> noteDetails;
//	for (  ; row < index_count;  ) {
//		//分页提取主题帖内容
//		noteDetails = getDbNoteDetailsContent(row, count);
//		row += noteDetails.size();
//		//对帖子标题建立索引
//		for (int i = 0; i < noteDetails.size(); i++) {
//			addDocument(nrtIndexDetail,noteDetails.get(i).getNote_url(), noteDetails.get(i).getReply_context());
//		}
//		nrtIndexDetail.commit();
//	}
//}