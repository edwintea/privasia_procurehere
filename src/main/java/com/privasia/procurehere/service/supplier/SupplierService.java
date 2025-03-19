/**
 * 
 */
package com.privasia.procurehere.service.supplier;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;
import com.privasia.procurehere.core.entity.SupplierOtherDocuments;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.ExtentionValidity;
import com.privasia.procurehere.core.pojo.NotesPojo;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierReportPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Arc
 */
public interface SupplierService {

	/**
	 * @return
	 */
	List<SupplierPojo> findPendingSuppliers();

	/**
	 * @param supplier
	 * @param sendNotification
	 * @return
	 * @throws Exception
	 */
	Supplier saveSupplier(Supplier supplier, boolean sendNotification) throws Exception;

	/**
	 * @param supplier
	 * @return
	 */
	Supplier updateSupplier(Supplier supplier);

	/**
	 * @param supplier
	 */
	void deleteSupplier(Supplier supplier);

	/**
	 * @return
	 */
	List<Supplier> findAllactiveSuppliers();

	/**
	 * @param supplierId
	 * @return
	 */
	Supplier findSupplierById(String supplierId);

	/**
	 * @param supplier
	 * @param sendNotification
	 * @return
	 * @throws ApplicationException
	 */
	Supplier confirmSupplier(Supplier supplier, boolean sendNotification) throws ApplicationException;

	/**
	 * @param status
	 * @param order
	 * @param globalSearch
	 * @return
	 */
	List<Supplier> searchSuppliers(String status, String order, String globalSearch);

	/**
	 * @param id
	 * @return
	 */
	SupplierCompanyProfile findCompanyProfileById(String id);

