package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventAwardAudit;

/**
 * @author Priyanka
 */
public interface RfaEventAwardAuditDao extends GenericDao<RfaEventAwardAudit, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	List<RfaEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);


	/**
	 * @param id
	 */
	void deleteDocumentsByRfaAuditId(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	RfaEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);
}
