/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Vipul
 */
public class YearStatisticsPojo implements Serializable {

	private static final long serialVersionUID = 1774404562052020043L;

	String label;
	YearStatisticsValuePojo valuePojo;

	// double value2;
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the valuePojo
	 */
	public YearStatisticsValuePojo getValuePojo() {
		return valuePojo;
	}

	/**
	 * @param valuePojo the valuePojo to set
	 */
	public void setValuePojo(YearStatisticsValuePojo valuePojo) {
		this.valuePojo = valuePojo;
	}

}