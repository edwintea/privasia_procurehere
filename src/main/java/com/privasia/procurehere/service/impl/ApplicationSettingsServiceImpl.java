/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ApplicationSettingsDao;
import com.privasia.procurehere.core.entity.ApplicationSettings;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.ApplicationSettingsService;

/**
 * @author Ravi
 */
@Service
public class ApplicationSettingsServiceImpl implements ApplicationSettingsService {

	@Autowired
	ApplicationSettingsDao settingsDao;

	@Transactional(readOnly = false)
	@Override
	public void delete(ApplicationSettings settings) {
		settingsDao.delete(settings);
		/*
		 * EventAudit auditTrail = new EventAudit(SecurityLibrary.getLoggedInUser(), new Date(),
		 * UserActivity.DELETE.toString(), "Application setting parameter name Deleted by : " +
		 * SecurityLibrary.getLoggedInUserLoginId()); auditTrailService.save(auditTrail);
		 */
	}

	@Transactional(readOnly = true)
	@Override
	public List<ApplicationSettings> getApplicationSettings() {
		return settingsDao.findAll(ApplicationSettings.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ApplicationSettings loadById(String id) {
		return settingsDao.loadById(id);
	}

	@Transactional(readOnly = false)
	@Override
	public void save(ApplicationSettings settings) {
		settings.setCreatedBy(SecurityLibrary.getLoggedInUser());
		settings.setCreatedDate(new Date());
		settingsDao.save(settings);
		/*
		 * EventAudit auditTrail = new EventAudit(SecurityLibrary.getLoggedInUser(), new Date(),
		 * UserActivity.ADD.toString(), "Application setting parameter name  Created by : " +
		 * SecurityLibrary.getLoggedInUserLoginId()); auditTrailService.save(auditTrail);
		 */
	}

	@Transactional(readOnly = false)
	@Override
	public void update(ApplicationSettings settings) {
		settings.setModifiedBy(SecurityLibrary.getLoggedInUser());
		settings.setModifiedDate(new Date());
		settingsDao.update(settings);
		/*
		 * EventAudit auditTrail = new EventAudit(SecurityLibrary.getLoggedInUser(), new Date(),
		 * UserActivity.UPDATE.toString(), "Application setting parameter name  Updated by : " +
		 * SecurityLibrary.getLoggedInUserLoginId()); auditTrailService.save(auditTrail);
		 */
	}

}
