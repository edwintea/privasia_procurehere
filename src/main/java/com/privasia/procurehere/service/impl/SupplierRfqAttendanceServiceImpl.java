package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierRfqAttendanceService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class SupplierRfqAttendanceServiceImpl implements SupplierRfqAttendanceService {

	Logger LOG = LogManager.getLogger(SupplierRfqAttendanceServiceImpl.class);

	@Autowired
	RfqSupplierMeetingAttendanceDao rfqSupplierMeetingAttendanceDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierMeetingAttendance saveMeetingAttendance(RfqSupplierMeetingAttendance attendance) {
		return rfqSupplierMeetingAttendanceDao.saveOrUpdate(attendance);
	}

	@Override
	public List<RfqSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		return rfqSupplierMeetingAttendanceDao.getAllSupplierAttendance(meetingId, eventId);
	}

	@Override
	public String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId) {
		String meetingNames = "";
		List<RfqEventMeeting> meetingNamesList = rfqEventMeetingDao.findMandatoryAttendMeetingsByEventId(eventId, supplierId);
		if (CollectionUtil.isNotEmpty(meetingNamesList)) {
			for (RfqEventMeeting meeting : meetingNamesList) {
				RfqSupplierMeetingAttendance attendance = rfqSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meeting.getId(), supplierId);
				if (attendance == null || (attendance != null && attendance.getAttended() == Boolean.FALSE && attendance.getMeetingAttendanceStatus() != MeetingAttendanceStatus.Accepted)) {
					meetingNames += meeting.getTitle() + ",";
				}

			}
		}
		if (StringUtils.checkString(meetingNames).length() > 0) {
			meetingNames = StringUtils.checkString(meetingNames).substring(0, meetingNames.length() - 1);
		}
		return meetingNames;
	}
}
