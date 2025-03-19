package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.ErpIntegrationTypeForAward;
import com.privasia.procurehere.core.enums.ErpIntegrationTypeForPr;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_ERP_SETUP")
@SqlResultSetMapping(name = "erpEventResult", classes = { @ConstructorResult(targetClass = MobileEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventId"), @ColumnResult(name = "eventType"), @ColumnResult(name = "status") }) })
public class ErpSetup implements Serializable {

	private static final long serialVersionUID = -122044561120426239L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "TENANT_ID", length = 64)
	private String tenantId;

	@Column(name = "IS_ERP_ENABLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isErpEnable = Boolean.FALSE;

	@Column(name = "ERP_URL", length = 500)
	private String erpUrl;

	@Column(name = "ERP_USERNAME", length = 500)
	private String erpUsername;

	@Column(name = "ERP_PASSWORD", length = 500)
	private String erpPassword;

	@Column(name = "EVENT_TYPE")
	@Convert(converter = RfxTypesCoverter.class)
	private RfxTypes type;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RFA_TEMPLATE_ID", nullable = true)
	private RfxTemplate rfaTemplate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RFT_TEMPLATE_ID", nullable = true)
	private RfxTemplate rftTemplate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RFP_TEMPLATE_ID", nullable = true)
	private RfxTemplate rfpTemplate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RFQ_TEMPLATE_ID", nullable = true)
	private RfxTemplate rfqTemplate;

	@Column(name = "APP_ID", length = 150)
	private String appId;

	@Column(name = "IS_GENERATE_PO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isGeneratePo = Boolean.TRUE;

	@Column(name = "CREATE_EVENT_AUTO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean createEventAuto = Boolean.TRUE;

	@Transient
	private RfxTemplate rfxTemplate;

	@Column(name = "ERP_SEQ_NO", length = 3)
	private Integer erpSeqNo;

	@Column(name = "ERP_SEQ_MONTH", length = 2)
	private Integer erpSeqMonth = 0;

	@Column(name = "AWARD_INTERFACE_TYPE_PULL")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean awardInterfaceTypePull = Boolean.FALSE;

	@Column(name = "ENABLE_UOM_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableUomApi = Boolean.FALSE;

	@Column(name = "ENABLE_PRODUCT_ITEM_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableProductItemApi = Boolean.FALSE;

	@Column(name = "ENABLE_PRODUCT_CAT_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableProductCategoryApi = Boolean.FALSE;

	@Column(name = "ENABLE_ITEM_CONTRACT_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableContractApi = Boolean.FALSE;

	@Column(name = "ENABLE_PO_CREATE_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enablePoCreateApi = Boolean.FALSE;

	@Column(name = "ENABLE_GRN_CREATE_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableGrnCreateApi = Boolean.FALSE;

	@Column(name = "ENABLE_DO_CREATE_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableDoCreateApi = Boolean.FALSE;

	@Column(name = "ENABLE_INV_CREATE_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableInvoiceCreateApi = Boolean.FALSE;

	@Column(name = "ENABLE_SAP_PR_RESPONSE_API")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSapPrResponseApi = Boolean.FALSE;

	@Column(name = "ENABLE_RFS_ERP_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableRfsErpPush = Boolean.FALSE;

	@Column(name = "ENABLE_SUPLIER_PERFORM_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSupplierPerformanceErpPush = Boolean.FALSE;

	@Column(name = "ENABLE_AWARD_ERP_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardErpPush = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "PR_INTEGRATION_TYPE", nullable = true, length = 50)
	private ErpIntegrationTypeForPr erpIntegrationTypeForPr;

	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_INTEGRATION_TYPE", nullable = true, length = 50)
	private ErpIntegrationTypeForAward erpIntegrationTypeForAward;

	@Column(name = "SMS_URL", length = 150)
	private String smsUrl;

	@Column(name = "SMS_USER_NAME", length = 150)
	private String smsUsername;

	@Column(name = "SMS_PASSWORD", length = 150)
	private String smsPassword;

	@Column(name = "SMS_FROM", length = 150)
	private String smsFrom;

	@Column(name = "FAX_PORT", length = 150)
	private String faxPort;// 587 it should be comma seprated

	@Column(name = "FAX_HOST", length = 150)
	private String faxHost;// 58.27.17.174

	@Column(name = "FAX_DOMAIN", length = 150)
	private String faxDomain;// @faxcore.iwk.com.my

	@Column(name = "FAX_USERNAME", length = 150)
	private String faxUsername;

	@Column(name = "FAX_PASSWPRD", length = 150)
	private String faxPassword;

	@Column(name = "FAX_FORM", length = 150)
	private String faxFrom; // procurement@iwk.com.my

	@Column(name = "ENABLE_PO_ACC_DECL_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enablePoAcceptDeclinePush = Boolean.FALSE;

	@Column(name = "ENABLE_CONTRACT_ERP_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean enableContractErpPush = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ERP_CREATOR_ID", nullable = true)
	private User erpCreator;

	@Column(name = "ENABLE_BINARY_FILE_PUSH")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean enableBinaryFilePush = Boolean.FALSE;

	@Column(name = "ENABLE_PO_SEND_TO_SAP")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean enablePoSendToSap = Boolean.FALSE;

	public Integer getErpSeqNo() {
		return erpSeqNo;
	}

	public void setErpSeqNo(Integer erpSeqNo) {
		this.erpSeqNo = erpSeqNo;
	}

	public Integer getErpSeqMonth() {
		return erpSeqMonth;
	}

	public void setErpSeqMonth(Integer erpSeqMonth) {
		this.erpSeqMonth = erpSeqMonth;
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
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the isErpEnable
	 */
	public Boolean getIsErpEnable() {
		return isErpEnable;
	}

	/**
	 * @param isErpEnable the isErpEnable to set
	 */
	public void setIsErpEnable(Boolean isErpEnable) {
		this.isErpEnable = isErpEnable;
	}

	/**
	 * @return the erpUrl
	 */
	public String getErpUrl() {
		return erpUrl;
	}

	/**
	 * @param erpUrl the erpUrl to set
	 */
	public void setErpUrl(String erpUrl) {
		this.erpUrl = erpUrl;
	}

	/**
	 * @return the type
	 */
	public RfxTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RfxTypes type) {
		this.type = type;
	}

	/**
	 * @return the rfaTemplate
	 */
	public RfxTemplate getRfaTemplate() {
		return rfaTemplate;
	}

	/**
	 * @param rfaTemplate the rfaTemplate to set
	 */
	public void setRfaTemplate(RfxTemplate rfaTemplate) {
		this.rfaTemplate = rfaTemplate;
	}

	/**
	 * @return the rftTemplate
	 */
	public RfxTemplate getRftTemplate() {
		return rftTemplate;
	}

	/**
	 * @param rftTemplate the rftTemplate to set
	 */
	public void setRftTemplate(RfxTemplate rftTemplate) {
		this.rftTemplate = rftTemplate;
	}

	/**
	 * @return the rfpTemplate
	 */
	public RfxTemplate getRfpTemplate() {
		return rfpTemplate;
	}

	/**
	 * @param rfpTemplate the rfpTemplate to set
	 */
	public void setRfpTemplate(RfxTemplate rfpTemplate) {
		this.rfpTemplate = rfpTemplate;
	}

	/**
	 * @return the rfqTemplate
	 */
	public RfxTemplate getRfqTemplate() {
		return rfqTemplate;
	}

	/**
	 * @param rfqTemplate the rfqTemplate to set
	 */
	public void setRfqTemplate(RfxTemplate rfqTemplate) {
		this.rfqTemplate = rfqTemplate;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the isGeneratePo
	 */
	public Boolean getIsGeneratePo() {
		return isGeneratePo;
	}

	/**
	 * @param isGeneratePo the isGeneratePo to set
	 */
	public void setIsGeneratePo(Boolean isGeneratePo) {
		this.isGeneratePo = isGeneratePo;
	}

	/**
	 * @return the createEventAuto
	 */
	public Boolean getCreateEventAuto() {
		return createEventAuto;
	}

	/**
	 * @param createEventAuto the createEventAuto to set
	 */
	public void setCreateEventAuto(Boolean createEventAuto) {
		this.createEventAuto = createEventAuto;
	}

	/**
	 * @return the rfxTemplate
	 */
	public RfxTemplate getRfxTemplate() {
		return rfxTemplate;
	}

	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setRfxTemplate(RfxTemplate rfxTemplate) {
		this.rfxTemplate = rfxTemplate;
	}

	public Boolean getAwardInterfaceTypePull() {
		return awardInterfaceTypePull;
	}

	public void setAwardInterfaceTypePull(Boolean awardInterfaceTypePull) {
		this.awardInterfaceTypePull = awardInterfaceTypePull;
	}

	public Boolean getEnableUomApi() {
		return enableUomApi;
	}

	public void setEnableUomApi(Boolean enableUomApi) {
		this.enableUomApi = enableUomApi;
	}

	public Boolean getEnableProductItemApi() {
		return enableProductItemApi;
	}

	public void setEnableProductItemApi(Boolean enableProductItemApi) {
		this.enableProductItemApi = enableProductItemApi;
	}

	public void setEnableProductCategoryApi(Boolean enableProductCategoryApi) {
		this.enableProductCategoryApi = enableProductCategoryApi;
	}

	public ErpIntegrationTypeForPr getErpIntegrationTypeForPr() {
		return erpIntegrationTypeForPr;
	}

	public void setErpIntegrationTypeForPr(ErpIntegrationTypeForPr erpIntegrationTypeForPr) {
		this.erpIntegrationTypeForPr = erpIntegrationTypeForPr;
	}

	/**
	 * @return the enableContractApi
	 */
	public Boolean getEnableContractApi() {
		return enableContractApi;
	}

	/**
	 * @param enableContractApi the enableContractApi to set
	 */
	public void setEnableContractApi(Boolean enableContractApi) {
		this.enableContractApi = enableContractApi;
	}

	/**
	 * @return the enableSapPrResponseApi
	 */
	public Boolean getEnableSapPrResponseApi() {
		return enableSapPrResponseApi;
	}

	/**
	 * @param enableSapPrResponseApi the enableSapPrResponseApi to set
	 */
	public void setEnableSapPrResponseApi(Boolean enableSapPrResponseApi) {
		this.enableSapPrResponseApi = enableSapPrResponseApi;
	}

	/**
	 * @return the enableProductCategoryApi
	 */
	public Boolean getEnableProductCategoryApi() {
		return enableProductCategoryApi;
	}

	/**
	 * @return the erpUsername
	 */
	public String getErpUsername() {
		return erpUsername;
	}

	/**
	 * @param erpUsername the erpUsername to set
	 */
	public void setErpUsername(String erpUsername) {
		this.erpUsername = erpUsername;
	}

	/**
	 * @return the erpPassword
	 */
	public String getErpPassword() {
		return erpPassword;
	}

	/**
	 * @param erpPassword the erpPassword to set
	 */
	public void setErpPassword(String erpPassword) {
		this.erpPassword = erpPassword;
	}

	/**
	 * @return the enablePoCreateApi
	 */
	public Boolean getEnablePoCreateApi() {
		return enablePoCreateApi;
	}

	/**
	 * @param enablePoCreateApi the enablePoCreateApi to set
	 */
	public void setEnablePoCreateApi(Boolean enablePoCreateApi) {
		this.enablePoCreateApi = enablePoCreateApi;
	}

	/**
	 * @return the enableGrnCreateApi
	 */
	public Boolean getEnableGrnCreateApi() {
		return enableGrnCreateApi;
	}

	/**
	 * @param enableGrnCreateApi the enableGrnCreateApi to set
	 */
	public void setEnableGrnCreateApi(Boolean enableGrnCreateApi) {
		this.enableGrnCreateApi = enableGrnCreateApi;
	}

	/**
	 * @return the enableDoCreateApi
	 */
	public Boolean getEnableDoCreateApi() {
		return enableDoCreateApi;
	}

	/**
	 * @param enableDoCreateApi the enableDoCreateApi to set
	 */
	public void setEnableDoCreateApi(Boolean enableDoCreateApi) {
		this.enableDoCreateApi = enableDoCreateApi;
	}

	/**
	 * @return the enableInvoiceCreateApi
	 */
	public Boolean getEnableInvoiceCreateApi() {
		return enableInvoiceCreateApi;
	}

	/**
	 * @param enableInvoiceCreateApi the enableInvoiceCreateApi to set
	 */
	public void setEnableInvoiceCreateApi(Boolean enableInvoiceCreateApi) {
		this.enableInvoiceCreateApi = enableInvoiceCreateApi;
	}

	/**
	 * @return the erpIntegrationTypeForAward
	 */
	public ErpIntegrationTypeForAward getErpIntegrationTypeForAward() {
		return erpIntegrationTypeForAward;
	}

	/**
	 * @param erpIntegrationTypeForAward the erpIntegrationTypeForAward to set
	 */
	public void setErpIntegrationTypeForAward(ErpIntegrationTypeForAward erpIntegrationTypeForAward) {
		this.erpIntegrationTypeForAward = erpIntegrationTypeForAward;
	}

	/**
	 * @return the enableRfsErpPush
	 */
	public Boolean getEnableRfsErpPush() {
		return enableRfsErpPush;
	}

	/**
	 * @param enableRfsErpPush the enableRfsErpPush to set
	 */
	public void setEnableRfsErpPush(Boolean enableRfsErpPush) {
		this.enableRfsErpPush = enableRfsErpPush;
	}

	/**
	 * @return the enableAwardErpPush
	 */
	public Boolean getEnableAwardErpPush() {
		return enableAwardErpPush;
	}

	/**
	 * @param enableAwardErpPush the enableAwardErpPush to set
	 */
	public void setEnableAwardErpPush(Boolean enableAwardErpPush) {
		this.enableAwardErpPush = enableAwardErpPush;
	}

	/**
	 * @return the smsUrl
	 */
	public String getSmsUrl() {
		return smsUrl;
	}

	/**
	 * @param smsUrl the smsUrl to set
	 */
	public void setSmsUrl(String smsUrl) {
		this.smsUrl = smsUrl;
	}

	/**
	 * @return the smsUsername
	 */
	public String getSmsUsername() {
		return smsUsername;
	}

	/**
	 * @param smsUsername the smsUsername to set
	 */
	public void setSmsUsername(String smsUsername) {
		this.smsUsername = smsUsername;
	}

	/**
	 * @return the smsPassword
	 */
	public String getSmsPassword() {
		return smsPassword;
	}

	/**
	 * @param smsPassword the smsPassword to set
	 */
	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}

	/**
	 * @return the smsFrom
	 */
	public String getSmsFrom() {
		return smsFrom;
	}

	/**
	 * @param smsFrom the smsFrom to set
	 */
	public void setSmsFrom(String smsFrom) {
		this.smsFrom = smsFrom;
	}

	/**
	 * @return the faxPort
	 */
	public String getFaxPort() {
		return faxPort;
	}

	/**
	 * @param faxPort the faxPort to set
	 */
	public void setFaxPort(String faxPort) {
		this.faxPort = faxPort;
	}

	/**
	 * @return the faxHost
	 */
	public String getFaxHost() {
		return faxHost;
	}

	/**
	 * @param faxHost the faxHost to set
	 */
	public void setFaxHost(String faxHost) {
		this.faxHost = faxHost;
	}

	/**
	 * @return the faxDomain
	 */
	public String getFaxDomain() {
		return faxDomain;
	}

	/**
	 * @param faxDomain the faxDomain to set
	 */
	public void setFaxDomain(String faxDomain) {
		this.faxDomain = faxDomain;
	}

	/**
	 * @return the faxUsername
	 */
	public String getFaxUsername() {
		return faxUsername;
	}

	/**
	 * @param faxUsername the faxUsername to set
	 */
	public void setFaxUsername(String faxUsername) {
		this.faxUsername = faxUsername;
	}

	/**
	 * @return the faxPassword
	 */
	public String getFaxPassword() {
		return faxPassword;
	}

	/**
	 * @param faxPassword the faxPassword to set
	 */
	public void setFaxPassword(String faxPassword) {
		this.faxPassword = faxPassword;
	}

	/**
	 * @return the faxFrom
	 */
	public String getFaxFrom() {
		return faxFrom;
	}

	/**
	 * @param faxFrom the faxFrom to set
	 */
	public void setFaxFrom(String faxFrom) {
		this.faxFrom = faxFrom;
	}

	/**
	 * @return the enablePoAcceptDeclinePush
	 */
	public Boolean getEnablePoAcceptDeclinePush() {
		return enablePoAcceptDeclinePush;
	}

	/**
	 * @param enablePoAcceptDeclinePush the enablePoAcceptDeclinePush to set
	 */
	public void setEnablePoAcceptDeclinePush(Boolean enablePoAcceptDeclinePush) {
		this.enablePoAcceptDeclinePush = enablePoAcceptDeclinePush;
	}

	/**
	 * @return the enableSupplierPerformanceErpPush
	 */
	public Boolean getEnableSupplierPerformanceErpPush() {
		return enableSupplierPerformanceErpPush;
	}

	/**
	 * @param enableSupplierPerformanceErpPush the enableSupplierPerformanceErpPush to set
	 */
	public void setEnableSupplierPerformanceErpPush(Boolean enableSupplierPerformanceErpPush) {
		this.enableSupplierPerformanceErpPush = enableSupplierPerformanceErpPush;
	}

	public Boolean getEnableContractErpPush() {
		return enableContractErpPush;
	}

	public void setEnableContractErpPush(Boolean enableContractErpPush) {
		this.enableContractErpPush = enableContractErpPush;
	}

	public User getErpCreator() {
		return erpCreator;
	}

	public void setErpCreator(User erpCreator) {
		this.erpCreator = erpCreator;
	}

	public Boolean getEnableBinaryFilePush() {
		return enableBinaryFilePush;
	}

	public void setEnableBinaryFilePush(Boolean enableBinaryFilePush) {
		this.enableBinaryFilePush = enableBinaryFilePush;
	}

	public Boolean getEnablePoSendToSap() {
		return enablePoSendToSap;
	}

	public void setEnablePoSendToSap(Boolean enablePoSendToSap) {
		this.enablePoSendToSap = enablePoSendToSap;
	}
}
