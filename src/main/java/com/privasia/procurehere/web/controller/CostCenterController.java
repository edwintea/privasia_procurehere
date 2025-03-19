package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CostCenterService;

/**
 * @author RT-Kapil
 */
@Controller
@RequestMapping(path = "/buyer")
public class CostCenterController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BuyerService buyerService;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/costCenter", method = RequestMethod.GET)
	public ModelAndView createCostCenter(@ModelAttribute CostCenter costCenter, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("costCenter", "costCenter", new CostCenter());
	}

	@RequestMapping(path = "/costCenter", method = RequestMethod.POST)
	public ModelAndView saveCostCenter(@ModelAttribute CostCenter costCenter, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		try {
			LOG.info("Cost Center 1");

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());

				}
				LOG.info("error..");
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("errors", errMessages);
				model.addAttribute("Buyer", buyerService.findAllBuyers());
				return new ModelAndView("costCenter", "costCenter", new CostCenter());
			} else {

				if (doValidate(costCenter)) {
					LOG.info("Buyer In cost Center" + SecurityLibrary.getLoggedInUser().getBuyer());
					if (StringUtils.checkString(costCenter.getId()).length() == 0) {
						costCenter.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						costCenter.setCreatedBy(SecurityLibrary.getLoggedInUser());
						costCenter.setCreatedDate(new Date());
						costCenterService.saveCostCenter(costCenter);
						redir.addFlashAttribute("success", messageSource.getMessage("costcenter.save.success", new Object[] { costCenter.getCostCenter() }, Global.LOCALE));
					} else {
						CostCenter persistObj = costCenterService.getCostCenterById(costCenter.getId());
						LOG.info("cost Center :  " + persistObj);
						persistObj.setCostCenter(costCenter.getCostCenter());
						persistObj.setDescription(costCenter.getDescription());
						persistObj.setStatus(costCenter.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						costCenterService.updateCostCenter(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("costcenter.update.success", new Object[] { costCenter.getCostCenter() }, Global.LOCALE));
						LOG.info("cost Center 2 :  " + persistObj);
					}
				} else {

					model.addAttribute("errors", messageSource.getMessage("costCenter.error.duplicate", new Object[] { costCenter.getCostCenter() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("costCenter", "costCenter", costCenter);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the cost center" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("costCenter.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			return new ModelAndView("costCenter", "costCenter", new CostCenter());
		}

		return new ModelAndView("redirect:listCostCenter");

	}

	private boolean doValidate(CostCenter costCenter) {
		boolean validate = true;
		if (costCenterService.isExists(costCenter, SecurityLibrary.getLoggedInUserTenantId())) {
			LOG.info("inside validation");

			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listCostCenter", method = RequestMethod.GET)
	public String listCostCenter(Model model) {
		return "listCostCenter";
	}

	@RequestMapping(path = "/editCostCenter", method = RequestMethod.GET)
	public ModelAndView editCostCenter(@RequestParam String id, Model model) {
		LOG.info("Edit CostCenter Called  " + id);
		CostCenter costCenter = costCenterService.getCostCenterById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("costCenter", "costCenter", costCenter);
	}

	@RequestMapping(path = "/deleteCostCenter", method = RequestMethod.GET)
	public String deleteCostCenter(@RequestParam String id, Model model) {
		LOG.info("Delete CostCenter Called");
		CostCenter costCenter = costCenterService.getCostCenterById(id);
		try {
			costCenter.setModifiedBy(SecurityLibrary.getLoggedInUser());
			costCenter.setModifiedDate(new Date());
			costCenterService.deleteCostCenter(costCenter);
			model.addAttribute("success", messageSource.getMessage("costcenter.success.delete", new Object[] { costCenter.getCostCenter() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Cost Center [ " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("costcenter.error.delete", new Object[] { costCenter.getCostCenter() }, Global.LOCALE));
		}
		return "listCostCenter";
	}

	@RequestMapping(path = "/costCenterData", method = RequestMethod.GET)
	public ResponseEntity<TableData<CostCenter>> costCenterData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<CostCenter> data = new TableData<CostCenter>(costCenterService.findCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = costCenterService.findTotalFilteredCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = costCenterService.findTotalCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<CostCenter>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Cost Center list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Cost Center list : " + e.getMessage());
			return new ResponseEntity<TableData<CostCenter>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/costCenterTemplate", method = RequestMethod.GET)
	public void downloadCostCenterExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			costCenterService.costCenterDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error while downloading cost center  template :: " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/uploadCostCenter", method = RequestMethod.POST)
	public ResponseEntity<String> uploadCostCenterExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			validateUploadCostCenter(file);
			LOG.info("uploadCostCenterExcel method called" + file.getOriginalFilename());
			costCenterService.costCenterUploadFile(file, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
			headers.add("success", "Cost center list uploaded successfully");
			return new ResponseEntity<String>(headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Uploading cost center  template :: " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public void validateUploadCostCenter(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}
	
	@RequestMapping(path = "/downloadCCTemplate", method = RequestMethod.GET)
	public void downloadCostCenter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CostCenterTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = templateDownloader(workbook);

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
			LOG.error("Error while downloading SupplierTemplate :: " + e.getMessage(), e);
		}
	}

	protected XSSFWorkbook templateDownloader(XSSFWorkbook workbook) {

		XSSFSheet sheet = workbook.createSheet("Cost Center List");
		buildHeader(workbook, sheet);

		XSSFSheet lookupSheet4 = workbook.createSheet("STATUS");
		int index2 = 0;
		Status[] statsArr = Status.values();
		for (Status status : statsArr) {
			XSSFRow firstRow = lookupSheet4.createRow(index2++);
			XSSFCell cell1 = firstRow.createCell(0);
			cell1.setCellValue(status.toString());
		}
		
		// Status
		XSSFDataValidationHelper validationHelperStatus = new XSSFDataValidationHelper(lookupSheet4);
		XSSFDataValidationConstraint constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createFormulaListConstraint("'STATUS'!$B$1:$B$" + (3));
		constraintStatus = (XSSFDataValidationConstraint) validationHelperStatus.createExplicitListConstraint(new String[] { "ACTIVE", "INACTIVE" });
		CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1000, 2, 2);
		XSSFDataValidation validationStatus = (XSSFDataValidation) validationHelperStatus.createValidation(constraintStatus, cellRangeAddressList);
		validationStatus.setSuppressDropDownArrow(true);
		validationStatus.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validationStatus.createErrorBox("Invalid Status Selected", "Please select Status from the list");
		validationStatus.createPromptBox("STATUS List", "Select STATUS from the list provided. It has been exported from your master data.");
		validationStatus.setShowPromptBox(true);
		validationStatus.setShowErrorBox(true);
		sheet.addValidationData(validationStatus);
		workbook.setSheetHidden(1, true);
		
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return workbook;
	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.COST_CENTER_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}
	
	@RequestMapping(path = "/costCenterCsv", method = RequestMethod.GET)
	public void downloadCostCenterListCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			File file = File.createTempFile("CostCenter", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			costCenterService.downloadCostCenterCsvFile(response, file,  SecurityLibrary.getLoggedInUserTenantId());
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Cost Center is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.CostCenter);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=CostCenter.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading User template :: " + e.getMessage(), e);
		}

	}

}
