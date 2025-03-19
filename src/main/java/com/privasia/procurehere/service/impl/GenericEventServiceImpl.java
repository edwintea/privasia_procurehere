package com.privasia.procurehere.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.service.GenericEventService;

@Service
@Transactional(readOnly = true)
public class GenericEventServiceImpl implements GenericEventService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(GenericEventServiceImpl.class);

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Override
	public Event getEventById(String id, RfxTypes type) {
		Event event = null;

		switch (type) {
		case RFA:
			event = rfaEventDao.findByEventId(id);
			break;
		case RFI:
			event = rfiEventDao.findById(id);
			break;
		case RFP:
			event = rfpEventDao.findByEventId(id);
			break;
		case RFQ:
			event = rfqEventDao.findByEventId(id);
			break;
		case RFT:
			event = rftEventDao.findByEventId(id);
			break;
		default:
			break;
		}

		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public Event updateEvent(Event event, RfxTypes type) {
		Event retEvent = null;

		switch (type) {
		case RFA:
			retEvent = rfaEventDao.update((RfaEvent) event);
			break;
		case RFI:
			retEvent = rfiEventDao.update((RfiEvent) event);
			break;
		case RFP:
			retEvent = rfpEventDao.update((RfpEvent) event);
			break;
		case RFQ:
			retEvent = rfqEventDao.update((RfqEvent) event);
			break;
		case RFT:
			retEvent = rftEventDao.update((RftEvent) event);
			break;
		default:
			break;
		}

		return retEvent;
	}

}
