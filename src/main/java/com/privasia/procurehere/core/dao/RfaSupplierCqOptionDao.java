package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierCqOption;

public interface RfaSupplierCqOptionDao extends GenericDao<RfaSupplierCqOption, String> {

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId);

}
