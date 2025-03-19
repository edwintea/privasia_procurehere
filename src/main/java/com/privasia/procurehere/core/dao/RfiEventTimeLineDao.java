package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.RfiEventTimeLine;

/**
 * @author Teja
 */
public interface RfiEventTimeLineDao extends GenericDao<RfiEventTimeLine, String> {

	void deleteTimeline(String id);

	List<RfiEventTimeLine> getRfiEventTimeline(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventTimeline> getPlainRfiEventTimeline(String id);

}
