/**
 * 
 */
package com.privasia.procurehere.core.utils;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.PrTemplateField;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateField;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.enums.PrTemplateFieldName;
import com.privasia.procurehere.core.enums.RfxTemplateFieldName;
import com.privasia.procurehere.core.enums.SourcingTemplateFieldName;

/**
 * @author Nitin Otageri
 */
public class TemplateUtils {

	private static final Logger LOG = LogManager.getLogger(TemplateUtils.class);

	@SuppressWarnings("rawtypes")
	public static boolean contains(List list, Object field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking contains for field : " + field);
		}
		return list.contains(field);
	}

	public static String defaultValue(List<TemplateField> list, RfxTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking default value for field : " + field);
		}
		if (list != null) {
			for (TemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getDefaultValue();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return "";
	}

	public static boolean visibility(List<TemplateField> list, RfxTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking visibility for field : " + field);
		}
		if (list != null) {
			for (TemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getVisible();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return true;
	}

	public static boolean readonly(List<TemplateField> list, RfxTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking readonly for field : " + field);
		}
		if (list != null) {
			for (TemplateField tf : list) {
				if (tf.getFieldName() == field) {
					return tf.getReadOnly();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return false;
	}

	public static boolean required(List<TemplateField> list, RfxTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking required for field : " + field);
		}
		if (list != null) {
			for (TemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return !tf.getOptional();
				}
			}
			return false;
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return true;
	}

	public static String defaultValue(List<PrTemplateField> list, PrTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking default value for field : " + field);
		}
		if (list != null) {
			for (PrTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getDefaultValue();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return "";
	}

	public static boolean visibility(List<PrTemplateField> list, PrTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking visibility for field : " + field);
		}
		if (list != null) {
			for (PrTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getVisible();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return true;
	}

	public static boolean readonly(List<PrTemplateField> list, PrTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking readonly for field : " + field);
		}
		if (list != null) {
			for (PrTemplateField tf : list) {
				if (tf.getFieldName() == field) {
					return tf.getReadOnly();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return false;
	}

	public static boolean required(List<PrTemplateField> list, PrTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking required for field : " + field);
		}
		if (list != null) {
			for (PrTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return !tf.getOptional();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return true;
	}
	
	public static boolean visibility(PrTemplate prTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval visibility : " + (prTemplate != null ? prTemplate.getApprovalVisible() : "" ));
		}
		if (prTemplate != null) {
			return prTemplate.getApprovalVisible(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("prTemplate is null");
			}
		}
		return true;
	}

	public static boolean readonly(PrTemplate prTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval readonly : " + (prTemplate != null ? prTemplate.getApprovalReadOnly() : "" ));
		}
		if (prTemplate != null) {
			return prTemplate.getApprovalReadOnly(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("prTemplate is null");
			}
		}
		return false;
	}

	public static boolean required(PrTemplate prTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval Optinal : " + (prTemplate != null ? prTemplate.getApprovalOptional() : "" ));
		}
		if (prTemplate != null) {
			return !prTemplate.getApprovalOptional(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("prTemplate is null");
			}
		}
		return false;
	}

	public static boolean visibility(RfxTemplate rfxTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval visibility : " + (rfxTemplate != null ? rfxTemplate.getApprovalVisible() : "" ));
		}
		if (rfxTemplate != null) {
			return rfxTemplate.getApprovalVisible(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("rfxTemplate is null");
			}
		}
		return true;
	}

	public static boolean readonly(RfxTemplate rfxTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval readonly : " + (rfxTemplate != null ? rfxTemplate.getApprovalReadOnly() : "" ));
		}
		if (rfxTemplate != null) {
			return rfxTemplate.getApprovalReadOnly(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("rfxTemplate is null");
			}
		}
		return false;
	}

	public static boolean required(RfxTemplate rfxTemplate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking Approval Optinal : " + (rfxTemplate != null ? rfxTemplate.getApprovalOptional() : "" ));
		}
		if (rfxTemplate != null) {
			return !rfxTemplate.getApprovalOptional(); 
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("rfxTemplate is null");
			}
		}
		return false;
	}
	
	public static String defaultValue(List<SourcingTemplateField> list, SourcingTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking default value for field : " + field);
		}
		if (list != null) {
			for (SourcingTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getDefaultValue();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return "";
	}
	
	public static boolean visibility(List<SourcingTemplateField> list, SourcingTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking visibility for field : " + field);
		}
		if (list != null) {
			for (SourcingTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return tf.getVisible();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template function is null : " + list);
			}
		}
		return true;
	}

	public static boolean readonly(List<SourcingTemplateField> list, SourcingTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking readonly for field : " + field);
		}
		if (list != null) {
			for (SourcingTemplateField tf : list) {
				if (tf.getFieldName() == field) {
					return tf.getReadOnly();
				}
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return false;
	}

	public static boolean required(List<SourcingTemplateField> list, SourcingTemplateFieldName field) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking required for field : " + field);
		}
		if (list != null) {
			for (SourcingTemplateField tf : list) {
				if (tf.getFieldName().equals(field)) {
					return !tf.getOptional();
				}
			}
			return false;
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("List in Template Function is null : " + list);
			}
		}
		return true;
	}
	

}
