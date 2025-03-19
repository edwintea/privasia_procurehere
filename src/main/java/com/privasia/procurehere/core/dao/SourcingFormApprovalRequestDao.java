package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;

public interface SourcingFormApprovalRequestDao extends GenericDao<SourcingFormApprovalRequest, String> {

	/**
	 * @param reqId
	 * @return
	 */
	SourcingFormApprovalRequest getSourcingFormActiveApproverById(String reqId);

	/**
	 * @param id
	 * @return
	 */
	SourcingFormApprovalRequest findSourcingFormApprovalById(String id);

}
