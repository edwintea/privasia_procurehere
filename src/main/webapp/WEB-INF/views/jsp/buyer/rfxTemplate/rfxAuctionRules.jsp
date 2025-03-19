<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message code="prtemplate.label.visible" var="visible" />
<spring:message code="prtemplate.label.read.only" var="read" />
<spring:message code="prtemplate.label.optional" var="optional" />
<div class="auctionRulesTemp" style="display: none;">

	<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
		<div class="meeting2-heading">
			<h3>
				<spring:message code="summarydetails.auctionrules.title" />
			</h3>
		</div>
		<div class="import-supplier-inner-first-new pad_all_15 global-list">
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="summarydetails.auctionrules.prebid.by" /></label>
				</div>
				<div class="col-md-4 dd sky  ">
					<form:select class="chosen-select disablesearch" id="idPreBidBy" path="templateFieldBinding.preBidBy">
						<form:options items="${preBidByList}" />
					</form:select>
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.extraForDisAbleField" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.preBidByDisabled" class="custom-checkbox" title="" label="${read}" />
				</div>
				<%--									<div class="check-wrapper">--%>
				<%--										<form:checkbox path="templateFieldBinding.extraForDisAbleField" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
				<%--									</div>--%>
			</div>
			<div class="row marg-bottom-10">
				<%-- <div class="col-md-3">
                    <label class="marg-top-10"><spring:message code="summarydetails.auctionrules.supplier.provide" /></label>
                </div> --%>
				<div class="col-md-3"></div>
				<div class="lowerPriceSetting">
					<div class="col-md-4 marg-top-10  ">
						<form:checkbox path="templateFieldBinding.isPreBidHigherPrice" class="custom-checkbox" title="" />
						<label class="marg-top-10" id="labelchange3" style="margin-top: 0 !important"><spring:message code="auctionrules.supplier.higher.price" /></label>
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.isPreBidHigherPriceVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
						<%-- <form:checkbox path="templateFieldBinding.isPreBidHigherPriceVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.isPreBidHigherPriceDisabled" class="custom-checkbox" title="${read}" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.isPreBidHigherPriceOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
					<%--											&lt;%&ndash; <form:checkbox path="templateFieldBinding.isPreBidHigherPriceOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
					<%--										</div>--%>
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3"></div>
				<div class="col-md-4 marg-top-10  ">
					<spring:message code="rfxtemplate.allow.same.pre.bid" var="allowsuppsamebid" />
					<form:checkbox path="templateFieldBinding.isPreBidSameBidPrice" class="custom-checkbox" title="" label="${allowsuppsamebid}" />
				</div>
				<div class="check-wrapper first">
					<form:checkbox path="templateFieldBinding.isPreBidSameBidPriceVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
					<%-- <form:checkbox path="templateFieldBinding.isPreBidSameBidPriceVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.isPreBidSameBidPriceDisabled" class="custom-checkbox" title="${read}" label="${read}" />
				</div>
				<%--									<div class="check-wrapper">--%>
				<%--										<form:checkbox path="templateFieldBinding.isPreBidSameBidPriceOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
				<%--										&lt;%&ndash; <form:checkbox path="templateFieldBinding.isPreBidSameBidPriceOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
				<%--									</div>--%>
			</div>
			<div class="row marg-bottom-10 allowSamePreSetBidForAllSuppliers ">
				<div class="col-md-3"></div>
				<div class="col-md-4 marg-top-10  ">
					<spring:message code="auction.rules.pre.bid.preset" var="allowSameBidForAllSuppliers" />
					<form:checkbox path="templateFieldBinding.isPreSetSamePreBidForAllSuppliers" class="custom-checkbox" id="idSamePreBidForAllSuppliers" title="" label="${allowSameBidForAllSuppliers}" />
				</div>
				<div class="check-wrapper first">
					<form:checkbox path="templateFieldBinding.isPreSetSamePreBidForAllSuppliersVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.isPreSetSamePreBidForAllSuppliersDisabled" class="custom-checkbox" id="readOnlyPreSetAllSupp" title="${read}" label="${read}" />
				</div>
				<%--									<div class="check-wrapper">--%>
				<%--										<form:checkbox path="templateFieldBinding.isPreSetSamePreBidForAllSuppliersOptional" disabled="true" id="optionalPreSetAllSupp" class="custom-checkbox disabled" title="" label="${optional}" />--%>
				<%--									</div>--%>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="auctionrules.bidding.type" /></label>
				</div>
				<div class="col-md-4 dd sky  ">
					<form:select class="chosen-select disablesearch" id="biddingType" path="templateFieldBinding.biddingType">
						<form:option value="ITEMIZEDWITHTAX">
							<spring:message code="rfx.itemized.tax" />
						</form:option>
						<form:option value="ITEMIZEDWITHOUTTAX">
							<spring:message code="rfx.itemized.without.tax" />
						</form:option>
						<form:option value="LUMPSUMWITHTAX">
							<spring:message code="rfx.lumpsum.with.tax" />
						</form:option>
						<form:option value="LUMPSUMWITHOUTTAX">
							<spring:message code="rfx.lumpsum.without.tax" />
						</form:option>
					</form:select>
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.extraForDisAbleField" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.biddingTypeDisabled" class="custom-checkbox" title="" label="${read}" />
				</div>
				<%--									<div class="check-wrapper">--%>
				<%--										<form:checkbox path="templateFieldBinding.extraForDisAbleField" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
				<%--									</div>--%>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label id="labelchange1" class="marg-top-10"><spring:message code="rfx.increment.own.previous" /></label>
				</div>
				<div class="col-md-4 marg-top-10">
					<spring:message code="application.yes" var="yes" />
					<form:checkbox path="templateFieldBinding.isBiddingMinValueFromPrevious" class="custom-checkbox" id="chkPrevious" title="${yes}" label="${yes}" />
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.isBiddingMinValueFromPreviousVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
					<%-- <form:checkbox path="templateFieldBinding.isBiddingMinValueFromPreviousVisible" class="custom-checkbox" title="Visible" label="Visible" chcked="true" /> --%>
				</div>
				<div class="check-wrapper">
					<form:checkbox id="idIsBiddingMinValueFromPreviousDisabled" path="templateFieldBinding.isBiddingMinValueFromPreviousDisabled" class="custom-checkbox" title="" label="${read}" />
				</div>
				<%--									<div class="check-wrapper">--%>
				<%--										<form:checkbox path="templateFieldBinding.isBiddingMinValueFromPreviousOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
				<%--										&lt;%&ndash; <form:checkbox path="templateFieldBinding.isBiddingMinValueFromPreviousOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
				<%--									</div>--%>
			</div>
			<div class="fadeChkPrevious ">
				<div class="row marg-bottom-10">
					<div class="col-md-3"></div>
					<div class="col-md-4 dd sky">
						<form:select class="chosen-select disablesearch" id="biddingMinValueType" path="templateFieldBinding.biddingMinValueType">
							<form:options items="${biddingMinValueTypelist}" />
						</form:select>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3"></div>
					<div class="col-md-4">
						<spring:message code="rfx.templatelist.default.value" var="tempdefalutval" />
						<form:input path="templateFieldBinding.biddingMinValue" id="idBiddingMinValue" data-validation="length custom" data-validation-regexp="^\d{0,9}(\.\d{1,2})?$" data-validation-length="0-9" placeholder="${tempdefalutval}" class="form-control" />
					</div>
				</div>
			</div>
			<div class="sealedBidHide">
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="summarydetails.auctionrules.start.gate" /></label>
					</div>
					<div class="col-md-4 marg-top-10">
						<form:checkbox path="templateFieldBinding.isStartGate" class="custom-checkbox" title="${yes}" label="${yes}" />
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.isStartGateVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
						<%-- <form:checkbox path="templateFieldBinding.isStartGateVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.isStartGateDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.isStartGateOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
					<%--											&lt;%&ndash; <form:checkbox path="templateFieldBinding.isStartGateOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10" id="labelchange2"><spring:message code="summarydetails.auctionrules.start.gate" /></label>
					</div>
					<div class="col-md-4 marg-top-10">
						<form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBid" class="custom-checkbox" id="chkOther" title="Yes" label="Yes" />
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBidVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
						<%-- <form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBidVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBidDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBidOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
					<%--											&lt;%&ndash; <form:checkbox path="templateFieldBinding.isBiddingPriceHigherLeadingBidOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
					<%--										</div>--%>
				</div>
				<div class="fadeChkOther">
					<div class="row marg-bottom-10">
						<div class="col-md-3"></div>
						<div class="col-md-4 dd sky">
							<form:select class="chosen-select disablesearch" id="biddingPriceHigher" path="templateFieldBinding.biddingPriceHigherLeadingBidType">
								<form:options items="${biddingPriceHigherLeadingBidTypelist}" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-3"></div>
						<div class="col-md-4">
							<form:input path="templateFieldBinding.biddingPriceHigherLeadingBidValue" data-validation="length custom required" data-validation-regexp="^\d{1,9}(\.\d{1,2})?$" data-validation-length="0-9" placeholder="${tempdefalutval}" class="form-control" />
						</div>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.supplier.same.bid" /></label>
					</div>
					<div class="col-md-4 marg-top-10">
						<form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBid" class="custom-checkbox" title="Yes" label="Yes" id="idSamebidsupplier" />
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBidVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
						<%-- <form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBidVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBidDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBidOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
					<%--											&lt;%&ndash; <form:checkbox path="templateFieldBinding.isBiddingAllowSupplierSameBidOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
					<%--										</div>--%>
				</div>

				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.pre.bid.price" /></label>
					</div>
					<div class="col-md-4 marg-top-10">
						<form:checkbox path="templateFieldBinding.prebidAsFirstBid" class="custom-checkbox" title="Yes" label="Yes" />
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.prebidAsFirstBidVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.prebidAsFirstBidDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.prebidAsFirstBidOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>

				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="auctionrules.supplier.console.setting" /></label>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.price" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="auctionConsolePrice" path="templateFieldBinding.auctionConsolePriceType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_LEADING">
								<spring:message code="auctionrules.option.show.leading" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.auctionConsolePriceTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="Productz.favoriteSupplier" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="auctionConsoleVender" path="templateFieldBinding.auctionConsoleVenderType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_LEADING">
								<spring:message code="auctionrules.option.show.leading" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.auctionConsoleVenderTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="summarydetails.auctionrules.rank" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="auctionConsoleRank" path="templateFieldBinding.auctionConsoleRankType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_MY_RANK">
								<spring:message code="auctionrules.option.show.rank" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.auctionConsoleRankTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="auctionrules.buyer.console.setting" /></label>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.price" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="buyerAuctionConsolePrice" path="templateFieldBinding.buyerAuctionConsolePriceType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_LEADING">
								<spring:message code="auctionrules.option.show.leading" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.buyerAuctionConsolePriceTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="application.supplier" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="buyerAuctionConsoleVender" path="templateFieldBinding.buyerAuctionConsoleVenderType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_LEADING">
								<spring:message code="auctionrules.option.show.leading" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.buyerAuctionConsoleVenderTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="summarydetails.auctionrules.rank" /></label>
					</div>
					<div class="col-md-4 dd sky  ">
						<form:select class="chosen-select disablesearch" id="buyerAuctionConsoleRank" path="templateFieldBinding.buyerAuctionConsoleRankType">
							<form:option value="SHOW_ALL">
								<spring:message code="auctionrules.option.showall" />
							</form:option>
							<form:option value="SHOW_LEADING">
								<spring:message code="auctionrules.option.show.leading" />
							</form:option>
							<form:option value="SHOW_NONE">
								<spring:message code="auctionrules.option.show.none" />
							</form:option>
						</form:select>
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${visible}" checked="true" />
					</div>
					<div class="check-wrapper">
						<form:checkbox path="templateFieldBinding.buyerAuctionConsoleRankTypeDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
					<%--										<div class="check-wrapper">--%>
					<%--											<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="" label="${optional}" />--%>
					<%--										</div>--%>
				</div>
			</div>
		</div>
	</div>
	<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20 sealedBidHide">
		<div class="meeting2-heading">
			<h3>
				<spring:message code="eventsummary.eventdetail.time.extension" />
			</h3>
		</div>
		<div class="import-supplier-inner-first-new pad_all_15 global-list">
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="eventsummary.eventdetail.extensiontype" /></label>
				</div>
				<div class="col-md-4 dd sky  ">
					<form:select class="chosen-select disablesearch" id="idTypeExtension" path="templateFieldBinding.timeExtensionType">
						<form:options items="${timeExtensionTypeList}" />
					</form:select>
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="${visible}" label="${visible}" checked="true" />
				</div>
				<div class="check-wrapper">
					<form:checkbox path="templateFieldBinding.timeExtensionTypeDisabled" class="custom-checkbox" id="idtimeExtRead" title="" label="${read}" />
				</div>
