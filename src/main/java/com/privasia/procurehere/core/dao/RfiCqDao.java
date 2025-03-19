package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;

/**
 * @author RT-Kapil
 * @author Ravi
 */
public interface RfiCqDao extends GenericCqDao<RfiCq, String> {

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

	List<RfiCqItem> getCqItemForEventId(String eventId);

	List<String> getNotSectionAddedRfiCqIdsByEventId(String eventId);

	List<String> getNotSectionItemAddedRfiCqIdsByEventId(String eventId);

	List<RfiCq> findRfiCqForEventByEnvelopeId(String eventId, String envelopeId);

	List<RfiCq> findCqsForEventEnvelopeId(List<String> cqid, String id);

	List<CqPojo> findEventForCqPojoByEventId(String eventId);

}
