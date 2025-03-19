package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.PoAuditDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoDocumentDao;
import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.PrDocumentDao;
import com.privasia.procurehere.core.dao.PrItemDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.ErpPoItemPojo;
import com.privasia.procurehere.core.pojo.ErpPoPojo;
import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.MobilePrPojo;
import com.privasia.procurehere.core.pojo.PrPojo;
import com.privasia.procurehere.core.pojo.PrResponseErpPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPrPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PaymentTermsService;
import com.privasia.procurehere.service.PoAuditService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.PrAuditService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.PrTemplateService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.controller.PrItemsSummaryPojo;
import com.privasia.procurehere.web.controller.PrSummaryPojo;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Parveen
 */
@Service
@Transactional(readOnly = true)
public class PrServiceImpl implements PrService {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	PrDao prDao;

	@Autowired
	PrService prService;

	@Autowired
	PrDocumentDao prDocumentDao;

	@Autowired
	PrItemDao prItemDao;

	@Autowired
	UomService uomService;

	@Autowired
	UserService userService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	UserDao userDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	 DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	PoDocumentDao poDocumentDao;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	PoDao poDao;

	@Autowired
	PoAuditDao poAuditDao;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	PoService poService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	PaymentTermsService paymentTermesService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public Pr prCreate(Pr pr, boolean isDraft, User loggedInUser) {
		Pr persistObj = getLoadedPrById(pr.getId());

		// setting pr approvals
		setPrApprovalUsers(pr);

		LOG.info("Old decimal: " + persistObj.getDecimal() + " New decimal : " + pr.getDecimal());
		String decimal = persistObj.getDecimal();

		persistObj.setUrgentPr(pr.getUrgentPr());
		persistObj.setReferenceNumber(pr.getReferenceNumber());
		persistObj.setName(pr.getName());
		persistObj.setDescription(pr.getDescription());
		persistObj.setRequester(pr.getRequester());
		persistObj.setCorrespondenceAddress(pr.getCorrespondenceAddress());
		persistObj.setCurrency(pr.getCurrency());
		persistObj.setCostCenter(pr.getCostCenter());
		persistObj.setAvailableBudget(pr.getDecimal() != null ? pr.getAvailableBudget() != null ?
				pr.getAvailableBudget().setScale(Integer.parseInt(pr.getDecimal()), RoundingMode.HALF_UP) : null : pr.getAvailableBudget());
		persistObj.setDecimal(pr.getDecimal());
		persistObj.setPrDetailCompleted(Boolean.TRUE);
		persistObj.setPrApprovals(pr.getPrApprovals());
		persistObj.setModifiedBy(loggedInUser);
		persistObj.setModifiedDate(new Date());
		persistObj.setEnableApprovalReminder(pr.getEnableApprovalReminder());
		persistObj.setReminderAfterHour(pr.getReminderAfterHour());
		persistObj.setReminderCount(pr.getReminderCount());
		persistObj.setLockBudget(pr.getLockBudget());
		persistObj.setNotifyEventOwner(pr.getNotifyEventOwner());
		if (persistObj.getPaymentTermes() == null) {
			if (pr.getPaymentTermes() != null) {
				persistObj.setPaymentTerm(pr.getPaymentTermes().getPaymentTermCode() + " - " + pr.getPaymentTermes().getDescription());
				persistObj.setPaymentTermDays(pr.getPaymentTermes().getPaymentDays());
				persistObj.setPaymentTermes(pr.getPaymentTermes());
			} else {
				persistObj.setPaymentTerm(pr.getPaymentTerm());
			}
		} else {
			persistObj.setPaymentTerm(pr.getPaymentTerm());
			persistObj.setPaymentTermDays(pr.getPaymentTermDays());
			persistObj.setPaymentTermes(pr.getPaymentTermes());
		}

		if (null != pr.getConversionRate()) {
			persistObj.setConversionRate(pr.getConversionRate());
		}
		if (isDraft) {
			persistObj.setStatus(PrStatus.DRAFT);
		}

		IdSettings idSettings = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(loggedInUser.getTenantId(), "PR");
		if (IdSettingType.BUSINESS_UNIT != idSettings.getIdSettingType()) {
			persistObj.setBusinessUnit(pr.getBusinessUnit());
		}

		prDao.saveOrUpdate(persistObj);

		if (!pr.getDecimal().equals(decimal)) {
			LOG.info("change decimal: ");
			List<PrItem> list = prItemDao.getAllPrItemByPrId(persistObj.getId());
			LOG.info("List :" + list.size());
			if (CollectionUtil.isNotEmpty(list)) {
				int count = 0;
				for (PrItem item : list) {

					if (item.getQuantity() != null && item.getUnitPrice() != null) {
						item.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						item.setQuantity(item.getQuantity() != null ? item.getQuantity().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						item.setTotalAmount(item.getUnitPrice() != null && item.getQuantity() != null ? item.getUnitPrice().multiply(item.getQuantity()).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
						if (item.getItemTax() != null) {
							taxAmount = new BigDecimal(item.getItemTax()).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
							taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
							// Roundup the decimals after calculation
							taxAmount = taxAmount.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
						}
						item.setTaxAmount(taxAmount);
						item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						if (count == 0) {
							persistObj.setTotal(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						}
						count++;
						if (item.getTotalAmountWithTax() == null) {
							item.setTotalAmountWithTax(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						}
						if (persistObj.getAdditionalTax() == null) {
							persistObj.setAdditionalTax(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
						} else {
							persistObj.setAdditionalTax(persistObj.getAdditionalTax().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP));
						}

						// Add back the updated value as we had deducted the old value
						persistObj.setTotal(persistObj.getTotal().add(item.getTotalAmountWithTax()));
						persistObj.setGrandTotal(persistObj.getTotal().add(persistObj.getAdditionalTax()));

						prDao.saveOrUpdate(persistObj);
						prItemDao.update(item);
					}
				}
			}
		}
		return persistObj;
	}

	/**
	 * @param pr
	 */
	private void setPrApprovalUsers(Pr pr) {
		if (CollectionUtil.isNotEmpty(pr.getPrApprovals())) {
			int level = 1;
			for (PrApproval app : pr.getPrApprovals()) {
				app.setPr(pr);
				app.setLevel(level++);
				LOG.info("app Type :" + app.getApprovalType());
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (PrApprovalUser prApprovalUser : app.getApprovalUsers()) {
						prApprovalUser.setApproval(app);
						// Deleting user id which is assigned to bind the object with spring form
						prApprovalUser.setId(null);
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Pr savePr(User loggedInUser) {
		Pr pr = new Pr();
		pr.setPrId(eventIdSettingDao.generateEventId(loggedInUser.getTenantId(), "PR"));
		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(loggedInUser.getTenantId());
		pr.setCurrency(buyerSettings.getCurrency());
		pr.setDecimal(buyerSettings.getDecimal());
		pr.setBuyer(loggedInUser.getBuyer());
		pr.setCreatedBy(loggedInUser);
		pr.setStatus(PrStatus.DRAFT);
		pr.setPrCreatedDate(new Date());
		pr = prDao.saveOrUpdate(pr);

		try {
			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.CREATE);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(messageSource.getMessage("pr.audit.create", new Object[] { pr.getPrId() }, Global.LOCALE));
			audit.setPr(pr);
			prAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
		}

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR '" + pr.getPrId() + "' Created", pr.getCreatedBy().getTenantId(), pr.getCreatedBy(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return pr;
	}

	@Override
	public Pr getPrById(String id) {
		Pr pr = prDao.findById(id);
		if (pr != null) {
			if (pr.getSupplier() != null) {
				pr.getSupplier().getFullName();
			}
			if (pr.getCurrency() != null) {
				pr.getCurrency().getCurrencyCode();
			}
			if (pr.getCreatedBy() != null) {
				pr.getCreatedBy().getName();
			}

			if (pr.getCorrespondenceAddress() != null) {
				pr.getCorrespondenceAddress().getTitle();
			}
			if (pr.getSupplier() != null && pr.getSupplier().getSupplier() != null) {
				pr.getSupplier().getSupplier().getCompanyName();
			}

			if (pr.getTemplate() != null) {
				pr.getTemplate().getTemplateName();
				pr.getTemplate().getContractItemsOnly();
			}
			if (pr.getBuyer() != null) {
				pr.getBuyer().getCompanyName();
			}
			if (pr.getPaymentTermes() != null) {
				pr.getPaymentTermes().getId();
				pr.getPaymentTermes().getPaymentDays();
				pr.getPaymentTermes().getPaymentTermCode();
				pr.getPaymentTermes().getDescription();
			}
		}

		return pr;
	}

	@Override
	public Pr getLoadedPrById(String prId) {
		Pr pr = prDao.findByPrId(prId);
		if (pr.getCorrespondenceAddress() != null) {
			pr.getCorrespondenceAddress().getTitle();
			pr.getCorrespondenceAddress().getLine1();
			pr.getCorrespondenceAddress().getLine2();
			pr.getCorrespondenceAddress().getState().getCountry().getCountryName();
			pr.getCorrespondenceAddress().getCity();
			pr.getCorrespondenceAddress().getZip();
			pr.getCorrespondenceAddress().getState().getStateName();
		}
		if (pr.getDeliveryAddress() != null) {
			pr.getDeliveryAddress().getTitle();
			pr.getDeliveryAddress().getLine1();
			pr.getDeliveryAddress().getLine2();
			pr.getDeliveryAddress().getState().getCountry().getCountryName();
			pr.getDeliveryAddress().getCity();
			pr.getDeliveryAddress().getZip();
			pr.getDeliveryAddress().getState().getStateName();
		}
		if (CollectionUtil.isNotEmpty(pr.getPrTeamMembers())) {
			for (PrTeamMember approver : pr.getPrTeamMembers()) {
				approver.getUser().getName();
			}
		}
		for (PrApproval approval : pr.getPrApprovals()) {
			for (PrApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
				approvalUser.getUser().getLoginId();
			}
		}
		if (CollectionUtil.isNotEmpty(pr.getPrTeamMembers())) {
			for (PrTeamMember teamMembers : pr.getPrTeamMembers()) {
				teamMembers.getTeamMemberType();
				if (teamMembers.getUser() != null)
					teamMembers.getUser().getName();
			}
		}
		if (pr.getTemplate() != null) {
			pr.getTemplate().getTemplateName();
			pr.getTemplate().getContractItemsOnly();
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getFields())) {
				for (PrTemplateField field : pr.getTemplate().getFields()) {
					field.getFieldName();
				}
			}
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getApprovals())) {
				for (PrTemplateApproval approval : pr.getTemplate().getApprovals()) {
					approval.getApprovalType();
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						for (PrTemplateApprovalUser user : approval.getApprovalUsers()) {
							if (user.getUser() != null)
								user.getUser().getName();
						}
					}
				}
			}
		}
		if (pr.getBuyer() != null) {
			pr.getBuyer().getCompanyName();
			pr.getBuyer().getCompanyContactNumber();
			pr.getBuyer().getFaxNumber();
			pr.getBuyer().getLine1();
			pr.getBuyer().getLine2();
			pr.getBuyer().getCity();
			if (pr.getBuyer().getState() != null)
				pr.getBuyer().getState().getStateName();
			if (pr.getBuyer().getRegistrationOfCountry() != null)
				pr.getBuyer().getRegistrationOfCountry().getCountryName();
		}
		if (pr.getSupplier() != null) {
			FavouriteSupplier favSupplier = pr.getSupplier();
			favSupplier.getSupplier().getCompanyName();
			favSupplier.getSupplier().getFaxNumber();
			favSupplier.getSupplier().getCompanyContactNumber();
			favSupplier.getSupplier().getLine1();
			favSupplier.getSupplier().getLine2();
			favSupplier.getSupplier().getCity();
			if (favSupplier.getSupplier().getState() != null) {
				favSupplier.getSupplier().getState().getStateName();
				// favSupplier.getSupplier().getState().getCountry().getCountryName();
			}
			if (favSupplier.getSupplier().getRegistrationOfCountry() != null) {
				favSupplier.getSupplier().getRegistrationOfCountry().getCountryCode();
			}
		}
		if (CollectionUtil.isNotEmpty(pr.getPrContacts())) {
			for (PrContact contact : pr.getPrContacts()) {
				contact.getContactName();
				contact.getContactNumber();
				contact.getComunicationEmail();
			}
		}
		if (pr.getBusinessUnit() != null) {
			pr.getBusinessUnit().getUnitName();
			// for budget
			pr.getBusinessUnit().getId();
		}
		// for budget conversion rate
		if (pr.getCurrency() != null) {
			pr.getCurrency().getCurrencyCode();
		}
		if (pr.getPaymentTermes() != null) {
			pr.getPaymentTermes().getPaymentTermCode();
			pr.getPaymentTermes().getPaymentDays();
			pr.getPaymentTermes().getId();
			pr.getPaymentTermes().getDescription();
		}

		return pr;
	}

	@Override
	public Pr getPlainPrById(String prId) {
		Pr pr = prDao.findByPrId(prId);
		if (pr.getTemplate() != null) {
			pr.getTemplate().getTemplateName();
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getFields())) {
				for (PrTemplateField field : pr.getTemplate().getFields()) {
					field.getFieldName();
				}
			}
		}

		if (pr.getSupplier() != null) {
			FavouriteSupplier favSupplier = pr.getSupplier();
			favSupplier.getSupplier().getCompanyName();
			favSupplier.getSupplier().getFaxNumber();
			favSupplier.getSupplier().getCompanyContactNumber();
			favSupplier.getSupplier().getLine1();
			favSupplier.getSupplier().getLine2();
			favSupplier.getSupplier().getCity();
			if (favSupplier.getSupplier().getState() != null) {
				favSupplier.getSupplier().getState().getStateName();
				// favSupplier.getSupplier().getState().getCountry().getCountryName();
			}
			if (favSupplier.getSupplier().getRegistrationOfCountry() != null) {
				favSupplier.getSupplier().getRegistrationOfCountry().getCountryCode();
			}
		}

		if (CollectionUtil.isNotEmpty(pr.getPrContacts())) {
			for (PrContact contact : pr.getPrContacts()) {
				contact.getContactName();
				contact.getContactNumber();
				contact.getComunicationEmail();
			}
		}
		return pr;
	}

	@Override
	public List<User> findEditorsByPrId(String prId) {
		return prDao.findEditorsByPrId(prId);
	}

	@Override
	public List<User> findViewersByPrId(String prId) {
		return prDao.findViewersByPrId(prId);
	}

	@Override
	@Transactional(readOnly = false)
	public Pr updatePr(Pr pr) {
		return prDao.saveOrUpdate(pr);
	}

	@Override
	@Transactional(readOnly = false)
	public PrDocument savePrDocument(PrDocument prDocument) {
		return prDocumentDao.saveOrUpdate(prDocument);
	}

	@Override
	public List<PrDocument> findAllPrDocsbyPrId(String prId) {
		return prDocumentDao.findAllPrDocsbyPrId(prId);
	}

