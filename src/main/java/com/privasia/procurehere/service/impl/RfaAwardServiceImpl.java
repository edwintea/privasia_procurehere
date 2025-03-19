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
import com.privasia.procurehere.core.dao.RfaEventAwardDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.dao.RfaEventAwardAuditDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.RfaAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventAward;
import com.privasia.procurehere.core.entity.RfaEventAwardApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaEventAwardDetails;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
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
import com.privasia.procurehere.service.RfaAwardService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.TatReportService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class RfaAwardServiceImpl implements RfaAwardService {

	private static final Logger LOG = LogManager.getLogger(RfaAwardServiceImpl.class);
	private static final Logger ERP_LOG = LogManager.getLogger(Global.ERP_LOG);

	@Autowired
	RfaEventAwardDao rfaEventAwardDao;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaEventDao rfaEventDao;

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
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfaEventAwardAuditDao rfaEventAwardAuditDao;

	@Override
	@Transactional(readOnly = false)
	public RfaEventAward saveEventAward(RfaEventAward rfaEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude) throws IOException {
		rfaEventAward.setCreatedBy(SecurityLibrary.getLoggedInUser());
		rfaEventAward.setCreatedDate(new Date());
		rfaEventAward.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfaEventAward.setModifiedDate(new Date());
		List<RfaEventAwardDetails> awardList = rfaEventAward.getRfxAwardDetails();
		// to avoid duplicate entry of awarded supplier
		Set<String> awardedSupplierIds = new HashSet<String>();
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfaEventAwardDetails rfaEventAwardDetails : awardList) {
				Supplier supplier = rfaEventAwardDetails.getSupplier();
				if ((supplier != null) && (supplier.getId() != null)) {
					awardedSupplierIds.add(supplier.getId());
				}
			}
		}

		RfaEvent rfaEvent = rfaEventDao.findByEventId(rfaEventAward.getRfxEvent().getId());

		if (CollectionUtil.isNotEmpty(awardedSupplierIds)) {
			if (rfaEvent != null) {
				rfaEvent = rfaEventDao.findById(rfaEvent.getId());
				List<Supplier> awardedSuppliers = null;
				if (CollectionUtil.isNotEmpty(rfaEvent.getAwardedSuppliers())) {
					awardedSuppliers = rfaEvent.getAwardedSuppliers();
				} else {
					awardedSuppliers = new ArrayList<Supplier>();
				}
				for (String suppId : awardedSupplierIds) {
					Supplier supplier = new Supplier();
					supplier.setId(suppId);
					awardedSuppliers.add(supplier);
				}
				rfaEvent.setAwardedSuppliers(awardedSuppliers);
				rfaEventDao.saveOrUpdate(rfaEvent);
			}
		}

		Set<String> supplierList = new HashSet<String>();

		if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {
			for (RfaEventAwardDetails awardDetails : rfaEventAward.getRfxAwardDetails()) {
				awardDetails.setEventAward(rfaEventAward);
				if (awardDetails.getSupplier() != null) {
					supplierList.add(awardDetails.getSupplier().getId());
				}
			}
		}

		RfaEventAward rfaEventAward2 = rfaEventAwardDao.findById(rfaEventAward.getId());
		if(rfaEventAward2 != null && rfaEventAward2.getFileData() != null) {
			rfaEventAward.setFileData(rfaEventAward2.getFileData());
			rfaEventAward.setFileName(rfaEventAward2.getFileName());
			rfaEventAward.setCredContentType(rfaEventAward2.getCredContentType());
		}

		RfaEventAward dbRfaEventAward = rfaEventAwardDao.saveOrUpdateWithFlush(rfaEventAward);
		if (dbRfaEventAward.getRfxEvent() != null) {
			Double sumOfAwardedPriceForEvent = rfaEventAwardDao.getSumOfAwardedPrice(rfaEventAward.getRfxEvent().getId());
			BigDecimal sumAwardPrice = null;
			if (sumOfAwardedPriceForEvent != null) {
				sumAwardPrice = new BigDecimal(sumOfAwardedPriceForEvent).setScale(4, BigDecimal.ROUND_HALF_EVEN);
			}
			try {
				rfaEventDao.updateRfaForAwardPrice(rfaEventAward.getRfxEvent().getId(), sumAwardPrice);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(rfaEventAward.getAwardApprovals())) {
			int level = 1;
			for (RfaEventAwardApproval app : rfaEventAward.getAwardApprovals()) {
				app.setEvent(rfaEvent);
				app.setLevel(level++);
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
					for (RfaAwardApprovalUser approvalUser : app.getApprovalUsers()) {
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
		RfaEvent event = rfaEventDao.findById(dbRfaEventAward.getRfxEvent().getId());
		event.setAwardedSuppliers(awardedSuppliers);
		// Only Save is called. Now set the Award status as DRAFT or our internal consumption.
		// DRAFT status is not shown to the user.
		if (!transfer && !conclude) {
			event.setAwardStatus(AwardStatus.DRAFT);
		}
		if (conclude) {
			event.setAwardStatus(AwardStatus.PENDING);
		}
		if (rfaEventAward.getRfxEvent() != null) {
			event.setEnableAwardApproval(rfaEventAward.getRfxEvent().getEnableAwardApproval());
		}
		event.setAwardApprovals(rfaEventAward.getAwardApprovals());
		event = rfaEventDao.update(event);

		// Jasper report method calling here-------------------------------------------------------------------------
		if (transfer == false && conclude == false) {
			if (StringUtils.checkString(dbRfaEventAward.getId()).length() > 0) {
				// RfaEventAward dbRfaEventAward = rfaEventAwardDao.findById(rfaEventAward.getId());

				try {
					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward = getAwardSnapShotPdf(dbRfaEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					ByteArrayOutputStream baos1 = getAwardSnapShotXcel(dbRfaEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					if (baos1 != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward);
						byte[] excelSnapshot = baos1.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfaEventAwardAudit audit = null;
						if (Boolean.FALSE == conclude) {
							audit = new RfaEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Saved", snapshot, excelSnapshot);
							if (rfaEventAward.getAttachment() != null) {
								RfaEventAward rfaEventAward1 = rfaEventAwardDao.findById(dbRfaEventAward.getId());
								try {
									rfaEventAward1.setFileData(rfaEventAward.getAttachment().getBytes());
									audit.setFileData(rfaEventAward.getAttachment().getBytes());
								} catch (IOException e) {
									LOG.error("Eroor While getting file data" + e.getMessage(), e);
								}
								rfaEventAward1.setFileName(rfaEventAward.getAttachment().getOriginalFilename());
								rfaEventAward1.setCredContentType(rfaEventAward.getAttachment().getContentType());
								audit.setFileName(rfaEventAward.getAttachment().getOriginalFilename());
								audit.setCredContentType(rfaEventAward.getAttachment().getContentType());
								rfaEventAwardDao.saveOrUpdate(rfaEventAward1);
							}
							eventAwardAuditService.saveRfaAwardAudit(audit);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
			}
		} else if(conclude) {
			if (rfaEventAward.getAttachment() != null) {
				LOG.info("File Name:-------->" + rfaEventAward.getAttachment().getOriginalFilename());
				RfaEventAward rfaEventAward1 = rfaEventAwardDao.findById(dbRfaEventAward.getId());
				try {
					rfaEventAward1.setFileData(rfaEventAward.getAttachment().getBytes());								} catch (IOException e) {
					LOG.error("Eroor While getting file data" + e.getMessage(), e);
				}
				rfaEventAward1.setFileName(rfaEventAward.getAttachment().getOriginalFilename());
				rfaEventAward1.setCredContentType(rfaEventAward.getAttachment().getContentType());
				rfaEventAwardDao.saveOrUpdate(rfaEventAward1);
			}
		}
		// Jasper end-------------------------------------------------------------------------

		if (dbRfaEventAward != null) {

			if (dbRfaEventAward.getRfxEvent() != null) {
				dbRfaEventAward.getRfxEvent().getId();
				dbRfaEventAward.getRfxEvent().getEventName();
			}
		}
		if (CollectionUtil.isNotEmpty(dbRfaEventAward.getRfxAwardDetails())) {
			for (RfaEventAwardDetails details : dbRfaEventAward.getRfxAwardDetails()) {
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
		if (dbRfaEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		ErpSetup erpSetup = erpSetupService.findErpByWithTepmlateTenantId(loggedInUser.getTenantId());
		if (erpSetup == null || (erpSetup != null && Boolean.FALSE.equals(erpSetup.getIsErpEnable()))) {
			if (Boolean.TRUE == conclude) {
				rfaEvent.setConcludeRemarks(dbRfaEventAward.getAwardRemarks());
				rfaEvent.setConcludeBy(loggedInUser);
				rfaEvent.setConcludeDate(new Date());
				rfaEvent.setStatus(EventStatus.COMPLETE);
				rfaEvent.setAwarded(Boolean.TRUE);
			}
			rfaEventDao.update(rfaEvent);
			if (Boolean.TRUE == conclude) {
				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRfaEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfaEventAwardDao.getRfaEventAwardDetailsByEventIdandBqId(dbRfaEventAward.getId());

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
			if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2 && Boolean.FALSE == rfaEvent.getBusinessUnit().getBudgetCheck()) {
				// rfaEvent.setStatus(EventStatus.FINISHED);
				// rfaEvent.setAwarded(Boolean.TRUE);
				// rfaEventDao.update(rfaEvent);

				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(rfaEvent.getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rfaEventAwardDao.getRfaEventAwardDetailsByEventIdandBqId(dbRfaEventAward.getId());

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
		return dbRfaEventAward;
	}

	@Override
	public RfaEventAward rfaEventAwardByEventIdandBqId(String eventId, String bqId) {
		RfaEventAward eventAward = rfaEventAwardDao.rfaEventAwardByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfaEventAwardDetails details : eventAward.getRfxAwardDetails()) {
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
	public RfaSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId) {
		RfaSupplierBqItem rfaSupplierBqItem = rfaSupplierBqItemDao.getBqItemByRfaBqItemId(bqItemId, supplierId);
		if (rfaSupplierBqItem != null) {
			rfaSupplierBqItem.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, supplierId));
			if (CollectionUtil.isNotEmpty(rfaSupplierBqItem.getProductCategoryList())) {
				for (ProductCategory productCategory : rfaSupplierBqItem.getProductCategoryList()) {
					productCategory.setBuyer(null);
					productCategory.setCreatedBy(null);
					productCategory.setModifiedBy(null);
				}
			}
			if (rfaSupplierBqItem.getSupplier() != null) {
				rfaSupplierBqItem.getSupplier().getFullName();
				// supp.getFullName();
			}
			if (CollectionUtil.isNotEmpty(rfaSupplierBqItem.getChildren())) {
				rfaSupplierBqItem.setChildren(null);
			}
			if (rfaSupplierBqItem.getParent() != null) {
				rfaSupplierBqItem.setParent(null);
				// if()
			}
			if (rfaSupplierBqItem.getBqItem() != null) {
				RfaBqItem bqitem = rfaSupplierBqItem.getBqItem();
				bqitem.getItemName();
			}
			if (rfaSupplierBqItem.getUom() != null) {
				rfaSupplierBqItem.setUom(null);
			}

		}
		return rfaSupplierBqItem;
	}

	@Override
	public List<RfaEventAward> getRfaEventAwardsByEventId(String eventId) {
		List<RfaEventAward> awardList = rfaEventAwardDao.getRfaEventAwardsByEventId(eventId);
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RfaEventAward rfaEventAward : awardList) {
				if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {
					for (RfaEventAwardDetails detail : rfaEventAward.getRfxAwardDetails()) {
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
	public void transferRfaAward(String eventId, String tenantId, String rfaEventAwardId, HttpSession session, User loggedInUser, Boolean transfer, RfxTypes eventType) throws Exception {

		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(tenantId);

		if (Boolean.FALSE == erpSetup.getIsErpEnable()) {
			LOG.warn(">>>>>>>>> ERP IS NOT ENABLED. Skipping transfer...  Event Id: " + eventId);
			return;
		}

		RfaEvent rfaEvent = rfaEventDao.findByEventId(eventId);

		// If award type integration is FGV
		if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2) {

			if (Boolean.FALSE == rfaEvent.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("Business unit not enabled for budget checking ... Event Id : " + eventId + ", Business Unit : " + rfaEvent.getBusinessUnit().getUnitName());
				throw new ApplicationException("Cannot send this event award details to ERP as Event Business unit not enabled for budget checking.");
			}

			RfaEventAward dbEventAward = rfaEventAwardDao.findById(rfaEventAwardId);

			if (dbEventAward == null) {
				LOG.error("Could not identify the award details to push to ERP. Please contact the application administrator. Award ID : " + rfaEventAwardId + ", Event Id : " + eventId);
				throw new ApplicationException("Could not identify the award details to push to ERP. Please contact the application administrator.");
			}

			AwardErp2Pojo awardPojo = new AwardErp2Pojo();

			if (StringUtils.checkString(rfaEvent.getPreviousRequestId()).length() == 0) {
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			}
			SourcingFormRequest rfs = sourcingFormRequestDao.findById(rfaEvent.getPreviousRequestId());

			if (rfs == null) {
				LOG.warn("RFS not found during award... Event Id : " + eventId + ", RFS ID : " + rfaEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			} else if (Boolean.FALSE == rfs.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("RFS Business unit not enabled for budget checking ... Event Id : " + eventId + ", RFS ID : " + rfaEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as RFS Business unit not enabled for budget checking.");
			}

			// pr.setErpDocNo(prResponse.getSapRefNo());
			// pr.setErpMessage(prResponse.getMessage());
			// pr.setErpPrTransferred(Boolean.TRUE);
			// pr.setErpStatus(prResponse.getStatus());
			awardPojo.setLoginEmail(loggedInUser.getLoginId());
			awardPojo.setSapRefNo(rfs.getErpDocNo());
			awardPojo.setSapDocType(SapDocType.RFA);
			awardPojo.setEventId(rfaEvent.getEventId());
			List<AwardDetailsErp2Pojo> items = new ArrayList<AwardDetailsErp2Pojo>();

			// Order the award details by BQ Level and Order sequence
			SortedMap<Integer, RfaEventAwardDetails> sm = new TreeMap<Integer, RfaEventAwardDetails>();
			for (RfaEventAwardDetails eventAward : dbEventAward.getRfxAwardDetails()) {
				if (eventAward.getBqItem().getOrder() > 0) {
					String str = String.valueOf(eventAward.getBqItem().getLevel() * 10000) + String.valueOf(eventAward.getBqItem().getOrder());
					sm.put(Integer.parseInt(str), eventAward);
				}
			}

			int sequence = 1;
			for (Map.Entry<Integer, RfaEventAwardDetails> m : sm.entrySet()) {
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
				rfaEventAwardDao.save(dbEventAward);

				ERP_LOG.info("ERP Response : " + responseMsg);

				try {
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapShotXcel(dbEventAward, session, loggedInUser, null, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("FILE Size : " + snapshot.length);
						RfaEventAwardAudit audit = new RfaEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfaAwardAudit(audit);

						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfaEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
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

			List<RfaEventAward> awardList = getRfaEventAwardsByEventId(eventId);

			if (CollectionUtil.isEmpty(awardList)) {
				LOG.warn("Award List is Empty for eventId :" + eventId);
				return;
			}

			List<AwardErpPojo> awardPojoList = new ArrayList<>();
			for (RfaEventAward eventAward : awardList) {
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
				for (RfaEventAwardDetails eventAwardDetails : eventAward.getRfxAwardDetails()) {
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
						} else {
							throw new ApplicationException("Vendor Code not Assigned to " + supplier.getSupplier().getCompanyName());
						}
					}
					awardDetails.setLnno(eventAwardDetails.getBqItem().getParent().getField8());
					awardDetails.setId(eventAwardDetails.getId());
					awardDetails.setLevel(eventAwardDetails.getBqItem().getLevel());
					awardDetails.setOrder(eventAwardDetails.getBqItem().getOrder());
					awardDetails.setItemName(eventAwardDetails.getBqItem().getItemName());
					awardDetails.setItemDesc(eventAwardDetails.getBqItem().getItemDescription());
					awardDetails.setQuantity(eventAwardDetails.getBqItem().getQuantity());
					// awardDetails.setUnitPrice(eventAwardDetails.getAwardedPrice());
					awardDetails.setUnitPrice(eventAwardDetails.getAwardedPrice().divide(eventAwardDetails.getBqItem().getQuantity(), 3, RoundingMode.DOWN));
					awardDetails.setTotalAmount(eventAwardDetails.getTotalPrice());

					// awardDetails.setUnitPrice(eventAwardDetails.getAwardedPrice());
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

			if (StringUtils.checkString(rfaEventAwardId).length() > 0) {
				RfaEventAward dbRfpEventAward = rfaEventAwardDao.findById(rfaEventAwardId);
				try {
					JasperPrint eventAward = getAwardSnapShotPdf(dbRfpEventAward, session, loggedInUser, resultMap, transfer, false);
					ByteArrayOutputStream baos1 = getAwardSnapShotXcel(dbRfpEventAward, session, loggedInUser, resultMap, transfer, false);
					if (baos1 != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward);
						byte[] excelSnapshot = baos1.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RfaEventAwardAudit audit = new RfaEventAwardAudit(dbRfpEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRfaAwardAudit(audit);

						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rfaEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
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
		if (rfaEvent != null && rfaEvent.getAwardDate() == null) {
			rfaEvent.setAwardDate(new Date());
		}
		rfaEvent.setStatus(EventStatus.FINISHED);
		rfaEvent.setAwarded(Boolean.TRUE);
		rfaEventDao.update(rfaEvent);

		DecimalFormat df = null;
		if (rfaEvent.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (rfaEvent.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (rfaEvent.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (rfaEvent.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (rfaEvent.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (rfaEvent.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		try {
			TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, tenantId);
			String awardedSuppliersName = "";
			String awardedSuppliersValue = "";

			if (tatReport != null) {
				List<AwardReferenceNumberPojo> award = rfaEventAwardDao.getRfaEventAwardDetailsByEventIdandBqId(rfaEventAwardId);

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
	public JasperPrint getAwardSnapShotPdf(RfaEventAward dbRfaEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) {

		// For Financial Standard
		DecimalFormat df = null;
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		if (dbRfaEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}
		JasperPrint eventAward = null;
		List<RftEventAwardDetailsPojo> auditTrailSummary = new ArrayList<RftEventAwardDetailsPojo>();

		List<RfaEventAwardDetails> rfaEventAwardDetails = dbRfaEventAward.getRfxAwardDetails();

		if (CollectionUtil.isNotEmpty(rfaEventAwardDetails)) {
			for (RfaEventAwardDetails rfaEventAwardDetail : rfaEventAwardDetails) {
				RftEventAwardDetailsPojo rftEventAwardDetailsPojo = new RftEventAwardDetailsPojo();
				if (rfaEventAwardDetail.getBqItem() != null) {
					String itemSeq = rfaEventAwardDetail.getBqItem().getLevel() + "." + rfaEventAwardDetail.getBqItem().getOrder();
					rftEventAwardDetailsPojo.setItemSeq(itemSeq);

					// rfaEventAwardDetail.getEventAward().getBq().getRfxEvent().getEventId();
					// rfaEventAwardDetail.getEventAward().getRfxEvent().getEventName();

					rftEventAwardDetailsPojo.setItemName(StringUtils.checkString(rfaEventAwardDetail.getBqItem().getItemName()));
					if (rfaEventAwardDetail.getSupplier() != null && rfaEventAwardDetail.getSupplier().getCompanyName() != null) {
						rftEventAwardDetailsPojo.setSupplierName(rfaEventAwardDetail.getSupplier().getCompanyName());
					} else {
						rftEventAwardDetailsPojo.setSupplierName("");
					}
					rftEventAwardDetailsPojo.setSupplierPrice(rfaEventAwardDetail.getOriginalPrice() != null ? df.format(rfaEventAwardDetail.getOriginalPrice()) : "");
					rftEventAwardDetailsPojo.setAwardedPrice(rfaEventAwardDetail.getAwardedPrice() != null ? df.format(rfaEventAwardDetail.getAwardedPrice()) : "");
					if ((rfaEventAwardDetail.getBqItem().getOrder() > 0)) {
						if (rfaEventAwardDetail.getTax() != null) {
							rftEventAwardDetailsPojo.setTax(df.format(rfaEventAwardDetail.getTax()));
						} else {
							rftEventAwardDetailsPojo.setTax("N/A");
						}
						if (awardResponsePojo != null) {
							if (CollectionUtil.isNotEmpty(awardResponsePojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardReferenceNumberPojo : awardResponsePojo.getRefNumList()) {
									if (rfaEventAwardDetail.getBqItem().getLevel() == awardReferenceNumberPojo.getLevel() && rfaEventAwardDetail.getBqItem().getOrder() == awardReferenceNumberPojo.getOrder()) {
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

					rftEventAwardDetailsPojo.setTaxType(rfaEventAwardDetail.getTaxType() != null ? rfaEventAwardDetail.getTaxType().toString() : "");
					rftEventAwardDetailsPojo.setTotalPrice(rfaEventAwardDetail.getTotalPrice() != null ? df.format(rfaEventAwardDetail.getTotalPrice()) : "");

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
			parameters.put("remark", dbRfaEventAward.getAwardRemarks());
			parameters.put("eventNo", dbRfaEventAward.getRfxEvent().getEventId());
			parameters.put("eventName", dbRfaEventAward.getRfxEvent().getEventName());
			parameters.put("bqName", dbRfaEventAward.getBq().getName());
			parameters.put("actionBy", loggedInUser.getName());
			if (Boolean.TRUE == transfer) {
				parameters.put("actionType", "Save & Transfer");
			} else if (Boolean.TRUE == conclude) {
				parameters.put("actionType", "Conclude Award");
			} else {
				parameters.put("actionType", "Save");
			}
			parameters.put("totalSupplierPrice", String.valueOf(df.format(dbRfaEventAward.getTotalSupplierPrice())));
			parameters.put("totalAwardPrice", String.valueOf(df.format(dbRfaEventAward.getTotalAwardPrice())));
			parameters.put("GrandPrice", String.valueOf(df.format(dbRfaEventAward.getGrandTotalPrice())));

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auditTrailSummary, false);
			eventAward = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Could not generate Audit Trail PDF Report. " + e.getMessage(), e);
		}

		return eventAward;
	}

	@Override
	public ByteArrayOutputStream getAwardSnapShotXcel(RfaEventAward dbRfaEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DecimalFormat df = null;
		if (dbRfaEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRfaEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}
		Workbook workbook = new XSSFWorkbook();
		try {
			List<RfaEventAwardDetails> rftEventAwardDetails = dbRfaEventAward.getRfxAwardDetails();
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
			cell.setCellValue(dbRfaEventAward.getRfxEvent().getEventId());

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
			cell.setCellValue(dbRfaEventAward.getRfxEvent().getEventName());

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
			cell.setCellValue(dbRfaEventAward.getBq().getName());

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
			for (RfaEventAwardDetails rftEventAwardDetail : rftEventAwardDetails) {
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
			cell.setCellValue(df.format(dbRfaEventAward.getGrandTotalPrice()));
			cell.setCellStyle(styleHeading);
			try {
				cell = row.createCell(cellNumber - 5);
				cell.setCellValue(String.valueOf(df.format(dbRfaEventAward.getTotalAwardPrice())));
				cell.setCellStyle(styleHeading);
				cell = row.createCell(cellNumber - 6);
				cell.setCellValue(String.valueOf(df.format(dbRfaEventAward.getTotalAwardPrice())));
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
			cellRemarks.setCellValue(dbRfaEventAward.getAwardRemarks());
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
		RfaEventAwardAudit audit = eventAwardAuditService.findByRfaAuditId(id);
		response.setContentLength(audit.getSnapshot().length);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".pdf\"");

		FileCopyUtils.copy(audit.getSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws IOException {
		RfaEventAwardAudit audit = eventAwardAuditService.findByRfaAuditId(id);
		response.setContentLength(audit.getExcelSnapshot().length);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".xls\"");

		FileCopyUtils.copy(audit.getExcelSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadAwardAttachFileSnapShot(String id, HttpServletResponse response) throws IOException {
		RfaEventAwardAudit audit = eventAwardAuditService.findByRfaAuditId(id);
		response.setContentLength(audit.getFileData().length);
		response.setContentType(audit.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + audit.getFileName());
		FileCopyUtils.copy(audit.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadAwardAttachFile(String id, HttpServletResponse response) throws IOException {
		RfaEventAward rfaEventAward = rfaEventAwardDao.findById(id);
		LOG.info("File Name:" + rfaEventAward.getFileName());
		response.setContentLength(rfaEventAward.getFileData().length);
		response.setContentType(rfaEventAward.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + rfaEventAward.getFileName());
		FileCopyUtils.copy(rfaEventAward.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public RfaEventAward rfaEventAwardDetailsByEventIdandBqId(String eventId, String bqId) {

		RfaEventAward eventAward = rfaEventAwardDao.rfaEventAwardDetailsByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RfaEventAwardDetails details : eventAward.getRfxAwardDetails()) {
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
	public Boolean updateEventAwardApproval(RfaEventAward eventAward, User user, String eventId) {
		LOG.info(" Rfa Event Award " + eventAward.getAwardApprovals());

		RfaEvent rfaEvent = rfaEventDao.findByEventId(eventId);

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfaEventAwardApproval approvalRequest : rfaEvent.getAwardApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfaAwardApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(eventAward.getAwardApprovals())) {
			int level = 1;
			for (RfaEventAwardApproval app : eventAward.getAwardApprovals()) {
				app.setEvent(rfaEvent);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaAwardApprovalUser approvalUser : app.getApprovalUsers()) {
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

		rfaEvent.setAwardApprovals(eventAward.getAwardApprovals());
		rfaEvent.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rfaEvent.setModifiedDate(new Date());
		if (eventAward.getRfxEvent() != null) {
			rfaEvent.setEnableAwardApproval(eventAward.getRfxEvent().getEnableAwardApproval());
		}
		rfaEvent = rfaEventDao.saveOrUpdate(rfaEvent);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				try {
					RfaEventAudit audit = new RfaEventAudit(rfaEvent, user, new Date(), AuditActionType.Update, auditMessage);
					eventAuditService.save(audit);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Award Approval Route is updated for Event '" + rfaEvent.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFA);
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
