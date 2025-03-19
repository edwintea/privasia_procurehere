/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author jayshree
 *
 */
public interface PaymentTermsDao extends GenericDao<PaymentTermes, String> {

	/**
	 * @param paymentTermes
	 * @param tenantId
	 * @return
	 */
	boolean isExists(PaymentTermes paymentTermes, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<PaymentTermes> getAllActivePaymentTermesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public List<PaymentTermes> findPaymentTermesForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public long findTotalFilteredPaymentTermesForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	public long findTotalPaymentTermesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */

	public List<PaymentTermes> getAllPaymentTermesByTenantId(String tenantId);

	/**
	 * @param paymentTermes
	 * @param tenantId TODO
	 * @return
	 */
	PaymentTermes getByPaymentTermes(String paymentTermes, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<PaymentTermes> getActivePaymentTermesByTenantId(String tenantId);

	/**
	 * 
	 * @param description
	 * @param tenantId
	 * @return
	 */
	PaymentTermes getByPaymentTermsByDesc(String description, String tenantId);

}
