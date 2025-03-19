
package com.privasia.procurehere.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.ApprovalDocument;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.MobileRequestPojo;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author pooja
 */

public interface SourcingFormRequestService {
	/**
	 * @param formId
	 * @return
	 */
	SourcingFormRequest loadFormById(String formId);

	/**
	 * @param sourcingForm
	 */
	void updateSourcingFormRequest(SourcingFormRequest sourcingForm);

	/**
	 * @param sourcingTemplateId
	 * @param loggedInUser
	 * @param tenantId
	 * @param businessUnit
	 * @return
	 * @throws ApplicationException
	 */
	SourcingFormRequest copySourcingTemplate(String sourcingTemplateId, User loggedInUser, String tenantId, BusinessUnit businessUnit) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	SourcingFormRequest getSourcingRequestById(String formId);

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
	 * @return
	 */
	List<SourcingFormApprovalRequest> getApproval(String requestId);

	/**
	 * <p>
	 * get CQ by request ID
	 * </p>
	 * 
	 * @param requestId
	 * @return SourcingFormRequestCqItem
	 */
	List<SourcingFormRequestCqItem> getCqItembyRequestId(String requestId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return
	 */
	List<SourcingFormRequestPojo> findTotalMyPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * <p>
	 * method is used for buyer dash board screen
	 * </p>
	 * 
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 * @author yogesh
	 */
	long findTotalMyPendingRequestCount(String loggedInUserTenantId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return myDraftRequestList
	 */
	List<SourcingFormRequest> myDraftRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input TODO
	 * @return myDraftRequestListCount
	 */
	long myDraftRequestListCount(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return myPendingRequestList
	 */
	List<SourcingFormRequestPojo> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input TODO
	 * @return myPendingRequestListCount
	 */
	long myPendingRequestListCount(String loggedInUserTenantId, String id, TableDataInput input,List<String> bizUnitIds);

	/**
	 * @param requestId
	 * @return List<SourcingFormRequestBq>
	 */
	List<SourcingFormRequestBq> getSourcingRequestBq(String requestId);


	/**
	 * @param requestId
	 * @return
	 */
	List<SourcingFormRequestSor> getSourcingRequestSor(String requestId);

	/**
	 * @param requestId
	 * @return List of Bq Names
	 */
	List<String> getSourcingRequestBqNames(String requestId);

	/**
	 * @param sourcingFormRequest
	 * @return
	 */
	SourcingFormRequest update(SourcingFormRequest sourcingFormRequest);

	/**
	 * create rfx event from request
	 * 
	 * @param requestId
	 * @param selectedRfxType
	 * @param auctionType
	 * @param bqId
	 * @param loggedInUser
	 * @param idRfxTemplate
	 * @param businessUnitId
	 * @return
	 * @throws ApplicationException
	 * @throws Exception
	 */
	String createNextEvent(String requestId, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, List<String> docList) throws ApplicationException, Exception;

	/**
	 * @param user
	 * @param reqestId
	 * @return
	 */
	EventPermissions getUserPemissionsForRequest(User user, String reqestId);

	/**
	 * @param requestId
	 * @return
	 */
	long getBqCount(String requestId);

	RequestAudit saveAudit(RequestAudit audit);

	List<RequestAudit> getReqAudit(String formId);

	/**
	 * @param formId
	 * @return
	 */
	boolean checkSourcingRequestStatus(String formId);

	SourcingFormRequest findById(String requestId);

	List<SourcingFormRequest> searchSourcingRequestByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo);

	List<SourcingFormRequestPojo> myDraftRequestPojoList(String loggedInUserTenantId, String id, TableDataInput input);

	List<SourcingFormRequestPojo> myCompletedRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	SourcingFormRequest copyFromSourcingRequest(String formId, User loggedInUser) throws ApplicationException;

	MobileRequestPojo getMobileRequestDetails(String requestId);

	List<SourcingFormRequestPojo> myApprvedRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequestPojo> myApprvedRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	long myApprovedRequestListCount(String loggedInUserTenantId, String id, TableDataInput input);
	long myApprovedRequestListCountBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	List<SourcingFormRequestPojo> myPendingRequestAppList(String loggedInUserTenantId, String id, TableDataInput input);

	long myPendingRequestAppListCount(String loggedInUserTenantId, String id, TableDataInput input);

	SourcingFormRequest getSourcingRequestByIdForSummary(String formId);

	long finishedRequestCount(String loggedInUserTenantId, TableDataInput input, String userId);
	long finishedRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds);

	List<SourcingFormRequestPojo> getAllSourcingRequestList(User user, String id, TableDataInput input, Date startDate, Date endDate);

	long getAllSourcingRequestListCount(User user, String id, Date startDate, Date endDate);

	long getCancelRequestCount(String loggedInUserTenantId, TableDataInput input, String userId);
	long getCancelRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds);

