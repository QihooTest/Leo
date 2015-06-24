package com.github.qihootest.leo.toolkit.util;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 字符串相关处理工具类
 *	@author lianghui (lianghui@360.cn)
 */
public class StringUtil {
	
	private  static LogUtil log=LogUtil.getLogger(StringUtil.class);//记录日志
	/**
	 * 过滤指定的特殊字符
	 * @param str
	 * @return String
	 */
    public   static   String filterSpecialChar(String str)  {      
        // 清除掉所有特殊字符   
        String regEx="[`~!@#$%^&*()+-=|{}':;',//\\[//\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";   
        Pattern   p   =   Pattern.compile(regEx);      
        Matcher   m   =   p.matcher(str);      
        return   m.replaceAll("").trim();      
    }
    
    /**
     * 判断字符串是否空值或者空串，
     * @param str
     * @return boolean 非空且长度大于1时，返回false；
     */
    public static boolean IsNullOrEmpty(String str) {
		if (null== str || str.length()<1) {
			return true;
		}else{
			return false;
		}
	}

    /**
	 * 以指定标识分割字符串，
	 * @param s
	 * @return List<String> 当字符串或分割标识为null或空时，返回大小为0的List
	 */
	public static List<String> getListFromString(String s,String flag) {
		if (IsNullOrEmpty(s)) {
			return new ArrayList<>();
		}
		if (IsNullOrEmpty(flag)) {
			return new ArrayList<>();
		}
		String[] ns=s.split(flag);
		List<String> list=new ArrayList<>();
		for (int i = 0; i < ns.length; i++) {
			list.add(ns[i]);
		}
		return list;
	}

	/**
	 * 查找字符串中是否存在指定字符串中的任一字符
	 * 
	 * @param str
	 * @return boolean 如果存在任一个返回true，否则返回false
	 */
	public static boolean check(String str,String sIllegal) {
		int len = sIllegal.length();
		if (null == str)
			return false;
		for (int i = 0; i < len; i++) {
			if (str.indexOf(sIllegal.charAt(i)) != -1)
				return true;
		}
		return false;
	}

	/**
	 * 将带有htmlcode代码的字符转换成<>&'"
	 * @param str
	 * @return String
	 */
	public static String htmlcodeToSpecialchars(String str) {
		str = str.replaceAll("&amp;", "&");
		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&#039;", "'");
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		return str;
	}
	
	
	/**
	 * 把16进制转换成字节码
	 * 
	 * @param hex
	 * @return byte[]
	 */
	public static byte[] hex2byte(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}
	
	/**
	 * 判断字符串中是否包含数字
	 * @param src
	 * @return boolean 有数字时返回true 否则false
	 */
	public static boolean isNumeric(String src) {
		Pattern numericPattern = Pattern.compile("^[0-9]+$");
		if (src == null || src.equals(""))
			return false;
		boolean return_value = false;
		if (src != null && src.length() > 0) {
			Matcher m = numericPattern.matcher(src);
			if (m.find()) {
				return_value = true;
			}
		}
		return return_value;
	}
	
	/**
	 * 判断是否位数字,并可为空
	 * @param src
	 * @return boolean
	 */
	public static boolean isNumericAndCanNull(String src) {
		Pattern numericPattern = Pattern.compile("^[0-9]+$");
		if (src == null || src.equals(""))
			return true;
		boolean return_value = false;
		if (src != null && src.length() > 0) {
			Matcher m = numericPattern.matcher(src);
			if (m.find()) {
				return_value = true;
			}
		}
		return return_value;
	}
	
	/**
	 * 把json串中的uncode编码转换为字符串显示，转换失败返回原json串
	 * @param jsonStr
	 * @return String
	 */
	public static String getStrFromJson(String jsonStr) {
		try {
			JSONObject tempJSON = JSONObject.fromObject(jsonStr);
			return tempJSON.toString();
		} catch (Exception e) {
			return jsonStr;
		}

	}

