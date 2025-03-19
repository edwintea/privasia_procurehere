package com.privasia.procurehere.rest.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.dao.*;

import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.privasia.procurehere.core.dao.BuyerAddressDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.ProductContractNotifyUsersDao;
import com.privasia.procurehere.core.dao.RequestAuditDao;
import com.privasia.procurehere.core.dao.UserDao;

import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.ErpAuditType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrAuditType;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.ProcurehereDocumentType;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Constant;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

import com.privasia.procurehere.core.pojo.ErpDoPojo;
import com.privasia.procurehere.core.pojo.ErpGrnPojo;
import com.privasia.procurehere.core.pojo.ErpPoPojo;
import com.privasia.procurehere.core.pojo.ErpRequestPojo;
import com.privasia.procurehere.core.pojo.PoItemsPojo;
import com.privasia.procurehere.core.pojo.PoPojo;
import com.privasia.procurehere.core.pojo.PrResponseErpPojo;
import com.privasia.procurehere.core.pojo.PrResponsePojo;
import com.privasia.procurehere.core.pojo.PrToAuctionErpPojo;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.PurchaseGroupsPojo;
import com.privasia.procurehere.core.pojo.RFQResponseErpPojo;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UomPojo;

import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.FavouriteSupplierEditor;
import com.privasia.procurehere.web.editors.ProductCategoryMaintenanceEditor;
import com.privasia.procurehere.web.editors.UomEditor;

import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author parveen
 */
@RestController
@RequestMapping("/erpApi")
@Api(value = "Erp Integration", description = "Er Integration Api")
public class ErpIntegrationRestController {

	private static final Logger LOG = LogManager.getLogger(Global.ERP_LOG);
	private Boolean isOldProductContract = Boolean.TRUE;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ErpAuditService erpAuditService;

	@Autowired
	UserDao userDao;

	@Autowired
	UserService userService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	PrService prService;

	@Autowired
	PaymentTermsService paymentTermsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Resource
	MessageSource messageSource;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	ErpAwardStaggingService erpAwardStaggingService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	UomService uomService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	ProductContractService productContractService;

	@Autowired
	UomEditor uomEditor;

	@Autowired
	ProductCategoryMaintenanceEditor pcmEditor;

	@Autowired
	FavouriteSupplierEditor fsEditor;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ProductContractItemsService productContractItemsService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	RequestAuditDao requestAuditDao;

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	PoService poService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	BuyerAddressDao buyerAddressDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	GoodsReceiptNoteService goodsReceiptNoteService;

	@Autowired
	DeliveryOrderService deliveryOrderService;

	@Autowired
	ProductContractNotifyUsersDao productContractNotifyUsersDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	TatReportDao tatReportDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	AgreementTypeDao agreementTypeDao;

	@Autowired
	GroupCodeDao groupCodeDao;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	PoReportDao poReportDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	PoAuditService poAuditService;

