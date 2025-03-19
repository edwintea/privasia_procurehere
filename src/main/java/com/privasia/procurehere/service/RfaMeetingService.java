package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author RT-Kapil
 */

public interface RfaMeetingService {

	RfaEventMeeting saveRfaMeeting(RfaEventMeeting rfaEventMeeting, List<RfaEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibilityType) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfaEventMeeting getRfaMeetingById(String id);

	/**
	 * @param rftMeeting
	 */
	void updateRfaMeeting(RfaEventMeeting rfaMeeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventMeeting> getAllRfaMeetingByEventId(String eventId);

	/**
	 * @param document
	 */
	void storeEventMettingDocs(RfaEventMeetingDocument document);

	/**
	 * @param docId
	 * @return
	 */
	RfaEventMeetingDocument getRfaEventMeetingDocument(String docId);

	/**
	 * @param id
	 * @return
	 */
	RfaEventMeetingContact getRfaMeetingContactById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfaEventMeeting getOngoingMeeting(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfaEventMeetingContact> getRfaMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfaMeetings(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId
	 * @return
	 */
	List<RfaEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId);

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
	void updateRfaMeetingAttendanceForSupplier(RfaSupplierMeetingAttendance persistObj);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent getRfaEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	void removeMeetingDocs(RfaEventMeetingDocument document);

	/**
	 * @param docId
	 * @return
	 */
	EventMeetingDocument getMeetingDocumentForDelete(String docId);

	/**
	 * @param document
	 */
	void removeMeetingDocs(EventMeetingDocument document);

	Integer getCountOfSupplierForMeeting(String meetingId);

	List<RfaEventMeetingReminder> getAllRfaEventMeetingReminderForMeeting(String meetingId);

	List<Supplier> removeSupplier(String meetingId, String supplierId);

	/**
	 * @param eventMeeting
	 * @return
	 */
	RfaEventMeeting cancelMeeting(RfaEventMeeting eventMeeting);

	/**
	 * @param rfaEventMeeting
	 */
	void deleteMeeting(RfaEventMeeting rfaEventMeeting);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Supplier> getAllSuppliersByMeetId(String meetingId);

	/**
	 * @param supplierMeetingAttendances
	 */
	void saveOrUpdateAttendance(List<RfaSupplierMeetingAttendance> supplierMeetingAttendances);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RfaEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RfaEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param id
	 * @return
	 */
	List<RfaEventMeeting> getAllRfaMeetingWithPlainDocByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @return
	 */
	List<RfaEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);

}
