package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.Sor;

import java.util.List;

public interface RfiSorDao extends GenericSorDao<RfiEventSor, String> {
    List<Sor> findSorbyEventId(String eventId);

    Integer getCountOfSorByEventId(String eventId);

    List<String> rfiSorNamesByEventId(String eventId);

    List<String> getNotSectionAddedRfiSorIdsByEventId(String eventId);


    List<String> getNotSectionItemAddedRfiSorIdsByEventId(String eventId);
}
