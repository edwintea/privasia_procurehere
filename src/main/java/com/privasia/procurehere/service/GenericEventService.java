package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.enums.RfxTypes;

public interface GenericEventService {

	Event updateEvent(Event event, RfxTypes type);

	Event getEventById(String id, RfxTypes type);

}
