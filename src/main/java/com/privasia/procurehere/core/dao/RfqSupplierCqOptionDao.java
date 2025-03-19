package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierCqOption;

public interface RfqSupplierCqOptionDao extends GenericDao<RfqSupplierCqOption, String> {

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId);

}
