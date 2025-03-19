package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 * @author Ravi
 */
public interface RfpCqDao extends GenericCqDao<RfpCq, String> {

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

	List<String> getNotSectionAddedRfpCqIdsByEventId(String eventId);

	List<String> getNotSectionItemAddedRfpCqIdsByEventId(String eventId);

	List<RfpCq> findCqsForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfpCq> findCqsForEventEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

}
