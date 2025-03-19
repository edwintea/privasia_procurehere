package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierRftMeetingAttendanceService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class SupplierRftAttendanceServiceImpl implements SupplierRftMeetingAttendanceService {

	Logger LOG = LogManager.getLogger(SupplierRftAttendanceServiceImpl.class);

	@Autowired
	RftSupplierMeetingAttendanceDao rftSupplierMeetingAttendanceDao;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public RftSupplierMeetingAttendance saveMeetingAttendance(RftSupplierMeetingAttendance attendance) {
		return rftSupplierMeetingAttendanceDao.saveOrUpdate(attendance);

	}

	@Override
	public List<RftSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		return rftSupplierMeetingAttendanceDao.getAllSupplierAttendance(meetingId, eventId);
	}

	@Override
	public String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId) {
		String meetingNames = "";
		List<RftEventMeeting> meetingNamesList = rftEventMeetingDao.findMandatoryAttendMeetingsByEventId(eventId, supplierId);
		if (CollectionUtil.isNotEmpty(meetingNamesList)) {
			for (RftEventMeeting meeting : meetingNamesList) {
				LOG.info("Title" + meeting.getTitle());
				RftSupplierMeetingAttendance attendance = rftSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meeting.getId(), supplierId);
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
