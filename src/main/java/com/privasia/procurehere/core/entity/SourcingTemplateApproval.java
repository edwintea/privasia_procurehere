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

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_APP")
public class SourcingTemplateApproval extends EventApproval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5824992894434278951L;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_SF_APPROVAL_ID"))
	private SourcingFormTemplate sourcingForm;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "approval", orphanRemoval = true, cascade = { CascadeType.ALL })
	private List<SourcingFormApprovalUser> approvalUsers;

	/**
	 * @return the sourcingForm
	 */
	public SourcingFormTemplate getSourcingForm() {
		return sourcingForm;
	}

	/**
	 * @param sourcingForm the sourcingForm to set
	 */
	public void setSourcingForm(SourcingFormTemplate sourcingForm) {
		this.sourcingForm = sourcingForm;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<SourcingFormApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<SourcingFormApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

}