<%--				<div class="check-wrapper">--%>
<%--					<form:checkbox path="templateFieldBinding.extraForDisAbleField" class="custom-checkbox disabled" disabled="true" title="${optional}" label="${optional}" />--%>
<%--				</div>--%>
			</div>
			<div class="hideDiv">
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="eventsummary.eventdetail.extensionduration" /></label>
					</div>
					<div class="col-md-2 dd sky  ">
						<form:input id="child1" path="templateFieldBinding.timeExtensionDuration" data-validation="length custom" data-validation-regexp="^\d{0,9}?$" data-validation-length="0-9" placeholder="${tempdefalutval}" class="form-control" />
					</div>
					<div class="col-md-2 dd sky ">
						<form:select path="templateFieldBinding.timeExtensionDurationType" cssClass="form-control chosen-select disablesearch">
							<form:option value="MINUTE">
								<spring:message code="label.minute" />
							</form:option>
							<form:option value="HOUR">
								<spring:message code="label.hours" />
							</form:option>
						</form:select>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.time.extension.trigger" /></label>
					</div>
					<div class="col-md-2 dd sky neweew">
						<form:input id="child2" path="templateFieldBinding.timeExtensionLeadingBidValue" data-validation="length custom" data-validation-regexp="^\d{0,9}?$" data-validation-length="0-9" placeholder="${tempdefalutval}" class="form-control" />
					</div>
					<div class="col-md-2 dd sky  ">
						<form:select path="templateFieldBinding.timeExtensionLeadingBidType" data-validation="required" cssClass="form-control chosen-select disablesearch">
							<form:option value="MINUTE">
								<spring:message code="label.minute" />
							</form:option>
							<form:option value="HOUR">
								<spring:message code="label.hours" />
							</form:option>
						</form:select>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.round.of.extension" /></label>
					</div>
					<div class="col-md-4">
						<form:input id="child3" path="templateFieldBinding.extensionCount" data-validation="length custom" data-validation-regexp="^\d{0,9}?$" data-validation-length="0-3" placeholder="${tempdefalutval}" class="form-control" />
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.worst.bidder.disqualify" /></label>
					</div>
					<div class="col-md-4 marg-top-10">
						<form:checkbox path="templateFieldBinding.autoDisqualify" class="custom-checkbox" id="idBidderDisqualify" title="Yes" label="Yes" />
					</div>
					<div class="check-wrapper first">
						<form:checkbox path="templateFieldBinding.autoDisqualifyVisible" disabled="true" class="custom-checkbox disabled" title="" label="${visible}" checked="true" />
						<%-- <form:checkbox path="templateFieldBinding.autoDisqualifyVisible" class="custom-checkbox" title="Visible" label="Visible" /> --%>
					</div>
					<div class="check-wrapper">
						<form:checkbox id="idBidderDisRadio" path="templateFieldBinding.autoDisqualifyDisabled" class="custom-checkbox" title="" label="${read}" />
					</div>
<%--					<div class="check-wrapper">--%>
<%--						<form:checkbox path="templateFieldBinding.autoDisqualifyOptional" disabled="true" class="custom-checkbox disabled" title="" label="${optional}" />--%>
<%--						&lt;%&ndash; <form:checkbox path="templateFieldBinding.autoDisqualifyOptional" class="custom-checkbox" title="Optional" label="Optional" /> &ndash;%&gt;--%>
<%--					</div>--%>
				</div>
				<div class="row marg-bottom-10 bidderCountDiv">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="rfx.count.bidder.disqualify" /></label>
					</div>
					<div class="col-md-4">
						<form:input id="child4" path="templateFieldBinding.bidderDisqualify" data-validation="length custom" data-validation-regexp="^\d{0,9}?$" data-validation-length="0-9" placeholder="${tempdefalutval}" class="form-control" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


