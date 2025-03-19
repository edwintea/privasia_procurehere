package com.privasia.procurehere.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Supplier;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.UsageLimitType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.BuyerPlanEditor;
import com.privasia.procurehere.web.editors.SupplierEditor;
import com.privasia.procurehere.web.editors.SupplierPlanEditor;

/**
 * @author parveen
 * @author vipul
 */
@Controller
@RequestMapping(value = "/admin")
public class PromotionalCodeController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	BuyerService buyerService;
	
	@Autowired
	SupplierService supplierService;
	
	
	@Autowired
	SupplierPlanService supplierPlanService;
	
	
	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	BuyerEditor buyerEditor;
	
	@Autowired
	SupplierEditor supplierEditor;
	
	@Autowired
	BuyerPlanEditor buyerPlanEditor;
	
	@Autowired
	SupplierPlanEditor supplierPlanEditor;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
		binder.registerCustomEditor(Supplier.class, supplierEditor);

		binder.registerCustomEditor(BuyerPlan.class, buyerPlanEditor);
		binder.registerCustomEditor(SupplierPlan.class, supplierPlanEditor);



		// TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		// if (strTimeZone != null) {
		// timeZone = TimeZone.getTimeZone(strTimeZone);
		// }
		SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
		// formatterDate.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "effectiveDate", new CustomDateEditor(formatterDate, true));
		binder.registerCustomEditor(Date.class, "expiryDate", new CustomDateEditor(formatterDate, true));

	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@ModelAttribute("discountList")
	public List<ValueType> getDiscountTypeList() {
		return Arrays.asList(ValueType.values());
	}

	@ModelAttribute("usageLimitTypeList")
	public List<UsageLimitType> getUsageLimitTypeList() {
		return Arrays.asList(UsageLimitType.values());
	}

	@RequestMapping(path = "/promotionalCodeList")
	public String listpromotionalCode(Model model) throws JsonProcessingException {
		return "promotionalCodeList";
	}

	@RequestMapping(path = "/promotionalCode", method = RequestMethod.GET)
	public ModelAndView promotionalCodeCreate(@ModelAttribute PromotionalCode promotionalCode, Model model) {
		LOG.info("promotionalCodeCreate.... called");
		model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
		model.addAttribute("supplierList", supplierService.findAllactiveSuppliers());
		model.addAttribute("supplierPlanList", supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE));
		model.addAttribute("buyerplanList", buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE));

		return new ModelAndView("promotionalCode", "promotionObject", new PromotionalCode());
	}

	@RequestMapping(path = "/promotionalCode", method = RequestMethod.POST)
	public ModelAndView savepromotionalCode(@Valid @ModelAttribute PromotionalCode promotionalCode, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save promotional Code Method Called ");
		List<String> errMessages = new ArrayList<String>();
		try {

			if (promotionalCode.getEffectiveDate() != null && promotionalCode.getExpiryDate() != null && promotionalCode.getEffectiveDate().after(promotionalCode.getExpiryDate())) {
				model.addAttribute("error", messageSource.getMessage("promotion.error.dateCheck", new Object[] {}, Global.LOCALE));
				model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
				return new ModelAndView("promotionalCode", "promotionObject", promotionalCode);
			}
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					LOG.info(oe.getObjectName() + " - " + oe.getDefaultMessage());
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
				model.addAttribute("errors", errMessages);
				return new ModelAndView("promotionalCode", "promotionObject", promotionalCode);
			} else {
				LOG.info("Saving the promotionalCode : " + promotionalCode.getId());
				if (StringUtils.checkString(promotionalCode.getId()).length() == 0) {
					if (doValidate(promotionalCode)) {

						promotionalCode.setCreatedBy(SecurityLibrary.getLoggedInUser());
						promotionalCode.setCreatedDate(new Date());
						promotionalCode.setStatus(promotionalCode.getStatus());
						model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
						promotionalCodeService.savePromotionCode(promotionalCode);
						redir.addFlashAttribute("success", messageSource.getMessage("promotion.create.success", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
					} else {
						model.addAttribute("errors", messageSource.getMessage("promotion.error.duplicate", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
						model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
						return new ModelAndView("promotionalCode", "promotionObject", promotionalCode);
					}
				} else {
					PromotionalCode persistObj = promotionalCodeService.getPromotionalCodeById(promotionalCode.getId());
					if (persistObj.getInUse().equals(false)) {

						persistObj.setPromoCode(promotionalCode.getPromoCode());
						persistObj.setPromoName(promotionalCode.getPromoName());
						persistObj.setPromoDiscount(promotionalCode.getPromoDiscount());
						persistObj.setDiscountType(promotionalCode.getDiscountType());
						persistObj.setUsageLimit(promotionalCode.getUsageLimit());
						persistObj.setUsageLimitType(promotionalCode.getUsageLimitType());
						persistObj.setBuyer(promotionalCode.getBuyer());
						persistObj.setSupplier(promotionalCode.getSupplier());
						persistObj.setSupplierPlan(promotionalCode.getSupplierPlan());
						persistObj.setBuyerPlan(promotionalCode.getBuyerPlan());
						persistObj.setEffectiveDate(promotionalCode.getEffectiveDate());
						persistObj.setExpiryDate(promotionalCode.getExpiryDate());
						persistObj.setStatus(promotionalCode.getStatus());
						model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
						model.addAttribute("supplierList", supplierService.findAllactiveSuppliers());
						model.addAttribute("supplierPlanList", supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE));
						model.addAttribute("buyerplanList", buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE));
						promotionalCodeService.updatePromotionalCode(persistObj, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
					} else {
						persistObj.setStatus(promotionalCode.getStatus());
						model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
						
						promotionalCodeService.updatePromotionalCode(persistObj, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
					}
					redir.addFlashAttribute("success", messageSource.getMessage("promotion.update.success", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
				}
			}
		} catch (Exception e) {
			LOG.error("Error in saving PromotionalCode " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("promotion.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
			return new ModelAndView("promotionalCode", "promotionObject", promotionalCode);
		}
		return new ModelAndView("redirect:promotionalCodeList");
	}

	private boolean doValidate(PromotionalCode promotionalCode) {
		boolean validate = true;
		if (promotionalCodeService.isExists(promotionalCode)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/editPromotionalCode", method = RequestMethod.GET)
	public ModelAndView editPromotionalCode(@RequestParam String id, Model model) throws JsonProcessingException {
		PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeById(id);
		model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
		model.addAttribute("supplierList", supplierService.findAllactiveSuppliers());
		model.addAttribute("supplierPlanList", supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE));
		model.addAttribute("buyerplanList", buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE));
		return new ModelAndView("promotionalCode", "promotionObject", promotionalCode);
	}

	@RequestMapping(path = "/deletePromotionalCode", method = RequestMethod.GET)
	public String deletePromotionalCode(@RequestParam String id, PromotionalCode promotionalCode, Model model) throws JsonProcessingException {
		promotionalCode = promotionalCodeService.getPromotionalCodeById(id);
		try {
			if (promotionalCode != null) {
				if (promotionalCode.getInUse().equals(false)) {
					promotionalCodeService.deletePromotionalCode(promotionalCode, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
					model.addAttribute("success", messageSource.getMessage("promotion.success.delete", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
				} else {
					LOG.error("Error while deleting Promotional Code ");
					model.addAttribute("error", messageSource.getMessage("promotion.error.delete.use", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
				}
			}
		} catch (Exception e) {
			LOG.error("Error while deleting Promotional Code , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("promotion.error.delete", new Object[] { promotionalCode.getPromoCode() }, Global.LOCALE));
		}
		return "promotionalCodeList";
	}

	@RequestMapping(path = "/promotionalCodeData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PromotionalCode>> promotionalCodeData(TableDataInput input) throws JsonProcessingException {
		try {
			TableData<PromotionalCode> data = new TableData<PromotionalCode>(promotionalCodeService.findPromotionCodeForTenant(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = promotionalCodeService.findTotalFilteredPromotionalCodeList(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = promotionalCodeService.findTotalPromotionalCodeList();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PromotionalCode>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Promotion Code  : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error Fetching Promotion Code List : " + e.getMessage());
			return new ResponseEntity<TableData<PromotionalCode>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
