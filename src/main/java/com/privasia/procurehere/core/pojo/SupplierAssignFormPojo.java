package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class SupplierAssignFormPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8245272081382090946L;

	private String[] supplierFormIds;
	private String[] supplierIds;
	private Boolean assignToAllSuppliers;
	private Boolean reassignForm;

	public SupplierAssignFormPojo() {
		reassignForm = Boolean.FALSE;
		assignToAllSuppliers = Boolean.FALSE;
	}

	/**
	 * @return the supplierFormIds
	 */
	public String[] getSupplierFormIds() {
		return supplierFormIds;
	}

	/**
	 * @param supplierFormIds the supplierFormIds to set
	 */
	public void setSupplierFormIds(String[] supplierFormIds) {
		this.supplierFormIds = supplierFormIds;
	}

	/**
	 * @return the supplierIds
	 */
	public String[] getSupplierIds() {
		return supplierIds;
	}

	/**
	 * @param supplierIds the supplierIds to set
	 */
	public void setSupplierIds(String[] supplierIds) {
		this.supplierIds = supplierIds;
	}

	/**
	 * @return the assignToAllSuppliers
	 */
	public Boolean getAssignToAllSuppliers() {
		return assignToAllSuppliers;
	}

	/**
	 * @param assignToAllSuppliers the assignToAllSuppliers to set
	 */
	public void setAssignToAllSuppliers(Boolean assignToAllSuppliers) {
		this.assignToAllSuppliers = assignToAllSuppliers;
	}

	/**
	 * @return the reassignForm
	 */
	public Boolean getReassignForm() {
		return reassignForm;
	}

	/**
	 * @param reassignForm the reassignForm to set
	 */
	public void setReassignForm(Boolean reassignForm) {
		this.reassignForm = reassignForm;
	}

}
