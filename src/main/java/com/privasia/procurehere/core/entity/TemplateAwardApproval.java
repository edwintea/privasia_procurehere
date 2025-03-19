/**
 * 
 */
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
 * @author Aishwarya
 *
 */
@Entity
@Table(name = "PROC_TEMP_AWARD_APPROVAL")
public class TemplateAwardApproval extends EventApproval implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601264397898216781L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFX_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_TMPL_AWARD_TMPL_ID"))
	private RfxTemplate rfxTemplate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "awardApproval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<TemplateAwardApprovalUser> approvalUsers;

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
	public List<TemplateAwardApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<TemplateAwardApprovalUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public String toLogString() {
		return "TemplateSuspensionApproval [ " + super.toLogString() + "]";
	}

}
