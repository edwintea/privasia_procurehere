/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFQ_EVENT_BQ")
public class RfqEventBq extends Bq implements Serializable {

	private static final long serialVersionUID = -8425585426647163092L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_BQ"))
	private RfqEvent rfxEvent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bq", orphanRemoval = true)
	@OrderBy("level, order")
	private List<RfqBqItem> bqItems;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bq", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqBqTotalEvaluationComments> evaluationComments;

	public RfqEventBq(SourcingFormRequestBq bq) {
		this.setName(bq.getName());
		this.setBqOrder(bq.getBqOrder());
		this.setDescription(bq.getDescription());
		this.setCreatedDate(bq.getCreatedDate());
		this.setModifiedDate(bq.getModifiedDate());
		this.setField1Label(bq.getField1Label());
		if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
			this.setField1FilledBy(BqUserTypes.BUYER);
		}
		this.setField2Label(bq.getField2Label());
		if (StringUtils.checkString(bq.getField2Label()).length() > 0) {
			this.setField2Visible(Boolean.TRUE);
			this.setField2FilledBy(BqUserTypes.BUYER);
		}
		this.setField3Label(bq.getField3Label());
		if (StringUtils.checkString(bq.getField3Label()).length() > 0) {
			this.setField3Visible(Boolean.TRUE);
			this.setField3FilledBy(BqUserTypes.BUYER);
		}
		this.setField4Label(bq.getField4Label());
		if (StringUtils.checkString(bq.getField4Label()).length() > 0) {
			this.setField4Visible(Boolean.TRUE);
			this.setField4FilledBy(BqUserTypes.BUYER);
		}
		this.setField5Label(bq.getField5Label());
		if (StringUtils.checkString(bq.getField5Label()).length() > 0) {
			this.setField5Visible(Boolean.TRUE);
			this.setField5FilledBy(BqUserTypes.BUYER);
		}
		this.setField6Label(bq.getField6Label());
		if (StringUtils.checkString(bq.getField6Label()).length() > 0) {
			this.setField6Visible(Boolean.TRUE);
			this.setField6FilledBy(BqUserTypes.BUYER);
		}
		this.setField7Label(bq.getField7Label());
		if (StringUtils.checkString(bq.getField7Label()).length() > 0) {
			this.setField7Visible(Boolean.TRUE);
			this.setField7FilledBy(BqUserTypes.BUYER);
		}
		this.setField8Label(bq.getField8Label());
		if (StringUtils.checkString(bq.getField8Label()).length() > 0) {
			this.setField8Visible(Boolean.TRUE);
			this.setField8FilledBy(BqUserTypes.BUYER);
		}
		this.setField9Label(bq.getField9Label());
		if (StringUtils.checkString(bq.getField9Label()).length() > 0) {
			this.setField9Visible(Boolean.TRUE);
			this.setField9FilledBy(BqUserTypes.BUYER);
		}
		this.setField10Label(bq.getField10Label());
		if (StringUtils.checkString(bq.getField10Label()).length() > 0) {
			this.setField10Visible(Boolean.TRUE);
			this.setField10FilledBy(BqUserTypes.BUYER);
		}
	}

	public RfqEventBq() {
		// TODO Auto-generated constructor stub
	}

	public RfqEventBq createMobileShallowCopy() {
		RfqEventBq bq = new RfqEventBq();
		bq.setId(getId());
		bq.setName(getName());
		bq.setBqOrder(getBqOrder());
		bq.setDescription(getDescription());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			bq.setBqItem(new ArrayList<>());
			for (RfqBqItem bqItem : getBqItems()) {
				bq.getBqItem().add(bqItem.createMobileShallowCopy());
			}
		}
		bq.setField1ToShowSupplier(null);
		bq.setField1Required(null);
		bq.setField2ToShowSupplier(null);
		bq.setField2Required(null);
		bq.setField2Visible(null);
		bq.setField3ToShowSupplier(null);
		bq.setField3Required(null);
		bq.setField3Visible(null);
		bq.setField4ToShowSupplier(null);
		bq.setField4Required(null);
		bq.setField4Visible(null);
		return bq;
	}

	public RfqEventBq createMobileEnvelopShallowCopy() {
		RfqEventBq bq = new RfqEventBq();
		bq.setId(getId());
		bq.setName(getName());
		bq.setBqOrder(getBqOrder());
		bq.setDescription(getDescription());
		bq.setField1ToShowSupplier(null);
		bq.setField1Required(null);
		bq.setField2ToShowSupplier(null);
		bq.setField2Required(null);
		bq.setField2Visible(null);
		bq.setField3ToShowSupplier(null);
		bq.setField3Required(null);
		bq.setField3Visible(null);
		bq.setField4ToShowSupplier(null);
		bq.setField4Required(null);
		bq.setField4Visible(null);

		bq.setField5ToShowSupplier(null);
		bq.setField5Required(null);
		bq.setField6ToShowSupplier(null);
		bq.setField6Required(null);
		bq.setField6Visible(null);
		bq.setField7ToShowSupplier(null);
		bq.setField7Required(null);
		bq.setField7Visible(null);
		bq.setField8ToShowSupplier(null);
		bq.setField8Required(null);
		bq.setField8Visible(null);
		bq.setField9ToShowSupplier(null);
		bq.setField9Required(null);
		bq.setField9Visible(null);
		bq.setField10ToShowSupplier(null);
		bq.setField10Required(null);
		bq.setField10Visible(null);
		return bq;
	}

	public RfqEventBq copyForRfq(boolean isSupplierInvited) {
		RfqEventBq newBq = new RfqEventBq();
		if (isSupplierInvited)
			newBq.setId(this.getId()); // take it for now. We will erase it later
		newBq.setName(getName());
		newBq.setBqOrder(getBqOrder());
		newBq.setDescription(getDescription());
		newBq.setCreatedDate(getCreatedDate());
		newBq.setModifiedDate(getModifiedDate());
		newBq.setField1Label(getField1Label());
		newBq.setField1FilledBy(getField1FilledBy());
		newBq.setField1ToShowSupplier(getField1ToShowSupplier());
		newBq.setField1Required(getField1Required());
		newBq.setField2Label(getField2Label());
		newBq.setField2FilledBy(getField2FilledBy());
		newBq.setField2ToShowSupplier(getField2ToShowSupplier());
		newBq.setField2Required(getField2Required());
		newBq.setField2Visible(getField2Visible());
		newBq.setField3Label(getField3Label());
		newBq.setField3FilledBy(getField3FilledBy());
		newBq.setField3ToShowSupplier(getField3ToShowSupplier());
		newBq.setField3Required(getField3Required());
		newBq.setField3Visible(getField3Visible());
		newBq.setField4Label(getField4Label());
		newBq.setField4FilledBy(getField4FilledBy());
		newBq.setField4ToShowSupplier(getField4ToShowSupplier());
		newBq.setField4Required(getField4Required());
		newBq.setField4Visible(getField4Visible());

		newBq.setField5Label(getField5Label());
		newBq.setField5FilledBy(getField5FilledBy());
		newBq.setField5ToShowSupplier(getField5ToShowSupplier());
		newBq.setField5Required(getField5Required());
		newBq.setField6Label(getField6Label());
		newBq.setField6FilledBy(getField6FilledBy());
		newBq.setField6ToShowSupplier(getField6ToShowSupplier());
		newBq.setField6Required(getField6Required());
		newBq.setField6Visible(getField6Visible());
		newBq.setField7Label(getField7Label());
		newBq.setField7FilledBy(getField7FilledBy());
		newBq.setField7ToShowSupplier(getField7ToShowSupplier());
		newBq.setField7Required(getField7Required());
		newBq.setField7Visible(getField7Visible());
		newBq.setField8Label(getField8Label());
		newBq.setField8FilledBy(getField8FilledBy());
		newBq.setField8ToShowSupplier(getField8ToShowSupplier());
		newBq.setField8Required(getField8Required());
		newBq.setField8Visible(getField8Visible());
		newBq.setField9Label(getField7Label());
		newBq.setField9FilledBy(getField9FilledBy());
		newBq.setField9ToShowSupplier(getField9ToShowSupplier());
		newBq.setField9Required(getField9Required());
		newBq.setField9Visible(getField9Visible());
		newBq.setField10Label(getField10Label());
		newBq.setField10FilledBy(getField10FilledBy());
		newBq.setField10ToShowSupplier(getField10ToShowSupplier());
		newBq.setField10Required(getField10Required());
		newBq.setField10Visible(getField10Visible());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			newBq.setBqItems(new ArrayList<RfqBqItem>());
			for (RfqBqItem bqItem : getBqItems()) {
				RfqBqItem newBqItem = bqItem.copyForRfq(newBq);
				if (isSupplierInvited)
					newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
				newBq.getBqItems().add(newBqItem);
			}
		}
		return newBq;
	}

	public RftEventBq copyForRft(boolean invitedSuppliersOnly) {
		RftEventBq newBq = new RftEventBq();
		if (invitedSuppliersOnly)
			newBq.setId(this.getId()); // take it for now. We will erase it later
		newBq.setName(getName());
		newBq.setBqOrder(getBqOrder());
		newBq.setDescription(getDescription());
		newBq.setCreatedDate(getCreatedDate());
		newBq.setModifiedDate(getModifiedDate());
		newBq.setField1Label(getField1Label());
		newBq.setField1FilledBy(getField1FilledBy());
		newBq.setField1ToShowSupplier(getField1ToShowSupplier());
		newBq.setField1Required(getField1Required());
		newBq.setField2Label(getField2Label());
		newBq.setField2FilledBy(getField2FilledBy());
		newBq.setField2ToShowSupplier(getField2ToShowSupplier());
		newBq.setField2Required(getField2Required());
		newBq.setField2Visible(getField2Visible());
		newBq.setField3Label(getField3Label());
		newBq.setField3FilledBy(getField3FilledBy());
		newBq.setField3ToShowSupplier(getField3ToShowSupplier());
		newBq.setField3Required(getField3Required());
		newBq.setField3Visible(getField3Visible());
		newBq.setField4Label(getField4Label());
		newBq.setField4FilledBy(getField4FilledBy());
		newBq.setField4ToShowSupplier(getField4ToShowSupplier());
		newBq.setField4Required(getField4Required());
		newBq.setField4Visible(getField4Visible());

		newBq.setField5Label(getField5Label());
		newBq.setField5FilledBy(getField5FilledBy());
		newBq.setField5ToShowSupplier(getField5ToShowSupplier());
		newBq.setField5Required(getField5Required());
		newBq.setField6Label(getField6Label());
		newBq.setField6FilledBy(getField6FilledBy());
		newBq.setField6ToShowSupplier(getField6ToShowSupplier());
		newBq.setField6Required(getField6Required());
		newBq.setField6Visible(getField6Visible());
		newBq.setField7Label(getField7Label());
		newBq.setField7FilledBy(getField7FilledBy());
		newBq.setField7ToShowSupplier(getField7ToShowSupplier());
		newBq.setField7Required(getField7Required());
		newBq.setField7Visible(getField7Visible());
		newBq.setField8Label(getField8Label());
		newBq.setField8FilledBy(getField8FilledBy());
		newBq.setField8ToShowSupplier(getField8ToShowSupplier());
		newBq.setField8Required(getField8Required());
		newBq.setField8Visible(getField8Visible());
		newBq.setField9Label(getField7Label());
		newBq.setField9FilledBy(getField9FilledBy());
		newBq.setField9ToShowSupplier(getField9ToShowSupplier());
		newBq.setField9Required(getField9Required());
		newBq.setField9Visible(getField9Visible());
		newBq.setField10Label(getField10Label());
		newBq.setField10FilledBy(getField10FilledBy());
		newBq.setField10ToShowSupplier(getField10ToShowSupplier());
		newBq.setField10Required(getField10Required());
		newBq.setField10Visible(getField10Visible());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			newBq.setBqItems(new ArrayList<RftBqItem>());
			for (RfqBqItem bqItem : getBqItems()) {
				RftBqItem newBqItem = bqItem.copyForRft(newBq);
				if (invitedSuppliersOnly)
					newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
				newBq.getBqItems().add(newBqItem);
			}
		}
		return newBq;
	}

	public RfpEventBq copyForRfp(boolean invitedSuppliersOnly) {
		RfpEventBq newBq = new RfpEventBq();
		if (invitedSuppliersOnly)
			newBq.setId(this.getId()); // take it for now. We will erase it later
		newBq.setName(getName());
		newBq.setBqOrder(getBqOrder());
		newBq.setDescription(getDescription());
		newBq.setCreatedDate(getCreatedDate());
		newBq.setModifiedDate(getModifiedDate());
		newBq.setField1Label(getField1Label());
		newBq.setField1FilledBy(getField1FilledBy());
		newBq.setField1ToShowSupplier(getField1ToShowSupplier());
		newBq.setField1Required(getField1Required());
		newBq.setField2Label(getField2Label());
		newBq.setField2FilledBy(getField2FilledBy());
		newBq.setField2ToShowSupplier(getField2ToShowSupplier());
		newBq.setField2Required(getField2Required());
		newBq.setField2Visible(getField2Visible());
		newBq.setField3Label(getField3Label());
		newBq.setField3FilledBy(getField3FilledBy());
		newBq.setField3ToShowSupplier(getField3ToShowSupplier());
		newBq.setField3Required(getField3Required());
		newBq.setField3Visible(getField3Visible());
		newBq.setField4Label(getField4Label());
		newBq.setField4FilledBy(getField4FilledBy());
		newBq.setField4ToShowSupplier(getField4ToShowSupplier());
		newBq.setField4Required(getField4Required());
		newBq.setField4Visible(getField4Visible());

		newBq.setField5Label(getField5Label());
		newBq.setField5FilledBy(getField5FilledBy());
		newBq.setField5ToShowSupplier(getField5ToShowSupplier());
		newBq.setField5Required(getField5Required());
		newBq.setField6Label(getField6Label());
		newBq.setField6FilledBy(getField6FilledBy());
		newBq.setField6ToShowSupplier(getField6ToShowSupplier());
		newBq.setField6Required(getField6Required());
		newBq.setField6Visible(getField6Visible());
		newBq.setField7Label(getField7Label());
		newBq.setField7FilledBy(getField7FilledBy());
		newBq.setField7ToShowSupplier(getField7ToShowSupplier());
		newBq.setField7Required(getField7Required());
		newBq.setField7Visible(getField7Visible());
		newBq.setField8Label(getField8Label());
		newBq.setField8FilledBy(getField8FilledBy());
		newBq.setField8ToShowSupplier(getField8ToShowSupplier());
		newBq.setField8Required(getField8Required());
		newBq.setField8Visible(getField8Visible());
		newBq.setField9Label(getField7Label());
		newBq.setField9FilledBy(getField9FilledBy());
		newBq.setField9ToShowSupplier(getField9ToShowSupplier());
		newBq.setField9Required(getField9Required());
		newBq.setField9Visible(getField9Visible());
		newBq.setField10Label(getField10Label());
		newBq.setField10FilledBy(getField10FilledBy());
		newBq.setField10ToShowSupplier(getField10ToShowSupplier());
		newBq.setField10Required(getField10Required());
		newBq.setField10Visible(getField10Visible());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			newBq.setBqItems(new ArrayList<RfpBqItem>());
			for (RfqBqItem bqItem : getBqItems()) {
				RfpBqItem newBqItem = bqItem.copyForRfp(newBq);
				if (invitedSuppliersOnly)
					newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
				newBq.getBqItems().add(newBqItem);
			}
		}
		return newBq;
	}

	public RfaEventBq copyForRfa(boolean invitedSuppliersOnly) {
		RfaEventBq newBq = new RfaEventBq();
		if (invitedSuppliersOnly)
			newBq.setId(this.getId()); // take it for now. We will erase it later
		newBq.setName(getName());
		newBq.setBqOrder(1);
		newBq.setDescription(getDescription());
		newBq.setCreatedDate(getCreatedDate());
		newBq.setModifiedDate(getModifiedDate());
		newBq.setField1Label(getField1Label());
		newBq.setField1FilledBy(getField1FilledBy());
		newBq.setField1ToShowSupplier(getField1ToShowSupplier());
		newBq.setField1Required(getField1Required());
		newBq.setField2Label(getField2Label());
		newBq.setField2FilledBy(getField2FilledBy());
		newBq.setField2ToShowSupplier(getField2ToShowSupplier());
		newBq.setField2Required(getField2Required());
		newBq.setField2Visible(getField2Visible());
		newBq.setField3Label(getField3Label());
		newBq.setField3FilledBy(getField3FilledBy());
		newBq.setField3ToShowSupplier(getField3ToShowSupplier());
		newBq.setField3Required(getField3Required());
		newBq.setField3Visible(getField3Visible());
		newBq.setField4Label(getField4Label());
		newBq.setField4FilledBy(getField4FilledBy());
		newBq.setField4ToShowSupplier(getField4ToShowSupplier());
		newBq.setField4Required(getField4Required());
		newBq.setField4Visible(getField4Visible());

		newBq.setField5Label(getField5Label());
		newBq.setField5FilledBy(getField5FilledBy());
		newBq.setField5ToShowSupplier(getField5ToShowSupplier());
		newBq.setField5Required(getField5Required());
		newBq.setField6Label(getField6Label());
		newBq.setField6FilledBy(getField6FilledBy());
		newBq.setField6ToShowSupplier(getField6ToShowSupplier());
		newBq.setField6Required(getField6Required());
		newBq.setField6Visible(getField6Visible());
		newBq.setField7Label(getField7Label());
		newBq.setField7FilledBy(getField7FilledBy());
		newBq.setField7ToShowSupplier(getField7ToShowSupplier());
		newBq.setField7Required(getField7Required());
		newBq.setField7Visible(getField7Visible());
		newBq.setField8Label(getField8Label());
		newBq.setField8FilledBy(getField8FilledBy());
		newBq.setField8ToShowSupplier(getField8ToShowSupplier());
		newBq.setField8Required(getField8Required());
		newBq.setField8Visible(getField8Visible());
		newBq.setField9Label(getField7Label());
		newBq.setField9FilledBy(getField9FilledBy());
		newBq.setField9ToShowSupplier(getField9ToShowSupplier());
		newBq.setField9Required(getField9Required());
		newBq.setField9Visible(getField9Visible());
		newBq.setField10Label(getField10Label());
		newBq.setField10FilledBy(getField10FilledBy());
		newBq.setField10ToShowSupplier(getField10ToShowSupplier());
		newBq.setField10Required(getField10Required());
		newBq.setField10Visible(getField10Visible());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			newBq.setBqItems(new ArrayList<RfaBqItem>());
			for (RfqBqItem bqItem : getBqItems()) {
				RfaBqItem newBqItem = bqItem.copyForRfa(newBq);
				if (invitedSuppliersOnly)
					newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
				newBq.getBqItems().add(newBqItem);
			}
		}
		return newBq;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfqEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfqEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bqItems
	 */
	public List<RfqBqItem> getBqItems() {
		return bqItems;
	}

	/**
	 * @param bqItems the bqItems to set
	 */
	public void setBqItems(List<RfqBqItem> bqItems) {
		if (this.bqItems == null) {
			this.bqItems = new ArrayList<RfqBqItem>();
		} else {
			this.bqItems.clear();
		}
		if (bqItems != null) {
			this.bqItems.addAll(bqItems);
		}
	}

	/**
	 * @return the evaluationComments
	 */
	public List<RfqBqTotalEvaluationComments> getEvaluationComments() {
		return evaluationComments;
	}

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	public void setEvaluationComments(List<RfqBqTotalEvaluationComments> evaluationComments) {
		this.evaluationComments = evaluationComments;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "RfqEventBillOfQuantity [ " + super.toLogString() + "]";
	}

}
