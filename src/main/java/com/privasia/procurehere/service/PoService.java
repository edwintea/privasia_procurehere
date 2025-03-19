package com.privasia.procurehere.service;

import java.io.File;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.PoItemPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Nitin Otageri
 */
public interface PoService {

	/**
	 * @return
	 */
	@Deprecated
	Po createPo(Po persistObj);

	/**
	 * @return
	 */
	Po savePo(Po persistObj);

	/**
	 * @param persistObj
	 * @return
	 */
	Po updatePo(Po persistObj);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> findAllPoItemByPoIdForSummary(String poId);

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
	 * @param poId
	 * @return
	 */
	Po findPoById(String poId);

	/**
	 * @param tenantId
	 * @param poArr
	 * @param response
	 * @param session
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param searchFilterPoPojo
	 */
	void downloadPoReports(String tenantId, String[] poArr, HttpServletResponse response, HttpSession session, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo);

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
	long findPoCountBasedOnStatusAndTenantAndBuisnessUnit(String loggedInUser, String tenantId, PoStatus status,List<String> businessUnitIds);

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
	 * @param poId
	 * @return
	 */
	Po getLoadedPoById(String poId);

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
	List<PoItem> findAllPoItemByPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> findAllPoItemByPoIdPojo(String poId);


	/**
	 * @param poId TODO
	 * @param poStatus TODO
	 * @param remarks TODO
	 * @param loggedInUser TODO
	 * @param virtualizer TODO
	 * @throws ApplicationException
	 */
	void updatePoStatus(String poId, PoStatus poStatus, String remarks, User loggedInUser, JRSwapFileVirtualizer virtualizer) throws ApplicationException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

	/**
	 * @param poId TODO
	 * @param poStatus TODO
	 * @param remarks TODO
	 * @param loggedInUser TODO
	 * @param virtualizer TODO
	 * @throws ApplicationException
	 */
	void handlePoAction(String poId, PoStatus poStatus, String remarks, User loggedInUser, JRSwapFileVirtualizer virtualizer) throws ApplicationException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

	/**
	 * @param po
	 * @param loggedInUser TODO
	 * @return
	 */
	Po updatePoApproval(Po po, User loggedInUser);

	/**
	 * @param loggedInUserId TODO
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Po> findAllSearchFilterPo(String loggedInUserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUntiIds);

	/**
	 * @param loggedInuserId TODO
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterPoCount(String loggedInuserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUntiIds);

	/**
	 * @param tenantId
	 * @param poArr
	 * @param response
	 * @param session
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param searchFilterPoPojo
	 * @param loggedInUser
	 */
	void downloadBuyerPoReports(String tenantId, String[] poArr, HttpServletResponse response, HttpSession session, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo, String loggedInUser);

	/**
	 * @param poId
	 * @return
	 */
	Po loadPoById(String poId);

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
	String getBusinessUnitname(String poId);

	/**
	 * @param po
	 * @param virtualizer
	 * @return
	 */
	JasperPrint getBuyerPoPdf(Po po, JRSwapFileVirtualizer virtualizer);

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
	 * @param poId
	 * @return
	 */
	Po findById(String poId);

	/**
	 * @param poNumber
	 * @param tenantId TODO
	 * @return
	 */
	Po findPoByPoNumber(String poNumber, String tenantId);


	/**
	 * @param poReferenceNumber
	 * @param tenantId
	 * @return
	 */
	Po findPoByReferenceNumber(String poReferenceNumber, String tenantId);

	List<Po> findAllPoByTenantId(String loggedInUserTenantId);

	void downloadCsvFileForPoSummary(HttpServletResponse response, File file, String[] eventArr, SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String loggedInUserTenantId, String loggedInUser, HttpSession session,String poType,String status);

	void downloadCsvFileForPoSupplierSummary(HttpServletResponse response, File file, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String loggedInUserTenantId, String loggedInUser, HttpSession session);

	/**
	 * @param response
	 * @param po
	 */
	void generatePoReport(HttpServletResponse response, Po po);

	/**
	 * @param po
	 * @return
	 */
	JasperPrint savePoPdf(Po po);

	/**
	 * @param poId
	 * @param poItemId
	 * @return
	 */
	PoItem getPoItembyPoIdAndPoItemId(String poId, String poItemId);

	/**
	 * @param item
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	PoItem savePoItem(PoItem item, User loggedInUser) throws Exception;

	/**
	 * @param poItem
	 * @return
	 */
	boolean isExists(PoItem poItem,boolean isSection);

	String replaceSmartQuotes(String input);
	/**
	 * @param poItem
	 * @throws NotAllowedException
	 * @throws Exception
	 */
	void updatePoItem(PoItem poItem) throws Exception, NotAllowedException;

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getPoItemListByPoId(String poId);

	/**
	 * @param poItemId
	 * @param poId
	 * @throws ApplicationException
	 */
	void deletePoItemByItemIdAndPoId(String poItemId, String poId) throws ApplicationException;

	/**
	 * @param userId
	 * @param poId
	 * @return
	 */
	EventPermissions getUserPemissionsForPo(String userId, String poId);

	/**
	 * @param loggedInUser
	 * @param po
	 * @param loggedInUser
	 */
	void sendRevisePoEmailNotification(User mailTo, Po po, User loggedInUser);