	/**
	 * @return
	 */
	List<SupplierCompanyProfile> findCompanyProfileAll();

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierCompanyProfile> findAllCompanyProfileBySupplierId(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierOtherCredentials findOtherCredentialById(String id);

	/**
	 * @return
	 */
	List<SupplierOtherCredentials> findOtherCredentialAll();

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierOtherCredentials> findAllOtherCredentialBySupplierId(String supplierId);

	/**
	 * @param projectId
	 * @return
	 */
	SupplierProjects findSupplierProjectById(String projectId);

	/**
	 * @param supplierProjects
	 */
	void deleteSupplierProject(SupplierProjects supplierProjects);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExists(Supplier supplier);

	/**
	 * @param supplierProjects
	 */
	void updateSupplierProject(SupplierProjects supplierProjects);

	/**
	 * @param supplierProjects
	 * @return
	 */
	SupplierProjects saveSupplierProject(SupplierProjects supplierProjects);

	/**
	 * @param supplierProjects
	 * @return
	 */
	SupplierCompanyProfile saveSupplierProfile(SupplierCompanyProfile supplierProjects);

	/**
	 * @param supplierProjects
	 */
	void deleteSupplierProfile(SupplierCompanyProfile supplierProjects);

	/**
	 * @param loginEmail
	 * @return
	 */
	boolean isExistsLoginEmail(String loginEmail);

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierProjects> findProjectsForSupplierId(String supplierId);

	/**
	 * @param projectId
	 * @return
	 */
	SupplierProjects findBySupplierId(String projectId);

	/**
	 * @param profileId
	 */
	void removeCompanyProfile(String profileId);

	/**
	 * @param otherCredentials
	 * @return
	 */
	SupplierOtherCredentials saveSupplierOtherCredentials(SupplierOtherCredentials otherCredentials);

	/**
	 * @param credentialId
	 */
	void removeOtherCredentials(String credentialId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<Country> assignedCountriesForSupplierId(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<State> assignedStatesForSupplierId(String supplierId);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierForProjectTrackById(String id);

	/**
	 * @param projectId
	 * @return
	 */
	List<Country> assignedCountriesForProjectTrackId(String projectId);

	/**
	 * @param selected
	 * @param search
	 * @return
	 */
	List<Coverage> doSearchCoverage(String activeTab, String supplierId, String projectId, String[] selected, String search);

	// List<Coverage> constructCoverage(String[] selected);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExistsRegistrationNumber(Supplier supplier);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExistsCompanyName(Supplier supplier);

	/**
	 * @param notes
	 */
	void saveNotes(Notes notes);

	/**
	 * @param supplierId
	 * @return
	 */
	Supplier findSuppById(String supplierId);

	/**
	 * @return
	 */
	List<NotesPojo> getAllNotesPojo();

	/**
	 * @param id
	 * @return
	 */

	void removeTrackProject(String projectId);

	/**
	 * @param supplierId
	 * @return
	 */
	Supplier findSupplierOnDashbordById(String supplierId);

	/**
	 * @param ncid
	 * @return
	 */
	List<Supplier> findSuppliersOfNaicsCode(String ncid);

	/**
	 * @param supplierId
	 * @return
	 */
	long countTotalInvitedEventOfSupplier(String supplierId);

	long countTotalParticipatedEventOfSupplier(String supplierId);

	List<Supplier> getAllSupplierFromIds(List<String> supplierIds);

	List<NotesPojo> notesForSupplier(String id, String buyerId);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierSubscriptionDetailsBySupplierId(String id);

	/**
	 * @param otherDocuments
	 * @return
	 */
	SupplierOtherDocuments saveSupplierOtherDocuments(SupplierOtherDocuments otherDocuments);

	/**
	 * @param documentId
	 */
	void removeOtherDocuments(String documentId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierOtherDocuments> findAllOtherDocumentBySupplierId(String supplierId);

	/**
	 * @param documentId
	 * @return
	 */
	SupplierOtherDocuments findOtherDocumentById(String documentId);

	/**
	 * @param supplierId
	 * @param oldCommunicationEmail
	 * @param newCommunicationEmail
	 */
	void updateSupplierCommunicationEmail(String supplierId, String oldCommunicationEmail, String newCommunicationEmail);

	/**
	 * @param suppId
	 * @return
	 */
	Supplier findSupplierAndAssocitedBuyersById(String suppId);

	/**
	 * @return
	 */
	List<Supplier> findSuppliersForSubscriptionExpireOrExtend();

	/**
	 * @param remindDate
	 * @return
	 */
	List<Supplier> findSuppliersForExpiryNotificationReminder(Date remindDate);

	/**
	 * @param suppId
	 * @return
	 */
	long totalEventAwardedSupplier(String suppId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param id
	 * @param approved
	 * @return
	 */
	List<PoSupplierPojo> findAllSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalSearchFilterPoForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String status);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalPoForSupplier(String loggedInUserTenantId);

	/**
	 * @param prId
	 * @return
	 */
	Pr getPrByIdForSupplierView(String prId);

	/**
	 * @param po
	 * @param session
	 * @return
	 */
	JasperPrint getSupplierPOSummaryPdf(Po po, HttpSession session);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> findAllPrItemByPrId(String prId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllPOForSupplier(String loggedInUserTenantId, String id);

	boolean isExistsRegistrationNumberWithId(Supplier supplier);

	boolean isExistsCompanyNameWithId(Supplier supplier);

	void saveAuitTrail(String message, User loggedInUser);

	void updateSupplierCommunicationEmailForSupplierOnly(String supplierId, String communicationEmail, String emailId);

	/**
	 * @param zos
	 * @param response
	 * @param tenantId
	 * @param session
	 * @return
	 */
	String generateAllPoZip(ZipOutputStream zos, HttpServletResponse response, String tenantId, HttpSession session);

	List<FinancePo> findFinanceSuppliers(String id);

	List<FinancePo> searchFinanceSuppliers(String string, String order, String globalSreach, String string2);

	List<Pr> findAllSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, PrStatus approved, String selectedSupplier);

	long findTotalSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, PrStatus approved, String selectedSupplier);

	long findTotalPoForFinance(String loggedInUserTenantId);

	Pr getPrByIdForFinanceView(String prId);

	JasperPrint getFinancePOSummaryPdf(Po po, HttpSession session);

	// List<PrItem> findAllPrItemByPrId(String prId);

	long findCountOfAllPOForFinance(String tenantId, String userId);

	List<Pr> findAllSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus approved, String selectedSupplier);

	long findTotalSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus approved, String selectedSupplier);

	long findTotalPoForOwner();

	Supplier getSupplierWithAssoBuyersAndSubPackageById(String id);

	List<Coverage> doSearchCoverageForSupplierRegistration(String activeTab, String supplierId, String projectId, String[] selected, String search);

	Supplier findSupplierForAdminProfileById(String supplierId);

	List<SupplierPojo> searchSuppliersForPagination(String status, String order, String globalSearch, String pageNo);

	Supplier findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(String id);

	// void downloadPoReports(List<FinancePo> suppliers, HttpServletResponse response, HttpSession session);

	// List<FinancePo> findFinancePO(String tenantId);

	SupplierFinanicalDocuments saveSupplierFinancialDocuments(SupplierFinanicalDocuments supplierFinancialDocuments);

	SupplierFinanicalDocuments findFinancialDocumentId(String id);

	SupplierBoardOfDirectors saveSupplierBoardOfDirector(SupplierBoardOfDirectors directors);

	List<SupplierFinanicalDocuments> findAllFinancialDocumentsBySupplierID(String id);

	void removeSupplierFinancialDocuments(String id);

	List<SupplierBoardOfDirectors> findAllDirectorsBySupplierID(String id);

	List<SupplierBoardOfDirectors> findDuplicateDirector(String idNumber);

	SupplierBoardOfDirectors findDirectorById(String id);

	void removeBoardOfDirector(String id);

	void checkIfProfileIsComplete(Model model, Supplier supplier);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalAssocitedBuyersById(String teanantId);

	/**
	 * @return
	 */
	long getTotalSupplierCount();

	// void downloadPoReports(List<FinancePo> suppliers, HttpServletResponse response, HttpSession session);

	// List<FinancePo> findFinancePO(String tenantId);

	RequestedAssociatedBuyer findSupplierRequestBySupplierAndBuyerId(String id, String buyerId);

	RequestedAssociatedBuyer findAssocoaitedRequestById(String id);

	RequestedAssociatedBuyer updateSupplierRequest(RequestedAssociatedBuyer buyer);

	long findCountOfPoForSupplierBasedOnStatus(String loggedInUserTenantId, PoStatus status);

	List<PoSupplierPojo> findAllSearchFilterPoForSupplierByStatus(String loggedInUserTenantId, TableDataInput input, PoStatus status);

	long findTotalSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status);

