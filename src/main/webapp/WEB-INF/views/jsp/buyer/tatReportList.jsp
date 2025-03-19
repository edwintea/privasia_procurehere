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
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxTemplateList}] });
});
</script>
<div id="page-content" view-name="eventReport">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li><a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a></li>
			<li class="active"><spring:message code="defaultmenu.tat.report.menu" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="defaultmenu.tat.report.menu" />
			</h2>
		</div>

		<div class="container-fluid col-md-12"></div>
		<div class="clear"></div>


		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />

			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<c:url value="/tatReport/exportTatCsvReport" var="downloadCsvReport" />
							<form:form action="${downloadCsvReport}" method="post" id="exportEventForm" ModelAttribute="tatReport">
								<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
									<label><spring:message code="application.filter.By.Date" /></label>
									<div class="row">
										<div class="col-md-4 col-xl-3">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy - dd/mm/yyyy" />
										</div>
										<div class="col-md-8 col-xl-9">
											<button id="exportCsvReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tatCsv.export.button"/>'>
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="tatCsv.export.button" /></span>
											</button>
										</div>
									</div>
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="row">
									<div class="col-xs-12">
										<div class="form-group col-md-12 bordered-row">
											<div class="ph_tabel_wrapper scrolableTable_list">
												<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.formId" /></th>
															<th search-type="select" search-options="requestStatusList" class="align-center width_200 width_200_fix "><spring:message code="table.tat.heading.reqStatus" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.reqCreatedDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.reqLastApprvDate" /></th>
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventId" /></th>
															<th search-type="select" search-options="eventStatusList" class="align-center width_200 width_200_fix "><spring:message code="table.tat.heading.eventStatus" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventCreatedDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventLastApprvDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventStartDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventEndDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.eventMeetingDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.evalCompleteDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.unmaskingDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.concludeDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.awardDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.sapPoDate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="table.tat.heading.paperApprDays" /></th>
															<th search-type="" class="width_150 width_150_fix align-left"><spring:message code="table.tat.heading.overAllTat" /></th>

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
<script type="text/javascript" src="<c:url value="/resources/js/view/rfxTemplate.js"/>"></script>

<script type="text/javascript">
	var table = null;
	$('#daterangepicker-time').on('apply.daterangepicker', function(e, picker) {
		e.preventDefault();
		$('.error-range.text-danger').remove();
		table.ajax.reload();
	})

	function cb(start, end) {
        $('#datepicker-date-time-nodisable div').html(start.format('DD/MM/YYYY') + ' - ' + end.format('DD/MM/YYYY'));
	}

	$('document').ready(function() {

		$('#datepicker-date-time-nodisable').daterangepicker({
			"format" : 'DD/MM/YYYY',
			"startDate" : moment().subtract(90, 'days'),
			"endDate" : moment(),
			"maxDate": moment(),
			"autoUpdateInput": false,
		}, cb);
		
		cb(moment().subtract(90, 'days'), moment());
		


		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				$('div[id=idGlobalError]').hide();
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/tatReport/tatReportListData",
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
			"order" : [[2, "asc"]],
			"columns" : [ 
				{
					"data" : "formId",
					"className" : "align-left",
					"defaultContent" : ""
			},{
				"data" : "requestStatus",
				"className" : "align-center",
				"defaultContent" : "",
				'orderable': false
			},{
				"data" : "createdDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "lastApprovedDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "status",
				"className" : "align-center",
				"defaultContent" : "",
				'orderable': false
			},{
				"data" : "eventCreatedDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventLastApprovedDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventStart",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventEnd",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventFirstMeetingDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "evaluationCompletedDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "unmskingDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventConcludeDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "eventAwardDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "sapPocreatedDate",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "paperApprovalDaysCount",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "overallTat",
				"className" : "align-left",
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
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"  name="'+(title.replace(/ /g,"")).toLowerCase()+'">';
					
					if (optionsType == 'requestStatusList') {
						<c:forEach items="${requestStatusList}" var="item1">
						htmlSearch += '<option value="${item1}">${item1}</option>';
						</c:forEach>
						htmlSearch += '<option value="ALL">ALL</option>';
					}
				
					if (optionsType == 'eventStatusList') {
						htmlSearch += '<option value="">ALL</option>';
						<c:forEach items="${eventStatusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
				
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="'+$(this).attr("style")+'"><input type="text" name="'+(title.replace(/ /g,"")).toLowerCase()+'" placeholder="<spring:message code="buyercreation.search.case"/> '+title+'" data-index="'+i+'" /></th>';
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
		
		
		$('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {
			e.preventDefault();
			$('.error-range.text-danger').remove();
			table.ajax.reload();
		});
		
		$("#exportCsvReport").click(function(e) {
				e.preventDefault();
				$('.error-range.text-danger').remove();
				
				var val = [];
				$('.custom-checkbox1:checked').each(
					function(i) {
					val[i] = $(this).val();
				});
				console.log(val + "val");

				$('#exportEventForm').submit();
		});					
		}
	});
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
.btn-black {
	height: 37px;
}
.h-37 {
	height: 37px;
}
.line-h-35 {
	line-height: 35px;
}
.f-r {
		float: right;
	}
</style>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
