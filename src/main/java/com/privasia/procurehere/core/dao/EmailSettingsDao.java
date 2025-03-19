package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.EmailSettings;

/**
 * @author Arc
 */
public interface EmailSettingsDao extends GenericDao<EmailSettings, String> {

	/**
	 * @return
	 */
	EmailSettings loadEmailSettings();
	
	/**
	 * 
	 */
	void createViews();

	void createViewForDashboard();
}
