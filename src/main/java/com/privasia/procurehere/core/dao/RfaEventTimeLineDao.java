package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfaEventTimeLine;

/**
 * @author Teja
 */
public interface RfaEventTimeLineDao extends GenericDao<RfaEventTimeLine, String> {

	void deleteTimeline(String id);

	List<RfaEventTimeLine> getRfaEventTimeline(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventTimeline> getPlainRfaEventTimeline(String id);

}
