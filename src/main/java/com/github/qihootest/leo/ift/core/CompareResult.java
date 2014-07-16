package com.github.qihootest.leo.ift.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.JsonUtil;
import com.github.qihootest.leo.toolkit.util.StringUtil;
import com.github.qihootest.leo.toolkit.util.XmlUtil;


/**
 * 说明：比对期望结果与实际结果
 * 
 * @author lianghui (lianghui@360.cn)
 * 
 */
public class CompareResult {

	private String clearActres;
	private String clearExpres;
	
	/**
	 * 默认构造函数
	 */
	public CompareResult() {
		clearActres = "";
		clearExpres = "";
	}
	
	/**
	 * 说明：期望结果与实际结果的比对  
	 * @param expRes 预期结果
	 * @param actRes 从请求响应中提取过滤后的实际结果
	 * @param config 可选参数默认为1，只解析一层，2时全解析
	 * @return boolean 相同时返回true，不同时返回false
	 */
	public boolean getCompareResult(String expRes, String actRes,int config){
		//开始比对之前，清空已整理后的预期与实际结果字符串
		setClearActres("");
		setClearExpres("");
		
		Map<String, String> exp = new TreeMap<String, String>();
		Map<String, String> act = new TreeMap<String, String>();
		if (StringUtil.IsNullOrEmpty(expRes)) {
			setClearExpres("预期结果为null或空字符串，不进行比对");
			setClearActres("未设置预期值&实际结果为："+actRes);
			return true;//预期结果为空或null时，不再进行比对处理，直接返回true
		}
		exp = trimExpres(expRes);
		if (StringUtil.IsNullOrEmpty(actRes)) {
			setClearActres("实际结果为null或空字符串，未找到");
			return false;//实际结果为空或null时，不再进行比对处理，直接返回false
		}
		act = trimActres(actRes,config);
		return compareMap(exp, act);
	}

	/**
	 * 说明：期望结果与实际结果的比对 json解析方式 使用默认
	 * @param expRes 预期结果
	 * @param actRes 从请求响应中提取过滤后的实际结果
	 * @return boolean 相同时返回true，不同时返回false
	 */
	public boolean getCompareResult(String expRes, String actRes) {
		return getCompareResult(expRes,actRes,IftConf.parseJson);
	}

