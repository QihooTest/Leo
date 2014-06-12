package com.github.qihootest.leo.toolkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TreeMap;

import com.github.qihootest.leo.ift.IftConf;

/**
 *  通用工具包
 *	@author lianghui (lianghui@360.cn) 2013-01-10
 */
public class CommUtils {

	private static LogUtil log = LogUtil.getLogger(CommUtils.class);// 日志记录

	/**
	 * 将指定字符串作MD5加密
	 * @param source
	 * @param enCoding
	 * @return String 计算异常返回null
	 */
	public static String getMD5(String source,String enCoding) {
		byte[] sourceByte = null;
		try {
			sourceByte = source.getBytes(enCoding);
		} catch (UnsupportedEncodingException e) {
			log.error("按编码："+enCoding+"解析为字节异常");
			log.error(e.getMessage());
			return null;
		}
		return getMD5(sourceByte);
	}

	/**
	 * 将指定字符串中的中文按GBK编码进行md5加密
	 * @param source
	 * @return String
	 */
	public static String getMD5Gbk(String source) {
		return getMD5(source,"GBK");
	}

	/**
	 * 计算文件的md5值 
	 * @param filepath
	 * @return String  异常返回null
	 */
	public static String fileMD5(String filepath){
		byte[] bytes = null;
		try {
			bytes = getBytesFromFile(filepath);
		} catch (IOException e) {
			log.error("读取文件异常："+filepath);
			log.error(e.getMessage());
			return null;
		}
		return getMD5(bytes);
	}

	/**
	 * urlencode指定字符串,编码格式为GBK
	 * 
	 * @param strmes
	 * @return String 转码异常返回null
	 */
	public static String urlEncode(String strmes) {
		return urlEncode(strmes, "GBK");
	}

	/**
	 * urlencode指定字符串，需指定编码
	 * @param strmes
	 * @param enCoding
	 * @return String 转码异常返回null
	 */
	public static String urlEncode(String strmes, String enCoding) {
		String EncodeRes;
		try {
			EncodeRes = URLEncoder.encode(strmes, enCoding);
		} catch (UnsupportedEncodingException e) {
			EncodeRes = null;
			log.error("指定的编码：" + enCoding + "不支持，请检查");
			log.error(e.getMessage());
		}
		return EncodeRes;

	}

	/**
	 * urldecode指定字符串，使用GBK编码
	 * 
	 * @param strmes
	 * @return  String 转码异常返回null
	 */
	public static String urlDecode(String strmes) {
		return urlDecode(strmes,"GBK");

	}

	/**
	 * urldecode指定字符串，指定编码
	 * @param strmes
	 * @param encoding
	 * @return String 转码异常返回null
	 */
	public static String urlDecode(String strmes, String encoding) {
		String decodeRes;
		try {
			decodeRes = URLDecoder.decode(strmes, encoding);
		} catch (UnsupportedEncodingException e) {
			decodeRes = null;
			log.error(e.getMessage());
		}
		return decodeRes;

	}

	/**
	 * urlRawDecode指定字符串 如果字符中有空格则对空格不作decode
	 * 使用编码为GBK
	 * @param strmes
	 * @return String 转码异常返回null
	 */
	public static String urlRawDecode(String strmes) {
		String decodeRes;
		String tmpdecodeRes;
		try {
			tmpdecodeRes = URLDecoder.decode(strmes, "GBK");
			decodeRes = tmpdecodeRes.replace(" ", "+");
		} catch (UnsupportedEncodingException e) {
			decodeRes = null;
			log.error(e.getMessage());
		}
		return decodeRes;

	}

	/**
	 * 生成随机数
	 * @param length 指定字符串长度
	 * @return String
	 */
	public static String getRandomStr(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
		Object initLock = new Object();
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			synchronized (initLock) {
				if (randGen == null) {
					randGen = new Random();
					numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
				}
			}
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}
	
