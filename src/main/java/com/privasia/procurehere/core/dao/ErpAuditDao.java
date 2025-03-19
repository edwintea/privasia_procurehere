package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ErpAudit;
import com.privasia.procurehere.core.enums.ErpAuditType;

/**
 * @author parveen
 */
public interface ErpAuditDao extends GenericDao<ErpAudit, String> {
	/**
	 * @param tenantId
	 * @param type
	 * @return
	 */
	List<ErpAudit> getAllAuditByTenantIdAndActionType(String tenantId, ErpAuditType type);

	/**
	 * @param prNo
	 * @param tenantId
	 * @return
	 */
	boolean isExists(String prNo, String tenantId);

}
