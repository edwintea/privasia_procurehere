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
import com.privasia.procurehere.core.enums.ModuleType;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_SUPPLIER_AUDIT_TRAIL", indexes = { @Index(name = "IDX_SUPPLIER_AUDIT", columnList = "TENANT_ID") })
public class SupplierAuditTrail extends AuditTrail implements Serializable {

	private static final long serialVersionUID = -8556119686751944129L;

	public SupplierAuditTrail() {
	}

	public SupplierAuditTrail(AuditTypes activity, String desc, String tenantId, User actionBy, Date date,ModuleType moduleType) {
		this.setActivity(activity);
		this.setDescription(desc);
		this.setTenantId(tenantId);
		this.setActionBy(actionBy);
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
		return "SupplierAuditTrail [" + super.toLogString() + "]";
	}

}
