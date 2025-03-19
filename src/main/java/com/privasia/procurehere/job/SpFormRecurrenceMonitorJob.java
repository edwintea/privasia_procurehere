/**
 * 
 */
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

import com.privasia.procurehere.service.SupplierPerformanceFormService;

/**
 * @author anshul
 */
@Component
public class SpFormRecurrenceMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(SpFormRecurrenceMonitorJob.class);

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Override
	public void execute(JobExecutionContext ctx) {
		LOG.debug("Start  Supplier Performance Form Recurrence Job - " + System.currentTimeMillis());
		try {
			List<String> newFormIds = supplierPerformanceFormService.createRecurrenceForm();
			for(String newFormId : newFormIds) {
				try {
					String queue = "QUEUE.FORM.FINISH.NOTIFICATION";
					jmsTemplate.send(queue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(newFormId);
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}				
			}
		} catch (Exception e) {
			LOG.error("Error while creating Recurrence Performance Evaluation Form : " + e.getMessage(), e);
		}
		LOG.debug("END  Supplier Performance Form Recurrence Job - " + System.currentTimeMillis());
	}

}
