package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeetingContact;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RfpMeetingService {

	RfpEventMeeting saveRfpMeeting(RfpEventMeeting rfpEventMeeting, List<RfpEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeeting getRfpMeetingById(String id);

	/**
	 * @param rftMeeting
	 */
	void updateRfpMeeting(RfpEventMeeting rftMeeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> getAllRfpMeetingByEventId(String eventId);

	/**
	 * @param document
	 */
	void storeEventMettingDocs(RfpEventMeetingDocument document);

	/**
	 * @param docId
	 * @return
	 */
	RfpEventMeetingDocument getRfpEventMeetingDocument(String docId);

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeetingContact getRfpMeetingContactById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeeting getOngoingMeeting(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventMeetingContact> getRfpMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfpMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfpMeetings(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId
	 * @return
	 */
	List<RfpEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId);

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
	void updateRfpMeetingAttendanceForSupplier(RfpSupplierMeetingAttendance persistObj);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent getRfpEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	List<Supplier> removeSupplier(String meetingId, String supplierId);

	/**
	 * @param docId
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String docId);

	/**
	 * @param document
	 */
	void removeMeetingDocs(EventMeetingDocument document);

	/**
	 * @param eventMeeting
	 * @return
	 */
	RfpEventMeeting cancelMeeting(RfpEventMeeting eventMeeting);

	Integer getCountOfSupplierForMeeting(String meetingId);

	List<RfpEventMeetingReminder> getAllRfpEventMeetingReminderForMeeting(String meetingId);

	/**
	 * @param rfpEventMeeting
	 */
	void deleteMeeting(RfpEventMeeting rfpEventMeeting);

	/**
	 * @param supplierMeetingAttendances
	 */
	void saveOrUpdateAttendance(List<RfpSupplierMeetingAttendance> supplierMeetingAttendances);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Supplier> getAllSuppliersByMeetId(String meetingId);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RfpEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	List<Date> getAllMeetingDateByEventId(String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RfpEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventMeeting> getAllRfpMeetingWithPlainDocByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @return
	 */
	List<RfpEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);

}
