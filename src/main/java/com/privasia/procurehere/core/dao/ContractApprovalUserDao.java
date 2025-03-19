package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;

/**
 * @author anshul
 */
public interface ContractApprovalUserDao extends GenericDao<ContractApprovalUser, String> {

	/**
	 * @return
	 */
	List<ContractApprovalUser> findContractApprovelUserForNotification();

	/**
	 * @return
	 */
	List<ContractApproval> findContractApprovelLevelsForNotification();

	/**
	 * @param level
	 */
	void updateContractApproval(ContractApproval level);

}
