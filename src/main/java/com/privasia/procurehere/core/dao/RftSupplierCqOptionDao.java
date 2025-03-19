package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierCqOption;

public interface RftSupplierCqOptionDao extends GenericDao<RftSupplierCqOption, String> {

	/**
	 * @param cqId
	 * @return
	 */
	List<RftSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RftSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId);

}
