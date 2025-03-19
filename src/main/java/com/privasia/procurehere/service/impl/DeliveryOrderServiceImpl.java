package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.DeliveryOrderAuditDao;
import com.privasia.procurehere.core.dao.DeliveryOrderDao;
import com.privasia.procurehere.core.dao.DeliveryOrderItemDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DeliveryOrderAuditType;
import com.privasia.procurehere.core.enums.DoAuditVisibilityType;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.ErpDoItemsPojo;
import com.privasia.procurehere.core.pojo.ErpDoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.DeliveryOrderService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.controller.DeliveryItemsSummaryPojo;
import com.privasia.procurehere.web.controller.DeliveryOrderSummaryPojo;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	DeliveryOrderDao deliveryOrderDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	PoDao poDao;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	DeliveryOrderAuditDao deliveryOrderAuditDao;

	@Autowired
	DeliveryOrderItemDao deliveryOrderItemDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	UserService userService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	UomService uomService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<DoSupplierPojo> findAllSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return deliveryOrderDao.findAllSearchFilterDoForSupplier(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return deliveryOrderDao.findTotalSearchFilterDoForSupplier(tenantId, input, startDate, endDate);

	}

	@Override
	public long findTotalDoForSupplier(String tenantId) {
		return deliveryOrderDao.findTotalDoForSupplier(tenantId);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public DeliveryOrder createDo(User loggedInUser, Po po) throws ApplicationException, EmailException {
		DeliveryOrder deliveryOrder = null;
		if (po != null) {
			po = poDao.findById(po.getId());

			deliveryOrder = new DeliveryOrder();
			deliveryOrder.setDeliveryId(eventIdSettingsService.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "DO", null));
			deliveryOrder.setBuyer(po.getBuyer());
			deliveryOrder.setBusinessUnit(po.getBusinessUnit());

			if (StringUtils.checkString(po.getLine1()).length() > 0) {
				deliveryOrder.setLine1(po.getLine1());
				deliveryOrder.setLine2(po.getLine2());
				deliveryOrder.setLine3(po.getLine3());
				deliveryOrder.setLine4(po.getLine4());
				deliveryOrder.setLine5(po.getLine5());
				deliveryOrder.setLine6(po.getLine6());
				deliveryOrder.setLine7(po.getLine7());

			} else {
				deliveryOrder.setLine1(StringUtils.checkString(po.getLine1()).length() > 0 ? po.getLine1() : po.getBusinessUnit().getLine1());
				deliveryOrder.setLine2(StringUtils.checkString(po.getLine2()).length() > 0 ? po.getLine2() : po.getBusinessUnit().getLine2());
				deliveryOrder.setLine3(StringUtils.checkString(po.getLine3()).length() > 0 ? po.getLine3() : po.getBusinessUnit().getLine3());
				deliveryOrder.setLine4(StringUtils.checkString(po.getLine4()).length() > 0 ? po.getLine4() : po.getBusinessUnit().getLine4());
				deliveryOrder.setLine5(StringUtils.checkString(po.getLine5()).length() > 0 ? po.getLine5() : po.getBusinessUnit().getLine5());
				deliveryOrder.setLine6(StringUtils.checkString(po.getLine6()).length() > 0 ? po.getLine6() : po.getBusinessUnit().getLine6());
				deliveryOrder.setLine7(StringUtils.checkString(po.getLine7()).length() > 0 ? po.getLine7() : po.getBusinessUnit().getLine7());
			}

			deliveryOrder.setCorrespondenceAddress(po.getCorrespondenceAddress());
			if (po.getCorrespondenceAddress() != null) {
				deliveryOrder.setCorrespondAddressTitle(StringUtils.checkString(po.getCorrespondenceAddress().getTitle()));
				deliveryOrder.setCorrespondAddressLine1(StringUtils.checkString(po.getCorrespondenceAddress().getLine1()));
				deliveryOrder.setCorrespondAddressLine2(StringUtils.checkString(po.getCorrespondenceAddress().getLine2()));
				deliveryOrder.setCorrespondAddressZip(StringUtils.checkString(po.getCorrespondenceAddress().getZip()));
				deliveryOrder.setCorrespondAddressCity(StringUtils.checkString(po.getCorrespondenceAddress().getCity()));
				deliveryOrder.setCorrespondAddressState(StringUtils.checkString(po.getCorrespondenceAddress().getState().getStateName()));
				deliveryOrder.setCorrespondAddressCountry(StringUtils.checkString(po.getCorrespondenceAddress().getState().getCountry().getCountryName()));
			}
			if (po.getDeliveryAddress() != null) {
				deliveryOrder.setDeliveryAddressTitle(StringUtils.checkString(po.getDeliveryAddress().getTitle()));
				deliveryOrder.setDeliveryAddressLine1(StringUtils.checkString(po.getDeliveryAddress().getLine1()));
				deliveryOrder.setDeliveryAddressLine2(StringUtils.checkString(po.getDeliveryAddress().getLine2()));
				deliveryOrder.setDeliveryAddressZip(StringUtils.checkString(po.getDeliveryAddress().getZip()));
				deliveryOrder.setDeliveryAddressCity(StringUtils.checkString(po.getDeliveryAddress().getCity()));
				deliveryOrder.setDeliveryAddressState(StringUtils.checkString(po.getDeliveryAddress().getState().getStateName()));
				deliveryOrder.setDeliveryAddressCountry(StringUtils.checkString(po.getDeliveryAddress().getState().getCountry().getCountryName()));
			}
			deliveryOrder.setCostCenter(po.getCostCenter());
			deliveryOrder.setCreatedBy(loggedInUser);
			deliveryOrder.setCreatedDate(new Date());
			deliveryOrder.setCurrency(po.getCurrency());
			deliveryOrder.setDecimal(po.getDecimal());
			deliveryOrder.setDeliveryAddress(po.getDeliveryAddress());
			deliveryOrder.setDeliveryDate(po.getDeliveryDate());
			deliveryOrder.setDeliveryReceiver(po.getDeliveryReceiver());
			deliveryOrder.setDescription(po.getDescription());
			deliveryOrder.setGrandTotal(po.getGrandTotal());
			deliveryOrder.setName(po.getName());
			// deliveryOrder.setPaymentTerm(po.getPaymentTerm());
			deliveryOrder.setReferenceNumber(po.getReferenceNumber());
			deliveryOrder.setRemarks(po.getRemarks());
			deliveryOrder.setRequester(po.getRequester());
			deliveryOrder.setStatus(DoStatus.DRAFT);

			deliveryOrder.setSupplierName(po.getSupplierName());
			deliveryOrder.setSupplierAddress(po.getSupplierAddress());
			deliveryOrder.setSupplierTelNumber(po.getSupplierTelNumber());
			deliveryOrder.setSupplierAddress(po.getSupplierAddress());
			deliveryOrder.setSupplierTaxNumber(po.getSupplierTaxNumber());
			deliveryOrder.setSupplierFaxNumber(po.getSupplierFaxNumber());

			if (po.getSupplier() != null) {
				if (po.getSupplier().getSupplier() != null) {
					deliveryOrder.setSupplier(po.getSupplier().getSupplier());
					po.getSupplier().getSupplier().getCompanyName();
				}
			}

			deliveryOrder.setTaxDescription(po.getTaxDescription());
			deliveryOrder.setTermsAndConditions(po.getTermsAndConditions());
			deliveryOrder.setTotal(po.getTotal());
			if (po.getAdditionalTax() != null) {
				deliveryOrder.setAdditionalTax(po.getAdditionalTax());
			}
			deliveryOrder.setGrandTotal(po.getGrandTotal());
			deliveryOrder.setPo(po);

			List<DeliveryOrderItem> deliveryOrderItems = new ArrayList<DeliveryOrderItem>();
			List<PoItem> itemList = poItemDao.getAllPoItemByPoId(po.getId());
			if (CollectionUtil.isNotEmpty(itemList)) {
				for (PoItem prItem : itemList) {
					if (prItem.getParent() == null) {
						// LOG.info("pr parent");
						DeliveryOrderItem parent = new DeliveryOrderItem();
						parent.setItemName(prItem.getItemName());
						parent.setLevel(prItem.getLevel());
						parent.setOrder(prItem.getOrder());
						parent.setDeliveryReceiver(po.getDeliveryReceiver());
						parent.setBuyer(prItem.getBuyer());
						parent.setDeliverOrder(deliveryOrder);
						parent.setItemDescription(prItem.getItemDescription());
						List<DeliveryOrderItem> childrenList = new ArrayList<DeliveryOrderItem>();
						if (CollectionUtil.isNotEmpty(prItem.getChildren())) {
							for (PoItem itemPojo : prItem.getChildren()) {
								LOG.info("Children not empty > " + prItem.getChildren().size());
								DeliveryOrderItem item = new DeliveryOrderItem();
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
								item.setItemTax(new BigDecimal(itemPojo.getItemTax()).setScale(Integer.parseInt(po.getDecimal()), RoundingMode.HALF_DOWN));
								item.setLevel(itemPojo.getLevel());
								item.setOrder(itemPojo.getOrder());
								item.setParent(parent);
								item.setDeliverOrder(deliveryOrder);
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
							deliveryOrderItems.add(parent);
						}
					}

				}
			} else {
				throw new ApplicationException("Missing PO Items");
			}

			if (CollectionUtil.isNotEmpty(deliveryOrderItems)) {
				deliveryOrder.setDeliveryOrderItems(deliveryOrderItems);

				deliveryOrder = deliveryOrderDao.saveOrUpdate(deliveryOrder);

				// Update DO count in PO
				po.setDoCount((int) deliveryOrderDao.findTotalDoForPo(po.getId()));
				poDao.update(po);

				DeliveryOrderAudit audit = new DeliveryOrderAudit();
				audit.setAction(DeliveryOrderAuditType.CREATE);
				audit.setActionBy(loggedInUser);
				audit.setActionDate(new Date());
				audit.setBuyer(po.getBuyer());
				Supplier supplier = new Supplier();
				supplier.setId(loggedInUser.getTenantId());
				audit.setSupplier(supplier);
				audit.setVisibilityType(DoAuditVisibilityType.SUPPLIER);
				audit.setDescription(messageSource.getMessage("do.audit.ready", new Object[] { po.getPoNumber() }, Global.LOCALE));
				audit.setDeliveryOrder(deliveryOrder);
				deliveryOrderAuditDao.save(audit);

			} else {
				LOG.warn("NO Items found ..");
			}

		}
		return deliveryOrder;
	}

	@Override
	public DeliveryOrder getDoByIdForSupplierView(String doId) {
		DeliveryOrder deliveryOrder = deliveryOrderDao.findByDoId(doId);
		if (deliveryOrder != null) {
			if (deliveryOrder.getBuyer() != null) {
				deliveryOrder.getBuyer().getCompanyName();
			}
			if (deliveryOrder.getSupplier() != null) {
				deliveryOrder.getSupplier().getCompanyName();
				deliveryOrder.getSupplier().getFaxNumber();
			}
			if (deliveryOrder.getCreatedBy() != null) {
				deliveryOrder.getCreatedBy().getName();
				deliveryOrder.getCreatedBy().getCommunicationEmail();
				deliveryOrder.getCreatedBy().getPhoneNumber();
			}
			if (deliveryOrder.getPo() != null) {
				deliveryOrder.getPo().getPoNumber();
			}
			if (deliveryOrder.getCurrency() != null) {
				deliveryOrder.getCurrency().getCurrencyCode();
			}
			if (deliveryOrder.getBusinessUnit() != null) {
				deliveryOrder.getBusinessUnit().getDisplayName();
			}

		}
		return deliveryOrder;
	}

	@Override
	public List<DeliveryOrderItem> findAllDoItemByDoIdForSummary(String doId) {
		return deliveryOrderItemDao.getAllDoItemByDoId(doId);
	}

	@Override
	public List<DoSupplierPojo> findAllSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return deliveryOrderDao.findAllSearchFilterDoForBuyer(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return deliveryOrderDao.findTotalSearchFilterDoForBuyer(tenantId, input, startDate, endDate);

	}

	@Override
	public long findTotalDoForBuyer(String tenantId) {
		return deliveryOrderDao.findTotalDoForBuyer(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public DeliveryOrder finishDeliverOrder(String doId, User loggedInUser) {

		DeliveryOrder deliveryOrder = deliveryOrderDao.findByDoId(doId);

		deliveryOrder.setStatus(DoStatus.DELIVERED);
		deliveryOrderDao.update(deliveryOrder);

		try {
			DeliveryOrderAudit audit = new DeliveryOrderAudit();
			audit.setAction(DeliveryOrderAuditType.DELIVERED);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(deliveryOrder.getBuyer());
			Supplier supplier = new Supplier();
			supplier.setId(loggedInUser.getTenantId());
			audit.setSupplier(supplier);
			audit.setDescription(messageSource.getMessage("do.finish.notification.message", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
			audit.setDeliveryOrder(deliveryOrder);
			audit.setVisibilityType(DoAuditVisibilityType.BOTH);
			deliveryOrderAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Finihing Delivery order : " + e.getMessage(), e);
		}

		return deliveryOrder;
	}

	@Override
	@Transactional(readOnly = false)
	public DeliveryOrder declineOrder(String doId, User loggedInUser, String buyerRemark) throws EmailException {

		DeliveryOrder deliveryOrder = deliveryOrderDao.findByDoId(doId);

		if (deliveryOrder != null) {
			deliveryOrder.setStatus(DoStatus.DECLINED);
			deliveryOrder.setActionBy(loggedInUser);
			deliveryOrder.setActionDate(new Date());
			deliveryOrderDao.update(deliveryOrder);

			DeliveryOrderAudit buyerAudit = new DeliveryOrderAudit();
			buyerAudit.setAction(DeliveryOrderAuditType.DECLINED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(deliveryOrder.getBuyer());
			buyerAudit.setSupplier(deliveryOrder.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("do.declined", new Object[] { buyerRemark }, Global.LOCALE));
			buyerAudit.setVisibilityType(DoAuditVisibilityType.BOTH);
			buyerAudit.setDeliveryOrder(deliveryOrder);
			deliveryOrderAuditDao.save(buyerAudit);
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DECLINED, "DO '"+deliveryOrder.getDeliveryId()+"' is declined", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.DO);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			try {
				sendEmailNotificationToSupplier(deliveryOrder, loggedInUser, false, buyerRemark);
			} catch (Exception e) {
				LOG.error("Error while sending do decline mail to supplier " + deliveryOrder.getSupplierName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending do decline mail to supplier");
			}

		}

		return deliveryOrder;
	}

	@Override
	@Transactional(readOnly = false)
	public DeliveryOrder acceptOrder(String doId, User loggedInUser, String buyerRemark) throws EmailException {

		DeliveryOrder deliveryOrder = deliveryOrderDao.findByDoId(doId);

		if (deliveryOrder != null) {
			deliveryOrder.setStatus(DoStatus.ACCEPTED);
			deliveryOrder.setActionBy(loggedInUser);
			deliveryOrder.setActionDate(new Date());
			deliveryOrderDao.update(deliveryOrder);

			DeliveryOrderAudit buyerAudit = new DeliveryOrderAudit();
			buyerAudit.setAction(DeliveryOrderAuditType.ACCEPTED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(deliveryOrder.getBuyer());
			buyerAudit.setSupplier(deliveryOrder.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("do.buyerAudit.accepted", new Object[] { buyerRemark }, Global.LOCALE));
			buyerAudit.setVisibilityType(DoAuditVisibilityType.BOTH);
			buyerAudit.setDeliveryOrder(deliveryOrder);
			deliveryOrderAuditDao.save(buyerAudit);
			
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "DO '" + deliveryOrder.getDeliveryId() + "' Accepted", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.DO);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			try {
				sendEmailNotificationToSupplier(deliveryOrder, loggedInUser, true, buyerRemark);

			} catch (Exception e) {
				LOG.error("Error while sending do accept mail to supplier " + deliveryOrder.getSupplierName() + " :  " + e.getMessage(), e);
				throw new EmailException("Error while sending do accept mail to supplier");
			}

		}

		return deliveryOrder;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public DeliveryOrder cancelDo(String doId, User loggedInUser, String supplierRemark) throws EmailException {

		DeliveryOrder deliveryOrder = deliveryOrderDao.findByDoId(doId);

		if (deliveryOrder != null) {
			DoStatus status = deliveryOrder.getStatus();
			deliveryOrder.setStatus(DoStatus.CANCELLED);
			deliveryOrderDao.update(deliveryOrder);

			Po po = deliveryOrder.getPo();
			if (po.getDoCancelCount() == null) {
				po.setDoCancelCount(1);
			} else {
				po.setDoCancelCount(po.getDoCancelCount() + 1);
			}
			poDao.saveOrUpdate(po);

			DeliveryOrderAudit buyerAudit = new DeliveryOrderAudit();
			buyerAudit.setAction(DeliveryOrderAuditType.CANCELLED);
			buyerAudit.setActionBy(loggedInUser);
			buyerAudit.setActionDate(new Date());
			buyerAudit.setBuyer(deliveryOrder.getBuyer());
			buyerAudit.setSupplier(deliveryOrder.getSupplier());
			buyerAudit.setDescription(messageSource.getMessage("do.audit.cancel", new Object[] { supplierRemark }, Global.LOCALE));
			if (DoStatus.DELIVERED == status) {
				buyerAudit.setVisibilityType(DoAuditVisibilityType.BOTH);
			} else {
				buyerAudit.setVisibilityType(DoAuditVisibilityType.SUPPLIER);
			}
			buyerAudit.setDeliveryOrder(deliveryOrder);
			deliveryOrderAuditDao.save(buyerAudit);
			// if (DoStatus.DELIVERED == status) {
			// try {
			// sendEmailNotificationToBuyer(deliveryOrder, loggedInUser, false, supplierRemark);
			// } catch (Exception e) {
			// throw new EmailException("Error while sending cancel email to buyer");
			// }
			// }
		}

		return deliveryOrder;
	}

	@Override
	public DeliveryOrder findByDoId(String doId) {
		return deliveryOrderDao.findById(doId);
	}

	@Override
	public JasperPrint getGeneratedBuyerDoPdf(DeliveryOrder deliveryOrder, String strTimeZone) {
		deliveryOrder = deliveryOrderDao.findById(deliveryOrder.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (deliveryOrder.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (deliveryOrder.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (deliveryOrder.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (deliveryOrder.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (deliveryOrder.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (deliveryOrder.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/BuyerDoReport.jasper");
			File jasperfile = resource.getFile();

			DeliveryOrderSummaryPojo summary = new DeliveryOrderSummaryPojo();
			String createDate = deliveryOrder.getDoSendDate() != null ? sdf.format(deliveryOrder.getDoSendDate()).toUpperCase() : "";
			String deliveryDate = deliveryOrder.getDeliveryDate() != null ? sdf.format(deliveryOrder.getDeliveryDate()).toUpperCase() : "";

			summary.setDoName(deliveryOrder.getName());
			summary.setDeliveryId(deliveryOrder.getDeliveryId());
			summary.setPaymentNote("Hello");
			summary.setPoNumber(deliveryOrder.getPo().getPoNumber());
			summary.setCreatedDate(createDate);
			summary.setTrackingNumber(deliveryOrder.getTrackingNumber()!= null ? deliveryOrder.getTrackingNumber() : "N/A");
			summary.setCourierName(deliveryOrder.getCourierName() != null ? deliveryOrder.getCourierName() : "N/A");
			if (deliveryOrder.getFooter() != null) {
				summary.setFooter(deliveryOrder.getFooter().getContent());
			}

			SupplierSettings supplierSettings = supplierSettingsDao.getSupplierSettingsByTenantId(deliveryOrder.getSupplier().getId());

			if (supplierSettings != null) {
				ImageIcon n;
				if (supplierSettings.getFileAttatchment() != null) {
					n = new ImageIcon(supplierSettings.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(deliveryOrder.getSupplier().getCompanyName());
				}
				summary.setLogo(n.getImage());
			}
			getSummaryOfAddressAndDoitems(deliveryOrder, df, summary, deliveryDate);

			BusinessUnit bUnit = deliveryOrder.getBusinessUnit();
			String buyerAddress = "";

			if (bUnit != null) {
				summary.setDisplayName(bUnit.getUnitName());
			}
			if (StringUtils.checkString(deliveryOrder.getLine1()).length() > 0) {
				buyerAddress = deliveryOrder.getLine1() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine2()).length() > 0) {
				buyerAddress += deliveryOrder.getLine2() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine3()).length() > 0) {
				buyerAddress += deliveryOrder.getLine3() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine4()).length() > 0) {
				buyerAddress += deliveryOrder.getLine4() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine5()).length() > 0) {
				buyerAddress += deliveryOrder.getLine5() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine6()).length() > 0) {
				buyerAddress += deliveryOrder.getLine6() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine7()).length() > 0) {
				buyerAddress += deliveryOrder.getLine7() + "\r\n";
			}
			summary.setBuyerAddress(buyerAddress);

			List<DeliveryOrderSummaryPojo> deliveryOrderSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (deliveryOrder.getSupplier() != null) {
				Supplier supplier = deliveryOrder.getSupplier();
				supplierAddress += supplier.getCompanyName() + "\r\n";
				supplierAddress += supplier.getLine1();
				if (StringUtils.checkString(deliveryOrder.getSupplier().getLine2()).length() > 0) {
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
				supplierAddress += deliveryOrder.getSupplierName() + "\r\n";
				supplierAddress += deliveryOrder.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (deliveryOrder.getSupplierTelNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (deliveryOrder.getSupplierFaxNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierFaxNumber() + "\r\n\n";
				}

				supplierAddress += "Tax Reg No : ";
				if (deliveryOrder.getSupplierTaxNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierTaxNumber();
				}
			}

			if (deliveryOrder.getSupplier() != null) {
				summary.setSupplierName(deliveryOrder.getSupplier() != null ? deliveryOrder.getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(deliveryOrder.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(deliveryOrder.getSupplierTaxNumber() != null ? deliveryOrder.getSupplierTaxNumber() : "");

			parameters.put("DELIVERYORDER_SUMMARY", deliveryOrderSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(deliveryOrderSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (

		Exception e) {
			LOG.error("Could not generate Do Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private void getSummaryOfAddressAndDoitems(DeliveryOrder deliveryOrder, DecimalFormat df, DeliveryOrderSummaryPojo summary, String deliveryDate) {

		try {

			// Delivery Address

			String deliveryAddress = "";
			if (StringUtils.checkString(deliveryOrder.getDeliveryAddressTitle()).length() > 0) {
				deliveryAddress += deliveryOrder.getDeliveryAddressTitle() + "\n";

			}
			deliveryAddress += deliveryOrder.getDeliveryAddressLine1();
			if (deliveryOrder.getDeliveryAddressLine2() != null) {
				deliveryAddress += "\n" + deliveryOrder.getDeliveryAddressLine2();
			}
			deliveryAddress += "\n" + deliveryOrder.getDeliveryAddressZip() + ", " + deliveryOrder.getDeliveryAddressCity() + "\n";
			deliveryAddress += deliveryOrder.getDeliveryAddressState() + ", " + deliveryOrder.getDeliveryAddressCountry();

			summary.setDeliveryAddress(deliveryAddress);
			if (StringUtils.checkString(deliveryOrder.getDeliveryReceiver()).length() > 0) {
				summary.setDeliveryReceiver(deliveryOrder.getDeliveryReceiver());
			}
			if (StringUtils.checkString(deliveryOrder.getTrackingNumber()).length() > 0) {
				summary.setTrackingNumber(deliveryOrder.getTrackingNumber());
			}
			if (StringUtils.checkString(deliveryOrder.getCourierName()).length() > 0) {
				summary.setCourierName(deliveryOrder.getCourierName());
			}
			summary.setDeliveryDate(deliveryDate);
			// deliveryOrder items List
			List<DeliveryItemsSummaryPojo> doItemList = new ArrayList<DeliveryItemsSummaryPojo>();
			List<DeliveryOrderItem> doList = findAllDoItemByDoId(deliveryOrder.getId());
			if (CollectionUtil.isNotEmpty(doList)) {
				for (DeliveryOrderItem item : doList) {
					DeliveryItemsSummaryPojo dos = new DeliveryItemsSummaryPojo();
					dos.setSlno(item.getLevel() + "." + item.getOrder());
					dos.setItemName(item.getItemName());
					dos.setCurrency(item.getDeliverOrder().getCurrency().getCurrencyCode());
					dos.setItemDescription(item.getItemDescription());
					dos.setAdditionalTax(df.format(item.getDeliverOrder().getAdditionalTax()));
					dos.setGrandTotal(df.format(item.getDeliverOrder().getGrandTotal()));
					dos.setSumAmount(df.format(deliveryOrder.getTotal()));
					dos.setTaxDescription(item.getDeliverOrder().getTaxDescription());
					dos.setDecimal(deliveryOrder.getDecimal());
					doItemList.add(dos);
					if (item.getChildren() != null) {
						for (DeliveryOrderItem childItem : item.getChildren()) {
							DeliveryItemsSummaryPojo childPr = new DeliveryItemsSummaryPojo();
							childPr.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPr.setItemName(childItem.getItemName());
							childPr.setItemDescription(childItem.getItemDescription());
							childPr.setQuantity(childItem.getQuantity());
							childPr.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPr.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPr.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPr.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPr.setUom(childItem.getUnit() != null ? (childItem.getUnit().getUom() != null ? childItem.getUnit().getUom() : "") : (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPr.setCurrency(childItem.getDeliverOrder().getCurrency().getCurrencyCode());
							childPr.setAdditionalTax(df.format(childItem.getDeliverOrder().getAdditionalTax()));
							childPr.setGrandTotal(df.format(childItem.getDeliverOrder().getGrandTotal()));
							childPr.setSumAmount(df.format(deliveryOrder.getTotal()));
							childPr.setTaxDescription(StringUtils.checkString(childItem.getDeliverOrder().getTaxDescription()).length() > 0 ? childItem.getDeliverOrder().getTaxDescription() : "");
							childPr.setSumTaxAmount(childItem.getTaxAmount());
							childPr.setSumTotalAmt(childItem.getTotalAmount());
							childPr.setDecimal(deliveryOrder.getDecimal());
							doItemList.add(childPr);
						}
					}

				}
			}
			summary.setDoItems(doItemList);
		} catch (Exception e) {
			LOG.error("Could not Get Do Items and Address " + e.getMessage(), e);
		}
	}

	@Override
	public List<DeliveryOrderItem> findAllDoItemByDoId(String doId) {
		List<DeliveryOrderItem> returnList = new ArrayList<DeliveryOrderItem>();
		List<DeliveryOrderItem> list = deliveryOrderItemDao.getAllDoItemByDoId(doId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (DeliveryOrderItem item : list) {
				DeliveryOrderItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}

				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (DeliveryOrderItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<DeliveryOrderItem>());
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
	public JasperPrint getGeneratedSupplierDoPdf(DeliveryOrder deliveryOrder, String strTimeZone) {
		deliveryOrder = deliveryOrderDao.findById(deliveryOrder.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (deliveryOrder.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (deliveryOrder.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (deliveryOrder.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (deliveryOrder.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (deliveryOrder.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (deliveryOrder.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		try {
			Resource resource = applicationContext.getResource("classpath:reports/SupplierDoReport.jasper");
			File jasperfile = resource.getFile();

			DeliveryOrderSummaryPojo summary = new DeliveryOrderSummaryPojo();
			String createDate = deliveryOrder.getDoSendDate() != null ? sdf.format(deliveryOrder.getDoSendDate()).toUpperCase() : "";
			String deliveryDate = deliveryOrder.getDeliveryDate() != null ? sdf.format(deliveryOrder.getDeliveryDate()).toUpperCase() : "";

			summary.setDoName(deliveryOrder.getName());
			summary.setDeliveryId(deliveryOrder.getDeliveryId());
			summary.setPaymentNote("Hello");
			summary.setPoNumber(deliveryOrder.getPo().getPoNumber());
			summary.setCreatedDate(createDate);
			summary.setTrackingNumber(deliveryOrder.getTrackingNumber() != null ? deliveryOrder.getTrackingNumber() : "N/A");
			summary.setCourierName(deliveryOrder.getCourierName() != null ? deliveryOrder.getCourierName() : "N/A");

			if (deliveryOrder.getFooter() != null) {
				summary.setFooter(deliveryOrder.getFooter().getContent());
			}
			SupplierSettings supplierSettings = supplierSettingsDao.getSupplierSettingsByTenantId(deliveryOrder.getSupplier().getId());

			if (supplierSettings != null) {
				ImageIcon n;
				if (supplierSettings.getFileAttatchment() != null) {
					n = new ImageIcon(supplierSettings.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(deliveryOrder.getSupplier().getCompanyName());
				}
				summary.setLogo(n.getImage());
			}
			getSummaryOfAddressAndDoitems(deliveryOrder, df, summary, deliveryDate);

			BusinessUnit bUnit = deliveryOrder.getBusinessUnit();
			String buyerAddress = "";

			if (bUnit != null) {
				summary.setDisplayName(bUnit.getUnitName());
			}
			if (StringUtils.checkString(deliveryOrder.getLine1()).length() > 0) {
				buyerAddress = deliveryOrder.getLine1() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine2()).length() > 0) {
				buyerAddress += deliveryOrder.getLine2() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine3()).length() > 0) {
				buyerAddress += deliveryOrder.getLine3() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine4()).length() > 0) {
				buyerAddress += deliveryOrder.getLine4() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine5()).length() > 0) {
				buyerAddress += deliveryOrder.getLine5() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine6()).length() > 0) {
				buyerAddress += deliveryOrder.getLine6() + "\r\n";
			}
			if (StringUtils.checkString(deliveryOrder.getLine7()).length() > 0) {
				buyerAddress += deliveryOrder.getLine7() + "\r\n";
			}

			summary.setBuyerAddress(buyerAddress);

			List<DeliveryOrderSummaryPojo> deliveryOrderSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (deliveryOrder.getSupplier() != null) {
				Supplier supplier = deliveryOrder.getSupplier();
				supplierAddress += supplier.getCompanyName() + "\r\n";
				supplierAddress += supplier.getLine1();
				if (StringUtils.checkString(deliveryOrder.getSupplier().getLine2()).length() > 0) {
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
				supplierAddress += deliveryOrder.getSupplierName() + "\r\n";
				supplierAddress += deliveryOrder.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (deliveryOrder.getSupplierTelNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (deliveryOrder.getSupplierFaxNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierFaxNumber() + "\r\n\n";
				}

				supplierAddress += "Tax Reg No : ";
				if (deliveryOrder.getSupplierTaxNumber() != null) {
					supplierAddress += deliveryOrder.getSupplierTaxNumber();
				}
			}

			if (deliveryOrder.getSupplier() != null) {
				summary.setSupplierName(deliveryOrder.getSupplier() != null ? deliveryOrder.getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(deliveryOrder.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(deliveryOrder.getSupplierTaxNumber() != null ? deliveryOrder.getSupplierTaxNumber() : "");

			parameters.put("DELIVERYORDER_SUMMARY", deliveryOrderSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(deliveryOrderSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Do  Report. " + e.getMessage(), e);
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

	private void sendEmailNotificationToSupplier(DeliveryOrder deliveryOrder, User actionBy, boolean isAccept, String buyerRemark) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(deliveryOrder.getCreatedBy().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String subject = null;
		if (isAccept) {
			subject = "DO Accepted";
		} else {
			subject = "DO Declined";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("supplierName", deliveryOrder.getSupplier() != null ? deliveryOrder.getSupplier().getCompanyName() : deliveryOrder.getSupplierName());
		map.put("doNumber", deliveryOrder.getDeliveryId());
		map.put("doDate", sdf.format(deliveryOrder.getDoSendDate()));
		map.put("poNumber", deliveryOrder.getPo() != null ? deliveryOrder.getPo().getPoNumber() : "N/A");
		map.put("dueDate", sdf.format(deliveryOrder.getDeliveryDate()));
		map.put("deliveryDate", sdf.format(deliveryOrder.getDeliveryDate()));
		map.put("comments", buyerRemark);
		map.put("buyerCompany", deliveryOrder.getBuyer().getCompanyName());

		map.put("buyerName", actionBy.getName());
		map.put("buyerLoginEmail", actionBy.getLoginId());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		map.put("date", sdf.format(new Date()));
		if(deliveryOrder.getCreatedBy().getEmailNotifications()) {
			sendEmail(deliveryOrder.getCreatedBy().getCommunicationEmail(), subject, map, isAccept ? Global.DO_ACCEPTED_TEMPLATE : Global.DO_DECLINE_TEMPLATE);
		}
		SupplierNotificationMessage suppMessage = new SupplierNotificationMessage();
		suppMessage.setCreatedBy(actionBy);
		suppMessage.setCreatedDate(new Date());
		String message = null;
		if (isAccept) {
			message = messageSource.getMessage("do.declined", new Object[] { buyerRemark }, Global.LOCALE);
		} else {
			message = messageSource.getMessage("do.buyerAudit.accepted", new Object[] { buyerRemark }, Global.LOCALE);
		}
		suppMessage.setMessageTo(deliveryOrder.getCreatedBy());
		suppMessage.setMessage(message);
		suppMessage.setNotificationType(NotificationType.GENERAL);
		suppMessage.setSubject(subject);
		suppMessage.setTenantId(deliveryOrder.getSupplier().getId());
		dashboardNotificationService.saveSupplierNotificationMessage(suppMessage);

	}

	@Override
	public List<DoSupplierPojo> getAllBuyerDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return deliveryOrderDao.getAllBuyerDoDetailsForExcelReport(tenantId, doIds, doSupplierPojo, select_all, startDate, endDate);
	}

	@Override
	public List<DoSupplierPojo> getAllSupplierDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return deliveryOrderDao.getAllSupplierDoDetailsForExcelReport(tenantId, doIds, doSupplierPojo, select_all, startDate, endDate, sdf);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoSupplierPojo> getDosByPoId(String poId) {
		return deliveryOrderDao.getDosByPoId(poId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoSupplierPojo> getDosByPoIdForBuyer(String poId) {
		return deliveryOrderDao.getDosByPoIdForBuyer(poId);
	}

	@Override
	public long findTotalDoForBuyerPo(String poId) {
		return deliveryOrderDao.findTotalDoForBuyerPo(poId);
	}

	@Override
	@Transactional(readOnly = false)
	public DeliveryOrder createDoFromErp(ErpDoPojo erpDoPojo, Buyer buyer) throws ApplicationException, EmailException {

		User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
		if (user == null) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
		}

		CostCenter costCenter = null;
		if (StringUtils.checkString(erpDoPojo.getCostCenter()).length() > 0) {
			costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(erpDoPojo.getCostCenter(), buyer.getId());
			if (costCenter == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { erpDoPojo.getCostCenter() }, Global.LOCALE));
			}
		}

		BusinessUnit businessUnit = null;
		if (StringUtils.checkString(erpDoPojo.getBusinesUnit()).length() > 0) {
			businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), erpDoPojo.getBusinesUnit());
			if (businessUnit == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { erpDoPojo.getBusinesUnit() }, Global.LOCALE));
			}
		}

		Currency currency = null;
		if (StringUtils.checkString(erpDoPojo.getCurrency()).length() == 0) {
			throw new ApplicationException("Invalid Currency Code: " + erpDoPojo.getCurrency());
		}

		currency = currencyDao.findByCurrencyCode(erpDoPojo.getCurrency());
		if (currency == null) {
			throw new ApplicationException("Invalid Currency Code: " + erpDoPojo.getCurrency());
		}

		FavouriteSupplier supplier = null;
		if (StringUtils.checkString(erpDoPojo.getSupplierCode()).length() > 0) {
			supplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(erpDoPojo.getSupplierCode()), buyer.getId());
		}

		DeliveryOrder deliveryOrder = new DeliveryOrder();
		deliveryOrder.setLine1(businessUnit.getLine1());
		deliveryOrder.setLine2(businessUnit.getLine2());
		deliveryOrder.setLine3(businessUnit.getLine3());
		deliveryOrder.setLine4(businessUnit.getLine4());
		deliveryOrder.setLine5(businessUnit.getLine5());
		deliveryOrder.setLine6(businessUnit.getLine6());
		deliveryOrder.setLine7(businessUnit.getLine7());

		deliveryOrder.setDeliveryAddressTitle(StringUtils.checkString(erpDoPojo.getDeliveryAddressTitle()));
		deliveryOrder.setDeliveryAddressLine1(StringUtils.checkString(erpDoPojo.getDeliveryAddressLine1()));
		deliveryOrder.setDeliveryAddressLine2(StringUtils.checkString(erpDoPojo.getDeliveryAddressLine2()));
		deliveryOrder.setDeliveryAddressZip(StringUtils.checkString(erpDoPojo.getDeliveryAddressZip()));
		deliveryOrder.setDeliveryAddressCity(StringUtils.checkString(erpDoPojo.getDeliveryAddressCity()));
		deliveryOrder.setDeliveryAddressState(StringUtils.checkString(erpDoPojo.getDeliveryAddressState()));
		deliveryOrder.setDeliveryAddressCountry(StringUtils.checkString(erpDoPojo.getDeliveryAddressCountry()));

		deliveryOrder.setCostCenter(costCenter);
		deliveryOrder.setCreatedBy(user);
		deliveryOrder.setCreatedDate(new Date());
		deliveryOrder.setCurrency(currency);
		deliveryOrder.setDecimal(erpDoPojo.getDecimal() != null ? String.valueOf(erpDoPojo.getDecimal()) : "2");
		deliveryOrder.setDeliveryDate(erpDoPojo.getDeliveryDate());
		deliveryOrder.setDeliveryReceiver(erpDoPojo.getDeliveryReceiver());
		deliveryOrder.setGrandTotal(erpDoPojo.getGrandTotal());
		deliveryOrder.setName(erpDoPojo.getDoName());
		deliveryOrder.setReferenceNumber(erpDoPojo.getReferenceNumber());
		deliveryOrder.setRemarks(erpDoPojo.getRemarks());
		// deliveryOrder.setRequester(erpDoPojo.getRequester());
		deliveryOrder.setStatus(DoStatus.DRAFT);

		deliveryOrder.setSupplierName(erpDoPojo.getSupplierName());
		deliveryOrder.setSupplierAddress(erpDoPojo.getSupplierAddress());
		deliveryOrder.setSupplierTelNumber(erpDoPojo.getSupplierTelNumber());
		deliveryOrder.setSupplierAddress(erpDoPojo.getSupplierAddress());
		deliveryOrder.setSupplierTaxNumber(erpDoPojo.getSupplierTaxNumber());
		deliveryOrder.setSupplierFaxNumber(erpDoPojo.getSupplierFaxNumber());

		if (supplier != null && supplier.getSupplier() != null) {
			deliveryOrder.setSupplier(supplier.getSupplier());
		}

		deliveryOrder.setTaxDescription(erpDoPojo.getTaxDescription());
		deliveryOrder.setTermsAndConditions(erpDoPojo.getTermsAndConditions());
		deliveryOrder.setTotal(erpDoPojo.getTotal());
		if (erpDoPojo.getAdditionalTax() != null) {
			deliveryOrder.setAdditionalTax(erpDoPojo.getAdditionalTax());
		}
		deliveryOrder.setGrandTotal(erpDoPojo.getGrandTotal());

		List<DeliveryOrderItem> deliveryOrderItems = new ArrayList<DeliveryOrderItem>();
		List<DeliveryOrderItem> childItems = new ArrayList<DeliveryOrderItem>();
		if (CollectionUtil.isNotEmpty(erpDoPojo.getItemList())) {
			DeliveryOrderItem parent = new DeliveryOrderItem();
			parent.setItemName("Bill Of Item");
			parent.setLevel(1);
			parent.setOrder(0);
			parent.setBuyer(buyer);
			parent.setDeliverOrder(deliveryOrder);
			int order = 0;
			for (ErpDoItemsPojo prItem : erpDoPojo.getItemList()) {

				if (prItem.getQuantity().floatValue() <= 0.0f) {
					throw new ApplicationException("Quantity should be more than zero. Item No: " + prItem.getItemNo());
				}
				Uom uom = uomService.getUomByUomAndTenantId(prItem.getUom(), buyer.getId());
				if (uom == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { prItem.getUom() }, Global.LOCALE));
				}
				costCenter = null;
				if (StringUtils.checkString(prItem.getCostCenter()).length() > 0) {
					costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(prItem.getCostCenter(), buyer.getId());
				}

				if (StringUtils.checkString(prItem.getBusinessUnit()).length() > 0) {
					businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), prItem.getBusinessUnit());
					if (businessUnit == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { prItem.getBusinessUnit() }, Global.LOCALE));
					}
				}

				DeliveryOrderItem item = new DeliveryOrderItem();
				item.setBusinessUnit(businessUnit);
				item.setBuyer(buyer);
				item.setCostCenter(costCenter);

				item.setDeliveryAddressTitle(StringUtils.checkString(prItem.getDeliveryAddressTitle()));
				item.setDeliveryAddressLine1(StringUtils.checkString(prItem.getDeliveryAddressLine1()));
				item.setDeliveryAddressLine2(StringUtils.checkString(prItem.getDeliveryAddressLine2()));
				item.setDeliveryAddressZip(StringUtils.checkString(prItem.getDeliveryAddressZip()));
				item.setDeliveryAddressCity(StringUtils.checkString(prItem.getDeliveryAddressCity()));
				item.setDeliveryAddressState(StringUtils.checkString(prItem.getDeliveryAddressState()));
				item.setDeliveryAddressCountry(StringUtils.checkString(prItem.getDeliveryAddressCountry()));

				item.setDeliveryDate(deliveryOrder.getDeliveryDate());
				item.setDeliveryReceiver(deliveryOrder.getDeliveryReceiver());
				item.setItemDescription(prItem.getItemDescription());
				item.setItemName(prItem.getItemName());
				item.setItemTax(new BigDecimal(prItem.getItemTax()).setScale(Integer.parseInt(deliveryOrder.getDecimal()), RoundingMode.DOWN));
				item.setLevel(1);
				item.setOrder(++order);
				item.setParent(parent);
				item.setDeliverOrder(deliveryOrder);
				item.setQuantity(prItem.getQuantity());
				item.setTaxAmount(prItem.getTaxAmount());
				item.setTotalAmount(prItem.getTotalAmount());
				item.setTotalAmountWithTax(prItem.getTotalAmountWithTax());
				item.setUnit(uom);
				item.setUnitPrice(prItem.getUnitPrice());
				childItems.add(item);
			}
			parent.setChildren(childItems);
			deliveryOrderItems.add(parent);

			if (CollectionUtil.isNotEmpty(deliveryOrderItems)) {
				deliveryOrder.setDeliveryOrderItems(deliveryOrderItems);
				deliveryOrder = deliveryOrderDao.saveOrUpdate(deliveryOrder);

				DeliveryOrderAudit audit = new DeliveryOrderAudit();
				audit.setAction(DeliveryOrderAuditType.CREATE);
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setBuyer(buyer);
				if (supplier != null && supplier.getSupplier() != null) {
					audit.setSupplier(supplier.getSupplier());
				}
				audit.setVisibilityType(DoAuditVisibilityType.BUYER);
				audit.setDescription(messageSource.getMessage("do.audit.ready", new Object[] { deliveryOrder.getName() }, Global.LOCALE));
				audit.setDeliveryOrder(deliveryOrder);
				deliveryOrderAuditDao.save(audit);

			} else {
				LOG.warn("NO Items found ..");
			}
		}
		return deliveryOrder;
	}

	@Override
	public void downloadCsvFileForDoList(HttpServletResponse response, File file, String[] doIdArr, Date startDate, Date endDate, DoSupplierPojo doSupplierPojo, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.BUYER_DO_REPORT_CSV_COLUMNS;
			String[] columns = new String[] { "deliveryId", "name", "ponumber", "supplierCompanyName", "businessunit", "sendDateStr", "actionDateStr", "currency", "grandTotal", "status" };

			long count = findTotalDoForBuyer(tenantId);

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
				List<DoSupplierPojo> list = deliveryOrderDao.findDoForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, doIdArr, doSupplierPojo, select_all, startDate, endDate);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (DoSupplierPojo pojo : list) {
					if (pojo.getSendDate() != null) {
						pojo.setSendDateStr(sdf.format(pojo.getSendDate()));
					}
					if (pojo.getActionDate() != null) {
						pojo.setActionDateStr(sdf.format(pojo.getActionDate()));
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
		CellProcessor[] processor = new CellProcessor[] { new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // SUPPLIER
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // CURRENCY
				new Optional(), //
				new Optional(), //
		};
		return processor;
	}

	@Override
	public void downloadCsvFileForSupplierDoList(HttpServletResponse response, File file, String[] doIds, Date startDate, Date endDate, DoSupplierPojo doSupplierPojo, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.SUPLLIER_DO_REPORT_CSV_COLUMNS;
			String[] columns = new String[] { "deliveryId", "referencenumber", "name", "ponumber", "buyerCompanyName", "businessunit", "docreatedby", "createdDateStr", "sendDateStr", "actionDateStr", "currency", "grandTotal", "status" };

			long count = findTotalDoForSupplier(tenantId);
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
				List<DoSupplierPojo> list = deliveryOrderDao.findDoForTenantIdForSupplierCsv(tenantId, PAGE_SIZE, pageNo, doIds, doSupplierPojo, select_all, startDate, endDate);
				LOG.info("size ........" + list.size() + ".... count " + count);
				for (DoSupplierPojo pojo : list) {
					LOG.info("BussinessUnit*****" + pojo.getBusinessUnit());
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
				LOG.info("Write done........");
			}
			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.info("Error ..." + e, e);
		}
	}

	private CellProcessor[] getCellProcessors() {
		CellProcessor[] processor = new CellProcessor[] {

				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), new Optional(), // BUYER
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // DO DATE
				new Optional(), //
				new Optional(), //
				new Optional(), new Optional() };
		return processor;
	}

	@Override
	public long findTotalDoForBuyerPoById(String poId) {
		return deliveryOrderDao.findTotalDoForBuyerPoById(poId);
		}

}