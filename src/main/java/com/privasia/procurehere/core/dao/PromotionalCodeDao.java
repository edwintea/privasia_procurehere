package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface PromotionalCodeDao extends GenericDao<PromotionalCode, String> {
	/**
	 * @param promoCode
	 * @return
	 * @throws Exception
	 */
	PromotionalCode getPromotionalCodeByPromoCode(String promoCode) throws Exception;

	/**
	 * @param promotionalCode
	 * @return
	 */
	boolean isExists(PromotionalCode promotionalCode);

	/**
	 * @param tableParams
	 * @return
	 */
	List<PromotionalCode> findAllPromotionCodeForTenant(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredPromotionalCodeList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalPromotionalCodeList();

	/**
	 * @param id
	 * @return
	 */
	PromotionalCode findPromotionalCodeById(String id);

	/**
	 * @param promoCodeId
	 */
	void updatePromoCode(String promoCodeId);

	/**
	 * @param promoCode
	 * @return
	 * @throws Exception
	 */
	PromotionalCode checkPromotionalCodeByPromoCode(String promoCode) throws Exception;

	/**
	 * @param id
	 * @param buyerId TODO
	 * @return TODO
	 * @throws Exception
	 */
	boolean checkPromoCodeUseByBuyer(String id, String buyerId) throws Exception;

	/**
	 * @param promoCode
	 * @return
	 */
	PromotionalCode findPromoCodeByName(String promoCode) throws Exception;

	/**
	 * @param id
	 * @return
	 */
	long findCountOfSupplierPromoCodeById(String id);

}
