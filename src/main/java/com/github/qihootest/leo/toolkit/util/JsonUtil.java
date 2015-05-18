package com.github.qihootest.leo.toolkit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 说明：解析Json字符串，解析后为map存储的键值对
 * @author lianghui (lianghui@360.cn)
 *
 */
public class JsonUtil {
	private Map<String, Object> oneResult = new TreeMap<String, Object>();


	/**
	 * 单层解析json字符串
	 * @param str
	 * @return Map<String, Object> 异常返回null
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResult(String str) {
		JsonUtil jsonUtil = new JsonUtil();
		try {
			jsonUtil.oneResult = JSONObject.fromObject(str);
		} catch (JSONException e) {
			jsonUtil.oneResult = null;
		}
		return jsonUtil.oneResult;
	}

	/**
	 * 多层解析 json字符串
	 * @param str
	 * @return	Map<String, Object> 异常返回null
	 */
	public static Map<String, Object> getAllResult(String str) {
		JsonUtil jsonUtil = new JsonUtil();
		try {
			JSONObject tempJSON = JSONObject.fromObject(str);
			jsonToMap(tempJSON, jsonUtil.oneResult);
		} catch (Exception e) {
			jsonUtil.oneResult = null;
		}
		return jsonUtil.oneResult;
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
				if (resultMap.containsKey(realKey)) { // 判断是否存在相同的key，如果存在相同的key，map将用<String,ArrayList>泛型存储
					ArrayList valueList = new ArrayList();
					if (resultMap.get(realKey) instanceof ArrayList) { // 判断value是否已经为ArrayList，等二次发现有相同的key
						valueList = (ArrayList) resultMap.get(realKey);
						valueList.add(valueObj.toString().trim());
						resultMap.put(realKey, valueList);
					} else { // 第一次处理相同的key，new一个ArrayList
						valueList.add(resultMap.get(realKey));
						valueList.add(valueObj.toString().trim());
						resultMap.put(realKey, valueList);
					}

				} else {
					resultMap.put(realKey, valueObj.toString().trim());
				}

			}
		}
	}
}
