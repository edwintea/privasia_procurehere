package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.privasia.procurehere.service.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.enums.InvoiceAuditType;
import com.privasia.procurehere.core.enums.InvoiceAuditVisibilityType;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.FooterService;
import com.privasia.procurehere.service.InvoiceAuditService;
import com.privasia.procurehere.service.InvoiceFinanceRequestService;
import com.privasia.procurehere.service.InvoiceItemService;
import com.privasia.procurehere.service.InvoiceService;
import com.privasia.procurehere.service.PoService;

@Controller
@RequestMapping("/supplier")
public class SupplierInvoiceController {

	private static final Logger LOG = LogManager.getLogger(Global.INV_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	PoService poService;

	@Autowired
	PrService prService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	InvoiceAuditService invoiceAuditService;

	@Autowired
	FooterService footerService;

	@Autowired
	InvoiceItemService invoiceItemService;

	@Autowired
	ServletContext context;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	InvoiceFinanceRequestService invoiceFinanceRequestService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("invoiceStatusList")
	public List<InvoiceStatus> getInvoiceStatusList() {
		return Arrays.asList(InvoiceStatus.values());
	}

	@RequestMapping(path = "/invoiceList", method = RequestMethod.GET)
	public String supplierDoList() {
		return "invoiceList";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
	}

	@RequestMapping(path = "/invoiceListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<InvoiceSupplierPojo>> invoiceListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Supplier Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			List<InvoiceSupplierPojo> invoiceList = invoiceService.findAllSearchFilterInvoiceForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<InvoiceSupplierPojo> data = new TableData<InvoiceSupplierPojo>(invoiceList);
			data.setDraw(input.getDraw());
			long recordFiltered = invoiceService.findTotalSearchFilterInvoiceForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			long totalCount = invoiceService.findTotalInvoiceForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<InvoiceSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching invoice List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching invoice List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<InvoiceSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/createInvoice", method = RequestMethod.POST)
	public String createInvoice(@RequestParam("poId") String poId, RedirectAttributes redir) {
		try {
			LOG.info("Creating invoice for po Id :" + poId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName());
			Invoice invoice = null;
			if (StringUtils.checkString(poId).length() > 0) {
				Po po = poService.findPoById(poId);
				if (po != null) {
					invoice = invoiceService.createInvoice(SecurityLibrary.getLoggedInUser(), po);
					if (invoice != null && StringUtils.checkString(invoice.getId()).length() > 0) {
						LOG.info("Invoice created succefully:" + invoice.getInvoiceId());
						redir.addFlashAttribute("success", messageSource.getMessage("success.invoice.created", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
					}
				}
			}
			return "redirect:/supplier/invoice/" + invoice.getId();
		} catch (Exception e) {
			LOG.error("Error while creating invoice :" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while creating DO:" + e.getMessage());
			return "invoiceView";
		}
	}

	@RequestMapping(path = "/invoice/{invoiceId}", method = RequestMethod.GET)
	public String invoiceView(@PathVariable String invoiceId, Model model, HttpServletRequest request, RedirectAttributes redir, HttpSession session) {
		LOG.info("Supplier View GET called By Invoice id :" + invoiceId);
		try {
			constructInvoiceSummaryAttributesForSupplierView(invoiceId, model, session);
		} catch (Exception e) {
			LOG.info("Error in view Invoice For Supplier:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.do.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "invoiceView";
	}

	public Invoice constructInvoiceSummaryAttributesForSupplierView(String invoiceId, Model model, HttpSession session) throws ParseException {
		Invoice invoice = invoiceService.getInvoiceByIdForSupplierView(invoiceId);
		model.addAttribute("invoice", invoice);

		// long count = invoiceFinanceRequestService.findOnboardedBuyerForInvoiceRequest(invoice.getBuyer().getId());
		// if (count > 0) {
		// InvoiceFinanceRequest invoiceFinanceRequest =
		// invoiceFinanceRequestService.findInvoiceFinanceRequestByInvoiceId(invoice.getId());
		// model.addAttribute("invoiceFinanceRequest", invoiceFinanceRequest);
		// }

		boolean onboarded = false;
		long supplierCount = invoiceFinanceRequestService.findOnboardedSupplierForFinancingRequest(SecurityLibrary.getLoggedInUserTenantId());
		if (supplierCount > 0) {
			long buyerCount = invoiceFinanceRequestService.findOnboardedBuyerForInvoiceRequest(invoice.getBuyer().getId());
			if (buyerCount > 0) {
				onboarded = true;
			}
		}
		model.addAttribute("buyerOnboarded", onboarded);

		if (onboarded) {
			InvoiceFinanceRequest invoiceFinanceRequest = invoiceFinanceRequestService.findInvoiceFinanceRequestByInvoiceId(invoice.getId());
			model.addAttribute("invoiceFinanceRequest", invoiceFinanceRequest);

			if (invoice.getPo() != null) {
				PoFinanceRequestPojo poFinanceRequest = invoiceFinanceRequestService.getPoFinanceRequestPojoByPoId(invoice.getPo().getId());
				model.addAttribute("poFinanceRequest", poFinanceRequest);
			}
		}

		List<InvoiceItem> invoiceItemlist = invoiceService.findAllInvoiceItemByInvoiceIdForSummary(invoiceId);
		LOG.info("invoiceItemlist" + invoiceItemlist.size());
		model.addAttribute("itemlist", invoiceItemlist);
		List<InvoiceAudit> auditList = invoiceAuditService.getInvoiceAuditForSupplierByInvoiceId(invoiceId);
		model.addAttribute("auditList", auditList);
		List<Footer> footerList = footerService.getFootersByTypeForTenant(FooterType.INVOICE, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("footerList", footerList);
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(invoice.getBuyer().getId());
		model.addAttribute("addressList", addressList);

		return invoice;
	}

	@RequestMapping(path = "/invoiceFinish11", method = RequestMethod.POST)
	public String finishDelivieryOrder(@RequestParam("invoiceId") String invoiceId, Model model, RedirectAttributes redir) {

		try {
			Invoice invoice = invoiceService.finishInvoice(invoiceId, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("invoice.finish.notification.message", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while finishing the Invoice :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return "invoiceView";
		}
		return "redirect:/supplier/invoice/" + invoiceId;
	}

	@RequestMapping(path = "/requestFinance", method = RequestMethod.POST)
	public String requestFinance(@RequestParam("invoiceId") String invoiceId, Model model, RedirectAttributes redir) {

		try {
			Invoice request = invoiceFinanceRequestService.requestFinancing(invoiceId, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("invoice.finance.request.submitted", new Object[] { request.getInvoiceId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while requesting financing for the invoice: " + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error during requesting financing for the invoice: " + e.getMessage());
			return "redirect:/supplier/invoice/" + invoiceId;
		}
		return "redirect:/supplier/invoice/" + invoiceId;
	}

	@RequestMapping(path = "/cancelInvoice", method = RequestMethod.POST)
	public String cancelInvoice(@RequestParam("invoiceId") String invoiceId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier Canceled Invoice :++++++++++++++++++++++++++++++++++ " + invoiceId);
			Invoice invoice = invoiceService.cancelInvoice(invoiceId, SecurityLibrary.getLoggedInUser(), supplierRemark);
			if (invoice != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("invoice.cancel.notification.message", new Object[] { invoice.getInvoiceId(), invoice.getBuyer().getCompanyName() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.invoice", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("invoice.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			// redir.addAttribute("error", e.getMessage());
			return "invoiceView";
		}
		return "redirect:/supplier/invoiceList";
	}

	@RequestMapping(path = "/downloadInvoice/{invoiceId}", method = RequestMethod.GET)
	public void downloadInvoice(@PathVariable("invoiceId") String invoiceId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info(" Invoice REPORT : " + invoiceId);
			Invoice invoice = invoiceService.findByInvoiceId(invoiceId);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Invoice '"+invoice.getInvoiceId()+"' is downloaded", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Invoice);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			invoiceService.generateInvoiceReport(response, invoice);
			try {
				InvoiceAudit invoiceAudit = new InvoiceAudit();
				invoiceAudit.setAction(InvoiceAuditType.DOWNLOADED);
				invoiceAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				invoiceAudit.setActionDate(new Date());
				if (invoice.getBuyer() != null) {
					invoiceAudit.setBuyer(invoice.getBuyer());
				}
				invoiceAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				invoiceAudit.setDescription(messageSource.getMessage("invoice.audit.downloadInvoice", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
				invoiceAudit.setVisibilityType(InvoiceAuditVisibilityType.SUPPLIER);
				invoiceAudit.setInvoice(invoice);
				invoiceAuditService.save(invoiceAudit);
			} catch (Exception e) {
				LOG.error("Error while saving Invoice audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Could not generate Invoice Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/updateInvoiceItems", method = RequestMethod.POST)
	public String updateInvoiceItems(@RequestParam(name = "invoiceId") String invoiceId, //
			@RequestParam(name = "poId") String poId, //
			@RequestParam(name = "parentId", required = false) String[] parentId, //
			@RequestParam(name = "itemId", required = false) String[] itemId, //
			@RequestParam(name = "itemName", required = false) String[] itemName, //
			@RequestParam(name = "quantity", required = false) String[] quantity, //
			@RequestParam(name = "unitPrice", required = false) String[] unitPrice, //
			@RequestParam(name = "itemTax", required = false) String[] itemTax, //
			@RequestParam(name = "additionalTax", required = false) String additionalTax, //
			@RequestParam(name = "footer", required = false) String footer, //
			@RequestParam(name = "referenceNumber", required = false) String referenceNumber, //
			@RequestParam(name = "itemDescription", required = false) String[] itemDescription, //
			@RequestParam(name = "deliveryAddress", required = false) String deliveryAddress, //
			@RequestParam(name = "deliveryReceiver", required = false) String deliveryReceiver, //
			@RequestParam(name = "attentionTo", required = false) String attentionTo, //
			@RequestParam(name = "correspondenceAddress", required = false) String correspondenceAddress, //
			@RequestParam(name = "dueDate", required = false) String dueDate, //
			@RequestParam(name = "name", required = false) String invoiceName, //

			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "includeDelievryAdress", required = false) boolean includeDelievryAdress, //
			@RequestParam(name = "requestForFinance", required = false) boolean requestForFinance, //
			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/supplier/invoice/" + invoiceId;
		}
		LOG.info("itemDescription :  " + itemDescription.length + " includeDelievryAdress " + includeDelievryAdress);
		return updateItems(invoiceId, itemId, itemName, quantity, unitPrice, itemTax, additionalTax, footer, referenceNumber, model, redir, null, SecurityLibrary.getLoggedInUser(), itemDescription, deliveryAddress, deliveryReceiver, attentionTo, correspondenceAddress, dueDate, invoiceName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, includeDelievryAdress, requestForFinance);

	}

	@RequestMapping(path = "/invoiceFinish", method = RequestMethod.POST)
	public String finishInvoice(@RequestParam(name = "invoiceId") String invoiceId, //
			@RequestParam(name = "poId") String poId, //
			@RequestParam(name = "parentId", required = false) String[] parentId, //
			@RequestParam(name = "itemId", required = false) String[] itemId, //
			@RequestParam(name = "itemName", required = false) String[] itemName, //
			@RequestParam(name = "quantity", required = false) String[] quantity, //
			@RequestParam(name = "unitPrice", required = false) String[] unitPrice, //
			@RequestParam(name = "itemTax", required = false) String[] itemTax, //
			@RequestParam(name = "additionalTax", required = false) String additionalTax, //
			@RequestParam(name = "footer", required = false) String footer, //
			@RequestParam(name = "referenceNumber", required = false) String referenceNumber, //
			@RequestParam(name = "itemDescription", required = false) String[] itemDescription, //
			@RequestParam(name = "deliveryAddress", required = false) String deliveryAddress, //
			@RequestParam(name = "deliveryReceiver", required = false) String deliveryReceiver, //
			@RequestParam(name = "attentionTo", required = false) String attentionTo, //
			@RequestParam(name = "correspondenceAddress", required = false) String correspondenceAddress, //
			@RequestParam(name = "dueDate", required = false) String dueDate, //
			@RequestParam(name = "name", required = false) String invoiceName, //

			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "includeDelievryAdress", required = false) boolean includeDelievryAdress, //
			@RequestParam(name = "requestForFinance", required = false) boolean requestForFinance, //
			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/supplier/invoice/" + invoiceId;
		}
		LOG.info("itemDescription :  " + itemDescription.length);
		return updateItems(invoiceId, itemId, itemName, quantity, unitPrice, itemTax, additionalTax, footer, referenceNumber, model, redir, InvoiceStatus.INVOICED, SecurityLibrary.getLoggedInUser(), itemDescription, deliveryAddress, deliveryReceiver, attentionTo, correspondenceAddress, dueDate, invoiceName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, includeDelievryAdress, requestForFinance);
	}

	private String updateItems(String invoiceId, String[] itemId, String[] itemName, String[] quantity, String[] unitPrice, String[] itemTax, String additionalTax, String footer, String referenceNumber, Model model, RedirectAttributes redir, InvoiceStatus invoiceStatus, User loggedInUser, String[] itemDescription, String deliveryAddress, String deliveryReceiver, String attentionTo, String correspondenceAddress, String dueDate, String invoiceName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, boolean includeDelievryAdress, Boolean requestForFinance) {

		LOG.info("DO ID : " + itemId.length + " Reference Number: " + referenceNumber);
		List<InvoiceItem> itemList = new ArrayList<InvoiceItem>();
		Invoice invoice = null;
		try {
			int index = 0;
			for (String id : itemId) {
				LOG.info("ID : " + id);
				if (StringUtils.checkString(id).length() > 0) {
					InvoiceItem invoiceItem = invoiceItemService.findById(id);
					invoice = invoiceItem.getInvoice();
					if (invoiceItem.getParent() != null) {
						invoiceItem.setItemName(prService.replaceSmartQuotes(itemName[index].replaceAll(",", "")));
						invoiceItem.setItemDescription(prService.replaceSmartQuotes(StringUtils.checkString(itemDescription[index]).length() > 0 ? StringUtils.checkString(itemDescription[index]).replaceAll(",", "") : ""));
						LOG.info("quantity[index] " + quantity[index]);
						invoiceItem.setQuantity(new BigDecimal(StringUtils.checkString(quantity[index]).length() > 0 ? quantity[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						invoiceItem.setUnitPrice(new BigDecimal(StringUtils.checkString(unitPrice[index]).length() > 0 ? unitPrice[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						invoiceItem.setItemTax(new BigDecimal(StringUtils.checkString(itemTax[index]).length() > 0 ? itemTax[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						LOG.info("Item : " + invoiceItem.toString());
						itemList.add(invoiceItem);
					}
				} else {
				}
				index++;
			}

			if (Boolean.TRUE == requestForFinance) {
				PoFinanceRequest req = null;
				try {
					req = invoiceFinanceRequestService.findPoFinanceRequestByPoId(invoice.getPo().getId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (req != null && req.getRequestStatus() != FinanceRequestStatus.ACTIVE) {
					throw new ApplicationException("Cannot raise Invoice for this PO as the Financing for PO : " + req.getPoNumber() + " is not yet in Active status");
				}

			}

			Invoice invoiceObj = invoiceItemService.updateItems(itemList, (StringUtils.checkString(additionalTax.replaceAll(",", "")).length() > 0 ? new BigDecimal(additionalTax.replaceAll(",", "")).setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN) : BigDecimal.ZERO), footer, invoiceStatus, loggedInUser, referenceNumber, deliveryAddress, deliveryReceiver, attentionTo, correspondenceAddress, dueDate, invoiceName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, includeDelievryAdress, requestForFinance);
			if (invoiceStatus == InvoiceStatus.INVOICED) {
				redir.addFlashAttribute("success", messageSource.getMessage("invoice.send.notification.message", new Object[] { invoiceObj.getInvoiceId(), invoice.getBuyer().getCompanyName() }, Global.LOCALE));
				invoiceService.saveInvoicePdf(invoiceObj);
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("invoice.item.update", new Object[] {}, Global.LOCALE));
			}
			return "redirect:/supplier/invoice/" + invoiceId;
		} catch (Exception e) {
			if (invoiceStatus == InvoiceStatus.INVOICED) {
				redir.addFlashAttribute("error", messageSource.getMessage("invoice.item.update.error", new Object[] { e.getMessage() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			LOG.error("error while updaing items" + e.getMessage(), e);
			int index = 0;
			for (String id : itemId) {
				LOG.info("ID : " + id);
				if (StringUtils.checkString(id).length() > 0) {
					InvoiceItem invoiceItem = invoiceItemService.getItemById(id);
					invoice = invoiceItem.getInvoice();
					if (invoiceItem.getParent() != null) {
						invoiceItem.setItemName(itemName[index].replaceAll(",", ""));
						invoiceItem.setItemDescription(StringUtils.checkString(itemDescription[index]).length() > 0 ? StringUtils.checkString(itemDescription[index]).replaceAll(",", "") : "");
						invoiceItem.setQuantity(new BigDecimal(StringUtils.checkString(quantity[index]).length() > 0 ? quantity[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						invoiceItem.setUnitPrice(new BigDecimal(StringUtils.checkString(unitPrice[index]).length() > 0 ? unitPrice[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						invoiceItem.setItemTax(new BigDecimal(StringUtils.checkString(itemTax[index]).length() > 0 ? itemTax[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(invoice.getDecimal()), BigDecimal.ROUND_DOWN));
						itemList.add(invoiceItem);
					}
				}
				index++;
			}

			model.addAttribute("itemlist", itemList);
			List<InvoiceAudit> auditList = invoiceAuditService.getInvoiceAuditForSupplierByInvoiceId(invoiceId);
			model.addAttribute("auditList", auditList);
			invoice = invoiceService.getInvoiceByIdForSupplierView(invoiceId);
			model.addAttribute("invoice", invoice);
			List<Footer> footerList = footerService.getFootersByTypeForTenant(FooterType.INVOICE, SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("footerList", footerList);
			return "invoiceView";
		}
	}

	@RequestMapping(path = "/ExportInvoiceReport", method = RequestMethod.POST)
	public void downloadInvoiceReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("invoiceSupplierPojo") InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			String EventArr[] = null;
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoiceIds()).length() > 0) {
				EventArr = invoiceSupplierPojo.getInvoiceIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "Supplier_Invoice_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			invoiceReportDownloader(workbook, EventArr, session, invoiceSupplierPojo, select_all, startDate, endDate);

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");
			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while downloading Invoice Report List :: " + e.getMessage(), e);
		}
	}

	private void invoiceReportDownloader(XSSFWorkbook workbook, String[] invoiceIds, HttpSession session, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		XSSFSheet sheet = workbook.createSheet("Supplier Invoice Report List");
		int r = 1;
		buildInvoiceHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<InvoiceSupplierPojo> invoiceList = getSearchInvoiceDetails(invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(invoiceList)) {
			for (InvoiceSupplierPojo invoiceReport : invoiceList) {

				DecimalFormat df = null;
				if (invoiceReport.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (invoiceReport.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (invoiceReport.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (invoiceReport.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (invoiceReport.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (invoiceReport.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(invoiceReport.getInvoiceId() != null ? invoiceReport.getInvoiceId() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getReferenceNumber() != null ? invoiceReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getName() != null ? invoiceReport.getName() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getPoNumber() != null ? invoiceReport.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getBuyerCompanyName() != null ? invoiceReport.getBuyerCompanyName() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getBusinessUnit() != null ? invoiceReport.getBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getCreatedBy() != null ? invoiceReport.getCreatedBy() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getCreatedDate() != null ? sdf.format(invoiceReport.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getSendDate() != null ? sdf.format(invoiceReport.getSendDate()) : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getActionDate() != null ? sdf.format(invoiceReport.getActionDate()) : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getCurrency() != null ? invoiceReport.getCurrency() : "");

				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(invoiceReport.getGrandTotal() != null ? df.format(invoiceReport.getGrandTotal()) : "");
				CellStyle gt = workbook.createCellStyle();
				gt.setAlignment(CellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellStyle(gt);

				row.createCell(cellNum++).setCellValue(invoiceReport.getStatus() != null ? invoiceReport.getStatus().toString() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildInvoiceHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_SUPLLIER_INVOICE_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<InvoiceSupplierPojo> getSearchInvoiceDetails(String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return invoiceService.getAllInvoiceDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate, sdf);
	}
	
	@RequestMapping(path = "/exportInvoiceCsv", method = RequestMethod.POST)
	public void downloadInvoiceCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("invoiceSupplierPojo") InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("Supplier_Invoice_Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			String invIds[] = null;
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoiceIds()).length() > 0) {
				invIds = invoiceSupplierPojo.getInvoiceIds().split(",");
			}

			Date startDate = null;
			Date endDate = null;
			
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);
				}
			}
			invoiceService.downloadCsvFileForSupplierInvoices(response, file, invIds, startDate, endDate, invoiceSupplierPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(),session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Supplier_Invoice_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading supplier Invoice Report :: " + e.getMessage(), e);
		}
	}

}