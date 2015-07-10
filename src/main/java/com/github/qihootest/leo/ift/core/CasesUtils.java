package com.github.qihootest.leo.ift.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.ift.testcase.IftTestCase;
import com.github.qihootest.leo.ift.util.ExportReportExcel;
import com.github.qihootest.leo.toolkit.httpclient.HttpUtil;
import com.github.qihootest.leo.toolkit.httpclient.HttpsUtil;
import com.github.qihootest.leo.toolkit.httpclient.ResponseInfo;
import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.LogUtil;
import com.github.qihootest.leo.toolkit.util.StringUtil;

/**
 * 说明：用例执行的公共处理类，执行粒度是一个IftTestCase 即Excel用例文件中的一条用例，或Mysql数据库中的一条用例
 * 
 * @author lianghui (lianghui@360.cn) 2013-01-10
 */
public class CasesUtils {
	/**
	 * 日志记录
	 */
	protected static LogUtil log = LogUtil.getLogger(CasesUtils.class);
	/**
	 * get方法所需参数拼接后的字符串
	 */
	protected String getUrl;
	/**
	 * post方法所需参数拼接后的字符串
	 */
	protected String postUrl;
	/**
	 * 发起请求的地址
	 */
	protected String httpUrl;
	/**
	 * header的参数值对
	 */
	protected TreeMap<String, String> headersMap;
	/**
	 * httpurl的参数值对
	 */
	protected LinkedHashMap<String, String> urlParaMap;
	/**
	 * post提交时form的参数值对
	 */
	protected LinkedHashMap<String, String> formParaMap;
	/**
	 * http请求执行类
	 */
	protected HttpUtil httpUtil;
	
	/**
	 * 无参构造函数 说明：创建httpclient连接初始化
	 */
	public CasesUtils() {
		this.getUrl = "";
		this.postUrl = "";
		this.httpUrl = "";
		this.headersMap = new TreeMap<String, String>();
		this.urlParaMap = new LinkedHashMap<String, String>();
		this.formParaMap = new LinkedHashMap<String, String>();
		if (IftConf.ProxyEnable.equals("Y")) {
			log.info("已设置使用代理："+IftConf.ProxyIp+":"+IftConf.PROXY_PORT);
			httpUtil = new HttpUtil(IftConf.ProxyIp,IftConf.PROXY_PORT);
		} else {
			log.info("未设置代理");
			httpUtil = new HttpUtil();
		}
	}

