package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.Sor;

import java.util.List;

public interface RfpSorDao extends GenericSorDao<RfpEventSor, String> {
    List<Sor> findSorbyEventId(String eventId);

    public Integer getCountOfSorByEventId(String eventId);

    List<String> rfpSorNamesByEventId(String eventId);

    List<String> getNotSectionAddedRfpSorIdsByEventId(String eventId);


    List<String> getNotSectionItemAddedRfpSorIdsByEventId(String eventId);
}
