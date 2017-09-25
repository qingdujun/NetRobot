package se.index.operation;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.Query;

import se.index.manager.IndexManager;

/**
 * 
 * @author Dolphix.J Qing
 *
 */
public class NRTIndex {
	private TrackingIndexWriter trackingIndexWriter;
	private String indexName;
	
	public NRTIndex(String indexName){
		this.indexName = indexName;
		this.trackingIndexWriter = IndexManager.getIndexManager(indexName).getTrackingIndexWriter();
	}
	/**
	 * 添加索引
	 * @param doc
	 * @return
	 */
	public boolean addDocument(Document doc) {
		try {
			trackingIndexWriter.addDocument(doc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 删除索引
	 * @param query
	 * @return
	 */
	public boolean deleteDocument(Query query){
		try {
			trackingIndexWriter.deleteDocuments(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 索引清空操作
	 * @return
	 */
	public boolean deleteAll(){
		try {
			trackingIndexWriter.deleteAll();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 索引修改操作
	 * @param term
	 * @param doc
	 * @return
	 */
	public boolean updateDocument(Term term, Document doc){
		try {
			trackingIndexWriter.updateDocument(term, doc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 提交索引
	 */
	public void commit() {
		IndexManager.getIndexManager(indexName).commit();
	}
}
