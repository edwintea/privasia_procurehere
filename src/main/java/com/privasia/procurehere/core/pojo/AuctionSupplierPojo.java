/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author user
 */
public class AuctionSupplierPojo implements Serializable {

	private static final long serialVersionUID = -474287386475311575L;

	private String id;

	private String supplierId;

	private String supplierCompanyName;

	private List<AuctionSupplierBidPojo> auctionSupplierBids;

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
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	/**
	 * @return the auctionSupplierBids
	 */
	public List<AuctionSupplierBidPojo> getAuctionSupplierBids() {
		return auctionSupplierBids;
	}

	/**
	 * @param auctionSupplierBids the auctionSupplierBids to set
	 */
	public void setAuctionSupplierBids(List<AuctionSupplierBidPojo> auctionSupplierBids) {
		this.auctionSupplierBids = auctionSupplierBids;
	}

}
