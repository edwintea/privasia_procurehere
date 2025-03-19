/**
 * 
 */
package com.privasia.procurehere.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.FreeTrialEnquiryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.UserService;

/**
 * @author ravi
 */
@Component
public class FreeTrialMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(FreeTrialMonitorJob.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	FreeTrialEnquiryService freeTrialInquiryService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	UserService userService;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Autowired
	MessageSource messageSource;

	@SuppressWarnings("deprecation")
	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		List<FreeTrialEnquiry> pendingSubmitionList = freeTrialInquiryService.findFreeTrialForNotification();
		if (CollectionUtil.isNotEmpty(pendingSubmitionList)) {
			Calendar cal = Calendar.getInstance();
			for (FreeTrialEnquiry enquiry : pendingSubmitionList) {
				try {
					User user = userService.getUserByLoginId(enquiry.getEmailId());

					if (user != null) {
						LOG.info("User : " + user.getId());
						SecurityToken securityToken = securityTokenDao.generateTokenWithValidityForUser(TokenValidity.ONE_DAY, user);

						Map<String, String> data = new HashMap<String, String>();
						data.put("token", securityToken.getToken());
						data.put("email", user.getLoginId());

						String[] urlData = EncryptionUtils.encryptObject(data);

						HashMap<String, Object> map = new HashMap<String, Object>();
						String mailTo = enquiry.getEmailId();
						String subject = "Don’t miss out on your e-procurement opportunity";
						String appUrl = APP_URL + "/buyer/buyerProfileSetup?d=" + urlData[0] + "&v=" + urlData[1];
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
						df.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
						map.put("date", df.format(new Date()));
						map.put("userName", enquiry.getUserName());
						map.put("appUrl", appUrl);

						map.put("year", cal.get(Calendar.YEAR));
						map.put("logoImage", APP_URL + "/resources/images/public/procuhereLogo.png");
						try {
							notificationService.sendEmail(mailTo, subject, map, Global.TRIAL_SUBMITTION_NOTIFICATION_TEMPLATE);
							//LOG.info("Followup email sent to : " + enquiry.getUserName());
						} catch (Exception e) {
							LOG.error("Error while sending followup email to " + enquiry.getUserName(), e);
						}

						enquiry.setFollowupEmailDate(new Date());
						enquiry.setFollowupEmailSent(Boolean.TRUE);

						freeTrialInquiryService.updateFreeTrialEnquiry(enquiry);
					}
				} catch (Exception e) {
					LOG.error("Error occured while sending followup email notification for buyer profile setup : " + e.getMessage(), e);
				}
			}

		}
	}
}
