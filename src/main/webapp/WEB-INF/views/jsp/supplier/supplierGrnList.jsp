<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/supplier/supplierDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="grn.list.grnList.label" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="grn.list.grnList.label" /></h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

			<div class="clear"></div>


			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<c:url value="/supplier/exportSupplierGrnReport" var="downloadGrnReport" />
							<form:form action="${downloadGrnReport}" method="post" id="exportGrnReportForm" ModelAttribute="searchFilterGrnPojo">

								<div class="col-md-12 marg-top-10 mb-10">
									<label><spring:message code="application.filter.By.Date" /></label>
									<div class="row">
										<div class="col-md-5 col-sm-8 col-lg-5">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" required />
										</div>
										<div class="col-md-2 col-sm-4 resetBtn-padding">
											<spring:message code="application.reset"  var="resetLabel"/>
											<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
												<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
											</button>
										</div>
										<div class="col-md-5">
											<button id="exportGrnReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="grn.export.report" />'>
												<input type="hidden" id="grnReportId" value="false"> <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="grn.export.report" /></span>
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
												<table id="grnList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
															<th search-type="" class="width_100_fix"><spring:message code="application.action" /></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="grn.list.grn.number"/></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="grn.list.grn.name"/></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="applications.buyer" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="grn.list.grn.createddate"/></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="grn.list.grn.acceptRejectDate"/></th>
															<th search-type="" class="align-right width_150 width_150_fix"><spring:message code="grn.list.grn.currency" /></th>
															<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="grn.list.grn.grandtotal" /></th>
															<th search-type="select" search-options="grnStatusList"  class="align-center width_150_fix" class="width_200 width_200_fix align-left"><spring:message code="grn.list.grn.status"/></th>
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
<style>
.checkbox-stylling input {
	width: 15px !important;
	height: 15px !important;
	text-align: center !important;
}
td {
	text-align: center !important;
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
.mb-10 {
	margin-bottom: 10px;
}
</style>

<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<input type="hidden" id="delId" value="" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/js/view/rfxTemplate.js"/>"></script>
<script type="text/javascript">
	$('document').ready(function() {

		// Setup - add a text input to each footer cell

		var table = $('#grnList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
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
				"url" : getContextPath() + "/supplier/supplierGrnListData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					console.log("Sending dateTimeRange: " + d.dateTimeRange);  // Debug log
				},
				"error" : function(request, textStatus, errorThrown) {
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
					return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="grnIds" name="grnIds">';
				}
			},{
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"mRender" : function(data, type, row) {
					var action = '<div style="display: flex;"><a href="supplierGrnView/' + row.id + '"  title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
					if(row.status=='ACCEPTED'){
						action += '<a href="downloadGrnReport/' + row.id + '"  title=<spring:message code="tooltip.download" />><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a></div>';
					}
					return action;
				}
			}, {
				"data" : "grnId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "grnTitle",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "buyerCompanyName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"className" : "align-left",
				"type" : 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "acceptRejectDate",
				"className" : "align-left",
				"searchable" : false,
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "currency",
				"className" : "align-right",
				"defaultContent" : ""
			},{
				"data" : "grandTotal",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
				}
			}, {
				"data" : "status",
				"className" : "align-left",
				"defaultContent" : "",

			}],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#grnList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="' + i + '"  name="' + (title.replace(/ /g, "")).toLowerCase() + '"><option value="">ALL</option>';

					if (optionsType == 'grnStatusList') {
						<c:forEach items="${grnStatusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i + '" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#grnList thead').append(htmlSearch);
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
				table.ajax.reload();
			});

		$("#resetDate").click(function(e) {
			e.preventDefault();
			if ($("#datepicker-date-time-nodisable").val() !== '') {
				location.reload();
			}
			$("#datepicker-date-time-nodisable").val('');
		});

		$("#exportGrnReport").click(function(e) {

			$('.error-range.text-danger').remove();
			var val = [];
			$('.custom-checkbox1:checked').each(

			function(i) {
				val[i] = $(this).val();

			});

			console.log(val + "val");
			e.preventDefault();
			//var val=$("input[name='dateTimeRange']").val();

			$('.error-range.text-danger').remove();

			if (typeof val === 'undefined' || val == '') {

				$('#exportGrnReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one GRN</p>');
				return false;
			} else {
				$('#exportGrnReportForm').submit();
			}

		});

		$('.custom-checkAllbox').on('change', function() {
			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$(".custom-checkbox1").prop('checked', check);
				$.uniform.update($(this));
			});
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