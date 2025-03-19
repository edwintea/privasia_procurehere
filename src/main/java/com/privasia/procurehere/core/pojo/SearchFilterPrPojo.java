package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author sudesha
 */
public class SearchFilterPrPojo implements Serializable {

	private static final long serialVersionUID = -5706067632082706741L;

	private String prIds;

	private String referencenumber;

	private String nameofpr;

	private String suppliername;

	private String prnumber;

	private String prcreatedby;

	private String prapprovedby;

	private String currency;

	private String businessunit;

	private String prstatus;

	private String nameofpo;

	private String ponumber;

	private String pocreatedby;

	private String postatus;

	/**
	 * @return the prIds
	 */
	public String getPrIds() {
		return prIds;
	}

	/**
	 * @param prIds the prIds to set
	 */
	public void setPrIds(String prIds) {
		this.prIds = prIds;
	}

	/**
	 * @return the referencenumber
	 */
	public String getReferencenumber() {
		return referencenumber;
	}

	/**
	 * @param referencenumber the referencenumber to set
	 */
	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	/**
	 * @return the nameofpr
	 */
	public String getNameofpr() {
		return nameofpr;
	}

	/**
	 * @param nameofpr the nameofpr to set
	 */
	public void setNameofpr(String nameofpr) {
		this.nameofpr = nameofpr;
	}

	/**
	 * @return the suppliername
	 */
	public String getSuppliername() {
		return suppliername;
	}

	/**
	 * @param suppliername the suppliername to set
	 */
	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	/**
	 * @return the prnumber
	 */
	public String getPrnumber() {
		return prnumber;
	}

	/**
	 * @param prnumber the prnumber to set
	 */
	public void setPrnumber(String prnumber) {
		this.prnumber = prnumber;
	}

	/**
	 * @return the prcreatedby
	 */
	public String getPrcreatedby() {
		return prcreatedby;
	}

	/**
	 * @param prcreatedby the prcreatedby to set
	 */
	public void setPrcreatedby(String prcreatedby) {
		this.prcreatedby = prcreatedby;
	}

	/**
	 * @return the prapprovedby
	 */
	public String getPrapprovedby() {
		return prapprovedby;
	}

	/**
	 * @param prapprovedby the prapprovedby to set
	 */
	public void setPrapprovedby(String prapprovedby) {
		this.prapprovedby = prapprovedby;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the businessunit
	 */
	public String getBusinessunit() {
		return businessunit;
	}

	/**
	 * @param businessunit the businessunit to set
	 */
	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
	}

	/**
	 * @return the prstatus
	 */
	public String getPrstatus() {
		return prstatus;
	}

	/**
	 * @param prstatus the prstatus to set
	 */
	public void setPrstatus(String prstatus) {
		this.prstatus = prstatus;
	}

	/**
	 * @return the nameofpo
	 */
	public String getNameofpo() {
		return nameofpo;
	}

	/**
	 * @param nameofpo the nameofpo to set
	 */
	public void setNameofpo(String nameofpo) {
		this.nameofpo = nameofpo;
	}

	/**
	 * @return the ponumber
	 */
	public String getPonumber() {
		return ponumber;
	}

	/**
	 * @param ponumber the ponumber to set
	 */
	public void setPonumber(String ponumber) {
		this.ponumber = ponumber;
	}

	/**
	 * @return the pocreatedby
	 */
	public String getPocreatedby() {
		return pocreatedby;
	}

	/**
	 * @return the postatus
	 */
	public String getPostatus() {
		return postatus;
	}

	/**
	 * @param postatus the postatus to set
	 */
	public void setPostatus(String postatus) {
		this.postatus = postatus;
	}

	/**
	 * @param pocreatedby the pocreatedby to set
	 */
	public void setPocreatedby(String pocreatedby) {
		this.pocreatedby = pocreatedby;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchFilterPrPojo [prIds=" + prIds + ", referencenumber=" + referencenumber + ", nameofpr=" + nameofpr + ", suppliername=" + suppliername + ", prnumber=" + prnumber + ", prcreatedby=" + prcreatedby + ", prapprovedby=" + prapprovedby + ", businessunit=" + businessunit + ", prstatus=" + prstatus + ", nameofpo=" + nameofpo + ", ponumber=" + ponumber + ", pocreatedby=" + pocreatedby + ", postatus=" + postatus + "]";
	}

}
