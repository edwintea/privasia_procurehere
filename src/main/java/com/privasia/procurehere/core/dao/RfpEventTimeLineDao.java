package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfpEventTimeLine;

/**
 * @author Teja
 */
public interface RfpEventTimeLineDao extends GenericDao<RfpEventTimeLine, String> {

	void deleteTimeline(String id);

	List<RfpEventTimeLine> getRfpEventTimeline(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventTimeline> getPlainRfpEventTimeline(String id);

}
