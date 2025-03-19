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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_CQ_ITEM")
public class SourcingTemplateCqItem extends CqItem implements Serializable {

	private static final long serialVersionUID = -3886431885670707069L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FORM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SOURCING_FORM_CQ_ITEM"))
	private SourcingFormTemplate sourcingForm;
	
	@JsonIgnore
	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SOURCING_FORM_CQ"))
	private SourcingTemplateCq cq;
	
	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SOURCING_FORM_CQ_PARENT"))
	private SourcingTemplateCqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SourcingTemplateCqItem> children;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "formCqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<SourcingFormCqOption> cqOptions;
	
	@JsonIgnore
	@Transient
	private List<CqOption> displayCqOptions;

	public SourcingTemplateCqItem(SourcingTemplateCqItem cqItem) {
		this.setAttachment(cqItem.getAttachment());
		this.setCqOptions(cqItem.getCqOptions());
		this.setDisplayCqOptions(cqItem.getDisplayCqOptions());
		this.setIsSupplierAttachRequired(cqItem.getIsSupplierAttachRequired());
		this.setItemDescription(cqItem.getItemDescription());
		this.setItemName(cqItem.getItemName());
		this.setLevel(cqItem.getLevel());
		this.setOrder(cqItem.getOrder());
		this.setOptional(cqItem.getOptional());
		this.setCqType(cqItem.getCqType());
	}

	public SourcingTemplateCqItem() {

	}

	public SourcingTemplateCqItem createShallowCopy() {
		SourcingTemplateCqItem ic = new SourcingTemplateCqItem();
		ic.setItemDescription(getItemDescription());
		ic.setItemName(getItemName());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		return ic;
	}

	public SourcingFormTemplate getSourcingForm() {
		return sourcingForm;
	}

	public void setSourcingForm(SourcingFormTemplate sourcingForm) {
		this.sourcingForm = sourcingForm;
	}

	/**
	 * @return the cq
	 */
	public SourcingTemplateCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(SourcingTemplateCq cq) {
		this.cq = cq;
	}

	/**
	 * @return the parent
	 */
	public SourcingTemplateCqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SourcingTemplateCqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SourcingTemplateCqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SourcingTemplateCqItem> children) {
		if (this.children == null) {
			this.children = new ArrayList<SourcingTemplateCqItem>();
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
	public List<SourcingFormCqOption> getCqOptions() {
		return cqOptions;
	}

	/**
	 * @param cqOptions the cqOptions to set
	 */
	public void setCqOptions(List<SourcingFormCqOption> cqOptions) {
		if (this.cqOptions == null) {
			this.cqOptions = new ArrayList<SourcingFormCqOption>();
		} else {
			this.cqOptions.clear();
		}
		if (cqOptions != null) {
			this.cqOptions.addAll(cqOptions);
		}
	}

	/**
	 * @return the displayCqOptions
	 */
	public List<CqOption> getDisplayCqOptions() {
		displayCqOptions = null;
		if (getCqOptions() != null) {
			for (SourcingFormCqOption option : getCqOptions()) {
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
		return "SourcingFormCqItem [ " + super.toLogString() + "]" + " Cq Id : " + cq.getId();
	}

	public List<SourcingTemplateCqItem> copyCqItem(SourcingTemplateCq cq) {
		List<SourcingTemplateCqItem> cqList = new ArrayList<>();
		for (SourcingTemplateCqItem cqItem : cq.getCqItems()) {
			cqList.add(cqItem);
		}
		return cqList;
	}

	public SourcingTemplateCqItem copyFrom(SourcingTemplateCq newCq) {
		SourcingTemplateCqItem newCqItem = new SourcingTemplateCqItem();
		newCqItem.setItemDescription(getItemDescription());
		newCqItem.setCq(newCq);
		newCqItem.setItemName(getItemName());
		newCqItem.setLevel(getLevel());
		newCqItem.setOrder(getOrder());
		newCqItem.setCqType(getCqType());
		newCqItem.setAttachment(getAttachment());
		newCqItem.setOptional(getOptional());
		if (CollectionUtil.isNotEmpty(getCqOptions())) {
			List<SourcingFormCqOption> options = new ArrayList<SourcingFormCqOption>();
			for (SourcingFormCqOption cqOption : getCqOptions()) {
				SourcingFormCqOption newCqOption = cqOption.copyFrom(newCqItem);
				options.add(newCqOption);
			}
			newCqItem.setCqOptions(options);
		}

		return newCqItem;
	}

}
