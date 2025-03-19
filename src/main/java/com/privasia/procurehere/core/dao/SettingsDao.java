package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.Settings;

public interface SettingsDao extends GenericDao<Settings, String> {

	/**
	 * @return
	 */
	Iterable<Settings> findAll();

}
