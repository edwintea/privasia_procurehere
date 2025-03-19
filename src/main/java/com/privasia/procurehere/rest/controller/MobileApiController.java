package com.privasia.procurehere.rest.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAuthorizedException;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.MobileBuyerMetrics;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.MobilePrPojo;
import com.privasia.procurehere.core.pojo.MobileRequestPojo;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.security.JwtAuthenticationToken;

import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @api {post} /auth/login Login
 * @apiName doLogin
 * @apiGroup User
 * @apiHeader {String} Content-Type should be application/json
 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
 * @apiHeaderExample {json} Header-Example:
 *                   {
 *                   "Content-Type" : "application/json",
 *                   "X-Requested-With" : "XMLHttpRequest"
 *                   }
 * @apiParam {String} username User Login Email.
 * @apiParam {String} password User account password.
 * @apiParam {String} deviceId User Mobile Device Id.
 * @apiParamExample {json} Request-Example:
 *                  {
 *                  "username" : "buyer@procurehere.com",
 *                  "password" : "Password@1",
 *                  "deviceId": "MOBILE-DEVICE-ID"
 *                  }
 * @apiSuccess {String} token Jwt Token.
 * @apiSuccess {String} refreshToken Refresh Jwt of the User.
 * @apiExample {curl} Example usage:
 *             curl -X POST -H "X-Requested-With: XMLHttpRequest" -H "Content-Type: application/json" -H "Cache-Control:
 *             no-cache" -d '{ "username": "admin@procurehere.com", "password": "admin123", "deviceId": "MOBILE-DEVICE-ID" }'
 *             "http://procurehere.dhriti-solution.com/procurehere/api/auth/login"
 * @apiSuccessExample Success-Response:
 *                    HTTP/1.1 200 OK
 *                    {
 *                    "token" :
 *                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w",
 *                    "refreshToken" :
 *                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9SRUZSRVNIX1RPS0VOIl0sInRlbmFudElkIjoiMmM5Zjk2Nzk1OGUwZTk3ZTAxNThlMGViMmEyNDFiODEiLCJ0ZW5hbnRUeXBlIjoiQlVZRVIiLCJpZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhNmMxYjgzIiwiaXNzIjoicHJvY3VyZWhlcmUuY29tIiwianRpIjoiN2Y0NGQ0NDctOWMyYi00NGJlLWI4ZTItMzIxM2E1ODBhZmVhIiwiaWF0IjoxNDk3MzQzODk4LCJleHAiOjE0OTczNDc0OTh9.nsqMBPZbiqgUUDqidDKkkNOCZdA2LIKSCv6UGMujQ0p2Ti5gdhJxzcD6aeMefFgJPzZso9h-Pe811yERp2M3-A"
 *                    }
 * @apiError InvalidUser The id or password of the User mismatch.
 * @apiErrorExample Error-Response:
 *                  HTTP/1.1 403 Forbidden
 *                  {
 *                  "status" : 401,
 *                  "message" : "Invalid username or password",
 *                  "errorCode" : 10,
 *                  "timestamp" : 1497343691661
 *                  }
 */

/**
 * @api {get} /auth/token Refresh Token
 * @apiName getToken
 * @apiGroup User
 * @apiHeader {String} Content-Type should be application/json
 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
 * @apiHeader {String} X-Authorization Bearer [refreshToken]
 * @apiHeaderExample {json} Header-Example:
 * 
 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
 *                   </pre>
 * 
 * @apiExample {curl} Example usage: curl -X GET -H "X-Requested-With: XMLHttpRequest" -H "Content-Type:
 *             application/json" -H "Cache-Control: no-cache"
 *             "http://procurehere.dhriti-solution.com/procurehere/api/auth/token"
 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "token":
 *                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6ImZmODA4MTgxNThkOGVmMTgwMTU4ZDhmMDM0ZjkwNGZlIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiJmZjgwODE4MTU4ZDhlZjE4MDE1OGQ4ZjAzNTQzMDUwMCIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5ODczMjU5MiwiZXhwIjoxNDk4NzMzNDkyfQ.DaZXFlGv8l-WEBOQ5r3U7HBjSBIKAdWRtS-NquZKFo5ddIifLP8wO0YOJwC0PwarBGqZ7YWj1sflDEBLDhF5lA"
 *                    }
 * @apiUse TOKEN_UNAUTHORIZED
 * @apiUse TOKEN_BAD_REQUEST
 * @apiUse TOKEN_NOT_ACCEPTABLE
 */
@RestController
public class MobileApiController {

	private static final Logger LOG = LogManager.getLogger(MobileApiController.class);

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	PrService prService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	UserService userService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	BusinessUnitService businessUnitService;

	/**
	 * @api {get} /me Request User information
	 * @apiName getUserProfile
	 * @apiGroup User
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
		      {  
				"Content-Type":"application/json",
				"X-Requested-With":"XMLHttpRequest",
				"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
				}
	 *                   </pre>
	 * 
	 * @apiSuccess {String} id Internal Id of the User.
	 * @apiSuccess {String} username User name.
	 * @apiSuccess {String} loginId Login email of the User.
	 * @apiSuccess {String} tenantId User tenant id.
	 * @apiSuccess {String} tenantType User tenant type. BUYER, SUPPLIER or OWNER.
	 * @apiSuccess {Boolean} locked User account lock status. true = locked, false = unlocked
	 * @apiSuccess {Boolean} deleted User account delete status. true = deleted, false = existing
	 * @apiSuccess {Boolean} active User account active status. true = active, false = inactive
	 * @apiSuccess {Boolean} checkControl [ignore this param]
	 * @apiSuccess {String[]} grantedAuthorities List containing granted access rights to this user
	 * @apiSuccess {Boolean} passwordExpired Password expiry status. true = expired. false = active
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization: Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" "http://procurehere.dhriti-solution.com/procurehere/api/me"
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK { "id" : "2c9f967958e0e97e0158e0eb2a6c1b83", "loginId" :
	 *                    "BUYER@PROCUREHERE.COM", "tenantId" : "2c9f967958e0e97e0158e0eb2a241b81", "tenantType" :
	 *                    "BUYER", "locked" : false, "deleted" : false, "active" : true, "checkControl" : true,
	 *                    "grantedAuthorities" : [ "ROLE_BUYER" ], "passwordExpired" : true, "isAdmin" : false,
	 *                    "accountNonExpired" : true, "accountNonLocked" : true, "credentialsNonExpired" : true,
	 *                    "authorities" : [ { "authority" : "ROLE_BUYER" } ], "enabled" : true, "username" :
	 *                    "BUYER@PROCUREHERE.COM" }
	 * @apiUse UNAUTHORIZED
	 */
	@RequestMapping(value = "/api/me", method = RequestMethod.GET)
	public @ResponseBody AuthenticatedUser getUserProfile(JwtAuthenticationToken token) {
		AuthenticatedUser user = (AuthenticatedUser) token.getPrincipal();
		LOG.info("USER Buyer :" + user.getTenantId());
		return user;
	}

