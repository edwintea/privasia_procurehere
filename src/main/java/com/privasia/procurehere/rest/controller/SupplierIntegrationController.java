package com.privasia.procurehere.rest.controller;

import java.util.ArrayList;
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
import com.privasia.procurehere.core.pojo.SupplierActivationIntegrationPojo;
import com.privasia.procurehere.core.pojo.SupplierIntigrationPojo;
import com.privasia.procurehere.core.pojo.SupplierSuspendIntegrationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;

import io.swagger.annotations.Api;

/**
 * @author sarang
 */
@RestController
@RequestMapping("/integration/supplierApi")
@Api(value = "Supplier Integration", description = "E-daftar Integartion Api")
public class SupplierIntegrationController {
	private static final Logger LOG = LogManager.getLogger(Global.API_LOG);

	@Autowired
	BuyerSettingsService buyerSettingService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@RequestMapping(value = "/uploadSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierIntigrationPojo> supplier) {
		Map<String, Object> map = new HashMap<>();
		try {
			ObjectMapper mapperObj = new ObjectMapper();
			String payload = mapperObj.writeValueAsString(supplier);
			LOG.info("Payload :" + payload);
			LOG.info("Buyer Key :" + buyerKey);
			List<String> errorList = null;
			BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
			if (setting != null) {
				if (CollectionUtil.isNotEmpty(supplier)) {
					errorList = favoriteSupplierService.addSupplierTo(supplier, setting.getTenantId());
				} else {
					map.put("error", "Supplier Data Empty");
					LOG.error("Data is empty");
				}
				if (CollectionUtil.isNotEmpty(errorList)) {
					map.put("error", errorList);
				} else {
					map.put("success", "Supplier Data updated in EPROC successfully");
				}
			} else {
				LOG.error("Auth key not valid");
				map.put("error", "Auth key not valid");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Auth key not valid");
				return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error("Error while Supplier Data update in procurehere :" + e.getMessage(), e);
			map.put("error", "Error while getting Supplier Data in EPROC :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting Supplier Data in EPROC :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/suspendSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> suspendSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierSuspendIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Suspend : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.suspendSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing suspend supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing suspend supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing suspend supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/unSuspendSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> unSuspendSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierSuspendIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Un Suspend : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.unSuspendSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing un suspend supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing un suspend supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing un suspend supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/blockListSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> blockListSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierSuspendIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Blocklist : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.blockListSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing blocklist supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing blocklist supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing blocklist supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/releaseSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> releaseSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierSuspendIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Release : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.releaseSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing blocklist release supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing blocklist release supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing blocklist release supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/inActivateSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> inActivateSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierActivationIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Inactivate : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.inActivateSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing inactivate supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing inactivate supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing inactivate supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/activateSupplier", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> activateSupplier(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody List<SupplierActivationIntegrationPojo> suppliers) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("Activate : Buyer Key : " + buyerKey + " Supplier List " + (suppliers != null ? suppliers.size() : 0));
			if (CollectionUtil.isNotEmpty(suppliers)) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					errorList = favoriteSupplierService.activateSuppliers(suppliers, setting.getTenantId());
					if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
						map.put("error", errorList);
					} else {
						map.put("success", "Supplier request processed successfully");
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("error", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}

			} else {
				LOG.warn("Empty supplier list recived...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing activate supplier request :" + e.getMessage(), e);
			map.put("error", "Error while processing activate supplier request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing activate supplier request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
}
