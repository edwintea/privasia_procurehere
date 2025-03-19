package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventApproval;

/**
 * @author ravi
 */
public interface RfpEventApprovalDao extends GenericEventApprovalDao<RfpEventApproval, String> {

	/**
	 * @return
	 */
	List<RfpEventApproval> findRfpApprovelEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateRfpApproval(RfpEventApproval level);

}
