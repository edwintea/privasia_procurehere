package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfqEventBq;

public interface RfqBqDao extends GenericBqDao<RfqEventBq, String> {

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

	List<String> getNotSectionAddedRfqBqIdsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> rfqBqNamesByEventId(String eventId);

	List<String> getNotSectionItemAddedRfqBqIdsByEventId(String eventId);

	List<Bq> findBqbyEventIdAndEnvelopeId(String eventId, String envelopeId);

	List<RfqEventBq> findBqbyyEventIdAndEnvelopeId(String id, String id2);


}
