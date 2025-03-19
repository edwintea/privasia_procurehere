/**
 * 
 */
package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.User;

/**
 * @author jayshree
 *
 */
public interface EventSettingsService {

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	EventSettings getEventSettingsByTenantId(String loggedInUserTenantId);

	/**
	 * @param id
	 * @return
	 */
	EventSettings getEventSettingsById(String id);

	/**
	 * @param newEventSettings
	 * @return TODO
	 */
	EventSettings saveEventSettings(EventSettings newEventSettings);

	/**
	 * @param perstObj
	 * @param user TODO
	 * @return TODO
	 */
	EventSettings updateEventSettings(EventSettings perstObj, User user);

}