	/**
	 * 发起请求，目前只支持get和post两个方法，待扩展
	 * 
	 * @param testCase
	 * @return ResponseInfo
	 */
	public ResponseInfo execResquest(IftTestCase testCase) {
		ResponseInfo resInfo=new ResponseInfo();
		// 设置发起请求时使用的编码
		log.info("发起请求使用的编码为："+testCase.getEnCoding());
		this.httpUtil.setCharset(testCase.getEnCoding());
		// 获取发起请求的http地址
		if (!updateHttpUrl(testCase)) {
			log.error("发起http请求时，获取http地址失败");
			resInfo.setErrMsgInfo("发起http请求时，获取http地址失败");
			return resInfo;
		}
		// 获取发起请求的url参数信息
		if (!updateUrlPara(testCase)) {
			log.error("发起http请求时，获取url参数信息失败");
			resInfo.setErrMsgInfo("发起http请求时，获取url参数信息失败");
			return resInfo;
		}
		// 获取发起请求的post参数信息
		if (!updateFormParaMap(testCase)) {
			log.error("发起http请求时，获取post参数信息失败");
			resInfo.setErrMsgInfo("发起http请求时，获取post参数信息失败");
			return resInfo;
		}
		// 获取发起请求的headers信息
		if (!updateHeadersMap(testCase)) {
			log.error("发起http请求时，获取headers信息失败");
			resInfo.setErrMsgInfo("发起http请求时，获取headers信息失败");
			return resInfo;
		}
		// 调用https对象进行重新赋值
		if(getProtocol().equals("https")){
			//使用本地ssl认证信息
			if(IftConf.SSL.equals("Y")){
				httpUtil = new HttpsUtil(IftConf.KeyPath,IftConf.KeyPassword);
			}else{
				//不需要认证信息
				httpUtil = new HttpsUtil();
			}
		}
		try {
			// 发起请求
			if (testCase.getHttpMethod().equalsIgnoreCase("get")) {
				resInfo= httpUtil.get(headersMap, httpUrl + getUrl);
				resInfo.setHttpUrl(httpUrl + getUrl);
			} else if (testCase.getHttpMethod().equalsIgnoreCase("post")) {
				resInfo= httpUtil.post(headersMap, httpUrl+getUrl, postUrl);
				resInfo.setHttpUrl("post请求的url信息：" + httpUrl + postUrl);
			} else {// 待扩展
				
			}
		} catch (Exception e) {
			log.error("发送http请求异常");
			log.error("httpurl:" + httpUrl);
			log.error("getUrl:" + getUrl);
			log.error("postUrl:" + postUrl);
			log.error(e.getMessage());
			resInfo.setErrMsgInfo("发送http请求异常,请查看执行日志记录");
			return resInfo;
		}

		// 存储执行结果和拼接的url
		return resInfo;
	}

	
	/**
	 * 返回结果比对后的信息
	 * @param resInfo http请求后返回的信息
	 * @param expRes 从用例中读取的预期结果
	 * @param actRes 格式化后的实际结果 目前只支持json和xml格式
	 * @return IFtResultInfo
	 */
	public IFtResultInfo getIFtResultInfo(ResponseInfo resInfo,String expRes,String actRes) {
		return getIFtResultInfo(resInfo, expRes,actRes, 1);
	}
	/**
	 * 返回结果比对后的信息
	 * @param resInfo http请求后返回的信息
	 * @param expRes 从用例中读取的预期结果
	 * @param actRes 格式化后的实际结果 目前只支持json和xml格式
	 * @param parseJson json的解析方式 默认为单层解析
	 * @return IFtResultInfo
	 */
	public IFtResultInfo getIFtResultInfo(ResponseInfo resInfo,String expRes,String actRes,int parseJson) {
		IFtResultInfo iFtResultInfo =  new IFtResultInfo();
		//结果比对处理类 目前只能处理json和xml格式
		CompareResult comresult=new CompareResult();
		boolean compareRes=false;
		
		//http请求执行成功后，才进行比对
		if (resInfo.getErrMsgInfo() == null) {
			//预期结果中匹配${}
			Pattern pattern = Pattern.compile("^\\$\\{(.*)\\}$");
			Matcher matcher = pattern.matcher(expRes);
			if(matcher.matches()){   //是否匹配到${}
				String para = matcher.group(1);
				if(para.equals("cookie")){ //判断预期结果中是否存在cookie依赖参数
					if(resInfo.getCookies()!=null&(resInfo.getCookies().length()>0)){
						IftConf.DependPara.put(matcher.group(1), resInfo.getCookies());  //添加依赖参数(赋值)
						actRes = matcher.group(1)+"="+resInfo.getCookies();
						compareRes = true;
					}else{
						compareRes=false;
						actRes=resInfo.getCookies();
						expRes=comresult.getClearExpres();	
					}
				}else{
					//结果比对
					compareRes=comresult.getCompareResult(expRes,actRes,parseJson);//解析json串
					actRes=comresult.getClearActres();
					expRes=comresult.getClearExpres();
				}				
			}else{
				//结果比对
				compareRes=comresult.getCompareResult(expRes,actRes,parseJson);//解析json串
				actRes=comresult.getClearActres();
				expRes=comresult.getClearExpres();
			}

		}else{
			resInfo.setResBodyInfo(resInfo.getErrMsgInfo());
			actRes="";
		}

		//处理返回结果
		iFtResultInfo.setResponseInfo(resInfo);
		iFtResultInfo.setCompareRes(compareRes);
		iFtResultInfo.setActRes(actRes);
		iFtResultInfo.setExpRes(expRes);
		return iFtResultInfo;
	}
	/**
	 * 关闭httpclient连接
	 */
	public void closeConn() {
		this.httpUtil.close();
	}

	/**
	 * 更新httpurl
	 * @param testCase
	 * @return boolean 更新成功返回true
	 */
	protected boolean updateHttpUrl(IftTestCase testCase) {
		String httpUrlTmp = testCase.getUrl();
		if (null == httpUrlTmp || httpUrlTmp.length() < 1) {
			log.error("用例请求的http地址为空，请检查");
			return false;
		}
		String 	secondurl = testCase.getCaseMap().get("secondurl");
		if (null != secondurl && !secondurl.equals("")) {
			if (secondurl.equals("rand"))secondurl = CommUtils.getRandomStr(5);
		httpUrlTmp += secondurl;
		}
		this.httpUrl = httpUrlTmp;
		return true;
	}

