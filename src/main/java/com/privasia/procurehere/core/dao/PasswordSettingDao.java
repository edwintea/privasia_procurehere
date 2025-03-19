/**
 * 
 */
package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.PasswordSettings;
/**
 * 
 * @author Yogesh
 *
 */
public interface PasswordSettingDao extends GenericDao<PasswordSettings, String> {

	PasswordSettings getPasswordSettingsByTenantId(String tenantId);

}
