package com.github.qihootest.leo.toolkit.util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 说明：解析Json字符串，解析后为map存储的键值对
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class JsonUtil {
	private static Map<String, Object> oneResult = new TreeMap<String, Object>();


	/**
	 * 单层解析json字符串
	 * @param str
	 * @return Map<String, Object> 异常返回null
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResult(String str) {
		try {
			oneResult = JSONObject.fromObject(str);
		} catch (JSONException e) {
			oneResult=null;
		}
		return oneResult;
	}

	/**
	 * 多层解析 json字符串
	 * @param str
	 * @return	Map<String, Object> 异常返回null
	 */
	public static Map<String, Object> getAllResult(String str) {
		try {
			JSONObject tempJSON = JSONObject.fromObject(str);
			jsonToMap(tempJSON,oneResult);
		} catch (Exception e) {
			oneResult=null;			
		}
		return oneResult;
	}

	@SuppressWarnings("unchecked")
	private static void jsonToMap(JSONObject tempJSON,Map<String,Object> resultMap) {
		for (Iterator<String> it = tempJSON.keys(); it.hasNext();) {
			String key = it.next();
			String realKey = key;
			Object valueObj = tempJSON.get(key);
			if (valueObj instanceof JSONObject) {
				JSONObject jo = (JSONObject) valueObj;
				jsonToMap(jo,resultMap);
			} else if (valueObj instanceof JSONArray) {
				JSONArray ja = (JSONArray) valueObj;
				for (int i = 0; i < ja.size(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					jsonToMap(jo,resultMap);
				}
			} else { // 简单类型
				resultMap.put(realKey, valueObj.toString());
			}
		}
	}
}
