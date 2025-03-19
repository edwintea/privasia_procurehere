package com.privasia.procurehere.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventIdSettingsService;

@Controller
@RequestMapping(path = "/supplier")
public class SupplierIdSettingsController {
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	MessageSource messageSource;

	@GetMapping("/supplierIdSettingsList")
	public String listId(IdSettings idSettings, Model model) throws JsonProcessingException {
		return "supplierIdSettingsList";
	}

	@GetMapping("/supplierIdSettingsListData")
	public ResponseEntity<TableData<IdSettings>> supplierIdSettingsListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<IdSettings> data = new TableData<IdSettings>(eventIdSettingsService.findAllIdSettingsList(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = eventIdSettingsService.findTotalFilteredCountryList(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = eventIdSettingsService.findTotalIdSetList(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<IdSettings>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching IdSettings list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching IdSettings list : " + e.getMessage());
			return new ResponseEntity<TableData<IdSettings>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/editIdSettings")
	public ModelAndView editIdSettings(@RequestParam String id, Model model) {
		IdSettings idSettings = eventIdSettingsService.getIdSettingsById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
	}

	// @GetMapping("/supplierIdSettings")
	// public ModelAndView idSettingsCreate(@ModelAttribute IdSettings idSettings, Model model) {
	// LOG.info("idSettingsCreate Controller.... called");
	// model.addAttribute("btnValue", "Create");
	// return new ModelAndView("supplierIdSettings", "idSettings", new IdSettings());
	// }

	@PostMapping("/supplierIdSettings")
	public ModelAndView saveIdSettings(@Valid @ModelAttribute IdSettings idSettings, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("saveIdSettings Method Called " + idSettings.getId());
		String fieldValue = null;
		model.addAttribute("btnValue", "Create");
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					LOG.info(oe.getObjectName() + " - " + oe.getDefaultMessage());
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
			} else {
				if (doValidate(idSettings, SecurityLibrary.getLoggedInUserTenantId())) {
					if (StringUtils.checkString(idSettings.getId()).length() == 0) {
						fieldValue = idSettings.getIdDatePattern();
						boolean isValid = isValidDate(fieldValue);
						HttpHeaders headers = new HttpHeaders();
						if (isValid == true) {
							headers.add("success", "*** Date is Valid ***");
						} else {
							headers.add("error", "*** Date is Not Valid ***");
							model.addAttribute("error", messageSource.getMessage("idSettings.dtPattern.error", new Object[] { idSettings.getIdDatePattern() }, Global.LOCALE));
							return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
						}

						eventIdSettingsService.createIdSettings(idSettings);
						redir.addFlashAttribute("success", messageSource.getMessage("idSet.save.success", new Object[] { idSettings.getIdType() }, Global.LOCALE));
						LOG.info("create idSettings Called" + SecurityLibrary.getLoggedInUser());
					} else {
						IdSettings persistObj = eventIdSettingsService.getIdSettingsById(idSettings.getId());
						persistObj.setIdType(idSettings.getIdType());
						// persistObj.setIdSequence(idSettings.getIdSequence());
						persistObj.setIdPerfix(idSettings.getIdPerfix());
						persistObj.setIdDelimiter(idSettings.getIdDelimiter());
						persistObj.setIdDatePattern(idSettings.getIdDatePattern());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setPaddingLength(idSettings.getPaddingLength());
						persistObj.setModifiedDate(new Date());
						// persistObj.setIdSettingPattern(idSettings.getIdSettingPattern());
						// persistObj.setIdSettingType(idSettings.getIdSettingType());
						fieldValue = idSettings.getIdDatePattern();
						boolean isValid = isValidDate(fieldValue);
						HttpHeaders headers = new HttpHeaders();
						if (isValid == true) {
							headers.add("success", "*** Date is Valid ***");
						} else {
							headers.add("error", "*** Date is Not Valid ***");
							model.addAttribute("error", messageSource.getMessage("idSettings.dtPattern.error", new Object[] { idSettings.getIdDatePattern() }, Global.LOCALE));
							return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
						}
						eventIdSettingsService.updateIdSettings(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("idSet.update.success", new Object[] { persistObj.getIdType() }, Global.LOCALE));
						LOG.info("update IdSettings Called");
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("idSettings.error.duplicate", new Object[] { idSettings.getIdType() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
				}
			}
		} catch (Exception e) {
			LOG.error("Error in saving IdSettings " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("idSettings.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("supplierIdSettings", "idSettings", idSettings);
		}
		return new ModelAndView("redirect:supplierIdSettingsList");
	}

	private boolean doValidate(IdSettings idSettings, String tenantId) {
		boolean validate = true;
		if (eventIdSettingsService.isExists(idSettings, tenantId)) {
			LOG.info("false called");
			validate = false;
		}
		return validate;
	}

	private boolean isValidDate(String fieldValue) {
		boolean valid = true;
		try {
			LOG.info("*** dateCheck isValidDate(fieldValue) called ***" + fieldValue);
			SimpleDateFormat formatter = new SimpleDateFormat(fieldValue);
			Date parsedDate = formatter.parse(formatter.format(new Date()));
			LOG.info(formatter.parse(formatter.format(new Date())));
			LOG.info(parsedDate + "parsedDate &&& String " + formatter.format(new Date()));
		} catch (Exception e) {
			LOG.info("*** ParseException called ***");
			valid = false;
		}
		return valid;
	}
	
	@PostMapping("/showPreView")
	public @ResponseBody ResponseEntity<String> showPreView(@RequestParam String type, @RequestParam String length, @RequestParam String preFix, @RequestParam String delimiter, @RequestParam String datePattern) {
		HttpHeaders headers = new HttpHeaders();

		LOG.info("=============type:"+type
				+"=============length:"+length
				+"=============preFix:"+preFix
				+"=============delimiter:"+delimiter
				+"=============datePattern:"+datePattern
				);
		
		
		String id = "";

		try {

			if (StringUtils.checkString(datePattern).length() > 0) {
				SimpleDateFormat df = new SimpleDateFormat(datePattern);
				datePattern = df.format(new Date());

			}

			String squence = StringUtils.lpad("" + 1, Integer.parseInt(length), '0');
			id = getIdByPatternOrder(preFix, datePattern, delimiter, squence);

//			switch (pattern) {
//			
//			case "PRE_DATE_DEL_BU_DEL_NNNNN":
//				id = getIdByPatternOrder(preFix, datePattern, delimiter, "BU", delimiter, squence);
//				break;
//			
//			case "PRE_DATE_DEL_NNNNN_DEL_BU":
//
//				id = getIdByPatternOrder(preFix, datePattern, delimiter, squence, delimiter, "BU");
//
//				break;
//			case "PRE_DEL_BU_DEL_DATE_DEL_NNNN":
//				id = getIdByPatternOrder(preFix, delimiter, "BU",delimiter, datePattern, delimiter, squence);
//				break;
//			case "PRE_DEL_BU_DEL_DATE_NNNN":
//				id = getIdByPatternOrder(preFix, delimiter, "BU",delimiter, datePattern, squence);
//				break;
//			case "PRE_DEL_DATE_DEL_NNNN_DEL_BU":
//				id = getIdByPatternOrder(preFix, delimiter, datePattern, delimiter, squence, delimiter, "BU");
//				break;
//			case "PRE_DEL_DATE_NNNN_DEL_BU":
//				id = getIdByPatternOrder(preFix, delimiter, datePattern, squence, delimiter, "BU");
//				break;
//			default:
//				id = getIdByPatternOrder(preFix, delimiter, datePattern, delimiter, squence);
//
//				break;
//
//			}
		} catch (Exception e) {
			LOG.error("Error while showinig preview " + e.getMessage(), e);
			headers.add("error", "Error while Showing Preview");
			return new ResponseEntity<String>("error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(id, headers, HttpStatus.OK);
	}
	
	
	private String getIdByPatternOrder(String... args) {
		String id = "";
		for (String string : args) {
			id += StringUtils.checkString(string);
		}
		return id;
	}

}