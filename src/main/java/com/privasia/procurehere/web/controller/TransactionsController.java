/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/owner")
public class TransactionsController implements Serializable {

	private static final long serialVersionUID = 841146981826854124L;

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@RequestMapping(value = "/transaction", method = RequestMethod.GET)
	public String viewTransactions(Model model) {
		return "transaction";
	}

}
