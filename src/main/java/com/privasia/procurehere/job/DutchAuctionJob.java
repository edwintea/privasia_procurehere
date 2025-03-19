/**
 * 
 */
package com.privasia.procurehere.job;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.pojo.WebSocketPojo;
import com.privasia.procurehere.service.RfaEventService;

/**
 * @author Nitin Otageri
 */
@Component
public class DutchAuctionJob extends QuartzJobBean {

	private static final Logger LOG = LogManager.getLogger(DutchAuctionJob.class);

	private static final String APPLICATION_CONTEXT_KEY = "applicationContext";

	RfaEventService rfaEventService;

	SchedulerFactoryBean schedulerFactoryBean;
	
	SimpMessagingTemplate simpMessagingTemplate;

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {

		rfaEventService = (RfaEventService) getApplicationContext(context).getBean(RfaEventService.class);
		schedulerFactoryBean = (SchedulerFactoryBean) getApplicationContext(context).getBean(SchedulerFactoryBean.class);
		simpMessagingTemplate = (SimpMessagingTemplate) getApplicationContext(context).getBean(SimpMessagingTemplate.class);
		LOG.info("Auto wire of rfaEventService in job 1 : " + rfaEventService);
		String auctionId = (String) context.getJobDetail().getJobDataMap().get("auctionId");

		LOG.info("Auto wire of rfaEventService in job : " + auctionId);
		AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById(auctionId);

		String eventId = auctionRules.getEvent().getId();
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);

		if (Boolean.FALSE == auctionRules.getAuctionStarted() && event.getStatus() == EventStatus.COMPLETE) {
			LOG.info("here the auction is start : ");
			auctionRules.setAuctionStarted(Boolean.TRUE);
		}

		LOG.info("before cal current step : " + auctionRules.getDutchAuctionCurrentStep() + ":  amount  :  " + auctionRules.getDutchAuctionCurrentStepAmount());
		Integer currentStep = auctionRules.getDutchAuctionCurrentStep();
		if (currentStep == 0) {
			auctionRules.setDutchAuctionCurrentStepAmount(auctionRules.getDutchStartPrice());
		} else {
			BigDecimal dutchAuctionCurrentStepAmount = BigDecimal.ZERO;
			if (auctionRules.getDutchAuctionTotalStep() - 1 != currentStep) {
				if (Boolean.TRUE == auctionRules.getFowardAuction()) {
					dutchAuctionCurrentStepAmount = auctionRules.getDutchAuctionCurrentStepAmount().subtract(auctionRules.getAmountPerIncrementDecrement());
				} else {
					dutchAuctionCurrentStepAmount = auctionRules.getDutchAuctionCurrentStepAmount().add(auctionRules.getAmountPerIncrementDecrement());
				}
			} else {
				dutchAuctionCurrentStepAmount = auctionRules.getDutchMinimumPrice();
				LOG.info("after cal last step : " + auctionRules.getDutchAuctionCurrentStep() + ":  amount  :  " + dutchAuctionCurrentStepAmount);
			}
			auctionRules.setDutchAuctionCurrentStepAmount(dutchAuctionCurrentStepAmount);
		}
		try {
			auctionRules.setDutchAuctionCurrentStep(currentStep + 1);
			LOG.info("after cal current step : " + auctionRules.getDutchAuctionCurrentStep() + ":  amount  :  " + auctionRules.getDutchAuctionCurrentStepAmount());
			auctionRules = rfaEventService.updateAuctionRules(auctionRules);
			WebSocketPojo webSocketPojo = new WebSocketPojo(auctionRules.getDutchAuctionCurrentStepAmount(), auctionRules.getDutchAuctionCurrentStep(),event.getStatus());
			LOG.info("simpMessagingTemplate "+simpMessagingTemplate);
			simpMessagingTemplate.convertAndSend("/dutchAuctionData/" + eventId, webSocketPojo);
		} catch (Exception e1) {
			LOG.info("Error : " + e1.getMessage(), e1);
		}

		try {
			JobDetail jobDetail = context.getJobDetail();

			if (auctionRules.getDutchAuctionCurrentStep() > auctionRules.getDutchAuctionTotalStep()) {
				LOG.info("here to stop a job : " + jobDetail.getKey());
				schedulerFactoryBean.getScheduler().deleteJob(jobDetail.getKey());
				// auctionRules.setAuctionCompleted(Boolean.TRUE);
				auctionRules = rfaEventService.updateAuctionRules(auctionRules);

				// RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
				event.setAuctionComplitationTime(new Date());
				event.setStatus(EventStatus.CLOSED);
				rfaEventService.updateRfaEvent(event);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param cxt cxt
	 * @return ApplicationContext
	 * @throws JobExecutionException in case any error
	 */
	private ApplicationContext getApplicationContext(JobExecutionContext cxt) throws JobExecutionException {
		ApplicationContext context = null;

		try {
			context = (ApplicationContext) cxt.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
		} catch (SchedulerException ex) {
			throw new JobExecutionException(ex.getMessage());
		}

		if (context == null) {
			throw new JobExecutionException("No application context available in scheduler context for key \"" + APPLICATION_CONTEXT_KEY + "\"");
		}
		return context;
	}
	
}
