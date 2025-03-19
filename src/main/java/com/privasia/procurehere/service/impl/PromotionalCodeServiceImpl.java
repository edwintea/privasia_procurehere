/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.PromotionalCodeDao;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UsageLimitType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PromotionalCodeService;

/**
 * @author parveen
 * @author vipul
 */
@Service
@Transactional(readOnly = true)
public class PromotionalCodeServiceImpl implements PromotionalCodeService {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@Autowired
	PromotionalCodeDao promotionalCodeDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Override
	public PromotionalCode getPromotionalCodeByPromoCode(String promoCode) throws Exception {
		PromotionalCode promotionalCode = promotionalCodeDao.getPromotionalCodeByPromoCode(promoCode);
		if (promotionalCode != null) {
			if (promotionalCode.getUsageLimit() != null) {
				if (promotionalCode.getUsageCount() != null && promotionalCode.getUsageCount() >= promotionalCode.getUsageLimit()) {
					throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
				}
			}

			if (promotionalCode.getSupplierPlan() != null) {
				promotionalCode.getSupplierPlan().getId();
			}

			if (promotionalCode.getBuyerPlan() != null) {
				promotionalCode.getBuyerPlan().getId();
			}
		}

		// promotionalCode.setCreatedBy(null);
		// promotionalCode.setBuyer(null);
		return promotionalCode;
	}

	@Override
	public PromotionalCode getPromotionalCodeByPromoCodeId(String promoCodeId) {
		return promotionalCodeDao.findById(promoCodeId);
	}

	@Override
	public boolean isExists(PromotionalCode promotionalCode) {
		return promotionalCodeDao.isExists(promotionalCode);
	}

