<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta charset="UTF-8">
<!--[if IE]><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><![endif]-->
<title></title>
<meta name="description" content="">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<sec:authorize access="hasRole('ADMIN')" var="canEdit" />
<sec:authentication property="principal.buyer" var="buyer" />
<sec:authentication property="principal.supplier" var="supplier" />
<sec:authentication property="principal.owner" var="owner" />
<spring:message var="buyerAuditTrailDesk"
	code="application.buyer.audit.trail" />
<spring:message var="supplierAuditTrailDesk"
	code="application.supplier.audit.trail" />
<spring:message var="ownerAuditTrailDesk"
	code="application.owner.audit.trail" />
<script>
zE(function() {
	<c:if test="${buyer != null}">
	zE.setHelpCenterSuggestions({ labels: [${buyerAuditTrailDesk}] });
</c:if>
<c:if test="${supplier != null}">
	zE.setHelpCenterSuggestions({ labels: [${supplierAuditTrailDesk}] });
</c:if>
 <c:if test="${owner != null}">
	zE.setHelpCenterSuggestions({ labels: [${ownerAuditTrailDesk}] });
</c:if>
 });
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
</head>
<div id="page-content" view-name="auditTrail">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:if test="${buyer != null}">
				<c:url value="/buyer/buyerDashboard" var="dashboard" />
			</c:if>
			<c:if test="${supplier != null}">
				<c:url value="/supplier/supplierDashboard" var="dashboard" />
			</c:if>
			<c:if test="${owner != null}">
				<c:url value="/owner/ownerDashboard" var="dashboard" />
			</c:if>
 			<li><a href="${dashboard}"><spring:message code="application.dashboard"/></a></li>
			<li class="active"><spring:message code="audit.trail.list"/></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="audit.trail.label"/></h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<section class="index_table_block">
						<form id="command" action="${pageContext.request.contextPath}/downloadAuditTrail" method="GET">
							<div class="row">
								<div class="col-md-5 col-sm-5 col-xs-12 col-lg-4">
									<label><spring:message code="application.filter.By.Date"/></label> <input onfocus="this.blur()"
										name="dateTimeRange" data-date-start-date="0d"
										id="datepicker-date-time-nodisable"
										class="form-control for-clander-view" type="text"
										data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
								</div>
								<div class="col-md-1 col-sm-1 resetBtn-padding btn-pd">
									<spring:message code="application.reset" var="resetLabel" />
									<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
										<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
									</button>
								</div>
								<div class="col-md-5 col-sm-5 col-xs-12 col-lg-5">
									<label><spring:message code="audit.trail.filter.by.module"/></label> <select
										class="chosen-select" name="moduleType" id="moduleTypeList">
										<option value=""><spring:message code="buyercreation.all"/></option>
										<c:forEach items="${moduleTypeList}" var="item">
											<option value="${item}">${item.value}</option>
										</c:forEach>
									</select>
								</div>
								<div class="col-md-2 col-sm-2 col-xs-12 col-lg-2 col-2-div">
									<label></label>
									<button class="btn btn-primary" id="moduleTypeFilter"
										name="moduleFilter"><spring:message code="application.search"/></button>
								</div>
							</div>

							<div class="row">
								<div class="col-xs-12 ">
									<div class="ph_tabel_wrapper scrolableTable_UserList">

										<div class="row">
											<div class="col-md-12 col-sm-12">
												<div>
													<button class="btn btn-sm btn-success hvr-pop mrg-tp-10 mt-0"  data-toggle="tooltip" data-placement="top"
														data-original-title='<spring:message code="tooltip.download.audit.trail"/>'>
														<span class="glyph-icon icon-separator"> <i
															class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="auditrail.audit.trail"/></span>
													</button>
													<div>
														<input type="hidden" name="${_csrf.parameterName}"
															value="${_csrf.token}" />
													</div>
												</div>


											</div>
										</div>

										<table id="tableList"
											class="data  display table table-bordered noarrow"
											cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="select" search-options="activityList"
														class="align-left width_100 width_100_fix"><spring:message code="application.action1"/></th>
													<th search-type="text"
														class="align-left width_300 width_300_fix"><spring:message code="application.action.by"/></th>
													<th search-type=""
														class="align-left width_200 width_200_fix"><spring:message code="application.action1"/>
														<spring:message code="application.date"/></th>
													<th search-type="text" class="align-left"><spring:message code="application.description"/></th>
													<th search-type="" class="align-left"><spring:message code="audit.trail.module.type"/></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
									<div id="morris-bar-yearly" class="graph"
										style="visibility: hidden"></div>
								</div>
							</div>
						</form>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.col-2-div {
	text-align: center;
	padding-top: 4px;
}

.col-2-div button {
	min-width: 100%;
	font-size: 16px;
}
.mt-0 {
margin-top: 0 !important;
}
table.dataTable { 
    margin-top: 25px !important;
 }
.mrg-tp-10 {
	margin-top: 10px;
	float: right;
	margin-right: 10px;
	position: relative;
	top: 50px;
	position: relative;
}
</style>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
	var table = null;
	$('#daterangepicker-time').on('apply.daterangepicker', function(e, picker) {
		e.preventDefault();
		$('.error-range.text-danger').remove();
		table.ajax.reload();
	})


	$('document').ready(function() {

		// Setup - add a text input to each footer cell

		table = $('#tableList').DataTable({
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
				"url" : getContextPath() + "/auditTrailData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					console.log(d.dateTimeRange);
					d.moduleType=$("select[name='moduleType']").val();
					console.log(d.moduleType);
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
				"data" : "activity",
				"className" : "align-left",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					return row.activityStr
				},
			}, {
				"data" : "actionBy.loginId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "actionDate",
				"className" : "align-left",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "description",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data": "moduleTypeName",
				"className": "align-left",
				"searchable" : false,
				"sortable" : false,
				"defaultContent" : "",
				
				
			}],
			
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'activityList') {
						<c:forEach items="${activityList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function(e) {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {

				// ignore arrow keys
				switch (e.keyCode) {
				case 37:
					return false;
					break;
				case 38:
					return false;
					break;
				case 39:
					return false;
					break;
				case 40:
					return false;
					break;
				}
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
		
		$("#moduleTypeFilter").click(
		function(e) {
			e.preventDefault();
			$('.error-range.text-danger').remove();
			
			var dateTime= $("input[name='dateTimeRange']").val();
			
			if(dateTime === 'undefined'||dateTime == ''){
				$('#datepicker-date-time-nodisable').after('<p style="margin-top:5px;" class="error-range text-danger">Please Select Date</p>');
			return false;
		}
			table.ajax.reload();
		});
		}
	});
});
</script>
<style>
#tableList td {
	text-align: center;
}
.btn-pd {
   padding-top: 22px;
}
.btn-black {
	height: 37px;
}
</style>
<!-- daterange picker js and css start -->
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>