package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;

/**
 * @author Teja
 */
public interface RfpEventAuditDao extends GenericDao<RfpEventAudit, String> {

	List<RfpEventAudit> getRfpEventAudit(String eventId);

	List<RfpEventAudit> getRfpEventAuditForSupplier(String eventId, String supplierId);

	/**
	 * 
	 * @param actionBy
	 * @param action
	 * @param eventId
	 * @return
	 */
	Date findUnMaskigDateForActionByAndAction(String actionBy, AuditActionType action, String eventId);

}
