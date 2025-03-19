<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="po.title" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="po.title" /></h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="clear"></div>
			<div class="row">
				<div class="col_12">
				<jsp:include page="/WEB-INF/views/jsp/prpo/poDashboard.jsp" />

					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
                            <%--<c:url value="/buyer/exportPoSummaryCsvReport" var="downloadPoReport" /> --%>
							<form:form action="" method="post" id="exportPoReportForm" ModelAttribute="searchFilterPoPojo">
                                <div class="col-md-12 marg-top-10">

                                    <div class="row" style="display: flex; align-items: end; margin-bottom: 10px">
                                        <div class="col-md-4 col-sm-8">
											<label><spring:message code="application.filter.by.created.date" /></label>
                                            <input
                                              onfocus="this.blur()"
                                              name="dateTimeRange"
                                              data-date-min-date="-3M"
                                              data-date-max-date="0"
                                              data-date-start-date="-3M"
                                              data-date-end-date="0"
                                              id="datepicker-date-time-nodisable"
                                              class="form-control for-clander-view"
                                              type="text"
                                              data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"
                                            />
                                        </div>
										<input type="hidden" name="status" id="status" />
                                        <div class="col-md-1">
                                            <button id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=<spring:message code="application.reset" />>
                                                <i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
                                            </button>
                                        </div>

                                        <div class="col-md-7" style="display: flex; align-items: end">
                                            <div class="col-md-4">
                                                <spring:message code="po.filter.by.view" />
                                                <select
                                                    class="chosen-select form-control" name="poType" id="viewType">
                                                    <option value="all"><spring:message code="buyercreation.all"/></option>
                                                    <option value="me">For Me</option>
                                                </select>
                                            </div>

                                            <div id= "showError" class="col-md-8 text-right">
                                                <button id="exportPoItemReport" class="btn btn-sm btn-success hvr-pop" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="po.item.export.report" />'>
                                                    <input type="hidden" id="poItemReportId" value="false"> <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
                                                    </span> <span class="button-content"><spring:message code="po.item.export.report" /></span>
                                                </button>
                                                <button id="exportPoReport" class="btn btn-sm btn-success hvr-pop" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="po.export.report" />'>
                                                    <input type="hidden" id="poReportId" value="false"> <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
                                                    </span> <span class="button-content"><spring:message code="po.export.report" /></span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
								<div class="row">
									<div class="col-xs-12">
										<div class="form-group col-md-12 bordered-row">
											<div class="ph_tabel_wrapper scrolableTable_list">
												<table id="poList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
															<th search-type="" class="width_100_fix"><spring:message code="application.action" /></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.number"/></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.name"/></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="application.supplier" /></th>
															<th style="text-align: left;" class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.createdby"/></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.createddate"/></th>
															<th style="text-align: left;" search-type="text" class="align-left width_200 width_200_fix"><spring:message code="label.buyer.poList.orderby" /></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="label.buyer.poList.orderDate" /></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="label.buyer.poList.reviseDate" /></th>
															<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.poListing.acceptRejectDate"/></th>
															<th search-type="" class="align-right width_150 width_150_fix"><spring:message code="supplier.poListing.currency" /></th>
															<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal" /></th>
															<th style="text-align: left;" class="align-left width_200 width_200_fix"><spring:message code="buyer.po.pr.number" /></th>
															<th style="text-align: left;" class="align-left width_200 width_200_fix"><spring:message code="label.businessUnit" /></th>
															<!--
															<th search-type="select" search-options="poStatusList"  class="align-center width_150_fix" class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.status"/></th>
														    -->
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
.resetBtn-padding {
	padding-top: 3px;
	padding-left: 0px;
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

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>

<c:set var="userId" value="${userId}" scope="request" />
<script type="text/javascript">
	$('document').ready(function() {
	    const userId = '${userId}'
	    const isAdmin = '${isAdmin}'
	    console.log("User Id : "+userId+" is Admin:"+isAdmin);

	    //PH-4113
	    var viewType='all'; //set default view to all
        var today = new Date();
        var threeMonthsAgo = new Date(today.getFullYear(), today.getMonth() - 3, today.getDate());


        var options={}
        /*
        if(isAdmin==='false'){
            options={
                "startDate": threeMonthsAgo,
                "endDate": today,
                "minDate": threeMonthsAgo,
                "maxDate": today,
                "locale": {
                  "format": "MM/DD/YYYY hh:mm A"
                }
            }
        }
        */
        $('#datepicker-date-time-nodisable').daterangepicker(options);

		// Setup - add a text input to each footer cell

		/* var firstRow = $('#poList thead tr:nth-child(1)').html();
		var secondRow = $('#poList thead tr:nth-child(2)').html();
		$('#poList thead tr:nth-child(1)').html(secondRow);
		$('#poList thead tr:nth-child(2)').html(firstRow); */

		var table = $('#poList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#select-all').prop('checked', false);
				// $('div[id=idGlobalError]').hide();
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();

				$.each($('.url_images'),function(i,e){
				    let href = $(e).data('href');
                    let userid = $(e).data('userid');
                    let creator = $(e).data('creator');

                    if(userid !== creator){
                        href=$(e).data('view');
                    }

                    $(e).attr('href',href);
                });

			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/poListData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					d.viewType = viewType;
					d.status = status;
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
					return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
				}
			},{
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"mRender" : function(data, type, row) {
					var action = '<div style="display: flex;">';

                    var prId = row.pr != null?row.pr.id:null;

                    var urlCreate = typeof(prId) === 'object' ?"poCreate/" + row.id :"poCreate/" + row.id + "?prId="+prId;
                    var urlView = typeof(prId) === 'object' ?"poView/" + row.id :"poView/" + row.id + "?prId="+prId;
                    //ADD LOGIC FOR REDIRECTION HERE
                    // 4113 case no 12

					if(row.status==='DRAFT' || row.status==='SUSPENDED' ){
					    action+='<a href="#" data-href="'+urlCreate+'" data-view="'+urlView+'" data-userid="'+userId+'" data-creator="'+row.createdBy.id+'" class="url_images" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
					}else{
					    action+='<a href="#" data-href="'+urlView+'"  data-view="'+urlView+'" data-userid="'+userId+'" data-creator="'+row.createdBy.id+'" class="url_images" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
					}

                    action += (row.fromIntegration && row.status === 'READY') ?
                        '<div><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" style="opacity: 0.5; cursor: not-allowed;" /></div>' :
                        '<a href="downloadPoReport/' + row.id + '" title="<spring:message code="tooltip.download" />"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a></div>';

					return action;
				}
			}, {
				"data" : "poNumber",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "name",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "supplier.fullName",
				"className" : "align-left",
				"defaultContent" : ""
			},{

				"data" : "createdBy.name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"className" : "align-left",
				"type" : 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "orderedBy.name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "orderedDate",
				"className" : "align-left",
				"searchable" : false,
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "poRevisedDate",
				"className" : "align-left",
				"searchable" : false,
				"type": 'custom-date',
				"defaultContent" : "-"
			}, {
				"data" : "actionDate",
				"className" : "align-left",
				"searchable" : false,
				"type": 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "currency.currencyCode",
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
				"data" : "po.poId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "businessUnit.unitName",
				"className" : "align-left",
				"defaultContent" : ""
			},
			/*
			{
				"data" : "status",
				"className" : "align-left",
				"defaultContent" : "",

			}
			*/],
		
		    "initComplete": function(settings, json) {
                var htmlSearch = '<tr class="tableHeaderWithSearch">';
                $('#poList thead tr:nth-child(1) th').each(function(i) {
                    var title = $(this).text();
                    if (!(title == "Actions") && $(this).attr('search-type') != '') {
                        if ($(this).attr('search-type') == 'select') {
                            var optionsType = $(this).attr('search-options');
                            htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="' + i + '"  name="' + (title.replace(/ /g, "")).toLowerCase() + '"><option value="">ALL</option>';

                            if (optionsType == 'poStatusList') {
                                <c:forEach items="${poStatusList}" var="item">
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
                $('#poList thead').append(htmlSearch);
                $(table.table().container()).on('keyup', 'thead input', function() {
                    if ($.trim(this.value).length > 2 || this.value.length == 0) {
                        table.column($(this).data('index')).search(this.value).draw();
                    }
                });
                $(table.table().container()).on('change', 'thead select', function() {
                    table.column($(this).data('index')).search(this.value).draw();
                });

                // Assuming moment.js is available
                const MAX_MONTHS = 3;

                $('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {
                    var start = picker.startDate; // Use moment objects directly
                    var end = picker.endDate;

                    // Calculate the difference in months and days
                    var monthDiff = end.diff(start, 'months');
                    var dayDiff = end.diff(start, 'days');

                    // If the month difference is greater than MAX_MONTHS, show error
                    if (monthDiff > MAX_MONTHS || (monthDiff === MAX_MONTHS && dayDiff > 0)) {
                        $('p[id=idGlobalErrorMessage]').html("The date range exceeds 3 months!");
                        $('div[id=idGlobalError]').show();
                    } else {
                        $('div[id=idGlobalError]').hide();
                        table.ajax.reload();
                    }
                });

                $('#viewType').change(function(){
                    viewType = $(this).find(':selected').val();
                    table.ajax.reload();
                });

                $("#resetDate").click(function(e) {

                    location.reload();
                    /*
                    if ($("#datepicker-date-time-nodisable").val() !== '') {
                        location.reload();
                    }
                    $("#datepicker-date-time-nodisable").val('');
                    */
                    e.preventDefault();
                });

                $("#exportPoReport").click(function(e) {

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

                        $('#showError').after('<p style="float: right;" class="error-range text-danger">Please Select atleast one PO </p>');
                        return false;
                    } else {
                        $('#exportPoReportForm').attr('action', getContextPath() + "/buyer/exportPoSummaryCsvReport");
                        $('#exportPoReportForm').submit();
                    }

                });

                $("#exportPoItemReport").click(function(e) {

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

                        $('#showError').after('<p style="float: right;" class="error-range text-danger">Please Select atleast one PO </p>');
                        return false;
                    } else {
                        $('#exportPoReportForm').attr('action', getContextPath() + "/buyer/exportPoItemSummaryCsvReport");
                        $('#exportPoReportForm').submit();
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

