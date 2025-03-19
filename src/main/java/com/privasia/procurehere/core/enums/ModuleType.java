package com.privasia.procurehere.core.enums;

import java.util.Arrays;
import java.util.List;

import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
public enum ModuleType {
	Supplier("Supplier"), Buyer("Buyer"), Country("Country"), IndustryCategory("Industry Category"), BuyerSettings("Buyer Settings"),
	ProductCategory("Product Category"), ProductItem("Product Item"), PromotionalCode("Promotional Code"), OwnerSettings("Owner Settings"),
	CompanyStatus("Company Type"), BuyerAddress("Buyer Address"), CostCenter("Cost Center"), Currency("Currency"), EmailSettings("Email Settings"),
	NaicsCodes("Naics Codes"), SupplierSettings("Supplier Settings"), TimeZone("TimeZone"), Uom("UOM"), State("State"), FavouriteSupplier("Favourite Supplier"),
	User("User"), UserRole("User Role"), BusinessUnit("Business Unit"), FinanceCompanySettings("Finance Company Settings"), PasswordSetting("Password Setting"),
	ERP("ERP Configuration"), PublishedProfile("Published Profile"), BudgetPlanner("Budget Planner"), IdSetting("Id Settings"), PaymentTerms("Payment Terms"),
	ProcurementMethod("Procurement Method"), ProcurementCategory("Procurement Category"), GroupCode("Group Code"), Announcement("Announcement"),
	ContractList("Contract List"), DeclarationSetting("Declaration Setting"), RFA("RFA"), RFI("RFI"), RFP("RFP"), RFS("RFS"), RFT("RFT"), RFQ("RFQ"),
	GRN("GRN"), DO("DO"), Invoice("Invoice"), PR("PR"), PO("PO"), SupplierTags("Supplier Tags"), PRTemplate("PR Template"),
	RFXAuctionTemplate("RFX/Auction Template"), SourcingTemplate("Sourcing Template"), SupplierForm("Supplier Form"),ManageBudget("Manage Budget"),
	EventReport("Event Report"), AuctionSummaryReport("Auction Summary Report"),AuditTrail("Audit Trail"), SupplierList("Supplier List"),
	ProductList("Product List"), SupplierPerformanceTemplate("Supplier Performance Template"), PerformanceEvaluation("Performance Evaluation"),AgreementType("Agreement Type");

	private String value;

	/**
	 * @param value as Type
	 */
	private ModuleType(String value) {
		this.value = value;
	}

	/**
	 * @return value as number
	 */
	@Override
	public String toString() {
		return value;
	}

	public static List<ModuleType> getModuleTypeForSupplier() {
		return Arrays.asList(ModuleType.SupplierSettings, ModuleType.User, ModuleType.UserRole);

	}

	public static List<ModuleType> getModuleTypeForBuyer() {
		return Arrays.asList(ModuleType.RFI, ModuleType.RFQ, ModuleType.RFP, ModuleType.RFT, ModuleType.RFA, ModuleType.RFS, ModuleType.PublishedProfile, ModuleType.EventReport, ModuleType.AuctionSummaryReport, ModuleType.Announcement, ModuleType.SupplierList, ModuleType.SupplierForm, ModuleType.RFXAuctionTemplate, ModuleType.PRTemplate, ModuleType.SourcingTemplate, ModuleType.PR, ModuleType.PO, ModuleType.GRN, ModuleType.DO, ModuleType.Invoice, ModuleType.ContractList, ModuleType.UserRole, ModuleType.User, ModuleType.BudgetPlanner, ModuleType.Uom, ModuleType.SupplierTags, ModuleType.BuyerSettings, ModuleType.DeclarationSetting, ModuleType.IndustryCategory, ModuleType.GroupCode, ModuleType.CostCenter, ModuleType.BusinessUnit, ModuleType.ProcurementMethod, ModuleType.ProcurementCategory, ModuleType.ProductCategory, ModuleType.ProductList, ModuleType.BuyerAddress, ModuleType.PaymentTerms, ModuleType.IdSetting, ModuleType.PasswordSetting, ModuleType.ERP, ModuleType.SupplierPerformanceTemplate, ModuleType.PerformanceEvaluation);
	}

	public static List<ModuleType> getModuleTypeForOwner() {
		return Arrays.asList(ModuleType.UserRole, ModuleType.User, ModuleType.OwnerSettings, ModuleType.Uom, ModuleType.Currency, ModuleType.State, ModuleType.NaicsCodes, ModuleType.TimeZone, ModuleType.Country, ModuleType.CompanyStatus, ModuleType.PromotionalCode, ModuleType.Supplier, ModuleType.PerformanceEvaluation);

	}

