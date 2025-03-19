package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractComment;
import com.privasia.procurehere.core.entity.ContractTeamMember;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.pojo.ContractPojo;
import com.privasia.procurehere.core.pojo.ContractProductItemPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductContractDao extends GenericDao<ProductContract, String> {

	ProductContract findByContractByReferenceNumber(String sapContractNumber, String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */

	List<ContractPojo> findProductContractListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);
	List<ContractPojo> findProductContractListForBizUnit(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredProductListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalProductListForTenant(String loggedInUserTenantId, String userId);

	/**
	 * @param id
	 * @param loggedInUser
	 * @return
	 */
	ProductContract findProductContractById(String id, String loggedInUser);

	void deleteProductContractbyTenanatId(String tenantId);

	/**
	 * @return
	 */
	List<ProductContract> contractExpiryNotificationReminderBefore30Days();

	/**
	 * @return
	 */
	List<ProductContract> contractExpiryNotificationReminderBefore90Days();

	/**
	 * @return
	 */
	List<ProductContract> contractExpiryNotificationReminderBefore180Days();

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findNewUpcomingContractByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore30DayExpireByTeanantId(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore90DayExpireByTeanantId(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore180DayExpireByTeanantId(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractGreaterThanSixMonthExpireByTeanantId(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param input
	 * @param isExpired
	 * @param contractStatus TODO
	 * @return
	 */
	List<ProductContractPojo> findContractListByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus);

	/**
	 * @param tenantId
	 * @param input
	 * @param currentDate
	 * @param contractStatus TODO
	 * @return
	 */
	long findTotalFilteredContractByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus);

	long findTotalContractEventCountForCsv(String tenantId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param isbetween
	 * @param greaterThanSixMonth
	 * @param status TODO
	 * @param expiryFrom TODO
	 * @param expiryTo TODO
	 * @return
	 */
	List<ProductContractPojo> findProductContractListForCsvTenantRecords(String tenantId, String userId, Date startDate, Date endDate, boolean isbetween, boolean greaterThanSixMonth, ContractStatus status, Date expiryFrom, Date expiryTo);

	/**
	 * @param contractReferenceNumber
	 * @param buyerId
	 * @param contractId
	 * @return
	 */
	boolean isExists(String contractReferenceNumber, String buyerId, String contractId);

	ContractProductItemPojo getProductItemListByProductItemId(String productItemId);

	long findDraftContractByTenantId(String loggedInUserTenantId, String userId);

	long findPendingContractByTenantId(String loggedInUserTenantId, String userId);

	long findContractByStatusForTeanant(String loggedInUserTenantId, String userId, ContractStatus contractStatus);

	List<ContractPojo> findProductContractListForTenantForCsv(String tenantId, String userId, TableDataInput tableParams, Date startDate, Date endDate);

	List<ContractTeamMember> getPlainTeamMembersForContract(String contractId);

	List<ContractApprovalUser> fetchAllApprovalUsersByContractId(String id);

	ProductContract findProductContractForApprovalById(String id);

	List<ContractComment> findAllContractCommentsByContractId(String id);

	EventPermissions getUserPemissionsForContract(String userId, String contractId);

	long findCountOfContractPendingApprovals(String tenantId, String id, TableDataInput input);

	List<ProductContractPojo> getAllContractForApproval(String tenantId, String id, TableDataInput input);

	List<ProductContractPojo> findDraftContractListForTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredDraftContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredPendingContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	List<ProductContractPojo> findTerminatedContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredTerminatedContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredContracForApproval(String tenantId, String userId, TableDataInput input);

	long findTotalCountContractForApproval(String tenantId, String userId, TableDataInput input);

	List<ProductContractPojo> findPendingContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @return
	 */
	int updateStatusFromApprovedToActive();

	int updateStatusFromActiveToExpired();

	/**
	 * @param contractId
	 * @param userId
	 * @return
	 */
	ContractTeamMember getContractTeamMemberByUserIdAndContractId(String contractId, String userId);

	/**
	 * @param sapContractNumber
	 * @param tenantId
	 * @return
	 */
	ProductContract findProductContractByBuyerId(String sapContractNumber, String tenantId);

	/**
	 * @param productContractId
	 */
	void deleteProductContractById(String productContractId);

	void transferOwnership(String fromUser, String toUser);
}
