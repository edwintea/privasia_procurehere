package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventAwardAudit;

/**
 * @author Priyanka
 */
public interface RfqEventAwardAuditDao extends GenericDao<RfqEventAwardAudit, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	List<RfqEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);

	/**
	 * @param id
	 */
	void deleteDocumentsByRfqAuditId(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	RfqEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);
}
