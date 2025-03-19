package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.Sor;

import java.util.List;

public interface RftSorDao extends GenericSorDao<RftEventSor, String> {

    List<Sor> findSorbyEventId(String eventId);

    Integer getCountOfSorByEventId(String eventId);

    List<String> rftSorNamesByEventId(String eventId);

    List<String> getNotSectionAddedRftSorIdsByEventId(String eventId);

    List<String> getNotSectionItemAddedRftSorIdsByEventId(String eventId);
}
