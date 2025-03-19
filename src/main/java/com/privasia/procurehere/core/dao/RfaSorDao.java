package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.Sor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RfaSorDao extends GenericSorDao<RfaEventSor, String> {
    List<Sor> findSorbyEventId(String eventId);

    Integer getCountOfSorByEventId(String eventId);

    List<String> rfaSorNamesByEventId(String eventId);

    List<String> getNotSectionAddedRfaSorIdsByEventId(String eventId);

    List<String> getNotSectionItemAddedRfaSorIdsByEventId(String eventId);
}
