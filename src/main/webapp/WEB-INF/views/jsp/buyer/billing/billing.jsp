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
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="buyerBillingDesk" code="application.buyer.billing" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerBillingDesk}] });
});
</script>
<!-- Pop Up -->
<!-- 
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/elements/featherlight.css" />
<!-- JS Core -->
<div id="page-content" view-name="buyerBilling">
	<div class="container">
		<ol class="breadcrumb">
			<li>
				<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a>
			</li>
			<li class="active"><spring:message code="billing.label"/></li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap billing_icon"><spring:message code="billing.title"/></h2>
		</div>
		<%-- 	<div>
			<div class="upload_download_wrapper">
				<div class="right_button">
					<c:url value="/buyer/billing/subscriptionHistoryExcel" var="subscriptionHistory" />
					<a href="${subscriptionHistory}">
						<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal">
							<i class="excel_icon"></i>Download Subscription History
						</button>
					</a>
				</div>
			</div>
		</div> --%>

		<%-- <div>
			<div class="upload_download_wrapper">
				<div class="right_button">
					<c:url value="/buyer/billing/paymentTransactionExcel" var="paymentTransactions" />
					<a href="${paymentTransactions}">
						<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal">
							<i class="excel_icon"></i>Download Payment Transactions
						</button>
					</a>
				</div>
			</div>
		</div> --%>
		<div class="clear"></div>
		<div class="Invited-Supplier-List dashboard-main">
			<div class="Invited-Supplier-List-table add-supplier">
				<div class="ph_tabel_wrapper">
					<div class=" ph_table_border payment marg-bottom-20 document-table">

						<div class="Invited-Supplier-List create_sub marg-bottom-20 last-invoice">
							<div class="row">
								<h4><spring:message code="billing.subscription.history"/></h4>
								<div class="ph_tabel_wrapper scrolableTable_list">
									<table id="subscriptionHistoryTableList" class=" display table table-bordered noarrow" cellpadding="0" cellspacing="0" border="1" width="100%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th search-type="text" class="width_400 align-left"><spring:message code="plan.planName"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="billing.user.limit"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="billing.event.limit"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="application.startdate"/></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="rfaevent.end.date"/></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="account.overview.amount"/></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
												<th search-type="select" search-options="planTypeList" class="width_100 width_100_fix align-left"><spring:message code="plan.type" /></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="Invited-Supplier-List create_sub marg-bottom-20 last-invoice">
							<div class="row">
								<h4><spring:message code="account.overview.payment.description" /></h4>
								<div class="ph_tabel_wrapper scrolableTable_list">
									<table id="tableList" class=" display table table-bordered noarrow" cellpadding="0" cellspacing="0" border="1" width="90%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th search-type="" class="width_400 align-left"><spring:message code="account.overview.plan" /></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="account.overview.payment.time" /></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="Product.remarks" /></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="account.overview.bill.ref.id" /></th>
												<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="account.overview.amount"/></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="account.overview.subscription.discount"/></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="account.overview.promotional.discount"/></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="account.overview.tax"/></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="account.overview.tax.description"/></th>
												<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="prtemplate.total.amount" /></th>
												<th search-type="" class="width_100 width_100_fix align-left"><spring:message code="label.currency" /></th>
												<th search-type="" class="width_200 width_200_fix align-center"><spring:message code="promotion.title" /></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<!-- div class="Invited-Supplier-List create_sub marg-bottom-20 last-invoice bill-detail start-monthly">
							<div class="row border-bottom">
								<h3>You are saving two months a year</h3>
								<button class="btn btn-block btn-info hvr-pop hvr-rectangle-out ph_btn_midium">Start paying monthly</button>
							</div>
							<div class="row">
								<div class="col-md-6 border-right">
									<h5>Want to reduce you payments?</h5>
									<p>
										You can cut down on your privasia subscription payment by
										<a href="">recommanding Privasia to your friends</a>
										. You earn thier first month’s payment as a credit on your account
									</p>
								</div>
								<div class="col-md-6">
									<h5>Need to cancel your subscription?</h5>
								</div>
							</div>
						</div -->
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>

.pagination > li > a {
	color: #8da0aa !important;
}

.pagination > .active > a
{
	color: #fff !important;
}
.pagination>.disabled>a {
	cursor: not-allowed;
	color: #999 !important;
	background-color: #fff;
}
#subscriptionHistoryTableList th {
    padding: 17px 12px;
    font-family: "open_sanssemibold";
    font-weight: 500;
    font-size: 13px;
}

#tableList th {
    padding: 17px 12px;
    font-family: "open_sanssemibold";
    font-weight: 500;
    font-size: 13px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript">
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

		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
