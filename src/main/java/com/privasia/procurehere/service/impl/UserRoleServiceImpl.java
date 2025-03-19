/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserRolePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.AccessRightsService;
import com.privasia.procurehere.service.UserRoleService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class UserRoleServiceImpl implements UserRoleService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	AccessRightsService accListService;

	@Autowired
	UserDao userDao;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	/*
	 * New
	 */

	@Override
	@Transactional(readOnly = false)
	public String saveRole(UserRole userRole, User user) {
		try {
			LOG.info("UserRole " + userRole.getRoleName() + "     :   " + userRole.getAccessControlList());

			userRole = userRoleDao.saveOrUpdate(userRole);
			try {
				switch (user.getTenantType()) {
				case BUYER:
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					buyerAuditTrailDao.save(buyerAuditTrail);
					break;

				case OWNER:
					OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					ownerAuditTrailDao.save(OwnerAuditTrail);
					break;

				case SUPPLIER:
					SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					supplierAuditTrailDao.save(supplierAuditTrail);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				LOG.error("ERROR while storing user role audit.. " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("ERROR while User Role storing .. " + e.getMessage(), e);
		}
		return (userRole != null ? userRole.getId() : null);

	}
	
	
	
	@Override
	@Transactional(readOnly = false)
	public UserRole saveUserRole(UserRole userRole, User user) {
		try {
			LOG.info("UserRole " + userRole.getRoleName() + "     :   " + userRole.getAccessControlList());

			userRole = userRoleDao.saveOrUpdate(userRole);
			try {
				switch (user.getTenantType()) {
				case BUYER:
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					buyerAuditTrailDao.save(buyerAuditTrail);
					break;

				case OWNER:
					OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					ownerAuditTrailDao.save(OwnerAuditTrail);
					break;

				case SUPPLIER:
					SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.CREATE, "'" + userRole.getRoleName() + "' User Role created", user.getTenantId(), user, new Date(), ModuleType.UserRole);
					supplierAuditTrailDao.save(supplierAuditTrail);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				LOG.error("ERROR while storing user role audit.. " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("ERROR while User Role storing .. " + e.getMessage(), e);
		}
		return userRole;

	}
	
	

	@Override
	@Transactional(readOnly = false)
	public void updateRole(UserRole userRole, User user) {
		userRoleDao.update(userRole);
		try {
			switch (user.getTenantType()) {
			case BUYER:
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + userRole.getRoleName() + "' User Role updated", user.getTenantId(), user, new Date(), ModuleType.UserRole);
				buyerAuditTrailDao.save(buyerAuditTrail);
				break;

			case OWNER:
				OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + userRole.getRoleName() + "' User Role updated", user.getTenantId(), user, new Date(), ModuleType.UserRole);
				ownerAuditTrailDao.save(OwnerAuditTrail);
				break;

			case SUPPLIER:
				SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.UPDATE, "'" + userRole.getRoleName() + "' User Role updated",user.getTenantId(), user, new Date(), ModuleType.UserRole);
				supplierAuditTrailDao.save(supplierAuditTrail);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("ERROR while updating user role audit.. " + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRole(UserRole userRole, User user) {
		LOG.warn("User Role " + userRole + " being deleted by : " + SecurityLibrary.getLoggedInUserLoginId());
		if (userDao.findAllActiveUsersForRole(userRole)) {
			LOG.info("FIND USERS ");
			ConstraintViolationException cause = new ConstraintViolationException("Active User(s) already exists for this Role : " + userRole.getRoleName(), null, "Unique Constraint");
			throw new DataIntegrityViolationException("Constraint Violation", cause);
		}
		String roleName = userRole.getRoleName();
		userRoleDao.delete(userRole);
		try {
			switch (user.getTenantType()) {
			case BUYER:
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "'" + roleName + "' User Role deleted", user.getTenantId(), user, new Date(), ModuleType.UserRole);
				buyerAuditTrailDao.save(buyerAuditTrail);
				break;

			case OWNER:
				OwnerAuditTrail OwnerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + roleName + "' User Role deleted", user.getTenantId(), user, new Date(), ModuleType.UserRole);
				ownerAuditTrailDao.save(OwnerAuditTrail);
				break;

			case SUPPLIER:
				SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(AuditTypes.DELETE, "'" + roleName + "' User Role deleted", user.getTenantId(), user, new Date(), ModuleType.UserRole);
				supplierAuditTrailDao.save(supplierAuditTrail);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("ERROR while deleting user role audit.. " + e.getMessage(), e);
		}

	}

	@Override
	public boolean isExists(UserRole userRole, String tenantId) {
		return userRoleDao.isExist(userRole, tenantId);
	}

	@Override
	public List<UserRolePojo> getAllUserRolePojo(String tenantId) {
		List<UserRolePojo> returnList = new ArrayList<UserRolePojo>();
		List<UserRole> list = userRoleDao.findAllActiveRoles(tenantId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (UserRole userRole : list) {
				if (userRole.getCreatedBy() != null)
					userRole.getCreatedBy().getLoginId();
				if (userRole.getModifiedBy() != null)
					userRole.getModifiedBy().getLoginId();

				UserRolePojo cp = new UserRolePojo(userRole);

				returnList.add(cp);
			}
		}

		return returnList;

	}

	@Override
	public UserRole getUserRoleById(String id) {
		UserRole role = userRoleDao.findById(id);
		List<AccessRights> list = role.getAccessControlList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (AccessRights acl : list) {
				acl.getAclName();
				if (acl.getParent() != null) {
					acl.getParent().getAclName();
				}
				// Traverse Child Nodes
				if (CollectionUtil.isNotEmpty(acl.getChildren())) {
					for (AccessRights cacl : acl.getChildren()) {
						cacl.getAclName();
						if (CollectionUtil.isNotEmpty(cacl.getChildren())) {
							for (AccessRights ccacl : cacl.getChildren()) {
								ccacl.getAclName();
								if (CollectionUtil.isNotEmpty(ccacl.getChildren())) {
									for (AccessRights cccacl : ccacl.getChildren()) {
										cccacl.getAclName();
										if (CollectionUtil.isNotEmpty(cccacl.getChildren())) {
											for (AccessRights ccccacl : cccacl.getChildren()) {
												ccccacl.getAclName();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return role;
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.eccs.core.service.UserRoleService#findUserRoleById(java. lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public UserRole findUserRoleById(String userRoleId) {
		return userRoleDao.findById(userRoleId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserRole> getUserRoles() {
		LOG.info("inside the UserRle Service" + userRoleDao.findAllActiveRoles(SecurityLibrary.getLoggedInUserTenantId()));
		return userRoleDao.findAllActiveRoles(SecurityLibrary.getLoggedInUserTenantId());
	}

	@Transactional(readOnly = true)
	@Override
	public UserRole loadUserRoleById(String userRoleId) {
		return userRoleDao.loadById(userRoleId);
	}

	@Transactional(readOnly = false)
	@Override
	public void saveUserRole(UserRole userRole, List<AccessRights> accessControlLists) {
		LOG.info("User Role " + userRole + " being created by : " + SecurityLibrary.getLoggedInUserLoginId() + " - [" + userRole.toLogString() + "]");
		userRole.setAccessControlList(accessControlLists);
		userRoleDao.save(userRole);
		// AuditTrail auditTrail = new AuditTrail(SecurityLibrary.getLoggedInUser(), new Date(),
		// UserActivity.ADD.toString(), "User Role " + userRole.getRoleName() + " Created by : " +
		// SecurityLibrary.getLoggedInUserLoginId());
		// auditTrailService.save(auditTrail);

	}

	@Transactional(readOnly = false)
	@Override
	public void updateUserRole(UserRole userRole, List<AccessRights> accessControlLists) {
		LOG.info("User Role " + userRole + " being modified by : " + SecurityLibrary.getLoggedInUserLoginId() + " - [" + userRole.toLogString() + "]");
		userRole.setAccessControlList(accessControlLists);
		userRole.setModifiedBy(SecurityLibrary.getLoggedInUser());
		userRole.setModifiedDate(new Date());
		userRoleDao.update(userRole);
		/*
		 * EventAudit auditTrail = new EventAudit(SecurityLibrary.getLoggedInUser(), new Date(),
		 * UserActivity.UPDATE.toString(), "User Role " + userRole.getRoleName() + " Updated by : " +
		 * SecurityLibrary.getLoggedInUserLoginId()); auditTrailService.save(auditTrail);
		 */

	}

	@Override
	@Transactional(readOnly = true)
	public List<AccessRights> getAssignedAccessControlList(String userRoleId) {
		UserRole userRole = findUserRoleById(userRoleId);
		if (userRole != null && userRole.getAccessControlList() != null) {
			for (AccessRights acl : userRole.getAccessControlList())
				acl.getAclValue();

			return userRole.getAccessControlList();
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccessRights> getParentAccessControlListForType(TenantType tenentType, boolean isEventBasedSubscription) {
		return accListService.getParentAccessControlListForType(tenentType, isEventBasedSubscription);
	}

	@Override
	public long findTotalUserRoleListForTenant(String tenantId) {
		return userRoleDao.findTotalUserRoleListForTenant(tenantId);
	}

	@Override
	public long findTotalFilteredUserRoleListForTenant(TableDataInput tableParams, String tenantId) {
		return userRoleDao.findTotalFilteredUserRoleListForTenant(tableParams, tenantId);
	}

	@Override
	public List<UserRole> findAllUserRoleListForTenant(TableDataInput tableParams, String tenantId) {
		return userRoleDao.findAllUserRoleListForTenant(tableParams, tenantId);
	}

	@Override
	public UserRole findByUserRoleAndTenantId(String roleName, String tenantId) {

		return userRoleDao.findByUserRoleAndTenantId(roleName, tenantId);
	}

}
