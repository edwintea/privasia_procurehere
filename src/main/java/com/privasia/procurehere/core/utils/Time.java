package com.privasia.procurehere.core.utils;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 19-Oct-2010
 * Time: 17:02:16
 * To change this template use File | Settings | File Templates.
 */
public class Time  implements Serializable {
	private static final long serialVersionUID = -9082356106505693557L;

	private Log log = LogFactory.getLog(Time.class);
	private String time;

	public Time() {
	}

	public static void main(String a[]) {		

	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		if (time != null)
			time = time.trim();
		this.time = getCorrectTimeStampFormat(time);
	}

	public int getHour() {
		if (org.apache.commons.lang.StringUtils.isNotEmpty(time)) {
			String[] splittedTime = time.split(":");
			if (splittedTime.length > 0)
				return Integer.parseInt(splittedTime[0].trim());
		}
		return 0;
	}

	public int getMinute() {
		if (StringUtils.isNotEmpty(time)) {
			String[] splittedTime = time.split(":");
			if (splittedTime.length > 1)
				return Integer.parseInt(splittedTime[1].trim());
		}
		return 0;
	}

	public int getSecond() {
		if (StringUtils.isNotEmpty(time)) {
			String[] splittedTime = time.split(":");
			if (splittedTime.length > 2)
				return Integer.parseInt(splittedTime[2].trim());
		}
		return 0;
	}

	private String getCorrectTimeStampFormat(String time) {
		String corTime = "00:00:00";
		String strHours = "";
		String strMins = "";
		String strSecs = "";

		try {
			if (time.length() > 0) {
				if (time.indexOf(":") > 0) {
					String[] splittedTime = time.split(":");
					if (splittedTime.length == 3) {
						int hours = Integer.parseInt(splittedTime[0]);
						int mins = Integer.parseInt(splittedTime[1]);
						int secs = Integer.parseInt(splittedTime[2]);

						while (secs > 59) {
							secs = secs - 60;
							mins = mins + 1;
						}
						while (mins > 59) {
							mins = mins - 60;
							hours = hours + 1;
						}
						strHours = String.valueOf(hours).length() == 1 ? "0" + String.valueOf(hours) : String
								.valueOf(hours);
						strMins = String.valueOf(mins).length() == 1 ? "0" + String.valueOf(mins) : String
								.valueOf(mins);
						strSecs = String.valueOf(secs).length() == 1 ? "0" + String.valueOf(secs) : String
								.valueOf(secs);

						corTime = strHours + ":" + strMins + ":" + strSecs;
					} else if (splittedTime.length == 2) {
						int hours = Integer.parseInt(splittedTime[0]);
						int mins = Integer.parseInt(splittedTime[1]);

						while (mins > 59) {
							mins = mins - 60;
							hours = hours + 1;
						}
						strHours = String.valueOf(hours).length() == 1 ? "0" + String.valueOf(hours) : String
								.valueOf(hours);
						strMins = String.valueOf(mins).length() == 1 ? "0" + String.valueOf(mins) : String
								.valueOf(mins);

						corTime = strHours + ":" + strMins + ":00";
					} else {
						corTime = splittedTime[0].length() == 1 ? "0" + splittedTime[0] + ":00:00" : splittedTime[0]
								+ ":00:00";
					}
				} else {
					/* only hours */
					corTime = time.length() == 1 ? "0" + time + ":00:00" : time + ":00:00";
				}
			}

		} catch (Exception e) {
			log.warn(time);
			return corTime;
		}
		return corTime;
	}
}
