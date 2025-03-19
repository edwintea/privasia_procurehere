package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 * @author Ravi
 */
public interface RfqCqDao extends GenericCqDao<RfqCq, String> {

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfCqByEventId(String eventId);

	List<String> getNotSectionAddedRfqCqIdsByEventId(String eventId);

	List<String> getNotSectionItemAddedRfqCqIdsByEventId(String eventId);

	List<RfqCq> findCqsForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfqCq> findCqsForEventEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

}
