package ltp.cloud.analysis;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import ltp.cloud.bean.Word;
import ltp.cloud.utils.ConstUtil;
import ltp.cloud.utils.NetRequest;

public class TypeWord {

	//http://api.ltp-cloud.com/analysis/?api_key=API_KEY&text=""&pattern=pos&format=json
	/**
	 * 获得分析结果List
	 * @param keywords
	 * @return
	 */
	public static List<Word> analysisWordType(String keywords){
		
		String json = NetRequest.doGet(getLTPPosURL(keywords));
		if (json == null) {
			return null;
		}

		return parseJson(json);
	}
	
	/**
	 * 拼接LTP请求URL
	 * @param text
	 * @return
	 */
	private static String getLTPPosURL(String text){
		return ConstUtil.LTP_URL+"?api_key="+ConstUtil.api_key+"&text="+text+"&pattern=pos&format=json";
	}
	/**
	 * Json转List
	 * @param json
	 * @return
	 */
	private static List<Word> parseJson(String json){
		List<Word> words = new ArrayList<Word>();
		JSONArray jsonArray = JSONArray.fromObject(json).getJSONArray(0).getJSONArray(0);
		
		for (int i = 0; i < jsonArray.size(); i++) {
			String pos = (String)jsonArray.getJSONObject(i).getString("pos");
			if (pos.equals("n") || pos.equals("nh") || pos.equals("ni") || pos.equals("nl") || pos.equals("ns")
					|| pos.equals("nt") || pos.equals("nz") || pos.equals("ws")) {
				String cont = (String)jsonArray.getJSONObject(i).getString("cont");
				
				Word word = new Word();
				word.setPos(pos);
				word.setCont(cont);
				
				words.add(word);
			}
		}
		return words;
	}
	
	public static void main(String[] args) {
//		String json = "[[[{\"id\": 2, \"cont\": \"中国\", \"pos\": \"ns\"}, {\"id\": 3, \"cont\": \"人\", \"pos\": \"n\"}]]]";

		for (Word w : analysisWordType("基于Android虚拟聊天机器人")) {
			System.out.println(w.getPos()+"-----"+w.getCont());
		}

	}
}
