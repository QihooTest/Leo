package com.github.qihootest.leo.ift.core;

import com.github.qihootest.leo.toolkit.httpclient.ResponseInfo;


/**
 * 接口测试执行结果信息
 * @author lianghui (lianghui@360.cn)
 */
public class IFtResultInfo {
	private ResponseInfo responseInfo;//http请求的返回信息
	private String expRes;//预期结果信息
	private String actRes;//整理后的实际结果信息
	private boolean compareRes;//预期与实际的比对结果  true或false
	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}
	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}
	public String getExpRes() {
		return expRes;
	}
	public void setExpRes(String expRes) {
		this.expRes = expRes;
	}
	public String getActRes() {
		return actRes;
	}
	public void setActRes(String actRes) {
		this.actRes = actRes;
	}
	public boolean getCompareRes() {
		return compareRes;
	}
	public void setCompareRes(boolean compareRes) {
		this.compareRes = compareRes;
	}
	
	

}
