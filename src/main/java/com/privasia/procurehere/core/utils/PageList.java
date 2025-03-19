/**
 * 
 */
package com.privasia.procurehere.core.utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author ravi
 * @param <E>
 */
public class PageList<E> extends ArrayList<E> implements Serializable {

	private static final long serialVersionUID = -7154992371221470488L;

	private int totalSize;

	/**
	 * @return the totalSize
	 */
	public int getTotalSize() {
		return totalSize;
	}

	/**
	 * @param totalSize the totalSize to set
	 */
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

}