	/**
	 * @param loggedInUser
	 * @param po
	 * @param loggedInUser
	 */
	void sendPoEmailNotificationToSupplier(User mailTo, Po po, User loggedInUser,String action);

	/**
	 * @param loggedInUser
	 * @param po
	 * @param loggedInUser
	 */
	void sendSuspendPoEmailNotification(User mailTo,Po po, User loggedInUser);
	/**
	 * @param mailTo
	 * @param persistObj
	 * @param loggedInUser
	 */
	void sendRevisePoFinishedEmailNotification(User mailTo, Po persistObj, User loggedInUser);

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
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Po> findRevisePo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long countRevisePo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Po> findPendingCancelPo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long countPendingCancelPo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<Po> findPendingPo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param userId
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long countPendingPo(String userId, String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	long findTotalFilteredPendingPo(String tenantId, String userId, TableDataInput input);

	/**
	 * @param po
	 * @param virtualizer TODO
	 * @param loggedInUser TODO
	 * @return
	 * @throws Exception
	 */
	Po updatePoDetails(Po po, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception;

	/**
	 * @param po
	 * @param validations
	 * @return
	 */
	List<String> validatePo(Po po, Class<?>... validations);

	/**
	 * @param poId
	 * @param tenantId
	 */
	void deletePoReportByPoId(String poId, String tenantId);

	/**
	 * @param prDocument
	 * @return
	 */
	PurchaseOrderDocument savePoDocument(PurchaseOrderDocument prDocument);

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPlainPoDocsbyPoId(String poId);

	/**
	 * @param docId
	 * @return
	 */
	PurchaseOrderDocument findPoDocById(String docId);

	/**
	 * @param docId TODO
	 * @param poId TODO
	 */
	void removePoDocument(String docId, String poId);

	/**
	 * @param docId
	 * @param docDesc
	 * @param poId
	 * @param internal TODO
	 */
	void updatePoDocumentDesc(String docId, String docDesc, String poId, boolean internal);

	/**
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadPrDocument(String docId, HttpServletResponse response) throws Exception;


	/**
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadPoDocument(String docId, HttpServletResponse response) throws Exception;

	/**
	 * @param poId
	 * @return
	 */
	List<PurchaseOrderDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId);

	/**
	 * @param poId
	 * @param revisePoDetails
	 */
	void updatePoRevisedSnapshot(String poId, String revisePoDetails);

	/**
	 * @param poItemIds
	 * @param poId
	 * @return
	 */
	List<PoItem> deletePoItems(String[] poItemIds, String poId);


	/**
	 * @param prItemsIds
	 * @param poId TODO
	 * @return
	 */
	Long findItemCountByPrItemIds(String[] prItemsIds, String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<POSnapshotDocument> findAllSnapshotPlainPoDocsbyPoIdForSupplier(String poId);

	/**
	 * @param docId
	 * @return
	 */
	POSnapshotDocument findPoSnapShotDocById(String docId);

	/**
	 * @param pOSnapshotDocument
	 */
	void saveOrUpdatePoSnapShotDocument(POSnapshotDocument pOSnapshotDocument);

	void downloadCsvFileForPoItemSummary(HttpServletResponse response, File file, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String loggedInUserTenantId,  String loggedInUser, HttpSession session,String poType,String status);

	void downloadCsvFileForPoItemSupplierSummary(HttpServletResponse response, File file, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String loggedInUserTenantId, String userId, HttpSession session);

	long findTotalCancelPo(String loggedInUserTenantId, String id);
	long findTotalFilteredCancelPo(String tenantId, String userId, TableDataInput input);

	List<Po> findAllCancelPo(String tenantId, String userId, TableDataInput input);
	/**
	 * @param po
	 */
	void sendPoFinishMail(Po po,String action);


	/**
	 * @param loggedInUser
	 * @param tenantId
	 */
	long findPoPendingCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,PoStatus status,List<String> businessUnitIds);

	/**
	 * @param loggedInUser
	 * @param tenantId
	 */
	long findPoRevisedCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds);

	/**
	 * @param loggedInUser
	 * @param tenantId
	 */
	long findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds);

	/**
	 * @param poId
	 * @param userId
	 * @param memberType
	 * @return
	 */
	List<PoTeamMember> addTeamMemberToList(String poId, String userId, TeamMemberType memberType);

	/**
	 * @param poId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForPo(String poId);

	/**
	 * @param poId
	 * @param userId
	 * @param dbTeamMember TODO
	 * @return
	 */
	List<User> removeTeamMemberfromList(String poId, String userId, PoTeamMember dbTeamMember);

	PoTeamMember getPoTeamMemberByUserIdAndPoId(String poId, String userId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> findAllChildPoItemByPoId(String poId);

	List<PoItem> findAllLoadedChildPoItemByPoId(String poId);

	List<PoItem> deletePoItemsByPoId(String poId);

	/**
	 * @param label
	 * @param poId
	 */
	void deletefieldInPoItems(String label, String poId);

	/**
	 * @param itemId
	 * @return
	 */
	PoItem getPoItembyPoItemId(String itemId);

	/**
	 * @param poItem
	 * @throws NotAllowedException
	 */
	void reOrderPoItems(PoItem poItem) throws NotAllowedException;


	/**
	 * @param poId
	 * @return
	 */
	List<PoTeamMember> getPlainTeamMembersForPoSummary(String poId);
}
