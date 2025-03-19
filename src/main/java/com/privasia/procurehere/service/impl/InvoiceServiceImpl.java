package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.InvoiceAuditDao;
import com.privasia.procurehere.core.dao.InvoiceDao;
import com.privasia.procurehere.core.dao.InvoiceFinanceRequestDao;
import com.privasia.procurehere.core.dao.InvoiceItemDao;
import com.privasia.procurehere.core.dao.InvoiceReportDao;
import com.privasia.procurehere.core.dao.OnboardedBuyerDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoFinanceRequestDao;
import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.dao.SettingsDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.entity.InvoiceReport;
import com.privasia.procurehere.core.entity.OnboardedBuyer;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Settings;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.enums.InvoiceAuditType;
import com.privasia.procurehere.core.enums.InvoiceAuditVisibilityType;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.FinansHereInvoiceRequestPojo;
import com.privasia.procurehere.core.pojo.FinansHereResponsePojo;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.InvoiceService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.web.controller.InvoiceItemsSummaryPojo;
import com.privasia.procurehere.web.controller.InvoiceSummaryPojo;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Value("${financehere.api.url}")
	String finansHereApiUrl;

	@Value("${financehere.api.key}")
	String finansHereApiKey;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	InvoiceItemDao invoiceItemDao;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	PoDao poDao;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	InvoiceAuditDao invoiceAuditDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	InvoiceFinanceRequestDao invoiceFinanceRequestDao;

	@Autowired
	PoFinanceRequestDao poFinanceRequestDao;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	InvoiceReportDao invoiceReportDao;

	@Autowired
	SettingsDao settingsDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Override
	public List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return invoiceDao.findAllSearchFilterInvoiceForSupplier(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return invoiceDao.findTotalSearchFilterInvoiceForSupplier(tenantId, input, startDate, endDate);

	}

	@Override
	public long findTotalInvoiceForSupplier(String tenantId) {
		return invoiceDao.findTotalInvoiceForSupplier(tenantId);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public Invoice createInvoice(User loggedInUser, Po po) throws ApplicationException, EmailException {
		Invoice invoice = null;
		if (po != null) {
			po = poDao.findById(po.getId());

			invoice = new Invoice();
			invoice.setInvoiceId(eventIdSettingsService.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "INV", null));
			invoice.setBuyer(po.getBuyer());
			invoice.setBusinessUnit(po.getBusinessUnit());
			if (StringUtils.checkString(po.getLine1()).length() > 0) {
				invoice.setLine1(po.getLine1());
				invoice.setLine2(po.getLine2());
				invoice.setLine3(po.getLine3());
				invoice.setLine4(po.getLine4());
				invoice.setLine5(po.getLine5());
				invoice.setLine6(po.getLine6());
				invoice.setLine7(po.getLine7());

			} else {
				invoice.setLine1(StringUtils.checkString(po.getLine1()).length() > 0 ? po.getLine1() : po.getBusinessUnit().getLine1());
				invoice.setLine2(StringUtils.checkString(po.getLine2()).length() > 0 ? po.getLine2() : po.getBusinessUnit().getLine2());
				invoice.setLine3(StringUtils.checkString(po.getLine3()).length() > 0 ? po.getLine3() : po.getBusinessUnit().getLine3());
				invoice.setLine4(StringUtils.checkString(po.getLine4()).length() > 0 ? po.getLine4() : po.getBusinessUnit().getLine4());
				invoice.setLine5(StringUtils.checkString(po.getLine5()).length() > 0 ? po.getLine5() : po.getBusinessUnit().getLine5());
				invoice.setLine6(StringUtils.checkString(po.getLine6()).length() > 0 ? po.getLine6() : po.getBusinessUnit().getLine6());
				invoice.setLine7(StringUtils.checkString(po.getLine7()).length() > 0 ? po.getLine7() : po.getBusinessUnit().getLine7());
			}

			invoice.setCorrespondenceAddress(po.getCorrespondenceAddress());
			if (po.getCorrespondenceAddress() != null) {
				invoice.setCorrespondAddressTitle(StringUtils.checkString(po.getCorrespondenceAddress().getTitle()));
				invoice.setCorrespondAddressLine1(StringUtils.checkString(po.getCorrespondenceAddress().getLine1()));
				invoice.setCorrespondAddressLine2(StringUtils.checkString(po.getCorrespondenceAddress().getLine2()));
				invoice.setCorrespondAddressZip(StringUtils.checkString(po.getCorrespondenceAddress().getZip()));
				invoice.setCorrespondAddressCity(StringUtils.checkString(po.getCorrespondenceAddress().getCity()));
				invoice.setCorrespondAddressState(StringUtils.checkString(po.getCorrespondenceAddress().getState().getStateName()));
				invoice.setCorrespondAddressCountry(StringUtils.checkString(po.getCorrespondenceAddress().getState().getCountry().getCountryName()));
			}
			if (po.getDeliveryAddress() != null) {
				invoice.setDeliveryAddressTitle(StringUtils.checkString(po.getDeliveryAddress().getTitle()));
				invoice.setDeliveryAddressLine1(StringUtils.checkString(po.getDeliveryAddress().getLine1()));
				invoice.setDeliveryAddressLine2(StringUtils.checkString(po.getDeliveryAddress().getLine2()));
				invoice.setDeliveryAddressZip(StringUtils.checkString(po.getDeliveryAddress().getZip()));
				invoice.setDeliveryAddressCity(StringUtils.checkString(po.getDeliveryAddress().getCity()));
				invoice.setDeliveryAddressState(StringUtils.checkString(po.getDeliveryAddress().getState().getStateName()));
				invoice.setDeliveryAddressCountry(StringUtils.checkString(po.getDeliveryAddress().getState().getCountry().getCountryName()));
			}
			invoice.setCostCenter(po.getCostCenter());
			invoice.setCreatedBy(loggedInUser);
			invoice.setCreatedDate(new Date());
			invoice.setCurrency(po.getCurrency());
			invoice.setDecimal(po.getDecimal());
			invoice.setDeliveryAddress(po.getDeliveryAddress());
			// invoice.setDeliveryDate(po.getDeliveryDate());
			invoice.setDeliveryReceiver(po.getDeliveryReceiver());
			invoice.setDescription(po.getDescription());
			invoice.setGrandTotal(po.getGrandTotal());
			invoice.setName(po.getName());
			invoice.setReferenceNumber(po.getReferenceNumber());
			invoice.setRemarks(po.getRemarks());
			invoice.setRequester(po.getRequester());
			invoice.setStatus(InvoiceStatus.DRAFT);
			if (po.getPaymentTermes() != null) {
				invoice.setPaymentTerm(po.getPaymentTerm());
				invoice.setPaymentTermDays(po.getPaymentTermDays());
				invoice.setPaymentTermes(po.getPaymentTermes());
			} else {
				invoice.setPaymentTerm(po.getPaymentTerm());
			}

			if (po.getSupplier() != null) {
				if (po.getSupplier().getSupplier() != null) {
					invoice.setSupplier(po.getSupplier().getSupplier());
					po.getSupplier().getSupplier().getCompanyName();
				}
			}

			invoice.setSupplierName(po.getSupplierName());
			invoice.setSupplierAddress(po.getSupplierAddress());
			invoice.setSupplierTelNumber(po.getSupplierTelNumber());
			invoice.setSupplierAddress(po.getSupplierAddress());
			invoice.setSupplierTaxNumber(po.getSupplierTaxNumber());
			invoice.setSupplierFaxNumber(po.getSupplierFaxNumber());
			invoice.setTaxDescription(po.getTaxDescription());
			invoice.setTermsAndConditions(po.getTermsAndConditions());
			invoice.setTotal(po.getTotal());
			if (po.getAdditionalTax() != null) {
				invoice.setAdditionalTax(po.getAdditionalTax());
			}
			invoice.setGrandTotal(po.getGrandTotal());
			invoice.setPo(po);

			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			List<PoItem> itemList = poItemDao.getAllPoItemByPoId(po.getId());
			if (CollectionUtil.isNotEmpty(itemList)) {
				for (PoItem poItem : itemList) {
					if (poItem.getParent() == null) {
						// LOG.info("pr parent");
						InvoiceItem parent = new InvoiceItem();
						parent.setItemName(poItem.getItemName());
						parent.setLevel(poItem.getLevel());
						parent.setOrder(poItem.getOrder());
						parent.setDeliveryReceiver(po.getDeliveryReceiver());
						parent.setBuyer(poItem.getBuyer());
						parent.setInvoice(invoice);
						parent.setItemDescription(poItem.getItemDescription());
						List<InvoiceItem> childrenList = new ArrayList<InvoiceItem>();
						if (CollectionUtil.isNotEmpty(poItem.getChildren())) {
							for (PoItem itemPojo : poItem.getChildren()) {
								LOG.info("Children not empty" + poItem.getChildren().size());
								InvoiceItem item = new InvoiceItem();
								item.setBusinessUnit(po.getBusinessUnit());
								item.setBuyer(itemPojo.getBuyer());
								item.setCostCenter(po.getCostCenter());
								item.setDeliveryAddress(po.getDeliveryAddress());
								if (po.getDeliveryAddress() != null) {
									item.setDeliveryAddressTitle(StringUtils.checkString(po.getDeliveryAddress().getTitle()));
									item.setDeliveryAddressLine1(StringUtils.checkString(po.getDeliveryAddress().getLine1()));
									item.setDeliveryAddressLine2(StringUtils.checkString(po.getDeliveryAddress().getLine2()));
									item.setDeliveryAddressZip(StringUtils.checkString(po.getDeliveryAddress().getZip()));
									item.setDeliveryAddressCity(StringUtils.checkString(po.getDeliveryAddress().getCity()));
									item.setDeliveryAddressState(StringUtils.checkString(po.getDeliveryAddress().getState().getStateName()));
									item.setDeliveryAddressCountry(StringUtils.checkString(po.getDeliveryAddress().getState().getCountry().getCountryName()));
								}
								item.setDeliveryDate(po.getDeliveryDate());
								item.setDeliveryReceiver(po.getDeliveryReceiver());
								item.setFreeTextItemEntered(itemPojo.getFreeTextItemEntered());
								item.setItemDescription(itemPojo.getItemDescription());
								item.setItemName(itemPojo.getItemName());
								item.setItemTax(StringUtils.checkString(itemPojo.getItemTax()).length() > 0 ? new BigDecimal(itemPojo.getItemTax()) : BigDecimal.ZERO);
								item.setLevel(itemPojo.getLevel());
								item.setOrder(itemPojo.getOrder());
								item.setParent(parent);
								item.setInvoice(invoice);
								item.setQuantity(itemPojo.getQuantity());
								item.setTaxAmount(itemPojo.getTaxAmount());
								item.setTotalAmount(itemPojo.getTotalAmount());
								item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax());
								item.setUnit(itemPojo.getUnit());
								item.setUnitPrice(itemPojo.getUnitPrice());
								if (itemPojo.getPo() != null) {
									item.setPo(itemPojo.getPo());
									item.setPoItem(itemPojo);
								}
								childrenList.add(item);
							}
							parent.setChildren(childrenList);
							invoiceItems.add(parent);
						}
					}

				}
			} else {
				throw new ApplicationException("Missing PO Items");
			}

			if (CollectionUtil.isNotEmpty(invoiceItems)) {
				invoice.setInvoiceItems(invoiceItems);
				invoice = invoiceDao.save(invoice);

				// Update Invoice count in PO
				po.setInvoiceCount(((int) invoiceDao.findTotalInvoiceForPo(po.getId())));
				poDao.update(po);

				InvoiceAudit audit = new InvoiceAudit();
				audit.setAction(InvoiceAuditType.CREATE);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				audit.setBuyer(po.getBuyer());
				Supplier supplier = new Supplier();
				supplier.setId(loggedInUser.getTenantId());
				audit.setSupplier(supplier);
				audit.setVisibilityType(InvoiceAuditVisibilityType.SUPPLIER);
				audit.setDescription(messageSource.getMessage("invoice.audit.ready", new Object[] { po.getPoNumber() }, Global.LOCALE));
				audit.setInvoice(invoice);
				invoiceAuditDao.save(audit);

			} else {
				LOG.warn("NO Items found ..");
			}

		}
		return invoice;
	}

	@Override
	public Invoice getInvoiceByIdForSupplierView(String invoiceId) {
		Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);
		if (invoice != null) {
			if (invoice.getBuyer() != null) {
				invoice.getBuyer().getCompanyName();
			}
			if (invoice.getSupplier() != null) {
				invoice.getSupplier().getCompanyName();
				invoice.getSupplier().getFaxNumber();
			}
			if (invoice.getCreatedBy() != null) {
				invoice.getCreatedBy().getName();
				invoice.getCreatedBy().getCommunicationEmail();
				invoice.getCreatedBy().getPhoneNumber();
			}
			if (invoice.getPo() != null) {
				invoice.getPo().getPoNumber();
			}
			if (invoice.getCurrency() != null) {
				invoice.getCurrency().getCurrencyCode();
			}
			if (invoice.getBusinessUnit() != null) {
				invoice.getBusinessUnit().getUnitName();
			}
			if (invoice.getFooter() != null) {
				invoice.getFooter().getContent();
			}

		}
		return invoice;
	}

	@Override
	public List<InvoiceItem> findAllInvoiceItemByInvoiceIdForSummary(String invoiceId) {
		return invoiceItemDao.getAllInvoiceItemByInvoiceId(invoiceId);
	}

	@Override
	public List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return invoiceDao.findAllSearchFilterInvoiceForBuyer(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return invoiceDao.findTotalSearchFilterInvoiceForBuyer(tenantId, input, startDate, endDate);

	}

	@Override
	public long findTotalInvoiceForBuyer(String tenantId) {
		return invoiceDao.findTotalInvoiceForBuyer(tenantId);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public Invoice declineInvoice(String invoiceId, User loggedInUser, String buyerRemark) throws EmailException {

		Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);

		if (invoice != null) {
			invoice.setStatus(InvoiceStatus.DECLINED);
			invoice.setActionBy(loggedInUser);
			invoice.setActionDate(new Date());
			invoiceDao.update(invoice);

			InvoiceAudit buyerAudit = new InvoiceAudit();
			buyerAudit.setAction(InvoiceAuditType.DECLINED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(invoice.getBuyer());
			buyerAudit.setSupplier(invoice.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("invoice.audit.declined", new Object[] { buyerRemark }, Global.LOCALE));
			buyerAudit.setVisibilityType(InvoiceAuditVisibilityType.BOTH);
			buyerAudit.setInvoice(invoice);
			invoiceAuditDao.save(buyerAudit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DECLINED, "Invoice '" + invoice.getInvoiceId() + "' is declined", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.Invoice);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			try {
				sendEmailNotificationToSupplier(invoice, loggedInUser, false, buyerRemark);
			} catch (Exception e) {
				LOG.error("Error while sending invoice decline mail to supplier " + invoice.getSupplierName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending invoice decline mail to supplier");
			}
		}

		return invoice;
	}

	@Autowired
	OnboardedBuyerDao onboardedBuyerDao;

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public Invoice acceptInvoice(String invoiceId, User loggedInUser, String buyerRemark) throws EmailException, ApplicationException {

		Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);

		if (invoice != null) {
			invoice.setStatus(InvoiceStatus.ACCEPTED);
			invoice.setActionBy(loggedInUser);
			invoice.setActionDate(new Date());
			invoiceDao.update(invoice);

			// if Finance Request is present, raise the request.
			if (Boolean.TRUE == invoice.getRequestForFinance()) {
				OnboardedBuyer ob = onboardedBuyerDao.getOnboardedBuyerByBuyerId(invoice.getBuyer().getId());
				if (ob != null) {

					try {
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
						headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
						headers.set("AUTH-KEY", finansHereApiKey);

						FinansHereInvoiceRequestPojo request = new FinansHereInvoiceRequestPojo();
						request.setBuyerId(invoice.getBuyer().getId());
						request.setBuyerName(invoice.getBuyer().getCompanyName());
						request.setBuyerRoc(invoice.getBuyer().getCompanyRegistrationNumber());
						request.setCurrencyCode(invoice.getCurrency().getCurrencyCode());
						request.setDecimal(invoice.getDecimal());
						request.setPaymentTerms(invoice.getPaymentTermDays() != null ? invoice.getPaymentTermDays() : 30);
						request.setInvoiceAmount(invoice.getGrandTotal());
						request.setInvoiceDate(invoice.getInvoiceSendDate());
						request.setInvoiceId(invoice.getId());
						request.setInvoiceTitle(invoice.getName());
						request.setInvoiceNumber(invoice.getInvoiceId());
						request.setPoNumber(invoice.getPo().getPoNumber());
						request.setPoId(invoice.getPo().getId());
						request.setSupplierId(invoice.getSupplier().getId());
						request.setSupplierName(invoice.getSupplierName());
						request.setSupplierRoc(invoice.getSupplier().getCompanyRegistrationNumber());
						request.setActionBy(loggedInUser.getId());

						LOG.info("Request values >> : " + request.toLogString());

						MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
						Map<String, String> maps = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {
						});
						LOG.info("Data values : " + Arrays.asList(maps));
						parameters.setAll(maps);

						HttpEntity<MultiValueMap<String, String>> poRequest = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

						ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
						RestTemplate restTemplate = new RestTemplate(factory);
						restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
						restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
						restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

						FinansHereResponsePojo res = restTemplate.postForObject(finansHereApiUrl + "create/invoice", poRequest, FinansHereResponsePojo.class);

						if (StringUtils.checkString(res.getStatus()).equalsIgnoreCase("OK")) {
							LOG.info("Invoice Finansing request sent successfully to FinansHere : " + res.getMessage());
						} else {
							LOG.error("Error from Finanshere API : " + res.getMessage());
							// throw new ApplicationException("Error during requesting for Invoice Financing : " +
							// res.getMessage());
						}

						// } catch (ApplicationException e) {
						// LOG.error("Error during requesting for Invoice Financing : " + e.getMessage(), e);
						// throw e;
					} catch (Exception e) {
						LOG.error("Error while raising financing request. Error : " + e.getMessage(), e);
						// throw new ApplicationException("Error while raising financing request. Error : " +
						// e.getMessage());
					}

				} else {
					LOG.error("Buyer is not onboarded in Finanshere hence not inserting request for Invoice: " + invoiceId + " Buyer Id: " + invoice.getBuyer().getId());
				}
			}

			InvoiceAudit invoiceAudit = new InvoiceAudit();
			invoiceAudit.setAction(InvoiceAuditType.ACCEPTED);
			invoiceAudit.setActionBy(loggedInUser);
			invoiceAudit.setActionDate(new Date());
			invoiceAudit.setBuyer(invoice.getBuyer());
			invoiceAudit.setSupplier(invoice.getSupplier());
			invoiceAudit.setDescription(messageSource.getMessage("invoice.audit.accepted", new Object[] { buyerRemark }, Global.LOCALE));
			invoiceAudit.setVisibilityType(InvoiceAuditVisibilityType.BOTH);
			invoiceAudit.setInvoice(invoice);
			invoiceAuditDao.save(invoiceAudit);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Invoice '" + invoice.getInvoiceId() + "' Accepted", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.Invoice);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			try {
				sendEmailNotificationToSupplier(invoice, loggedInUser, true, buyerRemark);
			} catch (Exception e) {
				LOG.error("Error while sending invoice accept mail to supplier " + invoice.getSupplierName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending invoice accept mail to supplier");
			}
		}

		return invoice;
	}

	@Override
	@Transactional(readOnly = false)
	public Invoice finishInvoice(String invoiceId, User loggedInUser) {

		Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);

		invoice.setStatus(InvoiceStatus.INVOICED);
		invoiceDao.update(invoice);

		try {

			InvoiceAudit buyerAudit = new InvoiceAudit();
			buyerAudit.setAction(InvoiceAuditType.INVOICED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(invoice.getBuyer());
			buyerAudit.setSupplier(invoice.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("invoice.finish.notification.message", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
			buyerAudit.setVisibilityType(InvoiceAuditVisibilityType.BOTH);
			buyerAudit.setInvoice(invoice);
			invoiceAuditDao.save(buyerAudit);

		} catch (Exception e) {
			LOG.error("Error while Finihing Delivery order : " + e.getMessage(), e);
		}

		return invoice;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public Invoice cancelInvoice(String invoiceId, User loggedInUser, String supplierRemark) throws EmailException {

		Invoice invoice = invoiceDao.findByInvoiceId(invoiceId);
		if (invoice != null) {
			if (invoice.getBuyer() != null && invoice.getBuyer().getCompanyName() != null) {
				invoice.getBuyer().getCompanyName();
			}
			InvoiceStatus status = invoice.getStatus();
			invoice.setStatus(InvoiceStatus.CANCELLED);
			invoiceDao.update(invoice);

			Po po = invoice.getPo();
			if (po.getInvoiceCancelCount() == null) {
				po.setInvoiceCancelCount(1);
			} else {
				po.setInvoiceCancelCount(po.getInvoiceCancelCount() + 1);
			}
			poDao.saveOrUpdate(po);

			InvoiceAudit buyerAudit = new InvoiceAudit();
			buyerAudit.setAction(InvoiceAuditType.CANCELLED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(invoice.getBuyer());
			buyerAudit.setSupplier(invoice.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("invoice.audit.cancel", new Object[] { supplierRemark }, Global.LOCALE));
			if (InvoiceStatus.INVOICED == status) {
				buyerAudit.setVisibilityType(InvoiceAuditVisibilityType.BOTH);
			} else {
				buyerAudit.setVisibilityType(InvoiceAuditVisibilityType.SUPPLIER);
			}
			buyerAudit.setInvoice(invoice);
			invoiceAuditDao.save(buyerAudit);

			// if (InvoiceStatus.INVOICED == status) {
			// try {
			// sendEmailNotificationToBuyer(invoice, loggedInUser, false, supplierRemark);
			// } catch (Exception e) {
			// LOG.error("Error while sending invoice cancel mail to buyer " + invoice.getBuyer().getCompanyName() + " :
			// " + e.getMessage(), e);
			// throw new EmailException("Error while sending invoice cancel mail to buyer");
			// }
			// }
		}

		return invoice;
	}

	private void getSummaryOfAddressAndInvoiceitems(Invoice invoice, DecimalFormat df, InvoiceSummaryPojo summary) {

		try {

			// Delivery Address
			String deliveryAddress = "";
			if (StringUtils.checkString(invoice.getDeliveryAddressTitle()).length() > 0) {
				deliveryAddress += invoice.getDeliveryAddressTitle() + "\n";
			}
			deliveryAddress += invoice.getDeliveryAddressLine1();
			if (invoice.getDeliveryAddressLine2() != null) {
				deliveryAddress += "\n" + invoice.getDeliveryAddressLine2();
			}
			deliveryAddress += "\n" + invoice.getDeliveryAddressZip() + ", " + invoice.getDeliveryAddressCity() + "\n";
			deliveryAddress += invoice.getDeliveryAddressState() + ", " + invoice.getDeliveryAddressCountry();

			summary.setDeliveryAddress(deliveryAddress);
			LOG.info("deliveryAddress:" + deliveryAddress);
			if (StringUtils.checkString(invoice.getDeliveryReceiver()).length() > 0) {
				summary.setDeliveryReceiver(invoice.getDeliveryReceiver());
			}
			summary.setIncludeDelievryAdress(invoice.isIncludeDelievryAdress());
			// Invoice items List
			List<InvoiceItemsSummaryPojo> invoiceItemList = new ArrayList<InvoiceItemsSummaryPojo>();
			List<InvoiceItem> invoiceList = findAllInvoiceItemByInvoiceId(invoice.getId());
			if (CollectionUtil.isNotEmpty(invoiceList)) {
				for (InvoiceItem item : invoiceList) {
					InvoiceItemsSummaryPojo pos = new InvoiceItemsSummaryPojo();
					pos.setSlno(item.getLevel() + "." + item.getOrder());
					pos.setItemName(item.getItemName());
					pos.setCurrency(item.getInvoice().getCurrency().getCurrencyCode());
					pos.setItemDescription(item.getItemDescription());
					pos.setAdditionalTax(df.format(item.getInvoice().getAdditionalTax()));
					pos.setGrandTotal(df.format(item.getInvoice().getGrandTotal()));
					pos.setSumAmount(df.format(invoice.getTotal()));
					pos.setTaxDescription(item.getInvoice().getTaxDescription());
					pos.setDecimal(invoice.getDecimal());
					invoiceItemList.add(pos);
					if (item.getChildren() != null) {
						for (InvoiceItem childItem : item.getChildren()) {
							InvoiceItemsSummaryPojo childPr = new InvoiceItemsSummaryPojo();
							childPr.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPr.setItemName(childItem.getItemName());
							childPr.setItemDescription(childItem.getItemDescription());
							childPr.setQuantity(childItem.getQuantity());
							childPr.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPr.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPr.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPr.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPr.setUom(childItem.getUnit() != null ? (childItem.getUnit().getUom() != null ? childItem.getUnit().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPr.setCurrency(childItem.getInvoice().getCurrency().getCurrencyCode());
							childPr.setAdditionalTax(df.format(childItem.getInvoice().getAdditionalTax()));
							childPr.setGrandTotal(df.format(childItem.getInvoice().getGrandTotal()));
							childPr.setSumAmount(df.format(invoice.getTotal()));
							childPr.setTaxDescription(StringUtils.checkString(childItem.getInvoice().getTaxDescription()).length() > 0 ? childItem.getInvoice().getTaxDescription() : "");
							childPr.setSumTaxAmount(childItem.getTaxAmount());
							childPr.setSumTotalAmt(childItem.getTotalAmount());
							childPr.setDecimal(invoice.getDecimal());
							invoiceItemList.add(childPr);
						}
					}

				}
			}
			summary.setInvoiceItems(invoiceItemList);
		} catch (Exception e) {
			LOG.error("Could not Get Invoice Items and Address " + e.getMessage(), e);
		}
	}

	@Override
	public List<InvoiceItem> findAllInvoiceItemByInvoiceId(String invoiceId) {
		List<InvoiceItem> returnList = new ArrayList<InvoiceItem>();
		List<InvoiceItem> list = invoiceItemDao.getAllInvoiceItemByInvoiceId(invoiceId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (InvoiceItem item : list) {
				InvoiceItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}

				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (InvoiceItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<InvoiceItem>());
						}
						if (child.getUnit() != null) {
							child.getUnit().getUom();
						}

						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;

	}

	@Override
	public Invoice findByInvoiceId(String invoiceId) {
		return invoiceDao.findById(invoiceId);
	}

	private JasperPrint getGeneratedInvoicePdf(Invoice invoice) {
		invoice = invoiceDao.findById(invoice.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		SupplierSettings supplierSettings = supplierSettingsDao.getSupplierSettingsByTenantId(invoice.getSupplier().getId());

		TimeZone timeZone = TimeZone.getDefault();

		if (supplierSettings != null && supplierSettings.getTimeZone() != null) {
			timeZone = TimeZone.getTimeZone(supplierSettings.getTimeZone().getTimeZone());
		} else {
			String timeZoneStr = "GMT+8:00";
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(invoice.getBuyer().getId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (invoice.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (invoice.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (invoice.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (invoice.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (invoice.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (invoice.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/InvoiceReport.jasper");
			File jasperfile = resource.getFile();

			InvoiceSummaryPojo summary = new InvoiceSummaryPojo();
			String createDate = invoice.getInvoiceSendDate() != null ? sdf.format(invoice.getInvoiceSendDate()).toUpperCase() : "";
			summary.setInvoiceName(invoice.getName());
			summary.setInvoiceId(invoice.getInvoiceId());
			summary.setPaymentNote("Hello");
			summary.setPoNumber(invoice.getPo().getPoNumber());
			summary.setCreatedDate(createDate);
			summary.setDueDate(invoice.getDueDate() != null ? sdf.format(invoice.getDueDate()).toUpperCase() : "N/A");

			PoFinanceRequestPojo poFinanceRequest = poFinanceRequestDao.getPoFinanceRequestPojoByPoId(invoice.getPo().getId());
			if (poFinanceRequest != null && (poFinanceRequest.getStatus() == FinanceRequestStatus.APPROVED || poFinanceRequest.getStatus() == FinanceRequestStatus.ACTIVE)) {
				summary.setFooter(null);
			} else {
				if (invoice.getFooter() != null) {
					summary.setFooter(invoice.getFooter().getContent());
				} else {
					summary.setFooter("");
				}
			}

			if (supplierSettings != null) {
				ImageIcon n;
				if (supplierSettings.getFileAttatchment() != null) {
					n = new ImageIcon(supplierSettings.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(invoice.getSupplier().getCompanyName());
				}
				summary.setLogo(n.getImage());
			}
			getSummaryOfAddressAndInvoiceitems(invoice, df, summary);

			BusinessUnit bUnit = invoice.getBusinessUnit();

			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				summary.setDisplayName(bUnit.getUnitName());
			}
			if (StringUtils.checkString(invoice.getLine1()).length() > 0) {
				buyerAddress = invoice.getLine1() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine2()).length() > 0) {
				buyerAddress += invoice.getLine2() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine3()).length() > 0) {
				buyerAddress += invoice.getLine3() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine4()).length() > 0) {
				buyerAddress += invoice.getLine4() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine5()).length() > 0) {
				buyerAddress += invoice.getLine5() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine6()).length() > 0) {
				buyerAddress += invoice.getLine6() + "\r\n";
			}
			if (StringUtils.checkString(invoice.getLine7()).length() > 0) {
				buyerAddress += invoice.getLine7() + "\r\n";
			}

			if (StringUtils.checkString(invoice.getAttentionTo()).length() > 0) {
				buyerAddress += "Attention: " + invoice.getAttentionTo() + "\r\n";
			} else {
				buyerAddress += "Attention: N/A" + "\r\n";
			}

			summary.setBuyerAddress(buyerAddress);

			List<InvoiceSummaryPojo> invoiceSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (invoice.getSupplier() != null) {
				Supplier supplier = invoice.getSupplier();
				supplierAddress += supplier.getCompanyName() + "\r\n";
				supplierAddress += supplier.getLine1();
				if (StringUtils.checkString(invoice.getSupplier().getLine2()).length() > 0) {
					supplierAddress += "\r\n" + supplier.getLine2();
				}
				supplierAddress += "\r\n" + supplier.getCity() + ", ";
				if (supplier.getState() != null) {
					supplierAddress += supplier.getState().getStateName() + "\r\n\n";
				}
				supplierAddress += "TEL : ";

				if (supplier.getCompanyContactNumber() != null) {
					supplierAddress += supplier.getCompanyContactNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (supplier.getFaxNumber() != null) {
					supplierAddress += supplier.getFaxNumber() + "\r\n\n";
				}
				supplierAddress += "Company Reg No : ";

				if (supplier.getCompanyRegistrationNumber() != null) {
					supplierAddress += supplier.getCompanyRegistrationNumber();
				}
				supplierAddress += "\r\nTax Reg No : ";
				if (supplier.getTaxRegistrationNumber() != null) {
					supplierAddress += supplier.getTaxRegistrationNumber();
				}

			} else {
				supplierAddress += invoice.getSupplierName() + "\r\n";
				supplierAddress += invoice.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (invoice.getSupplierTelNumber() != null) {
					supplierAddress += invoice.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (invoice.getSupplierFaxNumber() != null) {
					supplierAddress += invoice.getSupplierFaxNumber() + "\r\n\n";
				}

				supplierAddress += "Tax Reg No : ";
				if (invoice.getSupplierTaxNumber() != null) {
					supplierAddress += invoice.getSupplierTaxNumber();
				}
			}
			if (invoice.getSupplier() != null) {
				summary.setSupplierName(invoice.getSupplier() != null ? invoice.getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(invoice.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(invoice.getSupplierTaxNumber() != null ? invoice.getSupplierTaxNumber() : "");

			parameters.put("INVOICE_SUMMARY", invoiceSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(invoiceSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Invoice  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private String getTimeZoneBySupplierSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
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

	private void sendEmailWithcc(String mailTo, String mailBcc, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				notificationService.sendEmailWithcc(mailTo, mailBcc, subject, message);
			} catch (Exception e) {
				LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendEmailNotificationToSupplier(Invoice invoice, User actionBy, boolean isAccept, String buyerRemark) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat dueDatesdf = new SimpleDateFormat("dd/MM/yyyy");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(invoice.getCreatedBy().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String subject = null;
		if (isAccept) {
			subject = "Invoice Accepted";
		} else {
			subject = "Invoice Declined";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("supplierName", invoice.getSupplier().getCompanyName());
		map.put("invoiceNumber", invoice.getInvoiceId());
		map.put("invoiceDate", sdf.format(invoice.getInvoiceSendDate()));
		map.put("poNumber", invoice.getPo() != null ? invoice.getPo().getPoNumber() : "N/A");
		map.put("dueDate", invoice.getDueDate() != null ? dueDatesdf.format(invoice.getDueDate()) : dueDatesdf.format(new Date()));
		map.put("comments", buyerRemark);
		map.put("buyerCompany", invoice.getBuyer().getCompanyName());
		map.put("buyerName", actionBy.getName());
		map.put("buyerLoginEmail", actionBy.getLoginId());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		map.put("date", sdf.format(new Date()));

		PoFinanceRequestPojo poFinanceRequest = poFinanceRequestDao.getPoFinanceRequestPojoByPoId(invoice.getPo().getId());
		if(invoice.getCreatedBy().getEmailNotifications()) {
			if (poFinanceRequest != null && (poFinanceRequest.getStatus() == FinanceRequestStatus.APPROVED || poFinanceRequest.getStatus() == FinanceRequestStatus.ACTIVE) && isAccept == Boolean.TRUE) {
				Iterable<Settings> is = settingsDao.findAll();
				Settings settings = null;
				for (Settings s : is) {
					settings = s;
				}
				sendEmailWithcc(invoice.getCreatedBy().getCommunicationEmail(), settings.getCommunicationEmail(), subject, map, isAccept ? Global.INVOICE_ACCEPTED_TEMPLATE : Global.INVOICE_DECLINE__TEMPLATE);
			} else {
				sendEmail(invoice.getCreatedBy().getCommunicationEmail(), subject, map, isAccept ? Global.INVOICE_ACCEPTED_TEMPLATE : Global.INVOICE_DECLINE__TEMPLATE);
			}
		}
		SupplierNotificationMessage suppMessage = new SupplierNotificationMessage();
		suppMessage.setCreatedBy(actionBy);
		suppMessage.setCreatedDate(new Date());
		String message = null;
		if (isAccept) {
			message = messageSource.getMessage("invoice.audit.declined", new Object[] { buyerRemark }, Global.LOCALE);
		} else {
			message = messageSource.getMessage("invoice.audit.accepted", new Object[] { buyerRemark }, Global.LOCALE);
		}
		suppMessage.setMessageTo(invoice.getCreatedBy());
		suppMessage.setMessage(message);
		suppMessage.setNotificationType(NotificationType.GENERAL);
		suppMessage.setSubject(subject);
		suppMessage.setTenantId(invoice.getSupplier().getId());
		dashboardNotificationService.saveSupplierNotificationMessage(suppMessage);

	}

	@Override
	public List<InvoiceSupplierPojo> getAllInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return invoiceDao.getAllInvoiceDetailsForExcelReport(tenantId, invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate);
	}

	@Override
	public List<InvoiceSupplierPojo> getAllBuyerInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return invoiceDao.getAllBuyerInvoiceDetailsForExcelReport(tenantId, invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate, sdf);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceSupplierPojo> getInvoicesByPoId(String poId) {
		return invoiceDao.getInvoicesByPoId(poId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceSupplierPojo> getInvoicesByPoIdForBuyer(String poId) {
		return invoiceDao.getInvoicesByPoIdForBuyer(poId);
	}

	@Override
	public long findTotalBuyerInvoiceForPo(String poId) {
		return invoiceDao.findTotalBuyerInvoiceForPo(poId);
	}

	@Override
	public void downloadCsvFileForInvoice(HttpServletResponse response, File file, String[] invIds, Date startDate, Date endDate, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.BUYER_INVOICE_REPORT_EXCEL_COLUMNS;
			String[] columns = new String[] { "invoiceId", "name", "ponumber", "supplierCompanyName", "businessunit", "sendDateStr", "actionDateStr", "currency", "grandTotal", "status" };

			long count = findTotalInvoiceForBuyer(tenantId);

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
				List<InvoiceSupplierPojo> list = invoiceDao.findBuyerInvoicesForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, invIds, invoiceSupplierPojo, select_all, startDate, endDate);

				LOG.info("size ........: " + list.size() + ".... count: " + count);
				for (InvoiceSupplierPojo pojo : list) {
					if (pojo.getSendDate() != null) {
						pojo.setSendDateStr(sdf.format(pojo.getSendDate()));
					}
					if (pojo.getActionDate() != null) {
						pojo.setActionDateStr(sdf.format(pojo.getActionDate()));
					}
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done.................................");
			}
			beanWriter.close();
			beanWriter = null;

		} catch (Exception e) {
			LOG.info("Error is >>>>>>>> : " + e.getMessage(), e);
		}
	}

	private CellProcessor[] getProcessors() {
		CellProcessor[] processor = new CellProcessor[] {

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // businessUnit
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional() };
		return processor;
	}

	@Override
	public void downloadCsvFileForSupplierInvoices(HttpServletResponse response, File file, String[] invIds, Date startDate, Date endDate, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.SUPLLIER_INVOICE_REPORT_EXCEL_COLUMNS;
			String[] columns = new String[] { "invoiceId", "referencenumber", "name", "ponumber", "buyerCompanyName", "businessunit", "invoicecreatedby", "createdDateStr", "sendDateStr", "actionDateStr", "currency", "grandTotal", "status" };

			long count = findTotalInvoiceForSupplier(tenantId);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getCellProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<InvoiceSupplierPojo> list = invoiceDao.findSupplierInvoicesForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, invIds, invoiceSupplierPojo, select_all, startDate, endDate);

				for (InvoiceSupplierPojo pojo : list) {
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getSendDate() != null) {
						pojo.setSendDateStr(sdf.format(pojo.getSendDate()));
					}
					if (pojo.getActionDate() != null) {
						pojo.setActionDateStr(sdf.format(pojo.getActionDate()));
					}
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done.................................");
			}
			beanWriter.close();
			beanWriter = null;

		} catch (Exception e) {
			LOG.info("Error is >>>>>>>> : " + e.getMessage(), e);
		}
	}

	private CellProcessor[] getCellProcessors() {
		CellProcessor[] processor = new CellProcessor[] {

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), // businessUnit
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional() };
		return processor;
	}

	@Override
	public void generateInvoiceReport(HttpServletResponse response, Invoice invoice) {
		try {
			InvoiceReport reportObj = invoiceReportDao.findReportByInvoiceId(invoice.getId());

			// Check file is present in database
			if (reportObj != null) {
				LOG.info("Already present in DB");
				response.setContentType("application/pdf");
				response.setContentLength(reportObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + reportObj.getFileName() + "\"");
				FileCopyUtils.copy(reportObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				String poFilename = "UnknownPO.pdf";
				if (invoice.getInvoiceId() != null) {
					poFilename = (invoice.getInvoiceId()).replace("/", "-") + ".pdf";
				}
				String filename = poFilename;
				JasperPrint jasperPrint = saveInvoicePdf(invoice);
				if (jasperPrint != null) {
					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + filename);
					JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while generating Invoice Summary Report : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public JasperPrint saveInvoicePdf(Invoice invoice) {
		JasperPrint jasperPrint = getGeneratedInvoicePdf(invoice);
		try {
			String poFilename = "UnknownPO.pdf";
			if (invoice.getInvoiceId() != null) {
				poFilename = (invoice.getInvoiceId()).replace("/", "-") + ".pdf";
			}
			String filename = poFilename;
			if (jasperPrint != null) {
				byte[] outputFile = JasperExportManager.exportReportToPdf(jasperPrint);
				InvoiceReport attach = new InvoiceReport();
				attach.setFileData(outputFile);
				attach.setFileName(filename);
				attach.setInvoiceNumber(invoice.getInvoiceId());
				attach.setInvoice(invoice);
				invoiceReportDao.saveOrUpdate(attach);
			}
		} catch (Exception e) {
			LOG.error("Error while generating Invoice Summary Report : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public long findTotalBuyerInvoiceByPoId(String poId) {
		return invoiceDao.findTotalBuyerInvoiceByPoId(poId);
	}
}