	// @formatter:off
	/**
	 * @api {post} /erpApi/prResponseData PR Response
	 * @apiName PR Response Data
	 * @apiGroup ERP
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiParam {String{64}} prReqNo Reference Number for EPROC PR
	 * @apiParam {String{64}} status Message type (SUCCESS/ERROR)
	 * @apiParam {String} headerNote Header note contain PRID,Approver Name,GeneralRemarks,ApprovalRemarks with ','
	 *           separated
	 * @apiParam {String{1050}} message Message
	 * @apiParamExample {json} Request-Example: { "message":"Purchase requisition number 0010093788 created",
	 *                  "status":"SUCCESS", "headerNote":"PR2381,G.RAVINDRAN GUNASEKARAN", "prReqNo":"P190407001" }
	 * @apiExample {curl} Example usage: curl -X POST -H
	 *             "X-AUTH-KEY:284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control:
	 *             no-cache" -H "Content-Type: application/json" -d ' { "message":"Purchase requisition number
	 *             0010093788 created", "status":"SUCCESS", "headerNote":"PR2381,G.RAVINDRAN GUNASEKARAN",
	 *             "prReqNo":"P190407001" }' "https://testtwo.procurehere.com/erpApi/prResponseData"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "success" : "PR response updated in EPROC successfully" }
	 * @apiSuccess success Success Message
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting PR response in EPROC" }
	 */
	// @formatter:on
	@RequestMapping(value = "/prResponseData", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> prResponseData(@RequestBody PrResponseErpPojo prResponseErpPojo, @RequestHeader(name = "X-AUTH-KEY", required = true) String authKey) {
		LOG.info("PR Response Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(prResponseErpPojo);
			LOG.info("Payload :" + payload);
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				String prId = prResponseErpPojo.getHeaderNote().split(",")[0];
				prResponseErpPojo.setId(prId);
				Pr pr = prService.updatePrResponse(prResponseErpPojo, erpSetup);
				if (pr != null) {
					map.put("success", "PR response updated in EPROC successfully");
				} else {
					map.put("error", "Need attention something is wrong to update the pr which is null for pr id :" + prResponseErpPojo.getId());
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Need attention something is wrong to update the pr which is null for pr id :" + prResponseErpPojo.getId());
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				LOG.info("Erp is null for app ID :" + prResponseErpPojo.getAppId());
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while PR response updated in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting PR response in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting PR response in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {post} /erpApi/rfqResponseData RFX Response
	 * @apiName RFX Response Data
	 * @apiGroup ERP
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiParam {String{500}} rfxDocNo Reference Number for EPROC RFX
	 * @apiParam {String{10}} status Message type (SUCCESS/ERROR)
	 * @apiParam {String{3000}} message Message
	 * @apiParamExample {json} Request-Example: { "message":"RFQ created under the number P190407001",
	 *                  "status":"SUCCESS","rfxDocNo":"P190407001" }
	 * @apiExample {curl} Example usage: curl -X POST -H
	 *             "X-AUTH-KEY:284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control:
	 *             no-cache" -H "Content-Type: application/json" -d '{ "message":"RFQ created under the number
	 *             P190407001", "status":"SUCCESS","rfxDocNo":"P190407001" }'
	 *             "https://testtwo.procurehere.com/erpApi/rfqResponseData"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "success" : "RFx Response Get In EPROC Successfully" }
	 * @apiSuccess success Success Message
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting RFX response in EPROC" }
	 */

	// @formatter:on
	@RequestMapping(value = "/rfqResponseData", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> rfqResponseData(@RequestBody RFQResponseErpPojo rfqResponseErpPojo, @RequestHeader(name = "X-AUTH-KEY", required = true) String authKey) {
		LOG.info("RFQ Response Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(rfqResponseErpPojo);
			LOG.info("Payload :" + payload);
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				erpIntegrationService.updateRfqResponse(rfqResponseErpPojo, erpSetup);
			} else {
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
			map.put("success", "RFX Response Get In EPROC Successfully");
		} catch (Exception e) {
			LOG.error("Error while RFQ response updated in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting RFX response in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting RFX response in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {post} /erpApi/erpAuctionData Create RFX
	 * @apiName Auction Response Data
	 * @apiGroup ERP
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiParam {String{64}} erpRefNo Reference Number for EPROC RFX
	 * @apiParam {String{64}} curr Currency Code
	 * @apiParam {String[]} itemList Item List
	 * @apiParam (Item List){String{100}} itemNo Item No
	 * @apiParam (Item List){String{100}} itemName Item Name
	 * @apiParam (Item List){String{250}} itemDesc Item Desc
	 * @apiParam (Item List){String{10}} qty Item Quantity
	 * @apiParam (Item List){String{64}} ordrUom Unit of Measurement
	 * @apiParam (Item List){String{100}} extSubItm Child Item No [optional]
	 * @apiParam (Item List){String{10}} extSvcQty Child Item quantity [optional]
	 * @apiParam (Item List){String{64}} extBaseUom Child Unit of measurement [optional]
	 * @apiParam (Item List){String{250}} extValItm Child Item Name [optional]
	 * @apiParam (Item List){String{100}} itemCategory Item Category
	 * @apiParam (Item List){String{100}} materialNo Material No
	 * @apiParam (Item List){String{100}} materialGroup Material Group
	 * @apiParam (Item List){String{100}} brandDesc Brand Description
	 * @apiParam (Item List){String{100}} mfr_PartNO Manufacturer Part No
	 * @apiParam (Item List){String{100}} purhaseGroup Purchase Group
	 * @apiParam (Item List){String{100}} deliveryDate Delivery Date
	 * @apiParamExample {json} Request-Example: { "erpRefNo":"0010090429", "curr":"MYR", "itemList":[ {
	 *                  "itemNo":"00010", "itemName":"item 1", "itemDesc":"To Supply Install 1 Unit LC Meter for",
	 *                  "qty":"1.0", "ordrUom":"AU", "extSubItm":null, "extSvcQty":null, "extBaseUom":null,
	 *                  "extValItm":null, "itemCategory":"9", "materialNo":null, "materialGroup":"ZZ001",
	 *                  "brandDesc":null, "mfr_PartNO":null, "purhaseGroup":"XUP", "deliveryDate":"20190114", }, {
	 *                  "itemNo":"00020", "itemName":"item 2", "itemDesc":"To Supply Install 1 Unit LC Meter for",
	 *                  "qty":"1.0", "ordrUom":"AU", "extSubItm":null, "extSvcQty":null, "extBaseUom":null,
	 *                  "extValItm":null, "itemCategory":"9", "materialNo":null, "materialGroup":"ZZ001",
	 *                  "brandDesc":null, "mfr_PartNO":null, "purhaseGroup":"XUP", "deliveryDate":"20190114", }, {
	 *                  "itemNo":"00030", "itemName":"item 3", "itemDesc":"To Supply Install 1 Unit LC Meter for",
	 *                  "qty":"1.0", "ordrUom":"AU", "extSubItm":null, "extSvcQty":null, "extBaseUom":null,
	 *                  "extValItm":null, "itemCategory":"9", "materialNo":null, "materialGroup":"ZZ001",
	 *                  "brandDesc":null, "mfr_PartNO":null, "purhaseGroup":"XUP", "deliveryDate":"20190114", } ] }
	 * @apiExample {curl} Example usage: curl -X POST -H "X-AUTH-KEY:
	 *             284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control: no-cache" -H
	 *             "Content-Type: application/json" -d '{ "erpRefNo":"0010090429", "curr":"MYR", "itemList":[ {
	 *             "itemNo":"00010", "itemName":"item 1", "itemDesc":"To Supply Install 1 Unit LC Meter for",
	 *             "qty":"1.0", "ordrUom":"AU", "extSubItm":null, "extSvcQty":null, "extBaseUom":null, "extValItm":null,
	 *             "itemCategory":"9", "materialNo":null, "materialGroup":"ZZ001", "brandDesc":null, "mfr_PartNO":null,
	 *             "purhaseGroup":"XUP", "deliveryDate":"20190114", }, { "itemNo":"00020", "itemName":"item 2",
	 *             "itemDesc":"To Supply Install 1 Unit LC Meter for", "qty":"1.0", "ordrUom":"AU", "extSubItm":null,
	 *             "extSvcQty":null, "extBaseUom":null, "extValItm":null, "itemCategory":"9", "materialNo":null,
	 *             "materialGroup":"ZZ001", "brandDesc":null, "mfr_PartNO":null, "purhaseGroup":"XUP",
	 *             "deliveryDate":"20190114", }, { "itemNo":"00030", "itemName":"item 3", "itemDesc":"To Supply Install
	 *             1 Unit LC Meter for", "qty":"1.0", "ordrUom":"AU", "extSubItm":null, "extSvcQty":null,
	 *             "extBaseUom":null, "extValItm":null, "itemCategory":"9", "materialNo":null, "materialGroup":"ZZ001",
	 *             "brandDesc":null, "mfr_PartNO":null, "purhaseGroup":"XUP", "deliveryDate":"20190114", } ] }'
	 *             "https://testtwo.procurehere.com/erpApi/erpAuctionData"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "success" : "Auction Data Get In EPROC Successfully" }
	 * @apiSuccess success Success Message
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting auction data in EPROC" }
	 */

	// @formatter:on
	@RequestMapping(value = "/erpAuctionData", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> erpAuctionData(@RequestBody PrToAuctionErpPojo prToAuctionErpPojo, @RequestHeader(name = "X-AUTH-KEY", required = true) String authKey) {
		LOG.info("send auction Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(prToAuctionErpPojo);
			LOG.info("Payload From Sap=====================================>:" + payload);
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authKey);
			if (erpConfig != null) {
				prToAuctionErpPojo.setAppId(authKey);
				boolean sendCreationMail = false;
				boolean sendDuplicateMail = false;
				Event event = null;
				ErpAudit erpAudit = new ErpAudit();
				erpAudit.setPrNo(prToAuctionErpPojo.getPrNo());
				Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
				User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				adminUser.setBuyer(buyer);
				if (!duplicate(prToAuctionErpPojo.getPrNo(), erpConfig.getTenantId())) {
					if (Boolean.TRUE == erpConfig.getCreateEventAuto()) {
						erpAudit.setAction(ErpAuditType.CREATED);
						erpAudit.setResponseMsg("Created");
						switch (erpConfig.getType()) {
						case RFA:
							if (erpConfig.getRfaTemplate() != null) {
								event = erpIntegrationService.copyFromRfaTemplateForErp(erpConfig.getRfaTemplate().getId(), adminUser, prToAuctionErpPojo);

								sendCreationMail = true;
							}
							break;
						case RFP:
							if (erpConfig.getRfpTemplate() != null) {
								event = erpIntegrationService.copyFromRfpTemplateForErp(erpConfig.getRfpTemplate().getId(), adminUser, prToAuctionErpPojo);
								sendCreationMail = true;
							}
							break;
						case RFQ:
							if (erpConfig.getRfqTemplate() != null) {
								event = erpIntegrationService.copyFromRfqTemplateForErp(erpConfig.getRfqTemplate().getId(), adminUser, prToAuctionErpPojo);
								sendCreationMail = true;
							}
							break;
						case RFT:
							if (erpConfig.getRftTemplate() != null) {
								event = erpIntegrationService.copyFromRftTemplateForErp(erpConfig.getRftTemplate().getId(), adminUser, prToAuctionErpPojo);
								sendCreationMail = true;
							}
							break;
						default: {
							erpAudit.setAction(ErpAuditType.PENDING);
							erpAudit.setResponseMsg("Pending");
						}
							break;
						}
					} else {
						erpAudit.setAction(ErpAuditType.PENDING);
						erpAudit.setResponseMsg("Pending");
					}
				} else {
					LOG.info("Duplicate event for sap PR NO :" + prToAuctionErpPojo.getPrNo());
					erpAudit.setAction(ErpAuditType.DUPLICATE);
					erpAudit.setResponseMsg("Duplicate No: " + prToAuctionErpPojo.getPrNo());
					LOG.info("Send duplicate mail for pr no: " + prToAuctionErpPojo.getPrNo());
					// sending duplicate email
					sendDuplicateRecordInAudit(adminUser, prToAuctionErpPojo.getPrNo());
					sendDuplicateMail = true;
				}
				// setting erp event data.
				try {
					erpAudit.setActionBy(adminUser);
					erpAudit.setActionDate(new Date());
					erpAudit.setTenantId(adminUser.getTenantId());
					erpAudit.setPayload(payload);
					erpAudit = erpAuditService.save(erpAudit);
				} catch (Exception e) {
					LOG.error("Error while saving audit create event :" + e.getMessage(), e);
				}
				if (sendCreationMail) {

					event.setNextEventType(erpConfig.getType());
					sendRfxCreatedEmails(event, adminUser, erpConfig.getType());
					LOG.info("Send creation mail for pr no: " + prToAuctionErpPojo.getPrNo());
				} else if (!sendDuplicateMail) {
					LOG.info("Send pending mail for pr no: " + prToAuctionErpPojo.getPrNo());
					sendAddedRecordInAudit(adminUser, prToAuctionErpPojo.getPrNo());
				}
			} else {
				LOG.info("config is null for \"" + prToAuctionErpPojo.getAppId() + "\" APP ID");
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
			map.put("success", "Auction Data Get In EPROC Successfully");
		} catch (Exception e) { // 500
			// TODO sending event error mail to buyer admin for now, will change it to ERP configurable emails.
			saveErrorAudit(prToAuctionErpPojo, e);
			LOG.error("Error while getting auction data in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting auction data in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting auction data in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	/**
	 * @param prToAuctionErpPojo
	 * @param e
	 */
	private void saveErrorAudit(PrToAuctionErpPojo prToAuctionErpPojo, Exception e) {
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(prToAuctionErpPojo.getAppId());
			if (erpConfig != null) {
				Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
				User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				String payload = mapperObj.writeValueAsString(prToAuctionErpPojo);
				LOG.info("Error Payload :" + payload);
				ErpAudit erpAudit = new ErpAudit();
				erpAudit.setPrNo(prToAuctionErpPojo.getPrNo());
				erpAudit.setAction(ErpAuditType.ERROR);
				if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
					erpAudit.setResponseMsg("Id setting is based on business unit which is not present in template.");
				} else {
					erpAudit.setResponseMsg("Error while process event: " + e.getMessage());
				}
				erpAudit.setActionBy(adminUser);
				erpAudit.setActionDate(new Date());
				erpAudit.setTenantId(adminUser.getTenantId());
				erpAudit.setPayload(payload);
				erpAudit = erpAuditService.save(erpAudit);
				sendErrorNotificationWhileCreating(adminUser, prToAuctionErpPojo.getPrNo(), e.getMessage());
			} else {
				LOG.info("config is null for \"" + prToAuctionErpPojo.getAppId() + "\" APP ID");
			}
		} catch (Exception e1) {
			LOG.error("Error while parsing object to json: " + e1.getMessage(), e1);
		}
	}

	private void sendAddedRecordInAudit(User user, String prNo) {
		String mailTo = "";
		String subject = "ERP event added in ERP Event List";
		String url = APP_URL + "/buyer/erpManualList";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId());
			map.put("userName", " ");
			map.put("prNo", prNo);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			sendEmail(mailTo, subject, map, Global.ERP_EVENT_PENDING_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While sending pending mail For adding Erp audit into manual list :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("erp.added.notification.message", new Object[] { prNo }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}

	}

	private void sendDuplicateRecordInAudit(User user, String prNo) {
		String mailTo = "";
		String subject = "Duplicate ERP event added in ERP Event List";
		String url = APP_URL + "/buyer/erpManualList";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId());
			map.put("userName", "");
			map.put("prNo", prNo);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			sendEmail(mailTo, subject, map, Global.ERP_EVENT_DUPLICATE_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While sending pending mail For adding Erp audit into manual list :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("erp.added.notification.message", new Object[] { prNo }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}

	}

	private void sendErrorNotificationWhileCreating(User user, String prNo, String error) {
		String mailTo = "";
		String subject = "Error ERP event added in ERP Event List";
		String url = APP_URL + "/buyer/erpManualList";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {

			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId());
			map.put("userName", "");
			map.put("prNo", prNo);
			map.put("error", error);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			sendEmail(mailTo, subject, map, Global.ERP_EVENT_ERROR_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While sending pending mail For adding Erp audit into manual list :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("erp.added.notification.message", new Object[] { prNo }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}
	}

	private void sendErrorNotificationWhilePoCreating(User user, String poNo, String error) {
		String mailTo = "";
		String subject = "ERP PO Creation error ";
		String url = APP_URL + "/login";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {

			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId());
			map.put("userName", "");
			map.put("poNo", poNo);
			map.put("error", error);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			sendEmail(mailTo, subject, map, Global.ERP_PO_ERROR_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While sending pending mail For adding Erp po creation :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("erp.po.creation.error.notification.message", new Object[] { poNo }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For erp po creation :" + e.getMessage(), e);
		}
	}

	private boolean duplicate(String prNo, String tenantId) {
		if (StringUtils.checkString(prNo).length() == 0) {
			return false;
		}
		return erpAuditService.isExists(prNo, tenantId);

	}

	// Rest api to Generate PO
	@RequestMapping(value = "/erpPoResponseData", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> prResponseData(@RequestBody List<PrResponseErpPojo> poResponseErpList, @RequestHeader(name = "X-AUTH-KEY") String authKey) {
		LOG.info("PR Response Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(poResponseErpList);
			LOG.info("Payload :" + payload);
			prService.updatePoResponse(poResponseErpList);
			map.put("success", "PO response updated in procurehere successfully");
		} catch (Exception e) {
			LOG.error("Error while Generate PO in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while Generate PO in procurehere :" + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", "Error while Generate PO in procurehere :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, header, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/erpPoData", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> poData(@RequestBody ErpPoPojo erpPo, @RequestHeader(name = "X-AUTH-KEY") String authKey) {
		LOG.info("PR Response Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				erpPo.setAppId(authKey);
				ObjectMapper mapperObj = new ObjectMapper();
				String payload = mapperObj.writeValueAsString(erpPo);
				LOG.info("Payload :" + payload);
				prService.createPo(erpPo, erpSetup.getTenantId());
				map.put("success", "PO created in procurehere successfully");
			} else {
				LOG.info("config is not found for " + authKey);
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			savePoErrorAudit(erpPo, e);
			LOG.error("Error while Generate PO in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while Generate PO in procurehere :" + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", "Error while Generate PO in procurehere :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, header, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	private void savePoErrorAudit(ErpPoPojo erpPo, Exception e) {
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(erpPo.getAppId());
			if (erpConfig != null) {
				Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
				User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				String payload = mapperObj.writeValueAsString(erpPo);
				LOG.info("Error Payload :" + payload);
				sendErrorNotificationWhilePoCreating(adminUser, erpPo.getPoNumber(), e.getMessage());
			} else {
				LOG.info("config is null for \"" + erpPo.getAppId() + "\" APP ID");
			}
		} catch (Exception e1) {
			LOG.error("Error while parsing object to json: " + e1.getMessage(), e1);
		}
	}

	protected void sendRfxCreatedEmails(Event event, User user, RfxTypes type) {
		String mailTo = "";
		String subject = "Event Created";
		// String url = APP_URL + "/login";
		String url = APP_URL + "/buyer/" + event.getNextEventType().name() + "/createEventDetails/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			boolean isContainMail = true;
			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId()).toUpperCase();
			if (StringUtils.checkString(mailTo).length() > 0) {
				String[] mails = mailTo.split(",");
				for (String mail : mails) {
					if (mail.equalsIgnoreCase(user.getCommunicationEmail())) {
						isContainMail = false;
					}
				}
			}
			if (isContainMail) {
				mailTo = user.getCommunicationEmail() + "," + mailTo;
			}

			LOG.info("-------------------Sending Emails to the --------------" + mailTo);
			map.put("userName", "");
			map.put("event", event);
			map.put("eventType", type.name());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			sendEmail(mailTo, subject, map, Global.ERP_EVENT_CREATED_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While sending mail For Event CREATION :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("event.created.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}
	}

	public void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		notificationService.sendEmail(mailTo, subject, map, template);
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private String getErpNotifiactionEmailsByBuyerSettings(String tenantId) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				LOG.info("fetching buyer setting-------------------");
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(tenantId);
				if (buyerSettings != null) {
					return StringUtils.checkString(buyerSettings.getErpNotificationEmails());
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting :" + e.getMessage(), e);
		}
		return "";
	}

	// @formatter:off
	/**
	 * @api {get} /erpApi/awardList RFX Awards List
	 * @apiName RFX Awards List
	 * @apiGroup ERP
	 * @apiDescription API to fetch the RFX Award List containing the Awards that have not been sent to the caller yet
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiExample {curl} Example usage: curl -X GET -H
	 *             "X-AUTH-KEY:284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control:
	 *             no-cache" -H "Content-Type: application/json" "https://testtwo.procurehere.com/erpApi/awardList"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "awardList": [ { "eventId": "RFQ05205", "eventName":"TEST
	 *                    NAME", "eventReferenceNumber": "RFQ-002", "businessUnitName": "BU", "eventOwner": "TEST OWNER
	 *                    NAME", "awardRemark":"Test AwardRemark", "totalAwardPrice":"9999.00", "createdDate":
	 *                    "20/05/2019 10:24:15", "startDate": "20/05/2019 10:24:20", "endDate": "19/05/2019 23:24:59",
	 *                    "validityDays": 25, "currencyCode":"MYR" "deliveryDate": "21/05/2019 00:24:00", "paymentTerm":
	 *                    "Check", "deliveryAddress": { "title": "Office Address", "line1": "pune", "line2": "pune",
	 *                    "city": "pune", "zip": "21454", "state": "MLK", "country": "MY" }, "bqList": [ { "bqName":
	 *                    "Bill of Quantity 1", "bqItems": [ { "id": "ff8081816ad36fe0016ad39a6c470004", "level": 1,
	 *                    "order": 1, "itemName": "Item 1", "itemDescription": "Item 1 Description", "itemNo": "0010",
	 *                    "quantity": 12, "uom": "Centimeter", "totalAmount": 252, "unitPrice": 21, "itemCategory":
	 *                    "FIELD1", "bqItemCode": "FIELD2", "materialGroup": "MAT_G", "mfr_PartNO" :
	 *                    "CA-AC-BUS-NS-JP-01", "lnno": "", "supplier": { "companyName": "SupplierPrivaa1",
	 *                    "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326", "countryCode": "MY",
	 *                    "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam", "communicationEmail":
	 *                    "sudesha.nikam@gmail.com", "designation": "software developer", "yearOfEstablished": 1985,
	 *                    "faxNumber": "+94 21219326", "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city":
	 *                    "PUNE", "stateCode": "NSN" } }, { "id": "ff8081816ad36fe0016ad39a6c520006", "level": 1,
	 *                    "order": 2, "itemNo": "0020", "itemName": "Item 2", "itemDescription": "Item 2 Description",
	 *                    "quantity": 12, "uom": "Centimeter", "totalAmount": 252, "unitPrice": 21, "vendorCode":
	 *                    "1234", "itemCategory": "FIELD1", "bqItemCode": "FIELD2", "materialGroup": "MAT_G",
	 *                    "mfr_PartNO" : "CA-AC-BUS-NS-JP-01", "lnno": "", "supplier": { "companyName":
	 *                    "SupplierPrivaa1", "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326",
	 *                    "countryCode": "MY", "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam",
	 *                    "communicationEmail": "sudesha.nikam@gmail.com", "designation": "software developer",
	 *                    "yearOfEstablished": 1985, "faxNumber": "+94 21219326" "taxNumber":"12345678", "line1":
	 *                    "pune", "line2": "PUNE", "city": "PUNE", "stateCode": "NSN" } } ] }, { "bqName": "Bill of
	 *                    Quantity 2", "bqItems": [ { "id": "ff8081816ad36fe0016ad39b2dc6000c", "level": 1, "order": 1,
	 *                    "itemNo": "0030", "itemName": "Item 1", "itemDescription": "Item Description", "quantity": 45,
	 *                    "totalAmount": 1564, "uom": "Fahrenheit", "unitPrice": 34, "vendorCode": "1234",
	 *                    "itemCategory": "FIELD1", "bqItemCode": "FIELD2", "materialGroup": "MAT_G", "mfr_PartNO" :
	 *                    "CA-AC-BUS-NS-JP-01", "lnno": "", "supplier": { "companyName": "SupplierPrivaa1",
	 *                    "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326", "countryCode": "MY",
	 *                    "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam", "communicationEmail":
	 *                    "sudesha.nikam@gmail.com", "designation": "software developer", "yearOfEstablished": 1985,
	 *                    "faxNumber": "+94 21219326", "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city":
	 *                    "PUNE", "stateCode": "NSN" } }, { "id": "ff8081816ad36fe0016ad39b2dcc000e", "level": 1,
	 *                    "order": 2, "itemName": "Item 2", "itemDescription": "Item 2 Description", "quantity": 23,
	 *                    "uom": "Foot/feet", "unitPrice": 32, "totalAmount": 736, "vendorCode": "1234", "itemCategory":
	 *                    "FIELD1", "bqItemCode": "FIELD2", "materialGroup": "MAT_G", "mfr_PartNO" :
	 *                    "CA-AC-BUS-NS-JP-01", "lnno": "", "supplier": { "companyName": "SupplierPrivaa1",
	 *                    "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326", "countryCode": "MY",
	 *                    "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam", "communicationEmail":
	 *                    "sudesha.nikam@gmail.com", "designation": "software developer", "yearOfEstablished": 1985,
	 *                    "faxNumber": "+94 21219326", "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city":
	 *                    "PUNE", "stateCode": "NSN" } } ] } ] } ] } @apiSuccess {Object} awardDetail Award from EPROC
	 * @apiSuccess (Award){String{64}} eventId EventId for RFX
	 * @apiSuccess (Award){String{250}} eventName Event Name for RFX
	 * @apiSuccess (Award){String{64}} eventReferenceNumber RFX Reference Number
	 * @apiSuccess (Award){String{64}} businessUnitName Business Unit Name
	 * @apiSuccess (Award){String{160}} eventOwner Event Owner Name
	 * @apiSuccess (Award){String{1000}} awardRemark Award Remark
	 * @apiSuccess (Award){String{64}} currencyCode Currency Code
	 * @apiSuccess (Award){BigDecimal{16,4}} totalAwardPrice Total Award Price
	 * @apiSuccess (Award){Date} createdDate RFX Created Date
	 * @apiSuccess (Award){Date} startDate RFX Start Date
	 * @apiSuccess (Award){Date} endDate RFX End Date
	 * @apiSuccess (Award){Date} deliveryDate RFX Delivery Date
	 * @apiSuccess (Award){Integer{3}} validityDays RFX Validity Days
	 * @apiSuccess (Award){String{550}} paymentTerm RFX Payment Term
	 * @apiSuccess (Award){Object} deliveryAddress RFX Delivery Address
	 * @apiSuccess (Delivery Address){String{128}} title Address Title
	 * @apiSuccess (Delivery Address){String{250}} line1 Address Line 1
	 * @apiSuccess (Delivery Address){String{250}} line2 Address Line 2
	 * @apiSuccess (Delivery Address){String{250}} city Address city
	 * @apiSuccess (Delivery Address){String{32}} zip Address Zip
	 * @apiSuccess (Delivery Address){String{64}} state Address State Code
	 * @apiSuccess (Delivery Address){String{64}} country Address Country Code
	 * @apiSuccess (Award){Object[]} bqList RFX Bill of Quantity(BQ List)
	 * @apiSuccess (BQ List){String{128}} bqName RFX Bill of Quantity Name
	 * @apiSuccess (BQ List){Object[]} bqItems RFX Bq Item
	 * @apiSuccess (Bq Item){Integer{2}} level Item level
	 * @apiSuccess (Bq Item){Integer{2}} order Item Order
	 * @apiSuccess (Bq Item){String{10}} itemNo Item Number
	 * @apiSuccess (Bq Item){String{250}} itemName Item Name
	 * @apiSuccess (Bq Item){String{1100}} itemDescription Item Description
	 * @apiSuccess (Bq Item){BigInteger{10}} quantity Item Quantity
	 * @apiSuccess (Bq Item){String{64}} uom Unit Of Measurement
	 * @apiSuccess (Bq Item){BigDecimal{16,4}} totalAmount Total Amount
	 * @apiSuccess (Bq Item){BigDecimal{16,4}} unitPrice Unit Price
	 * @apiSuccess (Bq Item){String{100}} itemCategory Item Category
	 * @apiSuccess (Bq Item){String{100}} bqItemCode Bq Item Code
	 * @apiSuccess (Bq Item){String{100}} materialGroup Material Group
	 * @apiSuccess (Bq Item){String{100}} mfr_PartNO MFR PartNO
	 * @apiSuccess (Bq Item){String{100}} itemNo Item No
	 * @apiSuccess (Bq Item){String{100}} lnno Line Item No
	 * @apiSuccess (Bq Item){String{100}} vendorCode Vendor Code
	 * @apiSuccess (Bq Item){Object} supplier Supplier
	 * @apiSuccess (Supplier){String{128}} companyName Supplier Company Name
	 * @apiSuccess (Supplier){String{16}} mobileNumber Mobile Number
	 * @apiSuccess (Supplier){String{16}} companyContactNumber Company Contact Number
	 * @apiSuccess (Supplier){String{64}} countryCode Country Code
	 * @apiSuccess (Supplier){String{128}} companyRegistrationNumber Company Registration Number
	 * @apiSuccess (Supplier){String{128}} fullName Supplier Full Name
	 * @apiSuccess (Supplier){String{128}} communicationEmail Communication Email
	 * @apiSuccess (Supplier){String{128}} designation Designation
	 * @apiSuccess (Supplier){String{4}} yearOfEstablished Year Of Established
	 * @apiSuccess (Supplier){String{16}} faxNumber FaxNumber
	 * @apiSuccess (Supplier){String{32}} taxNumber Supplier Tax Number
	 * @apiSuccess (Supplier){String{250}} line1 Address Line1
	 * @apiSuccess (Supplier){String{250}} line2 Address Line2
	 * @apiSuccess (Supplier){String{250}} city City
	 * @apiSuccess (Supplier){String{64}} stateCode State Code
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting Award detail response in
	 *                  EPROC" }
	 */

	// @formatter:on
	@RequestMapping(value = "/awardList", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAwardDetails(@RequestHeader(name = "X-AUTH-KEY", required = true) String authKey) {
		LOG.info("RFQ Award Detail List Data Api called..");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				if (erpSetup.getAwardInterfaceTypePull()) {
					List<ErpAwardStaging> staggingList = erpAwardStaggingService.getStaggingData(erpSetup.getTenantId(), null);
					List<Object> list = new ArrayList<Object>();
					for (ErpAwardStaging erpAwardStaging : staggingList) {
						list.add(mapperObj.readValue(erpAwardStaging.getPayload(), Object.class));

					}
					map.put("awardList", list);
				} else {
					map.put("error", "Interface not active");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Interface not active");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}
			} else {
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while geting Award List " + e.getMessage(), e);
			map.put("error", "Error while geting Award List in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while geting Award List in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {post} /erpApi/awardDetail RFX Awards Detail
	 * @apiName RFX Awards Data
	 * @apiGroup ERP
	 * @apiDescription API to fetch the RFX Award individually based on Reference Number
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiParam referenceNo eventId
	 * @apiParamExample {json} Request-Example: { "referenceNo":"RFQ05205" }
	 * @apiExample {curl} Example usage: curl -X POST -H
	 *             "X-AUTH-KEY:284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control:
	 *             no-cache" -H "Content-Type: application/json" -d '{ "referenceNo" : "RFQ05205" }'
	 *             "https://testtwo.procurehere.com/erpApi/awardDetail"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "awardDetail": { "eventId": "RFQ05205", "eventName":"TEST
	 *                    NAME", "eventReferenceNumber": "RFQ-002", "businessUnitName": "BU", "eventOwner": "TEST OWNER
	 *                    NAME", "awardRemark":"Test AwardRemark", "totalAwardPrice":"9999.00", "createdDate":
	 *                    "20/05/2019 10:24:15", "startDate": "20/05/2019 10:24:20", "endDate": "19/05/2019 23:24:59",
	 *                    "validityDays": 25, "deliveryDate": "21/05/2019 00:24:00", "paymentTerm": "Check",
	 *                    "currencyCode":"MYR" "deliveryAddress": { "title": "Office Address", "line1": "pune", "line2":
	 *                    "pune", "city": "pune", "zip": "21454", "state": "MLK", "country": "MY" }, "bqList": [ {
	 *                    "bqName": "Bill of Quantity 1", "bqItems": [ { "id": "ff8081816ad36fe0016ad39a6c470004",
	 *                    "level": 1, "order": 1, "itemName": "Item 1", "itemDescription": "Item 1 Description",
	 *                    "itemNo": "0010", "quantity": 12, "uom": "Centimeter", "totalAmount": 252, "unitPrice": 21,
	 *                    "vendorCode": "1234", "itemCategory": "FIELD1", "bqItemCode": "FIELD2", "materialGroup":
	 *                    "MAT_G", "mfr_PartNO" : "CA-AC-BUS-NS-JP-01", "lnno": "", "supplier": { "companyName":
	 *                    "SupplierPrivaa1", "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326",
	 *                    "countryCode": "MY", "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam",
	 *                    "communicationEmail": "sudesha.nikam@gmail.com", "designation": "software developer",
	 *                    "yearOfEstablished": 1985, "faxNumber": "+94 21219326", "taxNumber":"12345678", "line1":
	 *                    "pune", "line2": "PUNE", "city": "PUNE", "stateCode": "NSN" } }, { "id":
	 *                    "ff8081816ad36fe0016ad39a6c520006", "level": 1, "order": 2, "itemNo": "0020", "itemName":
	 *                    "Item 2", "itemDescription": "Item 2 Description", "quantity": 12, "uom": "Centimeter",
	 *                    "totalAmount": 252, "unitPrice": 21, "vendorCode": "1234", "itemCategory": "FIELD1",
	 *                    "bqItemCode": "FIELD2", "materialGroup": "MAT_G", "mfr_PartNO" : "CA-AC-BUS-NS-JP-01", "lnno":
	 *                    "", "supplier": { "companyName": "SupplierPrivaa1", "mobileNumber": "+45 126323",
	 *                    "companyContactNumber": "+94 21219326", "countryCode": "MY", "companyRegistrationNumber":
	 *                    "400", "fullName": "sudesha sanjay nikam", "communicationEmail": "sudesha.nikam@gmail.com",
	 *                    "designation": "software developer", "yearOfEstablished": 1985, "faxNumber": "+94 21219326",
	 *                    "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city": "PUNE", "stateCode": "NSN" }
	 *                    } ] }, { "bqName": "Bill of Quantity 2", "bqItems": [ { "id":
	 *                    "ff8081816ad36fe0016ad39b2dc6000c", "level": 1, "order": 1, "itemNo": "0030", "itemName":
	 *                    "Item 1", "itemDescription": "Item Description", "quantity": 45, "totalAmount": 1564, "uom":
	 *                    "Fahrenheit", "unitPrice": 34, "vendorCode": "1234", "itemCategory": "FIELD1", "bqItemCode":
	 *                    "FIELD2", "materialGroup": "MAT_G", "mfr_PartNO" : "CA-AC-BUS-NS-JP-01", "lnno": "",
	 *                    "supplier": { "companyName": "SupplierPrivaa1", "mobileNumber": "+45 126323",
	 *                    "companyContactNumber": "+94 21219326", "countryCode": "MY", "companyRegistrationNumber":
	 *                    "400", "fullName": "sudesha sanjay nikam", "communicationEmail": "sudesha.nikam@gmail.com",
	 *                    "designation": "software developer", "yearOfEstablished": 1985, "faxNumber": "+94 21219326",
	 *                    "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city": "PUNE", "stateCode": "NSN" }
	 *                    }, { "id": "ff8081816ad36fe0016ad39b2dcc000e", "level": 1, "order": 2, "itemName": "Item 2",
	 *                    "itemDescription": "Item 2 Description", "quantity": 23, "uom": "Foot/feet", "unitPrice": 32,
	 *                    "totalAmount": 736, "vendorCode": "1234", "itemCategory": "FIELD1", "bqItemCode": "FIELD2",
	 *                    "materialGroup": "MAT_G", "lnno": "", "supplier": { "companyName": "SupplierPrivaa1",
	 *                    "mobileNumber": "+45 126323", "companyContactNumber": "+94 21219326", "countryCode": "MY",
	 *                    "companyRegistrationNumber": "400", "fullName": "sudesha sanjay nikam", "communicationEmail":
	 *                    "sudesha.nikam@gmail.com", "designation": "software developer", "yearOfEstablished": 1985,
	 *                    "faxNumber": "+94 21219326", "taxNumber":"12345678", "line1": "pune", "line2": "PUNE", "city":
	 *                    "PUNE", "stateCode": "NSN" } } ] } ] } }
	 * @apiSuccess {Object} awardDetail Award from EPROC
	 * @apiSuccess (Award){String{64}} eventId EventId for RFX
	 * @apiSuccess (Award){String{250}} eventName Event Name for RFX
	 * @apiSuccess (Award){String{64}} eventReferenceNumber RFX Reference Number
	 * @apiSuccess (Award){String{64}} businessUnitName Business Unit Name
	 * @apiSuccess (Award){String{160}} eventOwner Event Owner Name
	 * @apiSuccess (Award){String{64}} currencyCode Currency Code
	 * @apiSuccess (Award){String{1000}} awardRemark Award Remark
	 * @apiSuccess (Award){BigDecimal{16,4}} totalAwardPrice Total Award Price
	 * @apiSuccess (Award){Date} createdDate RFX Created Date
	 * @apiSuccess (Award){Date} startDate RFX Start Date
	 * @apiSuccess (Award){Date} endDate RFX End Date
	 * @apiSuccess (Award){Date} deliveryDate RFX Delivery Date
	 * @apiSuccess (Award){Integer{3}} validityDays RFX Validity Days
	 * @apiSuccess (Award){String{550}} paymentTerm RFX Payment Term
	 * @apiSuccess (Award){Object} deliveryAddress RFX Delivery Address
	 * @apiSuccess (Delivery Address){String{128}} title Address Title
	 * @apiSuccess (Delivery Address){String{250}} line1 Address Line 1
	 * @apiSuccess (Delivery Address){String{250}} line2 Address Line 2
	 * @apiSuccess (Delivery Address){String{250}} city Address city
	 * @apiSuccess (Delivery Address){String{32}} zip Address Zip
	 * @apiSuccess (Delivery Address){String{64}} state Address State Code
	 * @apiSuccess (Delivery Address){String{64}} country Address Country Code
	 * @apiSuccess (Award){Object[]} bqList RFX Bill of Quantity(BQ List)
	 * @apiSuccess (BQ List){String{128}} bqName RFX Bill of Quantity Name
	 * @apiSuccess (BQ List){Object[]} bqItems RFX Bq Item
	 * @apiSuccess (Bq Item){Integer{2}} level Item level
	 * @apiSuccess (Bq Item){Integer{2}} order Item Order
	 * @apiSuccess (Bq Item){String{10}} itemNo Item Number
	 * @apiSuccess (Bq Item){String{250}} itemName Item Name
	 * @apiSuccess (Bq Item){String{1100}} itemDescription Item Description
	 * @apiSuccess (Bq Item){BigInteger{10}} quantity Item Quantity
	 * @apiSuccess (Bq Item){String{64}} uom Unit Of Measurement
	 * @apiSuccess (Bq Item){BigDecimal{16,4}} totalAmount Total Amount
	 * @apiSuccess (Bq Item){BigDecimal{16,4}} unitPrice Unit Price
	 * @apiSuccess (Bq Item){String{100}} itemCategory Item Category
	 * @apiSuccess (Bq Item){String{100}} bqItemCode Bq Item Code
	 * @apiSuccess (Bq Item){String{100}} materialGroup Material Group
	 * @apiSuccess (Bq Item){String{100}} mfr_PartNO MFR PartNO
	 * @apiSuccess (Bq Item){String{100}} itemNo Item No
	 * @apiSuccess (Bq Item){String{100}} lnno Line Item No
	 * @apiSuccess (Bq Item){String{100}} vendorCode Vendor Code
	 * @apiSuccess (Bq Item){Object} supplier Supplier
	 * @apiSuccess (Supplier){String{128}} companyName Supplier Company Name
	 * @apiSuccess (Supplier){String{16}} mobileNumber Mobile Number
	 * @apiSuccess (Supplier){String{16}} companyContactNumber Company Contact Number
	 * @apiSuccess (Supplier){String{64}} countryCode Country Code
	 * @apiSuccess (Supplier){String{128}} companyRegistrationNumber Company Registration Number
	 * @apiSuccess (Supplier){String{128}} fullName Supplier Full Name
	 * @apiSuccess (Supplier){String{128}} communicationEmail Communication Email
	 * @apiSuccess (Supplier){String{128}} designation Designation
	 * @apiSuccess (Supplier){String{4}} yearOfEstablished Year Of Established
	 * @apiSuccess (Supplier){String{16}} faxNumber FaxNumber
	 * @apiSuccess (Supplier){String{32}} taxNumber Supplier Tax Number
	 * @apiSuccess (Supplier){String{250}} line1 Address Line1
	 * @apiSuccess (Supplier){String{250}} line2 Address Line2
	 * @apiSuccess (Supplier){String{250}} city City
	 * @apiSuccess (Supplier){String{64}} stateCode State Code
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting Award detail in EPROC" }
	 */

	// @formatter:on

	@RequestMapping(value = "/awardDetail", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getAwardDetail(@RequestHeader(name = "X-AUTH-KEY", required = true) String authKey, @RequestBody(required = true) ErpRequestPojo erpRequest) {
		LOG.info("RFQ Award Detail Data Api called.." + erpRequest);
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				if (erpSetup.getAwardInterfaceTypePull()) {
					List<ErpAwardStaging> staggingList = erpAwardStaggingService.getStaggingData(erpSetup.getTenantId(), erpRequest.getReferenceNo());
					for (ErpAwardStaging erpAwardStaging : staggingList) {
						map.put("awardDetail", mapperObj.readValue(erpAwardStaging.getPayload(), Object.class));
					}
				} else {
					map.put("error", "Interface not active");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Interface not active");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}
			} else {
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while getting Award detail procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting Award detail in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting Award detail in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	// @formatter:off
	/**
	 * @api {post} /erpApi/markAwardAsProcessed Mark Award As Processed
	 * @apiName Mark Award As Processed
	 * @apiGroup ERP
	 * @apiDescription API to Mark Award As Processed individually based on Reference Number
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" }
	 * @apiParam referenceNo eventId
	 * @apiParamExample {json} Request-Example: { "referenceNo":"RFQ05205" }
	 * @apiExample {curl} Example usage: curl -X POST -H
	 *             "X-AUTH-KEY:284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa34" -H "Cache-Control:
	 *             no-cache" -H "Content-Type: application/json" -d '{ "referenceNo" : "RFQ05205" }'
	 *             "https://testtwo.procurehere.com/erpApi/markAwardAsProcessed"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "success" : "Award marked as processed successfully" }
	 * @apiSuccess success Success Message
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while processing request" }
	 */
	// @formatter:on
	@RequestMapping(value = "/markAwardAsProcessed", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> markAwardAsProcessed(@RequestHeader(name = "X-AUTH-KEY", required = true) String authKey, @RequestBody(required = true) ErpRequestPojo erpRequest) {
		LOG.info("RFQ Award Detail Data Api called.." + erpRequest);
		Map<String, Object> map = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		try {
			ErpSetup erpSetup = erpSetupDao.getErpConfigByAppId(authKey);
			if (erpSetup != null) {
				if (erpSetup.getAwardInterfaceTypePull()) {
					List<ErpAwardStaging> staggingList = erpAwardStaggingService.getStaggingData(erpSetup.getTenantId(), erpRequest.getReferenceNo());
					if (CollectionUtil.isEmpty(staggingList)) {
						throw new ApplicationException("Invalid reference number");
					}
					for (ErpAwardStaging erpAwardStaging : staggingList) {
						erpAwardStaging.setSentFlag(true);
						erpAwardStaging.setSentDate(new Date());
						erpAwardStaggingService.update(erpAwardStaging);

						map.put("success", "Award marked as processed successfully");
						headers.add("success", "Award marked as processed successfully");

					}
				} else {
					map.put("error", "Interface not active");
					headers.add("error", "Interface not active");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}
			} else {
				map.put("error", "Auth key not valid");
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while processing request :" + e.getMessage(), e);
			map.put("error", "Error while processing request :" + e.getMessage());
			headers.add("error", "Error while processing request:" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/uom", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> syncUomData(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody UomPojo uomPojo) {
		LOG.info("UOM Response Data Api called...");
		Map<String, Object> map = new HashMap<>();
		try {

			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}
			if (Boolean.FALSE == erpConfig.getEnableUomApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
			}

			Uom uom = null;

			if (StringUtils.checkString(uomPojo.getUom()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notEmpty", new Object[] {}, Global.LOCALE));
			}
			if (StringUtils.checkString(uomPojo.getUomDescription()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.uomDesc.notEmpty", new Object[] {}, Global.LOCALE));
			}

			uom = uomService.getUombyCode(uomPojo.getUom(), buyer.getId());

			switch (uomPojo.getOperation()) {
			case C: { // CREATE
				if (uom != null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.exists", new Object[] { uomPojo.getUom() }, Global.LOCALE));
				} else {
					uom = new Uom();
					createUom(uomPojo, buyer, user, uom);
				}
				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			case D: { // DELETE
				if (uom != null) {
					uom = uomService.getUombyCode(uomPojo.getUom(), buyer.getId());
					uom.setStatus(Status.INACTIVE);
					uom.setModifiedBy(user);
					uom.setModifiedDate(new Date());
					uomService.updateUom(uom, Boolean.TRUE);

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notExists", new Object[] {}, Global.LOCALE));
				}
				break;
			}
			case U: { // UPDATE
				if (uom != null) {
					updateUom(uomPojo, user, uom);

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notExists", new Object[] {}, Global.LOCALE));
				}
				break;
			}
			case CU: { // CREATE OR UPDATE
				if (uom != null) {
					updateUom(uomPojo, user, uom);
				} else {
					uom = new Uom();
					createUom(uomPojo, buyer, user, uom);
				}

				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error storing UOM : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.uom.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	private void updateUom(UomPojo uomPojo, User user, Uom uom) {
		uom.setUomDescription(uomPojo.getUomDescription());
		uom.setStatus(Status.ACTIVE);
		uom.setModifiedBy(user);
		uom.setModifiedDate(new Date());
		uomService.updateUom(uom, Boolean.TRUE);
	}

	private void createUom(UomPojo uomPojo, Buyer buyer, User user, Uom uom) {
		uom.setUom(uomPojo.getUom());
		uom.setUomDescription(uomPojo.getUomDescription());
		uom.setBuyer(buyer);
		uom.setStatus(Status.ACTIVE);
		uom.setCreatedDate(new Date());
		uom.setCreatedBy(user);
		uomService.createUom(uom, Boolean.TRUE);
	}

	@RequestMapping(value = "/productCategory", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> syncProductCategory(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody ProductCategoryPojo productCategoryPojo) {
		LOG.info("Product Category Data Api called...");
		Map<String, Object> map = new HashMap<>();
		try {

			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}
			if (Boolean.FALSE == erpConfig.getEnableProductCategoryApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}
			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productCategoryPojo.getProductCode()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productCode.notEmpty", new Object[] {}, Global.LOCALE));
			}
			if (StringUtils.checkString(productCategoryPojo.getProductName()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productName.notEmpty", new Object[] {}, Global.LOCALE));
			}

			ProductCategory productCatgory = productCategoryMaintenanceService.findProductCategoryByCode(productCategoryPojo.getProductCode(), buyer.getId());

			switch (productCategoryPojo.getOperation()) {
			case C: { // CREATE
				if (productCatgory != null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.exists", new Object[] { productCategoryPojo.getProductCode() }, Global.LOCALE));
				} else {
					productCatgory = new ProductCategory();
					createProductCategory(productCategoryPojo, buyer, user, productCatgory);
				}
				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			case D: { // DELETE
				if (productCatgory != null) {
					productCatgory.setStatus(Status.INACTIVE);
					productCatgory.setModifiedBy(user);
					productCatgory.setModifiedDate(new Date());
					productCategoryMaintenanceService.updateProductCategory(productCatgory, Boolean.TRUE);

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.notExists", new Object[] { productCategoryPojo.getProductCode() }, Global.LOCALE));
				}
				break;
			}
			case U: { // UPDATE
				if (productCatgory != null) {
					updateProductCategory(productCategoryPojo, user, productCatgory);

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.notExists", new Object[] { productCategoryPojo.getProductCode() }, Global.LOCALE));
				}
				break;
			}
			case CU: { // CREATE OR UPDATE
				if (productCatgory != null) {
					updateProductCategory(productCategoryPojo, user, productCatgory);
				} else {
					productCatgory = new ProductCategory();
					createProductCategory(productCategoryPojo, buyer, user, productCatgory);
				}
				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error storing Product category : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.productCategory.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	private void updateProductCategory(ProductCategoryPojo productCategoryPojo, User user, ProductCategory productCatgory) {
		productCatgory.setProductName(productCategoryPojo.getProductName());
		productCatgory.setModifiedBy(user);
		productCatgory.setModifiedDate(new Date());
		productCatgory.setStatus(Status.ACTIVE);
		productCategoryMaintenanceService.updateProductCategory(productCatgory, Boolean.TRUE);
	}

	private void createProductCategory(ProductCategoryPojo productCategoryPojo, Buyer buyer, User user, ProductCategory productCatgory) {
		productCatgory.setProductCode(productCategoryPojo.getProductCode());
		productCatgory.setProductName(productCategoryPojo.getProductName());
		productCatgory.setCreatedDate(new Date());
		productCatgory.setCreatedBy(user);
		productCatgory.setBuyer(buyer);
		productCatgory.setStatus(Status.ACTIVE);
		productCategoryMaintenanceService.createProductCategory(productCatgory, Boolean.TRUE);
	}

	@RequestMapping(value = "/productItem", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> syncProductItem(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody ProductItemPojo productItemPojo) {
		LOG.info("Product Item Data Api called..." + productItemPojo);
		Map<String, Object> map = new HashMap<>();
		try {

			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}
			if (Boolean.FALSE == erpConfig.getEnableProductItemApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}
			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
			}

			ProductCategory productCategory = null;
			FavouriteSupplier favouriteSupplier = null;
			Uom uom = null;

			if (StringUtils.checkString(productItemPojo.getItemCode()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productCode.notEmpty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productItemPojo.getItemName()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productName.notEmpty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productItemPojo.getItemCategory()).length() > 0) {
				productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(productItemPojo.getItemCategory(), buyer.getId());
				if (productCategory == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.notExists", new Object[] { productItemPojo.getItemCategory() }, Global.LOCALE));
				}
			} else {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.notEmpty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productItemPojo.getUom()).length() > 0) {
				uom = uomService.getUomByUomAndTenantId(productItemPojo.getUom(), buyer.getId());
				if (uom == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notExists", new Object[] {}, Global.LOCALE));
				}
			} else {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notEmpty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productItemPojo.getVendorCode()).length() > 0) {
				favouriteSupplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(productItemPojo.getVendorCode()), buyer.getId());
				if (favouriteSupplier == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.supplier.notExists", new Object[] { productItemPojo.getVendorCode() }, Global.LOCALE));
				}
			}

			if (Boolean.TRUE == productItemPojo.getContractItem()) {
				if (productItemPojo.getValidityDate() == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.validity.notEmpty", new Object[] {}, Global.LOCALE));
				}
			}
			productItemPojo.setInterfaceCode(productItemPojo.getItemCode());

			List<ProductItemPojo> requestList = new ArrayList<ProductItemPojo>();

			// If there are multiple Purchase Group codes present, then split them into multiple product items

			if (CollectionUtil.isNotEmpty(productItemPojo.getPurchaseGroups())) {
				for (PurchaseGroupsPojo pgCode : productItemPojo.getPurchaseGroups()) {
					LOG.info("Group Code : " + pgCode.getPurchaseGroupCodes());
					ProductItemPojo pojo = productItemPojo.createCopy();
					pojo.setItemCode(pojo.getItemCode() + pgCode.getPurchaseGroupCodes());
					pojo.setPurchaseGroupCode(pgCode.getPurchaseGroupCodes());
					requestList.add(pojo);
				}
			} else {
				if (StringUtils.checkString(productItemPojo.getPurchaseGroupCode()).length() > 0) {
					productItemPojo.setItemCode(productItemPojo.getItemCode() + StringUtils.checkString(productItemPojo.getPurchaseGroupCode()));
				}
				requestList.add(productItemPojo);
			}

			for (ProductItemPojo productItemRequest : requestList) {
				LOG.info("ProductItemRequest : " + productItemRequest.toString());
				// Ignore ProductType for item sync
				ProductItem productItem = productListMaintenanceService.findProductItemByCode(productItemRequest.getItemCode(), buyer.getId(), null);

				switch (productItemRequest.getOperation()) {
				case C: { // CREATE
					if (productItem != null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.productItem.exists", new Object[] { productItemRequest.getItemCode() }, Global.LOCALE));
					} else {
						productItem = new ProductItem();
						createProductItem(productItem, productItemRequest, buyer, user, productCategory, favouriteSupplier, uom);
					}

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
					break;
				}
				case D: { // DELETE
					if (productItem != null) {
						productItem.setStatus(Status.INACTIVE);
						productItem.setModifiedBy(user);
						productItem.setModifiedDate(new Date());
						productListMaintenanceService.updateProductItem(productItem, Boolean.TRUE);

						map.put("errorcode", 0);
						map.put("errorMessage", "SUCCESS");
					} else {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.productItem.notExists", new Object[] { productItemRequest.getItemCode() }, Global.LOCALE));
					}
					break;
				}
				case U: { // UPDATE
					if (productItem != null) {
						updateProductItem(productItem, productItemRequest, user, productCategory, favouriteSupplier, uom);

						map.put("errorcode", 0);
						map.put("errorMessage", "SUCCESS");
					} else {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.productItem.notExists", new Object[] { productItemRequest.getItemCode() }, Global.LOCALE));
					}
					break;
				}
				case CU: { // CREATE OR UPDATE
					if (productItem != null) {
						updateProductItem(productItem, productItemRequest, user, productCategory, favouriteSupplier, uom);
					} else {
						productItem = new ProductItem();
						createProductItem(productItem, productItemRequest, buyer, user, productCategory, favouriteSupplier, uom);
					}
					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
					break;
				}
				default:
					break;
				}
			}

		} catch (Exception e) {
			LOG.error("Error Processing Product Item Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/sapToPhPoFile", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> sapToPhPoFile(
			@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey,
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "poNumber", required = true) String poNumber) {

		LOG.info("Sap To PH PO file API called for this poNumber..." + poNumber);
		Map<String, String> response = new HashMap<>();
		long MAX_FILE_SIZE = 1 * 1024 * 1024; // Max file 1MB
		ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);

		try {
			if (erpConfig == null) {
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[]{}, Global.LOCALE)
				);
			}

			if (Boolean.FALSE.equals(erpConfig.getEnableBinaryFilePush())) {
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[]{}, Global.LOCALE)
				);
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.buyer.empty", new Object[]{}, Global.LOCALE)
				);
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.user.empty", new Object[]{}, Global.LOCALE)
				);
			}

			if(file.getSize() > MAX_FILE_SIZE) {
				LOG.error("File size exceeds the maximum allowed size of 1 MB");
				throw new Exception("File size exceeds the maximum allowed size of 1 MB");
			}

			if(file.getOriginalFilename() != null && file.getOriginalFilename().length() < 10) {
				LOG.error("File Name length is less then 10");
				throw new Exception("File Name length is less then 10");
			}

			if(!file.getOriginalFilename().split("\\.")[1].equals("bin")) {
				throw new Exception("File does not have a '.bin' extension");
			}

			// Here poNumber is SAP poNumber so meaning this ReferenceNumber in PH
			Po po = poService.findPoByReferenceNumber(poNumber, erpConfig.getTenantId());
			po.setStatus(PoStatus.ORDERED);
			PoReport poReport = poReportDao.findReportByPoId(po.getId());

			if (poReport == null) {
				poReport = new PoReport();
			}

			byte[] data = file.getBytes();
			poReport.setFileData(data);
			poReport.setFileName(file.getOriginalFilename().substring(0, 10) + ".pdf");
			poReport.setPoNumber(po.getPoNumber());
			poReport.setPo(po);

			poReportDao.saveOrUpdate(poReport);
			poService.updatePo(po);
			response.put("TYPE", "Success");
			response.put("MESSAGE", Constant.FGV_SUCESSFULLY_PO_DOCUMENT);
			LOG.info("Sucess for "+poNumber);
		} catch (Exception e) {
			Po po = poService.findPoByReferenceNumber(poNumber, erpConfig.getTenantId());
			if(po != null) {
				po.setStatus(PoStatus.READY);
				poService.updatePo(po);
			}
			e.printStackTrace();
			response.put("TYPE", "Error");
			response.put("MESSAGE", Constant.FGV_FAILED_PO_DOCUMENT);
			LOG.info("Failed for "+poNumber);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}



	@RequestMapping(value = "/sapPoToPhPo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> sapToPoTPhPo(
			@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey,
			@RequestBody PORequest poRequest) {

		LOG.info("SAP PO To PH PO Api called" + poRequest);
		LOG.info("AuthKey is :"+authkey);
		Map<String, String> response = new HashMap<>();
		Boolean isUpdate = Boolean.TRUE;
		SapPoToPhPoPojo sapPoToPhPoPojo = poRequest.getPoHeader();

		try {
			LOG.info(sapPoToPhPoPojo.getReferenceNumber() + " is in process");
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				LOG.error("ErpConfig is null");
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[]{}, Global.LOCALE)
				);
			}

			if (Boolean.FALSE.equals(erpConfig.getEnableBinaryFilePush())) {
				LOG.error("ErpConfig is not enable to binary file push");
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[]{}, Global.LOCALE)
				);
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				LOG.error("Buyer is null");
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.buyer.empty", new Object[]{}, Global.LOCALE)
				);
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				LOG.error("user is null");
				throw new ApplicationException(
						messageSource.getMessage("erpIntegration.user.empty", new Object[]{}, Global.LOCALE)
				);
			}

			Po po = poService.findPoByReferenceNumber(sapPoToPhPoPojo.getReferenceNumber(), buyer.getId());

			if(po == null) {
				po = new Po();
				LOG.error(sapPoToPhPoPojo.getReferenceNumber() + " will be updated ");
				isUpdate = Boolean.FALSE;
			}

//			if (StringUtils.isBlank(sapPoToPhPoPojo.getPoName())) {
//               throw new Exception("PO_NAME is empty");
//			}

			po.setName(StringUtils.checkString(sapPoToPhPoPojo.getPoName()).length() > 0 ? sapPoToPhPoPojo.getPoName().length() > 128 ? sapPoToPhPoPojo.getPoName().substring(0, 128) :
					sapPoToPhPoPojo.getPoName() : null);

//			if(StringUtils.isBlank(sapPoToPhPoPojo.getReferenceNumber())) {
//				throw new Exception("REFERENCE_NUMBER is empty");
//			}

			po.setReferenceNumber(sapPoToPhPoPojo.getReferenceNumber());
			Date today = new Date();
			po.setCreatedDate(isUpdate ? po.getCreatedDate() : today);
			if(isUpdate) {
				po.setPoRevisedDate(today);
			}
			po.setStatus(PoStatus.READY);

			if(StringUtils.isBlank(sapPoToPhPoPojo.getSupplierCode())) {
				LOG.error("FAV_VENDOR_CODE is missing");
				throw new Exception("FAV_VENDOR_CODE is missing");
			}

			FavouriteSupplier favouriteSupplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(sapPoToPhPoPojo.getSupplierCode(), erpConfig.getTenantId());

			if(favouriteSupplier == null || (favouriteSupplier != null &&
					!favouriteSupplier.getSupplier().getCompanyName().equals(sapPoToPhPoPojo.getSupplierName())) ||
					!favouriteSupplier.getSupplier().getCompanyRegistrationNumber().equals(sapPoToPhPoPojo.getSupplierRegistrationNumber())) {
				 LOG.error("FAV_VENDOR_CODE is missing");
				 throw new Exception("Vendor is missing");
			}
			po.setSupplierName(favouriteSupplier.getFullName());
			po.setSupplier(favouriteSupplier);

			BusinessUnit businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(erpConfig.getTenantId(), sapPoToPhPoPojo.getParentBusinessUnitCode());
			if(businessUnit == null) {
				LOG.error("BusinessUnit is missing");
				throw new Exception("BusinessUnit is missing");
			}

			// handling po number
			if (po.getPoNumber() != null) {
				List<String> poNumberList = new ArrayList<>(Arrays.asList(po.getPoNumber().split("-")));
				if (poNumberList.size() > 5) {
					int updateNumber = Integer.parseInt(poNumberList.get(5));
					updateNumber++;
					poNumberList.set(5, String.valueOf(updateNumber));
				} else {
					poNumberList.add("1");
				}
				po.setPoNumber(String.join("-", poNumberList));
			} else {
				po.setPoNumber(eventIdSettingsDao.generateEventIdByBusinessUnit(buyer.getId(), "PO", businessUnit));
			}


			po.setBusinessUnit(businessUnit);

			User erpCreator =  userService.getUserByLoginIdNoTouch(sapPoToPhPoPojo.getPoCreator());

			if(erpCreator == null || (erpCreator != null && !erpCreator.getTenantId().equals(erpConfig.getTenantId()))) {
				LOG.error("ID creator is missing");
				throw new Exception("ID creator is missing");
			}

			po.setCreatedBy(erpCreator);
			po.setRequester(sapPoToPhPoPojo.getRequester());
			Currency currency = currencyDao.findByCurrencyCode(sapPoToPhPoPojo.getBaseCurrency());

			if(currency == null) {
				LOG.error("Base Currency is missing");
				throw new Exception("Base Currency is missing");
			}
			po.setCurrency(currency);
			po.setDecimal("5");
			po.setFromIntegration(Boolean.TRUE);

			PaymentTermes paymentTermes = paymentTermsService.getByPaymentTermes(sapPoToPhPoPojo.getPaymentTerms(), erpConfig.getTenantId());
			if(paymentTermes == null) {
				LOG.error("PaymentTerms is missing");
				throw new Exception("PaymentTerms is missing");
			}
			po.setPaymentTermes(paymentTermes);
			po.setPaymentTermDays(paymentTermes.getPaymentDays());
			po.setPaymentTerm(paymentTermes.getDescription());

			if(sapPoToPhPoPojo.getCorrespondenceAddress() == null || sapPoToPhPoPojo.getCorrespondenceAddress().isEmpty()) {
				LOG.error("CorrespondenceAddress is missing");
				throw new Exception("CorrespondenceAddress is missing");
			}

			String[] addressList = sapPoToPhPoPojo.getCorrespondenceAddress().split(",");

			BuyerAddress buyerAddress = buyerAddressDao.getBuyerAddressForTenantByTitle(addressList[0], erpConfig.getTenantId());

			if(buyerAddress == null) {
				LOG.error("CorrespondenceAddress is missing");
				throw new Exception("CorrespondenceAddress is missing");
			}
			po.setCorrespondenceAddress(buyerAddress);
			po.setCorrespondAddressCity(buyerAddress.getCity());
			po.setCorrespondAddressCountry(buyerAddress.getState() != null ? buyerAddress.getState().getCountry().getCountryName() : "");
			po.setCorrespondAddressLine1(buyerAddress.getLine1());
			po.setCorrespondAddressLine2(buyerAddress.getLine2());
			po.setCorrespondAddressTitle(buyerAddress.getTitle());
			po.setCorrespondAddressState(buyerAddress.getState().getStateName());
			po.setCorrespondAddressZip(buyerAddress.getZip());

			if(sapPoToPhPoPojo.getDeliveryAddress() == null || sapPoToPhPoPojo.getDeliveryAddress().isEmpty()) {
				LOG.error("DELIVERY_ADDRESS is missing");
				throw new Exception("DELIVERY_ADDRESS is missing");
			}

			String[] addressListDelivery = sapPoToPhPoPojo.getCorrespondenceAddress().split(",");

			BuyerAddress buyerAddressDelivery = buyerAddressDao.getBuyerAddressForTenantByTitle(addressListDelivery[0], erpConfig.getTenantId());

			if(buyerAddressDelivery == null) {
				LOG.error("DELIVERY_ADDRESS is missing");
				throw new Exception("DELIVERY_ADDRESS is missing");
			}

			po.setDeliveryAddress(buyerAddressDelivery);
			po.setDeliveryAddressCity(buyerAddressDelivery.getCity());
			po.setDeliveryAddressCountry(buyerAddressDelivery.getState() != null ? buyerAddressDelivery.getState().getCountry().getCountryName() : "");
			po.setDeliveryAddressLine1(buyerAddressDelivery.getLine1());
			po.setDeliveryAddressLine2(buyerAddressDelivery.getLine2());
			po.setDeliveryAddressTitle(buyerAddressDelivery.getTitle());
			po.setDeliveryAddressState(buyerAddressDelivery.getState().getStateName());
			po.setDeliveryAddressZip(buyerAddressDelivery.getZip());

			// Supplier Info and supplier address:
			po.setSupplierFaxNumber(favouriteSupplier.getSupplier().getFaxNumber());
			po.setSupplierName(favouriteSupplier.getSupplier().getFullName());
			po.setSupplierRemark(favouriteSupplier.getSupplier().getRemarks());
			po.setSupplierTaxNumber(favouriteSupplier.getSupplier().getTaxRegistrationNumber());
			po.setSupplierTelNumber(favouriteSupplier.getSupplier().getMobileNumber());
			po.setSupplierAddress(favouriteSupplier.getSupplier().getCity());
			po.setDeliveryReceiver(erpConfig.getErpCreator().getName());
			po.setBuyer(buyer);


			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			if(StringUtils.isBlank(sapPoToPhPoPojo.getDeliveryDateTime())) {
				LOG.error("DELIVERY_DATE is missing");
				throw new Exception("DELIVERY_DATE is missing");
			}

			try {
				Date deliveryDate = formatter.parse(sapPoToPhPoPojo.getDeliveryDateTime());
				po.setDeliveryDate(deliveryDate);
			} catch (ParseException e) {
				LOG.error("Date can't be converted");
				throw new Exception("Date can't be converted");
			}

			// PO items

			if(poRequest.getPoItems() == null && poRequest.getPoItems().isEmpty()) {
				LOG.error("PoItems is missing");
				throw new Exception("PoItems is missing");
			}
			po.setPoItems(setPoItemForApi(poRequest.getPoItems(), buyer, po));
			Po createdPo = poService.updatePo(po);

			// save po-audit
			PoAudit poAudit = new PoAudit();
            poAudit.setAction(isUpdate ? PoAuditType.REVISED : PoAuditType.CREATE);
			poAudit.setActionBy(erpConfig.getErpCreator());
			poAudit.setDescription(isUpdate ? "PO \"" + po.getPoNumber() + "\" revised" : "PO \"" + po.getPoNumber() + "\" created");
			poAudit.setActionDate(new Date());
			poAudit.setBuyer(buyer);

			if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
				poAudit.setSupplier(po.getSupplier().getSupplier());
			}
			poAudit.setVisibilityType(isUpdate ? PoAuditVisibilityType.BOTH : PoAuditVisibilityType.BUYER);
			poAudit.setPo(createdPo);
			poAuditService.save(poAudit);

			// save buyer audit
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(isUpdate ? AuditTypes.REVISED : AuditTypes.CREATE,
					isUpdate ? "PO \"" + po.getPoNumber() + "\" revised" : "PO \"" + po.getPoNumber() + "\" created",
					erpConfig.getTenantId(), erpConfig.getErpCreator(), new Date(),
					ModuleType.PO);
			buyerAuditTrailDao.save(buyerAuditTrail);

			LOG.info(sapPoToPhPoPojo.getReferenceNumber() + " is updated");

			response.put("TYPE", "S");
			response.put("MESSAGE", isUpdate ? Constant.REFERENCE_NUMBER_PH_SUCCESS_UPDATE.replace("{0}", po.getReferenceNumber()).replace("{1}", po.getPoNumber()) : Constant.REFERENCE_NUMBER_PH_SUCCESS_CREATE.replace("{0}", po.getReferenceNumber()).replace("{1}", po.getPoNumber()));
			response.put("EBELN", sapPoToPhPoPojo.getReferenceNumber());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("TYPE", "R");
			response.put("MESSAGE", !isUpdate ? sapPoToPhPoPojo.getReferenceNumber() + Constant.REFERENCE_NUMBER_PH_FAILED_CREATE + " "+e.getMessage() : sapPoToPhPoPojo.getReferenceNumber() + Constant.REFERENCE_NUMBER_PH_FAILED_UPDATE+ " "+e.getMessage());
			response.put("EBELN", sapPoToPhPoPojo.getReferenceNumber());
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public List<PoItem> setPoItemForApi(List<SapPoToPhPoPojoItem> sapPoToPhPoPojoItemList, Buyer buyer, Po po) throws Exception {
		List<PoItem> poItemList = new ArrayList<>();


		// ADD SECTION
		PoItem poItemSection = new PoItem();
		poItemSection.setItemName("Purchase Item");
		poItemSection.setLevel(1);
		poItemSection.setOrder(0);
		poItemSection.setBuyer(buyer);
		poItemSection.setPo(po);
		poItemList.add(poItemSection);

		BigDecimal total = BigDecimal.ZERO;
		Boolean isAllDeleted = Boolean.TRUE;

		// poitems save
		for(SapPoToPhPoPojoItem sapPoToPhPoPojoItem : sapPoToPhPoPojoItemList) {
			PoItem poItem = new PoItem();

			int itemNumber;
			if(sapPoToPhPoPojoItem.getNo() == null || sapPoToPhPoPojoItem.getNo().isEmpty()) {
				throw new Exception("NO is missing");
			}

			itemNumber = Integer.valueOf(sapPoToPhPoPojoItem.getNo()) / 10;

			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getItemIndicator())) {
				LOG.error("ITEM_INDICATOR is missing at "+itemNumber);
				throw new Exception("ITEM_INDICATOR is missing at "+itemNumber);
			}

			poItem.setItemIndicator(sapPoToPhPoPojoItem.getItemIndicator());

			if(!sapPoToPhPoPojoItem.getItemIndicator().equals("003")) {
				isAllDeleted = Boolean.FALSE;
			}

			poItem.setItemCategory(sapPoToPhPoPojoItem.getItemCategory());

			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getItemName())) {
				throw new Exception("ITEM_NAME is missing at "+itemNumber);
			}

			poItem.setItemName(sapPoToPhPoPojoItem.getItemName());
			poItem.setLevel(1);
			poItem.setOrder(itemNumber);
			poItem.setItemDescription(sapPoToPhPoPojoItem.getItemDescription());
			poItem.setPo(po);
			poItem.setBuyer(buyer);
			Uom uom = uomService.getUomByUomAndTenantId(sapPoToPhPoPojoItem.getUom(), buyer.getId());

			if(uom == null) {
				throw new Exception("Uom is missing at "+itemNumber);
			}
			poItem.setUom(sapPoToPhPoPojoItem.getUom());
			poItem.setUnit(uom);

			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getQuantity())) {
				throw new Exception("ITEM_QUANTITY is missing at "+itemNumber);
			}

			poItem.setQuantity(BigDecimal.valueOf(Double.valueOf(sapPoToPhPoPojoItem.getQuantity())));

			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getUnitPrice())) {
				throw new Exception("UNIT_PRICE is missing at "+itemNumber);
			}

			poItem.setUnitPrice(BigDecimal.valueOf(Double.valueOf(sapPoToPhPoPojoItem.getUnitPrice())));
			// this value is not assigned yet
			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getPer())) {
				throw new Exception("Per is missing at "+itemNumber);
			}
			BigDecimal pricePerUnit = new BigDecimal(sapPoToPhPoPojoItem.getPer().trim());
			poItem.setPricePerUnit(pricePerUnit);
			BigDecimal totalAmount = poItem.getUnitPrice().divide(poItem.getPricePerUnit(), Integer.valueOf(po.getDecimal()), RoundingMode.HALF_UP).multiply(poItem.getQuantity());
			poItem.setTotalAmount(totalAmount);
			poItem.setTaxAmount(BigDecimal.valueOf(0).setScale(Integer.valueOf(po.getDecimal()), RoundingMode.HALF_UP));
			poItem.setTotalAmountWithTax(poItem.getTotalAmount().add(poItem.getTaxAmount()));

			// total will not be calcualted for the deleted items(003)
			if(!sapPoToPhPoPojoItem.getItemIndicator().equals("003")) {
				total = total.add(poItem.getTotalAmountWithTax());
			}

			if(StringUtils.isBlank(sapPoToPhPoPojoItem.getChildBusinessUnit())) {
				throw new Exception("CHILD_BUSINESS_UNIT is missing at "+itemNumber);
			}
			poItem.setChildBusinessUnit(sapPoToPhPoPojoItem.getChildBusinessUnit());
			poItemList.add(poItem);
		}
		po.setTotal(total);
		po.setGrandTotal(total);

		if(isAllDeleted) {
			po.setStatus(PoStatus.CANCELLED);
		}

		return poItemList;
	}

	@Transactional
	@RequestMapping(value = "/createOrUpdateIndustryCategory", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> createIndustryCategory(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey,
																	  @RequestBody String jsonBody) throws IOException {

		Map<String, String> response = new HashMap<>();

		jsonBody = jsonBody.trim();
		LOG.debug("JsonBody is ");
		LOG.debug(jsonBody);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			IndustryCategoryEdaftarPojo industryCategoryEdaftarPojo = objectMapper.readValue(jsonBody, IndustryCategoryEdaftarPojo.class);

			if (StringUtils.isBlank(industryCategoryEdaftarPojo.getCategoryCode()) ||
					StringUtils.isBlank(industryCategoryEdaftarPojo.getSubcategoryDescBi()) || StringUtils.isBlank(industryCategoryEdaftarPojo.getActivityDescBi())
					|| industryCategoryEdaftarPojo.getStatus() == null) {
				response.put(Constant.MESSAGE, Constant.INDUSTRY_CATGORY_WRONG_MESSAGE);
				response.put(Constant.TYPE, Constant.ERROR);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

			LOG.info("Request body for industry category is ");
			LOG.info(industryCategoryEdaftarPojo);

			try {
				ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);

				if (erpConfig == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[]{}, Global.LOCALE));
				}

				if (Boolean.FALSE == erpConfig.getIsErpEnable()) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[]{}, Global.LOCALE));
				}

				Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());

				if (buyer == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[]{}, Global.LOCALE));
				}

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());

				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[]{}, Global.LOCALE));
				}

				IndustryCategory industryCategory = industryCategoryService.getIndustryCategoryByCodeExceptStatus(
						industryCategoryEdaftarPojo.getCategoryCode(),
						buyer.getId()
				);

				String industryCategoryString = StringUtils.isNotBlank(industryCategoryEdaftarPojo.getSubcategoryDescBi()) &&
						StringUtils.isNotBlank(industryCategoryEdaftarPojo.getActivityDescBi()) ? industryCategoryEdaftarPojo.getSubcategoryDescBi() + " - " + industryCategoryEdaftarPojo.getActivityDescBi() :
						(StringUtils.isBlank(industryCategoryEdaftarPojo.getActivityDescBi()) ? industryCategoryEdaftarPojo.getSubcategoryDescBi() :
								(StringUtils.isBlank(industryCategoryEdaftarPojo.getSubcategoryDescBi()) ? industryCategoryEdaftarPojo.getActivityDescBi() : ""));

				if (industryCategory == null) {
					industryCategory = new IndustryCategory();
					industryCategory.setCode(industryCategoryEdaftarPojo.getCategoryCode());
					industryCategory.setCreatedBy(erpConfig.getErpCreator());
					industryCategory.setCreatedDate(new Date());
					industryCategory.setName(industryCategoryString);
					industryCategory.setBuyer(buyer);
					industryCategory.setStatus(industryCategoryEdaftarPojo.getStatus());
					industryCategoryService.save(industryCategory);
					response.put(Constant.MESSAGE, Constant.INDUSTRY_CATEGORY_SUCCESS_MESSAGE);
				} else {
					industryCategory.setStatus(industryCategoryEdaftarPojo.getStatus());
					industryCategory.setModifiedBy(user);
					industryCategory.setModifiedDate(new Date());
					industryCategory.setName(industryCategoryString);
					industryCategoryService.update(industryCategory);
					response.put(Constant.MESSAGE, Constant.INDUSTRY_CATEGORY_SUCCESS_MESSAGE);
				}
				response.put(Constant.TYPE, Constant.SUCCESS);
				;
			} catch (Exception e) {
				response.put(Constant.TYPE, Constant.ERROR);
				response.put(Constant.MESSAGE, Constant.INDUSTRY_CATGORY_WRONG_MESSAGE);
				LOG.error(e.getMessage());
			}
		} catch (Exception e) {
			response.put(Constant.TYPE, Constant.ERROR);
			response.put(Constant.MESSAGE, Constant.INDUSTRY_CATGORY_WRONG_MESSAGE);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@Transactional
	@RequestMapping(value = "/sapToPhContract", method = RequestMethod.POST)
	public ResponseEntity<Map<String,String>> sapToPhContract(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody SapContractPojo sapContractPojo)
			throws ApplicationException {

		LOG.info("RequestBody For sapToPhContract ");
		LOG.info(sapContractPojo);
		Map<String,String> response = new HashMap<>();

		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[]{}, Global.LOCALE));
			}
			if (Boolean.FALSE == erpConfig.getEnableContractErpPush()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[]{}, Global.LOCALE));
			}
			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[]{}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[]{}, Global.LOCALE));
			}

			if (sapContractPojo.getItemContractList().size() == 0) {
				throw new ApplicationException("List should not be empty");
			}

			ProductContract productContract = productContractService.findProductContractByBuyerId(sapContractPojo.getDocNumber(), buyer.getId());

			isOldProductContract = Boolean.TRUE;

			if (productContract == null) {
				isOldProductContract = Boolean.FALSE;
				productContract = new ProductContract();
				productContract.setCreatedDate(new Date());
			}
			productContract.setSapContractNumber(sapContractPojo.getDocNumber());
			FavouriteSupplier favouriteSupplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(sapContractPojo.getVendor(), buyer.getId());
			if (favouriteSupplier == null) {
				throw new ApplicationException("Supplier is not valid for this buyer");
			} else {
				productContract.setSupplier(favouriteSupplier);
			}


			BusinessUnit businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(erpConfig.getTenantId(), sapContractPojo.getPurchOrg());
			if (businessUnit == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[]{sapContractPojo.getPurchOrg()}, Global.LOCALE));
			}
			productContract.setBusinessUnit(businessUnit);

			if (StringUtils.checkString(sapContractPojo.getPurchGroup()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.groupCode.notEmpty", new Object[]{}, Global.LOCALE));
			}

			GroupCode groupCode = groupCodeDao.getByGroupCode(sapContractPojo.getPurchGroup(), erpConfig.getTenantId());
			if(groupCode == null) {
				throw new ApplicationException("Group Code is not valid for this user");
			}
            productContract.setGroupCode(groupCode);
			productContract.setGroupCodeStr(sapContractPojo.getPurchGroup());

			Currency currency = currencyDao.findByCurrencyCode(sapContractPojo.getCurrency());
			productContract.setCurrency(currency);

			AgreementType agreementType = agreementTypeDao.getAgreementTypebyCode(sapContractPojo.getDocType(), erpConfig.getTenantId());

			if(agreementType == null) {
				throw new ApplicationException("Doc Type is not valid for this user");
			}

			productContract.setAgreementType(agreementType);
			productContract.setContractId(sapContractPojo.getDocNumber());
			productContract.setIsEditable(Boolean.FALSE);

			productContract.setDocumentDate(sapContractPojo.getDocDate());
			productContract.setContractStartDate(sapContractPojo.getContStartDate());
			productContract.setContractEndDate(sapContractPojo.getContEndDate());
			productContract.setContractValue(sapContractPojo.getContValue() != null ?
					sapContractPojo.getContValue().setScale(2, RoundingMode.HALF_UP) : null);
			productContract.setContractReferenceNumber(sapContractPojo.getOurRef());
			productContract.setContractName(sapContractPojo.getJenisKerja().length() > 250 ? sapContractPojo.getJenisKerja().substring(0,250) :
					sapContractPojo.getJenisKerja());

			User erpContractCreator = erpConfig.getErpCreator();
			productContract.setContractCreator(erpContractCreator != null ? erpContractCreator : user);
			productContract.setCreatedBy(erpContractCreator != null ? erpContractCreator : user);
			productContract.setDecimal(String.valueOf(2));
			productContract.setEnableApproval(Boolean.FALSE);

			ProductContract productContract1 = null;

			if (isOldProductContract == Boolean.TRUE) {
				if(productContract.getContractStartDate().after(new Date())) {
					productContract.setStatus(ContractStatus.APPROVED);
				}
				productContract1 = productContractService.update(productContract);
				productContract1.setModifiedBy(erpContractCreator != null ? erpContractCreator : user);
				productContract1.setModifiedDate(new Date());

				// saving contract audit
				ContractAudit contractAudit = new ContractAudit(productContract1, erpContractCreator != null ? erpContractCreator : user, new Date(), AuditActionType.Update, "Contract \"" + StringUtils.checkString(productContract1.getContractId()) + "\" updated");
				contractAuditDao.save(contractAudit);
			} else {
				productContract.setBuyer(buyer);
				if(productContract.getContractStartDate().after(new Date())) {
					productContract.setStatus(ContractStatus.APPROVED);
				}
				else {
					productContract.setStatus(ContractStatus.ACTIVE);
				}
				productContract1 = productContractService.createProductContract(productContract);

				// saving contract audit
				ContractAudit contractAudit = new ContractAudit(productContract1, erpContractCreator != null ? erpContractCreator : user, new Date(), AuditActionType.Create, "Contract \"" + StringUtils.checkString(productContract1.getContractId()) + "\" created");
				contractAuditDao.save(contractAudit);
			}

			try {
				convertProductContractListItem(sapContractPojo.getItemContractList(), productContract1, buyer);

				List<ProductContractItems> productContractItemsList = productContractItemsService.
						findProductContractItemsByProductContractIdIsDeleted(productContract1.getId());

				productContract1.setProductContractItem(productContractItemsService.
						findProductContractItemsByProductContractIdWithoutCondition(productContract1.getId()));

				if (productContractItemsList == null || productContractItemsList.size() == 0) {
					productContract1.setErpTransferred(Boolean.TRUE);
					productContract1.setStatus(ContractStatus.TERMINATED);
					productContractService.update(productContract1);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TERMINATED, "Contract '" +
							productContract1.getContractId() + "' terminated by ERP", erpContractCreator != null ? erpContractCreator.getTenantId() : user.getTenantId(),
							erpContractCreator != null ? erpContractCreator : user, new Date(), ModuleType.ContractList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} else {
					productContract1.setErpTransferred(Boolean.TRUE);
					productContractService.update(productContract1);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "Request Got from ERP : ",
							erpContractCreator != null ? erpContractCreator.getTenantId() : user.getTenantId(),
							erpContractCreator != null ? erpContractCreator : user, new Date(), ModuleType.ContractList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				}
				response.put("TYPE", "S");
				response.put("Message", "Successfully Created ");
				response.put("CONTRACT_ID", productContract1.getId());
				response.put("SAP_CONTRACT_ID", productContract1.getSapContractNumber());
			} catch (Exception e) {
				e.printStackTrace();
				response.put("TYPE", "E");
				response.put("Message", e.getMessage());
				// WIll check if list item is null : if null then delete the product contract from PH
				if(isOldProductContract) {
					List<ProductContractItems> productContractItemsList = productContractItemsService.
							findProductContractItemsByProductContractIdIsDeleted(productContract1.getId());

					if (productContractItemsList == null || productContractItemsList.size() == 0) {
						contractAuditDao.deleteContractAuditByProductContractId(productContract1.getId());
						productContractItemsService.deleteContractItemsForFailedProductContractTransaction(productContract1.getId());
						productContractService.deleteProductContractById(productContract1.getId());
					}
				}
				else {
					contractAuditDao.deleteContractAuditByProductContractId(productContract1.getId());
					productContractItemsService.deleteContractItemsForFailedProductContractTransaction(productContract1.getId());
					productContractService.deleteProductContractById(productContract1.getId());
				}
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("TYPE", "E");
			response.put("Message",e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	public void convertProductContractListItem(List<ItemContractPojo> itemContractPojoList, ProductContract productContract, Buyer buyer)
			throws ApplicationException {


		List<ProductContractItems> productContractItemsList = new ArrayList<>();

		for (ItemContractPojo itemContractPojo : itemContractPojoList) {

			ProductContractItems productContractItems = new ProductContractItems();
			//productContractItems.setIt(name) 
			if (itemContractPojo.getItemNumber() != null) {
				productContractItems.setContractItemNumber(itemContractPojo.getItemNumber());
			} else {
				throw new ApplicationException("No itemNumber");
			}
			productContractItems.setItemCode(itemContractPojo.getMaterial());
			productContractItems.setItemName(itemContractPojo.getShortText());
			productContractItems.setProductContract(productContract);

			ProductCategory productCategory = null;

			if (StringUtils.checkString(itemContractPojo.getMaltGroup()).length() > 0) {
				productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(itemContractPojo.getMaltGroup(), buyer.getId());
				if (productCategory == null) {
					throw new ApplicationException(" Material Group not found for code: "+itemContractPojo.getMaltGroup());
				}
			} else {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.productCategory.notEmpty", new Object[]{}, Global.LOCALE));
			}

			productContractItems.setProductCategory(productCategory);

			BusinessUnit businessItem = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), itemContractPojo.getPlant());
			if (businessItem == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[]{itemContractPojo.getPlant()}, Global.LOCALE));
			}

			productContractItems.setBusinessUnit(businessItem);
			productContractItems.setStorageLocation(itemContractPojo.getStorageLoc());
			productContractItems.setQuantity(itemContractPojo.getTargetQty());

			Uom uom = null;
			if (StringUtils.checkString(itemContractPojo.getPoUnit()).length() > 0) {
				uom = uomService.getUomByUomAndTenantId(itemContractPojo.getPoUnit(), buyer.getId());
				if (uom == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notExists", new Object[]{}, Global.LOCALE));
				}
			} else {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.notEmpty", new Object[]{}, Global.LOCALE));
			}
			productContractItems.setUom(uom);
			productContractItems.setUnitPrice(itemContractPojo.getNetPrice());
			productContractItems.setPricePerUnit(itemContractPojo.getPriceUnit());

			if (itemContractPojo.getDeleteIndex().equals("X")) {
				if (productContract.getId() != null) {
					deleteProductContractItems(productContract.getId(), itemContractPojo.getItemNumber());
				} else {
					throw new ApplicationException("No Product Contract");
				}
			} else {
				updateProductContractItems(productContract.getId(), itemContractPojo.getItemNumber(), productContractItems);
			}

			productContractItemsList.add(productContractItems);
		}
		productContract.setProductContractItem(productContractItemsList);
	}

	private void deleteProductContractItems(String productContractId, String contractItemNumber) throws ApplicationException {
		ProductContractItems productContractItems1 = productContractItemsService.
				findProductContractItemsByProductContractIdAndContractItemNumber(productContractId, contractItemNumber);
		if (productContractItems1 != null) {
			productContractItems1.setDeleted(true);
			productContractItemsService.updateProductContractItems(productContractItems1);
		} else {
			LOG.error("No ProductContractItems is found for productContractId: "+productContractId + " and "+contractItemNumber);
			throw new ApplicationException("No ProductContractItems is found");
		}
	}

	private void updateProductContractItems(String productContractId, String contractItemNumber, ProductContractItems productContractItems) throws ApplicationException {
		ProductContractItems productContractItems1 = productContractItemsService.
				findProductContractItemsByProductContractIdAndContractItemNumber(productContractId, contractItemNumber);
		if (productContractItems1 != null) {
			productContractItems1.setContractItemNumber(productContractItems.getContractItemNumber());
			productContractItems1.setItemCode(productContractItems.getItemCode());
			productContractItems1.setItemName(productContractItems.getItemName());
			productContractItems1.setProductCategory(productContractItems.getProductCategory());

			productContractItems1.setBusinessUnit(productContractItems.getBusinessUnit());
			productContractItems1.setStorageLocation(productContractItems.getStorageLocation());
			productContractItems1.setQuantity(productContractItems.getQuantity());
			productContractItems1.setPricePerUnit(productContractItems.getPricePerUnit());
			productContractItems1.setUnitPrice(productContractItems.getUnitPrice());
			productContractItems1.setUom(productContractItems.getUom());
			productContractItems1.setDeleted(false);
			productContractItemsService.updateProductContractItems(productContractItems1);
		} else {
			productContractItemsService.saveContractItem(productContractItems);
		}
	}

	private void updateProductItem(ProductItem productItem, ProductItemPojo productItemPojo, User user, ProductCategory productCategory, FavouriteSupplier favouriteSupplier, Uom uom) {
		productItem.setProductName(productItemPojo.getItemName());
		productItem.setTax(productItemPojo.getTax());
		productItem.setContractItem(Boolean.TRUE == productItemPojo.getContractItem() ? true : false);
		productItem.setUnitPrice(productItemPojo.getUnitPrice());
		productItem.setUom(uom);
		productItem.setProductCategory(productCategory);
		productItem.setFavoriteSupplier(favouriteSupplier);
		productItem.setValidityDate(productItemPojo.getValidityDate());
		productItem.setRemarks(productItemPojo.getRemarks());
		productItem.setBrand(productItemPojo.getBrand());
		productItem.setGlCode(productItemPojo.getGlCode());
		productItem.setPurchaseGroupCode(productItemPojo.getPurchaseGroupCode());
		// productItem.setContractReferenceNumber(productItemPojo.getContractReferenceNumber());
		productItem.setStatus(Status.ACTIVE);
		productItem.setProductItemType(productItemPojo.getItemType());
		productItem.setModifiedBy(user);
		productItem.setModifiedDate(new Date());
		productListMaintenanceService.updateProductItem(productItem, Boolean.TRUE);
	}

	private void createProductItem(ProductItem productItem, ProductItemPojo productItemPojo, Buyer buyer, User user, ProductCategory productCategory, FavouriteSupplier favouriteSupplier, Uom uom) {
		productItem.setInterfaceCode(productItemPojo.getInterfaceCode());
		productItem.setProductCode(productItemPojo.getItemCode());
		productItem.setProductName(productItemPojo.getItemName());
		productItem.setTax(productItemPojo.getTax());
		productItem.setContractItem(Boolean.TRUE == productItemPojo.getContractItem() ? true : false);
		productItem.setUnitPrice(productItemPojo.getUnitPrice());
		productItem.setUom(uom);
		productItem.setProductCategory(productCategory);
		productItem.setFavoriteSupplier(favouriteSupplier);
		productItem.setValidityDate(productItemPojo.getValidityDate());
		productItem.setRemarks(productItemPojo.getRemarks());
		productItem.setBrand(productItemPojo.getBrand());
		productItem.setGlCode(productItemPojo.getGlCode());
		productItem.setPurchaseGroupCode(productItemPojo.getPurchaseGroupCode());
		// productItem.setContractReferenceNumber(productItemPojo.getContractReferenceNumber());
		productItem.setStatus(Status.ACTIVE);
		productItem.setProductItemType(productItemPojo.getItemType());
		productItem.setBuyer(buyer);
		productItem.setCreatedDate(new Date());
		productItem.setCreatedBy(user);
		productListMaintenanceService.createProductItem(productItem, Boolean.TRUE);
	}

	@RequestMapping(value = "/productContract", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> syncItemContracts(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody ProductContractPojo productContractPojo) {
		LOG.info("Product contract Data Api called..." + productContractPojo);
		Map<String, Object> map = new HashMap<>();
		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}

			if (Boolean.FALSE == erpConfig.getEnableContractApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			if (StringUtils.checkString(productContractPojo.getContractCreator()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.contractCreator.empty", new Object[] {}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(productContractPojo.getContractCreator());

			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.contractCreator.notfound", new Object[] { productContractPojo.getContractCreator() }, Global.LOCALE));
			}

			BusinessUnit businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(erpConfig.getTenantId(), productContractPojo.getBusinessUnit());
			if (businessUnit == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { productContractPojo.getBusinessUnit() }, Global.LOCALE));
			}

			if (StringUtils.checkString(productContractPojo.getContractReferenceNumber()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.refrence.notEmpty", new Object[] {}, Global.LOCALE));
			}
			if (StringUtils.checkString(productContractPojo.getGroupCode()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.groupCode.notEmpty", new Object[] {}, Global.LOCALE));
			}
			if (productContractPojo.getContractEndDate() == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.endDate.notEmpty", new Object[] {}, Global.LOCALE));
			}
			if (productContractPojo.getContractStartDate() == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.startDate.notEmpty", new Object[] {}, Global.LOCALE));
			}

			if (productContractPojo.getOperation() == null) {
				throw new ApplicationException("Operation is null");
			}

			FavouriteSupplier favouriteSupplier = null;
			if (StringUtils.checkString(productContractPojo.getVendorCode()).length() > 0) {
				favouriteSupplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(productContractPojo.getVendorCode()), buyer.getId());
				if (favouriteSupplier == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.supplier.notExists", new Object[] { productContractPojo.getVendorCode() }, Global.LOCALE));
				}
			}

			ProductContract productContract = productContractService.findProductContractByReferenceNumber(productContractPojo.getContractReferenceNumber(), buyer.getId());

			switch (productContractPojo.getOperation()) {
			case C: { // CREATE
				if (productContract != null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productContract.exists", new Object[] { productContractPojo.getContractReferenceNumber() }, Global.LOCALE));
				} else {
					productContract = new ProductContract();
					saveProductContract(productContract, productContractPojo, buyer, user, favouriteSupplier, businessUnit);
				}
				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			case D: { // DELETE
				if (productContract != null) {
					productContract.setStatus(ContractStatus.TERMINATED);
					productContract.setModifiedBy(user);
					productContract.setModifiedDate(new Date());
					productContract = productContractService.update(productContract);

					// Termination Audit
					try {
						snapShotAuditService.doContractAudit(productContract, null, productContract, user, AuditActionType.Terminated, "Contract '" + productContract.getContractId() + "' terminated by ERP", null);
					} catch (Exception e) {
						LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
					}

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TERMINATED, "Contract '" + productContract.getContractId() + "' terminated by ERP", user.getTenantId(), user, new Date(), ModuleType.ContractList);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productContract.notExists", new Object[] { productContractPojo.getContractReferenceNumber() }, Global.LOCALE));
				}
				break;
			}
			case U: { // UPDATE
				if (productContract != null) {
					updateProductContract(productContract, productContractPojo, user, buyer, favouriteSupplier, businessUnit);

					map.put("errorcode", 0);
					map.put("errorMessage", "SUCCESS");
				} else {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.productContract.notExists", new Object[] { productContractPojo.getContractReferenceNumber() }, Global.LOCALE));
				}
				break;
			}
			case CU: { // CREATE OR UPDATE
				if (productContract != null) {
					updateProductContract(productContract, productContractPojo, user, buyer, favouriteSupplier, businessUnit);
				} else {
					productContract = new ProductContract();
					saveProductContract(productContract, productContractPojo, buyer, user, favouriteSupplier, businessUnit);
				}
				map.put("errorcode", 0);
				map.put("errorMessage", "SUCCESS");
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error Processing Contract Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	private void updateProductContract(ProductContract productContract, ProductContractPojo productContractPojo, User user, Buyer buyer, FavouriteSupplier favouriteSupplier, BusinessUnit businessUnit) throws ApplicationException {
		productContract.setBusinessUnit(businessUnit);
		// productContract.setContractReferenceNumber(productContractPojo.getContractReferenceNumber());
		productContract.setGroupCodeStr(productContractPojo.getGroupCode());
		productContract.setContractStartDate(productContractPojo.getContractStartDate());
		productContract.setContractEndDate(productContractPojo.getContractEndDate());
		productContract.setContractValue(productContractPojo.getContractValue());
		productContract.setSupplier(favouriteSupplier);
		productContract.setStatus(productContractPojo.getStatus());
		productContract.setModifiedDate(new Date());
		productContract.setModifiedBy(user);
		// for (ProductContractItems Data : productContract.getProductContractItem()) {
		// LOG.info("delete*******..." + Data.getContractItemNumber());
		// productContractItemsService.deleteBycontractItemNumber(Data.getContractItemNumber());
		// }

		if (CollectionUtil.isNotEmpty(productContractPojo.getItemList())) {
			for (ProductContractItemsPojo itemPojo : productContractPojo.getItemList()) {

				if (itemPojo.getQuantity().floatValue() <= 0.0f) {
					throw new ApplicationException("Quantity should be more than zero. Contract Item No: " + itemPojo.getContractItemNumber());
				}

				// Check with existing item in db
				ProductContractItems dbItem = null;
				for (ProductContractItems item : productContract.getProductContractItem()) {
					// Find matching using the itemCode and Storage Location and item contract number
					if (//
					item.getProductItem().getProductCode().equals(itemPojo.getItemCode()) //
							&& StringUtils.checkString(item.getStorageLocation()).equals(StringUtils.checkString(itemPojo.getStorageLoc())) //
							&& StringUtils.checkString(item.getContractItemNumber()).equals(StringUtils.checkString(itemPojo.getContractItemNumber()))) {
						dbItem = item;
						break;
					}
				}

				CostCenter costCenter = null;
				if (StringUtils.checkString(itemPojo.getCostCenter()).length() > 0) {
					costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(itemPojo.getCostCenter(), buyer.getId());
					if (costCenter == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { itemPojo.getCostCenter() }, Global.LOCALE));
					}
				}

				// new Item
				if (dbItem == null) {
					if (itemPojo.getItemType() == null) {
						throw new ApplicationException(messageSource.getMessage("itemType.missing", new Object[] {}, Global.LOCALE));
					}

					ProductItem productItem = productListMaintenanceService.findProductItemByCode(itemPojo.getItemCode(), buyer.getId(), itemPojo.getItemType());
					if (productItem == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.itemCode.notFound", new Object[] { itemPojo.getItemCode(), itemPojo.getItemType() }, Global.LOCALE));
					}

					Uom uom = uomService.getUomByUomAndTenantId(itemPojo.getUom(), buyer.getId());
					if (uom == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { itemPojo.getUom() }, Global.LOCALE));
					}

					ProductCategory productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(itemPojo.getItemCategory(), buyer.getId());
					if (productCategory == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.invalid", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() != null && !productCategory.getId().equals(productItem.getProductCategory().getId())) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.mismatch", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() == null && productCategory != null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.notFound", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() != null && productCategory == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.linked", new Object[] { productItem.getProductCategory().getProductCode() }, Global.LOCALE));
					}

					BusinessUnit businessItem = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), itemPojo.getBusinessUnit());
					if (businessItem == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { itemPojo.getBusinessUnit() }, Global.LOCALE));
					}

					dbItem = new ProductContractItems();
					dbItem.setProductContract(productContract);
					dbItem.setContractItemNumber(String.valueOf(Integer.parseInt(itemPojo.getContractItemNumber()) / 10));
					dbItem.setProductItem(productItem);
					dbItem.setQuantity(itemPojo.getQuantity());
					dbItem.setBalanceQuantity(itemPojo.getQuantity());
					dbItem.setUom(uom);
					dbItem.setUnitPrice(itemPojo.getUnitPrice());
					dbItem.setProductCategory(productCategory);
					dbItem.setBusinessUnit(businessItem);
					dbItem.setStorageLocation(itemPojo.getStorageLoc());
					dbItem.setCostCenter(costCenter);
					dbItem.setItemCode(itemPojo.getItemCode());
					dbItem.setItemName(itemPojo.getItemName());

					productContract.getProductContractItem().add(dbItem);
				} else {
					LOG.info("Updating existing item - Item Code : " + dbItem.getProductItem().getProductCode() + ", Storage Loc: " + dbItem.getStorageLocation() + ", Bal Qty : " + dbItem.getBalanceQuantity());
					// old = 100, new = 20, diff = 80 means qty reducted by 80. Then subtract that from balance qty.
					// old = 100, new = 120, diff 20 means qty increased by 20. Then add that to balance.
					BigDecimal diff = dbItem.getQuantity().subtract(itemPojo.getQuantity());
					dbItem.setQuantity(itemPojo.getQuantity());
					dbItem.setBalanceQuantity(dbItem.getBalanceQuantity().subtract(diff));
					if (dbItem.getBalanceQuantity().floatValue() < 0) {
						throw new ApplicationException("Reduction in item quantity rejected as balance is becoming negative. Item Code : " + dbItem.getProductItem().getProductCode() + ", Storage Loc: " + dbItem.getStorageLocation());
					}
					dbItem.setUnitPrice(itemPojo.getUnitPrice());
					dbItem.setCostCenter(costCenter);
				}

			}
		} else {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.notEmpty", new Object[] {}, Global.LOCALE));
		}

		productContractService.update(productContract);
	}

	private void saveProductContract(ProductContract productContract, ProductContractPojo productContractPojo, Buyer buyer, User user, FavouriteSupplier favouriteSupplier, BusinessUnit businessUnit) throws ApplicationException {
		productContract.setBusinessUnit(businessUnit);
		productContract.setContractReferenceNumber(productContractPojo.getContractReferenceNumber());
		productContract.setGroupCodeStr(productContractPojo.getGroupCode());
		productContract.setContractStartDate(productContractPojo.getContractStartDate());
		productContract.setContractEndDate(productContractPojo.getContractEndDate());
		productContract.setContractValue(productContractPojo.getContractValue());
		productContract.setSupplier(favouriteSupplier);
		productContract.setStatus(ContractStatus.ACTIVE);
		productContract.setDecimal("2");
		productContract.setContractId(eventIdSettingDao.generateEventIdByBusinessUnit(user.getTenantId(), "CTR", null));
		Currency currency = currencyDao.findByCurrencyCode("MYR");
		productContract.setCurrency(currency);
		productContract.setBuyer(buyer);
		productContract.setCreatedDate(new Date());
		productContract.setCreatedBy(user);

		productContract.setSapContractNumber(productContractPojo.getContractReferenceNumber());

		List<ProductContractItems> listItem = new ArrayList<>();
		Set<ProductCategory> productCategoryIdList = new HashSet<ProductCategory>();

		if (CollectionUtil.isNotEmpty(productContractPojo.getItemList())) {
			for (ProductContractItemsPojo itemPojo : productContractPojo.getItemList()) {

				if (itemPojo.getQuantity().floatValue() <= 0.0f) {
					throw new ApplicationException("Quantity should be more than zero. Contract Item No: " + itemPojo.getContractItemNumber());
				}

				if (itemPojo.getItemType() == null) {
					throw new ApplicationException(messageSource.getMessage("itemType.missing", new Object[] {}, Global.LOCALE));
				}

				ProductItem productItem = productListMaintenanceService.findProductItemByCode(itemPojo.getItemCode(), buyer.getId(), itemPojo.getItemType());

				if (productItem == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.itemCode.notFound", new Object[] { itemPojo.getItemCode(), itemPojo.getItemType() }, Global.LOCALE));
				}

				Uom uom = uomService.getUomByUomAndTenantId(itemPojo.getUom(), buyer.getId());
				if (uom == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { itemPojo.getUom() }, Global.LOCALE));
				}

				CostCenter costCenter = null;
				if (StringUtils.checkString(itemPojo.getCostCenter()).length() > 0) {
					costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(itemPojo.getCostCenter(), buyer.getId());
					if (costCenter == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { itemPojo.getCostCenter() }, Global.LOCALE));
					}
				}

				ProductCategory productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(itemPojo.getItemCategory(), buyer.getId());
				if (productCategory == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.category.invalid", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
				}

				if (productItem.getProductCategory() != null && !productCategory.getId().equals(productItem.getProductCategory().getId())) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.category.mismatch", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
				}

				if (productItem.getProductCategory() == null && productCategory != null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.category.notFound", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
				}

				if (productItem.getProductCategory() != null && productCategory == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.category.linked", new Object[] { productItem.getProductCategory().getProductCode() }, Global.LOCALE));
				}

				BusinessUnit businessItem = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), itemPojo.getBusinessUnit());
				if (businessItem == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { itemPojo.getBusinessUnit() }, Global.LOCALE));
				}
				productCategoryIdList.add(productItem.getProductCategory());

				ProductContractItems item = new ProductContractItems();
				item.setProductContract(productContract);
				item.setContractItemNumber(String.valueOf(Integer.parseInt(itemPojo.getContractItemNumber()) / 10));
				item.setProductItem(productItem);
				item.setQuantity(itemPojo.getQuantity());
				item.setBalanceQuantity(itemPojo.getQuantity());
				item.setUom(uom);
				item.setUnitPrice(itemPojo.getUnitPrice());
				item.setProductCategory(productCategory);
				item.setBusinessUnit(businessItem);
				item.setStorageLocation(itemPojo.getStorageLoc());
				item.setCostCenter(costCenter);
				item.setItemCode(itemPojo.getItemCode());
				item.setItemName(itemPojo.getItemName());
				listItem.add(item);
			}
		} else {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.notEmpty", new Object[] {}, Global.LOCALE));
		}

		// persistObj.setProductContractItem(listItem);
		// productContractService.update(persistObj);
		productContract.setProductContractItem(listItem);
		productContract = productContractService.createProductContract(productContract);
		ProductContractNotifyUsers usr = new ProductContractNotifyUsers();
		usr.setUser(user);
		usr.setProductContract(productContract);

		productContractNotifyUsersDao.saveOrUpdate(usr);

		List<ProductCategory> favprdctCategoryList = new ArrayList<ProductCategory>();
		List<ProductCategory> tempList = new ArrayList<ProductCategory>();

		if (CollectionUtil.isNotEmpty(favouriteSupplier.getProductCategory())) {
			LOG.info("favouriteSupplier  category not empty if");

			favprdctCategoryList = favouriteSupplier.getProductCategory();
			for (ProductCategory category : productCategoryIdList) {
				LOG.info("category  : " + category.getId());

				if (!favprdctCategoryList.contains(category)) {
					LOG.info(" not Contain : ");
					ProductCategory productCat = new ProductCategory();
					productCat.setId(category.getId());
					tempList.add(productCat);
				}

			}
		} else {
			LOG.info("else");
			for (ProductCategory category : productCategoryIdList) {
				LOG.info("add direct " + category.getId());
				ProductCategory productCat = new ProductCategory();
				productCat.setId(category.getId());
				tempList.add(productCat);

			}

		}
		favprdctCategoryList.addAll(tempList);
		favouriteSupplier.setProductCategory(favprdctCategoryList);
		favoriteSupplierService.updateFavoriteSupplier(favouriteSupplier);

	}

	@RequestMapping(value = "/sapPrResponse", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sapPrResponse(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody PrResponsePojo prResponse) {
		LOG.info("PR Response Api called..." + prResponse);
		Map<String, Object> map = new HashMap<>();
		map.put("responseId", prResponse.getTransactionId());
		map.put("resTransactionId", prResponse.getConsumerReferenceId());
		map.put("providerId", "Procurehere");
		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}

			if (Boolean.FALSE == erpConfig.getEnableSapPrResponseApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			if (prResponse.getTransactionType() == ProcurehereDocumentType.PR) {

				String prId = prResponse.getTransactionId();
				Pr pr = prService.findPrBySystemGeneratedPrIdAndTenantId(prId, buyer.getId());

				if (pr == null) {
					throw new ApplicationException("Invalid Procurehere PR No received from ERP : " + prId);
				}

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}
				PrAudit audit = new PrAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setBuyer(buyer);
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {

					// If Failed at ERP, then reset the status of PR to APPROVED so that it can be transferred again.
					pr.setErpDocNo(null);

					String message = "";
					try {
						if (prResponse.getMessage() instanceof List) {
							HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) prResponse.getMessage()).get(0);
							message = hashMap.get("msg_item");
						} else {
							HashMap<String, String> hashMap = (HashMap<String, String>) prResponse.getMessage();
							message = hashMap.get("msg_item");
						}
					} catch (Exception k) {

					}

					pr.setErpMessage(message);
					pr.setErpPrTransferred(Boolean.FALSE);
					pr.setErpStatus(null);
					pr.setStatus(PrStatus.APPROVED);
					prService.updatePr(pr);

					audit.setAction(PrAuditType.TRANSFERRED);
					audit.setDescription("Received failed response from ERP : " + prResponse.getMessage() + " Ref: " + prResponse.getSapRefNo());
					audit.setPr(pr);

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, "received failed response from ERP : " + prResponse.getMessage() + " Ref: " + prResponse.getSapRefNo(), user.getTenantId(), user, new Date(), ModuleType.PR);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					prAuditService.save(audit);
				} else {
					pr.setErpDocNo(prResponse.getSapRefNo());
					pr.setErpMessage((String) prResponse.getMessage());
					pr.setErpPrTransferred(Boolean.TRUE);
					pr.setErpStatus(prResponse.getStatus());

					audit.setAction(PrAuditType.DELIVERED);
					audit.setDescription(prResponse.getSapDocType() + " created in ERP with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage());
					audit.setPr(pr);
					prAuditService.save(audit);

					// Create PO
					Boolean isAutoCreatePo = buyerSettingsService.isAutoCreatePoSettingsByTenantId(buyer.getId());
					if (Boolean.TRUE == erpConfig.getIsGeneratePo() && isAutoCreatePo) {
						Po po = prService.createPo(pr.getCreatedBy(), pr);
						pr.setIsPo(Boolean.TRUE);
						approvalService.sendPoCreatedEmailToCreater(pr.getCreatedBy(), pr, pr.getCreatedBy());
						try {
							if (po.getStatus() == PoStatus.ORDERED) {
								po = poService.getLoadedPoById(po.getId());
								if (po.getSupplier() != null) {
									approvalService.sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());
								}
							}
						} catch (Exception e) {
							LOG.error("Error while sending PO email notification to supplier:" + e.getMessage(), e);
						}
					}
					prService.updatePr(pr);

				}

			} else if (prResponse.getTransactionType() == ProcurehereDocumentType.RFS) {

				String rfsId = prResponse.getTransactionId();

				SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.getSourcingFormByFormIdAndTenant(rfsId, buyer.getId());

				if (sourcingFormRequest == null) {
					throw new ApplicationException("Invalid Procurehere SR No received from ERP : " + rfsId);
				}

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				RequestAudit audit = new RequestAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setBuyer(buyer);
				audit.setReq(sourcingFormRequest);
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {

					if (prResponse.getOperation() == OperationType.D) {
						// If Failed at ERP, then reset the status of PR to APPROVED so that it can be transferred
						// again.
						audit.setAction(RequestAuditType.ERROR);
						audit.setDescription("Received failed response from ERP for Delete : " + prResponse.getMessage() + " Ref: " + prResponse.getSapRefNo());

					} else {
						// If Failed at ERP, then reset the status of PR to APPROVED so that it can be transferred
						// again.
						audit.setAction(RequestAuditType.ERROR);
						audit.setDescription("Received failed response from ERP : " + prResponse.getMessage() + " Ref: " + prResponse.getSapRefNo());

						sourcingFormRequest.setStatus(SourcingFormStatus.DRAFT);
						sourcingFormRequest.setSummaryCompleted(false);
						sourcingFormRequest.setErpDocNo(null);

						String message = "";
						try {
							if (prResponse.getMessage() instanceof List) {
								HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) prResponse.getMessage()).get(0);
								message = hashMap.get("msg_item");
							} else {
								HashMap<String, String> hashMap = (HashMap<String, String>) prResponse.getMessage();
								message = hashMap.get("msg_item");
							}
						} catch (Exception k) {

						}

						sourcingFormRequest.setErpMessage(message);
						sourcingFormRequest.setErpTransferred(Boolean.FALSE);
						sourcingFormRequest.setErpStatus(prResponse.getStatus());
						sourcingFormRequestService.update(sourcingFormRequest);
					}

				} else {

					audit.setAction(RequestAuditType.ERP);

					if (prResponse.getOperation() != OperationType.D) {
						audit.setDescription(prResponse.getSapDocType() + " created in ERP with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage());
						// Must call Approval method before saving the SAP Response into Request. There is logic
						// attached to
						// it
						sourcingFormRequest = approvalService.doRequestApproval(sourcingFormRequest, sourcingFormRequest.getFormOwner());

						sourcingFormRequest.setErpDocNo(prResponse.getSapRefNo());

						String message = "";
						try {
							if (prResponse.getMessage() instanceof List) {
								HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) prResponse.getMessage()).get(0);
								message = hashMap.get("msg_item");
							} else {
								HashMap<String, String> hashMap = (HashMap<String, String>) prResponse.getMessage();
								message = hashMap.get("msg_item");
							}
						} catch (Exception k) {

						}

						sourcingFormRequest.setErpMessage(message);
						sourcingFormRequest.setErpTransferred(Boolean.TRUE);
						sourcingFormRequest.setErpStatus(prResponse.getStatus());
						sourcingFormRequest = sourcingFormRequestService.update(sourcingFormRequest);

						try {

							tatReportService.updateTatReportSapPrId(sourcingFormRequest.getId(), prResponse.getSapRefNo(), buyer.getId());
						} catch (Exception e) {
							LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
						}

					} else {
						audit.setDescription(prResponse.getSapDocType() + " deleted in ERP with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage());
					}
				}
				requestAuditDao.save(audit);

			} else if (prResponse.getTransactionType() == ProcurehereDocumentType.RFQ) {

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				String eventId = prResponse.getTransactionId();

				if (StringUtils.checkString(eventId).length() == 0) {
					throw new ApplicationException("Received empty Transaction ID for RFQ Response");
				}

				RfqEvent event = rfqEventService.getPlainEventByFormattedEventIdAndTenantId(eventId, buyer.getId());
				if (event == null) {
					throw new ApplicationException("Invalid Procurehere RFQ ID received from ERP : " + eventId);
				}

				// PH-1178 - Revert status of event to COMPLETE so that the save and transfer of award can be done again
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {
					LOG.warn("SAP Response FAIL received for event " + eventId);
					// event.setStatus(EventStatus.COMPLETE);
					// event = rfqEventService.updateEvent(event);
					rfqEventService.revertEventAwardStatus(event.getId());

					try {
						tatReportService.updateTatReportOnSapResponse(event.getId(), EventStatus.COMPLETE, buyer.getId());
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				} else {
					LOG.info("SAP Response success received for event " + eventId);
					try {
						TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(), buyer.getId());
						if (tatReport != null) {
							setSapPoNumbers(prResponse, buyer, tatReport);
							double diffPOcDAndRlad = DateUtil.differenceInDays(new Date(), tatReport.getLastApprovedDate());
							BigDecimal diffPOcDAndRladBigD = BigDecimal.valueOf(diffPOcDAndRlad).setScale(2, RoundingMode.HALF_UP);
							BigDecimal overallTat = diffPOcDAndRladBigD.subtract(tatReport.getPaperApprovalDaysCount() != null ? tatReport.getPaperApprovalDaysCount() : BigDecimal.ZERO);
							tatReportService.updateTatReportSapoDetails(tatReport.getId(), prResponse.getSapRefNo(), new Date(), overallTat);
						}
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				}

				String message = "";
				message = "ERP replied " + prResponse.getStatus() + " for " + prResponse.getSapDocType() + " with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage();
				// RfqEventAudit audit = new RfqEventAudit(buyer, event, user, new Date(), AuditActionType.Transfer,
				// message, null);
				RfqEventAudit audit = new RfqEventAudit(event, user, new Date(), AuditActionType.Transfer, message);

				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, message, user.getTenantId(), user, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				eventAuditService.save(audit);

			} else if (prResponse.getTransactionType() == ProcurehereDocumentType.RFT) {

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				String eventId = prResponse.getTransactionId();
				if (StringUtils.checkString(eventId).length() == 0) {
					throw new ApplicationException("Received empty Transaction ID for RFQ Response");
				}

				RftEvent event = rftEventService.getPlainEventByFormattedEventIdAndTenantId(eventId, buyer.getId());
				if (event == null) {
					throw new ApplicationException("Invalid Procurehere RFT ID received from ERP : " + eventId);
				}

				// PH-1178 - Revert status of event to COMPLETE so that the save and transfer of award can be done again
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {
					LOG.warn("SAP Response FAIL received for event " + eventId);
					// event.setStatus(EventStatus.COMPLETE);
					// event = rftEventService.updateEventSimple(event);
					rftEventService.revertEventAwardStatus(event.getId());

					try {
						tatReportService.updateTatReportOnSapResponse(event.getId(), EventStatus.COMPLETE, buyer.getId());
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				} else {
					LOG.info("SAP Response success received for event " + eventId);
					try {
						TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(), buyer.getId());
						if (tatReport != null) {
							setSapPoNumbers(prResponse, buyer, tatReport);
							double diffPOcDAndRlad = DateUtil.differenceInDays(new Date(), tatReport.getLastApprovedDate());
							BigDecimal diffPOcDAndRladBigD = BigDecimal.valueOf(diffPOcDAndRlad).setScale(2, RoundingMode.HALF_UP);
							BigDecimal overallTat = diffPOcDAndRladBigD.subtract(tatReport.getPaperApprovalDaysCount() != null ? tatReport.getPaperApprovalDaysCount() : BigDecimal.ZERO);
							tatReportService.updateTatReportSapoDetails(tatReport.getId(), tatReport.getSapPoId(), new Date(), overallTat);

						}
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				}

				String message = "";
				message = "ERP replied " + prResponse.getStatus() + " for " + prResponse.getSapDocType() + " with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage();
				// RftEventAudit audit = new RftEventAudit(buyer, event, user, new Date(), AuditActionType.Transfer,
				// message, null);
				RftEventAudit audit = new RftEventAudit(event, user, new Date(), AuditActionType.Transfer, message);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, message, user.getTenantId(), user, new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				eventAuditService.save(audit);

			} else if (prResponse.getTransactionType() == ProcurehereDocumentType.RFP) {

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				String eventId = prResponse.getTransactionId();
				if (StringUtils.checkString(eventId).length() == 0) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.transection.empty", new Object[] {}, Global.LOCALE));

				}

				RfpEvent event = rfpEventService.getPlainEventByFormattedEventIdAndTenantId(eventId, buyer.getId());
				if (event == null) {
					throw new ApplicationException(messageSource.getMessage("invalid.rfp.id", new Object[] { eventId }, Global.LOCALE));
				}

				// PH-1178 - Revert status of event to COMPLETE so that the save and transfer of award can be done again
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {
					LOG.warn("SAP Response FAIL received for event " + eventId);
					// event.setStatus(EventStatus.COMPLETE);
					// event = rfpEventService.updateEvent(event);

					rfpEventService.revertEventAwardStatus(event.getId());
					try {
						tatReportService.updateTatReportOnSapResponse(event.getId(), EventStatus.COMPLETE, buyer.getId());
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				} else {
					LOG.warn("SAP Response FAIL received for event " + eventId);
					try {
						TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(), buyer.getId());
						if (tatReport != null) {
							setSapPoNumbers(prResponse, buyer, tatReport);
							double diffPOcDAndRlad = DateUtil.differenceInDays(new Date(), tatReport.getLastApprovedDate());
							BigDecimal diffPOcDAndRladBigD = BigDecimal.valueOf(diffPOcDAndRlad).setScale(2, RoundingMode.HALF_UP);
							BigDecimal overallTat = diffPOcDAndRladBigD.subtract(tatReport.getPaperApprovalDaysCount() != null ? tatReport.getPaperApprovalDaysCount() : BigDecimal.ZERO);
							tatReportService.updateTatReportSapoDetails(tatReport.getId(), tatReport.getSapPoId(), new Date(), overallTat);
						}
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				}

				String message = "";
				message = "ERP replied " + prResponse.getStatus() + " for " + prResponse.getSapDocType() + " with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage();
				// RfpEventAudit audit = new RfpEventAudit(buyer, event, user, new Date(), AuditActionType.Transfer,
				// message, null);
				RfpEventAudit audit = new RfpEventAudit(event, user, new Date(), AuditActionType.Transfer, message);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, message, user.getTenantId(), user, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				eventAuditService.save(audit);

			} else if (prResponse.getTransactionType() == ProcurehereDocumentType.RFA) {

				User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
				if (user == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
				}

				String eventId = prResponse.getTransactionId();
				if (StringUtils.checkString(eventId).length() == 0) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.transection.empty", new Object[] {}, Global.LOCALE));
				}

				RfaEvent event = rfaEventService.getPlainEventByFormattedEventIdAndTenantId(eventId, buyer.getId());
				if (event == null) {
					throw new ApplicationException(messageSource.getMessage("invalid.rfa.id", new Object[] { eventId }, Global.LOCALE));
				}

				// PH-1178 - Revert status of event to COMPLETE so that the save and transfer of award can be done again
				if (StringUtils.checkString(prResponse.getStatus()).equalsIgnoreCase("FAIL")) {

					LOG.warn("SAP Response FAIL received for event " + eventId);
					// event.setStatus(EventStatus.COMPLETE);
					// event = rfaEventService.updateRfaEvent(event);

					rfaEventService.revertEventAwardStatus(event.getId());

					try {
						tatReportService.updateTatReportOnSapResponse(event.getId(), EventStatus.COMPLETE, buyer.getId());
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				} else {
					LOG.warn("SAP Response FAIL received for event " + eventId);
					try {
						TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(), buyer.getId());
						if (tatReport != null) {
							setSapPoNumbers(prResponse, buyer, tatReport);
							double diffPOcDAndRlad = DateUtil.differenceInDays(new Date(), tatReport.getLastApprovedDate());
							BigDecimal diffPOcDAndRladBigD = BigDecimal.valueOf(diffPOcDAndRlad).setScale(2, RoundingMode.HALF_UP);
							BigDecimal overallTat = diffPOcDAndRladBigD.subtract(tatReport.getPaperApprovalDaysCount() != null ? tatReport.getPaperApprovalDaysCount() : BigDecimal.ZERO);
							tatReportService.updateTatReportSapoDetails(tatReport.getId(), tatReport.getSapPoId(), new Date(), overallTat);
						}
					} catch (Exception e) {
						LOG.error("ERROR while saving tat report data : " + e.getMessage(), e);
					}
				}

				String message = "";
				message = "ERP replied " + prResponse.getStatus() + " for " + prResponse.getSapDocType() + " with Reference : " + prResponse.getSapRefNo() + " Response: " + prResponse.getMessage();
				// RfaEventAudit audit = new RfaEventAudit(buyer, event, user, new Date(), AuditActionType.Transfer,
				// message, null);
				RfaEventAudit audit = new RfaEventAudit(event, user, new Date(), AuditActionType.Transfer, message);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TRANSFERRED, message, user.getTenantId(), user, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				eventAuditService.save(audit);

			} else {
				LOG.info("Bhad mein ja Transaction Type : " + prResponse.getTransactionType() + " ko lekar.");
			}

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

			map.put("resTimestamp", df.format(new Date()));
			map.put("resStatus", "SUCC");
			map.put("msgCode", "IM-001");
			map.put("msgDesc", "Message received successfully");
		} catch (Exception e) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			LOG.error("Error Processing ERP PR Response : " + e.getMessage(), e);
			map.put("resTimestamp", df.format(new Date()));
			map.put("resStatus", "FAIL");
			map.put("msgCode", "IM-002");
			map.put("msgDesc", "Error Processing ERP Response : " + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	private void setSapPoNumbers(PrResponsePojo prResponse, Buyer buyer, TatReportPojo tatReport) {
		String message = "";

		try {
			if (prResponse.getMessage() instanceof List) {
				HashMap<String, String> hashMap = (HashMap<String, String>) ((List<?>) prResponse.getMessage()).get(0);
				message = hashMap.get("msg_item");
			} else {
				HashMap<String, String> hashMap = (HashMap<String, String>) prResponse.getMessage();
				message = hashMap.get("msg_item");
			}
		} catch (Exception k) {

		}

		if (StringUtils.checkString(message).length() > 11 && message.indexOf("(") != -1) {
			String vendorCode = message.substring(10, message.indexOf("("));
			String poId = prResponse.getSapRefNo();
			// get the supplier based on vendorCode and buyerId
			String supplierName = favoriteSupplierService.getSupplierNameByVendorCode(vendorCode, buyer.getId());
			if (StringUtils.checkString(supplierName).length() > 0) {
				String awardedSupp = tatReport.getAwardedSupplier();
				if (StringUtils.checkString(awardedSupp).length() > 0) {
					String[] awardedSupps = awardedSupp.split(",");
					String awardedSuppliersPos = tatReport.getSapPoId();
					int index = -1;
					for (String sup : awardedSupps) {
						if (!StringUtils.checkString(sup).equalsIgnoreCase(StringUtils.checkString(supplierName))) {
							index++;
						} else {
							break;
						}
					}

					if (StringUtils.checkString(awardedSuppliersPos).length() > 0) {
						String[] poIds = awardedSuppliersPos.split(",");
						for (int i = 0; i < poIds.length; i++) {
							poIds[i] = StringUtils.checkString(poIds[i]);
						}
						poIds[index + 1] = poId;
						tatReport.setSapPoId(String.join(", ", poIds));
					} else {
						String poIds[] = new String[awardedSupps.length];
						for (int i = 0; i < poIds.length; i++) {
							poIds[i] = "";
						}
						poIds[index + 1] = poId;
						tatReport.setSapPoId(String.join(", ", poIds));
					}
				} else {
					LOG.warn("Award Supplier details not found in TatReport ");
				}
			} else {
				LOG.warn("Supplier details not found for Vendor code " + vendorCode);
			}
		}
	}

	@RequestMapping(value = "/createPo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createPO(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody PoPojo poPojo) {
		LOG.info("Create PO Api called..." + poPojo);
		Map<String, Object> map = new HashMap<>();
		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}

			if (Boolean.FALSE == erpConfig.getEnablePoCreateApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (user == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[] {}, Global.LOCALE));
			}

			CostCenter costCenter = null;
			if (StringUtils.checkString(poPojo.getCostCenter()).length() > 0) {
				costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(poPojo.getCostCenter(), buyer.getId());
				if (costCenter == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { poPojo.getCostCenter() }, Global.LOCALE));
				}
			}

			BusinessUnit businessUnit = null;
			if (StringUtils.checkString(poPojo.getBusinessUnit()).length() > 0) {
				businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), poPojo.getBusinessUnit());
				if (businessUnit == null) {
					throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { poPojo.getBusinessUnit() }, Global.LOCALE));
				}
			}

			BuyerAddress correspondenceAddress = null;

			if (poPojo.getCorrespondenceAddress() == null) {
				// throw new ApplicationException("Missing correspondence address.");
			} else {
				correspondenceAddress = new BuyerAddress();
				if (StringUtils.checkString(poPojo.getCorrespondenceAddress().getTitle()).length() == 0) {
					throw new ApplicationException("Missing Correspondence Address Title.");
				}
				if (StringUtils.checkString(poPojo.getCorrespondenceAddress().getLine1()).length() == 0) {
					throw new ApplicationException("Missing Correspondence Address Line 1.");
				}
				if (StringUtils.checkString(poPojo.getCorrespondenceAddress().getLine2()).length() == 0) {
					throw new ApplicationException("Missing Correspondence Address Line 2.");
				}
				if (StringUtils.checkString(poPojo.getCorrespondenceAddress().getCity()).length() == 0) {
					throw new ApplicationException("Missing Correspondence Address City.");
				}
				if (StringUtils.checkString(poPojo.getCorrespondenceAddress().getZipCode()).length() == 0) {
					throw new ApplicationException("Missing Correspondence Address zipCode.");
				}

				correspondenceAddress.setBuyer(buyer);
				correspondenceAddress.setTitle(poPojo.getCorrespondenceAddress().getTitle());
				correspondenceAddress.setLine1(poPojo.getCorrespondenceAddress().getLine1());
				correspondenceAddress.setLine2(poPojo.getCorrespondenceAddress().getLine2());
				correspondenceAddress.setCity(poPojo.getCorrespondenceAddress().getCity());
				correspondenceAddress.setZip(poPojo.getCorrespondenceAddress().getZipCode());
				String countryCode = poPojo.getCorrespondenceAddress().getCountryCode();
				if (StringUtils.checkString(countryCode).length() == 0) {
					throw new ApplicationException("Missing Country.");
				}
				Country country = countryService.getCountryByCode(countryCode);
				if (country == null) {
					throw new ApplicationException("Invalid country code : " + countryCode);
				}
				correspondenceAddress.setCountry(country);
				String stateCode = poPojo.getCorrespondenceAddress().getState();
				if (StringUtils.checkString(stateCode).length() == 0) {
					throw new ApplicationException("Missing State.");
				}

				List<State> stateList = stateService.searchStatesByNameOrCode(stateCode);
				if (CollectionUtil.isEmpty(stateList)) {
					throw new ApplicationException("Invalid State Name or Code : " + stateCode);
				}

				// Find matching state within the given country ( TODO: Or write a query to fetch it based on country so
				// you dont have to loop to a list)
				State state = null;
				for (State st : stateList) {
					if (st.getCountry().getCountryCode().equals(countryCode)) {
						state = st;
						break;
					}
				}
				if (state == null) {
					throw new ApplicationException("Invalid State Name or Code for given country: " + stateCode);
				}

				correspondenceAddress.setState(state);
				correspondenceAddress.setStatus(Status.ACTIVE);
			}

			BuyerAddress deliveryAddress = null;
			if (poPojo.getDeliveryAddress() == null) {
				// throw new ApplicationException("Missing delivery address.");
			} else {
				deliveryAddress = new BuyerAddress();
				deliveryAddress.setBuyer(buyer);

				if (StringUtils.checkString(poPojo.getDeliveryAddress().getTitle()).length() == 0) {
					throw new ApplicationException("Missing Delivery Address Title.");
				}
				if (StringUtils.checkString(poPojo.getDeliveryAddress().getLine1()).length() == 0) {
					throw new ApplicationException("Missing Delivery Address Line 1.");
				}
				if (StringUtils.checkString(poPojo.getDeliveryAddress().getLine2()).length() == 0) {
					throw new ApplicationException("Missing Delivery Address Line 2.");
				}
				if (StringUtils.checkString(poPojo.getDeliveryAddress().getCity()).length() == 0) {
					throw new ApplicationException("Missing Delivery Address City.");
				}
				if (StringUtils.checkString(poPojo.getDeliveryAddress().getZipCode()).length() == 0) {
					throw new ApplicationException("Missing Delivery Address zipCode.");
				}

				deliveryAddress.setTitle(poPojo.getDeliveryAddress().getTitle());
				deliveryAddress.setLine1(poPojo.getDeliveryAddress().getLine1());
				deliveryAddress.setLine2(poPojo.getDeliveryAddress().getLine2());
				deliveryAddress.setCity(poPojo.getDeliveryAddress().getCity());
				deliveryAddress.setZip(poPojo.getDeliveryAddress().getZipCode());
				String countryCode = poPojo.getDeliveryAddress().getCountryCode();
				if (StringUtils.checkString(countryCode).length() == 0) {
					throw new ApplicationException("Missing Country.");
				}
				Country country = countryService.getCountryByCode(countryCode);
				if (country == null) {
					throw new ApplicationException("Invalid country code : " + countryCode);
				}
				deliveryAddress.setCountry(country);
				String stateCode = poPojo.getDeliveryAddress().getState();
				if (StringUtils.checkString(stateCode).length() == 0) {
					throw new ApplicationException("Missing State.");
				}

				List<State> stateList = stateService.searchStatesByNameOrCode(stateCode);
				if (CollectionUtil.isEmpty(stateList)) {
					throw new ApplicationException("Invalid State Name or Code : " + stateCode);
				}

				// Find matching state within the given country ( TODO: Or write a query to fetch it based on country so
				// you dont have to loop to a list)
				State state = null;
				for (State st : stateList) {
					if (st.getCountry().getCountryCode().equals(countryCode)) {
						state = st;
						break;
					}
				}
				if (state == null) {
					throw new ApplicationException("Invalid State Name or Code for given country: " + stateCode);
				}

				deliveryAddress.setState(state);
				deliveryAddress.setStatus(Status.ACTIVE);
			}

			Currency currency = null;
			if (StringUtils.checkString(poPojo.getCurrency()).length() == 0) {
				throw new ApplicationException("Invalid Currency Code: " + poPojo.getCurrency());
			}

			currency = currencyDao.findByCurrencyCode(poPojo.getCurrency());
			if (currency == null) {
				throw new ApplicationException("Invalid Currency Code: " + poPojo.getCurrency());
			}

			if (poPojo.getDecimal() == null || (poPojo.getDecimal() != null && poPojo.getDecimal().intValue() == 0)) {
				throw new ApplicationException("Invalid Decimal Value: " + poPojo.getDecimal());
			}

			FavouriteSupplier supplier = null;
			if (StringUtils.checkString(poPojo.getSupplierCode()).length() > 0) {
				supplier = favoriteSupplierService.getFavouriteSupplierByVendorCode(StringUtils.checkString(poPojo.getSupplierCode()), buyer.getId());
				if (supplier == null) {
					throw new ApplicationException("Invalid Supplier Code: " + poPojo.getSupplierCode());
				}
			}

			Po po = new Po();
			po.setBuyer(buyer);
			po.setBusinessUnit(businessUnit);
			po.setCorrespondenceAddress(correspondenceAddress);
			po.setCostCenter(costCenter);
			po.setCreatedBy(user);
			po.setCreatedDate(poPojo.getCreatedDate());
			po.setCurrency(currency);
			po.setDecimal(String.valueOf(poPojo.getDecimal()));
			po.setDeliveryAddress(deliveryAddress);
			po.setDeliveryDate(poPojo.getDeliveryDate());
			po.setDeliveryReceiver(poPojo.getDeliveryReceiver());
			// po.setDescription(poPojo.getDe);
			po.setErpDocNo(poPojo.getReferenceNumber());
			// po.setErpMessage(poPojo.get);
			po.setErpPrTransferred(Boolean.FALSE);
			// po.setErpStatus(erpStatus);
			po.setGrandTotal(poPojo.getGrandTotal());
			po.setIsPoReportSent(Boolean.FALSE);
			po.setName(poPojo.getName());
			po.setPaymentTerm(poPojo.getPaymentTerm());
			po.setPoNumber(poPojo.getReferenceNumber());
			po.setReferenceNumber(poPojo.getReferenceNumber());
			po.setRemarks(poPojo.getRemarks());
			po.setRequester(poPojo.getRequester());
			po.setStatus(PoStatus.ORDERED);
			po.setOrderedBy(user);
			po.setOrderedDate(new Date());
			po.setSupplier(supplier);
			if (supplier == null) {
				po.setSupplierName(poPojo.getSupplierName());
				// po.setSupplierAddress(poPojo.get);
				// po.setSupplierTelNumber(supplier.getCompanyContactNumber());
				// po.setSupplierAddress(supplierAddress);
				// po.setSupplierTaxNumber(supplier.getFavouriteSupplierTaxNumber());
				// po.setSupplierFaxNumber(supplier.getFavouriteSupplierTaxNumber());
			}
			// po.setTaxDescription(taxDescription);
			po.setTermsAndConditions(poPojo.getTermsAndConditions());
			po.setTotal(poPojo.getGrandTotal()); // as there is no additional tax coming from SAP
			po.setUrgentPo(Boolean.FALSE);

			List<PoItem> poItems = new ArrayList<PoItem>();

			if (CollectionUtil.isNotEmpty(poPojo.getItemList())) {
				PoItem parent = new PoItem();
				parent.setItemName("Bill of Items");
				parent.setLevel(1);
				parent.setOrder(0);

				for (PoItemsPojo itemPojo : poPojo.getItemList()) {
					PoItem item = new PoItem();

					if (itemPojo.getQuantity().floatValue() <= 0.0f) {
						throw new ApplicationException("Quantity should be more than zero. Item No: " + itemPojo.getItemNo());
					}

					if (itemPojo.getItemType() == null) {
						throw new ApplicationException(messageSource.getMessage("itemType.missing", new Object[] {}, Global.LOCALE));
					}

					ProductItem productItem = productListMaintenanceService.findProductItemByCode(itemPojo.getItemCode(), buyer.getId(), itemPojo.getItemType());

					if (productItem == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.itemCode.notFound", new Object[] { itemPojo.getItemCode(), itemPojo.getItemType() }, Global.LOCALE));
					}

					Uom uom = uomService.getUomByUomAndTenantId(itemPojo.getUom(), buyer.getId());
					if (uom == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.uom.invalid", new Object[] { itemPojo.getUom() }, Global.LOCALE));
					}

					ProductCategory productCategory = productCategoryMaintenanceService.getProductCategoryAndTenantId(itemPojo.getItemCategory(), buyer.getId());
					if (productCategory == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.invalid", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() != null && !productCategory.getId().equals(productItem.getProductCategory().getId())) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.mismatch", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() == null && productCategory != null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.notFound", new Object[] { itemPojo.getItemCategory() }, Global.LOCALE));
					}

					if (productItem.getProductCategory() != null && productCategory == null) {
						throw new ApplicationException(messageSource.getMessage("erpIntegration.category.linked", new Object[] { productItem.getProductCategory().getProductCode() }, Global.LOCALE));
					}

					if (itemPojo.getDeliveryAddress() == null) {
						// throw new ApplicationException("Missing delivery address.");
					} else {

						deliveryAddress = new BuyerAddress();

						deliveryAddress.setBuyer(buyer);

						if (StringUtils.checkString(itemPojo.getDeliveryAddress().getTitle()).length() == 0) {
							throw new ApplicationException("Missing Delivery Address Title.");
						}
						if (StringUtils.checkString(itemPojo.getDeliveryAddress().getLine1()).length() == 0) {
							throw new ApplicationException("Missing Delivery Address Line 1.");
						}
						if (StringUtils.checkString(itemPojo.getDeliveryAddress().getLine2()).length() == 0) {
							throw new ApplicationException("Missing Delivery Address Line 2.");
						}
						if (StringUtils.checkString(itemPojo.getDeliveryAddress().getCity()).length() == 0) {
							throw new ApplicationException("Missing Delivery Address City.");
						}
						if (StringUtils.checkString(itemPojo.getDeliveryAddress().getZipCode()).length() == 0) {
							throw new ApplicationException("Missing Delivery Address zipCode.");
						}

						deliveryAddress.setTitle(itemPojo.getDeliveryAddress().getTitle());
						deliveryAddress.setLine1(itemPojo.getDeliveryAddress().getLine1());
						deliveryAddress.setLine2(itemPojo.getDeliveryAddress().getLine2());
						deliveryAddress.setCity(itemPojo.getDeliveryAddress().getCity());
						deliveryAddress.setZip(itemPojo.getDeliveryAddress().getZipCode());
						String countryCode = itemPojo.getDeliveryAddress().getCountryCode();
						if (StringUtils.checkString(countryCode).length() == 0) {
							throw new ApplicationException("Missing Country.");
						}
						Country country = countryService.getCountryByCode(countryCode);
						if (country == null) {
							throw new ApplicationException("Invalid country code : " + countryCode);
						}
						deliveryAddress.setCountry(country);

						String stateCode = itemPojo.getDeliveryAddress().getState();
						if (StringUtils.checkString(stateCode).length() == 0) {
							throw new ApplicationException("Missing State.");
						}
						List<State> stateList = stateService.searchStatesByNameOrCode(stateCode);
						if (CollectionUtil.isEmpty(stateList)) {
							throw new ApplicationException("Invalid State Name or Code : " + stateCode);
						}

						// Find matching state within the given country ( TODO: Or write a query to fetch it based on
						// country so you dont have to loop to a list)
						State state = null;
						for (State st : stateList) {
							if (st.getCountry().getCountryCode().equals(countryCode)) {
								state = st;
								break;
							}
						}
						if (state == null) {
							throw new ApplicationException("Invalid State Name or Code for given country: " + stateCode);
						}

						deliveryAddress.setState(state);
						deliveryAddress.setStatus(Status.ACTIVE);

					}

					if (StringUtils.checkString(itemPojo.getCostCenter()).length() > 0) {
						costCenter = costCenterService.getActiveCostCenterForTenantByCostCenterName(itemPojo.getCostCenter(), buyer.getId());
						if (costCenter == null) {
							throw new ApplicationException(messageSource.getMessage("erpIntegration.cost.center.invalid", new Object[] { poPojo.getCostCenter() }, Global.LOCALE));
						}
					}

					if (StringUtils.checkString(itemPojo.getBusinessUnit()).length() > 0) {
						businessUnit = businessUnitService.findBusinessUnitForTenantByUnitCode(buyer.getId(), itemPojo.getBusinessUnit());
						if (businessUnit == null) {
							throw new ApplicationException(messageSource.getMessage("erpIntegration.unitCode.invalid", new Object[] { poPojo.getBusinessUnit() }, Global.LOCALE));
						}
					}

					item.setBusinessUnit(businessUnit);
					item.setBuyer(buyer);
					item.setCostCenter(costCenter);
					item.setDeliveryAddress(deliveryAddress);
					item.setDeliveryDate(itemPojo.getDeliveryDate());
					item.setDeliveryReceiver(itemPojo.getDeliveryReceiver());
					item.setFreeTextItemEntered(Boolean.FALSE);
					item.setItemDescription(itemPojo.getItemDescription());
					item.setItemName(itemPojo.getItemName());
					item.setItemTax(itemPojo.getItemTax());
					item.setLevel(1);
					item.setOrder((Integer.parseInt(itemPojo.getItemNo()) / 10));
					item.setParent(parent);
					item.setPo(po);
					item.setProduct(productItem);
					item.setProductCategory(productCategory);
					// item.setProductContractItem(productContractItem);
					item.setQuantity(itemPojo.getQuantity());
					item.setTaxAmount(itemPojo.getTaxAmount());
					item.setTotalAmount(itemPojo.getTotalAmount());
					item.setTotalAmountWithTax(itemPojo.getTotalAmountWithTax());
					item.setUnit(uom);
					item.setUnitPrice(itemPojo.getUnitPrice());
				}
			} else {
				throw new ApplicationException("Missing PO Items");
			}

			po.setPoItems(poItems);

			po = poService.savePo(po);
			poService.savePoPdf(po);
			map.put("errorcode", 0);
			map.put("errorMessage", "SUCCESS");

		} catch (Exception e) {
			LOG.error("Error Processing Create PO Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.createPo.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/createGrn", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createGrn(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody ErpGrnPojo erpGrnPojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			if (Boolean.FALSE == erpConfig.getEnableGrnCreateApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}

			GoodsReceiptNote note = goodsReceiptNoteService.createGrn(erpGrnPojo, buyer);
			map.put("success", "Goods Receipt Note " + note.getGrnId() + " created Successfully");
		} catch (ApplicationException e) {
			LOG.error("Error Processing Create GRN Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error Processing Create GRN Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.creategrn.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/createDo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createDo(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody ErpDoPojo erpDoPojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ErpSetup erpConfig = erpSetupDao.getErpConfigByAppId(authkey);
			if (erpConfig == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[] {}, Global.LOCALE));
			}

			Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
			if (buyer == null) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[] {}, Global.LOCALE));
			}

			if (Boolean.FALSE == erpConfig.getEnableDoCreateApi()) {
				throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[] {}, Global.LOCALE));
			}
			DeliveryOrder deliveryOrder = deliveryOrderService.createDoFromErp(erpDoPojo, buyer);
			map.put("success", "DO " + deliveryOrder.getDeliveryId() + " Created Successfully");
		} catch (ApplicationException e) {
			LOG.error("Error Processing Create DO Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error Processing Create DO Request : " + e.getMessage(), e);
			map.put("errorCode", 1001);
			map.put("errorMessage", messageSource.getMessage("erpIntegration.creategrn.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = "/sapPOtoRFX", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sapToPh(@RequestHeader(name = "X-AUTH-KEY", required = true) String authkey, @RequestBody Map<String, String> data)
			throws ApplicationException {

		ErpSetup erpConfig = erpSetupDao.getErpConfigWithTepmlateByAppId(authkey);
		if (erpConfig == null) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.empty", new Object[]{}, Global.LOCALE));
		}
		if (Boolean.FALSE == erpConfig.getEnableContractErpPush()) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.erpconfig.notallowed", new Object[]{}, Global.LOCALE));
		}
		Buyer buyer = buyerService.findBuyerById(erpConfig.getTenantId());
		if (buyer == null) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.buyer.empty", new Object[]{}, Global.LOCALE));
		}

		User user = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
		if (user == null) {
			throw new ApplicationException(messageSource.getMessage("erpIntegration.user.empty", new Object[]{}, Global.LOCALE));
		}

		Map<String, Object> response = new HashMap<>();


		if (data.containsKey("eventType") && data.containsKey("eventId") && data.containsKey("message") &&
				data.get("eventType").length() > 0 && data.get("eventId").length() > 0 && data.get("message").length() > 0) {
			if (data.get("eventType").equals("RFQ")) {
				RfqEventAudit audit = new RfqEventAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setDescription("ERP replied SUCCESS for "+data.get("message"));
				audit.setAction(AuditActionType.Transfer);
				RfqEvent event = rfqEventDao.findEventForSapByEventId(data.get("eventId"));
				if(event == null) {
					throw new ApplicationException("Event is not found");
				}
				audit.setEvent(event);
				eventAuditService.save(audit);
			} else if (data.get("eventType").equals("RFI")) {
				RfiEventAudit audit = new RfiEventAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setDescription("ERP replied SUCCESS for "+data.get("message"));
				audit.setAction(AuditActionType.Transfer);
				RfiEvent event = rfiEventDao.findEventForSapByEventId(data.get("eventId"));
				if(event == null) {
					throw new ApplicationException("Event is not found");
				}
				audit.setEvent(event);
				eventAuditService.save(audit);
			} else if (data.get("eventType").equals("RFT")) {
				RftEventAudit audit = new RftEventAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setDescription("ERP replied SUCCESS for "+data.get("message"));
				audit.setAction(AuditActionType.Transfer);
				RftEvent event = rftEventDao.findEventForSapByEventId(data.get("eventId"));
				if(event == null) {
					throw new ApplicationException("Event is not found");
				}
				audit.setEvent(event);
				eventAuditService.save(audit);
			} else if (data.get("eventType").equals("RFP")) {
				RfpEventAudit audit = new RfpEventAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setDescription("ERP replied SUCCESS for "+data.get("message"));
				audit.setAction(AuditActionType.Transfer);
				RfpEvent event = rfpEventDao.findEventForSapByEventId(data.get("eventId"));
				if(event == null) {
					throw new ApplicationException("Event is not found");
				}
				audit.setEvent(event);
				eventAuditService.save(audit);
			} else if (data.get("eventType").equals("RFA")) {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setActionBy(user);
				audit.setActionDate(new Date());
				audit.setDescription("ERP replied SUCCESS for "+data.get("message"));
				audit.setAction(AuditActionType.Transfer);
				RfaEvent event = rfaEventDao.findEventForSapByEventId(data.get("eventId"));
				if(event == null) {
					throw new ApplicationException("Event is not found");
				}
				audit.setEvent(event);
				eventAuditService.save(audit);
			}
		} else {
			response.put("type","E");
			response.put("message","All value is not provided");
		}

		// save in tat report
		if(data.containsKey("prNo")  && data.containsKey("eventId") && data.containsKey("eventType")) {
                TatReport tatReport = tatReportDao.getTatReportyEventIdAndTenantIdAndEventTypeAndPrNo(data.get("eventId"),
						erpConfig.getTenantId(), RfxTypes.valueOf(data.get("eventType")));

				if(tatReport == null) {
                   response.put("type","E");
				   response.put("message", "Purchase Requisition number "+data.get("prNo")+" doesn't exist in TatReport");
				}
				else {
					tatReport.setSapPrId(data.get("prNo"));
					if(tatReport.getSapPoId() == null || !tatReport.getSapPoId().contains(data.get("poNo"))) {
						if(tatReport.getSapPoId() == null || tatReport.getSapPoId().isEmpty()) {
							tatReport.setSapPoId(data.get("poNo"));
						}
						else {
							tatReport.setSapPoId(tatReport.getSapPoId() + " " + data.get("poNo"));
						}
					}
					tatReportDao.saveOrUpdate(tatReport);
					response.put("type","S");
					response.put("message", "Successfully request is accepted");
				}
		}
		else {
			response.put("type","E");
			response.put("message","All value is not provided");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static void main(String[] args) {
		// setSapPoNumbers(PrResponsePojo prResponse, Buyer buyer, TatReport tatReport)
		PrResponsePojo pojo = new PrResponsePojo();
		pojo.setSapRefNo("3301554069");
		pojo.setMessage("PO Created0100008571(RETROFIT POWER ENGINEERING (M) SDN.):1301196436");
		String message = "";
		TatReport tatReport = new TatReport();
		tatReport.setSapPoId(", 3301558425");
		tatReport.setAwardedSupplier("RETROFIT POWER ENGINEERING (M) SDN. BHD., SPARTAS (MALAYSIA) SDN BHD");
		System.out.println(">>> tatReport SAP PO " + tatReport.getSapPoId());
		if (StringUtils.checkString(message).length() > 11 && message.indexOf("(") != -1) {
			String vendorCode = message.substring(10, message.indexOf("("));
			String poId = pojo.getSapRefNo();
			// get the supplier based on vendorCode and buyerId
			String supplierName = "RETROFIT POWER ENGINEERING (M) SDN. BHD.";

			String awardedSupp = tatReport.getAwardedSupplier();
			String[] awardedSupps = awardedSupp.split(",");
			String awardedSuppliersPos = tatReport.getSapPoId();

			int index = -1;
			for (String sup : awardedSupps) {
				System.out.println(">>>>>>> SUP " + sup);
				if (!StringUtils.checkString(sup).equalsIgnoreCase(StringUtils.checkString(supplierName))) {
					System.out.println("Not eq Index " + index);
					index++;
				} else {
					break;
				}
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> Index " + index);
			if (StringUtils.checkString(awardedSuppliersPos).length() > 0) {
				String[] poIds = awardedSuppliersPos.split(",");
				for (int i = 0; i < poIds.length; i++) {
					poIds[i] = StringUtils.checkString(poIds[i]);
				}
				poIds[index + 1] = poId;
				tatReport.setSapPoId(String.join(", ", poIds));
			} else {
				String poIds[] = new String[awardedSupps.length];
				for (int i = 0; i < poIds.length; i++) {
					poIds[i] = "";
				}
				poIds[index + 1] = poId;
				tatReport.setSapPoId(String.join(", ", poIds));
			}

			System.out.println("----------------- " + tatReport.getSapPoId());
		}
	}
}