	/**
	 * 把map表转换为json格式字符串，转换失败返回null
	 * @param map
	 * @return String
	 */
	public static String getJsonStrFromMap(Map<String, String> map) {
		JSONObject oneResult = new JSONObject();
		try {
			oneResult = JSONObject.fromObject(map);
		} catch (JSONException e) {
			log.error("从map表：" + map.toString() + "转换为json字符串出错");
			log.error(e.getMessage());
			return null;
		}
		return oneResult.toString();
	}

	/**
	 * 返回map中指定key的value，如果异常返回""
	 * @param map
	 * @param key
	 * @return String
	 */
	public static String getValueFromMapByKey(Map<String, ?> map, String key) {
		try {
			return map.get(key).toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回map中指定key列表的map
	 * @param map
	 * @param list
	 * @return Map<String, String>
	 */
	public static Map<String, String> getMapFromMapByKeyList(Map<String, String> map, List<String> list) {
		Map<String, String> mapTmp = new TreeMap<>();
		if (null == list || list.size() < 1) {
			log.error("指定的key列信息为空，请检查");
			return null;
		}
		for (int j = 0; j < list.size(); j++) {// 获取指定列信息
			String key = list.get(j);
			String value = getValueFromMapByKey(map, key);
			mapTmp.put(key, value);
		}
		return mapTmp;
	}
	
	/**
	 * 返回map表中key的列表
	 * @param expMap
	 * @return List<String>
	 */
	public static List<String> getKeyListFromMap(Map<String, String> expMap) {
		List<String> list = new ArrayList<>();
		try {
			for (Entry<String, String> entry : expMap.entrySet()) {
				list.add(entry.getKey().toString());
			}
		} catch (Exception e) {
			return null;
		}
		return list;
	}

	/**
	 * 返回字符串指定开始和结束位置之间的值
	 * @param str
	 * @param start
	 * @param end
	 * @return String
	 */
	public static String getValueFromStr(String str, String start, String end) {
		try {
			return str.substring(str.indexOf(start),
					str.indexOf(end) + end.length());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把字符串中的中文按指定编码urlEncode
	 * @param value
	 * @param encoding
	 * @return String
	 */
	public static String transForChinese(String value, String encoding) {
		if (value.matches("[0-9a-zA-Z.]*")) {
			return value;
		} else {
			return CommUtils.urlEncode(value, encoding);
		}
	}

	/**
	 * 把map表中字符串中的中文按指定编码urlEncode
	 * @param map
	 * @param encoding
	 * @return Map<String, String>
	 */
	public static Map<String, String> transForChinese(Map<String, String> map,String encoding) {
		Map<String, String> mapTmp = map;
		try {
			for (Entry<String, String> entry : mapTmp.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				value = transForChinese(value, encoding);
				mapTmp.put(key, value);
			}
			return mapTmp;
		} catch (Exception e) {
			log.error("转换map表中的中文失败");
			log.error(e.getMessage());
			return map;
		}

	}

	/**
	 * 把字符串按&和=标识转换为map表
	 * @param str
	 * @return Map<String, String>
	 */
	public static Map<String, String> getMapFromString(String str) {
		Map<String, String> map = new TreeMap<>();
		try {
			String[] res = str.split("&");
			for (int i = 0; i < res.length; i++) {
				String[] tmp = res[i].split("=");
				String key = tmp[0];
				String value = tmp[1];
				map.put(key, value);
			}
		} catch (Exception e) {
			log.error("字符串：" + str + "无法转换为map表");
			log.error(e.getMessage());
			map = null;
		}
		return map;
	}

	/**
	 * 把map表转换为字符串，使用= 和&连接
	 * @param map
	 * @return String
	 */
	public static String getStrFromMap(Map<String, String> map) {
		String str = "";
		try {
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				str += key + "=" + value + "&";
			}
			str = str.substring(0, str.length() - 1);
		} catch (Exception e) {
			log.error("从map中转换为字符串失败");
			log.error(e.getMessage());
		}

		return str;
	}

	/**
	 * 复制一份map表的副本
	 * @param map
	 * @return Map<String, String>
	 */
	public static Map<String, String> cloneMap(Map<String, String> map) {
		Map<String, String> clonemap = new TreeMap<>();
		if (null == map) {
			return null;
		}
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			clonemap.put(key, value);
		}
		return clonemap;
	}

	/**
	 * 把map转换为xml格式内容，其中中文不进行urlEncode转码
	 * @param map
	 * @return String
	 */
	public static String getXmlFromMap(Map<String, String> map) {
		String xmlbody = "";
		if (null == map || map.size() < 1) {
			return "";
		}
		for (Entry<String, String> entry : map.entrySet()) {
			xmlbody += "<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">";
		}
		return xmlbody;
	}

	/**
	 * 把map转换为xml格式内容，其中中文按指定编码 urlEncode转码
	 * @param map
	 * @param encoding
	 * @return String
	 */
	public static String getXmlFromMap(Map<String, String> map, String encoding) {
		String xmlbody = "";
		if (null == map || map.size() < 1) {
			return "";
		}
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			if (!value.matches("[0-9a-zA-Z.]*")) {
				// 中文按照enCoding字段指定编码进行urlEncode转码
				value = CommUtils.urlEncode(value, encoding);
			}
			xmlbody += "<" + key + ">" + value + "</" + key + ">";
		}
		return xmlbody;
	}
	
	/**
	 * 重新编码
	 * @param str
	 * @param newCharset
	 * @return String
	 */
	public static String changeCharset(String str, String newCharset)  {
		  if (!IsNullOrEmpty(str)) {
		        // 用默认字符编码解码字符串。
		        byte[] bs = str.getBytes();
		        // 用新的字符编码生成字符串
		        try {
					return new String(bs, newCharset);
				} catch (UnsupportedEncodingException e) {
					log.error(e.getMessage());
					return null;
				}
		     }
		    return null;
	}
	/**
	 * 重新编码
	 * @param str
	 * @param oldCharset
	 * @param newCharset
	 * @return String
	 */
	public static String changeCharset(String str, String oldCharset,String newCharset)  {
		  if (!IsNullOrEmpty(str)) {
		        try {
		        	 // 用默认字符编码解码字符串。
			        byte[] bs = str.getBytes(oldCharset);
			        // 用新的字符编码生成字符串
					return new String(bs, newCharset);
				} catch (UnsupportedEncodingException e) {
					return null;
				}
		     }
		  return null;
	}
	
	

	/**
	 * 判断字符串是否包含中文 如果包含则返回true
	 * @param strName
	 * @return boolean
	 */
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}
	
