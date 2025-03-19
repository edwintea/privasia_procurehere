package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventAwardAudit;

/**
 * @author Priyanka
 */
public interface RfpEventAwardAuditDao extends GenericDao<RfpEventAwardAudit, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	List<RfpEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);

	/**
	 * @param id
	 */
	void deleteDocumentsByRfpAuditId(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	RfpEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);
}
