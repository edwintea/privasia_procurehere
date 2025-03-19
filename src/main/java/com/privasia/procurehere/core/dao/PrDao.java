package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;
import com.privasia.procurehere.core.entity.PrComment;
import com.privasia.procurehere.core.entity.PrContact;
import com.privasia.procurehere.core.entity.PrTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPrPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Parveen
 */
public interface PrDao extends GenericDao<Pr, String> {
	/**
	 * @param prId
	 * @return
	 */
	Pr findByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<User> findEditorsByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<User> findViewersByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	Pr findPrSupplierByPrId(String prId);

	/**
	 * @param contactId
	 * @return
	 */
	PrContact getPrContactById(String contactId);

	/**
	 * @param contactId
	 */
	void deletePrContact(String contactId);

	/**
	 * @param prContact
	 * @param prId TODO
	 * @return
	 */
	boolean isExists(PrContact prContact, String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrComment> getAllPrCommentByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrContact> findAllPrContactsByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrApproval> getAllPrApprovalsByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForPr(String prId);

	/**
	 * @param prId
	 * @param userId
	 * @return
	 */
	PrTeamMember getPrTeamMemberByUserIdAndPrId(String prId, String userId);

	/**
	 * @param userId TODO
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Pr> findAllPendingPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @param input TODO
	 * @return
	 */
	long findTotalFilteredPendingPr(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @param input TODO
	 * @return
	 */
	long findTotalDraftPr(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);

	/**
	 * @param userId TODO
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Pr> findAllDraftPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId TODO
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredDraftPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId TODO
	 * @param tenantId
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<Pr> findAllPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param searchVal
	 * @param tenantId TODO
	 * @param userId TODO
	 * @param status TODO
	 * @param type TODO
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<Pr> getAllPrPoFromGlobalSearch(String searchVal, String tenantId, boolean isSupplier, String userId, String status, String type, Date startDate, Date endDate);

	/**
	 * @param userId TODO
	 * @param tenantId
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalFilteredPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findTotalApprovedPrs(String tenantId, String userId,List<String> businessUnits);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId TODO
	 * @param pageNo TODO
	 * @return
	 */
	List<Pr> searchPrByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo);

	/**
	 * @param loggedInUserTenantId
	 * @param userId TODO
	 * @param input
	 * @return
	 */
	List<Pr> findAllPrForTenant(String loggedInUserTenantId, String userId, TableDataInput input);

	/**
	 * @param userId
	 * @param prId
	 * @return
	 */
	EventPermissions getUserPemissionsForPr(String userId, String prId);

	/**
	 * @param prId
	 * @return
	 */
	Pr findPrForApprovalById(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrComment> findAllPrCommentsByPrId(String prId);

	List<Pr> getAllPrByLoginId(String loginId);

	long findTotalPendingPr(String tenantId, String userId,List<String> businessUnitIds);

	/**
	 * @param prId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForPr(String prId);

	/**
	 * @param id
	 * @return
	 */
	List<PrApprovalUser> fetchAllApprovalUsersByPrId(String id);

	/**
	 * @param prId
	 * @return
	 */
	boolean checkTemplateStatusForPr(String prId);

	/**
	 * @param pdId
	 * @return
	 */
	String getBusineessUnitname(String prId);

	/**
	 * @param id
	 * @return
	 */
	Pr getMobilePrDetails(String id);

	/**
	 * @param tenantId
	 * @param status
	 * @param filter TODO
	 * @return
	 * @throws Exception
	 */
	long findPrPoCountForTenant(String tenantId, PrStatus status, RequestParamPojo filter) throws Exception;

	/**
	 * @param tenantId
	 * @param approved
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	BigDecimal findPrPoValueForTenant(String tenantId, PrStatus approved, RequestParamPojo filter) throws Exception;

	/**
	 * @param prArr
	 * @return
	 */
	List<Pr> findPrByIds(String[] prArr);

	/**
	 * @param prArr
	 * @param select_all
	 * @param tenantId
	 * @param endDate
	 * @param startDate
	 * @param searchFilterPrPojo
	 */
	void updateSentPoReport(String[] prArr, boolean select_all, String tenantId, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param statuses
	 * @return
	 */
	List<Pr> findAllSearchFilterPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses);

	/**
	 * @param userId
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param statuses
	 * @return
	 */
	long findTotalSearchFilterPo(String userId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses);

	/**
	 * @param prId
	 * @return
	 */
	Pr getPrForErpById(String prId);

	/**
	 * @param prId
	 * @param tenantId TODO
	 * @return
	 */
	Pr findPrBySystemGeneratedPrIdAndTenantId(String prId, String tenantId);

	/**
	 * @param prDocNo
	 * @return
	 */
	Pr findPrByErpDocNo(String prDocNo);

	long findTotalPrTransfer(String tenantId, String userId,List<String> businessUnitIds);

	List<Pr> findAllPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	boolean isExistsPrId(String tenantId, String id);

	/*
	 *
	 */
	void deletePrContactsByPrId(String prId);

	List<Pr> findAllSearchFilterPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType);
	List<Pr> findAllSearchFilterPrBizUnit(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType,List<String> businessUnitIds);

	long findTotalFilteredPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prstatus,String selectedStatus,String prType,List<String> businessUnitIds);

