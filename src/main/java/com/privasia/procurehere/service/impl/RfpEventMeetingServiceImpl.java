/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.service.RfpEventMeetingService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfpEventMeetingServiceImpl implements RfpEventMeetingService {

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RfpEventMeeting meeting) {
		rfpEventMeetingDao.saveOrUpdate(meeting);
	}

	@Override
	public List<RfpEventMeeting> getMeetingsByEventId(String eventId) {
		return rfpEventMeetingDao.getRfpMeetingByEventId(eventId);
	}

	@Override
	public List<RfpEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		return rfpEventMeetingDao.findMeetingWithoutSuplliersByEventId(eventId);
	}

}
