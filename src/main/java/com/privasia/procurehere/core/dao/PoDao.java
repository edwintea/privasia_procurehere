package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoTeamMember;
import com.privasia.procurehere.core.entity.PrTeamMember;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface PoDao extends GenericDao<Po, String> {

	/**
	 * @param poId
	 * @return
	 */
	Po findByPoId(String poId);

	/**
	 * @param poNumber
	 * @param buyerId
	 * @return
	 */
	Po getPoItemByPoNumberAndBuyerId(String poNumber, String buyerId);

	/**
	 * @param poId
	 * @return
	 */
	Po findPoSupplierByPoId(String poId);

	/**
	 * @param tenantId
	 * @param status TODO
	 * @return
	 */
	long findCountOfPoForSupplierBasedOnStatus(String tenantId, PoStatus status);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	List<PoSupplierPojo> findAllSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	long findTotalSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalPo(String tenantId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Po> findAllPo(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredPo(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param poArr
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param searchFilterPoPojo
	 * @return
	 */
	List<PoSupplierPojo> findSearchPoByIds(String tenantId, String[] poArr, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @param status
	 * @return
	 */
	long findPoCountBasedOnStatusAndTenant(String loggedInUser, String tenantId, PoStatus status);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @param status
	 * @return
	 */
	long findPoCountBasedOnStatusAndTenantAndBusinessUnit(String loggedInUser, String tenantId, PoStatus status,List<String> businessUnitIds);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	List<Po> findAllPoByStatus(String loggedInUser, String tenantId, TableDataInput input, PoStatus status);

	/**
	 * @param loggedInUserId TODO
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	long findTotalFilteredPoByStatus(String loggedInUserId, String tenantId, TableDataInput input, PoStatus status);

	/**
	 * @param loggedInUserId TODO
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Po> findAllSearchFilterPo(String loggedInUserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUnitIds);

	/**
	 * @param loggedInUserId TODO
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterPoCount(String loggedInUserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUnitIds);

	/**
	 * @param tenantId
	 * @param poArr
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param searchFilterPoPojo
	 * @param loggedInUser TODO
	 * @return
	 */
	List<Po> findSearchBuyerPoByIds(String tenantId, String[] poArr, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo, String loggedInUser);

	/**
	 * @param tenantId
	 * @return
	 */
	List<Po> findSupplierAllPo(String tenantId);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	List<Po> findAllPoforSharingAll(String supplierId, String buyerId);

	/**
	 * @param poId
	 * @return
	 */
	String getBusineessUnitname(String poId);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param status
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Po> getAllPoFromGlobalSearch(String searchVal, String tenantId, String status, String type, Date startDate, Date endDate);

	/**
	 * @param prId
	 * @return
	 */
	Po findByPrId(String prId);

	/**
	 * @param id
	 * @return
	 */
	Po findSupplierByFavSupplierId(String id);

	/**
	 * @param poNumber
	 * @param tenantId TODO
	 * @return
	 */
	Po findByPoNomber(String poNumber, String tenantId);

	/**
	 * @param poReferenceNumber
	 * @param tenantId
	 * @return
	 */

	Po findByPoReferenceNomber(String poReferenceNumber, String tenantId);

	/**
	 * @param prId
	 * @return
	 */
	Po findPoById(String prId);

	/**
	 * @param id
	 * @return
	 */
	Po getMobilePoDetails(String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<Po> findAllPoByTenantId(String loggedInUserTenantId);

	List<Po> findPoForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate, String userId,String poType, List<String> businessUnitIds,String status);

	List<PoSupplierPojo> findPoSummaryForSupplierCsvReport(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate);

	long findTotalPoSupplierSummaryCountForCsv(String tenantId, Date startDate, Date endDate);

	long findTotalPoSummaryCountForCsv(String tenantId, Date startDate, Date endDate);

	/**
	 * @param poId
	 * @return
	 */
	Po findPoForApprovalById(String poId);

	/**
	 * @param poId
	 * @return
	 */
	String getBusineessUnitnameByPoId(String poId);

	/**
	 * @param userId
	 * @param poId
	 * @return
	 */
	EventPermissions getUserPemissionsForPo(String userId, String poId);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Po> findAllPendingPo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalPendingPo(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	long findTotalFilteredPendingPo(String tenantId, String userId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @param param
	 * @return List of Po
	 */
	List<Po> findPoByParams(String userId, String tenantId, TableDataInput input, Po param);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @param param
	 * @return List of Po
	 */
	long countPoByParams(String userId, String tenantId, TableDataInput input, Po param);

	/**
	 *
	 * @param poId
	 * @param revisePoDetails
	 */
	void updatePoRevisedSnapshot(String poId, String revisePoDetails);

	long findTotalFilteredCancelPo(String tenantId, String userId, TableDataInput input);

	List<Po> findAllCancelPo(String tenantId, String userId, TableDataInput input);

	long findTotalCancelPo(String tenantId, String userId);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @return
	 */
	long findPoPendingCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,PoStatus status,List<String> businessUnitIds);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @return
	 */
	long findPoRevisedCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds);

	/**
	 * @param loggedInUser TODO
	 * @param tenantId
	 * @return
	 */
	long findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds);


    void updateTransferOwnerForPo(String fromUser, String toUser);

	/**
	 * @param poId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForPo(String poId);

	/**
	 * @param poId
	 * @param userId
	 * @return
	 */
	PoTeamMember getPoTeamMemberByUserIdAndPoId(String poId, String userId);

	/**
	 * @param id
	 * @param associateOwner
	 * @return
	 */
	List<PoTeamMember> findAssociateOwnerOfPo(String id, TeamMemberType associateOwner);

	/**
	 * @param poId
	 * @return
	 */
	List<PoTeamMember> getPlainTeamMembersForPoSummary(String poId);

}
