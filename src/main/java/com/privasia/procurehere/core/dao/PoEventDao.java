package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.PoEvent;
import com.privasia.procurehere.core.entity.RfqEvent;

import java.util.List;

/**
 * @author Edwin
 */
public interface PoEventDao extends GenericDao<PoEvent, String> {

	/**
	 * @param poId
	 * @return
	 */
	List<PoEvent> findEventByPoId(String poId);

	/**
	 * @param eventId
	 * @return
	 */
	PoEvent findByEventId(String eventId);

}
