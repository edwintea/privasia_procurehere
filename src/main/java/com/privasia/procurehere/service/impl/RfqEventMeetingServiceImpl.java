/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.service.RfqEventMeetingService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfqEventMeetingServiceImpl implements RfqEventMeetingService {

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RfqEventMeeting meeting) {
		rfqEventMeetingDao.saveOrUpdate(meeting);
	}

	@Override
	public List<RfqEventMeeting> getMeetingsByEventId(String eventId) {
		return rfqEventMeetingDao.getMeetingByEventId(eventId);
	}

	@Override
	public List<RfqEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		return rfqEventMeetingDao.findMeetingWithoutSuplliersByEventId(eventId);
	}

}
