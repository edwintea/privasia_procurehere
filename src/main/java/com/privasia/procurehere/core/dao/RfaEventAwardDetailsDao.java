package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaEventAwardDetails;

public interface RfaEventAwardDetailsDao extends GenericDao<RfaEventAwardDetails, String> {

	/**
	 * @param awardId
	 * @param bqId
	 * @return
	 */
	RfaEventAwardDetails rfaEventAwardByEventIdandBqId(String awardId, String bqId);

}
