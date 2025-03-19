/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ApplicationSettings;

/**
 * @author Ravi
 */
public interface ApplicationSettingsService {

	/**
	 * @return
	 */
	List<ApplicationSettings> getApplicationSettings();

	/**
	 * @param settings
	 */
	void save(ApplicationSettings settings);

	/**
	 * @param settings
	 */
	void update(ApplicationSettings settings);

	/**
	 * @param settings
	 */
	void delete(ApplicationSettings settings);

	/**
	 * @param id
	 * @return
	 */
	ApplicationSettings loadById(String id);

}
