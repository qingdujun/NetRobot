package se.index.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import se.index.manager.IndexManager;
import se.index.model.SearchResultBean;

/**
 * 索引查询操作
 * @author Dolphix.J Qing
 *
 */
public class NRTSearch {
	private IndexManager indexManager;
	
	public NRTSearch(String indexName){
		this.indexManager = IndexManager.getIndexManager(indexName);
	}
	/**
	 * 使用自定义查询方式
	 * @param query
	 * @param start
	 * @param end
	 * @param sort
	 * @return
	 */
	public SearchResultBean search(Query query, int start, int end, Sort sort){
		start = (start > 0) ? start : 0;
		end = (end > 0) ? end : 0;
		if (null == query || start > end || null == indexManager) {
			return null;
		}
		SearchResultBean bean = new SearchResultBean();
		List<Document> docs = new ArrayList<Document>();
		bean.setDocs(docs);
		IndexSearcher searcher = indexManager.getIndexSearcher();
		try {
			TopDocs topDocs = searcher.search(query, end, sort);
			bean.setCount(topDocs.totalHits);
			end = (end > topDocs.totalHits) ? topDocs.totalHits : end; 
			for (int i = start; i < end; i++) {
				docs.add(searcher.doc(topDocs.scoreDocs[i].doc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			indexManager.release(searcher);
		}
		return bean;
	}
	/**
	 * 默认查询方式
	 * @param query
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResultBean search(Query query, int start, int end) {
		start = (start > 0) ? start : 0;
		end = (end > 0) ? end : 0;
		if (null == query || start > end || null == indexManager) {
			return null;
		}
		SearchResultBean bean = new SearchResultBean();
		List<Document> docs = new ArrayList<Document>();
		bean.setDocs(docs);
		IndexSearcher searcher = indexManager.getIndexSearcher();
		try {
			TopDocs topDocs = searcher.search(query, end);
			bean.setCount(topDocs.totalHits);
			end = (end > topDocs.totalHits) ? topDocs.totalHits : end; 
			for (int i = start; i < end; i++) {
				docs.add(searcher.doc(topDocs.scoreDocs[i].doc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			indexManager.release(searcher);
		}
		return bean;
	}
}
