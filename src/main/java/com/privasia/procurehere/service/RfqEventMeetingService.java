/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventMeeting;

/**
 * @author ravi
 */
public interface RfqEventMeetingService {

	/**
	 * @param meeting
	 */
	void updateMeeting(RfqEventMeeting meeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> getMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
