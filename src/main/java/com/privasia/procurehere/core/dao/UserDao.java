package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;

/**
 * @author Ravi
 */
public interface UserDao extends GenericDao<User, String> {

	/*
	 * new
	 * @author Kapil
	 */
	boolean isExist(User user);

	List<User> getAllUserPojo();

	/**
	 * @param value
	 * @return
	 */
	User findByUser(String value);

	/**
	 * @deprecated use {@link UserDao#fetchAllActiveUsersForTenant(String)} instead
	 * @param tenantId
	 * @return
	 */
	@Deprecated
	List<User> findAllActiveUsers(String tenantId);

	/**
	 * @return
	 */
	User getAdminUser();

	/**
	 * @param id
	 * @return
	 */
	User loadById(String id);

	/**
	 * @param role
	 * @return
	 */
	boolean findAllActiveUsersForRole(UserRole role);

	/**
	 * @param loginId
	 * @return
	 */
	User getUserByLoginIdNoTouch(String loginId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllUserByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	User getDetailsOfLoggedinUser(String loginId);

	/**
	 * @param buyerId
	 * @return
	 */
	@Deprecated
	List<RfxTemplate> getAllTemplatesOfBuyer(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	User getDetailsOfLoggedinBuyerWithTemplates(String tenantId, String id);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<UserPojo> findUserForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return Total user count for the tenant based on applied table filter
	 */
	long findTotalFilteredUserForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return Total user count for tenant (including inactive users)
	 */
	long findTotalUserForTenant(String tenantId);

	/**
	 * To fetch all Active User accounts for a particular tenant ({@link Buyer} or {@link Supplier} or {@link Owner})
	 * based on tenantId
	 * 
	 * @param tenantId
	 * @return List of active Users
	 */
	List<User> fetchAllActiveUsersForTenant(String tenantId);

	/**
	 * Fetch the Admin (primary) account of the Buyer
	 * 
	 * @param buyer
	 * @return Buyer admin user account
	 */
	User getAdminUserForBuyer(Buyer buyer);

	/**
	 * Fetch the Admin (primary) account of the Supplier
	 * 
	 * @param supplier
	 * @return Supplier admin user account
	 */
	User getAdminUserForSupplier(Supplier supplier);

	/**
	 * @param tenantId
	 * @return
	 */
	int getActiveUserCountForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveUsersForEnvelopForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminUsersForSupplier(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminUsersForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminPlainUsersForSupplier(String tenantId);

	/**
	 * @param userId
	 * @return
	 */
	User findUserById(String userId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllUsersForTenant(String tenantId);

	/**
	 * @param loginId
	 * @return
	 */
	User getPlainUserByLoginId(String loginId);

	/**
	 * @param tenantId
	 * @param isActiveUser
	 * @return
	 */
	long findTotalRegisteredOrActiveUserForTenant(String tenantId, boolean isActiveUser);

	/**
	 * @param buyer
	 */
	void deactivateAllUsersExceptAdminUser(Buyer buyer);

	/**
	 * @param pageLength
	 * @param userId TODO
	 */
	void updateUserBqPageLength(Integer pageLength, String userId);

	/**
	 * @param tenantId
	 * @param userType
	 * @return
	 */
	List<User> fetchAllActiveUsersForTenantAndUserType(String tenantId, UserType userType);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveNormalUsersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveNormalUsersForEnvelopForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveUsersForAdmin(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActivePlainAdminUsersForTenant(String tenantId);

	/**
	 * @param userId
	 * @return
	 */
	List<RfxTemplate> getAllAssignedRfxTemplateByUserId(String userId);

	List<PrTemplate> getAllAssignedPrTemplateByUserId(String id);

	void deleteNotifactions(String tenantId);

	List<String> getUserIdList(String tenantId);

	void deleteSecurityToken(String id);

	void deleteBuyerUsers(String id);

	void setModifyNull(String id);

	void deleteBuyerNotifactions(String tenantId);

	void deleteUsers(String tenantId);

	void deletePasswordHistory(String uid);

	void deleteNote(String uid);

	void deleteErpAudit(String uid);

	void setCreatedNull(String id);

	boolean isExistsLoginEmailGlobal(String loginEmail);

	/**
	 * @param loginId
	 * @return
	 */
	User getUserWithTenantCompaniesByLoginId(String loginId);

	User getAdminUserForFinance(FinanceCompany financeCompany);

	List<User> getPrCreatorUser(String loggedInUserTenantId);

	boolean getfindAvalableAdminUser(String userid, String tenantId);

	List<User> fetchAllActiveUserForTenantId(String tenantId);

	List<User> getAllAdminPlainUsersForSupplierNotification(String tenantId);

	User getAdminUserForBuyer(String tenantId);

	void updateLangCodeForUser(String id, String code);

	List<UserPojo> fetchAllUsersForTenant(String loggedInUserTenantId, String searchValue, UserType userType);

	List<UserPojo> fetchAllUsersForPoApproval(String loggedUserId,String loggedInUserTenantId, String searchValue, UserType userType);

	long fetchFilterCountAllUsersForTenant(String loggedInUserTenantId, String searchVlaue, UserType userType);

	User getUsersForRfxById(String usersId);

	User getUsersForPrById(String usersId);





	User getUsersNameAndId(String assgnedUser);

	List<User> getUsersNameAndIdForTemplate(List usersIds);

	/**
	 * @param loggedInUserTenantId
	 * @param searchValue
	 * @param userType
	 * @param templateId
	 * @return
	 */
	long fetchFilterCountUnAssignedUsersForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId);

	/**
	 * @param loggedInUserTenantId
	 * @param searchValue
	 * @param userType
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchUnAssignedUsersForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId);

	List<UserPojo> fetchUnAssignedUsersForPrTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId);

	long fetchFilterCountUnAssignedUsersForPrTemplate(String loggedInUserTenantId, String string, UserType userType, String templateId);







	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<User> findUserListForTenantIdForCsv(String tenantId, int pageSize, int pageNo);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param userType
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchUnAssignedUsersForSourcingTemplate(String tenantId, String searchValue, UserType userType, String templateId);

	/**
	 * @param tenantId
	 * @param string
	 * @param userType
	 * @param templateId
	 * @return
	 */
	long fetchFilterCountUnAssignedUsersForSourcingTemplate(String tenantId, String string, UserType userType, String templateId);

	/**
	 * @param userID
	 * @return
	 */
	User getUsersForSourcingFormById(String userID);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param userType
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchUnAssignedUsersForSPTemplate(String tenantId, String searchValue, UserType userType, String templateId);

	/**
	 * @param tenantId
	 * @param string
	 * @param userType
	 * @param templateId
	 * @return
	 */
	long fetchFilterCountUnAssignedUsersForSPTemplate(String tenantId, String string, UserType userType, String templateId);

	/**
	 * @param userId
	 * @return
	 */
	User getUsersForSupplierPerformanceTemplateById(String userId);
	
	List<User> fetchUserByCommunicationEmail(String emailId, String tenantId);

	void revokeUserAccount(String emailId, String tenantId);

	User getUserDetailsBySupplier(String supplierId, String loginId);

	User getUserDetailsByBuyer(String buyerId, String loginId);

	/**
	 * @param tenantId
	 * @return
	 */
	User getDetailsOfLoggedinBuyerWithBizUnits(String tenantId, String id);

}
