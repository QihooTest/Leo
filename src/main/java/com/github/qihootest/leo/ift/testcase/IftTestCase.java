package com.github.qihootest.leo.ift.testcase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 说明：测试用例实体类
 * @author lianghui (lianghui@360.cn)
 *
 */
public class IftTestCase {
	private String url;//发起请求的url地址
	private String httpMethod;//请求的方法
	private int argCount;	//参与签名计算的参数个数
	private String enCoding;//编码
	private String casesetName;//测试集名称
	private String templatePath;//使用的模板存储位置
	private String cookie;//此用例的cookie信息
	private String secretKey;//参与签名计算的密钥
	private String signKey;//标识签名值参数的key值
	private boolean signFlag;//标识是否计算签名,true--需要签名，false不需要签名
	private LinkedHashMap<String,String> caseMap;//所有键值对信息
	private List<String> paralist;//get请求的参数项列表
	private List<String> formlist;//post请求的参数项列表
	private List<String> signlist;//参与签名计算的参数项列表
	private List<String> headerlist;//header的参数项列表
	private TreeMap<String, String> headerMap;//发起请求时使用的header信息
	private Map<String, String> results;//依据此用例发起请求后返回的结果串
	
	private String caseId;//用例的ID标识
	private String testPoint;//测试点
	
	/**
	 * 默认构造函数
	 */
	public IftTestCase() {
		this.url = "";
		this.httpMethod = "";
		this.argCount = 0;
		this.enCoding = "";
		this.casesetName = "";
		this.templatePath = "";
		this.cookie = "";
		this.secretKey = "";
		this.signFlag = false;
		this.caseMap = new LinkedHashMap<String,String>();
		this.paralist = new ArrayList<String>();
		this.formlist = new ArrayList<String>();
		this.signlist = new ArrayList<String>();
		this.headerlist = new ArrayList<String>();
		this.headerMap = new TreeMap<String,String>();
		this.results = new TreeMap<String,String>();
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	/**
	 * @return the argCount
	 */
	public int getArgCount() {
		return argCount;
	}
	/**
	 * @param argCount the argCount to set
	 */
	public void setArgCount(int argCount) {
		this.argCount = argCount;
	}
	/**
	 * @return the enCoding
	 */
	public String getEnCoding() {
		return enCoding;
	}
	/**
	 * @param enCoding the enCoding to set
	 */
	public void setEnCoding(String enCoding) {
		this.enCoding = enCoding;
	}
	/**
	 * @return the casesetName
	 */
	public String getCasesetName() {
		return casesetName;
	}
	/**
	 * @param casesetName the casesetName to set
	 */
	public void setCasesetName(String casesetName) {
		this.casesetName = casesetName;
	}
	/**
	 * @return the templatePath
	 */
	public String getTemplatePath() {
		return templatePath;
	}
	/**
	 * @param templatePath the templatePath to set
	 */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return cookie;
	}
	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}
	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	/**
	 * @return the signKey
	 */
	public String getSignKey() {
		return signKey;
	}
	/**
	 * @param signKey the signKey to set
	 */
	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}
	/**
	 * @return the signFlag
	 */
	public boolean isSignFlag() {
		return signFlag;
	}
	/**
	 * @param signFlag the signFlag to set
	 */
	public void setSignFlag(boolean signFlag) {
		this.signFlag = signFlag;
	}
	
	/**
	 * @return the caseMap
	 */
	public LinkedHashMap<String, String> getCaseMap() {
		return caseMap;
	}
	/**
	 * @param caseMap the caseMap to set
	 */
	public void setCaseMap(LinkedHashMap<String, String> caseMap) {
		this.caseMap = caseMap;
	}
	/**
	 * @return the paralist
	 */
	public List<String> getParalist() {
		return paralist;
	}
	/**
	 * @param paralist the paralist to set
	 */
	public void setParalist(List<String> paralist) {
		this.paralist = paralist;
	}
	/**
	 * @return the formlist
	 */
	public List<String> getFormlist() {
		return formlist;
	}
	/**
	 * @param formlist the formlist to set
	 */
	public void setFormlist(List<String> formlist) {
		this.formlist = formlist;
	}
	/**
	 * @return the signlist
	 */
	public List<String> getSignlist() {
		return signlist;
	}
	/**
	 * @param signlist the signlist to set
	 */
	public void setSignlist(List<String> signlist) {
		this.signlist = signlist;
	}
	/**
	 * @return the headerlist
	 */
	public List<String> getHeaderlist() {
		return headerlist;
	}
	/**
	 * @param headerlist the headerlist to set
	 */
	public void setHeaderlist(List<String> headerlist) {
		this.headerlist = headerlist;
	}
	/**
	 * @return the headerMap
	 */
	public TreeMap<String, String> getHeaderMap() {
		return headerMap;
	}
	/**
	 * @param headerMap the headerMap to set
	 */
	public void setHeaderMap(TreeMap<String, String> headerMap) {
		this.headerMap = headerMap;
	}
	/**
	 * @return the results
	 */
	public Map<String, String> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(Map<String, String> results) {
		this.results = results;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getTestPoint() {
		return testPoint;
	}
	public void setTestPoint(String testPoint) {
		this.testPoint = testPoint;
	}
	
}
