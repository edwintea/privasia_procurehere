package com.privasia.procurehere.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.RfqEventAwardDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqEventAwardAuditDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.RfqAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventAward;
import com.privasia.procurehere.core.entity.RfqEventAwardApproval;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqEventAwardDetails;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.ErpIntegrationTypeForAward;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SapDocType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.AwardDetailsErp2Pojo;
import com.privasia.procurehere.core.pojo.PrResponsePojo;
import com.privasia.procurehere.core.pojo.AwardDetailsErpPojo;
import com.privasia.procurehere.core.pojo.AwardErp2Pojo;
import com.privasia.procurehere.core.pojo.AwardErpPojo;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;
import com.privasia.procurehere.core.pojo.DeliveryAddressPojo;
import com.privasia.procurehere.core.pojo.ErpSupplierPojo;
import com.privasia.procurehere.core.pojo.RftEventAwardDetailsPojo;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.PrResponseUpdateWrapper;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.EventAwardAuditService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.RfqAwardService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.RfqEventService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class RfqAwardServiceImpl implements RfqAwardService {

	private static final Logger LOG = LogManager.getLogger(RfqAwardServiceImpl.class);
	private static final Logger ERP_LOG = LogManager.getLogger(Global.ERP_LOG);

	@Autowired
	RfqEventAwardDao rfqEventAwardDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	StateService stateService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	EventAwardAuditService eventAwardAuditService;

	@Autowired
	ServletContext context;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfqEventAwardAuditDao rfqEventAwardAuditDao;

	@Autowired
	RfqEventService rfqEventService;


	@Override
	@Transactional(readOnly = false)
	public RfqEventAward saveEventAward(RfqEventAward rfqEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude) {
		rfqEventAward.setCreatedBy(SecurityLibrary.getLoggedInUser());
		rfqEventAward.setCreatedDate(new Date());
		rfqEventAward.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfqEventAward.setModifiedDate(new Date());

		List<RfqEventAwardDetails> awardList = rfqEventAward.getRfxAwardDetails();
		Set<String> awardedSupplierIds = new HashSet<String>();
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfqEventAwardDetails rfqEventAwardDetails : awardList) {
				Supplier supplier = rfqEventAwardDetails.getSupplier();
				if ((supplier != null) && (supplier.getId() != null)) {
					awardedSupplierIds.add(supplier.getId());
				}
			}
		}

		RfqEvent rfqEvent = rfqEventDao.findById(rfqEventAward.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(awardedSupplierIds)) {
			if (rfqEvent != null) {
				List<Supplier> awardedSuppliers = null;
				if (CollectionUtil.isNotEmpty(rfqEvent.getAwardedSuppliers())) {
					awardedSuppliers = rfqEvent.getAwardedSuppliers();
				} else {
					awardedSuppliers = new ArrayList<Supplier>();
				}
				for (String suppId : awardedSupplierIds) {
					Supplier supplier = new Supplier();
					supplier.setId(suppId);
					awardedSuppliers.add(supplier);
				}
				rfqEvent.setAwardedSuppliers(awardedSuppliers);
				rfqEventDao.saveOrUpdate(rfqEvent);
			}
		}

		Set<String> supplierList = new HashSet<String>();

		if (CollectionUtil.isNotEmpty(rfqEventAward.getRfxAwardDetails())) {
			for (RfqEventAwardDetails awardDetails : rfqEventAward.getRfxAwardDetails()) {
				awardDetails.setEventAward(rfqEventAward);
				if (awardDetails.getSupplier() != null) {
					supplierList.add(awardDetails.getSupplier().getId());
				}
			}
		}

		RfqEventAward rfqEventAward2 = rfqEventAwardDao.findById(rfqEventAward.getId());
		if(rfqEventAward2 != null && rfqEventAward2.getFileData() != null) {
			rfqEventAward.setFileData(rfqEventAward2.getFileData());
			rfqEventAward.setFileName(rfqEventAward2.getFileName());
			rfqEventAward.setCredContentType(rfqEventAward2.getCredContentType());
		}

		RfqEventAward dbRfqEventAward = rfqEventAwardDao.saveOrUpdateWithFlush(rfqEventAward);

		if (CollectionUtil.isNotEmpty(rfqEventAward.getAwardApprovals())) {
			int level = 1;
			for (RfqEventAwardApproval app : rfqEventAward.getAwardApprovals()) {
				app.setEvent(rfqEvent);
				app.setLevel(level++);
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
					for (RfqAwardApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);
					}
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		if (dbRfqEventAward.getRfxEvent() != null) {
			Double sumOfAwardedPriceForEvent = rfqEventAwardDao.getSumOfAwardedPrice(dbRfqEventAward.getRfxEvent().getId());
			BigDecimal sumAwardPrice = null;
			if (sumOfAwardedPriceForEvent != null) {
				sumAwardPrice = new BigDecimal(sumOfAwardedPriceForEvent);
			}
			try {
				rfqEventDao.updateRfaForAwardPrice(dbRfqEventAward.getRfxEvent().getId(), sumAwardPrice);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		// Save the list of awarded suppliers into separate table
		List<Supplier> awardedSuppliers = new ArrayList<Supplier>();
		for (String supplierId : supplierList) {
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			awardedSuppliers.add(supplier);
		}
		RfqEvent event = rfqEventDao.findById(dbRfqEventAward.getRfxEvent().getId());
		event.setAwardedSuppliers(awardedSuppliers);
		// Only Save is called. Now set the Award status as DRAFT or our internal consumption.
		// DRAFT status is not shown to the user.
		if (!transfer && !conclude) {
			event.setAwardStatus(AwardStatus.DRAFT);
		}
		if (conclude) {
			event.setAwardStatus(AwardStatus.PENDING);
		}
		if (rfqEventAward.getRfxEvent() != null) {
			event.setEnableAwardApproval(rfqEventAward.getRfxEvent().getEnableAwardApproval());
		}
		event.setAwardApprovals(rfqEventAward.getAwardApprovals());
		event = rfqEventDao.update(event);
		// Jasper report method calling here-------------------------------------------------------------------------
		if (transfer == false && conclude == false) {
			if (StringUtils.checkString(dbRfqEventAward.getId()).length() > 0) {
				// dbRfqEventAward = rfqEventAwardDao.findById(eventAward.getId());
				LOG.info("********" + dbRfqEventAward.toString());
				try {
					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbRfqEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbRfqEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfqEventAwardAudit audit = null;
						if (Boolean.FALSE == conclude) {
							audit = new RfqEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award saved", snapshot, excelSnapshot);
							if (rfqEventAward.getAttachment() != null) {
								LOG.info("File Name:-------->" + rfqEventAward.getAttachment().getOriginalFilename());
								RfqEventAward rfqEventAward1 = rfqEventAwardDao.findById(dbRfqEventAward.getId());
								try {
									rfqEventAward1.setFileData(rfqEventAward.getAttachment().getBytes());
									audit.setFileData(rfqEventAward.getAttachment().getBytes());
								} catch (IOException e) {
									LOG.error("Eroor While getting file data" + e.getMessage(), e);
								}
								rfqEventAward1.setFileName(rfqEventAward.getAttachment().getOriginalFilename());
								rfqEventAward1.setCredContentType(rfqEventAward.getAttachment().getContentType());
								audit.setFileName(rfqEventAward.getAttachment().getOriginalFilename());
								audit.setCredContentType(rfqEventAward.getAttachment().getContentType());
								rfqEventAwardDao.saveOrUpdate(rfqEventAward1);
							}
							eventAwardAuditService.saveRfqAwardAudit(audit);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
			}
		} else if(conclude){
			if (rfqEventAward.getAttachment() != null) {
				LOG.info("File Name:-------->" + rfqEventAward.getAttachment().getOriginalFilename());
				RfqEventAward rfqEventAward1 = rfqEventAwardDao.findById(dbRfqEventAward.getId());
				try {
					rfqEventAward1.setFileData(rfqEventAward.getAttachment().getBytes());								} catch (IOException e) {
					LOG.error("Eroor While getting file data" + e.getMessage(), e);
				}
				rfqEventAward1.setFileName(rfqEventAward.getAttachment().getOriginalFilename());
				rfqEventAward1.setCredContentType(rfqEventAward.getAttachment().getContentType());
				rfqEventAwardDao.saveOrUpdate(rfqEventAward1);
			}
		}

		// Jasper end-------------------------------------------------------------------------

		if (dbRfqEventAward != null) {

			if (dbRfqEventAward.getRfxEvent() != null) {
				dbRfqEventAward.getRfxEvent().getId();
				dbRfqEventAward.getRfxEvent().getEventName();
			}
		}
		if (CollectionUtil.isNotEmpty(dbRfqEventAward.getRfxAwardDetails())) {
			for (RfqEventAwardDetails details : dbRfqEventAward.getRfxAwardDetails()) {
				if (details.getSupplier() != null) {
					LOG.info(details.getSupplier().getId());

					details.getSupplier().getId();
				}
				if (details.getBqItem() != null) {
					details.getBqItem().getId();
				}
				LOG.info(details.getAwardedPrice());
				LOG.info(details.getOriginalPrice());
				details.getAwardedPrice();
				details.getOriginalPrice();
				details.getTaxType();
				details.getTax();
			}

		}

		DecimalFormat df = null;
		if (dbRfqEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		ErpSetup erpSetup = erpSetupService.findErpByWithTepmlateTenantId(loggedInUser.getTenantId());
		if (erpSetup == null || (erpSetup != null && Boolean.FALSE == erpSetup.getIsErpEnable())) {
			LOG.info("ERP Is NOT Enabled...");

			if (Boolean.TRUE == conclude) {
				rfqEvent.setConcludeRemarks(dbRfqEventAward.getAwardRemarks());
				rfqEvent.setConcludeBy(loggedInUser);
				rfqEvent.setConcludeDate(new Date());
				rfqEvent.setStatus(EventStatus.COMPLETE);
				rfqEvent.setAwarded(Boolean.TRUE);
			}
			rfqEventDao.update(rfqEvent);
			if (Boolean.TRUE == conclude) {
				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRfqEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfqEventAwardDao.getRfqEventAwardDetailsByEventIdandBqId(dbRfqEventAward.getId());

						for (AwardReferenceNumberPojo rfaEventAwardDet : award) {
							if (rfaEventAwardDet.getAwardedPrice() != null && StringUtils.checkString(rfaEventAwardDet.getSupllierCompanyName()).length() > 0) {
								if (StringUtils.checkString(awardedSuppliersName).length() == 0) {
									awardedSuppliersName += rfaEventAwardDet.getSupllierCompanyName();
								} else {
									awardedSuppliersName += ", " + rfaEventAwardDet.getSupllierCompanyName();
								}

								if (StringUtils.checkString(awardedSuppliersValue).length() == 0) {
									awardedSuppliersValue += df.format(rfaEventAwardDet.getAwardedPrice());
								} else {
									awardedSuppliersValue += ", " + df.format(rfaEventAwardDet.getAwardedPrice());
								}
							}
						}
						Double paperApprovalDays = null;
						if (tatReport.getEvaluationCompletedDate() != null) {
							paperApprovalDays = DateUtil.differenceInDays(new Date(), tatReport.getEvaluationCompletedDate());
						}

						tatReportService.updateTatReportAwardDetails(tatReport.getId(), awardedSuppliersName, awardedSuppliersValue, new Date(), EventStatus.FINISHED, new Date(), new Date(), paperApprovalDays != null ? BigDecimal.valueOf(paperApprovalDays).setScale(2, RoundingMode.HALF_UP) : null);

					}
				} catch (Exception e) {
					LOG.info("ERROR while updating tat report : " + e.getMessage(), e);
				}
			}
		} else if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {

			LOG.info("ERP Is Enabled...");
			// If Event Award is supposed to be pushed to ERP, check if its interface type is TYPE_2 (which means FGV -
			// i.e. with budget checking)
			// If this business unit is not enabled for budget checking then complete the process.
			if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2 && Boolean.FALSE == rfqEvent.getBusinessUnit().getBudgetCheck()) {
				// rfqEvent.setStatus(EventStatus.FINISHED);
				// rfqEvent.setAwarded(Boolean.TRUE);
				// rfqEventDao.update(rfqEvent);

				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRfqEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfqEventAwardDao.getRfqEventAwardDetailsByEventIdandBqId(dbRfqEventAward.getId());

						for (AwardReferenceNumberPojo rfaEventAwardDet : award) {
							if (rfaEventAwardDet.getAwardedPrice() != null && StringUtils.checkString(rfaEventAwardDet.getSupllierCompanyName()).length() > 0) {
								if (StringUtils.checkString(awardedSuppliersName).length() == 0) {
									awardedSuppliersName += rfaEventAwardDet.getSupllierCompanyName();
								} else {
									awardedSuppliersName += ", " + rfaEventAwardDet.getSupllierCompanyName();
								}

								if (StringUtils.checkString(awardedSuppliersValue).length() == 0) {
									awardedSuppliersValue += df.format(rfaEventAwardDet.getAwardedPrice());
								} else {
									awardedSuppliersValue += ", " + df.format(rfaEventAwardDet.getAwardedPrice());
								}
							}
						}
						Double paperApprovalDays = null;
						if (tatReport.getEvaluationCompletedDate() != null) {
							paperApprovalDays = DateUtil.differenceInDays(new Date(), tatReport.getEvaluationCompletedDate());
						}
						tatReportService.updateTatReportAwardDetails(tatReport.getId(), awardedSuppliersName, awardedSuppliersValue, new Date(), EventStatus.FINISHED, new Date(), new Date(), paperApprovalDays != null ? BigDecimal.valueOf(paperApprovalDays).setScale(2, RoundingMode.HALF_UP) : null);
					}
				} catch (Exception e) {
					LOG.info("ERROR while updating tat report : " + e.getMessage(), e);
				}
			}
		}

		return dbRfqEventAward;
	}

	@Override
	public RfqEventAward rfqEventAwardByEventIdandBqId(String eventId, String bqId) {
		RfqEventAward eventAward = rfqEventAwardDao.rfqEventAwardByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfqEventAwardDetails details : eventAward.getRfxAwardDetails()) {
					if (details.getSupplier() != null) {
						LOG.info(details.getSupplier().getId());

						details.getSupplier().getId();
					}
					if (details.getBqItem() != null) {
						details.getBqItem().getId();
					}
					LOG.info(details.getAwardedPrice());
					LOG.info(details.getOriginalPrice());
					details.getAwardedPrice();
					details.getOriginalPrice();
					details.getTaxType();
					details.getTax();
				}

			}
		}
		return eventAward;
	}

	@Override
	public RfqSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId) {
		RfqSupplierBqItem rfqSupplierBqItem = rfqSupplierBqItemDao.getBqItemByRfqBqItemId(bqItemId, supplierId);
		if (rfqSupplierBqItem != null) {
			rfqSupplierBqItem.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, supplierId));
			if (CollectionUtil.isNotEmpty(rfqSupplierBqItem.getProductCategoryList())) {
				for (ProductCategory productCategory : rfqSupplierBqItem.getProductCategoryList()) {
					productCategory.setBuyer(null);
					productCategory.setCreatedBy(null);
					productCategory.setModifiedBy(null);
				}
			}
			if (rfqSupplierBqItem.getSupplier() != null) {
				rfqSupplierBqItem.getSupplier().getFullName();
				// supp.getFullName();
			}
			if (CollectionUtil.isNotEmpty(rfqSupplierBqItem.getChildren())) {
				rfqSupplierBqItem.setChildren(null);
			}
			if (rfqSupplierBqItem.getParent() != null) {
				rfqSupplierBqItem.setParent(null);
				// if()
			}
			if (rfqSupplierBqItem.getBqItem() != null) {
				RfqBqItem bqitem = rfqSupplierBqItem.getBqItem();
				bqitem.getItemName();
			}
			if (rfqSupplierBqItem.getUom() != null) {
				rfqSupplierBqItem.setUom(null);
			}

		}
		return rfqSupplierBqItem;
	}

	@Override
	public List<RfqEventAward> getRfqEventAwardsByEventId(String eventId) {
		List<RfqEventAward> awardList = rfqEventAwardDao.getRfqEventAwardsByEventId(eventId);
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfqEventAward rfqEventAward : awardList) {
				if (CollectionUtil.isNotEmpty(rfqEventAward.getRfxAwardDetails())) {
					for (RfqEventAwardDetails detail : rfqEventAward.getRfxAwardDetails()) {
						if (detail.getBqItem() != null) {
							detail.getBqItem().getItemName();
						}
					}
				}
			}
		}
		return awardList;
	}

	@Override
	@Transactional(readOnly = false)
	public void transferRfqAward(String eventId, String tenantId, HttpSession session, User loggedInUser, String rfqAwardEvevtId, Boolean transfer, RfxTypes eventType) throws Exception {

		/*uncomment to bypass SSL verification in local */
		/*TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}*/
		/*kdkd*/

		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(tenantId);

		if (Boolean.FALSE == erpSetup.getIsErpEnable()) {
			LOG.warn(">>>>>>>>> ERP IS NOT ENABLED. Skipping transfer...  Event Id: " + eventId);
			return;
		}

		RfqEvent rfqEvent = rfqEventDao.getPlainEventById(eventId);
		boolean isFailFromERP = false;
		// If award type integration is FGV
		if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2) {

			if (Boolean.FALSE == rfqEvent.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("Business unit not enabled for budget checking ... Event Id : " + eventId + ", Business Unit : " + rfqEvent.getBusinessUnit().getUnitName());
				throw new ApplicationException("Cannot send this event award details to ERP as Event Business unit not enabled for budget checking.");
			}

			RfqEventAward dbRfqEventAward = rfqEventAwardDao.findById(rfqAwardEvevtId);

			if (dbRfqEventAward == null) {
				LOG.error("Could not identify the award details to push to ERP. Please contact the application administrator. Award ID : " + rfqAwardEvevtId + ", Event Id : " + eventId);
				throw new ApplicationException("Could not identify the award details to push to ERP. Please contact the application administrator.");
			}

			AwardErp2Pojo awardPojo = new AwardErp2Pojo();

			if (StringUtils.checkString(rfqEvent.getPreviousRequestId()).length() == 0) {
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			}
			SourcingFormRequest rfs = sourcingFormRequestDao.findById(rfqEvent.getPreviousRequestId());

			if (rfs == null) {
				LOG.warn("RFS not found during award... Event Id : " + eventId + ", RFS ID : " + rfqEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			} else if (Boolean.FALSE == rfs.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("RFS Business unit not enabled for budget checking ... Event Id : " + eventId + ", RFS ID : " + rfqEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as RFS Business unit not enabled for budget checking.");
			}

			// pr.setErpDocNo(prResponse.getSapRefNo());
			// pr.setErpMessage(prResponse.getMessage());
			// pr.setErpPrTransferred(Boolean.TRUE);
			// pr.setErpStatus(prResponse.getStatus());

			awardPojo.setSapRefNo(rfs.getErpDocNo());
			awardPojo.setLoginEmail(loggedInUser.getLoginId());
			awardPojo.setSapDocType(SapDocType.RFQ);
			awardPojo.setEventId(rfqEvent.getEventId());
			List<AwardDetailsErp2Pojo> items = new ArrayList<AwardDetailsErp2Pojo>();

			// Order the award details by BQ Level and Order sequence
			SortedMap<Integer, RfqEventAwardDetails> sm = new TreeMap<Integer, RfqEventAwardDetails>();
			for (RfqEventAwardDetails eventAward : dbRfqEventAward.getRfxAwardDetails()) {
				if (eventAward.getBqItem().getOrder() > 0) {
					String str = String.valueOf(eventAward.getBqItem().getLevel() * 10000) + String.valueOf(eventAward.getBqItem().getOrder());
					sm.put(Integer.parseInt(str), eventAward);
				}
			}

			int sequence = 1;
			for (Map.Entry<Integer, RfqEventAwardDetails> m : sm.entrySet()) {
				AwardDetailsErp2Pojo item = new AwardDetailsErp2Pojo();
				item.setItemNo(StringUtils.lpad(String.valueOf(sequence * 10), 5, '0'));
				item.setItemCode(m.getValue().getBqItem().getField1());
				item.setQuantity(m.getValue().getBqItem().getQuantity());
				// item.setUnitPrice(m.getValue().getAwardedPrice());
				// FIX for PH-1168 - Unitprice should be computed from total award price/quantity rounding to 2 decimal
				item.setUnitPrice(m.getValue().getAwardedPrice().divide(m.getValue().getBqItem().getQuantity(), 2, RoundingMode.DOWN));
				// get Fav Supplier Vendor Code based on supplier id for the tenant.
				String vendorCode = favoriteSupplierDao.getFavouriteSupplierVendorCodeBySupplierIdAndTenant(m.getValue().getSupplier().getId(), tenantId);
				item.setVendorCode(vendorCode);
				items.add(item);
				sequence++;
			}
			awardPojo.setBqItems(items);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

			String responseMsg = "";
			HttpHeaders headers = new HttpHeaders();
			ObjectMapper mapperObj = new ObjectMapper();

			if (StringUtils.checkString(erpSetup.getErpUsername()).length() > 0 && StringUtils.checkString(erpSetup.getErpPassword()).length() > 0) {
				String auth = StringUtils.checkString(erpSetup.getErpUsername()) + ":" + StringUtils.checkString(erpSetup.getErpPassword());
				byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				headers.set("Authorization", authHeader);
			}

			String payload = mapperObj.writeValueAsString(awardPojo);
			ERP_LOG.info("jsonObject  :" + payload);

			HttpEntity<AwardErp2Pojo> request = new HttpEntity<AwardErp2Pojo>(awardPojo, headers);

			try {
				PrResponseUpdateWrapper prResponseUpdateWrapper = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRUpdate/", request, PrResponseUpdateWrapper.class);
				dbRfqEventAward.setTransferred(Boolean.TRUE);
				dbRfqEventAward = rfqEventAwardDao.save(dbRfqEventAward);

				// ASYNC TO SYNC
				if(prResponseUpdateWrapper != null && prResponseUpdateWrapper.getPrResponsePojo() != null &&
						StringUtils.checkString(prResponseUpdateWrapper.getPrResponsePojo().getStatus()).equalsIgnoreCase("FAIL")) {

					rfqEventService.revertEventAwardStatus(rfqEvent.getId());
					isFailFromERP = true;

					try {
						tatReportService.updateTatReportOnSapResponse(rfqEvent.getId(), EventStatus.COMPLETE, erpSetup.getTenantId());
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				} else {
					LOG.info("SAP Response success received for event " + eventId);
					try {
						TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(rfqEvent.getId(), erpSetup.getTenantId());
						if (tatReport != null) {
							setSapPoNumbers(prResponseUpdateWrapper.getPrResponsePojo(), loggedInUser.getBuyer(), tatReport);
							double diffPOcDAndRlad = DateUtil.differenceInDays(new Date(), tatReport.getLastApprovedDate());
							BigDecimal diffPOcDAndRladBigD = BigDecimal.valueOf(diffPOcDAndRlad).setScale(2, RoundingMode.HALF_UP);
							BigDecimal overallTat = diffPOcDAndRladBigD.subtract(tatReport.getPaperApprovalDaysCount() != null ? tatReport.getPaperApprovalDaysCount() : BigDecimal.ZERO);
							tatReportService.updateTatReportSapoDetails(tatReport.getId(), prResponseUpdateWrapper.getPrResponsePojo().getSapRefNo(), new Date(), overallTat);
						}
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				}

				String message = "";
				message = "ERP replied " + prResponseUpdateWrapper.getPrResponsePojo().getStatus() + " for " + prResponseUpdateWrapper.getPrResponsePojo().getSapDocType() + " with Reference : " + prResponseUpdateWrapper.getPrResponsePojo().getSapRefNo() + " Response: " + prResponseUpdateWrapper.getPrResponsePojo().getMessage();
				RfqEventAudit rfqEventAudit = new RfqEventAudit(rfqEvent, loggedInUser, new Date(), AuditActionType.Transfer, message);

				eventAuditService.save(rfqEventAudit);
				// end of ASYNC TO SYNC

				try {

					JasperPrint eventAward1 = getAwardSnapShotPdf(dbRfqEventAward, session, loggedInUser, null, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbRfqEventAward, session, loggedInUser, null, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("FILE Size : " + snapshot.length);
						RfqEventAwardAudit audit = new RfqEventAwardAudit(dbRfqEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfqAwardAudit(audit);
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfqEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while saving Award Audit :" + e.getMessage(), e);
				}

			} catch (HttpClientErrorException | HttpServerErrorException ex) {
				responseMsg = ex.getMessage();
				ERP_LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
				ERP_LOG.error("Response Body : " + ex.getResponseBodyAsString());
				throw new ApplicationException("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(), ex);
			}

		} else { // Assumed to be null or type_1
			List<RfqEventAward> awardList = getRfqEventAwardsByEventId(eventId);

			if (CollectionUtil.isEmpty(awardList)) {
				LOG.warn("Award List is Empty for eventId :" + eventId);
				return;
			}
			List<AwardErpPojo> awardPojoList = new ArrayList<>();
			for (RfqEventAward eventAward : awardList) {
				AwardErpPojo award = new AwardErpPojo();
				award.setId(eventAward.getId());
				award.setEventId(eventAward.getRfxEvent().getEventId());
				award.setEventName(eventAward.getRfxEvent().getEventName());
				award.setCurrencyCode(eventAward.getRfxEvent().getBaseCurrency() != null ? eventAward.getRfxEvent().getBaseCurrency().getCurrencyCode() : "");
				award.setAwardRemark(eventAward.getAwardRemarks());
				award.setEventOwner(eventAward.getRfxEvent().getCreatedBy() != null ? eventAward.getCreatedBy().getName() : "");
				award.setBusinessUnitName(eventAward.getRfxEvent().getBusinessUnit() != null ? eventAward.getRfxEvent().getBusinessUnit().getDisplayName() : "");
				award.setReferenceNumber(StringUtils.checkString(eventAward.getRfxEvent().getReferanceNumber()));
				award.setCreatedDate(eventAward.getRfxEvent().getCreatedDate());
				award.setStartDate(eventAward.getRfxEvent().getEventStart());
				award.setEndDate(eventAward.getRfxEvent().getEventEnd());
				award.setValidityDays(eventAward.getRfxEvent().getSubmissionValidityDays());
				award.setDeliveryDate(eventAward.getRfxEvent().getDeliveryDate());
				award.setPaymentTerm(eventAward.getRfxEvent().getPaymentTerm());
				award.setBqName(eventAward.getBq() != null ? eventAward.getBq().getName() : "");
				if (eventAward.getRfxEvent().getDeliveryAddress() != null) {
					DeliveryAddressPojo deliveryAddress = new DeliveryAddressPojo();
					deliveryAddress.setTitle(eventAward.getRfxEvent().getDeliveryAddress().getTitle());
					deliveryAddress.setLine1(eventAward.getRfxEvent().getDeliveryAddress().getLine1());
					deliveryAddress.setLine2(eventAward.getRfxEvent().getDeliveryAddress().getLine2());
					deliveryAddress.setCity(eventAward.getRfxEvent().getDeliveryAddress().getCity());
					deliveryAddress.setZip(eventAward.getRfxEvent().getDeliveryAddress().getZip());

					if (eventAward.getRfxEvent().getDeliveryAddress().getState() != null) {
						deliveryAddress.setState(eventAward.getRfxEvent().getDeliveryAddress().getState().getStateCode());
						State state = stateService.getState(eventAward.getRfxEvent().getDeliveryAddress().getState().getId());
						eventAward.getRfxEvent().getDeliveryAddress().setCountry(state.getCountry());
					}
					if (eventAward.getRfxEvent().getDeliveryAddress().getCountry() != null)
						deliveryAddress.setCountry(eventAward.getRfxEvent().getDeliveryAddress().getCountry().getCountryCode());

					award.setDeliveryAddress(deliveryAddress);
				}
				List<AwardDetailsErpPojo> awardDetailsList = new ArrayList<>();
				for (RfqEventAwardDetails eventAwardDetails : eventAward.getRfxAwardDetails()) {
					if (eventAwardDetails.getBqItem().getOrder() == 0) {
						continue;
					}
					AwardDetailsErpPojo awardDetails = new AwardDetailsErpPojo();
					// setting fav supplier vendorCode
					if (eventAwardDetails.getSupplier() != null) {
						FavouriteSupplier supplier = favoriteSupplierService.findFavSupplierBySuppId(eventAwardDetails.getSupplier().getId(), tenantId);
						if (StringUtils.checkString(supplier.getVendorCode()).length() > 0) {
							ErpSupplierPojo supplierPojo = new ErpSupplierPojo();
							supplierPojo.setCity(supplier.getSupplier().getCity());
							supplierPojo.setCommunicationEmail(supplier.getSupplier().getCommunicationEmail());
							supplierPojo.setCompanyName(supplier.getSupplier().getCompanyName());
							supplierPojo.setMobileNumber(supplier.getSupplier().getMobileNumber());
							supplierPojo.setCompanyContactNumber(supplier.getSupplier().getCompanyContactNumber());
							supplierPojo.setCountryCode(supplier.getSupplier().getRegistrationOfCountry().getCountryCode());
							supplierPojo.setCompanyRegistrationNumber(supplier.getSupplier().getCompanyRegistrationNumber());
							supplierPojo.setFullName(supplier.getSupplier().getFullName());
							supplierPojo.setDesignation(supplier.getSupplier().getDesignation());
							supplierPojo.setYearOfEstablished(supplier.getSupplier().getYearOfEstablished());
							supplierPojo.setFaxNumber(supplier.getSupplier().getFaxNumber());
							supplierPojo.setLine1(supplier.getSupplier().getLine1());
							supplierPojo.setLine2(supplier.getSupplier().getLine2());
							supplierPojo.setStateCode(supplier.getSupplier().getState().getStateCode());
							supplierPojo.setTaxNumber(supplier.getTaxNumber());
							awardDetails.setVendorCode(supplier.getVendorCode());
							awardDetails.setSupplier(supplierPojo);
							awardDetails.setVendorCode(supplier.getVendorCode());
						} else {
							throw new ApplicationException("Vendor Code not Assigned to " + supplier.getSupplier().getCompanyName());
						}
					}
					awardDetails.setTotalAmount(eventAwardDetails.getTotalPrice());
					awardDetails.setLnno(eventAwardDetails.getBqItem().getParent().getField8());
					awardDetails.setId(eventAwardDetails.getId());
					awardDetails.setLevel(eventAwardDetails.getBqItem().getLevel());
					awardDetails.setOrder(eventAwardDetails.getBqItem().getOrder());
					awardDetails.setItemName(eventAwardDetails.getBqItem().getItemName());
					awardDetails.setItemDesc(eventAwardDetails.getBqItem().getItemDescription());
					awardDetails.setQuantity(eventAwardDetails.getBqItem().getQuantity());
					awardDetails.setUnitPrice(eventAwardDetails.getAwardedPrice().divide(eventAwardDetails.getBqItem().getQuantity(), 3, RoundingMode.DOWN));

					if (eventAwardDetails.getBqItem().getUom() != null) {
						awardDetails.setUom(eventAwardDetails.getBqItem().getUom().getUom());
					}
					awardDetails.setItemCategory(eventAwardDetails.getBqItem().getField1());
					awardDetails.setBqItemCode(eventAwardDetails.getBqItem().getField2());
					awardDetails.setMfr_PartNO(eventAwardDetails.getBqItem().getField5());
					awardDetails.setMaterialGroup(eventAwardDetails.getBqItem().getField3());
					awardDetails.setItemNo(eventAwardDetails.getBqItem().getField8());

					awardDetailsList.add(awardDetails);
				}
				award.setBqItems(awardDetailsList);
				awardPojoList.add(award);
			}
			AwardResponsePojo resultMap = erpIntegrationService.sendAwardPage(awardPojoList, eventId, eventType);

			if (StringUtils.checkString(rfqAwardEvevtId).length() > 0) {
				RfqEventAward dbRfqEventAward = rfqEventAwardDao.findById(rfqAwardEvevtId);

				try {
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbRfqEventAward, session, loggedInUser, resultMap, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbRfqEventAward, session, loggedInUser, resultMap, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfqEventAwardAudit audit = new RfqEventAwardAudit(dbRfqEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfqAwardAudit(audit);
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfqEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
			}
		}

		if (rfqEvent != null && rfqEvent.getAwardDate() == null) {
			rfqEvent.setAwardDate(new Date());
			rfqEvent.setConcludeDate(new Date());
		}

		if(isFailFromERP == false){
			rfqEvent.setStatus(EventStatus.FINISHED);
			rfqEvent.setAwarded(Boolean.TRUE);
		}else{
			rfqEvent.setStatus(EventStatus.COMPLETE);
			rfqEvent.setAwarded(Boolean.FALSE);
			rfqEvent.setAwardStatus(AwardStatus.DRAFT);
		}

		rfqEvent.setConcludeBy(loggedInUser);

		rfqEventDao.update(rfqEvent);

		DecimalFormat df = null;
		if (rfqEvent.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (rfqEvent.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (rfqEvent.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (rfqEvent.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (rfqEvent.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (rfqEvent.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		try {
			TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, tenantId);
			String awardedSuppliersName = "";
			String awardedSuppliersValue = "";

			if (tatReport != null) {
				List<AwardReferenceNumberPojo> award = rfqEventAwardDao.getRfqEventAwardDetailsByEventIdandBqId(rfqAwardEvevtId);

				for (AwardReferenceNumberPojo rfaEventAwardDet : award) {
					if (rfaEventAwardDet.getAwardedPrice() != null && StringUtils.checkString(rfaEventAwardDet.getSupllierCompanyName()).length() > 0) {
						if (StringUtils.checkString(awardedSuppliersName).length() == 0) {
							awardedSuppliersName += rfaEventAwardDet.getSupllierCompanyName();
						} else {
							awardedSuppliersName += ", " + rfaEventAwardDet.getSupllierCompanyName();
						}

						if (StringUtils.checkString(awardedSuppliersValue).length() == 0) {
							awardedSuppliersValue += df.format(rfaEventAwardDet.getAwardedPrice());
						} else {
							awardedSuppliersValue += ", " + df.format(rfaEventAwardDet.getAwardedPrice());
						}
					}
				}
				Double paperApprovalDays = null;
				if (tatReport.getEvaluationCompletedDate() != null) {
					paperApprovalDays = DateUtil.differenceInDays(new Date(), tatReport.getEvaluationCompletedDate());
				}

				tatReportService.updateTatReportAwardDetails(tatReport.getId(), awardedSuppliersName, awardedSuppliersValue, new Date(), EventStatus.FINISHED, new Date(), new Date(), paperApprovalDays != null ? BigDecimal.valueOf(paperApprovalDays).setScale(2, RoundingMode.HALF_UP) : null);

			}
		} catch (Exception e) {
			LOG.info("ERROR while updating tat report : " + e.getMessage(), e);
		}

	}

	@Override
	public JasperPrint getAwardSnapShotPdf(RfqEventAward dbRfqEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) {

		// For Financial Standard
		DecimalFormat df = null;
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		if (dbRfqEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		JasperPrint eventAward = null;
		List<RftEventAwardDetailsPojo> auditTrailSummary = new ArrayList<RftEventAwardDetailsPojo>();

		List<RfqEventAwardDetails> rfqEventAwardDetails = dbRfqEventAward.getRfxAwardDetails();

		if (CollectionUtil.isNotEmpty(rfqEventAwardDetails)) {

			for (RfqEventAwardDetails rfqEventAwardDetail : rfqEventAwardDetails) {
				RftEventAwardDetailsPojo rftEventAwardDetailsPojo = new RftEventAwardDetailsPojo();
				if (rfqEventAwardDetail.getBqItem() != null) {
					String itemSeq = rfqEventAwardDetail.getBqItem().getLevel() + "." + rfqEventAwardDetail.getBqItem().getOrder();
					rftEventAwardDetailsPojo.setItemSeq(itemSeq);
					rftEventAwardDetailsPojo.setItemName(StringUtils.checkString(rfqEventAwardDetail.getBqItem().getItemName()));
					if (rfqEventAwardDetail.getSupplier() != null && rfqEventAwardDetail.getSupplier().getCompanyName() != null) {
						rftEventAwardDetailsPojo.setSupplierName(rfqEventAwardDetail.getSupplier().getCompanyName());
					} else {
						rftEventAwardDetailsPojo.setSupplierName("");
					}
					rftEventAwardDetailsPojo.setSupplierPrice(rfqEventAwardDetail.getOriginalPrice() != null ? df.format(rfqEventAwardDetail.getOriginalPrice()) : "");
					rftEventAwardDetailsPojo.setAwardedPrice(rfqEventAwardDetail.getAwardedPrice() != null ? df.format(rfqEventAwardDetail.getAwardedPrice()) : "");
					if ((rfqEventAwardDetail.getBqItem().getOrder() > 0)) {
						if (rfqEventAwardDetail.getTax() != null) {
							rftEventAwardDetailsPojo.setTax(df.format(rfqEventAwardDetail.getTax()));
						} else {
							rftEventAwardDetailsPojo.setTax("N/A");
						}
						if (awardResponsePojo != null) {
							if (CollectionUtil.isNotEmpty(awardResponsePojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardReferenceNumberPojo : awardResponsePojo.getRefNumList()) {
									if (rfqEventAwardDetail.getBqItem().getLevel() == awardReferenceNumberPojo.getLevel() && rfqEventAwardDetail.getBqItem().getOrder() == awardReferenceNumberPojo.getOrder()) {
										rftEventAwardDetailsPojo.setRefNo(awardReferenceNumberPojo.getReferenceNumber());
									}
								}
							} else {
								rftEventAwardDetailsPojo.setRefNo("N/A");
							}
						} else {
							rftEventAwardDetailsPojo.setRefNo("N/A");
						}
					} else {
						rftEventAwardDetailsPojo.setTax(" ");
					}
					// rftEventAwardDetailsPojo.setTax(((rftEventAwardDetail.getBqItem().getOrder() > 0) &&
					// rftEventAwardDetail.getTax() != null) ? df.format(rftEventAwardDetail.getTax()) : "N/A");
					rftEventAwardDetailsPojo.setTaxType(rfqEventAwardDetail.getTaxType() != null ? rfqEventAwardDetail.getTaxType().toString() : "");
					rftEventAwardDetailsPojo.setTotalPrice(rfqEventAwardDetail.getTotalPrice() != null ? df.format(rfqEventAwardDetail.getTotalPrice()) : "");

				} else {
					rftEventAwardDetailsPojo.setItemName(" ");
					rftEventAwardDetailsPojo.setSupplierName(" ");
					rftEventAwardDetailsPojo.setAwardedPrice(" ");
					rftEventAwardDetailsPojo.setTax(" ");
					rftEventAwardDetailsPojo.setTaxType("");
				}
				rftEventAwardDetailsPojo.setCurrentDate(sdf.format(new Date()));
				auditTrailSummary.add(rftEventAwardDetailsPojo);

			}
		}

		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			Resource resource = applicationContext.getResource("classpath:reports/AwardAuditReport.jasper");
			File jasperfile = resource.getFile();
			parameters.put("AUDIT_TRAIL", auditTrailSummary);
			parameters.put("date", sdf.format(new Date()));
			parameters.put("title", "Award History");
			parameters.put("remark", dbRfqEventAward.getAwardRemarks());
			parameters.put("eventNo", dbRfqEventAward.getRfxEvent().getEventId());
			parameters.put("eventName", dbRfqEventAward.getRfxEvent().getEventName());
			parameters.put("bqName", dbRfqEventAward.getBq().getName());
			parameters.put("actionBy", loggedInUser.getName());
			if (Boolean.TRUE == transfer) {
				parameters.put("actionType", "Save & Transfer");
			} else if (Boolean.TRUE == conclude) {
				parameters.put("actionType", "Conclude Award");
			} else {
				parameters.put("actionType", "Save");
			}

			parameters.put("totalSupplierPrice", String.valueOf(df.format(dbRfqEventAward.getTotalSupplierPrice())));
			parameters.put("totalAwardPrice", String.valueOf(df.format(dbRfqEventAward.getTotalAwardPrice())));
			parameters.put("GrandPrice", String.valueOf(df.format(dbRfqEventAward.getGrandTotalPrice())));

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auditTrailSummary, false);
			eventAward = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Could not generate Audit Trail PDF Report. " + e.getMessage(), e);
		}

		return eventAward;
	}

	@Override
	public ByteArrayOutputStream getAwardSnapShotXcel(RfqEventAward dbRfqEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DecimalFormat df = null;
		if (dbRfqEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfqEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		Workbook workbook = new XSSFWorkbook();
		try {
			List<RfqEventAwardDetails> rftEventAwardDetails = dbRfqEventAward.getRfxAwardDetails();
			Sheet spreadsheet = workbook.createSheet("History");
			Cell cell;
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			Row row1 = spreadsheet.createRow((short) 0);
			cell = row1.createCell(1);
			cell.setCellValue("Award History Report");
			CellStyle styleHeading1 = workbook.createCellStyle();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading1.setFont(font);
			styleHeading1.setAlignment(CellStyle.ALIGN_CENTER);
			cell.setCellStyle(styleHeading1);
			spreadsheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 7));

			row1 = spreadsheet.createRow((short) 2);
			cell = row1.createCell(1);
			cell.setCellValue("Event no : ");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(2);
			cell.setCellValue(dbRfqEventAward.getRfxEvent().getEventId());

			cell = row1.createCell(4);
			cell.setCellValue("Action By");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(5);
			cell.setCellValue(loggedInUser.getName());

			row1 = spreadsheet.createRow((short) 3);
			cell = row1.createCell(1);
			cell.setCellValue("Event name");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(2);
			cell.setCellValue(dbRfqEventAward.getRfxEvent().getEventName());

			cell = row1.createCell(4);
			cell.setCellValue("Action Type");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(5);
			if (Boolean.TRUE == transfer) {
				cell.setCellValue("Save & Transfer");
			} else if (Boolean.TRUE == conclude) {
				cell.setCellValue("Conclude Award");
			} else {
				cell.setCellValue("Save");
			}

			row1 = spreadsheet.createRow((short) 4);
			cell = row1.createCell(1);
			cell.setCellValue("BQ name : ");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(2);
			cell.setCellValue(dbRfqEventAward.getBq().getName());

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(timeZone);
			String strDate = sdf.format(new Date());
			cell = row1.createCell(4);
			cell.setCellValue("Action Date");
			cell.setCellStyle(styleHeading);
			cell = row1.createCell(5);
			cell.setCellValue(strDate);

			Row row = spreadsheet.createRow(7);

			int cellNo = 0;
			styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			for (String col : Global.EVENT_AWARD_EXCEL_COLUMNS) {
				cell = row.createCell(cellNo++);
				cell.setCellValue(col);
				cell.setCellStyle(styleHeading);
			}
			// Write Data into Excel
			int cellNumber = 0;
			int i = 8;
			for (RfqEventAwardDetails rftEventAwardDetail : rftEventAwardDetails) {
				row = spreadsheet.createRow(i++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getBqItem().getLevel() + "." + rftEventAwardDetail.getBqItem().getOrder());
				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getBqItem() != null ? StringUtils.checkString(rftEventAwardDetail.getBqItem().getItemName()).length() > 0 ? rftEventAwardDetail.getBqItem().getItemName() : "" : "");

				if (rftEventAwardDetail.getSupplier() != null) {
					row.createCell(cellNum++).setCellValue(StringUtils.checkString(rftEventAwardDetail.getSupplier().getCompanyName()).length() > 0 ? rftEventAwardDetail.getSupplier().getCompanyName() : "");
				} else {
					row.createCell(cellNum++).setCellValue("");
				}

				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getOriginalPrice() != null ? StringUtils.checkString(df.format(rftEventAwardDetail.getOriginalPrice())).length() > 0 ? df.format(rftEventAwardDetail.getOriginalPrice()) : "N/A" : "");
				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getAwardedPrice() != null ? df.format(rftEventAwardDetail.getAwardedPrice()) : "");
				// row.createCell(cellNum++).setCellValue("XYZ");
				// row.createCell(cellNum++).setCellValue("abc");

				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getTaxType() != null ? rftEventAwardDetail.getTaxType().toString() : "");
				if ((rftEventAwardDetail.getBqItem().getOrder() > 0)) {
					if (rftEventAwardDetail.getTax() != null) {
						row.createCell(cellNum++).setCellValue(df.format(rftEventAwardDetail.getTax()));
					} else {
						row.createCell(cellNum++).setCellValue("N/A");
					}
				} else {
					row.createCell(cellNum++).setCellValue(" ");
				}
				row.createCell(cellNum++).setCellValue(rftEventAwardDetail.getTotalPrice() != null ? df.format(rftEventAwardDetail.getTotalPrice()) : "");
				if ((rftEventAwardDetail.getBqItem().getOrder() > 0)) {
					if (awardResponsePojo != null) {
						if (CollectionUtil.isNotEmpty(awardResponsePojo.getRefNumList())) {
							for (AwardReferenceNumberPojo awardReferenceNumberPojo : awardResponsePojo.getRefNumList()) {
								if (rftEventAwardDetail.getBqItem().getLevel() == awardReferenceNumberPojo.getLevel() && rftEventAwardDetail.getBqItem().getOrder() == awardReferenceNumberPojo.getOrder()) {
									row.createCell(cellNum++).setCellValue(awardReferenceNumberPojo.getReferenceNumber());
								}
							}
						} else {
							row.createCell(cellNum++).setCellValue("N/A");
						}
					} else {
						row.createCell(cellNum++).setCellValue("N/A");
					}
				}
				cellNumber = cellNum;
			}
			i++;
			row = spreadsheet.createRow(i++);
			LOG.info("cellNumber : " + cellNumber);
			cell = row.createCell(cellNumber - 2);
			LOG.info("cellNumber : " + cellNumber);
			cell.setCellValue(df.format(dbRfqEventAward.getGrandTotalPrice()));
			cell.setCellStyle(styleHeading);
			try {
				cell = row.createCell(cellNumber - 5);
				cell.setCellValue(String.valueOf(df.format(dbRfqEventAward.getTotalAwardPrice())));
				cell.setCellStyle(styleHeading);
				cell = row.createCell(cellNumber - 6);
				cell.setCellValue(String.valueOf(df.format(dbRfqEventAward.getTotalAwardPrice())));
				cell.setCellStyle(styleHeading);
				cell = row.createCell(cellNumber - 7);
				cell.setCellValue("Total");
				cell.setCellStyle(styleHeading);
			} catch (Exception e) {
				LOG.error("Error while :" + e.getMessage(), e);
			}

			int j = (i++) + 1;
			row = spreadsheet.createRow(j);
			cell = row.createCell(0);
			cell.setCellValue("Remarks");
			cell.setCellStyle(styleHeading);

			Cell cellRemarks = row.createCell(1);
			cellRemarks.setCellValue(dbRfqEventAward.getAwardRemarks());
			for (int k = 0; k < 15; k++) {
				spreadsheet.autoSizeColumn(k, true);
			}

			workbook.write(baos);
		} catch (Exception e) {
			LOG.error("Error while :" + e.getMessage(), e);
		}

		return baos;
	}

	@Override
	public void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws IOException {
		RfqEventAwardAudit audit = eventAwardAuditService.findByRfqAuditId(id);
		response.setContentLength(audit.getSnapshot().length);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".pdf\"");

		FileCopyUtils.copy(audit.getSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws IOException {
		RfqEventAwardAudit audit = eventAwardAuditService.findByRfqAuditId(id);
		response.setContentLength(audit.getExcelSnapshot().length);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".xls\"");

		FileCopyUtils.copy(audit.getExcelSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFileSnapShot(String id, HttpServletResponse response) throws IOException {
		RfqEventAwardAudit audit = eventAwardAuditService.findByRfqAuditId(id);
		LOG.info("File Name:" + audit.getFileName());
		response.setContentLength(audit.getFileData().length);
		response.setContentType(audit.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + audit.getFileName());
		FileCopyUtils.copy(audit.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFile(String id, HttpServletResponse response) throws IOException {
		RfqEventAward rfqEventAward = rfqEventAwardDao.findById(id);
		LOG.info("File Name:" + rfqEventAward.getFileName());
		response.setContentLength(rfqEventAward.getFileData().length);
		response.setContentType(rfqEventAward.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + rfqEventAward.getFileName());
		FileCopyUtils.copy(rfqEventAward.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public RfqEventAward rfqEventAwardDetailsByEventIdandBqId(String eventId, String bqId) {
		RfqEventAward eventAward = rfqEventAwardDao.rfqEventAwardDetailsByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfqEventAwardDetails details : eventAward.getRfxAwardDetails()) {
					if (details.getSupplier() != null) {
						details.getSupplier().getId();
					}
					if (details.getBqItem() != null) {
						details.getBqItem().getId();
					}
					details.getAwardedPrice();
					details.getOriginalPrice();
					details.getTaxType();
					details.getTax();
				}

			}
		}
		return eventAward;
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean updateEventAwardApproval(RfqEventAward eventAward, User user, String eventId) {
		LOG.info(" Rfa Event Award " + eventAward.getAwardApprovals());

		RfqEvent rfqEvent = rfqEventDao.findByEventId(eventId);

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfqEventAwardApproval approvalRequest : rfqEvent.getAwardApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfqAwardApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(eventAward.getAwardApprovals())) {
			int level = 1;
			for (RfqEventAwardApproval app : eventAward.getAwardApprovals()) {
				app.setEvent(rfqEvent);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqAwardApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);
						// If db existing user list is empty, then it means this is a new level which is not existing in
						// database yet.
						// therefore we need to add all user list coming from frontend for current level as new users
						if (CollectionUtil.isEmpty(existingUserList)) {
							auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
						} else {
							// If db existing user list does not contain the user coming from frontend, then it has been
							// added as new user for current level
							if (!existingUserList.contains(approvalUser.getUser().getName())) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
							}
						}

						levelUsers.add(approvalUser.getUser().getName());
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

		rfqEvent.setAwardApprovals(eventAward.getAwardApprovals());
		rfqEvent.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfqEvent.setModifiedDate(new Date());
		if (eventAward.getRfxEvent() != null) {
			rfqEvent.setEnableAwardApproval(eventAward.getRfxEvent().getEnableAwardApproval());
		}
		rfqEvent = rfqEventDao.saveOrUpdate(rfqEvent);
		for (RfqEventAwardApproval appr : rfqEvent.getAwardApprovals()) {
			for (RfqAwardApprovalUser apprs : appr.getApprovalUsers()) {
				LOG.info("Approvers Id " + apprs.getId());
			}
		}
		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfqEventAudit audit = new RfqEventAudit(rfqEvent, user, new Date(), AuditActionType.Update, auditMessage);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Award Approval Route is updated for Event '" + rfqEvent.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		return auditMessages.size() > 0 ? Boolean.TRUE : Boolean.FALSE;
	}

	private void setSapPoNumbers(PrResponsePojo prResponse, Buyer buyer, TatReportPojo tatReport) {
		String message = "";
		try {
			if (prResponse.getMessage() instanceof List) {
				HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) prResponse.getMessage()).get(0);
				message = hashMap.get("msg_item");
			} else {
				HashMap<String, String> hashMap = (HashMap<String, String>) prResponse.getMessage();
				message = hashMap.get("msg_item");
			}
		} catch (Exception k) {

		}
		if (StringUtils.checkString(message).length() > 11 && message.indexOf("(") != -1) {
			String vendorCode = message.substring(10, message.indexOf("("));
			String poId = prResponse.getSapRefNo();
			// get the supplier based on vendorCode and buyerId
			String supplierName = favoriteSupplierService.getSupplierNameByVendorCode(vendorCode, buyer.getId());
			if (StringUtils.checkString(supplierName).length() > 0) {
				String awardedSupp = tatReport.getAwardedSupplier();
				if (StringUtils.checkString(awardedSupp).length() > 0) {
					String[] awardedSupps = awardedSupp.split(",");
					String awardedSuppliersPos = tatReport.getSapPoId();
					int index = -1;
					for (String sup : awardedSupps) {
						if (!StringUtils.checkString(sup).equalsIgnoreCase(StringUtils.checkString(supplierName))) {
							index++;
						} else {
							break;
						}
					}

					if (StringUtils.checkString(awardedSuppliersPos).length() > 0) {
						String[] poIds = awardedSuppliersPos.split(",");
						for (int i = 0; i < poIds.length; i++) {
							poIds[i] = StringUtils.checkString(poIds[i]);
						}
						poIds[index + 1] = poId;
						tatReport.setSapPoId(String.join(", ", poIds));
					} else {
						String[] poIds = new String[awardedSupps.length];
						for (int i = 0; i < poIds.length; i++) {
							poIds[i] = "";
						}
						poIds[index + 1] = poId;
						tatReport.setSapPoId(String.join(", ", poIds));
					}
				} else {
					LOG.warn("Award Supplier details not found in TatReport ");
				}
			} else {
				LOG.warn("Supplier details not found for Vendor code " + vendorCode);
			}
		}
	}
}
