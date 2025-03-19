/**
 * 
 */
package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author RT-Kapil
 */
@Component
public class TemplateEditor extends PropertyEditorSupport {

	private static final Logger LOG=LogManager.getLogger(Global.ADMIN_LOG);
	
	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Override
	public void setAsText(String value) {
		LOG.info("value in temp editor   :  " +value);
		if (StringUtils.checkString(value).length() > 0) {
			RfxTemplate template = rfxTemplateDao.findById(value);
			
			this.setValue(template);
			LOG.info("afafsf asdad  "+template); 
		}
	}

	@Override
	public String getAsText() {
		LOG.info("afafsf asdad  "+getValue().toString());
		return getValue() == null ? "" : ((RfxTemplate) getValue()).toString();
	}
}
