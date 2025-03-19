<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:message var="supplierTagsListDesk" code="application.buyer.supplier.tags.list" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SUPPLIER_TAG_EDIT') or hasRole('ADMIN')" var="canEdit" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="suppliertags.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="suppliertags.list" />
			</h2>
		</div>
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<div class="container-fluid col-md-12">
			<div class="white_box_brd pad_all_15">
				<div class="row">
					<div class="col-xs-12">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="ph_tabel_wrapper scrolableTable_UserList">
							<table id="tableList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th search-type=""><spring:message code="application.action" /></th>
										<th search-type="text"><spring:message code="label.supplier.tagName" /></th>
										<th search-type="text"><spring:message code="label.suppliertags.description" /></th>
										<th search-type="text"><spring:message code="application.createdby" /></th>
										<th search-type=""><spring:message code="application.createddate" /></th>
										<th search-type="text"><spring:message code="application.modifiedby" /></th>
										<th search-type=""><spring:message code="application.modifieddate" /></th>
										<th search-type="select" search-options="statusList"><spring:message code="label.suppliertags.status" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
								<form method="get" action="supplierTags">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<button type="submit" class="btn btn-plus btn-info top-marginAdminList ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}">
										<spring:message code="suppliertags.create" />
									</button>
								</form>
							</div>
						<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var table = '';

	$('document').ready(
			function() {

				// Setup - add a text input to each footer cell

				table = $('#tableList').DataTable(
						{
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
								"url" : getContextPath() + "/buyer/supplierTagsData",
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
											var ret = '';
											tImg = '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
												
											ret += '<a href="editSupplierTags?id=' + row.id + '"  title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
											if (canEdit() == "true" ) {
												ret += '<a href="#myModal" onClick="javascript:updateLink(\''+ row.id + '\');" title="Delete" role="button" data-toggle="modal">' + tImg + '</a>';
											}	
												return ret;        
										}
									}, {
										"data" : "supplierTags"
									}, {
										"data" : "description",
										"defaultContent" : ""
									}, {
										"data" : "createdBy.loginId",
										"defaultContent" : ""
									}, {
										"data" : "createdDate",
										"searchable" : false,
										"defaultContent" : ""
									}, {
										"data" : "modifiedBy.loginId",
										"defaultContent" : ""
									}, {
										"data" : "modifiedDate",
										"searchable" : false,
										"defaultContent" : ""
									}, {
										"data" : "status",
										"defaultContent" : ""
									} ]
						});
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#tableList thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						if ($(this).attr('search-type') == 'select') {
							var optionsType = $(this).attr('search-options');
							htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value="">Search ' + title + '</option>';
							if (optionsType == 'statusList') {
								<c:forEach items="${statusList}" var="item">
								htmlSearch += '<option value="${item}">${item}</option>';
								</c:forEach>
							}
							htmlSearch += '</select></th>';
						} else {
							htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
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
			});

	function updateLink(id) {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}

	function doCancel() {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteSupplierTags?id=');
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
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="suppliertags.delete" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/deleteSupplierTags?id=" title="Delete"> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
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
</style>