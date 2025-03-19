/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftEventAudit;

/**
 * @author Teja
 */
public interface EventAuditService {

	/**
	 * @param audit
	 */
	void save(RfiEventAudit audit);

	/**
	 * @param audit
	 */
	void save(RfaEventAudit audit);

	/**
	 * @param audit
	 */
	void save(RfqEventAudit audit);

	/**
	 * @param audit
	 */
	void save(RfpEventAudit audit);

	/**
	 * @param audit
	 */
	void save(RftEventAudit audit);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventAudit> getRftEventAudit(String eventId);

	List<RfpEventAudit> getRfpEventAudit(String eventId);

	List<RfqEventAudit> getRfqEventAudit(String eventId);

	List<RfiEventAudit> getRfiEventAudit(String eventId);

	List<RfaEventAudit> getRfaEventAudit(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RftEventAudit getRftEventAuditById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfpEventAudit getRfpEventAuditById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfqEventAudit getRfqEventAuditById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfiEventAudit getRfiEventAuditById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfaEventAudit getRfaEventAuditById(String id);

	List<RftEventAudit> getRftEventAuditForSupplier(String eventId, String supplierId);

	List<RfaEventAudit> getRfaEventAuditForSupplier(String eventId, String supplierId);

	List<RfiEventAudit> getRfiEventAuditForSupplier(String eventId, String supplierId);

	List<RfqEventAudit> getRfqEventAuditForSupplier(String eventId, String supplierId);

	List<RfpEventAudit> getRfpEventAuditForSupplier(String eventId, String supplierId);


}
