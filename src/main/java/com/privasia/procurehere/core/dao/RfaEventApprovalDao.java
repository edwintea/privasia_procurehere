package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventApproval;

/**
 * @author ravi
 */
public interface RfaEventApprovalDao extends GenericEventApprovalDao<RfaEventApproval, String> {

	/**
	 * @return
	 */
	List<RfaEventApproval> findRfaApprovelEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateRfaApproval(RfaEventApproval level);

}
