package se.index.model;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import se.index.utils.Const;
/**
 * 索引配置文件
 * @author Dolphix.J Qing
 *
 */
public class ConfigBean {
	private String indexName = Const.INDEX_NAME;
	private String indexPath = Const.INDEX_PATH;
	private Analyzer analyzer = new IKAnalyzer();
	private double indexReopenMaxStateSeconds = 10;
	private double indexReopenMinStateSeconds = 0.025;
	private double indexCommitSeconds = 60;//索引写入磁盘时间间隔
	
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getIndexPath() {
		return indexPath;
	}
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	public Analyzer getAnalyzer() {
		return analyzer;
	}
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public double getIndexReopenMaxStateSeconds() {
		return indexReopenMaxStateSeconds;
	}
	public void setIndexReopenMaxStateSeconds(double indexReopenMaxStateSeconds) {
		this.indexReopenMaxStateSeconds = indexReopenMaxStateSeconds;
	}
	public double getIndexReopenMinStateSeconds() {
		return indexReopenMinStateSeconds;
	}
	public void setIndexReopenMinStateSeconds(double indexReopenMinStateSeconds) {
		this.indexReopenMinStateSeconds = indexReopenMinStateSeconds;
	}
	public double getIndexCommitSeconds() {
		return indexCommitSeconds;
	}
	public void setIndexCommitSeconds(double indexCommitSeconds) {
		this.indexCommitSeconds = indexCommitSeconds;
	}
	
}
