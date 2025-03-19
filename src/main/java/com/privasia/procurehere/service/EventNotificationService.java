package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventMeeting;
import com.privasia.procurehere.core.entity.EventMeetingContact;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;

public interface EventNotificationService {

	/**
	 * @param timeZone
	 * @param rfxView
	 * @param id
	 * @param invited
	 */
	void sendActiveEventNotificationToSupplier(String timeZone, RfxView rfxView, String id, List<EventSupplierPojo> invited);

	/**
	 * @param rfxView
	 */
	void sendRfqEventCloseNotification(RfxView rfxView);

	/**
	 * @param rfxView
	 */
	void sendRfpEventCloseNotification(RfxView rfxView);

	/**
	 * @param rfxView
	 */
	void sendRfiEventCloseNotification(RfxView rfxView);

	/**
	 * @param rfxView
	 */
	void sendRftEventCloseNotification(RfxView rfxView);

	/**
	 * @param rfxView
	 */
	void sendRfaEventCloseNotification(RfxView rfxView);

	/**
	 * @param event
	 * @param meeting
	 * @param contactList
	 * @param user
	 * @param rfxType
	 * @param businessUnit TODO
	 */
	void sendMeetingReminder(Event event, EventMeeting meeting, List<EventMeetingContact> contactList, User user, RfxTypes rfxType, String businessUnit);

	/**
	 * @param event
	 * @param eventOwner
	 * @param rfxTypes
	 * @param businessUnit
	 * @param startReminder TODO
	 */
	void sendEventReminder(Event event, User eventOwner, RfxTypes rfxTypes, String businessUnit, Boolean startReminder);

	/**
	 * @param rfxView
	 */
	void sendEventStartNotification(RfxView rfxView);

}
