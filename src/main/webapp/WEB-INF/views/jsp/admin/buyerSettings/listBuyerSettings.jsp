<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="UTF-8">
<!--[if IE]><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><![endif]-->
<title><spring:message code="buyersettings.title" /></title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
</head>
<body>
	<div id="page-content">
		<div class="container col-md-12">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li><a href="#"><spring:message code="application.dashboard" /></a></li>
				<li class="active"><spring:message code="buyersettings.list" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="buyersettings.registered" />
				</h2>
			</div>
			<div class="container-fluid  col-md-12">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd pad_all_15">
							<section class="index_table_block"> <header class="form_header"> <form:errors path="*" cssClass="error" /> 
 							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr>
													<th><spring:message code="label.timezone" /></th>
													<th><spring:message code="label.currency" /></th>
													<th><spring:message code="buyersettings.decimal" /></th>
													<th><spring:message code="application.createdby" /></th>
													<th><spring:message code="application.createddate" /></th>
													<th><spring:message code="application.modifiedby" /></th>
													<th><spring:message code="application.modifieddate" /></th>
													<th><spring:message code="application.edit/delete" /></th>
												</tr>
											</thead>
										</table>
										<form method="get" action="buyerSettings">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" class="btn btn-plus btn-info top-marginAdminList">
												<spring:message code="buyersettings.create" />
											</button>
										</form>
									</div>
									<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
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
</style>
	<script type="text/javascript">
		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
	</script>
	<script type="text/javascript">
		$('document')
				.ready(
						function() {
							var data = eval('${buyerSettingsList}');

							$('#tableList')
									.DataTable(
											{
												'aaData' : data,
												"aoColumns" : [
														{
															"mData" : "timeZone.timeZone",
															"defaultContent" : ""
														},
														{
															"mData" : "currency.currencyName",
															"defaultContent" : ""
														},
														{
															"mData" : "decimal",
															"defaultContent" : ""
														},
														{
															"mData" : "createdBy"
														},
														{
															"mData" : "createdDate"
														},
														{
															"mData" : "modifiedBy"
														},
														{
															"mData" : "modifiedDate"
														},
														{
															"mData" : "id",
															"mRender" : function(
																	data, type,
																	row) {
																return '<a href="buySetEdit?id='
																		+ row.id
																		+ '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png" data-placement="top" title="Edit"></a>   <a href="#myModal" onClick="javascript:updateLink(\''
																		+ row.id
																		+ '\');"  data-placement="top" title="Delete" role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
															},

														} ]
											});
						});

		function updateLink(id) {
			/* 			var link = $("a#idConfirmDelete");
			 link.data('href', link.attr("href"));
			 link.attr("href", link.data('href') + ''+ id); */
			var link1 = $("input#hiddenId");
			link1.attr("value", id);
		}

		function doCancel() {
			var link = $("a#idConfirmDelete");
			link.data('href', link.attr("href"));
			link.attr("href", '${pageContext.request.contextPath}/buyer/buySetDlt?id=');
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
					<label><spring:message code="buyersettings.delete" /></label>
				</div>
				<input type="hidden" id="hiddenId" value="" />
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" title="Delete"><spring:message code="application.delete" /></a>
					<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="<c:url value="/resources/js/view/buyerSettingsDelete.js"/>"></script>
</body>
</html>
