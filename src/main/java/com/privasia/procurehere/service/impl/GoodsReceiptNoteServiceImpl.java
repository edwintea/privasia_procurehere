package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.GoodsReceiptNoteAuditDao;
import com.privasia.procurehere.core.dao.GoodsReceiptNoteDao;
import com.privasia.procurehere.core.dao.GoodsReceiptNoteItemDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.GrnAuditType;
import com.privasia.procurehere.core.enums.GrnAuditVisibilityType;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.ErpGrnItemsPojo;
import com.privasia.procurehere.core.pojo.ErpGrnPojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNoteSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.GoodsReceiptNoteService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.controller.GrnItemsSummaryPojo;
import com.privasia.procurehere.web.controller.GrnSummaryPojo;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class GoodsReceiptNoteServiceImpl implements GoodsReceiptNoteService {

	private static final Logger LOG = LogManager.getLogger(Global.GRN_LOG);

	@Value("${app.url}")
	String APP_URL;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	GoodsReceiptNoteDao goodsReceiptNoteDao;

	@Autowired
	GoodsReceiptNoteAuditDao goodsReceiptNoteAuditDao;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	UserService userService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	UomService uomService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	PoDao poDao;

	@Autowired
	GoodsReceiptNoteItemDao goodsReceiptNoteItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	EventIdSettingsService eventIdSettingsService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = true)
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return goodsReceiptNoteDao.findAllSearchFilterGrns(tenantId, input, startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public long findTotalSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return goodsReceiptNoteDao.findTotalSearchFilterGrns(tenantId, input, startDate, endDate);

	}

	@Override
	@Transactional(readOnly = true)
	public GoodsReceiptNote findByGrnId(String grnId) {
		LOG.info("GRN ID : " + grnId);
		return goodsReceiptNoteDao.findByGrnId(grnId);
	}

	@Override
	@Transactional(readOnly = false)
	public GoodsReceiptNote createGrn(ErpGrnPojo erpGrnPojo, Buyer buyer) throws NoSuchMessageException, ApplicationException {
		LOG.info(">> " + erpGrnPojo.toLogString());
		User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
		if (user == null) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
		}

		CostCenter costCenter = null;
		if (StringUtils.checkString(erpGrnPojo.getCostCenter()).length() > 0) {
			costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(erpGrnPojo.getCostCenter(), buyer.getId());
			if (costCenter == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { erpGrnPojo.getCostCenter() }, Global.LOCALE));
			}
		}

		BusinessUnit businessUnit = null;
		if (StringUtils.checkString(erpGrnPojo.getBusinessUnit()).length() > 0) {
			businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), erpGrnPojo.getBusinessUnit());
			if (businessUnit == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { erpGrnPojo.getBusinessUnit() }, Global.LOCALE));
			}
		}

		Currency currency = null;
		if (StringUtils.checkString(erpGrnPojo.getCurrency()).length() == 0) {
			throw new ApplicationException("Invalid Currency Code: " + erpGrnPojo.getCurrency());
		}

		currency = currencyDao.findByCurrencyCode(erpGrnPojo.getCurrency());
		if (currency == null) {
			throw new ApplicationException("Invalid Currency Code: " + erpGrnPojo.getCurrency());
		}

		FavouriteSupplier supplier = null;
		if (StringUtils.checkString(erpGrnPojo.getSupplierCode()).length() > 0) {
			supplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(erpGrnPojo.getSupplierCode()), buyer.getId());
		}

		GoodsReceiptNote note = new GoodsReceiptNote();
		note.setBuyer(buyer);
		note.setBusinessUnit(businessUnit);
		note.setCostCenter(costCenter);
		note.setGrnId(erpGrnPojo.getGrnId());
		note.setGrnTitle(erpGrnPojo.getGrnTitle());
		note.setReferenceNumber(erpGrnPojo.getReferenceNumber());
		note.setCreatedBy(user);
		note.setCreatedDate(erpGrnPojo.getCreatedDate());
		note.setCurrency(currency);
		note.setDecimal(erpGrnPojo.getDecimal() != null ? erpGrnPojo.getDecimal() : 2);
		note.setGrandTotal(erpGrnPojo.getGrandTotal());
		if (supplier != null && supplier.getSupplier() != null) {
			Supplier sup = supplier.getSupplier();
			note.setSupplier(sup);
			note.setSupplierName(sup.getCompanyName());
			note.setSupplierAddress(sup.getLine1() + "\n" + sup.getLine2() + "\n" + sup.getCity() + "\n" + sup.getPostalCode() + "\n" + sup.getState().getStateName() + "\n" + sup.getRegistrationOfCountry().getCountryName());
			note.setSupplierFaxNumber(supplier.getFavouriteSupplierTaxNumber());
			note.setSupplierTaxNumber(supplier.getTaxNumber());
			note.setSupplierTelNumber(supplier.getCompanyContactNumber());
		}

		note.setSupplierName(erpGrnPojo.getSupplierName());
		note.setSupplierAddress(erpGrnPojo.getSupplierAddress());
		note.setSupplierFaxNumber(erpGrnPojo.getSupplierFaxNumber());
		note.setSupplierTaxNumber(erpGrnPojo.getSupplierTaxNumber());
		note.setSupplierTelNumber(erpGrnPojo.getSupplierTelNumber());

		note.setStatus(GrnStatus.DRAFT);

		// Corrosponding Adress
		note.setLine1(businessUnit.getLine1());
		note.setLine2(businessUnit.getLine2());
		note.setLine3(businessUnit.getLine3());
		note.setLine4(businessUnit.getLine4());
		note.setLine5(businessUnit.getLine5());
		note.setLine6(businessUnit.getLine6());
		note.setLine7(businessUnit.getLine7());

		// Delivery Adress
		note.setDeliveryAddressTitle(erpGrnPojo.getDeliveryAddressTitle());
		note.setDeliveryAddressLine1(erpGrnPojo.getDeliveryAddressLine1());
		note.setDeliveryAddressLine2(erpGrnPojo.getDeliveryAddressLine2());
		note.setDeliveryAddressCity(erpGrnPojo.getDeliveryAddressCity());
		note.setDeliveryAddressZip(erpGrnPojo.getDeliveryAddressZip());
		note.setDeliveryAddressState(erpGrnPojo.getDeliveryAddressState());
		note.setDeliveryAddressCountry(erpGrnPojo.getDeliveryAddressCountry());

		List<GoodsReceiptNoteItem> items = new ArrayList<GoodsReceiptNoteItem>();

		if (CollectionUtil.isNotEmpty(erpGrnPojo.getItemList())) {
			GoodsReceiptNoteItem parent = new GoodsReceiptNoteItem();
			parent.setItemName("Bill of Items");
			parent.setLevel(1);
			parent.setOrder(0);
			parent.setBuyer(buyer);
			parent.setGoodsReceiptNote(note);

			int order = 0;
			for (ErpGrnItemsPojo itemPojo : erpGrnPojo.getItemList()) {
				LOG.info(">>>> " + itemPojo.toLogString());

				if (itemPojo.getQuantity().floatValue() <= 0.0f) {
					throw new ApplicationException("Quantity should be more than zero. Item No: " + itemPojo.getItemNo());
				}
				Uom uom = uomService.getUomByUomAndTenantId(itemPojo.getUom(), buyer.getId());
				if (uom == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { itemPojo.getUom() }, Global.LOCALE));
				}
				costCenter = null;
				if (StringUtils.checkString(itemPojo.getCostCenter()).length() > 0) {
					costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(itemPojo.getCostCenter(), buyer.getId());
				}

				if (StringUtils.checkString(itemPojo.getBusinessUnit()).length() > 0) {
					businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), itemPojo.getBusinessUnit());
					if (businessUnit == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { itemPojo.getBusinessUnit() }, Global.LOCALE));
					}
				}

				GoodsReceiptNoteItem item = new GoodsReceiptNoteItem();
				item.setParent(parent);
				item.setGoodsReceiptNote(note);
				item.setBusinessUnit(businessUnit);
				item.setBuyer(buyer);
				item.setCostCenter(costCenter);
				item.setItemDescription(itemPojo.getItemDescription());
				item.setItemName(itemPojo.getItemName());
				item.setItemTax(StringUtils.checkString(itemPojo.getItemTax()).length() > 0 ? new BigDecimal(itemPojo.getItemTax()) : BigDecimal.ZERO);
				item.setLevel(1);
				item.setOrder(++order);
				item.setQuantity(itemPojo.getQuantity().setScale(note.getDecimal(), RoundingMode.DOWN));
				item.setTaxAmount(itemPojo.getTaxAmount().setScale(note.getDecimal(), RoundingMode.DOWN));
				item.setItemTax(new BigDecimal(itemPojo.getItemTax()).setScale(note.getDecimal(), RoundingMode.DOWN));
				item.setTotalAmount(itemPojo.getTotalAmount().setScale(note.getDecimal(), RoundingMode.DOWN));
				item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax().setScale(note.getDecimal(), RoundingMode.DOWN));
				item.setUnit(uom);
				item.setUnitPrice(itemPojo.getUnitPrice().setScale(note.getDecimal(), RoundingMode.DOWN));

				item.setDeliveryAddressTitle(itemPojo.getDeliveryAddressTitle());
				item.setDeliveryAddressLine1(itemPojo.getDeliveryAddressLine1());
				item.setDeliveryAddressLine2(itemPojo.getDeliveryAddressLine2());
				item.setDeliveryAddressCity(itemPojo.getDeliveryAddressCity());
				item.setDeliveryAddressZip(itemPojo.getDeliveryAddressZip());
				item.setDeliveryAddressState(itemPojo.getDeliveryAddressState());
				item.setDeliveryAddressCountry(itemPojo.getDeliveryAddressCountry());

				items.add(item);
			}

			note.setGoodsReceiptNoteItems(items);
		}
		note = goodsReceiptNoteDao.save(note);

		try {
			GoodsReceiptNoteAudit audit = new GoodsReceiptNoteAudit();
			audit.setAction(GrnAuditType.CREATE);
			audit.setActionBy(user);
			audit.setActionDate(new Date());
			audit.setBuyer(buyer);
			if (supplier != null && supplier.getSupplier() != null) {
				audit.setSupplier(supplier.getSupplier());
			}
			audit.setVisibilityType(GrnAuditVisibilityType.BUYER);
			audit.setDescription(messageSource.getMessage("grn.audit.ready", new Object[] { note.getGrnId() }, Global.LOCALE));
			audit.setGoodsReceiptNote(note);
			goodsReceiptNoteAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error while saving GRN Create Audit " + e.getMessage(), e);
		}

		return note;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public GoodsReceiptNote acceptGrn(String grnId, User loggedInUser, String supplierRemark) throws EmailException {

		GoodsReceiptNote grn = goodsReceiptNoteDao.findByGrnId(grnId);

		if (grn != null) {
			grn.setStatus(GrnStatus.ACCEPTED);
			goodsReceiptNoteDao.update(grn);

			GoodsReceiptNoteAudit audit = new GoodsReceiptNoteAudit();
			audit.setAction(GrnAuditType.ACCEPTED);
			audit.setActionBy(loggedInUser);
			audit.setActionDate(new Date());
			audit.setBuyer(grn.getBuyer());
			audit.setSupplier(grn.getSupplier());
			audit.setDescription(messageSource.getMessage("grn.buyerAudit.accepted", new Object[] { supplierRemark }, Global.LOCALE));
			audit.setVisibilityType(GrnAuditVisibilityType.BOTH);
			audit.setGoodsReceiptNote(grn);
			goodsReceiptNoteAuditDao.save(audit);

			try {
				sendEmailNotificationToBuyer(grn, loggedInUser, true, supplierRemark);

			} catch (Exception e) {
				LOG.error("Error while sending do accept mail to buyer " + grn.getBuyer().getCompanyName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending do accept mail to buyer");
			}

		}

		return grn;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public GoodsReceiptNote declineGrn(String grnId, User loggedInUser, String supplierRemark) throws EmailException {

		GoodsReceiptNote grn = goodsReceiptNoteDao.findByGrnId(grnId);

		if (grn != null) {
			grn.setStatus(GrnStatus.DECLINED);
			goodsReceiptNoteDao.update(grn);

			GoodsReceiptNoteAudit buyerAudit = new GoodsReceiptNoteAudit();
			buyerAudit.setAction(GrnAuditType.DECLINED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(grn.getBuyer());
			buyerAudit.setSupplier(grn.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("grn.declined", new Object[] { supplierRemark }, Global.LOCALE));
			buyerAudit.setVisibilityType(GrnAuditVisibilityType.BOTH);
			buyerAudit.setGoodsReceiptNote(grn);
			goodsReceiptNoteAuditDao.save(buyerAudit);

			try {
				sendEmailNotificationToBuyer(grn, loggedInUser, false, supplierRemark);
			} catch (Exception e) {
				LOG.error("Error while sending do decline mail to buyer " + grn.getBuyer().getCompanyName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending do decline mail to buyer");
			}

		}

		return grn;
	}

	@Override
	@Transactional(readOnly = false)
	public GoodsReceiptNote cancelGrn(String grnId, User loggedInUser, String supplierRemark) {
		GoodsReceiptNote grn = goodsReceiptNoteDao.findByGrnId(grnId);
		if (grn != null) {
			GrnStatus status = grn.getStatus();
			grn.setStatus(GrnStatus.CANCELLED);
			goodsReceiptNoteDao.update(grn);

			GoodsReceiptNoteAudit audit = new GoodsReceiptNoteAudit();
			audit.setAction(GrnAuditType.CANCELLED);
			audit.setActionBy(loggedInUser);
			audit.setActionDate(new Date());
			audit.setBuyer(grn.getBuyer());
			audit.setSupplier(grn.getSupplier());
			audit.setDescription(messageSource.getMessage("grn.audit.cancel", new Object[] { supplierRemark }, Global.LOCALE));
			if (GrnStatus.DELIVERED == status) {
				audit.setVisibilityType(GrnAuditVisibilityType.BOTH);
			} else {
				audit.setVisibilityType(GrnAuditVisibilityType.BUYER);
			}
			audit.setGoodsReceiptNote(grn);
			goodsReceiptNoteAuditDao.save(audit);

		}

		return grn;
	}

	private void sendEmailNotificationToBuyer(GoodsReceiptNote grn, User actionBy, boolean isAccept, String supplierRemark) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneForBuyer(grn.getCreatedBy().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String subject = null;
		if (isAccept) {
			subject = "GRN Accepted";
		} else {
			subject = "GRN Declined";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("buyerCompany", grn.getBuyer().getCompanyName());
		map.put("grnNumber", grn.getGrnId());
		map.put("comments", supplierRemark);
		map.put("poNumber", grn.getPo().getPoNumber());
		map.put("grnDate", sdf.format(new Date()));

		map.put("supplierName", actionBy.getName());
		map.put("supplierLoginEmail", actionBy.getLoginId());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		map.put("date", sdf.format(new Date()));
		// TODO:: Need to create email templates
		if(grn.getCreatedBy().getEmailNotifications()) {
			sendEmail(grn.getCreatedBy().getCommunicationEmail(), subject, map, isAccept ? Global.GRN_ACCEPTED_TEMPLATE : Global.GRN_DECLINE_TEMPLATE);
		}
		BuyerNotificationMessage buyerMessage = new BuyerNotificationMessage();
		buyerMessage.setCreatedBy(actionBy);
		buyerMessage.setCreatedDate(new Date());
		String message = null;
		if (isAccept) {
			message = messageSource.getMessage("grn.declined", new Object[] { supplierRemark }, Global.LOCALE);
		} else {
			message = messageSource.getMessage("grn.audit.accepted", new Object[] { supplierRemark }, Global.LOCALE);
		}
		buyerMessage.setMessageTo(grn.getCreatedBy());
		buyerMessage.setMessage(message);
		buyerMessage.setNotificationType(NotificationType.GENERAL);
		buyerMessage.setSubject(subject);
		buyerMessage.setTenantId(actionBy.getTenantId());
		dashboardNotificationService.saveBuyerNotificationMessage(buyerMessage);

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

	private String getTimeZoneForBuyer(String tenantId, String timeZone) {
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
	public GoodsReceiptNote loadGrnById(String id) {
		GoodsReceiptNote grn = goodsReceiptNoteDao.findById(id);
		if (grn != null) {
			if (grn.getCurrency() != null) {
				grn.getCurrency().getCurrencyCode();
			}
			if (grn.getCreatedBy() != null) {
				grn.getCreatedBy().getName();
			}
			if (grn.getBuyer() != null) {
				grn.getBuyer().getFullName();
			}
			if (grn.getBusinessUnit() != null) {
				grn.getBusinessUnit().getDisplayName();
			}
			if (grn.getPo() != null) {
				grn.getPo().getPoNumber();
				grn.getPo().getSupplierAddress();
				if (grn.getPo().getSupplier() != null) {
					grn.getPo().getSupplier().getCompanyContactNumber();
					grn.getPo().getSupplier().getCommunicationEmail();
					grn.getPo().getSupplier().getFullName();
				}
			}
			if (grn.getSupplier() != null) {
				grn.getSupplier().getCompanyName();
				grn.getSupplier().getLine1();
				grn.getSupplier().getLine2();
				grn.getSupplier().getCity();
			}

			List<GoodsReceiptNoteItem> returnList = new ArrayList<GoodsReceiptNoteItem>();
			if (CollectionUtil.isNotEmpty(grn.getGoodsReceiptNoteItems())) {
				for (GoodsReceiptNoteItem item : grn.getGoodsReceiptNoteItems()) {
					GoodsReceiptNoteItem parent = item.createShallowCopy();
					if (item.getParent() == null) {
						returnList.add(parent);
					}

					if (CollectionUtil.isNotEmpty(item.getChildren())) {
						for (GoodsReceiptNoteItem child : item.getChildren()) {
							if (parent.getChildren() == null) {
								parent.setChildren(new ArrayList<GoodsReceiptNoteItem>());
							}
							if (child.getUnit() != null) {
								child.getUnit().getUom();
							}
							parent.getChildren().add(child.createShallowCopy());
						}
					}
				}
			}
			grn.setGoodsReceiptNoteItems(returnList);
		}
		return grn;
	}

	@Override
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrnsForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		return goodsReceiptNoteDao.findAllSearchFilterGrnsForSupplier(loggedInUserTenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterGrnForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		return goodsReceiptNoteDao.findTotalSearchFilterGrnForSupplier(loggedInUserTenantId, input, startDate, endDate);
	}

	@Override
	public List<GoodsReceiptNotePojo> getAllBuyerGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		// return goodsReceiptNoteDao.getAllBuyerGrnDetailsForExcelReport(loggedInUserTenantId, grnIds,
		// goodsReceiptNotePojo, select_all, startDate, endDate, sdf);
		return null;
	}

	@Override
	public List<GoodsReceiptNotePojo> getAllSupplierGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return goodsReceiptNoteDao.getAllSupplierGrnDetailsForExcelReport(loggedInUserTenantId, grnIds, goodsReceiptNotePojo, select_all, startDate, endDate, sdf);
	}

	@Override
	public JasperPrint getGeneratedBuyerGrnPdf(GoodsReceiptNote goodsReceiptNote, String strTimeZone) {
		goodsReceiptNote = goodsReceiptNoteDao.findById(goodsReceiptNote.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (goodsReceiptNote.getDecimal() == 1) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (goodsReceiptNote.getDecimal() == 2) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (goodsReceiptNote.getDecimal() == 3) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (goodsReceiptNote.getDecimal() == 4) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (goodsReceiptNote.getDecimal() == 5) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (goodsReceiptNote.getDecimal() == 6) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/BuyerGrnReport.jasper");
			File jasperfile = resource.getFile();

			GrnSummaryPojo summary = new GrnSummaryPojo();

			summary.setGrnId(goodsReceiptNote.getGrnId());
			summary.setReferenceNumber(goodsReceiptNote.getReferenceNumber());

			if (goodsReceiptNote.getPo() != null) {
				summary.setPoNumber(goodsReceiptNote.getPo().getPoNumber());
			}
			if (goodsReceiptNote.getGoodsReceiptDate() != null) {
				summary.setGoodsReceiptDate(sdf.format(goodsReceiptNote.getGoodsReceiptDate()));
			}
			if (goodsReceiptNote.getGrnReceivedDate() != null) {
				summary.setGrnReceivedDate(sdf.format(goodsReceiptNote.getGrnReceivedDate()));
			}
			getSummaryOfAddressAndGrnItemsforBuyerReport(goodsReceiptNote, df, summary);

			BusinessUnit bUnit = goodsReceiptNote.getBusinessUnit();
			Buyer buyer = goodsReceiptNote.getBuyer();
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
			summary.setBuyerAddress(buyerAddress);

			List<GrnSummaryPojo> grnSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (goodsReceiptNote.getSupplier() != null) {
				Supplier supplier = goodsReceiptNote.getSupplier();
				supplierAddress += supplier.getCompanyName() + "\r\n";
				supplierAddress += supplier.getLine1();
				if (StringUtils.checkString(goodsReceiptNote.getSupplier().getLine2()).length() > 0) {
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
					supplierAddress += supplier.getFaxNumber() + "\r\n";
				}
				supplierAddress += "Email : ";

				if (supplier.getCommunicationEmail() != null) {
					supplierAddress += supplier.getCommunicationEmail();
				}
				supplierAddress += "\r\nTax Reg No : ";
				if (supplier.getTaxRegistrationNumber() != null) {
					supplierAddress += supplier.getTaxRegistrationNumber();
				}

			} else {
				supplierAddress += goodsReceiptNote.getSupplierName() + "\r\n";
				supplierAddress += goodsReceiptNote.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (goodsReceiptNote.getSupplierTelNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (goodsReceiptNote.getSupplierFaxNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierFaxNumber() + "\r\n\n";
				}

				supplierAddress += "Tax Reg No : ";
				if (goodsReceiptNote.getSupplierTaxNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierTaxNumber();
				}
			}

			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
			if (buyerSettings.getFileAttatchment() != null) {
				ImageIcon n = new ImageIcon(buyerSettings.getFileAttatchment());
				summary.setLogo(n.getImage());
				summary.setComanyName(null);

			}
			if (goodsReceiptNote.getSupplier() != null) {
				summary.setSupplierName(goodsReceiptNote.getSupplier() != null ? goodsReceiptNote.getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(goodsReceiptNote.getSupplierName());
			}
			summary.setSupplierAddress(StringUtils.checkString(supplierAddress).length() > 0 ? supplierAddress : "N/A");
			summary.setTaxnumber(goodsReceiptNote.getSupplierTaxNumber() != null ? goodsReceiptNote.getSupplierTaxNumber() : "");

			parameters.put("GRN_SUMMARY", grnSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(grnSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Buyer GRN Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private void getSummaryOfAddressAndGrnItemsforBuyerReport(GoodsReceiptNote goodsReceiptNote, DecimalFormat df, GrnSummaryPojo summary) {
		try {

			// grn items List
			List<GrnItemsSummaryPojo> grnItemList = new ArrayList<GrnItemsSummaryPojo>();
			List<GoodsReceiptNoteItem> grnList = findAllGrnItemByGrnId(goodsReceiptNote.getId());
			if (CollectionUtil.isNotEmpty(grnList)) {
				for (GoodsReceiptNoteItem item : grnList) {
					GrnItemsSummaryPojo grns = new GrnItemsSummaryPojo();
					grns.setSlno(item.getLevel() + "." + item.getOrder());
					grns.setItemName(item.getItemName());
					grns.setCurrency(item.getGoodsReceiptNote().getCurrency().getCurrencyCode());
					grns.setItemDescription(item.getItemDescription());
					grns.setAdditionalTax(df.format(item.getGoodsReceiptNote().getAdditionalTax()));
					grns.setGrandTotal(df.format(item.getGoodsReceiptNote().getGrandTotal()));
					grns.setSumAmount(df.format(goodsReceiptNote.getTotal()));
					grns.setTaxDescription(item.getGoodsReceiptNote().getTaxDescription());
					grns.setDecimal(goodsReceiptNote.getDecimal().toString());
					grnItemList.add(grns);
					if (item.getChildren() != null) {
						for (GoodsReceiptNoteItem childItem : item.getChildren()) {
							GrnItemsSummaryPojo childGrn = new GrnItemsSummaryPojo();
							childGrn.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childGrn.setItemName(childItem.getItemName());
							childGrn.setItemDescription(childItem.getItemDescription());
							childGrn.setQuantity(childItem.getReceivedQuantity());
							childGrn.setUnitPrice(df.format(childItem.getUnitPrice()));
							childGrn.setTaxAmount(df.format(childItem.getTaxAmount()));
							childGrn.setTotalAmount(df.format(childItem.getTotalAmount()));
							childGrn.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childGrn.setUom(childItem.getUnit() != null ? (childItem.getUnit().getUom() != null ? childItem.getUnit().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childGrn.setCurrency(childItem.getGoodsReceiptNote().getCurrency().getCurrencyCode());
							childGrn.setAdditionalTax(df.format(childItem.getGoodsReceiptNote().getAdditionalTax()));
							childGrn.setGrandTotal(df.format(childItem.getGoodsReceiptNote().getGrandTotal()));
							childGrn.setSumAmount(df.format(goodsReceiptNote.getTotal()));
							childGrn.setTaxDescription(StringUtils.checkString(childItem.getGoodsReceiptNote().getTaxDescription()).length() > 0 ? childItem.getGoodsReceiptNote().getTaxDescription() : "");
							childGrn.setSumTaxAmount(childItem.getTaxAmount());
							childGrn.setSumTotalAmt(childItem.getTotalAmount());
							childGrn.setDecimal(goodsReceiptNote.getDecimal().toString());
							grnItemList.add(childGrn);
						}
					}

				}
			}
			summary.setGrnItems(grnItemList);
		} catch (Exception e) {
			LOG.error("Could not Get Grn Items and Address " + e.getMessage(), e);
		}

	}

	private void getSummaryOfAddressAndGrnItems(GoodsReceiptNote goodsReceiptNote, DecimalFormat df, GrnSummaryPojo summary) {
		try {

			String deliveryAddress = "";
			if (StringUtils.checkString(goodsReceiptNote.getDeliveryAddressTitle()).length() > 0) {
				LOG.info("title:" + goodsReceiptNote.getDeliveryAddressTitle());
				deliveryAddress += goodsReceiptNote.getDeliveryAddressTitle() + "\n";

				if (StringUtils.checkString(goodsReceiptNote.getDeliveryAddressLine1()).length() > 0) {
					deliveryAddress += goodsReceiptNote.getDeliveryAddressLine1();
				}
				if (StringUtils.checkString(goodsReceiptNote.getDeliveryAddressLine2()).length() > 0) {
					deliveryAddress += "\n" + goodsReceiptNote.getDeliveryAddressLine2();
				}
				deliveryAddress += "\n" + goodsReceiptNote.getDeliveryAddressZip() + ", " + goodsReceiptNote.getDeliveryAddressCity() + "\n";
				deliveryAddress += goodsReceiptNote.getDeliveryAddressState() + ", " + goodsReceiptNote.getDeliveryAddressCountry();
			}
			summary.setDeliveryAddress(StringUtils.checkString(deliveryAddress).length() > 0 ? deliveryAddress : "N/A");

			// grn items List
			List<GrnItemsSummaryPojo> grnItemList = new ArrayList<GrnItemsSummaryPojo>();
			List<GoodsReceiptNoteItem> grnList = findAllGrnItemByGrnId(goodsReceiptNote.getId());
			if (CollectionUtil.isNotEmpty(grnList)) {
				for (GoodsReceiptNoteItem item : grnList) {
					GrnItemsSummaryPojo grns = new GrnItemsSummaryPojo();
					grns.setSlno(item.getLevel() + "." + item.getOrder());
					grns.setItemName(item.getItemName());
					grns.setCurrency(item.getGoodsReceiptNote().getCurrency().getCurrencyCode());
					grns.setItemDescription(item.getItemDescription());
					grns.setAdditionalTax(df.format(item.getGoodsReceiptNote().getAdditionalTax()));
					grns.setGrandTotal(df.format(item.getGoodsReceiptNote().getGrandTotal()));
					grns.setSumAmount(df.format(goodsReceiptNote.getTotal()));
					grns.setTaxDescription(item.getGoodsReceiptNote().getTaxDescription());
					grns.setDecimal(goodsReceiptNote.getDecimal().toString());
					grnItemList.add(grns);
					if (item.getChildren() != null) {
						for (GoodsReceiptNoteItem childItem : item.getChildren()) {
							GrnItemsSummaryPojo childGrn = new GrnItemsSummaryPojo();
							childGrn.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childGrn.setItemName(childItem.getItemName());
							childGrn.setItemDescription(childItem.getItemDescription());
							childGrn.setQuantity(childItem.getQuantity());
							childGrn.setUnitPrice(df.format(childItem.getUnitPrice()));
							childGrn.setTaxAmount(df.format(childItem.getTaxAmount()));
							childGrn.setTotalAmount(df.format(childItem.getTotalAmount()));
							childGrn.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childGrn.setUom(childItem.getUnit() != null ? (childItem.getUnit().getUom() != null ? childItem.getUnit().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childGrn.setCurrency(childItem.getGoodsReceiptNote().getCurrency().getCurrencyCode());
							childGrn.setAdditionalTax(df.format(childItem.getGoodsReceiptNote().getAdditionalTax()));
							childGrn.setGrandTotal(df.format(childItem.getGoodsReceiptNote().getGrandTotal()));
							childGrn.setSumAmount(df.format(goodsReceiptNote.getTotal()));
							childGrn.setTaxDescription(StringUtils.checkString(childItem.getGoodsReceiptNote().getTaxDescription()).length() > 0 ? childItem.getGoodsReceiptNote().getTaxDescription() : "");
							childGrn.setSumTaxAmount(childItem.getTaxAmount());
							childGrn.setSumTotalAmt(childItem.getTotalAmount());
							childGrn.setDecimal(goodsReceiptNote.getDecimal().toString());
							grnItemList.add(childGrn);
						}
					}

				}
			}
			summary.setGrnItems(grnItemList);
		} catch (Exception e) {
			LOG.error("Could not Get Grn Items and Address " + e.getMessage(), e);
		}

	}

	private List<GoodsReceiptNoteItem> findAllGrnItemByGrnId(String grnId) {
		List<GoodsReceiptNoteItem> returnList = new ArrayList<GoodsReceiptNoteItem>();
		List<GoodsReceiptNoteItem> list = goodsReceiptNoteItemDao.getAllGrnItemByGrnId(grnId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (GoodsReceiptNoteItem item : list) {
				GoodsReceiptNoteItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}

				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (GoodsReceiptNoteItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<GoodsReceiptNoteItem>());
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
	public JasperPrint getGeneratedSupplierGrnPdf(GoodsReceiptNote goodsReceiptNote, String strTimeZone) {
		goodsReceiptNote = goodsReceiptNoteDao.findById(goodsReceiptNote.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (goodsReceiptNote.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (goodsReceiptNote.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (goodsReceiptNote.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (goodsReceiptNote.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (goodsReceiptNote.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (goodsReceiptNote.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/SupplierGrnReport.jasper");
			File jasperfile = resource.getFile();

			GrnSummaryPojo summary = new GrnSummaryPojo();

			summary.setGrnTitle(goodsReceiptNote.getGrnTitle());
			summary.setGrnId(goodsReceiptNote.getGrnId());
			summary.setPaymentNote("Hello");
			if (goodsReceiptNote.getPo() != null) {
				summary.setPoNumber(goodsReceiptNote.getPo().getPoNumber());
			}

			getSummaryOfAddressAndGrnItems(goodsReceiptNote, df, summary);

			BusinessUnit bUnit = goodsReceiptNote.getBusinessUnit();
			String buyerAddress = "";
			Buyer buyer = goodsReceiptNote.getBuyer();

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
				summary.setDisplayName(bUnit.getUnitName());

				if (StringUtils.checkString(goodsReceiptNote.getLine1()).length() > 0) {
					buyerAddress = goodsReceiptNote.getLine1() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine2()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine2() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine3()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine3() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine4()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine4() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine5()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine5() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine6()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine6() + "\r\n";
				}
				if (StringUtils.checkString(goodsReceiptNote.getLine7()).length() > 0) {
					buyerAddress += goodsReceiptNote.getLine7() + "\r\n";
				}
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
				LOG.info("buyerAddress : " + buyerAddress);
			}

			summary.setBuyerAddress(StringUtils.checkString(buyerAddress).length() > 0 ? buyerAddress : "N/A");

			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());

			ImageIcon n1 = null;
			if (buyerSettings.getFileAttatchment() != null) {
				n1 = new ImageIcon(buyerSettings.getFileAttatchment());
				summary.setComanyName(null);
				summary.setLogo(n1.getImage());
			}

			List<GrnSummaryPojo> grnSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (goodsReceiptNote.getSupplier() != null) {
				Supplier supplier = goodsReceiptNote.getSupplier();
				supplierAddress += supplier.getCompanyName() + "\r\n";
				supplierAddress += supplier.getLine1();
				if (StringUtils.checkString(goodsReceiptNote.getSupplier().getLine2()).length() > 0) {
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
				supplierAddress += goodsReceiptNote.getSupplierName() + "\r\n";
				supplierAddress += goodsReceiptNote.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (goodsReceiptNote.getSupplierTelNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (goodsReceiptNote.getSupplierFaxNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierFaxNumber() + "\r\n\n";
				}

				supplierAddress += "Tax Reg No : ";
				if (goodsReceiptNote.getSupplierTaxNumber() != null) {
					supplierAddress += goodsReceiptNote.getSupplierTaxNumber();
				}
			}

			if (goodsReceiptNote.getSupplier() != null) {
				summary.setSupplierName(goodsReceiptNote.getSupplier() != null ? goodsReceiptNote.getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(goodsReceiptNote.getSupplierName());
			}
			summary.setSupplierAddress(StringUtils.checkString(supplierAddress).length() > 0 ? supplierAddress : "N/A");
			summary.setTaxnumber(goodsReceiptNote.getSupplierTaxNumber() != null ? goodsReceiptNote.getSupplierTaxNumber() : "");

			parameters.put("GRN_SUMMARY", grnSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(grnSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Buyer GRN Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	@Transactional(readOnly = false)
	public GoodsReceiptNote createGrnFromPo(User loggedInUser, Po po) throws ApplicationException {
		GoodsReceiptNote goodsReceiptNote = null;
		if (po != null) {
			BigDecimal total = BigDecimal.ZERO;
			goodsReceiptNote = new GoodsReceiptNote();
			goodsReceiptNote.setGrnId(eventIdSettingsService.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "GRN", po.getBusinessUnit()));
			goodsReceiptNote.setPo(po);
			goodsReceiptNote.setGrnTitle(po.getName());
			goodsReceiptNote.setReferenceNumber(po.getReferenceNumber());
			goodsReceiptNote.setDescription(po.getDescription());
			goodsReceiptNote.setBuyer(loggedInUser.getBuyer());
			goodsReceiptNote.setCurrency(po.getCurrency());
			goodsReceiptNote.setDecimal(Integer.parseInt(po.getDecimal()));
			goodsReceiptNote.setCostCenter(po.getCostCenter());
			goodsReceiptNote.setBusinessUnit(po.getBusinessUnit());

			goodsReceiptNote.setLine1(po.getLine1());
			goodsReceiptNote.setLine2(po.getLine2());
			goodsReceiptNote.setLine3(po.getLine3());
			goodsReceiptNote.setLine4(po.getLine4());
			goodsReceiptNote.setLine5(po.getLine5());
			goodsReceiptNote.setLine6(po.getLine6());
			goodsReceiptNote.setLine7(po.getLine7());

			if (po.getSupplier() != null) {
				Supplier sup = favoriteSupplierService.getSupplierByFavSupplierId(po.getSupplier().getId());
				goodsReceiptNote.setSupplier(sup);
			}
			goodsReceiptNote.setSupplierName(po.getSupplierName());
			goodsReceiptNote.setSupplierAddress(po.getSupplierAddress());
			goodsReceiptNote.setSupplierTelNumber(po.getSupplierTelNumber());
			goodsReceiptNote.setSupplierFaxNumber(po.getSupplierFaxNumber());
			goodsReceiptNote.setSupplierTaxNumber(po.getSupplierTaxNumber());

			goodsReceiptNote.setRemarks(po.getRemarks());
			goodsReceiptNote.setTermsAndConditions(po.getTermsAndConditions());

			goodsReceiptNote.setField1Label(po.getField1Label());
			goodsReceiptNote.setField2Label(po.getField2Label());
			goodsReceiptNote.setField3Label(po.getField3Label());
			goodsReceiptNote.setField4Label(po.getField4Label());
			goodsReceiptNote.setField5Label(po.getField5Label());
			goodsReceiptNote.setField6Label(po.getField6Label());
			goodsReceiptNote.setField7Label(po.getField7Label());
			goodsReceiptNote.setField8Label(po.getField8Label());
			goodsReceiptNote.setField9Label(po.getField9Label());
			goodsReceiptNote.setField10Label(po.getField10Label());

			goodsReceiptNote.setStatus(GrnStatus.DRAFT);
			goodsReceiptNote.setCreatedDate(new Date());
			goodsReceiptNote.setCreatedBy(loggedInUser);

			goodsReceiptNote.setDeliveryAddressTitle(po.getDeliveryAddressTitle());
			goodsReceiptNote.setDeliveryAddressLine1(po.getDeliveryAddressLine1());
			goodsReceiptNote.setDeliveryAddressLine2(po.getDeliveryAddressLine2());
			goodsReceiptNote.setDeliveryAddressCity(po.getDeliveryAddressCity());
			goodsReceiptNote.setDeliveryAddressState(po.getDeliveryAddressState());
			goodsReceiptNote.setDeliveryAddressZip(po.getDeliveryAddressZip());
			goodsReceiptNote.setDeliveryAddressCountry(po.getDeliveryAddressCountry());

			List<PoItem> itemList = poItemDao.getAllPoItemByPoId(po.getId());
			List<GoodsReceiptNoteItem> grnItems = new ArrayList<GoodsReceiptNoteItem>();
			if (CollectionUtil.isNotEmpty(itemList)) {
				for (PoItem prItem : itemList) {
					if (prItem.getParent() == null) {
						GoodsReceiptNoteItem parent = new GoodsReceiptNoteItem();
						parent.setItemName(prItem.getItemName());
						parent.setLevel(prItem.getLevel());
						parent.setOrder(prItem.getOrder());
						parent.setBuyer(prItem.getBuyer());
						parent.setGoodsReceiptNote(goodsReceiptNote);
						parent.setItemDescription(prItem.getItemDescription());
						List<GoodsReceiptNoteItem> childrenList = new ArrayList<GoodsReceiptNoteItem>();
						if (CollectionUtil.isNotEmpty(prItem.getChildren())) {
							for (PoItem itemPojo : prItem.getChildren()) {
								LOG.info("Children not empty > " + prItem.getChildren().size());
								GoodsReceiptNoteItem item = new GoodsReceiptNoteItem();
								if (StringUtils.checkString(itemPojo.getItemTax()).length() > 0) {
									item.setItemTax(new BigDecimal(itemPojo.getItemTax()).setScale(Integer.parseInt(po.getDecimal()), RoundingMode.HALF_DOWN));
								}
								item.setLevel(itemPojo.getLevel());
								item.setOrder(itemPojo.getOrder());
								item.setParent(parent);
								item.setGoodsReceiptNote(goodsReceiptNote);
								item.setQuantity(itemPojo.getQuantity());
								item.setUnit(itemPojo.getUnit());
								item.setUnitPrice(itemPojo.getUnitPrice());

								BigDecimal sumofReceivedQuantity = goodsReceiptNoteItemDao.findReceivedQuantitiesByPoAndPoItemId(po.getId(), itemPojo.getId());
								item.setReceivedQuantity(sumofReceivedQuantity != null ? item.getQuantity().subtract(sumofReceivedQuantity) : item.getQuantity());
								// Strip additional decimals from unit price and quantity
								BigDecimal totalAmount = (item.getUnitPrice() != null && item.getReceivedQuantity() != null) ? item.getUnitPrice().setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN).multiply(item.getReceivedQuantity().setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN)) : new BigDecimal(0);
								// round the totalAmount based on decimal
								item.setTotalAmount(totalAmount.setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP));
								if (item.getItemTax() != null) {
									BigDecimal taxAmount = item.getTotalAmount().multiply(item.getItemTax()).divide(new BigDecimal(100), goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP).setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP);
									item.setTaxAmount(taxAmount);
								}
								BigDecimal totalAmountWithTax = item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0);
								item.setTotalAmountWithTax(totalAmountWithTax);

								total = total.add(item.getTotalAmountWithTax());

								item.setBusinessUnit(po.getBusinessUnit());
								item.setBuyer(itemPojo.getBuyer());
								item.setCostCenter(po.getCostCenter());
								if (po.getDeliveryAddress() != null) {
									item.setDeliveryAddressTitle(StringUtils.checkString(po.getDeliveryAddress().getTitle()));
									item.setDeliveryAddressLine1(StringUtils.checkString(po.getDeliveryAddress().getLine1()));
									item.setDeliveryAddressLine2(StringUtils.checkString(po.getDeliveryAddress().getLine2()));
									item.setDeliveryAddressZip(StringUtils.checkString(po.getDeliveryAddress().getZip()));
									item.setDeliveryAddressCity(StringUtils.checkString(po.getDeliveryAddress().getCity()));
									item.setDeliveryAddressState(StringUtils.checkString(po.getDeliveryAddress().getState().getStateName()));
									item.setDeliveryAddressCountry(StringUtils.checkString(po.getDeliveryAddress().getState().getCountry().getCountryName()));
								}
								item.setItemDescription(itemPojo.getItemDescription());
								item.setItemName(itemPojo.getItemName());
								if (itemPojo.getPo() != null) {
									item.setPo(itemPojo.getPo());
									item.setPoItem(itemPojo);
								}
								childrenList.add(item);
							}
							parent.setChildren(childrenList);
							grnItems.add(parent);
						}
					}

				}
				goodsReceiptNote.setTotal(total);
				goodsReceiptNote.setTaxDescription(po.getTaxDescription());
				goodsReceiptNote.setAdditionalTax(po.getAdditionalTax());
				goodsReceiptNote.setGrandTotal(total);

			} else {
				throw new ApplicationException("Missing PO Items");
			}
			if (CollectionUtil.isNotEmpty(grnItems)) {
				goodsReceiptNote.setGoodsReceiptNoteItems(grnItems);
				goodsReceiptNote = goodsReceiptNoteDao.save(goodsReceiptNote);

				// Update GRN count in PO
				po.setGrnCount(((int) goodsReceiptNoteDao.findTotalGrnForPo(po.getId())));
				poDao.update(po);

				try {
					GoodsReceiptNoteAudit audit = new GoodsReceiptNoteAudit();
					audit.setAction(GrnAuditType.CREATE);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					audit.setBuyer(loggedInUser.getBuyer());
					if (goodsReceiptNote.getSupplier() != null) {
						audit.setSupplier(goodsReceiptNote.getSupplier());
					}
					audit.setVisibilityType(GrnAuditVisibilityType.BUYER);
					audit.setDescription(messageSource.getMessage("grn.audit.create", new Object[] { goodsReceiptNote.getGrnId(), po.getPoNumber() }, Global.LOCALE));
					audit.setGoodsReceiptNote(goodsReceiptNote);
					goodsReceiptNoteAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving GRN Create Audit " + e.getMessage(), e);
				}
				
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "GRN '" + goodsReceiptNote.getGrnId() + "' created from '"+po.getPoNumber()+"' ", goodsReceiptNote.getCreatedBy().getTenantId(), goodsReceiptNote.getCreatedBy(), new Date(), ModuleType.GRN);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

			}
		}
		return goodsReceiptNote;
	}

	@Override
	@Transactional(readOnly = false)
	public GoodsReceiptNote sendGrn(String grnId, User loggedInUser) {

		GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteDao.findByGrnId(grnId);

		if (goodsReceiptNote != null) {
			goodsReceiptNote.setStatus(GrnStatus.DELIVERED);
			goodsReceiptNoteDao.update(goodsReceiptNote);

			try {
				GoodsReceiptNoteAudit audit = new GoodsReceiptNoteAudit();
				audit.setAction(GrnAuditType.DELIVERED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setBuyer(goodsReceiptNote.getBuyer());
				if (goodsReceiptNote.getSupplier() != null) {
					audit.setSupplier(goodsReceiptNote.getSupplier());
				}
				audit.setDescription(messageSource.getMessage("grn.finish.notification.message", new Object[] {}, Global.LOCALE));
				audit.setGoodsReceiptNote(goodsReceiptNote);
				audit.setVisibilityType(GrnAuditVisibilityType.BOTH);
				goodsReceiptNoteAuditDao.save(audit);
			} catch (Exception e) {
				LOG.error("Error while Finihing Delivery order : " + e.getMessage(), e);
			}
		}
		return goodsReceiptNote;
	}

	@Override
	@Transactional(readOnly = false)
	public GoodsReceiptNote updateGrnDetails(List<GoodsReceiptNoteItem> itemList, String grnName, String referenceNumber, User loggedInUser, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, GrnStatus status, HttpSession session, String deliveryReceiver, Date receiptDate) {
		// List<String> itemIds = new ArrayList<String>();
		BigDecimal total = BigDecimal.ZERO;
		GoodsReceiptNote goodsReceiptNote = null;
		goodsReceiptNote = itemList.get(0).getGoodsReceiptNote();
		if (CollectionUtil.isNotEmpty(itemList)) {
			List<String> ids = new ArrayList<String>();
			for (GoodsReceiptNoteItem item : itemList) {
				if (item.getParent() != null) {
					if (!ids.contains(item.getParent().getId())) {
						ids.add(item.getParent().getId());
					}
				}
				ids.add(item.getId());
			}
			LOG.info("List Of Ids : " + String.join(",", ids));
			goodsReceiptNoteItemDao.deleteItems(ids, goodsReceiptNote.getId());

			for (GoodsReceiptNoteItem item : itemList) {
				// Strip additional decimals from unit price and quantity
				BigDecimal totalAmount = (item.getUnitPrice() != null && item.getReceivedQuantity() != null) ? item.getUnitPrice().setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN).multiply(item.getReceivedQuantity().setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN)) : new BigDecimal(0);
				// round the totalAmount based on decimal
				item.setTotalAmount(totalAmount.setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP));
				if (item.getItemTax() != null) {
					BigDecimal taxAmount = item.getTotalAmount().multiply(item.getItemTax()).divide(new BigDecimal(100), goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP).setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_HALF_UP);
					item.setTaxAmount(taxAmount);
				}
				BigDecimal totalAmountWithTax = item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0);
				item.setTotalAmountWithTax(totalAmountWithTax);

				total = total.add(item.getTotalAmountWithTax());
				goodsReceiptNoteItemDao.saveOrUpdate(item);
			}
			if (receiptDate != null) {
				goodsReceiptNote.setGoodsReceiptDate(receiptDate);
			}
			goodsReceiptNote.setModifiedBy(loggedInUser);
			goodsReceiptNote.setModifiedDate(new Date());
			goodsReceiptNote.setGrandTotal(total);
			goodsReceiptNote.setTotal(total);
			goodsReceiptNote.setStatus(status);
			goodsReceiptNote.setReferenceNumber(StringUtils.checkString(referenceNumber));
			goodsReceiptNote.setGrnTitle(StringUtils.checkString(grnName));
			goodsReceiptNote.setDeliveryAddressTitle(StringUtils.checkString(deliveryAddressTitle));
			goodsReceiptNote.setDeliveryAddressLine1(StringUtils.checkString(deliveryAddressLine1));
			goodsReceiptNote.setDeliveryAddressLine2(StringUtils.checkString(deliveryAddressLine2));
			goodsReceiptNote.setDeliveryAddressZip(StringUtils.checkString(deliveryAddressZip));
			goodsReceiptNote.setDeliveryAddressCity(StringUtils.checkString(deliveryAddressCity));
			goodsReceiptNote.setDeliveryAddressState(StringUtils.checkString(deliveryAddressState));
			goodsReceiptNote.setDeliveryAddressCountry(StringUtils.checkString(deliveryAddressCountry));
			goodsReceiptNote.setDeliveryReceiver(StringUtils.checkString(deliveryReceiver));
			if (status == GrnStatus.RECEIVED) {
				goodsReceiptNote.setGrnReceivedDate(new Date());
				goodsReceiptNote.setReceivedBy(loggedInUser);
			}
			goodsReceiptNote = goodsReceiptNoteDao.saveOrUpdate(goodsReceiptNote);
			if (status == GrnStatus.RECEIVED) {
				try {
					GoodsReceiptNoteAudit buyerAudit = new GoodsReceiptNoteAudit();
					buyerAudit.setActionBy(loggedInUser);
					buyerAudit.setAction(GrnAuditType.RECEIVED);
					buyerAudit.setActionDate(new Date());
					buyerAudit.setBuyer(goodsReceiptNote.getBuyer());
					buyerAudit.setSupplier(goodsReceiptNote.getSupplier());
					buyerAudit.setDescription(messageSource.getMessage("grn.received.audit.message", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
					buyerAudit.setVisibilityType(GrnAuditVisibilityType.BUYER);
					buyerAudit.setGoodsReceiptNote(goodsReceiptNote);
					goodsReceiptNoteAuditDao.save(buyerAudit);

				} catch (Exception e) {
					LOG.error("Error while Finihing Delivery order : " + e.getMessage(), e);
				}
				
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RECEIVED, " '" +goodsReceiptNote.getGrnId()+ "' is received", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.GRN);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}

			}

		}
		return goodsReceiptNote;
	}

	@Override
	public List<GoodsReceiptNotePojo> getGrnListByPoIdForBuyer(String poId) {
		return goodsReceiptNoteDao.getGrnListByPoIdForBuyer(poId);
	}

	@Override
	public long findTotalGrnForPo(String poId) {
		return goodsReceiptNoteDao.findTotalGrnForPo(poId);
	}

	@Override
	public long findTotalDraftOrReceivedGrnForPo(String poId) {
		return goodsReceiptNoteDao.findTotalDraftOrReceivedGrnForPo(poId);

	}

	@Override
	public void downloadCsvFileForGrnList(HttpServletResponse response, File file, String tenantId, GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, String[] grnIds, DateFormat formatter) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.ALL_BUYER_GRN_REPORT_EXCEL_COLUMNS;

			final String[] columns = new String[] { "grnId", "grnTitle", "referenceNumber", "poNumber", "supplierCompanyName", "businessUnit", "createdBy", "createDate", "receiptDate", "grnDate", "currency", "grandTotal", "status" };

			long count = goodsReceiptNoteDao.findTotalGrnCountByTenantIdForBuyer(tenantId);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<GoodsReceiptNotePojo> list = goodsReceiptNoteDao.getAllBuyerGrnDetailsForExcelReport(tenantId, grnIds, goodsReceiptNotePojo, select_all, startDate, endDate, null);
				for (GoodsReceiptNotePojo pojo : list) {
					pojo.setCreateDate(formatter.format(pojo.getCreatedDate()));
					if (pojo.getGrnReceivedDate() != null) {
						pojo.setGrnDate(formatter.format(pojo.getGrnReceivedDate()));
					}
					if (pojo.getGoodsReceiptDate() != null) {
						pojo.setReceiptDate(formatter.format(pojo.getGoodsReceiptDate()));
					}
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}

	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // GRN Number
				new Optional(), // GRN Title
				new Optional(), // Reference Number
				new NotNull(), // PO Number
				new NotNull(), // Supplier
				new Optional(), // Business Unit
				new NotNull(), // Created By
				new NotNull(), // Created Date
				new Optional(), // Goods Receipt Date
				new Optional(), // GRN DATE
				new Optional(), // Currency
				new NotNull(), // GRAND TOTAL
				new NotNull() // STATUS
		};
		return processors;
	}

	@Override
	public long findTotalDraftOrReceivedGrnForPoByPoId(String poId) {
		return goodsReceiptNoteDao.findTotalDraftOrReceivedGrnForPoByPoId(poId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		//grns for supplier
		return goodsReceiptNoteDao.findAllSearchFilterGrnsSupp(tenantId, input, startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public long findTotalSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		LOG.info(">>>>>>>>>>>>> findTotalSearchFilterGrnsSupp");
		return goodsReceiptNoteDao.findTotalSearchFilterGrnsSupp(tenantId, input, startDate, endDate);

	}
}