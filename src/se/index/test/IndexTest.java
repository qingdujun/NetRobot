//package se.index.test;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//
//public class IndexTest{
//	private static int count = 1;
//	
//	
//	private static  final String url = "http://10.100.58.51:8080/NetRobot/servlet/Vcrobot?email=487f87505f619bf9ea08f26bb34f8118&keywords=";
//	private static final String[] question = {
//		"骊山小道怎么走？",
//		"周五校园网可以用吗？",
//		"213宿舍谁最帅？",
//		"西科大校长是谁？",
//		"今天天气怎么样？",
//		"明天呢？",
//		"国庆爬华山人多不？",
//		"兵马俑门票是多少？",
//		"高数挂科了，直接清考吗？",
//		"你叫什么名字？"
//	};
//	
//	public static void main(String[] args) {
//		for (int i = 0; i < 10; i++) {
//			doGet(url+question[i]);
//			try {
//				Thread.sleep(100);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//	
//	
//	//创建虚拟用户
//	private static void createThread(){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				long start = System.currentTimeMillis();
//				//发送服务请求
//				doGet(url);
//				long end = System.currentTimeMillis();
//				System.out.println((count++)+"  "+(end-start)+"ms");
//			}
//		}).start();
//	}
//	
//	/**
//     * doGet发送网络请求
//     * @param url
//     * @return
//     */
//    public static String doGet(String url) {
//
//        try{
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url);
//            HttpResponse httpResponse = httpClient.execute(httpGet);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                HttpEntity httpEntity = httpResponse.getEntity();
//                InputStream inputStream = httpEntity.getContent();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String line = null;
//                StringBuffer stringBuffer = new StringBuffer();
//                while (null != (line = bufferedReader.readLine())) {
//                    stringBuffer.append(line);
//                }
//                return stringBuffer.toString();
//            }
//        }catch (Exception e){e.printStackTrace();}
//
//        return null;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package se.index.test;
////
////import java.util.HashSet;
////
////import org.apache.lucene.analysis.standard.StandardAnalyzer;
////import org.apache.lucene.document.Document;
////import org.apache.lucene.document.Field.Store;
////import org.apache.lucene.document.StringField;
////import org.apache.lucene.document.TextField;
////import org.apache.lucene.index.Term;
////import org.apache.lucene.queryparser.classic.ParseException;
////import org.apache.lucene.queryparser.classic.QueryParser;
////import org.apache.lucene.search.Query;
////import org.apache.lucene.util.Version;
////
////import se.index.manager.IndexManager;
////import se.index.model.ConfigBean;
////import se.index.model.IndexConfig;
////import se.index.model.SearchResultBean;
////import se.index.operation.NRTIndex;
////import se.index.operation.NRTSearch;
////
////public class IndexTest {
////
////	public static void main(String[] args) throws ParseException {
////		HashSet<ConfigBean> configBeans = new HashSet<ConfigBean>();
////		//添加3个索引文件配置
////		for (int i = 0; i < 4; i++) {
////			ConfigBean bean = new ConfigBean();
////			bean.setIndexPath("E:/lucene_index/test");
////			bean.setIndexName("test"+i);
////			configBeans.add(bean);
////		}
////		IndexConfig.setConfigBeans(configBeans);
////		String indexName = "test0";
////		NRTIndex nrtIndex = new NRTIndex(indexName);
////		Document doc1 = new Document();
////		
////		doc1.add(new StringField("id","1",Store.YES));
////		doc1.add(new TextField("content","极客学院",Store.YES));
////		nrtIndex.addDocument(doc1);
////		
////		Document doc2 = new Document();
////		doc1.add(new StringField("id","2",Store.YES));
////		doc1.add(new TextField("content","Lucene案例开发",Store.YES));
////		nrtIndex.addDocument(doc2);
////		nrtIndex.commit();
////		System.out.println("已添加2条记录");
////		
////		NRTSearch nrtSearch = new NRTSearch(indexName);
////		QueryParser parser = new QueryParser(Version.LUCENE_43, "content", new StandardAnalyzer(Version.LUCENE_43));
////		Query query = parser.parse("极客学院Lucene案例开发");
////		SearchResultBean bean = nrtSearch.search(query, 0, 10);
////		System.out.println("第1次查询"+bean.getCount());
////
////		doc1 = new Document();
////		doc1.add(new StringField("id","2",Store.YES));
////		doc1.add(new TextField("content","",Store.YES));
////		Term term = new Term("id", "2");
////		nrtIndex.updateDocument(term, doc1);
////		nrtIndex.commit();
////		System.out.println("第1次修改记录");
////		bean = nrtSearch.search(query, 0, 10);
////		System.out.println("第2次查询"+bean.getCount());
////	}
////
////}
