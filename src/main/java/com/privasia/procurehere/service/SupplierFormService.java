package com.privasia.procurehere.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SupplierFormItemPojo;
import com.privasia.procurehere.core.pojo.SupplierFormPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface SupplierFormService {
	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<SupplierFormPojo> findSupplierFormsByTeantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalActiveSupplierFormsForTenant(String loggedInUserTenantId);

	/**
	 * @param supplierForm
	 * @return
	 */
	SupplierForm getSupplierFormById(String formId);

	/**
	 * @param supplierFormItem
	 * @param formId
	 * @param parentId
	 * @return
	 */
	boolean isExists(SupplierFormItem supplierFormItem, String formId, String parentId);

	/**
	 * @param item
	 */
	SupplierFormItem saveSupplierFormItem(SupplierFormItem item);

	/**
	 * @param itemId
	 * @return
	 */
	SupplierFormItem getFormItembyFormItemId(String itemId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormItem> findFormItembyFormId(String formId);

	/**
	 * @param formItemPojo
	 */
	void updateFormItem(SupplierFormItemPojo formItemPojo);

	/**
	 * @param supplierFormObj
	 */
	SupplierForm saveOrUpdate(SupplierForm supplierFormObj);

	/**
	 * @param items
	 * @param formId
	 * @param isFormitemEmpy TODO
	 * @throws NotAllowedException
	 */
	void deleteFormItems(String[] items, String formId, boolean isFormitemEmpy) throws NotAllowedException;

	/**
	 * @param formData
	 * @param formId TODO
	 * @param tenantId TODO
	 * @return
	 * @throws NotAllowedException
	 */
	String doExcelDataSave(Map<Integer, Map<Integer, SupplierFormItem>> formData, String formId, String tenantId) throws NotAllowedException;

	/**
	 * @param id
	 * @return
	 */
	SupplierForm getFormById(String id);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormItem> getAllFormitemsbyFormId(String formId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<SupplierForm> getSupplierFormListByTenantId(String loggedInUserTenantId);

	/**
	 * @param supplierFormObj
	 */
	void deleteSupplierForm(SupplierForm supplierFormObj);

	/**
	 * @param supplierFormObj
	 * @param loggedInUserTenantId
	 * @return
	 */
	boolean isFormNameExists(SupplierForm supplierFormObj, String loggedInUserTenantId);

	/**
	 * @param workbook
	 * @param formId
	 */
	void supplierFormDownloader(XSSFWorkbook workbook, String formId);

	/**
	 * @param supplierFormItemPojo
	 * @throws NotAllowedException
	 */
	void reorderFormItem(SupplierFormItemPojo supplierFormItemPojo) throws NotAllowedException;

	/**
	 * @param formId
	 * @return
	 */
	List<String> getNotSectionItemAddedByFormId(String formId);

	/**
	 * @param formObj
	 */
	void deleteFormItemDoc(SupplierFormItemAttachment formObj);

	/**
	 * @param supplierFormObj
	 * @param loggedInUser TODO
	 * @return
	 */
	SupplierForm saveSupplierForm(SupplierForm supplierFormObj, User loggedInUser);

	/**
	 * @param string
	 * @param loggedInUserTenantId
	 * @return
	 */
	SupplierForm getSupplierFormByTenantAndId(String formId, String loggedInUserTenantId);

	/**
	 * @param oldSupplierForm
	 * @param formName
	 * @param loggedInUser
	 * @param formDesc TODO
	 * @return
	 */
	SupplierForm copySupplierForm(SupplierForm oldSupplierForm, String formName, User loggedInUser, String formDesc);

}