package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventSor;

/**
 * @author RT-Kapil
 */

public interface RfaEnvelopDao extends GenericEnvelopDao<RfaEnvelop, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventBq> getNotAssignedRfaBqIdsByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */

	List<RfaCq> getNotAssignedRfaCqIdsByEventId(String eventId);

	List<RfaEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	RfaEvaluatorUser getRfaEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

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
	int getcountClosedEnvelop(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	RfaEvent getEventbyEnvelopeId(String envelopeId);

	List<String> getBqIdlistByEnvelopId(String id);

	List<String> getCqIdlistByEnvelopId(String id);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	RfaEnvelop getRfaEnvelopBySeq(int i, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEnvelop> getEnvelopListByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getEnvelopIdsByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfaCq> getCqsByEnvelopIdByOrder(String envelopId);

	RfaEnvelop getRfaEnvelopDocument(String envelopId);

	List<RfaEventSor> getSorsByEnvelopIdByOrder(String envelopId);

	List<RfaEventSor> getNotAssignedSorIdsByEventId(String eventId);


	/**
	 * @param eventId
	 */
	void removeSorsFromEnvelops(String eventId);
}
