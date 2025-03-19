/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.service.AccessRightsService;

/**
 * @author Arc
 */
@Service
public class AccessControlListServiceImpl implements AccessRightsService {

	@Autowired
	AccessRightsDao accessRightDao;

	@Transactional(readOnly = false)
	@Override
	public void deleteAccessRights(AccessRights accessControlList) {
		accessRightDao.delete(accessControlList);
	}

	@Transactional(readOnly = true)
	@Override
	public AccessRights findAccessRights(String aclId) {
		return accessRightDao.findById(aclId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AccessRights> getAccessRights() {
		return accessRightDao.findAll(AccessRights.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AccessRights> getParentAccessRights() {
		return accessRightDao.getParentAccessControlList();
	}

	@Transactional(readOnly = false)
	@Override
	public void saveAccessRights(AccessRights accessControlList) {
		accessRightDao.save(accessControlList);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateAccessRights(AccessRights accessControlList) {
		accessRightDao.update(accessControlList);
	}

	@Override
	@Transactional(readOnly = true)
	public AccessRights findByProperty(String propertyName, Object value) {
		return accessRightDao.findByAccessControl(value.toString());
	}

	@Override
	public AccessRights getAccessRightsById(String id) {

		return accessRightDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccessRights> findChildAccessForId(String aclValue) {
		return accessRightDao.findChildAccessForId(aclValue);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccessRights> getParentAccessControlListForType(TenantType tenentType, boolean isEventBasedSubscription) {
		return accessRightDao.getParentAccessControlListForType(tenentType, isEventBasedSubscription);
	}

}
