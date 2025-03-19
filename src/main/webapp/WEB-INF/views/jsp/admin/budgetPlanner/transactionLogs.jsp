<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prDetailsDesk" code="application.pr.create.details" />
<style>
.over-auto {
	overflow: auto;
}

.top-btn {
	margin-bottom: 15px;
	position: relative;
	top: 5px;
	float: right;
}

.right_button {
	float: right;
}

.upload_download_wrapper {
	border: none;
	margin-top: 10px;
	border-radius: 4px;
	min-height: 60px;
	padding: 9px 0px 9px 15px;
}
</style>

<div id="page-content">
	<div class="container col-md-12">
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a>
			</li>
			<c:url value="/admin/budgets/listBudget" var="manageBudget" />
			<li>
				<a id="manageBudget" href="${manageBudget}"> <spring:message code="defaultmenu.budget.manage" />
				</a>
			</li>
			<li class="active">
				<spring:message code="transaction.logs" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="transaction.logs" />
			</h2>
		</div>
<%-- 		<spring:url value="/admin/transactionLog/exportTransactionLogs" var="exportTransactionLogs" htmlEscape="true" /> --%>
		<spring:url value="/admin/transactionLog/exportTransactionLogsCsv" var="exportCsvTransactionLogs" htmlEscape="true" />
		<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT') or hasRole('ADMIN')" var="canEdit" />
		<!-- <div class="top-btn top-btn col-md-12 p-0"> -->
		<%-- <a href="${canEdit ? exportTransactionLogs : '#'}" class="btn btn-default hvr-pop hvr-rectangle-out3 ${canEdit ? '' : 'disableEve'} ${!buyerReadOnlyAdmin ? '':'disabled'}"><i class="excel_icon"></i> <spring:message code="export.transaction.logs" /></a> --%>

		<!-- </div> -->

		<div class="container-fluid col-md-12">
			<div class="upload_download_wrapper">
				<div class="right_button">
					<a href="${exportCsvTransactionLogs}">
						<button id="export" class="btn btn-sm btn-success hvr-pop" data-toggle="tooltip" data-original-title="Download Transaction Logs" style="margin-right: 20px;">
							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
							</span> <span class="button-content">Download Transaction Logs</span>
						</button>
					</a>
				</div>
			</div>
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">
									<div class="form-group col-md-12 bordered-row">
										<div class="ph_tabel_wrapper scrolableTable_list over-auto">
											<table id="transactionLogTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type="text" class="width_100 width_100_fix">Reference Number</th>
														<th search-type="" class="width_200 width_200_fix">Transaction Date/Time</th>
														<th search-type="text" class="width_100 width_100_fix">Business Unit</th>
														<th search-type="text" class="width_100 width_100_fix">Cost center</th>
														<th search-type="" class="width_100 width_100_fix">New</th>
														<th search-type="" class="width_100 width_100_fix">Add</th>
														<th search-type="" class="width_100 width_100_fix">Deduct</th>
														<th search-type="text" class="width_300 width_300_fix">Transfer From BusinessUnit</th>
														<th search-type="text" class="width_200 width_200_fix">Transfer To BusinessUnit</th>
														<th search-type="text" class="width_100 width_100_fix">Purchase Order</th>
														<th search-type="select" search-options="txStatusList" class="width_100 width_100_fix">Status</th>
														<th search-type="text" class="width_100 width_100_fix">Locked</th>
														<th search-type="text" class="width_100 width_100_fix">PR Base Currency</th>
														<th search-type="text" class="width_100 width_100_fix">Budget Currency</th>
														<th search-type="" class="width_200 width_200_fix">Conversion Rate</th>
														<th search-type="" class="width_200 width_200_fix">Amount After Conversion</th>
														<th search-type="" class="width_200 width_200_fix">Remaining Amount</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
											</table>
										</div>
										<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>



<script>
	$('document').ready(function() {

		var table = $('#transactionLogTableList').DataTable({
			"oLanguage" : {
				"sUrl" : getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				// $('div[id=idGlobalError]').hide();
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
				"url" : getContextPath() + "/admin/transactionLog/transactionLogData",
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
				"data" : "referenceNumber",
				"defaultContent" : ""
			}, {
				"data" : "transactionTimeStamp",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"defaultContent" : ""
			}, {
				"data" : "costCenter",
				"defaultContent" : ""
			}, {
				"data" : "newAmount",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.newAmount);
				}
			}, {
				"data" : "addAmount",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.addAmount);
				}
			}, {
				"data" : "deductAmount",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.deductAmount);
				}
			}, {
				"data" : "fromBusinessUnit",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "toBusinessUnit",
				"className" : "align-center",
				"defaultContent" : ""
			}, {
				"data" : "purchaseOrder",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.purchaseOrder);
				}
			}, {
				"data" : "txStatus",
				"orderable" : false,
				"defaultContent" : ""
			}, {
				"data" : "locked",
				"defaultContent" : ""
			}, {
				"data" : "prBaseCurrency",
				"defaultContent" : ""
			}, {
				"data" : "budgetBaseCurrency",
				"defaultContent" : ""
			}, {
				"data" : "conversionRateAmount",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.conversionRateAmount);
				}
			}, {
				"data" : "amountAfterConversion",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.amountAfterConversion);
				}
			}, {
				"data" : "remainingAmount",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.remainingAmount);
				}
			} ],

			"initComplete" : function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#transactionLogTableList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						if ($(this).attr('search-type') == 'select') {
							var optionsType = $(this).attr('search-options');
							htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value="">All ' + title + '</option>';
							if (optionsType == 'txStatusList') {
								console.log("inside status****");
								<c:forEach items="${txStatusList}" var="item">
								htmlSearch += '<option value="${item}">${item}</option>';
								</c:forEach>
							}
							htmlSearch += '</select></th>';
						} else {
							htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i + '" /></th>';
						}
					} else {
						htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
					}
				});
				htmlSearch += '</tr>';
				$('#transactionLogTableList thead').append(htmlSearch);
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
		
		function ReplaceNumberWithCommasFormat(yourNumber) {
			if(yourNumber!='' && yourNumber!=undefined){
				yourNumber = parseFloat(yourNumber).toFixed(2);
				var n = yourNumber.toString().split(".");
				n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
				return n.join(".");
			}
			return yourNumber; 
		}
	});
</script>


<script>
	
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/js/view/transactionLogs.js"/>"></script>
