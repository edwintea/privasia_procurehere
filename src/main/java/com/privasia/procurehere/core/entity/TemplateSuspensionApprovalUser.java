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
 * @author jayshree
 *
 */
@Entity
@Table(name = "PROC_TEMP_SUSPENSION_APPR_USER")
public class TemplateSuspensionApprovalUser extends ApprovalUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3345443945601373896L;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TEMPLATE_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_TMPL_SUSPENSION_APPROVAL_ID"))
	private TemplateSuspensionApproval suspensionApproval;

	public TemplateSuspensionApprovalUser() {
	}
	
	public TemplateSuspensionApprovalUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	/**
	 * @return the suspensionApproval
	 */
	public TemplateSuspensionApproval getSuspensionApproval() {
		return suspensionApproval;
	}

	/**
	 * @param suspensionApproval the suspensionApproval to set
	 */
	public void setSuspensionApproval(TemplateSuspensionApproval suspensionApproval) {
		this.suspensionApproval = suspensionApproval;
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
