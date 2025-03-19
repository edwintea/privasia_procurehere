package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;

/**
 * @author Vipul
 * @author Ravi
 */

public interface SupplierRfiAttendanceService {

	RfiSupplierMeetingAttendance saveMeetingAttendance(RfiSupplierMeetingAttendance attendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId);

}
