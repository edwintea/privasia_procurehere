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
import com.privasia.procurehere.core.dao.RfpEventAwardDao;
import com.privasia.procurehere.core.dao.RfpEventAwardAuditDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.RfpAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.entity.RfpEventAwardApproval;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpEventAwardDetails;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
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
import com.privasia.procurehere.core.pojo.AwardDetailsErpPojo;
import com.privasia.procurehere.core.pojo.AwardErp2Pojo;
import com.privasia.procurehere.core.pojo.AwardErpPojo;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;
import com.privasia.procurehere.core.pojo.DeliveryAddressPojo;
import com.privasia.procurehere.core.pojo.ErpSupplierPojo;
import com.privasia.procurehere.core.pojo.RftEventAwardDetailsPojo;
import com.privasia.procurehere.core.pojo.TatReportPojo;
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
import com.privasia.procurehere.service.RfpAwardService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.TatReportService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class RfpAwardServiceImpl implements RfpAwardService {

	private static final Logger LOG = LogManager.getLogger(RfpAwardServiceImpl.class);
	private static final Logger ERP_LOG = LogManager.getLogger(Global.ERP_LOG);

	@Autowired
	RfpEventAwardDao rfpEventAwardDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

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
	RfpEventDao rfpEventDao;

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
	RfpEventAwardAuditDao rfpEventAwardAuditDao;

	@Override
	@Transactional(readOnly = false)
	public RfpEventAward saveEventAward(RfpEventAward rfpEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude) {
		rfpEventAward.setCreatedBy(SecurityLibrary.getLoggedInUser());
		rfpEventAward.setCreatedDate(new Date());
		rfpEventAward.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfpEventAward.setModifiedDate(new Date());

		LOG.info(" Award Approvals " + rfpEventAward.getAwardApprovals());

		List<RfpEventAwardDetails> awardList = rfpEventAward.getRfxAwardDetails();
		// to avoid duplicate entry of awarded supplier
		Set<String> awardedSupplierIds = new HashSet<String>();
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfpEventAwardDetails rfpEventAwardDetails : awardList) {
				Supplier supplier = rfpEventAwardDetails.getSupplier();
				if ((supplier != null) && (supplier.getId() != null)) {
					awardedSupplierIds.add(supplier.getId());
				}
			}
		}
		RfpEvent rfpEvent = rfpEventDao.findByEventId(rfpEventAward.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(awardedSupplierIds)) {
			if (rfpEvent != null) {
				rfpEvent = rfpEventDao.findById(rfpEvent.getId());
				List<Supplier> awardedSuppliers = null;
				if (CollectionUtil.isNotEmpty(rfpEvent.getAwardedSuppliers())) {
					awardedSuppliers = rfpEvent.getAwardedSuppliers();
				} else {
					awardedSuppliers = new ArrayList<Supplier>();
				}
				for (String suppId : awardedSupplierIds) {
					Supplier supplier = new Supplier();
					supplier.setId(suppId);
					awardedSuppliers.add(supplier);
				}
				rfpEvent.setAwardedSuppliers(awardedSuppliers);
				rfpEvent = rfpEventDao.saveOrUpdate(rfpEvent);
			}
		}

		Set<String> supplierList = new HashSet<String>();

		if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {
			for (RfpEventAwardDetails awardDetails : rfpEventAward.getRfxAwardDetails()) {
				awardDetails.setEventAward(rfpEventAward);
				if (awardDetails.getSupplier() != null) {
					supplierList.add(awardDetails.getSupplier().getId());
				}
			}
		}

		RfpEventAward rfpEventAward2 = rfpEventAwardDao.findById(rfpEventAward.getId());
		if(rfpEventAward2 != null && rfpEventAward2.getFileData() != null) {
			rfpEventAward.setFileData(rfpEventAward2.getFileData());
			rfpEventAward.setFileName(rfpEventAward2.getFileName());
			rfpEventAward.setCredContentType(rfpEventAward2.getCredContentType());
		}
		RfpEventAward dbRfpEventAward = rfpEventAwardDao.saveOrUpdateWithFlush(rfpEventAward);

		if (dbRfpEventAward.getRfxEvent() != null) {
			Double sumOfAwardedPriceForEvent = rfpEventAwardDao.getSumOfAwardedPrice(dbRfpEventAward.getRfxEvent().getId());
			BigDecimal sumAwardPrice = null;
			if (sumOfAwardedPriceForEvent != null) {
				sumAwardPrice = new BigDecimal(sumOfAwardedPriceForEvent);
			}
			try {
				rfpEventDao.updateRfaForAwardPrice(dbRfpEventAward.getRfxEvent().getId(), sumAwardPrice);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(rfpEventAward.getAwardApprovals())) {
			int level = 1;
			for (RfpEventAwardApproval app : rfpEventAward.getAwardApprovals()) {
				app.setEvent(rfpEvent);
				app.setLevel(level++);
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
					for (RfpAwardApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);
					}
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		// Save the list of awarded suppliers into separate table
		List<Supplier> awardedSuppliers = new ArrayList<Supplier>();
		for (String supplierId : supplierList) {
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			awardedSuppliers.add(supplier);
		}
		RfpEvent event = rfpEventDao.findById(dbRfpEventAward.getRfxEvent().getId());
		event.setAwardedSuppliers(awardedSuppliers);
		// Only Save is called. Now set the Award status as DRAFT or our internal consumption.
		// DRAFT status is not shown to the user.
		if (!transfer && !conclude) {
			event.setAwardStatus(AwardStatus.DRAFT);
		}
		if (conclude) {
			event.setAwardStatus(AwardStatus.PENDING);
		}
		if (rfpEventAward.getRfxEvent() != null) {
			event.setEnableAwardApproval(rfpEventAward.getRfxEvent().getEnableAwardApproval());
		}
		event.setAwardApprovals(rfpEventAward.getAwardApprovals());
		event = rfpEventDao.update(event);

		// Jasper report method calling here-------------------------------------------------------------------------
		if (transfer == false && conclude == false) {
			if (StringUtils.checkString(dbRfpEventAward.getId()).length() > 0) {
				// RfpEventAward dbRfpEventAward = rfpEventAwardDao.findById(eventAward.getId());

				try {
					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbRfpEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbRfpEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfpEventAwardAudit audit = null;
						if (Boolean.FALSE == conclude) {
							audit = new RfpEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award saved", snapshot, excelSnapshot);
							if (rfpEventAward.getAttachment() != null) {
								LOG.info("File Name:-------->" + rfpEventAward.getAttachment().getOriginalFilename());
								RfpEventAward rfpEventAward1 = rfpEventAwardDao.findById(dbRfpEventAward.getId());
								try {
									rfpEventAward1.setFileData(rfpEventAward.getAttachment().getBytes());
									audit.setFileData(rfpEventAward.getAttachment().getBytes());
								} catch (IOException e) {
									LOG.error("Eroor While getting file data" + e.getMessage(), e);
								}
								rfpEventAward1.setFileName(rfpEventAward.getAttachment().getOriginalFilename());
								rfpEventAward1.setCredContentType(rfpEventAward.getAttachment().getContentType());
								audit.setFileName(rfpEventAward.getAttachment().getOriginalFilename());
								audit.setCredContentType(rfpEventAward.getAttachment().getContentType());
								rfpEventAwardDao.saveOrUpdate(rfpEventAward1);
							}
							eventAwardAuditService.saveRfpAwardAudit(audit);
						}
					}

				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
			}

		} else if(conclude) {
			if (rfpEventAward.getAttachment() != null) {
				LOG.info("File Name:-------->" + rfpEventAward.getAttachment().getOriginalFilename());
				RfpEventAward rfpEventAward1 = rfpEventAwardDao.findById(dbRfpEventAward.getId());
				try {
					rfpEventAward1.setFileData(rfpEventAward.getAttachment().getBytes());								} catch (IOException e) {
					LOG.error("Eroor While getting file data" + e.getMessage(), e);
				}
				rfpEventAward1.setFileName(rfpEventAward.getAttachment().getOriginalFilename());
				rfpEventAward1.setCredContentType(rfpEventAward.getAttachment().getContentType());
				rfpEventAwardDao.saveOrUpdate(rfpEventAward1);
			}
		}
		// Jasper end-------------------------------------------------------------------------
		if (dbRfpEventAward != null) {

			if (dbRfpEventAward.getRfxEvent() != null) {
				dbRfpEventAward.getRfxEvent().getId();
				dbRfpEventAward.getRfxEvent().getEventName();
			}
		}
		if (CollectionUtil.isNotEmpty(dbRfpEventAward.getRfxAwardDetails())) {
			for (RfpEventAwardDetails details : dbRfpEventAward.getRfxAwardDetails()) {
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

		DecimalFormat df = null;
		if (dbRfpEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		ErpSetup erpSetup = erpSetupService.findErpByWithTepmlateTenantId(loggedInUser.getTenantId());
		if (erpSetup == null || (erpSetup != null && Boolean.FALSE.equals(erpSetup.getIsErpEnable()))) {
			if (Boolean.TRUE == conclude) {
				rfpEvent.setConcludeRemarks(dbRfpEventAward.getAwardRemarks());
				rfpEvent.setConcludeBy(loggedInUser);
				rfpEvent.setConcludeDate(new Date());
				rfpEvent.setStatus(EventStatus.COMPLETE);
				rfpEvent.setAwarded(Boolean.TRUE);
			}
			rfpEventDao.update(rfpEvent);

			if (Boolean.TRUE == conclude) {
				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRfpEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfpEventAwardDao.getRfpEventAwardDetailsByEventIdandBqId(dbRfpEventAward.getId());

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
			// If Event Award is supposed to be pushed to ERP, check if its interface type is TYPE_2 (which means FGV -
			// i.e. with budget checking)
			// If this business unit is not enabled for budget checking then complete the process.
			if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2 && Boolean.FALSE == rfpEvent.getBusinessUnit().getBudgetCheck()) {
				// rfpEvent.setStatus(EventStatus.FINISHED);
				// rfpEvent.setAwarded(Boolean.TRUE);
				// rfpEventDao.update(rfpEvent);

				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRfpEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfpEventAwardDao.getRfpEventAwardDetailsByEventIdandBqId(dbRfpEventAward.getId());

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

		return dbRfpEventAward;
	}

	@Override
	public RfpEventAward rfpEventAwardByEventIdandBqId(String eventId, String bqId) {
		RfpEventAward eventAward = rfpEventAwardDao.rfpEventAwardByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfpEventAwardDetails details : eventAward.getRfxAwardDetails()) {
					if (details.getSupplier() != null) {
						LOG.info(details.getSupplier().getId());

						details.getSupplier().getId();
					}
					if (details.getBqItem() != null) {
						details.getBqItem().getId();

						if (details.getBqItem().getUom() != null) {
							LOG.info("items.getBqItem().getUom()  : " + details.getBqItem().getUom().getUom());
							details.getBqItem().getUom().getUom();
						}

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
	public RfpSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId) {
		RfpSupplierBqItem rfpSupplierBqItem = rfpSupplierBqItemDao.getBqItemByRfpBqItemId(bqItemId, supplierId);
		if (rfpSupplierBqItem != null) {
			rfpSupplierBqItem.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, supplierId));
			if (CollectionUtil.isNotEmpty(rfpSupplierBqItem.getProductCategoryList())) {
				for (ProductCategory productCategory : rfpSupplierBqItem.getProductCategoryList()) {
					productCategory.setBuyer(null);
					productCategory.setCreatedBy(null);
					productCategory.setModifiedBy(null);
				}
			}
			if (rfpSupplierBqItem.getSupplier() != null) {
				rfpSupplierBqItem.getSupplier().getFullName();
				// supp.getFullName();
			}
			if (CollectionUtil.isNotEmpty(rfpSupplierBqItem.getChildren())) {
				rfpSupplierBqItem.setChildren(null);
			}
			if (rfpSupplierBqItem.getParent() != null) {
				rfpSupplierBqItem.setParent(null);
				// if()
			}
			if (rfpSupplierBqItem.getBqItem() != null) {
				RfpBqItem bqitem = rfpSupplierBqItem.getBqItem();
				bqitem.getItemName();
			}
			if (rfpSupplierBqItem.getUom() != null) {
				rfpSupplierBqItem.setUom(null);
			}

		}
		return rfpSupplierBqItem;
	}

	@Override
	public List<RfpEventAward> getRfpEventAwardsByEventId(String eventId) {
		List<RfpEventAward> awardList = rfpEventAwardDao.getRfpEventAwardsByEventId(eventId);
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfpEventAward rfpEventAward : awardList) {
				if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {
					for (RfpEventAwardDetails detail : rfpEventAward.getRfxAwardDetails()) {
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
	public void transferRfpAward(String eventId, String tenantId, HttpSession session, User loggedInUser, String rfpEventAwardId, Boolean transfer, RfxTypes eventType) throws Exception {

		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(tenantId);

		if (Boolean.FALSE == erpSetup.getIsErpEnable()) {
			LOG.warn(">>>>>>>>> ERP IS NOT ENABLED. Skipping transfer...  Event Id: " + eventId);
			return;
		}

		RfpEvent rfpEvent = rfpEventDao.findByEventId(eventId);

		// If award type integration is FGV
		if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2) {

			if (Boolean.FALSE == rfpEvent.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("Business unit not enabled for budget checking ... Event Id : " + eventId + ", Business Unit : " + rfpEvent.getBusinessUnit().getUnitName());
				throw new ApplicationException("Cannot send this event award details to ERP as Event Business unit not enabled for budget checking.");
			}

			RfpEventAward dbEventAward = rfpEventAwardDao.findById(rfpEventAwardId);

			if (dbEventAward == null) {
				LOG.error("Could not identify the award details to push to ERP. Please contact the application administrator. Award ID : " + rfpEventAwardId + ", Event Id : " + eventId);
				throw new ApplicationException("Could not identify the award details to push to ERP. Please contact the application administrator.");
			}

			AwardErp2Pojo awardPojo = new AwardErp2Pojo();

			if (StringUtils.checkString(rfpEvent.getPreviousRequestId()).length() == 0) {
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			}
			SourcingFormRequest rfs = sourcingFormRequestDao.findById(rfpEvent.getPreviousRequestId());

			if (rfs == null) {
				LOG.warn("RFS not found during award... Event Id : " + eventId + ", RFS ID : " + rfpEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			} else if (Boolean.FALSE == rfs.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("RFS Business unit not enabled for budget checking ... Event Id : " + eventId + ", RFS ID : " + rfpEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as RFS Business unit not enabled for budget checking.");
			}

			// pr.setErpDocNo(prResponse.getSapRefNo());
			// pr.setErpMessage(prResponse.getMessage());
			// pr.setErpPrTransferred(Boolean.TRUE);
			// pr.setErpStatus(prResponse.getStatus());
			awardPojo.setLoginEmail(loggedInUser.getLoginId());
			awardPojo.setSapRefNo(rfs.getErpDocNo());
			awardPojo.setSapDocType(SapDocType.RFP);
			awardPojo.setEventId(rfpEvent.getEventId());
			List<AwardDetailsErp2Pojo> items = new ArrayList<AwardDetailsErp2Pojo>();

			// Order the award details by BQ Level and Order sequence
			SortedMap<Integer, RfpEventAwardDetails> sm = new TreeMap<Integer, RfpEventAwardDetails>();
			for (RfpEventAwardDetails eventAward : dbEventAward.getRfxAwardDetails()) {
				if (eventAward.getBqItem().getOrder() > 0) {
					String str = String.valueOf(eventAward.getBqItem().getLevel() * 10000) + String.valueOf(eventAward.getBqItem().getOrder());
					sm.put(Integer.parseInt(str), eventAward);
				}
			}

			int sequence = 1;
			for (Map.Entry<Integer, RfpEventAwardDetails> m : sm.entrySet()) {
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
				responseMsg = restTemplate.postForObject(erpSetup.getErpUrl() + "/PH/PRUpdate/", request, String.class);
				dbEventAward.setTransferred(Boolean.TRUE);
				rfpEventAwardDao.save(dbEventAward);

				ERP_LOG.info("ERP Response : " + responseMsg);

				try {
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbEventAward, session, loggedInUser, null, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("FILE Size : " + snapshot.length);
						RfpEventAwardAudit audit = new RfpEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfpAwardAudit(audit);

						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfpEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
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

			List<RfpEventAward> awardList = getRfpEventAwardsByEventId(eventId);

			if (CollectionUtil.isEmpty(awardList)) {
				LOG.warn("Award List is Empty for eventId :" + eventId);
				return;
			}

			List<AwardErpPojo> awardPojoList = new ArrayList<>();
			for (RfpEventAward eventAward : awardList) {
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
					if (eventAward.getRfxEvent().getDeliveryAddress().getCountry() != null) {
						deliveryAddress.setCountry(eventAward.getRfxEvent().getDeliveryAddress().getCountry().getCountryCode());
					}
					award.setDeliveryAddress(deliveryAddress);
				}
				List<AwardDetailsErpPojo> awardDetailsList = new ArrayList<>();
				for (RfpEventAwardDetails eventAwardDetails : eventAward.getRfxAwardDetails()) {
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
					LOG.info("Awarded Price===============>" + eventAwardDetails.getAwardedPrice());
					LOG.info("Awarded Price===============>" + eventAwardDetails.getBqItem().getQuantity());

					awardDetails.setUnitPrice(eventAwardDetails.getAwardedPrice().divide(eventAwardDetails.getBqItem().getQuantity(), 3, RoundingMode.DOWN));
					LOG.info("setUnitPricee===============>" + awardDetails.getUnitPrice());

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

			if (StringUtils.checkString(rfpEventAwardId).length() > 0) {
				RfpEventAward dbRfpEventAward = rfpEventAwardDao.findById(rfpEventAwardId);
				try {
					JasperPrint eventAward = getAwardSnapShotPdf(dbRfpEventAward, session, loggedInUser, resultMap, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbRfpEventAward, session, loggedInUser, resultMap, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward);
						byte[] excelSnapshot = workbook.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfpEventAwardAudit audit = new RfpEventAwardAudit(dbRfpEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfpAwardAudit(audit);
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfpEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
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

		if (rfpEvent != null && rfpEvent.getAwardDate() == null) {
			rfpEvent.setAwardDate(new Date());
		}
		rfpEvent.setStatus(EventStatus.FINISHED);
		rfpEvent.setAwarded(Boolean.TRUE);
		rfpEventDao.update(rfpEvent);

		DecimalFormat df = null;
		if (rfpEvent.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (rfpEvent.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (rfpEvent.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (rfpEvent.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (rfpEvent.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (rfpEvent.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		try {
			TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, tenantId);
			String awardedSuppliersName = "";
			String awardedSuppliersValue = "";

			if (tatReport != null) {
				List<AwardReferenceNumberPojo> award = rfpEventAwardDao.getRfpEventAwardDetailsByEventIdandBqId(rfpEventAwardId);

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
	public JasperPrint getAwardSnapShotPdf(RfpEventAward dbRfpEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) {
		// For Financial Standard
		DecimalFormat df = null;
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		if (dbRfpEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		JasperPrint eventAward = null;
		List<RftEventAwardDetailsPojo> auditTrailSummary = new ArrayList<RftEventAwardDetailsPojo>();

		List<RfpEventAwardDetails> rfpEventAwardDetails = dbRfpEventAward.getRfxAwardDetails();

		if (CollectionUtil.isNotEmpty(rfpEventAwardDetails)) {
			for (RfpEventAwardDetails rfpEventAwardDetail : rfpEventAwardDetails) {
				RftEventAwardDetailsPojo rftEventAwardDetailsPojo = new RftEventAwardDetailsPojo();
				if (rfpEventAwardDetail.getBqItem() != null) {
					String itemSeq = rfpEventAwardDetail.getBqItem().getLevel() + "." + rfpEventAwardDetail.getBqItem().getOrder();
					rftEventAwardDetailsPojo.setItemSeq(itemSeq);

					// rfpEventAwardDetail.getEventAward().getBq().getRfxEvent().getEventId();
					// rfpEventAwardDetail.getEventAward().getRfxEvent().getEventName();

					rftEventAwardDetailsPojo.setItemName(StringUtils.checkString(rfpEventAwardDetail.getBqItem().getItemName()));
					if (rfpEventAwardDetail.getSupplier() != null && rfpEventAwardDetail.getSupplier().getCompanyName() != null) {
						rftEventAwardDetailsPojo.setSupplierName(rfpEventAwardDetail.getSupplier().getCompanyName());
					} else {
						rftEventAwardDetailsPojo.setSupplierName("");
					}
					rftEventAwardDetailsPojo.setSupplierPrice(rfpEventAwardDetail.getOriginalPrice() != null ? df.format(rfpEventAwardDetail.getOriginalPrice()) : "");
					rftEventAwardDetailsPojo.setAwardedPrice(rfpEventAwardDetail.getAwardedPrice() != null ? df.format(rfpEventAwardDetail.getAwardedPrice()) : "");
					if ((rfpEventAwardDetail.getBqItem().getOrder() > 0)) {
						if (rfpEventAwardDetail.getTax() != null) {
							rftEventAwardDetailsPojo.setTax(df.format(rfpEventAwardDetail.getTax()));
						} else {
							rftEventAwardDetailsPojo.setTax("N/A");
						}
						if (awardResponsePojo != null) {
							if (CollectionUtil.isNotEmpty(awardResponsePojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardReferenceNumberPojo : awardResponsePojo.getRefNumList()) {
									if (rfpEventAwardDetail.getBqItem().getLevel() == awardReferenceNumberPojo.getLevel() && rfpEventAwardDetail.getBqItem().getOrder() == awardReferenceNumberPojo.getOrder()) {
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
					rftEventAwardDetailsPojo.setTaxType(rfpEventAwardDetail.getTaxType() != null ? rfpEventAwardDetail.getTaxType().toString() : "");
					rftEventAwardDetailsPojo.setTotalPrice(rfpEventAwardDetail.getTotalPrice() != null ? df.format(rfpEventAwardDetail.getTotalPrice()) : "");

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
			parameters.put("remark", dbRfpEventAward.getAwardRemarks());
			parameters.put("eventNo", dbRfpEventAward.getRfxEvent().getEventId());
			parameters.put("eventName", dbRfpEventAward.getRfxEvent().getEventName());
			parameters.put("bqName", dbRfpEventAward.getBq().getName());
			parameters.put("actionBy", loggedInUser.getName());
			if (Boolean.TRUE == transfer) {
				parameters.put("actionType", "Save & Transfer");
			} else if (Boolean.TRUE == conclude) {
				parameters.put("actionType", "Conclude Award");
			} else {
				parameters.put("actionType", "Save");
			}
			parameters.put("totalSupplierPrice", String.valueOf(df.format(dbRfpEventAward.getTotalSupplierPrice())));
			parameters.put("totalAwardPrice", String.valueOf(df.format(dbRfpEventAward.getTotalAwardPrice())));
			parameters.put("GrandPrice", String.valueOf(df.format(dbRfpEventAward.getGrandTotalPrice())));

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auditTrailSummary, false);
			eventAward = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Could not generate Audit Trail PDF Report. " + e.getMessage(), e);
		}

		return eventAward;
	}

	@Override
	public ByteArrayOutputStream getAwardSnapShotXcel(RfpEventAward dbRfpEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DecimalFormat df = null;
		if (dbRfpEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfpEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		Workbook workbook = new XSSFWorkbook();
		try {
			List<RfpEventAwardDetails> rftEventAwardDetails = dbRfpEventAward.getRfxAwardDetails();
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
			cell.setCellValue(dbRfpEventAward.getRfxEvent().getEventId());

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
			cell.setCellValue(dbRfpEventAward.getRfxEvent().getEventName());

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
			cell.setCellValue(dbRfpEventAward.getBq().getName());

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
			for (RfpEventAwardDetails rftEventAwardDetail : rftEventAwardDetails) {
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
			cell.setCellValue(df.format(dbRfpEventAward.getGrandTotalPrice()));
			cell.setCellStyle(styleHeading);
			try {
				cell = row.createCell(cellNumber - 5);
				cell.setCellValue(String.valueOf(df.format(dbRfpEventAward.getTotalAwardPrice())));
				cell.setCellStyle(styleHeading);
				cell = row.createCell(cellNumber - 6);
				cell.setCellValue(String.valueOf(df.format(dbRfpEventAward.getTotalAwardPrice())));
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
			cellRemarks.setCellValue(dbRfpEventAward.getAwardRemarks());
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
	public void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws Exception {
		RfpEventAwardAudit audit = eventAwardAuditService.findByRfpAuditId(id);
		response.setContentLength(audit.getSnapshot().length);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".pdf\"");

		FileCopyUtils.copy(audit.getSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws Exception {
		RfpEventAwardAudit audit = eventAwardAuditService.findByRfpAuditId(id);
		response.setContentLength(audit.getExcelSnapshot().length);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".xls\"");

		FileCopyUtils.copy(audit.getExcelSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFileSnapShot(String id, HttpServletResponse response) throws IOException {
		RfpEventAwardAudit audit = eventAwardAuditService.findByRfpAuditId(id);
		response.setContentLength(audit.getFileData().length);
		response.setContentType(audit.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + audit.getFileName());
		FileCopyUtils.copy(audit.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFile(String id, HttpServletResponse response) throws IOException {
		RfpEventAward rfpEventAward = rfpEventAwardDao.findById(id);
		LOG.info("File Name:" + rfpEventAward.getFileName());
		response.setContentLength(rfpEventAward.getFileData().length);
		response.setContentType(rfpEventAward.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + rfpEventAward.getFileName());
		FileCopyUtils.copy(rfpEventAward.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public RfpEventAward rfpEventAwardDetailsByEventIdandBqId(String eventId, String bqId) {
		RfpEventAward eventAward = rfpEventAwardDao.rfpEventAwardDetailsByEventIdandBqId(eventId, bqId);
		List<User> awardApproverUser = new ArrayList<User>();
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfpEventAwardDetails details : eventAward.getRfxAwardDetails()) {
					if (details.getSupplier() != null) {
						details.getSupplier().getId();
					}
					if (details.getBqItem() != null) {
						details.getBqItem().getId();

						if (details.getBqItem().getUom() != null) {
							details.getBqItem().getUom().getUom();
						}

					}
					details.getAwardedPrice();
					details.getOriginalPrice();
					details.getTaxType();
					details.getTax();
				}

			}

			if (eventAward.getAwardApprovals() != null) {
				for (RfpEventAwardApproval awardApp : eventAward.getAwardApprovals()) {
					for (RfpAwardApprovalUser awardAppU : awardApp.getApprovalUsers()) {
						if (!awardApproverUser.contains(awardAppU.getUser())) {
							awardApproverUser.add(awardAppU.getUser());
						}
					}
				}
			}
		}
		return eventAward;
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean updateEventAwardApproval(RfpEventAward eventAward, User user, String eventId) {

		RfpEvent rfpEvent = rfpEventDao.findByEventId(eventId);

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfpEventAwardApproval approvalRequest : rfpEvent.getAwardApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfpAwardApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(eventAward.getAwardApprovals())) {
			int level = 1;
			for (RfpEventAwardApproval app : eventAward.getAwardApprovals()) {
				app.setEvent(rfpEvent);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpAwardApprovalUser approvalUser : app.getApprovalUsers()) {
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

		rfpEvent.setAwardApprovals(eventAward.getAwardApprovals());
		rfpEvent.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfpEvent.setModifiedDate(new Date());
		if (eventAward.getRfxEvent() != null) {
			rfpEvent.setEnableAwardApproval(eventAward.getRfxEvent().getEnableAwardApproval());
		}
		rfpEvent = rfpEventDao.saveOrUpdate(rfpEvent);
		LOG.info(" ****************** In Service " + rfpEvent.getAwardApprovals().size());

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				try {
					RfpEventAudit audit = new RfpEventAudit(rfpEvent, user, new Date(), AuditActionType.Update, auditMessage);
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Award Approval Route is updated for Event '" + rfpEvent.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		return auditMessages.size() > 0 ? Boolean.TRUE : Boolean.FALSE;
	}

}
