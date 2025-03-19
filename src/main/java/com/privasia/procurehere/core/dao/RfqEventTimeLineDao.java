package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfqEventTimeLine;

/**
 * @author Teja
 */
public interface RfqEventTimeLineDao extends GenericDao<RfqEventTimeLine, String> {

	void deleteTimeline(String id);

	List<RfqEventTimeLine> getRfqEventTimeline(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventTimeline> getPlainRfqEventTimeline(String id);

}
