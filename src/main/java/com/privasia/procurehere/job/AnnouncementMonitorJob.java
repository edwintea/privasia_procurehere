package com.privasia.procurehere.job;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.pojo.AnnouncementPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.AnnouncementService;

/**
 * @author yogesh
 */
@Component
public class AnnouncementMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(AnnouncementMonitorJob.class);

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	AnnouncementService announcementService;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
//		LOG.debug("Start  Announsment - " + System.currentTimeMillis());
//		LOG.info("Running Announcement ---->");
		sendAnnounsment();
	}

	private void sendAnnounsment() {
		try {
			List<AnnouncementPojo> list = announcementService.getAnnouncementList();
			if (CollectionUtil.isNotEmpty(list)) {
				LOG.info("Publish Announcement List Size---->" + list.size());
				for (AnnouncementPojo announcement : list) {
					if (Boolean.TRUE.equals(announcement.getSms()) && Boolean.FALSE.equals(announcement.getIsSmsSent())) {
						sentToQue(announcement.getId(), "QUEUE.ANNOUNCEMENT.SMS");
						LOG.info("SMS sent---->" + announcement.getId());
						announcementService.updateAnnouncementSMSSentFlag(announcement.getId());
					}
					if (Boolean.TRUE.equals(announcement.getFax()) && Boolean.FALSE.equals(announcement.getIsFaxSent())) {
						sentToQue(announcement.getId(), "QUEUE.ANNOUNCEMENT.FAX");
						LOG.info("FAX sent---->" + announcement.getId());
						announcementService.updateAnnouncementFaxSentFlag(announcement.getId());
					}
					if (Boolean.TRUE.equals(announcement.getEmail()) && Boolean.FALSE.equals(announcement.getIsemailSent())) {
						sentToQue(announcement.getId(), "QUEUE.ANNOUNCEMENT.EMAIL");
						LOG.info("EMAIL sent---->" + announcement.getId());
						announcementService.updateAnnouncementEmailSentFlag(announcement.getId());
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during Sending Announcment notification : " + e.getMessage(), e);
		}
	}

	private void sentToQue(String announcement, String queName) {
		try {
//			jmsTemplate.setDefaultDestinationName(queName);
			jmsTemplate.send(queName, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage objectMessage = session.createTextMessage();
					objectMessage.setText(announcement);
					return objectMessage;
				}
			});
		} catch (Exception e) {
			LOG.error("Error sending message to queue : " + e.getMessage(), e);
		}
	}

}
