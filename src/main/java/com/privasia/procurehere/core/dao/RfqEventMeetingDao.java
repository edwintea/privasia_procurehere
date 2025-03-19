package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeetingContact;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.MeetingStatus;

/**
 * @author arc
 */
public interface RfqEventMeetingDao extends GenericDao<RfqEventMeeting, String> {

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeeting getMeetingById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> getMeetingByEventId(String eventId);

	/**
	 * @param contactId
	 * @return
	 */
	RfqEventMeetingContact getMeetingContactById(String contactId);

	/**
	 * @param id
	 * @return
	 */
	RfqEventMeeting getMeetingByIdAndStatus(String id);

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
	Date getMaxMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfMeetingByEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteAllMeetings(String id);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @param tenantId TODO
	 * @return
	 */
	List<RfqEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId);

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
	RfqEventMeeting getMeetingForIdAndEvent(String id, String eventId);

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
	List<RfqEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> getAllRfqMeetingWithPlainDocByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfqEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
