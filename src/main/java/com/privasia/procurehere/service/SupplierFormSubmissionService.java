package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SupplierAssignFormPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sana
 */

public interface SupplierFormSubmissionService {

	/**
	 * @param tenantId
	 * @param status
	 * @return
	 */
	long pendingBuyerFormCount(String tenantId, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	List<SupplierFormSubmissionPojo> findAllSearchFilterPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	long findTotalPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param supplierId
	 * @return
	 */
	List<SupplierFormSubmissionPojo> findAllSearchFilterFormBySuppAndBuyerId(String loggedInUserTenantId, TableDataInput input, String supplierId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param supplierId
	 * @return
	 */
	long findTotalSearchFilterFormBySuppAndBuyerId(String loggedInUserTenantId, TableDataInput input, String supplierId);

	/**
	 * @param loggedInUserTenantId
	 * @param supplierId
	 * @return
	 */
	long findTotalFormBySuppAndBuyerId(String loggedInUserTenantId, String supplierId);

	void assignFormsToSupplier(String[] formIds, Supplier supplier, User loggedInUser);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalFormOfSupplier(String loggedInUserTenantId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierFormSubmition getSupplierformById(String formId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param status
	 * @return
	 */
	List<SupplierFormSubmissionPojo> findAllSearchFilterFormByBuyerIdAndStatus(String loggedInUserTenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param status
	 * @return
	 */
	long findTotalSearchFilterFormByBuyerIdAndStatus(String loggedInUserTenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param loggedInUserTenantId
	 * @param status
	 * @return
	 */
	long findTotalFormByBuyerIdAnStatus(String loggedInUserTenantId, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param formSubId
	 * @param loggedInUser
	 * @param buyerRemark TODO
	 * @return TODO
	 */
	SupplierFormSubmition acceptSupplierForm(String formSubId, User loggedInUser, String buyerRemark);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmissionItem> findSupplierSubFormItemById(String formId);

	/**
	 * @param formId
	 * @param subForm
	 */
	void saveSupplierFormSubmission(String formId, SupplierFormSubmition subForm);

	/**
	 * @param formId
	 * @param itemId
	 * @return
	 */
	SupplierFormSubmissionItem findFormSubmissionItem(String formId, String itemId);

	/**
	 * @param list
	 * @param formId
	 * @param loggedInUser
	 * @param flag
	 * @return TODO
	 */
	SupplierFormSubmition updateSupplierForm(List<SupplierFormSubmissionItem> list, String formId, User loggedInUser, Boolean flag);

	/**
	 * @param formSubId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 */
	SupplierFormSubmition reviseSupplierForm(String formSubId, User loggedInUser, String buyerRemark);

	/**
	 * @param formSubId
	 * @param formSubItemId
	 * @return
	 */
	boolean resetAttachement(String formSubId, String formSubItemId);

	/**
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findOnBoardingFormAvailable(String buyerId, String supplierId);

	/**
	 * @param supplierFormObj
	 */
	void deleteSuppFormSubmission(SupplierFormSubmition supplierFormObj);

	/**
	 * @param loggedInUser
	 * @param supplierAssignFormPojo TODO
	 * @return TODO
	 * @throws ApplicationException
	 */
	long assignFormsToFavSupplier(User loggedInUser, SupplierAssignFormPojo supplierAssignFormPojo) throws ApplicationException;

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmissionItem> findSupplierSubFormItemByIdForBuyer(String formId);

	/**
	 * @param id
	 * @return
	 */
	List<SupplierFormItemAttachment> findAllFormDocsByFormItemId(String id);

	/**
	 * @param documentId
	 * @return
	 */
	SupplierFormItemAttachment findSupplierformItemAttachment(String documentId);

	/**
	 * @param loggedInUser
	 * @param buyerId
	 * @return TODO
	 */
	SupplierFormSubmition assignFormsPreQualifierForm(User loggedInUser, String buyerId);

	/**
	 * @param id
	 * @return
	 */
	boolean isFormAssigned(String id);

	/**
	 * @param favSuppId
	 * @param buyerId
	 * @return
	 */
	SupplierFormSubmition findOnboardingFormSubmitionByFavSuppIdAndBuyerId(String favSuppId, String buyerId);

	/**
	 * @param supplierFormSubmition
	 * @param loggedInUser
	 * @return
	 */
	SupplierFormSubmition updateSupplierFormApproval(SupplierFormSubmition supplierFormSubmition, User loggedInUser);

	/**
	 * @param supplierFormSubmition
	 * @param formId
	 * @param loggedInUser
	 */
	void addAdditionalApprover(SupplierFormSubmition supplierFormSubmition, String formId, User loggedInUser);

	/**
	 * @param supplierFormAdditionalApprover
	 * @param formId
	 * @param loggedInUser
	 * @throws ApplicationException
	 */
	void finishAdditionalApprover(SupplierFormSubmition supplierFormAdditionalApprover, String formId, User loggedInUser) throws ApplicationException;

	/**
	 * @param loggedInUser
	 * @param formSubId
	 * @return
	 */
	EventPermissions getUserPemissionsForApprovalofSuppForm(User loggedInUser, String formSubId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @param input
	 * @return
	 */
	List<SupplierFormSubmissionPojo> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param userid
	 * @param input
	 * @return
	 */
	long myPendingSupplierFormListCount(String loggedInUserTenantId, String userid, TableDataInput input);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormApproval> getAllApprovalListByFormId(String formId);
}
