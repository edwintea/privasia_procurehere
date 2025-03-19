package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventAwardAudit;

/**
 * @author priyanka
 */
public interface RftEventAwardAuditDao extends GenericDao<RftEventAwardAudit, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	List<RftEventAwardAudit> findAllAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);

	/**
	 * @param id
	 */
	void deleteDocumentsByRftAuditId(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	RftEventAwardAudit findLatestAwardAuditForTenantIdAndEventId(String loggedInUserTenantId, String eventId);
}
