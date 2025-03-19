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
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFT_EVAL_BQ_TOTAL_COMTS")
public class RftBqTotalEvaluationComments extends Comments implements Serializable {

	private static final long serialVersionUID = 7896422428536904860L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_BQ_TOT_EVALU_EVENT"))
	private RftEvent event;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVALU_BQ_TOT"))
	private RftEventBq bq;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVALU_BQ_TOT_SUPP"))
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
	 * @return the bq
	 */
	public RftEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RftEventBq bq) {
		this.bq = bq;
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

	public RftBqTotalEvaluationComments createShallowCopy() {
		RftBqTotalEvaluationComments ic = new RftBqTotalEvaluationComments();
		ic.setId(getId());
		ic.setComment(getComment());
		ic.setUserName(getUserName());
		ic.setLoginName(getLoginName());
		ic.setCreatedDate(getCreatedDate());
		ic.setFlagForCommentOwner(getFlagForCommentOwner());
		return ic;
	}

}
