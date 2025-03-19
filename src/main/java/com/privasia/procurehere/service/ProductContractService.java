package com.privasia.procurehere.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractComment;
import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.core.entity.ContractTeamMember;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.ContractPojo;
import com.privasia.procurehere.core.pojo.ContractProductItemPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface ProductContractService {

	ProductContract findProductContractByReferenceNumber(String sapContractNumber, String tenantId);

	/**
	 * @param productContract
	 * @return
	 */

	ProductContract createProductContract(ProductContract productContract);

	/**
	 * @param productContract
	 * @param true1
	 * @return TODO
	 */
	ProductContract update(ProductContract persistObj);

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
	 * @param string
	 * @return
	 */
	ProductContract findProductContractById(String id, String string);

	/**
	 * @param id
	 * @return
	 */
	ProductContract getProductContractById(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findNewUpcomingContractByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore30DayExpireByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore90DayExpireByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractBefore180DayExpireByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @return
	 */
	long findContractGreaterThanSixMonthExpireByTeanantId(String loggedInUserTenantId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param currentDate
	 * @param contractStatus TODO
	 * @return
	 */
	List<ProductContractPojo> findContractListByExpiredDaysBetweenForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param currentDate
	 * @param contractStatus TODO
	 * @return
	 */
	long findTotalFilteredContractByExpiredDaysBetweenForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus);

	/**
	 * @param response
	 * @param file
	 * @param eventArr
	 * @param productContractPojo
	 * @param select_all
	 * @param loggedInUserTenantId
	 * @param formatter
	 * @param userId
	 * @param startDate
	 * @param endDate
	 */
	void downloadCsvFileForContract(HttpServletResponse response, File file, String[] eventArr, ContractPojo productContractPojo, boolean select_all, String loggedInUserTenantId, SimpleDateFormat formatter, String userId, Date startDate, Date endDate);

	/**
	 * @param contractReferenceNumber
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	boolean isExists(String contractReferenceNumber, String loggedInUserTenantId, String id);

	/**
	 * @param productContract
	 * @return
	 */
	ProductContract createContract(ProductContract productContract, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param productListObj
	 * @param decimalChanged TODO
	 * @return TODO
	 */
	ProductContract updateContract(ProductContract productListObj, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, boolean decimalChanged);

	ContractProductItemPojo getProductItemListByProductItemId(String productItemId);

	long findDraftContractByTenantId(String loggedInUserTenantId, String userId);

	long findPendingContractByTenantId(String loggedInUserTenantId, String userId);

	long findContractByStatusForTeanant(String loggedInUserTenantId, String userId, ContractStatus contractStatus);

	List<ContractTeamMember> getPlainTeamMembersForContract(String contractId);

	List<ContractTeamMember> addTeamMemberToList(String contractId, String userId, TeamMemberType memberType, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer);

	ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String id);

	JasperPrint getContractSummaryPdf(String productContract, User userId, String attribute, JRSwapFileVirtualizer virtualizer);

	public List<ContractApprovalUser> fetchAllApprovalUsersByContractId(String id);

	List<ContractComment> findAllContractCommentsByContractId(String id);

	EventPermissions getUserPemissionsForContract(String userId, String contractid);

	long findCountOfContractPendingApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	List<ProductContractPojo> getAllContractForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalFilteredContracForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalCountContractForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	List<ProductContractPojo> findDraftContractListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredDraftContractByTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	List<ProductContractPojo> findPendingContractListByTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredPendingContractByTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	List<ProductContractPojo> findTerminatedContractListByTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredTerminatedContractByTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param response
	 * @param file
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param isbetween
	 * @param greaterThanSixMonth
	 * @param tenantId
	 * @param formatter
	 * @param status TODO
	 * @param expiryFrom TODO
	 * @param expiryTo TODO
	 */
	void downloadCsvFileForContractList(HttpServletResponse response, File file, String userId, Date startDate, Date endDate, boolean isbetween, boolean greaterThanSixMonth, String tenantId, SimpleDateFormat formatter, ContractStatus status, Date expiryFrom, Date expiryTo);

	/**
	 * @param id
	 * @return
	 */
	ProductContract getContractById(String id);

	List<ProductContractItems> findAllContractItemsByContractId(String id);

	/**
	 * @param contractId
	 * @param userId
	 * @param loggedInUser
	 * @return
	 * @throws ApplicationException
	 */
	List<ContractTeamMember> removeTeamMemberfromList(String contractId, String userId, User loggedInUser) throws ApplicationException;

	/**
	 * @param contractId
	 * @param userId
	 * @return
	 */
	ContractTeamMember getContractTeamMemberByUserIdAndPrId(String contractId, String userId);

	/**
	 * @param contractId
	 * @param loggedInUser TODO
	 * @return TODO
	 * @throws ApplicationException
	 */
	ProductContract contractFinish(String contractId, User loggedInUser) throws ApplicationException;

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

}
