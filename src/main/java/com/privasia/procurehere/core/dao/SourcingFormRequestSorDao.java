package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SourcingFormRequestSor;

import java.util.List;

public interface SourcingFormRequestSorDao extends GenericDao<SourcingFormRequestSor, String> {

    /**
     * @param formId
     * @return
     */
    List<SourcingFormRequestSor> findSorByFormIdByOrder(String formId);

    /**
     * @param formId
     * @param bqId
     * @param name
     * @return
     */
    boolean isSorExists(String formId, String bqId, String name);


    /**
     * @param id
     */
    void deleteSor(String id);


    List<SourcingFormRequestSor> findSorsByFormId(String formId);


    /**
     * @param bqId
     * @param label
     */
    void deletefieldInSor(String bqId, String label);
}
