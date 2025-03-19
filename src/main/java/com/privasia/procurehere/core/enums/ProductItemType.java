/**
 * 
 */
package com.privasia.procurehere.core.enums;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public enum ProductItemType {
	MATERIAL, SERVICE;
	
	public static ProductItemType fromString(String value) {
		try {
			for (ProductItemType itemType : ProductItemType.values()) {
				if (StringUtils.checkString(value).equals(itemType.toString())) {
					return itemType;
				}
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Product Item Type : " + value);
		}
	}
}
