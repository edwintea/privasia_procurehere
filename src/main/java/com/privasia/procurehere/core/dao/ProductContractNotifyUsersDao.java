package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;

public interface ProductContractNotifyUsersDao extends GenericDao<ProductContractNotifyUsers, String> {

	/**
	 * @param contractId
	 * @return
	 */
	List<ProductContractNotifyUsers> getAllContractNotifyUsersByContractId(String contractId);

}
