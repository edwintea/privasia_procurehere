package com.privasia.procurehere.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.*;
import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;

@Service
public class ErpIntegrationServiceImpl implements ErpIntegrationService {

	private static final Logger LOG = LogManager.getLogger(Global.ERP_LOG);

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RftEventAuditDao rftEventAuditDao;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RftBqDao rftEventBqDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfpBqDao rfpEventBqDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RfqBqDao rfqEventBqDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaBqItemDao rfaBqItemDao;

	@Autowired
	RfaBqDao rfaEventBqDao;

	@Autowired
	UomService uomService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	ErpSetupDao erpConfigDao;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ErpAuditService erpAuditService;

	@Autowired
	UserDao userDao;

	@Autowired
	PrService prService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	BuyerService buyerService;

	@Autowired
	UserService userService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	EventAwardAuditService eventAwardAuditService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	ErpAwardStaggingService erpAwardStaggingService;

	@Autowired
	ProductContractItemsDao productContractItemsDao;

	@Autowired
	SourcingFormRequestService requestService;

	@Autowired
	RequestAuditDao requestAuditDao;

	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Autowired
	PoService poService;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	ProductContractService productContractService;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Autowired
	RftEventAwardDao rftEventAwardDao;

	@Autowired
	RftAwardService rftAwardService;

	@Autowired
	RfaEventAwardDao rfaEventAwardDao;

	@Autowired
	RfaAwardService rfaAwardService;

	@Autowired
	RfqEventAwardDao rfqEventAwardDao;

	@Autowired
	RfqAwardService rfqAwardService;

	@Autowired
	RfpEventAwardDao rfpEventAwardDao;

	@Autowired
	RfpAwardService rfpAwardService;

