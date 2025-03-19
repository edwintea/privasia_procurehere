package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Priyanka
 */
public class RftEventAwardPojo implements Serializable {

	private static final long serialVersionUID = -1399044888943281514L;
	/*
	 * private String title; private String itemSeq; private String itemName;
	 * private String supplierName; private String supplierPrice; private String
	 * totalSupplierPrice; private String awardedPrice; private String
	 * totalAwardPrice; private String tax; private String taxType; private
	 * String awardRemarks; private String grandTotalPrice; private String
	 * totalPrice; private String currentDate;
	 */

	private List<RftEventAwardDetailsPojo> rftEventAwardDetailsPojo;

	private String enventId;

	private String remark;

	public List<RftEventAwardDetailsPojo> getRftEventAwardDetailsPojo() {
		return rftEventAwardDetailsPojo;
	}

	public void setRftEventAwardDetailsPojo(List<RftEventAwardDetailsPojo> rftEventAwardDetailsPojo) {
		this.rftEventAwardDetailsPojo = rftEventAwardDetailsPojo;
	}

	public String getEnventId() {
		return enventId;
	}

	public void setEnventId(String enventId) {
		this.enventId = enventId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
