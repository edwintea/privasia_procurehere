package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;

/**
 * @author ravi
 */
public interface PrApprovalUserDao extends GenericDao<PrApprovalUser, String> {

	/**
	 * @return
	 */
	List<PrApprovalUser> findPrApprovelUserForNotification();

	/**
	 * @return
	 */
	List<PrApproval> findPrApprovelLevelsForNotification();
	
	/**
	 * @return
	 */
	List<PrApproval> findPrApprovelEscalationsForNotification();

	/**
	 * @param level
	 */
	void updatePrApproval(PrApproval level);

}
