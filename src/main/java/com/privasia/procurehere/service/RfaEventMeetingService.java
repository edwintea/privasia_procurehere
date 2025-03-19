/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventMeeting;

/**
 * @author ravi
 */
public interface RfaEventMeetingService {

	/**
	 * @param meeting
	 */
	void updateMeeting(RfaEventMeeting meeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventMeeting> getRfaMeetingByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
