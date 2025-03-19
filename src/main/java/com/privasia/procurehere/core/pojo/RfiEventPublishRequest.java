/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author pooja
 */
public class RfiEventPublishRequest extends EventPublishRequest implements Serializable {

	private static final long serialVersionUID = 6762967565906093732L;

	@JsonProperty("TarikhTerbitdiPortal")
	private String eventStartDate;

	@JsonProperty("TarikhTutupdiPortal")
	private String eventEndDate;

	@JsonProperty("TarikhTenderDibuka")
	private String tenderStartDate;

	@JsonProperty("TarikhTenderDitutup")
	private String tenderEndDate;

	@JsonProperty("TenderDeposit")
	private String tenderDeposit;

	@JsonProperty("TarikhdanMasaPenjualanDokumenTen")
	private String feeStartDate;

	@JsonProperty("JualanDokumenTenderTamat")
	private String feeEndDate;

	@JsonProperty("SaleReady")
	private String saleReady;

	/**
	 * @return the eventStartDate
	 */
	public String getEventStartDate() {
		return eventStartDate;
	}

	/**
	 * @param eventStartDate the eventStartDate to set
	 */
	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	/**
	 * @return the eventEndDate
	 */
	public String getEventEndDate() {
		return eventEndDate;
	}

	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(String eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	/**
	 * @return the tenderStartDate
	 */
	public String getTenderStartDate() {
		return tenderStartDate;
	}

	/**
	 * @param tenderStartDate the tenderStartDate to set
	 */
	public void setTenderStartDate(String tenderStartDate) {
		this.tenderStartDate = tenderStartDate;
	}

	/**
	 * @return the tenderEndDate
	 */
	public String getTenderEndDate() {
		return tenderEndDate;
	}

	/**
	 * @param tenderEndDate the tenderEndDate to set
	 */
	public void setTenderEndDate(String tenderEndDate) {
		this.tenderEndDate = tenderEndDate;
	}

	/**
	 * @return the tenderDeposit
	 */
	public String getTenderDeposit() {
		return tenderDeposit;
	}

	/**
	 * @param tenderDeposit the tenderDeposit to set
	 */
	public void setTenderDeposit(String tenderDeposit) {
		this.tenderDeposit = tenderDeposit;
	}

	/**
	 * @return the feeStartDate
	 */
	public String getFeeStartDate() {
		return feeStartDate;
	}

	/**
	 * @param feeStartDate the feeStartDate to set
	 */
	public void setFeeStartDate(String feeStartDate) {
		this.feeStartDate = feeStartDate;
	}

	/**
	 * @return the feeEndDate
	 */
	public String getFeeEndDate() {
		return feeEndDate;
	}

	/**
	 * @param feeEndDate the feeEndDate to set
	 */
	public void setFeeEndDate(String feeEndDate) {
		this.feeEndDate = feeEndDate;
	}

	/**
	 * @return the saleReady
	 */
	public String getSaleReady() {
		return saleReady;
	}

	/**
	 * @param saleReady the saleReady to set
	 */
	public void setSaleReady(String saleReady) {
		this.saleReady = saleReady;
	}

}