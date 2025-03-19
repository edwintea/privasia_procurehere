/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.privasia.procurehere.core.enums.AuditTypes;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_FINANCE_AUDIT_TRAIL", indexes = { @Index(name = "IDX_FINANCE_AUDIT", columnList = "TENANT_ID") })
public class FinanceAuditTrail extends AuditTrail implements Serializable {

	

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6057101439539277722L;

	public FinanceAuditTrail() {

	}

	public FinanceAuditTrail(AuditTypes activity, String desc, String tenantId, User createdBy, Date date) {
		this.setActivity(activity);
		this.setDescription(desc);
		this.setTenantId(tenantId);
		this.setActionBy(createdBy);
		this.setActionDate(date);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FinanceAuditTrail [hashCode()=" + hashCode() + "]";
	}

	

}
