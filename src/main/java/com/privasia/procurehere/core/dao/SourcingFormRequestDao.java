package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RequestComment;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface SourcingFormRequestDao extends GenericDao<SourcingFormRequest, String> {
	/**
	 * @param tenantId
	 * @param formId
	 * @return
	 */
	boolean isExistFormId(String tenantId, String formId);

	/**
	 * @param formId
	 * @return
	 */
	SourcingFormRequest findByFormId(String formId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<SourcingFormRequest> findAllSourcingFormForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param formId
	 * @return
	 */
	SourcingFormRequest getSourcingBqByFormId(String formId);


	/**
	 * @param formId
	 * @return
	 */
	SourcingFormRequest getSourcingSorByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingTemplateCq> getCq(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> getBq(String formId);

	/**
	 * @param requestId
	 * @return
	 */
	SourcingFormTemplate getSourcingFormByReqId(String requestId);

	/**
	 * @param requestId
	 * @returnList<SourcingFormApprovalRequest>
	 */
	List<SourcingFormApprovalRequest> getApproval(String requestId);

	List<SourcingFormRequestCqItem> getCqItembyRequestId(String requestId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return count
	 * @author yogesh
	 */
	long findTotalMyPendingRequestCount(String loggedInUserTenantId, String id);

	/**
	 * get count for request list
	 *
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @author yogesh
	 * @return List<SourcingFormRequest>
	 */
	List<SourcingFormRequest> findTotalMyPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return
	 */
	List<SourcingFormRequest> myDraftRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input TODO
	 * @return
	 */
	long myDraftRequestListCount(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return
	 */
	List<SourcingFormRequest> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input TODO
	 * @return
	 */
	long myPendingRequestListCount(String loggedInUserTenantId, String id, TableDataInput input,List<String> bizUnitIds);

	/**
	 * @param requestId
	 * @return
	 */
	List<SourcingFormRequestBq> getSourcingRequestBq(String requestId);


	/**
	 * @param requestId
	 * @return
	 */
	List<SourcingFormRequestSor> getSourcingRequestSor(String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	List<String> getSourcingRequestBqNames(String requestId);

	/**
	 * @param id
	 * @return
	 */
	String getBusineessUnitnamerequest(String id);

	/**
	 * @param loggedInUserId
	 * @param requestId
	 * @return
	 */
	EventPermissions getUserPemissionsForRequest(String loggedInUserId, String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	long getBqItemCount(String requestId);

	/**
	 * @param formId
	 * @return
	 */
	boolean checkSourcingRequestStatus(String formId);

	List<SourcingFormRequest> searchSourcingRequestByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo);

	List<SourcingFormRequest> myCompletedRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	List<SourcingFormRequest> myApprvedRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequest> myApprvedRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	long myApprovedRequestListCount(String loggedInUserTenantId, String id, TableDataInput input);
	long myApprovedRequestListCountBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	List<SourcingFormRequest> myPendingRequestAppList(String loggedInUserTenantId, String id, TableDataInput input);

	long myPendingRequestAppListCount(String loggedInUserTenantId, String userid, TableDataInput input);

	long finishedRequestCount(String loggedInUserTenantId, TableDataInput input, String userId);
	long finishedRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds);

	List<SourcingFormRequest> getAllSourcingRequestList(User user, String id, TableDataInput input, Date startDate, Date endDate);

	long getAllSourcingRequestListCount(User user, String id, Date startDate, Date endDate);

	long getCancelRequestCount(String loggedInUserTenantId, TableDataInput input, String userId);
	long getCancelRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds);

	List<SourcingFormRequest> myCancelRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequest> myCancelRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	List<SourcingFormRequest> myFinishRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequest> myFinishRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	SourcingFormRequest findByFormIdById(String formId);

	SourcingFormRequest findByApprovalDocumentFormIdById(String formId);

	SourcingFormRequest getSourcingFormForAdditionalApproverById(String rfsId);

	List<SourcingFormApprovalRequest> getApprovalWithOutDone(String rfsId);

	List<EventTeamMember> getPlainTeamMembersForSourcing(String formId);

	SourcingFormTeamMember getTeamMemberByUserIdAndFormId(String formId, String userId);

	List<RequestComment> findAllSourcingCommentsByFormId(String id);

	/**
	 * @param formId
	 * @param tenantId
	 * @return
	 */
	SourcingFormRequest getSourcingFormByFormIdAndTenant(String formId, String tenantId);

	List<SourcingFormRequestCqItem> getCqItembyRequestIdCqId(String requestId, String cqId);

	/**
	 * @param tenantId
	 * @param formId
	 * @return
	 */
	boolean isBudgetCheckingEnabledForBusinessUnit(String tenantId, String formId);

	void deleteRfs(String buyerId);

	List<SourcingFormRequest> findAllSourcingRequestForTenant(String tenantId);

	List<SourcingFormRequestPojo> getAllSourcingWithSearchFilter(String tenantId, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate);

	List<SourcingFormRequest> getAllSourcingFormRequestList(User user, String id, TableDataInput input, Date startDate, Date endDate);

	long getAllSourcingFormRequestFilterList(User user, String id, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param requestId
	 * @return
	 */
	List<String> getNotSectionAddedRfsBq(String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	List<String> getNotSectionItemAddedRfsBq(String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	long findTotaApprovalLevelsRequestCount(String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	List<SourcingFormApprovalRequest> getApprovalRequestList(String requestId);

	/**
	 * @param id
	 * @param associateOwner
	 * @return
	 */
	List<SourcingFormTeamMember> findAssociateOwnerOfRfs(String id, TeamMemberType associateOwner);

	/**
	 * @param user
	 * @param id
	 * @param tdi
	 * @param startDate
	 * @param endDate
	 * @param page_size
	 * @param pageNo
	 * @return
	 */
	List<SourcingFormRequestPojo> getAllSourcingRequestForList(User user, String id, TableDataInput tdi, Date startDate, Date endDate, int page_size, int pageNo);

	/**
	 * @param user
	 * @param id
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getFilteredCountOfSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param user
	 * @param id
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getTotalSourcingRequestCountForList(User user, String id, Date startDate, Date endDate);

	List<SourcingFormRequestPojo> getAllSourcingRequestForCsv(User user, String id, TableDataInput tdi, Date startDate, Date endDate, int page_size, int pageNo);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long getTotalSourcingRequestCountForTenantId(String searchValue, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param tenantId
	 * @param userId
	 * @param start TODO
	 * @return
	 */
	List<SourcingFormRequest> searchSourcingRequestByNameAndRefNumForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId);

	List<SourcingFormRequestPojo> getAllSourcingRequestListForAssignedBizUnit(User user, String id, TableDataInput tdi, Date startDate, Date endDate, int page_size, int pageNo,List<String> businessUnitIds);
	long getFilteredCountOfSourcingRequestListBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds);
	/**
	 * @param user
	 * @param id
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getTotalSourcingRequestCountBizUnit(User user, String id, Date startDate, Date endDate,List<String> businessUnitIds);

	List<SourcingFormRequestPojo> getAllSourcingRequestBizUnitForCsv(User user, String id, TableDataInput tdi, Date startDate, Date endDate, int page_size, int pageNo,List<String> businessUnitIds);

	/**
	 * Changes various ownership to a new owner
	 * @param sourceUserId old owner
	 * @param targetUserId new owner
	 */
	void transferOwnership(String sourceUserId, String targetUserId);
}
