<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxTemplateList" code="application.rfx.template.list" />
<style>
#tableList_length {
	margin-top: 15px;
}
</style>
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxTemplateList}] });
});
</script>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a>
			</li>
			<li class="active">
				<spring:message code="sourcingform.list.title" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="sourcingform.list.label" />
			</h2>
		</div>

		<div class="container-fluid col-md-12"></div>
		<div class="clear"></div>


		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row"></div>



			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">

<%-- 							<c:url value="/buyer/ExportSourcingReport" var="downloadSourcingReport" />  --%>
<%-- 							<form:form action="${downloadSourcingReport}" method="post" id="exportSourcingForm" ModelAttribute="sourcingFormRequestPojo"> --%>
							<c:url value="/buyer/exportSourcingCsvReport" var="downloadSourcingReportCsv" /> 
							<form:form action="${downloadSourcingReportCsv}" method="post" id="exportSourcingForm" ModelAttribute="sourcingFormRequestPojo">

								<div class="col-md-5 col-sm-5 col-xs-12 col-lg-5">
									<label><spring:message code="application.filter.By.Date" /></label> <input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
								</div>
								<div class="col-md-2 col-sm-4 resetBtn-padding btn-pd">
									<spring:message code="application.reset" var="resetLabel" />
									<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
										<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
									</button>
								</div>
<!-- 								<div class="col-md-5" style="display: flex; justify-content: flex-end; margin-top: 25px;"> -->
<%-- 									<button id="exportSourcingReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="sourcingReport.export.button"/>'> --%>
<!-- 										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 										</span> <span class="button-content"><spring:message code="sourcingReport.export.button" /></span> --%>
<!-- 									</button> -->
<!-- 								</div> -->
								
								<div class="col-md-5" style="display: flex; justify-content: flex-end; margin-top: 25px;">
									<button id="exportSourcingReportCsv" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="sourcingReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
										</span> <span class="button-content"><spring:message code="sourcingReport.export.button" /></span>
									</button>
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
								<div class="row">
									<div class="col-xs-12">
										<div class="form-group col-md-12 bordered-row">
											<div class="ph_tabel_wrapper scrolableTable_list">
												<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
															<th search-type="" class="align-left width_100_fix"><spring:message code="application.action1" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.nameofrequest" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.referencenumber" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.sourcing.requestid" /></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createdby" /></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.request.createddate" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.requestowner" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.costcenter" /></th>
															<th search-type="select" search-options="sourcingFormStatusList" class="align-left width_150_fix" class="width_200 width_200_fix align-left"><spring:message code="application.status" /></th>

														</tr>
													</thead>
													<tbody id="tableBody"></tbody>
												</table>

											</div>
											<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
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
<input type="hidden" id="delId" value="" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/js/view/rfxTemplate.js"/>"></script>

