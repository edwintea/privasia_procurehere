package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;

/**
 * @author Vipul
 */

public interface SupplierRftMeetingAttendanceService {

	RftSupplierMeetingAttendance saveMeetingAttendance(RftSupplierMeetingAttendance attendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RftSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	String findMandatoryAttendMeetingsByEventId(String loggedInUserTenantId, String eventId);
}
