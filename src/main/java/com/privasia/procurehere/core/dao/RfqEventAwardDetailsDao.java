package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfqEventAwardDetails;

public interface RfqEventAwardDetailsDao extends GenericDao<RfqEventAwardDetails, String> {

	/**
	 * @param awardId
	 * @param bqId
	 * @return
	 */
	RfqEventAwardDetails rfqEventAwardByEventIdandBqId(String awardId, String bqId);

}
