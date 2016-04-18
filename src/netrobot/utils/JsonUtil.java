package netrobot.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Json工具类(import:jackson-databind-2.1.4.jar,jackson-annotations-2.1.4.jar,jackson-core-2.1.4.jar)
 * @author qingdujun
 *
 */
public class JsonUtil {

	private static final String noData = "{\"result\" : null}";
	private static ObjectMapper mapper;
	
	static{
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	/**
	 * Json转化为字符串
	 * @param object
	 * @return
	 */
	public static String parseJson(Object object){
		if (null == object) {
			return noData;
		}try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noData;
	}
	
	/**
	 * Json转化为java bean
	 * @param json
	 * @return
	 */
	public static JsonNode json2Object(String json){
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 给定java对象生成对应json,可以指定一个json的root名
	 * @param obj
	 * @param root
	 * @return 当解析失败返回{datas:null} 
	 * 
	 */
	public static String parseJson(Object obj, String root){

		if(obj == null){
			return noData;
		}

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("{\"");
			sb.append(root);
			sb.append("\":");
			sb.append(mapper.writeValueAsString(obj));
			sb.append("}");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noData;
	}

	/**
	 * 将json字符串包装成jsonp，例如var data={}方式
	 * @param json
	 * @param var
	 * @return 若传入var为null，则默认变量名为datas
	 * 
	 */
	public static String wrapperJsonp(String json, String var){
		if(var == null){
			var = "datas";
		}
		return new StringBuilder().append("var ").append(var).append("=").append(json).toString();
	}
}
