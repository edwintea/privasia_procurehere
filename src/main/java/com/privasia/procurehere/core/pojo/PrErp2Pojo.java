package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrComment;
import com.privasia.procurehere.core.entity.PrItem;
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
public class PrErp2Pojo implements Serializable {

	private static final long serialVersionUID = -6261114381561962366L;

	@JsonProperty("PRHeaderPRType")
	private String parentBusinessUnit; // AC7 Business Unit (Parent)

	@JsonProperty("TransactionId")
	private String transactionId;

	@JsonProperty("TransactionType")
	private String transactionType = "PR"; // RFS or PR or RFx

	@JsonProperty("ITEM")
	private List<PrItemErp2Pojo> prItems;

	public PrErp2Pojo() {

	}

	public PrErp2Pojo(Pr pr) {
		this.parentBusinessUnit = (pr.getBusinessUnit().getParent() != null ? pr.getBusinessUnit().getParent().getUnitCode() : pr.getBusinessUnit().getUnitCode());
		this.transactionId = pr.getPrId();
		this.transactionType = SapDocType.PR.name();

		Date approvedDate = null;

		if (CollectionUtil.isNotEmpty(pr.getPrComments())) {
			PrComment prComment = pr.getPrComments().get(pr.getPrComments().size() - 1);
			approvedDate = prComment.getCreatedDate();
		} else {
			approvedDate = pr.getPrCreatedDate();
		}

		if (CollectionUtil.isNotEmpty(pr.getPrItems())) {
			this.prItems = new ArrayList<>();
			int sequence = 1;
			for (PrItem prItem : pr.getPrItems()) {
				if (prItem.getOrder() != 0) {
					PrItemErp2Pojo prItemErp2Pojo = new PrItemErp2Pojo(prItem, pr, approvedDate, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2Pojo(SourcingFormRequest sourcingFormRequest) {
		this.parentBusinessUnit = (sourcingFormRequest.getBusinessUnit().getParent() != null ? sourcingFormRequest.getBusinessUnit().getParent().getUnitCode() : sourcingFormRequest.getBusinessUnit().getUnitCode());
		this.transactionId = sourcingFormRequest.getFormId();
		this.transactionType = SapDocType.RFS.name();
		Date approvedDate = new Date();
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingRequestBqs())) {
			
			this.prItems = new ArrayList<>();
			int sequence = 1;
			for (SourcingFormRequestBqItem item : sourcingFormRequest.getSourcingRequestBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2Pojo prItemErp2Pojo = new PrItemErp2Pojo(item, sourcingFormRequest, approvedDate, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2Pojo(SourcingFormRequest sourcingFormRequest, boolean isDelete) {
		this.transactionId = sourcingFormRequest.getFormId();
		this.transactionType = SapDocType.RFS.name();
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingRequestBqs())) {
			this.prItems = new ArrayList<>();
			int sequence = 1;
			for (SourcingFormRequestBqItem item : sourcingFormRequest.getSourcingRequestBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2Pojo prItemErp2Pojo = new PrItemErp2Pojo(item, sequence, true);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	public PrErp2Pojo(RfqEvent event, String rfsFormId) {
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<>();
			int sequence = 1;
			for (RfqBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2Pojo prItemErp2Pojo = new PrItemErp2Pojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}
	
	public PrErp2Pojo(RftEvent event, String rfsFormId) {
		this.transactionId = rfsFormId;
		this.transactionType = SapDocType.RFQ.name();
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			this.prItems = new ArrayList<>();
			int sequence = 1;
			for (RftBqItem item : event.getEventBqs().get(0).getBqItems()) {
				if (item.getOrder() != 0) {
					PrItemErp2Pojo prItemErp2Pojo = new PrItemErp2Pojo(item, sequence);
					this.prItems.add(prItemErp2Pojo);
					sequence++;
				}
			}
		}
	}

	
	/**
	 * @return the parentBusinessUnit
	 */
	public String getParentBusinessUnit() {
		return parentBusinessUnit;
	}

	/**
	 * @param parentBusinessUnit the parentBusinessUnit to set
	 */
	public void setParentBusinessUnit(String parentBusinessUnit) {
		this.parentBusinessUnit = parentBusinessUnit;
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
	public List<PrItemErp2Pojo> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItemErp2Pojo> prItems) {
		this.prItems = prItems;
	}

}
