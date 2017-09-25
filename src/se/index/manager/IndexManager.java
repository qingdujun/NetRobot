package se.index.manager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManagerReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import se.index.model.ConfigBean;
import se.index.model.IndexConfig;

/**
 * 索引管理类
 * @author Dolphix.J Qing
 *
 */
public class IndexManager {
	private IndexWriter indexWriter;
	private TrackingIndexWriter trackingIndexWriter;
	private NRTManager nrtManager;
	private NRTManagerReopenThread nrtManagerReopenThread;
	private IndexCommitThread indexCommitThread;
	private ConfigBean configBean;
	private Analyzer analyzer;
	
	private IndexManager(ConfigBean configBean){
		//索引的存储路径
		String indexFile = configBean.getIndexPath()+"/"+configBean.getIndexName();
		analyzer = configBean.getAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.configBean = configBean;
		Directory directory = null;
		try {
			directory = NIOFSDirectory.open(new File(indexFile));
			if (IndexWriter.isLocked(directory)) {
				IndexWriter.unlock(directory);
			}
			indexWriter = new IndexWriter(directory,indexWriterConfig);
			//将indexWriter委托给trackingIndexWriter
			trackingIndexWriter = new TrackingIndexWriter(indexWriter);
			nrtManager = new NRTManager(trackingIndexWriter, new SearcherFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//启动线程
		startThread();
	}
	
	private static class LazyIndexManager{
		private static HashMap<String, IndexManager> indexManagerMap = new HashMap<String,IndexManager>();
		static{
			for (ConfigBean bean : IndexConfig.getConfigBeans()) {
				indexManagerMap.put(bean.getIndexName(), new IndexManager(bean));
			}
		}
	}
	
	public static IndexManager getIndexManager(String indexName) {
		return LazyIndexManager.indexManagerMap.get(indexName);
	}
	/**
	 * 获取IndexSearcher
	 * @return
	 */
	public IndexSearcher getIndexSearcher() {
		try {
			return nrtManager.acquire();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 释放IndexSearcher
	 * @param indexSearcher
	 */
	public void release(IndexSearcher indexSearcher) {
		try {
			nrtManager.release(indexSearcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 启动线程
	 */
	private void startThread(){		
		//内存重读线程
		nrtManagerReopenThread = new NRTManagerReopenThread(nrtManager, configBean.getIndexReopenMaxStateSeconds(), configBean.getIndexReopenMinStateSeconds());
		nrtManagerReopenThread.setName("NRTManger Reopen Thread");
		//将线程设置为守护线程
		nrtManagerReopenThread.setDaemon(true);
		nrtManagerReopenThread.start();
		//启动硬盘索引线程
		indexCommitThread = new IndexCommitThread(configBean.getIndexName()+"index commit thread");
		indexCommitThread.setDaemon(true);
		indexCommitThread.start();
	}
	
	
	/**
	 * 内部私有类，将内存索引 写入硬盘索引
	 * @author Dolphix.J Qing
	 *
	 */
	private class IndexCommitThread extends Thread{

		private boolean flag = false;
		
		public IndexCommitThread(String name){
			super(name);
		}
		
		@Override
		public void run() {
			flag = true;
			while(flag){
				try {
					indexWriter.commit();
//					System.out.println(new Date()+"\t"+configBean.getIndexName()+"\tcommit");
					Thread.sleep((long)configBean.getIndexCommitSeconds());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void commit() {
		try {
			indexWriter.commit();
			System.out.println(new Date()+"\t"+configBean.getIndexName()+"\tcommit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public TrackingIndexWriter getTrackingIndexWriter() {
		return trackingIndexWriter;
	}
	public Analyzer getAnalyzer() {
		return analyzer;
	}
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
}
