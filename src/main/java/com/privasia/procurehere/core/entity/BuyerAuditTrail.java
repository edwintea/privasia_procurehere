/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PoAuditType;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_BUYER_AUDIT_TRAIL", indexes = { @Index(name = "IDX_BUYER_AUDIT", columnList = "TENANT_ID") })
public class BuyerAuditTrail extends AuditTrail implements Serializable {

	private static final long serialVersionUID = -8556119686751944129L;

	public BuyerAuditTrail() {

	}

	public BuyerAuditTrail(AuditTypes activity, String desc, String tenantId, User createdBy, Date date, ModuleType moduleType) {
		this.setActivity(activity);
		this.setDescription(desc);
		this.setTenantId(tenantId);
		this.setActionBy(createdBy);
		this.setActionDate(date);
		this.setModuleType(moduleType);
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BuyerAuditTrail [" + super.toLogString() + "]";
	}

}
