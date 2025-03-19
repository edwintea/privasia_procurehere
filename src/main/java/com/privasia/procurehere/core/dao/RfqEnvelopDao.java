package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */

public interface RfqEnvelopDao extends GenericEnvelopDao<RfqEnvelop, String> {

	List<User> removeEvaluator = null;
	List<User> addEvaluator = null;

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventBq> getNotAssignedBqIdsByEventId(String eventId);


	List<RfqEventSor> getNotAssignedSorIdsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqCq> getNotAssignedCqIdsByEventId(String eventId);

	List<RfqEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	RfqEvaluatorUser getRfqEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	void removeBqsFromEnvelops(String eventId);

	void removeCqsFromEnvelops(String eventId);

	void removeSorsFromEnvelops(String eventId);

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
	List<RfqEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	RfqEvent getEventbyEnvelopeId(String envelopeId);

	List<String> getBqIdlistByEnvelopId(String id);

	List<String> getCqIdlistByEnvelopId(String id);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	RfqEnvelop getRfiEnvelopBySeq(int i, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEnvelop> getEnvelopListByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfqEventBq> getBqsByEnvelopIdByOrder(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfqCq> getCqIdlistByEnvelopIdByOrder(String envelopId);

	RfqEnvelop getEvaluationDocument(String envelopId);

	List<RfqEventSor> getSorsByEnvelopIdByOrder(String envelopId);

}
