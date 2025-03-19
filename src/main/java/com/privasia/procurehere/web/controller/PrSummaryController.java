package com.privasia.procurehere.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PrAuditService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.PrTemplateService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.TransactionLogService;
import com.privasia.procurehere.web.editors.PrApprovalUserEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrSummaryController extends PrBaseController {

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	ServletContext context;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	PrApprovalUserEditor prApprovalUserEditor;

	@Autowired
	PrService prService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	BudgetService budgetservice;

	@Autowired
	TransactionLogService transactionLogService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("step")
	public String getStep() {
		return "7";
	}

	@ModelAttribute("poDocType")
	public List<PoDocumentType> getPoDocType() {
		return Arrays.asList(PoDocumentType.values());
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(PrApprovalUser.class, prApprovalUserEditor);
		binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					// PrApprovalUser group = prService.getPrApprovalUser(id);
					return new PrApprovalUser(user);
				}
				return null;
			}
		});
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy");
		deliveryDate.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "deliveryTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "deliveryDate", new CustomDateEditor(deliveryDate, true));
	}

	/**
	 * @param prId
	 * @param model
	 * @param eventPermissions
	 * @return
	 */
	public Pr constructPrSummaryAttributes(String prId, Model model, EventPermissions eventPermissions) {
		Pr pr = prService.getLoadedPrById(prId);
		// set budget to decimal places
		pr.setAvailableBudget(pr.getDecimal() != null ? pr.getAvailableBudget() != null ?
				pr.getAvailableBudget().setScale(Integer.parseInt(pr.getDecimal()), RoundingMode.HALF_UP) : null : pr.getAvailableBudget());

		Pr prForBudget = null;
		Budget budget = null;
		try {
			// check budget remaining amount
			prForBudget = prService.findPrBUAndCCForBudgetById(prId);
			if (prForBudget != null && prForBudget.getBusinessUnit() != null && prForBudget.getCostCenter() != null) {
				budget = budgetService.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
			}
		} catch (Exception e) {
			LOG.error("error in PR Bugdet not created " + e.getMessage(), e);
		}
		if (null != budget) {
			pr.setRemainingBudgetAmount(budget.getRemainingAmount());
			pr.setBudgetCurrencyCode(budget.getBaseCurrency().getCurrencyCode());
		}

		model.addAttribute("pr", pr);
		pr = prService.findPrSupplierByPrId(prId);
		model.addAttribute("prSupplier", pr);

		List<PrComment> prCommentList = prService.findAllPrCommentsByPrId(prId);
		model.addAttribute("prCommentList", prCommentList);

		List<PrItem> prItemlist = prService.findAllPrItemByPrIdForSummary(prId);
		model.addAttribute("prItemlist", prItemlist);

		if(null !=pr.getIsPo() && pr.getIsPo()){
			Po po=poService.findByPrId(prId);
			model.addAttribute("po",po);
		}

		// Pr users for Approval
		// List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> assignedTeamMembers = new ArrayList<>();
		List<User> userTeamMemberList = new ArrayList<User>();
		for (PrTeamMember prTeamMember : pr.getPrTeamMembers()) {
			try {
				assignedTeamMembers.add((User) prTeamMember.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error while fetching Team Members");
			}
		}

		// List<User> userTeamMemberList =
		// userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		List<PrApprovalUser> approvalUserList = new ArrayList<PrApprovalUser>();
		List<PrApprovalUser> appUserList = prService.fetchAllApprovalUsersByPrId(prId);
		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			userTeamMemberList.add(u);
		}

		List<UserPojo> allUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null); // userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		for (PrApprovalUser approvalUser : appUserList) {
			User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(),approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
			if (!approvalUserList.contains(new PrApprovalUser(user))) {
				approvalUserList.add(new PrApprovalUser(user));
			}
		}

		for (UserPojo userPojo : allUserList) {
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new PrApprovalUser(user))) {
				approvalUserList.add(new PrApprovalUser(user));
			}
		}

		userTeamMemberList.removeAll(assignedTeamMembers);
		model.addAttribute("userTeamMemberList", userTeamMemberList);

		model.addAttribute("userList", approvalUserList);
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("listDocs", prService.findAllPlainPrDocsbyPrId(prId));
		model.addAttribute("listPoDocs", prService.findAllPlainPoDocsbyPrId(prId));

		if(pr.getTemplate() != null) {
			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(),
					SecurityLibrary.getLoggedInUserTenantId());

			model.addAttribute("templateFieldsVisibilty", prTemplate.getFields());
		}

		try {
			if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
				model.addAttribute("prTemplate", pr.getTemplate());
			}
		} catch (Exception e) {
			LOG.error("Error While fetching PR Template :" + e.getMessage(), e);
		}
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		List<PrAudit> prAuditList = prAuditService.getPrAuditByPrId(pr.getId());
		model.addAttribute("prAuditList", prAuditList);
		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("erpSetup", erpSetup);
		model.addAttribute("isAutoCreatePo", buyerSettingsService.isAutoCreatePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		Po po = poService.findPoByPoNumber(pr.getPoNumber(), SecurityLibrary.getLoggedInUserTenantId());
		if (po != null) {
			model.addAttribute("poId", po.getId());
		}

		return pr;
	}

	@RequestMapping(path = "/prSummary/{prId}", method = RequestMethod.GET)
	public String prSummary(@PathVariable String prId, Model model) {
		LOG.info("create pr Summary GET called pr id :" + prId);
		EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
		constructPrSummaryAttributes(prId, model, eventPermissions);
		// if (eventPermissions.isPrDraft() && eventPermissions!=null) {
		// return "redirect:/buyer/createPrDetails/"+eventPermissions.getPrId();
		// }
		return "prSummary";
	}

	// pr item download
	@RequestMapping(path = "/prItemTemplate/{prId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String prId, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Download prItemTemplate... ");
		try {
			Pr pr = prService.getPrById(prId);
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "prItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			List<PrItem> prList = prService.findAllPrItemByPrId(prId);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PR Item List");

			// For Financial Standard
			DecimalFormat df = null;
			if (pr.getDecimal().equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (pr.getDecimal().equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (pr.getDecimal().equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (pr.getDecimal().equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (pr.getDecimal().equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (pr.getDecimal().equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			} else {
				df = new DecimalFormat("#,###,###,##0.00");
			}

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;
			for (String column : Global.PR_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);
				if (column.equals("UNIT PRICE")) {
					cell.setCellValue(column + "(" + pr.getCurrency().getCurrencyCode() + ")");
				} else {
					cell.setCellValue(column);
				}
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField1Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField1Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField2Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField2Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField3Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField3Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField4Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField4Label());
				cell.setCellStyle(styleHeading);
			}

			if (pr.getField5Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField5Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField6Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField6Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField7Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField7Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField8Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField8Label());
				cell.setCellStyle(styleHeading);
			}

			if (pr.getField9Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField9Label());
				cell.setCellStyle(styleHeading);
			}
			if (pr.getField10Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(pr.getField10Label());
				cell.setCellStyle(styleHeading);
			}

			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue("TOTAT_AMOUNT(" + pr.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			cell = rowHeading.createCell(i++);
			cell.setCellValue("TAX_AMOUNT(" + pr.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			cell = rowHeading.createCell(i++);
			cell.setCellValue("TOTAT_AMOUNT_WITH_TAX(" + pr.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			int cellNumber = 0;
			int r = 1;
			// Write Data into Excel
			for (PrItem item : prList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
				row.createCell(cellNum++).setCellValue(item.getItemName());
				row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
				row.createCell(cellNum++).setCellValue(item.getProduct() != null ? (item.getProduct().getUom() != null ? item.getProduct().getUom().getUom() : "") : (item.getUnit() != null ? item.getUnit().getUom() : ""));
				int colNum = 6;
				if (StringUtils.checkString(pr.getField1Label()).length() > 0 && pr.getField1Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField2Label()).length() > 0 && pr.getField2Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField3Label()).length() > 0 && pr.getField3Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField4Label()).length() > 0 && pr.getField4Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField5Label()).length() > 0 && pr.getField5Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField6Label()).length() > 0 && pr.getField6Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField7Label()).length() > 0 && pr.getField7Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField8Label()).length() > 0 && pr.getField8Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField9Label()).length() > 0 && pr.getField9Label() != null)
					colNum++;
				if (StringUtils.checkString(pr.getField10Label()).length() > 0 && pr.getField10Label() != null)
					colNum++;

				sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum + 3));
				cellNumber = colNum + 4;
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PrItem children : item.getChildren()) {
						Row childrow = sheet.createRow(r++);
						int childCellNum = 0;
						childrow.createCell(childCellNum++).setCellValue(children.getLevel() + "." + children.getOrder());
						childrow.createCell(childCellNum++).setCellValue(children.getProduct() != null ? children.getProduct().getProductName() : "");
						childrow.createCell(childCellNum++).setCellValue(children.getItemDescription() != null ? children.getItemDescription() : "");
						childrow.createCell(childCellNum++).setCellValue(children.getProduct() != null ? (children.getProduct().getUom() != null ? children.getProduct().getUom().getUom() : "") : (children.getUnit() != null ? children.getUnit().getUom() : ""));
						childrow.createCell(childCellNum++).setCellValue(children.getQuantity() != null ? String.valueOf(children.getQuantity()) : "");
						childrow.createCell(childCellNum++).setCellValue(children.getUnitPrice() != null ? df.format(children.getUnitPrice()) : "");
						childrow.createCell(childCellNum++).setCellValue(children.getPricePerUnit() != null ? df.format(children.getPricePerUnit()) : "");
						childrow.createCell(childCellNum++).setCellValue(children.getItemTax() != null ? children.getItemTax() : "");
						if (StringUtils.checkString(pr.getField1Label()).length() > 0 && pr.getField1Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField1() != null ? children.getField1() : "");
						if (StringUtils.checkString(pr.getField2Label()).length() > 0 && pr.getField2Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField2() != null ? children.getField2() : "");
						if (StringUtils.checkString(pr.getField3Label()).length() > 0 && pr.getField3Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField3() != null ? children.getField3() : "");
						if (StringUtils.checkString(pr.getField4Label()).length() > 0 && pr.getField4Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField4() != null ? children.getField4() : "");

						if (StringUtils.checkString(pr.getField5Label()).length() > 0 && pr.getField5Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField5() != null ? children.getField5() : "");
						if (StringUtils.checkString(pr.getField6Label()).length() > 0 && pr.getField6Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField6() != null ? children.getField6() : "");
						if (StringUtils.checkString(pr.getField7Label()).length() > 0 && pr.getField7Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField7() != null ? children.getField7() : "");
						if (StringUtils.checkString(pr.getField8Label()).length() > 0 && pr.getField8Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField8() != null ? children.getField8() : "");
						if (StringUtils.checkString(pr.getField9Label()).length() > 0 && pr.getField9Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField9() != null ? children.getField9() : "");
						if (StringUtils.checkString(pr.getField10Label()).length() > 0 && pr.getField10Label() != null)
							childrow.createCell(childCellNum++).setCellValue(children.getField10() != null ? children.getField10() : "");

						childrow.createCell(childCellNum++).setCellValue(children.getTotalAmount() != null ? df.format(children.getTotalAmount()) : df.format(0));
						childrow.createCell(childCellNum++).setCellValue(children.getTaxAmount() != null ? df.format(children.getTaxAmount()) : df.format(0));
						childrow.createCell(childCellNum++).setCellValue(children.getTotalAmountWithTax() != null ? df.format(children.getTotalAmountWithTax()) : df.format(0));
						cellNumber = childCellNum;
					}
				}
			}
			r++;
			Row row = sheet.createRow(r++);
			row.createCell(cellNumber - 2).setCellValue("Total(" + pr.getCurrency().getCurrencyCode() + ")");
			row.createCell(cellNumber - 1).setCellValue(df.format(pr.getTotal()));
			if (pr.getTaxDescription() != null) {
				row = sheet.createRow(r++);
				row.createCell(cellNumber - 4).setCellValue("Additional Charges");
				row.createCell(cellNumber - 3).setCellValue(pr.getTaxDescription());
				row.createCell(cellNumber - 2).setCellValue("(" + pr.getCurrency().getCurrencyCode() + ")");
				row.createCell(cellNumber - 1).setCellValue(df.format(pr.getAdditionalTax()));
			}
			row = sheet.createRow(r++);
			Cell grandTotalCell = row.createCell(cellNumber - 2);
			grandTotalCell.setCellValue("Grand Total(" + pr.getCurrency().getCurrencyCode() + ")");
			grandTotalCell.setCellStyle(styleHeading);
			grandTotalCell = row.createCell(cellNumber - 1);
			grandTotalCell.setCellValue(df.format(pr.getGrandTotal()));
			grandTotalCell.setCellStyle(styleHeading);

			// Auto Fit
			for (int k = 0; k < 21; k++) {
				sheet.autoSizeColumn(k, true);
			}

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
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading PR items  Excel : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/prFinish/{prId}", method = RequestMethod.GET)
	public ModelAndView prFinish(@PathVariable String prId, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("pr finish called...");

		try {
			// Check subscription limit
			Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

			if (buyer != null && buyer.getBuyerPackage() != null && buyer.getBuyerPackage().getPlan() != null) {
				BuyerPackage bp = buyer.getBuyerPackage();
				if (bp.getPlan().getPlanType() == PlanType.PER_USER) {
					if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
						throw new SubscriptionException("Your Subscription has Expired.");
					}
				} else {
					if (bp.getEventLimit() != null) {
						if (bp.getNoOfEvents() == null) {
							bp.setNoOfEvents(0);
						}
						if (bp.getNoOfEvents() >= bp.getEventLimit()) {
							throw new SubscriptionException("Event limit of " + bp.getEventLimit() + " reached.");
						} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
							throw new SubscriptionException("Your Subscription has Expired.");
						}
					}
				}
			}
		} catch (SubscriptionException e) {
			LOG.error("Error :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:/buyer/buyerDashboard");
		}

		Pr probj = prService.getLoadedPrById(prId);
		if (Boolean.TRUE == probj.getEnableApprovalReminder()) {
			if (probj.getReminderAfterHour() == null) {
				model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
				EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
				constructPrSummaryAttributes(prId, model, eventPermissions);
				return new ModelAndView("prSummary");
			}
			if (probj.getReminderCount() == null) {
				model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
				EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
				constructPrSummaryAttributes(prId, model, eventPermissions);
				return new ModelAndView("prSummary");
			}
		}

		Pr pr = prService.findPrSupplierByPrId(prId);
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
			dateTimeFormat.setTimeZone(timeZone);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
			// check Budget
			Pr prForBudget = prService.findPrBUAndCCForBudgetById(prId);

			Budget budget = null;
			if (Boolean.TRUE == prForBudget.getLockBudget()) {
				if (prForBudget.getBusinessUnit() != null && prForBudget.getCostCenter() != null) {
					budget = budgetservice.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
					// budget checking
					if (budget != null) {
						if (budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
							LOG.info("Budget is approved or active");
						} else {
							// PR cant be made if budget is not ACTIVE or APPROVED
							LOG.info("Budget is not  approved or active");
							throw new ApplicationException(messageSource.getMessage("budget.pr.create.bu.notActive.error", new Object[] {}, Global.LOCALE));
						}
					} else {
						LOG.info("Budget not created");
						throw new ApplicationException(messageSource.getMessage("budget.pr.create.bu.error", new Object[] {}, Global.LOCALE));
					}

				}
			}
			if (super.validatePr(pr, model, Pr.PrCreate.class)) {
				pr = prService.getLoadedPrById(prId);
				super.constructPrAttributes(model);
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				super.constructPrTeamAttributes(model, pr);
				return new ModelAndView("createPrDetails", "pr", pr);
			} else if (pr.getSupplier() == null && (StringUtils.checkString(pr.getSupplierName()).length() == 0)) {
				model.addAttribute("error", messageSource.getMessage("pr.supplier.required", new Object[] {}, Global.LOCALE));
				model.addAttribute("prContactList", pr.getPrContacts());
				super.constructPrSupplierAttributes(model);
				return new ModelAndView("prSupplier", "pr", pr);
			} else if (pr.getSupplier() != null && super.validatePr(pr, model, Pr.PrSupplierList.class)) {
				model.addAttribute("prContactList", pr.getPrContacts());
				super.constructPrSupplierAttributes(model);
				return new ModelAndView("prSupplier", "pr", pr);
			} else if ((StringUtils.checkString(pr.getSupplierName()).length() > 0) && super.validatePr(pr, model, Pr.PrSupplierManual.class)) {
				model.addAttribute("prContactList", pr.getPrContacts());
				super.constructPrSupplierAttributes(model);
				return new ModelAndView("prSupplier", "pr", pr);
			} else if (super.validatePr(pr, model, Pr.PrDelivery.class)) {
				pr = prService.findPrById(prId);
				List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("addressList", addressList);
				return new ModelAndView("prDelivery", "pr", pr);
			} else if (pr.getDeliveryDate() != null && pr.getDeliveryDate().before(new Date())) {
				LOG.info("DeliveryDate:" + pr.getDeliveryDate());
				pr = prService.findPrById(prId);
				pr.setDeliveryTime(pr.getDeliveryDate());
				model.addAttribute("error", messageSource.getMessage("pr.error.deliveryDate", new Object[] { dateTimeFormat.format(pr.getDeliveryDate()) }, Global.LOCALE));
				List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("addressList", addressList);
				return new ModelAndView("prDelivery", "pr", pr);
			} else if (super.validatePr(pr, model, Pr.PrRemark.class)) {
				return new ModelAndView("prRemark", "pr", pr);
			}
			pr = approvalService.doApproval(pr, SecurityLibrary.getLoggedInUser(), Boolean.TRUE);

			// prService.updatePr(pr);

			if (Boolean.TRUE == prForBudget.getLockBudget()) {
				// for budget
				try {
					Pr prForApprovals = prService.findPrApprovalForBudgetById(pr.getId());
					Budget savedBudget = null;

					if (budget != null && budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
						// create new transaction of budget
						TransactionLog transactionLog = new TransactionLog();
						transactionLog.setBudget(budget);
						transactionLog.setReferanceNumber(budget.getBudgetId());
						transactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						transactionLog.setTransactionTimeStamp(new Date());
						transactionLog.setPrBaseCurrency(prForBudget.getCurrency().getCurrencyCode());

						// conversionRate not equal to zero
						// convert pr amount if currency is different
						BigDecimal prAfterConversion = null;
						if (null != prForBudget.getConversionRate() && !(0 == prForBudget.getConversionRate().compareTo(BigDecimal.ZERO))) {
							prAfterConversion = prForBudget.getGrandTotal().multiply(prForBudget.getConversionRate());
							LOG.info("**************************prAfterConversion " + prAfterConversion);
							// update values in transaction
							transactionLog.setConversionRateAmount(prForBudget.getConversionRate());
							transactionLog.setAfterConversionAmount(prAfterConversion);
							transactionLog.setPrBaseCurrency(prForBudget.getCurrency().getCurrencyCode());
						}
						// if PR don't have approvals
						if (null != prForApprovals && CollectionUtil.isEmpty(prForApprovals.getPrApprovals())) {
							// if budget overrun is enabled
							if (budget.getBudgetOverRun()) {
								// if budget locking enabled
								if (prForBudget.getLockBudget()) {
									budget.setLockedAmount(budget.getLockedAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								} else {
									budget.setPaidAmount(budget.getPaidAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								}
								budget.setRemainingAmount(budget.getRemainingAmount().subtract(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
							} else if (1 == budget.getRemainingAmount().compareTo(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()) || 0 == budget.getRemainingAmount().compareTo(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal())) {
								// if budget overrun is disabled
								// only if Pr grandTotal is less than or equal to budget Remaining amount
								if (prForBudget.getLockBudget()) {
									// if budget locking enabled
									budget.setLockedAmount(budget.getLockedAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								} else {
									budget.setPaidAmount(budget.getPaidAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								}
								budget.setRemainingAmount(budget.getRemainingAmount().subtract(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
							}
							// update budget and create transaction
							savedBudget = budgetService.updateBudget(budget);
							LOG.info("log 5 =========savedBudget===================> " + savedBudget.getRemainingAmount());
							if (savedBudget != null) {
								transactionLog.setTransactionLogStatus(TransactionLogStatus.DEDUCT);
								if (prForBudget.getLockBudget()) {
									transactionLog.setLocked("YES");
								}
								transactionLog.setDeductAmount(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal());
								transactionLog.setRemainingAmount(savedBudget.getRemainingAmount());
							}
							transactionLogService.saveTransactionLog(transactionLog);
						}
						// if PR have approvals
						else {
							// if budget overrun is enabled
							if (budget.getBudgetOverRun()) {
								// if budget locking enabled
								if (prForBudget.getLockBudget()) {
									budget.setLockedAmount(budget.getLockedAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								} else {
									budget.setPendingAmount(budget.getPendingAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								}
								budget.setRemainingAmount(budget.getRemainingAmount().subtract(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
							} else if (1 == budget.getRemainingAmount().compareTo(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()) || 0 == budget.getRemainingAmount().compareTo(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal())) {
								// if budget overrun is disabled
								// only if Pr grandTotal is less than or equal to Remaining amount of Budget
								if (prForBudget.getLockBudget()) {
									// if budget locking enabled
									budget.setLockedAmount(budget.getLockedAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								} else {
									budget.setPendingAmount(budget.getPendingAmount().add(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
								}
								budget.setRemainingAmount(budget.getRemainingAmount().subtract(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()));
							}
							// update budget and create transaction
							savedBudget = budgetService.updateBudget(budget);
							if (savedBudget != null) {
								// create new transaction of budget
								transactionLog.setTransactionLogStatus(TransactionLogStatus.DEDUCT);
								if (prForBudget.getLockBudget()) {
									transactionLog.setLocked("YES");
								}
								transactionLog.setDeductAmount(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal());
								transactionLog.setRemainingAmount(savedBudget.getRemainingAmount());
								transactionLogService.saveTransactionLog(transactionLog);
							}
						}

						// send Budget overrun notification
						if ((savedBudget.getBudgetOverRun() && savedBudget.isBudgetOverRunNotification()) && (-1 == savedBudget.getRemainingAmount().compareTo(BigDecimal.ZERO))) {
							LOG.info("Sending budget over run notification");
							budgetService.sendBudgetOverrunNotification(savedBudget.getId());
						}

						// send notification if budget utilized 80%,85%,90%,95%
						if (savedBudget != null) {
							BigDecimal budgetTotalAmount = savedBudget.getTotalAmount();
							BigDecimal budgetRemainingAmount = savedBudget.getRemainingAmount();
							BigDecimal budgetUsedAmount = budgetTotalAmount.subtract(budgetRemainingAmount);

							BigDecimal percentageUsed = (budgetUsedAmount.multiply(new BigDecimal(100)).divide(budgetTotalAmount, 2, RoundingMode.HALF_EVEN));

							LOG.info("****************************budget percentage**************1 " + percentageUsed);

							if ((1 == percentageUsed.compareTo(new BigDecimal(95))) && (-1 == percentageUsed.compareTo(new BigDecimal(100)))) {
								LOG.info("budget utilized=====95%========>" + percentageUsed);
								budgetService.sendBudgetUtilizedNotifications(savedBudget.getId(), percentageUsed);
							} else if ((1 == percentageUsed.compareTo(new BigDecimal(90))) && (-1 == percentageUsed.compareTo(new BigDecimal(95)))) {
								LOG.info("budget utilized=====90%========>" + percentageUsed);
								budgetService.sendBudgetUtilizedNotifications(savedBudget.getId(), percentageUsed);
							} else if ((1 == percentageUsed.compareTo(new BigDecimal(85))) && (-1 == percentageUsed.compareTo(new BigDecimal(90)))) {
								LOG.info("budget utilized=====85%========>" + percentageUsed);
								budgetService.sendBudgetUtilizedNotifications(savedBudget.getId(), percentageUsed);
							} else if ((1 == percentageUsed.compareTo(new BigDecimal(80))) && (-1 == percentageUsed.compareTo(new BigDecimal(85)))) {
								LOG.info("budget utilized=====80%========>" + percentageUsed);
								budgetService.sendBudgetUtilizedNotifications(savedBudget.getId(), percentageUsed);
							}
						}
					}

				} catch (Exception ee) {
					LOG.error("Error While Updateing Budget : " + ee.getMessage(), ee);
					model.addAttribute("error", messageSource.getMessage("budget.not.created", new Object[] {}, Global.LOCALE));
					EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
					constructPrSummaryAttributes(prId, model, eventPermissions);
					return new ModelAndView("prSummary");
				}
			}
			redir.addFlashAttribute("success", messageSource.getMessage("pr.finish.success", new Object[] { pr.getName() }, Global.LOCALE));
			prService.sendPrFinishMail(pr);

		} catch (Exception e) {
			LOG.error("Error While Finish :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
			constructPrSummaryAttributes(prId, model, eventPermissions);
			return new ModelAndView("prSummary");
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/prView/{prId}", method = RequestMethod.GET)
	public String prView(@PathVariable String prId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("create pr View GET called pr id :" + prId);
		try {
			EventPermissions eventPermissions = prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId);
			Pr pr = constructPrSummaryAttributes(prId, model, eventPermissions);

			// for budget fetch PR BU
			Pr prForBudget = prService.findPrBUAndCCForBudgetById(pr.getId());
			Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit() != null ? prForBudget.getBusinessUnit().getId() : null, prForBudget.getCostCenter() != null ? prForBudget.getCostCenter().getId() : null);
			if (budget != null) {
				DecimalFormat df = new DecimalFormat("#,###.######");
				model.addAttribute("remainingAmount", df.format(budget.getRemainingAmount()) + " " + budget.getBaseCurrency().getCurrencyCode());
			}

			if (pr.getStatus() == PrStatus.DRAFT && (eventPermissions.isOwner() || eventPermissions.isViewer() || eventPermissions.isEditor())) {
				return "createPrDetails";
			}
			if (!checkPermissionToAllow(eventPermissions)) {
				//redir.addFlashAttribute("requestedUrl", request.getRequestURL());
				//return "redirect:/403_error";
				// validation done in main page instead
				return "prView";
			}
		} catch (Exception e) {
			LOG.error("Error in view Pr :" + e.getMessage(), e);
			// model.addAttribute("error", "Error in view Pr :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("prsummary.error.view.pr", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "prView";
	}

	/**
	 * @param eventPermissions
	 * @return
	 */
	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isApprover() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOpener() || eventPermissions.isViewer()) {
			allow = true;
		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}
		return allow;
	}

	@RequestMapping(path = "/prApproved", method = RequestMethod.POST)
	public String prApproved(@RequestParam String prId, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("create pr Approved GET called pr id :" + prId + " remarks :" + remarks + " SecurityLibrary.getLoggedInUser() :" + SecurityLibrary.getLoggedInUser().getCommunicationEmail());
		try {
			Pr pr = new Pr();
			pr.setId(prId);
			Pr prApproval = approvalService.doApproval(pr, SecurityLibrary.getLoggedInUser(), remarks, true);

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.pr.approved", new Object[] { (StringUtils.checkString(prApproval.getName()).length() > 0 ? prApproval.getName() : " ") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while approving pr :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/prRejected", method = RequestMethod.POST)
	public String prRejected(@RequestParam String prId, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("create pr Rejected GET called pr id :" + prId + " remarks :" + remarks);
		try {
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.cannot.empty", new Object[] {}, Global.LOCALE));
				return "redirect:prView/" + prId;
			}
			Pr pr = new Pr();
			pr.setId(prId);
			Pr prReject = approvalService.doApproval(pr, SecurityLibrary.getLoggedInUser(), remarks, false);
			if (prReject != null) {

				try {
					PrAudit audit = new PrAudit();
					audit.setAction(PrAuditType.REJECTED);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					audit.setDescription(messageSource.getMessage("pr.audit.reject", new Object[] { prReject.getPrId() }, Global.LOCALE));
					audit.setPr(pr);
					prAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED, "PR '" + prReject.getPrId() + "'  Rejected", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
				}
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.pr.rejected", new Object[] { (StringUtils.checkString(prReject.getName()).length() > 0 ? prReject.getName() : " ") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting pr :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.pr", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/updatePrApproval", method = RequestMethod.POST)
	public String updatePrApproval(@ModelAttribute Pr pr, RedirectAttributes redir) {
		try {
			pr = prService.updatePrApproval(pr, SecurityLibrary.getLoggedInUser());
			approvalService.doApproval(pr, SecurityLibrary.getLoggedInUser(), Boolean.FALSE);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while Updating PR Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/prView/" + pr.getId();
	}

	@RequestMapping(value = "/prTeamMembersList/{prId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventTeamMembersList(@PathVariable(name = "prId") String prId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(prService.getPlainTeamMembersForPr(prId));
			data.setDraw(input.getDraw());
			LOG.info("PR Team Members ******** :" + data);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	// @RequestMapping(path = "/poReport/{prId}", method = RequestMethod.GET)
	// public void generatePrReport(@PathVariable("prId") String prId, HttpServletResponse response, HttpSession
	// session) throws Exception {
	// try {
	// LOG.info(" PO REPORT : " + prId);
	// String poFilename = "UnknownPO.pdf";
	// Pr pr = prService.getLoadedPrById(prId);
	// if (pr.getPoNumber() != null) {
	// poFilename = (pr.getPoNumber()).replace("/", "-") + ".pdf";
	// }
	// String filename = poFilename;
	//
	// JasperPrint jasperPrint = prService.getEvaluationSummaryPdf(pr, (String)
	// session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
	// if (jasperPrint != null) {
	// streamReport(jasperPrint, filename, response);
	// }
	//
	// } catch (Exception e) {
	// LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
	// }
	// }

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@RequestMapping(path = "/downlaodPrSummary/{prId}", method = RequestMethod.GET)
	public void downlaodPrSummary(@PathVariable("prId") String prId, HttpServletResponse response, HttpSession session) throws Exception {
		try {

			Pr pr = prService.getLoadedPrById(prId);
			String prFilename = (pr.getPrId()).replace("/", "-") + ".pdf";
			String filename = prFilename;

			JasperPrint jasperPrint = prService.getPrSummaryPdf(pr, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "downloadPrSummaryDocument/{docId}", method = RequestMethod.GET)
	public void downloadRftSummaryDocument(@PathVariable String docId, HttpServletResponse response, Model model) throws IOException {
		try {
			PrDocument docs = prService.findPrDocById(docId);
			super.buildDocumentFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloading Pr  event Document : " + e.getMessage(), e);
			// model.addAttribute("error", "Error in DownLoading Document :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("prsummary.error.downloading.documents", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(value = "/prMultipleApproved", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> prMultipleApproved(@RequestParam(value = "prId[]") String[] prId, @RequestParam String remarks, RedirectAttributes redir, Model model) {
		LOG.info("create pr Approved GET called pr id 1 :" + prId[0] + " remarks :" + remarks + " SecurityLibrary.getLoggedInUser() :" + SecurityLibrary.getLoggedInUser().getCommunicationEmail());
		HttpHeaders headers = new HttpHeaders();
		String msg = "";
		try {
			long count = prService.doMultipleApproval(prId, SecurityLibrary.getLoggedInUser(), remarks, true);
			if (count > 0) {
				msg = "" + count + " PR's has been approved";
				headers.add("success", messageSource.getMessage("flashsuccess.pr.approved", new Object[] { count }, Global.LOCALE));
				return new ResponseEntity<String>(msg, headers, HttpStatus.OK);

			} else {
				headers.add("error", messageSource.getMessage("flashsuccess.pr.approved.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("flashsuccess.pr.approved.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
