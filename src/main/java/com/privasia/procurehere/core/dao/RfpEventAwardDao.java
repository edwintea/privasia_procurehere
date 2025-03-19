package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;

public interface RfpEventAwardDao extends GenericDao<RfpEventAward, String> {

	RfpEventAward rfpEventAwardByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventAward> getRfpEventAwardsByEventId(String eventId);

	Double getSumOfAwardedPrice(String id);

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RfpEventAward rfpEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventAwardId
	 * @return
	 */
	List<AwardReferenceNumberPojo> getRfpEventAwardDetailsByEventIdandBqId(String eventAwardId);

	/**
	 * @param eventId
	 * @return TODO
	 */
	int removeAwardDetails(String eventId);

}
