package com.github.qihootest.leo.dispatch.report;

/**
 * 一个testng的测试集或测试套的执行结果信息
 * @author lianghui (lianghui@360.cn)
 */
public class TngCount {
	/**
	 * 测试套/集名称
	 */
	private String name;
	/**
	 * 所属的测试套的名称，为空标识无所属测试套
	 */
	private String suiteName;
	/**
	 * 失败数
	 */
	private int failed;
	/**
	 * 通过数
	 */
	private int passed;
	/**
	 * 跳过数
	 */
	private int skipped;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public int getPassed() {
		return passed;
	}
	public void setPassed(int passed) {
		this.passed = passed;
	}
	public int getSkipped() {
		return skipped;
	}
	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}
}
