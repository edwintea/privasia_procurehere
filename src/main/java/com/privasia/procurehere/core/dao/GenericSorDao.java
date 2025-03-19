package com.privasia.procurehere.core.dao;


import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;

import java.io.Serializable;
import java.util.List;

public interface GenericSorDao <T extends Sor, PK extends Serializable> extends GenericDao<T, PK> {

    /**
     * @param bq
     * @param eventId
     * @return
     */
    boolean isExists(final T bq, String eventId);

    /**
     * @param eventId
     * @return
     */
    List<T> findSorsByEventIdByOrder(String eventId);

    /**
     * @param id
     */
    void deleteSorForEventId(String id);


    /**
     * @param envelopeId
     * @param rfxTypes
     */
    void deleteFromEnvSor(String envelopeId, RfxTypes rfxTypes);

    /**
     * @param eventId
     * @param bqIds
     * @return
     */
    List<T> findSorsByEventIdForEnvelop(String eventId, List<String> bqIds);

    /**
     * @param ids
     * @return
     */
    List<T> findAllSorsByIds(String[] ids);

    /**
     * @param id
     * @return
     */

    RfqEventSor getRfqEventSorBySorId(String id);

    /**
     * @param id
     * @return
     */
    RfpEventSor getRfpEventSorBySorId(String id);

    /**
     * @param id
     * @return
     */
    RfaEventSor getRfaEventSorBySorId(String id);

    /**
     * @param id
     * @return
     */
    RfiEventSor getRfiEventSorBySorId(String id);

    /**
     * @param id
     * @param eventType
     * @throws Exception
     */
    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

    /**
     * @param id
     */
    void deleteSor(String id);


    /**
     * @param bqId
     * @param label
     */
    void deletefieldInSor(String bqId, String label);
}
