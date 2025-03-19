package com.privasia.procurehere.rest.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.enums.AmountType;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.RfaEventApiPojo;
import com.privasia.procurehere.core.pojo.RfiEventApiPojo;
import com.privasia.procurehere.core.pojo.RfpEventApiPojo;
import com.privasia.procurehere.core.pojo.RfqEventApiPojo;
import com.privasia.procurehere.core.pojo.RftEventApiPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author sarang
 */
@RestController
@RequestMapping("/integration/buyerApi")
@Api(value = "BuyerIntigrationController", description = "Payment Intigartion Api")
public class BuyerApiController {
	private static final Logger LOG = LogManager.getLogger(Global.API_LOG);

	@Autowired
	BuyerSettingsService buyerSettingService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	// @formatter:off
	/**
	 * @api {post} /updatePayment Update Payment
	 * @apiName Buyer Api for Payment Update
	 * @apiGroup API
	 * @apiHeader {String} Content-Type Should be application/json
	 * @apiHeader {String} X-Authorization X-AUTH-KEY
	 * @apiHeaderExample {json} Header-Example: { "Content-Type":"application/json",
	 *                   "X-AUTH-KEY":"284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa35" }
	 * @apiParam {BigDecimal{16,4}} amount Amount Paid By Supplier
	 * @apiParam {String{150}} reference Payment Reference
	 * @apiParam {String{100}} supplierCode Supplier Code
	 * @apiParam {String{100}} eventRefranceNo Event Refrance No
	 * @apiParam {String{10}} amountType Amount Type(FEE, DEPOSIT)
	 * @apiParamExample {json} Request-Example: { "amount":"100", "reference":"ANY", "supplierCode":"123456",
	 *                  "eventRefranceNo":"RFA12345", "amountType":"FEE" }
	 * @apiExample {curl} Example usage: curl -X POST -H "X-AUTH-KEY:
	 *             284c6798089445cf444516eea037feff1ad25dbe27ef7800a22dd2888ba6aa35" -H "Cache-Control: no-cache" -H
	 *             "Content-Type: application/json" -d '{ "amount":"100", "reference":"ANY", "supplierCode":"123456",
	 *             "eventRefranceNo":"RFA12345", "amountType":"FEE" }'
	 *             "https://test.procurehere.com/buyerApi/updatePayment"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "success" : "Payment Data updated in EPROC successfully" }
	 * @apiSuccess success Success Message
	 * @apiError 401 Unauthorized access to protected resource.
	 * @apiError 500 Internal Server error
	 * @apiErrorExample HTTP/1.1 401 Unauthorized { "error" : "Auth key not valid" }
	 * @apiErrorExample HTTP/1.1 500 Internal server error { "error" : "Error while getting Payment Data in EPROC" }
	 */
	// @formatter:on
	@ApiOperation("Buyer Api for Payment Update")
	@RequestMapping(value = "/updatePayment", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updatePayment(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody FeePojo feePojo) {
		LOG.info("Update Payment Response Data Api called....");
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(feePojo);
			LOG.info("Payload :" + payload);
			LOG.info("Payload :" + buyerKey);

			BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
			if (setting != null) {
				if (feePojo != null) {
					LOG.info("PayLoad--->" + feePojo.toString());
					Event event = null;
					if (event == null) {
						event = rfaEventService.getEventByEventRefranceNo(feePojo.getEventId(), setting.getTenantId());
						if (event != null) {
							LOG.info("Event Type--->RFA");
							List<RfaEventSupplier> suppliers = rfaEventSupplierService.getAllSuppliersByEventIdAndSupplierCode(event.getId(), feePojo.getSupplierCode(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(suppliers)) {
								for (RfaEventSupplier eventSupplier : suppliers) {
									LOG.info("eventSupplier---->" + eventSupplier.getId());
									if (AmountType.FEE == feePojo.getAmountType()) {
										eventSupplier.setFeePaid(Boolean.TRUE);
										eventSupplier.setFeeReference(feePojo.getReference());
										eventSupplier.setFeePaidDate(new Date());
									} else {
										eventSupplier.setDepositPaid(Boolean.TRUE);
										eventSupplier.setDepositReference(feePojo.getReference());
										eventSupplier.setDepositPaidDate(new Date());
									}
									rfaEventSupplierService.updateRfaEventSuppliers(eventSupplier);
								}
							} else {
								map.put("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								HttpHeaders headers = new HttpHeaders();
								headers.add("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
							}

						}
					}
					if (event == null) {
						event = rfiEventService.getEventByEventRefranceNo(feePojo.getEventId(), setting.getTenantId());
						if (event != null) {
							LOG.info("Event Type--->RFI");
							List<RfiEventSupplier> suppliers = rfiEventSupplierService.getAllSuppliersByEventIdAndSupplierCode(event.getId(), feePojo.getSupplierCode(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(suppliers)) {
								for (RfiEventSupplier eventSupplier : suppliers) {
									LOG.info("eventSupplier---->" + eventSupplier.getId());
									if (AmountType.FEE == feePojo.getAmountType()) {
										eventSupplier.setFeePaid(Boolean.TRUE);
										eventSupplier.setFeeReference(feePojo.getReference());
										eventSupplier.setFeePaidDate(new Date());
									} else {
										eventSupplier.setDepositPaid(Boolean.TRUE);
										eventSupplier.setDepositReference(feePojo.getReference());
										eventSupplier.setDepositPaidDate(new Date());
									}
									rfiEventSupplierService.updateEventSuppliers(eventSupplier);
								}
							} else {
								map.put("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								HttpHeaders headers = new HttpHeaders();
								headers.add("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
							}
						}
					}
					if (event == null) {
						event = rfpEventService.getEventByEventRefranceNo(feePojo.getEventId(), setting.getTenantId());
						if (event != null) {
							LOG.info("Event Type--->RFP");
							List<RfpEventSupplier> suppliers = rfpEventSupplierService.getAllSuppliersByEventIdAndSupplierCode(event.getId(), feePojo.getSupplierCode(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(suppliers)) {
								for (RfpEventSupplier eventSupplier : suppliers) {
									LOG.info("eventSupplier---->" + eventSupplier.getId());
									if (AmountType.FEE == feePojo.getAmountType()) {
										eventSupplier.setFeePaid(Boolean.TRUE);
										eventSupplier.setFeeReference(feePojo.getReference());
										eventSupplier.setFeePaidDate(new Date());
									} else {
										eventSupplier.setDepositPaid(Boolean.TRUE);
										eventSupplier.setDepositReference(feePojo.getReference());
										eventSupplier.setDepositPaidDate(new Date());
									}
									rfpEventSupplierService.updateEventSuppliers(eventSupplier);
								}
							} else {
								map.put("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								HttpHeaders headers = new HttpHeaders();
								headers.add("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
							}
						}
					}
					if (event == null) {
						event = rfqEventService.getEventByEventRefranceNo(feePojo.getEventId(), setting.getTenantId());
						if (event != null) {
							LOG.info("Event Type--->RFQ");
							List<RfqEventSupplier> suppliers = rfqEventSupplierService.getAllSuppliersByEventIdAndSupplierCode(event.getId(), feePojo.getSupplierCode(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(suppliers)) {
								for (RfqEventSupplier eventSupplier : suppliers) {
									LOG.info("eventSupplier---->" + eventSupplier.getId());
									if (AmountType.FEE == feePojo.getAmountType()) {
										eventSupplier.setFeePaid(Boolean.TRUE);
										eventSupplier.setFeeReference(feePojo.getReference());
										eventSupplier.setFeePaidDate(new Date());
									} else {
										eventSupplier.setDepositPaid(Boolean.TRUE);
										eventSupplier.setDepositReference(feePojo.getReference());
										eventSupplier.setDepositPaidDate(new Date());
									}
									rfqEventSupplierService.updateEventSuppliers(eventSupplier);
								}
							} else {
								map.put("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								HttpHeaders headers = new HttpHeaders();
								headers.add("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
							}
						}
					}
					if (event == null) {
						event = rftEventService.getEventByEventRefranceNo(feePojo.getEventId(), setting.getTenantId());
						if (event != null) {
							LOG.info("Event Type--->RFT");
							List<RftEventSupplier> suppliers = rftEventSupplierService.getAllSuppliersByEventIdAndSupplierCode(event.getId(), feePojo.getSupplierCode(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(suppliers)) {
								for (RftEventSupplier eventSupplier : suppliers) {
									LOG.info("eventSupplier---->" + eventSupplier.getId());
									if (AmountType.FEE == feePojo.getAmountType()) {
										eventSupplier.setFeePaid(Boolean.TRUE);
										eventSupplier.setFeeReference(feePojo.getReference());
										eventSupplier.setFeePaidDate(new Date());
									} else {
										eventSupplier.setDepositPaid(Boolean.TRUE);
										eventSupplier.setDepositReference(feePojo.getReference());
										eventSupplier.setDepositPaidDate(new Date());
									}
									rftEventSupplierService.updateEventSuppliers(eventSupplier);
								}
							} else {
								map.put("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								HttpHeaders headers = new HttpHeaders();
								headers.add("error", "No Any Supplier Found for refrance No:" + feePojo.getEventId() + " and supplier code:" + feePojo.getSupplierCode());
								return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
							}
						}
					}
					if (event == null) {
						LOG.error("Event empty");
						map.put("error", "No Any Active Event Found for refrance No:" + feePojo.getEventId());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "No Any Active Event Found for refrance No:" + feePojo.getEventId());
						return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.NOT_FOUND);
					}
					map.put("success", "Payment Data updated in EPROC successfully");
				} else {
					LOG.error("Payload empty");
					map.put("error", "PayLoad will not be empty");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "PayLoad will not be empty");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.error("Auth key not valid");
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while Payment Data update in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting Payment Data in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting Payment Data in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info("All Ok");
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/buyerEventData", method = RequestMethod.POST, produces = "application/json") public
	 * ResponseEntity<Map<String, Object>> buyerEventData(@RequestHeader(name = "X-AUTH-KEY", required = true) String
	 * buyerKey) { try { String tenantId = buyerSettingService.getTenantIdBybuyerKey(buyerKey); List<RfiEvent>
	 * allRfiEvent = rfiEventService.getAllRfiEventByTenantId(tenantId); List<RftEvent> allRftEvent =
	 * rftEventService.getAllRftEventByTenantId(tenantId); List<RfpEvent> allRfpEvent =
	 * rfpEventService.getAllRfpEventByTenantId(tenantId); List<RfqEvent> allRfqEvent =
	 * rfqEventService.getAllRfqEventByTenantId(tenantId); List<RfaEvent> allRfaEvent =
	 * rfaEventService.getAllRfaEventByTenantId(tenantId); } catch (Exception e) { } return null; }
	 */

	public List<?> convertToResponsePojo(List<Event> event) {
		Event eventInstance = event.get(0);
		if (eventInstance instanceof RfiEvent) {
			List<RfiEventApiPojo> rfiEventApiPojoList = new ArrayList<RfiEventApiPojo>();
			for (Event event2 : event) {
				RfiEvent rfiEvent = (RfiEvent) event2;
				RfiEventApiPojo pojo = convertToRfiEventApiPojo(rfiEvent);
				rfiEventApiPojoList.add(pojo);

			}
			return rfiEventApiPojoList;
		}
		if (eventInstance instanceof RftEvent) {
			List<RftEventApiPojo> rftEventApiPojo = new ArrayList<RftEventApiPojo>();
			for (Event event2 : event) {
				RftEvent rftEvent = (RftEvent) event2;
				RftEventApiPojo pojo = convertToRftEventApiPojo(rftEvent);
				rftEventApiPojo.add(pojo);
			}
			return rftEventApiPojo;
		}

		if (eventInstance instanceof RfqEvent) {
			List<RfqEventApiPojo> rfqEventApiPojoList = new ArrayList<RfqEventApiPojo>();
			for (Event event2 : event) {
				RfqEvent rfqEvent = (RfqEvent) event2;
				RfqEventApiPojo pojo = convertToRfqEventApiPojo(rfqEvent);
				rfqEventApiPojoList.add(pojo);
			}
			return rfqEventApiPojoList;
		}

		if (eventInstance instanceof RfpEvent) {
			List<RfpEventApiPojo> rfpEventApiPojoList = new ArrayList<RfpEventApiPojo>();
			for (Event event2 : event) {
				RfpEvent rfpEvent = (RfpEvent) event2;
				RfpEventApiPojo pojo = convertToRfpEventApiPojo(rfpEvent);
				rfpEventApiPojoList.add(pojo);
			}
			return rfpEventApiPojoList;

		}
		if (eventInstance instanceof RfaEvent) {
			List<RfaEventApiPojo> rfaEventApiPojoList = new ArrayList<RfaEventApiPojo>();
			for (Event event2 : event) {
				RfaEvent rfaEvent = (RfaEvent) event2;
				RfaEventApiPojo pojo = convertToRfaEventApiPojo(rfaEvent);
				rfaEventApiPojoList.add(pojo);
			}
			return rfaEventApiPojoList;
		}

		return null;
	}

	private RfiEventApiPojo convertToRfiEventApiPojo(RfiEvent event) {
		RfiEventApiPojo pojo = new RfiEventApiPojo();
		return pojo;
	}

	private RftEventApiPojo convertToRftEventApiPojo(RftEvent event) {
		RftEventApiPojo pojo = new RftEventApiPojo();
		return pojo;
	}

	private RfpEventApiPojo convertToRfpEventApiPojo(RfpEvent event) {
		RfpEventApiPojo pojo = new RfpEventApiPojo();
		return pojo;
	}

	private RfqEventApiPojo convertToRfqEventApiPojo(RfqEvent event) {
		RfqEventApiPojo pojo = new RfqEventApiPojo();
		return pojo;
	}

	private RfaEventApiPojo convertToRfaEventApiPojo(RfaEvent event) {
		RfaEventApiPojo pojo = new RfaEventApiPojo();
		return pojo;
	}
}
