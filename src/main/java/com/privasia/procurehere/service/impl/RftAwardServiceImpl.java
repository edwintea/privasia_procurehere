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
import com.privasia.procurehere.core.dao.RftEventAwardDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventAwardAuditDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.RftAwardApprovalUser;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventAward;
import com.privasia.procurehere.core.entity.RftEventAwardApproval;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.entity.RftEventAwardDetails;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
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
import com.privasia.procurehere.service.RftAwardService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.TatReportService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class RftAwardServiceImpl implements RftAwardService {

	private static final Logger LOG = LogManager.getLogger(RftAwardServiceImpl.class);
	private static final Logger ERP_LOG = LogManager.getLogger(Global.ERP_LOG);

	@Autowired
	RftEventAwardDao rftEventAwardDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	StateService stateService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	EventAwardAuditService eventAwardAuditService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

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
	RftEventAwardAuditDao rftEventAwardAuditDao;

	@Override
	@Transactional(readOnly = false)
	public RftEventAward saveEventAward(RftEventAward rftEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude) {
		rftEventAward.setCreatedBy(loggedInUser);
		rftEventAward.setCreatedDate(new Date());
		rftEventAward.setModifiedBy(loggedInUser);
		rftEventAward.setModifiedDate(new Date());

		List<RftEventAwardDetails> awardList = rftEventAward.getRfxAwardDetails();
		Set<String> awardedSupplierIds = new HashSet<String>();
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RftEventAwardDetails rftEventAwardDetails : awardList) {
				Supplier supplier = rftEventAwardDetails.getSupplier();
				if ((supplier != null) && (supplier.getId() != null)) {
					awardedSupplierIds.add(supplier.getId());
				}
			}
		}
		RftEvent rftEvent = rftEventDao.findById(rftEventAward.getRfxEvent().getId());

		if (CollectionUtil.isNotEmpty(awardedSupplierIds)) {
			if (rftEvent != null) {
				rftEvent = rftEventDao.findById(rftEvent.getId());
				List<Supplier> awardedSuppliers = null;
				if (CollectionUtil.isNotEmpty(rftEvent.getAwardedSuppliers())) {
					awardedSuppliers = rftEvent.getAwardedSuppliers();
				} else {
					awardedSuppliers = new ArrayList<Supplier>();
				}
				for (String suppId : awardedSupplierIds) {
					Supplier supplier = new Supplier();
					supplier.setId(suppId);
					awardedSuppliers.add(supplier);
				}
				rftEvent.setAwardedSuppliers(awardedSuppliers);
				rftEventDao.saveOrUpdate(rftEvent);
			}
		}

		Set<String> supplierList = new HashSet<String>();

		if (CollectionUtil.isNotEmpty(rftEventAward.getRfxAwardDetails())) {
			for (RftEventAwardDetails awardDetails : rftEventAward.getRfxAwardDetails()) {
				awardDetails.setEventAward(rftEventAward);
				if (awardDetails.getSupplier() != null) {
					supplierList.add(awardDetails.getSupplier().getId());
				}
			}
		}

		RftEventAward rftEventAward2 = rftEventAwardDao.findById(rftEventAward.getId());
		if(rftEventAward2 != null && rftEventAward2.getFileData() != null) {
			rftEventAward.setFileData(rftEventAward2.getFileData());
			rftEventAward.setFileName(rftEventAward2.getFileName());
			rftEventAward.setCredContentType(rftEventAward2.getCredContentType());
		}

		RftEventAward dbRftEventAward = rftEventAwardDao.saveOrUpdateWithFlush(rftEventAward);

		if (dbRftEventAward.getRfxEvent() != null) {
			Double sumOfAwardedPriceForEvent = rftEventAwardDao.getSumOfAwardedPrice(dbRftEventAward.getRfxEvent().getId());
			BigDecimal sumAwardPrice = null;
			if (sumOfAwardedPriceForEvent != null) {
				sumAwardPrice = new BigDecimal(sumOfAwardedPriceForEvent);
			}
			try {
				rftEventDao.updateRfaForAwardPrice(dbRftEventAward.getRfxEvent().getId(), sumAwardPrice);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		if (CollectionUtil.isNotEmpty(rftEventAward.getAwardApprovals())) {
			int level = 1;
			for (RftEventAwardApproval app : rftEventAward.getAwardApprovals()) {
				app.setEvent(rftEvent);
				app.setLevel(level++);
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
					for (RftAwardApprovalUser approvalUser : app.getApprovalUsers()) {
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
		RftEvent event = rftEventDao.findById(dbRftEventAward.getRfxEvent().getId());
		event.setAwardedSuppliers(awardedSuppliers);
		// Only Save is called. Now set the Award status as DRAFT or our internal consumption.
		// DRAFT status is not shown to the user.
		if (!transfer && !conclude) {
			event.setAwardStatus(AwardStatus.DRAFT);
		}
		if (conclude) {
			event.setAwardStatus(AwardStatus.PENDING);
		}
		if (rftEventAward.getRfxEvent() != null) {
			event.setEnableAwardApproval(rftEventAward.getRfxEvent().getEnableAwardApproval());
		}
		event.setAwardApprovals(rftEventAward.getAwardApprovals());
		event = rftEventDao.update(event);

		LOG.info(" *********************** Transfer value " + transfer);
		LOG.info(" &&&&&&&&&&&&&&&&&&& COnclude Value " + conclude);

		if (transfer == false && conclude == false) {
			if (StringUtils.checkString(dbRftEventAward.getId()).length() > 0) {
				// RftEventAward dbRftEventAward = rftEventAwardDao.findById(eventAward.getId());
				try {
					AwardResponsePojo awardResponsePojo = new AwardResponsePojo();
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbRftEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					ByteArrayOutputStream baos1 = getAwardSnapshotExcel(dbRftEventAward, session, loggedInUser, awardResponsePojo, transfer, conclude);
					if (baos1 != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = baos1.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RftEventAwardAudit audit = null;
						if (Boolean.FALSE == conclude) {
							audit = new RftEventAwardAudit(event, loggedInUser.getBuyer(), loggedInUser, new Date(), "Award saved", snapshot, excelSnapshot);
							if (rftEventAward.getAttachment() != null) {
								LOG.info("File Name:-------->" + rftEventAward.getAttachment().getOriginalFilename());
								RftEventAward rftEventAward1 = rftEventAwardDao.findById(dbRftEventAward.getId());
								try {
									rftEventAward1.setFileData(rftEventAward.getAttachment().getBytes());
									audit.setFileData(rftEventAward.getAttachment().getBytes());
								} catch (IOException e) {
									LOG.error("Eroor While getting file data" + e.getMessage(), e);
								}
								rftEventAward1.setFileName(rftEventAward.getAttachment().getOriginalFilename());
								rftEventAward1.setCredContentType(rftEventAward.getAttachment().getContentType());
								audit.setFileName(rftEventAward.getAttachment().getOriginalFilename());
								audit.setCredContentType(rftEventAward.getAttachment().getContentType());
								rftEventAwardDao.saveOrUpdate(rftEventAward1);
							}
							eventAwardAuditService.saveRftAwardAudit(audit);
						}
					}
				} catch (Exception e) {
					LOG.error("Error while :" + e.getMessage(), e);
				}
			}
		} else if(conclude) {
			if (rftEventAward.getAttachment() != null) {
				LOG.info("File Name:-------->" + rftEventAward.getAttachment().getOriginalFilename());
				RftEventAward rftEventAward1 = rftEventAwardDao.findById(dbRftEventAward.getId());
				try {
					rftEventAward1.setFileData(rftEventAward.getAttachment().getBytes());								} catch (IOException e) {
					LOG.error("Eroor While getting file data" + e.getMessage(), e);
				}
				rftEventAward1.setFileName(rftEventAward.getAttachment().getOriginalFilename());
				rftEventAward1.setCredContentType(rftEventAward.getAttachment().getContentType());
				rftEventAwardDao.saveOrUpdate(rftEventAward1);
			}
		}

		DecimalFormat df = null;

		if (dbRftEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		ErpSetup erpSetup = erpSetupService.findErpByWithTepmlateTenantId(loggedInUser.getTenantId());
		if (erpSetup == null || (erpSetup != null && Boolean.FALSE.equals(erpSetup.getIsErpEnable()))) {
			if (Boolean.TRUE == conclude) {
				rftEvent.setConcludeRemarks(dbRftEventAward.getAwardRemarks());
				rftEvent.setConcludeBy(loggedInUser);
				rftEvent.setConcludeDate(new Date());
				rftEvent.setStatus(EventStatus.COMPLETE);
				rftEvent.setAwarded(Boolean.TRUE);
			}
			rftEventDao.update(rftEvent);

			if (Boolean.TRUE == conclude) {
				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRftEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rftEventAwardDao.getRftEventAwardDetailsByEventIdandBqId(dbRftEventAward.getId());

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
							// tatReport.setPaperApprovalDaysCount(BigDecimal.valueOf(paperApprovalDays).setScale(2,
							// RoundingMode.HALF_UP));
						}
						// tatReportService.updateTatReport(tatReport);
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
			if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2 && Boolean.FALSE == rftEvent.getBusinessUnit().getBudgetCheck()) {
				// rftEvent.setStatus(EventStatus.FINISHED);
				// rftEvent.setAwarded(Boolean.TRUE);
				// rftEventDao.update(rftEvent);

				try {
					TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(dbRftEventAward.getRfxEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
					String awardedSuppliersName = "";
					String awardedSuppliersValue = "";

					if (tatReport != null) {
						List<AwardReferenceNumberPojo> award = rftEventAwardDao.getRftEventAwardDetailsByEventIdandBqId(dbRftEventAward.getId());

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

		return dbRftEventAward;
	}

	@Override
	public RftEventAward rftEventAwardByEventIdandBqId(String eventId, String bqId) {
		RftEventAward eventAward = rftEventAwardDao.rftEventAwardByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RftEventAwardDetails details : eventAward.getRfxAwardDetails()) {
					if (details.getSupplier() != null) {
						LOG.info(details.getSupplier().getId());

						details.getSupplier().getId();
					}
					if (details.getBqItem() != null) {
						details.getBqItem().getId();
						if (details.getBqItem().getUom() != null) {
							LOG.info("UOM " + details.getBqItem().getUom().getUom());
							details.getBqItem().getUom().getUom();
						}
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
	public RftSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId) {
		RftSupplierBqItem rftSupplierBqItem = rftSupplierBqItemDao.getBqItemByRftBqItemId(bqItemId, supplierId);
		if (rftSupplierBqItem != null) {
			rftSupplierBqItem.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, supplierId));
			if (CollectionUtil.isNotEmpty(rftSupplierBqItem.getProductCategoryList())) {
				for (ProductCategory productCategory : rftSupplierBqItem.getProductCategoryList()) {
					productCategory.setBuyer(null);
					productCategory.setCreatedBy(null);
					productCategory.setModifiedBy(null);
				}
			}
			if (rftSupplierBqItem.getSupplier() != null) {
				rftSupplierBqItem.getSupplier().getFullName();
				// supp.getFullName();
			}
			if (CollectionUtil.isNotEmpty(rftSupplierBqItem.getChildren())) {
				rftSupplierBqItem.setChildren(null);
			}
			if (rftSupplierBqItem.getParent() != null) {
				rftSupplierBqItem.setParent(null);
				// if()
			}
			if (rftSupplierBqItem.getBqItem() != null) {
				RftBqItem bqitem = rftSupplierBqItem.getBqItem();
				bqitem.getItemName();
			}
			if (rftSupplierBqItem.getUom() != null) {
				rftSupplierBqItem.setUom(null);
			}

		}
		return rftSupplierBqItem;
	}

	@Override
	public List<RftEventAward> getRftEventAwardsByEventId(String eventId) {
		List<RftEventAward> awardList = rftEventAwardDao.getRftEventAwardsByEventId(eventId);
		if (CollectionUtil.isNotEmpty(awardList)) {
			for (RftEventAward rftEventAward : awardList) {
				if (CollectionUtil.isNotEmpty(rftEventAward.getRfxAwardDetails())) {
					for (RftEventAwardDetails detail : rftEventAward.getRfxAwardDetails()) {
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
	public void transferRftAward(String eventId, String tenantId, HttpSession session, User loggedInUser, String rftAwardEvevtId, Boolean transfer, RfxTypes eventType) throws Exception {

		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(tenantId);

		if (Boolean.FALSE == erpSetup.getIsErpEnable()) {
			LOG.warn(">>>>>>>>> ERP IS NOT ENABLED. Skipping transfer...  Event Id: " + eventId);
			return;
		}

		RftEvent rftEvent = rftEventDao.findByEventId(eventId);

		// If award type integration is FGV
		if (Boolean.TRUE == erpSetup.getEnableAwardErpPush() && erpSetup.getErpIntegrationTypeForAward() == ErpIntegrationTypeForAward.TYPE_2) {

			if (Boolean.FALSE == rftEvent.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("Business unit not enabled for budget checking ... Event Id : " + eventId + ", Business Unit : " + rftEvent.getBusinessUnit().getUnitName());
				throw new ApplicationException("Cannot send this event award details to ERP as Event Business unit not enabled for budget checking.");
			}

			RftEventAward dbEventAward = rftEventAwardDao.findById(rftAwardEvevtId);

			if (dbEventAward == null) {
				LOG.error("Could not identify the award details to push to ERP. Please contact the application administrator. Award ID : " + rftAwardEvevtId + ", Event Id : " + eventId);
				throw new ApplicationException("Could not identify the award details to push to ERP. Please contact the application administrator.");
			}

			AwardErp2Pojo awardPojo = new AwardErp2Pojo();

			if (StringUtils.checkString(rftEvent.getPreviousRequestId()).length() == 0) {
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			}
			SourcingFormRequest rfs = sourcingFormRequestDao.findById(rftEvent.getPreviousRequestId());

			if (rfs == null) {
				LOG.warn("RFS not found during award... Event Id : " + eventId + ", RFS ID : " + rftEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as it does not seem to have created from a Sourcing Request.");
			} else if (Boolean.FALSE == rfs.getBusinessUnit().getBudgetCheck()) {
				LOG.warn("RFS Business unit not enabled for budget checking ... Event Id : " + eventId + ", RFS ID : " + rftEvent.getPreviousRequestId());
				throw new ApplicationException("Cannot send this event award details to ERP as RFS Business unit not enabled for budget checking.");
			}

			// pr.setErpDocNo(prResponse.getSapRefNo());
			// pr.setErpMessage(prResponse.getMessage());
			// pr.setErpPrTransferred(Boolean.TRUE);
			// pr.setErpStatus(prResponse.getStatus());
			awardPojo.setLoginEmail(loggedInUser.getLoginId());
			awardPojo.setSapRefNo(rfs.getErpDocNo());
			awardPojo.setSapDocType(SapDocType.RFT);
			awardPojo.setEventId(rftEvent.getEventId());
			List<AwardDetailsErp2Pojo> items = new ArrayList<AwardDetailsErp2Pojo>();

			// Order the award details by BQ Level and Order sequence
			SortedMap<Integer, RftEventAwardDetails> sm = new TreeMap<Integer, RftEventAwardDetails>();
			for (RftEventAwardDetails eventAward : dbEventAward.getRfxAwardDetails()) {
				if (eventAward.getBqItem().getOrder() > 0) {
					String str = String.valueOf(eventAward.getBqItem().getLevel() * 10000) + String.valueOf(eventAward.getBqItem().getOrder());
					sm.put(Integer.parseInt(str), eventAward);
				}
			}

			int sequence = 1;
			for (Map.Entry<Integer, RftEventAwardDetails> m : sm.entrySet()) {
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
				rftEventAwardDao.save(dbEventAward);

				ERP_LOG.info("ERP Response : " + responseMsg);

				try {
					JasperPrint eventAward1 = getAwardSnapShotPdf(dbEventAward, session, loggedInUser, null, transfer, false);
					ByteArrayOutputStream workbook = getAwardSnapshotExcel(dbEventAward, session, loggedInUser, null, transfer, false);
					if (workbook != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward1);
						byte[] excelSnapshot = workbook.toByteArray();

						LOG.info("FILE Size : " + snapshot.length);
						RftEventAwardAudit audit = new RftEventAwardAudit(dbEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRftAwardAudit(audit);
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rftEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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

			List<RftEventAward> awardList = getRftEventAwardsByEventId(eventId);

			if (CollectionUtil.isEmpty(awardList)) {
				LOG.warn("Award List is Empty for eventId :" + eventId);
				return;
			}

			List<AwardErpPojo> awardPojoList = new ArrayList<>();
			for (RftEventAward eventAward : awardList) {
				AwardErpPojo award = new AwardErpPojo();
				award.setId(eventAward.getId());
				award.setEventId(eventAward.getRfxEvent().getEventId());
				award.setEventName(eventAward.getRfxEvent().getEventName());
				award.setCurrencyCode(eventAward.getRfxEvent().getBaseCurrency() != null ? eventAward.getRfxEvent().getBaseCurrency().getCurrencyCode() : "");
				award.setAwardRemark(eventAward.getAwardRemarks());
				award.setBusinessUnitName(eventAward.getRfxEvent().getBusinessUnit() != null ? eventAward.getRfxEvent().getBusinessUnit().getDisplayName() : "");
				award.setEventOwner(eventAward.getRfxEvent().getCreatedBy() != null ? eventAward.getCreatedBy().getName() : "");
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
						LOG.info("Country code : " + eventAward.getRfxEvent().getDeliveryAddress().getCountry().getCountryCode());
					}
					award.setDeliveryAddress(deliveryAddress);
				}
				List<AwardDetailsErpPojo> awardDetailsList = new ArrayList<>();
				for (RftEventAwardDetails eventAwardDetails : eventAward.getRfxAwardDetails()) {
					if (eventAwardDetails.getBqItem().getOrder() == 0) {
						continue;
					}
					AwardDetailsErpPojo awardDetails = new AwardDetailsErpPojo();

					// setting fav supplier vendorCode
					if (eventAwardDetails.getSupplier() != null) {
						FavouriteSupplier supplier = favoriteSupplierService.findFavSupplierBySuppId(eventAwardDetails.getSupplier().getId(), tenantId);
						if (StringUtils.checkString(supplier.getVendorCode()).length() > 0) {
							awardDetails.setVendorCode(supplier.getVendorCode());
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

			// AwardResponsePojo resultMap=erpIntegrationService.sendAwardPage(awardPojoList);

			if (StringUtils.checkString(rftAwardEvevtId).length() > 0) {
				RftEventAward dbRftEventAward = rftEventAwardDao.findById(rftAwardEvevtId);
				try {
					JasperPrint eventAward = getAwardSnapShotPdf(dbRftEventAward, session, loggedInUser, resultMap, transfer, false);
					ByteArrayOutputStream baos1 = getAwardSnapshotExcel(dbRftEventAward, session, loggedInUser, resultMap, transfer, false);
					if (baos1 != null) {
						byte[] snapshot = JasperExportManager.exportReportToPdf(eventAward);
						byte[] excelSnapshot = baos1.toByteArray();
						LOG.info("SUSPEND FILE Size : " + snapshot.length);
						RftEventAwardAudit audit = new RftEventAwardAudit(dbRftEventAward.getRfxEvent(), loggedInUser.getBuyer(), loggedInUser, new Date(), "Award Transfer", snapshot, excelSnapshot);
						eventAwardAuditService.saveRftAwardAudit(audit);
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Award transferred for event '" + rftEvent.getEventId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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

		if (rftEvent != null && rftEvent.getAwardDate() == null) {
			rftEvent.setAwardDate(new Date());
		}
		rftEvent.setStatus(EventStatus.FINISHED);
		rftEvent.setAwarded(Boolean.TRUE);
		rftEventDao.update(rftEvent);

		DecimalFormat df = null;
		if (rftEvent.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (rftEvent.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (rftEvent.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (rftEvent.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (rftEvent.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (rftEvent.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		try {
			TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, tenantId);
			String awardedSuppliersName = "";
			String awardedSuppliersValue = "";

			if (tatReport != null) {
				List<AwardReferenceNumberPojo> award = rftEventAwardDao.getRftEventAwardDetailsByEventIdandBqId(rftAwardEvevtId);

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
	public JasperPrint getAwardSnapShotPdf(RftEventAward dbRftEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) {

		// For Financial Standard
		DecimalFormat df = null;

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		LOG.info("getDecimal" + dbRftEventAward.getRfxEvent().getDecimal());
			if(dbRftEventAward.getRfxEvent().getDecimal() != null){
				if (dbRftEventAward.getRfxEvent().getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				}
			}

		JasperPrint eventAward = null;
		List<RftEventAwardDetailsPojo> auditTrailSummary = new ArrayList<RftEventAwardDetailsPojo>();

		List<RftEventAwardDetails> rftEventAwardDetails = dbRftEventAward.getRfxAwardDetails();

		if (CollectionUtil.isNotEmpty(rftEventAwardDetails)) {

			for (RftEventAwardDetails rftEventAwardDetail : rftEventAwardDetails) {
				RftEventAwardDetailsPojo rftEventAwardDetailsPojo = new RftEventAwardDetailsPojo();
				if (rftEventAwardDetail.getBqItem() != null) {
					String itemSeq = rftEventAwardDetail.getBqItem().getLevel() + "." + rftEventAwardDetail.getBqItem().getOrder();
					rftEventAwardDetailsPojo.setItemSeq(itemSeq);

					rftEventAwardDetailsPojo.setItemName(StringUtils.checkString(rftEventAwardDetail.getBqItem().getItemName()));
					if (rftEventAwardDetail.getSupplier() != null && rftEventAwardDetail.getSupplier().getCompanyName() != null) {
						LOG.info("Supplier Name--------" + rftEventAwardDetail.getSupplier().getFullName());
						LOG.info("Company Name-------" + rftEventAwardDetail.getSupplier().getCompanyName());
						LOG.info("Supplier ID---------" + rftEventAwardDetail.getSupplier().getId());
						rftEventAwardDetailsPojo.setSupplierName(rftEventAwardDetail.getSupplier().getCompanyName());
						LOG.info("after company--------------------" + rftEventAwardDetailsPojo.getSupplierName());
					} else {

						rftEventAwardDetailsPojo.setSupplierName("");
					}
					rftEventAwardDetailsPojo.setSupplierPrice(rftEventAwardDetail.getOriginalPrice() != null ? df.format(rftEventAwardDetail.getOriginalPrice()) : "");
					rftEventAwardDetailsPojo.setAwardedPrice(rftEventAwardDetail.getAwardedPrice() != null ? df.format(rftEventAwardDetail.getAwardedPrice()) : "");
					if ((rftEventAwardDetail.getBqItem().getOrder() > 0)) {
						if (rftEventAwardDetail.getTax() != null) {
							rftEventAwardDetailsPojo.setTax(df.format(rftEventAwardDetail.getTax()));
						} else {
							rftEventAwardDetailsPojo.setTax("N/A");
						}
						if (awardResponsePojo != null) {
							if (CollectionUtil.isNotEmpty(awardResponsePojo.getRefNumList())) {
								for (AwardReferenceNumberPojo awardReferenceNumberPojo : awardResponsePojo.getRefNumList()) {
									if (rftEventAwardDetail.getBqItem().getLevel() == awardReferenceNumberPojo.getLevel() && rftEventAwardDetail.getBqItem().getOrder() == awardReferenceNumberPojo.getOrder()) {
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
					rftEventAwardDetailsPojo.setTaxType(rftEventAwardDetail.getTaxType() != null ? rftEventAwardDetail.getTaxType().toString() : "");
					rftEventAwardDetailsPojo.setTotalPrice(rftEventAwardDetail.getTotalPrice() != null ? df.format(rftEventAwardDetail.getTotalPrice()) : "");

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
			parameters.put("title", "Award History");
			parameters.put("date", sdf.format(new Date()));
			parameters.put("remark", dbRftEventAward.getAwardRemarks());
			parameters.put("eventNo", dbRftEventAward.getRfxEvent().getEventId());
			parameters.put("eventName", dbRftEventAward.getRfxEvent().getEventName());
			parameters.put("bqName", dbRftEventAward.getBq().getName());
			parameters.put("actionBy", loggedInUser.getName());
			if (Boolean.TRUE == transfer) {
				parameters.put("actionType", "Save & Transfer");
			} else if (Boolean.TRUE == conclude) {
				parameters.put("actionType", "Conclude Award");
			} else {
				parameters.put("actionType", "Save");
			}
			parameters.put("totalSupplierPrice", String.valueOf(df.format(dbRftEventAward.getTotalSupplierPrice())));
			parameters.put("totalAwardPrice", String.valueOf(df.format(dbRftEventAward.getTotalAwardPrice())));
			parameters.put("GrandPrice", String.valueOf(df.format(dbRftEventAward.getGrandTotalPrice())));

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auditTrailSummary, false);
			eventAward = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Could not generate Audit Trail PDF Report. " + e.getMessage(), e);
		}

		return eventAward;
	}

	@Override
	public void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws Exception {
		RftEventAwardAudit audit = eventAwardAuditService.findByRftAuditId(id);
		response.setContentLength(audit.getSnapshot().length);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".pdf\"");

		FileCopyUtils.copy(audit.getSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public ByteArrayOutputStream getAwardSnapshotExcel(RftEventAward dbRftEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DecimalFormat df = null;
		if (dbRftEventAward.getRfxEvent().getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (dbRftEventAward.getRfxEvent().getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}

		Workbook workbook = new XSSFWorkbook();
		try {
			List<RftEventAwardDetails> rftEventAwardDetails = dbRftEventAward.getRfxAwardDetails();
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
			cell.setCellValue(dbRftEventAward.getRfxEvent().getEventId());

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
			cell.setCellValue(dbRftEventAward.getRfxEvent().getEventName());

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
			cell.setCellValue(dbRftEventAward.getBq().getName());

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
			for (RftEventAwardDetails rftEventAwardDetail : rftEventAwardDetails) {
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
			cell.setCellValue(df.format(dbRftEventAward.getGrandTotalPrice()));
			cell.setCellStyle(styleHeading);
			try {
				cell = row.createCell(cellNumber - 5);
				cell.setCellValue(String.valueOf(df.format(dbRftEventAward.getTotalAwardPrice())));
				cell.setCellStyle(styleHeading);
				cell = row.createCell(cellNumber - 6);
				cell.setCellValue(String.valueOf(df.format(dbRftEventAward.getTotalAwardPrice())));
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
			cellRemarks.setCellValue(dbRftEventAward.getAwardRemarks());
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
	public void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws Exception {
		RftEventAwardAudit audit = eventAwardAuditService.findByRftAuditId(id);
		response.setContentLength(audit.getExcelSnapshot().length);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"Award_Audit_" + audit.getActionDate() + ".xls\"");

		FileCopyUtils.copy(audit.getExcelSnapshot(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFileSnapShot(String id, HttpServletResponse response) throws IOException {
		RftEventAwardAudit audit = eventAwardAuditService.findByRftAuditId(id);
		response.setContentLength(audit.getFileData().length);
		response.setContentType(audit.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + audit.getFileName());
		FileCopyUtils.copy(audit.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	public void downloadAwardAttachFile(String id, HttpServletResponse response) throws IOException {
		RftEventAward rftEventAward = rftEventAwardDao.findById(id);
		LOG.info("File Name:" + rftEventAward.getFileName());
		response.setContentLength(rftEventAward.getFileData().length);
		response.setContentType(rftEventAward.getCredContentType());
		response.addHeader("Content-Disposition", "attachment; filename=" + rftEventAward.getFileName());
		FileCopyUtils.copy(rftEventAward.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public RftEventAward rftEventAwardDetailsByEventIdandBqId(String eventId, String bqId) {
		RftEventAward eventAward = rftEventAwardDao.rftEventAwardDetailsByEventIdandBqId(eventId, bqId);
		if (eventAward != null) {
			if (eventAward.getRfxEvent() != null) {
				eventAward.getRfxEvent().getId();
				eventAward.getRfxEvent().getEventName();
			}
			if (CollectionUtil.isNotEmpty(eventAward.getRfxAwardDetails())) {
				for (RftEventAwardDetails details : eventAward.getRfxAwardDetails()) {
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
		}
		return eventAward;
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean updateEventAwardApproval(RftEventAward eventAward, User user, String eventId) {

		RftEvent rftEvent = rftEventDao.findByEventId(eventId);

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RftEventAwardApproval approvalRequest : rftEvent.getAwardApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RftAwardApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(eventAward.getAwardApprovals())) {
			int level = 1;
			for (RftEventAwardApproval app : eventAward.getAwardApprovals()) {
				app.setEvent(rftEvent);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftAwardApprovalUser approvalUser : app.getApprovalUsers()) {
						LOG.info("   I ddddddd 1" + approvalUser.getUserId());
						LOG.info("   I ddddddd " + approvalUser.getId());
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

		rftEvent.setAwardApprovals(eventAward.getAwardApprovals());
		rftEvent.setModifiedBy(SecurityLibrary.getLoggedInUser());
		rftEvent.setModifiedDate(new Date());
		if (eventAward.getRfxEvent() != null) {
			rftEvent.setEnableAwardApproval(eventAward.getRfxEvent().getEnableAwardApproval());
		}
		rftEvent = rftEventDao.saveOrUpdate(rftEvent);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				try {
					RftEventAudit audit = new RftEventAudit(rftEvent, user, new Date(), AuditActionType.Update, auditMessage);
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Award Approval Route is updated for Event '" + rftEvent.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFT);
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
