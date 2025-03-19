package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventAward;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;

public interface RfaEventAwardDao extends GenericDao<RfaEventAward, String> {

	RfaEventAward rfaEventAwardByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventAward> getRfaEventAwardsByEventId(String eventId);

	Double getSumOfAwardedPrice(String eventId);

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RfaEventAward rfaEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventAwardId
	 * @return
	 */
	List<AwardReferenceNumberPojo> getRfaEventAwardDetailsByEventIdandBqId(String eventAwardId);

	/**
	 * @param eventId
	 * @return TODO
	 */
	int removeAwardDetails(String eventId);

}
