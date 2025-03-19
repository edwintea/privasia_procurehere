/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.pojo.UserRevocationIntegrationPojo;

/**
 * @author Arc
 */
public interface UserService {

	/*
	 * NEW
	 * @AUTHOR KAPIL
	 */
	String saveUsers(User user, String generatedPassword) throws ApplicationException;

	void updateUsers(User user) throws ApplicationException;

	void deleteUsers(User user) throws ApplicationException;

	boolean isExists(User user);

	List<UserPojo> getAllUserPojo();

	User getUsersById(String id);

	/*
	 * END neW
	 */

	User getUserByLoginIdNoTouch(String loginEmail);

	/**
	 * @return
	 */
	List<User> getUsers();

	/**
	 * @param userId
	 * @return
	 */
	User findUserById(String userId);

	/**
	 * @param user
	 * @return TODO
	 */
	User saveUser(User user);

	/**
	 * @param user
	 * @return
	 */
	User updateUser(User user);

	/**
	 * @param user
	 */
	void deleteUser(User user);

	/**
	 * @return
	 */
	User getCurrentLoggedInUser();

	/**
	 * @param loginId
	 * @return
	 */
	User getUserByLoginId(String loginId);

	/**
	 * @param loginName
	 * @return
	 */
	User getUserByLoginName(String loginName);

	/**
	 * @param user
	 */
	void sendPasswordResetEmail(User user);

	/**
	 * @param tenantId
	 * @return
	 */

	User getDetailsOfLoggedinUser(String tenantId);

	/**
	 * @param buyerId
	 * @return
	 */
	List<RfxTemplate> getAllTemplatesOfBuyer(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	User getDetailsOfLoggedinBuyerWithTemplates(String tenantId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<UserPojo> findUserForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredUserForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalUserForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveUsersForTenant(String tenantId);

	/**
	 * @param buyer
	 * @return
	 */
	User getAdminUserForBuyer(Buyer buyer);

	/**
	 * List<User> fetchAllActiveUsersForEnvelopForTenant(String tenantId)
	 * 
	 * @param supplier
	 * @return
	 */
	User getAdminUserForSupplier(Supplier supplier);

	/**
	 * @param tenantId
	 * @return
	 */
	List<PrTemplate> getAllPrTemplatesOfBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllActiveUsersForEnvelopForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminUsersForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminUsersForSupplier(String tenantId);

	void toggleAdminAccountStatus(User adminUser, String buyerId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> getAllAdminPlainUsersForSupplier(String tenantId);

	/**
	 * @param userId
	 * @return
	 */
	User findTeamUserById(String userId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<User> fetchAllUsersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param isActiveUser
	 * @return
	 */
	long findTotalRegisteredOrActiveUserForTenant(String tenantId, boolean isActiveUser);

	/**
	 * @param loginEmail
	 * @return
	 */
	User getPlainUserByLoginId(String loginEmail);

	/**
	 * @param pageLength
	 * @param userId TODO
	 */
	void updateUserBqPageLength(Integer pageLength, String userId);

	void toggleErpStatus(User adminUser, String buyerId);

	List<User> fetchAllActiveUsersForTenantAndUserType(String tenantId, UserType userType);

	/**
	 * @param id
	 * @return
	 */
	User findById(String id);

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
	 * @param response
	 * @param loggedInUserTenantId
	 * @return
	 */
	void userDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId);

	String generateUserZip(ZipOutputStream zos, String strTimeZone, User user);

	User getAdminUser();

	boolean isExistsLoginEmailGlobal(String loginEmail);

	void toggleFinanceAdminAccountStatus(User adminUser, String financeid);

	User getAdminUserForFinance(FinanceCompany company);

	List<User> getPrCreatorUser(String loggedInUserTenantId);

	/**
	 * @param user
	 * @return
	 */
	User updateUserPlain(User user);

	User findUserWithRoleById(String userId);

	boolean findAvalableAdminUser(String id, String tenantId);

	List<User> fetchAllActiveUserForTenantId(String loggedInUserTenantId);

	List<User> getAllAdminPlainUsersForSupplierNotification(String tenantId);

	User getAdminUserForBuyer(String tenantId);

	void updateLangCodeForUser(String id, String code);

	User findUserByLoginId(String loginName);

	List<UserPojo> fetchAllUsersForTenant(String loggedInUserTenantId, String searchValue, UserType userType);

	List<UserPojo> fetchAllUsersForPoApproval(String loggedInUserId,String loggedInUserTenantId, String searchValue, UserType userType);

	User getUsersForRfxById(String usersId);

	User getUsersForPrById(String userId);




	User getUsersNameAndId(String assgnedUser);

	List<User> getUsersNameAndIdForTemplate(List usersIds);

	/**
	 * @param loggedInUserTenantId
	 * @param searchValue
	 * @param userType
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchAllUsersForTenantForRfxTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId);

	List<UserPojo> fetchAllUsersForTenantForPrTemplate(String loggedInUserTenantId, String searchValue, UserType userType, String templateId);




	/**
	 * @param response
	 * @param file
	 * @param tenantId
	 */
	void downloadUserCsvFile(HttpServletResponse response, File file, String tenantId, HttpSession session);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param type
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchAllUsersForTenantForSourcingTemplate(String tenantId, String searchValue, UserType type, String templateId);

	/**
	 * @param userID
	 * @return
	 */
	User getUsersForSourcingFormById(String userID);

	/**
	 * @param tenantId
	 * @return
	 */
	List<SourcingFormTemplate> getAllSourcingTemplatesOfBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param type
	 * @param templateId
	 * @return
	 */
	List<UserPojo> fetchAllUsersForTenantForSPTemplate(String tenantId, String searchValue, UserType type, String templateId);

	/**
	 * @param userId
	 * @return
	 */
	User getUsersForSupplierPerformanceTemplateById(String userId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceTemplate> getAllSpTemplatesOfBuyer(String tenantId);
	
	List<User> fetchUserByCommunicationEmail(String emailId, String tenantId);
	
	List<String> userUpdate(List<User> users, String emailId, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<BusinessUnit> getAllBizUnitOfBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	User getDetailsOfLoggedinBuyerWithBizUnits(String tenantId, String id);
}
