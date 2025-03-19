package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventApproval;

/**
 * @author ravi
 */
public interface RfiEventApprovalDao extends GenericEventApprovalDao<RfiEventApproval, String> {

	/**
	 * @return
	 */
	List<RfiEventApproval> findRfiApprovelEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateRfiApproval(RfiEventApproval level);
 
}
