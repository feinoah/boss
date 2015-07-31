package com.eeepay.boss.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 */
public class XMLChongzhi {

	/**
	 * 
	 * @param xmlStr
	 */
	public static Map<String,String> parsersXml(String xmlStr) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			StringReader sr = new StringReader(xmlStr); 
			InputSource is = new InputSource(sr); 
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList nl1 = doc.getElementsByTagName("items");
			int size1 = nl1.getLength();
			Map<String,String> params=new HashMap<String,String>();
			for (int i = 0; i < size1; i++) {
				Node n = nl1.item(i);
				NodeList nl2 = n.getChildNodes();
				int size2 = nl2.getLength();
				for (int j = 0; j < size2; j++) {
					Node n2 = nl2.item(j);
					NamedNodeMap map=n2.getAttributes();
					params.put(map.getNamedItem("name").getNodeValue(),  map.getNamedItem("value").getNodeValue());
				}
			}
//			return params.get("resultno");
			return params;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
