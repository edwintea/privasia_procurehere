<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="spEvaluationListDesk" code="application.sp.evaluation.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${spEvaluationListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
<style>
.width_fix {
	min-width: 120px !important;
}
.width_180_fix {
	min-width: 180px !important;
}
</style>
<div id="page-content" view-name="supplierPerformance">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href='<c:url value="buyerDashboard"/>'> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="sp.evaluation.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="sp.evaluation.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<c:url value="/buyer/exportSPEvaluationCsvReport" var="downloadCsvReport" />
							<form:form action="${downloadCsvReport}" method="post" id="exportEventForm" autocomplete="off" ModelAttribute="searchFilterPerformanceEvaluationPojo">
								<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
									<label><spring:message code="application.filter.By.Date" /></label>
									<div class="row">
										<div class="col-md-5 col-sm-8 col-lg-5">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
										</div>
										<div class="col-md-2 col-sm-4 resetBtn-padding">
											<spring:message code="application.reset" var="resetLabel" />
											<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
												<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
											</button>
										</div>

										<div class="col-md-5">
											<button id="exportCsvReport" class="btn btn-sm btn-success hvr-pop f-r h-37 float-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="performance.evaluation.csv.export.button"/>'>
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="performance.evaluation.csv.export.button" /></span>
											</button>
										</div>
									</div>
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
															<th search-type="" class="align-left width_100_fix"><spring:message code="application.action" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="defultMenu.sp.form.id" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="defultMenu.sp.form.name" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="application.referencenumber" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="defultMenu.sp.ref.name" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="defultMenu.sp.form.creator" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="label.procurement.category" /></th>
															<th search-type="text" class="width_100_fix"><spring:message code="label.businessUnit" /></th>

															<th search-type="text" class="width_100_fix"><spring:message code="supplier.name" /></th>
															<th search-type="" class="width_fix"><spring:message code="defultMenu.sp.total.evaluator" /></th>
															<th search-type="" style="text-align: left !important;min-width: 180px !important;"><spring:message code="defultMenu.sp.total.evaluator.complete" /></th>
															<th search-type="" class="width_180_fix"><spring:message code="defultMenu.sp.evaluation.start.date" /></th>
															<th search-type="" class="width_180_fix"><spring:message code="defultMenu.sp.evaluation.end.date" /></th>

															<th search-type="" class="width_100_fix"><spring:message code="defultMenu.sp.recurrent" /></th>
															<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
															<th search-type="" class="width_180_fix"><spring:message code="defultMenu.sp.conclude.date" /></th>
															<th search-type="" class="width_50_fix"><spring:message code="label.rating" /></th>
															<th search-type="" class="width_fix"><spring:message code="defultMenu.sp.overall.score" /></th>

														</tr>
													</thead>
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
<script type="text/javascript">
	$('document').ready(function() {
		console.log(" ***** ");
		
		


		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();//PH-655
				}, 20000);
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
				"url" : getContextPath() + "/buyer/supplierPerformanceListData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					console.log(d.dateTimeRange);
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
	         	'searchable': false,
	         	'orderable': false,
	         	'className': 'checkbox-stylling',
	         	'render': function (data, type, row){
	             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="formIds" name="formIds">';
	        	 }
			},{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					if(row.formStatus == 'DRAFT' || row.formStatus == 'SUSPENDED'){
						var ret = '<a href="editSupplierPerformanceEvaluation/' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					}else {
						var ret = '<a href="viewSPFSummary?formId=' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					}
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
					return ret;
				}
			}, {
				"data" : "formId",
				"defaultContent" : ""
			},{
				"data" : "formName",
				"defaultContent" : ""
			}, {
				"data" : "referenceNumber",
				"defaultContent" : ""
			}, {
				"data" : "referenceName",
				"defaultContent" : ""
			}, {
				"data" : "formOwner",
				"defaultContent" : ""
			}, {
				"data" : "procurementCategory",
				"defaultContent" : ""
			}, {
				"data" : "unitName",
				"defaultContent" : ""
			}, {
				"data" : "supplierName",
				"defaultContent" : ""
			},{
				"data" : "totalEvaluator",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "totalEvaluationComplete",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "evaluationStartDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "evaluationEndDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "isRecurrenceEvaluation",
				"searchable" : false,
				"defaultContent" : "",
				"render" : function(data, type, row) {
					if(row.isRecurrenceEvaluation){
						var ret = 'YES';
					}else {
						var ret = '';
					}
					return ret;
				}
			},  {
				"data" : "formStatus",
				"defaultContent" : ""
			}, {
				"data" : "concludeDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "scoreRating",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "overallScore",
				"searchable" : false,
				"defaultContent" : ""
			}],
			
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'" name="'+(title.replace(/ /g,"")).toLowerCase()+'"><option value=""><spring:message code="application.all2"/></option>'; 
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
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

$("#exportCsvReport").click(function(e) {
	e.preventDefault();
	$('.error-range.text-danger')
	.remove();
	var val = [];
	$('.custom-checkbox1:checked').each(
		function(i) {
		val[i] = $(this).val();
	});

	if (typeof val === 'undefined'|| val == '') {
		console.log("Error");
		$('#exportCsvReport').before('<p style="margin-top:10px;margin-left: 288px;" class="error-range text-danger">Please select atleast one performance evaluation</p>');
		return false;
	} else {
		$('#exportEventForm')
		.submit();
	}
});



		}
		});
	});
	function updateLink(id) {
		var link = $("#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}
	function doCancel() {
		var link = $("#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteSourcingTemplate?id=');
	}
</script>
<style>
.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0095d5 none repeat scroll 0 0;
	border-color: #0095d5;
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
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/deleteSourcingTemplate?id=" title=<spring:message code="tooltip.delete" />> <spring:message code="application.delete" />
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
