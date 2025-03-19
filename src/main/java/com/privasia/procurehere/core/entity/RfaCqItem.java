/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_CQ_ITEM")
public class RfaCqItem extends CqItem implements Serializable {

	private static final long serialVersionUID = -70831506353175875L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_EVENT_CQ_ITEM"))
	private RfaEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFA_EVNT_CQI_CQ"))
	private RfaCq cq;

	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFA_EVNT_CQ_PARENT"))
	private RfaCqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<RfaCqItem> children;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaCqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<RfaCqOption> cqOptions;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaCqEvaluationComments> evaluationComments;
	
	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	@Column(name = "LEAD_EVALUATION_COMMENT", length = 1050, nullable = true)
	private String leadEvaluationComment;

	@Column(name = "TOTAL_SCORE")
	private Integer totalScore;
	

	@Transient
	private List<CqOption> displayCqOptions;
	
	public RfaCqItem copyFrom(RfaCq newCq) {
		RfaCqItem newCqItem = new RfaCqItem();
		newCqItem.setItemDescription(getItemDescription());
		newCqItem.setCq(newCq);
		newCqItem.setItemName(getItemName());
		newCqItem.setLevel(getLevel());
		newCqItem.setOrder(getOrder());
		newCqItem.setCqType(getCqType());
		newCqItem.setAttachment(getAttachment());
		newCqItem.setIsSupplierAttachRequired(getIsSupplierAttachRequired());
		newCqItem.setOptional(getOptional());
		newCqItem.setTotalScore(getTotalScore());
		if (CollectionUtil.isNotEmpty(getCqOptions())) {
			List<RfaCqOption> options = new ArrayList<RfaCqOption>();
			for (RfaCqOption cqOption : getCqOptions()) {
				RfaCqOption newCqOption = cqOption.copyFrom(newCqItem);
				options.add(newCqOption);
			}
			newCqItem.setCqOptions(options);
		}

		return newCqItem;
	}

	public RfaCqItem createShallowCopy() {
		RfaCqItem ic = new RfaCqItem();
		ic.setItemDescription(getItemDescription());
		ic.setItemName(getItemName());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setCqType(getCqType());
		ic.setId(getId());
		return ic;
	}
	/**
	 * @return the rfxEvent
	 */
	public RfaEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfaEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the cq
	 */
	public RfaCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(RfaCq cq) {
		this.cq = cq;
	}

	/**
	 * @param cqOptions the cqOptions to set
	 */
	public void setCqOptions(List<RfaCqOption> cqOptions) {
		if (this.cqOptions == null) {
			this.cqOptions = new ArrayList<RfaCqOption>();
		} else {
			this.cqOptions.clear();
		}
		if (cqOptions != null) {
			this.cqOptions.addAll(cqOptions);
		}
	}

	/**
	 * @return the parent
	 */
	public RfaCqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfaCqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<RfaCqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfaCqItem> children) {
		if (this.children == null) {
			this.children = new ArrayList<RfaCqItem>();
		} else {
			this.children.clear();
		}
		if (children != null) {
			this.children.addAll(children);
		}
	}

	/**
	 * @return the cqOptions
	 */
	public List<RfaCqOption> getCqOptions() {
		return cqOptions;
	}

	/**
	 * @return the displayCqOptions
	 */
	public List<CqOption> getDisplayCqOptions() {
		if (getCqOptions() != null) {
			for (RfaCqOption option : getCqOptions()) {
				CqOption op = new CqOption();
				op.setId(option.getId());
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				if (displayCqOptions == null)
					displayCqOptions = new ArrayList<CqOption>();
				displayCqOptions.add(op);
			}
		}
		return displayCqOptions;
	}

	/**
	 * @param displayCqOptions the displayCqOptions to set
	 */
	public void setDisplayCqOptions(List<CqOption> displayCqOptions) {

		this.displayCqOptions = displayCqOptions;
	}

	/**
	 * @return the evaluationComments
	 */
	public List<RfaCqEvaluationComments> getEvaluationComments() {
		return evaluationComments;
	}

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	public void setEvaluationComments(List<RfaCqEvaluationComments> evaluationComments) {
		this.evaluationComments = evaluationComments;
	}

	/**
	 * @return the totalScore
	 */
	public Integer getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore the totalScore to set
	 */
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return the leadEvaluationComment
	 */
	public String getLeadEvaluationComment() {
		return leadEvaluationComment;
	}

	/**
	 * @param leadEvaluationComment the leadEvaluationComment to set
	 */
	public void setLeadEvaluationComment(String leadEvaluationComment) {
		this.leadEvaluationComment = leadEvaluationComment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	public String toLogString() {
		return "RfaCqItem [ " + super.toLogString() + "]";
	}
}
