package netrobot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * Xml工具类(import:dom4j-1.6.1.jar)
 * @author Edchel
 *
 */
public class XmlUtil {
	private static final String noResult = "<result>no result</result>";
	
	/**
	 * xml转化java bean
	 * @param xml
	 * @return
	 */
	public static Document createFromString(String xml) {
		try {
			return DocumentHelper.parseText(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * java bean转化为xml
	 * @param object
	 * @return
	 */
	public static String parseObject2Xml(Object object){
		if (null == object) {
			return noResult;
		}
		StringWriter sw = new StringWriter();
		JAXBContext jaxbContext;
		Marshaller marshaller;
		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(object, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noResult;	
	}
	/**
	 * 获取指定xpath的文本，当解析失败返回null
	 * @param xpath
	 * @param node
	 * @return
	 */
	public static String getTextFromNode(String xpath,Node node){
		try {
			return node.selectSingleNode(xpath).getText();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 读取xml文件
	 * @param path
	 * @return xml文件对应的Document
	 */
	public static Document createFromPath(String path){
		return createFromString(readFile(path));
	}

	/**
	 *  读文件
	 * @param path
	 * @return 返回文件内容字符串
	 */
	private static String readFile(String path) {
		File file = new File(path);
		FileInputStream fileInputStream;
		StringBuffer sb = new StringBuffer();
		try {
			fileInputStream = new FileInputStream(file);
			//错误使用UTF-8读取内容
			String charset = CharsetUtil.getStreamCharset(file.toURI().toURL(), "utf-8");
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String s;
			while ((s = bufferedReader.readLine()) != null){
				s = s.replaceAll("\t", "").trim();
				if (s.length() > 0){
					sb.append(s);
				}
			}
			fileInputStream.close();
			bufferedReader.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return sb.toString();
	}
}
