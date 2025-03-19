package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Cq;

/**
 * @author Nitin Otageri
 */
public interface GenericSupplierCqDao<T extends Cq, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<T> findSupplierCqItemListByCqId(String cqId, String supplierId);

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	T findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @return
	 */
	List<T> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId);

}
