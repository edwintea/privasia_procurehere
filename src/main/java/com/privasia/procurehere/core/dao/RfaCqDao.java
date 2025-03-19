package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 */
public interface RfaCqDao extends GenericCqDao<RfaCq, String> {

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaCqByEventId(String eventId);

	List<String> getNotSectionAddedRfaCqIdsByEventId(String eventId);

	List<String> getNotSectionItemAddedRfaCqIdsByEventId(String eventId);

	List<RfaCq> findCqsForEventEnvelopeId(String eventId, String envelopeId);

	List<RfaCq> findCqsForEventEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

}
