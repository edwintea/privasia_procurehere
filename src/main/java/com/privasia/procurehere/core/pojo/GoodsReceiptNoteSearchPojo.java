package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.GrnStatus;

/**
 * @author pooja
 */
public class GoodsReceiptNoteSearchPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6939422976761141889L;

	private String id;

	private String grnnumber;

	private String grnname;

	private String referencenumber;

	private String ponumber;

	private String supplier;

	private String businessunit;

	private String createdby;

	private GrnStatus status;

	private String grnIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGrnnumber() {
		return grnnumber;
	}

	public void setGrnnumber(String grnnumber) {
		this.grnnumber = grnnumber;
	}

	public String getGrnname() {
		return grnname;
	}

	public void setGrnname(String grnname) {
		this.grnname = grnname;
	}

	public String getReferencenumber() {
		return referencenumber;
	}

	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	public String getPonumber() {
		return ponumber;
	}

	public void setPonumber(String ponumber) {
		this.ponumber = ponumber;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getBusinessunit() {
		return businessunit;
	}

	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public GrnStatus getStatus() {
		return status;
	}

	public void setStatus(GrnStatus status) {
		this.status = status;
	}

	public String getGrnIds() {
		return grnIds;
	}

	public void setGrnIds(String grnIds) {
		this.grnIds = grnIds;
	}

}
