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
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT')" var="canEditBudget" />
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY')" var="canViewBudget" />

<style>
table.dataTable>tbody>tr>td:nth-child(1) {
	text-align: center;
}

.active-stat {
	background: #00b050;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.new-stat {
	background: #ff9000;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.cancel-stat {
	background: #acb1b7;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.rejected-stat {
	background: #ff5955;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.expired-stat {
	background: #ff0900;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.draft-stat {
	background: #bf9000;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.approved-stat {
	background: #0083c9;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.pending-stat {
	background: #f19829;
	border-radius: 5px;
	color: #fff;
	padding: 2px !important;
	font-size: 12px;
	width: 75px !important;
	position: relative;
	text-align: center;
	display: block;
}

.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0cb6ff none repeat scroll 0 0 !important;
	color: #fff;
	display: none;
	border-color: #0095d5;
}

.over-auto {
	overflow: auto;
}

.top-btn {
	margin-bottom: 15px;
	position: relative;
	top: 5px;
}

.mr {
	margin-right: 10px;
}

.btn-right {
	float: right;
}
</style>


<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a>
			</li>
			<li class="active">
				<spring:message code="breadcrum.budget.planner.manage" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="breadcrum.budget.planner.manage" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<a class="btn-right" href="${pageContext.request.contextPath}/admin/budgets/transactionLogs">
								<button class="btn btn-blue ">
									<i class="excel_icon"></i>
									<spring:message code="transaction.logs" />
								</button>
							</a>
							<div class="row">
								<div class="col-xs-12">
									<div class="form-group col-md-12 bordered-row">
										<div class="ph_tabel_wrapper scrolableTable_list over-auto">
											<table id=budgetTableList class=" display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th class="width_100 width_100_fix"><spring:message code="application.action" /></th>
														<!--<spring:message code="application.action" />  -->
														<th search-type="select" search-options="budgetStatus" class="width_100 width_100_fix">Status</th>
														<th search-type="text" class="align-left width_200 width_200_fix">Budget ID</th>
														<th search-type="text" class="align-left width_200 width_200_fix">Budget Name</th>
														<th search-type="text" class="align-left width_200 width_200_fix">Business Unit</th>
														<th search-type="text" class="align-left width_200 width_200_fix">Cost Center</th>
														<th search-type="" class="align-left width_200 width_200_fix">Valid From</th>
														<th search-type="" class="align-left width_200 width_200_fix">Valid To</th>
														<th search-type="" class="align-right width_200 width_200_fix">Total Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Pending Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Approved Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Locked Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Paid Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Transfer Amount</th>
														<th search-type="" class="align-right width_200 width_200_fix">Remaining Amount</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
											</table>
										</div>
										<spring:url value="/admin/budgets/createBudget" var="createBudget" htmlEscape="true" />
										<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT')" var="canEdit" />
										<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY')" var="canViewOnly" />
										<a href="${canEdit ? createBudget : '#'}" class="btn btn-info top-marginAdminList ${canEdit ? '' : 'disableEve'}"> <spring:message code="budget.create" /></a>

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
		var canViewBudgetJs=${canViewBudget};
		var canEdiBudgetJs=${canEditBudget};
		
		var table = $('#budgetTableList').DataTable({
			"oLanguage" : {
				"sUrl" : getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"rowCallback" : function(row, data, index) {
				if (data.budgetStatus == 'NEW') {
					$(row).find('td:eq(1) > div').addClass('new-stat');
				}
				if (data.budgetStatus == 'PENDING') {
					$(row).find('td:eq(1) > div').addClass('pending-stat');
				}
				if (data.budgetStatus == 'APPROVED') {
					$(row).find('td:eq(1) > div').addClass('approved-stat');
				}
				if (data.budgetStatus == 'ACTIVE') {
					$(row).find('td:eq(1) > div').addClass('active-stat');
				}
				if (data.budgetStatus == 'CANCELED') {
					$(row).find('td:eq(1) > div').addClass('cancel-stat');
				}
				if (data.budgetStatus == 'DRAFT') {
					$(row).find('td:eq(1) > div').addClass('draft-stat');
				}
				if (data.budgetStatus == 'REJECTED') {
					$(row).find('td:eq(1) > div').addClass('rejected-stat');
				}
				if (data.budgetStatus == 'EXPIRED') {
					$(row).find('td:eq(1) > div').addClass('expired-stat');
				}
			},
			"processing" : true,
			"deferRender" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/admin/budgets/budgetData",
				"data" : function(d) {
				},
			},
			"order" : [ [ 2, "desc" ] ],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"className" : "align-center",
				"render" : function(data, type, row) {
					if(row.budgetStatus == 'PENDING' || row.budgetStatus == 'ACTIVE' || row.budgetStatus == 'APPROVED' || row.budgetStatus == 'EXPIRED' || row.budgetStatus == 'CANCELED'){
						var ret ='<a href="viewBudget/' + row.id + '"  title=View><img height="21" width="21" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
						return ret;
					} else if((row.budgetStatus == 'DRAFT' || row.budgetStatus == 'REJECTED') && canEdiBudgetJs){
						var ret = '<a href="editBudget/' + row.id + '" class="${canEdit ? '' : ''}" title="<spring:message code="application.edit"/>"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
						console.log('Boo');
						return ret;
					} else if((row.budgetStatus == 'DRAFT' || row.budgetStatus == 'REJECTED') && canViewBudgetJs){
						var ret ='<a href="viewBudget/' + row.id + '"  title="View"><img height="21" width="21" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
						return ret;
					}
				}
			}, {
				"data" : "budgetStatus",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret = '<div>' + row.budgetStatus + '</div>';
					return ret;
				}
			}, {
				"data" : "budgetId",
				"defaultContent" : ""
			}, {
				"data" : "budgetName",
				"defaultContent" : ""
			}, {
				"data" : "businessUnitName",
				"defaultContent" : ""
			}, {
				"data" : "costCenterName",
				"defaultContent" : ""
			}, {
				"data" : "validFrom",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "validTo",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "totalAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.totalAmount);
				}
			}, {
				"data" : "pendingAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.pendingAmount);
				}
			}, {
				"data" : "approvedAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.approvedAmount);
				}
			}, {
				"data" : "lockedAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.lockedAmount);
				}

			}, {
				"data" : "paidAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.paidAmount);
				}
			}, {
				"data" : "transferAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.transferAmount);
				}
			}, {
				"data" : "remainingAmount",
				"searchable" : false,
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.remainingAmount);
				}
			} ],

			"initComplete" : function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#budgetTableList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						if ($(this).attr('search-type') == 'select') {
							var optionsType = $(this).attr('search-options');
							htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value="">All ' + title + '</option>';
							if (optionsType == 'budgetStatus') {
								<c:forEach items="${budgetStatus}" var="item">
								htmlSearch += '<option value="${item}">${item}</option>';
								</c:forEach>
							}
							htmlSearch += '</select></th>';
						} else {
							htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left width_200 width_200_fix"><input type="text" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i + '" /></th>';
						}
					} else {
						htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
					}
				});
				htmlSearch += '</tr>';
				$('#budgetTableList thead').append(htmlSearch);
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




<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/js/view/manageBudget.js"/>"></script>