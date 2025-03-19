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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.CurrencyPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CurrencyService;

/**
 * @author Javed Ahmed
 * @author Ravi
 */
@Controller
@RequestMapping(path = "/admin")
public class CurrencyController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	CurrencyService currencyService;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}
	
	@RequestMapping(path = "/baseCurrency", method = RequestMethod.GET)
	public ModelAndView createBasicCurrency(@ModelAttribute Currency currency, Model model) {
		LOG.info("Creating baseCurrency: " + currency);
		LOG.info("Creating baseCurrency =" + currency.getId());
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("baseCurrency", "CurrencyObject", new Currency());
	}

	@RequestMapping(path = "/baseCurrency", method = RequestMethod.POST)
	public ModelAndView saveBaseCurrency(@Valid @ModelAttribute Currency currency, BindingResult result, Model model, @RequestParam String currencyName, RedirectAttributes redir) {
		LOG.info("Inside save Currency" + currency.getCurrencyName());
		HttpHeaders headers = new HttpHeaders();
		if (result.hasErrors()) {
			List<String> errMessages = new ArrayList<String>();
			for (FieldError err : result.getFieldErrors()) {
				LOG.info("ERROR : " + err.getField());
				errMessages.add(err.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);
			model.addAttribute("btnValue", "Create");
			return new ModelAndView("baseCurrency", "CurrencyObject", new Currency());
		} else {

			if (doValidate(currency, headers)) {

				if (StringUtils.checkString(currency.getId()).length() == 0) {
					currency.setCreatedBy(SecurityLibrary.getLoggedInUser());
					currency.setCreatedDate(new Date());
					currencyService.createCurrency(currency);
					redir.addFlashAttribute("success", messageSource.getMessage("Currency.create.success", new Object[] { currency.getCurrencyName() }, Global.LOCALE));
					return new ModelAndView("redirect:baseCurrencyList");
				} else {
					LOG.info("inside update currency id :" + currency.getId());
					Currency persistObj = currencyService.getCurrency(currency.getId());
					LOG.info("inside update currency persistobj :" + persistObj.getCurrencyName());

					persistObj.setCurrencyCode(currency.getCurrencyCode());
					persistObj.setCurrencyName(currency.getCurrencyName());
					persistObj.setStatus(currency.getStatus());

					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());

					currencyService.updateCurrency(persistObj);
					redir.addFlashAttribute("success", messageSource.getMessage("Currency.update.success", new Object[] { currency.getCurrencyName() }, Global.LOCALE));

					return new ModelAndView("redirect:baseCurrencyList");
				}
			} else {
				if (StringUtils.checkString(currency.getId()).length() == 0) {
					model.addAttribute("btnValue", "Create");
				} else {
					model.addAttribute("btnValue", "Update");
				}
				model.addAttribute("errors", messageSource.getMessage("Currency.error.duplicate", new Object[] { currency.getCurrencyName() }, Global.LOCALE));
				return new ModelAndView("baseCurrency", "CurrencyObject", currency);

			}
		}
	}

	@RequestMapping(path = "/baseCurrencyList")
	public String baseCurrencyList(Model model) throws JsonProcessingException {
		return "baseCurrencyList";
	}

	@RequestMapping(path = "/currencyListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<CurrencyPojo>> currencyListData(@RequestParam("start") int start, @RequestParam("length") int length, @RequestParam("sort") String sort) throws JsonProcessingException {
		LOG.info("Start : " + start + " Length : " + length + " Sort : " + sort);
		TableData<CurrencyPojo> data = new TableData<CurrencyPojo>(currencyService.getAllCurrencyPojo(start, length, sort));
		List<Currency> totalRecord = currencyService.getAllCurrency();
		data.setRecordsTotal(totalRecord != null ? totalRecord.size() : 0);
		return new ResponseEntity<TableData<CurrencyPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/editCurrency", method = RequestMethod.GET)
	public ModelAndView editCurrency(@RequestParam String id, Model model) {
		LOG.info("Getting the Currency. : " + id);
		Currency currency = currencyService.getCurrency(id);
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("baseCurrency", "CurrencyObject", currency);
	}

	@RequestMapping(path = "/deleteCurrency", method = RequestMethod.GET)
	public String deleteState(@RequestParam String id, Currency currency, Model model) throws JsonProcessingException {
		LOG.info("Deleting the Currency for Id : " + id);
		currency.setId(id);
		Currency newCurrency = currencyService.getCurrency(id);
		model.addAttribute("info", messageSource.getMessage("Currency.delete.success", new Object[] { newCurrency.getCurrencyName() }, Global.LOCALE));
		currency.setModifiedDate(new Date());
		currencyService.deleteCurrency(currency,SecurityLibrary.getLoggedInUser());
		return "baseCurrencyList";
	}

	private boolean doValidate(Currency Currency, HttpHeaders headers) {
		boolean validate = true;
		if (currencyService.isExists(Currency)) {
			LOG.info("inside validation");
			validate = false;
		}
		return validate;
	}
	
	@RequestMapping(path = "/baseCurrencyData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Currency>> baseCurrencyData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<Currency> data = new TableData<Currency>(currencyService.findAllCurrencyList(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = currencyService.findTotalFilteredCurrencyList(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = currencyService.findTotalCurrencyList();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Currency>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Base Currency list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Base Currency list : " + e.getMessage());
			return new ResponseEntity<TableData<Currency>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	
}
