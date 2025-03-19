/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Aishwarya
 *
 */
@Entity
@Table(name = "PROC_TEMP_AWARD_APPR_USER")
public class TemplateAwardApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3345443945601373896L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TEMPLATE_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_TMPL_AWARD_APPROVAL_ID"))
	private TemplateAwardApproval awardApproval;

	public TemplateAwardApprovalUser() {
	}
	
	public TemplateAwardApprovalUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the suspensionApproval
	 */
	public TemplateAwardApproval getAwardApproval() {
		return awardApproval;
	}

	/**
	 * @param suspensionApproval the suspensionApproval to set
	 */
	public void setAwardApproval(TemplateAwardApproval awardApproval) {
		this.awardApproval = awardApproval;
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
		return "TemplateSuspensionApprovalUser [ " + super.toLogString() + "]";
	}

}
