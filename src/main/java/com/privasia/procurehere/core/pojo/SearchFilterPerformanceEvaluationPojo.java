package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author priyanka
 */
public class SearchFilterPerformanceEvaluationPojo implements Serializable {

	private static final long serialVersionUID = -6790659338696997981L;
	private String formid;
	private String formname;
	private String referencenumber;
	private String referencename;
	private String formcreator;
	private String procurementcategory;
	private String businessunit;
	private String suppliername;
	private String status;

	public String getFormid() {
		return formid;
	}

	public void setFormid(String formid) {
		this.formid = formid;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getReferencenumber() {
		return referencenumber;
	}

	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	public String getReferencename() {
		return referencename;
	}

	public void setReferencename(String referencename) {
		this.referencename = referencename;
	}

	public String getFormcreator() {
		return formcreator;
	}

	public void setFormcreator(String formcreator) {
		this.formcreator = formcreator;
	}

	public String getProcurementcategory() {
		return procurementcategory;
	}

	public void setProcurementcategory(String procurementcategory) {
		this.procurementcategory = procurementcategory;
	}

	public String getBusinessunit() {
		return businessunit;
	}

	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
	}

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}