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
 * @author jayshree
 *
 */
@Entity
@Table(name = "PROC_TEMP_SUSPENSION_APPROVAL")
public class TemplateSuspensionApproval extends EventApproval implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601264397898216781L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFX_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_TMPL_SUSPENSION_TMPL_ID"))
	private RfxTemplate rfxTemplate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "suspensionApproval", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<TemplateSuspensionApprovalUser> approvalUsers;

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
	public List<TemplateSuspensionApprovalUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<TemplateSuspensionApprovalUser> approvalUsers) {
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
