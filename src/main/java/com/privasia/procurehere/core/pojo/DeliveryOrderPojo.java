/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author ravi
 */
public class DeliveryOrderPojo implements Serializable {

	private static final long serialVersionUID = -1118984102530663683L;

	private String doId;

	private List<DeliveryOrderItemPojo> items;

	/**
	 * @return the doId
	 */
	public String getDoId() {
		return doId;
	}

	/**
	 * @param doId the doId to set
	 */
	public void setDoId(String doId) {
		this.doId = doId;
	}

	/**
	 * @return the items
	 */
	public List<DeliveryOrderItemPojo> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<DeliveryOrderItemPojo> items) {
		this.items = items;
	}

}
