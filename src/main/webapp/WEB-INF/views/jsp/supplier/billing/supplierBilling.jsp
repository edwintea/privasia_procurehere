<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<style>
.user-center {
	float: left;
	width: 93%;
	text-align: center;
	padding: 30px 0px;
}
</style>
<spring:message var="supplierBillingDesk" code="application.supplier.billing" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierBillingDesk}] });
});
</script>
<div id="page-content" view-name="supplierBilling">
	<div class="container">
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/supplier/supplierDashboard"><spring:message code="application.dashboard"/></a></li>
			<li class="active"><spring:message code="billing.label"/></li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap billing_icon"><spring:message code="billing.label"/></h2>
		</div>
		<div class="clear"></div>
		<div class="Invited-Supplier-List dashboard-main">
			<div class="Invited-Supplier-List-table add-supplier">
				<div class="ph_tabel_wrapper">
					<div class=" ph_table_border payment marg-bottom-20 document-table">
						
						<div class="Invited-Supplier-List create_sub marg-bottom-20 user-type">
							<div class="row">
								<div class="user-center col-md-12">
									<h3><spring:message code="supplierbilling.buyer.limits"/></h3>
									<h2>
										<c:if test="${supplier.supplierPackage != null}">
												${supplier.supplierPackage.buyerLimit >= 999 ? 'All Buyers' : supplier.supplierPackage.buyerLimit}
										</c:if>
										<c:if test="${supplier.supplierPackage == null}">
												0
										</c:if>
									</h2>
									<%-- 	<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2" onclick="window.location.href='${pageContext.request.contextPath}/admin/listUser';">Manage
										User</button> --%>
									<c:set var="isSingleBuyer" value="${!empty supplier.supplierPackage && supplier.supplierPackage.supplierPlan.buyerLimit == 1}" />
									<c:if test="${isSingleBuyer}">
										<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2" onclick="window.location.href='${pageContext.request.contextPath}/supplier/upgradePlan/${supplier.supplierPackage.supplierPlan.id}';"><spring:message code="supplier.dashboard.upgrade"/></button>
									</c:if>
									<c:if test="${supplier.supplierPackage == null}">
										<button class="btn ph_btn_midium hvr-pop hvr-rectangle-out4 font-white greeen hvr-pop hvr-rectangle-out2" onclick="window.location.href='${pageContext.request.contextPath}/supplier/subscribePlan';"><spring:message code="supplier.dashboard.subscribe"/></button>
									</c:if>
								</div>
							</div>
						</div>
						<div class="Invited-Supplier-List create_sub marg-bottom-20 last-invoice bill-detail">
							<div class="row">
								<h4><spring:message code="supplierbilling.billing.detail"/></h4>
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td align="left"><spring:message code="renew.plan.company.name"/></td>
										<td align="left"><strong>${supplier.companyName}</strong></td>
										<td align="right" class="">
											<!-- a href="#" class="btn ph_btn_small hvr-pop hvr-rectangle-out4">Change</a -->
										</td>
									</tr>
									<tr>
										<td align="left"><spring:message code="supplierbilling.subscribed.plan"/></td>
										<td align="left"><strong>${supplier.supplierPackage.supplierPlan == null ? 'Not Applicable' : (supplier.supplierPackage.supplierPlan.planName.concat(' - ').concat(supplier.supplierPackage.supplierPlan.shortDescription))}</strong></td>
										<td align="right" class="">
											<%-- <c:if test="${supplier.supplierSubscription.supplierPlan != null}">
												<a href="${pageContext.request.contextPath}/buyer/billing/changePlan/${buyer.buyerPackage.plan.id}" class="btn ph_btn_small hvr-pop hvr-rectangle-out4">Change Plan</a>
											</c:if> --%> &nbsp;
										</td>
									</tr>
									<tr>
										<td align="left"><spring:message code="supplierbilling.payment.method"/></td>
										<td align="left"><strong>${subscription.paymentTransaction == null ? 'Not Applicable' : fn:replace(subscription.paymentTransaction.paymentMethod, '_', ' ')}</strong></td>
										<td align="right" class=""></td>
									</tr>
									<tr>
										<td align="left"><spring:message code="supplierbilling.billing.date"/></td>
										<td align="left"><strong> <c:if test="${subscription.paymentTransaction != null}">
													<fmt:formatDate value="${subscription.paymentTransaction.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
												</c:if> <c:if test="${subscription.paymentTransaction == null}">
												<spring:message code="application.not.applicable2"/>
												</c:if>
										</strong></td>
										<td align="right" class=""></td>
									</tr>
									<tr>
										<td align="left"><spring:message code="supplierbilling.payment.amount"/></td>
										<td align="left"><strong> <c:if test="${subscription.paymentTransaction != null}">
													<fmt:formatNumber var="totalAmountFormt" type="number" minFractionDigits="0" maxFractionDigits="2" value="${subscription.paymentTransaction.totalPriceAmount}" />
												${subscription.paymentTransaction.currencyCode}&nbsp;${totalAmountFormt}
											</c:if> <c:if test="${subscription.paymentTransaction == null}">
												<spring:message code="application.not.applicable2"/>
											</c:if></td>
										</strong>
										<td align="right" class=""></td>
									</tr>
								</table>
							</div>
						</div>
						<div class="Invited-Supplier-List create_sub marg-bottom-20 last-invoice">
							<div class="row">
								<h4><spring:message code="account.overview.payment.description"/></h4>
								<div class="ph_tabel_wrapper scrolableTable_list">
									<table id="tableList" class=" display table table-bordered noarrow" cellpadding="0" cellspacing="0" border="1" width="100%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th search-type="" class="width_400 align-left"><spring:message code="account.overview.plan" /></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="account.overview.ref.id" /></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="account.overview.amount"/></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="account.overview.tax"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="account.overview.tax.description"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="prtemplate.total.amount" /></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="account.overview.payment.time" /></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
 
