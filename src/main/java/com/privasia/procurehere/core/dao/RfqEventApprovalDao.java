package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventApproval;

/**
 * @author ravi
 */
public interface RfqEventApprovalDao extends GenericEventApprovalDao<RfqEventApproval, String> {

	/**
	 * @return
	 */
	List<RfqEventApproval> findRfqApprovalEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateRfqApproval(RfqEventApproval level);

}