	/**
	 * 生成随机数(只有数字)
	 * @param length 指定字符串长度
	 * @return String
	 */
	public static String getNumRandomStr(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
		Object initLock = new Object();
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			synchronized (initLock) {
				if (randGen == null) {
					randGen = new Random();
					numbersAndLetters = ("0123456789").toCharArray();
				}
			}
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	

	/**
	 * 解析字符串返回map键值对(例：a=1&b=2 => a=1,b=2)
	 * @param query 源参数字符串
	 * @param split1 键值对之间的分隔符（例：&）
	 * @param split2 key与value之间的分隔符（例：=）
	 * @return TreeMap<String, String>
	 */
	public static TreeMap<String, String> parseQuery(String query, char split1,char split2) {
		if (!StringUtil.IsNullOrEmpty(query) && query.indexOf(split2) > 0) {
			TreeMap<String, String> result = new TreeMap<String, String>();
			String name = null;
			// 定义value值
			String value = null;
			@SuppressWarnings("unused")
			String tempValue = "";
			int len = query.length();
			for (int i = 0; i < len; i++) {
				char c = query.charAt(i);
				if (c == split2) {
					value = "";
				} else if (c == split1) {
					result.put(name, value);
					name = null;
					value = null;
				} else if (value != null) {
					value += c;
				} else {
					name = (name != null) ? (name + c) : "" + c;
				}
			}
			if (!StringUtil.IsNullOrEmpty(name) && value != null) {
				result.put(name, value);
			}
			return result;
		}
		return null;
	}

	/**
	 * 获取Sha1值
	 * @param byteText
	 * @return String 异常返回null
	 */
	public static String getSha1(byte[] byteText) {
		java.security.MessageDigest md = null;
		try {
			md = java.security.MessageDigest.getInstance("sha-1");
			md.update(byteText);
			byte[] sha1 = md.digest();
			return byte2hex(sha1);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	
	/**
	 * 获取文件的hash值
	 * @param fileName
	 * @param hashType
	 * @return String 异常返回null
	 */

	public static String getHash(String fileName, String hashType){
		
		try {
			InputStream fis = new FileInputStream(fileName);
			byte[] buffer = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance(hashType);
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	/**
	 * 获取十六进制字符串
	 * @param b
	 * @return String
	 */

	public static String toHexString(byte[] b) {
		char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 说明：获取当前时间，返回的格式为：yyyMMddHHmmss
	 * 
	 * @return String yyyMMddHHmmss
	 */
	public static String getNowTime() {
		String nowTime = "";
		Date date = new Date();
		SimpleDateFormat from = new SimpleDateFormat(IftConf.DateFormat);
		nowTime = from.format(date);
		return nowTime;
	}

	/**
	 * 说明：获取当前时间戳，Unix时间戳格式
	 * 
	 * @return String
	 */
	public static String getTimestamp() {
		String nowTime = Long.toString(new Date().getTime() / 1000);
		return nowTime;
	}

	/**当前线程休息miliSeconds毫秒*/
	/**
	 * 暂停
	 * @param miliSeconds 毫秒
	 */
	public static void sleep(int miliSeconds){
		try {
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}
	/**
	 * 获取纯数字字符串
	 * @param num
	 * @return String
	 */
	
	public static String getRandNum(int num) {
		String res="";
		if (num<1) return res;
		String arr[]={"0","1","2","3","4","5","6","7","8","9"};
		for (int i = 0; i < num; i++) {
			Random ran = new Random();
			int index=ran.nextInt(10);
			res=res+arr[index];
		}
		return res;
	}
	
	/**
	 * 说明：获取当前时间+指定位数随机数字，返回的格式为：yyyMMddHHmmss+rand
	 * @param num
	 * @return String yyyMMddHHmmss
	 */
	public static String getStrRandNum(int num) {
		return getNowTime()+getRandNum(num);
	}

	/**
	 * 获取当前被执行的行号
	 * @return int
	 */
	public static int getLineNum(){
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
	
	/**
	 * 获取当前执行的方法名
	 * @return String
	 */
	public static String getMethodName(){
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
	
	/**
	 * 获取当前执行的类名  不包括包名
	 * @return String
	 */
	public static String getClassSimpleName(){
		String classNameTmp=Thread.currentThread().getStackTrace()[2].getClassName();
		classNameTmp=classNameTmp.substring(classNameTmp.lastIndexOf(".")+1, classNameTmp.length());
		return classNameTmp;
	}
	
	private static byte[] getBytesFromFile(String filepath) throws IOException {
		File file = new File(filepath);
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		is.close();
		return bytes;
	}
	
	/**
	 * 将指定字节作MD5加密
	 * @param source
	 * @return String
	 */
	private static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
			// 进制需要 32 个字符
			int k = 0;// 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
				// 进制字符的转换
				byte byte0 = tmp[i];// 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
				// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

			}
			s = new String(str);// 换后的结果转换为字符串

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private static String byte2hex(byte[] b) {// 二行制转字符串
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toLowerCase();
	}

	

}
