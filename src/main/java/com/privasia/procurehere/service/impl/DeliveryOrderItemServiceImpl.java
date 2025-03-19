package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.privasia.procurehere.core.dao.DeliveryOrderAuditDao;
import com.privasia.procurehere.core.dao.DeliveryOrderDao;
import com.privasia.procurehere.core.dao.DeliveryOrderItemDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.DeliveryOrderAuditType;
import com.privasia.procurehere.core.enums.DoAuditVisibilityType;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.DeliveryOrderItemService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierSettingsService;

import freemarker.template.Configuration;

@Service
@Transactional(readOnly = true)
public class DeliveryOrderItemServiceImpl implements DeliveryOrderItemService {

	private static final Logger LOG = LogManager.getLogger(Global.INV_LOG);

	@Autowired
	DeliveryOrderAuditDao deliveryOrderAuditDao;

	@Autowired
	DeliveryOrderItemDao deliveryOrderItemDao;

	@Autowired
	DeliveryOrderDao deliveryOrderDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Value("${app.url}")
	String APP_URL;

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

	@Override
	@Transactional(readOnly = false)
	public DeliveryOrderItem updateItem(DeliveryOrderItem deliveryOrderItem) {
		return deliveryOrderItemDao.saveOrUpdate(deliveryOrderItem);
	}

	@Override
	public DeliveryOrderItem findById(String doItemId) {
		DeliveryOrderItem item = deliveryOrderItemDao.findById(doItemId);
		if (item != null && item.getDeliverOrder() != null) {
			item.getDeliverOrder().getName();
		}
		if (item != null && item.getParent() != null) {
			item.getParent().getItemName();
		}
		if (item != null && item.getDeliverOrder() != null && item.getDeliverOrder().getBuyer() != null) {
			item.getDeliverOrder().getBuyer().getCompanyName();
		}

		return item;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public DeliveryOrder updateItems(List<DeliveryOrderItem> items, BigDecimal additionalTax, String footer, DoStatus status, User loggedInUser, String referenceNumber, String deliveryAddress, String deliveryReceiver, String deliveryDate, String deliveryTime, String attentionTo, String correspondenceAddress, String doName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, String trackingNumber, String courierName) throws EmailException {
		DeliveryOrder deliveryOrder = null;
		if (additionalTax == null) {
			additionalTax = BigDecimal.ZERO;
		}
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		List<String> itemIds = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(items)) {
			deliveryOrder = items.get(0).getDeliverOrder();
			additionalTax = additionalTax.setScale(Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_DOWN);

			for (DeliveryOrderItem item : items) {
				itemIds.add(item.getId());
				if (!itemIds.contains(item.getParent().getId())) {
					itemIds.add(item.getParent().getId());
				}
				// Strip additional decimals from unit price and quantity
				BigDecimal totalAmount = (item.getUnitPrice() != null && item.getQuantity() != null) ? item.getUnitPrice().setScale(Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_DOWN).multiply(item.getQuantity().setScale(Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_DOWN)) : new BigDecimal(0);
				// round the totalAmount based on decimal
				item.setTotalAmount(totalAmount.setScale(Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_HALF_UP));
				LOG.info("Item Tax : " + item.getItemTax() + " TOTAl AMOUNT " + item.getTotalAmount() + " Decimal : " + deliveryOrder.getDecimal());
				if (item.getItemTax() != null) {
					BigDecimal taxAmount = item.getTotalAmount().multiply(item.getItemTax()).divide(new BigDecimal(100), Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_HALF_UP).setScale(Integer.parseInt(deliveryOrder.getDecimal()), BigDecimal.ROUND_HALF_UP);
					item.setTaxAmount(taxAmount);
				}
				BigDecimal totalAmountWithTax = item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0);
				item.setTotalAmountWithTax(totalAmountWithTax);

				total = total.add(item.getTotalAmountWithTax());
				deliveryOrderItemDao.saveOrUpdate(item);
			}
			grandTotal = total.add(additionalTax);
			deliveryOrder.setGrandTotal(grandTotal);
			deliveryOrder.setTotal(total);
			deliveryOrder.setAdditionalTax(additionalTax);
			if (StringUtils.checkString(footer).length() > 0) {
				Footer footerObj = new Footer();
				footerObj.setId(footer);
				deliveryOrder.setFooter(footerObj);
			} else {
				deliveryOrder.setFooter(null);
			}
			if (status == DoStatus.DELIVERED) {
				deliveryOrder.setStatus(status);
				deliveryOrder.setDoSendDate(new Date());
			}
			deliveryOrder.setReferenceNumber(StringUtils.checkString(referenceNumber));

			deliveryOrder.setDeliveryAddressTitle(deliveryAddressTitle);
			deliveryOrder.setDeliveryAddressLine1(deliveryAddressLine1);
			deliveryOrder.setDeliveryAddressLine2(deliveryAddressLine2);
			deliveryOrder.setDeliveryAddressZip(deliveryAddressZip);
			deliveryOrder.setDeliveryAddressCity(deliveryAddressCity);
			deliveryOrder.setDeliveryAddressState(deliveryAddressState);
			deliveryOrder.setDeliveryAddressCountry(deliveryAddressCountry);
			deliveryOrder.setTrackingNumber(trackingNumber);
			deliveryOrder.setCourierName(courierName);

			deliveryOrder.setDeliveryReceiver(StringUtils.checkString(deliveryReceiver));

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			Date deliveryDateTime = null;
			if (StringUtils.checkString(deliveryDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
				formatter.setTimeZone(timeZone);
				timeFormatter.setTimeZone(timeZone);
				try {
					Date eventDateParse = (Date) formatter.parse(deliveryDate);
					Date eventTimeParse = (Date) timeFormatter.parse(deliveryTime);
					deliveryDateTime = DateUtil.combineDateTime(eventDateParse, eventTimeParse, timeZone);
				} catch (Exception e) {
					LOG.error("Error While Saving Delivery Date" + e.getMessage(), e);
				}
			}
			LOG.info("DeliveryDateTime  : " + deliveryDateTime);
			deliveryOrder.setDeliveryDate(deliveryDateTime);

			deliveryOrder.setAttentionTo(StringUtils.checkString(attentionTo));

			deliveryOrder.setName(StringUtils.checkString(doName));

			deliveryOrder = deliveryOrderDao.saveOrUpdate(deliveryOrder);
			if (status == DoStatus.DELIVERED) {
				try {
					DeliveryOrderAudit audit = new DeliveryOrderAudit();
					audit.setAction(DeliveryOrderAuditType.DELIVERED);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setBuyer(deliveryOrder.getBuyer());
					Supplier supplier = new Supplier();
					supplier.setId(loggedInUser.getTenantId());
					audit.setSupplier(supplier);
					audit.setDescription(messageSource.getMessage("do.finish.notification.message", new Object[] {}, Global.LOCALE));
					audit.setDeliveryOrder(deliveryOrder);
					audit.setVisibilityType(DoAuditVisibilityType.BOTH);
					deliveryOrderAuditDao.save(audit);
				} catch (Exception e) {
					LOG.error("Error while Finihing Delivery order : " + e.getMessage(), e);
				}

				// Send email to po creator
				try {
					sendEmailNotificationToBuyer(deliveryOrder, loggedInUser, true, null);
				} catch (Exception e) {
					LOG.error("Error while sending do creatio mail to buyer " + deliveryOrder.getBuyer().getCompanyName() + " :  " + e.getMessage(), e);
					throw new EmailException("Error while sending do creatio mail to buyer");
				}

			}

			// Delete items which are not in UI list
			deliveryOrderItemDao.deleteItemsByIds(itemIds, deliveryOrder.getId());

		}
		return deliveryOrder;
	}

