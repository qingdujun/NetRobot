package se.index.model;

import java.util.List;

import org.apache.lucene.document.Document;

public class SearchResultBean {
	private int count;
	private List<Document> docs;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Document> getDocs() {
		return docs;
	}
	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}

}