	/**
	 * @api {post} /myTasks To Do List with Search
	 * @apiName getMyTasks
	 * @apiGroup DashBoard
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {String} searchValue Search for PR/Event Reference Number or PR/Event Name or PR/Event Owner Name
	 *           (Optional field).
	 * @apiParam {Integer} start Start value used for pagination (Optional field).
	 * @apiParam {Integer} length Length of records list from start value used for pagination (Optional field).
	 * @apiParamExample {json} Request-Example: { "searchValue" : "purchase Laptops", "start" : 0, "length" : 10 }
	 * @apiSuccess {Object[]} myToDoList User Pending Event/PR for Approval and Evaluation.
	 * @apiSuccess {String} myToDoList.id Internal Id of the PR/Event (Unique Id).
	 * @apiSuccess {String} myToDoList.eventId Id of the PR/Event (System generated Id).
	 * @apiSuccess {String} myToDoList.unitName Business Unit Name of PR/Event (Optional Field).
	 * @apiSuccess {String} myToDoList.eventName PR/Event Name.
	 * @apiSuccess {String} myToDoList.referenceNumber PR/Event Reference Number (Optional Field for PR).
	 * @apiSuccess {String} myToDoList.creatorName PR/Event Creator Name.
	 * @apiSuccess {Date} myToDoList.eDate Event start/end Date.
	 * @apiSuccess {String} myToDoList.status PR/Event Status.
	 * @apiSuccess {String} myToDoList.eventType PR/Event Event/PR Type.
	 * @apiSuccess {Boolean} myToDoList.urgentEvent Urgent PR/Event (Optional field).
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"searchValue" : "purchase Laptops", "start" : 0, "length" : 10}'
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/myTasks"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   {  
	"myToDoList":[  
	 {  
	    "id":"ff8081815c667b07015c683dc7ec0000",
	    "eventName":"Purchase Laptops",
	    "referenceNumber":"PL-RFA-001",
	    "eventId":"RFA/0602/76",
	    "creatorName":"Buyer Person A",
	    "eDate":"03/06/2017 09:00 AM",
	    "status":"CLOSED",
	    "eventType":"RFA",
	    "urgentEvent":true
	 },
	 {  
	    "id":"ff8081815aab9743015aacee862a00a6",
	    "eventName":"Purchase Laptops",
	    "referenceNumber":"ppppppppppp pppdcsaddddddddddddddd dsauyguyg fweferfewdw uhiu",
	    "eventId":"PR/032017/30",
	    "unitName":"Test",
	    "creatorName":"Buyer Person A",
	    "eDate":"08/03/2017 10:45 AM",
	    "status":"PENDING",
	    "eventType":"PR",
	    "urgentEvent":false
	 }
	]
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/myTasks", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Map<String, Object> getMyTasks(JwtAuthenticationToken token, @RequestBody(required = false) SearchSortFilterPojo search) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> myTasks = new HashMap<String, Object>();

		try {
			AuthenticatedUser user = checkAuthorizedUser(token);
			LOG.info("TenantType " + user.getTenantType());
			LOG.info("TenantId " + user.getTenantId());
			LOG.info("UserId " + user.getId());
			if (TenantType.SUPPLIER == user.getTenantType()) {
				List<PendingEventPojo> myToDoList = rftEventService.findMyToDoListForSupplier(user.getTenantId(), user.getId(), search);
				myTasks.put("myToDoList", myToDoList);
			} else if (TenantType.BUYER == user.getTenantType()) {
				List<PendingEventPojo> myToDoList = rftEventService.findMyToDoList(user.getTenantId(), user.getId(), search);
				myTasks.put("myToDoList", myToDoList);
			}
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return myTasks;
	}

	/**
	 * @api {get} /detail/{eventType}/{id} PR Details
	 * @apiName getPrDetails
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	 *     {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiSuccess {Object} details PR Details object.
	 * @apiSuccess {String} details.id Internal Id of the PR (Unique Id).
	 * @apiSuccess {String} details.prId PR Id (System generated Id).
	 * @apiSuccess {String} details.name PR Name.
	 * @apiSuccess {String} details.referenceNumber PR Reference Number.
	 * @apiSuccess {String} details.requester PR requester.
	 * @apiSuccess {String} details.description PR Description.
	 * @apiSuccess {String} details.currency PR Currency.
	 * @apiSuccess {String} details.currencyName PR Currency Name.
	 * @apiSuccess {String} details.creatorName PR Creator Name.
	 * @apiSuccess {String} details.decimal PR Decimal.
	 * @apiSuccess {String} details.paymentTerm PR Payment and Terms.
	 * @apiSuccess {String} details.supplierName PR Supplier Name.
	 * @apiSuccess {String} details.remarks PR Remarks.
	 * @apiSuccess {String} details.termsAndConditions PR Terms And Conditions.
	 * @apiSuccess {String} details.total PR Total Amount.
	 * @apiSuccess {String} details.additionalTax PR Additional tax on Total Amount.
	 * @apiSuccess {String} details.taxDescription PR Additional tax description.
	 * @apiSuccess {String} details.grandTotal PR Grand total(Total Amount with Additional Tax).
	 * @apiSuccess {Object[]} details.prItems Purchase Items.
	 * @apiSuccess {String} details.prItems.id Internal Id of the Purchase Items(Unique Id).
	 * @apiSuccess {Integer} details.prItems.level Item level number(Example 1.0, Here 1 is level).
	 * @apiSuccess {Integer} details.prItems.order Item order number(Example 1.0, Here 0 is order).
	 * @apiSuccess {String} details.prItems.itemName Purchase Item Name.
	 * @apiSuccess {String} details.prItems.itemDescription Purchase Item Description.
	 * @apiSuccess {String} details.prItems.quantity Purchase Item Quantity.
	 * @apiSuccess {String} details.prItems.uom Purchase Item UOM(Unit of Measurement).
	 * @apiSuccess {String} details.prItems.totalAmountWithTax Purchase Item Total Amount with Tax.
	 * @apiSuccess {Object[]} details.documents PR Documents.
	 * @apiSuccess {String} details.documents.id Internal Id of the Document (Unique Id).
	 * @apiSuccess {String} details.documents.fileName Document File Name.
	 * @apiSuccess {String} details.documents.fileSize Document File Size in Kb (Kilo Byte).
	 * @apiSuccess {Object[]} details.approvers PR Approvers.
	 * @apiSuccess {String} details.approvers.id Internal Id of the PR Approvers (Unique Id).
	 * @apiSuccess {Integer} details.approvers.level PR Approval level.
	 * @apiSuccess {String} details.approvers.approvalType PR Approval Type Condition(OR/AND).
	 * @apiSuccess {Boolean} details.approvers.done PR Approval level has completed or Not.
	 * @apiSuccess {Boolean} details.approvers.active PR Approval level is Active or Not.
	 * @apiSuccess {Object[]} details.approvers.users PR Approver Users.
	 * @apiSuccess {String} details.approvers.users.approvalStatus PR Approval User Status.
	 * @apiSuccess {String} details.approvers.users.userName PR Approval User Name.
	 * @apiSuccess {String} details.approvers.users.userName PR Approval User Id (Based on that show Comment box and
	 *             Approve/Reject button).
	 * @apiSuccess {String} details.approvers.users.actionDate PR Approval User Action Date.
	 * @apiSuccess {String} details.approvers.users.remarks PR Approval User Remarks.
	 * @apiSuccess {Object[]} details.comments PR Approval Comments.
	 * @apiSuccess {String} details.comments.comment PR Approval User Comment.
	 * @apiSuccess {Date} details.comments.createdDate PR Approval User Comment date.
	 * @apiSuccess {String} details.comments.userName PR Approval Comment User Name.
	 * @apiSuccess {Boolean} details.comments.transientIsApproved PR Approval Comment User Approved/Reject.
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization: Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache"
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/me/PR/ff8081815aab9743015aacee862a00a6"
	 * @apiSuccessExample (PR)Success-Response:
	 * 
	 *                    <pre>
	 *        HTTP/1.1 200 OK
	                     {  
	"details":{  
	  "id":"ff8081815aab9743015aacee862a00a6",
	  "prId":"PR/032017/30",
	  "name":"PR NAME",
	  "referenceNumber":"PR Reference Number",
	  "requester":"PR requester",
	  "description":"PR Description PR Description PR Description PR Description PR Description PR Description PR De",
	  "currency":"MYR",
	  "currencyName":"MALAYSIA",
	  "creatorName": "Buyer Person A",
	  "decimal":"2",
	  "paymentTerm":"Five Months EMI",
	  "supplierName":"Supplier Person A",
	  "prItems":[  
	     {  
	        "id":"ff8081815aab9743015aacf94b6900c5",
	        "itemName":"Laptop Bags",
	        "level":1,
	        "order":0,
	        "quantity":1,
	        "itemDescription":"Laptop Bags DescriptionLaptop Bags Description",
	        "totalAmountWithTax":0,
	        "uom":"PCs"
	     },
	     {  
	        "id":"ff8081815aab9743015aacf98a0200c6",
	        "itemName":"cwcda",
	        "level":1,
	        "order":1,
	        "quantity":4,
	        "unitPrice":1,
	        "itemDescription":"Namccccccccccsssssssssssscccccccccccccsssssssssssss",
	        "totalAmountWithTax":4.4,
	        "uom":"MM"
	     },
	     {  
	        "id":"ff8081815aab9743015aacf9dd5400c7",
	        "itemName":"cwcda",
	        "level":1,
	        "order":2,
	        "quantity":5,
	        "unitPrice":1,
	        "itemDescription":"Nasssss",
	        "totalAmountWithTax":5.5,
	        "uom":"CM"
	     }
	  ],
	  "remarks":"Please contact Rofina 00000 to schedule for delivery",
	  "termsAndConditions":"1. Terms & Conditions Terms & Conditions Terms & Conditions\r\n2.Terms 
	                 & Conditions Terms 
	                 & Conditions Terms & Conditions\r\n3. Terms & Conditions Terms & Conditions\r\n",
	  "total":9.9,
	  "taxDescription":"GST",
	  "additionalTax":100000000,
	  "grandTotal":100000009.9,
	  "documents":[  
	     {  
	        "id":"ff8081815aab9743015aacf376e200bd",
	        "fileName":"VipulDeduction.xlsx",
	        "fileSize":8
	     },
	     {  
	        "id":"ff8081815aab9743015aacf3987d00be",
	        "fileName":"VipulDeduction.xlsx",
	        "fileSize":8
	     },
	     {  
	        "id":"ff8081815aab9743015aacf31baa00bc",
	        "fileName":"VipulDeduction.xlsx",
	        "fileSize":8
	     }
	  ],
	  "approvers":[  
	     {  
	        "id":"ff8081815cf7915c015cf8c199f1003f",
	        "level":1,
	        "approvalType":"OR",
	        "done":true,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Nitin",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           },
	           {  
	              "approvalStatus":"APPROVED",
	              "userName":"Buyer Person A",
	              "userId":"ff8081815cf7915c015cf8c199f1003f",
	              "actionDate": "10/07/2017 12:10 PM",
	              "remarks": "all is well...!!!!!!!......"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815cf7915c015cf8c199f20042",
	        "level":2,
	        "approvalType":"OR",
	        "done":false,
	        "active":true,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Nitin",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Nitin Dhriti",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815cf7915c015cf8c199f20045",
	        "level":3,
	        "approvalType":"OR",
	        "done":false,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Nitin",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815cf7915c015cf8c199f30047",
	        "level":4,
	        "approvalType":"OR",
	        "done":false,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"B1@B1.COM",
	              "userId":"ff8081815cf7915c015cf8c199f1003k"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Buyer Person A",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"LILI BERHAD",
	              "userId":"ff8081815cf7915c015cf8c199f1003d"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Nitin",
	              "userId":"ff8081815cf7915c015cf8c199f1003l"
	           }
	        ]
	     }
	  ],
	  "comments":[  
	     {  
	        "comment":"all is well...!!!!!!!......",
	        "createdDate":"10/07/2017",
	        "userName":"Buyer Person A",
	        "transientIsApproved":true
	     }
	  ]
	}
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */

