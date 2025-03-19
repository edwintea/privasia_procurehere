package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeetingContact;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.MeetingStatus;

/**
 * @author arc
 */
public interface RfpEventMeetingDao extends GenericDao<RfpEventMeeting, String> {

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeeting getRfpMeetingById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> getRfpMeetingByEventId(String eventId);

	/**
	 * @param contactId
	 * @return
	 */
	RfpEventMeetingContact getRfpMeetingContactById(String contactId);

	/**
	 * @param id
	 * @return
	 */
	RfpEventMeeting getRfpMeetingByIdAndStatus(String id);

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
	Date getMaxMeetingDateForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfpMeetingByEventId(String eventId);

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
	List<RfpEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId);

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
	RfpEventMeeting getMeetingForIdAndEvent(String id, String eventId);

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
	List<RfpEventMeetingDocument> getPlainMeetingDocument(String meetingId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> getAllRfpMeetingWithPlainDocByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfpEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isSiteVisitExist(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId);

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
