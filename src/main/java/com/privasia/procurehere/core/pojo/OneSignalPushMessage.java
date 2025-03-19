/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Nitin Otageri
 *
 */
@JsonInclude(Include.NON_NULL)
public class OneSignalPushMessage implements Serializable {

	private static final long serialVersionUID = -5674621605196718045L;

	private String app_id;
	private List<String> included_segments;
	private List<String> include_player_ids;
	private Map<String, String> data;
	private OneSignalMessageContents contents;
	private String ios_sound = "notification.aiff";
	private String android_sound = "notification";

	public OneSignalPushMessage() {
	}

	public OneSignalPushMessage(String appId, String alert, String url, Map<String, String> payload, List<String> targetDevices) {
		this.app_id = appId;
		this.contents = new OneSignalMessageContents(alert);
		if (CollectionUtil.isNotEmpty(targetDevices)) {
			this.include_player_ids = targetDevices;
		} else {
			this.included_segments = new ArrayList<String>();
			this.included_segments.add("All");
		}
		this.data = payload;
	}

	/**
	 * @return the app_id
	 */
	public String getApp_id() {
		return app_id;
	}

	/**
	 * @param app_id
	 *            the app_id to set
	 */
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	/**
	 * @return the included_segments
	 */
	public List<String> getIncluded_segments() {
		return included_segments;
	}

	/**
	 * @param included_segments
	 *            the included_segments to set
	 */
	public void setIncluded_segments(List<String> included_segments) {
		this.included_segments = included_segments;
	}

	/**
	 * @return the include_player_ids
	 */
	public List<String> getInclude_player_ids() {
		return include_player_ids;
	}

	/**
	 * @param include_player_ids
	 *            the include_player_ids to set
	 */
	public void setInclude_player_ids(List<String> include_player_ids) {
		this.include_player_ids = include_player_ids;
	}
	
	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}

	/**
	 * @return the contents
	 */
	public OneSignalMessageContents getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(OneSignalMessageContents contents) {
		this.contents = contents;
	}

	/**
	 * @return the ios_sound
	 */
	public String getIos_sound() {
		return ios_sound;
	}

	/**
	 * @param ios_sound
	 *            the ios_sound to set
	 */
	public void setIos_sound(String ios_sound) {
		this.ios_sound = ios_sound;
	}

	/**
	 * @return the android_sound
	 */
	public String getAndroid_sound() {
		return android_sound;
	}

	/**
	 * @param android_sound
	 *            the android_sound to set
	 */
	public void setAndroid_sound(String android_sound) {
		this.android_sound = android_sound;
	}

}
