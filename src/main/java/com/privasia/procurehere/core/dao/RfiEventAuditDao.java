package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
public interface RfiEventAuditDao extends GenericDao<RfiEventAudit, String> {

	List<RfiEventAudit> getRfiEventAudit(String eventId);

	List<RfiEventAudit> getRfiEventAuditForSupplier(String eventId, String supplierId);

	/**
	 * @param actionBy
	 * @param action
	 * @param eventId
	 * @return
	 */
	Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId);

}
