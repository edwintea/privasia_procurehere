/**
 * 
 */
package com.privasia.procurehere.core.entity;

import org.hibernate.annotations.GenericGenerator;

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
import java.io.Serializable;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFT_EVALUATION_SOR_COMTS")
public class RftSorEvaluationComments extends Comments implements Serializable {

	private static final long serialVersionUID = 7896422428536904850L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SOR_RFT_EVALU_EVENT"))
	private RftEvent event;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SOR_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVALU_SORITEM"))
	private RftSorItem bqItem;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVALU_SOR_SUPP"))
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
	public RftEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RftEvent event) {
		this.event = event;
	}

	/**
	 * @return the bqItem
	 */
	public RftSorItem getBqItem() {
		return bqItem;
	}

	/**
	 * @param bqItem the bqItem to set
	 */
	public void setBqItem(RftSorItem bqItem) {
		this.bqItem = bqItem;
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

	public RftSorEvaluationComments createShallowCopy() {
		RftSorEvaluationComments ic = new RftSorEvaluationComments();
		ic.setId(getId());
		ic.setComment(getComment());
		ic.setUserName(getUserName());
		ic.setLoginName(getLoginName());
		ic.setCreatedDate(getCreatedDate());
		ic.setFlagForCommentOwner(getFlagForCommentOwner());
		return ic;
	}

}
