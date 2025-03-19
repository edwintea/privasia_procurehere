package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.ActionType;
import com.privasia.procurehere.core.enums.FilterTypes;

/**
 * @author parveen
 */
public class SearchSortFilterPojo implements Serializable{

	 
	private static final long serialVersionUID = -6424401233564736757L;

	private String searchValue;

	private FilterTypes type;

	private String creator;

	private String supplierName;

	private String unitName;

	private String status;

	private Integer start;

	private Integer length;

	private String sortValue;

	private String order;

	private ActionType actionType;
	
	private String eventOwner;

	private String buyerName;
	
	public SearchSortFilterPojo() {

	}

	/**
	 * @return the eventOwner
	 */
	public String getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * @return the type
	 */
	public FilterTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FilterTypes type) {
		this.type = type;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * @return the sortValue
	 */
	public String getSortValue() {
		return sortValue;
	}

	/**
	 * @param sortValue the sortValue to set
	 */
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
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

	/**
	 * @return the actionType
	 */
	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String toLogString() {
		return "SearchSortFilterPojo [searchValue=" + searchValue + ", type=" + type + ", creator=" + creator + ", supplierName=" + supplierName + ", unitName=" + unitName + ", status=" + status + ", start=" + start + ", length=" + length + "]";
	}
}