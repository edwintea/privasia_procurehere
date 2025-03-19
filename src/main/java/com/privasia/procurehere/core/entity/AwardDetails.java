package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.DecimalMax;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.TaxType;

/**
 * @author Priyanka Singh
 */

@MappedSuperclass
public class AwardDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2768739369575468045L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "EVENT_AWARD_ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID")
	private Supplier supplier;

	@DecimalMax("99999999999999.999999")
	@Column(name = "AWARDED_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal awardedPrice;

	@DecimalMax("9999999999.999999")
	@Column(name = "ORIGINAL_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal originalPrice;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "TAX", precision = 18, scale = 6, nullable = true)
	private BigDecimal tax;

	@Enumerated(EnumType.STRING)
	@Column(name = "TAX_TYPE")
	private TaxType taxType;

	@DecimalMax("99999999999999.999999")
	@Column(name = "TOTAL_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal totalPrice;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the awardedPrice
	 */
	public BigDecimal getAwardedPrice() {
		return awardedPrice;
	}

	/**
	 * @param awardedPrice the awardedPrice to set
	 */
	public void setAwardedPrice(BigDecimal awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * @return the taxType
	 */
	public TaxType getTaxType() {
		return taxType;
	}

	/**
	 * @param taxType the taxType to set
	 */
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	/**
	 * @return the totalPrice
	 */
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the originalPrice
	 */
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	/**
	 * @param originalPrice the originalPrice to set
	 */
	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	@Override
	public String toString() {
		return "AwardDetails [id=" + id + ", supplier=" + supplier + ", awardedPrice=" + awardedPrice + ", originalPrice=" + originalPrice + ", tax=" + tax + ", taxType=" + taxType + ", totalPrice=" + totalPrice + "]";
	}
	
}
