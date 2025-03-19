<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')"
	var="buyerReadOnlyAdmin" />
<spring:message var="industryCategoryListDesk"
	code="application.buyer.industry.category.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${industryCategoryListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a
				href="${pageContext.request.contextPath}/buyer/buyerDashboard"">
					<spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="industrycategory.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="industrycategory.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">

									<div class="upload_download_wrapper">
										<!-- 										<div class="right_button"> -->
										<%-- 											<c:url value="/buyer/industryCategoryTemplate" var="industryCategoryTemplate" /> --%>
										<%-- 											<a href="${industryCategoryTemplate}"> --%>
										<!-- 												<button class="btnIndustrycategory btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal"> -->
										<%-- 													<i class="excel_icon"></i><spring:message code="application.download.excel.button" /> --%>
										<!-- 												</button> -->
										<!-- 											</a> -->
										<!-- 											</div> -->
										
										<c:url value="/buyer/exportIndustryCsvReport" var="downloadCsvReport" />
											<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}" />
												<div class="row">
													<div class="col-md-5 col-sm-8 col-lg-5"></div>
													<div class="col-md-2 col-sm-4 resetBtn-padding"></div>
													<div class="col-md-5">
														<a href="${downloadCsvReport}" > 
															<button id="exportCsvReport"
																class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37"
																data-toggle="tooltip" data-placement="top"
																data-original-title='<spring:message code="industryCategory.csv.report"/>'>
																<span class="glyph-icon icon-separator"> <i
																	class="glyph-icon icon-download"></i>
																</span> <span class="button-content"><spring:message
																		code="industryCategory.csv.report" /></span>
															</button>
														</a>	
													</div>
												</div>
											</div>
											<div class="ph_tabel_wrapper scrolableTable_UserList">
												<table id="tableList"
													class="data display table table-bordered noarrow"
													cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type=""><spring:message
																	code="application.action" /></th>
															<th search-type="text"><spring:message
																	code="label.industrycategory.code" /></th>
															<th search-type="text"><spring:message
																	code="label.industrycategory.name" /></th>
															<th search-type="text"><spring:message
																	code="application.createdby" /></th>
															<th search-type=""><spring:message
																	code="application.createddate" /></th>
															<th search-type="text"><spring:message
																	code="application.modifiedby" /></th>
															<th search-type=""><spring:message
																	code="application.modifieddate" /></th>
															<th search-type="select" search-options="statusList"><spring:message
																	code="label.industrycategory.status" /></th>
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>
												<sec:authorize
													access="hasRole('BUYER_INDUSTRY_CATEGORY_EDIT')">
													<form method="get" action="industryCategory">
														<button type="submit"
															class="btn btn-plus btn-info top-marginAdminList hvr-pop hvr-rectangle-out ${buyerReadOnlyAdmin ? 'disabled' : '' }">
															<spring:message code="industrycategory.create" />
														</button>
														<a
															class="btn btn-info hvr-pop hvr-rectangle-out top-marginAdminList ph_btn_midium button-previous  ${buyerReadOnlyAdmin ? 'disabled' : '' }"
															href="#myConfirmNAICSModal" data-toggle="modal"><spring:message
																code="industrycategory.load.naics" /></a>
													</form>
												</sec:authorize>
											</div>
										<div id="morris-bar-yearly" class="graph"
											style="visibility: hidden"></div>
									</div>
								</div>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$('document').ready(function() {

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
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"order" : [],
			"ajax" : {
				"url" : getContextPath() + "/buyer/industryListData",
				error : function(error) {
					//console.log(error);
				}
			},
			"columns" : [ {
				"mData" : "id",
				"mRender" : function(data, type, row) {
					//return '<a href="editIndustryCategory?id=' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
					return '<a href="editIndustryCategory?id=' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
				},
				"searchable" : false,
				"sortable" : false
			}, {
				"mData" : "code"
			}, {
				"mData" : "name"
			}, {
				"mData" : "createdBy.loginId",
				"defaultContent" : ""
			}, {
				"mData" : "createdDate",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedBy.loginId",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedDate",
				"defaultContent" : ""
			}, {
				"mData" : "status",
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if($(this).attr('search-type') == 'select'){
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> '+title+'</option>';
					if(optionsType == 'statusList'){
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="'+$(this).attr("style")+'"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
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
		
		$('.loadNaicsIndustry').click(function(){
			$('#loading').show();
		});
		}
		});
		
	});

	function updateLink(id) {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}

	function doCancel() {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteIndustryCategory?id=');
	}
</script>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();"
					type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="industrycategory.delete" /></label>
			</div>
			<div
				class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete"
					class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"
					href="${pageContext.request.contextPath}/buyer/deleteIndustryCategory?id="
					title=<spring:message code="tooltip.delete" />> <spring:message
						code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();"
					class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1"
					data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="myConfirmNAICSModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="industrycategory.confirm.load" />
				</h3>
				<button class="close for-absulate" type="button"
					data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message
						code="industrycategory.sure.load.naicss" /></label>
			</div>
			<div
				class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a
					class="btn pull-left btn-info ph_btn_small hvr-pop hvr-rectangle-out loadNaicsIndustry"
					href="${pageContext.request.contextPath}/buyer/loadNaicsIndustry"
					title=<spring:message code="industrycategory.button.load" />> <spring:message
						code="industrycategory.button.load" />
				</a>
				<button type="button"
					class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right"
					data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<style>
.excel_icon {
	background:
		url(../resources/assets/image-resources/image-procurehere/excel-icon.png)
		no-repeat 0 0;
	width: 24px;
	height: 26px;
	display: inline-block;
	margin-right: 4px;
	vertical-align: middle;
}

.btnIndustrycategory {
	line-height: 38px;
	height: 38px;
	min-width: 36px;
	float: right;
	margin-right: 20px;
}

#exportCsvReport {
	float: right;
}
</style>
