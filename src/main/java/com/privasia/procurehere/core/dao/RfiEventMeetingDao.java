package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.MeetingStatus;

/**
 * @author Ravi
 */
public interface RfiEventMeetingDao extends GenericDao<RfiEventMeeting, String> {

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeeting getRfiMeetingById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> getRfiMeetingByEventId(String eventId);

	/**
	 * @param contactId
	 * @return
	 */
	RfiEventMeetingContact getRfiMeetingContactById(String contactId);

	/**
	 * @param id
	 * @return
	 */
	RfiEventMeeting getRfiMeetingByIdAndStatus(String id);

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
	Date getMaxMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfiMeetingByEventId(String eventId);

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
	List<RfiEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId);

	Integer getCountOfSupplierForMeeting(String meetingId);

	/**
	 * @param eventId
	 * @param meetStatus
	 * @return
	 */
	List<RfiEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus);

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
	RfiEventMeeting getMeetingForIdAndEvent(String id, String eventId);

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
	List<RfiEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> getAllRfiMeetingWithPlainDocByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfiEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
