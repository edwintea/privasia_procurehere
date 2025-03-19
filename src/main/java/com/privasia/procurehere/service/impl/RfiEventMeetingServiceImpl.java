/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.service.RfiEventMeetingService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfiEventMeetingServiceImpl implements RfiEventMeetingService {

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RfiEventMeeting meeting) {
		rfiEventMeetingDao.saveOrUpdate(meeting);
	}

	@Override
	public List<RfiEventMeeting> getMeetingsByEventId(String eventId) {
		return rfiEventMeetingDao.getRfiMeetingByEventId(eventId);
	}

	@Override
	public List<RfiEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		return rfiEventMeetingDao.findMeetingWithoutSuplliersByEventId(eventId);
	}

}
