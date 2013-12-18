package com.github.qihootest.leo.toolkit;

/**
 * 自定义异常信息
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class QtafException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public QtafException() {
    }

    public QtafException(String message) {
        super(message);
    }

    public QtafException(String message, Throwable cause) {
        super(message, cause);
    }

    public QtafException(Throwable cause) {
        super(cause);
    }

}