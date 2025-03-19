package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventAward;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;

public interface RfqEventAwardDao extends GenericDao<RfqEventAward, String> {

	RfqEventAward rfqEventAwardByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventAward> getRfqEventAwardsByEventId(String eventId);

	Double getSumOfAwardedPrice(String id);

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RfqEventAward rfqEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventAwardId
	 * @return
	 */
	List<AwardReferenceNumberPojo> getRfqEventAwardDetailsByEventIdandBqId(String eventAwardId);

	/**
	 * @param id
	 * @return TODO
	 */
	int removeAwardDetails(String id);

}
