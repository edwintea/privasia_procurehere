/**
 * 
 */
package com.privasia.procurehere.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Nitin
 */
public class GenericScheduleJob extends QuartzJobBean {

	private static final String APPLICATION_CONTEXT_KEY = "applicationContext";
	//private static final Logger LOG = LogManager.getLogger(GenericScheduleJob.class);

	private String batchProcessorName;

	@Override
	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
		BaseSchedulerJob job = (BaseSchedulerJob) getApplicationContext(ctx).getBean(batchProcessorName);
		job.execute(ctx);
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

	/**
	 * @return batchProcessorName
	 */
	public String getBatchProcessorName() {
		return batchProcessorName;
	}

	/**
	 * @param name to set batchProcessorName
	 */
	public void setBatchProcessorName(String name) {
		this.batchProcessorName = name;
	}

}
