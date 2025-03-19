/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.ChargeModel;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.web.editors.CurrencyEditor;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping(value = "/admin/supplierplan")
public class SupplierPlanController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	SupplierPlanService supplierplanService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	SubscriptionService subscriptionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("periodUnitTypeList")
	public List<PeriodUnitType> getPeriodUnitTypeList() {
		return Arrays.asList(PeriodUnitType.values());
	}

	@ModelAttribute("chargeModelList")
	public List<ChargeModel> getChargeModelList() {
		return Arrays.asList(ChargeModel.values());
	}

	@ModelAttribute("statusList")
	public List<PlanStatus> getStatusList() {
		return Arrays.asList(PlanStatus.values());
	}

	@RequestMapping(path = "/supplierplanList", method = RequestMethod.GET)
	public String planList(Model model) {
		return "supplierplanList";
	}

	@RequestMapping(path = "/planData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierPlan>> planData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<SupplierPlan> data = new TableData<SupplierPlan>(supplierplanService.findPlans(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = supplierplanService.findTotalFilteredPlans(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = supplierplanService.findTotalPlans();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<SupplierPlan>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Supplier plan list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Supplier plan list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPlan>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/supplierplan", method = RequestMethod.GET)
	public ModelAndView createPlan(Model model) {
		model.addAttribute("btnValue", "Create");
		model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
		model.addAttribute("plan", new SupplierPlan());
		return new ModelAndView("supplierplan");
	}

	@RequestMapping(path = "/editPlan/{id}", method = RequestMethod.GET)
	public String editPlan(@PathVariable(name = "id") String id, Model model, RedirectAttributes redir) {
		LOG.info("Getting the Supplier Plan. : " + id);
		SupplierPlan plan = supplierplanService.getPlanForEditById(id);
		
		// Error condition. Send the user back to listing screen.
		if (plan == null) {
			redir.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:supplierplanList";
		}
		model.addAttribute("inUse", subscriptionService.isSupplierPlanInUse(id));
		model.addAttribute("btnValue", "Update");
		model.addAttribute("currencyList", currencyService.getAllCurrency());
		model.addAttribute("plan", plan);
		return "supplierplan";
	}

	@RequestMapping(path = "/savePlan", method = RequestMethod.POST)
	public ModelAndView savePlan(@Valid @ModelAttribute("plan") SupplierPlan plan, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save Supplier Plan called : " + plan.toLogString());
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("currencyList", currencyService.getAllCurrency());
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", errMessages);
				return new ModelAndView("supplierplan", "plan", plan);
			} else {

				if (doValidate(plan)) {

					if (StringUtils.checkString(plan.getId()).length() == 0) {
						plan.setCreatedBy(SecurityLibrary.getLoggedInUser());
						plan.setCreatedDate(new Date());
						LOG.info("PERIOD TYPE : " + plan.getPeriodUnit());
						supplierplanService.savePlan(plan);
						redir.addFlashAttribute("success", messageSource.getMessage("plan.save.success", new Object[] { plan.getPlanName() }, Global.LOCALE));
						LOG.info("create Plan Called by : " + SecurityLibrary.getLoggedInUser());
					} else {
						SupplierPlan persistObj = supplierplanService.getPlanForEditById(plan.getId());
						persistObj.setPlanName(plan.getPlanName());
						persistObj.setPlanOrder(plan.getPlanOrder());
						persistObj.setChargeModel(plan.getChargeModel());
						persistObj.setDescription(plan.getDescription());
						persistObj.setShortDescription(plan.getShortDescription());
						persistObj.setPlanStatus(plan.getPlanStatus());
						persistObj.setPeriodUnit(plan.getPeriodUnit());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setPeriod(plan.getPeriod());
						persistObj.setPrice(plan.getPrice());
						persistObj.setCurrency(plan.getCurrency());
						persistObj.setBuyerLimit(plan.getBuyerLimit());
						supplierplanService.updatePlan(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("plan.update.success", new Object[] { plan.getPlanName() }, Global.LOCALE));
						LOG.info("update Supplier Plan Called");
					}
				} else {
					LOG.info("Validation error ...............");
					model.addAttribute("error", messageSource.getMessage("plan.error.duplicate", new Object[] { plan.getPlanName() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					model.addAttribute("currencyList", currencyService.getAllCurrency());
					return new ModelAndView("supplierplan", "plan", plan);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while saving the Supplier Plan : " + e.getMessage(), e);
//			model.addAttribute("error", "Error while saving the Supplier Plan : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.saving.supplierplan", new Object[] {e.getMessage()}, Global.LOCALE));
			model.addAttribute("currencyList", currencyService.getAllCurrency());
			return new ModelAndView("supplierplan", "plan", plan);
		}
		return new ModelAndView("redirect:supplierplanList");
	}

	private boolean doValidate(SupplierPlan plan) {
		boolean validate = true;
		if (supplierplanService.isExists(plan)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/deletePlan/{id}", method = RequestMethod.GET)
	public String deletePlan(@PathVariable(name = "id") String id, Model model) {
		LOG.info("Delete Plan called ");
		SupplierPlan plan = supplierplanService.getPlanForEditById(id);
		// Error condition. Send the user back to listing screen.
		if (plan == null) {
			model.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:supplierplanList";
		}
		try {
			supplierplanService.deletePlan(plan);
			model.addAttribute("success", messageSource.getMessage("plan.success.delete", new Object[] { plan.getPlanName() }, Global.LOCALE));
		} catch (DataIntegrityViolationException e) {
			LOG.error("Plan is in use!!!. Error while deleting Plan [ " + plan.getPlanName() + " ]" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("plan.error.delete", new Object[] { plan.getPlanName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Plan [ " + plan.getPlanName() + " ]" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("plan.error.delete", new Object[] { plan.getPlanName() }, Global.LOCALE));
		}
		return "supplierplanList";
	}

}
