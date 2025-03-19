/**
 * 
 */
package com.privasia.procurehere.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;

/**
 * @author Nitin Otageri
 */
@Component
public class MeetingStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(MeetingStatusMonitorJob.class);

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	MessageSource messageSource;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		try {
			int updatedMeetings = rftEventMeetingDao.activateScheduledMeetings();
			if (updatedMeetings > 0) {
				LOG.info("Activated RFT " + updatedMeetings + " meetings that reached their scheduled time");
			}
			updatedMeetings = rfpEventMeetingDao.activateScheduledMeetings();
			if (updatedMeetings > 0) {
				LOG.info("Activated RFP " + updatedMeetings + " meetings that reached their scheduled time");
			}
			updatedMeetings = rfqEventMeetingDao.activateScheduledMeetings();
			if (updatedMeetings > 0) {
				LOG.info("Activated RFQ " + updatedMeetings + " meetings that reached their scheduled time");
			}
			updatedMeetings = rfiEventMeetingDao.activateScheduledMeetings();
			if (updatedMeetings > 0) {
				LOG.info("Activated RFI " + updatedMeetings + " meetings that reached their scheduled time");
			}
			updatedMeetings = rfaEventMeetingDao.activateScheduledMeetings();
			if (updatedMeetings > 0) {
				LOG.info("Activated RFA " + updatedMeetings + " meetings that reached their scheduled time");
			}
		} catch (Exception e) {
			LOG.error("Error updating meeting status from SCHEDULED to ONGOING : " + e.getMessage(), e);
		}
	}
}
