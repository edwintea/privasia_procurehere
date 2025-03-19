/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author jayshree
 */
public interface PaymentTermsService {

	/**
	 * @param paymentTermes
	 */
	public void savePaymentTermes(PaymentTermes paymentTermes);

	/**
	 * @param paymentTermes
	 */
	public void updatePaymentTermes(PaymentTermes paymentTermes);

	/**
	 * @param paymentTermes
	 */
	public void deletePaymentTermes(PaymentTermes paymentTermes);

	/**
	 * @param paymentTermes
	 * @param tenantId
	 * @return
	 */
	boolean isExists(PaymentTermes paymentTermes, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	PaymentTermes getPaymentTermesById(String id);

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
	List<PaymentTermes> getAllActivePaymentTermesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */

	public List<PaymentTermes> getAllPaymentTermesByTenantId(String tenantId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void paymentTermesDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) throws FileNotFoundException, IOException;

	/**
	 * @param tenantId
	 * @return
	 */
	List<PaymentTermes> getActivePaymentTermesByTenantId(String tenantId);

	/**
	 * @param paymentTermes
	 * @param tenantId
	 * @return
	 */
	PaymentTermes getByPaymentTermes(String paymentTermes, String tenantId);
}
