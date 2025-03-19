package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventAward;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;

public interface RftEventAwardDao extends GenericDao<RftEventAward, String> {

	RftEventAward rftEventAwardByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventAward> getRftEventAwardsByEventId(String eventId);

	Double getSumOfAwardedPrice(String id);

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RftEventAward rftEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	/**
	 * @param eventAwardId
	 * @return
	 */
	List<AwardReferenceNumberPojo> getRftEventAwardDetailsByEventIdandBqId(String eventAwardId);

	/**
	 * @param eventId
	 * @return TODO
	 */
	int removeAwardDetails(String eventId);

}
