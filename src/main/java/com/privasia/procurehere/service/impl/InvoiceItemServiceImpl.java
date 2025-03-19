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

import com.privasia.procurehere.core.dao.InvoiceAuditDao;
import com.privasia.procurehere.core.dao.InvoiceDao;
import com.privasia.procurehere.core.dao.InvoiceItemDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.InvoiceAuditType;
import com.privasia.procurehere.core.enums.InvoiceAuditVisibilityType;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.InvoiceItemService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SupplierSettingsService;

import freemarker.template.Configuration;

@Service
@Transactional(readOnly = true)
public class InvoiceItemServiceImpl implements InvoiceItemService {

	private static final Logger LOG = LogManager.getLogger(Global.INV_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	InvoiceItemDao invoiceItemDao;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	InvoiceAuditDao invoiceAuditDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerAddressService buyerAddressService;

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

	@Override
	@Transactional(readOnly = false)
	public InvoiceItem updateItem(InvoiceItem deliveryOrderItem) {
		return invoiceItemDao.saveOrUpdate(deliveryOrderItem);
	}

	@Override
	public InvoiceItem findById(String doItemId) {
		InvoiceItem item = invoiceItemDao.findById(doItemId);
		if (item != null && item.getInvoice() != null) {
			item.getInvoice().getName();
		}
		if (item != null && item.getParent() != null) {
			item.getParent().getItemName();
		}
		if (item != null && item.getInvoice() != null && item.getInvoice().getBuyer() != null) {
			item.getInvoice().getBuyer().getCompanyName();
		}

		return item;
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { EmailException.class })
	public Invoice updateItems(List<InvoiceItem> items, BigDecimal additionalTax, String footer, InvoiceStatus invoiceStatus, User loggedInUser, String referenceNumber, String deliveryAddress, String deliveryReceiver, String attentionTo, String correspondenceAddress, String dueDate, String invoiceName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, boolean includeDelievryAdress, Boolean requestForFinance) throws EmailException {
		Invoice invoice = null;
		if (additionalTax == null) {
			additionalTax = BigDecimal.ZERO;
		}
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		List<String> itemIds = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(items)) {
			invoice = items.get(0).getInvoice();
			additionalTax = additionalTax.setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN);
			for (InvoiceItem item : items) {
				itemIds.add(item.getId());
				if (!itemIds.contains(item.getParent().getId())) {
					itemIds.add(item.getParent().getId());
				}
				// Strip additional decimals from unit price and quantity
				BigDecimal totalAmount = (item.getUnitPrice() != null && item.getQuantity() != null) ? item.getUnitPrice().setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN).multiply(item.getQuantity().setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN)) : new BigDecimal(0);
				// round the totalAmount based on decimal
				item.setTotalAmount(totalAmount.setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_HALF_UP));
				LOG.info("Item Tax : " + item.getItemTax() + " TOTAl AMOUNT " + item.getTotalAmount() + " Decimal : " + invoice.getDecimal());
				if (item.getItemTax() != null) {
					BigDecimal taxAmount = item.getTotalAmount().multiply(item.getItemTax()).divide(new BigDecimal(100), Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_HALF_UP).setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_HALF_UP);
					item.setTaxAmount(taxAmount);
				}
				BigDecimal totalAmountWithTax = item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0);
				item.setTotalAmountWithTax(totalAmountWithTax);

				total = total.add(item.getTotalAmountWithTax());
				invoiceItemDao.saveOrUpdate(item);
			}
			grandTotal = total.add(additionalTax);
			invoice.setGrandTotal(grandTotal);
			invoice.setTotal(total);
			invoice.setAdditionalTax(additionalTax);
			invoice.setRequestForFinance(requestForFinance);
			if (StringUtils.checkString(footer).length() > 0) {
				Footer footerObj = new Footer();
				footerObj.setId(footer);
				invoice.setFooter(footerObj);
			} else {
				invoice.setFooter(null);
			}
			if (invoiceStatus == InvoiceStatus.INVOICED) {
				invoice.setStatus(invoiceStatus);
				invoice.setInvoiceSendDate(new Date());
			}
			LOG.info("referenceNumber : " + referenceNumber);
			invoice.setReferenceNumber(StringUtils.checkString(referenceNumber));

			invoice.setIncludeDelievryAdress(includeDelievryAdress);
			invoice.setDeliveryAddressTitle(deliveryAddressTitle);
			invoice.setDeliveryAddressLine1(deliveryAddressLine1);
			invoice.setDeliveryAddressLine2(deliveryAddressLine2);
			invoice.setDeliveryAddressZip(deliveryAddressZip);
			invoice.setDeliveryAddressCity(deliveryAddressCity);
			invoice.setDeliveryAddressState(deliveryAddressState);
			invoice.setDeliveryAddressCountry(deliveryAddressCountry);

			invoice.setDeliveryReceiver(StringUtils.checkString(deliveryReceiver));

			invoice.setAttentionTo(StringUtils.checkString(attentionTo));

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			Date dueDateParse = null;
			if (StringUtils.checkString(dueDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				formatter.setTimeZone(timeZone);
				try {
					dueDateParse = (Date) formatter.parse(dueDate);
				} catch (Exception e) {
					LOG.error("Error While Saving Delivery Date" + e.getMessage(), e);
				}
			}
			LOG.info("dueDateParse  : " + dueDateParse);
			invoice.setDueDate(dueDateParse);

			invoice.setName(StringUtils.checkString(invoiceName));

			invoice = invoiceDao.saveOrUpdate(invoice);
			if (invoiceStatus == InvoiceStatus.INVOICED) {
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

				try {
					sendEmailNotificationToBuyer(invoice, loggedInUser, true, null);
				} catch (Exception e) {
					LOG.error("Error while sending invoice creatio mail to buyer " + invoice.getBuyer().getCompanyName() + " :  " + e.getMessage(), e);
					throw new EmailException("Error while sending invoice creation mail to buyer");
				}

			}

			// Delete items which are not in UI list
			invoiceItemDao.deleteItemsByIds(itemIds, invoice.getId());

		}
		return invoice;
	}

	@Override
	public InvoiceItem getItemById(String itemId) {
		return invoiceItemDao.getInvoiceItemById(itemId);
	}

	private void sendEmailNotificationToBuyer(Invoice invoice, User actionBy, boolean isRecived, String supplierRemark) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat dueDateSdf = new SimpleDateFormat("dd/MM/yyyy");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(invoice.getCreatedBy().getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String subject = null;
		if (isRecived) {
			subject = "Invoice Received";
		} else {
			subject = "Invoice Cancelled";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("buyerName", invoice.getBuyer().getCompanyName());
		map.put("supplierCompanyName", invoice.getSupplier().getCompanyName());
		map.put("invoiceNumber", invoice.getInvoiceId());
		map.put("invoiceDate", sdf.format(invoice.getInvoiceSendDate()));
		map.put("poNumber", invoice.getPo() != null ? invoice.getPo().getPoNumber() : "N/A");
		map.put("poDate", invoice.getPo() != null ? sdf.format(invoice.getPo().getCreatedDate()) : sdf.format(new Date()));
		map.put("dueDate", invoice.getDueDate() != null ? dueDateSdf.format(invoice.getDueDate()) : dueDateSdf.format(new Date()));
		map.put("supplierName", actionBy.getName());
		map.put("supplierLoginEmail", actionBy.getLoginId());
		if (!isRecived) {
			map.put("comments", supplierRemark);
		}
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", APP_URL + "/login");
		map.put("date", sdf.format(new Date()));
		if(invoice.getPo().getCreatedBy().getEmailNotifications()) {
			if (invoice.getPo() != null) {
				sendEmail(invoice.getPo().getCreatedBy().getCommunicationEmail(), subject, map, isRecived ? Global.INVOICE_RECEIVED_TEMPLATE : Global.INVOICE_CANCELLED_TEMPLATE);
			} else {
				sendEmail(invoice.getBuyer().getCommunicationEmail(), subject, map, isRecived ? Global.INVOICE_RECEIVED_TEMPLATE : Global.INVOICE_CANCELLED_TEMPLATE);
			}
		}
		try {
			BuyerNotificationMessage message = new BuyerNotificationMessage();
			message.setCreatedBy(actionBy);
			if (invoice.getPo() != null) {
				message.setMessageTo(invoice.getPo().getCreatedBy());
			}
			message.setCreatedDate(new Date());
			message.setMessage(messageSource.getMessage("invoice.deliverd.message", new Object[] { invoice.getSupplier().getCompanyName() }, Global.LOCALE));
			message.setNotificationType(NotificationType.GENERAL);
			message.setSubject(subject);
			message.setTenantId(invoice.getBuyer().getId());
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

}