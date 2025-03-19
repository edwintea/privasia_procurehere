package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sana
 */
public interface SupplierFormSubmissionDao extends GenericDao<SupplierFormSubmition, String> {

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

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalFormOfSupplier(String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	List<SupplierFormSubmissionPojo> findAllSearchFilterFormByBuyerIdAndStatus(String tenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param tenantId
	 * @param input
	 * @param status
	 * @return
	 */
	long findTotalSearchFilterFormByBuyerIdAndStatus(String tenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param loggedInUserTenantId
	 * @param status
	 * @return
	 */
	long findTotalFormByBuyerIdAnStatus(String loggedInUserTenantId, List<SupplierFormSubmitionStatus> status);

	/**
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findOnBoardingFormAvailable(String buyerId, String supplierId);

	/**
	 * @param formId
	 * @param id
	 * @return
	 */
	long findFormSubBySuppAndFormId(String formId, String id);

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
	 * @param formId
	 * @return
	 */
	SupplierFormSubmition findFormById(String formId);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	SupplierFormSubmition getAssignedSupplierForm(String supplierId, String buyerId);

	/**
	 * @param formId
	 * @return
	 */
	boolean isFormAssigned(String formId);

	/**
	 * @param favSuppId
	 * @param buyerId
	 * @return
	 */
	SupplierFormSubmition findOnboardingFormSubmitionByFavSuppIdAndBuyerId(String favSuppId, String buyerId);

	/**
	 * @param id
	 * @param favSupplierId
	 */
	void updateFavSupplier(String id, String favSupplierId);

	/**
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	long findFormSubBySuppAndBuyerId(String buyerId, String supplierId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierFormSubmition getSupplierFormForAdditionalApproverById(String formId);

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
	SupplierFormSubmitionApproval getSupplierFormActiveApproverById(String formId);

	/**
	 * @param formId
	 * @return
	 */
	SupplierFormSubmition getSupplierFormSubmitionById(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormApproval> getAllApprovalListByFormId(String formId);

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findOnBoardingFormForSupplier(String supplierId);

}
