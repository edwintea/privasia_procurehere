/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vipul
 */
public class StatisticsPojo implements Serializable {

	private static final long serialVersionUID = 1774404562052020043L;

	String label;
	double value;

	Map<String, Double> bData;

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
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the bData
	 */
	public Map<String, Double> getbData() {
		if(bData == null) {
			bData = new HashMap<String, Double>();
		}
		return bData;
	}

	/**
	 * @param bData the bData to set
	 */
	public void setbData(Map<String, Double> bData) {
		this.bData = bData;
	}
	
	

}