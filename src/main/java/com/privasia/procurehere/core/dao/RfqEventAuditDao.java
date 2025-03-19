package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
public interface RfqEventAuditDao extends GenericDao<RfqEventAudit, String> {

	List<RfqEventAudit> getRfqEventAudit(String eventId);

	List<RfqEventAudit> getRfqEventAuditForSupplier(String eventId, String supplierId);

	/**
	 * @param actionBy
	 * @param action
	 * @param eventId
	 * @return
	 */
	Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId);

}