<style>
.byePlan {
	position: relative;
	left: 80%;
	text-align: center;
	padding: 0px 15px !important;
}
.pagination>.disabled>a {
	cursor: not-allowed;
	color: #999 !important;
	background-color: #fff;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript">

function ReplaceNumberWithCommas(yourNumber) {
	var n = yourNumber.toString().split(".");
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	return n.join(".");
}

	$('document').ready(function() {
		/*
		$("#fl1_2").dialog({
		    modal: true,
		    autoOpen: false
		}); */

		$("#openRenew").click(function() {
			$("#idRenewDialog").modal();
		});

		$("#openBuyCredits").click(function() {
			$("#idBuyCreditsDialog").modal();
		});

		$('#planQuantity').mask('99999', {
			placeholder : "e.g. 10"
		});

		$('#planQuantity').keyup(function() {
			$('.idCredits').html($.trim($(this).val()));
			$('.idTotalPrice').html('${supplierSubscription.supplierPlan.currency.currencyCode} ' + (parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val()))));
		});

		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 3000);
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/supplier/billing/paymentTransactionData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order" : [],
			"columns" : [ /*{
										"data" : "id",
										"searchable" : false,
										"orderable" : false,
										"render" : function(data, type, row) {
											var ret = '<a href="viewPaymentTransaction/' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
											return ret;
										}
									},*/{
				"data" : "supplierPlan.planName",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = row.supplierPlan.planName + ' - ' + row.supplierPlan.shortDescription;
					return ret;
				}
			}, {
				"data" : "referenceTransactionId",
				"defaultContent" : ""
			}, {
				"data" : "amount",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = row.currencyCode + ' ' + (row.amount ? row.amount - row.additionalTax : 'UNKNOWN');
					return ret;
				}
			},  {
				"data" : "additionalTax",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.additionalTax ? ReplaceNumberWithCommas(row.additionalTax.toFixed(2)) : '0.00') ;
					return ret;
				}
			},{
				"data" : "additionalTaxDesc",
				"defaultContent" : ""
			},  {
				"data" : "totalPriceAmount",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.totalPriceAmount ? ReplaceNumberWithCommas(row.totalPriceAmount.toFixed(2)) : 'UNKNOWN');
					return ret;
				}
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			} ]
		});
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value="">Search ' + title + '</option>';
					if (optionsType == 'transactionTypeList') {
						<c:forEach items="${transactionTypeList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item"> - ui
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					-ui
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				table.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(table.table().container()).on('change', 'thead select', function() {
			table.column($(this).data('index')).search(this.value).draw();
		});
	});
</script>

