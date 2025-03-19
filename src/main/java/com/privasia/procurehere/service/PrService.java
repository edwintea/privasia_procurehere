package com.privasia.procurehere.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Event;
import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoDocument;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;
import com.privasia.procurehere.core.entity.PrAudit;
import com.privasia.procurehere.core.entity.PrComment;
import com.privasia.procurehere.core.entity.PrContact;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.PrTeamMember;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.ErpPoPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.MobilePrPojo;
import com.privasia.procurehere.core.pojo.PrPojo;
import com.privasia.procurehere.core.pojo.PrResponseErpPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPrPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Parveen
 */
public interface PrService {

	/**
	 * @return
	 */
	Pr savePr(User loggedInUser);

	/**
	 * @param prId
	 * @return
	 */
	Pr getLoadedPrById(String prId);

	/**
	 * @param persistObj
	 * @return
	 */
	Pr updatePr(Pr persistObj);

	/**
	 * @param prDocument
	 * @return
	 */
	PrDocument savePrDocument(PrDocument prDocument);

	/**
	 * @param prId
	 * @return
	 */
	List<PrDocument> findAllPrDocsbyPrId(String prId);

	/**
	 * @param docId
	 * @return
	 */
	PrDocument findPrDocById(String docId);

	/**
	 * @param prDocument
	 */
	void removePrDocument(PrDocument prDocument);

	/**
	 * @param persistObj
	 * @param supplierChoice
	 * @param isDraft
	 * @return
	 */
	Pr updatePrSupplier(Pr persistObj, String supplierChoice, boolean isDraft);

	/**
	 * @param prItem
	 * @return
	 */
	boolean isExists(PrItem prItem);

	/**
	 * @param item
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	PrItem savePrItem(PrItem item, User loggedInUser) throws Exception;

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> findAllPrItemByPrId(String prId);

	/**
	 * @param prId
	 * @param prItemId
	 * @return
	 */
	PrItem getPrItembyPrIdAndPrItemId(String prId, String prItemId);

	/**
	 * @param prItem
	 * @throws NotAllowedException
	 * @throws Exception
	 */
	void updatePrItem(PrItem prItem) throws Exception, NotAllowedException;

	/**
	 * @param prItemIds
	 * @param prId
	 * @return
	 */
	List<PrItem> deletePrItems(String[] prItemIds, String prId);

	/**
	 * @param prItem
	 * @throws NotAllowedException
	 */
	void reOrderPrItems(PrItem prItem) throws NotAllowedException;

	/**
	 * @param itemId
	 * @return
	 */
	PrItem getPrItembyPrItemId(String itemId);

	/**
	 * @param label
	 * @param prId
	 */
	void deletefieldInPrItems(String label, String prId);

	List<User> findEditorsByPrId(String prId);

	List<User> findViewersByPrId(String prId);

	List<User> addEditor(String prId, String userId);

	List<User> addViewer(String prId, String userId);

	List<User> removeEditorUser(String eventId, String userId);

	List<User> removeViwer(String prId, String userId);

	/**
	 * @param prId
	 * @param level
	 * @return
	 */
	PrItem getParentbyLevelId(String prId, Integer level);

	/**
	 * @param prId
	 */
	void deletePrItemsbyPrId(String prId);

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
	 * @param prId
	 * @return
	 */
	boolean isExistsPrContact(PrContact prContact, String prId);

