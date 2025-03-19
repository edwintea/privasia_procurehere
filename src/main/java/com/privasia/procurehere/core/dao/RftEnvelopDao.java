package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventSor;

/**
 * @author RT-Kapil
 */

public interface RftEnvelopDao extends GenericEnvelopDao<RftEnvelop, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventBq> getNotAssignedRftBqIdsByEventId(String eventId);

	List<RftCq> getNotAssignedRftCqIdsByEventId(String eventId);

	List<RftEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	RftEvaluatorUser getRftEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	void removeBqsFromEnvelops(String eventId);

	void removeCqsFromEnvelops(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	int findCountPendingEnvelopse(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	int getcountClosedEnvelop(String eventId);

	RftEvent getEventbyEnvelopeId(String envelopeId);

	List<String> getCqIdlistByEnvelopId(String id);

	List<String> getBqIdlistByEnvelopId(String string);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	String getEnvelipeTitleById(String envelopeId, String eventType);

	RftEnvelop getRftEnvelopBySeqId(int i, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEnvelop> getEnvelopListByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RftEventBq> getBqIdlistByEnvelopIdByOrder(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RftCq> getCqIdlistByEnvelopIdByOrder(String envelopId);

	RftEnvelop getEvaluationDocument(String envelopId);


	List<RftEventSor> getSorsByEnvelopIdByOrder(String envelopId);

	

	List<RftEventSor> getNotAssignedSorIdsByEventId(String eventId);


	/**
	 * @param eventId
	 */
	void removeSorsFromEnvelops(String eventId);

}
