package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class SourcingReqBqPojo implements Serializable {

	private static final long serialVersionUID = 8120655645791624398L;
	private String id;
	private String formId;
	private String bqName;
	private String bqDesc;

	private String searchVal;
	private String filterVal;

	private Integer start;
	private Integer pageLength;
	private Integer pageNo;

	public SourcingReqBqPojo() {
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
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the bqName
	 */
	public String getBqName() {
		return bqName;
	}

	/**
	 * @param bqName the bqName to set
	 */
	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	/**
	 * @return the bqDesc
	 */
	public String getBqDesc() {
		return bqDesc;
	}

	/**
	 * @param bqDesc the bqDesc to set
	 */
	public void setBqDesc(String bqDesc) {
		this.bqDesc = bqDesc;
	}

	/**
	 * @return the searchVal
	 */
	public String getSearchVal() {
		return searchVal;
	}

	/**
	 * @param searchVal the searchVal to set
	 */
	public void setSearchVal(String searchVal) {
		this.searchVal = searchVal;
	}

	/**
	 * @return the filterVal
	 */
	public String getFilterVal() {
		return filterVal;
	}

	/**
	 * @param filterVal the filterVal to set
	 */
	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	/**
	 * @return the start
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * @return the pageLength
	 */
	public Integer getPageLength() {
		return pageLength;
	}

	/**
	 * @param pageLength the pageLength to set
	 */
	public void setPageLength(Integer pageLength) {
		this.pageLength = pageLength;
	}

	/**
	 * @return the pageNo
	 */
	public Integer getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
}