	/**
	 * 拼接get方法所需的字符串 中文按照enCoding字段指定编码进行urlEncode转码
	 * @param testCase
	 * @return boolean 拼接成功返回true
	 */
	protected boolean updateUrlPara(IftTestCase testCase) {
		this.urlParaMap = new LinkedHashMap<String, String>();
		String urlTmp = "";
		if (null == testCase.getParalist()) {
			log.error("传入的httpurl的参数名列表为null，请检查");
			return false;
		}
		if (testCase.getParalist().size() < 1) {
			urlTmp = "";// 参与get参数拼接的个数为0
		} else {
			for (int i = 0; i < testCase.getParalist().size(); i++) {
				String value = testCase.getCaseMap().get(testCase.getParalist().get(i));
				if (value.toLowerCase().equals("null")) {
					// 参数值为null时，不参与拼接
				} else if (value.matches("[0-9a-zA-Z.]*")) {
					this.urlParaMap.put(testCase.getParalist().get(i), value);
					urlTmp += testCase.getParalist().get(i) + "=" + value + "&";
				} else {
					// 中文按照enCoding字段指定编码进行urlEncode转码
					value = CommUtils.urlEncode(value, testCase.getEnCoding());
					this.urlParaMap.put(testCase.getParalist().get(i), value);
					urlTmp += testCase.getParalist().get(i) + "=" + value + "&";
				}
			}
			// 去掉最后一个&符号
			if(!StringUtil.IsNullOrEmpty(urlTmp)){
				urlTmp = urlTmp.substring(0, urlTmp.length() - 1);
			}
			this.getUrl = urlTmp;
		}
		return true;
	}

	/**
	 * 拼接post方法所需的字符串 中文按照enCoding字段指定编码进行urlEncode转码
	 * @param testcase
	 * @return boolean 拼接成功返回true
	 */
	protected boolean updateFormParaMap(IftTestCase testcase) {
		this.formParaMap = new LinkedHashMap<String, String>();
		String postTmp = "";
		if (null == testcase.getFormlist()) {
			log.error("传入的form参数名列表为null，请检查");
			return false;
		}
		if (testcase.getFormlist().size() < 1) {
			postTmp = "";// 参与post参数拼接的个数为0
		} else {
			for (int i = 0; i < testcase.getFormlist().size(); i++) {
				String value = testcase.getCaseMap().get(testcase.getFormlist().get(i));
				if (value.toLowerCase().equals("null")) {
					// 参数值为null时，不参与拼接
				} else if (value.matches("[0-9a-zA-Z.]*")) {
					this.formParaMap.put(testcase.getFormlist().get(i), value);
					postTmp += testcase.getFormlist().get(i) + "=" + value + "&";
				} else {
					// 中文按照enCoding字段指定编码进行urlEncode转码
					value = CommUtils.urlEncode(value, testcase.getEnCoding());
					this.formParaMap.put(testcase.getFormlist().get(i), value);
					postTmp += testcase.getFormlist().get(i) + "=" + value + "&";
				}
			}
			// 去掉最后一个&符号
			if(!StringUtil.IsNullOrEmpty(postTmp)){
				postTmp = postTmp.substring(0, postTmp.length() - 1);
			}
			this.postUrl = postTmp;
		}
		return true;
	}

	/**
	 * 往headers中添加参数信息
	 * 
	 * @param testcase
	 * @return boolean 添加成功返回true
	 */
	protected boolean updateHeadersMap(IftTestCase testcase) {
		if (null != testcase.getHeaderMap()) {
			this.headersMap = testcase.getHeaderMap();
		} else {
			this.headersMap = new TreeMap<String, String>();
		}
		if (null == testcase.getHeaderlist()) {
			log.error("传入的headers参数名列表为null，请检查");
			return false;
		}
		for (int i = 0; i < testcase.getHeaderlist().size(); i++) {
			String value = testcase.getCaseMap().get(
					testcase.getHeaderlist().get(i));
			if (value.toLowerCase().equals("null")) {
				// 参数值为null时，不参与拼接
			} else if (value.matches("[0-9a-zA-Z.]*")) {
				this.headersMap.put(testcase.getHeaderlist().get(i), value);
			} else {
				// 中文按照enCoding字段指定编码进行urlEncode转码
				value = CommUtils.urlEncode(value, testcase.getEnCoding());
				this.headersMap.put(testcase.getHeaderlist().get(i), value);
			}
		}
		// cookie信息添加到headers中
		if (null != testcase.getCookie() && testcase.getCookie().length() > 0) {
			headersMap.put("Cookie", testcase.getCookie());
		}
		return true;
	}

