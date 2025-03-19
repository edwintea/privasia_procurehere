package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.MeetingStatus;

public interface RfaEventMeetingDao extends GenericDao<RfaEventMeeting, String> {

	/* List<User> findUserByIds(String[] userIds); */

	RfaEventMeeting getRfaMeetingById(String id);

	List<RfaEventMeeting> getRfaMeetingByEventId(String eventId);

	RfaEventMeetingContact getRfaMeetingContactById(String contactId);

	RfaEventMeeting getRfaMeetingByIdAndStatus(String id);

	List<RfaEventMeetingContact> getRfaMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	Date getMaxMeetingDateForEvent(String eventId);

	Integer getCountOfRfaMeetingByEventId(String eventId);

	void deleteAllMeetings(String id);

	List<RfaEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId);

	Integer getCountOfSupplierForMeeting(String meetingId);

	/**
	 * @param supplierId
	 * @param eventId TODO
	 * @return
	 */
	Integer countMeetingsForSupplier(String supplierId, String eventId);

	/**
	 * @return
	 */
	int activateScheduledMeetings();

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
	RfaEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	List<Date> getAllMeetingDateByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<Object[]> getNotRejectedMeetingByMeetingId(String id);

	/**
	 * @param meetingId
	 * @return
	 */
	List<RfaEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventMeeting> getAllRfaMeetingWithPlainDocByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfaEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