	@Override
	@Transactional(readOnly = false)
	public String savePromotionCode(PromotionalCode promotionalCode) {
		OwnerAuditTrail auditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + promotionalCode.getPromoName() + "' Promotional Code created", promotionalCode.getCreatedBy().getTenantId(), promotionalCode.getCreatedBy(), new Date(), ModuleType.PromotionalCode);
		ownerAuditTrailDao.save(auditTrail);
		promotionalCode = promotionalCodeDao.saveOrUpdate(promotionalCode);
		return (promotionalCode != null ? promotionalCode.getId() : null);
	}

	@Override
	public PromotionalCode getPromotionalCodeById(String id) {
		PromotionalCode promotionalCode = promotionalCodeDao.findById(id);
		if (promotionalCode != null) {
			if (promotionalCode.getBuyer() != null)
				promotionalCode.getBuyer().getCompanyName();
			if (promotionalCode.getSupplier() != null)
				promotionalCode.getSupplier().getCompanyName();
			if (promotionalCode.getBuyerPlan() != null)
				promotionalCode.getBuyerPlan().getPlanName();
			if (promotionalCode.getSupplierPlan() != null)
				promotionalCode.getSupplierPlan().getPlanName();

		}
		return promotionalCode;
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePromotionalCode(PromotionalCode persistObj, String tenantId, User createdBy) {
		OwnerAuditTrail auditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + persistObj.getPromoName() + "' Promotional Code updated", tenantId, createdBy, new Date(), ModuleType.PromotionalCode);
		ownerAuditTrailDao.save(auditTrail);
		promotionalCodeDao.update(persistObj);

	}

	@Override
	@Transactional(readOnly = false)
	public void deletePromotionalCode(PromotionalCode promotionalCode, String tenantId, User createdBy) {
		String promoName = promotionalCode.getPromoName();
		OwnerAuditTrail auditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + promoName + "' Promotional Code deleted", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PromotionalCode);
		ownerAuditTrailDao.save(auditTrail);
		promotionalCodeDao.delete(promotionalCode);
	}

	@Override
	public List<PromotionalCode> findPromotionCodeForTenant(TableDataInput tableParams) {
		return promotionalCodeDao.findAllPromotionCodeForTenant(tableParams);
	}

	@Override
	public long findTotalFilteredPromotionalCodeList(TableDataInput tableParams) {
		return promotionalCodeDao.findTotalFilteredPromotionalCodeList(tableParams);
	}

	@Override
	public long findTotalPromotionalCodeList() {
		return promotionalCodeDao.findTotalPromotionalCodeList();
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePromoCode(String promoCodeId) {
		promotionalCodeDao.updatePromoCode(promoCodeId);
	}

	@Override
	public PromotionalCode checkPromotionalCodeByPromoCode(String promoCode, String loggedInTenantId) throws Exception {
		PromotionalCode promotionalCode = promotionalCodeDao.checkPromotionalCodeByPromoCode(promoCode);
		if (promotionalCode != null) {
			if (promotionalCode.getUsageLimit() != null) {
				if (promotionalCode.getUsageCount() != null && promotionalCode.getUsageCount() >= promotionalCode.getUsageLimit()) {
					throw new ApplicationException("This Promo Code '" + promoCode + "' has reached its usage limit");
				}
			}

			if (promotionalCode.getBuyer() != null && !loggedInTenantId.equals(promotionalCode.getBuyer().getId())) {
				throw new ApplicationException("This Promo Code '" + promoCode + "' is not valid for you");
			}

			if (UsageLimitType.SINGLE == promotionalCode.getUsageLimitType()) {
				// check in payment transactions buyer already use Promo code
				if (promotionalCodeDao.checkPromoCodeUseByBuyer(promotionalCode.getId(), loggedInTenantId)) {
					throw new ApplicationException("This Promo Code '" + promoCode + "' has already been used once before by you");
				}
			}

			promotionalCode.setCreatedBy(null);
			promotionalCode.setBuyer(null);
		}
		return promotionalCode;

	}

	@Override
	public PromotionalCode getPromotionalCodePromoCode(String promoCode) throws Exception {
		// PromotionalCode promotionalCode = new PromotionalCode();
		PromotionalCode promotionalCode = promotionalCodeDao.getPromotionalCodeByPromoCode(promoCode);
		// promotionalCode.createShallowCopy(promotionalCode1);
		if (promotionalCode != null) {
			if (promotionalCode.getUsageLimit() != null) {
				if (promotionalCode.getUsageCount() != null && promotionalCode.getUsageCount() >= promotionalCode.getUsageLimit()) {
					throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
				}
			}
		}

		// promotionalCode.setCreatedBy(null);
		// promotionalCode.setBuyer(null);
		return promotionalCode;
	}

	@Override
	public PromotionalCode validatePromoCode(String promoCode, SupplierPlan sp, BuyerPlan bp, BigDecimal price, TenantType tenantType, String tenantId) throws Exception {
		try {
			PromotionalCode code = null;
			if (promoCode != null) {
				LOG.info("Fetching promo code for validatePromoCode : " + promoCode);
				code = promotionalCodeDao.findPromoCodeByName(promoCode);
				if (code != null) {
					LOG.info("Found promo code as : " + code.getPromoName() + " and total price: " + price);

					if (tenantType == TenantType.SUPPLIER) {
						if (code.getSupplierPlan() != null) {
							if (sp != null) {
								if (StringUtils.equals(code.getSupplierPlan().getId(), sp.getId())) {
									LOG.info("Code is applicable for supplier plan...");
								}
							} else {
								throw new ApplicationException("Promo code " + code.getPromoName() + " is not applicable for selected plan");
							}
							LOG.info("Finished validating supplier");
						}

						if (code.getSupplier() != null && !StringUtils.checkString(tenantId).equals(code.getSupplier().getId())) {
							throw new ApplicationException("This Promo Code '" + promoCode + "' is not valid for you");
						}

					}

					if (tenantType == TenantType.BUYER) {
						if (code.getBuyerPlan() != null) {
							if (bp != null) {
								if (StringUtils.equals(code.getBuyerPlan().getId(), bp.getId())) {
									LOG.info("Code is applicable for buyer plan");
								}

							} else {
								throw new ApplicationException("Promo code " + code.getPromoName() + " is not applicable for selected plan");
							}

						}
						LOG.info("Finished validating buyer");

						if (code.getBuyer() != null && !StringUtils.checkString(tenantId).equals(code.getBuyer().getId())) {
							throw new ApplicationException("This Promo Code '" + promoCode + "' is not valid for you");
						}

					}

					if (price != null) {
						BigDecimal promoDiscountPrice = BigDecimal.ZERO;
						if (code.getDiscountType() == ValueType.VALUE) {
							promoDiscountPrice = new BigDecimal(code.getPromoDiscount());
						} else {
							Integer discountPer = code.getPromoDiscount();
							if (discountPer != null) {
								promoDiscountPrice = price.multiply(new BigDecimal(discountPer)).setScale(2, BigDecimal.ROUND_HALF_UP);
								promoDiscountPrice = promoDiscountPrice.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
							}
						}
						BigDecimal totalPrice = price.subtract(promoDiscountPrice);

						if (totalPrice.compareTo(new BigDecimal(-1)) == -1 || totalPrice.compareTo(new BigDecimal(-1)) == 0) {
							throw new ApplicationException("This promo code is not applicable.");
						}

						LOG.info("Finished validating price");
					}

					if (code.getUsageLimit() != null) {
						if (code.getUsageCount() != null && code.getUsageCount() >= code.getUsageLimit()) {
							LOG.error("Failed validation of useage count.....");
							throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
						}
					}

				} else {
					LOG.info("Invalid promo code for validatePromoCode : " + promoCode);
					throw new ApplicationException("Invalid promo code provided.");
				}
			}
			return code;
		} catch (Exception e) {
			LOG.error("Error while fetching promo code: " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
	}

}
