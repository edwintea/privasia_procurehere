package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author shubham
 */
//@Entity
//@Table(name = "PROC_BUDGET_TRANSACTION_LOGS")
public class BudgetTransactionLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7960967277344846982L;
	/*
	 * @Id
	 * @GenericGenerator(name = "idGen", strategy = "uuid.hex")
	 * @GeneratedValue(generator = "idGen")
	 * @Column(name = "ID", length = 64) private String id;
	 * @Column(name = "REFERENCE_NUMBER", length = 64, nullable = false) private String referenceNumber;
	 * @Column(name = "TRANSACTION_TIMESTAMP", length = 64, nullable = false) private Date transactionTimeStamp;
	 * @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	 * @JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true) private BusinessUnit businessUnit;
	 * @ManyToOne(fetch = FetchType.LAZY, optional = true)
	 * @JoinColumn(name = "COST_CENTER", nullable = true) private CostCenter costCenter;
	 * @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 6) private BigDecimal newAmount;
	 * @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 6) private BigDecimal addAmount;
	 * @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 6) private BigDecimal deductAmount;
	 * @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 6) private BigDecimal transferAmount;
	 * @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 6) private BigDecimal purchaseOrderAmount;
	 */}
