package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;

/**
 * @author Vipul
 * @author Ravi
 */

public interface SupplierRfpAttendanceService {

	RfpSupplierMeetingAttendance saveMeetingAttendance(RfpSupplierMeetingAttendance attendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId);
}
