/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_RFA_EVALUATION_CQ_COMTS")
public class RfaCqEvaluationComments extends Comments implements Serializable {

	private static final long serialVersionUID = -7943705511201477025L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_CQ_EVALU_EVENT"))
	private RfaEvent event;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_CQ_EVALU_CQITEM"))
	private RfaCqItem cqItem;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_CQ_EVALU_SUPP"))
	private Supplier supplier;

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
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the cqItem
	 */
	public RfaCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(RfaCqItem cqItem) {
		this.cqItem = cqItem;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public RfaCqEvaluationComments createShallowCopy() {
		RfaCqEvaluationComments ic = new RfaCqEvaluationComments();
		ic.setId(getId());
		ic.setComment(getComment());
		ic.setUserName(getUserName());
		ic.setLoginName(getLoginName());
		ic.setCreatedDate(getCreatedDate());
		ic.setFlagForCommentOwner(getFlagForCommentOwner());
		return ic;
	}

}
