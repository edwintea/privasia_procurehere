package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RftEventAwardDetails;

public interface RftEventAwardDetailsDao extends GenericDao<RftEventAwardDetails, String> {

	/**
	 * @param awardId
	 * @param bqId
	 * @return
	 */
	RftEventAwardDetails rftEventAwardByEventIdandBqId(String awardId, String bqId);

}
