package com.github.qihootest.leo.dispatch.testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试用例基类
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 *
 */
public abstract class SuperCase implements ICase {

		protected List<String> xmlPathNameList;
		/**
		 * 构造函数
		 */
		public SuperCase(){
			xmlPathNameList=new ArrayList<>();
		}
		/**
		 * 返回用例列表  xml文件信息
		 * @return List<String>
		 */
		public List<String> getCaseList() {
			return this.xmlPathNameList;
		}
}