	@Override
	public DeliveryOrderItem getDoItemById(String itemId) {
		return deliveryOrderItemDao.getDoItemById(itemId);
	}

	private void sendEmailNotificationToBuyer(DeliveryOrder deliveryOrder, User actionBy, boolean isRecived, String supplierRemark) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(deliveryOrder.getCreatedBy().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String subject = null;
		if (isRecived) {
			subject = "DO Received";
		} else {
			subject = "DO Cancelled";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("buyerName", deliveryOrder.getBuyer().getCompanyName());
		map.put("supplierCompanyName", deliveryOrder.getSupplier().getCompanyName());
		map.put("doNumber", deliveryOrder.getDeliveryId());
		map.put("doDate", sdf.format(deliveryOrder.getDoSendDate()));
		map.put("poNumber", deliveryOrder.getPo() != null ? deliveryOrder.getPo().getPoNumber() : "N/A");
		map.put("deliveryDate", sdf.format(deliveryOrder.getDeliveryDate()));
		map.put("supplierName", actionBy.getName());
		map.put("supplierLoginEmail", actionBy.getLoginId());
		if (!isRecived) {
			map.put("comments", supplierRemark);
		}
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		map.put("date", sdf.format(new Date()));
		if (deliveryOrder.getPo() != null) {
			sendEmail(deliveryOrder.getPo().getCreatedBy().getCommunicationEmail(), deliveryOrder.getPo().getCreatedBy().getEmailNotifications(),subject, map, isRecived ? Global.DO_RECEIVED_TEMPLATE : Global.DO_CANCELLED_TEMPLATE);
		} else {
			sendEmail(deliveryOrder.getBuyer().getCommunicationEmail(),deliveryOrder.getPo().getCreatedBy().getEmailNotifications(), subject, map, isRecived ? Global.DO_RECEIVED_TEMPLATE : Global.DO_CANCELLED_TEMPLATE);
		}

		try {
			BuyerNotificationMessage message = new BuyerNotificationMessage();
			message.setCreatedBy(actionBy);
			if (deliveryOrder.getPo() != null) {
				message.setMessageTo(deliveryOrder.getPo().getCreatedBy());
			}
			message.setCreatedDate(new Date());
			message.setMessage(messageSource.getMessage("do.deliverd.message", new Object[] { deliveryOrder.getSupplier().getCompanyName() }, Global.LOCALE));
			message.setNotificationType(NotificationType.GENERAL);
			message.setSubject(subject);
			message.setTenantId(deliveryOrder.getBuyer().getId());
			dashboardNotificationService.saveBuyerNotificationMessage(message);
		} catch (Exception e) {
		}
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

	private void sendEmail(String mailTo, Boolean emailNotifications, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0 && emailNotifications) {
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

}