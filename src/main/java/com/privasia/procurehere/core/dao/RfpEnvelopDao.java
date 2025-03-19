package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventSor;

/**
 * @author Ravi
 */

public interface RfpEnvelopDao extends GenericEnvelopDao<RfpEnvelop, String> {

    /**
     * @param eventId
     * @return
     */
    List<RfpEventBq> getNotAssignedBqIdsByEventId(String eventId);

    /**
     * @param eventId
     * @return
     */
    List<RfpCq> getNotAssignedRfpCqIdsByEventId(String eventId);

    List<RfpEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

    RfpEvaluatorUser getRfpEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

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
    List<RfpEnvelop> getAllClosedEnvelopAndOpener(String eventId);

    RfpEvent getEventbyEnvelopeId(String envelopeId);

    List<String> getCqIdlistByEnvelopId(String envelopeId);

    boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

    List<String> getEventTeamMember(String eventId);

    RfpEnvelop getRfpEnvelopBySeq(int i, String eventId);

    /**
     * @param eventId
     * @return
     */
    List<RfpEnvelop> getEnvelopListByEventId(String eventId);

    /**
     * @param eventId
     * @return
     */
    RfpEnvelop getEmptyEnvelopByEventId(String eventId);

    /**
     * @param envelopId
     * @return
     */
    List<RfpEventBq> getBqsByEnvelopIdByOrder(String envelopId);

    /**
     * @param envelopId
     * @return
     */
    List<RfpCq> getCqsByEnvelopIdByOrder(String envelopId);

    RfpEnvelop getEvaluationDocument(String envelopId);

    List<RfpEventSor> getNotAssignedSorIdsByEventId(String eventId);

    List<RfpEventSor> getSorsByEnvelopIdByOrder(String envelopId);
}