	/**
	 * @api {get} /detail/{eventType}/{id} Event Details
	 * @apiName getEventDetails
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	 *     {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiSuccess {Object} details Event Details object.
	 * @apiSuccess {String} details.id Internal Id of the Event (Unique Id).
	 * @apiSuccess {String} details.eventId Event Id (System generated Id).
	 * @apiSuccess {String} details.eventName Event Name.
	 * @apiSuccess {String} details.referenceNumber Event Reference Number.
	 * @apiSuccess {String} details.eventDescription Event Description.
	 * @apiSuccess {String} details.currency Event Currency.
	 * @apiSuccess {String} details.currencyName Event Currency Name.
	 * @apiSuccess {String} details.decimal Event Decimal.
	 * @apiSuccess {String} details.eventOwner Event Owner Name.
	 * @apiSuccess {String} details.status Event Status.
	 * @apiSuccess {Object[]} details.suppliers Event Suppliers.
	 * @apiSuccess {String} details.suppliers.supplierCompanyName Event Supplier Name.
	 * @apiSuccess {Date} details.suppliers.previewTime Event Supplier Preview Time (For Progress Report).
	 * @apiSuccess {Date} details.suppliers.supplierEventReadTime Event Supplier Accepted Time (For Progress Report).
	 * @apiSuccess {Date} details.suppliers.supplierSubmittedTime Event Supplier Submitted Time (For Progress Report).
	 * @apiSuccess {Boolean} details.suppliers.revisedBidSubmitted Event Supplier Revised Bid Submitted (For Auction
	 *             Progress Report ).
	 * @apiSuccess {Date} details.suppliers.confirmPriceDateAndTime Rfa Event Supplier Confirm Price Date Time (For
	 *             Auction Progress Report ).
	 * @apiSuccess {Boolean} details.suppliers.confirmPriceSubmitted Rfa Event Supplier Confirm Price (For Auction
	 *             Progress Report ).
	 * @apiSuccess {Object[]} details.bqs Event Bill of Quantities.
	 * @apiSuccess {String} details.bqs.id Internal Id of the Event Bill of Quantities(Unique Id).
	 * @apiSuccess {String} details.bqs.name Event Bill of Quantities Name.
	 * @apiSuccess {String} details.bqs.description Event Bill of Quantities Description.
	 * @apiSuccess {Object[]} details.bqs.bqItem Event Bill of Quantities Items.
	 * @apiSuccess {String} details.bqs.bqItem.id Internal Id of the Event Bill of Quantities Item(Unique Id).
	 * @apiSuccess {String} details.bqs.bqItem.itemName Event Bill of Quantities Item Name.
	 * @apiSuccess {Integer} details.bqs.bqItem.level Event Bill of Quantities Item level number(Example 1.0, Here 1 is
	 *             level).
	 * @apiSuccess {Integer} details.bqs.bqItem.order Event Bill of Quantities Item order number(Example 1.0, Here 0 is
	 *             level).
	 * @apiSuccess {String} details.bqs.bqItem.itemDescription Event Bill of Quantities Item Description.
	 * @apiSuccess {String} details.bqs.bqItem.quantity Event Bill of Quantities Item Quantity.
	 * @apiSuccess {Object} details.bqs.bqItem.uom Event Bill of Quantities Item UOM(Unit of Measurement).
	 * @apiSuccess {String} details.bqs.bqItem.uom.uom Event Bill of Quantities Item UOM Name(Unit of Measurement).
	 * @apiSuccess {Object[]} details.timeLine Event Time lines.
	 * @apiSuccess {Date} details.timeLine.activityDate Event Time lines Activity Date.
	 * @apiSuccess {String} details.timeLine.activity Event Time lines Activity.
	 * @apiSuccess {String} details.timeLine.description Event Time lines Description.
	 * @apiSuccess {Object[]} details.documents Event Documents.
	 * @apiSuccess {String} details.documents.id Internal Id of the Event Document (Unique Id).
	 * @apiSuccess {String} details.documents.fileName Event Document File Name.
	 * @apiSuccess {String} details.documents.fileSize Event Document File Size in Kb (Kilo Byte).
	 * @apiSuccess {Object[]} details.envelops Event Envelopes.
	 * @apiSuccess {String} details.envelops.id Internal Id of the Event Envelopes (Unique Id).
	 * @apiSuccess {String} details.envelops.envelopTitle Event Envelope Title.
	 * @apiSuccess {String} details.envelops.envelopType Event Envelope Type(CLOSED/OPEN).
	 * @apiSuccess {Boolean} details.envelops.isOpen Event Envelope has been Opened Or Not.
	 * @apiSuccess {String} details.envelops.evaluationStatus Event Envelope Evaluation Status.
	 * @apiSuccess {Boolean} details.envelops.allowOpen Event Envelope is Allow to Open by User.
	 * @apiSuccess {Boolean} details.envelops.showOpen Please Ignore this Parameter.
	 * @apiSuccess {Boolean} details.envelops.showView Please Ignore this Parameter.
	 * @apiSuccess {Boolean} details.envelops.showFinish Please Ignore this Parameter.
	 * @apiSuccess {Object[]} details.envelops.bqs Envelop Bq List.
	 * @apiSuccess {String} details.envelops.bqs.id Envelop Bq Id.
	 * @apiSuccess {String} details.envelops.bqs.name Envelop Bq Name.
	 * @apiSuccess {String} details.envelops.bqs.description Envelop Bq Description.
	 * @apiSuccess {Object[]} details.envelops.cqs Envelop Cq List.
	 * @apiSuccess {String} details.envelops.cqs.id Envelop Cq Id.
	 * @apiSuccess {String} details.envelops.cqs.name Envelop Cq Name.
	 * @apiSuccess {String} details.envelops.cqs.description Envelop Cq Description.
	 * @apiSuccess {Boolean} details.envelops.cqs.mandatoryItemCount Please Ignore this Parameter.
	 * @apiSuccess {String} details.envelops.envelopOpenerName Envelop Opener Name.
	 * @apiSuccess {String} details.envelops.envelopEvaluationOwner Envelop Lead Evaluator Name.
	 * @apiSuccess {Object[]} details.envelops.evaluatorUser Envelop Evaluation Team List.
	 * @apiSuccess {Object[]} details.envelops.evaluatorUser.evalUser Envelop Evaluation User List.
	 * @apiSuccess {String} details.envelops.evaluatorUser.evalUser..id Envelop Evaluation Team Member Id.
	 * @apiSuccess {String} details.envelops.evaluatorUser.evalUser.name Envelop Evaluation Team Member Name.
	 * @apiSuccess {Boolean} details.envelops.evaluatorUser.evalUser.locked Please Ignore this Parameter.
	 * @apiSuccess {Boolean} details.envelops.evaluatorUser.evalUser.deleted Please Ignore this Parameter.
	 * @apiSuccess {Boolean} details.envelops.evaluatorUser.evalUser.active Please Ignore this Parameter.
	 * @apiSuccess {Boolean} details.envelops.evaluatorUser.evalUser.checkControl Please Ignore this Parameter.
	 * @apiSuccess {Object[]} details.approvers Event Approvers.
	 * @apiSuccess {String} details.approvers.id Internal Id of the Event Approvers (Unique Id).
	 * @apiSuccess {Integer} details.approvers.level Event Approval level.
	 * @apiSuccess {String} details.approvers.approvalType Event Approval Type Condition(OR/AND).
	 * @apiSuccess {Boolean} details.approvers.done Event Approval level has completed or Not.
	 * @apiSuccess {Boolean} details.approvers.active Event Approval level is Active or Not.
	 * @apiSuccess {Object[]} details.approvers.users Event Approver Users.
	 * @apiSuccess {String} details.approvers.users.approvalStatus Event Approval User Status.
	 * @apiSuccess {String} details.approvers.users.userName Event Approval User Name.
	 * @apiSuccess {String} details.approvers.users.actionDate Event Approval User Action Date.
	 * @apiSuccess {String} details.approvers.users.remarks Event Approval User Remarks.
	 * @apiSuccess {Object[]} details.comments Event Approval Comments.
	 * @apiSuccess {String} details.comments.comment Event Approval User Comment.
	 * @apiSuccess {Date} details.comments.createdDate Event Approval User Comment date.
	 * @apiSuccess {String} details.comments.userName Event Approval Comment User Name.
	 * @apiSuccess {Boolean} details.comments.transientIsApproved Event Approval Comment User Approved/Reject.
	 * @apiSuccess {Object} details.auctionRules RFA Event Auction Rules(For Auction).
	 * @apiSuccess {String} details.auctionRules.id Internal Id of the RFA Event Auction Rules(Unique Id).
	 * @apiSuccess {String} details.auctionRules.auctionType RFA Event Auction Type.
	 * @apiSuccess {Boolean} details.auctionRules.fowardAuction RFA Event Auction is Forward Auction.
	 * @apiSuccess {String} details.auctionRules.preBidBy RFA Event Auction Rule Pre Bid by BUYER/SUPPLIER.
	 * @apiSuccess {Boolean} details.auctionRules.isPreBidHigherPrice RFA Event Auction Rule is Pre Bid Higher Price.
	 * @apiSuccess {Boolean} details.auctionRules.isPreBidSameBidPrice RFA Event Auction Rule is Pre Bid Same Bid Price.
	 * @apiSuccess {Boolean} details.auctionRules.isBiddingMinValueFromPrevious RFA Event Auction Rule is Bidding
	 *             Minimum Value From Previous.
	 * @apiSuccess {String} details.auctionRules.biddingMinValueType RFA Event Auction Rule Bidding Minimum
	 *             VALUE/PERCENTAGE Type.
	 * @apiSuccess {Integer} details.auctionRules.biddingMinValue RFA Event Auction Rule Bidding Minimum Value.
	 * @apiSuccess {Boolean} details.auctionRules.isStartGate RFA Event Auction Rule is Start Gate.
	 * @apiSuccess {Boolean} details.auctionRules.isBiddingPriceHigherLeadingBid RFA Event Auction Rule is Bidding Price
	 *             Higher Leading Bid.
	 * @apiSuccess {Boolean} details.auctionRules.isBiddingAllowSupplierSameBid RFA Event Auction Rule is Bidding Allow
	 *             Supplier Same Bid.
	 * @apiSuccess {String} details.auctionRules.auctionConsolePriceType RFA Event Auction Rule Auction Console Price
	 *             Type.
	 * @apiSuccess {String} details.auctionRules.auctionConsoleVenderType RFA Event Auction Rule Auction Console Vender
	 *             Type.
	 * @apiSuccess {String} details.auctionRules.auctionConsoleRankType RFA Event Auction Rule Auction Console Rank
	 *             Type.
	 * @apiSuccess {Boolean} details.auctionRules.auctionStarted RFA Event Auction Rule is Auction Started.
	 * @apiSuccess {Boolean} details.auctionRules.itemizedBiddingWithTax RFA Event Auction Rule is itemized Bidding With
	 *             Tax.
	 * @apiSuccess {String} details.auctionRules.biddingPriceHigherLeadingBidType RFA Event Auction Rule Bidding Price
	 *             Higher Leading Bid VALUE/PERCENTAGE Type.
	 * @apiSuccess {Integer} details.auctionRules.biddingPriceHigherLeadingBidValue RFA Event Auction Rule Bidding Price
	 *             Higher Leading Bid Value.
	 * @apiSuccess {Boolean} details.auctionRules.lumsumBiddingWithTax RFA Event Auction Rule is lumsum Bidding With
	 *             Tax.
	 * @apiSuccess {Integer} details.auctionRules.dutchStartPrice RFA Event Auction Rule Dutch Auction Start Price.
	 * @apiSuccess {Integer} details.auctionRules.dutchMinimumPrice RFA Event Auction Rule Dutch Auction Minimum Price.
	 * @apiSuccess {Integer} details.auctionRules.amountPerIncrementDecrement RFA Event Auction Rule Amount Per
	 *             Increment/Decrement.
	 * @apiSuccess {String} details.auctionRules.intervalType RFA Event Auction Rule Interval Type MINUTE/SECONDS.
	 * @apiSuccess {Integer} details.auctionRules.interval RFA Event Auction Rule Interval.
	 * @apiSuccess {Integer} details.auctionRules.dutchAuctionTotalStep RFA Event Auction Rule Dutch Auction Total Step.
	 * @apiSuccess {Integer} details.auctionRules.dutchAuctionCurrentStep RFA Event Auction Rule Dutch Auction Current
	 *             Step.
	 * @apiSuccess {Integer} details.auctionRules.dutchAuctionCurrentStepAmount RFA Event Auction Rule Dutch Auction
	 *             Current Step Amount.
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization: Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache"
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/me/RFT/ff8081815a1c5ca1015a1d4150dd0009"
	 * @apiSuccessExample (RFx)Success-Response:
	 * 
	 *                    <pre>
	 *             HTTP/1.1 200 OK
	                  {  
	"details":{  
	  "id":"ff8081815a1c5ca1015a1d4150dd0009",
	  "eventId":"RFT/0208/33",
	  "referenceNumber":"RFT API TEST",
	  "eventName":"RFT API TEST",
	  "eventDescription":"fdgdfgd",
	  "eventOwner":"Buyer Person A",
	  "status":"PENDING",
	  "decimal":"2",
	  "currency":"MYR",
	  "currencyName":"MALAYSIA",
	  "suppliers":[  
	     {  
	        "supplierCompanyName":"Supplier-Test1",
	        "previewTime" : "08/07/2017 02:30 PM",
			"supplierEventReadTime":"09/07/2017 06:30 PM",
			"supplierSubmittedTime":"09/07/2017 07:20 PM",
			"revisedBidDateAndTime": "26/07/2017 01:00 PM",
	        "confirmPriceDateAndTime": "26/07/2017 01:00 PM",
			"revisedBidSubmitted":true
			"confirmPriceSubmitted": false
	     }
	  ],
	  "bqs":[  
	     {  
	        "id":"ff8081815d0c41b6015d0c46f8d1000f",
	        "name":"sd",
	        "description":"scacc",
	        "bqItem":[  
	           {  
	              "id":"ff8081815d0c41b6015d0c474ce50010",
	              "itemName":"cqcqadc",
	              "level":1,
	              "order":0,
	              "itemDescription":"csacqasd",
	              "uom":{  
	                 "uom":"aaa"
	              },
	              "quantity":1;
	           },
	           {  
	              "id":"ff8081815d0c41b6015d0c477f560011",
	              "itemName":"csdca",
	              "level":1,
	              "order":1,
	              "uom":{  
	                 "uom":"aaa"
	              },
	              "quantity":5,
	              "itemDescription":"dasdqawd"
	           }
	        ]
	     }
	  ],
	  "timeLine":[  
	     {  
	        "activityDate":"12/07/2017 02:30 PM",
	        "activity":"EVENT",
	        "description":"Event Publish."
	     },
	     {  
	        "activityDate":"29/07/2017 02:30 AM",
	        "activity":"EVENT",
	        "description":"Event Start."
	     },
	     {  
	        "activityDate":"10/09/2017 02:29 AM",
	        "activity":"EVENT",
	        "description":"Event End."
	     }
	  ],
	  "documents":[  
	     {  
	        "id":"ff8081815a280d58015a28112a370001",
	        "fileName":"datastudio7.0.zip",
	        "fileSize":0
	     }
	  ],
	  "envelops":[  
	     {  
	        "id":"ff8081815d0c41b6015d0c5b4e260012",
	        "envelopTitle":"Envelope Name",
	        "envelopType":"CLOSED",
	        "isOpen":false,
	        "evaluationStatus":"PENDING",
	        "showOpen":false,
	        "showView":false,
	        "showFinish":false,
	        "allowOpen":true
	        "bqs": [
	                {
	                    "id": "ff8081815d5dad5c015d5ec6b57001e2",
	                    "name": "RFT-EVALUATION TESTING",
	                    "description": "RFT-EVALUATION TESTING"
	                }
	            ],
	         "cqs": [
	                {
	                    "id": "ff8081815d5dad5c015d5ec6b57001e6",
	                    "name": "RFT-EVALUATION TESTING",
	                    "description": "RFT-EVALUATION TESTING",
	                    "mandatoryItemCount": 0
	                }
	            ],
	         "envelopOpenerName": "Buyer Person A",
	         "envelopEvaluationOwner": "Buyer Person A",
	         "evaluatorUser": [
	                {
	                    "evaluationStatus": "PENDING",
	                    "evalUser": {
	                        "id": "ff8081815d5dad5c015d5e749e3100f5",
	                        "name": "testuser1",
	                        "locked": false,
	                        "deleted": false,
	                        "active": true,
	                        "checkControl": true
	                    }
	                },
	                {
	                    "evaluationStatus": "PENDING",
	                    "evalUser": {
	                        "id": "ff8081815d5dad5c015d5e753df100f6",
	                        "name": "testuser2",
	                        "locked": false,
	                        "deleted": false,
	                        "active": true,
	                        "checkControl": true
	                    }
	                }
	         ]
	     }
	  ],
	  "approvers":[  
	     {  
	        "id":"ff8081815d0d0130015d0d5bfedb0000",
	        "level":1,
	        "approvalType":"OR",
	        "done":true,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"APPROVED",
	              "userName":"Buyer Person A",
	              "actionDate": "04/07/2017 12:10 PM",
	              "remarks": "ewrewrwrwrwrwr"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815d0d0130015d0d5bfef10002",
	        "level":2,
	        "approvalType":"OR",
	        "done":false,
	        "active":true,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Buyer Person A",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"parveen",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815d0d0130015d0d5bfef50005",
	        "level":3,
	        "approvalType":"OR",
	        "done":false,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Buyer Person A",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           }
	        ]
	     },
	     {  
	        "id":"ff8081815d0d0130015d0d5bfef60007",
	        "level":4,
	        "approvalType":"AND",
	        "done":false,
	        "active":false,
	        "users":[  
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"Buyer Person A",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           },
	           {  
	              "approvalStatus":"PENDING",
	              "userName":"not admin",
	              "userId":"ff8081815cf7915c015cf8c199f1003f"
	           }
	        ]
	     }
	  ],
	  "comments":[  
	     {  
	        "comment":"ewrewrwrwrwrwr",
	        "createdDate":"04/07/2017",
	        "userName":"Buyer Person A",
	        "transientIsApproved":true
	     }
	  ],
	   "auctionRules": {
	        "id": "ff8081815cc8e0d4015cc8e61060000b",
	        "auctionType": "REVERSE_SEALED_BID",
	        "fowardAuction": false,
	        "preBidBy": "SUPPLIER",
	        "isPreBidHigherPrice": false,
	        "isPreBidSameBidPrice": false,
	        "isBiddingMinValueFromPrevious": true,
	        "biddingMinValueType": "VALUE",
	        "biddingMinValue": 200,
	        "isStartGate": false,
	        "isBiddingPriceHigherLeadingBid": false,
	        "isBiddingAllowSupplierSameBid": false,
	        "auctionConsolePriceType": "SHOW_ALL",
	        "auctionConsoleVenderType": "SHOW_ALL",
	        "auctionConsoleRankType": "SHOW_ALL",
	        "auctionStarted": false,
	        "itemizedBiddingWithTax": true,
	        
	        "biddingPriceHigherLeadingBidType": "PERCENTAGE",
	        "biddingPriceHigherLeadingBidValue": 5,
	        "lumsumBiddingWithTax": true,
	        
	        "dutchStartPrice": 71500,
	        "dutchMinimumPrice": 100000,
	        "amountPerIncrementDecrement": 500,
	        "intervalType": "MINUTE",
	        "interval": 2,
	        "dutchAuctionTotalStep": 58,
	        "dutchAuctionCurrentStep": 1,
	        "dutchAuctionCurrentStepAmount": 71500,
	        
	    }
	}
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/detail/{eventType}/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, Object> getDetails(JwtAuthenticationToken token, @PathVariable String id, @PathVariable FilterTypes eventType) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> event = new HashMap<String, Object>();
		LOG.info("Id :" + id);
		try {
			AuthenticatedUser user = checkAuthorizedUser(token);
			if (user.getTenantType() == TenantType.BUYER) {
				switch (eventType) {
				case REQUEST:
					MobileRequestPojo mobileRequestPojo = sourcingFormRequestService.getMobileRequestDetails(id);
					event.put("details", mobileRequestPojo);
					break;
				case PR:
					MobilePrPojo prDetails = prService.getMobilePrDetails(id);
					event.put("details", prDetails);
					break;
				case RFA:
					LOG.info("RFA CASE");
					MobileEventPojo rfaDetails = rfaEventService.getMobileEventDetails(id, user.getId());
					event.put("details", rfaDetails);
					break;
				case RFI:
					MobileEventPojo rfiDetails = rfiEventService.getMobileEventDetails(id, user.getId());
					event.put("details", rfiDetails);
					break;
				case RFP:
					MobileEventPojo rfpDetails = rfpEventService.getMobileEventDetails(id, user.getId());
					event.put("details", rfpDetails);
					break;
				case RFQ:
					MobileEventPojo rfqDetails = rfqEventService.getMobileEventDetails(id, user.getId());
					event.put("details", rfqDetails);
					break;
				case RFT:
					MobileEventPojo rftDetails = rftEventService.getMobileEventDetails(id, user.getId());
					event.put("details", rftDetails);
					break;
				default:
					break;

				}
			} else if (user.getTenantType() == TenantType.SUPPLIER) {
				switch (eventType) {
				case PR:
					MobilePrPojo prDetails = prService.getMobilePrDetailsSupplier(id);
					LOG.info("PO : " + prDetails.toLogString());
					event.put("details", prDetails);
					break;
				case RFA:
					LOG.info("RFA CASE");
					MobileEventPojo rfaDetails = rfaEventService.getMobileEventDetailsForSupplier(id, user.getId(), user.getTenantId());
					event.put("details", rfaDetails);
					break;
				case RFI:
					MobileEventPojo rfiDetails = rfiEventService.getMobileEventDetailsForSupplier(id, user.getId(), user.getTenantId());
					event.put("details", rfiDetails);
					break;
				case RFP:
					MobileEventPojo rfpDetails = rfpEventService.getMobileEventDetailsForSupplier(id, user.getId(), user.getTenantId());
					event.put("details", rfpDetails);
					break;
				case RFQ:
					MobileEventPojo rfqDetails = rfqEventService.getMobileEventDetailsForSupplier(id, user.getId(), user.getTenantId());
					event.put("details", rfqDetails);
					break;
				case RFT:
					MobileEventPojo rftDetails = rftEventService.getMobileEventDetailsForSupplier(id, user.getId(), user.getTenantId());
					event.put("details", rftDetails);
					break;
				default:
					break;

				}
			}
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return event;
	}

	/**
	 * @api {post} /openEnvelope Open Envelope
	 * @apiName openEnvelope
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {String} envelopeId Id of Envelope (Required Parameter).
	 * @apiParam {String} eventId Id of Event (Required Parameter).
	 * @apiParam {String} key key or Password of User to Open the Envelope (Required Parameter).
	 * @apiParam {String} type Event Type (Required Parameter).
	 * @apiParamExample {json} Request-Example: { "envelopeId" : "ff8081815c528f14015c52a362910027", "eventId" :
	 *                  "ff8081815c528f14015c52a3628f001a", "key" : "Admin@123", "type" : "RFT" }
	 * @apiSuccess {Boolean} success Envelope successfully opened or Not.
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"envelopeId" : "ff8081815c528f14015c52a362910027", "eventId" :
	 *             "ff8081815c528f14015c52a3628f001a", "key" : "Admin@123", "type" : "RFT"}'
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/openEnvelope"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	          {
				"success": true
			}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/openEnvelope", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Map<String, Boolean> openEnvelope(JwtAuthenticationToken token, @RequestBody RequestParamPojo param) throws ApplicationException, NotAuthorizedException {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		Boolean success = Boolean.FALSE;
		try {
			LOG.info("Event Id : " + param.getEventId() + " Envelope Id : " + param.getEnvelopeId() + " password :" + param.getKey() + " Event Type :" + param.getType());
			AuthenticatedUser user = checkAuthorizedUser(token);
			switch (param.getType()) {
			case RFA:
				success = rfaEnvelopService.openEnvelope(param.getEnvelopeId(), param.getEventId(), param.getKey(), user);
				break;
			case RFI:
				success = rfiEnvelopService.openEnvelope(param.getEnvelopeId(), param.getEventId(), param.getKey(), user);
				break;
			case RFP:
				success = rfpEnvelopService.openEnvelope(param.getEnvelopeId(), param.getEventId(), param.getKey(), user);
				break;
			case RFQ:
				success = rfqEnvelopService.openEnvelope(param.getEnvelopeId(), param.getEventId(), param.getKey(), user);
				break;
			case RFT:
				success = rftEnvelopService.openEnvelope(param.getEnvelopeId(), param.getEventId(), param.getKey(), user);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while open envelope : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		map.put("success", success);
		return map;
	}

	/**
	 * @api {post} /approveReject Approval or Rejection of Event/PR
	 * @apiName ApproveReject
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {String} eventId Id of Event/PR (Required Parameter).
	 * @apiParam {String} type Event Type (Required Parameter).
	 * @apiParam {Boolean} approve User Approve or Reject Event/PR (Required Parameter).
	 * @apiParam {String} remarks Remarks on Approve or Reject Event/PR (Required Parameter if Rejected).
	 * @apiParamExample {json} Request-Example:
	 * 
	 *                  <pre>
	  {
	"eventId" : "ff8081815b3875ae015b387cfa4b0001",
	"type" : "RFT",
	"approve" : true,
	"remarks" : "RFT approved by buyer person...  "
	}
	 *                  </pre>
	 * 
	 * @apiSuccess {Boolean} success Envelope successfully opened or Not.
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"eventId" : "ff8081815b3875ae015b387cfa4b0001", "type" : "RFT",
	 *             "approve" : true, "remarks" : "RFT approved by buyer person... "}'
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/approveReject"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	{
	"success": true
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/approveReject", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Map<String, Boolean> doMobileApproval(JwtAuthenticationToken token, @RequestBody RequestParamPojo param, HttpSession session) throws ApplicationException, NotAuthorizedException {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		Boolean success = Boolean.FALSE;
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			if (StringUtils.checkString(param.getRemarks()).length() == 0 && !param.isApprove()) {
				throw new ApplicationException("Remark cannot be empty");
			}
			LOG.info("Event Id : " + param.getEventId() + " Event Type :" + param.getType() + " remarks : " + param.getRemarks() + " approved : " + param.isApprove());
			AuthenticatedUser user = checkAuthorizedUser(token);
			success = approvalService.doMobileApproval(param.getEventId(), param.getType(), user, param.getRemarks(), param.isApprove(), session, virtualizer);
		} catch (Exception e) {
			LOG.error("Error while Approve reject Event/PR : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}finally {
			if(virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		map.put("success", success);
		return map;
	}

	/**
	 * @api {post} /myHistory History List
	 * @apiName getMyHistory
	 * @apiGroup DashBoard
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {Integer} start Start value useful for pagination (Optional parameter).
	 * @apiParam {Integer} length Number of records of Entire list from start value useful for pagination (Optional
	 *           parameter).
	 * @apiParam {String} searchValue Search for PR/Event by Reference Number or Name or Owner Name(Optional parameter
	 *           Useful for Search bar).
	 * @apiParam {String} creator Filter for PR/Event by creator(Optional parameter Useful for Filter).
	 * @apiParam {String} supplierName Filter Only for PR/PO by supplier Name(Optional parameter Useful for Filter).
	 * @apiParam {String} unitName Filter for PR/Event by Business Unit Name(Optional parameter Useful for Filter).
	 * @apiParam {String} status Filter for PR/Event by status(Optional parameter Useful for Filter).
	 * @apiParam {String} type Filter for PR/Event by Type(Optional parameter Useful for Filter).
	 * @apiParam {String} actionType Filter for PR/Event by Action Type(Optional parameter Useful for Filter). Possible
	 *           values Are ('APPROVED','REJECTED','OPENED')
	 * @apiParam {String} sortValue Sorting for PR/Event by Event/PR Name or Approval/rejection date. Possible values
	 *           for sortValue eventName or actionDate(Optional parameter Useful for Sorting).
	 * @apiParam {String} order Sorting Order for PR/Event based on Sort value. Possible values ASC/DESC for Ascending
	 *           or Descending Order (Optional parameter Useful for Sorting Order).
	 * @apiParamExample {json} Request-Example: { "start" : 0, "length" : 10, "searchValue" : "fds", "creator" : "Buyer
	 *                  Person A", "supplierName" : "Supplier Person A", "unitName" : "name", "status": "APPROVED",
	 *                  "type" : "PR", "sortValue" : "actionDate/eventName", "order" : "DESC/ASC", "actionType" :
	 *                  "REJECTED" }
	 * @apiSuccess {Object[]} myHistoryList User Approved or Rejected Event/PR History List.
	 * @apiSuccess {String} myHistoryList.id Internal Id of the PR/Event (Unique Id).
	 * @apiSuccess {String} myHistoryList.actionDate Approved or Rejected Date of Event/PR.
	 * @apiSuccess {String} myHistoryList.type Type of PR/Event.
	 * @apiSuccess {String} myHistoryList.createdBy PR/Event Creator Name.
	 * @apiSuccess {String} myHistoryList.eventName PR/Event Name.
	 * @apiSuccess {String} myHistoryList.referenceNumber PR/Event Reference Number (Optional Field for PR).
	 * @apiSuccess {String} myHistoryList.status PR/Event Status.
	 * @apiSuccess {String} myHistoryList.auctionType Type of Auction.
	 * @apiSuccess {Date} myHistoryList.createdDate PR/Event Created Date.
	 * @apiSuccess {Boolean} myHistoryList.isApproved PR/Event is Approved or rejected.
	 * @apiSuccess {String} myHistoryList.unitName PR/Event Business Unit Name.
	 * @apiSuccess {String} myHistoryList.supplierName PR Supplier Name.
	 * @apiSuccess {String} myHistoryList.userComment PR Approval User Comment.
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"start" : 0, "length" : 10, "searchValue" : "fds", "creator" :
	 *             "Buyer Person A", "supplierName" : "Supplier Person A", "unitName" : "name", "status": "APPROVED",
	 *             "type" : "PR", "sortValue" : "actionDate/eventName", "order" : "DESC/ASC" ,"actionType" :
	 *             "REJECTED/APPROVED/OPENED"}' "http://procurehere.dhriti-solution.com/procurehere/api/myHistory"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   
	  {  
	"myHistoryList":[  
	  {  
	     "id":"ff80818159ba5c390159bac195c80010",
	     "actionDate":"02/02/2017 02:31 PM",
	     "type":"PR",
	     "createdBy":"Buyer Person A",
	     "eventName":"fdsf",
	     "referenceNumber":"fds",
	     "status":"APPROVED",
	     "auctionType":"PR",
	     "createdDate":"02/02/2017 02:29 PM",
	     "isApproved":true,
	     "unitName":"Unit Name",
	     "supplierName":"Supplier Person A",
	     "actionType": "APPROVED",
	     "userComment": "asdasd"
	  },
	  {  
	     "id":"ff80818159f8f6c20159f902f29c0000",
	     "actionDate":"19/06/2017 05:04 PM",
	     "type":"PR",
	     "createdBy":"Buyer Person A",
	     "eventName":"awsdf",
	     "referenceNumber":"wert",
	     "status":"PENDING",
	     "auctionType":"PR",
	     "createdDate":"19/06/2017 05:03 PM",
	     "isApproved":true,
	     "unitName":"Unit Name",
	     "actionType": "APPROVED",
	     "userComment": "asdasd"
	  }
	]
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/myHistory", method = RequestMethod.POST, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getMyHistory(JwtAuthenticationToken token, @RequestBody(required = false) SearchSortFilterPojo search) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> myHistory = new HashMap<String, Object>();
		try {
			AuthenticatedUser user = checkAuthorizedUser(token);
			List<ApprovedRejectEventPojo> approvedRejectList = rftEventService.findMyAprrovedRejectList(user.getTenantId(), user.getId(), search);
			myHistory.put("myHistoryList", approvedRejectList);
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return myHistory;
	}

	/**
	 * @api {post} /myEvent My Event List
	 * @apiName getMyEventList
	 * @apiGroup DashBoard
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {Integer} start Start value useful for pagination (Optional parameter).
	 * @apiParam {Integer} length Number of records of Entire list from start value useful for pagination (Optional
	 *           parameter).
	 * @apiParam {String} searchValue Search for PR/Event by Reference Number or Name or Owner Name(Optional parameter
	 *           Useful for Search bar).
	 * @apiParam {String} creator Filter for PR/Event by creator(Optional parameter Useful for Filter).
	 * @apiParam {String} supplierName Filter Only for PR/PO by supplier Name(Optional parameter Useful for Filter).
	 * @apiParam {String} unitName Filter for PR/Event by Business Unit Name(Optional parameter Useful for Filter).
	 * @apiParam {String} status Filter for PR/Event by status(Optional parameter Useful for Filter).
	 * @apiParam {String} type Filter for PR/Event by Type(Optional parameter Useful for Filter).
	 * @apiParam {String} sortValue Sorting for PR/Event by Event/PR Name or Created date. Possible values for sortValue
	 *           eventName or createdDate (Optional parameter Useful for Sorting).
	 * @apiParam {String} order Sorting Order for PR/Event based on Sort value. Possible values ASC/DESC for Ascending
	 *           or Descending Order (Optional parameter Useful for Sorting Order).
	 * @apiParamExample {json} Request-Example: { "start" : 0, "length" : 10, "searchValue" : "fds", "creator" : "Buyer
	 *                  Person A", "supplierName" : "Supplier Person A", "unitName" : "name", "status": "APPROVED",
	 *                  "type" : "PR", "sortValue" : "createdDate/eventName", "order" : "DESC/ASC" }
	 * @apiSuccess {Object[]} myEventList All Event/PR List in which User is a team member.
	 * @apiSuccess {String} myEventList.id Internal Id of the PR/Event (Unique Id).
	 * @apiSuccess {String} myEventList.type Type of PR/Event.
	 * @apiSuccess {String} myEventList.createdBy PR/Event Creator Name.
	 * @apiSuccess {String} myEventList.eventName PR/Event Name.
	 * @apiSuccess {String} myEventList.referenceNumber PR/Event Reference Number (Optional Field for PR).
	 * @apiSuccess {String} myEventList.status PR/Event Status.
	 * @apiSuccess {String} myEventList.auctionType Type of Auction.
	 * @apiSuccess {Date} myEventList.createdDate PR/Event Created Date.
	 * @apiSuccess {String} myEventList.unitName PR/Event Business Unit Name.
	 * @apiSuccess {String} myEventList.supplierName PR Supplier Name.
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"start" : 0, "length" : 10, "searchValue" : "fds", "creator" :
	 *             "Buyer Person A", "supplierName" : "Supplier Person A", "unitName" : "name", "status": "APPROVED",
	 *             "type" : "PR", "sortValue" : "createdDate/eventName", "order" : "DESC/ASC" }'
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/myEvent"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   
	 {
	"myEventList": [
	    {
	        "id": "ff8081815d4f08e3015d4f1b41e30064",
	        "type": "RFA",
	        "createdBy": "Buyer Person A",
	        "eventName": "new   lumsum without  RFA REVERSE ENGLISH AUCTION FOR min bid",
	        "referenceNumber": "new lumsum without RFA REVERSE ENGLISH AUCTION FOR min bid",
	        "status": "DRAFT",
	        "auctionType": "REVERSE_ENGISH",
	        "createdDate": "17/07/2017 01:52 PM",
	        "unitName": "Unit Name"
	    },
	    {
	        "id": "ff8081815d4f08e3015d4f0b73730000",
	        "type": "PR",
	        "createdBy": "Buyer Person A",
	        "eventName": "PR buyer team member test",
	        "referenceNumber": "PR buyer team member test",
	        "status": "DRAFT",
	        "auctionType": "PR",
	        "createdDate": "17/07/2017 01:35 PM",
	        "unitName": "Unit Name",
	        "supplierName": "parveen"
	    }
	]
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/myEvent", method = RequestMethod.POST, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getMyEventList(JwtAuthenticationToken token, @RequestBody(required = false) SearchSortFilterPojo search) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> myEvent = new HashMap<String, Object>();
		try {
			AuthenticatedUser user = checkAuthorizedUser(token);
			if (TenantType.SUPPLIER == user.getTenantType()) {
				List<ApprovedRejectEventPojo> myEventList = rftEventService.findMyEventListForSupplier(user.getTenantId(), user.getId(), search);

				myEvent.put("myEventList", myEventList);
			} else if (TenantType.BUYER == user.getTenantType()) {

				List<ApprovedRejectEventPojo> myEventList = rftEventService.findMyEventList(user.getTenantId(), user.getId(), search);
				myEvent.put("myEventList", myEventList);
			}
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return myEvent;
	}

	/**
	 * @api {post} /metrics Metrics
	 * @apiName getMetrics
	 * @apiGroup DashBoard
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiParam {String} currencyCode Filter Based on currency code for Metrics(Optional).
	 * @apiParam {Date} startDate Filter Based on Date some start Date to end Date for Metrics.
	 * @apiParam {Date} endDate Filter Based on Date some start Date to end Date for Metrics.
	 * @apiParamExample {json} Request-Example: { "currencyCode" : "MYR", "startDate" : "08/03/2017", "endDate" :
	 *                  "09/03/2017" }
	 * @apiSuccess {Object} buyerMetrics Buyer metrics.
	 * @apiSuccess {Integer} buyerMetrics.localSupplierCount Total Number of Suppliers Currently on the Local list.
	 * @apiSuccess {Integer} buyerMetrics.registeredUserCount Total Number of Registered Users for the Buyer Account.
	 * @apiSuccess {Integer} buyerMetrics.activeUserCount Total Number of Active Users for the Buyer Account.
	 * @apiSuccess {Integer} buyerMetrics.aggregateTotalPrCount ALL PR count in a selected currency and selected date
	 *             range.
	 * @apiSuccess {Integer} buyerMetrics.aggregatePendingPrCount Pending PR count in a selected currency and selected
	 *             date range.
	 * @apiSuccess {Integer} buyerMetrics.aggregatePOCount PO count in a selected currency and selected date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregateTotalPrValue Aggregate ALL PR Value in a selected currency and
	 *             selected date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregatePendingPrValue Pending PR Value in a selected currency and selected
	 *             date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregatePOValue PO Value in a selected currency and selected date range.
	 * @apiSuccess {Integer} buyerMetrics.aggregateTotalEventCount ALL Event count in a selected currency and selected
	 *             date range.
	 * @apiSuccess {Integer} buyerMetrics.aggregateActiveEventCount Active Event count in a selected currency and
	 *             selected date range.
	 * @apiSuccess {Integer} buyerMetrics.aggregateClosedCompletedEventCount CLOSED and COMPLETED Event count in a
	 *             selected currency and selected date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregateTotalEventValue ALL Event Value in a selected currency and selected
	 *             date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregateActiveEventValue Active Event Value in a selected currency and
	 *             selected date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregateClosedCompletedEventValue CLOSED and COMPLETED Event Value in a
	 *             selected currency and selected date range.
	 * @apiSuccess {Decimal} buyerMetrics.aggregateAwardedPriceValue FINISHED Event Value in a selected currency and
	 *             selected date range.
	 * @apiSuccess {Object[]} buyerMetrics.currentSupplierCountList Current Supplier Count List for each of the Top 5
	 *             Categories.
	 * @apiSuccess {Integer} buyerMetrics.currentSupplierCountList.supplierCount Supplier Count.
	 * @apiSuccess {Integer} buyerMetrics.currentSupplierCountList.industryCategoryName Industry Category Name.
	 * @apiExample {curl} Example usage: curl -X POST -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" -d '{"currencyCode" : "MYR", "startDate" : "08/03/2017", "endDate" :
	 *             "19/03/2017" }' "http://procurehere.dhriti-solution.com/procurehere/api/metrics"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   
	{
	"buyerMetrics": {
	    "localSupplierCount": 1,
	    "registeredUserCount": 3,
	    "activeUserCount": 3,
	    "currentSupplierCountList": [
	        {
	            "supplierCount": 0,
	            "industryCategoryName": "Timber Tract Operations"
	        },
	        {
	            "supplierCount": 0,
	            "industryCategoryName": "Fur-Bearing Animal and Rabbit Production"
	        },
	        {
	            "supplierCount": 0,
	            "industryCategoryName": "Forest Nurseries and Gathering of Forest Products"
	        }
	    ],
	    "aggregateTotalPrCount": 1,
	    "aggregatePendingPrCount": 0,
	    "aggregatePOCount": 1,
	    "aggregateTotalPrValue": 121.2,
	    "aggregatePendingPrValue": 0,
	    "aggregatePOValue": 121.2,
	    "aggregateTotalEventCount": 4,
	    "aggregateActiveEventCount": 0,
	    "aggregateClosedCompletedEventCount": 1,
	    "aggregateTotalEventValue": 65680.04,
	    "aggregateActiveEventValue": 551,
	    "aggregateClosedCompletedEventValue":5123.04,
	    "aggregateAwardedPriceValue": 60006
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/metrics", method = RequestMethod.POST, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getMetrics(JwtAuthenticationToken token, @RequestBody(required = false) RequestParamPojo filter) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> metrics = new HashMap<String, Object>();
		try {
			AuthenticatedUser user = checkAuthorizedUser(token);

			long localSupplierCount = favoriteSupplierService.countForFavSupplier(user.getTenantId());
			long registeredUserCount = userService.findTotalRegisteredOrActiveUserForTenant(user.getTenantId(), false);
			long activeUserCount = userService.findTotalRegisteredOrActiveUserForTenant(user.getTenantId(), true);

			List<SupplierCountPojo> currentSupplierCountList = favoriteSupplierService.getCurrentSupplierCountForTopFiveCategories(user.getTenantId());

			long aggregateTotalPrCount = prService.findPrPoCountForTenant(user.getTenantId(), null, filter);
			long aggregatePendingPrCount = prService.findPrPoCountForTenant(user.getTenantId(), PrStatus.PENDING, filter);
			long aggregatePOCount = prService.findPrPoCountForTenant(user.getTenantId(), PrStatus.APPROVED, filter);

			BigDecimal aggregateTotalPrValue = prService.findPrPoValueForTenant(user.getTenantId(), null, filter);
			BigDecimal aggregatePendingPrValue = prService.findPrPoValueForTenant(user.getTenantId(), PrStatus.PENDING, filter);
			BigDecimal aggregatePOValue = prService.findPrPoValueForTenant(user.getTenantId(), PrStatus.APPROVED, filter);

			long aggregateTotalEventCount = rftEventService.findAggregateEventCountForTenant(user.getTenantId(), null, filter);
			long aggregateActiveEventCount = rftEventService.findAggregateEventCountForTenant(user.getTenantId(), Arrays.asList(EventStatus.ACTIVE.name()), filter);
			long aggregateClosedCompletedEventCount = rftEventService.findAggregateEventCountForTenant(user.getTenantId(), Arrays.asList(EventStatus.CLOSED.name(), EventStatus.COMPLETE.name()), filter);

			BigDecimal aggregateActiveEventValue = rftEventService.findAggregateEventBudgetAmountValueForTenant(user.getTenantId(), Arrays.asList(EventStatus.ACTIVE.name()), filter);
			LOG.info(" AggregateActiveEventValue :" + aggregateActiveEventValue);

			BigDecimal aggregateAwardedPriceValue = rftEventService.findAggregateEventAwardedPriceValueForTenant(user.getTenantId(), Arrays.asList(EventStatus.FINISHED.name()), filter);
			LOG.info(" aggregateAwardedPriceValue :" + aggregateAwardedPriceValue);

			BigDecimal aggregateClosedCompletedEventValue = rftEventService.findAggregateClosedCompletedEventValueForTenant(user.getTenantId(), Arrays.asList(EventStatus.CLOSED.name(), EventStatus.COMPLETE.name(), EventStatus.FINISHED.name()), filter);
			LOG.info(" aggregateClosedCompletedEventValue " + aggregateClosedCompletedEventValue);

			BigDecimal aggregateTotalEventValue = aggregateActiveEventValue.add(aggregateAwardedPriceValue).add(aggregateClosedCompletedEventValue);
			LOG.info(" setAggregateTotalEventValue " + aggregateTotalEventValue);

			MobileBuyerMetrics buyerMetrics = new MobileBuyerMetrics();
			buyerMetrics.setLocalSupplierCount(localSupplierCount);
			buyerMetrics.setRegisteredUserCount(registeredUserCount);
			buyerMetrics.setActiveUserCount(activeUserCount);
			buyerMetrics.setCurrentSupplierCountList(currentSupplierCountList);
			buyerMetrics.setAggregateTotalPrCount(aggregateTotalPrCount);
			buyerMetrics.setAggregatePendingPrCount(aggregatePendingPrCount);
			buyerMetrics.setAggregatePOCount(aggregatePOCount);
			buyerMetrics.setAggregateTotalPrValue(aggregateTotalPrValue);
			buyerMetrics.setAggregatePendingPrValue(aggregatePendingPrValue);
			buyerMetrics.setAggregatePOValue(aggregatePOValue);
			buyerMetrics.setAggregateTotalEventCount(aggregateTotalEventCount);
			buyerMetrics.setAggregateActiveEventCount(aggregateActiveEventCount);
			buyerMetrics.setAggregateClosedCompletedEventCount(aggregateClosedCompletedEventCount);

			buyerMetrics.setAggregateClosedCompletedEventValue(aggregateClosedCompletedEventValue);
			buyerMetrics.setAggregateActiveEventValue(aggregateActiveEventValue);
			buyerMetrics.setAggregateAwardedPriceValue(aggregateAwardedPriceValue);
			buyerMetrics.setAggregateTotalEventValue(aggregateTotalEventValue);

			metrics.put("buyerMetrics", buyerMetrics);
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return metrics;
	}

	/**
	 * @api {get} /downloadEventDocument/{eventType}/{docId} Download Event/PR Document
	 * @apiName downloadEventDocument
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
		      {  
				"Content-Type":"application/json",
				"X-Requested-With":"XMLHttpRequest",
				"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
				}
	 *                   </pre>
	 * 
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization: Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache"
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/downloadEventDocument/PR/ff8081815d7246fc015d73d65415009c"
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/downloadEventDocument/{eventType}/{docId}", method = RequestMethod.GET)
	public void downloadEventDocument(JwtAuthenticationToken token, @PathVariable FilterTypes eventType, @PathVariable String docId, HttpServletResponse response) throws IOException, Exception, NotAuthorizedException {
		try {
			LOG.info(" Doc ID:: :: " + docId + "::::::Event TYPE :::::::" + eventType);
			checkAuthorizedUser(token);
			switch (eventType) {
			case PR:
				prService.downloadPrDocument(docId, response);
				break;
			case RFA:
				rfaEventService.downloadEventDocument(docId, response);
				break;
			case RFI:
				rfiEventService.downloadEventDocument(docId, response);
				break;
			case RFP:
				rfpEventService.downloadEventDocument(docId, response);
				break;
			case RFQ:
				rfqEventService.downloadEventDocument(docId, response);
				break;
			case RFT:
				rftEventService.downloadEventDocument(docId, response);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while downloading Document : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
	}

	/**
	 * @api {get} /downloadEventSummary/{eventType}/{eventId} Download Event/PR Summary Report
	 * @apiName downloadEventSummary
	 * @apiGroup Event
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
		      {  
				"Content-Type":"application/json",
				"X-Requested-With":"XMLHttpRequest",
				"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
				}
	 *                   </pre>
	 * 
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization: Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache"
	 *             "http://procurehere.dhriti-solution.com/procurehere/api/downloadEventSummary/PR/ff8081815b5cab75015b5cb2b6eb0027"
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/downloadEventSummary/{eventType}/{eventId}", method = RequestMethod.GET)
	public void downloadEventSummary(JwtAuthenticationToken token, @PathVariable FilterTypes eventType, @PathVariable String eventId, HttpServletResponse response, HttpSession session) throws IOException, Exception, NotAuthorizedException {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			LOG.info(" Doc  ID:: :: " + eventId + "::::::Event TYPE :::::::" + eventType);
			AuthenticatedUser loggedInUser = checkAuthorizedUser(token);
			switch (eventType) {
			case PR:
				prService.downloadPrSummary(eventId, response, session);
				break;
			case RFA:
				rfaEventService.downloadEventSummary(eventId, response, session, loggedInUser, virtualizer);
				break;
			case RFI:
				rfiEventService.downloadEventSummary(eventId, response, session, loggedInUser, virtualizer);
				break;
			case RFP:
				rfpEventService.downloadEventSummary(eventId, response, session, loggedInUser, virtualizer);
				break;
			case RFQ:
				rfqEventService.downloadEventSummary(eventId, response, session, loggedInUser, virtualizer);
				break;
			case RFT:
				rftEventService.downloadEventSummary(eventId, response, session, loggedInUser, virtualizer);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while downloading Document : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	/**
	 * @api {get} /currency Currency List
	 * @apiName getCurrencyList
	 * @apiGroup Master Data
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiSuccess {Object[]} currencyList Currency List.
	 * @apiSuccess {String} currencyList.currencyCode Currency Code.
	 * @apiSuccess {String} currencyList.currencyName Currency Name.
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" "http://procurehere.dhriti-solution.com/procurehere/api/currency"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   
	{
	"currencyList": [
	    {
	        "currencyCode": "AED",
	        "currencyName": "Arab Emirates Dirham"
	    },
	    {
	        "currencyCode": "AFN",
	        "currencyName": "Afghanistan Afghani"
	    },
	    {
	        "currencyCode": "ALL",
	        "currencyName": "Albanian Lek"
	    },
	    {
	        "currencyCode": "AOA",
	        "currencyName": "Angolan Kwanza"
	    },
	    {
	        "currencyCode": "ARS",
	        "currencyName": "Argentine Peso"
	    },
	    {
	        "currencyCode": "AUD",
	        "currencyName": "Australian Dollar"
	    },{
	        "currencyCode": "ZWD",
	        "currencyName": "Zimbabwe Dollar"
	    }
	]
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/currency", method = RequestMethod.GET, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getCurrencyList(JwtAuthenticationToken token) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> currencyMap = new HashMap<String, Object>();
		try {
			checkAuthorizedUser(token);

			List<Currency> currencyList = currencyService.getAllActiveCurrenciesForMobileApi();
			currencyMap.put("currencyList", currencyList);
		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return currencyMap;
	}

	/**
	 * @api {get} /businessUnit Business Unit List
	 * @apiName getBusinessUnitListForTenant
	 * @apiGroup Master Data
	 * @apiHeader {String} Content-Type should be application/json
	 * @apiHeader {String} X-Requested-With should be XMLHttpRequest
	 * @apiHeader {String} X-Authorization Bearer [jwtToken]
	 * @apiHeaderExample {json} Header-Example:
	 * 
	 *                   <pre>
	      {  
			"Content-Type":"application/json",
			"X-Requested-With":"XMLHttpRequest",
			"X-Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
			}
	 *                   </pre>
	 * 
	 * @apiSuccess {Object[]} businessUnitList Business Unit List.
	 * @apiSuccess {String} businessUnitList.id Business Unit Unique Id.
	 * @apiSuccess {String} businessUnitList.unitName Business Unit Name.
	 * @apiSuccess {String} businessUnitList.displayName Business Unit Display Name.
	 * @apiSuccess {String} businessUnitList.status Business Unit Status.
	 * @apiExample {curl} Example usage: curl -X GET -H "X-Authorization": "Bearer
	 *             eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w"
	 *             -H "Cache-Control: no-cache" "http://procurehere.dhriti-solution.com/procurehere/api/businessUnit"
	 * @apiSuccessExample Success-Response:
	 * 
	 *                    <pre>
	 *                    HTTP/1.1 200 OK
	                   
	{
	"businessUnitList": [
	    {
	        "id": "ff8080815d68ce2b015d68d433821fc3",
	        "unitName": "MYUNIT",
	        "displayName": "BUSINESSUNIT",
	        "status": "ACTIVE"
	    },
	    {
	        "id": "ff8080815e273732015e273c58f30000",
	        "unitName": "MYUNIT 1",
	        "displayName": "DISPLAY NAME",
	        "status": "ACTIVE"
	    },
	    {
	        "id": "ff8080815e273732015e273cb4b40001",
	        "unitName": "MYUNIT 2",
	        "displayName": "MYUNIT 2",
	        "status": "ACTIVE"
	    },
	    {
	        "id": "ff8080815e273732015e273d6c230002",
	        "unitName": "MYUNIT 3",
	        "displayName": "MYUNIT 3",
	        "status": "ACTIVE"
	    }
	]
	}
	 *                    </pre>
	 * 
	 * @apiUse UNAUTHORIZED
	 * @apiUse SERVER_ERROR
	 */
	@RequestMapping(value = "/api/businessUnit", method = RequestMethod.GET, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAllActiveBusinessUnitForMoblie(JwtAuthenticationToken token) throws NotAuthorizedException, ApplicationException {
		Map<String, Object> businessUnitMap = new HashMap<String, Object>();
		try {
			AuthenticatedUser user = checkAuthorizedUser(token);
			List<BusinessUnit> businessUnitList = businessUnitService.getAllActiveBusinessUnitForMoblie(user.getTenantId());

			businessUnitMap.put("businessUnitList", businessUnitList);
		} catch (Exception e) {
			LOG.error(" Error In Busines Unit List :" + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
		return businessUnitMap;
	}

	/**
	 * @apiDefine UNAUTHORIZED
	 * @apiError 401 Unauthorized access to protected resource. Requires login first.
	 * @apiErrorExample {json} Error-Response: HTTP/1.1 401 Unauthorized { "status" : 401, "message" : "Token has
	 *                  expired", "errorCode" : 11, "timestamp" : 1497346221994 }
	 */
	@ExceptionHandler(NotAuthorizedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public @ResponseBody Map<String, String> handleException(NotAuthorizedException e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", HttpStatus.UNAUTHORIZED.toString());
		response.put("errorMessage", e.getMessage());
		return response;
	}

	/**
	 * @apiDefine SERVER_ERROR
	 * @apiError 500 Internal Server Error.
	 * @apiErrorExample {json} Error-Response: HTTP/1.1 500 Server Error { "errorMessage": "/ by zero", "errorCode":
	 *                  "500" }
	 */
	@ExceptionHandler(ApplicationException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, String> handleApplicationException(ApplicationException e) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.toString());
		response.put("errorMessage", e.getMessage());
		return response;
	}

	private AuthenticatedUser checkAuthorizedUser(JwtAuthenticationToken token) throws NotAuthorizedException {
		AuthenticatedUser user = (AuthenticatedUser) token.getPrincipal();
		if (user == null) {
			throw new NotAuthorizedException("Invalid user");
		}
		LOG.info("User ID : " + user.getId());
		if (user.getAuthorities() == null) {
			throw new NotAuthorizedException("Invalid user");
		}
		boolean allowed = false;
		for (GrantedAuthority ga : user.getAuthorities()) {

			if (ga.getAuthority().equals("ROLE_BUYER") || ga.getAuthority().equals("ROLE_SUPPLIER")) {
				allowed = true;
				break;
			}
		}
		if (!allowed) {
			throw new NotAuthorizedException("Invalid user");
		}
		return user;
	}
}