	// 用例处理相关的公共方法
	/**
	 * 获取参与签名计算的键值对map表
	 * @param testcase
	 * @return TreeMap<String, String> 参与签名计算的键值对map表
	 */
	public TreeMap<String, String> getSignMap(IftTestCase testcase) {
		TreeMap<String, String> signMap = new TreeMap<String, String>();
		if (null == testcase.getSignlist()) {
			log.error("传入的签名参数名列表为null，请检查");
			return signMap;
		}
		for (int i = 0; i < testcase.getSignlist().size(); i++) {
			String value = testcase.getCaseMap().get(
					testcase.getSignlist().get(i));
			if (value.toLowerCase().equals("null")) {
				// 参数值为null时，不参与签名拼接
			} else {
				signMap.put(testcase.getSignlist().get(i), value);
			}
		}
		return signMap;
	}

	/**
	 * 更新参与签名计算、url参数、form参数、header参数的List到用例中
	 * @param testcase
	 * @param urlParaCheck
	 * @param formParaCheck
	 * @param headerParaCheck
	 * @return  IftTestCase
	 */
	public IftTestCase updateAllToListForCase(IftTestCase testcase,String[] urlParaCheck, 
			String[] formParaCheck,String[] headerParaCheck) {
		ArrayList<String> signList = new ArrayList<String>();// 参与签名计算的参数名列表
		ArrayList<String> urlParaList = new ArrayList<String>();// 参与url拼接的参数名列表
		ArrayList<String> formParaList = new ArrayList<String>();// 参与form构造的参数名列表
		ArrayList<String> headersParaList = new ArrayList<String>();// 参与header的参数名列表
		Iterator<Entry<String, String>> it = testcase.getCaseMap().entrySet().iterator();
		int i = 0;
		String argKey[] = new String[testcase.getCaseMap().size()];
		String argValue[] = new String[testcase.getCaseMap().size()];
		while (it.hasNext()) {
			Map.Entry<String, String> entity = (Entry<String, String>) it.next();
			argKey[i] = entity.getKey().toString();
			argValue[i] = entity.getValue().toString();
			i++;
		}
		// 默认为需要计算签名
		testcase.setSignFlag(true);
		try {
			// 从第四个参数 method开始遍历之后所有键值对
			for (int j = IftConf.paramStartCol; j < argValue.length; j++) {
				// 整理签名所需的字段
				if (testcase.getArgCount() == 0) {
					// 无需计算签名
					testcase.setSignFlag(false);
				} else {
					if (j < IftConf.paramStartCol + testcase.getArgCount()) {
						signList.add(argKey[j]);
					}
					if (j == IftConf.paramStartCol + testcase.getArgCount()) {
						testcase.setSignKey(argKey[j]);
					}
				}
				// 检查url、form、header内包含的参数列表
				for (int n = 0; n < urlParaCheck.length; n++) {
					if (argKey[j].toLowerCase().equals(urlParaCheck[n].toLowerCase())) {
						urlParaList.add(argKey[j]);
					}
				}
				for (int n = 0; n < formParaCheck.length; n++) {
					if (argKey[j].toLowerCase().equals(formParaCheck[n].toLowerCase())) {
						formParaList.add(argKey[j]);
					}
				}
				for (int n = 0; n < headerParaCheck.length; n++) {
					if (argKey[j].toLowerCase().equals(headerParaCheck[n].toLowerCase())) {
						headersParaList.add(argKey[j]);
					}
				}

			}// 用例中的所有键值对遍历完毕
			testcase.setParalist(urlParaList);
			testcase.setFormlist(formParaList);
			testcase.setHeaderlist(headersParaList);
			testcase.setSignlist(signList);
		} catch (Exception e) {
			log.error("传入的用例实体类信息有误，无法完成读取，请检查");
			log.error(e.getMessage());
		}
		return testcase;
	}

