package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.SupplierFormApprovalDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierFormItemDao;
import com.privasia.procurehere.core.dao.SupplierFormItemOptionDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionItemDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionApprovalDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionComment;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionItemOption;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SupplierFormApprovalStatus;
import com.privasia.procurehere.core.enums.SupplierFormSubAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionAuditType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SupplierAssignFormPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierSettingsService;

@Service
@Transactional(readOnly = true)
public class SupplierFormSubmissionServiceImpl implements SupplierFormSubmissionService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierFormSubmissionDao supplierFormSubmissDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	SupplierFormDao supplierFormDao;

	@Autowired
	SupplierFormItemDao supplierFormItemDao;

	@Autowired
	SupplierFormSubmitionAuditDao supplierFormSubmitionAuditDao;

	@Autowired
	SupplierFormSubmissionItemDao supplierFormSubItemDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	SupplierFormItemOptionDao supplierFormItemOptionDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	SupplierFormApprovalDao supplierFormApprovalDao;

	@Autowired
	UserDao userDao;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierFormSubmitionApprovalDao supplierFormSubmitionApprovalDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public long pendingBuyerFormCount(String tenantId, List<SupplierFormSubmitionStatus> status) {
		return supplierFormSubmissDao.pendingBuyerFormCount(tenantId, status);
	}

	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status) {
		return supplierFormSubmissDao.findAllSearchFilterPendingFormByStatus(tenantId, input, status);
	}

	@Override
	public long findTotalPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status) {
		return supplierFormSubmissDao.findTotalPendingFormByStatus(tenantId, input, status);
	}

	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterFormBySuppAndBuyerId(String loggedInUserTenantId, TableDataInput input, String supplierId) {
		return supplierFormSubmissDao.findAllSearchFilterFormBySuppAndBuyerId(loggedInUserTenantId, input, supplierId);
	}

	@Override
	public long findTotalSearchFilterFormBySuppAndBuyerId(String loggedInUserTenantId, TableDataInput input, String supplierId) {
		return supplierFormSubmissDao.findTotalSearchFilterFormBySuppAndBuyerId(loggedInUserTenantId, input, supplierId);
	}

	@Override
	public long findTotalFormBySuppAndBuyerId(String loggedInUserTenantId, String supplierId) {
		return supplierFormSubmissDao.findTotalFormBySuppAndBuyerId(loggedInUserTenantId, supplierId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void assignFormsToSupplier(String[] formIds, Supplier supplier, User loggedInUser) {
		FavouriteSupplier favouriteSupplier = favoriteSupplierDao.findFavSupplierBySuppId(supplier.getId(), loggedInUser.getTenantId());
		for (String formId : formIds) {
			LOG.info("Assign Form:" + formId + " to supplier.");
			SupplierForm supplierForm = supplierFormDao.findById(formId);
			if (supplierForm != null) {
				SupplierFormSubmition supplierFormSubmition = new SupplierFormSubmition();
				supplierFormSubmition.setRequestedBy(loggedInUser);
				supplierFormSubmition.setRequestedDate(new Date());
				supplierFormSubmition.setBuyer(loggedInUser.getBuyer());
				supplierFormSubmition.setSupplier(supplier);
				if (favouriteSupplier != null) {
					supplierFormSubmition.setFavouriteSupplier(favouriteSupplier);
				}
				supplierFormSubmition.setSupplierForm(supplierForm);
				supplierFormSubmition.setIsOnboardingForm(Boolean.FALSE);
				supplierFormSubmition.setName(supplierForm.getName());
				supplierFormSubmition.setDescription(supplierForm.getDescription());
				supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.PENDING);
				supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
				List<SupplierFormItem> formItems = supplierFormItemDao.getAllFormitemsbyFormId(supplierForm.getId());
				List<SupplierFormSubmissionItem> listFormSubItems = new ArrayList<SupplierFormSubmissionItem>();
				if (CollectionUtil.isNotEmpty(formItems)) {
					for (SupplierFormItem item : formItems) {
						SupplierFormSubmissionItem supplierCq = new SupplierFormSubmissionItem();
						supplierCq.setSupplierFormItem(item);
						supplierCq.setSupplierFormSubmition(supplierFormSubmition);
						listFormSubItems.add(supplierCq);
					}
				}
				supplierFormSubmition.setFormSubmitionItems(listFormSubItems);
				buildApprovalList(supplierFormSubmition, supplierForm);
				supplierFormSubmition = supplierFormSubmissDao.save(supplierFormSubmition);
				if (supplierFormSubmition != null) {
					supplierForm.setPendingCount(supplierForm.getPendingCount() != null && supplierForm.getPendingCount() != 0 ? supplierForm.getPendingCount() + 1 : 1);
					supplierFormDao.update(supplierForm);

					SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
					formAudit.setActionBy(loggedInUser);
					formAudit.setActionDate(new Date());
					formAudit.setDescription(messageSource.getMessage("supplier.form.audit.assigned", new Object[] {}, Global.LOCALE));
					formAudit.setBuyer(supplierFormSubmition.getBuyer());
					formAudit.setSupplier(supplierFormSubmition.getSupplier());
					formAudit.setSupplierFormSubmition(supplierFormSubmition);
					formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
					formAudit.setAction(SupplierFormSubmitionAuditType.ASSIGNED);
					supplierFormSubmitionAuditDao.save(formAudit);

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ASSIGNED, "Supplier Form Assigned", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierForm);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}

					try {
						sendEmailNotificationToSupplierForAssignForm(supplierFormSubmition, loggedInUser);

					} catch (Exception e) {
						LOG.error("Error while sending do accept mail to supplier " + supplierFormSubmition.getSupplier().getCompanyName() + " :  " + e.getMessage(), e);
					}
				}
			}
		}
	}

	private void sendEmailNotificationToSupplierForAssignForm(SupplierFormSubmition supplierFormSubmition, User loggedInUser) {
		LOG.info("Sending Email Request to supplier:" + supplierFormSubmition.getSupplier().getCompanyName());
		try {
			String supplierTimeZone = "GMT+8:00";
			supplierTimeZone = getTimeZoneBySupplierSettings(supplierFormSubmition.getSupplier().getId(), supplierTimeZone);
			TimeZone timeZone = TimeZone.getDefault();
			if (StringUtils.checkString(supplierTimeZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(supplierTimeZone);
			}
			String subject = "Supplier Form Received";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("buyerName", supplierFormSubmition.getBuyer().getCompanyName());
			map.put("supplierName", supplierFormSubmition.getSupplier().getFullName());
			map.put("formName", supplierFormSubmition.getName());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(timeZone);
			map.put("date", df.format(new Date()));
			map.put("requestedDate", df.format(supplierFormSubmition.getRequestedDate()));
			map.put("actionBy", loggedInUser.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");
			User user = userDao.getDetailsOfLoggedinUser(supplierFormSubmition.getSupplier().getLoginEmail());
			if (StringUtils.checkString(supplierFormSubmition.getSupplier().getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
				sendEmail(supplierFormSubmition.getSupplier().getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_ASSIGNED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + supplierFormSubmition.getSupplier().getCompanyName() + "... Not going to send email notification");
			}
		} catch (Exception e) {
			LOG.error("Error while Sending revised form email to supplier :" + e.getMessage(), e);
		}

	}

	public String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	public long findTotalFormOfSupplier(String loggedInUserTenantId) {
		return supplierFormSubmissDao.findTotalFormOfSupplier(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierFormSubmition getSupplierformById(String formId) {
		LOG.info("FORM ID : " + formId);
		SupplierFormSubmition formSubmition = supplierFormSubmissDao.getSupplierFormForAdditionalApproverById(formId);
		if (formSubmition != null && formSubmition.getBuyer() != null) {
			formSubmition.getBuyer().getCompanyName();
		}
		if (formSubmition != null && formSubmition.getSupplierForm() != null) {
			formSubmition.getSupplierForm().getName();
		}
		LOG.info("Form : " + formSubmition.getName());
		if (CollectionUtil.isNotEmpty(formSubmition.getApprovals())) {
			for (SupplierFormSubmitionApproval approval : formSubmition.getApprovals()) {
				approval.getLevel();
				approval.isActive();
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (SupplierFormSubmitionApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getApproval();
						if (approvalUser.getUser() != null) {
							approvalUser.getUser().getLoginId();
						}
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(formSubmition.getFormComments())) {
			for (SupplierFormSubmitionComment comment : formSubmition.getFormComments()) {
				if (comment != null) {
					comment.getComment();
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getName();
					}
					comment.isApproved();
				}
			}
		}
		return formSubmition;
	}

	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterFormByBuyerIdAndStatus(String loggedInUserTenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status) {
		return supplierFormSubmissDao.findAllSearchFilterFormByBuyerIdAndStatus(loggedInUserTenantId, input, status);
	}

	@Override
	public long findTotalSearchFilterFormByBuyerIdAndStatus(String loggedInUserTenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status) {
		return supplierFormSubmissDao.findTotalSearchFilterFormByBuyerIdAndStatus(loggedInUserTenantId, input, status);
	}

	@Override
	public long findTotalFormByBuyerIdAnStatus(String loggedInUserTenantId, List<SupplierFormSubmitionStatus> status) {
		return supplierFormSubmissDao.findTotalFormByBuyerIdAnStatus(loggedInUserTenantId, status);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public SupplierFormSubmition acceptSupplierForm(String formSubId, User loggedInUser, String buyerRemark) {
		SupplierFormSubmition supplierFormSubmition = supplierFormSubmissDao.findFormById(formSubId);
		if (supplierFormSubmition != null) {
			supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.ACCEPTED);
			supplierFormSubmissDao.update(supplierFormSubmition);

			SupplierForm supplierFormObj = supplierFormSubmition.getSupplierForm();
			if (supplierFormObj != null) {
				supplierFormObj.setAcceptedCount(supplierFormObj.getAcceptedCount() != null && supplierFormObj.getAcceptedCount() != 0 ? supplierFormObj.getAcceptedCount() + 1 : 1);
				supplierFormObj.setSubmittedCount(supplierFormObj.getSubmittedCount() != null && supplierFormObj.getSubmittedCount() != 0 ? supplierFormObj.getSubmittedCount() - 1 : 0);
				supplierFormDao.update(supplierFormObj);
			}

			SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
			formAudit.setActionBy(loggedInUser);
			formAudit.setActionDate(new Date());
			formAudit.setDescription(messageSource.getMessage("supplier.form.audit.accepted", new Object[] { buyerRemark }, Global.LOCALE));
			formAudit.setBuyer(supplierFormSubmition.getBuyer());
			formAudit.setSupplier(supplierFormSubmition.getSupplier());
			formAudit.setSupplierFormSubmition(supplierFormSubmition);
			formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
			formAudit.setAction(SupplierFormSubmitionAuditType.ACCEPTED);
			supplierFormSubmitionAuditDao.save(formAudit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Supplier Form '" + supplierFormObj.getName() + "'  is Accepted", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierForm);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			try {
				sendEmailNotificationToSupplier(supplierFormSubmition, loggedInUser, buyerRemark);

			} catch (Exception e) {
				LOG.error("Error while sending do accept mail to supplier " + supplierFormSubmition.getSupplier().getCompanyName() + " :  " + e.getMessage(), e);
			}
		} else {
			LOG.info("SupplierFormSubmition not found for formSubId " + formSubId);
		}
		return supplierFormSubmition;
	}

	private void sendEmailNotificationToSupplier(SupplierFormSubmition supplierFormSubmition, User loggedInUser, String buyerRemark) {
		LOG.info("Sending Email Request to supplier:" + supplierFormSubmition.getSupplier().getCompanyName());
		try {
			String supplierTimeZone = "GMT+8:00";
			supplierTimeZone = getTimeZoneBySupplierSettings(supplierFormSubmition.getSupplier().getId(), supplierTimeZone);
			TimeZone timeZone = TimeZone.getDefault();
			if (StringUtils.checkString(supplierTimeZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(supplierTimeZone);
			}
			String subject = "Supplier Form Accepted";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("buyerName", supplierFormSubmition.getBuyer().getCompanyName());
			map.put("supplierName", supplierFormSubmition.getSupplier().getFullName());
			map.put("formName", supplierFormSubmition.getName());
			map.put("buyerRemark", buyerRemark);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(timeZone);
			map.put("date", df.format(new Date()));
			map.put("requestedDate", df.format(supplierFormSubmition.getRequestedDate()));
			map.put("submittedDate", df.format(supplierFormSubmition.getSubmitedDate()));
			map.put("actionBy", loggedInUser.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");
			User user = userDao.getUserDetailsBySupplier(supplierFormSubmition.getSupplier().getId(), supplierFormSubmition.getSupplier().getLoginEmail());
			if (StringUtils.checkString(supplierFormSubmition.getSupplier().getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
				sendEmail(supplierFormSubmition.getSupplier().getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_ACCEPTED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + supplierFormSubmition.getSupplier().getCompanyName() + "... Not going to send email notification");
			}
		} catch (Exception e) {
			LOG.error("Error while Sending accepted email to supplier :" + e.getMessage(), e);
		}
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@Override
	public List<SupplierFormSubmissionItem> findSupplierSubFormItemById(String formId) {
		List<SupplierFormSubmissionItem> returnList = new ArrayList<SupplierFormSubmissionItem>();
		List<SupplierFormSubmissionItem> itemList = supplierFormSubItemDao.getAllSubFormById(formId);
		LOG.info("Cq Item List Size---------" + itemList.size());
		bulidSupplierSubmissionFormItemList(returnList, itemList);
		return returnList;
	}

	private void bulidSupplierSubmissionFormItemList(List<SupplierFormSubmissionItem> returnList, List<SupplierFormSubmissionItem> itemList) {
		if (CollectionUtil.isNotEmpty(itemList)) {
			for (SupplierFormSubmissionItem item : itemList) {
				if (item.getSupplierFormItem().getFormOptions() != null) {
					for (SupplierFormItemOption opt : item.getSupplierFormItem().getFormOptions()) {
						LOG.info("SupplierFormItemOption value: " + opt.getValue());
						opt.getValue();
					}
				}
				if (item.getListAnswers() != null) {
					for (SupplierFormSubmitionItemOption ans : item.getListAnswers()) {
						ans.getValue();
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierFormSubmission(String formId, SupplierFormSubmition subForm) {
		List<SupplierFormItem> itemList = supplierFormItemDao.getAllFormitemsbyFormId(formId);
		LOG.info("ItemList size: " + itemList.size());
		for (SupplierFormItem item : itemList) {
			SupplierFormSubmissionItem submObj = new SupplierFormSubmissionItem(item);
			submObj.setSupplierFormSubmition(subForm);
			submObj = supplierFormSubItemDao.saveOrUpdate(submObj);
		}
	}

	@Override
	public SupplierFormSubmissionItem findFormSubmissionItem(String formId, String itemId) {
		return supplierFormSubItemDao.findFormSubmissionItem(formId, itemId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierFormSubmition updateSupplierForm(List<SupplierFormSubmissionItem> itemList, String formId, User loggedInUser, Boolean flag) {
		for (SupplierFormSubmissionItem item : itemList) {
			List<SupplierFormItemOption> options = item.getListOptAnswers();
			SupplierFormSubmissionItem subItem = supplierFormSubItemDao.findById(item.getId());
			subItem.setTextAnswers(item.getTextAnswers());
			subItem.setFileData(item.getFileData());
			subItem.setContentType(item.getContentType());
			subItem.setFileName(item.getFileName());
			List<SupplierFormSubmitionItemOption> requestOption = new ArrayList<SupplierFormSubmitionItemOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (SupplierFormItemOption option : options) {
					SupplierFormItemOption op = supplierFormItemOptionDao.findById(option.getId());
					if (op != null) {
						SupplierFormSubmitionItemOption reqOp = new SupplierFormSubmitionItemOption();
						reqOp.setSupplierFormSubmitiomItem(subItem);
						reqOp.setOrder(op.getOrder());
						reqOp.setValue(op.getValue());
						requestOption.add(reqOp);
					}
				}
				subItem.setListAnswers(requestOption);
			}

			supplierFormSubItemDao.update(subItem);
		}
		LOG.info("formId : " + formId);
		SupplierFormSubmition supplierFormSubmition = supplierFormSubmissDao.findFormById(StringUtils.checkString(formId));
		if (supplierFormSubmition.getBuyer() != null) {
			supplierFormSubmition.getBuyer().getCompanyName();
		}
		boolean isSecondSubmit = false;
		if (supplierFormSubmition.getSubmittedBy() != null && supplierFormSubmition.getIsOnboardingForm() == Boolean.TRUE) {
			isSecondSubmit = true;
		}

		if (supplierFormSubmition != null && flag) {
			supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.SUBMITTED);
			supplierFormSubmition.setSubmitedDate(new Date());
			supplierFormSubmition.setSubmittedBy(loggedInUser);
			buildApprovalFormStattus(supplierFormSubmition);
			supplierFormSubmition = supplierFormSubmissDao.update(supplierFormSubmition);

			if (supplierFormSubmition != null && (Boolean.FALSE == supplierFormSubmition.getIsOnboardingForm())) {
				SupplierForm supplierFormObj = supplierFormSubmition.getSupplierForm();
				supplierFormObj.setPendingCount(supplierFormObj.getPendingCount() != null && supplierFormObj.getPendingCount() != 0 ? supplierFormObj.getPendingCount() - 1 : 0);
				supplierFormObj.setSubmittedCount(supplierFormObj.getSubmittedCount() != null && supplierFormObj.getSubmittedCount() != 0 ? supplierFormObj.getSubmittedCount() + 1 : 1);
				supplierFormDao.update(supplierFormObj);
			}
			if (supplierFormSubmition != null && (Boolean.TRUE == supplierFormSubmition.getIsOnboardingForm())) {
				if (isSecondSubmit) {
					SupplierForm supplierFormObj = supplierFormSubmition.getSupplierForm();
					supplierFormObj.setPendingCount(supplierFormObj.getPendingCount() != null && supplierFormObj.getPendingCount() != 0 ? supplierFormObj.getPendingCount() - 1 : 0);
					supplierFormObj.setSubmittedCount(supplierFormObj.getSubmittedCount() != null && supplierFormObj.getSubmittedCount() != 0 ? supplierFormObj.getSubmittedCount() + 1 : 1);
					supplierFormDao.update(supplierFormObj);

					// Updated suppliers status
					supplierDao.updateSupplierStatus(supplierFormSubmition.getSupplier().getId(), loggedInUser, null);
				}
			}

			SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
			formAudit.setActionBy(loggedInUser);
			formAudit.setActionDate(new Date());
			formAudit.setDescription(messageSource.getMessage("supplier.form.audit.submitted", new Object[] {}, Global.LOCALE));
			formAudit.setBuyer(supplierFormSubmition.getBuyer());
			formAudit.setSupplier(supplierFormSubmition.getSupplier());
			formAudit.setSupplierFormSubmition(supplierFormSubmition);
			formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
			formAudit.setAction(SupplierFormSubmitionAuditType.SUBMITTED);
			supplierFormSubmitionAuditDao.save(formAudit);

			try {
				sendEmailNotificationToSupplierFormOwner(supplierFormSubmition, loggedInUser);

			} catch (Exception e) {
				LOG.error("Error while sending form submitted mail to form owner " + supplierFormSubmition.getRequestedBy().getName() + " :  " + e.getMessage(), e);
			}

		}
		return supplierFormSubmition;
	}

	private void sendEmailNotificationToSupplierFormOwner(SupplierFormSubmition supplierFormSubmition, User loggedInUser) {
		LOG.info("Sending Email form submitted to form owner:" + supplierFormSubmition.getRequestedBy().getName());
		try {
			String buyerTimeZone = "GMT+8:00";
			buyerTimeZone = getTimeZoneByBuyerSettings(supplierFormSubmition.getRequestedBy().getTenantId(), buyerTimeZone);
			TimeZone timeZone = TimeZone.getDefault();
			if (StringUtils.checkString(buyerTimeZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(buyerTimeZone);
			}
			String subject = "Supplier Form Submitted";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("buyerName", supplierFormSubmition.getRequestedBy().getName());
			map.put("supplierName", supplierFormSubmition.getSupplier().getCompanyName());
			map.put("formName", supplierFormSubmition.getName());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(timeZone);
			map.put("date", df.format(new Date()));
			map.put("requestedDate", df.format(supplierFormSubmition.getRequestedDate()));
			map.put("submittedDate", df.format(supplierFormSubmition.getSubmitedDate()));
			map.put("actionBy", loggedInUser.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");

			if (StringUtils.checkString(supplierFormSubmition.getRequestedBy().getCommunicationEmail()).length() > 0 && supplierFormSubmition.getRequestedBy().getEmailNotifications()) {
				sendEmail(supplierFormSubmition.getRequestedBy().getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_SUBMITTED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + supplierFormSubmition.getRequestedBy().getName() + "... Not going to send email notification");
			}
		} catch (Exception e) {
			LOG.error("Error while Sending revised form email to supplier :" + e.getMessage(), e);
		}

	}

	private void buildApprovalFormStattus(SupplierFormSubmition supplierFormSubmition) {
		if (CollectionUtil.isNotEmpty(supplierFormSubmition.getApprovals())) {
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
			String buyerTimeZone = "GMT+8:00";
			for (SupplierFormSubmitionApproval formSubmitionApprovalObj : supplierFormSubmition.getApprovals()) {
				if (formSubmitionApprovalObj.getLevel() == 1) {
					formSubmitionApprovalObj.setActive(true);
				} else {
					formSubmitionApprovalObj.setActive(false);
				}
				formSubmitionApprovalObj.setDone(false);
				if (CollectionUtil.isNotEmpty(formSubmitionApprovalObj.getApprovalUsers())) {
					for (SupplierFormSubmitionApprovalUser formAppUser : formSubmitionApprovalObj.getApprovalUsers()) {
						formAppUser.setApprovalStatus(ApprovalStatus.PENDING);
						formAppUser.setActionDate(null);
						formAppUser.setRemarks(null);
						if (formSubmitionApprovalObj.getLevel() == 1) {
							buyerTimeZone = getTimeZoneByBuyerSettings(formAppUser.getUser().getTenantId(), buyerTimeZone);
							approvalService.sendEmailToSupplierFormApprovers(supplierFormSubmition, formAppUser, buyerTimeZone);

						}
					}
				}
			}
		} else {
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.APPROVED);
		}
	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public SupplierFormSubmition reviseSupplierForm(String formSubId, User loggedInUser, String buyerRemark) {
		SupplierFormSubmition supplierFormSubmition = supplierFormSubmissDao.findFormById(formSubId);
		if (supplierFormSubmition != null) {
			supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.PENDING);
			if (CollectionUtil.isNotEmpty(supplierFormSubmition.getApprovals())) {
				supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
				for (SupplierFormSubmitionApproval formSubmitionApprovalObj : supplierFormSubmition.getApprovals()) {
					formSubmitionApprovalObj.setActive(false);
					formSubmitionApprovalObj.setDone(false);
					if (CollectionUtil.isNotEmpty(formSubmitionApprovalObj.getApprovalUsers())) {
						for (SupplierFormSubmitionApprovalUser formAppUser : formSubmitionApprovalObj.getApprovalUsers()) {
							formAppUser.setApprovalStatus(ApprovalStatus.PENDING);
							formAppUser.setActionDate(null);
							formAppUser.setRemarks(null);

						}
					}
				}
			}
			supplierFormSubmissDao.update(supplierFormSubmition);

			SupplierForm supplierFormObj = supplierFormSubmition.getSupplierForm();
			if (supplierFormObj != null) {
				supplierFormObj.setPendingCount(supplierFormObj.getPendingCount() != null && supplierFormObj.getPendingCount() != 0 ? supplierFormObj.getPendingCount() + 1 : 1);
				supplierFormObj.setSubmittedCount(supplierFormObj.getSubmittedCount() != null && supplierFormObj.getSubmittedCount() != 0 ? supplierFormObj.getSubmittedCount() - 1 : 0);
				supplierFormDao.update(supplierFormObj);
			}

			SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
			formAudit.setActionBy(loggedInUser);
			formAudit.setActionDate(new Date());
			formAudit.setDescription(messageSource.getMessage("supplier.form.audit.revised", new Object[] { buyerRemark }, Global.LOCALE));
			formAudit.setBuyer(supplierFormSubmition.getBuyer());
			formAudit.setSupplier(supplierFormSubmition.getSupplier());
			formAudit.setSupplierFormSubmition(supplierFormSubmition);
			formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
			formAudit.setAction(SupplierFormSubmitionAuditType.REVISED);
			supplierFormSubmitionAuditDao.save(formAudit);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REVISED, "Supplier Form ‘" + supplierFormSubmition.getName() + "' sent for revision", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierForm);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			try {
				sendEmailNotificationToSupplierForReviseForm(supplierFormSubmition, loggedInUser, buyerRemark);
			} catch (Exception e) {
				LOG.error("Error while sending form revised mail to supplier " + supplierFormSubmition.getSupplier().getCompanyName() + " :  " + e.getMessage(), e);
			}
		} else {
			LOG.info("SupplierFormSubmition not found for formSubId " + formSubId);
		}
		return supplierFormSubmition;
	}

	private void sendEmailNotificationToSupplierForReviseForm(SupplierFormSubmition supplierFormSubmition, User loggedInUser, String buyerRemark) {
		LOG.info("Sending Email Request to supplier:" + supplierFormSubmition.getSupplier().getCompanyName());
		try {
			String supplierTimeZone = "GMT+8:00";
			supplierTimeZone = getTimeZoneBySupplierSettings(supplierFormSubmition.getSupplier().getId(), supplierTimeZone);
			TimeZone timeZone = TimeZone.getDefault();
			if (StringUtils.checkString(supplierTimeZone).length() > 0) {
				timeZone = TimeZone.getTimeZone(supplierTimeZone);
			}
			String subject = " Supplier Form Requires Revision";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("buyerName", supplierFormSubmition.getBuyer().getCompanyName());
			map.put("supplierName", supplierFormSubmition.getSupplier().getFullName());
			map.put("formName", supplierFormSubmition.getName());
			map.put("buyerRemark", buyerRemark);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(timeZone);
			map.put("date", df.format(new Date()));
			map.put("requestedDate", df.format(supplierFormSubmition.getRequestedDate()));
			map.put("submittedDate", df.format(supplierFormSubmition.getSubmitedDate()));
			map.put("actionBy", loggedInUser.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");
			User user = userDao.getDetailsOfLoggedinUser(supplierFormSubmition.getSupplier().getLoginEmail());
			if (StringUtils.checkString(supplierFormSubmition.getSupplier().getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
				sendEmail(supplierFormSubmition.getSupplier().getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_REVISED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + supplierFormSubmition.getSupplier().getCompanyName() + "... Not going to send email notification");
			}
		} catch (Exception e) {
			LOG.error("Error while Sending revised form email to supplier :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public boolean resetAttachement(String formSubId, String formSubItemId) {
		return supplierFormSubItemDao.resetAttachement(formSubId, formSubItemId);
	}

	@Override
	public SupplierFormSubmition findOnBoardingFormAvailable(String buyerId, String supplierId) {
		return supplierFormSubmissDao.findOnBoardingFormAvailable(buyerId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSuppFormSubmission(SupplierFormSubmition supplierFormObj) {
		if (StringUtils.checkString(supplierFormObj.getId()).length() > 0) {
			List<SupplierFormSubmitionAudit> formAuditList = supplierFormSubmitionAuditDao.getFormAuditByFormId(supplierFormObj.getId());
			if (CollectionUtil.isNotEmpty(formAuditList)) {
				for (SupplierFormSubmitionAudit audit : formAuditList) {
					supplierFormSubmitionAuditDao.delete(audit);
				}
			}
		}

		SupplierForm supplierForm = supplierFormDao.findById(supplierFormObj.getSupplierForm().getId());
		supplierFormSubmissDao.delete(supplierFormObj);
		if (supplierForm != null) {
			supplierForm.setPendingCount(supplierForm.getPendingCount() != null && supplierForm.getPendingCount() != 0 ? supplierForm.getPendingCount() - 1 : 0);
			supplierFormDao.update(supplierForm);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public long assignFormsToFavSupplier(User loggedInUser, SupplierAssignFormPojo supplierAssignFormPojo) throws ApplicationException {
		List<FavouriteSupplier> favouriteSupplierList = null;
		long notAssignedSuppCount = 0;
		if (Boolean.TRUE == supplierAssignFormPojo.getAssignToAllSuppliers()) {
			favouriteSupplierList = favoriteSupplierDao.getAllActiveFavouriteSupplierByBuyerId(loggedInUser.getTenantId());
		} else {
			favouriteSupplierList = favoriteSupplierDao.getAllActiveFavouriteSupplierBySuppIds(supplierAssignFormPojo.getSupplierIds());
		}
		if (CollectionUtil.isNotEmpty(favouriteSupplierList)) {
			List<FavouriteSupplier> resendFavSuppliersList = new ArrayList<FavouriteSupplier>();
			boolean allFormAssigned = true;
			for (String formId : supplierAssignFormPojo.getSupplierFormIds()) {
				SupplierForm supplierForm = supplierFormDao.findById(formId);
				for (FavouriteSupplier favouriteSupplier : favouriteSupplierList) {
					LOG.info("Assign Form:" + formId + " to supplier.");
					boolean isAssignedForm = false;
					if (Boolean.FALSE == supplierAssignFormPojo.getReassignForm()) {
						long isAssigned = supplierFormSubmissDao.findFormSubBySuppAndFormId(formId, favouriteSupplier.getId());
						if (isAssigned != 0) {
							isAssignedForm = true;
							if (!resendFavSuppliersList.contains(favouriteSupplier)) {
								resendFavSuppliersList.add(favouriteSupplier);
							}
						}
					}
					if (!isAssignedForm) {
						allFormAssigned = false;
						if (supplierForm != null) {
							SupplierFormSubmition supplierFormSubmition = new SupplierFormSubmition();
							supplierFormSubmition.setRequestedBy(loggedInUser);
							supplierFormSubmition.setRequestedDate(new Date());
							supplierFormSubmition.setBuyer(loggedInUser.getBuyer());
							supplierFormSubmition.setSupplier(favouriteSupplier.getSupplier());
							if (favouriteSupplier != null) {
								supplierFormSubmition.setFavouriteSupplier(favouriteSupplier);
							}
							supplierFormSubmition.setSupplierForm(supplierForm);
							supplierFormSubmition.setIsOnboardingForm(Boolean.FALSE);
							supplierFormSubmition.setName(supplierForm.getName());
							supplierFormSubmition.setDescription(supplierForm.getDescription());
							supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.PENDING);
							supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
							List<SupplierFormItem> formItems = supplierFormItemDao.getAllFormitemsbyFormId(supplierForm.getId());
							List<SupplierFormSubmissionItem> listFormSubItems = new ArrayList<SupplierFormSubmissionItem>();
							if (CollectionUtil.isNotEmpty(formItems)) {
								for (SupplierFormItem item : formItems) {
									SupplierFormSubmissionItem supplierCq = new SupplierFormSubmissionItem();
									supplierCq.setSupplierFormItem(item);
									supplierCq.setSupplierFormSubmition(supplierFormSubmition);
									listFormSubItems.add(supplierCq);
								}
							}
							supplierFormSubmition.setFormSubmitionItems(listFormSubItems);
							buildApprovalList(supplierFormSubmition, supplierForm);
							supplierFormSubmition = supplierFormSubmissDao.save(supplierFormSubmition);
							if (supplierFormSubmition != null) {
								supplierForm.setPendingCount(supplierForm.getPendingCount() != null && supplierForm.getPendingCount() != 0 ? supplierForm.getPendingCount() + 1 : 1);
								supplierFormDao.update(supplierForm);

								SupplierFormSubmitionAudit formAudit = new SupplierFormSubmitionAudit();
								formAudit.setActionBy(loggedInUser);
								formAudit.setActionDate(new Date());
								formAudit.setDescription(messageSource.getMessage("supplier.form.audit.assigned", new Object[] {}, Global.LOCALE));
								formAudit.setBuyer(supplierFormSubmition.getBuyer());
								formAudit.setSupplier(supplierFormSubmition.getSupplier());
								formAudit.setSupplierFormSubmition(supplierFormSubmition);
								formAudit.setVisibilityType(SupplierFormSubAuditVisibilityType.BOTH);
								formAudit.setAction(SupplierFormSubmitionAuditType.ASSIGNED);
								supplierFormSubmitionAuditDao.save(formAudit);

								try {
									BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ASSIGNED, "Supplier Form Assigned", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierForm);
									buyerAuditTrailDao.save(buyerAuditTrail);
								} catch (Exception e) {
									LOG.info("Error to create audit trails message");
								}

								try {
									sendEmailNotificationToSupplierForAssignForm(supplierFormSubmition, loggedInUser);

								} catch (Exception e) {
									LOG.error("Error while sending do accept mail to supplier " + supplierFormSubmition.getSupplier().getCompanyName() + " :  " + e.getMessage(), e);
								}
							}
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(resendFavSuppliersList)) {
				notAssignedSuppCount = resendFavSuppliersList.size();
				LOG.info("FORM RESEND" + notAssignedSuppCount);
			}
			if (allFormAssigned) {
				throw new ApplicationException(messageSource.getMessage("supplierForm.already.assigned.allSuppliers", new Object[] {}, Global.LOCALE));
			}

		} else {
			throw new ApplicationException(messageSource.getMessage("supplierForm.assigned.supplist.empty", new Object[] {}, Global.LOCALE));
		}
		return notAssignedSuppCount;

	}

	private void buildApprovalList(SupplierFormSubmition supplierFormSubmition, SupplierForm supplierForm) {
		List<SupplierFormApproval> approvalList = supplierFormApprovalDao.getAllApprovalListByFormId(supplierForm.getId());
		if (CollectionUtil.isNotEmpty(approvalList)) {
			List<SupplierFormSubmitionApproval> approvalSubList = new ArrayList<SupplierFormSubmitionApproval>();
			for (SupplierFormApproval supplierFormApproval : approvalList) {
				SupplierFormSubmitionApproval formSubmitionApprovalObj = new SupplierFormSubmitionApproval();
				if (supplierFormApproval != null) {
					formSubmitionApprovalObj.setApprovalType(supplierFormApproval.getApprovalType());
					formSubmitionApprovalObj.setBatchNo(0);
					formSubmitionApprovalObj.setLevel(supplierFormApproval.getLevel());
					formSubmitionApprovalObj.setSupplierFormSubmition(supplierFormSubmition);
					formSubmitionApprovalObj.setActive(false);
					formSubmitionApprovalObj.setDone(false);
					if (CollectionUtil.isNotEmpty(supplierFormApproval.getApprovalUsers())) {
						List<SupplierFormSubmitionApprovalUser> approvalSubUserList = new ArrayList<SupplierFormSubmitionApprovalUser>();
						for (SupplierFormApprovalUser formAppUser : supplierFormApproval.getApprovalUsers()) {
							SupplierFormSubmitionApprovalUser appSubUser = new SupplierFormSubmitionApprovalUser();
							appSubUser.setApproval(formSubmitionApprovalObj);
							appSubUser.setApprovalStatus(ApprovalStatus.PENDING);
							appSubUser.setUser(formAppUser.getUser());
							approvalSubUserList.add(appSubUser);
						}
						formSubmitionApprovalObj.setApprovalUsers(approvalSubUserList);
					}
					approvalSubList.add(formSubmitionApprovalObj);
				}
			}
			supplierFormSubmition.setApprovals(approvalSubList);
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
		} else {
			supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.APPROVED);
		}
	}

	@Override
	public List<SupplierFormSubmissionItem> findSupplierSubFormItemByIdForBuyer(String formId) {
		List<SupplierFormSubmissionItem> returnList = new ArrayList<SupplierFormSubmissionItem>();
		List<SupplierFormSubmissionItem> itemList = supplierFormSubItemDao.getAllSubFormById(formId);
		LOG.info("Cq Item List Size---------" + itemList.size());
		bulidSupplierSubmissionFormItemListForBuyer(returnList, itemList);
		return returnList;
	}

	private void bulidSupplierSubmissionFormItemListForBuyer(List<SupplierFormSubmissionItem> returnList, List<SupplierFormSubmissionItem> itemList) {
		if (CollectionUtil.isNotEmpty(itemList)) {
			for (SupplierFormSubmissionItem item : itemList) {
				if (item.getSupplierFormItem().getFormOptions() != null) {
					for (SupplierFormItemOption opt : item.getSupplierFormItem().getFormOptions()) {
						LOG.info("SupplierFormItemOption value: " + opt.getValue());
						opt.getValue();
					}
				}
				if (item.getSupplierFormSubmition().getStatus() == SupplierFormSubmitionStatus.SUBMITTED) {
					if (item.getListAnswers() != null) {
						for (SupplierFormSubmitionItemOption ans : item.getListAnswers()) {
							ans.getValue();
						}
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}
	}

	@Override
	public List<SupplierFormItemAttachment> findAllFormDocsByFormItemId(String id) {
		return supplierFormSubmissDao.findAllFormDocsByFormItemId(id);
	}

	@Override
	public SupplierFormItemAttachment findSupplierformItemAttachment(String documentId) {
		return supplierFormSubmissDao.findSupplierformItemAttachment(documentId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierFormSubmition assignFormsPreQualifierForm(User loggedInUser, String buyerId) {
		SupplierFormSubmition supplierFormSubmition = supplierFormSubmissDao.getAssignedSupplierForm(loggedInUser.getTenantId(), buyerId);
		if (supplierFormSubmition == null) {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			SupplierForm supplierForm = buyer.getSupplierForm();
			if (supplierForm != null) {
				supplierFormSubmition = new SupplierFormSubmition();
				supplierFormSubmition.setRequestedBy(supplierForm.getCreatedBy());
				supplierFormSubmition.setRequestedDate(new Date());
				supplierFormSubmition.setBuyer(buyer);
				Supplier supplier = supplierDao.findById(loggedInUser.getTenantId());
				supplierFormSubmition.setSupplier(supplier);
				supplierFormSubmition.setSupplierForm(supplierForm);
				supplierFormSubmition.setIsOnboardingForm(Boolean.TRUE);
				supplierFormSubmition.setName(supplierForm.getName());
				supplierFormSubmition.setDescription(supplierForm.getDescription());
				supplierFormSubmition.setStatus(SupplierFormSubmitionStatus.PENDING);
				List<SupplierFormItem> formItems = supplierFormItemDao.getAllFormitemsbyFormId(supplierForm.getId());
				List<SupplierFormSubmissionItem> listFormSubItems = new ArrayList<SupplierFormSubmissionItem>();
				if (CollectionUtil.isNotEmpty(formItems)) {
					for (SupplierFormItem item : formItems) {
						SupplierFormSubmissionItem supplierCq = new SupplierFormSubmissionItem();
						supplierCq.setSupplierFormItem(item);
						supplierCq.setSupplierFormSubmition(supplierFormSubmition);
						listFormSubItems.add(supplierCq);
					}
				}
				supplierFormSubmition.setFormSubmitionItems(listFormSubItems);
				buildApprovalList(supplierFormSubmition, supplierForm);
				supplierFormSubmition = supplierFormSubmissDao.save(supplierFormSubmition);
			}
		}
		return supplierFormSubmition;
	}

	@Override
	public boolean isFormAssigned(String formId) {
		return supplierFormSubmissDao.isFormAssigned(formId);
	}

	@Override
	public SupplierFormSubmition findOnboardingFormSubmitionByFavSuppIdAndBuyerId(String favSuppId, String buyerId) {
		return supplierFormSubmissDao.findOnboardingFormSubmitionByFavSuppIdAndBuyerId(favSuppId, buyerId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierFormSubmition updateSupplierFormApproval(SupplierFormSubmition supplierFormSubmition, User loggedInUser) {
		LOG.info("Supplier submission Form Id : " + supplierFormSubmition.getId());
		SupplierFormSubmition persistObj = supplierFormSubmissDao.findFormById(supplierFormSubmition.getId());
		List<SupplierFormSubmitionApproval> finalList = new ArrayList<SupplierFormSubmitionApproval>();
		int batchNo = 0;
		Map<Integer, SupplierFormSubmitionApproval> map = new HashMap<Integer, SupplierFormSubmitionApproval>();

		for (SupplierFormSubmitionApproval iterable_element : persistObj.getApprovals()) {
			if (iterable_element.isDone()) {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
			} else {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
				batchNo--;
				break;
			}
		}
		if (batchNo == 0) {
			batchNo = 1;
		} else {
			batchNo++;
		}

		if (CollectionUtil.isNotEmpty(supplierFormSubmition.getApprovals())) {
			int level = 0;
			int level1 = 0;
			for (SupplierFormSubmitionApproval app : supplierFormSubmition.getApprovals()) {
				// app.setSupplierFormSubmition(supplierFormSubmition);
				// app.setLevel(level++);
				LOG.info("Id : " + app.getId() + " Level : " + app.getLevel());
				if (StringUtils.checkString(app.getId()).length() > 0) {
					SupplierFormSubmitionApproval app1 = supplierFormSubmitionApprovalDao.findSupplierFormSubmitionApproval(app.getId());
					app.setActive(app1.isActive());
					app.setBatchNo(app1.getBatchNo());
					app.setDone(app1.isDone());
					app.setLevel(app1.getLevel());
					app.setSupplierFormSubmition(app1.getSupplierFormSubmition());
					level1 = app1.getLevel();
				} else {
					app.setSupplierFormSubmition(persistObj);
					app.setLevel(level1++);
					app.setBatchNo(batchNo);
				}

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (SupplierFormSubmitionApprovalUser approvalUser : app.getApprovalUsers()) {
						LOG.info("approvalUser  : " + approvalUser.toLogString());
						approvalUser.setApproval(app);
						approvalUser.setId(null);

					}
					map.put(++level, app);
				}
			}
			for (Map.Entry<Integer, SupplierFormSubmitionApproval> entry : map.entrySet()) {
				LOG.info("Key  : " + entry.getKey() + "   Level : " + entry.getValue().getLevel());
				SupplierFormSubmitionApproval appp = entry.getValue();
				appp.setLevel(entry.getKey());
				finalList.add(appp);
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		// persistObj.updateSupplierFormApprovals(finalList);

		if (CollectionUtil.isNotEmpty(finalList)) {
			persistObj.setApprovals(finalList);
		} else {
			persistObj.setApprovals(null);
		}

		persistObj = supplierFormSubmissDao.saveOrUpdate(persistObj);
		try {
			sendEmailNotificationToActiveApprovers(persistObj);
		} catch (Exception e) {
			LOG.info("Error while sending email to active approver users:" + e.getMessage());
		}
		return persistObj;
	}

	private void sendEmailNotificationToActiveApprovers(SupplierFormSubmition supplierFormSubmition) {
		SupplierFormSubmitionApproval approvals = supplierFormSubmissDao.getSupplierFormActiveApproverById(supplierFormSubmition.getId());
		if (approvals != null && CollectionUtil.isNotEmpty(approvals.getApprovalUsers())) {
			LOG.info("Sending email approval request to active users");
			String buyerTimeZone = "GMT+8:00";
			for (SupplierFormSubmitionApprovalUser formAppUser : approvals.getApprovalUsers()) {
				buyerTimeZone = getTimeZoneByBuyerSettings(formAppUser.getUser().getTenantId(), buyerTimeZone);
				approvalService.sendEmailToSupplierFormApprovers(approvals.getSupplierFormSubmition(), formAppUser, buyerTimeZone);

			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void addAdditionalApprover(SupplierFormSubmition supplierFormSubmition, String formId, User loggedInUser) {
		SupplierFormSubmition supplierFormSubmitionObj = supplierFormSubmissDao.getSupplierFormForAdditionalApproverById(formId);
		if (supplierFormSubmitionObj != null) {
			addAditionalApprover(supplierFormSubmition, supplierFormSubmitionObj, loggedInUser);
			supplierFormSubmissDao.update(supplierFormSubmitionObj);
		}
	}

	private void addAditionalApprover(SupplierFormSubmition supplierFormSubmition, SupplierFormSubmition persistObj, User logInUser) {
		int batchNo = 0;
		List<SupplierFormSubmitionApproval> additionalApprover = persistObj.getApprovals();

		if (CollectionUtil.isEmpty(additionalApprover)) {
			additionalApprover = new ArrayList<SupplierFormSubmitionApproval>();
		}

		for (SupplierFormSubmitionApproval iterable_element : persistObj.getApprovals()) {
			if (iterable_element.isDone()) {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
			}
		}
		if (batchNo == 0) {
			batchNo = 1;
		} else {
			batchNo++;
		}
		LOG.info("batchNo : " + batchNo);

		List<SupplierFormSubmitionApproval> finalList = new ArrayList<SupplierFormSubmitionApproval>();
		Map<Integer, SupplierFormSubmitionApproval> map = new HashMap<Integer, SupplierFormSubmitionApproval>();
		if (CollectionUtil.isNotEmpty(supplierFormSubmition.getApprovals())) {
			Integer level = 0;
			for (SupplierFormSubmitionApproval app : supplierFormSubmition.getApprovals()) {
				LOG.info("App Level :" + app.getLevel());

				if (StringUtils.checkString(app.getId()).length() > 0) {
					SupplierFormSubmitionApproval app1 = supplierFormSubmitionApprovalDao.findSupplierFormSubmitionApproval(app.getId());
					app.setActive(app1.isActive());
					app.setBatchNo(app1.getBatchNo());
					app.setDone(app1.isDone());
					app.setLevel(app1.getLevel());
					app.setSupplierFormSubmition(app1.getSupplierFormSubmition());
				} else {
					app.setSupplierFormSubmition(persistObj);
					app.setBatchNo(batchNo);
					app.setId(null);
					if (app.getLevel() == null || app.getLevel() == 0) {
						app.setApprovalUsers(null);
					}
				}

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (SupplierFormSubmitionApprovalUser formApprovalUser : app.getApprovalUsers()) {
						formApprovalUser.setApproval(app);
						LOG.info("Sourcing Form Request Id===============>" + formApprovalUser.getId());
						formApprovalUser.setId(null);
					}
					map.put(++level, app);
				}
				LOG.info("App Level :" + level);
			}

			for (Map.Entry<Integer, SupplierFormSubmitionApproval> entry : map.entrySet()) {
				LOG.info("Key  : " + entry.getKey() + "   Level : " + entry.getValue().getLevel());
				SupplierFormSubmitionApproval appp = entry.getValue();
				appp.setLevel(entry.getKey());
				finalList.add(appp);
			}
		}

		if (CollectionUtil.isNotEmpty(finalList)) {
			persistObj.setApprovals(finalList);
		} else {
			persistObj.setApprovals(null);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void finishAdditionalApprover(SupplierFormSubmition supplierFormAdditionalApprover, String formId, User loggedInUser) throws ApplicationException {
		SupplierFormSubmition supplierFormSubmitionObj = supplierFormSubmissDao.getSupplierFormForAdditionalApproverById(formId);
		if (supplierFormSubmitionObj != null) {

			boolean flag = true;
			for (SupplierFormSubmitionApproval sourcingFormApprovalRequest : supplierFormSubmitionObj.getApprovals()) {
				for (SupplierFormSubmitionApprovalUser iterable_element : sourcingFormApprovalRequest.getApprovalUsers()) {
					LOG.info("======iterable_element.getApprovalStatu=======>" + iterable_element.getApprovalStatus());
					if (ApprovalStatus.PENDING == iterable_element.getApprovalStatus()) {
						flag = false;
						break;
					}
				}

			}
			if (flag) {
				throw new ApplicationException("Please Save atleast one Additional Approval");
			}

			addAditionalApprover(supplierFormAdditionalApprover, supplierFormSubmitionObj, loggedInUser);

			if (CollectionUtil.isNotEmpty(supplierFormSubmitionObj.getApprovals())) {
				for (SupplierFormSubmitionApproval app : supplierFormSubmitionObj.getApprovals()) {
					if (!app.isDone()) {
						app.setActive(Boolean.TRUE);
						supplierFormSubmitionObj.setApprovalStatus(SupplierFormApprovalStatus.PENDING);
						break;
					}
				}
			}
			supplierFormSubmissDao.update(supplierFormSubmitionObj);
			try {
				sendEmailNotificationToActiveApprovers(supplierFormSubmitionObj);
			} catch (Exception e) {
				LOG.info("Error while sending email to active approver users:" + e.getMessage());
			}
		}

	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForApprovalofSuppForm(User loguser, String formSubId) {
		EventPermissions permissions = new EventPermissions();
		User loggedInUser = userDao.findById(loguser.getId());

		if (UserType.REQUESTOR_USER == loggedInUser.getUserType()) {
			permissions.setRequesterUser(true);
		}

		// sourcing Owner
		LOG.info("formSubId " + formSubId + " User : " + loggedInUser.getLoginId());
		SupplierFormSubmition supplierFormSubmitionObj = supplierFormSubmissDao.getSupplierFormSubmitionById(formSubId);
		LOG.info("Requested : " + supplierFormSubmitionObj.getRequestedBy().getLoginId());
		if (supplierFormSubmitionObj.getRequestedBy().getId().equals(loggedInUser.getId())) {
			permissions.setOwner(true);
		}

		// Approver
		List<SupplierFormSubmitionApproval> approvals = supplierFormSubmitionObj.getApprovals();
		for (SupplierFormSubmitionApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<SupplierFormSubmitionApprovalUser> users = approval.getApprovalUsers();
				for (SupplierFormSubmitionApprovalUser user : users) {
					if (user.getUser().getId().equals(loggedInUser.getId())) {
						permissions.setApprover(true);
						if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}
		return permissions;

	}

	@Override
	@Transactional(readOnly = true)
	public List<SupplierFormSubmissionPojo> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return supplierFormSubmissDao.myPendingRequestList(loggedInUserTenantId, id, input);
	}

	@Override
	@Transactional(readOnly = true)
	public long myPendingSupplierFormListCount(String loggedInUserTenantId, String userid, TableDataInput input) {
		return supplierFormSubmissDao.myPendingSupplierFormListCount(loggedInUserTenantId, userid, input);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupplierFormApproval> getAllApprovalListByFormId(String formId) {
		return supplierFormSubmissDao.getAllApprovalListByFormId(formId);
	}

}