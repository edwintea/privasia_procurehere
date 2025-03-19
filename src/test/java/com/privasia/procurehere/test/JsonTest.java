/**
 * 
 */
package com.privasia.procurehere.test;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.PricingTypes;

/**
 * @author Nitin Otageri
 */
public class JsonTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			// RfaSupplierBqItem

			BqItem item = new BqItem();
			item.setBqId("1233");
			item.setAdditionalTax(BigDecimal.ONE);
			item.setItemName("Item 1");
			item.setItemDescription("Desc of Item 1");
			item.setLevel(1);
			item.setOrder(2);
			Uom uom = new Uom();
			uom.setId("2334234");
			uom.setUom("CMS");
			uom.setUomDescription("Centemeters");
			item.setUom(uom);
			item.setQuantity(BigDecimal.TEN);
			item.setUnitPrice(BigDecimal.ONE);
			item.setPriceType(PricingTypes.TRADE_IN_PRICE);
			item.setTotalAmount(BigDecimal.TEN);

			String json = mapper.writeValueAsString(item);
			System.out.println("Serialize : " + json);

			item = mapper.readValue(json, BqItem.class);
			System.out.println("Deserialize : " + item.toLogString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
