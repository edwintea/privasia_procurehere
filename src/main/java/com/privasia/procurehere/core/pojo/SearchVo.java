/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author arc
 */
public class SearchVo implements Serializable{

 	private static final long serialVersionUID = -5929956111201391004L;
	private String id = null;
	private String globalSreach = null;
	private String status = null;
	private String order = null;
	private String pageNo = "";

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the globalSreach
	 */
	public String getGlobalSreach() {
		return globalSreach;
	}

	/**
	 * @param globalSreach the globalSreach to set
	 */
	public void setGlobalSreach(String globalSreach) {
		this.globalSreach = globalSreach;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

}
