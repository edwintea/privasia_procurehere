package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ErpAudit;
import com.privasia.procurehere.core.enums.ErpAuditType;

/**
 * @author parveen
 */
public interface ErpAuditService {
	/**
	 * @param erpAudit
	 * @return
	 */
	ErpAudit save(ErpAudit erpAudit);

	/**
	 * @param tenantId
	 * @param type
	 * @return
	 */
	List<ErpAudit> getAllAuditByTenantIdAndActionType(String tenantId, ErpAuditType type);

	/**
	 * @param auditId
	 * @return
	 */
	ErpAudit findById(String auditId);

	/**
	 * @param prNo
	 * @param tenantId
	 * @return
	 */
	boolean isExists(String prNo, String tenantId);

}
