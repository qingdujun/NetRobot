package se.index.operation.bar;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import se.index.bar.db.IndexXUSTbarDb;
import se.index.model.SearchResultBean;
import se.index.operation.NRTSearch;
import se.index.utils.Const;
import se.index.utils.QueryUtil;

/**
 * Db索引搜索
 * @author Dolphix.J Qing
 *
 */
public class BarNRTSearch extends NRTSearch{

	private IndexXUSTbarDb db = new IndexXUSTbarDb();

	public BarNRTSearch() {
		super(Const.INDEX_NAME);
	}

	/**
	 * 执行搜索
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryTopicNote(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{new SortField("title", SortField.Type.STRING),new SortField("count", SortField.Type.STRING)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}


	/**
	 * 执行搜索
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryDetail(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "content", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{new SortField("content", SortField.Type.STRING),new SortField("rid", SortField.Type.STRING)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 搜索最热相关主题帖子（待SQL）
	 * @param keywords
	 * @return
	 */
	public String searchHotTopicUrl(String keywords){
		int SIZE = 3;
		if (null == keywords || keywords.equals("")) {
			return null;
		}
		SearchResultBean bean = executeQueryTopicNote(keywords,0,SIZE);

		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		return docs.get((int)(Math.random() * docs.size())).get("url");
	}
	/**
	 * SQL 搜索数据库，随机获得返回值
	 * @param url
	 * @param n
	 * @return
	 */
	public String searchDb(String url, int n) {
		List<String> data = db.searchDbByUrl(url,n);
		if (null == data || 0 == data.size()) {
			return null;
		}
		return data.get((int)(Math.random() * data.size()));
	}

	/**
	 * 搜索最热相关帖子内容（直接返回）
	 * @param keywords
	 * @return
	 */
	public String searchRandomDetail(String keywords){
		int SIZE = 13;
		SearchResultBean bean = executeQueryDetail(keywords,0,SIZE);
		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		return docs.get((int)(Math.random() * docs.size())).get("content").trim();
	}
	/**
	 * 搜索引擎+数据库搜索
	 * @param keywords
	 * @return
	 */
	public String searchRandomTopicReply(String keywords){
		int SIZE = 13;
		if (null == keywords || keywords.equals("")) {
			return null;
		}
		String url = searchHotTopicUrl(keywords);
		if (null == url) {
			return null;
		}
		return searchDb(url, SIZE);
	}
	//------------联系上下文搜索--------------


	/**
	 * 搜索问题Q，相关度、回复数
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryQuestion(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField("count", SortField.Type.INT,true)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 搜索回复A，相关度、可信度
	 * @param a
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryAnswer(String a,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		//通配符搜索
		Query query = new WildcardQuery(new Term("url2", "*"+a+"*"));
		SearchResultBean bean = search(query, start, end,new Sort(new SortField[]{new SortField("score", SortField.Type.FLOAT,true),SortField.FIELD_SCORE}));
		return bean;
	}


	/**
	 * 联系上下文搜索
	 * @param email
	 * @param keywords
	 * @return
	 */
	public HashMap<String,String> searchInContext(String email,String keywords){

		//1.从数据库读取上下文
		List<String> context = db.readDbContext(email,keywords);
		//2.与当前句拼接
		if (context != null && context.size() > 1) {
			keywords = QueryUtil.parseQuery(context);
		}
//		System.out.println(keywords);
		//3.执行与或子搜索
		SearchResultBean bean = executeQueryQuestion(keywords,0,getSearchQCount());
		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		//4.获得问题
		String qUrl = docs.get((int)(Math.random() * docs.size())).get("url").trim();
		System.out.println("Q - "+qUrl);
		//多域搜索
		bean = executeQueryAnswer(qUrl, 0, getSearchACount());
		docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		//5.获得答案
		HashMap<String, String> hashMap = new HashMap<String,String>();
		String rid = docs.get((int)(Math.random() * docs.size())).get("rid").trim();
		String content = docs.get((int)(Math.random() * docs.size())).get("content").trim();
		System.out.println("A - "+content);
		hashMap.put("key", rid);
		hashMap.put("text", content);
		
		return hashMap;
	}

	/**
	 * 返回Q记录条数
	 * @return
	 */
	private int getSearchQCount(){

		if (Math.random() > 0.9f) {
			return 3;
		}else if (Math.random() > 0.7f) {
			return 2;
		}
		return 1;
	}
	
	/**
	 * 返回A记录条数
	 * @return
	 */
	private int getSearchACount(){

		if (Math.random() > 0.9f) {
			return 7;
		}else if (Math.random() > 0.7f) {
			return 5;
		}else if (Math.random() > 0.5f) {
			return 3;
		}
		return 2;
	}
	
	
	public static void main(String[] args) {

//		System.out.println(getSearchQCount());
//		System.out.println(getSearchACount());
		
//		BarNRTSearch bs = new BarNRTSearch();
//		//searchInContext("487f87505f619bf9ea08f26bb34f8118", "有没有爬华山的");
//		System.out.println(bs.searchInContext("487f87505f619bf9ea08f26bb34f8118", "有没有爬华山的"));
	}
}
