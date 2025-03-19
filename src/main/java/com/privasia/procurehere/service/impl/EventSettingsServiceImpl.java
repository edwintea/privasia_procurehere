/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.EventSettingsDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.EventSettingsService;

/**
 * @author jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class EventSettingsServiceImpl implements EventSettingsService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	EventSettingsDao eventSettingsDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@Autowired
	BuyerSettingsDao buyerSettingsDao;
	
	@Override
	@Transactional(readOnly = false)
	public EventSettings getEventSettingsByTenantId(String tenantId) {
		EventSettings settings = eventSettingsDao.findByTenantId(tenantId);
		if(settings == null) {
		    settings = new EventSettings();
		    settings.setBuyer(new Buyer());
		    settings.getBuyer().setId(tenantId);
		    settings = eventSettingsDao.saveOrUpdate(settings);
		}
		return settings;
	}

	@Override
	public EventSettings getEventSettingsById(String id) {
		return eventSettingsDao.findById(id);
	}

	@Transactional(readOnly = false)
	@Override
	public EventSettings saveEventSettings(EventSettings newEventSettings) {
		return eventSettingsDao.saveOrUpdate(newEventSettings);
	}

	@Override
	@Transactional(readOnly = false)
	public EventSettings updateEventSettings(EventSettings eventSettings, User user) {
		
		BuyerSettings bs = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
		bs.setAutoCreatePo(eventSettings.getAutoCreatePo());
		bs.setAutoPublishPo(eventSettings.getAutoPublishPo());
		buyerSettingsDao.update(bs);
		
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Event and PO Settings updated", user.getTenantId(), user, new Date(), ModuleType.BuyerSettings);
		buyerAuditTrailDao.save(ownerAuditTrail);
		return eventSettingsDao.saveOrUpdate(eventSettings);
	}

}
