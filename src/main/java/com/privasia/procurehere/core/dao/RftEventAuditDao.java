package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
public interface RftEventAuditDao extends GenericDao<RftEventAudit, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventAudit> getRftEventAudit(String eventId);

	List<RftEventAudit> getRftEventAuditForSupplier(String eventId, String supplierId);

	/**
	 * @param actionBy
	 * @param action
	 * @param eventId
	 * @return
	 */
	Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId);

}
