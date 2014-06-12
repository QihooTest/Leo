package com.github.qihootest.leo.toolkit.util;

import java.io.InputStream;
import java.util.Properties;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 针对CentOS服务器的文件进行下载，重命名等操作
 * @author lianghui (lianghui@360.cn)
 */
public class SftpFileUtil {

	private  static Session session = null;
	private  static Channel channel = null;
    private  LogUtil log=LogUtil.getLogger(SftpFileUtil.class);//日志记录
    /**
     * 主机host
     */
    public String FTPHOST;
    /**
     * 端口
     */
    public String PORT;
    /**
     * 用户名
     */
    public String FTPUSERNAME;
    /**
     * 密码
     */
    public String FTPPASSWORD;

    private  ChannelSftp getChannel()  {
    	
    	if (null!=channel) {
    		closeChannelOnly();
		}
        try {        	
	        channel = getSession().openChannel("sftp"); // 打开SFTP通道
	        channel.connect(); // 建立SFTP通道的连接
//	        log.info("连接SFTP通道创建成功");
	        return (ChannelSftp) channel;
	        
		} catch (JSchException e) {
			log.error("连接SFTP通道创建失败");
			log.error(e.getMessage());
			return null;
		} 
    }

    private  Session getSession()  {
    	
    	if (null!=session) {
    		 return session;
		}
    	
    	
        int ftpPort = 21;
        if (PORT != null && !PORT.equals("")) {
            ftpPort = Integer.valueOf(PORT);
        }

        JSch jsch = new JSch(); // 创建JSch对象
       
        try {        	
        	// 根据用户名，主机ip，端口获取一个Session对象
			session = jsch.getSession(FTPUSERNAME, FTPHOST, ftpPort);
			
	        if (FTPPASSWORD != null) {
	            session.setPassword(FTPPASSWORD); // 设置密码
	        }
	        Properties config = new Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config); // 为Session对象设置properties
	        session.setTimeout(300000); // 设置timeout时间为5分钟
	        session.connect(); // 通过Session建立链接
	        return session;
	        
		} catch (JSchException e) {
			log.error("连接 "+FTPHOST+":"+ftpPort+"的SFTP通道创建失败");
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
	public void closeChannel() {
		if (channel != null) {
			channel.disconnect();
			channel = null;
		}
		if (session != null) {
			session.disconnect();
			session = null;
		}
	}

    /**
     * 删除服务器上的文件
     * @param filepath
     * @return boolean
     */
    public  boolean delFile(String filepath) {
		boolean flag=false;
		ChannelSftp channel=getChannel();
		try {
			channel.rm(filepath);
			log.info("删除文件"+filepath+"成功");
			flag=true;
		} catch (SftpException e) {
			log.error("删除文件"+filepath+"失败");
			log.error(e.getMessage());
		}finally {
 			//channel.quit();
           
        }
		
		return flag;
	}
    
    /**
     * 删除指定目录，此目录必须为空的目录
     * @param directory
     * @return boolean
     */
    public  boolean delDir(String directory) {
  		boolean flag=false;
  		ChannelSftp channel=getChannel();
  		try {
  			channel.rmdir(directory);
  			log.info("删除目录："+directory+"成功");
  			flag=true;
  		} catch (SftpException e) {
  			log.error("删除目录："+directory+"失败");
  			log.error(e.getMessage());
  		}finally {
 			//channel.quit();            
        }
  		
  		return flag;
  	}

    /**
     * 文件重命名
     * @param oldpath
     * @param newpath
     * @return boolean
     */
    public  boolean rename(String oldpath,String newpath) {
 		boolean flag=false;
 		ChannelSftp channel=getChannel();
 		try {
 			channel.rename(oldpath, newpath);
 			log.info("重命名文件"+oldpath+"成功");
 			log.info("更新后的文件名为："+newpath);
 			flag=true;
 		} catch (SftpException e) {
 			log.error("重命名文件"+oldpath+"失败");
 			log.error(e.getMessage());
 		}finally {
 			//channel.quit();
           
        }
 		
 		return flag;
 	}

    /**
     * 下载指定的文档内容，保存到指定位置，返回文件内容
     * @param filepath
     * @param savepath
     * @return  String
     */
    public  String getFile(String filepath,String savepath) {
 		String strtmp=null;
    	InputStream  input;
 		ChannelSftp channel=getChannel();
 		try {
 			input=channel.get(filepath);
 			strtmp=FileUtil.readInputStreamToString(input, "UTF-8");
 			if (null!=savepath && savepath.length()>0) {
 				FileUtil.writeString(strtmp, savepath, "UTF-8");
			}
// 			log.info("从文件"+filepath+"获取信息成功");
 		} catch (SftpException e) {
 			log.error("从文件"+filepath+"获取信息失败");
 			log.error(e.getMessage());
 		}finally {
// 			channel.quit();
        }
 		
 		return strtmp;		
 	
 	}
    
    /**
     * 下载指定的文档内容，保返回文件内容
     * @param filepath
     * @return String
     */
    public  String getFile(String filepath) { 		
 		return getFile(filepath, null); 	
 	}

}
