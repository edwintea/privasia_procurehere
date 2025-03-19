<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')"
	var="buyerReadOnlyAdmin" />
<spring:message var="businessUnitListDesk"
	code="application.buyer.business.unit.list" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${businessUnitListDesk}] });
});
</script>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message
						code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="businessUnit.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="businessUnit.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<!-- 					<div class="upload_download_wrapper"> -->
					<!-- 						<div class="right_button"> -->
					<%-- 							<c:url value="/buyer/businessUnitTemplate" var="businessUnitTemplate" /> --%>
					<%-- 							<a href="${businessUnitTemplate}"> --%>
					<!-- 								<button class="btnBuList btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal"> -->
					<%-- 									<i class="excel_icon"></i><spring:message code="application.download.excel.button" /> --%>
					<!-- 								</button> -->
					<!-- 							</a> -->
					<!-- 						</div> -->
					<!-- 					</div> -->
					<!-- 					<div class="white_box_brd pad_all_15"> -->
					
					<c:url value="/buyer/exportBusinessUnitCsvReport" var="downloadCsvReport" />
						<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
							<div class="row">
								<div class="col-md-5 col-sm-8 col-lg-5"></div>
								<div class="col-md-2 col-sm-4 resetBtn-padding"></div>
								<div class="col-md-5">
								 	<a href="${downloadCsvReport}" > 
										<button id="exportCsvReport"
											class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37"
											data-toggle="tooltip" data-placement="top"
											data-original-title='<spring:message code="business.export.button"/>'>
											<span class="glyph-icon icon-separator"> <i
												class="glyph-icon icon-download"></i>
											</span> <span class="button-content"><spring:message
													code="business.export.button" /></span>
										</button>
									</a>
								</div>
							</div>
						</div>
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList"
											class="data  display table table-bordered noarrow"
											cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th data-search-type=""><spring:message
															code="application.action" /></th>
													<th data-search-type="text"><spring:message
															code="label.businessUnitName" /></th>
													<th data-search-type="text"><spring:message
															code="label.businessDisplayName" /></th>
													<th data-search-type="text"><spring:message
															code="label.parentBusinessUnit" /></th>
													<th data-search-type="text"><spring:message
															code="application.createdby" /></th>
													<th data-search-type=""><spring:message
															code="application.createddate" /></th>
													<th data-search-type="text"><spring:message
															code="application.modifiedby" /></th>
													<th data-search-type=""><spring:message
															code="application.modifieddate" /></th>
													<th data-search-type="select"
														data-search-options="statusList"><spring:message
															code="application.status" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
										<form method="get" action="businessUnit">
											<input type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}" />
											<button type="submit"
												class="btn marg-top-10 btn-plus btn-info top-marginAdminLisst ${!buyerReadOnlyAdmin ? '':'disabled'}">
												<spring:message code="businessUnit.create" />
											</button>
										</form>
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
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.upload_download_wrapper {
	border-bottom: 1px solid transparent;
}

.btnBuList {
	line-height: 38px;
	height: 38px;
	min-width: 36px;
	float: right;
	margin-right: 20px;
}
</style>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
	$('document').ready(
			function() {

				// Setup - add a text input to each footer cell

				var table = $('#tableList').DataTable(
						{"oLanguage":{
							"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
						},
							"processing" : true,
							"deferRender" : true,
							"preDrawCallback" : function(settings) {
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
								"url" : getContextPath() + "/buyer/businessUnitData",
								"data" : function(d) {
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
							"columns" : [
									{
										"mData" : "id",
										"searchable" : false,
										"orderable" : false,
										"mRender" : function(data, type, row) {
											var tImg = "";
											tImg = '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
											return '<a href="editBusinessUnit?id=' + row.id + '" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id
													+ '\');" title="Delete" role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">' + tImg + '</a>';
										}
									}, {
										"data" : "unitName"
									}, {
										"data" : "displayName",
										"defaultContent" : ""
									}, {
										"data" : "parent.unitName",
										"defaultContent" : ""
									}, {
										"data" : "createdBy.name",
										"defaultContent" : ""
									}, {
										"data" : "createdDate",
										"searchable" : false,
										"defaultContent" : ""
									}, {
										"data" : "modifiedBy.name",
										"defaultContent" : ""
									}, {
										"data" : "modifiedDate",
										"searchable" : false,
										"defaultContent" : ""
									}, {
										"data" : "status",
										"orderable" : false,
										"defaultContent" : ""
									} ],
				"initComplete": function(settings, json) {	
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#tableList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					if (!(title == "Actions") && $(this).attr('data-search-type') != '') {
						if ($(this).attr('data-search-type') == 'select') {
							var optionsType = $(this).attr('data-search-options');
							htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
							if (optionsType == 'statusList') {
								<c:forEach items="${statusList}" var="item">
								htmlSearch += '<option value="${item}">${item}</option>';
								</c:forEach>
							}
							htmlSearch += '</select></th>';
						} else {
							htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
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
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteBusinessUnit?id=');
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
				<label><spring:message code="businessunit.sure.delete.bu" /></label>
			</div>
			<div
				class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete"
					class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out"
					href="${pageContext.request.contextPath}/buyer/deleteBusinessUnit?id="
					title=<spring:message code="tooltip.delete" />> <spring:message
						code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();"
					class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1"
					data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<style>
#tableList td {
	text-align: center;
}
#exportCsvReport{
float :right;}
</style>