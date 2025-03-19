/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.math.BigDecimal;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpAudit;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ErpAuditType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.PrToAuctionDetailsErpPojo;
import com.privasia.procurehere.core.pojo.PrToAuctionErpPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.ErpAuditService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.web.editors.RfxTemplateEditor;

/**
 * @author parveen
 */
@Controller
@RequestMapping(path = "/buyer")
public class ErpSetupController {

	private static final Logger LOG = LogManager.getLogger(Global.ERP_LOG);

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String HMAC_SHA512 = "HmacSHA256";
	private static final String SECRET_KEY = "xyz0123456789";

	@Resource
	MessageSource messageSource;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	RfxTemplateEditor rfxTemplateEditor;

	@Autowired
	ErpAuditService erpAuditService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	BuyerAuditTrailService buyerAuditTrailService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(RfxTemplate.class, rfxTemplateEditor);
	}

	@RequestMapping(value = "/erpSetup", method = RequestMethod.GET)
	public String getErpSetup(Model model) {
		LOG.info("Erp Setup get called");
		try {
			ErpSetup erpSetup = erpSetupService.findErpByWithTepmlateTenantId(SecurityLibrary.getLoggedInUserTenantId());
			// IF exist in DB
			if (erpSetup == null) {
				erpSetup = new ErpSetup();
				erpSetup.setType(RfxTypes.RFA);
			}
			if (erpSetup.getType() != null) {
				switch (erpSetup.getType()) {
				case RFA:
					erpSetup.setRfxTemplate(erpSetup.getRfaTemplate());
					break;
				case RFP:
					erpSetup.setRfxTemplate(erpSetup.getRfpTemplate());
					break;
				case RFQ:
					erpSetup.setRfxTemplate(erpSetup.getRfqTemplate());
					break;
				case RFT:
					erpSetup.setRfxTemplate(erpSetup.getRftTemplate());
					break;
				default:
					break;
				}
				model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), erpSetup.getType()));
			}
			model.addAttribute("erpSetup", erpSetup);
			// List<ErpAudit> auditList =
			// erpAuditService.getAllAuditByTenantIdAndActionType(SecurityLibrary.getLoggedInUserTenantId(),
			// ErpAuditType.PENDING);
			// model.addAttribute("erpEventAuditList", auditList);
			model.addAttribute("eventTypeList", Arrays.asList(RfxTypes.RFA, RfxTypes.RFT, RfxTypes.RFP, RfxTypes.RFQ));
		} catch (Exception e) {
			LOG.error("Error while get erp setup :" + e.getMessage(), e);
		}
		return "erpSetup";
	}

	@RequestMapping(value = "/erpSetup", method = RequestMethod.POST)
	public String saveOrUpdateErpSetup(@ModelAttribute("erpSetup") ErpSetup erpSetup, RedirectAttributes redir) {
		LOG.info("Erp Setup POST called");
		try {
			BuyerAuditTrail audit = new BuyerAuditTrail();
			if (StringUtils.checkString(erpSetup.getId()).isEmpty()) {

				if (erpSetup.getRfxTemplate() != null) {
					erpSetup.setCreateEventAuto(Boolean.TRUE);
				} else {
					erpSetup.setCreateEventAuto(Boolean.FALSE);
				}
				erpSetup.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				erpSetup = erpSetupService.save(erpSetup);
				audit.setActivity(AuditTypes.CREATE);
				audit.setDescription("ERP setting created");

			} else {
				ErpSetup erpSetupDb = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
				erpSetupDb.setAppId(erpSetup.getAppId());
				erpSetupDb.setIsErpEnable(erpSetup.getIsErpEnable());
				erpSetupDb.setAwardInterfaceTypePull(erpSetup.getAwardInterfaceTypePull());
				erpSetupDb.setErpUrl(erpSetup.getErpUrl());
				erpSetupDb.setType(erpSetup.getType());
				erpSetupDb.setIsGeneratePo(erpSetup.getIsGeneratePo());
				erpSetupDb.setRfxTemplate(erpSetup.getRfxTemplate());
				erpSetupDb.setAwardInterfaceTypePull(erpSetup.getAwardInterfaceTypePull());
				erpSetup = erpSetupService.save(erpSetupDb);
				audit.setDescription("ERP Setting Changed");
				audit.setActivity(AuditTypes.UPDATE);
			}
			audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setModuleType(ModuleType.ERP);
			buyerAuditTrailService.save(audit);

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.setup.saved", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while save erp setup :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.dave.erpsetup", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:erpSetup";
	}

	/*
	 * private void updateSecurityLibraryUser(Boolean isBuyerErpEnable) { UPDATE THE SECURITY CONTEXT AS THE
	 * isBuyerErpEnable IS NOW CHANGED. // gonna need this to get user from Acegi SecurityContext ctx =
	 * SecurityContextHolder.getContext(); Authentication auth = ctx.getAuthentication(); // get user obj
	 * AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal(); // update the bq Page length on the user
	 * obj authUser.setIsBuyerErpEnable(isBuyerErpEnable); UsernamePasswordAuthenticationToken upat = new
	 * UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
	 * upat.setDetails(auth.getDetails()); ctx.setAuthentication(upat); }
	 */

	@RequestMapping(value = "/viewErpEventDetail/{auditId}", method = RequestMethod.GET)
	public String viewErpEventDetail(@PathVariable("auditId") String auditId, Model model) {
		LOG.info("view Erp Event Detail called Id :" + auditId);
		try {
			ErpAudit eventDetail = erpAuditService.findById(auditId);
			model.addAttribute("eventDetail", eventDetail);
			ObjectMapper mapper = new ObjectMapper();

			PrToAuctionErpPojo payload = mapper.readValue(eventDetail.getPayload(), PrToAuctionErpPojo.class);
			LOG.info("payload curr :" + payload.getCurr());

			model.addAttribute("eventPayload", payload);
			model.addAttribute("eventPayloadValue", createDSforBq(payload));
			model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFA));
			model.addAttribute("eventTypeList", Arrays.asList(RfxTypes.RFA, RfxTypes.RFT, RfxTypes.RFP, RfxTypes.RFQ));
		} catch (Exception e) {
			LOG.error("Error while view ERP detail :" + e.getMessage(), e);
			// model.addAttribute("error", "Error while view ERP detail :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("view.erp.detail", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "erpEventDetail";
	}

	/**
	 * this method is used for SAP line item show on erp event detail screen. it contain mix Payload,with node Payload
	 * and default Payload. default RfaBqItem taken Item used as POJO class.
	 * 
	 * @param payload
	 * @return
	 */
	private List<RfaBqItem> createDSforBq(PrToAuctionErpPojo payload) {
		List<RfaBqItem> list = new ArrayList<RfaBqItem>();
		List<RfaBqItem> children = new ArrayList<RfaBqItem>();
		int order = 0;
		int noOfSections = 1;
		int level = 1;
		String delDateString = "";
		RfaBqItem section = null;
		Map<String, RfaBqItem> sections = new HashMap<String, RfaBqItem>();
		RfaBqItem defaultBqItemSection = null;
		for (PrToAuctionDetailsErpPojo detail : payload.getAuctionDetails()) {

			String sapItemNo = StringUtils.checkString(detail.getItmNo());
			if (sapItemNo.length() > 5) {
				sapItemNo = sapItemNo.substring(0, 5);
			}
			String sapItemName = detail.getMatDesc();
			BigDecimal sapQty = new BigDecimal(StringUtils.checkString(detail.getQty()).length() > 0 ? StringUtils.checkString(detail.getQty()) : "0");
			String sapUom = detail.getOrdrUom();
			Uom uom = new Uom();
			level = 1;
			LOG.info("==============" + detail.getExtSubItm());
			if (StringUtils.checkString(detail.getExtSubItm()).length() > 0) {
				section = sections.get(sapItemNo);
				if (section == null) {
					section = new RfaBqItem();
					section.setLevel(noOfSections++);
					section.setOrder(0);
					section.setItemName(sapItemName);
					section.setField10(sapItemNo); // used for SAP item no
					section.setField9(String.valueOf(0)); // used temporarily to get the next item order for

					sections.put(sapItemNo, section);
				}

				order = Integer.parseInt(section.getField9()) + 1;
				section.setField9(String.valueOf(order));
				sapUom = detail.getExtBaseUom();
				sections.put(sapItemNo, section);

				// Extract the EXT info for the line item
				sapItemName = detail.getExtValItm(); // ex_svcDesc from SAP
				sapQty = new BigDecimal(StringUtils.checkString(detail.getExtSvcQty()).length() > 0 ? StringUtils.checkString(detail.getExtSvcQty()) : "0");
				level = section.getLevel();
				sapItemNo = detail.getExtSubItm();
			} else {

				if (defaultBqItemSection == null) {
					defaultBqItemSection = new RfaBqItem();
					defaultBqItemSection.setLevel(noOfSections++);
					defaultBqItemSection.setOrder(0);
					defaultBqItemSection.setItemName("Item Section");
					defaultBqItemSection.setField10(null);
					defaultBqItemSection.setField9(String.valueOf(0));
					sections.put(sapItemNo, defaultBqItemSection);
				}

				order = Integer.parseInt(defaultBqItemSection.getField9()) + 1;
				level = defaultBqItemSection.getLevel();
				defaultBqItemSection.setField9(String.valueOf(order));
				section = defaultBqItemSection;

			}

			// setting Item
			RfaBqItem bqItem = new RfaBqItem();
			bqItem.setLevel(level);
			bqItem.setOrder(order);
			uom.setUom(sapUom);
			bqItem.setUom(uom);
			bqItem.setItemName(sapItemName);
			bqItem.setQuantity(sapQty.toBigDecimal());
			bqItem.setPriceType(PricingTypes.NORMAL_PRICE);
			bqItem.setField10(sapItemNo); // used for SAP item no
			bqItem.setField1(StringUtils.checkString(detail.getField1()));
			bqItem.setField2(StringUtils.checkString(detail.getField2()));
			bqItem.setField3(StringUtils.checkString(detail.getField3()));
			bqItem.setField4(StringUtils.checkString(detail.getField4()));
			bqItem.setField5(StringUtils.checkString(detail.getField5()));
			bqItem.setField6(StringUtils.checkString(detail.getField6()));
			bqItem.setField7(StringUtils.checkString(detail.getField7()));
			bqItem.setField8(StringUtils.checkString(detail.getItmNo()));
			bqItem.setField9(StringUtils.checkString(detail.getField9()));
			bqItem.setField10(sapItemNo);
			bqItem.setParent(section);
			children.add(bqItem);
			if (StringUtils.checkString(delDateString).length() == 0) {
				delDateString = detail.getField7();
			}

		}
		for (Map.Entry<String, RfaBqItem> value : sections.entrySet()) {
			RfaBqItem parent = value.getValue();
			List<RfaBqItem> childrenItem = new ArrayList<RfaBqItem>();
			for (RfaBqItem rfaBqItem : children) {
				if (parent.getLevel() == rfaBqItem.getLevel()) {
					childrenItem.add(rfaBqItem);
					LOG.info("==================");
				}
			}
			if (CollectionUtil.isNotEmpty(childrenItem)) {
				childrenItem.sort(Comparator.comparing(BqItem::getOrder));
				LOG.info("==================" + childrenItem.size());
				parent.setChildren(childrenItem);
			}
			list.sort(Comparator.comparing(BqItem::getLevel));
			list.add(parent);
		}
		return list;
	}

	@RequestMapping(path = "/getRfxTemplates/{eventType}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfxTemplate>> getRfxTemplates(@PathVariable("eventType") String eventType) {
		LOG.info("getRfxTemplates Called " + eventType);
		HttpHeaders headers = new HttpHeaders();
		List<RfxTemplate> templateList = null;
		try {
			templateList = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.fromString(eventType));
			setLazyProperty(templateList);
		} catch (Exception e) {
			LOG.error("Error getting RFx templates" + e.getMessage(), e);
			return new ResponseEntity<List<RfxTemplate>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<RfxTemplate>>(templateList, headers, HttpStatus.OK);
	}

	private void setLazyProperty(List<RfxTemplate> templateList) {
		if (CollectionUtil.isNotEmpty(templateList)) {
			for (RfxTemplate rfxTemplate : templateList) {
				rfxTemplate.setCreatedBy(null);
				rfxTemplate.setModifiedBy(null);
			}
		}

	}

	@RequestMapping(value = "/erpManualList", method = RequestMethod.GET)
	public String getErpManualList(Model model) {
		LOG.info("get ERP Manual List called Id ");
		try {
			List<ErpAudit> auditList = erpAuditService.getAllAuditByTenantIdAndActionType(SecurityLibrary.getLoggedInUserTenantId(), ErpAuditType.PENDING);
			model.addAttribute("erpEventAuditList", auditList);
			model.addAttribute("actionTypeList", Arrays.asList(ErpAuditType.PENDING, ErpAuditType.DUPLICATE, ErpAuditType.ERROR, ErpAuditType.CREATED));

		} catch (Exception e) {
			LOG.error("Error while get manual ERP list :" + e.getMessage(), e);
			// model.addAttribute("error", "Error while get manual ERP list :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.manual.erplist", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "erpManualList";
	}

	@RequestMapping(path = "/getRfxTemplatesByName/{eventType}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfxTemplate>> getRfxTemplatesByName(@PathVariable("eventType") String eventType) {
		LOG.info("getRfxTemplates Called " + eventType);
		HttpHeaders headers = new HttpHeaders();
		List<RfxTemplate> templateList = null;
		try {
			templateList = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.getRfxTypes(eventType));
			setLazyProperty(templateList);
		} catch (Exception e) {
			LOG.error("Error getting RFx templates" + e.getMessage(), e);
			return new ResponseEntity<List<RfxTemplate>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<RfxTemplate>>(templateList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/createErpEvent", method = RequestMethod.POST)
	public String createErpEvent(@RequestParam("eventType") String eventType, @RequestParam("rfxTemplate") String rfxTemplateId, @RequestParam("auditId") String auditId, RedirectAttributes redir) {
		LOG.info("create Erp Event Type : " + eventType + " == rfxTemplate : " + rfxTemplateId + "=== auditId :" + auditId);
		try {
			if (StringUtils.checkString(eventType).length() == 0) {
				LOG.info("Event Type is null");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.select.eventtype", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/erpManualList";
			}
			if (StringUtils.checkString(rfxTemplateId).length() == 0) {
				LOG.info("Event Template is null");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.select.eventtemplate", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/erpManualList";
			}
			ErpAudit eventDetail = erpAuditService.findById(auditId);
			ObjectMapper mapper = new ObjectMapper();
			PrToAuctionErpPojo prToAuctionErpPojo = mapper.readValue(eventDetail.getPayload(), PrToAuctionErpPojo.class);
			boolean success = true;
			switch (RfxTypes.fromStringToRfxType(eventType)) {
			case RFA:
				erpIntegrationService.copyFromRfaTemplateForErp(rfxTemplateId, SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo);
				break;
			case RFP:
				erpIntegrationService.copyFromRfpTemplateForErp(rfxTemplateId, SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo);
				break;
			case RFQ:
				erpIntegrationService.copyFromRfqTemplateForErp(rfxTemplateId, SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo);
				break;
			case RFT:
				erpIntegrationService.copyFromRftTemplateForErp(rfxTemplateId, SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo);
				break;
			default:
				success = false;
				LOG.error("Switch in default condition for event type :" + eventType);
				break;
			}
			if (success) {
				eventDetail.setAction(ErpAuditType.CREATED);
				erpAuditService.save(eventDetail);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.created", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while create Erp event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flashherror.create.erpevent", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/erpManualList";
	}

	@RequestMapping(value = "/updateErpEvent", method = RequestMethod.POST)
	public String updateErpEvent(@RequestParam("prNo") String prNo, @RequestParam("auditId") String auditId, RedirectAttributes redir) {
		LOG.info("create Erp Event prNo : " + prNo + "=== auditId :" + auditId);
		try {
			if (StringUtils.checkString(prNo).length() == 0) {
				LOG.info("Event Type is null");
				redir.addFlashAttribute("error", messageSource.getMessage("erp.invalid.prno", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/erpManualList";
			}
			ErpAudit eventDetail = erpAuditService.findById(auditId);
			ObjectMapper mapper = new ObjectMapper();
			PrToAuctionErpPojo prToAuctionErpPojo = mapper.readValue(eventDetail.getPayload(), PrToAuctionErpPojo.class);
			boolean success = true;
			LOG.info("--------------------------------------------------------------------------");
			List<MobileEventPojo> eventDetails = erpIntegrationService.getEventTypeFromPrNo(prNo, SecurityLibrary.getLoggedInUser().getTenantId());
			if (CollectionUtil.isNotEmpty(eventDetails)) {
				for (MobileEventPojo mobileEventPojo : eventDetails) {
					LOG.info("mobileEventPojo: " + mobileEventPojo.getEventId() + "==== " + mobileEventPojo.getEventType() + "=====" + mobileEventPojo.getId());

					if (mobileEventPojo.getStatus() == EventStatus.DRAFT) {
						LOG.info("--------------------------------------------------------------------------" + mobileEventPojo.getEventType());
						switch (mobileEventPojo.getEventType()) {
						case "RFA":
							LOG.info("-------------------------------RFA-------------------------------------------");
							erpIntegrationService.overwriteFromRfaTemplateForErp(SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo, mobileEventPojo);
							break;
						case "RFP":
							LOG.info("-------------------------------RFP-------------------------------------------");
							erpIntegrationService.overwriteRfpTemplateForErp(SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo, mobileEventPojo);
							break;
						case "RFQ":
							LOG.info("-------------------------------RFQ-------------------------------------------");
							erpIntegrationService.overwriteRfqTemplateForErp(SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo, mobileEventPojo);
							break;
						case "RFT":
							LOG.info("-------------------------------RFT-------------------------------------------");
							erpIntegrationService.overwriteRftTemplateForErp(SecurityLibrary.getLoggedInUser(), prToAuctionErpPojo, mobileEventPojo);
							break;
						default:
							success = false;
							LOG.info("-------------------------------FAIl-------------------------------------------");
							LOG.error("Switch in default condition for event type :" + eventDetails);
							break;
						}
					} else {
						redir.addFlashAttribute("error", messageSource.getMessage("flasherror.create.erpevent.used", new Object[] {}, Global.LOCALE));
						return "redirect:/buyer/erpManualList";
					}
				}
			}
			if (success) {
				eventDetail.setAction(ErpAuditType.CREATED);
				erpAuditService.save(eventDetail);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.created", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while create Erp event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flashherror.create.erpevent", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/erpManualList";

	}

	@RequestMapping(value = "/erpManualList/{actionType}", method = RequestMethod.GET)
	public ResponseEntity<List<ErpAudit>> getErpManualListByStatus(@PathVariable String actionType) {
		LOG.info("get ERP Manual List called Id ");
		List<ErpAudit> auditList = new ArrayList<>();
		try {
			auditList = erpAuditService.getAllAuditByTenantIdAndActionType(SecurityLibrary.getLoggedInUserTenantId(), ErpAuditType.fromString(actionType));
			if (CollectionUtil.isNotEmpty(auditList)) {
				for (ErpAudit erpAudit : auditList) {
					erpAudit.setActionBy(null);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while get manual ERP list :" + e.getMessage(), e);
		}
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<List<ErpAudit>>(auditList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/erpAppKeyGenrate", method = RequestMethod.POST)
	public ResponseEntity<String> erpAppKeyGenrate() {
		char[] chararray = new char[10];
		for (int i = 0; i < 10; i++) {
			chararray[i] = genrateRandomNumber();
		}

		String key = hmacSha512(chararray.toString());
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(key, headers, HttpStatus.OK);
	}

	public static String hmacSha512(String value) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(DEFAULT_ENCODING), HMAC_SHA512);

			Mac mac = Mac.getInstance(HMAC_SHA512);
			mac.init(keySpec);
			return toHexString(mac.doFinal(value.getBytes(DEFAULT_ENCODING)));

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String returnStr = formatter.toString();
		if (formatter != null) {
			formatter.close();
			formatter = null;
		}
		return returnStr;
	}

	private static char genrateRandomNumber() {

		String tempString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 62) { // length of the random string.
			int index = (int) (rnd.nextFloat() * tempString.length());
			salt.append(tempString.charAt(index));
		}
		// String saltStr = salt.toString();
		return salt.charAt(0);
	}

}
