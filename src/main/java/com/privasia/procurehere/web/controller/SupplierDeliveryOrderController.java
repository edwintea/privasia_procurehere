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
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.DoReportDao;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.DoReport;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.DeliveryOrderAuditType;
import com.privasia.procurehere.core.enums.DoAuditVisibilityType;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.DeliveryOrderItemService;
import com.privasia.procurehere.service.DeliveryOrderService;
import com.privasia.procurehere.service.DoAuditService;
import com.privasia.procurehere.service.FooterService;
import com.privasia.procurehere.service.PoService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/supplier")
public class SupplierDeliveryOrderController {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@Autowired
	DeliveryOrderService deliveryOrderService;

	@Autowired
	DeliveryOrderItemService deliveryOrderItemService;

	@Autowired
	PoService poService;

	@Autowired
	PrService prService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DoAuditService doAuditService;

	@Autowired
	FooterService footerService;

	@Autowired
	ServletContext context;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	DoReportDao doReportDao;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
	}

	@ModelAttribute("doStatusList")
	public List<DoStatus> getDoStatusList() {
		return Arrays.asList(DoStatus.values());
	}

	@RequestMapping(path = "/deliveryOrderList", method = RequestMethod.GET)
	public String supplierDoList() {
		return "deliveryOrderList";
	}

	@RequestMapping(path = "/doListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<DoSupplierPojo>> doListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
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
			List<DoSupplierPojo> poList = deliveryOrderService.findAllSearchFilterDoForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<DoSupplierPojo> data = new TableData<DoSupplierPojo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = deliveryOrderService.findTotalSearchFilterDoForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			long totalCount = deliveryOrderService.findTotalDoForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<DoSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Do List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Do List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<DoSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/createDo", method = RequestMethod.POST)
	public String createDo(@RequestParam("poId") String poId, RedirectAttributes redir) {
		try {
			LOG.info("Creating Do for po Id :" + poId + " == User Name :" + SecurityLibrary.getLoggedInUser().getName());
			DeliveryOrder deliveryOrder = null;
			if (StringUtils.checkString(poId).length() > 0) {
				Po po = poService.findPoById(poId);
				if (po != null) {
					deliveryOrder = deliveryOrderService.createDo(SecurityLibrary.getLoggedInUser(), po);
					if (deliveryOrder != null && StringUtils.checkString(deliveryOrder.getId()).length() > 0) {
						LOG.info("Do created succefully:" + deliveryOrder.getDeliveryId());
						redir.addFlashAttribute("success", messageSource.getMessage("success.do.created", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
					}
				}
			}
			return "redirect:/supplier/deliveryOrder/" + deliveryOrder.getId();
		} catch (Exception e) {
			LOG.error("Error while creating DO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while creating DO:" + e.getMessage());
			return "redirect:/supplier/deliveryOrderView/" + poId;
		}
	}

	@RequestMapping(path = "/deliveryOrder/{doId}", method = RequestMethod.GET)
	public String deliveryOrderView(@PathVariable String doId, Model model, HttpServletRequest request, RedirectAttributes redir, HttpSession session) {
		LOG.info("Supplier View GET called By Do id :" + doId);
		try {
			constructDoSummaryAttributesForSupplierView(doId, model, session);

		} catch (Exception e) {
			LOG.info("Error in view DO For Supplier:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.do.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "deliveryOrderView";
	}

	public DeliveryOrder constructDoSummaryAttributesForSupplierView(String doId, Model model, HttpSession session) throws ParseException {
		DeliveryOrder deliveryOrder = deliveryOrderService.getDoByIdForSupplierView(doId);
		LOG.info("deliveryOrder " + deliveryOrder.getName());
		deliveryOrder.setDeliveryTime(deliveryOrder.getDeliveryDate());
		model.addAttribute("deliveryOrder", deliveryOrder);
		List<DeliveryOrderItem> doItemlist = deliveryOrderService.findAllDoItemByDoIdForSummary(doId);
		LOG.info("doItemlist" + doItemlist.size());
		model.addAttribute("doItemlist", doItemlist);
		List<DeliveryOrderAudit> auditList = doAuditService.getDoAuditForSupplierByDoId(doId);
		model.addAttribute("auditList", auditList);
		List<Footer> footerList = footerService.getFootersByTypeForTenant(FooterType.DELIVERY_ORDER, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("footerList", footerList);
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(deliveryOrder.getBuyer().getId());
		model.addAttribute("addressList", addressList);

		return deliveryOrder;
	}

	@RequestMapping(path = "/doFinish", method = RequestMethod.POST)
	public String finishDelivieryOrder(@RequestParam("doId") String doId, Model model, RedirectAttributes redir) {

		try {
			DeliveryOrder deliveryOrder = deliveryOrderService.finishDeliverOrder(doId, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("do.finish.notification.message", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while finishing the DO :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return "deliveryOrderView";
		}
		return "redirect:/supplier/deliveryOrder/" + doId;
	}

	@RequestMapping(path = "/cancelDo", method = RequestMethod.POST)
	public String cancelInvoice(@RequestParam("doId") String doId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier Canceled DO :++++++++++++++++++++++++++++++++++ " + doId);
			DeliveryOrder order = deliveryOrderService.cancelDo(doId, SecurityLibrary.getLoggedInUser(), supplierRemark);
			if (order != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("do.cancel.success", new Object[] { order.getDeliveryId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("do.cancel.error", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while cancel invoice " + e.getMessage(), e);
		}
		return "redirect:/supplier/deliveryOrderList";
	}

	@RequestMapping(path = "/downloadDo/{doId}", method = RequestMethod.GET)
	public void downloadDo(@PathVariable("doId") String doId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info(" Invoice REPORT : " + doId);
			String poFilename = "UnknownPO.pdf";
			DeliveryOrder deliveryOrder = deliveryOrderService.findByDoId(doId);
			DoReport reportObj = doReportDao.findReportByDoId(deliveryOrder.getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (reportObj != null) {
				response.setContentType("application/pdf");
				response.setContentLength(reportObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + reportObj.getFileName() + "\"");
				FileCopyUtils.copy(reportObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				if (deliveryOrder.getDeliveryId() != null) {
					poFilename = (deliveryOrder.getDeliveryId()).replace("/", "-") + ".pdf";
				}
				String filename = poFilename;

				JasperPrint jasperPrint = deliveryOrderService.getGeneratedSupplierDoPdf(deliveryOrder, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
				if (jasperPrint != null) {
					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + filename);

					JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
					
					byte[] outputFile = JasperExportManager.exportReportToPdf(jasperPrint);
					DoReport attach = new DoReport();
					attach.setFileData(outputFile);
					attach.setFileName(filename);
					attach.setDoNumber(deliveryOrder.getDeliveryId());
					attach.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					attach.setDeliveryOrder(deliveryOrder);
					doReportDao.saveOrUpdate(attach);
					
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
				try {
					DeliveryOrderAudit doAudit = new DeliveryOrderAudit();
					doAudit.setAction(DeliveryOrderAuditType.DOWNLOADED);
					doAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					doAudit.setActionDate(new Date());
					if (deliveryOrder.getBuyer() != null) {
						doAudit.setBuyer(deliveryOrder.getBuyer());
					}
					doAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					doAudit.setDescription(messageSource.getMessage("do.audit.downloadDo", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
					doAudit.setVisibilityType(DoAuditVisibilityType.SUPPLIER);
					doAudit.setDeliveryOrder(deliveryOrder);
					doAuditService.save(doAudit);
				} catch (Exception e) {
					LOG.error("Error while saving Do audit:" + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not generate Delivery Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/updateDoItems", method = RequestMethod.POST)
	public String updateDoItems(@RequestParam(name = "doId") String doId, //
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
			@RequestParam(name = "deliveryDate", required = false) String deliveryDate, //
			@RequestParam(name = "deliveryTime", required = false) String deliveryTime, //
			@RequestParam(name = "attentionTo", required = false) String attentionTo, //
			@RequestParam(name = "correspondenceAddress", required = false) String correspondenceAddress, //
			@RequestParam(name = "name", required = false) String doName, //
			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "trackingNumber", required = false) String trackingNumber, //
			@RequestParam(name = "courierName", required = false) String courierName, //
			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/supplier/deliveryOrder/" + doId;
		}

		return updateItems(doId, itemId, itemName, quantity, unitPrice, itemTax, additionalTax, footer, referenceNumber, model, redir, null, SecurityLibrary.getLoggedInUser(), itemDescription, deliveryAddress, deliveryReceiver, deliveryDate, deliveryTime, attentionTo, correspondenceAddress, doName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, trackingNumber, courierName);
	}

	@RequestMapping(path = "/orderFinish", method = RequestMethod.POST)
	public String finishInvoice(@RequestParam(name = "doId") String doId, //
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
			@RequestParam(name = "deliveryDate", required = false) String deliveryDate, //
			@RequestParam(name = "deliveryTime", required = false) String deliveryTime, //
			@RequestParam(name = "attentionTo", required = false) String attentionTo, //
			@RequestParam(name = "correspondenceAddress", required = false) String correspondenceAddress, //
			@RequestParam(name = "name", required = false) String doName, //
			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "trackingNumber", required = false) String trackingNumber, //
			@RequestParam(name = "courierName", required = false) String courierName, //
			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/supplier/deliveryOrder/" + doId;
		}
		return updateItems(doId, itemId, itemName, quantity, unitPrice, itemTax, additionalTax, footer, referenceNumber, model, redir, DoStatus.DELIVERED, SecurityLibrary.getLoggedInUser(), itemDescription, deliveryAddress, deliveryReceiver, deliveryDate, deliveryTime, attentionTo, correspondenceAddress, doName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, trackingNumber, courierName);
	}

	private String updateItems(String doId, String[] itemId, String[] itemName, String[] quantity, String[] unitPrice, String[] itemTax, String additionalTax, String footer, String referenceNumber, Model model, RedirectAttributes redir, DoStatus status, User loggedInUser, String[] itemDescription, String deliveryAddress, String deliveryReceiver, String deliveryDate, String deliveryTime, String attentionTo, String correspondenceAddress, String doName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, String trackingNumber, String courierName) {
		LOG.info("DO ID : " + itemId.length + " Reference Number: " + referenceNumber);
		List<DeliveryOrderItem> itemList = new ArrayList<DeliveryOrderItem>();
		DeliveryOrder order = null;
		try {
			int index = 0;
			for (String id : itemId) {
				LOG.info("ID : " + id);
				if (StringUtils.checkString(id).length() > 0) {
					DeliveryOrderItem deliveryOrderItem = deliveryOrderItemService.findById(id);
					order = deliveryOrderItem.getDeliverOrder();
					if (deliveryOrderItem.getParent() != null) {
						deliveryOrderItem.setItemName(prService.replaceSmartQuotes(itemName[index].replaceAll(",", "")));
						deliveryOrderItem.setItemDescription(prService.replaceSmartQuotes(StringUtils.checkString(itemDescription[index]).length() > 0 ? StringUtils.checkString(itemDescription[index]).replaceAll(",", "") : ""));
						LOG.info("quantity[index] " + quantity[index]);
						deliveryOrderItem.setQuantity(new BigDecimal(StringUtils.checkString(quantity[index]).length() > 0 ? quantity[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						deliveryOrderItem.setUnitPrice(new BigDecimal(StringUtils.checkString(unitPrice[index]).length() > 0 ? unitPrice[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						deliveryOrderItem.setItemTax(new BigDecimal(StringUtils.checkString(itemTax[index]).length() > 0 ? itemTax[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						LOG.info("Item : " + deliveryOrderItem.toLogString());
						itemList.add(deliveryOrderItem);
					}
				} else {

				}
				index++;
			}
			order = deliveryOrderItemService.updateItems(itemList, (StringUtils.checkString(additionalTax).length() > 0 ? new BigDecimal(additionalTax.replaceAll(",", "")).setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN) : BigDecimal.ZERO), footer, status, loggedInUser, referenceNumber, deliveryAddress, deliveryReceiver, deliveryDate, deliveryTime, attentionTo, correspondenceAddress, doName, session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, trackingNumber, courierName);
			if (status == DoStatus.DELIVERED) {
				redir.addFlashAttribute("success", messageSource.getMessage("do.send.notification.message", new Object[] { order.getDeliveryId(), order.getBuyer().getCompanyName() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("do.item.update", new Object[] {}, Global.LOCALE));
			}
			return "redirect:/supplier/deliveryOrder/" + doId;
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("do.item.update.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while updaing items" + e.getMessage(), e);
			int index = 0;
			for (String id : itemId) {
				LOG.info("ID : " + id);
				if (StringUtils.checkString(id).length() > 0) {
					DeliveryOrderItem deliveryOrderItem = deliveryOrderItemService.getDoItemById(id);
					order = deliveryOrderItem.getDeliverOrder();
					if (deliveryOrderItem.getParent() != null) {
						deliveryOrderItem.setItemName(itemName[index].replaceAll(",", ""));
						deliveryOrderItem.setItemDescription(StringUtils.checkString(itemDescription[index]).length() > 0 ? StringUtils.checkString(itemDescription[index]).replaceAll(",", "") : "");
						deliveryOrderItem.setQuantity(new BigDecimal(StringUtils.checkString(quantity[index]).length() > 0 ? quantity[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						deliveryOrderItem.setUnitPrice(new BigDecimal(StringUtils.checkString(unitPrice[index]).length() > 0 ? unitPrice[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						deliveryOrderItem.setItemTax(new BigDecimal(StringUtils.checkString(itemTax[index]).length() > 0 ? itemTax[index].replaceAll(",", "") : "0").setScale(Integer.parseInt(order.getDecimal()), BigDecimal.ROUND_DOWN));
						itemList.add(deliveryOrderItem);
					}
				}
				index++;
			}

			model.addAttribute("doItemlist", itemList);
			List<DeliveryOrderAudit> auditList = doAuditService.getDoAuditForSupplierByDoId(doId);
			model.addAttribute("auditList", auditList);
			DeliveryOrder deliveryOrder = deliveryOrderService.getDoByIdForSupplierView(doId);
			model.addAttribute("deliveryOrder", deliveryOrder);
			List<Footer> footerList = footerService.getFootersByTypeForTenant(FooterType.DELIVERY_ORDER, SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("footerList", footerList);
			return "deliveryOrderView";
		}
	}

	@RequestMapping(path = "/ExportSupplierDoReport", method = RequestMethod.POST)
	public void downloadSupplierDoReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("doSupplierPojo") DoSupplierPojo doSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			String doArr[] = null;
			if (StringUtils.checkString(doSupplierPojo.getDoIds()).length() > 0) {
				doArr = doSupplierPojo.getDoIds().split(",");
				LOG.info("_----------Id " + doSupplierPojo.getDoIds());
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
			String fileName = "Supplier_DO_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			buyerDoReportDownloader(workbook, doArr, session, doSupplierPojo, select_all, startDate, endDate);

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
			LOG.error("Error while downloading DO Report List :: " + e.getMessage(), e);
		}
	}

	private void buyerDoReportDownloader(XSSFWorkbook workbook, String[] doIds, HttpSession session, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		XSSFSheet sheet = workbook.createSheet("Supplier DO Report List");
		int r = 1;
		buildDoHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<DoSupplierPojo> doList = getSearchSupplierDoDetails(doIds, doSupplierPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(doList)) {
			for (DoSupplierPojo doReport : doList) {

				DecimalFormat df = null;
				if (doReport.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (doReport.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (doReport.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (doReport.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (doReport.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (doReport.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;

				row.createCell(cellNum++).setCellValue(doReport.getDeliveryId() != null ? doReport.getDeliveryId() : "");
				row.createCell(cellNum++).setCellValue(doReport.getReferenceNumber() != null ? doReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(doReport.getName() != null ? doReport.getName() : "");
				row.createCell(cellNum++).setCellValue(doReport.getPoNumber() != null ? doReport.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(doReport.getBuyerCompanyName() != null ? doReport.getBuyerCompanyName() : "");
				row.createCell(cellNum++).setCellValue(doReport.getBusinessUnit() != null ? doReport.getBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(doReport.getCreatedBy() != null ? doReport.getCreatedBy() : "");
				row.createCell(cellNum++).setCellValue(doReport.getCreatedDate() != null ? sdf.format(doReport.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(doReport.getSendDate() != null ? sdf.format(doReport.getSendDate()) : "");
				row.createCell(cellNum++).setCellValue(doReport.getActionDate() != null ? sdf.format(doReport.getActionDate()) : "");
				row.createCell(cellNum++).setCellValue(doReport.getCurrency() != null ? doReport.getCurrency() : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(doReport.getGrandTotal() != null ? df.format(doReport.getGrandTotal()) : "");
				CellStyle gt = workbook.createCellStyle();
				gt.setAlignment(CellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellStyle(gt);
				row.createCell(cellNum++).setCellValue(doReport.getStatus() != null ? doReport.getStatus().toString() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildDoHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_SUPLLIER_DO_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<DoSupplierPojo> getSearchSupplierDoDetails(String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return deliveryOrderService.getAllSupplierDoDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), doIds, doSupplierPojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/ExportSupplierDoCsv", method = RequestMethod.POST)
	public void downloadSupplierDoCsv(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("doSupplierPojo") DoSupplierPojo doSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		LOG.info("Exporting Supplier Do Csv  ");
		try {
			File file = File.createTempFile("Supplier_DO_Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String doArr[] = null;
			if (StringUtils.checkString(doSupplierPojo.getDoIds()).length() > 0) {
				doArr = doSupplierPojo.getDoIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);

			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);
				}
			}

			deliveryOrderService.downloadCsvFileForSupplierDoList(response, file, doArr, startDate, endDate, doSupplierPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Supplier_DO_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading DO Report List :: " + e.getMessage(), e);
		}
	}

}