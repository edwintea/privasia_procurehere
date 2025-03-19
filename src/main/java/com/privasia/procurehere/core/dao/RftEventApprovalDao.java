package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventApproval;

/**
 * @author ravi
 */
public interface RftEventApprovalDao extends GenericEventApprovalDao<RftEventApproval, String> {

	/**
	 * @return
	 */
	List<RftEventApproval> findRftApprovalEscalationsForNotification();

	/**
	 * @param level
	 */
	void updateRftApproval(RftEventApproval level);

}
