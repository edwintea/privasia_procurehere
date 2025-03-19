package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaEventBq;

public interface RfaBqDao extends GenericBqDao<RfaEventBq, String> {

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findRfaBqbyEventId(String eventId);

	List<String> getNotSectionAddedRfaBqIdsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> rfaBqNamesByEventId(String eventId);

	List<String> getNotSectionItemAddedRfaBqIdsByEventId(String eventId);

	List<Bq> findRfaBqbyEventIdByEnvelopeId(String eventId, String envelopeId);

	List<RfaEventBq> findBqbyEventIdAndEnvelopId(String id, String id2);

}
