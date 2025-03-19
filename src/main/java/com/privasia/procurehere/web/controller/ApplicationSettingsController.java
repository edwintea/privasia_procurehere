/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import com.privasia.procurehere.core.entity.ApplicationSettings;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.ApplicationSettingsService;

/**
 * @author Ravi
 */
@Controller
public class ApplicationSettingsController implements Serializable {

	private static final long serialVersionUID = -4008533250953191395L;

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	ApplicationSettingsService settingService;

	private List<ApplicationSettings> settingsList = null;

	private ApplicationSettings selectedSettings = new ApplicationSettings();

	private boolean editMode = false;

	@PostConstruct
	public void init() {
		if (CollectionUtil.isEmpty(settingsList)) {
		}
	}

	public void getRunningText() {
	}

	public void loadForEdit() throws IOException, JAXBException {
	}

	/**
	 * 
	 */
	public void addApplicationSettings() {
		this.selectedSettings = new ApplicationSettings();
		this.editMode = false;
	}

	/**
	 * @param event
	 */
	public void update() {
	}

	/**
	 * 
	 */
	public void save() {
		try {
			settingService.save(selectedSettings);
			this.editMode = true;
			LOG.info("Parametaer Name : " + selectedSettings.getParameterValue() + "  created By : " + SecurityLibrary.getLoggedInUserLoginId());
			this.settingsList = null;
			selectedSettings = new ApplicationSettings();
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	public String deleteRecord() {
		try {
			if (this.selectedSettings != null) {
				settingService.delete(selectedSettings);
				LOG.info("Service Record Successfully deleted...");
			} else {
				LOG.info("Service obj is null, so cannot delete...");
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			String cause = e.getCause().getCause().toString();
			if (cause.contains("Cannot delete or update a parent row")) {
			} else if (cause.contains("child record found")) {
			} else {
			}
		}
		return null;
	}

	public String reset() {
		setDefaults();
		return null;
	}

	/**
	 * THis method will set all default values
	 */
	private void setDefaults() {
		this.settingsList = null;
		this.selectedSettings = new ApplicationSettings();
	}

	/**
	 * @return the settingsList
	 */
	public List<ApplicationSettings> getSettingsList() {
		return settingsList;
	}

	/**
	 * @param settingsList the settingsList to set
	 */
	public void setSettingsList(List<ApplicationSettings> settingsList) {
		this.settingsList = settingsList;
	}

	/**
	 * @return the selectedSettings
	 */
	public ApplicationSettings getSelectedSettings() {
		return selectedSettings;
	}

	/**
	 * @param selectedSettings the selectedSettings to set
	 */
	public void setSelectedSettings(ApplicationSettings selectedSettings) {
		this.selectedSettings = selectedSettings;
	}

	/**
	 * @return the editMode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode the editMode to set
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

}
