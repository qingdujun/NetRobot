package netrobot.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	public static String getCurTime(){
		long curTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(curTime);  
		return sdf.format(date);  
	}
	
	public static void main(String[] args) {
		//2016-05-26 23:03
		System.out.println(getCurTime());
	}
}