	@Override
	public PrDocument findPrDocById(String docId) {
		return prDocumentDao.findById(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removePrDocument(PrDocument prDocument) {
		LOG.info("PrDocument " + prDocument);
		prDocumentDao.delete(prDocument);
	}

	/**
	 * @param supplierChoice
	 * @param pr
	 */
	@Override
	@Transactional(readOnly = false)
	public Pr updatePrSupplier(Pr pr, String supplierChoice, boolean isDraft) {
		Pr persistObj = findPrSupplierByPrId(pr.getId());
		if (StringUtils.checkString(pr.getId()).length() > 0) {
			if (supplierChoice.equals("LIST")) {
				persistObj.setSupplierName(null);
				persistObj.setSupplierAddress(null);
				persistObj.setSupplierTelNumber(null);
				persistObj.setSupplierFaxNumber(null);
				persistObj.setSupplierTaxNumber(null);
				persistObj.setSupplier(pr.getSupplier());
				if (isDraft)
					persistObj.setStatus(pr.getStatus());
				LOG.info("pr supplier from fav. list Updated succesfully");
			} else {
				persistObj.setSupplierName(pr.getSupplierName());
				persistObj.setSupplierAddress(pr.getSupplierAddress());
				persistObj.setSupplierTelNumber(pr.getSupplierTelNumber());
				persistObj.setSupplierFaxNumber(pr.getSupplierFaxNumber());
				persistObj.setSupplierTaxNumber(pr.getSupplierTaxNumber());
				persistObj.setSupplier(null);
				if (isDraft)
					persistObj.setStatus(pr.getStatus());
				LOG.info("pr supplier manual Updated succesfully");
			}
			persistObj.setSupplierCompleted(Boolean.TRUE);
		}
		return prDao.saveOrUpdate(persistObj);

	}

	@Override
	public boolean isExists(PrItem prItem) {
		return prItemDao.isExists(prItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public PrItem savePrItemBare(PrItem prItem) throws Exception {
		return prItemDao.saveOrUpdate(prItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public PrItem savePrItem(PrItem prItem, User loggedInUser) throws Exception {
		PrItem item = new PrItem();
		Pr pr = prDao.findById(prItem.getPr().getId());
		if (prItem.getProduct() != null)
			item.setProduct(productListMaintenanceService.getProductCategoryById(prItem.getProduct().getId()));
		item.setBuyer(loggedInUser.getBuyer());
		// Assuming pr.item contains smart quotes
		item.setItemDescription(prService.replaceSmartQuotes(prItem.getItemDescription()));
		item.setItemName(prService.replaceSmartQuotes(prItem.getItemName()));
		item.setItemTax(prItem.getItemTax());
		item.setPricePerUnit(prItem.getPricePerUnit());
		if (prItem.getUnitPrice() != null) {
			item.setUnitPrice(prItem.getUnitPrice().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (prItem.getQuantity() != null) {
			item.setQuantity(prItem.getQuantity().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		try {
			LOG.info(" Chote Yha Kya Kr Rha H Be");
			item.setTotalAmount((prItem.getUnitPrice() != null && prItem.getQuantity() != null) ? (prItem.getUnitPrice().divide(prItem.getPricePerUnit(), Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP))
			.multiply(item.getQuantity())
			.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP) 
			: new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
			BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
			if (prItem.getItemTax() != null) {
				taxAmount = new BigDecimal(prItem.getItemTax()).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
				taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
				taxAmount = taxAmount.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
			}
			item.setTaxAmount(taxAmount);
			item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0));
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new NotAllowedException(messageSource.getMessage("common.number.format.error", new Object[] {}, Global.LOCALE));
		}
		item.setPr(prItem.getPr());
		item.setParent(prItem.getParent());
		if (prItem.getProductContractItem() != null) {
			item.setProductContractItem(prItem.getProductContractItem());
		}
		item.setUnit(prItem.getUnit());
		item.setProductCategory(prItem.getProductCategory());
		item.setFreeTextItemEntered(prItem.getFreeTextItemEntered());

		item.setField1(prItem.getField1());
		item.setField2(prItem.getField2());
		item.setField3(prItem.getField3());
		item.setField4(prItem.getField4());
		item.setField5(prItem.getField5());
		item.setField6(prItem.getField6());
		item.setField7(prItem.getField7());
		item.setField8(prItem.getField8());
		item.setField9(prItem.getField9());
		item.setField10(prItem.getField10());
		item.setBuyer(loggedInUser.getBuyer());
		if (item.getParent() == null) {
			int itemLevel = 0;
			List<PrItem> list = prItemDao.getPrItemLevelOrder(item.getPr().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			item.setLevel(itemLevel + 1);
			item.setOrder(0);
		} else {
			PrItem parent = prItemDao.findById(item.getParent().getId());
			item.setLevel(parent.getLevel());
			LOG.info("parent.getChildren() " + parent.getChildren());
			item.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
		}

		// Updating total and grand total after add new item

		LOG.info("===================" + item.getTotalAmountWithTax());
		LOG.info("===================" + pr.getTotal());
		LOG.info("===================" + pr.getAdditionalTax());
		if (pr.getTotal() == null) {
			pr.setTotal(BigDecimal.ZERO);
		}
		if (pr.getAdditionalTax() == null) {
			pr.setAdditionalTax(BigDecimal.ZERO);
		}
		if (pr.getGrandTotal() == null) {
			pr.setGrandTotal(BigDecimal.ZERO);
		}

		pr.setTotal(pr.getTotal() != null && item.getTotalAmountWithTax() != null ? pr.getTotal().add(item.getTotalAmountWithTax()) : new BigDecimal(0));
		pr.setGrandTotal(pr.getTotal() != null && pr.getAdditionalTax() != null ? pr.getTotal().add(pr.getAdditionalTax()) : new BigDecimal(0));
		LOG.info("===================" + pr.getGrandTotal());
		LOG.info("===================" + pr.getTotal());
		if (CollectionUtil.isNotEmpty(validatePr(pr, Pr.PrPurchaseItem.class)) || CollectionUtil.isNotEmpty(validatePr(pr, PrItem.PurchaseItem.class))) {
			String message = ", ";
			if (CollectionUtil.isNotEmpty(validatePr(pr, Pr.PrPurchaseItem.class))) {
				for (String errMessage : validatePr(pr, Pr.PrPurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			if (CollectionUtil.isNotEmpty(validatePr(pr, PrItem.PurchaseItem.class))) {
				for (String errMessage : validatePr(pr, PrItem.PurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			LOG.error("message :" + message);
			throw new NotAllowedException(message);
		}
		prDao.update(pr);

		return prItemDao.saveOrUpdate(item);
	}

	@Override
	public List<PrItem> findAllPrItemByPrId(String prId) {
		List<PrItem> returnList = new ArrayList<PrItem>();
		List<PrItem> list = prItemDao.getAllPrItemByPrId(prId);
		LOG.info("List :" + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (PrItem item : list) {
				PrItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}

				if (item.getProductCategory() != null) {
					item.getProductCategory().getProductName();
				}

				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PrItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<PrItem>());
						}
						if (child.getUnit() != null) {
							child.getUnit().getUom();
						}

						if (child.getProductCategory() != null) {
							child.getProductCategory().getProductName();
						}

						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	@Deprecated
	public PrItem getPrItembyPrIdAndPrItemId(String prId, String prItemId) {
		return prItemDao.getPrItembyPrIdAndPrItemId(prId, prItemId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void updatePrItem(PrItem prItem) throws Exception, NotAllowedException {
		Pr pr = prDao.findById(prItem.getPr().getId());
		PrItem persistPrItemObj = prItemDao.findById(prItem.getId());
		//can't update prItem free text to not freetext
		/*if (prItem.getProduct() != null)
			persistPrItemObj.setProduct(productListMaintenanceService.getProductCategoryById(prItem.getProduct().getId()));*/
		persistPrItemObj.setItemName(prItem.getItemName());
		// strip additional decimals from quantity and price
		persistPrItemObj.setUnitPrice(prItem.getUnitPrice() != null ? prItem.getUnitPrice().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		persistPrItemObj.setPricePerUnit(prItem.getPricePerUnit() != null ? prItem.getPricePerUnit().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		persistPrItemObj.setQuantity(prItem.getQuantity() != null ? prItem.getQuantity().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		persistPrItemObj.setItemDescription(prItem.getItemDescription());
		persistPrItemObj.setItemTax(prItem.getItemTax());
		persistPrItemObj.setUnit(prItem.getUnit());
		persistPrItemObj.setProductCategory(prItem.getProductCategory());
		persistPrItemObj.setFreeTextItemEntered(prItem.getFreeTextItemEntered());

		try {
			if (persistPrItemObj.getParent() != null) {
				// subtract it initially to add the updated value later.
				pr.setTotal(pr.getTotal() != null ? pr.getTotal().subtract(persistPrItemObj.getTotalAmountWithTax()) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
				persistPrItemObj.setTotalAmount(persistPrItemObj.getUnitPrice() != null && persistPrItemObj.getQuantity() != null && persistPrItemObj.getPricePerUnit() != null
						&& !persistPrItemObj.getPricePerUnit().equals(0)? (persistPrItemObj.getUnitPrice().divide(persistPrItemObj.getPricePerUnit())).multiply(persistPrItemObj.getQuantity()).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
				BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
				if (prItem.getItemTax() != null) {
					taxAmount = new BigDecimal(prItem.getItemTax()).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
					taxAmount = persistPrItemObj.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
					// Roundup the decimals after calculation
					taxAmount = taxAmount.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_HALF_UP);
				}
				persistPrItemObj.setTaxAmount(taxAmount);
				persistPrItemObj.setTotalAmountWithTax(persistPrItemObj.getTotalAmount() != null ? persistPrItemObj.getTotalAmount().add(persistPrItemObj.getTaxAmount()) : new BigDecimal(0).setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
			}
		} catch (Exception e) {
			throw new NotAllowedException(messageSource.getMessage("common.number.format.error", new Object[] {}, Global.LOCALE));
		}

		if (prItem.getProductContractItem() != null) {
			persistPrItemObj.setProductContractItem(prItem.getProductContractItem());
		}
		persistPrItemObj.setField1(prItem.getField1());
		persistPrItemObj.setField2(prItem.getField2());
		persistPrItemObj.setField3(prItem.getField3());
		persistPrItemObj.setField4(prItem.getField4());
		persistPrItemObj.setField5(prItem.getField5());
		persistPrItemObj.setField6(prItem.getField6());
		persistPrItemObj.setField7(prItem.getField7());
		persistPrItemObj.setField8(prItem.getField8());
		persistPrItemObj.setField9(prItem.getField9());
		persistPrItemObj.setField10(prItem.getField10());

		// Updating total and grand total after update item
		if (pr.getTotal() == null) {
			pr.setTotal(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (persistPrItemObj.getTotalAmountWithTax() == null) {
			persistPrItemObj.setTotalAmountWithTax(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (pr.getAdditionalTax() == null) {
			pr.setAdditionalTax(BigDecimal.ZERO.setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		} else {
			pr.setAdditionalTax(pr.getAdditionalTax().setScale(Integer.parseInt(pr.getDecimal()), BigDecimal.ROUND_DOWN));
		}

		// Add back the updated value as we had deducted the old value
		pr.setTotal(pr.getTotal().add(persistPrItemObj.getTotalAmountWithTax()));
		pr.setGrandTotal(pr.getTotal().add(pr.getAdditionalTax()));
		if (CollectionUtil.isNotEmpty(validatePr(pr, Pr.PrPurchaseItem.class)) || CollectionUtil.isNotEmpty(validatePr(pr, PrItem.PurchaseItem.class))) {
			String message = ", ";
			if (CollectionUtil.isNotEmpty(validatePr(pr, Pr.PrPurchaseItem.class))) {
				for (String errMessage : validatePr(pr, Pr.PrPurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			if (CollectionUtil.isNotEmpty(validatePr(pr, PrItem.PurchaseItem.class))) {
				for (String errMessage : validatePr(pr, PrItem.PurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			LOG.error("message :" + message);
			throw new NotAllowedException(message);
		}
		prDao.update(pr);
		prItemDao.update(persistPrItemObj);
	}

	/**
	 * @param pr
	 */
	public List<String> validatePr(Pr pr, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<Pr>> constraintViolations = validator.validate(pr, validations);
		for (ConstraintViolation<Pr> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<PrItem> deletePrItems(String[] prItemIds, String prId) {
		prItemDao.deletePrItems(prItemIds, prId);
		/*
		 * List<PrItem> list = prItemDao.getPrItemsbyId(prId); if (CollectionUtil.isNotEmpty(list)) { for (PrItem item :
		 * list) { PrItem parent = item.createShallowCopy(); if (item.getParent() == null) { returnList.add(parent); }
		 * if (CollectionUtil.isNotEmpty(item.getChildren())) { for (PrItem child : item.getChildren()) { if
		 * (parent.getChildren() == null) { parent.setChildren(new ArrayList<PrItem>()); }
		 * parent.getChildren().add(child.createShallowCopy()); LOG.info("tot with tax :" +
		 * child.getTotalAmountWithTax()); } } } }
		 */
		return findAllPrItemByPrId(prId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reOrderPrItems(PrItem prItem) throws NotAllowedException {
		LOG.info("pr ITEM Object :: " + prItem.toLogString());
		PrItem prDbItem = getPrItembyPrItemId(prItem.getId());
		if (CollectionUtil.isNotEmpty(prDbItem.getChildren()) && prItem.getParent() != null) {
			LOG.info("Pr Item cannot be made a child if it has sub items");
			throw new NotAllowedException(messageSource.getMessage("subitem.reOrder.error", new Object[] { prDbItem.getItemName() }, Global.LOCALE));
		}
		LOG.info("DB Pr ITEM DETAILS ::" + prDbItem.toLogString());
		int oldOrder = prDbItem.getOrder();
		int newOrder = prItem.getOrder();
		int oldLevel = prDbItem.getLevel();
		int newLevel = prItem.getOrder(); // this will be ignored if it is made a child

		PrItem newParent = null;
		if (prItem.getParent() != null && StringUtils.checkString(prItem.getParent().getId()).length() > 0) {
			newParent = getPrItembyPrItemId(prItem.getParent().getId());
		}
		PrItem oldParent = prDbItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}
		// Update it to new position.
		prDbItem.setOrder(newOrder);
		prDbItem.setLevel(newParent == null ? prItem.getOrder() : prItem.getParent().getLevel());
		prDbItem.setParent(newParent);
		prItemDao.updateItemOrder(prDbItem.getPr().getId(), prDbItem, (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}

	@Override
	@Transactional(readOnly = false)
	@Deprecated
	public PrItem getPrItembyPrItemId(String itemId) {
		return prItemDao.getPrItembyPrItemId(itemId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletefieldInPrItems(String label, String prId) {
		prItemDao.deleteNewFieldPr(label, prId);
	}

	@Override
	public PrItem getParentbyLevelId(String prId, Integer level) {
		return prItemDao.getParentbyLevelId(prId, level);
	}

	@Override
	public void deletePrItemsbyPrId(String prId) {
		prItemDao.deletePrItemsbyPrid(prId);

	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addEditor(String prId, String userId) {
		LOG.info("PrService Impl  addEditor: prId " + prId + " userId: " + userId);
		Pr pr = getLoadedPrById(prId);
		List<User> editors = findEditorsByPrId(prId);
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.add(user);
		// pr.setPrEditors(editors);
		prDao.saveOrUpdate(pr);
		LOG.info("Editors :" + editors.size());
		return editors;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addViewer(String prId, String userId) {
		LOG.info("PrService Impl  addViewer: prId " + prId + " userId: " + userId);
		Pr pr = getLoadedPrById(prId);
		List<User> viewers = findViewersByPrId(prId);
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.add(user);
		// pr.setPrViewers(viewers);
		prDao.saveOrUpdate(pr);
		LOG.info("viewers :" + viewers.size());
		return viewers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEditorUser(String prId, String userId) {
		Pr pr = getLoadedPrById(prId);
		List<User> editors = findEditorsByPrId(prId);
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.remove(user);
		// pr.setPrEditors(editors);
		prDao.update(pr);
		return editors;

	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeViwer(String prId, String userId) {
		Pr pr = getLoadedPrById(prId);
		List<User> viewers = findViewersByPrId(prId);
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.remove(user);
		// pr.setPrViewers(viewers);
		prDao.update(pr);
		return viewers;
	}

	@Override
	public Pr findPrSupplierByPrId(String prId) {
		Pr pr = prDao.findPrSupplierByPrId(prId);
		for (PrContact contact : pr.getPrContacts()) {
			contact.getContactName();
		}
		for (PrComment comment : pr.getPrComments()) {
			comment.getCreatedBy().getName();
		}
		if (CollectionUtil.isNotEmpty(pr.getPrTeamMembers())) {
			for (PrTeamMember teamMembers : pr.getPrTeamMembers()) {
				teamMembers.getTeamMemberType();
				teamMembers.getUser().getName();
			}
		}

		if (pr.getSupplier() != null && pr.getSupplier().getSupplier() != null) {
			pr.getSupplier().getSupplier().getCompanyName();
		}
		if (pr.getTemplate() != null) {
			pr.getTemplate().getTemplateName();
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getFields())) {
				for (PrTemplateField field : pr.getTemplate().getFields()) {
					field.getFieldName();
				}
			}
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getApprovals())) {
				for (PrTemplateApproval approval : pr.getTemplate().getApprovals()) {
					approval.getApprovalType();
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						for (PrTemplateApprovalUser user : approval.getApprovalUsers()) {
							if (user.getUser() != null)
								user.getUser().getName();
						}
					}
				}
			}
		}
		return pr;
	}

	@Override
	public PrContact getPrContactById(String contactId) {
		return prDao.getPrContactById(contactId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePrContact(String contactId) {
		prDao.deletePrContact(contactId);
	}

	@Override
	public boolean isExistsPrContact(PrContact prContact, String prId) {
		return prDao.isExists(prContact, prId);
	}

	@Override
	public List<PrItem> findAllPrItemByPrIdForSummary(String prId) {
		return prItemDao.getAllPrItemByPrId(prId);
	}

	@Override
	public List<PrContact> findAllPrContactsByPrId(String prId) {
		return prDao.findAllPrContactsByPrId(prId);
	}

	@Override
	public List<PrApproval> getAllPrApprovalsByPrId(String prId) {
		return prDao.getAllPrApprovalsByPrId(prId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<PrComment> findAllPrCommentByPrId(String prId) {
		return prDao.getAllPrCommentByPrId(prId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<PrTeamMember> addTeamMemberToList(String prId, String userId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " prId: " + prId + " userId: " + userId);
		Pr pr = getLoadedPrById(prId);
		List<PrTeamMember> teamMembers = pr.getPrTeamMembers();
		LOG.info("teamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<PrTeamMember>();
		}
		User user = userService.getUsersById(userId);
		PrTeamMember prTeamMember = new PrTeamMember();
		prTeamMember.setPr(pr);
		prTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (PrTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				prTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;

			}
		}
		prTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(prTeamMember);
		}
		LOG.info("TeamMembers : " + prTeamMember.toLogString());

		pr.setPrTeamMembers(teamMembers);
		prDao.update(pr);

		try {
			if (!exists) {
				PrAudit audit = new PrAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PrAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), pr);
				prAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '"+memberType.getValue()+ "' for PR '"+pr.getPrId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
				buyerAuditTrailDao.save(buyerAuditTrail);

				LOG.info("************* PR Team Member event audit added successfully *************");
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					PrAudit audit = new PrAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PrAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), pr);
					prAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '"+previousMemberType+ "'to '"+memberType.getValue()+"' for PR '"+pr.getPrId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
					buyerAuditTrailDao.save(buyerAuditTrail);

					LOG.info("************* PR Team Member event audit changed successfully *************");
				}
			}

		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		return teamMembers;
	}

	// @Override
	// public List<EventTeamMember> getTeamMembersForPr(String prId) {
	// return prDao.getTeamMembersForPr(prId);
	// }

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String prId, String userId, PrTeamMember dbTeamMember) {
		Pr pr = getLoadedPrById(prId);
		List<PrTeamMember> teamMembers = pr.getPrTeamMembers();
		LOG.info("**************" + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<PrTeamMember>();
		}
		// LOG.info(teamMembers);
		teamMembers.remove(dbTeamMember);
		dbTeamMember.setPr(null);
		pr.setPrTeamMembers(teamMembers);
		prDao.update(pr);

		try {
			PrAudit audit = new PrAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PrAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), pr);
			prAuditService.save(audit);

			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed as '"+dbTeamMember.getTeamMemberType().getValue()+ "' for PR '"+pr.getPrId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);

			LOG.info("************* PR Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error removing audit details: " + e.getMessage());
		}

		List<User> userList = new ArrayList<User>();
		LOG.info("TeamMembers getTeamMembers(): " + pr.getPrTeamMembers());
		for (PrTeamMember app : pr.getPrTeamMembers()) {
			try {
				userList.add((User) app.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMembers operation : " + e.getMessage(), e);
			}
		}
		return userList;
	}

	@Override
	public PrTeamMember getPrTeamMemberByUserIdAndPrId(String prId, String userId) {
		return prDao.getPrTeamMemberByUserIdAndPrId(prId, userId);
	}

	@Override
	public List<Pr> findAllPendingPr(String userId, String tenantId, TableDataInput input) {
		return prDao.findAllPendingPr(userId, tenantId, input);
	}

	@Override
	public long findTotalFilteredPendingPr(String tenantId, String userId, TableDataInput input) {
		return prDao.findTotalFilteredPendingPr(tenantId, userId, input);
	}

	@Override
	public long findTotalDraftPr(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		return prDao.findTotalDraftPr(tenantId, userId, input,businessUnitIds);
	}

	@Override
	public long findTotalPendingPr(String tenantId, String userId,List<String> businessUnitIds) {
		return prDao.findTotalPendingPr(tenantId, userId, businessUnitIds);
	}

	@Override
	public List<PrItem> findAllChildPrItemByPrId(String prId) {
		return prItemDao.findAllChildPrItemByPrId(prId);
	}

	@Override
	public List<PrItem> findAllLoadedChildPrItemByPrId(String prId) {
		List<PrItem> list = prItemDao.findAllChildPrItemByPrId(prId);
		for (PrItem prItem : list) {
			if (prItem.getProductCategory() != null)
				prItem.getProductCategory().getId();
		}

		return list;
	}

	@Override
	public List<Pr> findAllDraftPr(String userId, String tenantId, TableDataInput input) {
		return prDao.findAllDraftPr(userId, tenantId, input);
	}

	@Override
	public long findTotalFilteredDraftPr(String userId, String tenantId, TableDataInput input) {
		return prDao.findTotalFilteredDraftPr(userId, tenantId, input);
	}

	@Override
	public List<Pr> findAllPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return prDao.findAllPo(userId, tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return prDao.findTotalFilteredPo(userId, tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalApprovedPrs(String tenantId, String userId,List<String> businessUnits) {
		return prDao.findTotalApprovedPrs(tenantId, userId,businessUnits);
	}

	@Override
	@Transactional(readOnly = false)
	public Pr copyFromTemplate(String templateId, User loggedInUser, String tenantId, BusinessUnit buyerbusinessUnit) throws ApplicationException {
		PrTemplate prTemplate = prTemplateService.getPrTemplateById(templateId);

		Pr newPr = new Pr();
		// autoPopulate currency for budget
		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (null != buyerSettings.getCurrency()) {
			newPr.setCurrency(buyerSettings.getCurrency());
		}
		newPr.setTemplate(prTemplate);
		newPr.setBuyer(loggedInUser.getBuyer());
		newPr.setCreatedBy(loggedInUser);
		newPr.setStatus(PrStatus.DRAFT);
		newPr.setPrCreatedDate(new Date());
		// p2p add budgetLock
		newPr.setLockBudget(prTemplate.getLockBudget());

		// copy approval from template
		if (CollectionUtil.isNotEmpty(prTemplate.getApprovals())) {
			List<PrApproval> approvalList = new ArrayList<>();
			for (PrTemplateApproval prTemplateApproval : prTemplate.getApprovals()) {
				LOG.info("PR Template Approval Level :" + prTemplateApproval.getLevel());
				PrApproval newPrApproval = new PrApproval();
				newPrApproval.setApprovalType(prTemplateApproval.getApprovalType());
				newPrApproval.setLevel(prTemplateApproval.getLevel());
				newPrApproval.setPr(newPr);
				if (CollectionUtil.isNotEmpty(prTemplateApproval.getApprovalUsers())) {
					List<PrApprovalUser> prApprovalList = new ArrayList<>();
					for (PrTemplateApprovalUser prTemplateApprovalUser : prTemplateApproval.getApprovalUsers()) {
						PrApprovalUser prApprovalUser = new PrApprovalUser();
						prApprovalUser.setApprovalStatus(prTemplateApprovalUser.getApprovalStatus());
						prApprovalUser.setApproval(newPrApproval);
						prApprovalUser.setRemarks(prTemplateApprovalUser.getRemarks());
						prApprovalUser.setUser(prTemplateApprovalUser.getUser());
						prApprovalList.add(prApprovalUser);
					}
					newPrApproval.setApprovalUsers(prApprovalList);
				}
				approvalList.add(newPrApproval);
			}
			newPr.setPrApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(prTemplate.getFields())) {

			for (PrTemplateField field : prTemplate.getFields()) {
				switch (field.getFieldName()) {
				case BASE_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newPr.setCurrency(currency);
						LOG.info("Base Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case COST_CENTER:
					if (field.getDefaultValue() != null) {
						CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
						newPr.setCostCenter(costCenter);
						LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
					}
					break;
				case BUSINESS_UNIT:
					if (field.getDefaultValue() != null) {
						BusinessUnit businessUnit = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
						newPr.setBusinessUnit(businessUnit);
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;

				case AVAILABLE_BUDGET:
					if (field.getDefaultValue() != null) {
						newPr.setAvailableBudget(new BigDecimal(field.getDefaultValue()));
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				case HIDE_OPEN_SUPPLIER:
					if (field.getVisible() != null) {
						newPr.setHideContractBased(field.getVisible());
					}
					break;
				case DECIMAL:
					if (field.getDefaultValue() != null) {
						newPr.setDecimal(field.getDefaultValue());
						LOG.info("Decimal Default value :  " + field.getDefaultValue());
					}
					break;
				case CORRESPONDENCE_ADDRESS:
					if (field.getDefaultValue() != null) {
						BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(field.getDefaultValue());

						if (buyerAddress != null && buyerAddress.getStatus() == Status.INACTIVE) {
							LOG.info("inactive Delivery address found ....");
							if (field.getReadOnly()) {
								throw new ApplicationException("Delivery address is Inactive for Template:" + prTemplate.getTemplateName());
							} else {
								LOG.info("inactive Delivery address found with not read only....");
								// no need to do anything here
							}
						} else {
							newPr.setCorrespondenceAddress(buyerAddress);
						}
						LOG.info("buyerAddress : " + buyerAddress + "Default value :  " + field.getDefaultValue());
					}
					break;
				case PR_NAME:
					if (field.getDefaultValue() != null) {
						newPr.setName(field.getDefaultValue());
					}
					break;
				case PAYMENT_TERM:
					// if (field.getDefaultValue() != null) {
					// newPr.setPaymentTerm(field.getDefaultValue());
					// }
					if (field.getDefaultValue() != null) {
						PaymentTermes pt = paymentTermesService.getPaymentTermesById(field.getDefaultValue());
						if (pt != null) {
							newPr.setPaymentTermes(pt);
							newPr.setPaymentTerm(pt.getPaymentTermCode().toString() + " - " + pt.getDescription());
							newPr.setPaymentTermDays(pt.getPaymentDays());
						} else {
							// this added bcoz pr gives validation error at time of saving
							// so setting first paymentTerm String val in pr to templates paymentTerm String
							newPr.setPaymentTerm(field.getDefaultValue());
						}
					}
					break;
				case REQUESTER:
					if (field.getDefaultValue() != null) {
						newPr.setRequester(field.getDefaultValue());
					}
					break;
				case TERM_AND_CONDITION:
					if (field.getDefaultValue() != null) {
						newPr.setTermsAndConditions(field.getDefaultValue());
					}
					break;
				case CONVERSION_RATE:
					if (field.getDefaultValue() != null) {
						newPr.setConversionRate(new BigDecimal(field.getDefaultValue()));
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				default:
					break;
				}
			}
		}
		// if buyer setting is enable for id generation upon business unit then user can select the own business unit
		if (eventIdSettingsDao.isBusinessSettingEnable(tenantId, "PR")) {
			if (buyerbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newPr.setBusinessUnit(buyerbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newPr.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

			}
		}

		// budget checking
		if (Boolean.TRUE == newPr.getLockBudget()) {
			if (null != newPr.getBusinessUnit() && null != newPr.getCostCenter()) {
				Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(StringUtils.checkString(newPr.getBusinessUnit().getId()), StringUtils.checkString(newPr.getCostCenter().getId()));
				if (budget != null) {
					if (budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
						LOG.info("Budget is approved or active");
					} else {
						// PR cant be made if budget is not ACTIVE or APPROVED
						LOG.info("Budget is not  approved or active");
						throw new ApplicationException(messageSource.getMessage("budget.pr.create.error", new Object[] {}, Global.LOCALE));
					}
				} else {
					LOG.info("Budget not created");
					throw new ApplicationException(messageSource.getMessage("err.msg.budget.not.create", new Object[] {}, Global.LOCALE));
				}

			}
		}

		newPr.setPrId(eventIdSettingDao.generateEventIdByBusinessUnit(tenantId, "PR", newPr.getBusinessUnit()));
		newPr = prDao.saveOrUpdate(newPr);

		List<PrTeamMember> teamMembers = new ArrayList<PrTeamMember>();
		if (CollectionUtil.isNotEmpty(prTemplate.getTeamMembers())) {
			for (TemplatePrTeamMembers team : prTemplate.getTeamMembers()) {
				PrTeamMember newTeamMembers = new PrTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setPr(newPr);
				teamMembers.add(newTeamMembers);
				sendAddTeamMemberEmailNotificationEmail(newPr, team.getUser(), newTeamMembers.getTeamMemberType());
			}
			newPr.setPrTeamMembers(teamMembers);
		}

		try {
			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.CREATE);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(messageSource.getMessage("pr.audit.create", new Object[] { newPr.getPrId() }, Global.LOCALE));
			audit.setPr(newPr);
			prAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
		}

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR '" + newPr.getPrId() + "' Created", newPr.getCreatedBy().getTenantId(), newPr.getCreatedBy(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		if (null != newPr.getBusinessUnit()) {
			newPr.getBusinessUnit().getId();
		}
		return newPr;
	}

	@Override
	public boolean checkProductInUse(String productId) {
		return prItemDao.checkProductInUse(productId);
	}

	@Override
	@Transactional(readOnly = false)
	public Pr copyFrom(String prId, User loggedInUser, BusinessUnit businessUnit) throws ApplicationException {
		Pr pr = this.getPrById(prId);

		if (pr.getDeliveryAddress() != null && pr.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for Pr:" + pr.getPrId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		// copy from previous
		Pr newPr = pr.copyFrom(pr);
		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "PR")) {
			if (businessUnit != null) {
				LOG.info("business unit selected by user choice");
				newPr.setBusinessUnit(businessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newPr.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

			}
		}
		// budget checking
		if (Boolean.TRUE == newPr.getLockBudget()) {
			if (null != newPr.getBusinessUnit() && null != newPr.getCostCenter()) {
				Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(StringUtils.checkString(newPr.getBusinessUnit().getId()), StringUtils.checkString(newPr.getCostCenter().getId()));
				if (budget != null) {
					if (budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
						LOG.info("Budget is approved or active");
					} else {
						// PR cant be made if budget is not ACTIVE or APPROVED
						LOG.info("Budget is not  approved or active");
						throw new ApplicationException(messageSource.getMessage("budget.pr.create.error", new Object[] {}, Global.LOCALE));
					}
				} else {
					LOG.info("Budget not created");
					throw new ApplicationException(messageSource.getMessage("err.msg.budget.not.create", new Object[] {}, Global.LOCALE));
				}

			}
		}
		newPr.setPrId(eventIdSettingDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "PR", newPr.getBusinessUnit()));
		newPr.setCreatedBy(loggedInUser);
		newPr.setPrCreatedDate(new Date());

		newPr.setStatus(PrStatus.DRAFT);
		newPr.setBuyer(loggedInUser.getBuyer());

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newPr.getPrTeamMembers())) {
			List<PrTeamMember> tm = new ArrayList<PrTeamMember>();
			for (PrTeamMember teamMember : newPr.getPrTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setPr(newPr);
				tm.add(teamMember);
				sendAddTeamMemberEmailNotificationEmail(pr, teamMember.getUser(), teamMember.getTeamMemberType());
			}
		}
		// Pr Contacts
		if (CollectionUtil.isNotEmpty(newPr.getPrContacts())) {
			List<PrContact> contact = new ArrayList<PrContact>();
			for (PrContact cont : newPr.getPrContacts()) {
				cont.setPr(newPr);
				contact.add(cont);
			}
		}

		Pr dbPr = prDao.saveOrUpdate(newPr);

		// Pr Items
		if (CollectionUtil.isNotEmpty(newPr.getPrItems())) {
			PrItem parent = null;
			for (PrItem item : newPr.getPrItems()) {
				if (item.getOrder() != 0) {
					item.setParent(parent);
				}
				item.setPr(dbPr);
				item = prItemDao.saveOrUpdate(item);
				if (item.getOrder() == 0) {
					parent = item;
				}
			}
		}

		try {
			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.CREATE);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(messageSource.getMessage("pr.audit.create", new Object[] { dbPr.getPrId() }, Global.LOCALE));
			audit.setPr(dbPr);
			prAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
		}

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR '" + dbPr.getPrId() + "' Created", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return dbPr;
	}

	@Override
	public List<Pr> searchPrByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo) {
		List<Pr> prList = prDao.searchPrByNameAndRefNum(searchValue, tenantId, userId, pageNo);
		if (CollectionUtil.isNotEmpty(prList)) {
			for (Pr pr : prList) {
				pr.setModifiedBy(null);
				if (pr.getTemplate() != null) {
					if (pr.getTemplate().getStatus() == Status.INACTIVE) {

						pr.setTemplateActive(true);
					} else {
						pr.setTemplateActive(false);
					}

				}

			}
		}
		return prList;
	}

	@Override
	public List<Pr> findAllPrForTenant(String loggedInUserTenantId, String userId, TableDataInput input) {
		List<Pr> prList = prDao.findAllPrWithTemplateForTenant(loggedInUserTenantId, userId, input);

		LOG.info("size of Pr List " + prList.size());
		for (Pr pr : prList) {
			if (pr.getTemplate() != null) {
				if (pr.getTemplate().getStatus() == Status.INACTIVE) {

					pr.setTemplateActive(true);
				} else {
					pr.setTemplateActive(false);
				}

			}
			LOG.info("is Pr Template is Active " + pr.isTemplateActive());
		}
		return prList;
	}

	@Override
	@Transactional(readOnly = false)
	public Pr updatePrApproval(Pr pr, User loggedInUser) {
		Pr persistObj = prDao.findById(pr.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (PrApproval approvalRequest : persistObj.getPrApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (PrApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(pr.getPrApprovals())) {
			int level = 1;
			for (PrApproval app : pr.getPrApprovals()) {
				app.setPr(pr);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;

				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				LOG.info("app Type :" + app.getApprovalType());
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (PrApprovalUser prApprovalUser : app.getApprovalUsers()) {
						prApprovalUser.setApproval(app);
						// Deleting user id which is assigned to bind the object with spring form
						prApprovalUser.setId(null);

						// If db existing user list is empty, then it means this is a new level which is not existing in
						// database yet.
						// therefore we need to add all user list coming from frontend for current level as new users
						if (CollectionUtil.isEmpty(existingUserList)) {
							auditMessages.add("Approval Level " + app.getLevel() + " User " + prApprovalUser.getUser().getName() + " has been added as Approver");
						} else {
							// If db existing user list does not contain the user coming from frontend, then it has been
							// added as new user for current level
							if (!existingUserList.contains(prApprovalUser.getUser().getName())) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + prApprovalUser.getUser().getName() + " has been added as Approver");
							}
						}

						levelUsers.add(prApprovalUser.getUser().getName());
					}

					/*
					 * Loop through the db existing user list for the current level and check if they exist in the
					 * userlist coming from frontend.
					 */
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
				// to check if approval type is changed
				if (existingType != null && existingType != app.getApprovalType()) {
					auditMessages.add("Approval Level " + app.getLevel() + " Type changed from " + (existingType == ApprovalType.OR ? "Any" : "All") + " to " + (app.getApprovalType() == ApprovalType.OR ? "Any" : "All"));
				}
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		while (CollectionUtil.isNotEmpty(existingUsers.get(newLevel))) {
			for (String existing : existingUsers.get(newLevel)) {
				auditMessages.add("Approval Level " + newLevel + " User " + existing + " has been removed as Approver");
			}
			newLevel++;
		}

		persistObj.setPrApprovals(pr.getPrApprovals());
		persistObj.setActionBy(loggedInUser);
		persistObj.setActionDate(new Date());
		persistObj = prDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				PrAudit audit = new PrAudit(persistObj, loggedInUser, new Date(), PrAuditType.UPDATE, auditMessage);
				prAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for PR '"+persistObj.getPrId()+"' ."+  auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.PR);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		return persistObj;
	}

	@Override
	public EventPermissions getUserPemissionsForPr(String userId, String prId) {
		return prDao.getUserPemissionsForPr(userId, prId);
	}

	public JasperPrint getEvaluationSummaryPdf(Pr pr, String strTimeZone) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (pr.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (pr.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (pr.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (pr.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (pr.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (pr.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/PrSummary.jasper");
			File jasperfile = resource.getFile();
			// List<PrSummaryPojo> prSummary = new ArrayList<PrSummaryPojo>();

			PrSummaryPojo summary = new PrSummaryPojo();
			String createDate = pr.getPoCreatedDate() != null ? sdf.format(pr.getPoCreatedDate()).toUpperCase() : "";
			String deliveryDate = pr.getDeliveryDate() != null ? sdf.format(pr.getDeliveryDate()).toUpperCase() : "";

			summary.setPrName(pr.getName());
			summary.setRemarks(pr.getRemarks());
			summary.setPaymentTerm(pr.getPaymentTerm());
			summary.setTermsAndConditions(pr.getTermsAndConditions() != null ? pr.getTermsAndConditions().replaceAll("(?m)^[ \t]*\r?\n", "") : "");
			summary.setRequester(pr.getRequester());
			summary.setPoNumber(pr.getPoNumber());
			summary.setCreatedDate(createDate);

			BusinessUnit bUnit = pr.getBusinessUnit();
			Buyer buyer = pr.getBuyer();
			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				ImageIcon n;
				if (bUnit.getFileAttatchment() != null) {
					n = new ImageIcon(bUnit.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(bUnit.getDisplayName());
				}
				summary.setLogo(n.getImage());

				getSummaryOfAddressAndPritems(pr, df, summary, deliveryDate);
				if (StringUtils.checkString(bUnit.getLine1()).length() > 0) {
					buyerAddress = bUnit.getLine1() + "\r\n";
				} else {
					buyerAddress = " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine2()).length() > 0) {
					buyerAddress += bUnit.getLine2() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine3()).length() > 0) {
					buyerAddress += bUnit.getLine3() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine4()).length() > 0) {
					buyerAddress += bUnit.getLine4() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine5()).length() > 0) {
					buyerAddress += bUnit.getLine5() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine6()).length() > 0) {
					buyerAddress += bUnit.getLine6() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine7()).length() > 0) {
					buyerAddress += bUnit.getLine7() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}

				summary.setDisplayName(bUnit.getDisplayName());
			} else {
				buyerAddress += "\r\n" + buyer.getCity();
				if (buyer.getState() != null) {
					buyerAddress += ", " + buyer.getState().getStateName();
					buyerAddress += "\r\n" + buyer.getState().getCountry().getCountryName();
				}
				buyerAddress += "\r\n";
				buyerAddress += "TEL : " + buyer.getCompanyContactNumber();
				buyerAddress += " FAX : ";
				if (buyer.getFaxNumber() != null) {
					buyerAddress += buyer.getFaxNumber();
				}
				summary.setComanyName(buyer.getCompanyName());
				summary.setDisplayName(buyer.getCompanyName());
			}
			LOG.info("buyerAddress : " + buyerAddress);
			summary.setBuyerAddress(buyerAddress);

			List<PrSummaryPojo> prSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (pr.getSupplier() != null) {
				FavouriteSupplier supplier = pr.getSupplier();
				supplierAddress += supplier.getSupplier().getCompanyName() + "\r\n";
				supplierAddress += supplier.getSupplier().getLine1();
				if (StringUtils.checkString(pr.getSupplier().getSupplier().getLine2()).length() > 0) {
					supplierAddress += "\r\n" + supplier.getSupplier().getLine2();
				}
				supplierAddress += "\r\n" + supplier.getSupplier().getCity() + ", ";
				if (supplier.getSupplier().getState() != null) {
					supplierAddress += supplier.getSupplier().getState().getStateName() + "\r\n\n";
				}
				supplierAddress += "TEL : ";

				if (supplier.getSupplier().getCompanyContactNumber() != null) {
					supplierAddress += supplier.getSupplier().getCompanyContactNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (supplier.getSupplier().getFaxNumber() != null) {
					supplierAddress += supplier.getSupplier().getFaxNumber() + "\n\n";
				}
				supplierAddress += "Attention: " + supplier.getFullName() + "\nEmail: " + supplier.getCommunicationEmail() + "\n";
			} else {
				supplierAddress += pr.getSupplierName() + "\r\n";
				supplierAddress += pr.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (pr.getSupplierTelNumber() != null) {
					supplierAddress += pr.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (pr.getSupplierFaxNumber() != null) {
					supplierAddress += pr.getSupplierFaxNumber();
				}
			}
			if (pr.getSupplier() != null) {
				summary.setSupplierName(pr.getSupplier().getSupplier() != null ? pr.getSupplier().getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(pr.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(pr.getSupplierTaxNumber() != null ? pr.getSupplierTaxNumber() : "");

			parameters.put("PR_SUMMARY", prSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(prSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (

		Exception e) {
			LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	/**
	 * @param pr
	 * @return
	 */
	private String prSupplierAddress(Pr pr) {
		String supplierAddress = "";
		if (pr.getSupplier() != null) {
			FavouriteSupplier supplier = pr.getSupplier();
			// supplierAddress += supplier.getSupplier().getCompanyName();
			supplierAddress += supplier.getSupplier().getLine1();
			if (pr.getSupplier().getSupplier().getLine2() != null) {
				supplierAddress += ", " + supplier.getSupplier().getLine2();
			}

			supplierAddress += "\r\n" + supplier.getSupplier().getCity() + ", ";

			if (supplier.getSupplier().getState() != null) {
				supplierAddress += supplier.getSupplier().getState().getStateName() + "\r\n\n";
			}
			supplierAddress += "TEL : ";
			if (supplier.getSupplier().getCompanyContactNumber() != null) {
				supplierAddress += supplier.getSupplier().getCompanyContactNumber();
			}
			supplierAddress += "\r\nFAX : ";
			if (supplier.getSupplier().getFaxNumber() != null) {
				supplierAddress += supplier.getSupplier().getFaxNumber() + "\n\n";
			}
			supplierAddress += "Attention: " + supplier.getFullName() + "\nEmail: " + supplier.getCommunicationEmail() + "\n";
		} else {
			// supplierAddress += pr.getSupplierName() + "\r\n";
			supplierAddress += pr.getSupplierAddress() + "\r\n\n";
			supplierAddress += "TEL :";
			if (pr.getSupplierTelNumber() != null) {
				supplierAddress += pr.getSupplierTelNumber();
			}
			supplierAddress += "\r\nFAX : ";
			if (pr.getSupplierFaxNumber() != null) {
				supplierAddress += pr.getSupplierFaxNumber();
			}
		}
		return supplierAddress;
	}

	@Override
	public List<PrComment> findAllPrCommentsByPrId(String prId) {
		return prDao.findAllPrCommentsByPrId(prId);
	}

	@Override
	public JasperPrint getPrSummaryPdf(Pr pr, String strTimeZone) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		// String imgPath = context.getRealPath("resources/images");
		DecimalFormat df = null;
		if (pr.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (pr.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (pr.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (pr.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (pr.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (pr.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/GeneratePrSummaryReport.jasper");
			File jasperfile = resource.getFile();

			String prId = pr.getPrId();
			LOG.info("PR ID >>>>>>>>>>>> "+prId);
			PrSummaryPojo summary = new PrSummaryPojo();

			String createDate = pr.getPrCreatedDate() != null ? sdf.format(pr.getPrCreatedDate()).toUpperCase() : "";
			String deliveryDate = pr.getDeliveryDate() != null ? sdf.format(pr.getDeliveryDate()).toUpperCase() : "";
			summary.setBaseCurrency(pr.getCurrency().getCurrencyCode());
			summary.setPrNo(pr.getPrId());
			summary.setDecimal(pr.getDecimal());
			summary.setPrDescription(pr.getDescription());
			summary.setRemarks(pr.getRemarks());
			summary.setPaymentTerm(pr.getPaymentTerm());
			summary.setPaymentTermDays(pr.getPaymentTermDays());
			summary.setTermsAndConditions(pr.getTermsAndConditions());
			summary.setRequester(pr.getRequester());
			summary.setPrRefnumber(pr.getReferenceNumber() != null ? pr.getReferenceNumber() : "");
			summary.setPrName(pr.getName());
			summary.setCreatedDate(createDate);
			summary.setReceiver(pr.getDeliveryReceiver());
			summary.setComanyName(pr.getBuyer().getCompanyName());
			summary.setCostCenter((pr.getCostCenter() != null ? pr.getCostCenter().getCostCenter() : "") + " - " + ((pr.getCostCenter() != null && pr.getCostCenter().getDescription() != null) ? pr.getCostCenter().getDescription() : ""));
			summary.setBusinesUnit(pr.getBusinessUnit() != null ? pr.getBusinessUnit().getDisplayName() : "");

			summary.setAvailableBudget(pr.getDecimal() != null ? pr.getAvailableBudget() != null ?
					pr.getAvailableBudget().setScale(Integer.parseInt(pr.getDecimal()), RoundingMode.HALF_UP) : null : pr.getAvailableBudget());

			// get the visible
			if(pr.getTemplate() != null) {
				PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(),
						SecurityLibrary.getLoggedInUserTenantId());

				for (PrTemplateField prTemplateField : prTemplate.getFields()) {
					if (prTemplateField.getFieldName().equals(PrTemplateFieldName.AVAILABLE_BUDGET)) {
						summary.setAvailableBudgetVisible(prTemplateField.getVisible());
						break;
					}
				}
			}

			if (df.format(pr.getTotal()) != null) {
				summary.setTotalAmount(pr.getCurrency().getCurrencyCode() + " " + df.format(pr.getGrandTotal()));
			}
			summary.setDeliveryDate(deliveryDate);

			summary.setEnableApprovalReminder(pr.getEnableApprovalReminder());
			if (Boolean.TRUE == pr.getEnableApprovalReminder()) {
				summary.setReminderAfterHour(pr.getReminderAfterHour());
				summary.setReminderCount(pr.getReminderCount());
			}
			summary.setNotifyEventOwner(pr.getNotifyEventOwner());
			if (pr.getSupplier() != null) {
				summary.setSupplierName(pr.getSupplier().getSupplier() != null ? pr.getSupplier().getSupplier().getCompanyName() : pr.getSupplierName());
			} else {
				summary.setSupplierName(pr.getSupplierName());

			}
			summary.setSupplierTaxNumber(StringUtils.checkString(pr.getSupplierTaxNumber()).length() > 0 ? pr.getSupplierTaxNumber() : "");

			String owner = "";
			/*
			 * owner += pr.getBuyer().getFullName() + "\n" + pr.getBuyer().getCommunicationEmail() + "\n"; owner +=
			 * "Tel:" + pr.getBuyer().getCompanyContactNumber() + "  Fax: "; if (pr.getBuyer().getFaxNumber() != null) {
			 * owner += pr.getBuyer().getFaxNumber(); }
			 */
			try {
				if (pr.getCreatedBy() != null) {
					owner += pr.getCreatedBy().getName() + "\n" + pr.getCreatedBy().getCommunicationEmail() + "\n";
					owner += "Tel:";
					if (pr.getCreatedBy().getPhoneNumber() != null) {
						owner += pr.getCreatedBy().getPhoneNumber();
					}
					summary.setOwner(owner);
				}
			} catch (Exception e) {
				Log.error("Unable to get the PR Owner Details.");
			}

			// Supplier Address
			Pr prSupplier = findPrSupplierByPrId(pr.getId());
			String supplierContact = "";
			if (prSupplier != null) {
				if (CollectionUtil.isNotEmpty(prSupplier.getPrContacts())) {
					for (PrContact item : prSupplier.getPrContacts()) {
						if (StringUtils.checkString(item.getTitle()).length() > 0) {
							supplierContact += item.getTitle() + "\n";
						}
						if (StringUtils.checkString(item.getContactName()).length() > 0) {
							supplierContact += item.getContactName() + "\n";
						}
						if (StringUtils.checkString(item.getDesignation()).length() > 0) {
							supplierContact += "  (" + item.getDesignation() + ")\n";
						}
						if (StringUtils.checkString(item.getContactNumber()).length() > 0) {
							supplierContact += "TEL: " + item.getContactNumber();
						}
						if (StringUtils.checkString(item.getMobileNumber()).length() > 0) {
							supplierContact += "   HP: " + item.getMobileNumber();
						}
						if (StringUtils.checkString(item.getFaxNumber()).length() > 0) {
							supplierContact += "   FAX: " + item.getFaxNumber();
						}
						if (StringUtils.checkString(item.getComunicationEmail()).length() > 0) {
							supplierContact += " EMAIL:" + item.getComunicationEmail();
						}

						// + "TEL: " + item.getContactNumber() + " HP: " + item.getMobileNumber() != null ?
						// item.getMobileNumber() : "" + " FAX: ";
						// if (item.getFaxNumber() != null) {
						// supplierContact += item.getFaxNumber() + "\nEMAIL:" + item.getComunicationEmail();
						// } else {
						// supplierContact += "EMAIL:" + item.getComunicationEmail();
						// }
						Arrays.asList(supplierContact);
					}
				} else {
					supplierContact = "N/A";
				}
			}
			summary.setSupplierContact(supplierContact);

			// Supplier Address

			String supplierAddress = prSupplierAddress(pr);
			summary.setSupplierAddress(supplierAddress);
			// Buyer Address
			String buyerAddress = "";

			Buyer buyer = pr.getBuyer();
			buyerAddress += buyer.getCompanyName();
			buyerAddress += "\r\n" + buyer.getLine1();
			if (buyer.getLine2() != null) {
				buyerAddress += ", " + buyer.getLine2();
			}

			//add ternary to check NPE
			String city = buyer.getCity()!= null ? buyer.getCity():"";
			String state = buyer.getState() != null?buyer.getState().getStateName():"";
			String country = buyer.getState() != null?buyer.getState().getCountry().getCountryName()+ "\n\n":"";
			String companyContactNumber = buyer.getCompanyContactNumber() != null ?"\nTEL : \n"+ buyer.getCompanyContactNumber() +"\n": "";
			String faxNumber = buyer.getFaxNumber() != null?"\nFAX : "+buyer.getFaxNumber():"";

			buyerAddress += "\n" + city + ", " + state + "\n";
			buyerAddress += country ;
			buyerAddress += companyContactNumber;
			buyerAddress += faxNumber;

			summary.setBuyerAddress(buyerAddress);

			getSummaryOfAddressAndPritems(pr, df, summary, deliveryDate);

			// PR Documents
			List<PrDocument> document = findAllPlainPrDocsbyPrId(pr.getId());
			List<EvaluationDocumentPojo> documentList = new ArrayList<EvaluationDocumentPojo>();
			if (CollectionUtil.isNotEmpty(document)) {
				for (PrDocument item : document) {
					EvaluationDocumentPojo docs = new EvaluationDocumentPojo();
					docs.setFileName(item.getFileName());
					docs.setDescription(item.getDescription());
					docs.setUploadDate(item.getUploadDate());
					docs.setSize((double) (item.getFileSizeInKb()));
					documentList.add(docs);
				}
			}
			summary.setDocuments(documentList);

			// PR Approvals
			List<EvaluationAprovalUsersPojo> approvalList = new ArrayList<EvaluationAprovalUsersPojo>();
			List<PrApproval> approvals = pr.getPrApprovals();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (PrApproval item : approvals) {
					EvaluationAprovalUsersPojo approverRoute = new EvaluationAprovalUsersPojo();
					approverRoute.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> usrRoute = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 0;
						for (PrApprovalUser usr : item.getApprovalUsers()) {
							index++;
							Integer cnt = item.getApprovalUsers().size();
							EvaluationAprovalUsersPojo user = new EvaluationAprovalUsersPojo();
							userName += "\t" + usr.getUser().getName() + "\t";
							if (cnt > index) {
								userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "" + "\t";
							}
							user.setName(userName);
							usrRoute.add(user);
						}
					}
					approverRoute.setName(userName);
					approvalList.add(approverRoute);
				}
			}
			summary.setApprovals(approvalList);

			// PR Approval Comments
			List<EvaluationAprovalUsersPojo> ApprovalCommentsList = new ArrayList<EvaluationAprovalUsersPojo>();
			List<PrComment> prCommentList = findAllPrCommentsByPrId(pr.getId());
			if (CollectionUtil.isNotEmpty(prCommentList)) {
				for (PrComment comment : prCommentList) {
					EvaluationAprovalUsersPojo approvComments = new EvaluationAprovalUsersPojo();
					approvComments.setName(comment.getCreatedBy().getName());
					approvComments.setCommentDate(sdf.format(comment.getCreatedDate()));
					approvComments.setComments(comment.getComment());
					ApprovalCommentsList.add(approvComments);
				}
			}
			summary.setApprovalComments(ApprovalCommentsList);

			List<PrSummaryPojo> prSummary = Arrays.asList(summary);

			parameters.put("PR_SUMMARY", prSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(prSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	/**
	 * @param pr
	 * @param df
	 * @param summary
	 * @param deliveryDate
	 */
	private void getSummaryOfAddressAndPritems(Pr pr, DecimalFormat df, PrSummaryPojo summary, String deliveryDate) {

		try {

			// Delivery Address

			String deliveryAddress = "";
			if (pr.getDeliveryAddress() != null) {
				deliveryAddress += pr.getDeliveryAddress().getTitle() + "\n";
				deliveryAddress += pr.getDeliveryAddress().getLine1();
				if (pr.getDeliveryAddress().getLine2() != null) {
					deliveryAddress += "\n" + pr.getDeliveryAddress().getLine2();
				}
				deliveryAddress += "\n" + pr.getDeliveryAddress().getZip() + ", " + pr.getDeliveryAddress().getCity() + "\n";
				deliveryAddress += pr.getDeliveryAddress().getState().getStateName() + ", " + pr.getDeliveryAddress().getState().getCountry().getCountryName();
			}

			summary.setDeliveryAddress(deliveryAddress);
			summary.setDeliveryReceiver(pr.getDeliveryReceiver());
			summary.setDeliveryDate(deliveryDate);

			// Correspondence Address
			if (pr.getCorrespondenceAddress() != null) {

				String correspondAddress = "";
				correspondAddress += pr.getCorrespondenceAddress().getTitle();
				correspondAddress += "\r\n" + pr.getCorrespondenceAddress().getLine1();
				if (pr.getCorrespondenceAddress().getLine2() != null) {
					correspondAddress += ", " + pr.getCorrespondenceAddress().getLine2();
				}
				correspondAddress += "\r\n" + pr.getCorrespondenceAddress().getZip() + ", " + pr.getCorrespondenceAddress().getCity();
				correspondAddress += "\r\n" + pr.getCorrespondenceAddress().getState().getStateName() + ", " + pr.getCorrespondenceAddress().getState().getCountry().getCountryName();
				summary.setCorrespondAddress(correspondAddress);
			}
			// Pr items List
			List<PrItemsSummaryPojo> prItemList = new ArrayList<PrItemsSummaryPojo>();
			List<PrItem> prList = findAllPrItemByPrId(pr.getId());
			if (CollectionUtil.isNotEmpty(prList)) {
				for (PrItem item : prList) {
					PrItemsSummaryPojo prs = new PrItemsSummaryPojo();
					prs.setSlno(item.getLevel() + "." + item.getOrder());
					prs.setItemName(item.getItemName());
					prs.setCurrency(item.getPr().getCurrency().getCurrencyCode());
					prs.setItemDescription(item.getItemDescription());
					prs.setAdditionalTax(df.format(item.getPr().getAdditionalTax()));
					prs.setGrandTotal(df.format(item.getPr().getGrandTotal()));
					prs.setSumAmount(df.format(pr.getTotal()));
					prs.setTaxDescription(item.getPr().getTaxDescription());
					prs.setDecimal(pr.getDecimal());
					prItemList.add(prs);
					if (item.getChildren() != null) {
						for (PrItem childItem : item.getChildren()) {
							PrItemsSummaryPojo childPr = new PrItemsSummaryPojo();
							childPr.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPr.setItemName(childItem.getItemName());
							childPr.setItemDescription(childItem.getItemDescription());
							childPr.setQuantity(df.format(childItem.getQuantity()));
							childPr.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPr.setPricePerUnit(df.format(childItem.getPricePerUnit()));
							childPr.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPr.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPr.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPr.setUom(childItem.getProduct() != null ? (childItem.getProduct().getUom() != null ? childItem.getProduct().getUom().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPr.setCurrency(childItem.getPr().getCurrency().getCurrencyCode());
							childPr.setAdditionalTax(df.format(childItem.getPr().getAdditionalTax()));
							childPr.setGrandTotal(df.format(childItem.getPr().getGrandTotal()));
							childPr.setSumAmount(df.format(pr.getTotal()));
							childPr.setTaxDescription(childItem.getPr().getTaxDescription());
							childPr.setSumTaxAmount(childItem.getTaxAmount());
							childPr.setSumTotalAmt(childItem.getTotalAmount());
							childPr.setDecimal(pr.getDecimal());
							prItemList.add(childPr);
						}
					}

				}
			}
			summary.setPrItems(prItemList);
		} catch (Exception e) {
			LOG.error("Could not Get PR Items and Address " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String prId) {
		PrDocument prDocument = findPrDocById(docId);
		prDocument.setDescription(docDesc);
		Pr pr = getPrById(prId);
		prDocument.setPr(pr);
		savePrDocument(prDocument);
	}

	@Override
	@Transactional(readOnly = false)
	public void sendPrFinishMail(Pr pr) {
		try {
			LOG.info("sendPrFinishMail called");
			String mailTo = pr.getCreatedBy().getCommunicationEmail();
			String subject = "PR Finished";
			String url = APP_URL + "/buyer/prView/" + pr.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", pr.getCreatedBy().getName());
			map.put("message", "You have Finished");
			map.put("pr", pr);
			map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
			map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(pr.getCreatedBy().getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if (StringUtils.checkString(mailTo).length() > 0 && pr.getCreatedBy().getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.PR_FINISH_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + pr.getCreatedBy().getLoginId() + "... Not going to send email notification");
			}

			String notificationMessage = messageSource.getMessage("pr.finish.notification.message", new Object[] { pr.getName() }, Global.LOCALE);
			sendDashboardNotification(pr.getCreatedBy(), url, subject, notificationMessage, pr.getCreatedBy());
		} catch (Exception e) {
			LOG.error("Error while sending pr finish mail :" + e.getMessage(), e);
		}
	}

	private String getBusinessUnitname(String prId) {
		String displayName = null;
		displayName = prDao.getBusineessUnitname(prId);
		return StringUtils.checkString(displayName);
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

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				notificationService.sendEmail(mailTo, subject, message);
			} catch (Exception e) {
				LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, User user) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(user);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.FINISH_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	public Pr findPrById(String prId) {
		return prDao.findByPrId(prId);
	}

	@Override
	public List<PrDocument> findAllPlainPrDocsbyPrId(String prId) {
		return prDocumentDao.findAllPlainPrDocsbyPrId(prId);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForPr(String prId) {
		return prDao.getPlainTeamMembersForPr(prId);
	}

	@Override
	public List<PrApprovalUser> fetchAllApprovalUsersByPrId(String id) {
		return prDao.fetchAllApprovalUsersByPrId(id);
	}

	@Override
	public boolean checkTemplateStatusForPr(String prId) {
		return prDao.checkTemplateStatusForPr(prId);
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public MobilePrPojo getMobilePrDetails(String id) {
		Pr pr = prDao.getMobilePrDetails(id);
		pr.setPrDocuments(prDocumentDao.findAllPrDocsNameAndId(id));

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(pr.getCreatedBy().getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobilePrPojo mobilePrPojo = new MobilePrPojo(pr);

		if (CollectionUtil.isNotEmpty(mobilePrPojo.getComments())) {
			for (Comments comm : mobilePrPojo.getComments()) {
				if (comm.getCreatedDate() != null)
					comm.setCreatedDateApk(df.format(comm.getCreatedDate()));
				else
					comm.setCreatedDateApk("N/A");
			}
		}

		if (CollectionUtil.isNotEmpty(pr.getPrApprovals())) {
			List<PrApproval> approvalList = new ArrayList<>();
			for (PrApproval prApproval : pr.getPrApprovals()) {
				mobilePrPojo.setApprovers(new ArrayList<>());
				if (CollectionUtil.isNotEmpty(prApproval.getApprovalUsers())) {
					for (ApprovalUser approvalUser : prApproval.getApprovalUsers()) {

						if (approvalUser.getActionDate() != null)
							approvalUser.setActionDateApk(df.format(approvalUser.getActionDate()));
						else
							approvalUser.setActionDateApk("N/A");

						prApproval.addUsers(approvalUser.createMobileShallowCopy());
					}
				}
				approvalList.add(prApproval);
			}
			mobilePrPojo.setApprovers(approvalList);
		}
		if (pr.getTemplate() != null) {
			mobilePrPojo.setTemplateName(pr.getTemplate().getTemplateName());
		}
		if (pr.getBusinessUnit() != null) {
			mobilePrPojo.setBusinessUnit(pr.getBusinessUnit().getUnitName());
		} else {
			mobilePrPojo.setBusinessUnit("N/A");
		}
		if (pr.getCostCenter() != null) {
			mobilePrPojo.setCostCenter(pr.getCostCenter().getCostCenter());
		} else {
			mobilePrPojo.setCostCenter("N/A");
		}
		return mobilePrPojo;
	}

	@Override
	public long findPrPoCountForTenant(String tenantId, PrStatus status, RequestParamPojo filter) throws Exception {
		return prDao.findPrPoCountForTenant(tenantId, status, filter);
	}

	@Override
	public BigDecimal findPrPoValueForTenant(String tenantId, PrStatus approved, RequestParamPojo filter) throws Exception {
		return prDao.findPrPoValueForTenant(tenantId, approved, filter);
	}

	@Override
	public void downloadPrDocument(String docId, HttpServletResponse response) throws Exception {
		PrDocument docs = findPrDocById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadPrSummary(String id, HttpServletResponse response, HttpSession session) {
		try {

			Pr pr = findPrById(id);
			String prFilename = (pr.getPrId()).replace("/", "-") + ".pdf";
			String filename = prFilename;

			JasperPrint jasperPrint = getPrSummaryPdf(pr, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
		}

	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@Override
	@Transactional(readOnly = false)
	public PoDocument savePoDocument(PoDocument poDocument) {
		return poDocumentDao.saveOrUpdate(poDocument);
	}

	@Override
	public List<PoDocument> findAllPlainPoDocsbyPrId(String prId) {
		return poDocumentDao.findAllPlainPoDocsbyPrId(prId);
	}

	@Override
	public PoDocument findPoDocById(String docId) {
		return poDocumentDao.findById(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removePoDocument(PoDocument prDocument) {
		poDocumentDao.delete(prDocument);
	}

	@Override
	public void downloadPoDocument(String docId, HttpServletResponse response) throws Exception {
		PoDocument docs = findPoDocById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePoDocumentDesc(String docId, String docDesc, String prId, String docType) {
		PoDocument poDocument = findPoDocById(docId);
		poDocument.setDescription(docDesc);
		if (StringUtils.checkString(docType).length() > 0) {
			poDocument.setDocType(PoDocumentType.valueOf(docType));
		} else {
			poDocument.setDocType(PoDocumentType.OTHER);
		}
		Pr pr = new Pr();
		pr.setId(prId);
		poDocument.setPr(pr);
		poDocumentDao.saveOrUpdate(poDocument);
	}

	@Override
	@Transactional(readOnly = false)
	public void downloadPoReports(String tenantId, String[] prArr, HttpServletResponse response, HttpSession session, boolean select_all, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo) {
		LOG.info("Download PO reports... " + response.getHeaderNames());
		try {
			List<Pr> prList = findSearchPoByIds(tenantId, prArr, select_all, startDate, endDate, searchFilterPrPojo);
			LOG.info("prList>>>>>>>>" + prList.size());
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "poReports.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PO Report");

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();

			// font.setColor(HSSFColor.WHITE.index);

			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

			// Aqua background
			// styleHeading.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			// styleHeading.setFillPattern(CellStyle.BIG_SPOTS);

			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;
			for (String column : Global.PO_REPORT_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);

				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}

			/*
			 * CellStyle style = wb.createCellStyle(); style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
			 * style.setFillPattern(CellStyle.BIG_SPOTS); row.setRowStyle(style);
			 */

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int r = 1;
			// Write Data into Excel
			for (Pr po : prList) {
				// For Financial Standard
				DecimalFormat df = null;
				if (po.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (po.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (po.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (po.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (po.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (po.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(r - 1);
				row.createCell(cellNum++).setCellValue(po.getReferenceNumber() != null ? po.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(po.getName() != null ? po.getName() : "");
				// row.createCell(cellNum++).setCellValue((po.getSupplier() != null && po.getSupplier().getFullName() !=
				// null) ? po.getSupplier().getFullName() : (po.getSupplierName() != null ? po.getSupplierName() : ""));
				// row.createCell(cellNum++).setCellValue(po.getSupplier() != null ?
				// (po.getSupplier().getSupplier()!=null?po.getSupplier().getSupplier().getCompanyName():"" ):"");
				row.createCell(cellNum++).setCellValue((po.getSupplier() != null && po.getSupplier().getFullName() != null) ? po.getSupplier().getSupplier().getCompanyName() : (po.getSupplierName() != null ? po.getSupplierName() : ""));
				row.createCell(cellNum++).setCellValue(po.getDescription() != null ? po.getDescription() : "");
				row.createCell(cellNum++).setCellValue(po.getPoNumber() != null ? po.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(po.getCreatedBy() != null ? po.getCreatedBy().getName() : "");
				row.createCell(cellNum++).setCellValue(po.getPoCreatedDate() != null ? sdf.format(po.getPoCreatedDate()) : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				// CellStyle grandTotalCellStyle = grandTotalCell.getCellStyle();
				// grandTotalCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellValue(po.getGrandTotal() != null ? df.format(po.getGrandTotal()) : "");
				row.createCell(cellNum++).setCellValue(po.getBusinessUnit() != null ? po.getBusinessUnit().getUnitName() : "");
				row.createCell(cellNum++).setCellValue(po.getStatus() != null ? po.getStatus().toString() : "");
			}
			// Auto Fit
			for (int k = 0; k < 15; k++) {
				sheet.autoSizeColumn(k, true);
			}

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					// response.getOutputStream().flush();
					// FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					LOG.info("Successfully written in Excel===========================");
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
			LOG.info("updating Po Report Sent");
			prDao.updateSentPoReport(prArr, select_all, tenantId, startDate, endDate, searchFilterPrPojo);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Reports Excel : " + e.getMessage(), e);
		}

	}

	private List<Pr> findSearchPoByIds(String tenantId, String[] prArr, boolean select_all, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo) {
		return prDao.findSearchPoByIds(tenantId, prArr, select_all, startDate, endDate, searchFilterPrPojo);
	}

	@Override
	public List<Pr> findPrByIds(String[] prArr) {
		return prDao.findPrByIds(prArr);
	}

	@Override
	public List<Pr> findAllSearchFilterPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses) {
		return prDao.findAllSearchFilterPo(userId, tenantId, input, startDate, endDate, statuses);
	}

	@Override
	public long findTotalSearchFilterPo(String userId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses) {
		return prDao.findTotalSearchFilterPo(userId, loggedInUserTenantId, input, startDate, endDate, statuses);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updatePoStatus(Pr pr, PrAudit audit) {
		prDao.saveOrUpdate(pr);
		LOG.info("Pr Audit :" + audit.toLogString());
		prAuditService.save(audit);
	}

	@Override
	public Pr getPrForErpById(String prId) {
		Pr pr = prDao.getPrForErpById(prId);
		if (pr != null) {
			if (CollectionUtil.isNotEmpty(pr.getPrItems())) {
				for (PrItem item : pr.getPrItems()) {
					if (item.getProduct() != null) {
						item.getProduct().getProductCode();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(pr.getPrComments())) {
				for (PrComment comment : pr.getPrComments()) {
					comment.getComment();
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getName();
					}
				}
			}
		}
		return pr;
	}

	@Override
	@Transactional(readOnly = false)
	public Pr updatePrResponse(PrResponseErpPojo prResponseErpPojo, ErpSetup erpSetup) {
		Buyer buyer = buyerService.findBuyerById(erpSetup.getTenantId());
		User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
		Pr pr = prDao.findPrBySystemGeneratedPrIdAndTenantId(prResponseErpPojo.getId(), buyer.getId());

		if (pr != null) {
			LOG.info("ERP Doc No. :" + prResponseErpPojo.getPrReqNo());
			LOG.info("Status :" + prResponseErpPojo.getStatus());
			LOG.info("Error Msg :" + prResponseErpPojo.getMessage());
			LOG.info("Response Type :" + prResponseErpPojo.getType());
			LOG.info("Header Note :" + prResponseErpPojo.getHeaderNote());
			pr.setErpDocNo(prResponseErpPojo.getPrReqNo());
			pr.setErpStatus(prResponseErpPojo.getStatus());
			pr.setErpMessage(prResponseErpPojo.getMessage());
			PrAudit audit = new PrAudit();
			if ("SUCCESS".equals(pr.getErpStatus())) {
				pr.setStatus(PrStatus.APPROVED);
				audit.setAction(PrAuditType.APPROVED);
				try {
					audit.setActionBy(adminUser);
					audit.setActionDate(new Date());
					audit.setBuyer(buyer);
					audit.setDescription("ERP >> " + pr.getErpMessage());
					audit.setPr(pr);
					prAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while audit PR history :" + e.getMessage(), e);
				}

				Boolean isAutoCreatePo = buyerSettingsService.isAutoCreatePoSettingsByTenantId(buyer.getId());

				if (Boolean.TRUE == erpSetup.getIsGeneratePo() && Boolean.TRUE == isAutoCreatePo) {
					// generating po number

					try {
						String poNumber = generatePo(pr);
						pr.setPoNumber(poNumber);
						pr.setPoCreatedDate(new Date());
						pr.setIsPo(true);

						Po po = createPo(pr.getCreatedBy(), pr);
						sendPoCreatedEmail(pr.getCreatedBy(), pr);
						try {
							if (po.getStatus() == PoStatus.ORDERED) {
								po = poService.getLoadedPoById(po.getId());
								if (po.getSupplier() != null) {
									approvalService.sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());
								}
							}
						} catch (Exception e) {
							LOG.error("Error while sending PO email notification to supplier:" + e.getMessage(), e);
						}

					} catch (ApplicationException e1) {
						LOG.error("Error while creating PO  " + e1.getMessage(), e1);
					}

				}
			} else {
				pr.setStatus(PrStatus.DRAFT);
				pr.setErpPrTransferred(Boolean.FALSE);
				audit.setAction(PrAuditType.REJECTED);
				LOG.info("Rejection mail to pr Creator :" + pr.getErpDocNo());

				if (pr.getPrApprovals() != null) {
					List<PrApproval> approvalList = pr.getPrApprovals();
					if (CollectionUtil.isNotEmpty(approvalList)) {
						for (PrApproval prApproval : approvalList) {
							prApproval.setDone(false);
							prApproval.setActive(false);
							for (PrApprovalUser user : prApproval.getApprovalUsers()) {
								try {
									// Send rejection email to all approver users
									// if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
									sendPrRejectionEmail(user.getUser(), pr, prResponseErpPojo.getMessage());
									// }
								} catch (Exception e) {
									LOG.info("ERROR while Sending PR reject mail :" + e.getMessage(), e);
								}
								user.setActionDate(null);
								user.setApprovalStatus(ApprovalStatus.PENDING);
								user.setRemarks(null);
								user.setActionDate(null);
							}
						}

					}
				}
				try {
					if (pr.getCreatedBy() != null) {
						sendPrRejectionEmail(pr.getCreatedBy(), pr, prResponseErpPojo.getMessage());
					}
				} catch (Exception e) {
					LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
				}
				// actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
				try {
					audit.setActionBy(adminUser);
					audit.setActionDate(new Date());
					audit.setBuyer(buyer);
					audit.setDescription("ERP >> " + pr.getErpMessage());
					audit.setPr(pr);
					prAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while audit PR history :" + e.getMessage(), e);
				}

			}
			pr.setIsFinalApproved(Boolean.FALSE);
			pr = updatePr(pr);
			return pr;
		} else {
			LOG.info("Need attention something is wrong to update the pr which is null for pr id :" + prResponseErpPojo.getId());
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Pr updatePoResponse(List<PrResponseErpPojo> poResponseErpList) {
		if (CollectionUtil.isNotEmpty(poResponseErpList)) {
			for (PrResponseErpPojo poResponseErp : poResponseErpList) {
				Pr pr = prDao.findPrByErpDocNo(poResponseErp.getPrReqNo());
				if (pr != null) {
					pr.setStatus(PrStatus.COMPLETE);
					LOG.info("status :" + pr.getStatus());
					// generating po number
					try {
						String poNumber = generatePo(pr);
						pr.setPoNumber(poNumber);
						pr.setPoCreatedDate(new Date());
						pr.setIsPo(true);
						sendPoCreatedEmail(pr.getCreatedBy(), pr);
						pr = updatePr(pr);
					} catch (ApplicationException e) {
						LOG.info("Error generating PO No for PR: " + pr.getPrId() + " Error: " + e.getMessage(), e);
					}
				} else {
					LOG.info("PR is NUll for Erp Doc No :" + poResponseErp.getPrReqNo());
				}
			}
		} else {
			LOG.info("PO Response list is Empty");
		}
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void createPo(ErpPoPojo poPojo, String buyerId) throws NoSuchMessageException, ApplicationException {
		if (poPojo != null) {
			Pr pr = prDao.findPrByErpDocNo(poPojo.getPoNumber());
			if (pr != null) {

				User user = userService.getUserByLoginIdNoTouch(pr.getBuyer().getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				pr.setStatus(PrStatus.APPROVED);
				LOG.info("status :" + pr.getStatus());
				// generating po number
				String poNumber = generatePo(pr);
				pr.setPoNumber(poNumber);
				pr.setPoCreatedDate(new Date());
				pr.setIsPo(true);
				sendPoCreatedEmail(pr.getCreatedBy(), pr);
				pr = updatePr(pr);

				// Create PO data will come for ERP AGENT
				Po po = poDao.findByPoNomber(poPojo.getPoNumber(), buyerId);
				if (po == null) {
					po = new Po();
				}
				po.setPoNumber(poPojo.getPoNumber());
				po.setBuyer(pr.getBuyer());
				po.setBusinessUnit(pr.getBusinessUnit());

				po.setLine1(pr.getBusinessUnit().getLine1());
				po.setLine2(pr.getBusinessUnit().getLine2());
				po.setLine3(pr.getBusinessUnit().getLine3());
				po.setLine4(pr.getBusinessUnit().getLine4());
				po.setLine5(pr.getBusinessUnit().getLine5());
				po.setLine6(pr.getBusinessUnit().getLine6());
				po.setLine7(pr.getBusinessUnit().getLine7());

				po.setCorrespondenceAddress(pr.getCorrespondenceAddress());
				if (pr.getCorrespondenceAddress() != null) {
					po.setCorrespondAddressTitle(StringUtils.checkString(pr.getCorrespondenceAddress().getTitle()));
					po.setCorrespondAddressLine1(StringUtils.checkString(pr.getCorrespondenceAddress().getLine1()));
					po.setCorrespondAddressLine2(StringUtils.checkString(pr.getCorrespondenceAddress().getLine2()));
					po.setCorrespondAddressZip(StringUtils.checkString(pr.getCorrespondenceAddress().getZip()));
					po.setCorrespondAddressCity(StringUtils.checkString(pr.getCorrespondenceAddress().getCity()));
					po.setCorrespondAddressState(StringUtils.checkString(pr.getCorrespondenceAddress().getState().getStateName()));
					po.setCorrespondAddressCountry(StringUtils.checkString(pr.getCorrespondenceAddress().getState().getCountry().getCountryName()));
				}
				if (pr.getDeliveryAddress() != null) {
					po.setDeliveryAddressTitle(StringUtils.checkString(pr.getDeliveryAddress().getTitle()));
					po.setDeliveryAddressLine1(StringUtils.checkString(pr.getDeliveryAddress().getLine1()));
					po.setDeliveryAddressLine2(StringUtils.checkString(pr.getDeliveryAddress().getLine2()));
					po.setDeliveryAddressZip(StringUtils.checkString(pr.getDeliveryAddress().getZip()));
					po.setDeliveryAddressCity(StringUtils.checkString(pr.getDeliveryAddress().getCity()));
					po.setDeliveryAddressState(StringUtils.checkString(pr.getDeliveryAddress().getState().getStateName()));
					po.setDeliveryAddressCountry(StringUtils.checkString(pr.getDeliveryAddress().getState().getCountry().getCountryName()));
				}
				po.setCostCenter(pr.getCostCenter());
				po.setCreatedBy(pr.getCreatedBy());
				po.setCreatedDate(new Date());
				po.setCurrency(pr.getCurrency());
				po.setDecimal(pr.getDecimal());
				po.setDeliveryAddress(pr.getDeliveryAddress());
				po.setDeliveryDate(pr.getDeliveryDate());
				po.setDeliveryReceiver(pr.getDeliveryReceiver());
				po.setDescription(pr.getDescription());
				po.setErpPrTransferred(Boolean.FALSE);
				po.setGrandTotal(pr.getGrandTotal());
				po.setIsPoReportSent(Boolean.FALSE);
				po.setName(pr.getName());
				if (pr.getPaymentTermes() != null) {
					po.setPaymentTerm(pr.getPaymentTerm());
					po.setPaymentTermDays(pr.getPaymentTermDays());
					po.setPaymentTermes(pr.getPaymentTermes());
				} else {
					po.setPaymentTerm(pr.getPaymentTerm());
				}
				po.setPoNumber(pr.getReferenceNumber());
				po.setReferenceNumber(pr.getReferenceNumber());
				po.setRemarks(pr.getRemarks());
				po.setRequester(pr.getRequester());
				po.setStatus(PoStatus.ORDERED);
				po.setOrderedBy(user);
				po.setOrderedDate(new Date());

				if (pr.getSupplier() == null) {
					po.setSupplierName(pr.getSupplierName());
					po.setSupplierAddress(pr.getSupplierAddress());
					po.setSupplierTelNumber(pr.getSupplierTelNumber());
					po.setSupplierAddress(pr.getSupplierAddress());
					po.setSupplierTaxNumber(pr.getSupplierTaxNumber());
					po.setSupplierFaxNumber(pr.getSupplierFaxNumber());
				}

				if (pr.getSupplier() != null) {
					if (pr.getSupplier().getSupplier() != null) {
						pr.getSupplier().getSupplier().getCompanyName();
						po.setSupplierName(StringUtils.checkString(pr.getSupplier().getSupplier().getCompanyName()));
						po.setSupplierAddress(StringUtils.checkString(pr.getSupplier().getSupplier().getCity()));
						po.setSupplierTelNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getMobileNumber()));
						po.setSupplierTaxNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getTaxRegistrationNumber()));
						po.setSupplierFaxNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getFaxNumber()));

					}
					po.setSupplier(pr.getSupplier());

				}
				po.setTaxDescription(pr.getTaxDescription());
				po.setTermsAndConditions(pr.getTermsAndConditions());
				po.setTotal(pr.getTotal()); // as there is no additional tax coming from SAP
				if (pr.getAdditionalTax() != null) {
					po.setAdditionalTax(pr.getAdditionalTax());
				}
				po.setGrandTotal(pr.getGrandTotal());
				po.setUrgentPo(Boolean.FALSE);

				List<PoItem> poItems = new ArrayList<PoItem>();
				List<PrItem> itemList = pr.getPrItems();
				if (CollectionUtil.isNotEmpty(itemList)) {
					if (CollectionUtil.isNotEmpty(po.getPoItems())) {
						po.getPoItems().clear();
					}
					for (PrItem prItem : itemList) {
						if (prItem.getParent() == null) {
							LOG.info("pr parent");
							PoItem parent = new PoItem();
							parent.setItemName(prItem.getItemName());
							parent.setLevel(prItem.getLevel());
							parent.setOrder(prItem.getOrder());
							parent.setDeliveryReceiver(pr.getDeliveryReceiver());
							parent.setBuyer(prItem.getBuyer());
							parent.setPo(po);
							parent.setItemDescription(prItem.getItemDescription());
							List<PoItem> childrenList = new ArrayList<PoItem>();
							if (CollectionUtil.isNotEmpty(prItem.getChildren())) {
								for (PrItem itemPojo : prItem.getChildren()) {
									LOG.info("Children not empty" + prItem.getChildren().size());
									PoItem item = new PoItem();
									item.setBusinessUnit(pr.getBusinessUnit());
									item.setBuyer(itemPojo.getBuyer());
									item.setCostCenter(pr.getCostCenter());
									item.setDeliveryAddress(pr.getDeliveryAddress());
									if (pr.getDeliveryAddress() != null) {
										item.setDeliveryAddressTitle(StringUtils.checkString(pr.getDeliveryAddress().getTitle()));
										item.setDeliveryAddressLine1(StringUtils.checkString(pr.getDeliveryAddress().getLine1()));
										item.setDeliveryAddressLine2(StringUtils.checkString(pr.getDeliveryAddress().getLine2()));
										item.setDeliveryAddressZip(StringUtils.checkString(pr.getDeliveryAddress().getZip()));
										item.setDeliveryAddressCity(StringUtils.checkString(pr.getDeliveryAddress().getCity()));
										item.setDeliveryAddressState(StringUtils.checkString(pr.getDeliveryAddress().getState().getStateName()));
										item.setDeliveryAddressCountry(StringUtils.checkString(pr.getDeliveryAddress().getState().getCountry().getCountryName()));
									}
									item.setDeliveryDate(pr.getDeliveryDate());
									item.setDeliveryReceiver(pr.getDeliveryReceiver());
									item.setFreeTextItemEntered(itemPojo.getFreeTextItemEntered());
									item.setItemDescription(itemPojo.getItemDescription());
									item.setItemName(itemPojo.getItemName());
									item.setItemTax(itemPojo.getItemTax());
									item.setLevel(itemPojo.getLevel());
									item.setOrder(itemPojo.getOrder());
									item.setParent(parent);
									item.setPo(po);
									item.setProduct(itemPojo.getProduct());
									item.setProductCategory(itemPojo.getProductCategory());
									item.setQuantity(itemPojo.getQuantity());

									//PH-4113 SET DEFAULT WHEN COPY PR TO NEW PO
									item.setLockedQuantity(itemPojo.getQuantity());
									item.setBalanceQuantity(itemPojo.getQuantity());

									item.setTaxAmount(itemPojo.getTaxAmount());
									item.setTotalAmount(itemPojo.getTotalAmount());
									item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax());
									item.setUnit(itemPojo.getUnit());
									item.setUnitPrice(itemPojo.getUnitPrice());
									//PH-4113 SET DEFAULT WHEN COPY PR TO NEW PO
									item.setPricePerUnit(itemPojo.getPricePerUnit());

									childrenList.add(item);
								}
								parent.setChildren(childrenList);
								poItems.add(parent);
							}
						}

					}
				}

				po.setPoItems(poItems);
				po = poService.savePo(po);

				try {
					PoAudit audit = new PoAudit();
					audit.setAction(PoAuditType.CREATE);
					audit.setActionBy(user);
					audit.setActionDate(new Date());
					audit.setBuyer(pr.getBuyer());
					if (pr.getSupplier() != null) {
						audit.setSupplier(pr.getSupplier().getSupplier());
					}
					audit.setVisibilityType(PoAuditVisibilityType.BUYER);
					audit.setDescription(messageSource.getMessage("po.audit.create", new Object[] { pr.getPrId() }, Global.LOCALE));
					audit.setPo(po);
					poAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
				}

				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PO '" + pr.getPrId() + "' Created", po.getCreatedBy().getTenantId(), po.getCreatedBy(), new Date(), ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

			} else {
				LOG.info("ERP PO Creation :  PO Number : " + poPojo.getPoNumber());
				// Create PO data will come for ERP AGENT
				Buyer buyer = buyerService.findPlainBuyerById(buyerId);
				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				BusinessUnit businessUnit = null;
				if (StringUtils.checkString(poPojo.getBusinesUnit()).length() > 0) {
					businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), poPojo.getBusinesUnit());
					if (businessUnit == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { poPojo.getBusinesUnit() }, Global.LOCALE));
					}
				}

				Currency currency = null;
				if (StringUtils.checkString(poPojo.getCurrencyCode()).length() == 0) {
					throw new ApplicationException("Invalid Currency Code: " + poPojo.getCurrencyCode());
				}

				currency = currencyDao.findByCurrencyCode(poPojo.getCurrencyCode());
				if (currency == null) {
					throw new ApplicationException("Invalid Currency Code: " + poPojo.getCurrencyCode());
				}

				FavouriteSupplier supplier = null;
				if (StringUtils.checkString(poPojo.getSupplierCode()).length() > 0) {
					supplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(poPojo.getSupplierCode()), buyer.getId());
				}

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				String timeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(buyerId);
				if (StringUtils.checkString(timeZone).length() > 0) {
					simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				} else {
					simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
				}

				Po po = poDao.findByPoNomber(poPojo.getPoNumber(), buyerId);
				if (po == null) {
					po = new Po();
				}

				po.setPoId(poPojo.getPoNumber());
				po.setPoNumber(poPojo.getPoNumber());
				po.setName(StringUtils.checkString(poPojo.getName()).length() > 0 ? poPojo.getName() : poPojo.getPoNumber());
				po.setBuyer(buyer);

				po.setDecimal("2");
				po.setRequester(user.getName());

				po.setBusinessUnit(businessUnit);

				po.setLine1(businessUnit.getLine1());
				po.setLine2(businessUnit.getLine2());
				po.setLine3(businessUnit.getLine3());
				po.setLine4(businessUnit.getLine4());
				po.setLine5(businessUnit.getLine5());
				po.setLine6(businessUnit.getLine6());
				po.setLine7(businessUnit.getLine7());

				po.setCurrency(currency);
				po.setSupplier(supplier);
				po.setCreatedBy(user);
				try {
					if (StringUtils.checkString(poPojo.getPoDate()).length() > 0) {
						po.setCreatedDate(simpleDateFormat.parse(poPojo.getPoDate()));
					}
				} catch (ParseException e1) {
				}

				po.setDeliveryReceiver(poPojo.getDeliveryReceiver());
				po.setErpPrTransferred(Boolean.FALSE);
				po.setGrandTotal(poPojo.getGrandTotal());
				po.setTotal(poPojo.getGrandTotal());
				po.setIsPoReportSent(Boolean.FALSE);

				PaymentTermes paymentTermes = paymentTermesService.getByPaymentTermes(poPojo.getPaymentTerm(), buyerId);
				if (paymentTermes == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.paymentterm.not.found", new Object[] { poPojo.getPaymentTerm() }, Global.LOCALE));
				}

				po.setPaymentTerm(paymentTermes.getPaymentTermCode() + " - " + paymentTermes.getDescription());
				po.setPaymentTermDays(paymentTermes.getPaymentDays());
				po.setPaymentTermes(paymentTermes);

				Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(buyer.getId());
				if (Boolean.TRUE == isAutoPublishPo) {
					po.setStatus(PoStatus.ORDERED);
					po.setOrderedBy(user);
					po.setOrderedDate(new Date());
				} else {
					po.setStatus(PoStatus.READY);
				}

				po.setSupplierName(poPojo.getSupplierName());
				po.setSupplierAddress(poPojo.getSupplierAddress());
				po.setSupplierTelNumber(poPojo.getSupplierTelNumber());
				po.setSupplierTaxNumber(poPojo.getSupplierTaxNumber());
				po.setSupplierFaxNumber(poPojo.getSupplierFaxNumber());

				po.setUrgentPo(Boolean.FALSE);

				List<PoItem> poItems = new ArrayList<PoItem>();
				if (CollectionUtil.isNotEmpty(po.getPoItems())) {
					po.getPoItems().clear();
				}

				if (CollectionUtil.isNotEmpty(poPojo.getItemList())) {
					PoItem parent = new PoItem();
					parent.setItemName("Bill of Items");
					parent.setLevel(1);
					parent.setOrder(0);
					parent.setBuyer(buyer);
					parent.setPo(po);
					poItems.add(parent);
					for (ErpPoItemPojo itemPojo : poPojo.getItemList()) {
						PoItem item = new PoItem();

						if (itemPojo.getQuantity().floatValue() <= 0.0f) {
							throw new ApplicationException("Quantity should be more than zero. Item Name: " + itemPojo.getItemName());
						}

						ProductItem productItem = productListMaintenanceService.findProductItemByCode(itemPojo.getProductItemCode(), buyer.getId(), ProductItemType.MATERIAL);

						Uom uom = uomService.getUomByUomAndTenantId(itemPojo.getUom(), buyer.getId());
						if (uom == null) {
							throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { itemPojo.getUom() }, Global.LOCALE));
						}

						ProductCategory productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(itemPojo.getItemCategory(), buyer.getId());

						// if product Item not found Product category is becomes Name and Name is Desc

						item.setBusinessUnit(businessUnit);
						item.setBuyer(buyer);
						if (StringUtils.checkString(itemPojo.getDateOfDelivery()).length() > 0) {
							try {
								item.setDeliveryDate(simpleDateFormat.parse(itemPojo.getDateOfDelivery()));
							} catch (ParseException e) {
							}
						}
						// item.setDeliveryReceiver(pr.getDeliveryReceiver());
						item.setFreeTextItemEntered(Boolean.FALSE);
						item.setItemDescription(itemPojo.getItemName());
						item.setItemName(itemPojo.getItemName());
						item.setItemCode(itemPojo.getProductItemCode());
						item.setItemTax(String.valueOf(itemPojo.getItemTax()));
						item.setLevel(1);
						item.setOrder(itemPojo.getOrder());
						item.setParent(parent);

						item.setPo(po);

						item.setProduct(productItem);
						item.setProductCategory(productCategory);
						item.setQuantity(itemPojo.getQuantity());
						item.setTaxAmount(itemPojo.getTaxAmount());
						item.setTotalAmount(itemPojo.getTotalAmount());
						item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax());
						item.setUnit(uom);
						item.setUnitPrice(itemPojo.getUnitPrice());

						item.setDeliveryAddressTitle(StringUtils.checkString(itemPojo.getDeliveryAddressTitle()));
						item.setDeliveryAddressLine1(StringUtils.checkString(itemPojo.getDeliveryAddressLine1()));
						item.setDeliveryAddressLine2(StringUtils.checkString(itemPojo.getDeliveryAddressLine2()));
						item.setDeliveryAddressZip(StringUtils.checkString(itemPojo.getDeliveryAddressZip()));
						item.setDeliveryAddressCity(StringUtils.checkString(itemPojo.getDeliveryAddressCity()));
						item.setDeliveryAddressState(StringUtils.checkString(itemPojo.getDeliveryAddressState()));
						item.setDeliveryAddressCountry(StringUtils.checkString(itemPojo.getDeliveryAddressCountry()));
						item.setDeliveryReceiver(StringUtils.checkString(itemPojo.getDeliveryReceiver()));
						poItems.add(item);
					}
				}

				po.getPoItems().addAll(poItems);
				if (poItems != null && poItems.size() > 1) {
					po.setDeliveryAddressTitle(StringUtils.checkString(poItems.get(1).getDeliveryAddressTitle()));
					po.setDeliveryAddressLine1(StringUtils.checkString(poItems.get(1).getDeliveryAddressLine1()));
					po.setDeliveryAddressLine2(StringUtils.checkString(poItems.get(1).getDeliveryAddressLine2()));
					po.setDeliveryAddressZip(StringUtils.checkString(poItems.get(1).getDeliveryAddressZip()));
					po.setDeliveryAddressCity(StringUtils.checkString(poItems.get(1).getDeliveryAddressCity()));
					po.setDeliveryAddressState(StringUtils.checkString(poItems.get(1).getDeliveryAddressState()));
					po.setDeliveryAddressCountry(StringUtils.checkString(poItems.get(1).getDeliveryAddressCountry()));
					po.setDeliveryReceiver(StringUtils.checkString(poItems.get(1).getDeliveryReceiver()));
					po.setDeliveryDate(poItems.get(1).getDeliveryDate());

				}

				po = poService.updatePo(po);

				try {
					PoAudit audit = new PoAudit();
					audit.setAction(PoAuditType.CREATE);
					audit.setActionBy(user);
					audit.setActionDate(new Date());
					audit.setBuyer(buyer);
					if (po.getSupplier() != null) {
						audit.setSupplier(po.getSupplier().getSupplier());
					}
					audit.setVisibilityType(PoAuditVisibilityType.BUYER);
					audit.setDescription(messageSource.getMessage("po.audit.create", new Object[] { po.getPoNumber() }, Global.LOCALE));
					audit.setPo(po);
					poAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
				}

				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PO '" + po.getPoNumber() + "' Created", user.getTenantId(), user, new Date(), ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				if (po.getSupplier() != null) {
					if (po.getStatus() == PoStatus.ORDERED) {
						PoAudit orderedAudit = new PoAudit();
						orderedAudit.setAction(PoAuditType.ORDERED);
						orderedAudit.setActionBy(user);
						orderedAudit.setActionDate(new Date());
						orderedAudit.setBuyer(po.getBuyer());
						if (po.getSupplier() != null) {
							orderedAudit.setSupplier(po.getSupplier().getSupplier());
							orderedAudit.setDescription(messageSource.getMessage("po.audit.ordered", new Object[] { po.getPoNumber(), po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : po.getSupplierName() }, Global.LOCALE));

							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ORDERED, "PO '"+po.getPoNumber()+"' sent to Supplier "+(po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : po.getSupplierName()), user.getTenantId(), user, new Date(), ModuleType.PO);
							buyerAuditTrailDao.save(buyerAuditTrail);

						} else {
							orderedAudit.setDescription(messageSource.getMessage("po.opensupplier.audit.ordered", new Object[] { po.getPoNumber(), StringUtils.checkString(po.getSupplierName()).length() > 0 ? po.getSupplierName() : po.getSupplierName() }, Global.LOCALE));
						}
						orderedAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
						orderedAudit.setPo(po);
						poAuditService.save(orderedAudit);

						PoAudit receivedAudit = new PoAudit();
						receivedAudit.setAction(PoAuditType.RECEIVED);
						receivedAudit.setActionBy(user);
						receivedAudit.setActionDate(new Date());
						receivedAudit.setBuyer(po.getBuyer());
						if (po.getSupplier() != null) {
							receivedAudit.setSupplier(po.getSupplier().getSupplier());
						}
						receivedAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
						receivedAudit.setDescription(messageSource.getMessage("po.audit.received", new Object[] { po.getPoNumber(), po.getBuyer().getCompanyName() }, Global.LOCALE));
						receivedAudit.setPo(po);
						poAuditService.save(receivedAudit);
					}
				}

			}
		} else {
			LOG.info("PO Response list is Empty");
		}
	}

	private String generatePo(Pr pr) throws ApplicationException {

		String poNumber = eventIdSettingsDao.generateEventIdByBusinessUnit(pr.getBuyer().getId(), "PO", pr.getBusinessUnit());

		// String poNumber = null;
		// if (pr.getPrId().startsWith("PR")) {
		// poNumber = pr.getPrId().replaceAll("PR", "PO");
		// } else {
		// poNumber = "PO" + pr.getPrId();
		// }
		return poNumber;
	}

	/**
	 * @param mailTo
	 * @param pr
	 */
	private void sendPoCreatedEmail(User mailTo, Pr pr) {
		LOG.info("Sending PO created email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		try {
			String subject = "PO Created";
			String url = APP_URL + "/buyer/prView/" + pr.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", "");
			map.put("pr", pr);
			map.put("buyerName", mailTo.getName());
			map.put("buyerLoginEmail", mailTo.getLoginId());
			map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("pocreatedDate", sdf.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			String erpNotifiactionemail = getErpNotifiactionEmailsByBuyerSettings(mailTo.getTenantId());
			if (StringUtils.checkString(erpNotifiactionemail).length() > 0 && mailTo.getEmailNotifications()) {
				sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PO_CREATED_TEMPLATE);
			} else {
				if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications())
					sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PO_CREATED_TEMPLATE);
				else
					LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
			}

			String notificationMessage = messageSource.getMessage("po.create.notification.message", new Object[] { pr.getName() }, Global.LOCALE);
			sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.CREATED_MESSAGE);
			prService.sendPrFinishMailToSupplier(pr);
		} catch (Exception e) {
			LOG.error("Error while Sending PO Created :" + e.getMessage(), e);
		}

	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	/**
	 * @param pr
	 * @param mailTo
	 * @param remarks
	 */
	private void sendPrRejectionEmail(User mailTo, Pr pr, String remarks) {
		LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		String url = APP_URL + "/buyer/prView/" + pr.getId();
		String subject = "PR Rejected";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", "");
		map.put("pr", pr);
		map.put("remarks", StringUtils.checkString(remarks));
		map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
		map.put("message", "ERP has Rejected");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		String erpNotifiactionemail = getErpNotifiactionEmailsByBuyerSettings(mailTo.getTenantId());
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(erpNotifiactionemail, subject, map, Global.ERP_PR_REJECT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("erp.pr.rejection.notification.message", new Object[] { pr.getName(), remarks }, Global.LOCALE);
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

		// if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
		// try {
		// LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
		// Map<String, String> payload = new HashMap<String, String>();
		// payload.put("id", pr.getId());
		// payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
		// payload.put("eventType", FilterTypes.PR.toString());
		// notificationService.pushOneSignalNotification(notificationMessage, null, payload,
		// Arrays.asList(mailTo.getDeviceId()));
		// } catch (Exception e) {
		// LOG.error("Error While sending PR reject Mobile push notification to '" + mailTo.getCommunicationEmail() + "'
		// : " + e.getMessage(), e);
		// }
		// } else {
		// LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
		// }
	}

	@Override
	public long findTotalPrTransfer(String tenantId, String userId,List<String> businessUnitIds) {
		return prDao.findTotalPrTransfer(tenantId, userId,businessUnitIds);
	}

	@Override
	public long findTotalCompletePr(String tenantId, String userId,List<String> businessUnitIds) {
		return prDao.findTotalCompletePr(tenantId, userId,businessUnitIds);
	}

	@Override
	public long findTotalCancelledPr(String tenantId, String userId,List<String> businessUnitIds) {
		return prDao.findTotalCancelledPr(tenantId, userId,businessUnitIds);
	}

	@Override
	public List<Pr> findAllPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return prDao.findAllPoTransfer(userId, tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return prDao.findTotalFilteredPoTransfer(userId, tenantId, input, startDate, endDate);
	}

	private String getErpNotifiactionEmailsByBuyerSettings(String tenantId) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				LOG.info("fetching buyer setting-------------------");
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(tenantId);
				if (buyerSettings != null) {
					return buyerSettings.getErpNotificationEmails();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting :" + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean isExistsPrId(String tenantId, String id) {
		return prDao.isExistsPrId(tenantId, id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePrContactsByPrId(String prId) {
		prDao.deletePrContactsByPrId(prId);
	}

	private void sendAddTeamMemberEmailNotificationEmail(Pr pr, User user, TeamMemberType memberType) {

		try {
			String subject = "You have been Invited as TEAM MEMBER in PR";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the PR but not finish the PR");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the PR without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the PR Owner.");
			String eventName = StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ";
			map.put("eventName", StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ");
			map.put("createdBy", pr.getCreatedBy().getName());
			map.put("eventId", pr.getPrId());
			map.put("eventRefNum", StringUtils.checkString(pr.getReferenceNumber()).length() > 0 ? pr.getReferenceNumber() : " ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE_PR), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/createPrDetails/" + pr.getId();

			String notificationMessage = messageSource.getMessage("team.pr.add", new Object[] { memberType, eventName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}
		/*
		 * String notificationMessage = messageSource.getMessage("po.create.notification.message", new Object[] {
		 * pr.getName() }, Global.LOCALE); sendDashboardNotification(user, url, subject, notificationMessage,
		 * NotificationType.CREATED_MESSAGE);
		 */

	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	public List<Pr> findAllSearchFilterPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType) {
		LOG.info("pr report findAllSearchFilterPr  "+userId);
		return prDao.findAllSearchFilterPr(userId, tenantId, input, startDate, endDate, prStatus,selectedStatus,prType);
	}
@Override
	public List<Pr> findAllSearchFilterPrBizUnit(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType,List<String> businessUnitIds) {
		LOG.info(" BIZ UNIT pr report findAllSearchFilterPr  "+userId);
		return prDao.findAllSearchFilterPrBizUnit(userId, tenantId, input, startDate, endDate, prStatus,selectedStatus,prType,businessUnitIds);
		//return prDao.findAllSearchFilterPr(userId, tenantId, input, startDate, endDate, prStatus,selectedStatus,prType);
	}

	@Override
	@Transactional(readOnly = false)
	public void downloadPRReports(List<PrPojo> prPojo, HttpServletResponse response, HttpSession session) {
		LOG.info("Download Pr reports... " + response.getHeaderNames());
		try {
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "prReports.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PR Report");

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();

			// font.setColor(HSSFColor.WHITE.index);

			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

			CellStyle totalStyle = workbook.createCellStyle();
			totalStyle.setAlignment(CellStyle.ALIGN_RIGHT);

			// Aqua background
			// styleHeading.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			// styleHeading.setFillPattern(CellStyle.BIG_SPOTS);

			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;
			for (String column : Global.PR_REPORT_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);

				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}

			/*
			 * CellStyle style = wb.createCellStyle(); style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
			 * style.setFillPattern(CellStyle.BIG_SPOTS); row.setRowStyle(style);
			 */

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int r = 1;
			// Write Data into Excel
			for (PrPojo po : prPojo) {
				// For Financial Standard
				DecimalFormat df = null;
				if (po.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (po.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (po.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (po.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (po.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (po.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(r - 1);
				row.createCell(cellNum++).setCellValue(po.getReferenceNumber() != null ? po.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(po.getName() != null ? po.getName() : "");
				// row.createCell(cellNum++).setCellValue((po.getSupplier() != null && po.getSupplier().getFullName() !=
				// null) ? po.getSupplier().getFullName() : (po.getSupplierName() != null ? po.getSupplierName() : ""));
				// row.createCell(cellNum++).setCellValue(po.getSupplier() != null ?
				// (po.getSupplier().getSupplier()!=null?po.getSupplier().getSupplier().getCompanyName():"" ):"");
				row.createCell(cellNum++).setCellValue((po.getSupplier() != null && po.getSupplier().getFullName() != null) ? po.getSupplier().getFullName() : po.getSupplierName());
				row.createCell(cellNum++).setCellValue(po.getDescription() != null ? po.getDescription() : "");
				row.createCell(cellNum++).setCellValue(po.getPrId() != null ? po.getPrId() : "");
				row.createCell(cellNum++).setCellValue(po.getCreatedBy() != null ? po.getCreatedBy().getName() : "");
				row.createCell(cellNum++).setCellValue(po.getPrCreatedDate() != null ? sdf.format(po.getPrCreatedDate()) : "");

				row.createCell(cellNum++).setCellValue(po.getApprovedBy() != null ? po.getApprovedBy().getName() : "");
				row.createCell(cellNum++).setCellValue(po.getPrApprovedDate() != null ? sdf.format(po.getPrApprovedDate()) : "");
				row.createCell(cellNum++).setCellValue(po.getCurrency() != null ? po.getCurrency().getCurrencyCode() : "");

				Cell grandTotalCell = row.createCell(cellNum++);
				// CellStyle grandTotalCellStyle = grandTotalCell.getCellStyle();
				// grandTotalCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellValue(po.getGrandTotal() != null ? df.format(po.getGrandTotal()) : "");
				grandTotalCell.setCellStyle(totalStyle);

				row.createCell(cellNum++).setCellValue(po.getBusinessUnit() != null ? po.getBusinessUnit().getUnitName() : "");
				row.createCell(cellNum++).setCellValue(po.getPoNumber() != null ? po.getPoNumber().toString() : "");
			}
			// Auto Fit
			for (int k = 0; k < 15; k++) {
				sheet.autoSizeColumn(k, true);
			}

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					// response.getOutputStream().flush();
					// FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					LOG.info("Successfully written in Excel===========================");
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
			LOG.info("updating Po Report Sent");

		} catch (Exception e) {
			LOG.error("Error while downloading PO Reports Excel : " + e.getMessage(), e);
		}

	}

	@Override
	public long findTotalFilteredPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType,List<String> businessUnitIds) {
		return prDao.findTotalFilteredPr(userId, tenantId, input, startDate, endDate, prStatus,selectedStatus,prType,businessUnitIds);
	}

	@Override
	public long findTotalPr(String tenantId, String userId) {
		return prDao.findTotalPr(tenantId, userId);
	}

	@Override
	public List<Pr> findSupplierAllPo(String tenantId) {
		return prDao.findSupplierAllPo(tenantId);
	}

	@Override
	public List<Pr> findBuyerAllPo(String tenantId, boolean isPo) {
		return prDao.findBuyerAllPo(tenantId, isPo);
	}

	@Override
	public List<Pr> findAllSearchFilterPrReport(String tenantId, String[] prArr, boolean select_all, SearchFilterPrPojo searchFilterPrPojo, Date startDate, Date endDate, String userId) {

		return prDao.findAllSearchFilterPrReport(tenantId, prArr, select_all, searchFilterPrPojo, startDate, endDate, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void downloadFinancePoReportsForAdmin(String dateTimeRange, HttpServletResponse response, HttpSession session) {
		LOG.info("Download PO reports for finance....................... " + dateTimeRange);
		try {

			if (dateTimeRange.length() > 0) {
				String[] dates = dateTimeRange.split("-");

				Date startDate = new SimpleDateFormat("dd/MM/yyyy HH:mm ").parse(dates[0]);
				Date endDate = new SimpleDateFormat("dd/MM/yyyy HH:mm ").parse(dates[1]);

				startDate = DateUtil.formatDateToStartTime(startDate);
				endDate = DateUtil.formatDateToEndTime(endDate);

				LOG.info("start date       " + startDate);
				LOG.info("end Date    " + endDate);

				List<FinancePo> poList = prDao.getPoForDateBetween(startDate, endDate);

				String downloadFolder = context.getRealPath("/WEB-INF/");
				String fileName = "poReports.xlsx";
				Path file = Paths.get(downloadFolder, fileName);
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("PO Report");

				// Style of Heading Cells
				CellStyle styleHeading = workbook.createCellStyle();
				Font font = workbook.createFont();

				// font.setColor(HSSFColor.WHITE.index);

				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

				// Aqua background
				// styleHeading.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				// styleHeading.setFillPattern(CellStyle.BIG_SPOTS);

				// Creating Headings

				Row adminrow = sheet.createRow(1);
				// adminrow.createCell(2).setCellValue("Privasia Sdn Bhd");

				Cell newcell = adminrow.createCell(1);

				newcell.setCellValue("Privasia Sdn Bhd");
				newcell.setCellStyle(styleHeading);

				Row reportrow = sheet.createRow(2);
				newcell = reportrow.createCell(1);

				newcell.setCellValue("Invoice Financing Report");
				newcell.setCellStyle(styleHeading);

				// adminrow.createCell(2).setCellValue("Invoice Financing
				// Report");

				Row dateTitlerow = sheet.createRow(4);

				newcell = dateTitlerow.createCell(1);

				newcell.setCellValue("Report From Date:");
				newcell.setCellStyle(styleHeading);

				newcell = dateTitlerow.createCell(3);

				newcell.setCellValue("Report To Date:");
				newcell.setCellStyle(styleHeading);

				adminrow = sheet.createRow(5);
				adminrow.createCell(1).setCellValue(dates[0]);
				adminrow.createCell(3).setCellValue(dates[1]);

				Row rowHeading = sheet.createRow(7);

				CellStyle tblheadstyle = workbook.createCellStyle();
				tblheadstyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
				Font tblheadfont = workbook.createFont();
				tblheadfont.setColor(IndexedColors.WHITE.getIndex());
				tblheadfont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				tblheadstyle.setFont(tblheadfont);
				styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
				tblheadstyle.setFillPattern(CellStyle.BIG_SPOTS);

				CellStyle alignright = workbook.createCellStyle();
				alignright.setAlignment(CellStyle.ALIGN_RIGHT);

				int i = 1;
				for (String column : Global.PO_FINANCE_REPORT_EXCEL_COLUMNS) {
					Cell cell = rowHeading.createCell(i++);

					cell.setCellValue(column);
					cell.setCellStyle(tblheadstyle);
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

				int r = 8;
				// Write Data into Excel

				if (CollectionUtil.isNotEmpty(poList)) {

					for (FinancePo po : poList) {
						DecimalFormat df = null;
						if (po.getPo().getDecimal().equals("1")) {
							df = new DecimalFormat("#,###,###,##0.0");
						} else if (po.getPo().getDecimal().equals("2")) {
							df = new DecimalFormat("#,###,###,##0.00");
						} else if (po.getPo().getDecimal().equals("3")) {
							df = new DecimalFormat("#,###,###,##0.000");
						} else if (po.getPo().getDecimal().equals("4")) {
							df = new DecimalFormat("#,###,###,##0.0000");
						} else if (po.getPo().getDecimal().equals("5")) {
							df = new DecimalFormat("#,###,###,##0.00000");
						} else if (po.getPo().getDecimal().equals("6")) {
							df = new DecimalFormat("#,###,###,##0.000000");
						} else {
							df = new DecimalFormat("#,###,###,##0.00");
						}
						// For Financial Standard

						Row row = sheet.createRow(r++);

						newcell = row.createCell(1);
						newcell.setCellValue(po.getPo() != null ? po.getPo().getCreatedDate().toString() : "");

						newcell = row.createCell(2);
						newcell.setCellValue(po.getSupplier() != null ? po.getFinanceCompany().getCompanyName() : "");

						newcell = row.createCell(3);
						newcell.setCellValue(po.getSupplier() != null ? po.getSupplier().getCompanyName() : "");

						newcell = row.createCell(4);
						newcell.setCellValue(po.getPo() != null ? df.format(po.getPo().getGrandTotal()).toString() : "");
						newcell.setCellStyle(alignright);

						newcell = row.createCell(5);
						newcell.setCellValue(df.format(po.getReferralFee()).toString());
						newcell.setCellStyle(alignright);

						newcell = row.createCell(6);
						newcell.setCellValue(po.getPaymentDate() != null ? po.getPaymentDate().toString() : "");

						newcell = row.createCell(7);
						newcell.setCellValue(po.getReferenceNum());

					}
				} else {

					Row row = sheet.createRow(r++);
					newcell = row.createCell(1);
					newcell.setCellValue("No Data Found");

				}
				// Auto Fit
				for (int k = 0; k < 15; k++) {
					sheet.autoSizeColumn(k, true);
				}

				// Color highlight
				SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
				ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule("MOD(ROW(),2)");
				PatternFormatting patternFmt = rule1.createPatternFormatting();
				patternFmt.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
				String value = "B9:H" + r;
				LOG.info("value.............:" + r);
				CellRangeAddress[] regions = { CellRangeAddress.valueOf(value) };
				sheetCF.addConditionalFormatting(regions, rule1);

				// Save Excel File
				FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
				workbook.write(out);
				out.close();
				LOG.info("Successfully written in Excel");

				if (Files.exists(file)) {
					response.setContentType("application/vnd.ms-excel");
					response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
					try {
						Files.copy(file, response.getOutputStream());
						// response.getOutputStream().flush();
						// FileCopyUtils.copy(docs.getFileData(),
						// response.getOutputStream());
						response.flushBuffer();
						response.setStatus(HttpServletResponse.SC_OK);
						LOG.info("Successfully written in Excel===========================");
					} catch (IOException e) {
						LOG.error("Error :- " + e.getMessage(), e);
					}
				}
				LOG.info("updating Po Report Sent");

			}
			// prDao.updateSentPoReport(prArr);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Reports Excel : " + e.getMessage(), e);
		}

	}

	@Override
	public String getBusineessUnitname(String prId) {
		return prDao.getBusineessUnitname(prId);
	}

	@Override
	@Transactional(readOnly = false)
	public void sendPrFinishMailToSupplier(Pr pr) {
		// try {
		// Pr dbPr = findPrById(pr.getId());
		// String mailTo = null;
		// LOG.info("sendPrFinishMailToSupplier called");
		// if (dbPr.getSupplier() != null) {
		//
		// mailTo = dbPr.getSupplier().getSupplier().getCommunicationEmail();
		//
		// LOG.info("COMMUNICATION MAIL" + dbPr.getSupplier().getSupplier().getCommunicationEmail());
		// LOG.info("company Name:" + dbPr.getSupplier().getSupplier().getCompanyName());
		// String subject = "Buyer shared PO";
		// String url = APP_URL + "/supplier/supplierPrView/" +dbPr.getId();
		// LOG.info("**************+dbPr.getId()*************"+dbPr.getId());
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("userName", dbPr.getSupplier().getSupplier().getCompanyName());
		// map.put("pr", pr);
		// map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
		// map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
		// map.put("buyer", dbPr.getBuyer().getCompanyName());
		// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		// String timeZone = "GMT+8:00";
		// timeZone = getTimeZoneByBuyerSettings(pr.getCreatedBy().getTenantId(), timeZone);
		// map.put("date", df.format(new Date()));
		// map.put("loginUrl", APP_URL + "/login");
		// map.put("appUrl", url);
		// if (StringUtils.checkString(mailTo).length() > 0) {
		// sendEmail(mailTo, subject, map, Global.PR_FINISH_SUPPLIER_TEMPLATE);
		// } else {
		// LOG.warn("No communication email configured for user : " + pr.getCreatedBy().getLoginId() + "... Not going to
		// send email notification");
		// }
		//
		// String notificationMessage = messageSource.getMessage("pr.supplier.notification.message", new Object[] {
		// pr.getName() }, Global.LOCALE);
		//
		// List<User> userList = userDao.getAllAdminPlainUsersForSupplier(dbPr.getSupplier().getSupplier().getId());
		// if (CollectionUtil.isNotEmpty(userList)) {
		// for (User adminUser : userList) {
		// sendDashboardNotification(adminUser, url, subject, notificationMessage, pr.getCreatedBy());
		// }
		// }
		//
		//
		//
		//
		// }
		// } catch (Exception e) {
		// LOG.error("Error while sending pr finish mail :" + e.getMessage(), e);
		// }

	}

	private Pr newPrFromEvent(Event event) {
		Pr pr = new Pr();
		pr.setName(event.getEventName());
		pr.setCurrency(event.getBaseCurrency());
		pr.setDecimal(event.getDecimal());
		pr.setCostCenter(event.getCostCenter());
		pr.setBusinessUnit(event.getBusinessUnit());
		pr.setAvailableBudget(event.getBudgetAmount());
		pr.setDescription(event.getEventDescription());
		pr.setDeliveryDate(event.getDeliveryDate());
		pr.setDeliveryTime(event.getDeliveryDate());
		pr.setDeliveryAddress(event.getDeliveryAddress());
		pr.setBuyer(event.getCreatedBy().getBuyer());
		pr.setPaymentTerm(event.getPaymentTerm());
		return pr;
	}

	@Override
	@Transactional(readOnly = false)
	public Pr copyFromTemplateWithAward(
			String templateId, User createdBy, User loggedInUser, String tenantId,
			BusinessUnit buyerbusinessUnit, FavouriteSupplier supplier,
			Map<String, List<PrItem>> sections, Event rfxEvent) throws Exception {
		LOG.info("-----------------------------------");
		LOG.info("-----------------------------------");
		LOG.info("-----------------------------------");
		PrTemplate prTemplate = prTemplateService.getPrTemplateById(templateId);
		Pr newPr = newPrFromEvent(rfxEvent);
		newPr.setTemplate(prTemplate);
		newPr.setBuyer(loggedInUser.getBuyer());
		newPr.setCreatedBy(createdBy);
		newPr.setStatus(PrStatus.DRAFT);
		newPr.setPrCreatedDate(new Date());
		newPr.setSupplier(supplier);

		// copy approval from template
		if (CollectionUtil.isNotEmpty(prTemplate.getApprovals())) {
			List<PrApproval> approvalList = new ArrayList<>();
			for (PrTemplateApproval prTemplateApproval : prTemplate.getApprovals()) {
				LOG.info("PR Template Approval Level :" + prTemplateApproval.getLevel());
				PrApproval newPrApproval = new PrApproval();
				newPrApproval.setApprovalType(prTemplateApproval.getApprovalType());
				newPrApproval.setLevel(prTemplateApproval.getLevel());
				newPrApproval.setPr(newPr);
				if (CollectionUtil.isNotEmpty(prTemplateApproval.getApprovalUsers())) {
					List<PrApprovalUser> prApprovalList = new ArrayList<>();
					for (PrTemplateApprovalUser prTemplateApprovalUser : prTemplateApproval.getApprovalUsers()) {
						PrApprovalUser prApprovalUser = new PrApprovalUser();
						prApprovalUser.setApprovalStatus(prTemplateApprovalUser.getApprovalStatus());
						prApprovalUser.setApproval(newPrApproval);
						prApprovalUser.setRemarks(prTemplateApprovalUser.getRemarks());
						prApprovalUser.setUser(prTemplateApprovalUser.getUser());
						prApprovalList.add(prApprovalUser);
					}
					newPrApproval.setApprovalUsers(prApprovalList);
				}
				approvalList.add(newPrApproval);
			}
			newPr.setPrApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(prTemplate.getFields())) {

			for (PrTemplateField field : prTemplate.getFields()) {
				if (!field.getReadOnly()) {
					continue;
				}
				switch (field.getFieldName()) {
				case BASE_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newPr.setCurrency(currency);
						LOG.info("Base Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case COST_CENTER:
					if (field.getDefaultValue() != null) {
						CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
						newPr.setCostCenter(costCenter);
						LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
					}
					break;
				case BUSINESS_UNIT:
					if (field.getDefaultValue() != null) {
						BusinessUnit businessUnit = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
						newPr.setBusinessUnit(businessUnit);
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				case HIDE_OPEN_SUPPLIER:
					if (field.getVisible() != null) {
						newPr.setHideContractBased(field.getVisible());
					}
					break;
				case AVAILABLE_BUDGET:
					if (field.getDefaultValue() != null) {
						newPr.setAvailableBudget(new BigDecimal(field.getDefaultValue()));
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				case DECIMAL:
					if (field.getDefaultValue() != null) {
						newPr.setDecimal(field.getDefaultValue());
						LOG.info("Decimal Default value :  " + field.getDefaultValue());
					}
					break;
				case CORRESPONDENCE_ADDRESS:
					if (field.getDefaultValue() != null) {
						BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(field.getDefaultValue());
						if (buyerAddress != null && buyerAddress.getStatus() == Status.INACTIVE) {
							LOG.info("inactive Delivery address found ....");
							if (field.getReadOnly())
								throw new ApplicationException("Delivery address is Inactive for Template:" + prTemplate.getTemplateName());
							else {
								LOG.info("inactive Delivery address found with not read only....");
								// no need to do anything here
							}
						} else {
							LOG.info("active Delivery address found ........");
							newPr.setCorrespondenceAddress(buyerAddress);
						}

						LOG.info("buyerAddress : " + buyerAddress + "Default value :  " + field.getDefaultValue());

					}
					break;
				case PR_NAME:
					if (field.getDefaultValue() != null) {
						newPr.setName(field.getDefaultValue());
					}
					break;
				case PAYMENT_TERM:
					if (field.getDefaultValue() != null) {
						newPr.setPaymentTerm(field.getDefaultValue());
					}
					break;
				case REQUESTER:
					if (field.getDefaultValue() != null) {
						newPr.setRequester(field.getDefaultValue());
					}
					break;
				case TERM_AND_CONDITION:
					if (field.getDefaultValue() != null) {
						newPr.setTermsAndConditions(field.getDefaultValue());
					}
					break;
				default:
					break;
				}
			}
		}
		// if buyer setting is enable for id generation upon business unit then user can select the own business unit
		if (eventIdSettingsDao.isBusinessSettingEnable(tenantId, "PR")) {
			if (buyerbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newPr.setBusinessUnit(buyerbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newPr.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("Business unit is Empty in PR Template");
				}

			}
		}
		newPr.setPrId(eventIdSettingsDao.generateEventIdByBusinessUnit(tenantId, "PR", newPr.getBusinessUnit()));
		LOG.info("-----------------------------------");
		LOG.info("-------------newPr.setPrId----------------------" + newPr.getPrId());
		LOG.info("-----------------------------------");
		newPr = prDao.saveOrUpdate(newPr);

		int level = 1;
		for (String sectionLevel : sections.keySet()) {
			List<PrItem> items = sections.get(sectionLevel);
			int order = 0;
			PrItem sectionItem = null;
			for (PrItem item : items) {
				item.setPr(newPr);
				item.setLevel(level);
				item.setOrder(order);
				LOG.info("Order" + item.getOrder());
				if (order == 0) {
					sectionItem = savePrItemBare(item);
				} else {
					item.setParent(sectionItem);
					try {
						item.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_DOWN));
						item.setPricePerUnit(item.getPricePerUnit() != null ? item.getPricePerUnit().setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(1).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_DOWN));
						item.setQuantity(item.getQuantity() != null ? item.getQuantity().setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_DOWN));
						item.setTotalAmount(item.getUnitPrice() != null && item.getQuantity() != null ? item.getUnitPrice().multiply(item.getQuantity()).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0));
						BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP);
						if (item.getItemTax() != null) {
							taxAmount = new BigDecimal(item.getItemTax()).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP);
							taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP);
							// Roundup the decimals after calculation
							taxAmount = taxAmount.setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP);
						}
						item.setTaxAmount(taxAmount);
						item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0).setScale(Integer.parseInt(newPr.getDecimal()), BigDecimal.ROUND_HALF_UP));
					} catch (Exception e) {
						LOG.error("Error : " + e.getMessage(), e);
						throw new NotAllowedException(messageSource.getMessage("common.number.format.error", new Object[] {}, Global.LOCALE));
					}
					item = savePrItemBare(item);
					newPr.setTotal(newPr.getTotal() != null && item.getTotalAmountWithTax() != null ? newPr.getTotal().add(item.getTotalAmountWithTax()) : new BigDecimal(0));
					newPr.setGrandTotal(newPr.getTotal() != null && newPr.getAdditionalTax() != null ? newPr.getTotal().add(newPr.getAdditionalTax()) : new BigDecimal(0));
					updatePr(newPr);
				}
				order++;
			}
			level++;
		}
		LOG.info("-----------------------------------");
		LOG.info("-------------newPr.setPrId----------------------" + newPr.getPrId());
		LOG.info("-----------------------------------");
		List<PrTeamMember> teamMembers = new ArrayList<PrTeamMember>();
		if (CollectionUtil.isNotEmpty(prTemplate.getTeamMembers())) {
			for (TemplatePrTeamMembers team : prTemplate.getTeamMembers()) {
				PrTeamMember newTeamMembers = new PrTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setPr(newPr);
				teamMembers.add(newTeamMembers);
				sendAddTeamMemberEmailNotificationEmail(newPr, team.getUser(), newTeamMembers.getTeamMemberType());
			}
			newPr.setPrTeamMembers(teamMembers);
		}

		try {
			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.CREATE);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(messageSource.getMessage("pr.audit.create", new Object[] { newPr.getPrId() }, Global.LOCALE));
			audit.setPr(newPr);
			prAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
		}
		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR '" + newPr.getPrId() + "' Created", newPr.getCreatedBy().getTenantId(), newPr.getCreatedBy(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		LOG.info("-----------------------------------");
		LOG.info("-------------newPr.setPrId----------------------" + newPr.getPrId());
		LOG.info("-----------------------------------");
		return newPr;
	}

	@Override
	@Transactional(readOnly = false)
	public List<PrItem> deletePrItemsByPrId(String prId) {

		{
			List<PrItem> returnList = new ArrayList<PrItem>();
			prItemDao.deletePrItemsByPrId(prId);
			List<PrItem> list = prItemDao.getPrItemsbyId(prId);
			if (CollectionUtil.isNotEmpty(list)) {
				for (PrItem item : list) {
					PrItem parent = item.createShallowCopy();
					if (item.getParent() == null) {
						returnList.add(parent);
					}
					if (CollectionUtil.isNotEmpty(item.getChildren())) {
						for (PrItem child : item.getChildren()) {
							if (parent.getChildren() == null) {
								parent.setChildren(new ArrayList<PrItem>());
							}
							parent.getChildren().add(child.createShallowCopy());
							LOG.info("tot with tax :" + child.getTotalAmountWithTax());
						}
					}
				}
			}
			return returnList;
		}

	}

	@Override
	public long findTotalPoList(String tenantId, String userId, List<PrStatus> prStatus) {
		return prDao.findTotalPoList(tenantId, userId, prStatus);
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public MobilePrPojo getMobilePrDetailsSupplier(String id) {
		// Pr pr = prDao.getMobilePrDetails(id);
		Po po = poDao.getMobilePoDetails(id);

		// pr.setPrDocuments(prDocumentDao.findAllPrDocsNameAndId(id));

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(po.getCreatedBy().getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobilePrPojo mobilePrPojo = new MobilePrPojo(po);

		mobilePrPojo.setPrId(po.getPoNumber());

		if (CollectionUtil.isNotEmpty(mobilePrPojo.getComments())) {
			for (Comments comm : mobilePrPojo.getComments()) {
				if (comm.getCreatedDate() != null)
					comm.setCreatedDateApk(df.format(comm.getCreatedDate()));
				else
					comm.setCreatedDateApk("N/A");
			}
		}

		if (po.getBusinessUnit() != null) {
			mobilePrPojo.setBusinessUnit(po.getBusinessUnit().getUnitName());
		} else {
			mobilePrPojo.setBusinessUnit("N/A");
		}
		if (po.getCostCenter() != null) {
			mobilePrPojo.setCostCenter(po.getCostCenter().getCostCenter());
		} else {
			mobilePrPojo.setCostCenter("N/A");
		}
		return mobilePrPojo;
	}

	@Override
	public long findTotalNotDraftPrList(String tenantId, String userId, List<PrStatus> prStatus,String selectedStatus,String prType) {
		return prDao.findTotalNotDraftPrList(tenantId, userId, prStatus, selectedStatus,prType);
	}

	@Override
	public Long findProductCategoryCountByPrId(String prId) {
		return prItemDao.findProductCategoryCountByPrId(prId);
	}

	@Override
	public Pr findPrBySystemGeneratedPrIdAndTenantId(String prId, String tenantId) {
		Pr pr = prDao.findPrBySystemGeneratedPrIdAndTenantId(prId, tenantId);
		if (pr != null && pr.getCreatedBy() != null) {
			pr.getCreatedBy().getLoginId();
		}
		return pr;
	}

	@Override
	public long findTotalPoReportList(String tenantId, String userId, List<PrStatus> prStatus) {
		return prDao.findTotalPoReportList(tenantId, userId, prStatus);
	}

	@Override
	public long findTotalFilteredPoReportList(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus) {
		return prDao.findTotalFilteredPoReportList(userId, tenantId, input, startDate, endDate, prStatus);
	}

	@Override
	public Pr findPrBUAndCCForBudgetById(String prId) {
		return prDao.findPrBUAndCCForBudgetById(prId);
	}

	@Override
	public Pr findPrApprovalForBudgetById(String id) {
		return prDao.findPrForApprovalById(id);
	}

	@Override
	public String replaceSmartQuotes(String input) {
			return input.replace("‘", "'").replace("’", "'").replace("“", "\"").replace("”", "\"");
	}

	@Override
	@Transactional(readOnly = false)
	public Po createPo(User loggedInUser, Pr pr) throws ApplicationException {
		Po po = null;
		if (pr != null) {
			pr = prDao.findById(pr.getId());
			Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(loggedInUser.getTenantId());
			// generating po number
			String poNumber = generatePo(pr);
			po = new Po();
			po.setPoNumber(poNumber);
			po.setBuyer(pr.getBuyer());
			po.setBusinessUnit(pr.getBusinessUnit());
			po.setLine1(pr.getBusinessUnit().getLine1());
			po.setLine2(pr.getBusinessUnit().getLine2());
			po.setLine3(pr.getBusinessUnit().getLine3());
			po.setLine4(pr.getBusinessUnit().getLine4());
			po.setLine5(pr.getBusinessUnit().getLine5());
			po.setLine6(pr.getBusinessUnit().getLine6());
			po.setLine7(pr.getBusinessUnit().getLine7());

			po.setCorrespondenceAddress(pr.getCorrespondenceAddress());
			if (pr.getCorrespondenceAddress() != null) {
				po.setCorrespondAddressTitle(StringUtils.checkString(pr.getCorrespondenceAddress().getTitle()));
				po.setCorrespondAddressLine1(StringUtils.checkString(pr.getCorrespondenceAddress().getLine1()));
				po.setCorrespondAddressLine2(StringUtils.checkString(pr.getCorrespondenceAddress().getLine2()));
				po.setCorrespondAddressZip(StringUtils.checkString(pr.getCorrespondenceAddress().getZip()));
				po.setCorrespondAddressCity(StringUtils.checkString(pr.getCorrespondenceAddress().getCity()));
				po.setCorrespondAddressState(StringUtils.checkString(pr.getCorrespondenceAddress().getState().getStateName()));
				po.setCorrespondAddressCountry(StringUtils.checkString(pr.getCorrespondenceAddress().getState().getCountry().getCountryName()));
			}
			if (pr.getDeliveryAddress() != null) {
				po.setDeliveryAddress(pr.getDeliveryAddress());
				po.setDeliveryAddressTitle(StringUtils.checkString(pr.getDeliveryAddress().getTitle()));
				po.setDeliveryAddressLine1(StringUtils.checkString(pr.getDeliveryAddress().getLine1()));
				po.setDeliveryAddressLine2(StringUtils.checkString(pr.getDeliveryAddress().getLine2()));
				po.setDeliveryAddressZip(StringUtils.checkString(pr.getDeliveryAddress().getZip()));
				po.setDeliveryAddressCity(StringUtils.checkString(pr.getDeliveryAddress().getCity()));
				po.setDeliveryAddressState(StringUtils.checkString(pr.getDeliveryAddress().getState().getStateName()));
				po.setDeliveryAddressCountry(StringUtils.checkString(pr.getDeliveryAddress().getState().getCountry().getCountryName()));
			}
			po.setCostCenter(pr.getCostCenter());
			po.setCreatedBy(loggedInUser);
			po.setCreatedDate(new Date());
			po.setCurrency(pr.getCurrency());
			po.setDecimal(pr.getDecimal());
			po.setDeliveryAddress(pr.getDeliveryAddress());
			po.setDeliveryDate(pr.getDeliveryDate());
			po.setDeliveryReceiver(pr.getDeliveryReceiver());
			po.setDescription(pr.getDescription());
			po.setErpPrTransferred(Boolean.FALSE);
			po.setGrandTotal(pr.getGrandTotal());
			po.setIsPoReportSent(Boolean.FALSE);
			po.setName(pr.getName());
			if (pr.getPaymentTermes() != null) {
				po.setPaymentTerm(pr.getPaymentTerm());
				po.setPaymentTermDays(pr.getPaymentTermDays());
				po.setPaymentTermes(pr.getPaymentTermes());
			} else {
				po.setPaymentTerm(pr.getPaymentTerm());
			}
			po.setReferenceNumber(pr.getReferenceNumber());
			po.setRemarks(pr.getRemarks());
			po.setRequester(pr.getRequester());
			//PH4113 to PO Approval
			po.setStatus(PoStatus.DRAFT);
			if (Boolean.TRUE == isAutoPublishPo) {
				//po.setStatus(PoStatus.ORDERED);
				po.setOrderedBy(loggedInUser);
				po.setOrderedDate(new Date());
			} else {
				//po.setStatus(PoStatus.READY);
			}
			if (pr.getSupplier() != null) {
				if (pr.getSupplier().getSupplier() != null) {
					pr.getSupplier().getSupplier().getCompanyName();
					po.setSupplierName(StringUtils.checkString(pr.getSupplier().getSupplier().getCompanyName()));
					po.setSupplierAddress(StringUtils.checkString(pr.getSupplier().getSupplier().getCity()));
					po.setSupplierTelNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getMobileNumber()));
					po.setSupplierTaxNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getTaxRegistrationNumber()));
					po.setSupplierFaxNumber(StringUtils.checkString(pr.getSupplier().getSupplier().getFaxNumber()));
				}
				po.setSupplier(pr.getSupplier());

			}else {
				po.setSupplierName(pr.getSupplierName());
				po.setSupplierAddress(pr.getSupplierAddress());
				po.setSupplierTelNumber(pr.getSupplierTelNumber());
				po.setSupplierAddress(pr.getSupplierAddress());
				po.setSupplierTaxNumber(pr.getSupplierTaxNumber());
				po.setSupplierFaxNumber(pr.getSupplierFaxNumber());
			}
			po.setTaxDescription(pr.getTaxDescription());
			po.setTermsAndConditions(pr.getTermsAndConditions());
			po.setTotal(pr.getTotal());
			if (pr.getAdditionalTax() != null) {
				po.setAdditionalTax(pr.getAdditionalTax());
			}
			po.setGrandTotal(pr.getGrandTotal());
			po.setUrgentPo(Boolean.FALSE);
			po.setPr(pr);

			po.setEnableApprovalReminder(pr.getEnableApprovalReminder());
			po.setReminderAfterHour(pr.getReminderAfterHour());
			po.setReminderCount(pr.getReminderCount());
			po.setNotifyEventOwner(pr.getNotifyEventOwner());

			// copy approval
			//PH-PG-4113 no.20 PO Approval should not carry out from PR
			if (CollectionUtil.isNotEmpty(pr.getTemplate().getPoApprovals())) {
				List<PoApproval> approvalList = new ArrayList<>();
				for (PoTemplateApproval poApproval : pr.getTemplate().getPoApprovals()) {
					PoApproval newPoApproval = new PoApproval();
					newPoApproval.setApprovalType(poApproval.getApprovalType());
					newPoApproval.setLevel(poApproval.getLevel());
					newPoApproval.setPo(po);
					if (CollectionUtil.isNotEmpty(poApproval.getApprovalUsers())) {
						List<PoApprovalUser> poApprovalList = new ArrayList<>();
						for (PoTemplateApprovalUser poApprovalUser : poApproval.getApprovalUsers()) {
							PoApprovalUser newPoApprovalUser = new PoApprovalUser();
							newPoApprovalUser.setApproval(newPoApproval);
							newPoApprovalUser.setUser(poApprovalUser.getUser());
							poApprovalList.add(newPoApprovalUser);
						}
						newPoApproval.setApprovalUsers(poApprovalList);
					}
					approvalList.add(newPoApproval);
				}
				po.setApprovals(approvalList);
				po.setEnableApprovalRoute(Boolean.TRUE);

			}

			List<PoTeamMember> memberList = new ArrayList<>();

			// add pr creator to po team member
			if(!pr.getCreatedBy().getId().equals(loggedInUser.getId())){
				PoTeamMember newPoTeamMember = new PoTeamMember();
				newPoTeamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				newPoTeamMember.setUser(pr.getCreatedBy());
				newPoTeamMember.setPo(po);
				memberList.add(newPoTeamMember);

			}

			//clone pr Team Member to Po Team Member
			if (CollectionUtil.isNotEmpty(pr.getPrTeamMembers())) {
				for (PrTeamMember prTeamMember : pr.getPrTeamMembers()) {
					if(!loggedInUser.getId().equals(prTeamMember.getUser().getId())){
						PoTeamMember newPoTeamMember = new PoTeamMember();
						newPoTeamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
						newPoTeamMember.setUser(prTeamMember.getUser());
						newPoTeamMember.setPo(po);
						memberList.add(newPoTeamMember);
					}
				}


			}
			po.setPoTeamMembers(memberList);

			//clone PO item from PR Item as default
			List<PoItem> poItems = new ArrayList<PoItem>();
			List<PrItem> itemList = prItemDao.getPrItemsbyId(pr.getId());
			if (CollectionUtil.isNotEmpty(itemList)) {
				for (PrItem prItem : itemList) {
					if (prItem.getParent() == null) {
						LOG.info("pr parent");
						PoItem parent = new PoItem();
						parent.setItemName(prItem.getItemName());
						parent.setLevel(prItem.getLevel());
						parent.setOrder(prItem.getOrder());
						parent.setDeliveryReceiver(pr.getDeliveryReceiver());
						parent.setBuyer(prItem.getBuyer());
						parent.setPo(po);
						parent.setItemDescription(prItem.getItemDescription());
						List<PoItem> childrenList = new ArrayList<PoItem>();
						if (CollectionUtil.isNotEmpty(prItem.getChildren())) {
							for (PrItem itemPojo : prItem.getChildren()) {
								LOG.info("Children not empty" + prItem.getChildren().size());
								PoItem item = new PoItem();
								item.setBusinessUnit(pr.getBusinessUnit());
								item.setBuyer(itemPojo.getBuyer());
								item.setCostCenter(pr.getCostCenter());
								item.setDeliveryAddress(pr.getDeliveryAddress());
								if (pr.getDeliveryAddress() != null) {
									item.setDeliveryAddressTitle(StringUtils.checkString(pr.getDeliveryAddress().getTitle()));
									item.setDeliveryAddressLine1(StringUtils.checkString(pr.getDeliveryAddress().getLine1()));
									item.setDeliveryAddressLine2(StringUtils.checkString(pr.getDeliveryAddress().getLine2()));
									item.setDeliveryAddressZip(StringUtils.checkString(pr.getDeliveryAddress().getZip()));
									item.setDeliveryAddressCity(StringUtils.checkString(pr.getDeliveryAddress().getCity()));
									item.setDeliveryAddressState(StringUtils.checkString(pr.getDeliveryAddress().getState().getStateName()));
									item.setDeliveryAddressCountry(StringUtils.checkString(pr.getDeliveryAddress().getState().getCountry().getCountryName()));
								}
								item.setDeliveryDate(pr.getDeliveryDate());
								item.setDeliveryReceiver(pr.getDeliveryReceiver());
								item.setFreeTextItemEntered(itemPojo.getFreeTextItemEntered());
								item.setItemDescription(itemPojo.getItemDescription());
								item.setItemName(itemPojo.getItemName());
								item.setItemTax(itemPojo.getItemTax());
								item.setLevel(itemPojo.getLevel());
								item.setOrder(itemPojo.getOrder());
								item.setParent(parent);
								item.setPo(po);
								item.setProduct(itemPojo.getProduct());
								item.setProductCategory(itemPojo.getProductCategory());
								item.setQuantity(itemPojo.getQuantity());

								//PH-4113 set default to new colum in PO
								item.setLockedQuantity(BigDecimal.ZERO);
								item.setBalanceQuantity(BigDecimal.ZERO);

								item.setTaxAmount(itemPojo.getTaxAmount());
								item.setTotalAmount(itemPojo.getTotalAmount());
								item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax());
								item.setUnit(itemPojo.getUnit());
								item.setUnitPrice(itemPojo.getUnitPrice());
								//PH-4113 set default to new colum in PO
								item.setPricePerUnit(BigDecimal.valueOf(1));

								childrenList.add(item);
							}
							parent.setChildren(childrenList);
							poItems.add(parent);
						}
					}

				}
			} else {
				throw new ApplicationException("Missing PO Items");
			}

			List<PrDocument> listDocs = findAllPrDocsbyPrId(pr.getId());
			if(CollectionUtil.isNotEmpty(listDocs)) {
				List<PurchaseOrderDocument> docs = new ArrayList<PurchaseOrderDocument>();
				for(PrDocument doc : listDocs) {
					LOG.info("File Name " + doc.getFileName());
					PurchaseOrderDocument poDocs = new PurchaseOrderDocument();
					poDocs.setCredContentType(doc.getCredContentType());
					poDocs.setDescription(doc.getDescription());
					poDocs.setFileData(doc.getFileData());
					poDocs.setFileName(doc.getFileName());
					poDocs.setFileSize(doc.getFileSize());
					poDocs.setFileSizeInKb(doc.getFileSizeInKb());
					poDocs.setInternal(Boolean.TRUE);
					poDocs.setUploadDate(doc.getUploadDate());
					poDocs.setPo(po);
					docs.add(poDocs);
				}
				po.setPurchaseOrderDocuments(docs);
			}

			po.setPoItems(poItems);

			//PH4113
			po.setPoDetailCompleted(Boolean.TRUE);
			po.setDocumentCompleted(Boolean.FALSE);
			po.setSupplierCompleted(Boolean.FALSE);
			po.setDeliveryCompleted(Boolean.FALSE);
			po.setPoItemCompleted(Boolean.FALSE);
			po.setRemarkCompleted(Boolean.FALSE);
			po.setSummaryCompleted(Boolean.FALSE);

			po = poService.savePo(po);
			updatePoAudit(po, loggedInUser);
			poService.savePoPdf(po);
		}
		return po;
	}

	public void updatePoAudit(Po po, User loggedInUser) {
		try {

			po = poService.getLoadedPoById(po.getId());
			PoAudit readyAudit = new PoAudit();
			readyAudit.setAction(PoAuditType.CREATE);
			readyAudit.setActionBy(loggedInUser);
			readyAudit.setActionDate(new Date());
			readyAudit.setBuyer(po.getBuyer());
			if (po.getSupplier() != null) {
				readyAudit.setSupplier(po.getSupplier().getSupplier());
			}
			readyAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
			String prId = "";
			if(po.getPr()!=null){
				prId = po.getPr().getPrId();
			}
			readyAudit.setDescription(messageSource.getMessage("po.audit.ready", new Object[] { po.getPoNumber(), prId }, Global.LOCALE));
			readyAudit.setPo(po);
			poAuditService.save(readyAudit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PO '" + po.getPoNumber() + "' Created from PR '" + prId + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			PrAudit prAudit = new PrAudit();
			prAudit.setAction(PrAuditType.CREATE);
			prAudit.setActionBy(loggedInUser);
			prAudit.setActionDate(new Date());
			prAudit.setBuyer(po.getBuyer());
			prAudit.setDescription(messageSource.getMessage("po.audit.create", new Object[] { po.getPoNumber() }, Global.LOCALE));
			prAudit.setPr(po.getPr());
			prAuditService.save(prAudit);

			if (po.getStatus() == PoStatus.ORDERED) {
				PoAudit orderedAudit = new PoAudit();
				orderedAudit.setAction(PoAuditType.ORDERED);
				orderedAudit.setActionBy(loggedInUser);
				orderedAudit.setActionDate(new Date());
				orderedAudit.setBuyer(po.getBuyer());
				if (po.getSupplier() != null) {
					orderedAudit.setSupplier(po.getSupplier().getSupplier());
					orderedAudit.setDescription(messageSource.getMessage("po.audit.ordered", new Object[] { po.getPoNumber(), po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : "" }, Global.LOCALE));

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ORDERED, "PO '"+po.getPoNumber()+"' sent to Supplier " +(po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : ""), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} else {
					orderedAudit.setDescription(messageSource.getMessage("po.opensupplier.audit.ordered", new Object[] { po.getPoNumber(), StringUtils.checkString(po.getSupplierName()).length() > 0 ? po.getSupplierName() : "" }, Global.LOCALE));
				}
				orderedAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				orderedAudit.setPo(po);
				poAuditService.save(orderedAudit);

				if (po.getSupplier() != null) {
					PoAudit receivedAudit = new PoAudit();
					receivedAudit.setAction(PoAuditType.RECEIVED);
					receivedAudit.setActionBy(loggedInUser);
					receivedAudit.setActionDate(new Date());
					receivedAudit.setBuyer(po.getBuyer());
					if (po.getSupplier() != null) {
						receivedAudit.setSupplier(po.getSupplier().getSupplier());
					}
					receivedAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
					receivedAudit.setDescription(messageSource.getMessage("po.audit.received", new Object[] { po.getPoNumber(), po.getBuyer().getCompanyName() }, Global.LOCALE));
					receivedAudit.setPo(po);
					poAuditService.save(receivedAudit);

//					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RECEIVED, "PO '"+po.getPoNumber()+"' received from '"+po.getBuyer().getCompanyName()+ "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.PR);
//					buyerAuditTrailDao.save(buyerAuditTrail);
				}
			}

		} catch (Exception e) {
			LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
		}

	}

	@Override
	public Pr findSupplierByFavSupplierId(String id) {
		return prDao.findSupplierByFavSupplierId(id);
	}

	@Override
	public void downloadCsvFileForPrList(HttpServletResponse response, File file, String[] prIds, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo, boolean select_all, String tenantId, String userId, List<PrStatus> statuses, HttpSession session,String prType,String status) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.PR_REPORT_EXCEL_COLUMNS;

			String[] columns = new String[] { "srNo", "referenceNumber", "name", "prSupplier", "description", "prId", "createdByName", "prCreatedDateStr", "approvedByName", "prApprovedDateStr", "currencyName", "grandTotal", "unitName", "poNumber" };

			long count = prDao.findTotalNotDraftPrList(tenantId, userId, statuses,null,prType);
			LOG.info("....>>>>>>>>>>>> count " + count);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<PrPojo> list = findPrForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, prIds, searchFilterPrPojo, select_all, startDate, endDate, userId,prType,status);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (PrPojo pojo : list) {

					if (pojo.getPrCreatedDate() != null) {
						pojo.setPrCreatedDateStr(sdf.format(pojo.getPrCreatedDate()));
					}
					if (pojo.getPrApprovedDate() != null) {
						pojo.setPrApprovedDateStr(sdf.format(pojo.getPrApprovedDate()));
					}

					beanWriter.write(pojo, columns, processors);


				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.info("Error ..." + e, e);
		}

	}

	private CellProcessor[] getProcessors() {
		final CellProcessor[] processor = new CellProcessor[] {

				new NotNull(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // DESCRIPTION
				new NotNull(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // PR APPROVED DATE
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new NotNull(), //
		};

		return processor;
	}

	private List<PrPojo>  findPrForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] prIds, SearchFilterPrPojo searchFilterPrPojo, boolean select_all, Date startDate, Date endDate, String userId,String prType,String status) {

		List<String> bizUnitIds =null;
		bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		List<Pr> prList = prDao.findPrForTenantIdForCsv(tenantId, pageSize, pageNo, prIds, searchFilterPrPojo, select_all, startDate, endDate, userId,bizUnitIds,prType,status);

		List<PrPojo> prPojoList = new ArrayList<>();
		int r = 1;
		for (Pr pr : prList) {

			PrPojo prpojo = new PrPojo();
			prpojo.setSrNo(r++);
			prpojo.setId(pr.getId());
			prpojo.setCreatedByName(pr.getCreatedBy().getName());
			prpojo.setPrCreatedDate(pr.getPrCreatedDate());
			prpojo.setUnitName(pr.getBusinessUnit().getUnitName());
			prpojo.setDescription(pr.getDescription());
			prpojo.setName(pr.getName());
			prpojo.setPoNumber(pr.getPoNumber() !=null ? pr.getPoNumber() : "");
			prpojo.setGrandTotal(pr.getGrandTotal());
			prpojo.setPrId(pr.getPrId());
			prpojo.setReferenceNumber(pr.getReferenceNumber());
			prpojo.setPrSupplier((pr.getSupplier() != null) ? pr.getSupplier().getFullName() : pr.getSupplierName());
			prpojo.setSupplierName(pr.getSupplierName());
			prpojo.setSupplier(pr.getSupplier());
			prpojo.setCurrencyName(pr.getCurrency().getCurrencyCode());
			for (PrApproval prAprovar : getAllPrApprovalsByPrId(pr.getId())) {
				List<PrApprovalUser> prApprovalUsers = prAprovar.getApprovalUsers();
				if (CollectionUtil.isNotEmpty(prApprovalUsers)) {

					PrApprovalUser prApprovalUser = prApprovalUsers.get(0);
					if (prApprovalUser.getApprovalStatus() == ApprovalStatus.APPROVED) {
						prpojo.setApprovedByName(prApprovalUser.getUser().getName());
						prpojo.setPrApprovedDate(prApprovalUser.getActionDate());
					}

				}
			}
			prPojoList.add(prpojo);
		}

		return prPojoList;
	}

	@Override
	public long getPrCountBySearchValueForTenant(String searchValue, String tenantId, String userId) {
		return prDao.getPrCountBySearchValueForTenant(searchValue, tenantId, userId);
	}

	@Override
	public List<Pr> findPrByNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId) {
		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		LOG.info(" start  : " + start);
		List<Pr> prList = prDao.findPrByNameForTenantId(searchValue, pageNo, pageLength, start, tenantId, userId);

		if (CollectionUtil.isNotEmpty(prList)) {
			for (Pr pr : prList) {
				pr.setModifiedBy(null);
				if (pr.getTemplate() != null) {
					if (pr.getTemplate().getStatus() == Status.INACTIVE) {

						pr.setTemplateActive(true);
					} else {
						pr.setTemplateActive(false);
					}

				}

			}
		}
		return prList;
	}

	@Override
	@Transactional(readOnly = false)
	public long doMultipleApproval(String[] prIds, User loggedInUser, String remarks, boolean isApproved) throws Exception {
		try {
			List<String> list = Arrays.asList(prIds);
			long approvedCount = 0;
			for (String prId : list) {
				Pr pr = prDao.findPrForApprovalById(prId);

				Pr approvedPr = approvalService.doApproval(pr, loggedInUser, remarks, isApproved);
				approvedCount++;
			}
			return approvedCount;

		} catch (Exception e) {
			LOG.info("Error is... " + e.getMessage(), e);
			return 0;
		}
	}
	@Override
	public Pr findPrbyprId(String prId){
		return prDao.findPrByPrId(prId);
	}

}
