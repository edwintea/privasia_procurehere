package com.privasia.procurehere.web.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.ProcurementMethodService;

/**
 * @author sana
 */
@Controller
@RequestMapping(path = "/buyer")
public class ProcurementMethodController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	BuyerService buyerService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	ServletContext context;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/procurementMethodList", method = RequestMethod.GET)
	public String listPaymentTermes(Model model) {
		return "procurementMethodList";
	}

	@GetMapping("/procurementMethodData")
	public ResponseEntity<TableData<ProcurementMethod>> procurementMethodData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<ProcurementMethod> data = new TableData<ProcurementMethod>(procurementMethodService.findProcurementMethodsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = procurementMethodService.findTotalFilteredProcurementMethodsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = procurementMethodService.findCountOfProcurementMethodsForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProcurementMethod>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Procurement Method list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Procurement Method list : " + e.getMessage());
			return new ResponseEntity<TableData<ProcurementMethod>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/procurementMethod")
	public ModelAndView createProcurementMethod(@ModelAttribute ProcurementMethod procurementMethod, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("procurementMethod", "procurementMethod", new ProcurementMethod());
	}

	@PostMapping("/procurementMethod")
	public ModelAndView saveProcurementMethod(@ModelAttribute ProcurementMethod procurementMethod, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error..");
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("errors", errMessages);
				model.addAttribute("Buyer", buyerService.findAllBuyers());
				return new ModelAndView("procurementMethod", "procurementMethod", new ProcurementMethod());
			} else {

				if (doValidate(procurementMethod)) {
					LOG.info("Buyer In Procurement Method" + SecurityLibrary.getLoggedInUser().getBuyer());
					if (StringUtils.checkString(procurementMethod.getId()).length() == 0) {
						procurementMethod.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						procurementMethod.setCreatedBy(SecurityLibrary.getLoggedInUser());
						procurementMethod.setCreatedDate(new Date());
						procurementMethodService.saveProcurementMethod(procurementMethod);
						redir.addFlashAttribute("success", messageSource.getMessage("procurement.method.save.success", new Object[] { procurementMethod.getProcurementMethod() }, Global.LOCALE));
					} else {
						ProcurementMethod persistObj = procurementMethodService.getProcurementMethodById(procurementMethod.getId());
						LOG.info("Procurement Method :  " + persistObj);
						persistObj.setProcurementMethodCode(procurementMethod.getProcurementMethodCode());
						persistObj.setProcurementMethod(procurementMethod.getProcurementMethod());
						persistObj.setDescription(procurementMethod.getDescription());
						persistObj.setStatus(procurementMethod.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						procurementMethodService.updateProcurementMethod(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("procurement.method.update.success", new Object[] { procurementMethod.getProcurementMethod() }, Global.LOCALE));
					}
				} else {
					model.addAttribute("errors", messageSource.getMessage("procurement.method.error.duplicate", new Object[] { procurementMethod.getProcurementMethod() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("procurementMethod", "procurementMethod", procurementMethod);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the procurement method" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("procurement.method.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			return new ModelAndView("procurementMethod", "procurementMethod", new ProcurementMethod());
		}
		return new ModelAndView("redirect:procurementMethodList");

	}

	private boolean doValidate(ProcurementMethod procurementMethod) {
		boolean validate = true;
		if (procurementMethodService.isExists(procurementMethod, SecurityLibrary.getLoggedInUserTenantId())) {
			LOG.info("inside validation");
			validate = false;
		}
		return validate;
	}

	@GetMapping("/editProcurementMethod")
	public ModelAndView editProcurementMethod(@RequestParam String id, Model model) {
		LOG.info("Edit Procurement Method Called  " + id);
		ProcurementMethod procurementMethod = procurementMethodService.getProcurementMethodById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("procurementMethod", "procurementMethod", procurementMethod);
	}

	@GetMapping("/deleteProcurementMethod")
	public String deleteProcurementMethod(@RequestParam String id, Model model) {
		LOG.info("Delete Procurement Method Called");
		ProcurementMethod procurementMethod = procurementMethodService.getProcurementMethodById(id);
		try {
			procurementMethod.setModifiedBy(SecurityLibrary.getLoggedInUser());
			procurementMethod.setModifiedDate(new Date());
			procurementMethodService.deleteProcurementMethod(procurementMethod);
			model.addAttribute("success", messageSource.getMessage("procurement.method.success.delete", new Object[] { procurementMethod.getProcurementMethod() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Procurement Method " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("procurement.method.error.delete", new Object[] { procurementMethod.getProcurementMethod() }, Global.LOCALE));
		}
		return "procurementMethodList";
	}

	@GetMapping("/procurementMethodCsv")
	public void downloadProcurementCsvExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			procurementMethodService.procurementMethodDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Procurement Method is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ProcurementMethod);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error while downloading procurement method template :: " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/downloadPMTemplate", method = RequestMethod.GET)
	public void downloadProcurementMethodTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "ProcurementMethodTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = procurementMethodService.templateDownloader(workbook);

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
			LOG.error("Error while downloading Procurement Method Template :: " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/uploadProcurementMethod", method = RequestMethod.POST)
	public ResponseEntity<String> uploadProcurementMethodExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			validateUploadProcurementMethod(file);
			LOG.info("uploadProcurement MethodExcel method called" + file.getOriginalFilename());
			procurementMethodService.procurementMethodUploadFile(file, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
			headers.add("success", "Procurement Method list uploaded successfully");
			return new ResponseEntity<String>(headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Uploading Procurement Method  template :: " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void validateUploadProcurementMethod(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}
}