	public static ModuleType fromString(String value) {
		try {
			if (StringUtils.checkString(value).equals(Supplier.toString())) {
				return ModuleType.Supplier;
			} else if (StringUtils.checkString(value).equals(Buyer.toString())) {
				return ModuleType.Buyer;
			} else if (StringUtils.checkString(value).equals(Country.toString())) {
				return ModuleType.Country;
			} else if (StringUtils.checkString(value).equals(IndustryCategory.toString())) {
				return ModuleType.IndustryCategory;
			} else if (StringUtils.checkString(value).equals(BuyerSettings.toString())) {
				return ModuleType.BuyerSettings;
			} else if (StringUtils.checkString(value).equals(ProductCategory.toString())) {
				return ModuleType.ProductCategory;
			} else if (StringUtils.checkString(value).equals(ProductItem.toString())) {
				return ModuleType.ProductItem;
			} else if (StringUtils.checkString(value).equals(PromotionalCode.toString())) {
				return ModuleType.PromotionalCode;
			} else if (StringUtils.checkString(value).equals(OwnerSettings.toString())) {
				return ModuleType.OwnerSettings;
			} else if (StringUtils.checkString(value).equals(CompanyStatus.toString())) {
				return ModuleType.CompanyStatus;
			} else if (StringUtils.checkString(value).equals(BuyerAddress.toString())) {
				return ModuleType.BuyerAddress;
			} else if (StringUtils.checkString(value).equals(CostCenter.toString())) {
				return ModuleType.CostCenter;
			} else if (StringUtils.checkString(value).equals(Currency.toString())) {
				return ModuleType.Currency;
			} else if (StringUtils.checkString(value).equals(EmailSettings.toString())) {
				return ModuleType.EmailSettings;
			} else if (StringUtils.checkString(value).equals(NaicsCodes.toString())) {
				return ModuleType.NaicsCodes;
			} else if (StringUtils.checkString(value).equals(SupplierSettings.toString())) {
				return ModuleType.SupplierSettings;
			} else if (StringUtils.checkString(value).equals(TimeZone.toString())) {
				return ModuleType.TimeZone;
			} else if (StringUtils.checkString(value).equals(Uom.toString())) {
				return ModuleType.Uom;
			} else if (StringUtils.checkString(value).equals(State.toString())) {
				return ModuleType.State;
			} else if (StringUtils.checkString(value).equals(FavouriteSupplier.toString())) {
				return ModuleType.FavouriteSupplier;
			} else if (StringUtils.checkString(value).equals(User.toString())) {
				return ModuleType.User;
			} else if (StringUtils.checkString(value).equals(UserRole.toString())) {
				return ModuleType.UserRole;
			} else if (StringUtils.checkString(value).equals(BusinessUnit.toString())) {
				return ModuleType.BusinessUnit;
			} else if (StringUtils.checkString(value).equals(PublishedProfile.toString())) {
				return ModuleType.PublishedProfile;
			} else if (StringUtils.checkString(value).equals(BudgetPlanner.toString())) {
				return ModuleType.BudgetPlanner;
			} else if (StringUtils.checkString(value).equals(IdSetting.toString())) {
				return ModuleType.IdSetting;
			} else if (StringUtils.checkString(value).equals(PaymentTerms.toString())) {
				return ModuleType.PaymentTerms;
			} else if (StringUtils.checkString(value).equals(ProcurementMethod.toString())) {
				return ModuleType.ProcurementMethod;
			} else if (StringUtils.checkString(value).equals(ProcurementCategory.toString())) {
				return ModuleType.ProcurementCategory;
			} else if (StringUtils.checkString(value).equals(GroupCode.toString())) {
				return ModuleType.GroupCode;
			} else if (StringUtils.checkString(value).equals(Announcement.toString())) {
				return ModuleType.Announcement;
			} else if (StringUtils.checkString(value).equals(ContractList.toString())) {
				return ModuleType.ContractList;
			} else if (StringUtils.checkString(value).equals(DeclarationSetting.toString())) {
				return ModuleType.DeclarationSetting;
			} else if (StringUtils.checkString(value).equals(RFA.toString())) {
				return ModuleType.RFA;
			} else if (StringUtils.checkString(value).equals(RFI.toString())) {
				return ModuleType.RFI;
			} else if (StringUtils.checkString(value).equals(RFP.toString())) {
				return ModuleType.RFP;
			} else if (StringUtils.checkString(value).equals(RFS.toString())) {
				return ModuleType.RFS;
			} else if (StringUtils.checkString(value).equals(RFT.toString())) {
				return ModuleType.RFT;
			} else if (StringUtils.checkString(value).equals(RFQ.toString())) {
				return ModuleType.RFQ;
			} else if (StringUtils.checkString(value).equals(GRN.toString())) {
				return ModuleType.GRN;
			} else if (StringUtils.checkString(value).equals(Invoice.toString())) {
				return ModuleType.Invoice;
			} else if (StringUtils.checkString(value).equals(PasswordSetting.toString())) {
				return ModuleType.PasswordSetting;
			} else if (StringUtils.checkString(value).equals(PR.toString())) {
				return ModuleType.PR;
			} else if (StringUtils.checkString(value).equals(PO.toString())) {
				return ModuleType.PO;
			} else if (StringUtils.checkString(value).equals(SupplierTags.toString())) {
				return ModuleType.SupplierTags;
			} else if (StringUtils.checkString(value).equals(PRTemplate.toString())) {
				return ModuleType.PRTemplate;
			} else if (StringUtils.checkString(value).equals(RFXAuctionTemplate.toString())) {
				return ModuleType.RFXAuctionTemplate;
			} else if (StringUtils.checkString(value).equals(SourcingTemplate.toString())) {
				return ModuleType.SourcingTemplate;
			} else if (StringUtils.checkString(value).equals(SupplierForm.toString())) {
				return ModuleType.SupplierForm;
			} else if (StringUtils.checkString(value).equals(ManageBudget.toString())) {
				return ModuleType.ManageBudget;
			} else if (StringUtils.checkString(value).equals(EventReport.toString())) {
				return ModuleType.EventReport;
			} else if (StringUtils.checkString(value).equals(AuctionSummaryReport.toString())) {
				return ModuleType.AuctionSummaryReport;
			} else if (StringUtils.checkString(value).equals(AuditTrail.toString())) {
				return ModuleType.AuditTrail;
			} else if (StringUtils.checkString(value).equals(SupplierList.toString())) {
				return ModuleType.SupplierList;
			} else if (StringUtils.checkString(value).equals(DO.toString())) {
				return ModuleType.DO;
			} else if(StringUtils.checkString(value).equals(ERP.toString())) {
				return ModuleType.ERP;
			} else if(StringUtils.checkString(value).equals(SupplierPerformanceTemplate.toString())) {
				return ModuleType.SupplierPerformanceTemplate;
			} else if(StringUtils.checkString(value).equals(PerformanceEvaluation.toString())) {
				return ModuleType.PerformanceEvaluation;
			}else if(StringUtils.checkString(value).equals(AgreementType.toString())) {
				return ModuleType.AgreementType;
			}
			return null;
		}

		catch (Exception e) {
			throw new IllegalArgumentException("Invalid value specified for Module Type : " + value);
		}
	}

