package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.enums.SapDocType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Nitin Otageri
 */
public class PrErp2DeletePojo implements Serializable {

	private static final long serialVersionUID = -6261114381561962366L;

	@JsonProperty("SapRefNo")
	private String sapRefNo;

	@JsonProperty("TransactionId")
	private String transactionId;

	@JsonProperty("TransactionType")
	private String transactionType = "PR"; // RFS or PR or RFx

	@JsonProperty("ITEM")
	private List<PrItemErp2DeletePojo> prItems;

	public PrErp2DeletePojo() {

	}

	public PrErp2DeletePojo(SourcingFormRequest sourcingFormRequest) {
		this.sapRefNo = sourcingFormRequest.getErpDocNo();
		this.transactionId = sourcingFormRequest.getFormId();
		this.transactionType = SapDocType.RFS.name();
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingRequestBqs())) {
			this.prItems = new ArrayList<PrItemErp2DeletePojo>();
			int sequence = 1;
			for (SourcingFormRequestBqItem item : sourcingFormRequest.getSourcingRequestBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2DeletePojo prItemErp2Pojo = new PrItemErp2DeletePojo(item, sequence, true);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2DeletePojo(RfqEvent event, String rfsFormId, String erpDocNo) {
		this.sapRefNo = erpDocNo;
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<PrItemErp2DeletePojo>();
			int sequence = 1;
			for (RfqBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2DeletePojo prItemErp2Pojo = new PrItemErp2DeletePojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2DeletePojo(RftEvent event, String rfsFormId, String erpDocNo) {
		this.sapRefNo = erpDocNo;
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<PrItemErp2DeletePojo>();
			int sequence = 1;
			for (RftBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2DeletePojo prItemErp2Pojo = new PrItemErp2DeletePojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2DeletePojo(RfaEvent event, String rfsFormId, String erpDocNo) {
		this.sapRefNo = erpDocNo;
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<PrItemErp2DeletePojo>();
			int sequence = 1;
			for (RfaBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2DeletePojo prItemErp2Pojo = new PrItemErp2DeletePojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2DeletePojo(RfpEvent event, String rfsFormId, String erpDocNo) {
		this.sapRefNo = erpDocNo;
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<PrItemErp2DeletePojo>();
			int sequence = 1;
			for (RfpBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2DeletePojo prItemErp2Pojo = new PrItemErp2DeletePojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	/**
	 * @return the sapRefNo
	 */
	public String getSapRefNo() {
		return sapRefNo;
	}

	/**
	 * @param sapRefNo the sapRefNo to set
	 */
	public void setSapRefNo(String sapRefNo) {
		this.sapRefNo = sapRefNo;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the prItems
	 */
	public List<PrItemErp2DeletePojo> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItemErp2DeletePojo> prItems) {
		this.prItems = prItems;
	}

}