	/**
	 * 更新计算后的签名值到用例中 
	 * @param testCase
	 * @param signValue
	 * @return IftTestCase
	 */
	public IftTestCase updateSignValueForCase(IftTestCase testCase,String signValue) {
		LinkedHashMap<String, String> caseMap = testCase.getCaseMap();
		if (!testCase.isSignFlag()) {
			// 无需计算签名
			log.info("无需计算签名");
			return testCase;
		}
		String value = caseMap.get(testCase.getSignKey());
		if (value.length() < 1) {
			// 签名项原参数值为空时，表示要发送的签名值为空，
			value = "";
		} else if (value.equalsIgnoreCase("null")) {
			// 签名项原参数值为null时，不改变签名值
		} else {
			// 签名项原参数值不为空，且不等于null时，更新为计算后的签名值
			value = signValue;
		}
		caseMap.put(testCase.getSignKey(), value);
		testCase.setCaseMap(caseMap);
		return testCase;
	}

	/**
	 * 针对参数值中的特殊标识做处理 当前只针对标识为rand、timetamp、date时进行处理
	 * @param testCase
	 * @param randNum
	 * @return IftTestCase
	 */
	public IftTestCase updateAllParaForCase(IftTestCase testCase,int randNum) {
		LinkedHashMap<String, String> caseMap = testCase.getCaseMap();
		if (null == caseMap || caseMap.size() < 1) {
			log.error("用例的参数值对为空，请检查");
			return testCase;
		}
		//预期结果中匹配${}
		Pattern pattern = Pattern.compile("\\$\\{(.*)\\}"); //匹配${}前后可以有字符
		Matcher matcher ;
		// 遍历所有键值对，针对特殊标识做处理
		Iterator<Entry<String, String>> it = caseMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entity = (Entry<String, String>) it.next();
			String key = entity.getKey().toString();
			String value = entity.getValue().toString();
			matcher = pattern.matcher(value);
			//对依赖参数进行替换			
			if(matcher.find()&(!key.equals("Expres"))&null!=value){ //匹配到${},且key不能为Expres
				StringBuffer sb = new StringBuffer();
				if((IftConf.DependPara).containsKey(matcher.group(1))){    //如果依赖参数map包含要替换的key
					value = (String) (IftConf.DependPara).get(matcher.group(1));  //重新赋值value
					matcher.appendReplacement(sb, value);
					matcher.appendTail(sb);
					caseMap.put(key, sb.toString());
				}
				
			}
			// 针对参数值特殊标识rand的处理，随机生成长度为10个字符串
			if (value.toLowerCase().equalsIgnoreCase("rand")) {
				value = CommUtils.getRandomStr(randNum);
				caseMap.put(key, value);
			}
			// 针对参数值特殊标识timestamp的处理，获取Unix格式时间戳
			if (value.toLowerCase().equalsIgnoreCase("timestamp")) {
				value = CommUtils.getTimestamp();
				caseMap.put(key, value);
			}
			// 参数值标识为date时，日期字符串格式20120626092109 年月日时分秒
			if (value.toLowerCase().equalsIgnoreCase("date")) {
				value = CommUtils.getStrRandNum(0);
				caseMap.put(key, value);
			}
			// 其它处理
		}
		testCase.setCaseMap(caseMap);
		return testCase;
	}

	/**
	 * 针对cookie信息的处理 中文按照用例中指定编码进行urlEncode转码
	 * 
	 * @param testCase
	 * @return IftTestCase
	 */
	protected static IftTestCase updateCookieForCase(IftTestCase testCase) {
		String cookies = "";
		if (null == testCase.getCookie() || testCase.getCookie().length() < 1) {
			cookies = "";
		}else{
			cookies = testCase.getCookie();
		}
		if(cookies.contains(";")){
			String cookiesTmp = "";
			TreeMap<String,String> cookieMap = new TreeMap<String,String>();
			cookieMap = CommUtils.parseQuery(cookies, ';', '=');
			Iterator<Entry<String,String>> ite = cookieMap.entrySet().iterator();
			while(ite.hasNext()){
				Entry<String,String> entry = (Entry<String,String>) ite.next();
				cookiesTmp += entry.getKey()+"="+CommUtils.urlEncode(entry.getValue().toString(),testCase.getEnCoding()) + ";";
			}
			cookies = cookiesTmp;
		}
		testCase.setCookie(cookies);
		return testCase;

	}
	
	//直接执行已处理好的post
	/**
	 * 发起Post
	 * @param header 
	 * @param http url地址
	 * @param posturl 参数键值对 格式key=value&key=value
	 * @return ResponseInfo
	 */
	public ResponseInfo ExecPostResquest(TreeMap<String, String> header,String http, String posturl) {
		ResponseInfo resInfo=new ResponseInfo();
		// 设置发起请求时使用的编码
		this.httpUtil.setCharset("UTF-8");
		try {
			// 发起请求
			resInfo= httpUtil.post(header, http, posturl);
			resInfo.setHttpUrl("post请求的url信息：" + http + "  post请求的body信息为-" + posturl);
		} catch (Exception e) {
			log.error("发送http请求失败，请检查");
			log.error(e.getMessage());
			resInfo.setErrMsgInfo("发送http请求失败，请检查");
			return resInfo;
		}
		return resInfo;
	}

	/**
	 * 发起Get请求
	 * @param header
	 * @param gethttpurl get请求的url 包括参数键值对 ...?key=value&key=value
	 * @return ResponseInfo
	 */
	public ResponseInfo ExecGetResquest(TreeMap<String, String> header,String gethttpurl) {
		ResponseInfo resInfo=new ResponseInfo();
		// 设置发起请求时使用的编码
		this.httpUtil.setCharset("UTF-8");
		try {
			// 发起请求
			resInfo= httpUtil.get(header, gethttpurl);
			resInfo.setHttpUrl(gethttpurl);
		} catch (Exception e) {
			log.error("发送http请求" + gethttpurl + "失败，请检查");
			log.error(e.getMessage());
			resInfo.setErrMsgInfo("发送http请求" + gethttpurl + "失败，请检查");
			return resInfo;
		}
		return resInfo;
	}
	
	/**
	 * 获取请求的协议：默认为http
	 * @return protocol
	 */
    private String getProtocol() {
      Pattern p = Pattern. compile("(.*?)://");
      Matcher m = p.matcher(httpUrl);
      String protocol = "http";
      while (m.find()) {  
    	  protocol = m.group(1);
      }
      return protocol;    
  }
    
	/**
	 * 通过java反射机制获取，静态属性
	 * @param conf DemoConf对象
	 * @return paraValue
	 */
    private static HashMap getConfParaValue(Object conf){
    	HashMap paraValue = new HashMap();
    	 Field[] field = conf.getClass().getDeclaredFields(); 
    	 for(int i =0; i<field.length;i++){
    		 String para = field[i].getName();    //获取属性的名字
    		 String type = field[i].getGenericType().toString(); //获取属性类型
    		 if(type.equals("class java.lang.String")){ //如果type是类类型，则前面包含"class "，后面跟类名
    			 try {
    				 String value = (String) field[i].get(conf); //直接获取静态属性值
					paraValue.put(para, value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e);
					e.printStackTrace();
				}
    		 }
    	 }
		return paraValue;
    }
    
    /**
     * 更新配置文件中的全局Header和Host
     * @param testCase
     * @param conf
     * @param HeardPara
     * @return testcase
     */
    public IftTestCase updateAllToConfForCase(IftTestCase testCase, 
    		Object conf, String[] HeardPara) {
		LinkedHashMap<String, String> caseMap = testCase.getCaseMap(); //获取参数Map
    	HashMap paraValue = new HashMap();
    	paraValue = getConfParaValue(conf);
    	for(int i=0;i<HeardPara.length;i++){ //更新配置中的Heard参数
    		if(paraValue.containsKey(HeardPara[i])&(!caseMap.containsKey(HeardPara[i]))){
    			caseMap.put(HeardPara[i], (String) paraValue.get(HeardPara[i]));
    		}
    	}
    	testCase.setCaseMap(caseMap); //将更新后的 参数赋值给testCase
    	updateUrlHost(testCase,paraValue); //对url中的特殊标识（host）做处理
    	return testCase;
    }
    
    /**
     * 对url中特殊标识（host）做处理
     * @param testCase
     * @param paraValue
     */
    private static void updateUrlHost(IftTestCase testCase,HashMap paraValue){
    	if(testCase.getUrl().equalsIgnoreCase("host")){
    		System.out.println("==="+paraValue.get("Host"));
    		if((null != paraValue.get("Host"))& (paraValue.get("Host").toString().length()>0)){
    			testCase.setUrl((String) paraValue.get("Host"));
    		}else{
    			log.error("DemoConf中的Host值为空，请检查！");
    		}
    	}
    }

}
