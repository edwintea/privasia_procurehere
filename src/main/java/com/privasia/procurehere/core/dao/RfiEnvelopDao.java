package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSor;

/**
 * @author Ravi
 */

public interface RfiEnvelopDao extends GenericEnvelopDao<RfiEnvelop, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiCq> getAllCqByEnvelopId(String envelopId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiCq> getNotAssignedCqIdsByEventId(String eventId);

	List<RfiEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	RfiEvaluatorUser getRfiEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

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
	List<RfiEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	RfiEvent getEventbyEnvelopeId(String envelopeId);

	List<String> getCqIdlistByEnvelopId(String id);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	RfiEnvelop getRfiEnvelopBySeq(Integer seq, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEnvelop> getEnvelopListByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiCq> getCqsByEnvelopIdByOrder(String envelopId);

	RfiEnvelop getEnvelopEvaluationDocument(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiEventSor> getSorsByEnvelopIdByOrder(String envelopId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventSor> getNotAssignedSorIdsByEventId(String eventId);


	/**
	 * @param eventId
	 */
	void removeSorsFromEnvelops(String eventId);


	String getEnvelipeTitleById(String envelopeId, String eventType);
}
