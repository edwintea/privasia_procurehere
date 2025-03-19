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
<div id="page-content" view-name="auctionReport">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a>
			</li>
			<li class="active">
				<spring:message code="buyer.auctionreport.label" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="buyer.auctionReport.name" />
			</h2>
		</div>

		<div class="container-fluid col-md-12"></div>
		<div class="clear"></div>


		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">

<%-- 							<c:url value="/buyer/ExportAuctionReport" var="downloadAuctionReport" /> --%>
<%-- 							<form:form action="${downloadAuctionReport}" method="post" id="exportAuctionForm" ModelAttribute="searchFilterEventPojo"> --%>
							<c:url value="/buyer/exportAuctionCsvReport" var="downloadAuctionCsvReport" />
							<form:form action="${downloadAuctionCsvReport}" method="post" id="exportAuctionForm" ModelAttribute="searchFilterEventPojo">

								<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">																	
									<div class="row">
										<div class="col-md-5 col-sm-8 col-lg-5">
											<label><spring:message code="application.filter.By.Date" /></label>
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
										</div>
										<div class="col-md-2 col-sm-8 col-lg-2">
										<label class=""><spring:message code="auctionReport.filter.by.auction" /></label>
											<select class="chosen-select" name="auctionTypeS" id="auctionsTypeList">
<!-- 												 <option value=""></option>  -->
												<c:forEach items="${auctionsTypeList}" var="item">
													<option value="${item}">${item}</option>
												</c:forEach>
											</select>
										</div>
										<div style="width: 200px;margin-top: 23px;" class="col-md-2 col-sm-2 col-xs-12 col-lg-2 col-2-div">
											<label></label>
											<button class="btn btn-primary" id="auctionTypeFilter" name="auctionFilter">
												<spring:message code="application.search" />
											</button>
										</div>
