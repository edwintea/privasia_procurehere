package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_TEMPLATE_EVENT_APPROVAL")
public class TemplateEventApproval extends EventApproval implements Serializable {

	private static final long serialVersionUID = -8867221756717560750L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFX_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_TMPL_APRVAL_TMPL_ID"))
	private RfxTemplate rfxTemplate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<TemplateApprovalUser> approvalUsers;

	/**
	 * @return the rfxTemplate
	 */
	public RfxTemplate getRfxTemplate() {
		return rfxTemplate;
	}

	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setRfxTemplate(RfxTemplate rfxTemplate) {
		this.rfxTemplate = rfxTemplate;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<TemplateApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<TemplateApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
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
		return "TemplateEventApproval [ " + super.toLogString() + "]";
	}

}