	public static String getValue(ModuleType type) {
		switch (type) {
		case Supplier:
			return Supplier.value;
		case Buyer:
			return Buyer.value;
		case Country:
			return Country.value;
		case IndustryCategory:
			return IndustryCategory.value;
		case BuyerSettings:
			return BuyerSettings.value;
		case ProductCategory:
			return ProductCategory.value;
		case ProductItem:
			return ProductItem.value;
		case PromotionalCode:
			return PromotionalCode.value;
		case OwnerSettings:
			return OwnerSettings.value;
		case CompanyStatus:
			return CompanyStatus.value;
		case BuyerAddress:
			return BuyerAddress.value;
		case CostCenter:
			return CostCenter.value;
		case Currency:
			return Currency.value;
		case EmailSettings:
			return EmailSettings.value;
		case NaicsCodes:
			return NaicsCodes.value;
		case SupplierSettings:
			return SupplierSettings.value;
		case TimeZone:
			return TimeZone.value;
		case Uom:
			return Uom.value;
		case State:
			return State.value;
		case FavouriteSupplier:
			return FavouriteSupplier.value;
		case User:
			return User.value;
		case UserRole:
			return UserRole.value;
		case BusinessUnit:
			return BusinessUnit.value;
		case PublishedProfile:
			return PublishedProfile.value;
		case BudgetPlanner:
			return BudgetPlanner.value;
		case IdSetting:
			return IdSetting.value;
		case PaymentTerms:
			return PaymentTerms.value;
		case ProcurementMethod:
			return ProcurementMethod.value;
		case ProcurementCategory:
			return ProcurementCategory.value;
		case GroupCode:
			return GroupCode.value;
		case Announcement:
			return Announcement.value;
		case ContractList:
			return ContractList.value;
		case DeclarationSetting:
			return DeclarationSetting.value;
		case RFA:
			return RFA.value;
		case RFI:
			return RFI.value;
		case RFP:
			return RFP.value;
		case RFS:
			return RFS.value;
		case RFT:
			return RFT.value;
		case RFQ:
			return RFQ.value;
		case GRN:
			return GRN.value;
		case DO:
			return DO.value;
		case Invoice:
			return Invoice.value;
		case PasswordSetting:
			return PasswordSetting.value;
		case PR:
			return PR.value;
		case PO:
			return PO.value;
		case SupplierTags:
			return SupplierTags.value;
		case PRTemplate:
			return PRTemplate.value;
		case RFXAuctionTemplate:
			return RFXAuctionTemplate.value;
		case SourcingTemplate:
			return SourcingTemplate.value;
		case SupplierForm:
			return SupplierForm.value;
		case ManageBudget:
			return ManageBudget.value;
		case EventReport:
			return EventReport.value;
		case AuctionSummaryReport:
			return AuctionSummaryReport.value;
		case AuditTrail:
			return AuditTrail.value;
		case SupplierList:
			return SupplierList.value;
		case ERP:
			return ERP.value;
		case SupplierPerformanceTemplate:
			return SupplierPerformanceTemplate.value;
		case PerformanceEvaluation:
			return PerformanceEvaluation.value;	
		case AgreementType:
			return AgreementType.value;
		default:
			return null;
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
