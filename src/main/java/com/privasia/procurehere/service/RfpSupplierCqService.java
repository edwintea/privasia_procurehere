package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierCq;

public interface RfpSupplierCqService {

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	RfpSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param rfpSupplierCq
	 * @return 
	 */
	RfpSupplierCq updateSupplierCq(RfpSupplierCq rfpSupplierCq);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

}