	List<SourcingFormRequestPojo> myCancelRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequestPojo> myCancelRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	List<SourcingFormRequestPojo> myFinishRequestList(String loggedInUserTenantId, String id, TableDataInput input);
	List<SourcingFormRequestPojo> myFinishRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds);

	RfsDocument saveRfsDocument(RfsDocument rfsDocument);

	List<RfsDocument> findAllPlainRfsDocsbyRfsId(String formId);

	RfsDocument findRfsDocById(String removeDocId);

	void removeRfsDocument(RfsDocument rfsDocument);

	void downloadRfsDocument(String docId, HttpServletResponse response) throws Exception;

	void updateRfsDocumentDesc(String docId, String docDesc, String formId, Boolean internal);

	SourcingFormRequest loadFormIdById(String formId);

	List<ApprovalDocument> findAllPlainApprovalDocsbyRfsId(String formId);

	void downloadApprovalDocument(String docId, HttpServletResponse response) throws Exception;

	void updateApprovalDocumentDesc(String docId, String docDesc, String formId);

	ApprovalDocument saveApprovalDocument(ApprovalDocument approvalDocument);

	void removeApprovalDocument(ApprovalDocument approvalDocument);

	ApprovalDocument findApprovalDocById(String removeDocId);

	SourcingFormRequest loadApprovaldocuemntFormIdById(String formId);

	SourcingFormRequest getSourcingFormForAdditionalApproverById(String rfsId);

	void addAdditionalApprover(SourcingFormRequest sourcingAdditionalApprovals, String rfsId, User logInUser);

	void finishAdditionalApprover(SourcingFormRequest sourcingAdditionalApprovals, String rfsId, User logInUser) throws ApplicationException;

	List<SourcingFormTeamMember> addTeamMemberToList(String formId, String userId, TeamMemberType memberType, User loggedInUser);

	List<EventTeamMember> getPlainTeamMembersForSourcing(String formId);

	SourcingFormTeamMember getTeamMemberByUserIdAndFormId(String formId, String userId);

	List<User> removeTeamMemberfromList(String formId, String userId, SourcingFormTeamMember sourcingFormTeamMember);

	JasperPrint getSourcingSummaryPdf(SourcingFormRequest sourcingFormRequest, String attribute);

	/**
	 * @param formId
	 * @param tenantId
	 * @return
	 */
	SourcingFormRequest getSourcingFormByFormIdAndTenant(String formId, String tenantId);

	/**
	 * @param reqId
	 * @param reason
	 */
	void cancelSourcingRequest(String reqId, String reason);

	List<SourcingFormRequestCqItem> getCqItembyRequestIdCqId(String requestId, String id);

	/**
	 * @param tenantId
	 * @param formId
	 * @return
	 */
	boolean isBudgetCheckingEnabledForBusinessUnit(String tenantId, String formId);

	SourcingFormRequest updateSourcingFormRequestApproval(SourcingFormRequest sourcingFormRequest, User loggedInUser);

	List<SourcingFormRequestPojo> getAllExcelSearchSourcingReportForBuyer(String loggedInUserTenantId, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	List<SourcingFormRequestPojo> getAllSourcingFormRequestList(User loggedInUser, Object object, TableDataInput input, Date startDate, Date endDate);

	long getAllSourcingFormRequestFilterList(User user, Object id, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param docId
	 * @return
	 */
	String findUploadFileName(String docId);

	/**
	 * @param formId
	 * @return
	 */
	List<RfsDocumentPojo> findAllPlainRfsDocsbyRfsIdAndUploadBy(String formId);

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
	 * @param response
	 * @param file
	 * @param eventIds
	 * @param sourcingFormRequestPojo
	 * @param startDate
	 * @param endDate
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForSourcing(HttpServletResponse response, File file, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, Date startDate, Date endDate, boolean select_all, String tenantId, User loggedInUser, String id, HttpSession session);

	/**
	 * @param user
	 * @param id
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<SourcingFormRequestPojo> getAllSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate);

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
	 * @return
	 */
	List<SourcingFormRequest> searchSourcingRequestByNameAndRefNumForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId);

	/**
	 * @param sourcingRequest
	 * @param loggedInUser
	 * @return
	 */
	SourcingFormRequest concludeSourcingRequest(SourcingFormRequest sourcingRequest, User loggedInUser);

	List<RfsDocument> findAllPlainRfsDocsbyRfsIdWithInternal(String formId);

	List<SourcingFormRequestPojo> getAllSourcingRequestListForAssignedBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds);
	/**
	 * @param user
	 * @param id
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getFilteredCountOfSourcingRequestListBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds);
	/**
	 * @param user
	 * @param id
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long getTotalSourcingRequestCountListBizUnit(User user, String id, Date startDate, Date endDate,List<String> businessUnitIds);

	void downloadCsvFileForSourcingBizUnit(HttpServletResponse response, File file, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, Date startDate, Date endDate, boolean select_all, String tenantId, User loggedInUser, String id, HttpSession session);


}
