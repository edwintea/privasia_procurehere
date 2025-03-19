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
				<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
				<li><a href="${dashboardUrl}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="supplier.dashboard.dolist" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon"><spring:message code="supplier.dashboard.dolist" /></h2>
			</div>
			<div class="container-fluid col-md-12">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd pad_all_15">
							<section class="index_table_block">
<%-- 							  <c:url value="/buyer/ExportBuyerDoReport" var="downloadBuyerDoReport" /> --%>
<%-- 							    <form:form action="${downloadBuyerDoReport}" method="post" id="exportBuyerDoForm" ModelAttribute="doSupplierPojo"> --%>
							    <c:url value="/buyer/ExportBuyerDoCsv" var="downloadBuyerDoCsv" />
							    <form:form action="${downloadBuyerDoCsv}" method="post" id="exportBuyerDoForm" ModelAttribute="doSupplierPojo">
							   
								<div class="col-md-12 marg-top-10">
									<label><spring:message code="do.filter.By.do.Date" /></label>
									<div class="row">
										<div class="col-md-4 col-sm-8">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
										</div>
										<div class="col-md-2 resetBtn-padding">
											<button id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=<spring:message code="application.reset" />>
												<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
											</button>
										</div>
										<div class="col-md-3 flt-rgt">
<%-- 											<button id="exportBuyerDo" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="do.export.button"/>'> --%>
<!-- 												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 												</span> <span class="button-content"><spring:message code="do.export.button" /></span> --%>
<!-- 											</button> -->
											<button id="exportBuyerDoCsv" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="do.export.button"/>'>
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="do.export.button" /></span>
											</button>
										</div>
									</div>
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
								<div class="row" >
									<div class="col-xs-12">
										<div class="form-group col-md-12 bordered-row" style="margin-top: 10px;">
											<div class="ph_tabel_wrapper scrolableTable_UserList">
												<table id="tableList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
														    <th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
															<th search-type="" class="align-left width_150 width_150_fix"><spring:message code="application.action" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.doListing.deliveryId" /></th>
															<%-- <th search-type="text" class="align-left width_200 width_200_fix"> <spring:message code="supplier.doListing.referenceNumber" /></th> --%>
															<th search-type="text" class="align-left width_200 width_200_fix"> <spring:message code="supplier.doListing.deliveryName" /></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.doListing.poNumber"/></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.supplier"/></th>
															<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.doListing.businessUnit"/></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.do.summary.doDate"/></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.doListing.actionDate"/></th>
															<th search-type="" class="align-right width_150 width_150_fix"><spring:message code="supplier.doListing.currency"/></th>
															<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="supplier.doListing.grandtotal"/></th>
															<th search-type="select"   search-options="doStatusList"class="align-center width_200 width_200_fix"><spring:message code="supplier.doListing.doStatus"/></th>
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
							   </form:form>	
							</section>
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

#tableList th {
	text-align: left;
}

.resetBtn-padding {
	padding-top: 3px;
	padding-left: 0px;
}

.excel_icon {
	background: url(../image-resources/image-procurehere/excel-icon.png)
		no-repeat 0 0;
	width: 24px;
	height: 26px;
	display: inline-block;
	margin-right: 4px;
	vertical-align: middle;
}

.shareIcon {
	line-height: 40px;
	margin: 10px;
	text-align: center;
	color: #92A0AE;
}

.flt-rgt {
float: right;
}

