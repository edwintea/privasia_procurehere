package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierCqOption;

public interface RfpSupplierCqOptionDao extends GenericDao<RfpSupplierCqOption, String> {

	/**
	 * @param cqId
	 * @return
	 */
	List<RfpSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId);

	/**
	 * 
	 * @param cqId
	 * @return
	 */
	List<RfpSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId);

}
