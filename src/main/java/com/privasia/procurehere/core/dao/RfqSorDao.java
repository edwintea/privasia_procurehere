package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.Sor;

import java.util.List;

public interface RfqSorDao extends GenericSorDao<RfqEventSor, String> {
    List<Sor> findSorbyEventId(String eventId);

    public Integer getCountOfSorByEventId(String eventId);

    List<String> rfqSorNamesByEventId(String eventId);

    List<String> getNotSectionAddedRfqSorIdsByEventId(String eventId);

    List<String> getNotSectionItemAddedRfqSorIdsByEventId(String eventId);
}