<script type="text/javascript">
var table = null;
$('#daterangepicker-time').on('apply.daterangepicker', function(e, picker) {
	e.preventDefault();
	$('.error-range.text-danger').remove();
	table.ajax.reload();
})

	$('document').ready(function() {

		
		// Setup - add a text input to each footer cell
		
		/* var firstRow = $('#tableList thead tr:nth-child(1)').html();
		var secondRow = $('#tableList thead tr:nth-child(2)').html();
		$('#tableList thead tr:nth-child(1)').html(secondRow);
		$('#tableList thead tr:nth-child(2)').html(firstRow); */
		
		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#select-all').prop('checked', false);
	         	$('#tableBody input[type="checkbox"]:checked').each(function() {
	         	$(this).prop('checked', false);
	    		});
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
				"url" : getContextPath() + "/buyer/sourcingFormList",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					console.log(d.dateTimeRange);
				},
				"error": function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order" : [],
			"columns" : [ 
				{
		         	'searchable': false,
		         	'orderable': false,
		         	'className': 'checkbox-stylling',
		         	'render': function (data, type, row){
		             return '<input type="checkbox" class="custom-checkbox1 eventIdsClass" value="'+row.id+'" id="eventIds" name="eventIds">';
		        	 }
				},
				{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/createSourcingFormDetails/'+row.id+'"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
				}
				}, {
					"data" : "sourcingFormName",
					"className" : "align-left",
					"defaultContent" : ""
				},{
					"data" : "referanceNumber",
					"className" : "align-left",
					"defaultContent" : ""
				},{
					"data" : "formId",
					"className" : "align-left",
					"defaultContent" : ""
				}, {
					"data" : "createdBy",
					"className" : "align-left",
					"defaultContent" : ""
				}, {
					"data" : "createdDate",
					"className" : "align-left",
					"type": 'custom-date',
					"defaultContent" : ""
				},{
					"data" : "formOwner",
					"className" : "align-left",
					"defaultContent" : ""
				},{
					"data" : "businessunit",
					"className" : "align-center",
					"defaultContent" : ""
				 } 
				,{
					"data" : "costcenter",
					"className" : "align-center",
					"defaultContent" : ""
				 } 
				,{
					"data" : "status",
					"className" : "align-center",
					"defaultContent" : ""
				 } 
			
			],
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if($(this).attr('search-type') == 'select'){
					var optionsType = $(this).attr('search-options');
					
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"  name="'+(title.replace(/ /g,"")).toLowerCase()+'"><option value=""><spring:message code="application.all2"/></option>';
					
					
					if (optionsType == 'rfxList') {
						<c:forEach items="${rfxList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
				
					if (optionsType == 'sourcingFormStatusList') {
						<c:forEach items="${sourcingFormStatusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
				
					
				
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="'+$(this).attr("style")+'"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
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

		$('#datepicker-date-time-nodisable').daterangepicker({
			autoUpdateInput: false, // Ensures input is updated manually on apply
		}, function (start, end) {
			$('#datepicker-date-time-nodisable').val(
				start.format('DD/MM/YYYY hh:mm A') + ' - ' + end.format('DD/MM/YYYY hh:mm A')
			);
		});


			$('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {
					e.preventDefault();
					$('.error-range.text-danger').remove();
					table.ajax.reload();
		});
		
		$("#resetDate").click(
				function(e) {
					e.preventDefault();
					if ($("#datepicker-date-time-nodisable")
							.val() !== '') {
						location.reload();
					}
					$("#datepicker-date-time-nodisable")
							.val('');
				});
		$('.custom-checkAllbox').on('change', function() {
			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$(".custom-checkbox1").prop('checked', check);
				$.uniform.update($(this));
			});
		});
		
		 $("#exportSourcingReport")
			.click(
					function(e) {
						e.preventDefault();

						$('.error-range.text-danger')
								.remove();
						var val = [];
						$(':checkbox:checked').each(
								function(i) {
									val[i] = $(this).val();
								});
						console.log(val + "val");

						if (typeof val === 'undefined'
								|| val == '') {
							console.log("Error");
							$('#exportSourcingReport')
									.before(
											'<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Sourcing Request</p>');
							return false;
						} else {
							$('#exportSourcingForm')
									.submit();
						}

					});
		
		
		$("#exportSourcingReportCsv")
		.click(
				function(e) {
					e.preventDefault();

					$('.error-range.text-danger')
							.remove();
					var val = [];
					$(':checkbox:checked').each(
							function(i) {
								val[i] = $(this).val();
							});
					console.log(val + "val");

					if (typeof val === 'undefined'
							|| val == '') {
						console.log("Error");
						$('#exportSourcingReportCsv')
								.before(
										'<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Sourcing Request</p>');
						return false;
					} else {
						$('#exportSourcingForm')
								.submit();
					}

				});
		}
		});
	/* 	$('#select-all').on('click', function(){
		      var rows = tableList.rows({ 'search': 'applied' }).nodes();
		      $('input[type="checkbox"]', rows).prop('checked', this.checked);
		   }); */
// 		$('#select-all').on('change', function(){  
// 			if($("#select-all").is(':checked')){
// 				$('.eventIdsClass').prop('checked', true);
// 			} else {
// 				$('.eventIdsClass').prop('checked', false);
// 			}
// 		});
		   
// 		$(document).delegate('.eventIdsClass', "change", function(){ 
// 			if(!$(this).is(':checked')){
// 				$('#select-all').prop('checked', false);	
// 			}  
//         });
	});
</script>




<style>
.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0cb6ff none repeat scroll 0 0 !important;
	color: #fff;
	border-color: #0095d5;
}

.btn-pd {
	padding-top: 22px;
}

.btn-black {
	height: 37px;
}
</style>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="rfxTemplate.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/rfxTemplate/deleteRfxTemplate?id=" title=<spring:message code="tooltip.delete" />> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>