	@Autowired
	ApprovalService approvalService;

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class, ApplicationException.class })
	public RftEvent copyFromRftTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception {
		RftEvent newEvent = rftEventService.copyFromTemplate(templateId, adminUser, null);
		newEvent = constructRftEventDetials(prToAuctionErpPojo, newEvent);
		// rftEventService.addAssociateOwners(adminUser, newEvent);
		return newEvent;
	}

	/**
	 * @param prToAuctionErpPojo
	 * @param newEvent
	 * @return
	 * @throws ApplicationException
	 */
	public RftEvent constructRftEventDetials(PrToAuctionErpPojo prToAuctionErpPojo, RftEvent newEvent) throws ApplicationException {
		if (StringUtils.checkString(prToAuctionErpPojo.getPrNo()).length() > 0) {
			LOG.info("prToAuctionErpPojo PrNo :" + prToAuctionErpPojo.getPrNo());
			newEvent.setReferanceNumber(prToAuctionErpPojo.getPrNo());
			newEvent.setErpDocNo(prToAuctionErpPojo.getPrNo());
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getCurr()).length() > 0) {
			LOG.info("prToAuctionErpPojo Currrency :" + prToAuctionErpPojo.getCurr());
			Currency baseCurrency = currencyDao.findByCurrencyCode(prToAuctionErpPojo.getCurr());
			newEvent.setBaseCurrency(baseCurrency);
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getItmCat()).length() > 0) {
			LOG.info("prToAuctionErpPojo Itm Cat :" + prToAuctionErpPojo.getItmCat());
			// IndustryCategory ic = industryCategoryService.getIndustryCategoryByCode(prToAuctionErpPojo.getItmCat(),
			// newEvent.getTenantId());
			// newEvent.setIndustryCategories(Arrays.asList(ic));
		}

		newEvent = rftEventDao.saveOrUpdate(newEvent);
		RftEventBq bq = new RftEventBq();
		bq.setName("Bill of Quantity");
		bq.setField1Label("Item Category");
		bq.setField1ToShowSupplier(Boolean.FALSE);
		bq.setField2Label("Bq Item Code");
		bq.setField2ToShowSupplier(Boolean.TRUE);
		bq.setField3Label("Material Group");
		bq.setField3ToShowSupplier(Boolean.FALSE);
		bq.setField4Label("Brand");
		bq.setField4ToShowSupplier(Boolean.TRUE);
		bq.setField5Label("MFR Part No");
		bq.setField5ToShowSupplier(Boolean.TRUE);
		bq.setField6Label(prToAuctionErpPojo.getField6Label());
		bq.setField6Label("Purchase Group");
		bq.setField7Label("Delivery Date");
		bq.setField7ToShowSupplier(Boolean.TRUE);
		bq.setField8Label("Item No");
		bq.setField8ToShowSupplier(Boolean.FALSE);
		bq.setField9Label(prToAuctionErpPojo.getField9Label());
		bq.setField9ToShowSupplier(Boolean.FALSE);
		bq.setField10Label(prToAuctionErpPojo.getField10Label());
		bq.setField10ToShowSupplier(Boolean.FALSE);

		if (CollectionUtil.isNotEmpty(prToAuctionErpPojo.getAuctionDetails())) {

			bq.setRfxEvent(newEvent);
			RftEventBq dbBq = rftEventBqDao.saveOrUpdate(bq);
			LOG.info("dbBq :" + dbBq.getName());
			int order = 0;
			int noOfSections = 1;
			int level = 1;
			String delDateString = "";

			Map<String, RftBqItem> sections = new HashMap<String, RftBqItem>();

			RftBqItem defaultBqItemSection = null;
			RftBqItem section = null;
			for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {

				String sapItemNo = StringUtils.checkString(detail.getItmNo());
				if (sapItemNo.length() > 5) {
					sapItemNo = sapItemNo.substring(0, 5);
				}
				String sapItemName = detail.getMatDesc();
				BigDecimal sapQty = new BigDecimal(StringUtils.checkString(detail.getQty()).length() > 0 ? StringUtils.checkString(detail.getQty()) : "0");
				String sapUom = detail.getOrdrUom();
				level = 1;

				if (StringUtils.checkString(detail.getExtSubItm()).length() > 0) {
					section = sections.get(sapItemNo);
					if (section == null) {
						section = new RftBqItem();
						section.setLevel(noOfSections++);
						section.setOrder(0);
						section.setItemName(sapItemName);
						section.setBq(dbBq);
						section.setRfxEvent(newEvent);
						section.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no
						section = rftBqItemDao.saveOrUpdate(section);

						section.setField9(String.valueOf(0)); // used temporarily to get the next item order for
																// section
						// sections.put(sapItemNo, section);
					}

					order = Integer.parseInt(section.getField9()) + 1;
					section.setField9(String.valueOf(order));
					sections.put(sapItemNo, section);

					// Extract the EXT info for the line item
					sapItemName = detail.getExtValItm(); // ex_svcDesc from SAP
					sapQty = new BigDecimal(StringUtils.checkString(detail.getExtSvcQty()).length() > 0 ? StringUtils.checkString(detail.getExtSvcQty()) : "0");
					sapUom = detail.getExtBaseUom();
					level = section.getLevel();
					sapItemNo = detail.getExtSubItm();
				} else {

					if (defaultBqItemSection == null) {
						defaultBqItemSection = new RftBqItem();
						defaultBqItemSection.setLevel(noOfSections++);
						defaultBqItemSection.setOrder(0);
						defaultBqItemSection.setItemName("Item Section");
						defaultBqItemSection.setBq(dbBq);
						defaultBqItemSection.setRfxEvent(newEvent);
						defaultBqItemSection.setField8(StringUtils.checkString(detail.getItmNo()));
						defaultBqItemSection = rftBqItemDao.saveOrUpdate(defaultBqItemSection);
						defaultBqItemSection.setField9(String.valueOf(0));
					}

					order = Integer.parseInt(defaultBqItemSection.getField9()) + 1;
					defaultBqItemSection.setField9(String.valueOf(order));
					level = defaultBqItemSection.getLevel();
					section = defaultBqItemSection;
				}

				// setting Item
				RftBqItem bqItem = new RftBqItem();
				bqItem.setLevel(level);
				bqItem.setOrder(order);
				bqItem.setItemName(sapItemName);
				bqItem.setQuantity(sapQty);
				bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
				// bqItem.setField10(sapItemNo); // used for SAP item no

				if (StringUtils.checkString(sapUom).length() > 0) {
					LOG.info("prToAuction deatils ErpPojo UOM :" + sapUom);
					Uom uom = uomService.getUomByUomAndTenantId(sapUom, newEvent.getTenantId());
					if (uom == null) {
						throw new ApplicationException("Invalid UOM : " + sapUom);
					}
					bqItem.setUom(uom);
				} else {
					throw new ApplicationException("Invalid UOM : " + sapUom);
				}
				// setting extra field
				bqItem.setField1(StringUtils.checkString(detail.getField1()));
				bqItem.setField2(StringUtils.checkString(detail.getField2()));
				bqItem.setField3(StringUtils.checkString(detail.getField3()));
				bqItem.setField4(StringUtils.checkString(detail.getField4()));
				bqItem.setField5(StringUtils.checkString(detail.getField5()));
				bqItem.setField6(StringUtils.checkString(detail.getField6()));
				bqItem.setField7(StringUtils.checkString(detail.getField7()));
				bqItem.setField8(StringUtils.checkString(detail.getItmNo()));
				bqItem.setField9(StringUtils.checkString(detail.getField9()));
				bqItem.setField10(StringUtils.checkString(detail.getField10()));

				bqItem.setParent(section);
				bqItem.setBq(dbBq);
				bqItem.setRfxEvent(newEvent);
				rftBqItemDao.saveOrUpdate(bqItem);

				if (StringUtils.checkString(delDateString).length() == 0) {
					delDateString = detail.getField7();
				}

			}
			if (StringUtils.checkString(delDateString).length() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date deliveryDate = null;
				try {
					deliveryDate = sdf.parse(delDateString);
					newEvent.setDeliveryDate(deliveryDate);
					newEvent = rftEventDao.saveOrUpdate(newEvent);
				} catch (ParseException e) {
					LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
				}
			}

		}
		// bq.setRfxEvent(newEvent);
		// RftEventBq dbBq = rftEventBqDao.saveOrUpdate(bq);
		// LOG.info("dbBq :" + dbBq.getName());
		// int order = 0;
		// // setting Section for each Item
		// RftBqItem bqItemSection = new RftBqItem();
		// bqItemSection.setLevel(1);
		// bqItemSection.setOrder(order++);
		// bqItemSection.setItemName("Item Section");
		// bqItemSection.setBq(dbBq);
		// bqItemSection.setRfxEvent(newEvent);
		// bqItemSection = rftBqItemDao.saveOrUpdate(bqItemSection);
		// String delDateString = "";
		// for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {
		// // setting Item
		// RftBqItem bqItem = new RftBqItem();
		// bqItem.setLevel(1);
		// bqItem.setOrder(order++);
		// bqItem.setItemName(detail.getMatDesc());
		// bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
		//
		// BigDecimal qty = new BigDecimal(detail.getQty());
		// bqItem.setQuantity(qty.toBigInteger());
		//
		// if (StringUtils.checkString(detail.getOrdrUom()).length() > 0) {
		// LOG.info("prToAuction deatils ErpPojo UOM :" + detail.getOrdrUom());
		// Uom uom = uomService.getUomByUomAndTenantId(detail.getOrdrUom(), newEvent.getTenantId());
		// if (uom == null) {
		// throw new ApplicationException("Invalid UOM : " + detail.getOrdrUom());
		// }
		// bqItem.setUom(uom);
		// } else {
		// throw new ApplicationException("Invalid UOM : " + detail.getOrdrUom());
		// }
		// // setting extra field
		// bqItem.setField1(detail.getField1());
		// bqItem.setField2(detail.getField2());
		// bqItem.setField3(detail.getField3());
		// bqItem.setField4(detail.getField4());
		// bqItem.setField5(detail.getField5());
		// bqItem.setField6(detail.getField6());
		// bqItem.setField7(detail.getField7());
		// bqItem.setField8(detail.getField8());
		// bqItem.setField9(detail.getField9());
		// bqItem.setField10(detail.getField10());
		//
		// bqItem.setParent(bqItemSection);
		// bqItem.setBq(dbBq);
		// bqItem.setRfxEvent(newEvent);
		// rftBqItemDao.saveOrUpdate(bqItem);
		// if (StringUtils.checkString(delDateString).length() == 0) {
		// delDateString = detail.getField7();
		// }
		//
		// }
		// if (StringUtils.checkString(delDateString).length() > 0) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// Date deliveryDate = null;
		// try {
		// deliveryDate = sdf.parse(delDateString);
		// newEvent.setDeliveryDate(deliveryDate);
		// newEvent = rftEventDao.saveOrUpdate(newEvent);
		// } catch (ParseException e) {
		// LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
		// }
		// }
		// }
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class, ApplicationException.class })
	public RfpEvent copyFromRfpTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception {
		RfpEvent newEvent = rfpEventService.copyFromTemplate(templateId, adminUser, null);
		newEvent = constructRfpEventDetials(prToAuctionErpPojo, newEvent);
		// rfpEventService.addAssociateOwners(adminUser, newEvent);
		return newEvent;
	}

	/**
	 * @param prToAuctionErpPojo
	 * @param newEvent
	 * @return
	 * @throws ApplicationException
	 */
	public RfpEvent constructRfpEventDetials(PrToAuctionErpPojo prToAuctionErpPojo, RfpEvent newEvent) throws ApplicationException {
		if (StringUtils.checkString(prToAuctionErpPojo.getPrNo()).length() > 0) {
			LOG.info("prToAuctionErpPojo PrNo :" + prToAuctionErpPojo.getPrNo());
			newEvent.setReferanceNumber(prToAuctionErpPojo.getPrNo());
			newEvent.setErpDocNo(prToAuctionErpPojo.getPrNo());
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getCurr()).length() > 0) {
			LOG.info("prToAuctionErpPojo Currrency :" + prToAuctionErpPojo.getCurr());
			Currency baseCurrency = currencyDao.findByCurrencyCode(prToAuctionErpPojo.getCurr());
			newEvent.setBaseCurrency(baseCurrency);
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getItmCat()).length() > 0) {
			LOG.info("prToAuctionErpPojo Itm Cat :" + prToAuctionErpPojo.getItmCat());
			// IndustryCategory ic = industryCategoryService.getIndustryCategoryByCode(prToAuctionErpPojo.getItmCat(),
			// newEvent.getTenantId());
			// newEvent.setIndustryCategories(Arrays.asList(ic));
		}

		newEvent = rfpEventDao.saveOrUpdate(newEvent);
		RfpEventBq bq = new RfpEventBq();
		bq.setName("Bill of Quantity");
		bq.setField1Label("Item Category");
		bq.setField1ToShowSupplier(Boolean.FALSE);
		bq.setField2Label("Bq Item Code");
		bq.setField2ToShowSupplier(Boolean.TRUE);
		bq.setField3Label("Material Group");
		bq.setField3ToShowSupplier(Boolean.FALSE);
		bq.setField4Label("Brand");
		bq.setField4ToShowSupplier(Boolean.TRUE);
		bq.setField5Label("MFR Part No");
		bq.setField5ToShowSupplier(Boolean.TRUE);
		bq.setField6Label(prToAuctionErpPojo.getField6Label());
		bq.setField6Label("Purchase Group");
		bq.setField7Label("Delivery Date");
		bq.setField7ToShowSupplier(Boolean.TRUE);
		bq.setField8Label("Item No");
		bq.setField8ToShowSupplier(Boolean.FALSE);
		bq.setField9Label(prToAuctionErpPojo.getField9Label());
		bq.setField9ToShowSupplier(Boolean.FALSE);
		bq.setField10Label(prToAuctionErpPojo.getField10Label());
		bq.setField10ToShowSupplier(Boolean.FALSE);

		if (CollectionUtil.isNotEmpty(prToAuctionErpPojo.getAuctionDetails())) {

			bq.setRfxEvent(newEvent);
			RfpEventBq dbBq = rfpEventBqDao.saveOrUpdate(bq);
			LOG.info("dbBq :" + dbBq.getName());
			int order = 0;
			int noOfSections = 1;
			int level = 1;
			String delDateString = "";

			Map<String, RfpBqItem> sections = new HashMap<String, RfpBqItem>();

			RfpBqItem defaultBqItemSection = null;
			RfpBqItem section = null;
			for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {

				String sapItemNo = StringUtils.checkString(detail.getItmNo());
				if (sapItemNo.length() > 5) {
					sapItemNo = sapItemNo.substring(0, 5);
				}
				String sapItemName = detail.getMatDesc();
				BigDecimal sapQty = new BigDecimal(StringUtils.checkString(detail.getQty()).length() > 0 ? StringUtils.checkString(detail.getQty()) : "0");
				String sapUom = detail.getOrdrUom();
				level = 1;

				if (StringUtils.checkString(detail.getExtSubItm()).length() > 0) {
					section = sections.get(sapItemNo);
					if (section == null) {
						section = new RfpBqItem();
						section.setLevel(noOfSections++);
						section.setOrder(0);
						section.setItemName(sapItemName);
						section.setBq(dbBq);
						section.setRfxEvent(newEvent);
						section.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no
						section = rfpBqItemDao.saveOrUpdate(section);

						section.setField9(String.valueOf(0)); // used temporarily to get the next item order for
																// section
						sections.put(sapItemNo, section);
					}

					order = Integer.parseInt(section.getField9()) + 1;
					section.setField9(String.valueOf(order));
					sections.put(sapItemNo, section);

					// Extract the EXT info for the line item
					sapItemName = detail.getExtValItm(); // ex_svcDesc from SAP
					sapQty = new BigDecimal(StringUtils.checkString(detail.getExtSvcQty()).length() > 0 ? StringUtils.checkString(detail.getExtSvcQty()) : "0");
					sapUom = detail.getExtBaseUom();
					level = section.getLevel();
					sapItemNo = detail.getExtSubItm();
				} else {

					if (defaultBqItemSection == null) {
						defaultBqItemSection = new RfpBqItem();
						defaultBqItemSection.setLevel(noOfSections++);
						defaultBqItemSection.setOrder(0);
						defaultBqItemSection.setItemName("Item Section");
						defaultBqItemSection.setBq(dbBq);
						defaultBqItemSection.setRfxEvent(newEvent);
						defaultBqItemSection.setField8(StringUtils.checkString(detail.getItmNo()));
						defaultBqItemSection = rfpBqItemDao.saveOrUpdate(defaultBqItemSection);
						defaultBqItemSection.setField9(String.valueOf(0));
					}

					order = Integer.parseInt(defaultBqItemSection.getField9()) + 1;
					defaultBqItemSection.setField9(String.valueOf(order));
					level = defaultBqItemSection.getLevel();
					section = defaultBqItemSection;
				}

				// setting Item
				RfpBqItem bqItem = new RfpBqItem();
				bqItem.setLevel(level);
				bqItem.setOrder(order);
				bqItem.setItemName(sapItemName);
				bqItem.setQuantity(sapQty);
				bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
				bqItem.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no

				if (StringUtils.checkString(sapUom).length() > 0) {
					LOG.info("prToAuction deatils ErpPojo UOM :" + sapUom);
					Uom uom = uomService.getUomByUomAndTenantId(sapUom, newEvent.getTenantId());
					if (uom == null) {
						throw new ApplicationException("Invalid UOM : " + sapUom);
					}
					bqItem.setUom(uom);
				} else {
					throw new ApplicationException("Invalid UOM : " + sapUom);
				}
				// setting extra field
				bqItem.setField1(StringUtils.checkString(detail.getField1()));
				bqItem.setField2(StringUtils.checkString(detail.getField2()));
				bqItem.setField3(StringUtils.checkString(detail.getField3()));
				bqItem.setField4(StringUtils.checkString(detail.getField4()));
				bqItem.setField5(StringUtils.checkString(detail.getField5()));
				bqItem.setField6(StringUtils.checkString(detail.getField6()));
				bqItem.setField7(StringUtils.checkString(detail.getField7()));
				bqItem.setField8(StringUtils.checkString(detail.getItmNo()));
				bqItem.setField9(StringUtils.checkString(detail.getField9()));
				bqItem.setField10(StringUtils.checkString(detail.getField10()));

				bqItem.setParent(section);
				bqItem.setBq(dbBq);
				bqItem.setRfxEvent(newEvent);
				rfpBqItemDao.saveOrUpdate(bqItem);

				if (StringUtils.checkString(delDateString).length() == 0) {
					delDateString = detail.getField7();
				}

			}
			if (StringUtils.checkString(delDateString).length() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date deliveryDate = null;
				try {
					deliveryDate = sdf.parse(delDateString);
					newEvent.setDeliveryDate(deliveryDate);
					newEvent = rfpEventDao.saveOrUpdate(newEvent);
				} catch (ParseException e) {
					LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
				}
			}
		}

		// bq.setRfxEvent(newEvent);
		// RfpEventBq dbBq = rfpEventBqDao.saveOrUpdate(bq);
		// LOG.info("dbBq :" + dbBq.getName());
		// int order = 0;
		// // setting Section for each Item
		// RfpBqItem bqItemSection = new RfpBqItem();
		// bqItemSection.setLevel(1);
		// bqItemSection.setOrder(order++);
		// bqItemSection.setItemName("Item Section");
		// bqItemSection.setBq(dbBq);
		// bqItemSection.setRfxEvent(newEvent);
		// bqItemSection = rfpBqItemDao.saveOrUpdate(bqItemSection);
		// String delDateString = "";
		// for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {
		// // setting Item
		// RfpBqItem bqItem = new RfpBqItem();
		// bqItem.setLevel(1);
		// bqItem.setOrder(order++);
		// bqItem.setItemName(detail.getMatDesc());
		// bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
		//
		// BigDecimal qty = new BigDecimal(detail.getQty());
		// bqItem.setQuantity(qty.toBigInteger());
		//
		// if (StringUtils.checkString(detail.getOrdrUom()).length() > 0) {
		// LOG.info("prToAuction deatils ErpPojo UOM :" + detail.getOrdrUom());
		// Uom uom = uomService.getUomByUomAndTenantId(detail.getOrdrUom(), newEvent.getTenantId());
		// if (uom == null) {
		// throw new ApplicationException("Invalid UOM : " + detail.getOrdrUom());
		// }
		// bqItem.setUom(uom);
		// } else {
		// throw new ApplicationException("Invalid UOM : " + detail.getOrdrUom());
		// }
		// // setting extra field
		// bqItem.setField1(detail.getField1());
		// bqItem.setField2(detail.getField2());
		// bqItem.setField3(detail.getField3());
		// bqItem.setField4(detail.getField4());
		// bqItem.setField5(detail.getField5());
		// bqItem.setField6(detail.getField6());
		// bqItem.setField7(detail.getField7());
		// bqItem.setField8(detail.getField8());
		// bqItem.setField9(detail.getField9());
		// bqItem.setField10(detail.getField10());
		//
		// bqItem.setParent(bqItemSection);
		// bqItem.setBq(dbBq);
		// bqItem.setRfxEvent(newEvent);
		// rfpBqItemDao.saveOrUpdate(bqItem);
		// if (StringUtils.checkString(delDateString).length() == 0) {
		// delDateString = detail.getField7();
		// }
		//
		// }
		// if (StringUtils.checkString(delDateString).length() > 0) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// Date deliveryDate = null;
		// try {
		// deliveryDate = sdf.parse(delDateString);
		// newEvent.setDeliveryDate(deliveryDate);
		// newEvent = rfpEventDao.saveOrUpdate(newEvent);
		// } catch (ParseException e) {
		// LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
		// }
		// }
		// }

		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class, ApplicationException.class })
	public RfqEvent copyFromRfqTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception {
		RfqEvent newEvent = rfqEventService.copyFromTemplate(templateId, adminUser, null);
		newEvent = constructRfqEventDetials(prToAuctionErpPojo, newEvent);
		// rfqEventService.addAssociateOwners(adminUser, newEvent);
		return newEvent;
	}

	/**
	 * @param prToAuctionErpPojo
	 * @param newEvent
	 * @return
	 * @throws ApplicationException
	 */
	private RfqEvent constructRfqEventDetials(PrToAuctionErpPojo prToAuctionErpPojo, RfqEvent newEvent) throws ApplicationException {
		if (StringUtils.checkString(prToAuctionErpPojo.getPrNo()).length() > 0) {
			LOG.info("prToAuctionErpPojo PrNo :" + prToAuctionErpPojo.getPrNo());
			newEvent.setReferanceNumber(prToAuctionErpPojo.getPrNo());
			newEvent.setErpDocNo(prToAuctionErpPojo.getPrNo());
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getCurr()).length() > 0) {
			LOG.info("prToAuctionErpPojo Currrency :" + prToAuctionErpPojo.getCurr());
			Currency baseCurrency = currencyDao.findByCurrencyCode(prToAuctionErpPojo.getCurr());
			newEvent.setBaseCurrency(baseCurrency);
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getItmCat()).length() > 0) {
			LOG.info("prToAuctionErpPojo Itm Cat :" + prToAuctionErpPojo.getItmCat());
			// IndustryCategory ic = industryCategoryService.getIndustryCategoryByCode(prToAuctionErpPojo.getItmCat(),
			// newEvent.getTenantId());
			// newEvent.setIndustryCategories(Arrays.asList(ic));
		}

		newEvent = rfqEventDao.saveOrUpdate(newEvent);
		RfqEventBq bq = new RfqEventBq();
		bq.setName("Bill of Quantity");
		bq.setField1Label("Item Category");
		bq.setField1ToShowSupplier(Boolean.FALSE);
		bq.setField2Label("Bq Item Code");
		bq.setField2ToShowSupplier(Boolean.TRUE);
		bq.setField3Label("Material Group");
		bq.setField3ToShowSupplier(Boolean.FALSE);
		bq.setField4Label("Brand");
		bq.setField4ToShowSupplier(Boolean.TRUE);
		bq.setField5Label("MFR Part No");
		bq.setField5ToShowSupplier(Boolean.TRUE);
		bq.setField6Label(prToAuctionErpPojo.getField6Label());
		bq.setField6Label("Purchase Group");
		bq.setField7Label("Delivery Date");
		bq.setField7ToShowSupplier(Boolean.TRUE);
		bq.setField8Label("Item No");
		bq.setField8ToShowSupplier(Boolean.FALSE);
		bq.setField9Label(prToAuctionErpPojo.getField9Label());
		bq.setField9ToShowSupplier(Boolean.FALSE);
		bq.setField10Label(prToAuctionErpPojo.getField10Label());
		bq.setField10ToShowSupplier(Boolean.FALSE);

		if (CollectionUtil.isNotEmpty(prToAuctionErpPojo.getAuctionDetails())) {

			bq.setRfxEvent(newEvent);
			RfqEventBq dbBq = rfqEventBqDao.saveOrUpdate(bq);
			LOG.info("dbBq :" + dbBq.getName());
			int order = 0;
			int noOfSections = 1;
			int level = 1;
			String delDateString = "";

			Map<String, RfqBqItem> sections = new HashMap<String, RfqBqItem>();

			RfqBqItem defaultBqItemSection = null;
			RfqBqItem section = null;
			for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {

				String sapItemNo = StringUtils.checkString(detail.getItmNo());
				if (sapItemNo.length() > 5) {
					sapItemNo = sapItemNo.substring(0, 5);
				}
				String sapItemName = detail.getMatDesc();
				BigDecimal sapQty = new BigDecimal(StringUtils.checkString(detail.getQty()).length() > 0 ? StringUtils.checkString(detail.getQty()) : "0");
				String sapUom = detail.getOrdrUom();
				level = 1;

				if (StringUtils.checkString(detail.getExtSubItm()).length() > 0) {
					section = sections.get(sapItemNo);
					if (section == null) {
						section = new RfqBqItem();
						section.setLevel(noOfSections++);
						section.setOrder(0);
						section.setItemName(sapItemName);
						section.setBq(dbBq);
						section.setRfxEvent(newEvent);
						section.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no
						section = rfqBqItemDao.saveOrUpdate(section);

						section.setField9(String.valueOf(0)); // used temporarily to get the next item order for
																// section
						sections.put(sapItemNo, section);
					}

					order = Integer.parseInt(section.getField9()) + 1;
					section.setField9(String.valueOf(order));
					sections.put(sapItemNo, section);

					// Extract the EXT info for the line item
					sapItemName = detail.getExtValItm(); // ex_svcDesc from SAP
					sapQty = new BigDecimal(StringUtils.checkString(detail.getExtSvcQty()).length() > 0 ? StringUtils.checkString(detail.getExtSvcQty()) : "0");
					sapUom = detail.getExtBaseUom();
					level = section.getLevel();
					sapItemNo = detail.getExtSubItm();
				} else {

					if (defaultBqItemSection == null) {
						defaultBqItemSection = new RfqBqItem();
						defaultBqItemSection.setLevel(noOfSections++);
						defaultBqItemSection.setOrder(0);
						defaultBqItemSection.setItemName("Item Section");
						defaultBqItemSection.setBq(dbBq);
						defaultBqItemSection.setRfxEvent(newEvent);
						defaultBqItemSection.setField8(StringUtils.checkString(detail.getItmNo()));
						defaultBqItemSection = rfqBqItemDao.saveOrUpdate(defaultBqItemSection);
						defaultBqItemSection.setField9(String.valueOf(0));
					}

					order = Integer.parseInt(defaultBqItemSection.getField9()) + 1;
					defaultBqItemSection.setField9(String.valueOf(order));
					level = defaultBqItemSection.getLevel();
					section = defaultBqItemSection;
				}

				// setting Item
				RfqBqItem bqItem = new RfqBqItem();
				bqItem.setLevel(level);
				bqItem.setOrder(order);
				bqItem.setItemName(sapItemName);
				bqItem.setQuantity(sapQty);
				bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
				bqItem.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no

				if (StringUtils.checkString(sapUom).length() > 0) {
					LOG.info("prToAuction deatils ErpPojo UOM :" + sapUom);
					Uom uom = uomService.getUomByUomAndTenantId(sapUom, newEvent.getTenantId());
					if (uom == null) {
						throw new ApplicationException("Invalid UOM : " + sapUom);
					}
					bqItem.setUom(uom);
				} else {
					throw new ApplicationException("Invalid UOM : " + sapUom);
				}
				// setting extra field
				bqItem.setField1(StringUtils.checkString(detail.getField1()));
				bqItem.setField2(StringUtils.checkString(detail.getField2()));
				bqItem.setField3(StringUtils.checkString(detail.getField3()));
				bqItem.setField4(StringUtils.checkString(detail.getField4()));
				bqItem.setField5(StringUtils.checkString(detail.getField5()));
				bqItem.setField6(StringUtils.checkString(detail.getField6()));
				bqItem.setField7(StringUtils.checkString(detail.getField7()));
				bqItem.setField8(StringUtils.checkString(detail.getItmNo()));
				bqItem.setField9(StringUtils.checkString(detail.getField9()));
				bqItem.setField10(StringUtils.checkString(detail.getField10()));

				bqItem.setParent(section);
				bqItem.setBq(dbBq);
				bqItem.setRfxEvent(newEvent);
				rfqBqItemDao.saveOrUpdate(bqItem);

				if (StringUtils.checkString(delDateString).length() == 0) {
					delDateString = detail.getField7();
				}

			}
			if (StringUtils.checkString(delDateString).length() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date deliveryDate = null;
				try {
					deliveryDate = sdf.parse(delDateString);
					newEvent.setDeliveryDate(deliveryDate);
					newEvent = rfqEventDao.saveOrUpdate(newEvent);
				} catch (ParseException e) {
					LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
				}
			}

		}
		// bq.setRfxEvent(newEvent);
		// RfqEventBq dbBq = rfqEventBqDao.saveOrUpdate(bq);
		// LOG.info("dbBq :" + dbBq.getName());
		// int order = 0;
		// // setting Section for each Item
		// RfqBqItem bqItemSection = new RfqBqItem();
		// bqItemSection.setLevel(1);
		// bqItemSection.setOrder(order++);
		// bqItemSection.setItemName("Item Section");
		// bqItemSection.setBq(dbBq);
		// bqItemSection.setRfxEvent(newEvent);
		// bqItemSection = rfqBqItemDao.saveOrUpdate(bqItemSection);
		// String delDateString = "";
		// for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {
		// // setting Item
		// RfqBqItem bqItem = new RfqBqItem();
		// bqItem.setLevel(1);
		// bqItem.setOrder(order++);
		// bqItem.setItemName(detail.getMatDesc());
		// bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
		//
		// BigDecimal qty = new BigDecimal(detail.getQty());
		// bqItem.setQuantity(qty.toBigInteger());
		//
		// if (StringUtils.checkString(detail.getOrdrUom()).length() > 0) {
		// LOG.info("prToAuction deatils ErpPojo UOM :" + detail.getOrdrUom());
		// Uom uom = uomService.getUomByUomAndTenantId(detail.getOrdrUom(), newEvent.getTenantId());
		// if (uom == null) {
		// throw new ApplicationException("Invalid UOM : " + detail.getOrdrUom());
		// }
		// bqItem.setUom(uom);
		// } else {
		// throw new ApplicationException("No UOM provided.");
		// }
		// // setting extra field
		// bqItem.setField1(detail.getField1());
		// bqItem.setField2(detail.getField2());
		// bqItem.setField3(detail.getField3());
		// bqItem.setField4(detail.getField4());
		// bqItem.setField5(detail.getField5());
		// bqItem.setField6(detail.getField6());
		// bqItem.setField7(detail.getField7());
		// bqItem.setField8(detail.getField8());
		// bqItem.setField9(detail.getField9());
		// bqItem.setField10(detail.getField10());
		//
		// bqItem.setParent(bqItemSection);
		// bqItem.setBq(dbBq);
		// bqItem.setRfxEvent(newEvent);
		// rfqBqItemDao.saveOrUpdate(bqItem);
		// if (StringUtils.checkString(delDateString).length() == 0) {
		// delDateString = detail.getField7();
		// }
		//
		// }
		// if (StringUtils.checkString(delDateString).length() > 0) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// Date deliveryDate = null;
		// try {
		// deliveryDate = sdf.parse(delDateString);
		// newEvent.setDeliveryDate(deliveryDate);
		// newEvent = rfqEventDao.saveOrUpdate(newEvent);
		// } catch (ParseException e) {
		// LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
		// }
		// }
		// }
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class, ApplicationException.class })
	public RfaEvent copyFromRfaTemplateForErp(String templateId, User createdBy, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception {
		RfaEvent newEvent = rfaEventService.copyFromTemplate(templateId, createdBy, null);
		newEvent = constructRfaEventDetials(prToAuctionErpPojo, newEvent);
		// rfaEventService.addAssociateOwners(createdBy, newEvent);
		return newEvent;
	}

	/**
	 * @param prToAuctionErpPojo
	 * @param newEvent
	 * @return
	 * @throws ApplicationException
	 */
	private RfaEvent constructRfaEventDetials(PrToAuctionErpPojo prToAuctionErpPojo, RfaEvent newEvent) throws ApplicationException {
		if (StringUtils.checkString(prToAuctionErpPojo.getPrNo()).length() > 0) {
			LOG.info("prToAuctionErpPojo PrNo :" + prToAuctionErpPojo.getPrNo());
			newEvent.setReferanceNumber(prToAuctionErpPojo.getPrNo());
			newEvent.setErpDocNo(prToAuctionErpPojo.getPrNo());
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getCurr()).length() > 0) {
			LOG.info("prToAuctionErpPojo Currrency :" + prToAuctionErpPojo.getCurr());
			Currency baseCurrency = currencyDao.findByCurrencyCode(prToAuctionErpPojo.getCurr());
			newEvent.setBaseCurrency(baseCurrency);
		}
		if (StringUtils.checkString(prToAuctionErpPojo.getItmCat()).length() > 0) {
			LOG.info("prToAuctionErpPojo Itm Cat :" + prToAuctionErpPojo.getItmCat() + "==== newEvent.getTenantId() :" + newEvent.getTenantId());
			// IndustryCategory ic = industryCategoryService.getIndustryCategoryByCode(prToAuctionErpPojo.getItmCat(),
			// newEvent.getTenantId());
			// if (ic != null) {
			// LOG.info("Industry Cat :" + ic.getCode());
			// newEvent.setIndustryCategories(new ArrayList<IndustryCategory>());
			// newEvent.getIndustryCategories().add(ic);
			// }else{
			// LOG.info("Industry Cat is null");
			// }
		}

		newEvent = rfaEventDao.saveOrUpdate(newEvent);

		RfaEventBq bq = new RfaEventBq();
		bq.setName("Bill of Quantity");
		bq.setName("Bill of Quantity");
		bq.setField1Label("Item Category");
		bq.setField1ToShowSupplier(Boolean.FALSE);
		bq.setField2Label("Bq Item Code");
		bq.setField2ToShowSupplier(Boolean.TRUE);
		bq.setField3Label("Material Group");
		bq.setField3ToShowSupplier(Boolean.FALSE);
		bq.setField4Label("Brand");
		bq.setField4ToShowSupplier(Boolean.TRUE);
		bq.setField5Label("MFR Part No");
		bq.setField5ToShowSupplier(Boolean.TRUE);
		bq.setField6Label(prToAuctionErpPojo.getField6Label());
		bq.setField6Label("Purchase Group");
		bq.setField7Label("Delivery Date");
		bq.setField7ToShowSupplier(Boolean.TRUE);
		bq.setField8Label("Item No");
		bq.setField8ToShowSupplier(Boolean.FALSE);
		bq.setField9Label(prToAuctionErpPojo.getField9Label());
		bq.setField9ToShowSupplier(Boolean.FALSE);
		bq.setField10Label(prToAuctionErpPojo.getField10Label());
		bq.setField10ToShowSupplier(Boolean.FALSE);

		if (CollectionUtil.isNotEmpty(prToAuctionErpPojo.getAuctionDetails())) {
			bq.setRfxEvent(newEvent);
			RfaEventBq dbBq = rfaEventBqDao.saveOrUpdate(bq);
			LOG.info("dbBq :" + dbBq.getName());
			int order = 0;
			int noOfSections = 1;
			int level = 1;
			String delDateString = "";

			Map<String, RfaBqItem> sections = new HashMap<String, RfaBqItem>();

			RfaBqItem defaultBqItemSection = null;
			RfaBqItem section = null;
			for (PrToAuctionDetailsErpPojo detail : prToAuctionErpPojo.getAuctionDetails()) {

				String sapItemNo = StringUtils.checkString(detail.getItmNo());
				if (sapItemNo.length() > 5) {
					sapItemNo = sapItemNo.substring(0, 5);
				}
				String sapItemName = detail.getMatDesc();
				BigDecimal sapQty = new BigDecimal(StringUtils.checkString(detail.getQty()).length() > 0 ? StringUtils.checkString(detail.getQty()) : "0");
				String sapUom = detail.getOrdrUom();
				level = 1;

				if (StringUtils.checkString(detail.getExtSubItm()).length() > 0) {
					section = sections.get(sapItemNo);
					if (section == null) {
						section = new RfaBqItem();
						section.setLevel(noOfSections++);
						section.setOrder(0);
						section.setItemName(sapItemName);
						section.setBq(dbBq);
						section.setRfxEvent(newEvent);
						section.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no
						section = rfaBqItemDao.saveOrUpdate(section);

						section.setField9(String.valueOf(0)); // used temporarily to get the next item order for
																// section
						sections.put(sapItemNo, section);
					}

					order = Integer.parseInt(section.getField9()) + 1;
					section.setField9(String.valueOf(order));
					sections.put(sapItemNo, section);

					// Extract the EXT info for the line item
					sapItemName = detail.getExtValItm(); // ex_svcDesc from SAP
					sapQty = new BigDecimal(StringUtils.checkString(detail.getExtSvcQty()).length() > 0 ? StringUtils.checkString(detail.getExtSvcQty()) : "0");
					sapUom = detail.getExtBaseUom();
					level = section.getLevel();
					sapItemNo = detail.getExtSubItm();
				} else {

					if (defaultBqItemSection == null) {
						defaultBqItemSection = new RfaBqItem();
						defaultBqItemSection.setLevel(noOfSections++);
						defaultBqItemSection.setOrder(0);
						defaultBqItemSection.setItemName("Item Section");
						defaultBqItemSection.setBq(dbBq);
						defaultBqItemSection.setRfxEvent(newEvent);
						defaultBqItemSection.setField8(StringUtils.checkString(detail.getItmNo()));
						defaultBqItemSection = rfaBqItemDao.saveOrUpdate(defaultBqItemSection);
						defaultBqItemSection.setField9(String.valueOf(0));
					}

					order = Integer.parseInt(defaultBqItemSection.getField9()) + 1;
					defaultBqItemSection.setField9(String.valueOf(order));
					level = defaultBqItemSection.getLevel();
					section = defaultBqItemSection;
				}

				// setting Item
				RfaBqItem bqItem = new RfaBqItem();
				bqItem.setLevel(level);
				bqItem.setOrder(order);
				bqItem.setItemName(sapItemName);
				bqItem.setQuantity(sapQty);
				bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
				bqItem.setField8(StringUtils.checkString(detail.getItmNo())); // used for SAP item no

				if (StringUtils.checkString(sapUom).length() > 0) {
					LOG.info("prToAuction deatils ErpPojo UOM :" + sapUom);
					Uom uom = uomService.getUomByUomAndTenantId(sapUom, newEvent.getTenantId());
					if (uom == null) {
						throw new ApplicationException("Invalid UOM : " + sapUom);
					}
					bqItem.setUom(uom);
				} else {
					throw new ApplicationException("Invalid UOM : " + sapUom);
				}
				// setting extra field
				bqItem.setField1(StringUtils.checkString(detail.getField1()));
				bqItem.setField2(StringUtils.checkString(detail.getField2()));
				bqItem.setField3(StringUtils.checkString(detail.getField3()));
				bqItem.setField4(StringUtils.checkString(detail.getField4()));
				bqItem.setField5(StringUtils.checkString(detail.getField5()));
				bqItem.setField6(StringUtils.checkString(detail.getField6()));
				bqItem.setField7(StringUtils.checkString(detail.getField7()));
				bqItem.setField8(StringUtils.checkString(detail.getItmNo()));
				bqItem.setField9(StringUtils.checkString(detail.getField9()));
				bqItem.setField10(StringUtils.checkString(detail.getField10()));

				bqItem.setParent(section);
				bqItem.setBq(dbBq);
				bqItem.setRfxEvent(newEvent);
				rfaBqItemDao.saveOrUpdate(bqItem);

				if (StringUtils.checkString(delDateString).length() == 0) {
					delDateString = detail.getField7();
				}

			}
			if (StringUtils.checkString(delDateString).length() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date deliveryDate = null;
				try {
					deliveryDate = sdf.parse(delDateString);
					newEvent.setDeliveryDate(deliveryDate);
					newEvent = rfaEventDao.saveOrUpdate(newEvent);
				} catch (ParseException e) {
					LOG.error("Error while parsing Delivery date :" + e.getMessage(), e);
				}
			}
		}
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public AwardResponsePojo sendAwardPage(List<AwardErpPojo> awardErpPojoList, String eventId, RfxTypes eventType) throws Exception {
		ErpSetup erpConfig = erpConfigDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
		String erpSeqNo = erpSetupService.genrateSquanceNumber();
		AwardResponsePojo resPojo = new AwardResponsePojo();
		if (erpConfig != null) {
			LOG.info("erpConfig erp enable :" + erpConfig.getIsErpEnable());

			if (Boolean.TRUE == erpConfig.getIsErpEnable()) {
				try {
					ObjectMapper mapperObj = new ObjectMapper();

					LOG.info("================================================------>" + awardErpPojoList.size());

					if (!erpConfig.getAwardInterfaceTypePull()) {
						for (AwardErpPojo awardErpPojo : awardErpPojoList) {
							awardErpPojo.setErpSeqNo(erpSeqNo);
						}
						HttpHeaders headers = new HttpHeaders();

						headers.set(Global.X_AUTH_KEY_HEADER_PROPERTY, erpConfig.getAppId());

						HttpEntity<List<AwardErpPojo>> request = new HttpEntity<List<AwardErpPojo>>(awardErpPojoList, headers);

						String responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/AwardSendData", request, String.class);

						// String responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/AwardSendData",
						// awardErpPojoList, String.class);
						LOG.info("response :" + responseMsg);

						resPojo = mapperObj.readValue(responseMsg, AwardResponsePojo.class);
					} else {

						TransferAwardPojo transferAwardPojo = new TransferAwardPojo();
						List<TransferAwardBqPojo> awards = new ArrayList<TransferAwardBqPojo>();
						for (AwardErpPojo awardErpPojo : awardErpPojoList) {
							transferAwardPojo.setCurrencyCode(awardErpPojo.getCurrencyCode());
							transferAwardPojo.setEventId(awardErpPojo.getEventId());
							transferAwardPojo.setEventName(awardErpPojo.getEventName());
							transferAwardPojo.setEventOwner(awardErpPojo.getEventOwner());
							transferAwardPojo.setAwardRemark(awardErpPojo.getAwardRemark());
							transferAwardPojo.setBusinessUnitName(awardErpPojo.getBusinessUnitName());
							transferAwardPojo.setDeliveryAddress(awardErpPojo.getDeliveryAddress());
							transferAwardPojo.setEventReferenceNumber(awardErpPojo.getReferenceNumber());
							transferAwardPojo.setCreatedDate(awardErpPojo.getCreatedDate());
							transferAwardPojo.setStartDate(awardErpPojo.getStartDate());
							transferAwardPojo.setEndDate(awardErpPojo.getEndDate());
							transferAwardPojo.setValidityDays(awardErpPojo.getValidityDays());
							transferAwardPojo.setDeliveryDate(awardErpPojo.getDeliveryDate());
							transferAwardPojo.setPaymentTerm(awardErpPojo.getPaymentTerm());
							transferAwardPojo.setErpSeqNo(awardErpPojo.getErpSeqNo());
							TransferAwardBqPojo bq = new TransferAwardBqPojo();
							bq.setBqName(awardErpPojo.getBqName());
							bq.setBqItems(awardErpPojo.getBqItems());
							awards.add(bq);
						}
						transferAwardPojo.setBqList(awards);
						String payload = mapperObj.writeValueAsString(transferAwardPojo);
						LOG.info("jsonObject  :" + payload);
						ErpAwardStaging dBstagging = erpAwardStaggingService.findAwardStaggingByEventID(transferAwardPojo.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
						if (dBstagging != null) {
							dBstagging.setPayload(payload);
							dBstagging.setSentFlag(Boolean.FALSE);
							dBstagging.setActionBy(SecurityLibrary.getLoggedInUser());
							dBstagging.setActionDate(new Date());
							erpAwardStaggingService.update(dBstagging);
						} else {
							ErpAwardStaging stagging = new ErpAwardStaging();
							stagging.setEventType(eventType);
							stagging.setPayload(payload);
							stagging.setDocNo(transferAwardPojo.getEventId());
							stagging.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
							stagging.setActionBy(SecurityLibrary.getLoggedInUser());
							stagging.setActionDate(new Date());
							stagging.setSentFlag(Boolean.FALSE);
							stagging = erpAwardStaggingService.saveErpAwardStaging(stagging);
						}

					}
					if (resPojo != null && StringUtils.checkString(resPojo.getError()).length() > 0) {
						throw new Exception(resPojo.getError());
					}

					switch (eventType) {
					case RFA:
						RfaEvent rfaEvent = rfaEventDao.findByEventId(eventId);
						if (rfaEvent != null) {
							String seprator = ",";
							List<String> refNoList = new ArrayList<String>();
							if (CollectionUtil.isNotEmpty(resPojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardRefPojo : resPojo.getRefNumList()) {
									refNoList.add(awardRefPojo.getReferenceNumber());

								}
							}
							rfaEvent.setErpAwardRefId(String.join(seprator, refNoList));
						}
						break;
					case RFI:
						break;
					case RFP:
						RfpEvent rfpEvent = rfpEventDao.findByEventId(eventId);
						if (rfpEvent != null) {
							String seprator = " , ";
							List<String> refNoList = new ArrayList<String>();
							if (CollectionUtil.isNotEmpty(resPojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardRefPojo : resPojo.getRefNumList()) {
									refNoList.add(awardRefPojo.getReferenceNumber());
								}
							}
							rfpEvent.setErpAwardRefId(String.join(seprator, refNoList));
						}
						break;
					case RFQ:
						RfqEvent rfqEvent = rfqEventDao.findByEventId(eventId);
						if (rfqEvent != null) {
							String seprator = " , ";
							List<String> refNoList = new ArrayList<String>();
							if (CollectionUtil.isNotEmpty(resPojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardRefPojo : resPojo.getRefNumList()) {
									refNoList.add(awardRefPojo.getReferenceNumber());
								}
							}
							rfqEvent.setErpAwardRefId(String.join(seprator, refNoList));
						}
						break;
					case RFT:
						RftEvent rftEvent = rftEventDao.findByEventId(eventId);
						if (rftEvent != null) {
							String seprator1 = " , ";
							List<String> refNoList1 = new ArrayList<String>();
							if (CollectionUtil.isNotEmpty(resPojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardRefPojo : resPojo.getRefNumList()) {
									refNoList1.add(awardRefPojo.getReferenceNumber());
								}
							}
							rftEvent.setErpAwardRefId(String.join(seprator1, refNoList1));
						}
						break;
					default:
						break;

					}

				} catch (Exception e) {
					LOG.error("Error while sending Award page to ERP :" + e.getMessage(), e);
					throw e;
				}
			}

		}
		return resPojo;
	}

	@Override
	@Transactional(readOnly = false)
	public void transferPrToErp(String prId) throws Exception {

		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpSetup erpConfig = erpConfigDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
			ErpIntegrationTypeForPr type = erpConfig.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpConfig.getIsErpEnable());
			if (erpConfig != null && Boolean.TRUE == erpConfig.getIsErpEnable()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending PR to ERP...");

				Pr pr = prService.getPrForErpById(prId);
				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (type == null || ErpIntegrationTypeForPr.TYPE_1 == type) {
					PrErpPojo prErpPojo = new PrErpPojo(pr);
					String erpSeqNo = erpSetupService.genrateSquanceNumber();
					prErpPojo.setErpSeqNo(erpSeqNo);
					payload = mapperObj.writeValueAsString(prErpPojo);
					LOG.info("jsonObject  :" + payload);

					if (StringUtils.checkString(prErpPojo.getVendorCode()).length() == 0 && pr.getSupplier() != null) {
						throw new ApplicationException("Vendor Code not Assigned to Supplier");
					} else {
						LOG.info("not empty vendor code ");
					}

					headers.set(Global.X_AUTH_KEY_HEADER_PROPERTY, erpConfig.getAppId());
					HttpEntity<PrErpPojo> request = new HttpEntity<PrErpPojo>(prErpPojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/PrSendData", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}

				} else if (ErpIntegrationTypeForPr.TYPE_2 == type) {

					// Fetch the contract reference numbers and storage location...
					if (pr.getTemplate() != null && Boolean.TRUE == pr.getTemplate().getContractItemsOnly()) {
						for (PrItem item : pr.getPrItems()) {
							if (item.getProduct() != null) {
								ProductContractItems pcItem = productContractItemsDao.findProductContractItemByItemId(item.getProduct().getId(), item.getProductContractItem() != null ? item.getProductContractItem().getId() : null);
								if (pcItem != null) {
									item.setStorageLocation(pcItem.getStorageLocation());
									item.setContractReferenceNumber(pcItem.getProductContract().getContractReferenceNumber());
									item.setItemContractReferenceNumber(pcItem.getContractItemNumber());
									item.setCostCenter(pcItem.getCostCenter() != null ? pcItem.getCostCenter().getCostCenter() : null);
									item.setPurchaseGroup(pcItem.getProductContract().getGroupCodeStr());
								}
							}
						}
					}

					if (StringUtils.checkString(erpConfig.getErpUsername()).length() > 0 && StringUtils.checkString(erpConfig.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpConfig.getErpUsername()) + ":" + StringUtils.checkString(erpConfig.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					// for Type 2
					PrErp2Pojo prErp2Pojo = new PrErp2Pojo(pr);
					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2Pojo> request = new HttpEntity<PrErp2Pojo>(prErp2Pojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/PH/PRCreate/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// String responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/PrSendData", prErpPojo,
				// String.class);
				// updating PR STATUS
				pr.setStatus(PrStatus.TRANSFERRED);
				pr.setErpPrTransferred(Boolean.TRUE);
				prService.updatePr(pr);
				// Storing audit history
				try {
					PrAudit audit = new PrAudit();
					audit.setAction(PrAuditType.TRANSFERRED);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					audit.setDescription("Sucessfully transferred to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setPr(pr);
					prAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				Pr pr = prService.getPrForErpById(prId);
				PrAudit audit = new PrAudit();
				audit.setAction(PrAuditType.ERROR);
				audit.setActionDate(new Date());
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(e.getMessage());
				audit.setPr(pr);
				prAuditService.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving ERP Audit History in catch block :" + error.getMessage(), error);
			}
			LOG.error("Error while transfer pr to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * PH-2791 - FGV | Supplier Performance Integration
	 */
	@Override
	@Transactional(readOnly = true)
	public String transferSupplierPerformanceToErp(String formId) throws ErpIntegrationException {
		String responseMsg = null;
		try {

			ErpSetup erpConfig = erpConfigDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());

			LOG.info("erpConfig erp enable :" + erpConfig.getIsErpEnable());
			if (erpConfig != null && Boolean.TRUE == erpConfig.getIsErpEnable() && Boolean.TRUE == erpConfig.getEnableSupplierPerformanceErpPush()) {

				SupplierPerformanceForm form = supplierPerformanceFormDao.getFormAndCriteriaAndBusinessUnitForErpIntegration(formId);

				FavouriteSupplier favSupplier = favoriteSupplierDao.getActiveFavouriteSupplierBySupplierIdForBuyer(form.getAwardedSupplier().getId(), form.getBuyer().getId());

				if (StringUtils.checkString(favSupplier.getVendorCode()).length() == 0) {
					throw new ErpIntegrationException("Supplier code does not exist. Please update the supplier code for " + form.getAwardedSupplier().getCompanyName());
				}

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
				restTemplate = new RestTemplate(factory);
				restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
				restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
				HttpHeaders headers = new HttpHeaders();

				// BASIC AUTH HEADERS for FGV
				if (StringUtils.checkString(erpConfig.getErpUsername()).length() > 0 && StringUtils.checkString(erpConfig.getErpPassword()).length() > 0) {
					String auth = StringUtils.checkString(erpConfig.getErpUsername()) + ":" + StringUtils.checkString(erpConfig.getErpPassword());
					byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
					String authHeader = "Basic " + new String(encodedAuth);
					headers.set("Authorization", authHeader);
				}

				// Prepare the request payload
				SupplierPerformanceErpRequestPojo requestObj = new SupplierPerformanceErpRequestPojo();
				if (form.getBusinessUnit() != null && form.getBusinessUnit().getParent() != null) {
					requestObj.setBusinessUnit(form.getBusinessUnit().getParent().getUnitCode());
				} else if (form.getBusinessUnit() != null) {
					requestObj.setBusinessUnit(form.getBusinessUnit().getUnitCode());
				}
				requestObj.setSupplierCode(favSupplier.getVendorCode());
				for (SupplierPerformanceFormCriteria criteria : form.getCriteria()) {
					if (criteria != null && criteria.getOrder() == 0) {
						if (criteria.getLevel() == 1) { // 1st Main Criteria in SPM Template - PH-2791
							requestObj.setPrice(criteria != null ? criteria.getAverageScore().toString() : "0");
							requestObj.setPriceWeightage(criteria != null && criteria.getWeightage() != null ? criteria.getWeightage().setScale(2).toString() : "0");
						} else if (criteria.getLevel() == 2) { // 2nd Main Criteria in SPM Template
							requestObj.setQuality(criteria != null ? criteria.getAverageScore().toString() : "0");
							requestObj.setQualityWeightage(criteria != null && criteria.getWeightage() != null ? criteria.getWeightage().setScale(2).toString() : "0");
						} else if (criteria.getLevel() == 3) { // 3rd Main Criteria in SPM Template
							requestObj.setDelivery(criteria != null ? criteria.getAverageScore().toString() : "0");
							requestObj.setDeliveryWeightage(criteria != null && criteria.getWeightage() != null ? criteria.getWeightage().setScale(2).toString() : "0");
						} else if (criteria.getLevel() == 4) { // 4th Main Criteria in SPM Template
							requestObj.setService(criteria != null ? criteria.getAverageScore().toString() : "0");
							requestObj.setServiceWeightage(criteria != null && criteria.getWeightage() != null ? criteria.getWeightage().setScale(2).toString() : "0");
						}
					}
				}

				requestObj.setAllScore(form.getOverallScore() != null ? form.getOverallScore().toString() : "0");

				payload = mapperObj.writeValueAsString(requestObj);
				LOG.info("ERP Request Payload for SPM " + form.getFormId() + " :" + payload);

				HttpEntity<SupplierPerformanceErpRequestPojo> request = new HttpEntity<SupplierPerformanceErpRequestPojo>(requestObj, headers);
				try {
					// URL for testing client FGD840:
					// https://sappoq.fgvholdings.com/RESTAdapter/FGD/SupplierPerformMgmt/
					responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/SupplierPerformMgmt/", request, String.class);

					if (StringUtils.checkString(responseMsg).length() > 0) {
						SupplierPerformanceErpResponsePojo res = mapperObj.readValue(responseMsg, SupplierPerformanceErpResponsePojo.class);
						if (StringUtils.checkString(res.getRes().getRes().getType()).equalsIgnoreCase("E")) {
							throw new ErpIntegrationException("Error received from ERP : " + responseMsg);
						}
					}

				} catch (HttpClientErrorException | HttpServerErrorException ex) {
					responseMsg = ex.getMessage();
					LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
					LOG.error("Response Body : " + ex.getResponseBodyAsString());
					throw new ErpIntegrationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
				}
			}
		} catch (ErpIntegrationException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Error while transfer Supplier Performance to erp :" + e.getMessage(), e);
			throw new ErpIntegrationException("Error while transfer Supplier Performance to erp :" + e.getMessage(), e);
		}
		return responseMsg;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public void transferRfsToErp(String rfsId, ErpSetup erpSetup, User loggedInUser) throws Exception {

		SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(rfsId);
		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending PR to ERP...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				PrResponsePojo responseMsg = new PrResponsePojo();
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingRequestBqs()) && sourcingFormRequest.getSourcingRequestBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send request to ERP");
					}

					// for Type 2

					PrErp2Pojo prErp2Pojo = new PrErp2Pojo(sourcingFormRequest);
					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2Pojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), item.getProductItemType());
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2Pojo> request = new HttpEntity<PrErp2Pojo>(prErp2Pojo, headers);

					if (CollectionUtil.isEmpty(prErp2Pojo.getPrItems())) {
						LOG.warn("No Line Items found for this sourcing form. Not going to send it to ERP : " + sourcingFormRequest.getId());
						return;
					}

					try {
						PrResponseWrapper responseWrapper = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRCreate/", request, PrResponseWrapper.class);
						LOG.info("Response wrapper: "+ responseWrapper);
						responseMsg = responseWrapper.getPrResponsePojo();
						LOG.info("Original Response: "+responseMsg);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// updating STATUS
				sourcingFormRequest.setErpTransferred(Boolean.TRUE);
				sourcingFormRequest.setStatus(SourcingFormStatus.PENDING);
				if (sourcingFormRequest.getSubmittedDate() == null) {
					sourcingFormRequest.setSubmittedDate(new Date());
				}
				requestService.update(sourcingFormRequest);

				// Response changes to async
				RequestAudit audit = new RequestAudit();
				if(responseMsg != null && !StringUtils.checkString(responseMsg.getStatus()).equalsIgnoreCase("FAIL")) {
					// success response
					if (responseMsg.getOperation() != OperationType.D) {
						sourcingFormRequest = approvalService.doRequestApproval(sourcingFormRequest, sourcingFormRequest.getFormOwner());

						String message = "";
						try {
							if (responseMsg.getMessage() instanceof List) {
								HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) responseMsg.getMessage()).get(0);
								message = hashMap.get("msg_item");
							} else {
								HashMap<String, String> hashMap = (HashMap<String, String>) responseMsg.getMessage();
								message = hashMap.get("msg_item");
							}
						} catch (Exception k) {

						}

						sourcingFormRequest.setErpDocNo(responseMsg.getSapRefNo());
						sourcingFormRequest.setErpMessage(message);
						sourcingFormRequest.setErpTransferred(Boolean.TRUE);
						sourcingFormRequest.setErpStatus(responseMsg.getStatus());
						sourcingFormRequest = requestService.update(sourcingFormRequest);
						audit.setDescription(responseMsg.getSapDocType() + " created in ERP with Reference : " + responseMsg.getSapRefNo() + " Response: " + responseMsg.getMessage());
					} else {
						audit.setDescription(responseMsg.getSapDocType() + " deleted in ERP with Reference : " + responseMsg.getSapRefNo() + " Response: " + responseMsg.getMessage());
					}
					audit.setAction(RequestAuditType.ERP);
				} else {
					// Fail response
					audit.setAction(RequestAuditType.ERROR);
					if (responseMsg.getOperation() == OperationType.D) {
						audit.setDescription("Received failed response from ERP for Delete : " + responseMsg.getMessage() + " Ref: " + responseMsg.getSapRefNo());
					} else {
						sourcingFormRequest.setStatus(SourcingFormStatus.DRAFT);
						sourcingFormRequest.setSummaryCompleted(false);
						sourcingFormRequest.setErpDocNo(null);

						String message = "";
						try {
							if (responseMsg.getMessage() instanceof List) {
								HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) responseMsg.getMessage()).get(0);
								message = hashMap.get("msg_item");
							} else {
								HashMap<String, String> hashMap = (HashMap<String, String>) responseMsg.getMessage();
								message = hashMap.get("msg_item");
							}
						} catch (Exception k) {

						}

						sourcingFormRequest.setErpMessage(message);
						sourcingFormRequest.setErpTransferred(Boolean.FALSE);
						sourcingFormRequest.setErpStatus(responseMsg.getStatus());
						sourcingFormRequest = requestService.update(sourcingFormRequest);

						audit.setDescription("Received failed response from ERP : " + responseMsg.getMessage() + " Ref: " + responseMsg.getSapRefNo());
					}
				}

				// Storing audit history
				try {
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer();
					buyer.setId(sourcingFormRequest.getTenantId());
					audit.setBuyer(buyer);
					audit.setReq(sourcingFormRequest);
					requestAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving Sourcing ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.ERROR);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				Buyer buyer = new Buyer();
				buyer.setId(sourcingFormRequest.getTenantId());
				audit.setBuyer(buyer);
				audit.setDescription(e.getMessage());
				audit.setReq(sourcingFormRequest);
				requestAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving Sourcing ERP Audit History in catch block :" + error.getMessage(), error);
			}
			LOG.error("Error while transfer pr to erp :" + e.getMessage(), e);
			throw e;
		}
	}


	@Override
	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public SapResponseToTransferAward transferRfxAwardToErp(RfxTypes rfxTypes ,String eventId, ErpSetup erpSetup, User loggedInUser, String awardId,
															HttpSession session) throws Exception {
		Event event = null;
		SapResponseToTransferAward sapResponseToTransferAward = new SapResponseToTransferAward();

		try {

			if (rfxTypes.equals(RfxTypes.RFP)) {
				event = rfpEventService.getEventById(eventId);
			} else if (rfxTypes.equals(RfxTypes.RFA)) {
				event = rfaEventService.getRfaEventById(eventId);
			} else if (rfxTypes.equals(RfxTypes.RFQ)) {
				event = rfqEventService.getEventById(eventId);
			} else if (rfxTypes.equals(RfxTypes.RFT)) {
				event = rftEventService.getRftEventById(eventId);
			}


			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForAward type = erpSetup.getErpIntegrationTypeForAward();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending PR to ERP...");

				if (ErpIntegrationTypeForAward.TYPE_3 == type) {
					// here sap-client=300 is haredcoded for SAP . Later can be changed
					String link = erpSetup.getErpUrl()+"?sap-client=300";
					URL url = new URL(link);
					HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
					BasicCredentialsProvider provider = new BasicCredentialsProvider();
					AuthScope authScope = new AuthScope(targetHost);
					provider.setCredentials(authScope, new UsernamePasswordCredentials(erpSetup.getErpUsername(), erpSetup.getErpPassword()));

					RfxErpPojoAward rfpErpPojoAward = convertToPrErpPojoAward(rfxTypes, event);

					ObjectMapper objectMapper = new ObjectMapper();

					String jsonPayload = objectMapper.writeValueAsString(rfpErpPojoAward);
					StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

					LOG.info("JsonPayload :" + jsonPayload);

					HttpPost post = new HttpPost(link);
					post.setEntity(entity);
					post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());


					try (CloseableHttpClient client = createSelfSignedHttpBuilder().setDefaultCredentialsProvider(provider).build();
						 CloseableHttpResponse res = client.execute(post)) {
						String jsonResponse = EntityUtils.toString(res.getEntity());
						// remove trailing comma(,) from the json response
						jsonResponse = jsonResponse.replaceAll(",\\s*\\}", "}");

						// Parse the JSON response using Gson
						Gson gson = new Gson();
						sapResponseToTransferAward = gson.fromJson(jsonResponse, SapResponseToTransferAward.class);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				LOG.info("response :" + sapResponseToTransferAward);

				// updating STATUS
				if(sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
					if (rfxTypes.equals(RfxTypes.RFP)) {
						RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
						rfpEvent.setTransfrAwrdRespFlag(Boolean.TRUE);
						rfpEventDao.saveOrUpdate(rfpEvent);

						// saving transfer award in the award audit
						RfpEventAward dbEventAward = rfpEventAwardDao.findById(awardId);
						JasperPrint eventAward1 = rfpAwardService.getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, true, false);
						ByteArrayOutputStream workbook = rfpAwardService.getAwardSnapShotXcel(dbEventAward, session, loggedInUser, null, true, false);
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						RfpEventAwardAudit audit = new RfpEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfpAwardAudit(audit);
					} else if (rfxTypes.equals(RfxTypes.RFA)) {
						RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
						rfaEvent.setTransfrAwrdRespFlag(Boolean.TRUE);
						rfaEventDao.saveOrUpdate(rfaEvent);

						// saving transfer award in the award audit
						RfaEventAward dbEventAward = rfaEventAwardDao.findById(awardId);
						JasperPrint eventAward1 = rfaAwardService.getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, true, false);
						ByteArrayOutputStream workbook = rfaAwardService.getAwardSnapShotXcel(dbEventAward, session, loggedInUser, null, true, false);
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						RfaEventAwardAudit audit = new RfaEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfaAwardAudit(audit);
					} else if (rfxTypes.equals(RfxTypes.RFQ)) {
						RfqEvent rfqEvent = rfqEventService.getEventById(eventId);
						rfqEvent.setTransfrAwrdRespFlag(Boolean.TRUE);
						rfqEventDao.saveOrUpdate(rfqEvent);

						// saving transfer award in the award audit
						RfqEventAward dbEventAward = rfqEventAwardDao.findById(awardId);
						JasperPrint eventAward1 = rfqAwardService.getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, true, false);
						ByteArrayOutputStream workbook = rfqAwardService.getAwardSnapShotXcel(dbEventAward, session, loggedInUser, null, true, false);
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						RfqEventAwardAudit audit = new RfqEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfqAwardAudit(audit);
					} else if (rfxTypes.equals(RfxTypes.RFT)) {
						RftEvent rftEvent = rftEventService.getRftEventById(eventId);
						rftEvent.setTransfrAwrdRespFlag(Boolean.TRUE);
						rftEventDao.saveOrUpdate(rftEvent);

						// saving transfer award in the award audit
						RftEventAward dbEventAward = rftEventAwardDao.findById(awardId);
						JasperPrint eventAward1 = rftAwardService.getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, true, false);
						ByteArrayOutputStream workbook = rftAwardService.getAwardSnapshotExcel(dbEventAward, session, loggedInUser, null, true, false);
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						RftEventAwardAudit audit = new RftEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRftAwardAudit(audit);
					}
				}
				else {
					if (rfxTypes.equals(RfxTypes.RFP)) {
						RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
						rfpEvent.setStatus(EventStatus.COMPLETE);
						rfpEvent.setAwarded(false);
						rfpEvent.setAwardStatus(AwardStatus.DRAFT);
						rfpEventDao.saveOrUpdate(rfpEvent);
					} else if (rfxTypes.equals(RfxTypes.RFA)) {
						RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
						rfaEvent.setStatus(EventStatus.COMPLETE);
						rfaEvent.setAwarded(false);
						rfaEvent.setAwardStatus(AwardStatus.DRAFT);
						rfaEventDao.saveOrUpdate(rfaEvent);
					} else if (rfxTypes.equals(RfxTypes.RFQ)) {
						RfqEvent rfqEvent = rfqEventService.getEventById(eventId);
						rfqEvent.setStatus(EventStatus.COMPLETE);
						rfqEvent.setAwarded(false);
						rfqEvent.setAwardStatus(AwardStatus.DRAFT);
						rfqEventDao.saveOrUpdate(rfqEvent);
					} else if (rfxTypes.equals(RfxTypes.RFT)) {
						RftEvent rftEvent = rftEventService.getRftEventById(eventId);
						rftEvent.setAwarded(false);
						rftEvent.setAwardStatus(AwardStatus.DRAFT);
						rftEvent.setStatus(EventStatus.COMPLETE);
						rftEventDao.saveOrUpdate(rftEvent);
					}
				}

				if (rfxTypes.equals(RfxTypes.RFQ)) {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setAction(AuditActionType.Transfer);
					if( sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
                        audit.setDescription("ERP replied SUCCESS for "+sapResponseToTransferAward.getMessage()+" with reference to  Event ID: "+event.getEventId());
					}
					else {
						audit.setDescription("ERP replied FAIL for RFQ with reference: Create Fail Response: "+sapResponseToTransferAward.getMessage());
					}
					RfqEvent rfqEvent = new RfqEvent();
					rfqEvent.setId(eventId);
					audit.setEvent(rfqEvent);
					eventAuditService.save(audit);
				} else if (rfxTypes.equals(RfxTypes.RFI)) {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setAction(AuditActionType.Transfer);
					if( sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
						audit.setDescription("ERP replied SUCCESS for "+sapResponseToTransferAward.getMessage()+" with reference to  Event ID: "+event.getEventId());
					}
					else {
						audit.setDescription("ERP replied FAIL for RFI with reference: Create Fail Response: "+sapResponseToTransferAward.getMessage());
					}
					RfiEvent rfiEvent = new RfiEvent();
					rfiEvent.setId(eventId);
					audit.setEvent(rfiEvent);
					eventAuditService.save(audit);
				} else if (rfxTypes.equals(RfxTypes.RFT)) {
					RftEventAudit audit = new RftEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setAction(AuditActionType.Transfer);
					if( sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
						audit.setDescription("ERP replied SUCCESS for "+sapResponseToTransferAward.getMessage()+" with reference to  Event ID: "+event.getEventId());
					}
					else {
						audit.setDescription("ERP replied FAIL for RFT with reference: Create Fail Response: "+sapResponseToTransferAward.getMessage());
					}
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(eventId);
					audit.setEvent(rftEvent);
					eventAuditService.save(audit);
				} else if (rfxTypes.equals(RfxTypes.RFP)) {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setAction(AuditActionType.Transfer);
					if( sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
						audit.setDescription("ERP replied SUCCESS for "+sapResponseToTransferAward.getMessage()+" with reference to  Event ID: "+event.getEventId());
					}
					else {
						audit.setDescription("ERP replied FAIL for RFP with reference: Create Fail Response: "+sapResponseToTransferAward.getMessage());
					}
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(eventId);
					audit.setEvent(rfpEvent);
					eventAuditService.save(audit);
				}

				// Storing audit history
				try {
					RequestAudit audit = new RequestAudit();
					audit.setAction(RequestAuditType.ERP);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer();
					buyer.setId(event.getTenantId());
					audit.setBuyer(buyer);
					audit.setDescription("Request sent to ERP. Response : " + StringUtils.checkString(sapResponseToTransferAward.getMessage()));
					requestAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving Sourcing ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.ERROR);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				Buyer buyer = new Buyer();
				buyer.setId(event.getTenantId());
				audit.setBuyer(buyer);
				audit.setDescription(e.getMessage());
				requestAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving Sourcing ERP Audit History in catch block :" + error.getMessage(), error);
			}
			LOG.error("Error while transfer pr to erp :" + e.getMessage(), e);
			throw e;
		}
		return sapResponseToTransferAward;
	}

	public static HttpClientBuilder createSelfSignedHttpBuilder() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		}};

		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, trustAllCerts, null);

		HttpClientBuilder builder = HttpClientBuilder.create();
		SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		builder.setSSLSocketFactory(sslFactory);

		PlainConnectionSocketFactory httpFactory = new PlainConnectionSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslFactory)
				.register("http", httpFactory)
				.build();

		HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
		builder.setConnectionManager(ccm);
		return builder;
	}

	private CloseableHttpClient createSelfSignedHttpClient() throws Exception {
		return createSelfSignedHttpBuilder().build();
	}


	@Override
	@Transactional(readOnly = false)
	public void transferRejectRfsToErp(String rfsId, ErpSetup erpSetup, User loggedInUser) throws Exception {

		SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(rfsId);
		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending PR to ERP...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingRequestBqs()) && sourcingFormRequest.getSourcingRequestBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send request to ERP");
					}

					// Check if ERP Doc No exists. If not, then probably this RFS is not sent for approval to ERP.
					// Therefore no need to send cancel request to ERP.
					if (StringUtils.checkString(sourcingFormRequest.getErpDocNo()).length() == 0) {
						LOG.warn("No Erp Doc No found for this sourcing form. Not going to send it to ERP : " + sourcingFormRequest.getId());
						return;
					}

					// for Type 2
					PrErp2DeletePojo prErp2Pojo = new PrErp2DeletePojo(sourcingFormRequest);

					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2DeletePojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), null);
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2DeletePojo> request = new HttpEntity<PrErp2DeletePojo>(prErp2Pojo, headers);

					if (CollectionUtil.isEmpty(prErp2Pojo.getPrItems())) {
						LOG.warn("No Line Items found for this sourcing form. Not going to send it to ERP : " + sourcingFormRequest.getId());
						return;
					}

					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRDelete/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP for RFS Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP for RFS Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// Storing audit history
				try {
					RequestAudit audit = new RequestAudit();
					audit.setAction(RequestAuditType.ERP);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer();
					buyer.setId(sourcingFormRequest.getTenantId());
					audit.setBuyer(buyer);
					audit.setDescription("Delete RFS Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setReq(sourcingFormRequest);
					requestAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving Sourcing ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.ERROR);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				Buyer buyer = new Buyer();
				buyer.setId(sourcingFormRequest.getTenantId());
				audit.setBuyer(buyer);
				audit.setDescription(e.getMessage());
				audit.setReq(sourcingFormRequest);
				requestAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving Sourcing ERP Audit History in catch block :" + error.getMessage(), error);
			}
			LOG.error("Error while transfer pr to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void transferRejectRfsToErp(RfqEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception {

		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending CANCEL PR to ERP due to RFQ Cancel...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					// Fetch the contract reference numbers and storage location...

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(event.getEventBqs()) && event.getEventBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send Cancel request to ERP");
					}

					if (StringUtils.checkString(event.getPreviousRequestId()).length() == 0) {
						throw new ApplicationException("Looks like this RFQ isnt created from RFS. Cannot send delete request to ERP");
					}

					SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(event.getPreviousRequestId());

					// for Type 2
					PrErp2DeletePojo prErp2Pojo = new PrErp2DeletePojo(event, sourcingFormRequest.getFormId(), sourcingFormRequest.getErpDocNo());

					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2DeletePojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), null);
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2DeletePojo> request = new HttpEntity<PrErp2DeletePojo>(prErp2Pojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRDelete/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP for RFQ Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP for RFQ Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// Storing audit history
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Transfer);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());

					audit.setBuyer(loggedInUser.getBuyer());
					audit.setDescription("Delete RFQ Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setEvent(event);
					rfqEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving RFQ ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Delete RFQ Request sent to ERP. Response : " + StringUtils.checkString(responseMsg), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RfqEventAudit audit = new RfqEventAudit();
				audit.setAction(AuditActionType.Transfer);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				audit.setBuyer(loggedInUser.getBuyer());
				audit.setDescription(e.getMessage());
				audit.setEvent(event);
				rfqEventAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving RFQ ERP Audit History in catch block :" + error.getMessage(), error);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, e.getMessage(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e1) {
				LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
			}
			LOG.error("Error while transfer Cancel Request to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void transferRejectRfsToErp(RftEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception {

		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending CANCEL PR to ERP due to RFT Cancel...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					// Fetch the contract reference numbers and storage location...

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(event.getEventBqs()) && event.getEventBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send Cancel request to ERP");
					}

					if (StringUtils.checkString(event.getPreviousRequestId()).length() == 0) {
						throw new ApplicationException("Looks like this RFT isnt created from RFS. Cannot send delete request to ERP");
					}

					SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(event.getPreviousRequestId());

					// for Type 2
					PrErp2DeletePojo prErp2Pojo = new PrErp2DeletePojo(event, sourcingFormRequest.getFormId(), sourcingFormRequest.getErpDocNo());
					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2DeletePojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), null);
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2DeletePojo> request = new HttpEntity<PrErp2DeletePojo>(prErp2Pojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRDelete/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP for RFT Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP for RFT Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// Storing audit history
				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Transfer);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					audit.setBuyer(loggedInUser.getBuyer());
					audit.setDescription("Delete RFT Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setEvent(event);
					rftEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving RFT ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Delete RFT Request sent to ERP. Response : " + StringUtils.checkString(responseMsg), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e1) {
					LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setAction(AuditActionType.Transfer);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(e.getMessage());
				audit.setEvent(event);
				rftEventAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving RFT ERP Audit History in catch block :" + error.getMessage(), error);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, e.getMessage(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e1) {
				LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
			}
			LOG.error("Error while transfer Cancel Request to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void transferRejectRfsToErp(RfaEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception {

		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending CANCEL PR to ERP due to RFA Cancel...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					// Fetch the contract reference numbers and storage location...

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(event.getEventBqs()) && event.getEventBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send Cancel request to ERP");
					}

					if (StringUtils.checkString(event.getPreviousRequestId()).length() == 0) {
						throw new ApplicationException("Looks like this RFA isnt created from RFS. Cannot send delete request to ERP");
					}

					SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(event.getPreviousRequestId());

					// for Type 2
					PrErp2DeletePojo prErp2Pojo = new PrErp2DeletePojo(event, sourcingFormRequest.getFormId(), sourcingFormRequest.getErpDocNo());
					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2DeletePojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), null);
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2DeletePojo> request = new HttpEntity<PrErp2DeletePojo>(prErp2Pojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRDelete/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP for RFA Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP for RFA Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// Storing audit history
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Transfer);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					audit.setBuyer(loggedInUser.getBuyer());
					audit.setDescription("Delete RFA Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setEvent(event);
					rfaEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving RFA ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Delete RFA Request sent to ERP. Response : " + StringUtils.checkString(responseMsg), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e1) {
					LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setAction(AuditActionType.Transfer);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				audit.setBuyer(loggedInUser.getBuyer());
				audit.setDescription(e.getMessage());
				audit.setEvent(event);
				rfaEventAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving RFA ERP Audit History in catch block :" + error.getMessage(), error);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, e.getMessage(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e1) {
				LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
			}
			LOG.error("Error while transfer Cancel Request to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void transferRejectRfsToErp(RfpEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception {

		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending CANCEL PR to ERP due to RFP Cancel...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					// Fetch the contract reference numbers and storage location...

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					if (CollectionUtil.isNotEmpty(event.getEventBqs()) && event.getEventBqs().size() > 1) {
						throw new ApplicationException("Multiple BQs found. Cannot send Cancel request to ERP");
					}

					if (StringUtils.checkString(event.getPreviousRequestId()).length() == 0) {
						throw new ApplicationException("Looks like this RFP isnt created from RFS. Cannot send delete request to ERP");
					}

					SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(event.getPreviousRequestId());

					// for Type 2
					PrErp2DeletePojo prErp2Pojo = new PrErp2DeletePojo(event, sourcingFormRequest.getFormId(), sourcingFormRequest.getErpDocNo());
					// Checking for interface code if exisit send interface code to erp insted of product code
					for (PrItemErp2DeletePojo item : prErp2Pojo.getPrItems()) {
						ProductItem productItem = productListMaintenanceDao.findProductItemByCode(item.getItemCode(), erpSetup.getTenantId(), null);
						if (productItem != null && StringUtils.checkString(productItem.getInterfaceCode()).length() > 0) {
							item.setItemCode(productItem.getInterfaceCode());
						}
					}

					payload = mapperObj.writeValueAsString(prErp2Pojo);
					LOG.info("jsonObject  :" + payload);

					HttpEntity<PrErp2DeletePojo> request = new HttpEntity<PrErp2DeletePojo>(prErp2Pojo, headers);
					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRDelete/", request, String.class);
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP for RFP Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ApplicationException("Error received from ERP for RFP Delete : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				LOG.info("response :" + responseMsg);

				// Storing audit history
				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Transfer);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer(loggedInUser.getTenantId());
					audit.setBuyer(buyer);
					audit.setDescription("Delete RFP Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setEvent(event);
					rfpEventAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving RFP ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Delete RFP Request sent to ERP. Response : " + StringUtils.checkString(responseMsg), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e1) {
					LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				RfpEventAudit audit = new RfpEventAudit();
				audit.setAction(AuditActionType.Transfer);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				Buyer buyer = new Buyer(loggedInUser.getTenantId());
				audit.setBuyer(buyer);
				audit.setDescription(e.getMessage());
				audit.setEvent(event);
				rfpEventAuditDao.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving RFP ERP Audit History in catch block :" + error.getMessage(), error);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, e.getMessage(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e1) {
				LOG.error("Error while recording auction event Resume " + e1.getMessage(), e1);
			}
			LOG.error("Error while transfer Cancel Request to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public List<MobileEventPojo> getEventTypeFromPrNo(String prNo, String tenantId) {
		return erpConfigDao.getEventTypeFromPrNo(prNo, tenantId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public RfaEvent overwriteFromRfaTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException {
		RfaEvent newEvent = rfaEventDao.findByEventId(draftEventPojo.getId());
		if (newEvent != null) {
			rfaBqService.deleteAllERPBqByEventId(newEvent.getId());
		}

		newEvent = constructRfaEventDetials(prToAuctionErpPojo, newEvent);
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public RfqEvent overwriteRfqTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException {
		RfqEvent newEvent = rfqEventDao.findByEventId(draftEventPojo.getId());
		if (newEvent != null) {
			rfqBqService.deleteAllERPBqByEventId(newEvent.getId());
		}
		newEvent = constructRfqEventDetials(prToAuctionErpPojo, newEvent);
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public RfpEvent overwriteRfpTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException {
		RfpEvent newEvent = rfpEventDao.findByEventId(draftEventPojo.getId());
		if (newEvent != null) {
			rfpBqService.deleteAllERPBqByEventId(newEvent.getId());
		}
		newEvent = constructRfpEventDetials(prToAuctionErpPojo, newEvent);
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public RftEvent overwriteRftTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException {
		RftEvent newEvent = rftEventDao.findByEventId(draftEventPojo.getId());
		if (newEvent != null) {
			rftBqService.deleteAllERPBqByEventId(newEvent.getId());
		}
		newEvent = constructRftEventDetials(prToAuctionErpPojo, newEvent);
		return newEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfqResponse(RFQResponseErpPojo rfqResponseErpPojo, ErpSetup erpSetup) {
		Buyer buyer = buyerService.findBuyerById(erpSetup.getTenantId());
		RfaEvent rfaEvent = rfaEventService.findRfaEventByErpAwardRefNoAndTenantId(rfqResponseErpPojo.getRFQ_docNo(), erpSetup.getTenantId());
		if (rfaEvent != null) {
			try {
				String response = StringUtils.checkString(rfaEvent.getErpAwardResponse());
				response += ",DOCNO=" + rfqResponseErpPojo.getRFQ_docNo() + ";STATUS=" + rfqResponseErpPojo.getStatus();
				rfaEvent.setErpAwardResponse(response);
				rfaEventService.updateRfaEvent(rfaEvent);
				if (rfqResponseErpPojo.getStatus().equalsIgnoreCase("ERROR")) {
					try {
						if (rfaEvent.getCreatedBy() != null) {
							sendRfxAwardTransferErrorEmail(rfaEvent.getCreatedBy(), RfxTypes.RFA, rfaEvent.getEventName(), rfaEvent.getId(), rfaEvent.getEventId(), rfqResponseErpPojo.getRFQ_docNo(), rfqResponseErpPojo.getMessage());

						}
					} catch (Exception e) {
						LOG.error("Error while Sending mail:" + e.getMessage(), e);
					}
					RfaEventAwardAudit audit = new RfaEventAwardAudit(rfaEvent, buyer, null, new Date(), "Error While transfer award to SAP:" + rfqResponseErpPojo.getMessage());
					eventAwardAuditService.saveRfaAwardAudit(audit);
				}
			} catch (Exception e) {
				RfaEventAwardAudit audit = new RfaEventAwardAudit(rfaEvent, buyer, null, new Date(), "Error While Processing SAP Response:" + e.getMessage());
				eventAwardAuditService.saveRfaAwardAudit(audit);
			}
		} else {
			RfpEvent rfpEvent = rfpEventService.findRfpEventByErpAwardRefNoAndTenantId(rfqResponseErpPojo.getRFQ_docNo(), erpSetup.getTenantId());
			if (rfpEvent != null) {
				try {
					String response = StringUtils.checkString(rfpEvent.getErpAwardResponse());
					response += ",DOCNO=" + rfqResponseErpPojo.getRFQ_docNo() + ";STATUS=" + rfqResponseErpPojo.getStatus();
					LOG.info("RES: " + response + ".");
					rfpEvent.setErpAwardResponse(response);
					rfpEventService.updateRfpEvent(rfpEvent);
					if (rfqResponseErpPojo.getStatus().equalsIgnoreCase("ERROR")) {
						try {
							if (rfpEvent.getCreatedBy() != null) {
								sendRfxAwardTransferErrorEmail(rfpEvent.getCreatedBy(), RfxTypes.RFP, rfpEvent.getEventName(), rfpEvent.getId(), rfpEvent.getEventId(), rfqResponseErpPojo.getRFQ_docNo(), rfqResponseErpPojo.getMessage());
							}
						} catch (Exception e) {
							LOG.error("Error while Sending mail :" + e.getMessage(), e);
						}
						RfpEventAwardAudit audit = new RfpEventAwardAudit(rfpEvent, buyer, null, new Date(), "Error While transfer award to SAP:" + rfqResponseErpPojo.getMessage());
						eventAwardAuditService.saveRfpAwardAudit(audit);

					}
				} catch (Exception e) {
					RfpEventAwardAudit audit = new RfpEventAwardAudit(rfpEvent, buyer, null, new Date(), "Error While Processing SAP Response" + e.getMessage());
					eventAwardAuditService.saveRfpAwardAudit(audit);
				}
			} else {
				RftEvent rftEvent = rftEventService.findRftEventByErpAwardRefNoAndTenantId(rfqResponseErpPojo.getRFQ_docNo(), erpSetup.getTenantId());
				if (rftEvent != null) {
					try {
						String response = StringUtils.checkString(rftEvent.getErpAwardResponse());
						response += ",DOCNO=" + rfqResponseErpPojo.getRFQ_docNo() + ";STATUS=" + rfqResponseErpPojo.getStatus();
						rftEvent.setErpAwardResponse(response);
						rftEventService.updateRftEvent(rftEvent);
						if (rfqResponseErpPojo.getStatus().equalsIgnoreCase("ERROR")) {
							try {
								if (rftEvent.getCreatedBy() != null) {
									sendRfxAwardTransferErrorEmail(rftEvent.getCreatedBy(), RfxTypes.RFT, rftEvent.getEventName(), rftEvent.getId(), rftEvent.getEventId(), rfqResponseErpPojo.getRFQ_docNo(), rfqResponseErpPojo.getMessage());
								}
							} catch (Exception e) {
								LOG.error("Error while Sending mail :" + e.getMessage(), e);
							}

							RftEventAwardAudit audit = new RftEventAwardAudit(rftEvent, buyer, null, new Date(), "Error While transfer award to SAP:" + rfqResponseErpPojo.getMessage());
							eventAwardAuditService.saveRftAwardAudit(audit);
						}
					} catch (Exception e) {

						RftEventAwardAudit audit = new RftEventAwardAudit(rftEvent, buyer, null, new Date(), "Error While Processing SAP Response:" + e.getMessage());
						eventAwardAuditService.saveRftAwardAudit(audit);

					}
				} else {
					RfqEvent rfqEvent = rfqEventService.findRfqEventByErpAwardRefNoAndTenantId(rfqResponseErpPojo.getRFQ_docNo(), erpSetup.getTenantId());
					try {
						if (rfqEvent != null) {
							String response = StringUtils.checkString(rfqEvent.getErpAwardResponse());
							response += ",REF_NO" + rfqResponseErpPojo.getRFQ_docNo() + ";STATUS" + rfqResponseErpPojo.getStatus();
							rfqEvent.setErpAwardResponse(response);
							rfqEventService.updateEvent(rfqEvent);
							if (rfqResponseErpPojo.getStatus().equalsIgnoreCase("ERROR")) {
								try {
									if (rfqEvent.getCreatedBy() != null) {
										sendRfxAwardTransferErrorEmail(rfqEvent.getCreatedBy(), RfxTypes.RFQ, rfqEvent.getEventName(), rfqEvent.getId(), rfqEvent.getEventId(), rfqResponseErpPojo.getRFQ_docNo(), rfqResponseErpPojo.getMessage());

									}
								} catch (Exception e) {
									LOG.error("Error while sending mail:" + e.getMessage(), e);

								}

								RfqEventAwardAudit audit = new RfqEventAwardAudit(rfqEvent, buyer, null, new Date(), "Error While transfer award to SAP:" + rfqResponseErpPojo.getMessage());
								eventAwardAuditService.saveRfqAwardAudit(audit);
							}
						} else {

							LOG.warn("No Matching event found for ERP Award Ref No : " + rfqResponseErpPojo.getRFQ_docNo());

						}
					} catch (Exception e) {

						RfqEventAwardAudit audit = new RfqEventAwardAudit(rfqEvent, buyer, null, new Date(), "Error While Processing SAP Response:" + e.getMessage());
						eventAwardAuditService.saveRfqAwardAudit(audit);

					}

				}
			}
		}

	}

	private void sendRfxAwardTransferErrorEmail(User user, RfxTypes eventType, String eventName, String id, String eventId, String referanceNumber, String remarks) {
		LOG.info("Sending error request email to(" + user.getName() + "):" + user.getCommunicationEmail());
		LOG.info("EventType-----------:" + eventType.name());
		String url = APP_URL + "/buyer/" + eventType.name() + "/eventAward/" + id + "";
		String subject = "Award transfer failed to SAP";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", user.getName());
		map.put("remarks", remarks);
		map.put("eventName", eventName);
		map.put("referanceNumber", referanceNumber);
		LOG.info("RefrenceNumber----------" + referanceNumber);
		map.put("message", "Failed award transfer to SAP");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if (StringUtils.checkString(user.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
			LOG.info("User Communication email-----------------" + user.getCommunicationEmail());
			sendEmail(user.getCommunicationEmail(), subject, map, Global.ERP_EVENT_REJECT_TEMPLATE);

		} else {
			LOG.warn("No communication email configured for user : " + user.getLoginId() + "... Not going to send email notification");
		}
		LOG.info("Noitification called here----------------");
		String notificationMessage = messageSource.getMessage("erp.rfx.error.notification.message", new Object[] { eventName, eventId, remarks }, Global.LOCALE);
		sendDashboardNotification(id, user, url, subject, notificationMessage, NotificationType.EVENT_MESSAGE);

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

	private void sendEmail(String mailTo, String subject, HashMap<String, Object> map, String template) {
		LOG.info("mailTo-----" + mailTo);
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

	private void sendDashboardNotification(String id, User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		try {
			LOG.info("Dashboard notification---------------");
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
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

	@Override
	public void sendPoAcceptDeclineToErp(PoAcceptDeclinePojo poAcceptDeclinePojo, ErpSetup erpSetup) throws Exception {
		try {
			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
				// sending PO Accept/Decline to ERP
				LOG.info("ERP Integration is enabled.... sending PO Accept/Decline to ERP...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				Po po = poService.findById(poAcceptDeclinePojo.getId());
				payload = mapperObj.writeValueAsString(poAcceptDeclinePojo);
				LOG.info("jsonObject  :" + payload);

				headers.set(Global.X_AUTH_KEY_HEADER_PROPERTY, erpSetup.getAppId());
				HttpEntity<PoAcceptDeclinePojo> request = new HttpEntity<PoAcceptDeclinePojo>(poAcceptDeclinePojo, headers);
				try {
					restTemplate.postForObject(erpSetup.getErpUrl() + "/poAcceptDecline", request, String.class);
				} catch (HttpClientErrorException | HttpServerErrorException ex) {
					LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
					LOG.error("Response Body : " + ex.getResponseBodyAsString());
					throw new ApplicationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
				}

				// Storing audit history
				try {
					PoAudit audit = new PoAudit();
					if (Boolean.TRUE == poAcceptDeclinePojo.getAccepted()) {
						audit.setAction(PoAuditType.ACCEPTED);
					} else {
						audit.setAction(PoAuditType.DECLINED);
					}
					audit.setActionBy(po.getCreatedBy());
					audit.setActionDate(new Date());
					audit.setBuyer(po.getBuyer());
					audit.setDescription("Sucessfully sent to ERP.");
					audit.setPo(poService.findById(poAcceptDeclinePojo.getId()));
					audit.setVisibilityType(PoAuditVisibilityType.BUYER);
					poAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving ERP Audit History :" + e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			// Storing audit history for error
			try {
				Po po = poService.findById(poAcceptDeclinePojo.getId());
				PoAudit audit = new PoAudit();
				audit.setAction(PoAuditType.ERROR);
				audit.setActionDate(new Date());
				audit.setActionBy(po.getCreatedBy());
				audit.setBuyer(po.getBuyer());
				audit.setDescription("Failed to send to ERP. " + e.getMessage());
				audit.setPo(po);
				audit.setVisibilityType(PoAuditVisibilityType.BUYER);
				poAuditService.save(audit);
			} catch (Exception error) {
				LOG.error("Error while saving ERP Audit History in catch block :" + error.getMessage(), error);
			}
			LOG.error("Error while transfer po accept/decline to erp :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void createContractInErp(String contractId, ErpSetup erpSetup, User loggedInUser) throws Exception, ErpIntegrationException {

		ProductContract productContract = productContractService.getProductContractById(contractId);
		if (CollectionUtil.isEmpty(productContract.getProductContractItem())) {
			LOG.warn("No Line Items found for this contract. Not going to send it to ERP : " + productContract.getContractId());
			return;

		}
		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending Contract to ERP...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {

					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					// for Type 2

					ProductContractOutBoundPojo erp2Pojo = new ProductContractOutBoundPojo();
					erp2Pojo.setAgreementType(productContract.getAgreementType() != null ? productContract.getAgreementType().getAgreementType() : "");
					erp2Pojo.setBaseCurrency(productContract.getCurrency().getCurrencyCode());
					erp2Pojo.setBusinessUnit((productContract.getBusinessUnit() != null && productContract.getBusinessUnit().getParent() != null) ? productContract.getBusinessUnit().getParent().getUnitCode() : productContract.getBusinessUnit().getUnitCode());
					erp2Pojo.setContractStartDate(df.format(productContract.getContractStartDate()));
					erp2Pojo.setContractEndDate(df.format(productContract.getContractEndDate()));
					erp2Pojo.setDocumentDate(df.format(productContract.getCreatedDate()));
					erp2Pojo.setGroupCode(productContract.getGroupCode() != null ? productContract.getGroupCode().getGroupCode() : "");
					erp2Pojo.setSupplierCode(productContract.getSupplier().getVendorCode());
					erp2Pojo.setContractValue(productContract.getContractValue());
					erp2Pojo.setContractId(productContract.getContractId());

					List<ProductContractItemsOutBoundPojo> items = new ArrayList<ProductContractItemsOutBoundPojo>();
					List<ProductContractItemsCondValidityOutBoundPojo> validity = new ArrayList<ProductContractItemsCondValidityOutBoundPojo>();
					List<ProductContractItemsConditionOutBoundPojo> conditions = new ArrayList<ProductContractItemsConditionOutBoundPojo>();

					if (CollectionUtil.isNotEmpty(productContract.getProductContractItem())) {
						String itemNumber = null;
						for (ProductContractItems cItem : productContract.getProductContractItem()) {
							ProductContractItemsOutBoundPojo item = new ProductContractItemsOutBoundPojo();
							if (StringUtils.checkString(cItem.getContractItemNumber()).length() == 5) {
								itemNumber = StringUtils.checkString(cItem.getContractItemNumber());
							} else {
								itemNumber = StringUtils.lpad(String.valueOf(Integer.parseInt(cItem.getContractItemNumber()) * 10), 5, '0');
							}

							item.setItemNumber(itemNumber);
							item.setAccountAssignment("U");
							item.setProductCode(cItem.getItemCode());
							item.setProductGroup(cItem.getProductCategory() != null ? cItem.getProductCategory().getProductCode() : "");
							item.setProductItem(cItem.getItemName());
							item.setQuantity(cItem.getQuantity());
							item.setUnitCode(cItem.getBusinessUnit() != null ? cItem.getBusinessUnit().getUnitCode() : "");
							item.setUnitPrice(cItem.getUnitPrice());
							item.setUom(cItem.getUom().getUom());
							item.setPricePerUnit(cItem.getPricePerUnit() != null ? cItem.getPricePerUnit() : java.math.BigDecimal.ONE);
							items.add(item);

							ProductContractItemsCondValidityOutBoundPojo valid = new ProductContractItemsCondValidityOutBoundPojo();
							valid.setItemNumber(StringUtils.checkString(itemNumber));
							valid.setContractStartDate(df.format(productContract.getContractStartDate()));
							valid.setContractEndDate(df.format(productContract.getContractEndDate()));
							validity.add(valid);

							ProductContractItemsConditionOutBoundPojo condition = new ProductContractItemsConditionOutBoundPojo();
							condition.setItemNumber(StringUtils.checkString(itemNumber));

							condition.setPricePerUnit(cItem.getPricePerUnit() != null ? cItem.getPricePerUnit() : java.math.BigDecimal.ONE);
							condition.setUnitPrice(cItem.getUnitPrice());
							condition.setUom(cItem.getUom().getUom());
							condition.setConditionType("PB00");
							condition.setCurrency(productContract.getCurrency().getCurrencyCode());
							conditions.add(condition);

						}
					}
					erp2Pojo.setItems(items);
					erp2Pojo.setValidity(validity);
					erp2Pojo.setConditions(conditions);

					payload = mapperObj.writeValueAsString(erp2Pojo);
					LOG.info("Contract [" + productContract.getContractId() + "] payload  :" + payload);

					HttpEntity<ProductContractOutBoundPojo> request = new HttpEntity<ProductContractOutBoundPojo>(erp2Pojo, headers);

					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/ContractCreate/", request, String.class);
						LOG.info("response :" + responseMsg);
						if (StringUtils.checkString(responseMsg).length() > 0) {
							ContractErpResponsePojo res = mapperObj.readValue(responseMsg, ContractErpResponsePojo.class);
							if (!StringUtils.checkString(res.getRes().getRes().getType()).equalsIgnoreCase("S")) {
								throw new ErpIntegrationException("Error while sending to ERP : " + StringUtils.checkString(res.getRes().getRes().getMessage()));
							} else {
								productContract.setErpTransferred(Boolean.TRUE);
								productContract.setSapContractNumber(res.getRes().getRes().getSapContractId());
								productContract = productContractService.update(productContract);
								productContractItemsDao.updateErpTransfer(productContract.getId());
							}
						}
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new ErpIntegrationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				// Storing audit history
				try {
					ContractAudit audit = new ContractAudit();
					audit.setAction(AuditActionType.Erp);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer();
					buyer.setId(productContract.getBuyer().getId());
					audit.setBuyer(buyer);
					audit.setDescription("Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setProductContract(productContract);
					contractAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving Contract ERP Audit History :" + e.getMessage(), e);
				}
			}
		} catch (ErpIntegrationException e) {
			LOG.error("Error while transfer Contract to erp :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while transfer Contract to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateContractInErp(String contractId, ErpSetup erpSetup, User loggedInUser) throws Exception, ErpIntegrationException {

		ProductContract productContract = productContractService.getProductContractById(contractId);
		if (CollectionUtil.isEmpty(productContract.getProductContractItem())) {
			LOG.warn("No Line Items found for this contract. Not going to send it to ERP : " + productContract.getContractId());
			return;

		}
		try {

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			ErpIntegrationTypeForPr type = erpSetup.getErpIntegrationTypeForPr();
			HttpHeaders headers = new HttpHeaders();

			LOG.info("erpConfig erp enable :" + erpSetup.getIsErpEnable());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
				// sending PR to ERP
				LOG.info("ERP Integration is enabled.... sending Contract to ERP...");

				ObjectMapper mapperObj = new ObjectMapper();
				String payload = "";

				String responseMsg = "";
				if (ErpIntegrationTypeForPr.TYPE_2 == type) {
					SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
					if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
						String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
						byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
						String authHeader = "Basic " + new String(encodedAuth);
						headers.set("Authorization", authHeader);
					}

					// for Type 2

					ProductContractChangeOutBoundPojo erp2Pojo = new ProductContractChangeOutBoundPojo();
					erp2Pojo.setSapContractNumber(productContract.getSapContractNumber());
					if (productContract.getOldContractStartDate() != null) {
						erp2Pojo.setContractStartDate(df.format(productContract.getContractStartDate()));
					}
					if (productContract.getOldContractEndDate() != null) {
						erp2Pojo.setContractEndDate(df.format(productContract.getContractEndDate()));
					}
					if (productContract.getOldContractValue() != null) {
						erp2Pojo.setContractValue(productContract.getContractValue());
					}

					List<ProductContractItemsChangeOutBoundPojo> items = new ArrayList<ProductContractItemsChangeOutBoundPojo>();

					if (CollectionUtil.isNotEmpty(productContract.getProductContractItem())) {
						for (ProductContractItems cItem : productContract.getProductContractItem()) {
							ProductContractItemsChangeOutBoundPojo item = new ProductContractItemsChangeOutBoundPojo();
							String itemNumber = String.valueOf(Integer.parseInt(cItem.getContractItemNumber()));
							if (StringUtils.checkString(itemNumber).length() == 5) {
								item.setItemNumber(StringUtils.checkString(itemNumber));
							} else {
								item.setItemNumber(StringUtils.lpad(String.valueOf(Integer.parseInt(cItem.getContractItemNumber()) * 10), 5, '0'));
							}
							if (Boolean.TRUE == cItem.getDeleted()) {
								item.setDeleteIndicator("X");
								items.add(item);
							} else if (cItem.getOldQuantity() != null) {
								item.setQuantity(cItem.getQuantity());
								items.add(item);
							} else if (cItem.getErpTransferred() == null || Boolean.FALSE == cItem.getErpTransferred()) {
								item.setAccountAssignment("U");
								item.setPricePerUnit(cItem.getPricePerUnit());
								item.setProductGroup(cItem.getProductCategory() != null ? cItem.getProductCategory().getProductCode() : "");
								item.setProductCode(cItem.getItemCode());
								item.setProductItem(cItem.getItemName());
								item.setQuantity(cItem.getQuantity());
								item.setUnitCode(cItem.getBusinessUnit() != null ? cItem.getBusinessUnit().getUnitCode() : "");
								item.setUnitPrice(cItem.getUnitPrice());
								item.setPricePerUnit(cItem.getPricePerUnit() != null ? cItem.getPricePerUnit() : java.math.BigDecimal.ONE);
								item.setUom(cItem.getUom().getUom());
								items.add(item);
							}
						}
					}
					erp2Pojo.setItems(items);

					payload = mapperObj.writeValueAsString(erp2Pojo);
					LOG.info("Contract Update [" + productContract.getContractId() + "] payload  :" + payload);

					HttpEntity<ProductContractChangeOutBoundPojo> request = new HttpEntity<ProductContractChangeOutBoundPojo>(erp2Pojo, headers);

					try {
						responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/ContractChange/", request, String.class);
						LOG.info("response :" + responseMsg);
						if (StringUtils.checkString(responseMsg).length() > 0) {
							ContractChangeErpResponsePojo res = mapperObj.readValue(responseMsg, ContractChangeErpResponsePojo.class);
							if (!StringUtils.checkString(res.getRes().getRes().getType()).equalsIgnoreCase("S")) {
								throw new ErpIntegrationException("Error while sending to ERP : " + StringUtils.checkString(res.getRes().getRes().getMessage()));
							} else {
								productContract.setErpTransferred(Boolean.TRUE);
								productContract.setOldContractEndDate(null);
								productContract.setOldContractStartDate(null);
								productContract.setOldContractValue(null);
								productContract = productContractService.update(productContract);
								productContractItemsDao.updateErpTransfer(productContract.getId());

								// send email to owner and team members
								try {
									sendUpdateContractEmail(productContract.getContractCreator(), productContract.getId(), productContract.getContractId(), productContract.getContractName(), productContract.getContractReferenceNumber());
									List<ContractTeamMember> teamMembers = productContract.getTeamMembers();
									if (CollectionUtil.isNotEmpty(teamMembers)) {
										for (ContractTeamMember team : teamMembers) {
											sendUpdateContractEmail(team.getUser(), productContract.getId(), productContract.getContractId(), productContract.getContractName(), productContract.getContractReferenceNumber());
										}
									}
								} catch (Exception e) {
									LOG.error("Error while sending contract update email " + e.getMessage(), e);
								}

							}
						}
					} catch (HttpClientErrorException | HttpServerErrorException ex) {
						responseMsg = ex.getMessage();
						LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
						LOG.error("Response Body : " + ex.getResponseBodyAsString());
						throw new Exception("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
					}
				}

				// Storing audit history
				try {
					ContractAudit audit = new ContractAudit();
					audit.setAction(AuditActionType.Erp);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					Buyer buyer = new Buyer();
					buyer.setId(productContract.getBuyer().getId());
					audit.setBuyer(buyer);
					audit.setDescription("Request sent to ERP. Response : " + StringUtils.checkString(responseMsg));
					audit.setProductContract(productContract);
					contractAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving Contract ERP Audit History :" + e.getMessage(), e);
				}
			}
		} catch (ErpIntegrationException e) {
			LOG.error("Error while transfer Contract update to erp :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while transfer Contract update to erp :" + e.getMessage(), e);
			throw e;
		}
	}

	private void sendUpdateContractEmail(User user, String id, String contractId, String contractName, String contractReferanceNumber) {
		LOG.info("Sending error request email to(" + user.getName() + "):" + user.getCommunicationEmail());
		// String url = APP_URL + "/buyer/contractView/" + id;
		String subject = "Contract Updated";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", user.getName());
		map.put("contractId", contractId);
		map.put("contractName", contractName);
		map.put("contractReferanceNumber", contractReferanceNumber);
		map.put("message", "Contact has been successfully updated");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		if (StringUtils.checkString(user.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
			LOG.info("User Communication email-----------------" + user.getCommunicationEmail());
			sendEmail(user.getCommunicationEmail(), subject, map, Global.TEAM_MEMBER_TEMPLATE_CONTRACT_UPDATE);

		} else {
			LOG.warn("No communication email configured for user : " + user.getLoginId() + "... Not going to send email notification");
		}
	}

	public RfxErpPojoAward convertToPrErpPojoAward(RfxTypes rfxTypes, Event event) throws ApplicationException {
		RfxErpPojoAward rfxErpPojoAward = new RfxErpPojoAward();
		rfxErpPojoAward.setEventId(event.getEventId());
		if(event.getProcurementMethod() != null && event.getProcurementMethod().getProcurementMethodCode() != null) {
			rfxErpPojoAward.setPrType(event.getProcurementMethod().getProcurementMethodCode());
		}
		else {
			throw new ApplicationException("No Procurement Method Code");
		}
		rfxErpPojoAward.setHeaderText(event.getEventName());
		rfxErpPojoAward.setEventType(rfxTypes);

		if(event.getBusinessUnit() == null) {
			throw new ApplicationException("No Business Unit");
		}

		switch (rfxTypes) {
			case RFQ:
				RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRfqEvent = rfqSupplierBqItemService.getTotalRfqSupplierBqItem(event.getId());
				rfxErpPojoAward.setItems(convertFromRfqSupplierBqItemToItemListPojo(rfxSupplierBqItemAndRfqEvent.getRfqSupplierBqItemList(),
						rfxSupplierBqItemAndRfqEvent.getRfqEventAward(), event));
				break;
			case RFP:
				RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRfpEvent = rfpSupplierBqItemService.getTotalRfpSupplierBqItem(event.getId());
				rfxErpPojoAward.setItems(convertFromRfpSupplierBqItemToItemListPojo(rfxSupplierBqItemAndRfpEvent.getRfpSupplierBqItemList(),
						rfxSupplierBqItemAndRfpEvent.getRfpEventAward(), event));
				break;
			case RFA:
				RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRfaEvent = rfaSupplierBqItemService.getTotalRfaSupplierBqItem(event.getId());
				rfxErpPojoAward.setItems(convertFromRfaSupplierBqItemToItemListPojo(rfxSupplierBqItemAndRfaEvent.getRfaSupplierBqItemList(),
						rfxSupplierBqItemAndRfaEvent.getRfaEventAward(), event));
				break;
			case RFT:
				RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRftEvent = rftSupplierBqItemService.getTotalRftSupplierBqItem(event.getId());
				rfxErpPojoAward.setItems(convertFromRftSupplierBqItemToItemListPojo(rfxSupplierBqItemAndRftEvent.getRftSupplierBqItemList(),
						rfxSupplierBqItemAndRftEvent.getRftEventAward() , event));
				break;
		}
		return rfxErpPojoAward;
	}


	public List<ItemListPojo> convertFromRfpSupplierBqItemToItemListPojo(List<RfpSupplierBqItem> rfpSupplierBqItemList,RfpEventAward rfpEventAward ,Event event) throws ApplicationException {
		List<ItemListPojo> itemListPojoList = new ArrayList<>();

		int i = 0;
		int p = 1;

		// here except preq price the other things are done

		for (RfpSupplierBqItem supplierBqItem : rfpSupplierBqItemList) {
			if (supplierBqItem.getOrder() != 0) {
				ItemListPojo itemListPojo = new ItemListPojo();

				String itemName = String.format("%05d", p);
				itemListPojo.setPreqItem(itemName);

				itemListPojo.setShortText(supplierBqItem.getItemName().length() > 40 ? supplierBqItem.getItemName().substring(0, 39) :
						supplierBqItem.getItemName());

				itemListPojo.setLongText(supplierBqItem.getItemDescription() != null ? supplierBqItem.getItemDescription() : "");

				if (supplierBqItem.getQuantity() != null) {
					itemListPojo.setQuantity(String.valueOf(supplierBqItem.getQuantity().setScale(3, RoundingMode.HALF_UP)));
				} else {
					throw new ApplicationException("No Quantity for item number "+i);
				}

				if (supplierBqItem.getUom() != null) {
					itemListPojo.setUnit(supplierBqItem.getUom().getUom());
				} else {
					throw new ApplicationException("No Uom for item number "+i);
				}

				if (event.getDeliveryDate() != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(event.getDeliveryDate());
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					itemListPojo.setDelivDate(formatter.format(calendar.getTime()));
				} else {
					throw new ApplicationException("No Delivery Date for this event ");
				}

				itemListPojo.setPlant(event.getBusinessUnit().getUnitCode());
				itemListPojo.setPurchOrg(event.getBusinessUnit() != null ? event.getBusinessUnit().getParent() != null ?
						event.getBusinessUnit().getParent().getUnitCode() : "" : "");
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				itemListPojo.setPreqDate(formatter.format(new Date()));

//				if (supplierBqItem.getUnitPrice() != null) {
//					itemListPojo.setPriceUnit(String.valueOf(supplierBqItem.getUnitPrice()));
//				} else {
//					itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));
//				}

				// price Unit by default 1
				itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));

				if(rfpEventAward.getRfxAwardDetails().get(i).getAwardedPrice() == null) {
					throw new ApplicationException("No Awarded Price for item number "+i);
				}

				String preqPrice = rfpEventAward.getRfxAwardDetails().size() != 0 ?
						String.valueOf(rfpEventAward.getRfxAwardDetails().get(i).getAwardedPrice().divide(supplierBqItem.getQuantity(), 2, RoundingMode.HALF_UP)) : "";

				itemListPojo.setPreqPrice(preqPrice);

				if(supplierBqItem.getSupplier() == null) {
					throw new ApplicationException("No supplier for item number "+i);
				}

				FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierIdAndBuyerId(supplierBqItem.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (favouriteSupplier != null && favouriteSupplier.getVendorCode() != null) {
					itemListPojo.setDesVendor(favouriteSupplier.getVendorCode() != null ? favouriteSupplier.getVendorCode() : "");
				} else {
					throw new ApplicationException("No vendor Code for item number "+ i);
				}

				itemListPojo.setStoreLoc("EPRC");

				if (supplierBqItem.getBqItem().getField1() != null) {
					itemListPojo.setMaterial(supplierBqItem.getBqItem().getField1());
				} else {
					throw new ApplicationException("No Material Code for item number "+i);
				}

				if (supplierBqItem.getBqItem().getField2() != null) {
					itemListPojo.setMatlGroup(supplierBqItem.getBqItem().getField2());
				} else {
					throw new ApplicationException("No Material Group for item number "+i);
				}


				try {
					if (event.getGroupCode().getGroupCode() != null) {
						itemListPojo.setPurGroup(event.getGroupCode().getGroupCode());
					} else {
						throw new ApplicationException("No Group Code for item number "+i);
					}
				} catch (Exception m) {
					throw new ApplicationException("Exception  in Group code for item number "+i);
				}
				itemListPojo.setAcctAssCat("U");
				itemListPojoList.add(itemListPojo);
				p++;
			}
			i++;
		}
		return itemListPojoList;
	}

	public List<ItemListPojo> convertFromRfqSupplierBqItemToItemListPojo(List<RfqSupplierBqItem> rfqSupplierBqItemList, RfqEventAward rfqEventAward , Event event) throws ApplicationException {
		List<ItemListPojo> itemListPojoList = new ArrayList<>();

		int i = 0;
		int p = 1;

		// here except preq price the other things are done

		for (RfqSupplierBqItem supplierBqItem : rfqSupplierBqItemList) {
			if (supplierBqItem.getOrder() != 0) {
				ItemListPojo itemListPojo = new ItemListPojo();

				String itemName = String.format("%05d", p);
				itemListPojo.setPreqItem(itemName);

				itemListPojo.setShortText(supplierBqItem.getItemName().length() > 40 ? supplierBqItem.getItemName().substring(0, 39) :
						supplierBqItem.getItemName());

				itemListPojo.setLongText(supplierBqItem.getItemDescription() != null ? supplierBqItem.getItemDescription() : "");

				if (supplierBqItem.getQuantity() != null) {
					itemListPojo.setQuantity(String.valueOf(supplierBqItem.getQuantity().setScale(3, RoundingMode.HALF_UP)));
				} else {
					throw new ApplicationException("No Quantity for item number "+i);
				}

				if (supplierBqItem.getUom() != null) {
					itemListPojo.setUnit(supplierBqItem.getUom().getUom());
				} else {
					throw new ApplicationException("No Uom for item number "+i);
				}

				if (event.getDeliveryDate() != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(event.getDeliveryDate());
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					itemListPojo.setDelivDate(formatter.format(calendar.getTime()));
				} else {
					throw new ApplicationException("No Delivery Date for this event ");
				}

				itemListPojo.setPlant(event.getBusinessUnit() != null ? event.getBusinessUnit().getUnitCode() : null);
				itemListPojo.setPurchOrg(event.getBusinessUnit() != null ? event.getBusinessUnit().getParent() != null ?
						event.getBusinessUnit().getParent().getUnitCode() : "" : "");

				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				itemListPojo.setPreqDate(formatter.format(new Date()));

//				if (supplierBqItem.getUnitPrice() != null) {
//					itemListPojo.setPriceUnit(String.valueOf(supplierBqItem.getUnitPrice()));
//				} else {
//					itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));
//				}
				// by default 1
				itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));

				if(rfqEventAward.getRfxAwardDetails().get(i).getAwardedPrice() == null) {
					throw new ApplicationException("No Awarded Price for item number "+i);
				}

				String preqPrice = rfqEventAward.getRfxAwardDetails().size() != 0 ?
						String.valueOf(rfqEventAward.getRfxAwardDetails().get(i).getAwardedPrice().divide(supplierBqItem.getQuantity(), 2, RoundingMode.HALF_UP)) : "";

				itemListPojo.setPreqPrice(preqPrice);

				if(supplierBqItem.getSupplier() == null) {
					throw new ApplicationException("No supplier for item number "+i);
				}

				FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierIdAndBuyerId(supplierBqItem.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (favouriteSupplier != null && favouriteSupplier.getVendorCode() != null) {
					itemListPojo.setDesVendor(favouriteSupplier.getVendorCode() != null ? favouriteSupplier.getVendorCode() : "");
				} else {
					throw new ApplicationException("No vendor Code for item number "+i);
				}

				if (supplierBqItem.getBqItem().getField1() != null) {
					itemListPojo.setMaterial(supplierBqItem.getBqItem().getField1());
				} else {
					throw new ApplicationException("No Material Code for item number "+i);
				}
				itemListPojo.setStoreLoc("EPRC");

				if (supplierBqItem.getBqItem().getField2() != null) {
					itemListPojo.setMatlGroup(supplierBqItem.getBqItem().getField2());
				} else {
					throw new ApplicationException("No Material Group for item number "+i);
				}

				try {
					if (event.getGroupCode().getGroupCode() != null) {
						itemListPojo.setPurGroup(event.getGroupCode().getGroupCode());
					} else {
						throw new ApplicationException("No Group Code for item number "+i);
					}
				} catch (Exception m) {
					throw new ApplicationException("Exception  in Group code for item number "+i);
				}
				itemListPojo.setAcctAssCat("U");
				itemListPojoList.add(itemListPojo);
				p++;
			}
			i++;
		}
		return itemListPojoList;
	}

	public List<ItemListPojo> convertFromRfaSupplierBqItemToItemListPojo(List<RfaSupplierBqItem> rfaSupplierBqItemList, RfaEventAward rfaEventAward, Event event) throws ApplicationException {
		List<ItemListPojo> itemListPojoList = new ArrayList<>();

		int i = 0;
		int p = 1;

		// here except preq price the other things are done

		for (RfaSupplierBqItem supplierBqItem : rfaSupplierBqItemList) {
			if (supplierBqItem.getOrder() != 0) {
				ItemListPojo itemListPojo = new ItemListPojo();

				String itemName = String.format("%05d", p);
				itemListPojo.setPreqItem(itemName);

				itemListPojo.setShortText(supplierBqItem.getItemName().length() > 40 ? supplierBqItem.getItemName().substring(0, 39) :
						supplierBqItem.getItemName());

				itemListPojo.setLongText(supplierBqItem.getItemDescription() != null ? supplierBqItem.getItemDescription() : "");

				if (supplierBqItem.getQuantity() != null) {
					itemListPojo.setQuantity(String.valueOf(supplierBqItem.getQuantity().setScale(3, RoundingMode.HALF_UP)));
				} else {
					throw new ApplicationException("No Quantity for item number "+i);
				}

				if (supplierBqItem.getUom() != null) {
					itemListPojo.setUnit(supplierBqItem.getUom().getUom());
				} else {
					throw new ApplicationException("No Uom for item number "+i);
				}

				if (event.getDeliveryDate() != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(event.getDeliveryDate());
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					itemListPojo.setDelivDate(formatter.format(calendar.getTime()));
				} else {
					throw new ApplicationException("No Delivery Date for this event ");
				}

				itemListPojo.setPlant(event.getBusinessUnit().getUnitCode());
				itemListPojo.setPurchOrg(event.getBusinessUnit() != null ? event.getBusinessUnit().getParent() != null ?
						event.getBusinessUnit().getParent().getUnitCode() : "" : "");
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				itemListPojo.setPreqDate(formatter.format(new Date()));

//				if (supplierBqItem.getUnitPrice() != null) {
//					itemListPojo.setPriceUnit(String.valueOf(supplierBqItem.getUnitPrice()));
//				} else {
//					itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));
//				}
				// by default 1
				itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));

				if(rfaEventAward.getRfxAwardDetails().get(i).getAwardedPrice() == null) {
					throw new ApplicationException("No Awarded Price for item number "+i);
				}
				String preqPrice = rfaEventAward.getRfxAwardDetails().size() != 0 ?
						String.valueOf(rfaEventAward.getRfxAwardDetails().get(i).getAwardedPrice().divide(supplierBqItem.getQuantity(), 2, RoundingMode.HALF_UP)) : "";

				itemListPojo.setPreqPrice(preqPrice);

				if(supplierBqItem.getSupplier() == null) {
					throw new ApplicationException("No supplier for item number "+i);
				}

				FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierIdAndBuyerId(supplierBqItem.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (favouriteSupplier != null && favouriteSupplier.getVendorCode() != null) {
					itemListPojo.setDesVendor(favouriteSupplier.getVendorCode() != null ? favouriteSupplier.getVendorCode() : "");
				} else {
					throw new ApplicationException("No vendor Code for item number "+i);
				}

				if (supplierBqItem.getBqItem().getField1() != null) {
					itemListPojo.setMaterial(supplierBqItem.getBqItem().getField1());
				} else {
					throw new ApplicationException("No Material Code for item number "+i);
				}
				itemListPojo.setStoreLoc("EPRC");

				if (supplierBqItem.getBqItem().getField2() != null) {
					itemListPojo.setMatlGroup(supplierBqItem.getBqItem().getField2());
				} else {
					throw new ApplicationException("No Material Group for item number "+i);
				}

				try {
					if (event.getGroupCode().getGroupCode() != null) {
						itemListPojo.setPurGroup(event.getGroupCode().getGroupCode());
					} else {
						throw new ApplicationException("No Group Code for item number "+i);
					}
				} catch (Exception m) {
					throw new ApplicationException("Exception  in Group code for item number "+i);
				}
				itemListPojo.setAcctAssCat("U");
				itemListPojoList.add(itemListPojo);
				p++;
			}
			i++;
		}
		return itemListPojoList;
	}

	public List<ItemListPojo> convertFromRftSupplierBqItemToItemListPojo(List<RftSupplierBqItem> rftSupplierBqItemList, RftEventAward rftEventAward, Event event) throws ApplicationException {
		List<ItemListPojo> itemListPojoList = new ArrayList<>();

		int i = 0;
		int p = 1;

		// here except preq price the other things are done

		for (RftSupplierBqItem supplierBqItem : rftSupplierBqItemList) {
			if (supplierBqItem.getOrder() != 0) {
				ItemListPojo itemListPojo = new ItemListPojo();

				String itemName = String.format("%05d", p);
				itemListPojo.setPreqItem(itemName);

				itemListPojo.setLongText(supplierBqItem.getItemDescription() != null ? supplierBqItem.getItemDescription() : "");

				itemListPojo.setShortText(supplierBqItem.getItemName().length() > 40 ? supplierBqItem.getItemName().substring(0, 39) :
						supplierBqItem.getItemName());

				if (supplierBqItem.getQuantity() != null) {
					itemListPojo.setQuantity(String.valueOf(supplierBqItem.getQuantity().setScale(3, RoundingMode.HALF_UP)));
				} else {
					throw new ApplicationException("No Quantity for item number "+i);
				}

				if (supplierBqItem.getUom() != null) {
					itemListPojo.setUnit(supplierBqItem.getUom().getUom());
				} else {
					throw new ApplicationException("No Uom for item number "+i);
				}

				if (event.getDeliveryDate() != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(event.getDeliveryDate());
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					itemListPojo.setDelivDate(formatter.format(calendar.getTime()));
				} else {
					throw new ApplicationException("No Delivery Date for this event ");
				}

				itemListPojo.setPlant(event.getBusinessUnit().getUnitCode());
				itemListPojo.setPurchOrg(event.getBusinessUnit().getParent() != null ? event.getBusinessUnit().getParent().getUnitCode() != null ?
						event.getBusinessUnit().getParent().getUnitCode() : "" : "");

				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				itemListPojo.setPreqDate(formatter.format(new Date()));

//				if (supplierBqItem.getUnitPrice() != null) {
//					itemListPojo.setPriceUnit(String.valueOf(supplierBqItem.getUnitPrice()));
//				} else {
//					itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));
//				}
				// by default 1
				itemListPojo.setPriceUnit(String.valueOf(BigDecimal.valueOf(1)));

				if(rftEventAward.getRfxAwardDetails().get(i).getAwardedPrice() == null) {
					throw new ApplicationException("No Awarded Price for item number "+i);
				}

				String preqPrice = rftEventAward.getRfxAwardDetails().size() != 0 ?
						String.valueOf(rftEventAward.getRfxAwardDetails().get(i).getAwardedPrice().divide(supplierBqItem.getQuantity(), 2, RoundingMode.HALF_UP)) : "";

				itemListPojo.setPreqPrice(preqPrice);

				if(supplierBqItem.getSupplier() == null) {
					throw new ApplicationException("No supplier for item number "+i);
				}

				FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierIdAndBuyerId(supplierBqItem.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (favouriteSupplier != null && favouriteSupplier.getVendorCode() != null) {
					itemListPojo.setDesVendor(favouriteSupplier.getVendorCode() != null ? favouriteSupplier.getVendorCode() : "");
				} else {
					throw new ApplicationException("No vendor Code for item number "+i);
				}
				itemListPojo.setStoreLoc("EPRC");

				if (supplierBqItem.getBqItem().getField1() != null) {
					itemListPojo.setMaterial(supplierBqItem.getBqItem().getField1());
				} else {
					throw new ApplicationException("No Material Code for item number "+i);
				}

				if (supplierBqItem.getBqItem().getField2() != null) {
					itemListPojo.setMatlGroup(supplierBqItem.getBqItem().getField2());
				} else {
					throw new ApplicationException("No Material Group for item number "+i);
				}

				try {
					if (event.getGroupCode().getGroupCode() != null) {
						itemListPojo.setPurGroup(event.getGroupCode().getGroupCode());
					} else {
						throw new ApplicationException("No Group Code for item number "+i);
					}
				} catch (Exception m) {
					throw new ApplicationException("Exception  in Group code for item number "+i);
				}
				itemListPojo.setAcctAssCat("U");
				itemListPojoList.add(itemListPojo);
				p++;
			}
			i++;
		}
		return itemListPojoList;
	}

	public Boolean sendPoAcceptanceToSap(Po po, PoStatus poStatus, Boolean isFromScheduler, String remarks) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, (chain, authType) -> true);
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(sslSocketFactory)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		LOG.info("PoAcceptanceToSap is being called ");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(po.getBuyer().getId());

		if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
			PoAcceptancePayloadSAP poAcceptancePayloadSAP = new PoAcceptancePayloadSAP();
			poAcceptancePayloadSAP.setREFERENCE_NUMBER(po.getReferenceNumber());
			poAcceptancePayloadSAP.setSUPPLIER_REMARK(remarks);
			poAcceptancePayloadSAP.setINDICATOR(PoStatus.ACCEPTED == poStatus ? "A" : "D");

			LOG.info("RequestBody: "+poAcceptancePayloadSAP);

			if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
				String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
				byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				headers.set("Authorization", authHeader);
			}

			HttpEntity<PoAcceptancePayloadSAP> requestEntity = new HttpEntity<>(poAcceptancePayloadSAP, headers);


			try {
				ResponseOfSapForPoAccept responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PHP2P/POAcknowledge", requestEntity, ResponseOfSapForPoAccept.class);

				LOG.info("PoAcceptanceToSap ResponseMessage for:"+poAcceptancePayloadSAP.getREFERENCE_NUMBER()+" "+ responseMsg);
				// save the audit
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.ERP);
				if(!isFromScheduler) {
					poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				}
				if(responseMsg.getMtPoAcknowledgeP2PRes().getType() == null || responseMsg.getMtPoAcknowledgeP2PRes().getType().equals("E")) {
					poAudit.setDescription("ERP Transfer Failed: "+responseMsg.getMtPoAcknowledgeP2PRes().getTheDescription());
				} else {
					poAudit.setDescription("ERP Transfer Success: " + responseMsg.getMtPoAcknowledgeP2PRes().getTheDescription());
				}
				poAudit.setActionDate(new Date());
				poAudit.setBuyer(po.getBuyer());

				if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
					poAudit.setSupplier(po.getSupplier().getSupplier());
				}
				poAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				poAudit.setPo(po);
				poAuditService.save(poAudit);

				if(responseMsg.getMtPoAcknowledgeP2PRes().getType() == null || responseMsg.getMtPoAcknowledgeP2PRes().getType().equals("E")) {
					return Boolean.FALSE;
				}

				return Boolean.TRUE;
			} catch (Exception e) {
				LOG.error("PoAcceptanceToSap got error : "+e.getMessage());
			}
		}
		return Boolean.FALSE;
	}

}
