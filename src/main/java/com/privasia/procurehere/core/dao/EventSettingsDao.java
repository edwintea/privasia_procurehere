package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EventSettings;

/**
 * @author nitin
 */
public interface EventSettingsDao extends GenericDao<EventSettings, String> {

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	EventSettings findByTenantId(String tenantId);
}
