package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.IndustryCategoryService;

@Controller
@RequestMapping(path = "/buyer")
public class IndustryCategoryController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	BuyerService buyerService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/industryCategory", method = RequestMethod.GET)
	public ModelAndView createIndustryCategory(@ModelAttribute IndustryCategory IndustryCategory, Model model) {
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("industryCategory", "industryCategory", new IndustryCategory());
	}

	@RequestMapping(path = "/industryCategory", method = RequestMethod.POST)
	public ModelAndView saveIndustryCategory(@ModelAttribute IndustryCategory industryCategory, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		LOG.info("Save method Called");
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("error", errMessages);
				return new ModelAndView("industryCategory", "industryCategory", new IndustryCategory());
			} else {

				if (doValidate(industryCategory, SecurityLibrary.getLoggedInUserTenantId())) {

					if (StringUtils.checkString(industryCategory.getId()).length() == 0) {
						industryCategory.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						industryCategory.setCreatedBy(SecurityLibrary.getLoggedInUser());
						industryCategory.setCreatedDate(new Date());
						industryCategoryService.save(industryCategory);
						redir.addFlashAttribute("success", messageSource.getMessage("industrycategory.save.success", new Object[] { industryCategory.getCode() }, Global.LOCALE));
					} else {
						IndustryCategory persistObj = industryCategoryService.getIndustryCategoryById(industryCategory.getId());
						LOG.info("Industry Category :  " + persistObj);
						persistObj.setName(industryCategory.getName());
						persistObj.setCode(industryCategory.getCode());
						persistObj.setStatus(industryCategory.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						industryCategoryService.update(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("industrycategory.update.success", new Object[] { industryCategory.getCode() }, Global.LOCALE));
					}
				} else {
					LOG.info("Category Code:" + industryCategory.getCode());
					model.addAttribute("error", messageSource.getMessage("industrycategory.error.duplicate", new Object[] { industryCategory.getCode() }, Global.LOCALE));
					if (StringUtils.checkString(industryCategory.getId()).length() == 0) {
						model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					} else {
						model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
					}
					return new ModelAndView("industryCategory", "industryCategory", industryCategory);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the state" + e.getMessage(), e);
			/*
			 * model.addAttribute("errors", messageSource.getMessage("buyerIndustryCategoty.error.save", new Object[] {
			 * e.getMessage() }, Global.LOCALE));
			 */
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			return new ModelAndView("industryCategory", "industryCategory", industryCategory);
		}

		return new ModelAndView("redirect:listIndustryCategory");

	}

	private boolean doValidate(IndustryCategory industryCategory, String tenantId) {
		boolean validate = true;

		if (industryCategoryService.isExists(industryCategory, tenantId)) {

			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/editIndustryCategory", method = RequestMethod.GET)
	public ModelAndView editIndustryCategory(@RequestParam String id, Model model) {
		LOG.info("Edit IndustryCategory Called  " + id);
		IndustryCategory industryCategory = industryCategoryService.getIndustryCategoryById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("industryCategory", "industryCategory", industryCategory);
	}

	@RequestMapping(path = "/deleteIndustryCategory", method = RequestMethod.GET)
	public String deleteIndustryCategory(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Delete IndustryCategory Called");
		IndustryCategory industryCategory = industryCategoryService.getIndustryCategoryById(id);
		try {
			industryCategory.setModifiedBy(SecurityLibrary.getLoggedInUser());
			industryCategory.setModifiedDate(new Date());
			industryCategoryService.delete(industryCategory);
			model.addAttribute("success", messageSource.getMessage("industrycategory.success.delete", new Object[] { industryCategory.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Industry Category [ " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("industrycategory.error.delete", new Object[] { industryCategory.getName() }, Global.LOCALE));
		}
		return "listIndustryCategory";
	}

	@RequestMapping(path = "/loadNaicsIndustry")
	public String loadNaicsIndustry(Model model) throws JsonProcessingException {
		try {
			industryCategoryService.loadNaicsCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			model.addAttribute("success", messageSource.getMessage("industrycategory.naics.success.save.upload", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while loading NAICS codes : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("industrycategory.naics.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "listIndustryCategory";
	}

	@RequestMapping(path = "/listIndustryCategory", method = RequestMethod.GET)
	public String listIndustryCategory(Model model) {
		return "listIndustryCategory";
	}

	@RequestMapping(path = "/industryListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<IndustryCategory>> buyerIndustryListData(TableDataInput input) throws JsonProcessingException {

		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<IndustryCategory> data = new TableData<IndustryCategory>(industryCategoryService.findIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = industryCategoryService.findTotalFilteredIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = industryCategoryService.findTotalIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<IndustryCategory>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Industry Category list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Industry Category list : " + e.getMessage());
			return new ResponseEntity<TableData<IndustryCategory>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/industryCategoryTemplate", method = RequestMethod.GET)
	public void downloadIndustryCategoryExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			industryCategoryService.industryCategoryExportTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading uom  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/exportIndustryCsvReport", method = RequestMethod.GET)
	public void downloadIndustryCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("industryCategoryPojo") IndustryCategoryPojo industryCategoryPojo) throws IOException {
		try {
			File file = File.createTempFile("IndustryCategory-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			industryCategoryService.downloadCsvFileForIndustryCategory(response, file, industryCategoryPojo, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Industry Category is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.IndustryCategory);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}


			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=IndustryCategory.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

}
