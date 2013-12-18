package com.github.qihootest.leo.toolkit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 连接到CentOS服务器，执行命令
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 */
public class SftpExecUtil {

	private  static Session session = null;
	private  static Channel channel = null;
    private  LogUtil log=LogUtil.getLogger(SftpExecUtil.class);//日志记录

    /**
     * 主机host
     */
    public  String SshHost = "";
    /**
     * 端口
     */
    public  String SshPort = "";
    /**
     * 用户名
     */
    public  String SshUserName = "";
    /**
     * 密码
     */
    public  String SshPassword = "";
    
    private Session getSession() {
    	if (null!=session) {
			return session;
		}
        String ftpHost = SshHost;
        String port = SshPort;
        String ftpUserName = SshUserName;
        String ftpPassword = SshPassword;

        int ftpPort = 21;
        if (port != null && !port.equals("")) {
            ftpPort = Integer.valueOf(port);
        }

        JSch jsch = new JSch(); // 创建JSch对象
       
        try {        	
        	// 根据用户名，主机ip，端口获取一个Session对象
			session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
	        if (ftpPassword != null) {
	            session.setPassword(ftpPassword); // 设置密码
	        }
	        Properties config = new Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config); // 为Session对象设置properties
	        session.setTimeout(300000); // 设置timeout时间为5分钟
	        session.connect(); // 通过Session建立链接
//	        log.info("连接 "+ftpHost+":"+ftpPort+"的SFTP通道创建成功");
	        return  session;
	        
		} catch (JSchException e) {
			log.error("连接 "+ftpHost+":"+ftpPort+"的SFTP通道创建失败");
			log.error(e.getMessage());
			return null;
		} 
	}
    private  Channel getChannel()  {
    	if (null!=channel){
    		closeChannelOnly();
    	}
    	session=getSession();              
        try {        	
	        channel = session.openChannel("exec"); // 打开exec通道
	        return  channel;
		} catch (JSchException e) {
			log.error("连接 SFTP通道创建失败");
			log.error(e.getMessage());
			return null;
		} 
    }
    
    /**
     * 关闭连接
     */
    public  void closeChannelOnly()  {
        if (channel != null) {
            channel.disconnect();
            channel=null;
        }
    }
    /**
     * 关闭连接
     */
    public  void closeChannel()  {
        if (channel != null) {
            channel.disconnect();
            channel=null;
        }
        if (session != null) {
            session.disconnect();
            session=null;
        }
    }

    /**
     * 返回执行命令的内容
     * @param command
     * @return String
     */
    public  String execStr(String command) {		
		Channel channel=getChannel();	
		String res="";
		try {
			((ChannelExec)channel).setCommand(command);
			InputStream in=channel.getInputStream();
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			 channel.connect(); 			 
			 res=FileUtil.readInputStreamToString(in, "UTF-8");
			 in=null;
//			 log.info("命令："+command+"执行完毕\n");
//			 log.info("命令执行结果为：\n"+res);
		} catch (IOException e) {
			log.error("命令："+command+"执行异常");
			log.error(e.getMessage());
		} catch (JSchException e) {
			log.error("命令："+command+"执行异常");
			log.error(e.getMessage());
		}
		return res;
	}
    
   
}
