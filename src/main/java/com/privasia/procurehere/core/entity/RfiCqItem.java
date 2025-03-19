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
@Table(name = "PROC_RFI_EVENT_CQ_ITEM")
public class RfiCqItem extends CqItem implements Serializable {

	private static final long serialVersionUID = 886692434947783973L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVENT_CQ_ITEM"))
	private RfiEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_EVNT_CQI_CQ"))
	private RfiCq cq;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVNT_CQ_PARENT"))
	private RfiCqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<RfiCqItem> children;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<RfiCqOption> cqOptions;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiCqEvaluationComments> evaluationComments;

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	@Column(name = "LEAD_EVALUATION_COMMENT", length = 1050, nullable = true)
	private String leadEvaluationComment;

	@Column(name = "TOTAL_SCORE")
	private Integer totalScore;

	@Transient
	private List<CqOption> displayCqOptions;

	public RfiCqItem copyFrom(RfiCq newCq) {
		RfiCqItem newCqItem = new RfiCqItem();
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
			List<RfiCqOption> options = new ArrayList<RfiCqOption>();
			for (RfiCqOption cqOption : getCqOptions()) {
				RfiCqOption newCqOption = cqOption.copyFrom(newCqItem);
				options.add(newCqOption);
			}
			newCqItem.setCqOptions(options);
		}

		return newCqItem;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfiEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfiEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the cq
	 */
	public RfiCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(RfiCq cq) {
		this.cq = cq;
	}

	/**
	 * @return the parent
	 */
	public RfiCqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfiCqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the cqOptions
	 */
	public List<RfiCqOption> getCqOptions() {
		return cqOptions;
	}

	/**
	 * @param cqOptions the cqOptions to set
	 */
	public void setCqOptions(List<RfiCqOption> cqOptions) {
		if (this.cqOptions == null) {
			this.cqOptions = new ArrayList<RfiCqOption>();
		} else {
			this.cqOptions.clear();
		}
		if (cqOptions != null) {
			this.cqOptions.addAll(cqOptions);
		}
	}

	/**
	 * @return the children
	 */
	public List<RfiCqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfiCqItem> children) {
		this.children = children;
	}

	/**
	 * @return the evaluationComments
	 */
	public List<RfiCqEvaluationComments> getEvaluationComments() {
		return evaluationComments;
	}

	/**
	 * @param evaluationComments the evaluationComments to set
	 */
	public void setEvaluationComments(List<RfiCqEvaluationComments> evaluationComments) {
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
	 * @return the displayCqOptions
	 */
	public List<CqOption> getDisplayCqOptions() {
		if (getCqOptions() != null) {
			for (RfiCqOption option : getCqOptions()) {
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
		return "RfiCqItem [ " + super.toLogString() + "]";
	}

	public RfiCqItem createShallowCopy() {
		RfiCqItem ic = new RfiCqItem();
		ic.setItemDescription(getItemDescription());
		ic.setItemName(getItemName());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setCqType(getCqType());
		ic.setId(getId());
		return ic;
	}
}
