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
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.StateEditor;
import org.apache.logging.log4j.*;
/**
 * @author Vipul
 */
@Controller
@RequestMapping(value = "/buyer")
public class BuyerAddressController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	private BuyerAddressService buyerAddressService;

	@Autowired
	private CountryService countryService;

	@Resource
	MessageSource messageSource;

	@Autowired
	private CountryEditor countryEditor;

	@Autowired
	private StateEditor stateEditor;

	@Autowired
	StateService stateService;

	@Autowired
	BuyerService buyerService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Country.class, "country", countryEditor);
		binder.registerCustomEditor(State.class, "state", stateEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/buyerAddress", method = RequestMethod.GET)
	public ModelAndView createBuyerAddress(@ModelAttribute BuyerAddress buyerAddress, Model model) {
		LOG.info("Creating  BuyerAddress Data: " + buyerAddress);
		model.addAttribute("countrys", countryService.getAllCountries());
		model.addAttribute("btnValue", "Save");
		model.addAttribute("btnValue2", "Create");

		return new ModelAndView("buyerAddress", "buyerAddressObject", new BuyerAddress());
	}

	@RequestMapping(path = "/buyerAddress", method = RequestMethod.POST)
	public ModelAndView saveBuyerAddress(@Valid @ModelAttribute("buyerAddressObject") BuyerAddress buyerAddress, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Saving the BuyerAddress Data : " + buyerAddress);
		List<String> errMessages = new ArrayList<String>();

		try {

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("btnValue", "Save");
				model.addAttribute("btnValue2", "Create");
				model.addAttribute("errors", errMessages);
				model.addAttribute("countrys", countryService.getAllCountries());
				return new ModelAndView("buyerAddress", "buyerAddressObject", new BuyerAddress());
			} else {

				LOG.info("Saving the BuyerAddress. Data : " + buyerAddress.getId());
				if (StringUtils.checkString(buyerAddress.getId()).length() == 0) {
					if (doValidate(buyerAddress)) {

						LOG.info("Saving the BuyerAddress. Data : ");
						buyerAddress.setBuyer(buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId()));
						buyerAddress.setCreatedBy(SecurityLibrary.getLoggedInUser());
						buyerAddress.setCreatedDate(new Date());
						buyerAddress.setStatus(buyerAddress.getStatus());
						buyerAddressService.createBuyerAddress(buyerAddress);
						redir.addFlashAttribute("success", messageSource.getMessage("buyeraddress.create.success", new Object[] {}, Global.LOCALE));
					} else {
						model.addAttribute("errors", messageSource.getMessage("buyeraddress.duplicate", new Object[] {}, Global.LOCALE));
						model.addAttribute("btnValue2", "Create");
						return new ModelAndView("buyerAddress", "buyerAddressObject", buyerAddress);
					}
				} else {

					LOG.info("updating the BuyerAddress. Data : " + buyerAddress.getId());

					LOG.info("update BuyerService Called");
					BuyerAddress persistObj = buyerAddressService.getBuyerAddress(buyerAddress.getId());
					
					Boolean goUpdate = false;
					if (persistObj.getTitle().equals(buyerAddress.getTitle())) {
						goUpdate = true;
					} else {
						if (doValidate(buyerAddress)) {
							goUpdate = true;
						} else {
							model.addAttribute("errors", messageSource.getMessage("buyeraddress.duplicate", new Object[] {}, Global.LOCALE));
							model.addAttribute("btnValue2", "Update");
							return new ModelAndView("buyerAddress", "buyerAddressObject", buyerAddress);
						}
					}
					System.out.println("UPDATE Go :: "+ goUpdate);
					if (goUpdate) {
						persistObj.setTitle(buyerAddress.getTitle());
						persistObj.setLine1(buyerAddress.getLine1());
						persistObj.setLine2(buyerAddress.getLine2());
						persistObj.setCity(buyerAddress.getCity());
						persistObj.setState(buyerAddress.getState());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setZip(buyerAddress.getZip());
						persistObj.setStatus(buyerAddress.getStatus());

						buyerAddressService.updateBuyerAddress(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("buyeraddress.update.success", new Object[] {}, Global.LOCALE));
					}

				}

			}
		} catch (Exception e) {
			LOG.info("Error While saving the buyeraddress" + e.getMessage());
			model.addAttribute("errors", messageSource.getMessage("buyeraddress.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("countrys", countryService.getAllCountries());
			return new ModelAndView("buyerAddress", "buyerAddressObject", new BuyerAddress());
		}

		return new ModelAndView("redirect:listBuyerAddress");
	}

	private boolean doValidate(BuyerAddress buyerAddress) {
		boolean validate = true;
		LOG.info("Enter Validation Block");
		if (buyerAddressService.isExists(buyerAddress, SecurityLibrary.getLoggedInUserTenantId())) {
			validate = false;
		}
		return validate;
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@RequestMapping(value = "/countryStatesList", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<State>> findStates(@RequestParam("countryId") String countryId) {
		List<State> stateList = null;
		try {
			LOG.info("Country  " + countryId);
			stateList = stateService.statesForCountry(countryId);
			if (stateList != null) {
				for (State state : stateList) {
					state.setCountry(null);
					state.setCreatedBy(null);
					state.setModifiedBy(null);
				}
			}
		} catch (Exception e) {
			LOG.info("Error while getting states for Country " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.error.state", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<State>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<State>>(stateList, HttpStatus.OK);
	}

	@RequestMapping(path = "/listBuyerAddress", method = RequestMethod.GET)
	public String getAllBuyerAddress(Model model) throws JsonProcessingException {
		List<BuyerAddressPojo> listBuyerAddress = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		for (BuyerAddressPojo pojo : listBuyerAddress) {
			pojo.setState(pojo.getState().createShallowCopy());
		}
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("listBuyerAddress", mapper.writeValueAsString(listBuyerAddress));
		return "listBuyerAddress";

	}

	@RequestMapping(path = "/buyerAddressEdit", method = RequestMethod.GET)
	public ModelAndView editBuyerAddress(@RequestParam String id, @ModelAttribute BuyerAddress buyerAddress, ModelMap model) {
		LOG.info("Edit Called ");
		BuyerAddress buyerAddress1 = buyerAddressService.getBuyerAddress(id);
		buyerAddress1.setCountry(buyerAddress1.getState().getCountry());
		model.addAttribute("countrys", countryService.getAllCountries());
		model.addAttribute("states", stateService.statesForCountry(buyerAddress1.getCountry().getId()));
		model.addAttribute("btnValue", "Update");
		model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("buyerAddress", "buyerAddressObject", buyerAddress1);

	}

	@RequestMapping(path = "/buyerAddressDelete", method = RequestMethod.GET)
	public String deleteBuyerAddress(@RequestParam String id, BuyerAddress buyerAddress, Model model) throws JsonProcessingException {
		buyerAddress.setId(id);
		try {
			buyerAddressService.deleteBuyerAddress(buyerAddress, SecurityLibrary.getLoggedInUser());
			model.addAttribute("success", messageSource.getMessage("buyeraddress.delete.success", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Deletion error occur " +e.getMessage() , e);
			model.addAttribute("error", messageSource.getMessage("buyeraddress.delete.error", new Object[] {}, Global.LOCALE));
		}

		List<BuyerAddressPojo> listBuyerAddress = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("listBuyerAddress", mapper.writeValueAsString(listBuyerAddress));
		return "listBuyerAddress";
	}

	@RequestMapping(path = "/buyerAddressData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BuyerAddress>> buyerAddressData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<BuyerAddress> data = new TableData<BuyerAddress>(buyerAddressService.findBuyerAddressForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			for (BuyerAddress addr : data.getData()) {
				if (addr.getState() != null) {
					addr.getState().setCreatedBy(null);
					addr.getState().setModifiedBy(null);
					addr.getState().getCountry().setCreatedBy(null);
					addr.getState().getCountry().setModifiedBy(null);
				}
			}

			data.setDraw(input.getDraw());
			long totalFilterCount = buyerAddressService.findTotalFilteredBuyerAddressForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = buyerAddressService.findTotalBuyerAddressForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<BuyerAddress>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Buyer Address list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Buyer Address list : " + e.getMessage());
			return new ResponseEntity<TableData<BuyerAddress>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/buyerAddressTemplate", method = RequestMethod.GET)
	public void downloadBuyerAddressListExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			buyerAddressService.buyerAddressDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}
	
	@RequestMapping(path = "/exportBuyerAddress", method = RequestMethod.GET)
	public void downloadBuyerAddressCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			File file = File.createTempFile("BuyerAddress", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			buyerAddressService.downloadBuyerAddressCsvFile(response, file,  SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Buyer Address is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.BuyerAddress);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=BuyerAddress.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}

}
