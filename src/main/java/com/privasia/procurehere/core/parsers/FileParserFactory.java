/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import com.privasia.procurehere.core.utils.Global;

/**
 * @author Ravi
 */
public class FileParserFactory {

	/**
	 * @param type Excel Parser Type
	 * @return parser instance
	 */
	@SuppressWarnings("rawtypes")
	public static FileParser getParserForType(String type) {
		switch (type) {
		case Global.ACL_LIST_EXCEL_PARSER:
			return new AclListParser();
		case Global.INDUSTRY_CATEGORY_EXCEL_PARSER:
			return new NaicsCodesParser();
		case Global.COUNTRY_EXCEL_PARSER:
			return new CountryParser();
		case Global.STATE_EXCEL_PARSER:
			return new StateParser();
		case Global.TIMEZONE_EXCEL_PARSER:
			return new TimeZoneParser();
		case Global.CURRENCY_EXCEL_PARSER:
			return new CurrencyParser();
		case Global.COMPANYSTATUS_EXCEL_PARSER:
			return new CompanyStatusParser();
		case Global.UOM_EXCEL_PARSER:
			return new UomParser();
		default:
			throw new IllegalArgumentException("Invalid/Unsupported file parser type requested : " + type);
		}
	}
}
