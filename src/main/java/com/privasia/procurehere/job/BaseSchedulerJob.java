/**
 * 
 */
package com.privasia.procurehere.job;

import org.quartz.JobExecutionContext;

/**
 * @author Nitin Otageri
 */
public interface BaseSchedulerJob {
	void execute(JobExecutionContext ctx);
}
