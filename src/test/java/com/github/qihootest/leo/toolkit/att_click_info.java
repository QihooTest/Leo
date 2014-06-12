/**
 * 
 */
package com.github.qihootest.leo.toolkit;

import com.github.qihootest.leo.toolkit.mysql.dao.Table;

/**
 * @author lianghui (lianghui@360.cn)
 *
 */
public class att_click_info extends Table{
	private String id;
	private String url_id;
	private String ip;
	private String agent;
	private String create_time;
	private String desc;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl_id() {
		return url_id;
	}
	public void setUrl_id(String url_id) {
		this.url_id = url_id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	

}
