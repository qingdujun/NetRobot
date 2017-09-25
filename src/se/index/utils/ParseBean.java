package se.index.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import se.index.model.bar.DetailIndexBean;
import se.index.model.bar.TopicIndexBean;

public class ParseBean {

	/**
	 * 将标题转化为Document
	 * @param bean
	 * @return
	 */
	public static Document parseTitle2Doc(TopicIndexBean bean){
		if (null == bean) {
			return null;
		}
		Document doc = new Document();
		if (null != bean.getUrl()) {
			doc.add(new StringField("url", bean.getUrl(), Store.YES));
		}
		doc.add(new StringField("count", ""+bean.getCount(), Store.NO));
		if (null != bean.getTitle()) {
			doc.add(new TextField("title", bean.getTitle(), Store.YES));
		}
		
		return doc;
	}
	/**
	 * 将内容转化为Document
	 * @param bean
	 * @return
	 */
	public static Document parseContent2Doc(DetailIndexBean bean){
		if (null == bean) {
			return null;
		}
		Document doc = new Document();
		
		if (null != bean.getUrl2()) {
			doc.add(new StringField("url2", bean.getUrl2(), Store.YES));
		}
		if (null != bean.getRid()) {
			doc.add(new StringField("rid", bean.getRid(), Store.YES));
		}
		if (null != bean.getContent()) {
			doc.add(new TextField("content", bean.getContent(), Store.YES));
		}
		
		return doc;
	}
	
}
