/**
 * 
 */
package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 * @author vipul
 */
public interface PromotionalCodeService {
	/**
	 * @param promoCode
	 * @return
	 * @throws Exception
	 */
	PromotionalCode getPromotionalCodeByPromoCode(String promoCode) throws Exception;

	/**
	 * @param promoCodeId
	 * @return
	 */
	PromotionalCode getPromotionalCodeByPromoCodeId(String promoCodeId);

	/**
	 * @param promotionalCode
	 * @return
	 */
	boolean isExists(PromotionalCode promotionalCode);

	/**
	 * @param promotionalCode
	 */
	String savePromotionCode(PromotionalCode promotionalCode);

	/**
	 * @param id
	 * @return
	 */
	PromotionalCode getPromotionalCodeById(String id);

	/**
	 * @param persistObj
	 */
	void updatePromotionalCode(PromotionalCode persistObj, String tenantId, User createdBy);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<PromotionalCode> findPromotionCodeForTenant(TableDataInput tableParams);

	/**
	 * @param input
	 * @return
	 */
	long findTotalFilteredPromotionalCodeList(TableDataInput tableParams);

	/**
	 * 
	 */
	long findTotalPromotionalCodeList();

	/**
	 * @param promotionalCode
	 */
	void deletePromotionalCode(PromotionalCode promotionalCode, String tenantId, User createdBy);

	/**
	 * @param promoCodeId
	 */
	void updatePromoCode(String promoCodeId);

	/**
	 * @param promoCode
	 * @param loggedInTenantId TODO
	 * @return
	 * @throws Exception
	 */
	PromotionalCode checkPromotionalCodeByPromoCode(String promoCode, String loggedInTenantId) throws Exception;

	/**
	 * @param promoCode
	 * @return
	 * @throws Exception
	 */
	PromotionalCode getPromotionalCodePromoCode(String promoCode) throws Exception;

	/**
	 * @param promoCode
	 * @param sp
	 * @param bp
	 * @param tenantType TODO
	 * @param tenantId TODO
	 * @return
	 * @throws Exception
	 */
	PromotionalCode validatePromoCode(String promoCode, SupplierPlan sp, BuyerPlan bp, BigDecimal price, TenantType tenantType, String tenantId) throws Exception;

}
