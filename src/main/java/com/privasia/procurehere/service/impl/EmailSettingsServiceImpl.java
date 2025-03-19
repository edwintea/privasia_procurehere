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

import com.privasia.procurehere.core.dao.EmailSettingsDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.entity.EmailSettings;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.service.EmailSettingsService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class EmailSettingsServiceImpl implements EmailSettingsService {
	Logger log = LogManager.getLogger(EmailSettingsServiceImpl.class);

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	EmailSettingsDao settingsDao;

	@Override
	public EmailSettings loadEmailSettings() {
		return settingsDao.loadEmailSettings();
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEmailSettings(EmailSettings user, User actionBy) {
		settingsDao.update(user);
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + user.getId() + " Email Settings updated ", actionBy.getTenantId(), actionBy, new Date(), ModuleType.EmailSettings);
		ownerAuditTrailDao.save(ownerAuditTrail);
	}

}
