package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 * @author Ravi
 */
public interface RftCqDao extends GenericCqDao<RftCq, String> {

	/**
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void isAllowToDeleteCq(String cqId) throws NotAllowedException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftCqByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getNotSectionAddedRftCqIdsByEventId(String eventId);

	List<String> getNotSectionItemAddedRftCqIdsByEventId(String eventId);

	List<RftCq> findCqsForEventByEnvelopeId(String eventId, String envelopeId);

	List<RftCq> findCqsForEventEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

}
