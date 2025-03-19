package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.privasia.procurehere.core.enums.SupplierCqStatus;

public class CqPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8209404764817769993L;

	private String id;

	private Date createdDate;

	private String name;

	private Integer cqOrder;
	
	private SupplierCqStatus supplierCqStatus = SupplierCqStatus.PENDING;

	public CqPojo() {

	}

	public CqPojo(String id, String name, Date createdDate) {
		this.id = id;
		this.createdDate = createdDate;
		this.name = name;
	}

	public CqPojo(String id, String name, Date createdDate, Integer cqOrder) {
		this.id = id;
		this.createdDate = createdDate;
		this.name = name;
		this.cqOrder = cqOrder;
	}

	public CqPojo(String id, String name, Integer cqOrder) {
		this.id = id;
		this.name = name;
		this.cqOrder = cqOrder;
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
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cqOrder
	 */
	public Integer getCqOrder() {
		return cqOrder;
	}

	/**
	 * @param cqOrder the cqOrder to set
	 */
	public void setCqOrder(Integer cqOrder) {
		this.cqOrder = cqOrder;
	}

	/**
	 * @return the supplierCqStatus
	 */
	public SupplierCqStatus getSupplierCqStatus() {
		return supplierCqStatus;
	}

	/**
	 * @param supplierCqStatus the supplierCqStatus to set
	 */
	public void setSupplierCqStatus(SupplierCqStatus supplierCqStatus) {
		this.supplierCqStatus = supplierCqStatus;
	}
	
}