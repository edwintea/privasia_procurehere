package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author yogesh
 */
public class PoShareBuyerPojo implements Serializable{

	
	private static final long serialVersionUID = 2586109522428775353L;

	private String id;

	private String buyerId;

	private String financeCompanyName;

	private String buyerCompanyName;

	public PoShareBuyerPojo() {

	}

	public PoShareBuyerPojo(String id, String financeCompanyName, String buyerCompanyName) {
		this.id = id;
		this.financeCompanyName = financeCompanyName;

		this.buyerCompanyName = buyerCompanyName;

	}

	public PoShareBuyerPojo(String id, String financeCompanyName, String buyerCompanyName, String buyerId) {
		this.id = id;
		this.buyerId = buyerId;
		this.financeCompanyName = financeCompanyName;

		this.buyerCompanyName = buyerCompanyName;

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
	 * @return the financeCompanyName
	 */
	public String getFinanceCompanyName() {
		return financeCompanyName;
	}

	/**
	 * @param financeCompanyName the financeCompanyName to set
	 */
	public void setFinanceCompanyName(String financeCompanyName) {
		this.financeCompanyName = financeCompanyName;
	}

	/**
	 * @return the buyerCompanyName
	 */
	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	/**
	 * @param buyerCompanyName the buyerCompanyName to set
	 */
	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	/**
	 * @return the buyerId
	 */
	public String getBuyerId() {
		return buyerId;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	
	
	
}
