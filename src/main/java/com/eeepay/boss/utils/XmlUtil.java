package com.eeepay.boss.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
/**
 * xml工具类
 * @author wg
 * @date   2014-2-20
 */
public class XmlUtil {
	/**
	 * xml转换成javaBean
	 * @param t
	 * @param xml
	 * @param encoding
	 * @return
	 */
	public static <T> T xmlToObj(Class<T> t,String xml,String encoding){
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance(t);
			Unmarshaller unmarshaller=jaxbContext.createUnmarshaller();
			ByteArrayInputStream bais=new ByteArrayInputStream(xml.getBytes(encoding));
			@SuppressWarnings("unchecked")
			T obj=	(T) unmarshaller.unmarshal(bais);
			bais.close();
			return obj;
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static Map<String, String> parseXmlStrList(String xmlStr) throws Exception {
		System.out.println("--------进入parseXmlStrList----------");
		// 将解析结果存储在HashMap中
		Map<String, Object> map = new HashMap<String, Object>();
		// 创建一个新的字符串   
	    StringReader xmlString = new StringReader(xmlStr);   
	    // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入   
	    InputSource source = new InputSource(xmlString);
	    SAXBuilder saxb = new SAXBuilder();
	    org.jdom.Document document = null;
		try {
			document = saxb.build(source);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		// 得到xml根元素
		org.jdom.Element root = document.getRootElement();
		System.out.println("--------已得到根元素----------");
		// 得到根元素的所有子节点
		List<org.jdom.Element> elementList = ((org.jdom.Element) root).getChildren();
		System.out.println("--------根元素的所有子节点----------"+elementList.size());
		Map<String, String> list=new HashMap<String,String>();
		// 遍历所有子节点
		for (org.jdom.Element e : elementList) {
			System.out.println("--------遍历所有子节点----------");
				
			list.put(e.getName(), e.getText());
			
		}
		System.out.println("--------返回list----------");
		return list;
	}
	
	
	
}
