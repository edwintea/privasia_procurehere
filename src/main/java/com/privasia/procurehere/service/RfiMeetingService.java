package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author Ravi
 */

public interface RfiMeetingService {

	/**
	 * @param rfiEventMeeting
	 * @param meetingDocumentList TODO
	 * @param selectAllSupplier TODO
	 * @param eventId TODO
	 * @param eventVisibility TODO
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	RfiEventMeeting saveRfiMeeting(RfiEventMeeting rfiEventMeeting, List<RfiEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeeting getRfiMeetingById(String id);

	/**
	 * @param rftMeeting
	 */
	void updateRfiMeeting(RfiEventMeeting rftMeeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> getAllRfiMeetingByEventId(String eventId);

	/**
	 * @param document
	 */
	void storeEventMettingDocs(RfiEventMeetingDocument document);

	/**
	 * @param docId
	 * @return
	 */
	RfiEventMeetingDocument getRfiEventMeetingDocument(String docId);

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeetingContact getRfiMeetingContactById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeeting getOngoingMeeting(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventMeetingContact> getRfiMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfiMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfiMeetings(String eventId, String eventRequirement);

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
	void updateRfiMeetingAttendanceForSupplier(RfiSupplierMeetingAttendance persistObj);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent getRfiEventById(String id);

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
	 * @param document
	 */
	void removeMeetingDocs(EventMeetingDocument document);

	/**
	 * @param ventMeeting
	 * @return
	 */
	RfiEventMeeting cancelMeeting(RfiEventMeeting ventMeeting);

	EventMeetingDocument getMeetingDocumentForDelete(String docId);

	Integer getCountOfSupplierForMeeting(String meetingId);

	List<RfiEventMeetingReminder> getAllRfiEventMeetingReminderForMeeting(String meetingId);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId
	 * @return
	 */
	List<RfiEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId);

	/**
	 * @param rfiEventMeeting
	 */
	void deleteMeeting(RfiEventMeeting rfiEventMeeting);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Supplier> getAllSuppliersByMeetId(String meetingId);

	/**
	 * @param supplierMeetingAttendances
	 */
	void saveOrUpdateAttendance(List<RfiSupplierMeetingAttendance> supplierMeetingAttendances);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RfiEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RfiEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventMeeting> getAllRfiMeetingWithPlainDocByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @return
	 */
	List<RfiEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);

}