// 				$('div[id=idGlobalError]').hide();
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
				"url" : getContextPath() + "/buyer/billing/paymentTransactionData",
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
			},*/ {
				"data" : "buyerPlan.planName",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = row.buyerPlan.planName + ' - ' + row.buyerPlan.shortDescription;
					return ret;
				}
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "remarks",
				"defaultContent" : ""
			}
			, {
				"data" : "referenceTransactionId",
				"defaultContent" : ""
			}, {
				"data" : "priceAmount",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.priceAmount ? ReplaceNumberWithCommas((row.priceAmount).toFixed(2)) : 'UNKNOWN');
					return ret;
				}
			}, {
				"data" : "priceDiscount",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.priceDiscount ? ReplaceNumberWithCommas(row.priceDiscount.toFixed(2)) : '0.00');
					return ret;
				}
			}, {
				"data" : "promoCodeDiscount",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.promoCodeDiscount ? ReplaceNumberWithCommas(row.promoCodeDiscount.toFixed(2)) : '0.00');
					return ret;
				}
			}, {
				"data" : "additionalTax",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.additionalTax ? ReplaceNumberWithCommas(row.additionalTax.toFixed(2)) : '0.00') ;
					return ret;
				}
			},{
				"data" : "additionalTaxDesc",
				"defaultContent" : "",
			}, {
				"data" : "totalPriceAmount",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = (row.totalPriceAmount ? ReplaceNumberWithCommas(row.totalPriceAmount.toFixed(2)) : 'UNKNOWN');
					return ret;
				}
			}, {
				"data" : "currencyCode",
				"defaultContent" : "",
				"className" : "align-left"
			}, {
				"data" : "promoCode.promoCode",
				"defaultContent" : ""
			} ],
			
		"initComplete": function(settings, json) {
			table.on( 'search.dt', function () { console.log('serching...'); $('div[id=idGlobalError]').hide();} );
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> '+title+'</option>';
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
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
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
		}
	});
		

		
		var subsTable = $('#subscriptionHistoryTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
// 				$('div[id=idGlobalError]').hide();
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
				"url" : getContextPath() + "/buyer/billing/subscriptionHistoryData",
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
			"columns" : [ {
				"data" : "plan.planName",
				"className" : "align-left",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = row.plan.planName + ' - ' + row.plan.shortDescription;
					return ret;
				}
			}, {
				"data" : "userQuantity",
				"defaultContent" : ""
			}, {
				"data" : "eventQuantity",
				"defaultContent" : ""
			}, {
				"data" : "startDate",
				"searchable" : false,
				"defaultContent" : ""
			} , {
				"data" : "endDate",
				"searchable" : false,
				"defaultContent" : ""
			} ,  {
				"data" : "totalPriceAmount",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = row.currencyCode + ' ' + (row.totalPriceAmount ? ReplaceNumberWithCommas(row.totalPriceAmount.toFixed(2)) : 'UNKNOWN');
					return ret;
				}
			}, {
				"data" : "subscriptionStatus",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "planType",
				"defaultContent" : ""
			}],
			
		"initComplete": function(settings, json) {
			table.on( 'search.dt', function () { console.log('serching...'); $('div[id=idGlobalError]').hide();} );
		var htmlSearch1 = '<tr class="tableHeaderWithSearch">';
		$('#subscriptionHistoryTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch1 += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'planTypeList') {
						<c:forEach items="${planTypeList}" var="item">
						htmlSearch1 += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					/* if (optionsType == 'subscriptionStatusList') {
						<c:forEach items="${subscriptionStatusList}" var="item">
						htmlSearch1 += '<option value="${item}">${item}</option>';
						</c:forEach>
					} */
					htmlSearch1 += '</select></th>';
			}else{	
				htmlSearch1 += '<th style="'+$(this).attr("style")+'" class="align-left"><input type="text" placeholder="<spring:message code="application.search"/>  '+title+'" data-index="'+i+'" /></th>';
			}
			} else {
				htmlSearch1 +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
		});
		htmlSearch1 += '</tr>';
		$('#subscriptionHistoryTableList thead').append(htmlSearch1);
		$(subsTable.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				subsTable.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(subsTable.table().container()).on('change', 'thead select', function() {
			subsTable.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	});
	
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '#dashboardLink, #tableList_length';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	function ReplaceNumberWithCommas(yourNumber) {
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return n.join(".");
	}
</script>
<!--  
<script src="<c:url value="/resources/assets/js-core/featherlight.min.js"/>" type="text/javascript" charset="utf-8"></script> -->
<script>
	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}', // production
			container : 'idSubscribeForm',
			condition : function() {
				return true;
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'idButtonHolder',
				type : 'checkout',
				color : 'blue',
				size : 'small',
				shape : 'rect'
			} ]
		});
	};

</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
