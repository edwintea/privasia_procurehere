package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingContact;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventMeetingReminder;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RftMeetingService {

	RftEventMeeting saveRftMeeting(RftEventMeeting rftEventMeeting, List<RftEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RftEventMeeting getRftMeetingById(String id);

	/**
	 * @param rftMeeting
	 */
	void updateRftMeeting(RftEventMeeting rftMeeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventMeeting> getRftMeetingByEventId(String eventId);

	/*	*//**
			 * @param document
			 *//*
				 * void storeEventMettingDocs(EventMeetingDocument document);
				 */

	/**
	 * @param docId
	 * @return
	 */
	RftEventMeetingDocument getRftEventMeetingDocument(String docId);

	/**
	 * @param id
	 * @return
	 */
	RftEventMeetingContact getRftMeetingContactById(String id);

	/**
	 * @param id
	 * @return
	 */
	RftEventMeeting getOngoingMeeting(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RftEventMeetingContact> getRftMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRftMeetings(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId
	 * @return
	 */
	List<RftEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMaxMeetingDateForEvent(String eventId);

	/**
	 * @param meetingId
	 */
	void deleteSupplierMeetingAttendence(String meetingId);

	/**
	 * @param persistObj
	 */
	void updateRftMeetingAttendanceForSupplier(RftSupplierMeetingAttendance persistObj);

	/**
	 * @param id
	 * @return
	 */
	RftEvent getRftEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	void removeMeetingDocs(EventMeetingDocument document);

	RftEventMeeting cancelMeeting(RftEventMeeting rftEventMeeting);

	/**
	 * @param docId
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String docId);

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	List<Supplier> removeSupplier(String meetingId, String supplierId);

	Integer getCountOfSupplierForMeeting(String meetingId);

	List<RftEventMeetingReminder> getAllRftEventMeetingReminderForMeeting(String meetingId);

	/**
	 * @param supplierId
	 * @return
	 */
	Integer countMeetingsForSupplier(String supplierId);

	/**
	 * @param rftEventMeeting
	 */
	void deleteMeeting(RftEventMeeting rftEventMeeting);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Supplier> getAllSuppliersByMeetId(String meetingId);

	/**
	 * @param supplierMeetingAttendances
	 */
	void saveOrUpdateAttendance(List<RftSupplierMeetingAttendance> supplierMeetingAttendances);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RftEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RftEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventMeeting> getAllRftMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	Date findMandatorySiteVisitMeetingsByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @return
	 */
	List<RftEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);
}
