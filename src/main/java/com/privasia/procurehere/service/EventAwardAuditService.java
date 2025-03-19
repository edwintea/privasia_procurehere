/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.enums.RfxTypes;

/**
 * @author Priyanka Ghadage
 */
public interface EventAwardAuditService {

	/**
	 * @param audit
	 */
	void saveRftAwardAudit(RftEventAwardAudit audit);

	RftEventAwardAudit findByRftAuditId(String id);

	List<RftEventAwardAudit> findAllAwardAuditForTenantIdAndRftEventId(String loggedInUserTenantId, String eventId);

	void saveRfqAwardAudit(RfqEventAwardAudit audit);

	List<RfqEventAwardAudit> findAllAwardAuditForTenantIdAndRfqEventId(String loggedInUserTenantId, String eventId);

	RfqEventAwardAudit findByRfqAuditId(String id);

	void saveRfpAwardAudit(RfpEventAwardAudit audit);

	List<RfpEventAwardAudit> findAllAwardAuditForTenantIdAndRfpEventId(String loggedInUserTenantId, String eventId);

	RfpEventAwardAudit findByRfpAuditId(String id);

	void saveRfaAwardAudit(RfaEventAwardAudit audit);

	List<RfaEventAwardAudit> findAllAwardAuditForTenantIdAndRfaEventId(String loggedInUserTenantId, String eventId);

	RfaEventAwardAudit findByRfaAuditId(String id);

	void deleteDocumentsByRfxAuditId(String id, RfxTypes rfxTypes);
}
