/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.PaymentTermsService;

/**
 * @author jayshree
 *
 */
@Controller
@RequestMapping(path = "/buyer")
public class PaymentTermsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	PaymentTermsService paymentTermesService;

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

	@RequestMapping(path = "/paymentTermes", method = RequestMethod.GET)
	public ModelAndView createPaymentTermes(@ModelAttribute PaymentTermes paymentTermes, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("paymentTermes", "paymentTermes", new PaymentTermes());
	}

	@RequestMapping(path = "/paymentTermes", method = RequestMethod.POST)
	public ModelAndView savePaymentTermes(@ModelAttribute PaymentTermes paymentTermes, BindingResult result, Model model, RedirectAttributes redir) {
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
				return new ModelAndView("paymentTermes", "paymentTermes", new PaymentTermes());
			} else {

				if (doValidate(paymentTermes)) {
					LOG.info("Buyer In PaymentTermes" + SecurityLibrary.getLoggedInUser().getBuyer());
					if (StringUtils.checkString(paymentTermes.getId()).length() == 0) {
						paymentTermes.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						paymentTermes.setCreatedBy(SecurityLibrary.getLoggedInUser());
						paymentTermes.setCreatedDate(new Date());
						paymentTermesService.savePaymentTermes(paymentTermes);
						redir.addFlashAttribute("success", messageSource.getMessage("paymenttermes.save.success", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
					} else {
						PaymentTermes persistObj = paymentTermesService.getPaymentTermesById(paymentTermes.getId());
						persistObj.setPaymentTermCode(paymentTermes.getPaymentTermCode());
						persistObj.setDescription(paymentTermes.getDescription());
						persistObj.setPaymentDays(paymentTermes.getPaymentDays());
						persistObj.setStatus(paymentTermes.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						paymentTermesService.updatePaymentTermes(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("paymenttermes.update.success", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
					}
				} else {

					model.addAttribute("errors", messageSource.getMessage("paymenttermes.error.duplicate", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("paymentTermes", "paymentTermes", paymentTermes);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the Payment Termes" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("paymenttermes.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			return new ModelAndView("paymentTermes", "paymentTermes", new PaymentTermes());
		}

		return new ModelAndView("redirect:listPaymentTerms");

	}

	private boolean doValidate(PaymentTermes paymentTermes) {
		boolean validate = true;
		if (paymentTermesService.isExists(paymentTermes, SecurityLibrary.getLoggedInUserTenantId())) {
			LOG.info("inside validation");
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listPaymentTerms", method = RequestMethod.GET)
	public String listPaymentTermes(Model model) {
		return "listPaymentTerms";
	}

	@RequestMapping(path = "/editPaymentTermes", method = RequestMethod.GET)
	public ModelAndView editPaymentTermes(@RequestParam String id, Model model) {
		LOG.info("Edit paymentTermes Called  " + id);
		PaymentTermes paymentTermes = paymentTermesService.getPaymentTermesById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("paymentTermes", "paymentTermes", paymentTermes);
	}


	@RequestMapping(path = "/deletePaymentTermes", method = RequestMethod.GET)
	public String deletePaymentTermes(@RequestParam String id, Model model,RedirectAttributes reAttributes) throws JsonProcessingException {
		PaymentTermes paymentTermes = paymentTermesService.getPaymentTermesById(id);
		try {
			if (paymentTermes != null) {
				paymentTermes.setModifiedBy(SecurityLibrary.getLoggedInUser());
				paymentTermes.setModifiedDate(new Date());
				paymentTermesService.deletePaymentTermes(paymentTermes);
				reAttributes.addFlashAttribute("success", messageSource.getMessage("paymenttermes.success.delete", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting payment terms , " + e.getMessage(), e);
			reAttributes.addFlashAttribute("error", messageSource.getMessage("paymenttermes.error.delete", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting payment terms , " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("paymenttermes.error.delete", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
//			reAttributes.addFlashAttribute("errors", messageSource.getMessage("paymenttermes.error.delete", new Object[] { paymentTermes.getPaymentTermCode() }, Global.LOCALE));
		}
		return "redirect:listPaymentTerms";

	}

	@RequestMapping(path = "/paymentTermesData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PaymentTermes>> paymentTermesData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<PaymentTermes> data = new TableData<PaymentTermes>(paymentTermesService.findPaymentTermesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = paymentTermesService.findTotalFilteredPaymentTermesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = paymentTermesService.findTotalPaymentTermesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PaymentTermes>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Payment Termes list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Payment Termes list : " + e.getMessage());
			return new ResponseEntity<TableData<PaymentTermes>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/paymentTermesTemplate", method = RequestMethod.GET)
	public void downloadPaymentTermesExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			paymentTermesService.paymentTermesDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Payment Terms is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PaymentTerms);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error while downloading payment Termes template :: " + e.getMessage(), e);
		}
	}

}
