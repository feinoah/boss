package com.eeepay.boss.utils;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.eeepay.boss.domain.MobileProduct;

/**
 * 
 * @author hdb
 */
public class XMLAnalysis {

	/**
	 * @param xmlStr
	 */
	public static void parsersXml(String xmlStr,List<MobileProduct> list) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			StringReader sr = new StringReader(xmlStr); 
			InputSource is = new InputSource(sr); 
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList nl1 = doc.getElementsByTagName("products");
			int size1 = nl1.getLength();
			for (int i = 0; i < size1; i++) {
				Node n = nl1.item(i);
				NodeList nl2 = n.getChildNodes();
				int size2 = nl2.getLength();
				Map<String,String> params=new HashMap<String,String>();
				for (int j = 0; j < size2; j++) {
					Node n2 = nl2.item(j);
					NamedNodeMap map=n2.getAttributes();
					params.put(map.getNamedItem("name").getNodeValue(),map.getNamedItem("value").getNodeValue());
				}
				list.add(new MobileProduct(params.get("prodId") , Integer.parseInt(params.get("prodContent")) , new BigDecimal(params.get("prodPrice")) ,
						params.get("prodIsptype") ,params.get("prodDelaytimes") , params.get("prodProvinceid"),
						params.get("prodType")) );
			}
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		}
	}

}
