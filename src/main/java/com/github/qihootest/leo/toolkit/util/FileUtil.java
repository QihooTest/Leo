package com.github.qihootest.leo.toolkit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * 对文件的底层操作，包括读、写，文件的及目录的创建、删除
 * @author lianghui (lianghui@360.cn)
 *
 */
public class FileUtil {
	private static LogUtil log=LogUtil.getLogger(FileUtil.class);//日志记录
	private static List<String>filelist=new ArrayList<>();
	
	//文件的读写
	
	/**
	 * 从文件中以字节流方式读取并返回
	 * @param pathName
	 * @return FileInputStream
	 */
	public static FileInputStream readToFileInputStream(String pathName) {
		try {
			return new FileInputStream(pathName);
		} catch (FileNotFoundException e) {
			log.error("字节流方式读取文件失败："+pathName);
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 读取字符流，指定编码
	 * @param patnName 文件位置，相对或绝对路径
	 * @param enCoding 读取时使用的编码
	 * @return InputStreamReader
	 */
	public static InputStreamReader readToInputStreamReader(String patnName,String enCoding) {
		try {
			return new InputStreamReader(readToFileInputStream(patnName), enCoding);
		} catch (Exception e) {
			log.error("读取文件失败："+patnName);
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 读取字符流，指定编码
	 * @param pathName
	 * @param enCoding
	 * @return BufferedReader
	 */
	public static BufferedReader readToBufferedReader(String pathName,String enCoding) {
		try {
			return new BufferedReader(readToInputStreamReader((pathName), enCoding));
		} catch (Exception e) {
			log.error("读取文件失败："+pathName);
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 *  读取字符流，指定编码
	 * @param pathName
	 * @param enCoding
	 * @return String
	 */
	public static String readToString(String pathName,String enCoding) {
		BufferedReader buff=readToBufferedReader(pathName,enCoding);
		String line="";
		StringBuilder  str=new StringBuilder();
		try {
			while ((line = buff.readLine()) != null) { 
				str.append(line).append("\r\n"); 
			 } 
		} catch (Exception e) {
			log.error(e.getMessage());
			str.append("");
		}
		
		return String.valueOf(str);
	}

	/**
	 *  读取字符流，指定编码
	 * @param input
	 * @param enCoding
	 * @return String
	 */
	public static String readInputStreamToString(InputStream input,String enCoding) {
		BufferedReader reader ;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(input,enCoding));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return sb.toString();

	}
	
	/**
	 * 读取字符流，指定编码，按每行数据返回List
	 * @param pathName
	 * @param enCoding
	 * @return List<String>
	 */
	public static List<String> readLineToList(String pathName,String enCoding) {
		BufferedReader br=null;
		String line="";
		List<String>lines=new ArrayList<>();
		
		br=readToBufferedReader(pathName, enCoding);
		if (null==br) return null;
		try {
			while((line=br.readLine())!=null){
				lines.add(line.trim());
			}
			br.close();
		} catch (Exception e) {
			log.error("按行读取文件失败："+pathName);
			log.error(e.getMessage());
		}
		return lines;
	}
	
	/**
	 * 创建到文件的输出流
	 * @param pathName
	 * @return FileOutputStream
	 */
	public static  FileOutputStream getFileOutStream(String pathName) {
		//先创建一空白文件
		try {
			createFile(pathName);
			return new FileOutputStream(pathName);
		} catch (FileNotFoundException e) {
			log.error("创建输出到文件失败："+pathName);
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 按字节写文件
	 * @param byteStream
	 * @param pathName
	 * @return boolean
	 */
	public static boolean writeByteArray(byte[] byteStream,String pathName) {
		boolean flag=false;
		try {
			//先创建一空白文件
			FileOutputStream outStream = getFileOutStream(pathName);
			outStream.write(byteStream);
			outStream.flush();
			outStream.close();
			flag=true;
		} catch (IOException e) {
			log.error("写入文件失败"+pathName);
			log.error(e.getMessage());
			flag=false;
		}
		return flag;
	}
	
	/**
	 * 把字节流写入文件
	 * @param inStream
	 * @param pathName
	 * @return boolean
	 */
	public static boolean writeStream(InputStream inStream,String pathName) {
		byte[] temp;
		try {
			temp=new byte[inStream.available()];
			inStream.read(temp);
			return writeByteArray(temp,pathName);
		} catch (Exception e) {
			log.error("写入文件失败1"+pathName);
			log.error(e.getMessage());
			return false;
		}
		
	}

	/**
	 * 把字符串按指定编码写入文件
	 * @param str
	 * @param pathName
	 * @param enCoding
	 * @return boolean
	 */
	public static boolean writeString(String str,String pathName,String enCoding) {
		byte[] b;
		try {
			b=str.getBytes(enCoding);
		} catch (UnsupportedEncodingException e) {
			log.error("字符串按编码 "+enCoding+"转换字节错误");
			log.error(e.getMessage());
			return false;
		}
		return writeByteArray(b,pathName);
	}
	
	/**
	 * 把List中的字符串按行写入文件
	 * @param strList
	 * @param pathName
	 * @param enCoding
	 * @return boolean
	 */
	public static boolean writeString(List<String> strList,String pathName,String enCoding) {
		if (null==strList ) {
			log.error("待写入文件"+pathName+"的字符列表为null");
			return false;
		}		
		StringBuffer tmp=new StringBuffer();
		for (int i = 0; i < strList.size(); i++) {
			tmp.append(strList.get(i)+"\r\n");
		}
		return writeString(tmp.toString(), pathName, enCoding);
	}
	
	
	
	//文件的删除、创建、改名等操作

	/**
	 * 说明：删除指定的文件
	 * 
	 * @param file    	文件的完整绝对路径或者相对路径
	 * @return boolean 		删除成功返回true，删除失败、指定路径不存在返回false
	 */
	public static boolean delFile(File file) {
		if (file.isFile() == true) {
			return file.delete();
		}else{
			log.error("文件："+file+" 不存在，或者不是文件类型");
		}
		return false;
	}

	
	/**
	 * 说明：删除指定的文件
	 * 
	 * @param pathName  文件的完整绝对路径或者相对路径
	 * @return boolean 删除成功返回true，删除失败、指定路径不存在返回false
	 */
	public static boolean delFile(String pathName) {
		File path = new File(pathName);
		return delFile(path);
	}

	/**
	 * 说明：/删除指定文件夹下所有文件
	 * 
	 * @param folderPath     文件夹完整绝对路径或者相对路径
	 * @return boolean  删除成功返回true，删除失败、指定路径不存在返回false
	 */
	public static boolean delAllFile(String folderPath) {
		File path = new File(folderPath);
		if (!path.exists()) {
//			log.error("删除指定的文件夹："+folderPath+"不存在");
			return false;
		}
		if (!path.isDirectory()) {
//			log.error("删除指定的："+folderPath+"不是个文件夹");
			return false;
		}
		
		String[] tempList = path.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if ((path.toString()).endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				delFile(temp);
			}
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + File.separator + tempList[i]);// 再删除空文件夹
			}
		}
		return true;
	}

	/**
	 * 说明：删除文件夹
	 * 
	 * @param folderPath  文件夹完整绝对路径或者相对路径
	 */
	public static void delFolder(String folderPath) {

		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			log.error("删除文件夹："+folderPath+"出错");
			log.error(e.getMessage());
		}
	}

	/**
	 * 创建目录
	 * @param folderPath
	 * @return boolean 创建成功或者已存在返回true，不存在且创建失败返回false
	 */
	public static boolean createDictory(String folderPath){
		if (null==folderPath) return false;
		File path=new File(folderPath);
		if(path.exists()){
			return   true;
		}else{
			if (new File(folderPath).mkdirs()) {
				return  true;
			}
			if(!path.exists()){
				log.error("创建文件夹："+folderPath+"失败");
				return   false;
			}
		}
		return true;//永远不会执行到这里，不写这句就报语法错误
	}
	
	/**
	 * 创建指定的文件，空白文件，如果上层目录不存在则会创建
	 * @param pathName
	 * @return boolean
	 */
	public static boolean createFile(String pathName){
		File pathNameFile=new File(pathName);
		if(!pathNameFile.exists()){
			StringBuilder dictoryPath=new StringBuilder(pathName);
			dictoryPath.delete(getFileNameIndexFromPath(pathName), pathName.length());
			if ( !createDictory(dictoryPath.toString()) )return false;			
			try {
				pathNameFile.createNewFile();
				return true;
			} catch (IOException e) {
				log.error("创建文件："+pathName+"失败");
				log.error(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断文件或文件夹是否存在
	 * @param path
	 * @return boolean 存在返回true 
	 */
	public static boolean isExist(String path){
		return new File(path).exists();
	}
	
	/**
	 * 返回文件扩展名
	 * @param fileName
	 * @return String
	 */
	public static String getExtensionName(String fileName) {   
		if ((fileName != null) && (fileName.length() > 0)) {   
            int dot = fileName.lastIndexOf('.');   
            if ((dot >-1) && (dot < (fileName.length() - 1))) {   
                return fileName.substring(dot + 1).toLowerCase();   
            }   
        }   
        return "";   
    }   
	/**
	 * 返回指定文件夹下所有文件路径名
	 * @param folderPath
	 * @return List<String>
	 */
	public static List<String> getFilesFromFolder(String folderPath) {
		FileUtil myfile=new FileUtil();
		return myfile.getFilesOfFolder(folderPath);
	}
	
	/**
	 * 返回文件绝对路径内的上层目录
	 * @param absolutePath
	 * @return String
	 */
	public static String getParentPath(String absolutePath) {
		if (!isExist(absolutePath)) {
			log.info("文件不存在:"+absolutePath);
			return null;
		}
		File file=new File(absolutePath);
		return file.getParent();
	}
	
	/**
	 * 返回文件绝对路径内的文件名
	 * @param absolutePath
	 * @return String
	 */
	public static String getFileName(String absolutePath) {
		if (!isExist(absolutePath)) {
			log.info("文件不存在:"+absolutePath);
			return null;
		}
		File file=new File(absolutePath);
		return file.getName();
	}
	/**
	 * 返回文件夹下指定扩展名的文件列表
	 * @param folderPath
	 * @param extensionName
	 * @return List<String>
	 */
	public static List<String> getFilesFromFolder(String folderPath,String extensionName) {
		List<String>files=getFilesFromFolder(folderPath);
		List<String> tmpList=new ArrayList<>();
		
		if (null==files) return tmpList;
		if (null==extensionName || extensionName.length()<1) return tmpList;
		
		for (String filename : files) {
			String exname=getExtensionName(filename);
			if (exname.indexOf(extensionName.toLowerCase())>-1) tmpList.add(filename);
		}
		return tmpList;
	}
	
	/**
	 * 关闭stream，并且不抛出异常
	 * @param stream 待关闭的输入流
	 */
	public static void closeStreamIgnoreExpection(InputStream stream) {
		try{
			if(stream!=null){
				stream.close();
			}
		}catch (Exception e) {
			// do nothing here
		}
	}
	
	/**
	 * 重命名文件，输入参数为完全路径名
	 * @param oldPathName
	 * @param newPathName
	 * @return boolean
	 */
	public static boolean renameFile(String oldPathName, String newPathName) {
		if (StringUtil.IsNullOrEmpty(oldPathName) || StringUtil.IsNullOrEmpty(newPathName))return false;
		if (oldPathName.equals(newPathName))return false;
			
		// 新的文件名和以前文件名不同时,才有必要进行重命名
		File oldfile = new File(oldPathName);
		File newfile = new File(newPathName);
		
		if (newfile.exists()){// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
				log.error(newPathName + "已经存在！");
				return false;
		}else {
				oldfile.renameTo(newfile);
				return true;
		}
	}
	
	/**
	 * 拷贝单个文件 源和目的文件都为完全路径名
	 * @param srcPathName
	 * @param distPathName
	 * @return boolean
	 */
	public static boolean  copyFile(String srcPathName,String distPathName) {
		if (StringUtil.IsNullOrEmpty(srcPathName) || StringUtil.IsNullOrEmpty(srcPathName)) return false;
		if (distPathName.equals(srcPathName)) 		return false;
		return writeStream(readToFileInputStream(srcPathName), distPathName);
	}
	
	/**
	 * 拷贝文件夹
	 * @param srcFolderPath
	 * @param distFolderPath
	 * @return boolean
	 */
	public static boolean copyFolder(String srcFolderPath,String distFolderPath) {
		if (StringUtil.IsNullOrEmpty(srcFolderPath) || StringUtil.IsNullOrEmpty(distFolderPath)) return false;
		if (srcFolderPath.toLowerCase().equals(distFolderPath)) return false;
		 // 新建目标目录
		createDictory(distFolderPath);
        // 获取源文件夹当前下的文件或目录   
		File[] file = (new File(srcFolderPath)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				String sourceFile = file[i].getAbsolutePath();
				// 目标文件
				String targetFile =new File(distFolderPath).getAbsolutePath() +"/"+file[i].getName();
				copyFile(sourceFile,targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = srcFolderPath + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = distFolderPath + "/" + file[i].getName();
				copyFolder(dir1, dir2);
			}
		}

		return true;
	}
	
	/**
	 * 从文件完全路径中，获取最后一个"/"或"\"的位置
	 * @param filePathName
	 * @return int
	 */
	public static int getFileNameIndexFromPath(String filePathName){
		if (filePathName.toLowerCase().lastIndexOf("/")>-1){
			return  filePathName.toLowerCase().lastIndexOf("/");
		}else{
			return  filePathName.toLowerCase().lastIndexOf("\\");
		}
	}

	/**
	 * 返回目录下所有文件列表
	 * @param folderPath
	 * @return List<String>
	 */
	private  List<String> getFilesOfFolder(String folderPath) {
		File dir = new File(folderPath); 
        File[] files = dir.listFiles(); 
        if (files == null) {
//        	log.info("此目录下未取得任何文件："+folderPath);
            return null; 
        }
        
        for (int i = 0; i < files.length; i++) { 
	            if (files[i].isDirectory()) { 
	            	getFilesOfFolder(files[i].getAbsolutePath()); 
	            } else { 
	                filelist.add(files[i].getAbsolutePath());                    
	            }     
         } 
		
		return filelist;
	}
	
}
