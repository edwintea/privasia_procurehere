package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;

/**
 * @author ravi
 */
public interface SourcingFormApprovalUserRequestDao extends GenericDao<SourcingFormApprovalUserRequest, String> {

	List<SourcingFormApprovalUserRequest> findRfsApprovelUserForNotification();

	/**
	 * @return
	 */
	List<SourcingFormApprovalRequest> findRfsApprovelLevelsForNotification();

	/**
	 * @return
	 */
	List<SourcingFormApprovalRequest> findRfsApprovelEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateSourcingFormApproval(SourcingFormApprovalRequest level);
}
