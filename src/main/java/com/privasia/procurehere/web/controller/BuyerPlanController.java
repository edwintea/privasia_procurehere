package com.privasia.procurehere.web.controller;

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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.web.editors.CurrencyEditor;

/**
 * @author parveen
 */
@Controller
@RequestMapping(value = "/admin/plan")
public class BuyerPlanController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerPlanService buyerPlanService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("statusList")
	public List<PlanStatus> getStatusList() {
		return Arrays.asList(PlanStatus.values());
	}

	@ModelAttribute("planTypeList")
	public List<PlanType> getPlanTypeList() {
		return Arrays.asList(PlanType.values());
	}

	@RequestMapping(path = "/buyerPlan", method = RequestMethod.GET)
	public ModelAndView createBuyerPlan(Model model) {
		LOG.info("Create Buyer Plan called");
		model.addAttribute("btnValue", "Create");
		model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
		model.addAttribute("buyerPlan", new BuyerPlan());
		return new ModelAndView("buyerPlan");
	}

	// TODO bean validation and UI validation
	@RequestMapping(path = "/saveBuyerPlan", method = RequestMethod.POST)
	public ModelAndView saveBuyerPlan(@Valid @ModelAttribute("buyerPlan") BuyerPlan buyerPlan, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save buyer Plan called : " + (buyerPlan != null ? buyerPlan.getPlanName() : "null"));
		LOG.info("Buyer plan id :" + buyerPlan.getId());
		try {
			if (StringUtils.checkString(buyerPlan.getId()).length() == 0) {
				buyerPlan.setCreatedBy(SecurityLibrary.getLoggedInUser());
				buyerPlan.setCreatedDate(new Date());
				buyerPlanService.saveBuyerPlan(buyerPlan);
			} else {
				LOG.info("Buyer plan id :" + buyerPlan.getId());
				buyerPlan.setModifiedBy(SecurityLibrary.getLoggedInUser());
				buyerPlan.setModifiedDate(new Date());
				buyerPlanService.updateBuyerPlan(buyerPlan);
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Plan is in use!!!. Error while deleting Plan [ " + buyerPlan.getPlanName() + " ]" + e.getMessage(), e);
			redir.addFlashAttribute("errors", messageSource.getMessage("plan.error.update", new Object[] { buyerPlan.getPlanName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while saving Buyer plan : " + e.getMessage(), e);
			model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
			model.addAttribute("buyerPlan", buyerPlan);
			model.addAttribute("error", messageSource.getMessage("buyer.save.plan", new Object[] { buyerPlan.getPlanName() }, Global.LOCALE));
			return new ModelAndView("buyerPlan");
		}
		return new ModelAndView("redirect:buyerPlanList");
	}

	@RequestMapping(path = "/buyerPlanList", method = RequestMethod.GET)
	public String planList(Model model) {
		return "buyerPlanList";
	}

	@RequestMapping(path = "/buyerPlanData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BuyerPlan>> planData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<BuyerPlan> data = new TableData<BuyerPlan>(buyerPlanService.findBuyerPlans(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = buyerPlanService.findTotalFilteredBuyerPlans(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = buyerPlanService.findTotalActiveBuyerPlans();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<BuyerPlan>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<BuyerPlan>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/editBuyerPlan/{id}", method = RequestMethod.GET)
	public String editPlan(@PathVariable(name = "id") String id, Model model, RedirectAttributes redir) {
		LOG.info("Getting the Buyer Plan. : " + id);
		try {
			BuyerPlan buyerPlan = buyerPlanService.getBuyerPlanForEditById(id);
			// Error condition. Send the user back to listing screen.
			if (buyerPlan == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
				return "redirect:/admin/plan/buyerPlanList";
			}
			model.addAttribute("currencyList", currencyService.getAllCurrency());
			model.addAttribute("buyerPlan", buyerPlan);

			// To check Plan in use or not
			boolean inUse = buyerPlanService.checkBuyerPlanInUse(id);
			model.addAttribute("planInUse", inUse);

			return "buyerPlan";
		} catch (Exception e) {
			LOG.error("Error while editPlan  :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/admin/plan/buyerPlanList";
		}
	}

	@RequestMapping(path = "/deleteBuyerPlan/{id}", method = RequestMethod.GET)
	public String deleteBuyerPlan(@PathVariable(name = "id") String id, Model model, RedirectAttributes redir) {
		LOG.info("Delete buyer Plan called ");
		BuyerPlan buyerPlan = buyerPlanService.getBuyerPlanForEditById(id);
		// Error condition. Send the user back to listing screen.
		if (buyerPlan == null) {
			redir.addFlashAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:/admin/plan/buyerPlanList";
		}
		try {
			buyerPlanService.deleteBuyerPlan(buyerPlan);
			redir.addFlashAttribute("success", messageSource.getMessage("plan.success.delete", new Object[] { buyerPlan.getPlanName() }, Global.LOCALE));
		} catch (DataIntegrityViolationException e) {
			LOG.error("Plan is in use!!!. Error while deleting Plan [ " + buyerPlan.getPlanName() + " ]" + e.getMessage(), e);
			redir.addFlashAttribute("errors", messageSource.getMessage("plan.error.delete", new Object[] { buyerPlan.getPlanName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Plan [ " + buyerPlan.getPlanName() + " ]" + e.getMessage(), e);
			redir.addFlashAttribute("errors", messageSource.getMessage("plan.error.delete", new Object[] { buyerPlan.getPlanName() }, Global.LOCALE));
		}
		return "redirect:/admin/plan/buyerPlanList";
	}
}
