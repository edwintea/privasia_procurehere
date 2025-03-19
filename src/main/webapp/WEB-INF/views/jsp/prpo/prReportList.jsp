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
<div id="page-content" view-name="prReportList">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li><a id="dashboardLink" href="${buyerDashboard}"><spring:message code="application.dashboard" /> </a></li>
			<li class="active"><spring:message code="pr.report.label" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="pr.report.title" /></h2>
		</div>

		<div class="main-div marg-top-20">
			<div class="top-row">
				<!-- Top Row: Draft, Pending, Approved -->
				<div class="box-main draftContainer">
					<div class="box-top gold">
						<span><spring:message code="label.rftenvelop.status.draft"/></span>
						<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/draft-event.png" alt="draft-event">
					</div>
					<div class="box-bottom" data-target="DRAFT">
						<div class="gold-con draftContainer">${draftPrCount}</div>
						<span><spring:message code="buyer.dashboard.prform.draft"/></span>
					</div>
				</div>

				<div class="box-main pendingContainer">
					<div class="box-top perpal">
						<span><spring:message code="buyer.dashboard.pendingapproval.pending"/></span>
						<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="pending-event">
					</div>
					<div class="box-bottom" data-target="PENDING">
						<div class="perpal-con pendingContainer">${pendingPrCount}</div>
						<span><spring:message code="buyer.dashboard.pendingapproval.pending"/></span>
					</div>
				</div>

				<div class="box-main myTaskContainer" data-intro="This is your task list. You can click on it to view the action items below" data-position="bottom">
					<div class="box-top navy">
						<span><spring:message code="buyer.dashboard.mytask.reportlist"/></span>
						<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/suspend-event.png" alt="active">

					</div>
					<div class="box-bottom" data-target="APPROVED">
						<div class="navy-con border-right-shaded myTaskContainer">${prApprovalsCount}</div>
						<span><spring:message code="buyer.dashboard.approve"/></span>
					</div>
				</div>
			</div>
			<div class="bottom-row">
				<!-- Bottom Row: Transferred, Completed, Cancelled -->

					<div class="box-main prcheseOrdersContainer">
						<div class="box-top light-gray">
							<span style="font-size: 13px;"><spring:message code="buyer.dashboard.transferred"/></span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/per-order.png" alt="per-order">
						</div>
						<div class="box-bottom" data-target="TRANSFERRED">
							<div class="light-gray-con finishedContainer">${prTransferCount}</div>
							<span><spring:message code="buyer.dashboard.transferred"/></span>
						</div>
					</div>

				<div class="box-main finishedContainer">
					<div class="box-top sky-blue">
						<span><spring:message code="buyer.dashboard.completed"/></span>
						<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="pending-event">
					</div>
					<div class="box-bottom" data-target="COMPLETE">
						<div class="sky-blue-con border-right-shaded finishedContainer">${completedPrCount}</div>
						<span><spring:message code="buyer.dashboard.completed"/></span>
					</div>
				</div>

				<div class="box-main cancelledContainer">
					<div class="box-top crimson">
						<span><spring:message code="buyer.dashboard.cancelled"/></span>
						<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/suspend-event.png" alt="active">
					</div>
					<div class="box-bottom" data-target="CANCELED">
						<div class="crimson-con finishedContainer">${cancelledPrCount}</div>
						<span><spring:message code="buyer.dashboard.cancelled"/></span>
					</div>
				</div>
			</div>
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

							<c:url value="/buyer/prReportCsv" var="downloadPrCsv" />
							<form:form action="${downloadPrCsv}" method="post" id="exportPrReportForm" ModelAttribute="searchFilterPrPojo">

								<div class="row">
									<div class="col-md-6 col-sm-6 col-lg-7 marg-top-10 mb-10">
										<label><spring:message code="application.filter.By.Date" /></label>
										<div class="row">
											<div class="col-md-10 col-sm-10 col-lg-10">
												<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" requerd />
											</div>
											<div class="col-md-2 col-sm-2 resetBtn-padding">
												<spring:message code="application.reset"  var="resetLabel"/>
												<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title=${resetLabel}>
													<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
												</button>
											</div>

										</div>
									</div>
									<div class="col-md-3 col-sm-6 col-lg-3 resetBtn-padding">
										<label><spring:message code="pr.filter.by.view" /></label>
										<select class="chosen-select" name="prType" id="prType" style="width: 100px;">
											<option value="ALL" selected><spring:message code="application.all" /></option>
											<option value="ME"><spring:message code="application.for.me" /></option>
										</select>
									</div>

									<input type="hidden" name="status" id="status" />
									<div class="col-md-3 col-sm-2 col-lg-2  marg-top-30">
											<%-- 											<button id="exportPrReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="pr.export.report" />'> --%>
										<!-- 												<span class="glyph-icon icon-separator line-h-35"> -->
										<!-- 													<i class="glyph-icon icon-download"></i> -->
										<!-- 												</span> -->
											<%-- 												<span class="button-content"><spring:message code="pr.export.report"/></span> --%>
										<!-- 											</button> -->
										<button id="exportPrCsv" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="pr.export.report" />'>
													<span class="glyph-icon icon-separator line-h-35">
														<i class="glyph-icon icon-download"></i>
													</span>
											<span class="button-content"><spring:message code="pr.export.report"/></span>
										</button>
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

															<th search-type="" class="checkbox-stylling">
																<input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all">
															</th>

															<th search-type="">
																<spring:message code="application.action" />
															</th>
															<th class="width_200 width_200_fix align-left"><spring:message code="application.referencenumber"/></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.prname"/></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="supplier.name" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="application.description"/></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="pr.number.label" /></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.pr.createdby"/></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="pr.created.date"/></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="pr.approved.by"/></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="pr.approved.date"/></th>
															<th search-type="" class="width_100 width_100_fix align-right"><spring:message code="label.currency"/></th>
															<th search-type="" class="width_200 width_200_fix align-right"><spring:message code="pr.grand.total"/></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="label.businessUnit" /></th>
															<th class="width_200 width_200_fix align-left"><spring:message code="buyer.dashboard.po.number"/></th>
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

	var status="";

	$(window).bind('load', function() {

		$('.box-bottom').click(function(e) {
			e.preventDefault();
			var targetElm = $(this).attr('data-target');
			$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
			$('#status').val(targetElm);
			status=targetElm;

			$('#tableList').DataTable().ajax.reload();

			$('html,body').animate({
				scrollTop : $('#tableList').offset().top
			}, 'slow');
		});
	});


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
				"url" : getContextPath() + "/buyer/prReportListData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
					d.prType=$('#prType').val();
					d.status = status;
					//var table = $('#tableList').DataTable()
					//d.page = (table != undefined) ? table.page.info().page : 0;
					//d.size = (table != undefined) ? table.page.info().length : 10;
					//d.sort = d.columns[d.order[0].column].data + ',' + d.order[0].dir;
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
			             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="poIds" name="prIds">';
			         }
				},{
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var action = '<a href="prView/' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					action += '<a href="downlaodPrSummary/' + row.id + '" title=<spring:message code="tooltip.download2" />><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
					return action;
				}
			},{
				"data" : "referenceNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "name",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "supplier.fullName",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "description",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "prId",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"data" : "createdBy.name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "prCreatedDate",
				"className" : "align-left",
				"type": 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "approvedBy.name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "prApprovedDate",
				"className" : "align-left",
				"type": 'custom-date',
				"searchable" : false,
				"defaultContent" : ""
				
			}, {
				"data" : "currency.currencyCode",
				"className" : "align-right",
				"searchable" : false,
				"defaultContent" : ""
				
			},{
				"data" : "grandTotal",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
				}
			},{
				"data" : "businessUnit.unitName",
				"className" : "align-center",
				"defaultContent" : ""
			 } ,{
				"data" : "poNumber",
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
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'" name="' + (title.replace(/ /g, "")).toLowerCase() + '"><option value=""><spring:message code="buyercreation.all.case" /></option>';
					
					if(optionsType == 'prStatusList'){
						<c:forEach items="${prStatusList}" var="item">
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

		$('#prType').on('change',function(){
					table.ajax.reload();
				});



				$('#datepicker-date-time-nodisable')
		.on(
				'apply.daterangepicker',
				function(e, picker) {
					
					table.ajax.reload();
					
				}
				);



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
		
		
		$("#exportPrReport")
		.click(
				function(e) {
					
					
					
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
					
						if (typeof val === 'undefined'
							|| val == '') {
				
							$('#exportPrReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one PR</p>');
							return false;
						}
						else{
							$('#exportPrReportForm').submit();
						}

						
				
				});
		
		$("#exportPrCsv").click(function(e) {
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
					$('#exportPrCsv').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one PR</p>');
						return false;
				}else{
					$('#exportPrReportForm').submit();
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
<script type="text/javascript">
$(document).ready(function() {
    // ... existing code ...

    $(window).bind('load', function() {
        $('.box-bottom').click(function(e) {
            e.preventDefault();
            var targetElm = $(this).attr('data-target');
            $('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
            var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.' + targetElm);

            // Max 3 tables in one group. Try each
            if (typeof window[targetElm] !== 'undefined') {
                window[targetElm].ajax.reload();
            }
            if (typeof window[targetElm + '1'] !== 'undefined') {
                window[targetElm + '1'].ajax.reload();
            }
            if (typeof window[targetElm + '2'] !== 'undefined') {
                window[targetElm + '2'].ajax.reload();
            }
            if (typeof window[targetElm + '3'] !== 'undefined') {
                window[targetElm + '3'].ajax.reload();
            }
            if (typeof window[targetElm + '4'] !== 'undefined') {
                window[targetElm + '4'].ajax.reload();
            }
            showTable.removeClass('flagvisibility');
            $('html,body').animate({
                scrollTop : showTable.offset().top
            }, 'slow');
        });

        /* Commented out section
        var targetElm = 'myTaskData';

        // Max 3 tables in one group. Try each
        if (typeof window[targetElm] !== 'undefined') {
            window[targetElm].ajax.reload();
        }
        if (typeof window[targetElm + '1'] !== 'undefined') {
            window[targetElm + '1'].ajax.reload();
        }
        if (typeof window[targetElm + '2'] !== 'undefined') {
            window[targetElm + '2'].ajax.reload();
        }
        if (typeof window[targetElm + '3'] !== 'undefined') {
            window[targetElm + '3'].ajax.reload();
        }
        if (typeof window[targetElm + '4'] !== 'undefined') {
            window[targetElm + '4'].ajax.reload();
        }
        */
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
.mb-10 {
	margin-bottom: 10px;
}

.box-bottom.width50 {
	width: 50%;
}
.box-top {
    height: 50px;
 }

.main-div {
	/* float: left; */
	/* float: left; */
	max-width: 900px;
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	width: 100%;
}

.box-bottom span {
	font-size: 16px;
	width: 100%;
	left: 0;
	position: absolute;
	top: 0;
	line-height: 16;
	height: 100%;
}

.box-bottom div {
	width: 100%;
	line-height: 3;
}

.border-right-shaded {
	/* border-width: 1px;
	border-left: 0;
	border-style: solid;
	-webkit-border-image: -webkit-gradient(linear, 0 100%, 0 0, from(#CCC),
		to(rgba(0, 0, 0, 0))) 1 100%;
	-webkit-border-image: -webkit-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-moz-border-image: -moz-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-o-border-image: -o-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0)) 1
		100%;
	border-image: linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 100%; */
	border-width: 1.5px 1.5px 1.5px 0;
    border-style: solid;
    -webkit-border-image:
      -webkit-gradient(linear, 0 0, 100% 0, from(black), to(rgba(0, 0, 0, 0))) 1 100%;
    -webkit-border-image:
      -webkit-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    -moz-border-image:
      -moz-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    -o-border-image:
      -o-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    border-image:
      linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 50%;
}

.box-main {
	border: 1px solid #eeeff0;
	border-radius: 0;
	margin: 0 1% 30px;
	height: 150px;
	overflow: hidden;
	position: relative;
	width: 200px;
	cursor: pointer;
	display: inline-block;
}

.box-top {
	top: 0;
	color: #fff;
	font-size: 14px;
	line-height: 18px;
	padding: 10px 3%;;
	position: absolute;
	text-align: left;
	text-transform: uppercase;
	width: 100%;
	border-radius: 0;
}

.box-bottom {
	float: left;
	padding: 23% 0 13% 0;
	position: relative;
	text-align: center;
	width: 100%;
	font-size: 34px;
}

.box-top span {
	float: left;
	line-height: 16px;
	margin-left: 3%;
	width: 79%;
}

.yellow {
	background: #F9C851;
	border: 1px solid #F9C851;
	color: #ffffff !important;
}

.yellow-con {
	/* font-size: 60px; */
	color: #F9C851;
	display: inline-block;
}

.orange {
	background: #FFA500;
	border: 1px solid #F9C851;
}

.limegreen {
	background: #32CD32;
	border: 1px solid #32CD32;
}

.green {
	background: #06CCB3;
	border: 1px solid #06CCB3;
}

.crimson {
	background: #ff5b5b;
	border: 1px solid #FF5B5B;
}

.gold {
	background: #FFD700;
	border: 1px solid #FFD700;
}

.navy {
	background: #000080;
	border: 1px solid #000080;
}

.navy-con {
	/* font-size: 60px; */
	color: #0000CD;
	display: inline-block;
}

.gold-con {
	/* font-size: 60px; */
	color: #FFD700;
	display: inline-block;
}

.red {
	background: #FF5B5B;
	border: 1px solid #FF5B5B;
}

.blue {
	background: #627fa7;
	border: 1px solid #627fa7;
}

.sky-blue {
	background: #35b4e9;
	border: 1px soild #35b4e9;
}

.sky-blue-con {
	/* font-size: 60px; */
	color: #35b4e9;
	display: inline-block;
}

.coffi {
	background: #cebf98;
	border: 1px solid #cebf98;
}

.light-blue {
	background: #00d1c6;
	border: 1px solid #00d1c6;
}

.light-blue-con {
	/* font-size: 60px; */
	color: #00d1c6;
	display: inline-block;
}

.light-gray {
	background: #727c88;
	border: 1px solid #727c88;
}

.light-gray-con {
	/* font-size: 60px; */
	color: #727c88;
	display: inline-block;
}

.perpal {
	background: #8809ff;
	border: 1px solid #8809ff;
}

.perpal-con {
	/* font-size: 60px; */
	color: #8809ff;
	display: inline-block;
}

.db-li {
	line-height: 19px !important;
}

.limegreen-con {
	/* font-size: 60px; */
	color: #32CD32;
}

.crimson-con {
	/* font-size: 60px; */
	color: #ff5b5b;
}

.red-con {
	/* font-size: 60px; */
	color: #f9c851;
}

.blck-con {
	font-size: 25px;
	color: #333333;
}

.orange-con {
	/* font-size: 60px; */
	color: #FFA500;
	display: inline-block;
}

.green-con {
	/* font-size: 60px; */
	color: #06ccb3;
	display: inline-block;
}

.gray-con {
	/* font-size: 60px; */
	color: #333333;
	display: inline-block;
}

.bottom-text {
	bottom: 5px;
	color: #333;
	float: left;
	font-size: 22px;
	width: 100%;
	margin-top: 30px;
}

.box-top img {
	width: 30px;
	vertical-align: top;
	float: right;
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
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal"></button>
			</div>
			<div class="modal-body">
				<label>
					<spring:message code="rfxTemplate.delete" />
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



<style>
	.checkbox-stylling input{
		 width: 15px !important; 
		 height: 15px !important; 
		 text-align:center !important;
	}
	 td{
	text-align: center !important;
	}
	.f-r {
		float: right;
	}
	
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>


