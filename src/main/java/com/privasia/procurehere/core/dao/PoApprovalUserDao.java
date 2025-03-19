/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PoApproval;
import com.privasia.procurehere.core.entity.PoApprovalUser;

/**
 * @author Jayshree
 *
 */
public interface PoApprovalUserDao extends GenericDao<PoApprovalUser, String> {

	/**
	 * @return
	 */
	List<PoApproval> findPoApprovalEscalationsForNotification();

	/**
	 * @param level
	 */
	void updatePoApproval(PoApproval level);

	/**
	 * @return
	 */
	List<PoApproval> findPoApprovalLevelsForNotification();
	
}
