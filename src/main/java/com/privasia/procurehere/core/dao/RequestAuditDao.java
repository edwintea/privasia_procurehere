package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RequestAudit;

/**
 * @author sarang
 */
public interface RequestAuditDao extends GenericDao<RequestAudit, String> {
	/**
	 * @param prId
	 * @return
	 */
	List<RequestAudit> RequestAuditById(String prId);

}
