package com.codingPower.ui.amap;

/**
 * 任务终止运行时异常
 * @author pengf
 *
 */
public class TaskCanceledException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -851226480904538252L;

	public TaskCanceledException() {
		super();
	}

	public TaskCanceledException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public TaskCanceledException(String detailMessage) {
		super(detailMessage);
	}

	public TaskCanceledException(Throwable throwable) {
		super(throwable);
	}

}
