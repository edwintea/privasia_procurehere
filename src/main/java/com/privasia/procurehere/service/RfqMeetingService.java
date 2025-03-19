package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeetingContact;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RfqMeetingService {

	/**
	 * @param rventMeeting
	 * @param meetingDocumentList TODO
	 * @param selectAllSupplier TODO
	 * @param eventId TODO
	 * @param eventVisibilityType TODO
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	RfqEventMeeting saveMeeting(RfqEventMeeting rventMeeting, List<RfqEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibilityType) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeeting getMeetingById(String id);

	/**
	 * @param rftMeeting
	 */
	void updateMeeting(RfqEventMeeting rftMeeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> getAllMeetingByEventId(String eventId);

	/**
	 * @param document
	 */
	void storeEventMettingDocs(RfqEventMeetingDocument document);

	/**
	 * @param docId
	 * @return
	 */
	RfqEventMeetingDocument getEventMeetingDocument(String docId);

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeetingContact getMeetingContactById(String id);

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeeting getOngoingMeeting(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventMeetingContact> getMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllMeetings(String eventId, String eventRequirement);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId
	 * @return
	 */
	List<RfqEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId);

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
	 * @param id
	 * @return
	 */
	RfqEvent getEventById(String id);

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
	RfqEventMeeting cancelMeeting(RfqEventMeeting eventMeeting);

	/**
	 * @param persistObj
	 */
	void updateMeetingAttendanceForSupplier(RfqSupplierMeetingAttendance persistObj);

	Integer getCountOfSupplierForMeeting(String meetingId);

	List<RfqEventMeetingReminder> getAllRfqEventMeetingReminderForMeeting(String meetingId);

	RfqEventMeetingDocument getRfqEventMeetingDocument(String docId);

	RfqEvent getRfqEventById(String id);

	/**
	 * @param rfqEventMeeting
	 */
	void deleteMeeting(RfqEventMeeting rfqEventMeeting);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Supplier> getAllSuppliersByMeetId(String meetingId);

	/**
	 * @param supplierMeetingAttendances
	 */
	void saveOrUpdateAttendance(List<RfqSupplierMeetingAttendance> supplierMeetingAttendances);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RfqEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RfqEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventMeeting> getAllRfqMeetingWithPlainDocByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * 
	 * @return
	 */
	List<RfqEventMeetingReminder> getMeetingRemindersForNotification();

	/**
	 * 
	 * @param reminderId
	 */
	void updateImmediately(String reminderId);

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersWithIdAndName(String eventId);

	List<Supplier> getEventSuppliersWithIdAndName(String eventId, TableDataInput input, RfxTypes rfxTypes);

	long getEventSuppliersCountWithIdAndName(String eventId, RfxTypes rfxType);

	long getEventSuppliersFilterCount(String eventId, TableDataInput input, RfxTypes rfxType);

	List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType);

}
