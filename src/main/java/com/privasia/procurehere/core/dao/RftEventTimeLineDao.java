package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RftEventTimeLine;

/**
 * @author Teja
 */
public interface RftEventTimeLineDao extends GenericDao<RftEventTimeLine, String> {

	/**
	 * @param id
	 */
	void deleteTimeline(String id);

	/**
	 * @param id
	 * @return
	 */

	List<RftEventTimeLine> getRftEventTimeline(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventTimeline> getPlainRftEventTimeline(String id);

}
