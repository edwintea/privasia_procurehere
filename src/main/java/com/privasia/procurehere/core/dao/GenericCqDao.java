package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Cq;

/**
 * @author Nitin Otageri
 */
public interface GenericCqDao<T extends Cq, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param cq
	 * @param eventId
	 * @return
	 */
	boolean isExists(final T cq, String eventId);

	/**
	 * @param ids
	 * @return
	 */
	List<T> findCqsByIds(String[] ids);

	/**
	 * @param id
	 * @return
	 */
	T getCqForId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> findCqsForEvent(String eventId);

	/**
	 * @param eventId
	 * @param cqIds
	 * @return
	 */
	List<T> findCqsByEventIdAndCqIds(String eventId, List<String> cqIds);

	/**
	 * @param id
	 */
	void deleteCqByEventId(String id);

	/**
	 * @param id
	 */
	void deleteCqById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> findCqsForEventByOrder(String eventId);

}
