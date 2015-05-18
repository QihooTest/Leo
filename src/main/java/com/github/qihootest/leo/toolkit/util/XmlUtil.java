package com.github.qihootest.leo.toolkit.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



/**
 * XML文件解析处理
 *	@author lianghui (lianghui@360.cn)
 */
public class XmlUtil {
	private TreeMap<String, Object> resultMap;
	private LogUtil log = LogUtil.getLogger(XmlUtil.class);// 日志记录
	
	public XmlUtil(){
		resultMap = new TreeMap<String, Object>();
	}
	/**
	 * xml文本串解析为Map表
	 * @param strXML
	 * @return Map<String, String>
	 */
	public Map<String, Object> fomatXMLToMap(String strXML) {
		try {
			Document doc = DocumentHelper.parseText(strXML);
			Element root = doc.getRootElement();
			AttriToMap(root); //标签属性解析
			traverse(root); //标签属性解析，遍历
			Dom2Map(doc); //结构解析			
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 判断字符串是否为xml格式
	 * @param str
	 * @return boolean xml可解析则返回true
	 */
	public static boolean isXmlText(String str) {
		try {
			@SuppressWarnings("unused")
			Document doc = DocumentHelper.parseText(str);
			doc=null;
			return true;
		} catch (DocumentException e) {
		}
		return false;
	}
	/**
	 * 标签属性解析
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	private void AttriToMap(Element e) {
		List<Attribute> tempList = e.attributes();
		for (int i = 0; i < tempList.size(); i++) {
			// 属性的取得
			Attribute item = tempList.get(i);
			putMap(resultMap, item.getName(), item.getValue());
//			resultMap.put(item.getName(), item.getValue());
		}

	}

	/**
	 * 标签属性解析，遍历
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	private void traverse(Element e) {
		List<Element> list = e.elements();
		for (int i = 0; i < list.size(); i++) {
			Element iter = list.get(i);
			AttriToMap(iter);
			traverse(iter);
		}
	}

	@SuppressWarnings("unchecked")
	private void Dom2Map(Document doc) {
		if (doc == null)
			return;
		Element root = doc.getRootElement();
		for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = iterator.next();
			List<Element> list = e.elements();
			if (list.size() > 0) {
				Dom2Map(e);
			} else
				putMap(resultMap, e.getName(), e.getText());
//				resultMap.put(e.getName(), e.getText());
		}
	}

	@SuppressWarnings("unchecked")
	private void Dom2Map(Element e) {
		List<Element> list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = list.get(i);
				if (iter.elements().size() > 0) {
					Dom2Map(iter);
				} else {
					putMap(resultMap, iter.getName(), iter.getText());
//					resultMap.put(iter.getName(), iter.getText());
				}
			}
		} else {
			putMap(resultMap, e.getName(), e.getText());
//			resultMap.put(e.getName(), e.getText());
		}
	}
	
	/**
	 * map增加key-value逻辑，如果已存在，则不覆盖，以arraylist存储
	 * @param map
	 * @param key
	 * @param value
	 */
	
	private void putMap(TreeMap<String,Object>map,String key,String value){
		if(map.containsKey(key)){ //判断原始map中是否已存在重复key
			Object valueOld = map.get(key);
			if (map.get(key) instanceof ArrayList) { // 判断value是否已经为ArrayList，等二次发现有相同的key
				((ArrayList) valueOld).add(value.trim());
				map.put(key, valueOld);
			} else { // 第一次处理相同的key，new一个ArrayList
				ArrayList valueList = new ArrayList();
				valueList.add(valueOld);
				valueList.add(value.trim());
				map.put(key, valueList);
		    }
		}else{
			map.put(key, value);
		}
	}
}
