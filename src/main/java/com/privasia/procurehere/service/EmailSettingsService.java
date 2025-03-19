/**
 * 
 */
package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.EmailSettings;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Arc
 */
public interface EmailSettingsService {

	/**
	 * @return
	 */
	EmailSettings loadEmailSettings();


	/**
	 * @param settings
	 * @param actionBy TODO
	 */
	void updateEmailSettings(EmailSettings settings, User actionBy);



}
