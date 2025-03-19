package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.pojo.CompanyStatusPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CompanyStatusService;

@Controller
@RequestMapping(path = "/admin")
public class CompanyStatusController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	CompanyStatusService companyStatusService;

	@RequestMapping(path = "/companyStatus")
	public ModelAndView createCompanyStatus(@ModelAttribute("companyObject") CompanyStatus companyStatus, Model model) {
		LOG.info("creating company status " + companyStatus.getId());
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("companyStatus", "companyObject", new CompanyStatus());

	}

	@RequestMapping(path = "/companyStatus", method = RequestMethod.POST)
	public ModelAndView saveCompanyStatus(@Valid @ModelAttribute("companyObject") CompanyStatus companyStatus, BindingResult result, Model model, RedirectAttributes redir) {

		LOG.info("Save Company status called: id :" + companyStatus.getId());
		List<String> errMessages = new ArrayList<String>();
		try {

			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("btnValue", "Create");
				model.addAttribute("errors", errMessages);
				return new ModelAndView("companyStatus", "companyObject", new CompanyStatus());

			} else {

				if (doValidate(companyStatus)) {
					if (StringUtils.checkString(companyStatus.getId()).length() == 0) {
						companyStatus.setCreatedBy(SecurityLibrary.getLoggedInUser());
						companyStatus.setCreatedDate(new Date());
						companyStatusService.createCompanyStatus(companyStatus);
						redir.addFlashAttribute("success", messageSource.getMessage("companystatus.save.success", new Object[] { companyStatus.getCompanystatus() }, Global.LOCALE));
						LOG.info("create companyStatus Called");
					} else {

						CompanyStatus persistObj = companyStatusService.getCompanyStatusById(companyStatus.getId());

						persistObj.setCompanystatus(companyStatus.getCompanystatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						companyStatusService.updateCompanyStatus(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("companystatus.update.success", new Object[] { companyStatus.getCompanystatus() }, Global.LOCALE));
						LOG.info("update companyStatus Called");
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("companystatus.error.duplicate", new Object[] { companyStatus.getCompanystatus() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					return new ModelAndView("companyStatus", "companyObject", new CompanyStatus());
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the companyStatus" + e.getMessage(), e);
			model.addAttribute("btnValue", "Create");
			model.addAttribute("error", messageSource.getMessage("companystatus.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("companyStatus", "companyObject", new CompanyStatus());
		}

		return new ModelAndView("redirect:listCompanyStatus");

	}

	private boolean doValidate(CompanyStatus companyStatus) {
		boolean validate = true;
		if (companyStatusService.isExists(companyStatus)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/editCompanyStatus", method = RequestMethod.GET)
	public ModelAndView editCompanyStatus(@RequestParam String id, Model model) {
		LOG.info("Edit Company Status Called  " + id);
		CompanyStatus companyStatus = companyStatusService.getCompanyStatusById(id);
		LOG.info("companyStatus : " + companyStatus.getCompanystatus());
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("companyStatus", "companyObject", companyStatus);
	}

	@RequestMapping(path = "editCompanyStatus/edit")
	public String handleRequest(@RequestParam("testPath") String testPath, @RequestParam("id") String id, @RequestParam("attr") String attr, Model model) {
		LOG.info("yaha aaaayaaaa");
		model.addAttribute("id", id);

		LOG.info("testPath:   " + testPath + "  id   " + id + "attr   " + attr);
		return "companyStatus";
	}

	@RequestMapping(path = "/deleteCompanyStatus", method = RequestMethod.GET)
	public String deleteCompanyStatus(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Delete CompanyStatus Called");
		CompanyStatus companyStatus = companyStatusService.getCompanyStatusById(id);
		try {
			if (companyStatus != null) {
				companyStatus.setModifiedBy(SecurityLibrary.getLoggedInUser());
				companyStatus.setModifiedDate(new Date());
				companyStatusService.deleteCompanyStatus(companyStatus);
				model.addAttribute("success", messageSource.getMessage("companystatustype.success.delete", new Object[] { companyStatus.getCompanystatus() }, Global.LOCALE));
			} else {
				model.addAttribute("error", messageSource.getMessage("companystatus.error", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while deleting country [ " + companyStatus.getCompanystatus() + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("companystatus.error.delete", new Object[] { companyStatus.getCompanystatus() }, Global.LOCALE));
		}
		List<CompanyStatusPojo> companyStatusList = companyStatusService.getAllComapnyStatusPojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("companyStatusList", mapper.writeValueAsString(companyStatusList));
		return "listCompanyStatus";
	}

	@RequestMapping(path = "/listCompanyStatus", method = RequestMethod.GET)
	public String listCompanyStatus(Model model) throws JsonProcessingException {
		LOG.info("Company Status List");

		List<CompanyStatusPojo> companyStatusList = companyStatusService.getAllComapnyStatusPojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("companyStatusList", mapper.writeValueAsString(companyStatusList));
		model.addAttribute("createdBy", SecurityLibrary.getLoggedInUserLoginId());

		return "listCompanyStatus";
	}

	@RequestMapping(path = "/companyListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<CompanyStatus>> companyListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<CompanyStatus> data = new TableData<CompanyStatus>(companyStatusService.findAllCompanyStatusList(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = companyStatusService.findTotalFilteredCompanyStatusList(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = companyStatusService.findTotalCompanyStatusList();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<CompanyStatus>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Company Status list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Company type list : " + e.getMessage());
			return new ResponseEntity<TableData<CompanyStatus>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
