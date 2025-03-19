package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Repository("userDao")
public class UserDaoImpl extends GenericDaoImpl<User, String> implements UserDao {

	private static final Logger LOG = LogManager.getLogger(UserDaoImpl.class);

	@Autowired
	private Environment env;

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExist(User user) {
		StringBuilder hsql = new StringBuilder("from User u where upper(u.loginId) = :loginId");
		if (StringUtils.checkString(user.getId()).length() > 0) {
			hsql.append(" and u.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("loginId", user.getLoginId().toUpperCase());
		if (StringUtils.checkString(user.getId()).length() > 0) {
			query.setParameter("id", user.getId());
		}
		List<User> usList = query.getResultList();
		return CollectionUtil.isNotEmpty(usList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUserPojo() {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c  where a.deleted =:deleted order by a.loginId");
		query.setParameter("deleted", false);
		return query.getResultList();
	}

	@Transactional(readOnly = true)
	@Override
	public User loadById(String id) {
		LOG.debug("Loading user...");
		User user = findById(id);
		UserRole userRole = user.getUserRole();
		if (userRole != null) {
			User createdBy = user.getCreatedBy();
			if (createdBy != null)
				createdBy.getName();
			User modifiedBy = user.getModifiedBy();
			if (modifiedBy != null)
				modifiedBy.getName();

			userRole.getRoleName();
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<User> findAllActiveUsers(String tenantId) {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active order by a.loginId");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.eccs.core.dao.impl.GenericDaoImpl#save(java.lang.Object)
	 */
	@Override
	public User save(User user) {
		user.setLoginId(user.getLoginId().toUpperCase());
		/*
		 * params.put("loginId", user.getLoginId()); params.put("deleted", new Boolean(false));
		 */User count = findByUser(user.getLoginId());
		if (count != null) {
			ConstraintViolationException cause = new ConstraintViolationException("Active User already exists by login id : " + user.getLoginId(), null, "Unique Constraint");
			throw new DataIntegrityViolationException("Duplicate Entry", cause);
		}
		return super.save(user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public User findByUser(String value) {
		try {
			final Query query = getEntityManager().createQuery("from User a left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow left outer join fetch a.financeCompany as fc where upper(a.loginId) = :loginId and a.deleted = false");
			query.setParameter("loginId", value.toUpperCase());
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUserByLoginIdNoTouch(String loginId) {
		try {
			final Query query = getEntityManager().createQuery("from User a where upper(a.loginId) =:loginId and a.deleted = false");
			query.setParameter("loginId", loginId.toUpperCase());
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public User findById(String value) {
		try {
			final Query query = getEntityManager().createQuery("from User a where a.id =:Id");
			query.setParameter("Id", value);
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
			// return (User) query.getSingleResult();
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public User findUserById(String value) {
		LOG.debug("Enter This Method :: " + value);
		try {
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.emailNotifications, a.tenantId) from User a where a.id =:Id");
			query.setParameter("Id", value);
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getAdminUser() {
		final Query query = getEntityManager().createQuery("from User u where upper(u.loginId) = :loginId");
		// List<User> userList = getEntityManager().createQuery("from User u where upper(u.loginId) =
		// :loginId").setParameter("loginId", "ADMIN@PROCUREHERE.COM").getResultList();
		query.setParameter("loginId", "ADMIN@PROCUREHERE.COM");
		List<User> userList = query.getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList.get(0);

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean findAllActiveUsersForRole(UserRole role) {

		final Query query = getEntityManager().createQuery("from User a  where a.deleted =:deleted and a.userRole.id =:roleId");
		query.setParameter("deleted", false);
		query.setParameter("roleId", role.getId());
		List<User> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUserByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow where a.tenantId =:tenantId ");
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@Override
	public User getDetailsOfLoggedinUser(String loginId) {
		final Query query = getEntityManager().createQuery(" from User as u left outer join fetch u.supplier s left outer join fetch u.buyer b left outer join fetch u.assignedTemplates rt inner join fetch u.userRole ur where upper(u.loginId) = :id");
		query.setParameter("id", loginId.toUpperCase());
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<RfxTemplate> getAllTemplatesOfBuyer(String buyerId) {
		StringBuffer sb = new StringBuffer("from RfxTemplate as rt left outer join fetch rt.buyer b where 1 = 0");

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null)
			sb.append(" or b.id =:bid");

		Query query = getEntityManager().createQuery(sb.toString());

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null)
			query.setParameter("bid", buyerId);

		return query.getResultList();
	}

	@Override
	public User getDetailsOfLoggedinBuyerWithTemplates(String tenantId, String id) {
		LOG.info("PARAMETERS getDetailsOfLoggedinBuyerWithTemplates >> "+tenantId +" id >> "+id);
		final Query query = getEntityManager().createQuery("from User u left outer join fetch u.assignedTemplates rt where u.tenantId =:tenantId and u.id =:id");
		query.setParameter("tenantId", tenantId);
		query.setParameter("id", id);
		return (User) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> findUserForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructUserForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredUserForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructUserForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalUserForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (u) from User u where u.tenantId = :tenantId and u.deleted = :deleted ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("deleted", false);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructUserForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct u.id) from User u";
		} else {
			hql += " select new com.privasia.procurehere.core.pojo.UserPojo(u.id, u.loginId, u.name, u.phoneNumber, u.createdDate, u.lastLoginTime, u.createdBy.name, u.modifiedBy.name, c.roleName, u.modifiedDate, u.active, u.locked,u.userType, buy.id ) from User u ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " inner join u.userRole as c left outer join u.buyer as buy left outer join u.createdBy as cb left outer join u.modifiedBy as mb ";
		} else {
			hql += " inner join u.userRole as c left outer join u.createdBy as cb left outer join u.modifiedBy as mb ";
		}

		hql += " where u.tenantId = :tenantId and u.deleted = :deleted ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("locked")) {
					hql += " and u.locked = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("userType")) {
					hql += " and u.userType = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("active")) {
					hql += " and u.active = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.name) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(u." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					LOG.info(dir + " " + orderColumn);
					if (orderColumn.equals("createdBy") || orderColumn.equals("modifiedBy")) {
						hql += " u.name " + dir + ",";
					} else {
						hql += " u." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by u.createdDate desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("deleted", false);
		// boolean isStatusFilterOn = false;

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("Param : " + cp.getData() + " - Value : " + cp.getSearch().getValue());
				if (cp.getData().equals("locked")) {
					query.setParameter(cp.getData(), cp.getSearch().getValue().equalsIgnoreCase("YES") ? Boolean.TRUE : Boolean.FALSE);
				} else if (cp.getData().equals("userType")) {
					query.setParameter(cp.getData(), UserType.fromString(cp.getSearch().getValue()));
				} else if (cp.getData().equals("active")) {
					query.setParameter(cp.getData().replace(".", ""), cp.getSearch().getValue().equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE);
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		/*
		 * if (!isStatusFilterOn) { query.setParameter("status", Status.ACTIVE); }
		 */
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveUsersForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveUsersForAdmin(String tenantId) {
		// final Query query = getEntityManager().createQuery("select distinct a from User a inner join fetch a.userRole
		// as ur inner join ur.accessControlList acl where a.deleted =:deleted and a.tenantId =:tenantId and a.active =
		// :active and acl.aclValue = 'ROLE_ADMIN'");
		// query.setParameter("deleted", false);
		// query.setParameter("active", Boolean.TRUE);
		// query.setParameter("tenantId", tenantId);

		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS, pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram .ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN' and pu.ACCOUNT_DELETED =:ACCOUNT_DELETED and pu.ACCOUNT_ACTIVE =:ACCOUNT_ACTIVE";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("ACCOUNT_DELETED", false);
		query.setParameter("ACCOUNT_ACTIVE", 1);

		List<Object[]> records = query.getResultList();
		List<User> userList = new ArrayList<>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
				if(result[3].toString().equals("0")){
					data.setEmailNotifications(false);
				}else{
					data.setEmailNotifications(true);
				}
			}
			data.setLoginId(((String) result[4]));
			data.setDeviceId(((String) result[5]));
			userList.add(data);
		}

		return userList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getAdminUserForBuyer(Buyer buyer) {
		List<User> userList = getEntityManager().createQuery("from User u where u.buyer = :buyer and upper(u.loginId) = :loginId").setParameter("buyer", buyer).setParameter("loginId", buyer.getLoginEmail().toUpperCase()).getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getAdminUserForSupplier(Supplier supplier) {
		List<User> userList = getEntityManager().createQuery("from User u where u.supplier = :supplier and upper(u.loginId) = :loginId").setParameter("supplier", supplier).setParameter("loginId", supplier.getLoginEmail().toUpperCase()).getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllAdminUsersForSupplier(String tenantId) {
		// final Query query = getEntityManager().createQuery("select u from User u inner join u.userRole ur inner join
		// ur.accessControlList acl where u.tenantId = :tenantId and acl.aclValue = 'ROLE_ADMIN'");
		// query.setParameter("tenantId", tenantId);
		// List<User> userList = query.getResultList();
		// if (CollectionUtil.isNotEmpty(userList)) {
		// return userList;
		// }

		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS, pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram .ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN'";
		final Query query = getEntityManager().createNativeQuery(sql);

		// final Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail,
		// u.loginId,u.tenantId,u.deviceId) from User u inner join u.userRole ur where u.tenantId = :tenantId and
		// ur.accessControlList.aclValue = 'ROLE_ADMIN'");
		query.setParameter("tenantId", tenantId);

		List<Object[]> records = query.getResultList();
		List<User> userList = new ArrayList<>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
				if(result[3].toString().equals("0")){
					data.setEmailNotifications(false);
				}else{
					data.setEmailNotifications(true);
				}
			}
			data.setLoginId(((String) result[4]));
			data.setDeviceId(((String) result[5]));
			userList.add(data);
		}
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList;
		}
		return null;
	}

	@Override
	public int getActiveUserCountForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (u) from User u where u.tenantId = :tenantId and u.deleted = false and u.active = true ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveUsersForEnvelopForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("from User a where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllAdminUsersForBuyer(String tenantId) {
		// Query query = getEntityManager().createQuery("select u from User u left outer join u.userRole as ur left
		// outer join ur.accessControlList as acl where u.tenantId = :tenantId and acl.aclValue = 'ROLE_ADMIN' ");
		// query.setParameter("tenantId", tenantId);
		// List<User> userList = query.getResultList();
		// LinkedHashSet<User> resultList = new LinkedHashSet<User>();
		// for (User user : userList) {
		// resultList.add(user);
		// }
		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS, pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram.ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN'";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);

		List<Object[]> records = query.getResultList();
		LinkedHashSet<User> resultList = new LinkedHashSet<User>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
		    if(result[3].toString().equals("0")){
				data.setEmailNotifications(false);
			}else{
				data.setEmailNotifications(true);
			}
			}
			data.setLoginId(((String) result[4]));
			data.setDeviceId(((String) result[5]));
			resultList.add(data);
		}

		return new ArrayList<User>(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllAdminPlainUsersForSupplier(String tenantId) {
		// final Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail,
		// u.tenantId) from User u inner join u.userRole ur inner join ur.accessControlList acl where u.tenantId =
		// :tenantId and acl.aclValue = 'ROLE_ADMIN'");
		// query.setParameter("tenantId", tenantId);
		// List<User> userList = query.getResultList();
		// if (CollectionUtil.isNotEmpty(userList)) {
		// return userList;
		// }
		// return null;

		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS,  pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram.ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN'";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);

		List<Object[]> records = query.getResultList();
		LinkedHashSet<User> resultList = new LinkedHashSet<User>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
				if(result[3].toString().equals("0")){
					data.setEmailNotifications(false);
				}else{
					data.setEmailNotifications(true);
				}
			}
			data.setLoginId(((String) result[4]));
			data.setDeviceId(((String) result[5]));
			resultList.add(data);
		}
		if (CollectionUtil.isNotEmpty(resultList)) {
			return new ArrayList<User>(resultList);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllUsersForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.emailNotifications, a.tenantId, a.deleted) from User a where a.deleted =:deleted and a.tenantId =:tenantId order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getPlainUserByLoginId(String loginId) {
		try {
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.emailNotifications, a.tenantId, a.deleted) from User a where upper(a.loginId) =:loginId and a.deleted = false");
			query.setParameter("loginId", loginId.toUpperCase());
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public long findTotalRegisteredOrActiveUserForTenant(String tenantId, boolean isActiveUser) {
		String hql = "select count (u) from User u where u.tenantId = :tenantId ";
		if (isActiveUser) {
			hql += " and u.active = :active ";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (isActiveUser) {
			query.setParameter("active", isActiveUser);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public void deactivateAllUsersExceptAdminUser(Buyer buyer) {
		final Query query = getEntityManager().createQuery("update User u set u.active = false where u.tenantId = :tenantId and upper(u.loginId) <> :loginId ");
		query.setParameter("tenantId", buyer.getId());
		query.setParameter("loginId", buyer.getLoginEmail().toUpperCase());
		int deactivatedUserResult = query.executeUpdate();
		LOG.info("deactivatedUserResult : " + deactivatedUserResult);
	}

	@Override
	public void updateUserBqPageLength(Integer pageLength, String userId) {
		final Query query = getEntityManager().createQuery("update User u set u.bqPageLength = :pageLength where u.id = :userId ");
		query.setParameter("pageLength", pageLength);
		query.setParameter("userId", userId);
		int pageLengthUpdateResult = query.executeUpdate();
		LOG.info("BQ Page Length Update Result : " + pageLengthUpdateResult);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveUsersForTenantAndUserType(String tenantId, UserType userType) {
		final Query query = getEntityManager().createQuery("select a from User a where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active and a.userType = :userType order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userType", userType);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveNormalUsersForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.emailNotifications,a.tenantId, a.deleted) from User a where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active and a.userType = :userType order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userType", UserType.NORMAL_USER);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveNormalUsersForEnvelopForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.emailNotifications, a.tenantId, a.deleted) from User a where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active and a.userType = :userType order by a.name");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userType", UserType.NORMAL_USER);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActivePlainAdminUsersForTenant(String tenantId) {
//		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.User(a.id, a.loginId, a.name, a.communicationEmail, a.tenantId, a.deleted) from User a left outer join a.userRole ur left outer join ur.accessControlList acl where a.deleted =:deleted and a.active = :active and a.tenantId =:tenantId and acl.aclValue = 'ROLE_ADMIN'");
//		query.setParameter("deleted", false);
//		query.setParameter("active", Boolean.TRUE);
//		query.setParameter("tenantId", tenantId);
//		return query.getResultList();

		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS, pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram .ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN' and pu.ACCOUNT_DELETED =:ACCOUNT_DELETED and pu.ACCOUNT_ACTIVE =:ACCOUNT_ACTIVE";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("ACCOUNT_DELETED", false);
		query.setParameter("ACCOUNT_ACTIVE", 1);
		
		List<Object[]> records = query.getResultList();
		LinkedHashSet<User> resultList = new LinkedHashSet<User>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
				if(result[3].toString().equals("0")){
					data.setEmailNotifications(false);
				}else{
					data.setEmailNotifications(true);
				}
			}
			data.setLoginId(((String) result[4]));
			data.setDeviceId(((String) result[5]));
			resultList.add(data);
		}
		if (CollectionUtil.isNotEmpty(resultList)) {
			return new ArrayList<User>(resultList);
		}
		return null;
	}

	@Override
	public void deleteUsers(String tenantId) {
		final Query query = getEntityManager().createQuery("delete from User u where u.tenantId = :tenantId or u.buyer.id = :tenantId");
		query.setParameter("tenantId", tenantId);
		int deactivatedUserResult = query.executeUpdate();
		LOG.info("deactivatedUserResult : " + deactivatedUserResult);

	}

	@Override
	public void deleteNotifactions(String tenantId) {

		final Query query = getEntityManager().createQuery("delete from NotificationMessage u where u.tenantId = :tenantId ");
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserIdList(String tenantId) {
		final Query query = getEntityManager().createQuery("select u.id from User u where u.tenantId = :tenantId or u.buyer.id = :tenantId");
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@Override
	public void deleteSecurityToken(String id) {
		final Query query = getEntityManager().createQuery("delete from SecurityToken u where u.user.id = :tenantId ");
		query.setParameter("tenantId", id);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerUsers(String id) {
		final Query query = getEntityManager().createQuery("delete from User u where u.id = :id");
		query.setParameter("id", id);
		int deactivatedUserResult = query.executeUpdate();
		LOG.info("deactivatedUserResult : " + deactivatedUserResult);

	}

	@Override
	public void setModifyNull(String id) {
		final Query query = getEntityManager().createQuery("update User u set u.modifiedBy = null where u.id = :id ");
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@Override
	public void setCreatedNull(String id) {
		final Query query = getEntityManager().createQuery("update User u set u.createdBy = null where u.id = :id ");
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerNotifactions(String tenantId) {
		final Query query = getEntityManager().createQuery("delete from BuyerNotificationMessage u where u.tenantId = :tenantId ");
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deletePasswordHistory(String uid) {
		final Query query = getEntityManager().createQuery("delete from PasswordHistory u where u.user.id = :tenantId ");
		query.setParameter("tenantId", uid);
		query.executeUpdate();

	}

	@Override
	public void deleteNote(String uid) {
		final Query query = getEntityManager().createQuery("delete from SupplierNoteDocument u where u.createdBy.id = :tenantId ");
		query.setParameter("tenantId", uid);
		query.executeUpdate();

		final Query querynNote = getEntityManager().createQuery("delete from Notes u where u.createdBy.id = :tenantId ");
		querynNote.setParameter("tenantId", uid);
		querynNote.executeUpdate();

	}

	@Override
	public void deleteErpAudit(String uid) {
		final Query query = getEntityManager().createQuery("delete from ErpAudit u where u.actionBy.id = :tenantId ");
		query.setParameter("tenantId", uid);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> getAllAssignedRfxTemplateByUserId(String userId) {
		final Query query = getEntityManager().createQuery("select a.assignedTemplates from User a where a.id =:userId");
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> getAllAssignedPrTemplateByUserId(String userId) {
		final Query query = getEntityManager().createQuery("select a.assignedPrTemplates from User a where a.id =:userId");
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExistsLoginEmailGlobal(String loginEmail) {
		try {
			LOG.info("longinEmail ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + loginEmail);
			Query query1 = getEntityManager().createQuery("from User a where  upper(a.loginId) = :loginEmail");
			query1.setParameter("loginEmail", loginEmail.toUpperCase());
			List<User> userlist = query1.getResultList();
			if (CollectionUtil.isEmpty(userlist)) {
				Query query2 = getEntityManager().createQuery("from Buyer b where  upper(b.loginEmail) = :loginEmail");
				query2.setParameter("loginEmail", loginEmail.toUpperCase());
				if (CollectionUtil.isEmpty(query2.getResultList())) {
					Query query3 = getEntityManager().createQuery("from Supplier c where  upper(c.loginEmail) = :loginEmail");
					query3.setParameter("loginEmail", loginEmail.toUpperCase());
					if (CollectionUtil.isEmpty(query3.getResultList())) {
						return false;

					}
				}
			}
		} catch (Exception e) {
			LOG.info("error" + e.getMessage(), e);
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUserWithTenantCompaniesByLoginId(String loginId) {
		try {
			final Query query = getEntityManager().createQuery("select a from User a left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow left outer join fetch a.financeCompany as fc where upper(a.loginId) = :loginId and a.deleted = false");
			query.setParameter("loginId", loginId.toUpperCase());
			List<User> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getAdminUserForFinance(FinanceCompany financeCompany) {
		List<User> userList = getEntityManager().createQuery("from User u where u.financeCompany = :finance and upper(u.loginId) = :loginId").setParameter("finance", financeCompany).setParameter("loginId", financeCompany.getLoginEmail().toUpperCase()).getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList.get(0);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getPrCreatorUser(String loggedInUserTenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId) from User u inner join u.userRole as ur inner join ur.accessControlList acl where u.tenantId = :tenantId and acl.aclValue = :aclValue and u.active = true and u.deleted = false and u.locked = false ");
		query.setParameter("tenantId", loggedInUserTenantId);
		// String[] role = { "Administrator".toUpperCase(), "PR Creator".toUpperCase(), "EVENT & PR CREATOR" };
		query.setParameter("aclValue", "ROLE_PR_CREATE");
		return query.getResultList();
	}

	@Override
	public boolean getfindAvalableAdminUser(String userid, String tenantId) {
		final Query query = getEntityManager().createQuery("select count(u.id) from User u inner join u.userRole as ur  where u.tenantId = :tenantId and u.id <> :userid and ur.roleName = :roleName ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("roleName", "ADMINISTRATOR");
		query.setParameter("userid", userid);
		return ((Number) query.getSingleResult()).intValue() > 0;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchAllActiveUserForTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c where a.deleted = :deleted and  a.active= :active and a.tenantId= :tenantId order by upper(a.name) ");
		query.setParameter("deleted", false);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllAdminPlainUsersForSupplierNotification(String tenantId) {
		String sql = "SELECT pu.ID ,pu.USER_NAME, pu.COMMUNICATION_EMAIL, pu.EMAIL_NOTIFICATIONS, pu.LOGIN_ID, pu.TENANT_ID, pu.DEVICE_ID  FROM PROC_USER pu, PROC_ROLE_ACL_MAPPING pram  WHERE PU .ROLE_ID = pram .ROLE_ID  AND pu.TENANT_ID = :tenantId AND  pram.ACL_ID   = 'ROLE_ADMIN'";
		final Query query = getEntityManager().createNativeQuery(sql);

		// final Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail,
		// u.loginId,u.tenantId,u.deviceId) from User u inner join u.userRole ur where u.tenantId = :tenantId and
		// ur.accessControlList.aclValue = 'ROLE_ADMIN'");
		query.setParameter("tenantId", tenantId);
		List<Object[]> records = query.getResultList();
		List<User> userList = new ArrayList<>();
		for (Object[] result : records) {
			User data = new User();
			data.setId((String) result[0]);
			data.setName((String) result[1]);
			data.setCommunicationEmail((String) result[2]);
			if(result[3] != null){
				if(result[3].toString().equals("0")){
					data.setEmailNotifications(false);
				}else{
					data.setEmailNotifications(true);
				}
			}
			data.setLoginId(((String) result[4]));
			data.setTenantId((String) result[5]);
			data.setDeviceId((String) result[6]);
			userList.add(data);
		}

		// List<User> userList = query.getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getAdminUserForBuyer(String tenantId) {
		List<User> userList = getEntityManager().createQuery("select distinct u from User u left outer join u.buyer buyer where u.buyer.id = :tenantId and buyer.id=:tenantId and upper(u.loginId) = upper(buyer.loginEmail)").setParameter("tenantId", tenantId).getResultList();
		if (CollectionUtil.isNotEmpty(userList)) {
			return userList.get(0);
		}
		return null;
	}

	@Override
	public void updateLangCodeForUser(String id, String code) {
		final Query query = getEntityManager().createQuery("update User u set u.languageCode =:code where u.id = :id");
		query.setParameter("id", id);
		query.setParameter("code", code);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchAllUsersForTenant(String loggedInUserTenantId, String searchValue, UserType userType) {

		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo(a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted =:deleted and a.tenantId =:tenantId and a.active = :active ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		hql.append(" order by a.name");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchAllUsersForPoApproval(String loggedUserId,String loggedInUserTenantId, String searchValue, UserType userType) {

		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo(a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) " +
				"from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active");

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like :searchValue");
//			hql.append(" and a.loginId not in (:userId) ");
		}

		hql.append(" order by a.name");
		LOG.info(">>>>>>>>. exclude user id "+loggedUserId);
		LOG.info(">>>>>>>>. search user for approval"+hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
//			query.setParameter("userId", loggedUserId);
		}

		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchUnAssignedUsersForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {

		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo(a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo(a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedTemplates at where at.id = :templateId ) ");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		hql.append(" order by a.name");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long fetchFilterCountAllUsersForTenant(String loggedInUserTenantId, String searchValue, UserType userType) {
		StringBuffer hql = new StringBuffer("select count(a) from User a where a.deleted =:deleted and a.tenantId = :tenantId and a.active = :active ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}
		// hql.append(" order by a.name");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public long fetchFilterCountUnAssignedUsersForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {
		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedTemplates at where at.id = :templateId ) ");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}
		// hql.append(" order by a.name");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchUnAssignedUsersForPrTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {

		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedPrTemplates at where at.id = :templateId)  ");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}
		hql.append(" order by a.name");
		EntityManager entityManager = getEntityManager();
		Query query = entityManager.createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}







	@Override
	public long fetchFilterCountUnAssignedUsersForPrTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId) {
		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedPrTemplates at where at.id = :templateId ) ");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}
		// hql.append(" order by a.name");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		return ((Number) query.getSingleResult()).intValue();
	}


	@Override
	public User getUsersForRfxById(String usersId) {
		final Query query = getEntityManager().createQuery("select u from User u left outer join fetch  u.assignedTemplates left outer join fetch u.buyer where u.id = :usersId");
		query.setParameter("usersId", usersId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@Override
	public User getUsersForPrById(String usersId) {
		final Query query = getEntityManager().createQuery("select u from User u left outer join fetch  u.assignedPrTemplates left outer join fetch u.buyer where u.id = :usersId");
		query.setParameter("usersId", usersId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}







	@Override
	public User getUsersNameAndId(String usersId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(u.id,u.name,u.loginId) from User u where u.id = :usersId");
		query.setParameter("usersId", usersId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersNameAndIdForTemplate(List usersIds) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(u.id,u.name,u.loginId) from User u where u.id in (:usersId)");
		query.setParameter("usersId", usersIds);
		return query.getResultList();
		/*
		 * try { } catch (Exception e) { LOG.error(e); return null; }
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findUserListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		final Query query = getEntityManager().createQuery("from User a inner join fetch a.userRole as c left outer join fetch a.supplier as s left outer join fetch a.buyer as by left outer join fetch a.owner as ow where a.tenantId =:tenantId ");
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<User> finalList = query.getResultList();
		if (CollectionUtil.isNotEmpty(finalList)) {
			return finalList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchUnAssignedUsersForSourcingTemplate(String tenantId, String searchValue, UserType userType, String templateId) {
		LOG.info("assignedSourcingTemplates 8");
		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedSourcingTemplates at where at.id = :templateId)  ");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		hql.append(" order by a.name");
		EntityManager entityManager = getEntityManager();
		Query query = entityManager.createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long fetchFilterCountUnAssignedUsersForSourcingTemplate(String tenantId, String searchValue, UserType userType, String templateId) {
		LOG.info("assignedSourcingTemplates 10");
		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedSourcingTemplates at where at.id = :templateId ) ");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		return ((Number) query.getSingleResult()).intValue();

	}

	@Override
	public User getUsersForSourcingFormById(String userID) {
		LOG.info("assignedSourcingTemplates 11");
		final Query query = getEntityManager().createQuery("select u from User u left outer join fetch u.assignedSourcingTemplates left outer join fetch u.buyer where u.id = :usersId");
		query.setParameter("usersId", userID);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPojo> fetchUnAssignedUsersForSPTemplate(String tenantId, String searchValue, UserType userType, String templateId) {

		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UserPojo (a.id, a.loginId, a.name, a.tenantId, a.deleted, a.communicationEmail, a.emailNotifications) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedSupplierPerformanceTemplates at where at.id = :templateId)  ");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		hql.append(" order by a.name");
		EntityManager entityManager = getEntityManager();
		Query query = entityManager.createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long fetchFilterCountUnAssignedUsersForSPTemplate(String tenantId, String searchValue, UserType userType, String templateId) {

		StringBuffer hql = null;
		if (StringUtils.checkString(templateId).length() == 0) {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active ");
		} else {
			hql = new StringBuffer("select count(a) from User a where a.deleted = :deleted and a.tenantId = :tenantId and a.active = :active and a.id not in (select u1.id from User u1 join u1.assignedSupplierPerformanceTemplates at where at.id = :templateId ) ");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(a.name) like (:searchValue) ");
		}
		if (userType != null) {
			hql.append(" and a.userType = :userType ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("deleted", false);
		query.setParameter("active", true);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(templateId).length() > 0) {
			query.setParameter("templateId", templateId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (userType != null) {
			query.setParameter("userType", userType);
		}
		return ((Number) query.getSingleResult()).intValue();

	}
	
	@Override
	public User getUsersForSupplierPerformanceTemplateById(String userId) {
		final Query query = getEntityManager().createQuery("select u from User u left outer join fetch u.assignedSupplierPerformanceTemplates left outer join fetch u.buyer where u.id = :usersId");
		query.setParameter("usersId", userId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchUserByCommunicationEmail(String emailId, String tenantId) {
		//User(String id, String name, String communicationEmail, String tenantId)

		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.User(u.id,u.name,u.communicationEmail,  u.emailNotifications, u.tenantId) from User u where u.communicationEmail in (:emailId) and u.tenantId=:tenantId and u.active = true ");
		query.setParameter("emailId", emailId);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
		/*
		 * try { } catch (Exception e) { LOG.error(e); return null; }
		 */
	}

	@Override
	public void revokeUserAccount(String emailId, String tenantId) {
		final Query query = getEntityManager().createQuery("update User u set u.active = false where u.tenantId = :tenantId and u.communicationEmail = :emailId  and u.active = true ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("emailId", emailId);
		query.executeUpdate();
	}

	@Override
	public User getUserDetailsBySupplier(String supplierId, String loginId) {
		final Query query = getEntityManager().createQuery("from User as u left outer join fetch u.supplier s left outer join fetch u.buyer b left outer join fetch u.assignedTemplates rt inner join fetch u.userRole ur where upper(u.loginId) = :id and u.supplier.id = :supplierId ");
		query.setParameter("id", loginId.toUpperCase());
		query.setParameter("supplierId", supplierId);
		return (User) query.getSingleResult();
	}

	@Override
	public User getUserDetailsByBuyer(String buyerId, String loginId) {
		final Query query = getEntityManager().createQuery("from User as u left outer join fetch u.supplier s left outer join fetch u.buyer b left outer join fetch u.assignedTemplates rt inner join fetch u.userRole ur where upper(u.loginId) = :id and u.buyer.id = :buyerId ");
		query.setParameter("id", loginId.toUpperCase());
		query.setParameter("buyerId", buyerId);
		return (User) query.getSingleResult();
	}

	@Override
	public User getDetailsOfLoggedinBuyerWithBizUnits(String tenantId, String id) {
		LOG.info("getDetailsOfLoggedinBuyerWithBizUnits 1");
		final Query query = getEntityManager().createQuery("from User u  left outer join fetch u.assignedBusinessUnits bu where u.tenantId =:tenantId and u.id =:id");
		query.setParameter("tenantId", tenantId);
		query.setParameter("id", id);
		return (User) query.getSingleResult();
	}

}
