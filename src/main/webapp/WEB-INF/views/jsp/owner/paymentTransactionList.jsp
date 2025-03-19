<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="paymentTransListDesk" code="application.owner.payment.transaction" />
<style>
.f-r{
float:right;
}
</style>
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${paymentTransListDesk}] });
});
</script>
<div id="page-content" view-name="paymentTransaction">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href="#"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active">Payment Transactions</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">Payment Transaction List</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<c:url value="/owner/paymentTransaction/exportPaymentTransactionReport" var="downloadPaymentTransReport" />
							<form:form action="${downloadPaymentTransReport}" method="get" id="exportPaymentReportForm" ModelAttribute="searchFilterPaymentTransPojo">

								<div class="col-md-12 marg-top-10 mb-10">
									<label><spring:message code="application.filter.By.Date" /></label>
									<div class="row">
										<div class="col-md-5 col-sm-8 col-lg-5">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" requerd />
										</div>
										<div class="col-md-2 col-sm-4 resetBtn-padding">
											<spring:message code="application.reset"  var="resetLabel"/>
											<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
												<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
											</button>
										</div>
										<div class="col-md-5">																			
											<button id="exportPaymentReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="owner.export.payment.report" />'>
												<input type="hidden" id="paymentReportId" value="false"> <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="owner.export.payment.report" /></span>
											</button>
										</div>
									</div>
								</div>
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
								<div class="row">
									<div class="col_12">
										<div class="row">
											<div class="col-xs-12">
												<div class="form-group col-md-12 bordered-row">
													<div class="ph_tabel_wrapper scrolableTable_list" style="margin-top: 10px;" >
														<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
															<thead>
																<tr class="tableHeaderWithSearch">
																		<th search-type="" class="checkbox-stylling width_100 width_100_fix align-left"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
																		<th search-type="text">Company Name</th>
																		<th search-type="text" class="align-left">Country</th>
																		<th search-type="text" class="align-left">Transaction Id</th>
																		<th search-type="" class="align-right">Amount</th>
																		<th search-type="" class="align-left">Plan</th>
																		<th search-type="" class="width_200 width_200_fix" class="align-left">Transaction Time</th>
																		<th search-type="select" search-options="statusList" class="align-left">Status</th>
																</tr>
															</thead>
														</table>
													</div>
													<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
												</div>
											</div>
										</div>
									</div>
							</form:form>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%-- <script type="text/javascript" src="<c:url value="/resources/js/view/paymentTransaction.js"/>"></script>
 --%>
<script type="text/javascript">
	$('document').ready(function() {

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
				"url" : getContextPath() + "/owner/paymentTransaction/paymentTransactionData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
				},
				"error": function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				},
				"complete" : function() {
					$('[data-toggle="tooltip"]').tooltip();
					$('#loading').hide();
					$('.error-range.text-danger').remove();
					if($('.custom-checkAllbox').is(":checked")){
						$("[type=checkbox]").each(function() {
							$(".custom-checkbox1").prop('checked', true);
							$.uniform.update($(this));
						});
					} 
				}
			},
			"order" : [],
			"columns" : [{
				'searchable' : false,
				'orderable' : false,
				'className' : 'checkbox-stylling',
				'render' : function(data, type, row) {
					return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="paymentIds" name="paymentIds">';
				}
			},{
				"data" : "companyName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "countryCode",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "referenceTransactionId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "totalPriceAmount",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					var ret =  row.currencyCode + ' ' + (row.totalPriceAmount ? row.totalPriceAmount : 'UNKNOWN');
					return ret;
				}
			}, {
				"data" : "plan.planName",
				"defaultContent" : "",
				"className" : "align-left",
				"render" : function(data, type, row) {
					var ret = '';
					if(row.buyerPlan != null) {
						ret = row.buyerPlan.planName;
					}
					if(row.supplierPlan != null) {
						ret = row.supplierPlan.planName;
					}
					return ret;
				}
			}, {
				"data" : "createdDate",
				"className" : "align-left",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "status",
				"className" : "align-left",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					var ret =  row.status.replace('_', ' ');
					return ret;
				}
			} ],
		
		"initComplete": function(settings, json) {
			table.on( 'search.dt', function () { console.log('serching...'); $('div[id=idGlobalError]').hide();} );
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if($(this).attr('search-type') == 'select'){
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="'+$(this).attr("style")+'"><select data-index="' + i + '"  name="' + (title.replace(/ /g, "")).toLowerCase() + '"><option value=""> '+title+'</option>';
					if(optionsType == 'transactionTypeList'){
						<c:forEach items="${transactionTypeList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					if(optionsType == 'statusList'){
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i + '" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			var searchLength = 2;
			if($(this).attr('placeholder') === 'Search Country'){
				searchLength = 1;
			}
			if ($.trim(this.value).length > searchLength || this.value.length == 0) {
				table.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(table.table().container()).on('change', 'thead select', function() {
			table.column($(this).data('index')).search(this.value).draw();
		});
		$('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {

			table.ajax.reload();

		});

		$("#resetDate").click(function(e) {
			e.preventDefault();
			if ($("#datepicker-date-time-nodisable").val() !== '') {
				location.reload();
			}
			$("#datepicker-date-time-nodisable").val('');
		});
		$("#exportPaymentReport").click(function(e) {

			$('.error-range.text-danger').remove();
			var val = [];
			$('.custom-checkbox1:checked').each(

			function(i) {
				val[i] = $(this).val();

			});

			console.log(val + "val");
			e.preventDefault();

			$('.error-range.text-danger').remove();

			if (typeof val === 'undefined' || val == '') {

				$('#exportPaymentReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Payment Transaction</p>');
				return false;
			} else {
				$('#exportPaymentReportForm').submit();
			}

		});

		$('.custom-checkAllbox').on('change', function() {
			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$(".custom-checkbox1").prop('checked', check);
				$.uniform.update($(this));
			});
		});
		$(document).on('change','.custom-checkbox1',function() {
			var check = this.checked;
			if(!check){
				$(".custom-checkAllbox").prop('checked', false);
				$.uniform.update($('.custom-checkAllbox'));
			}
		});
		
		}
		});
	});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>