	/**
	 * @param id
	 * @return
	 */
	Pr getPrById(String id);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> findAllPrItemByPrIdForSummary(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrComment> findAllPrCommentByPrId(String prId);

	/**
	 * @param id
	 * @return
	 */
	List<PrContact> findAllPrContactsByPrId(String id);

	/**
	 * @param prId
	 * @return
	 */
	List<PrApproval> getAllPrApprovalsByPrId(String prId);

	/**
	 * @param pr
	 * @param isDraft
	 * @return
	 */
	Pr prCreate(Pr pr, boolean isDraft, User loggedInUser);

	/**
	 * @param prId
	 * @param userId
	 * @param memberType
	 * @return
	 */
	List<PrTeamMember> addTeamMemberToList(String prId, String userId, TeamMemberType memberType);

	// /**
	// * @param prId
	// * @return
	// */
	// List<EventTeamMember> getTeamMembersForPr(String prId);

	/**
	 * @param prId
	 * @param userId
	 * @param dbTeamMember TODO
	 * @return
	 */
	List<User> removeTeamMemberfromList(String prId, String userId, PrTeamMember dbTeamMember);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Pr> findAllPendingPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalFilteredPendingPr(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalDraftPr(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> findAllChildPrItemByPrId(String prId);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Pr> findAllDraftPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredDraftPr(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<Pr> findAllPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalFilteredPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalApprovedPrs(String tenantId, String userId,List<String> businessUnits);

	/**
	 * @param templateId
	 * @param loggedInUser
	 * @param tenantId
	 * @return
	 * @throws ApplicationException
	 */
	Pr copyFromTemplate(String templateId, User loggedInUser, String tenantId, BusinessUnit unit) throws ApplicationException;

	/**
	 * @param productId
	 * @return
	 */
	boolean checkProductInUse(String productId);

	/**
	 * @param prId
	 * @param loggedInUser
	 * @return
	 * @throws ApplicationException
	 */
	Pr copyFrom(String prId, User loggedInUser, BusinessUnit businessUnit) throws ApplicationException;

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId TODO
	 * @param pageNo
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
	 * @param pr
	 * @param loggedInUser TODO
	 * @return
	 */
	Pr updatePrApproval(Pr pr, User loggedInUser);

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
	List<PrComment> findAllPrCommentsByPrId(String prId);

	/**
	 * @param pr
	 * @param strTimeZone TODO
	 * @return
	 */
	JasperPrint getPrSummaryPdf(Pr pr, String strTimeZone);

	void updateEventDocumentDesc(String docId, String docDesc, String prId);

	/**
	 * @param pr
	 */
	void sendPrFinishMail(Pr pr);

	/**
	 * @param prId
	 * @return
	 */
	Pr findPrById(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrDocument> findAllPlainPrDocsbyPrId(String prId);

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
	 * @param id
	 * @return
	 */
	MobilePrPojo getMobilePrDetails(String id);

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
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadPrDocument(String docId, HttpServletResponse response) throws Exception;

	/**
	 * @param id
	 * @param response
	 * @param session
	 */
	void downloadPrSummary(String id, HttpServletResponse response, HttpSession session);

	/**
	 * @param poDocument
	 */
	PoDocument savePoDocument(PoDocument poDocument);

	/**
	 * @param prId
	 * @return
	 */
	List<PoDocument> findAllPlainPoDocsbyPrId(String prId);

	/**
	 * @param removeDocId
	 * @return
	 */
	PoDocument findPoDocById(String removeDocId);

	/**
	 * @param poDocument
	 */
	void removePoDocument(PoDocument poDocument);

	/**
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadPoDocument(String docId, HttpServletResponse response) throws Exception;

	/**
	 * @param docId
	 * @param docDesc
	 * @param prId
	 * @param docType
	 */
	void updatePoDocumentDesc(String docId, String docDesc, String prId, String docType);

	/**
	 * @param tenantId
	 * @param prArr
	 * @param response TODO
	 * @param session TODO
	 * @param endDate
	 * @param startDate
	 * @param select_all
	 * @param searchFilterPrPojo
	 */
	void downloadPoReports(String tenantId, String[] prArr, HttpServletResponse response, HttpSession session, boolean select_all, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo);

	/**
	 * @param prArr
	 * @return
	 */
	List<Pr> findPrByIds(String[] prArr);

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
	 * @param object
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param statuses
	 * @return
	 */
	long findTotalSearchFilterPo(String userId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses);

	/**
	 * @param pr
	 * @param audit TODO
	 */
	void updatePoStatus(Pr pr, PrAudit audit);

	/**
	 * @param prId
	 * @return
	 */
	Pr getPrForErpById(String prId);

	/**
	 * @param prResponseErpPojo
	 * @param erpSetup TODO
	 * @return
	 */
	Pr updatePrResponse(PrResponseErpPojo prResponseErpPojo, ErpSetup erpSetup);

	/**
	 * @param poResponseErpList
	 * @return
	 */
	Pr updatePoResponse(List<PrResponseErpPojo> poResponseErpList);

	long findTotalPrTransfer(String tenantId, String userId,List<String> businessUnits);

	List<Pr> findAllPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	long findTotalFilteredPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate);

	boolean isExistsPrId(String tenantId, String id);

	/**
	 * @param prId
	 */
	void deletePrContactsByPrId(String prId);

	PrTeamMember getPrTeamMemberByUserIdAndPrId(String prId, String userId);

	List<Pr> findAllSearchFilterPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses,String selectedStatus,String prType);
	List<Pr> findAllSearchFilterPrBizUnit(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses,String selectedStatus,String prType,List<String> businessUnitIds);

	void downloadPRReports(List<PrPojo> prPojo, HttpServletResponse response, HttpSession session);

	long findTotalFilteredPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses,String selectedStatus,String prType,List<String> businessUnitIds);

	long findTotalPr(String tenantId, String userId);

	List<Pr> findSupplierAllPo(String tenantId);

	List<Pr> findBuyerAllPo(String tenantId, boolean isPo);

	List<Pr> findAllSearchFilterPrReport(String tenantId, String[] prArr, boolean select_all, SearchFilterPrPojo searchFilterPrPojo, Date startDate, Date endDate, String userId);

	void downloadFinancePoReportsForAdmin(String dateTimeRange, HttpServletResponse response, HttpSession session);

	String getBusineessUnitname(String prId);

	void sendPrFinishMailToSupplier(Pr pr);

	Pr copyFromTemplateWithAward(
			String templateId, User createdBy, User loggedInUser, String tenantId,
			BusinessUnit buyerbusinessUnit, FavouriteSupplier supplier,
			Map<String, List<PrItem>> sections, Event rfxEvent) throws ApplicationException, Exception;

	PrItem savePrItemBare(PrItem prItem) throws Exception;

	List<PrItem> deletePrItemsByPrId(String prId);

	List<PrItem> findAllLoadedChildPrItemByPrId(String prId);

	long findTotalPoList(String tenantId, String userId, List<PrStatus> prStatus);

	MobilePrPojo getMobilePrDetailsSupplier(String id);

	long findTotalNotDraftPrList(String tenantId, String userId, List<PrStatus> prStatus,String selectedStatus,String prType);

	/**
	 * @param prId
	 * @return
	 */
	Pr getPlainPrById(String prId);

	/**
	 * @param prId
	 * @return
	 */
	Long findProductCategoryCountByPrId(String prId);

	/**
	 * @param prId
	 * @param tenantId
	 * @return
	 */
	Pr findPrBySystemGeneratedPrIdAndTenantId(String prId, String tenantId);

	long findTotalPoReportList(String tenantId, String object, List<PrStatus> prStatus);

	long findTotalFilteredPoReportList(String object, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus);

	Pr findPrBUAndCCForBudgetById(String prId);

	Pr findPrApprovalForBudgetById(String id);

	// Helper method to replace smart quotes
	String replaceSmartQuotes(String input);

	/**
	 * @param erpPoList
	 * @param buyerId TODO
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	void createPo(ErpPoPojo erpPoList, String buyerId) throws NoSuchMessageException, ApplicationException;

	Po createPo(User loggedInUser, Pr pr) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	Pr findSupplierByFavSupplierId(String id);

	/**
	 * @param response
	 * @param file
	 * @param prArr
	 * @param startDate
	 * @param endDate
	 * @param searchFilterPrPojo
	 * @param select_all
	 * @param tenantId
	 * @param userId
	 */
	void downloadCsvFileForPrList(HttpServletResponse response, File file, String[] prArr, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo, boolean select_all, String tenantId, String userId, List<PrStatus> statuses, HttpSession session,String prType,String status);

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
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	List<Pr> findPrByNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId);

	long doMultipleApproval(String[] prId, User loggedInUser, String remarks, boolean b) throws Exception;

	long findTotalCompletePr(String loggedInUserTenantId, String id,List<String> businessUnitIds);

	long findTotalCancelledPr(String loggedInUserTenantId, String id,List<String> businessUnitIds);

	Pr findPrbyprId(String prId);
}