	long findTotalPr(String tenantId, String userId);

	List<Pr> findSupplierAllPo(String tenantId);

	List<Pr> findBuyerAllPo(String tenantId, boolean isPo);

	void deletePrDoc(String tenantId);

	List<String> getPrIdList(String tenantId);

	List<String> getPrAprovalIdList(String prId);

	void removePrApprovalUserByPrApprove(String prApproveId);

	void removePrApproval(String prId);

	void deletePrAudit(String tenantId);

	void deletePrComments(String tenantId);

	void deletePrContacts(String tenantId);

	void deletePrItem(String tenantId);

	void deletePrItemParent(String tenantId);

	void deletePrTeam(String tenantId);

	void deletePr(String prId);

	void deletePrTemplateFieldByTanent(String tenantId);

	List<String> getPrTemplateIdList(String tenantId);

	void deletePrTemplateApprovalUserByAproval(String apid);

	void deletePrTemplateApprovalById(String apid);

	void deletePrTemplateByTanent(String tenantId);

	List<String> getPrTemplateApprovalIdList(String tenantId);

	void deletePrTemplateById(String prTemplateId);

	void deletePrrecord(String prTemplateId);

	void deletePoDoc(String prId);

	void deleteErpSettings(String tenantId);

	void deletePrTeamMember(String prTemplateId);

	List<Pr> findAllSearchFilterPrReport(String tenantId, String[] prArr, boolean select_all, SearchFilterPrPojo searchFilterPrPojo, Date startDate, Date endDate, String userId);

	Pr findByPrIdForFinance(String prId);

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 */

	List<FinancePo> getPoForDateBetween(Date startDate, Date endDate);

	List<Pr> findAllPrWithTemplateForTenant(String loggedInUserTenantId, String userId, TableDataInput input);

	public void deletePrForFinance(String prId);

	List<Pr> findSearchPoByIds(String tenantId, String[] prArr, boolean select_all, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo);

	long findTotalPoList(String tenantId, String userId, List<PrStatus> prStatus);

	long findTotalNotDraftPrList(String tenantId, String userId, List<PrStatus> prStatus,String selectedStatus,String prType);

	long findTotalPoReportList(String tenantId, String userId, List<PrStatus> prStatus);

	long findTotalFilteredPoReportList(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus);

	void deletePrrecordForClose(String prTemplateId);

	Pr findPrBUAndCCForBudgetById(String prId);

	/**
	 * @param id
	 * @return
	 */
	Pr findSupplierByFavSupplierId(String id);

	/**
	 * @param id
	 * @param associateOwner
	 * @return
	 */
	List<PrTeamMember> findAssociateOwnerOfPr(String id, TeamMemberType associateOwner);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param prIds
	 * @param searchFilterPrPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @return
	 */
	List<Pr> findPrForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] prIds, SearchFilterPrPojo searchFilterPrPojo, boolean select_all, Date startDate, Date endDate, String userId,List<String> businessUnitIds,String prType,String status);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long getPrCountBySearchValueForTenant(String searchValue, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param start
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	List<Pr> findPrByNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId);

	/**
	 *
	 * @param fromUserId
	 * @param toUserId
	 */
	void transferOwnership(String fromUserId, String toUserId);

    long findTotalCompletePr(String tenantId, String userId,List<String> businessUnitIds);

	long findTotalCancelledPr(String tenantId, String userId,List<String> businessUnitIds);

	Pr findPrByPrId(String prId);
}