.h-37 {
	height: 37px;
}
.f-r {
	float: right;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
	<script type="text/javascript">
		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
	</script>
	<script type="text/javascript">
		$('document').ready(function() {

			// Setup - add a text input to each footer cell
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

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
					// in case your overlay needs to be put away automatically you can put it here
					$("#poReportId").val(false);
					$('#loading').hide();
				},
				"serverSide" : true,
				"pageLength" : 10,
				"searching" : true,
				"ajax" : {
					"url" : getContextPath() + "/buyer/doListData",
					"data" : function(d) {
						d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"columns" : [ 
					{
			         	'searchable': false,
			         	'orderable': false,
			         	'className': 'checkbox-stylling',
			         	'mRender': function (data, type, row){
			             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="doIds" name="doIds">';
			        	 }
					},{
					"mData" : "id",
					"searchable" : false,
					"orderable" : false,
					"mRender" : function(data, type, row) {
						var action = '<a href="deliveryOrderView/' + row.id + '" data-placement="top" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
						return action;
					}
				},{
					"data" : "deliveryId",
					"defaultContent" : ""
				},{
					"data" : "name",
					"defaultContent" : ""
				},{
					"data" : "poNumber",
					"defaultContent" : ""
				},{
					"data" : "supplierCompanyName",
					"defaultContent" : ""
				},{
					"data" : "businessUnit",
					"defaultContent" : ""
				},{
					"data" : "sendDate",
					"searchable" : false,
					"type" : 'custom-date',
					"defaultContent" : ""
				},{
					"data" : "actionDate",
					"searchable" : false,
					"type" : 'custom-date',
					"defaultContent" : ""
				},{
					"data" : "currency",
					"defaultContent" : "",
					"searchable" : false,
					"className" : "align-right"

				},{
					"data" : "grandTotal",
					"className" : "align-right",
					"defaultContent" : "",
					"mRender" : function(data, type, row) {
						return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
					}
				}, {
					"data" : "status",
					"defaultContent" : "",
					"className" : "align-center"

				}],
			"initComplete": function(settings, json) {
			var htmlSearch = '<tr class="tableHeaderWithSearch">';
			$('#tableList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlSearch += '<th style="' + $(this).attr("style") + ';text-align: center"><select name="'+(title.replace(/ /g,"")).toLowerCase()+'" data-index="'+i+'">';
						if (optionsType == 'doStatusList') {
							htmlSearch += '<option value="">All Status</option>';
							<c:forEach items="${doStatusList}" var="item">
							htmlSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlSearch += '</select></th>';
					} else {
						htmlSearch += '<th ><input type="text" name="'+(title.replace(/ /g,"")).toLowerCase()+'" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
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
				table.ajax.reload();
			});

			$("#resetDate").click(function(e) {
				e.preventDefault();
				if ($("#datepicker-date-time-nodisable").val() !== '') {
					$("#datepicker-date-time-nodisable").val('');
					table.ajax.reload();
				}
			});
			}
			});

		});
		function ReplaceNumberWithCommas(yourNumber) {
			// Seperates the components of the number
			var n = yourNumber.toString().split(".");
			// Comma-fies the first part
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			// Combines the two sections
			return n.join(".");
		}
		
		$("#exportBuyerDo")
		.click(
				function(e) {
					e.preventDefault();
					messege='Please select atleast one DO';
						
					$('.error-range.text-danger')
							.remove();
					var val = [];
					$('.custom-checkbox1:checked').each(
							function(i) {
								val[i] = $(this).val();
							});
					console.log(val + "val");

					if (typeof val === 'undefined'
							|| val == '') {
						console.log("Error");
						$.jGrowl(messege, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});      
						return false;
					} else {
						
						$('#exportBuyerDoForm')
								.submit();
					}
				});
		
		$("#exportBuyerDoCsv").click(function(e) {
					e.preventDefault();
					messege='Please select atleast one DO';
						
					$('.error-range.text-danger')
							.remove();
					var val = [];
					$('.custom-checkbox1:checked').each(
							function(i) {
								val[i] = $(this).val();
							});
					console.log(val + "val");

					if (typeof val === 'undefined'
							|| val == '') {
						console.log("Error");
						$.jGrowl(messege, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});      
						return false;
					} else {
						$('#exportBuyerDoForm')
								.submit();
					}
				});
		
		
		$('.custom-checkAllbox').on('change', function() {
			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$(".custom-checkbox1").prop('checked', check);
				$.uniform.update($(this));
			});
		});
	</script>

	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>