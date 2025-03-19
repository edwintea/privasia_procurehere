package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.enums.RfxTypes;

/**
 * @author Nitin Otageri
 */
public interface GenericBqDao<T extends Bq, PK extends Serializable> extends GenericDao<T, PK> {

	boolean isExists(final T bq, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> findBqsByEventId(String eventId);

	/**
	 * @param eventId
	 * @param bqIds
	 * @return
	 */
	List<T> findBqsByEventIdForEnvelop(String eventId, List<String> bqIds);

	/**
	 * @param ids
	 * @return
	 */
	List<T> findAllBqsByIds(String[] ids);

	/**
	 * @param id
	 */
	void deleteBQ(String id);

	/**
	 * @param id
	 * @param eventType TODO
	 */
	void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

	/**
	 * @param id
	 * @return
	 */
	T findBqItemById(String id);

	/**
	 * @param bqId
	 * @param label
	 */
	void deletefieldInBq(String bqId, String label);

	/**
	 * @param id
	 */
	void deleteBQForEventId(String id);

	RftEventBq getRftEventBqByBqId(String id);

	RfaEventBq getRfaEventBqByBqId(String id);

	RfpEventBq getRfpEventBqByBqId(String bqId);

	RfqEventBq getRfqEventBqByBqId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> findBqsByEventIdByOrder(String eventId);

	List<T> findBqAndBqItemsByEventIdByOrder(String eventId);
}
