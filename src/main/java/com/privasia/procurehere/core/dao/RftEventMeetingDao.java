package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingContact;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;

public interface RftEventMeetingDao extends GenericDao<RftEventMeeting, String> {

	/* List<User> findUserByIds(String[] userIds); */

	RftEventMeeting getRftMeetingById(String id);

	List<RftEventMeeting> getRftMeetingByEventId(String eventId);

	RftEventMeetingContact getRftMeetingContactById(String contactId);

	RftEventMeeting getRftMeetingByIdAndStatus(String id);

	List<RftEventMeetingContact> getRftMeetContactByMeetId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Date getMinMeetingDateForEvent(String eventId);

	Date getMaxMeetingDateForEvent(String eventId);

	Integer getCountOfRftMeetingByEventId(String eventId);

	void deleteAllMeetings(String id);

	List<RftEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId);

	Integer getCountOfSupplierForMeeting(String meetingId);

	/**
	 * @param supplierId
	 * @param eventId TODO
	 * @return
	 */
	Integer countMeetingsForSupplier(String supplierId, String eventId);

	/**
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
	RftEventMeeting getMeetingForIdAndEvent(String id, String eventId);

	List<Date> getAllMeetingDateByEventId(String eventId);

	/**
	 * @param meetingId
	 * @return
	 */
	List<Object[]> getNotRejectedMeetingByMeetingId(String meetingId);

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
	 * @param supplierId TODO
	 * @return
	 */
	List<RftEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	Date findMandatorySiteVisitMeetingsByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
