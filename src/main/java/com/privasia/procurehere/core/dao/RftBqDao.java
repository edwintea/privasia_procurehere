package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RftEventBq;

public interface RftBqDao extends GenericBqDao<RftEventBq, String> {

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findRftBqbyEventId(String eventId);

	List<String> getNotSectionAddedRftBqIdsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> rftBqNamesByEventId(String eventId);

	List<String> getNotSectionItemAddedRftBqIdsByEventId(String eventId);

	List<Bq> findRftBqbyEventIdAndEnvelopeId(String eventId, String envelopeId);

	List<RftEventBq> findBqbyEventIdAndEnvelopeId(String id, String id2);

}