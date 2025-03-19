/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.util.List;

/**
 * @author kapil
 *
 */
public class BidHistoryChartSupplierPojo {

	private String label;
	private List<List<String>> data;

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the data
	 */
	public List<List<String>> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
	}

}
