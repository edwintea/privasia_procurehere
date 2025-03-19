package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;

import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Edwin
 */
@Service
@Transactional(readOnly = true)
public class PoEventServiceImpl implements PoEventService {

	private static final Logger LOG = LogManager.getLogger(Global.PO_LOG);

	@Autowired
	PoEventDao poEventDao;

	@Override
	@Transactional(readOnly = false)
	public PoEvent save(PoEvent event) {
		LOG.info("Po Event :" + event.toLogString());
		return poEventDao.saveOrUpdate(event);
	}

	//PH-PG-4113
	//add PO event for mailbox
	@Override
	@Transactional(readOnly = false)
	public List<PoEvent> findEventByPoId(String poId) {
		return poEventDao.findEventByPoId(poId);
	}

	@Override
	public PoEvent getPoEventByeventId(String eventId) {
		PoEvent poEvent = poEventDao.findByEventId(eventId);
		if (poEvent.getEventOwner().getBuyer() != null) {
			poEvent.getEventOwner().getBuyer().getLine1();
			poEvent.getEventOwner().getBuyer().getLine2();
			poEvent.getEventOwner().getBuyer().getCity();
			if (poEvent.getEventOwner().getBuyer().getState() != null) {
				poEvent.getEventOwner().getBuyer().getState().getStateName();
				if (poEvent.getEventOwner().getBuyer().getState().getCountry() != null) {
					poEvent.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (poEvent.getTemplate() != null) {
			poEvent.getTemplate().getTemplateName();
		}

		/*
		if (CollectionUtil.isNotEmpty(poEvent.getSuppliers())) {
			for (PoEventSupplier item : poEvent.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}

		 */

		if (poEvent.getEventOwner().getBuyer() != null) {
			Buyer buyer = poEvent.getEventOwner().getBuyer();
			buyer.getLine1();
			buyer.getLine2();
			buyer.getCity();
			if (buyer.getState() != null) {
				buyer.getState().getStateName();
				if (buyer.getState().getCountry() != null) {
					buyer.getState().getCountry().getCountryName();
				}
			}
		}


		if (poEvent.getDeliveryAddress() != null) {
			poEvent.getDeliveryAddress().getLine1();
			poEvent.getDeliveryAddress().getLine2();
			poEvent.getDeliveryAddress().getCity();
			if (poEvent.getDeliveryAddress().getState() != null) {
				poEvent.getDeliveryAddress().getState().getStateName();
				poEvent.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}


		if (poEvent.getGroupCode() != null) {
			poEvent.getGroupCode().getId();
			poEvent.getGroupCode().getGroupCode();
			poEvent.getGroupCode().getStatus();
		}

		if (poEvent.getBusinessUnit() != null) {
			poEvent.getBusinessUnit().getUnitName();
		}

		if (poEvent.getCostCenter() != null) {
			poEvent.getCostCenter().getCostCenter();
		}

		return poEvent;
	}
}