<!-- 										<div class="col-md-3"> -->
<%-- 											<button style="margin-top: 23px" id="exportAuctionReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="auctionReport.export.button"/>'> --%>
<!-- 												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 												</span> <span class="button-content"><spring:message code="auctionReport.export.button" /></span> --%>
<!-- 											</button> -->
<!-- 										</div> -->

										<div class="col-md-3">
											<button style="margin-top: 23px" id="exportAuctionCsvReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="auctionReport.export.button"/>'>
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="auctionReport.export.button" /></span>
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
															<th class="align-left width_100_fix" search-type=""><spring:message code="application.action1" /></th>
															<th search-type="text" class="awidth_200 width_200_fix align-left"><spring:message code="label.businessUnit" /></th>
															<th search-type="" class="awidth_200 width_200_fix align-left"><spring:message code="buyer.eventReport.eventcategories" /></th>
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="application.referencenumber" /></th>
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="application.eventid" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="application.eventstartdate" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="application.eventenddate" /></th>
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="buyer.auctionReport.auctionName" /></th>
															<th style="text-align: left;" search-type="text" class="width_200 width_200_fix align-left"><spring:message code="buyer.auctionReport.template" /></th>
															<th search-type="select" style="text-align: left;" search-options="eventTypeList" class="align-center width_150_fix" class="width_200 width_200_fix align-left"><spring:message code="application.eventtype" /></th>
															<th search-type="text" style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="application.eventowner" /></th>
															<%--          	<th search-type="select" search-options="statusList" class="align-center width_150_fix" class="width_200 width_200_fix align-left"><spring:message code="application.status" /></th> --%>
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
				"url" : getContextPath() + "/buyer/auctionReportList",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					d.auctionTypeS = $("#auctionsTypeList").val();
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
		             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="eventIds" name="eventIds">';
		        	 }
				},
				{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var type = row.type;
					return '<a href="${pageContext.request.contextPath}/buyer/RFA/eventSummary/'+row.id+'" data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"> </a>'
				}
			},
			 {
				"data" : "unitName",
				"className" : "align-left",
				"defaultContent" : ""
			 },
			 {
					"data" : "eventCategories",
					"className" : "align-left",
					'orderable': false,
					"defaultContent" : ""
				 },
			{
				"data" : "referanceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},
			{
				"data" : "sysEventId",
				"className" : "align-left",
				"defaultContent" : ""
			},
			{
				"data" : "eventStart",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			},
			{
				"data" : "eventEnd",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, 
				{
				"data" : "eventName",
				"className" : "align-left",
				"defaultContent" : ""
			},
			{
				"data" : "templateName", 
				"className" : "align-left",
				"defaultContent" : ""
			},
			{
				"data" : "eventVisibility", 
				"defaultContent" : "",
				"className" : "align-left",
				'orderable': false
			},
			{
				"data" : "eventUser",
				"className" : "align-left",
				"defaultContent" : ""
			 },
			
			],
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if($(this).attr('search-type') == 'select'){
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"  name="'+(title.replace(/ /g,"")).toLowerCase()+'"><option value=""><spring:message code="buyercreation.all.case"/></option>';
					
					if (optionsType == 'eventTypeList') {
						<c:forEach items="${eventTypeList}" var="item">
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
					//table.ajax.reload();
		});
		
// 		$("#resetDate").click(
// 				function(e) {
// 					e.preventDefault();
// 					if ($("#datepicker-date-time-nodisable")
// 							.val() !== '') {
// 						location.reload();
// 					}
// 					$("#datepicker-date-time-nodisable")
// 							.val('');
// 				});
		$('.custom-checkAllbox').on('change', function() {
			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$(".custom-checkbox1").prop('checked', check);
				$.uniform.update($(this));
			});
		});
		
		}
	});
		
		 $("#exportAuctionReport")
			.click(
					function(e) {
						e.preventDefault();
						messege='Please Select atleast one Event';
							
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
// 							$('#exportAuctionReport')
// 									.after(
// 											'<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Event</p>');  
							return false;
						} else {
							
							$('#exportAuctionForm')
									.submit();
						}
					});
					
					
		 
		
		$("#auctionTypeFilter").click(
				function(e) {
					e.preventDefault();
					$('.error-range.text-danger').remove();
					
					var dateTime= $("input[name='dateTimeRange']").val();     //auctionType
					var actType=$("select[name='auctionTypeS']").val();
					console.log("Auction type--"+actType);
					
					if(dateTime === 'undefined'||dateTime == ''){
						$('#datepicker-date-time-nodisable').after('<p style="margin-top:5px;" class="error-range text-danger">Please Select Date</p>');
					return false;
				}
					if(actType === 'undefined'||actType == ''){
						$('#auctionsTypeList').after('<p style="margin-top:5px;" class="error-range text-danger">Please Select Auction Type</p>');	
						return false;
				}	
					table.ajax.reload();
				});
		
				$("#exportAuctionCsvReport")
					.click(
							function(e) {
								e.preventDefault();
								messege='Please Select atleast one Event';
									
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
//		 							$('#exportAuctionReport')
//		 									.after(
//		 											'<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Event</p>');  
									return false;
								} else {
									
									$('#exportAuctionForm')
											.submit();
								}
				});					
	});
</script>




<style>

.wd-25 {
    width : 300px;
}

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

.f-r-mr {
  float: right;
  margin-right: 758px;
 }
</style>
<!-- <div class="modal fade" id="myModal" role="dialog"> -->
<!-- 	<div class="modal-dialog for-delete-all reminder"> -->
<!-- 		<!-- Modal content-->
-->
<!-- 		<div class="modal-content"> -->
<!-- 			<div class="modal-header"> -->
<!-- 				<h3> -->
<%-- 					<spring:message code="application.confirm.delete" /> --%>
<!-- 				</h3> -->
<!-- 				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">�</button> -->
<!-- 			</div> -->
<!-- 			<div class="modal-body"> -->
<%-- 				<label> <spring:message code="rfxTemplate.delete" /> --%>
<!-- 				</label> -->
<!-- 			</div> -->
<!-- 			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1"> -->
<%-- 				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/rfxTemplate/deleteRfxTemplate?id=" title=<spring:message code="tooltip.delete" />> <spring:message code="application.delete" /> --%>
<!-- 				</a> -->
<!-- 				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"> -->
<%-- 					<spring:message code="application.cancel" /> --%>
<!-- 				</button> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 	</div> -->
<!-- </div> -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>