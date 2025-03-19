package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.ApplicationSettings;

/**
 * @author Ravi
 */
public interface ApplicationSettingsDao extends GenericDao<ApplicationSettings, String> {

	/**
	 * @param id
	 * @return
	 */
	ApplicationSettings loadById(String id);

}
