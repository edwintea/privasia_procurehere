package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfpEventBq;

public interface RfpBqDao extends GenericBqDao<RfpEventBq, String> {

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findBqbyEventId(String eventId);

	List<String> getNotSectionAddedRfpBqIdsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> rfpBqNamesByEventId(String eventId);

	List<String> getNotSectionItemAddedRfpBqIdsByEventId(String eventId);

	List<Bq> findBqbyEventIdAndEnvelopeId(String eventId, String envelopeId);

	List<RfpEventBq> findBqbyEventIdAndEnvelopId(String eventId, String envelopeId);


}
