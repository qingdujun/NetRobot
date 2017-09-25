package se.index.utils;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;

import se.index.manager.IndexManager;

public class QueryUtil {

	//联系上下文句数
	private static final int N = 3;
	private Analyzer analyzer;

	public QueryUtil(String indexName) {
		analyzer = IndexManager.getIndexManager(indexName).getAnalyzer();
	}

	public Query getOneFieldQuery (String field, String value) {
		if (field == null || value == null) {
			return null;
		}
		QueryParser parse = new QueryParser(Version.LUCENE_43, field, analyzer);
		try {
			return parse.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Query getStringQuery (String fieldName, String value) {
		if (fieldName == null || value == null) {
			return null;
		}
		return new TermQuery(new Term(fieldName, value));
	}

	/**
	 * 通配符查询
	 * @param field
	 * @param value
	 * @return
	 */
	public static Query getWildcardQuery (String field, String value) {
		if (field == null || value == null) {
			return null;
		}
		value = "*" + value + "*";
		return new WildcardQuery(new Term(field, value));
	}


	/**
	 * 组合查询
	 * @param queryList
	 * @return
	 */
	public static String parseQuery(List<String> queryList) {

		if (queryList == null) {
			return null;
		}
		//只有一句话
		if (queryList.size() < 2) {
			return queryList.get(0);
		}
		String query = "( ";

		for (int i = 0; i < queryList.size()-1; i++) {
			query += queryList.get(i)+" OR ";
		}
		query = query.substring(0, query.lastIndexOf("OR"))+" )";
		query += " AND "+queryList.get(queryList.size()-1);

		return query;
	}
	/**
	 * 联系上下文——3句
	 * @param queryList
	 * @param content
	 */
	public static void keepSize(List<String> queryList) {

		if (queryList == null) {
			return;
		}
		if (queryList.size() > N) {
			queryList.remove(0);
		}
	}
}
