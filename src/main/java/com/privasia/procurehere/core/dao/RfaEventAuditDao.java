package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
public interface RfaEventAuditDao extends GenericDao<RfaEventAudit, String> {

	List<RfaEventAudit> getRfaEventAudit(String eventId);

	List<RfaEventAudit> getRfaEventAuditForSupplier(String eventId, String supplierId);

	/**
	 * @param actionBy
	 * @param action
	 * @param eventId TODO
	 * @return
	 */
	Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId);

}
