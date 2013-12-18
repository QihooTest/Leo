package com.github.qihootest.leo.toolkit.httpclient;

import java.util.Map;

/**
 * http请求发送信息及返回信息
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class ResponseInfo {

	private String httpUrl;//发起get或post请求的url信息，post的body信息转换为参数对字符串标识
	private String resBodyInfo;//http请求返回的body信息
	private Map<String, String> resHeaderInfo;//http请求返回的Header键值对
	private String errMsgInfo;//记录错误信息
	public String getHttpUrl() {
		return httpUrl;
	}
	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}
	public String getResBodyInfo() {
		return resBodyInfo;
	}
	public void setResBodyInfo(String resBodyInfo) {
		this.resBodyInfo = resBodyInfo;
	}
	public Map<String, String> getResHeaderInfo() {
		return resHeaderInfo;
	}
	public void setResHeaderInfo(Map<String, String> resHeaderInfo) {
		this.resHeaderInfo = resHeaderInfo;
	}
	public String getErrMsgInfo() {
		return errMsgInfo;
	}
	public void setErrMsgInfo(String errMsgInfo) {
		this.errMsgInfo = errMsgInfo;
	}
}
