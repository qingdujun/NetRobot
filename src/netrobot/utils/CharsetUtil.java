package netrobot.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
/**
 *  编码检测(import:cpdetector_1.0.10.jar,chardet-1.0.jar)
 * @author qingdujun
 *
 */
public class CharsetUtil {

	private static final CodepageDetectorProxy detector;
	
	
	static{
		detector = CodepageDetectorProxy.getInstance();
		detector.add(new ParsingDetector(false));
		detector.add(ASCIIDetector.getInstance());
		detector.add(UnicodeDetector.getInstance());
		detector.add(JChardetFacade.getInstance());
	}
	/**
	 * 检测文件的编码方式
	 * @param url
	 * @param defaultCharset
	 * @return
	 */
	public static String getStreamCharset(URL url,String defaultCharset) {
		if (null == url) {
			return defaultCharset;
		}
		try {
			Charset charset = detector.detectCodepage(url);
			if (null != charset) {
				return charset.name();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return defaultCharset;
	}
	
	/**
	 * 检测流的编码方式
	 * @param inputStream
	 * @param defaultCharset
	 * @return
	 */
	public static String getStreamCharset(InputStream inputStream,String defaultCharset) {
		if (null == inputStream) {
			return defaultCharset;
		}
		int count = 200;
		try {
			//流的可用长度
			count = inputStream.available();
			Charset charset = detector.detectCodepage(inputStream, count);
			if (null != charset) {
				return charset.name();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return defaultCharset;
	}

}
