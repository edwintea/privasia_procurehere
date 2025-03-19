package com.privasia.procurehere.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.UserRevocationIntegrationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.UserService;

import io.swagger.annotations.Api;

/**
 * @author Nana
 */
@RestController
@RequestMapping("/integration/userRevocationApi")
@Api(value = "User Revocation Integration", description = "Westports User Revocation")
public class UserRevocationIntegrationController {
	private static final Logger LOG = LogManager.getLogger(Global.API_LOG);

	@Autowired
	BuyerSettingsService buyerSettingService;

	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateUser(@RequestHeader(name = "X-AUTH-KEY", required = true) String buyerKey, @RequestBody UserRevocationIntegrationPojo request) {
		Map<String, Object> map = new HashMap<>();
		try {
			LOG.info("WP Update User : Buyer Key : " + buyerKey + " Email Id : " + (request.getEmailId() != null ? request.getEmailId() : 0));
			if (request != null && StringUtils.isNotBlank(request.getEmailId())) {
				BuyerSettings setting = buyerSettingService.getTenantIdBybuyerKey(buyerKey);
				List<String> errorList = new ArrayList<String>();
				if (setting != null) {
					LOG.info("WP Update setting.getTenantId() : " + setting.getTenantId());
					if (StringUtils.isNotBlank(setting.getTenantId())) {
						List<User> users = userService.fetchUserByCommunicationEmail(request.getEmailId(), setting.getTenantId());
						LOG.info("WP Update users to be revoked : " + request.getEmailId() + ": " + users.size());
						if (CollectionUtil.isNotEmpty(users)) {
							errorList = userService.userUpdate(users, request.getEmailId(), setting.getTenantId());
							if (CollectionUtil.isNotEmpty(errorList) && errorList.size() > 0) {
								map.put("errorCode", 1);
								map.put("errorMessage", errorList.get(0));
							} else {
								map.put("errorcode", 0);
								map.put("errorMessage", "Successfully revoked " + users.size() + (users.size() > 1 ? " users" : " user"));
							}
						}
						else {
							LOG.warn("User Email Id : " + request.getEmailId() + " is not exist");
							map.put("errorCode", 1);
							map.put("errorMessage", "Record Not Found");
						}
					}
				} else {
					LOG.error("Auth key not valid");
					map.put("errorMessage", "Auth key not valid");
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Auth key not valid");
					return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.UNAUTHORIZED);
				}
			} else {
				LOG.warn("Empty Email Id Received...");
			}
		} catch (Exception e) {
			LOG.error("Error while processing updateUser request :" + e.getMessage(), e);
			map.put("error", "Error while processing updateUser request :" + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while processing updateUser request :" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
}