	Po getPoByIdForSupplierView(String poId);

	Supplier findPlainSupplierById(String supplierId);

	JasperPrint getSupplierPOSummaryPdfForDownload(Po po, HttpSession session);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> findAllPoItemByPoId(String poId);

	/**
	 * @param supplier
	 * @param extentionValidity
	 * @throws ApplicationException
	 */
	void extendValidity(Supplier supplier, ExtentionValidity extentionValidity) throws ApplicationException;

	/**
	 * @return
	 */
	List<Supplier> findSuppliersForFutureSubscriptionActivation();

	/**
	 * @param id
	 * @return
	 */
	List<SupplierSubscription> getSupplierFutureSubscriptionByCreatedDate(String id);

	/**
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<SupplierReportPojo> findAllSearchFilterSupplierReportList(TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalSearchFilterSupplierReportCount(TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalSuppliersCount(Date startDate, Date endDate);

	void downloadCsvFileForSupplierList(HttpServletResponse response, File file, SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, String[] supplierIds, DateFormat formatter, Date startDate, Date endDate);

	/**
	 * @param companyName
	 * @param supplierId
	 * @param tenantId
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	Supplier updateSupplierCompanyName(String companyName, String supplierId, String tenantId, HttpHeaders headers) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param regNumber
	 * @param supplierId
	 * @param tenantId
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	Supplier updateSupplierRegistrationNumber(String regNumber, String supplierId, String tenantId, HttpHeaders headers) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findSupplierFormBySupplierId(String supplierId);

	/**
	 * @param id
	 * @return
	 */
	Supplier findPlainSupplierUsingConstructorById(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	List<Buyer> getBuyerListForSupplierId(String tenantId);


}
