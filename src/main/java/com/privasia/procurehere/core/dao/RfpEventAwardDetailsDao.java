package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpEventAwardDetails;

public interface RfpEventAwardDetailsDao extends GenericDao<RfpEventAwardDetails, String> {

	/**
	 * @param awardId
	 * @param bqId
	 * @return
	 */
	RfpEventAwardDetails rfpEventAwardByEventIdandBqId(String awardId, String bqId);

}
