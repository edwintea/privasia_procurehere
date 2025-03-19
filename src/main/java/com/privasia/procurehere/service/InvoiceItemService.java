package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.exceptions.EmailException;

/**
 * @author ravi
 */
public interface InvoiceItemService {

	/**
	 * @param item
	 * @return
	 */
	InvoiceItem updateItem(InvoiceItem item);

	/**
	 * @param doItemId
	 * @return
	 */
	InvoiceItem findById(String doItemId);

	/**
	 * @param items
	 * @param additionalTax
	 * @param footer
	 * @param invoiceStatus
	 * @param loggedInUser
	 * @param referenceNumber
	 * @param deliveryAddress
	 * @param deliveryReceiver
	 * @param attentionTo
	 * @param correspondenceAddress
	 * @param dueDate
	 * @param invoiceName
	 * @param session
	 * @param includeDelievryAdress TODO
	 * @param requestForFinance TODO
	 * @return
	 * @throws EmailException 
	 */
	Invoice updateItems(List<InvoiceItem> items, BigDecimal additionalTax, String footer, InvoiceStatus invoiceStatus, User loggedInUser, String referenceNumber, String deliveryAddress, String deliveryReceiver, String attentionTo, String correspondenceAddress, String dueDate, String invoiceName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, boolean includeDelievryAdress, Boolean requestForFinance) throws EmailException;

	/**
	 * @param itemId
	 * @return
	 */
	public InvoiceItem getItemById(String itemId);
}