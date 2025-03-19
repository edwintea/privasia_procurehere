package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierCqOption;

public interface RfiSupplierCqOptionDao extends GenericDao<RfiSupplierCqOption, String> {

	/**
	 * @param cqId
	 * @return
	 */
	List<RfiSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId);

	/**
	 * 
	 * @param cqId
	 * @return
	 */
	List<RfiSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId);

}