	// 根据Unicode编码完美的判断中文汉字和符号
	private static boolean isChinese(char c) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
				return true;
			}
			return false;
		}
	
	/**
	 * 将含有[]的字符串转为数组
	 * @param strName
	 * @return
	 */
	public static String[] stringToArray(String strName){
		String[] result;
		if(strName.startsWith("[")&&strName.endsWith("]")){
			strName = strName.substring(1,strName.length());
			strName= strName.substring(0,strName.length()-1);
			result = strName.split(",");
		}else{                             //如果字符串中不包含[],则把字符中直接作为长度为1的数组
			result = new String[1];
			result[0]=strName;
		}
		return result;
	}
	
	/**
	 * 替换${}格式的数据（参数依赖）
	 * @param str
	 * @param replacement
	 * @return
	 */
	public static String paramReplace(String str,String replacement){
		//预期结果中匹配${}
		StringBuffer sb = new StringBuffer();
		if(str!=null&&replacement!=null){  //str与replacement不能为空，为空则返回原始str
			Pattern pattern = Pattern.compile("\\$\\{(.*)\\}"); //匹配${}前后可以有字符
			Matcher matcher = pattern.matcher(str);			
			if(matcher.find()){    //如果未匹配到则返回原始str
				matcher.appendReplacement(sb, replacement);
				matcher.appendTail(sb);
				return sb.toString();
			}	
		}
		return str;   
	}
	
}
