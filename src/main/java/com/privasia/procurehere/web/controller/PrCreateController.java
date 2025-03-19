package com.privasia.procurehere.web.controller;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.EventSettingsService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.PrTemplateService;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrCreateController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	PrService prService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BudgetService budgetService;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	EventSettingsService eventSettingsService;

	@RequestMapping(value = "/createPr", method = RequestMethod.GET)
	public String createPr(Model model) {
		LOG.info(" createPr controller called ");
		try {
			constructPrAndTemplateAttribute(model);
		} catch (Exception e) {
			LOG.error("Error While Fetching Pr Template :" + e.getMessage(), e);
		}
		return "createPr";
	}

	@RequestMapping(path = "/copyPrFromTemplate", method = RequestMethod.POST)
	public String copyPrFromTemplate(Model model, @RequestParam(value = "templateId") String templateId, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, RedirectAttributes redir) {
		Pr newPr = null;
		try {
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId))
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);

			newPr = prService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), SecurityLibrary.getLoggedInUserTenantId(), businessUnit);

		} catch (ApplicationException e) {
			LOG.error("=============We are here======================================" + e.getMessage());
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelForTemplateBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("templateId", templateId);

			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createPr";
		} catch (Exception e) {
			constructPrAndTemplateAttribute(model);
			LOG.error("Error While copy Pr from  Template :" + e.getMessage(), e);
			// model.addAttribute("error", "Error While copy Pr from Template :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("pr.error.copy.template", new Object[] { e.getMessage() }, Global.LOCALE));
			return "createPr";
		}
		return "redirect:/buyer/createPrDetails/" + newPr.getId();
	}

	@RequestMapping(value = "/searchPrTemplate", method = RequestMethod.POST)
	public ResponseEntity<List<PrTemplate>> searchPrTemplate(@RequestParam(required = false, name = "templateName") String searchValue, @RequestParam(required = false, name = "pageNo") String pageNo, Model model) {
		LOG.info("inside the search pr create controller : ");

		HttpHeaders headers = new HttpHeaders();
		List<PrTemplate> searchResultTemplate = null;
		try {
			searchResultTemplate = prTemplateService.findByTemplateNameForTenant(searchValue, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo);
			LOG.info(">>>>>>>>>>>>>>>>>> list size"+searchResultTemplate.size()+" ..... pageNo .. "+pageNo);
		} catch (Exception e) {
			LOG.error("Error While searchPrTemplate :" + e.getMessage(), e);
		}
		return new ResponseEntity<List<PrTemplate>>(searchResultTemplate, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/copyFromPr", method = RequestMethod.POST)
	public String copyFrom(Model model, @RequestParam(value = "prId") String prId, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, RedirectAttributes redir) {
		Pr newPr = null;
		try {
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId)) {
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
			}
			if (prService.checkTemplateStatusForPr(prId)) {
				constructPrAndTemplateAttribute(model);
				// model.addAttribute("error", "Cannot copy PR because template being used by this PR is inactive");
				model.addAttribute("error", messageSource.getMessage("pr.cannot.copy.inactive", new Object[] {}, Global.LOCALE));
				return "createPr";
			}
			newPr = prService.copyFrom(prId, SecurityLibrary.getLoggedInUser(), businessUnit);
		} catch (ApplicationException e) {
			LOG.error("=============We are here======================================" + e.getMessage());
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("prid", prId);
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createPr";
		}

		catch (Exception e) {
			LOG.error("Error While copy Pr from previous :" + e.getMessage(), e);
			constructPrAndTemplateAttribute(model);
			// model.addAttribute("error", "Error While copy Pr from previous :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("pr.error.copy.previous", new Object[] { e.getMessage() }, Global.LOCALE));
			return "createPr";
		}
		return "redirect:/buyer/createPrDetails/" + newPr.getId();
	}

	private void constructPrAndTemplateAttribute(Model model) {
		TableDataInput input = new TableDataInput();
		input.setStart(0);
		input.setLength(10);

		List<PrTemplate> allPrTemplate = prTemplateService.findByTemplateNameForTenant(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), "0");
		model.addAttribute("allPrTemplate", allPrTemplate);
		List<Pr> prList = prService.findAllPrForTenant(SecurityLibrary.getLoggedInUserTenantId(), (SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId()), input);
		model.addAttribute("prList", prList);
		
		EventSettings eventSettings = eventSettingsService.getEventSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("eventSettings", eventSettings);
		
		long templatesCount = prTemplateService.getTemplateCountBySearchValueForTenant(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("prTemplatesCount", templatesCount);
		
		long prCount = prService.getPrCountBySearchValueForTenant(null, SecurityLibrary.getLoggedInUserTenantId(), (SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId()));
		model.addAttribute("totalPrCount", prCount);
		
	}

	@RequestMapping(value = "/searchPr", method = RequestMethod.POST)
	public ResponseEntity<List<Pr>> searchPr(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") String pageNo, Model model) {
		LOG.info("inside the searchPr controller : ");
		HttpHeaders headers = new HttpHeaders();
		LOG.info("searchValue :" + searchValue + " pageNo :" + pageNo);
		List<Pr> searchResultPr = null;
		try {
			searchResultPr = prService.searchPrByNameAndRefNum(searchValue, SecurityLibrary.getLoggedInUserTenantId(), (SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId()), pageNo);
			for (Pr pr : searchResultPr) {
				pr.setCurrency(null);
				pr.setCostCenter(null);
				pr.setBusinessUnit(null);
				pr.setSupplier(null);
			}
		} catch (Exception e) {
			LOG.error("Error While search Pr from previous :" + e.getMessage(), e);
		}
		return new ResponseEntity<List<Pr>>(searchResultPr, headers, HttpStatus.OK);
	} 

	@RequestMapping(path = "/checkBudgetAmount/{businessUnitId}/{costCenterId}", method = RequestMethod.GET)
	public ResponseEntity addTeamMemberToList(@PathVariable("businessUnitId") String businessUnitId, @PathVariable("costCenterId") String costCenterId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(StringUtils.checkString(businessUnitId), StringUtils.checkString(costCenterId));
			if (budget != null) {
				if (budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
					LOG.info("Budget is approved or active");
					DecimalFormat df = new DecimalFormat("#,###.######");
					headers.add("remainingAmt", "Remaining Amount : " + df.format(budget.getRemainingAmount()) + " " + budget.getBaseCurrency().getCurrencyCode());
					LOG.info("**************************budgget** " + budget.getTotalAmount());
					return new ResponseEntity(null, headers, HttpStatus.OK);
				} else {
					// PR cant be made if budget is not ACTIVE or APPROVED
					LOG.info("Budget is not  approved or active");
					headers.add("error", messageSource.getMessage("budget.pr.create.bu.notActive.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.info("Budget not created");
				headers.add("error", messageSource.getMessage("budget.pr.create.bu.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error While finding budget for CostCenter and Business  : " + e.getMessage(), e);
			headers.add("error", "Budget is not created for given costCenter And Business");
			return new ResponseEntity(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@RequestMapping(value = "/prTemplatePagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<PrTemplate>> prTemplatePagination(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength) {
		LOG.info("inside the search pr create controller : ");

		HttpHeaders headers = new HttpHeaders();
		List<PrTemplate> searchResultTemplate = null;
		TableData<PrTemplate> data = null;
		try {
			searchResultTemplate = prTemplateService.findTemplatesByTemplateNameForTenantId(searchValue, pageNo, pageLength, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			
			long prCount = prTemplateService.getTemplateCountBySearchValueForTenant(searchValue, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			
			data = new TableData<PrTemplate>(searchResultTemplate);
			data.setRecordsTotal(prCount);
		} catch (Exception e) {
			LOG.error("Error While searchPrTemplate :" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<PrTemplate>>(data, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/purchaseRequestPagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<Pr>> prPagination(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength) {
		LOG.info("inside the search pr create controller : ");

		HttpHeaders headers = new HttpHeaders();
		List<Pr> searchResultPr = null;
		TableData<Pr> data = null;
		try {
			searchResultPr = prService.findPrByNameForTenantId(searchValue, pageNo, pageLength, SecurityLibrary.getLoggedInUserTenantId(), (SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId()));
			for (Pr pr : searchResultPr) {
				pr.setCurrency(null);
				pr.setCostCenter(null);
				pr.setBusinessUnit(null);
				pr.setSupplier(null);
			}
			
			long prCount = prService.getPrCountBySearchValueForTenant(searchValue, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			
			data = new TableData<Pr>(searchResultPr);
			data.setRecordsTotal(prCount);
		} catch (Exception e) {
			LOG.error("Error While searchPrTemplate :" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<Pr>>(data, headers, HttpStatus.OK);
	}
	
}
