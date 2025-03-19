package com.privasia.procurehere.core.dao;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.pojo.EventTimerPojo;

/**
 * @author arc
 */
public interface GenericEventDao<T extends Event, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param eventId
	 * @param suspensionType
	 * @param supensionRemarks TODO
	 */
	void suspendEvent(String eventId, SuspensionType suspensionType, String supensionRemarks);

	/**
	 * @param eventId
	 */
	void resumeEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	String findBusinessUnitName(String eventId);

	public EventTimerPojo getTimeEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);
}