	/**
	 * 说明：比较两个map
	 * @param expMap
	 * @param actMap
	 * @return boolean 如果map1∩map2等于map1或者map2，则返回true，否则返回false
	 */
	private boolean compareMap(Map<String, String> expMap,Map<String, String> actMap) {
		List<Integer> listFlag = new LinkedList<Integer>();
		String record = "";// 记录在实际结果中查找到的结果，记录格式为key=value&key=value.....

		// 遍历预期结果map表
		for (Entry<String, String> entryExp : expMap.entrySet()) {
			String expKey = entryExp.getKey();
			String expValue = entryExp.getValue();
			
			boolean flag=false;//在实际结果中是否找到对应的key-value
			
			//遍历实际结果map表
			for (Entry<String, String> entryAct : actMap.entrySet()) {
				String actKey = entryAct.getKey();
				String actValue = entryAct.getValue();
				if (actKey.equals(expKey)) {//在实际结果中找到对应的key-value
					// 记录每个键值的比对结果
					if (CompareStr(expValue, actValue)) {
						listFlag.add(1);
					} else {
						listFlag.add(0);
					}
					// 记录在实际结果中找到的记录
					record += actKey + "=" + actValue + "&";
					flag=true;
					break;//比对完毕，结束实际结果map表遍历
				}				
			}//实际结果map表遍历结束
			
			//在实际结果map表中未找到对应的key-value时的处理
			if (false==flag) {
				record +=expKey+"的值未找到";
				listFlag.add(0);
			}
			
		}// 预期结果map表遍历结束

		// 更新整理后实际结果的值
		if (record.length() > 2) {
			if (record.substring(record.length() - 1, record.length()).equals("&")) {
				setClearActres(record.substring(0, record.length() - 1));
			}else{
				setClearActres(record+"未找到");
			}
		} 
		
		if (record.indexOf("未找到")>-1)  {
			setClearActres(record+"&实际结果为："+StringUtil.getStrFromMap(actMap));
		}

		// 汇总比对结果
		int sum = 1;
		for (int i = 0; i < listFlag.size(); i++) {
			sum *= listFlag.get(i);
		}
		// 返回比对结果
		if (sum == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 单个预期结果值与对应实际结果值的比对，支持预期结果值以#标识分割，多个预期结果的处理
	 * @param expValue
	 * @param actValue
	 * @return boolean
	 * @author lianghui
	 */
	private boolean CompareStr(String expValue, String actValue) {
		//预期结果与实际结果任一为null，返回false
		if (null==actValue || null==expValue) return false;
		
		//对实际结果中一个关键字对应多个值，进行处理
		if(expValue.contains("^")){
			boolean flag = true;
			String[] allExpValue = expValue.split("\\^");
			String[] allActValue = actValue.split("\\^");
			for(int i = 0;i < allExpValue.length; i++){
				if(allExpValue[i].contains("int")){
					if(!allActValue[i].matches("[0-9]+")){
						flag = false;
						break;
					}
					}else if (!allExpValue[i].equals(allActValue[i])){
					flag = false;
					break;
				}
			}
			return flag;
		}

		//判断是否有多个预期结果值
		if (expValue.contains("#")) {
			boolean flag2=false;
			String[] allExpValue2 = expValue.split("#");
			for (int i = 0; i < allExpValue2.length; i++) {
					if (!actValue.equals(allExpValue2[i])) {
						flag2 = false;
					} else {
						flag2 = true;
						break;
					}
			}
			return flag2;//返回结果
		}
		
		//仅1个预期结果值,并对int关键词做处理
		if(expValue.contains("int")){ //预期结果中有int值
			if(actValue.matches("[0-9]+") & !actValue.equals("0")){ //匹配int类型实际结果，但实际结果不能为0
				return true;
			}else{
				return false;
			}
		}else if(!actValue.equalsIgnoreCase(expValue)){ //预期结果中没有int值
			return  false;
		} else {
			return  true;
		}
	}

	/**
	 * 说明：对预期结果的字符串进行清理，
	 * @param expres预期结果字符串
	 * @return Map<String, String> 返回整理后的预期结果
	 */
	private Map<String, String> trimExpres(String expres) {
		Map<String, String> trimExpres = new TreeMap<String, String>();
		trimExpres = CommUtils.parseQuery(expres, '&', '=');
		if (null == trimExpres) {
			trimExpres = new TreeMap<String, String>();
			setClearExpres("预期结果未找到,请检查："+expres);
			return trimExpres;
		}
		String temp = "";
		int i = 0;
		for (Entry<String, String> entry : trimExpres.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			if (i == 0) {
				temp += key + "=" + value;
			} else {
				temp += "&" + key + "=" + value;
			}
			i++;
		}
		setClearExpres(temp);
		return trimExpres;
	}

	/**
	 * 说明：对请求返回的实际结果字符串进行清理，
	 * @param responseRes 实际结果字符串
	 * @return Map<String, String> ，返回整理后的实际结果
	 */
	public Map<String, String> trimActres(String responseRes,int config) {
		Map<String, String> trimactres = new TreeMap<String, String>();
		Map<String, Object> map = new TreeMap<String,Object>();
		if (XmlUtil.isXmlText(responseRes)) {
			trimactres = XmlUtil.fomatXMLToMap(responseRes);
			if (trimactres.size()<1) {
				trimactres.put("解析xml格式错误", "---"+responseRes);
			}
		} else{
			if(config == 1){//单层方式解析json串
				map = JsonUtil.getResult(responseRes);
			}else if(config == 2){//多层方式解析json串
				map = JsonUtil.getAllResult(responseRes);
			}else{//config不为1、2时  按单层方式解析
				map = JsonUtil.getResult(responseRes);
			}
			if(map == null){
				trimactres.put("解析json格式错误", "---"+responseRes);
				return trimactres;
			}
			for (Iterator<Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext();) {
				@SuppressWarnings("rawtypes")
				Map.Entry entity = it.next();
				trimactres.put(entity.getKey().toString(), entity.getValue().toString());
			}
		}
		return trimactres;
	}


	public String getClearActres() {
		return clearActres;
	}

	public void setClearActres(String clearActres) {
		this.clearActres = clearActres;
	}

	public String getClearExpres() {
		return clearExpres;
	}

	public void setClearExpres(String clearExpres) {
		this.clearExpres = clearExpres;
	}

}
