/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_BQ_REQ")
public class SourcingFormRequestBq implements Serializable {

	private static final long serialVersionUID = 4646169699940304298L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "NAME", length = 128)
	private String name;

	@Column(name = "DESCRIPTION", length = 550)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_REQ_ID", foreignKey = @ForeignKey(name = "FK_SF_BQ_FORM_REQ_ID"))
	private SourcingFormRequest sourcingFormRequest;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bq", orphanRemoval = true)
	@OrderBy("level,order")
	private List<SourcingFormRequestBqItem> bqItems;

	@Column(name = "FIELD1_LABEL", nullable = true, length = 32)
	private String field1Label;

	@Column(name = "FIELD2_LABEL", nullable = true, length = 32)
	private String field2Label;

	@Column(name = "FIELD3_LABEL", nullable = true, length = 32)
	private String field3Label;

	@Column(name = "FIELD4_LABEL", nullable = true, length = 32)
	private String field4Label;

	@Column(name = "FIELD5_LABEL", nullable = true, length = 32)
	private String field5Label;

	@Column(name = "FIELD6_LABEL", nullable = true, length = 32)
	private String field6Label;

	@Column(name = "FIELD7_LABEL", nullable = true, length = 32)
	private String field7Label;

	@Column(name = "FIELD8_LABEL", nullable = true, length = 32)
	private String field8Label;

	@Column(name = "FIELD9_LABEL", nullable = true, length = 32)
	private String field9Label;

	@Column(name = "FIELD10_LABEL", nullable = true, length = 32)
	private String field10Label;

	@Column(name = "BQ_ORDER")
	private Integer bqOrder;

	public SourcingFormRequestBq() {
	}

	public SourcingFormRequestBq(String id, String name, String description, Integer bqOrder, Date createdDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.bqOrder = bqOrder;
		this.createdDate = createdDate;
	}

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the sourcingFormRequest
	 */
	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	/**
	 * @param sourcingFormRequest the sourcingFormRequest to set
	 */
	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
	}

	/**
	 * @return the bqItems
	 */
	public List<SourcingFormRequestBqItem> getBqItems() {
		return bqItems;
	}

	/**
	 * @param bqItems the bqItems to set
	 */
	public void setBqItems(List<SourcingFormRequestBqItem> bqItems) {
		this.bqItems = bqItems;
	}

	/**
	 * @return the field1Label
	 */
	public String getField1Label() {
		return field1Label;
	}

	/**
	 * @param field1Label the field1Label to set
	 */
	public void setField1Label(String field1Label) {
		this.field1Label = field1Label;
	}

	/**
	 * @return the field2Label
	 */
	public String getField2Label() {
		return field2Label;
	}

	/**
	 * @param field2Label the field2Label to set
	 */
	public void setField2Label(String field2Label) {
		this.field2Label = field2Label;
	}

	/**
	 * @return the field3Label
	 */
	public String getField3Label() {
		return field3Label;
	}

	/**
	 * @param field3Label the field3Label to set
	 */
	public void setField3Label(String field3Label) {
		this.field3Label = field3Label;
	}

	/**
	 * @return the field4Label
	 */
	public String getField4Label() {
		return field4Label;
	}

	/**
	 * @param field4Label the field4Label to set
	 */
	public void setField4Label(String field4Label) {
		this.field4Label = field4Label;
	}

	/**
	 * @return the field5Label
	 */
	public String getField5Label() {
		return field5Label;
	}

	/**
	 * @param field5Label the field5Label to set
	 */
	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	/**
	 * @return the field6Label
	 */
	public String getField6Label() {
		return field6Label;
	}

	/**
	 * @param field6Label the field6Label to set
	 */
	public void setField6Label(String field6Label) {
		this.field6Label = field6Label;
	}

	/**
	 * @return the field7Label
	 */
	public String getField7Label() {
		return field7Label;
	}

	/**
	 * @param field7Label the field7Label to set
	 */
	public void setField7Label(String field7Label) {
		this.field7Label = field7Label;
	}

	/**
	 * @return the field8Label
	 */
	public String getField8Label() {
		return field8Label;
	}

	/**
	 * @param field8Label the field8Label to set
	 */
	public void setField8Label(String field8Label) {
		this.field8Label = field8Label;
	}

	/**
	 * @return the field9Label
	 */
	public String getField9Label() {
		return field9Label;
	}

	/**
	 * @param field9Label the field9Label to set
	 */
	public void setField9Label(String field9Label) {
		this.field9Label = field9Label;
	}

	/**
	 * @return the field10Label
	 */
	public String getField10Label() {
		return field10Label;
	}

	/**
	 * @param field10Label the field10Label to set
	 */
	public void setField10Label(String field10Label) {
		this.field10Label = field10Label;
	}

	/**
	 * @return the bqOrder
	 */
	public Integer getBqOrder() {
		return bqOrder;
	}

	/**
	 * @param bqOrder the bqOrder to set
	 */
	public void setBqOrder(Integer bqOrder) {
		this.bqOrder = bqOrder;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public List<SourcingFormRequestBq> copyBq(SourcingFormRequest oldRequest, SourcingFormRequestBqDao bqDao, SourcingFormRequestBqItemDao bqItemDao, UomDao uomDao) {
		List<SourcingFormRequestBq> bqList = new ArrayList<>();

		for (SourcingFormRequestBq bq : oldRequest.getSourcingRequestBqs()) {
			SourcingFormRequestBq bq1 = createShallowBq(bq);
			bq1 = bqDao.save(bq1);
			SourcingFormRequestBqItem bqItem = new SourcingFormRequestBqItem();
			List<SourcingFormRequestBqItem> bqItemlist = bqItem.copyBqItem(bq, bqItemDao, uomDao);
			for (SourcingFormRequestBqItem newbqItem : bqItemlist) {
				newbqItem.setBq(bq1);
			}
			bq1.setBqItems(bqItemlist);
			bq1 = bqDao.saveOrUpdate(bq1);
			bqList.add(bq1);

		}
		return bqList;
	}

	public SourcingFormRequestBq createShallowBq(SourcingFormRequestBq bq) {
		SourcingFormRequestBq bq1 = new SourcingFormRequestBq();
		bq1.setCreatedDate(new Date());
		bq1.setDescription(bq.getDescription());
		bq1.setName(bq.getName());
		bq1.setBqOrder(bq.getBqOrder());
		bq1.setField1Label(bq.getField1Label());
		bq1.setField2Label(bq.getField2Label());
		bq1.setField3Label(bq.getField3Label());
		bq1.setField4Label(bq.getField4Label());
		bq1.setField5Label(bq.getField5Label());
		bq1.setField6Label(bq.getField6Label());
		bq1.setField7Label(bq.getField7Label());
		bq1.setField8Label(bq.getField8Label());
		bq1.setField9Label(bq.getField9Label());
		bq1.setField10Label(bq.getField10Label());
		return bq1;
	}

	public SourcingFormRequestBq createMobileShallowCopy() {

		SourcingFormRequestBq bq = new SourcingFormRequestBq();
		bq.setId(getId());
		bq.setName(getName());
		bq.setBqOrder(getBqOrder());
		bq.setDescription(getDescription());
		if (CollectionUtil.isNotEmpty(getBqItems())) {
			bq.setBqItems(new ArrayList<>());
			for (SourcingFormRequestBqItem bqItem : getBqItems()) {
				bq.getBqItems().add(bqItem.createMobileShallowCopy());
			}
		}
		return bq;
	}
}
