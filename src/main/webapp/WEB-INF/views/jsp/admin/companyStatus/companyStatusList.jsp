<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="UTF-8">
<!--[if IE]><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><![endif]-->
<title><spring:message code="companystatus.title" /></title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
<spring:message var="companyTypeListDesk" code="application.owner.company.type.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${companyTypeListDesk}] });
});
</script>
</head>
<body>
	<div id="page-content">
		<div class="container col-md-12">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/owner/ownerDashboard">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">
					<spring:message code="companystatus.list1" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="companystatus.list" />
				</h2>
			</div>
			<div class="container-fluid col-md-12">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd pad_all_15">
							<section class="index_table_block">
							<div class="row">
								<div class="col-xs-12">
									<div class="form-group col-md-12 bordered-row">
										<div class="alert alert-success" id="idGlobalSuccess" style="display: ${!empty success ? 'block' : 'none'}">
											<div class="bg-green alert-icon">
												<i class="glyph-icon icon-check"></i>
											</div>
											<div class="alert-content">
												<h4 class="alert-title">
													<spring:message code="application.success" />
												</h4>
												<p id="idGlobalSuccessMessage">
													<c:out value='${success}' />
												</p>
											</div>
										</div>
										<div class="alert alert-success" id="idGlobalSuccess" style="display: ${!empty info ? 'block' : 'none'}">
											<div class="bg-green alert-icon">
												<i class="glyph-icon icon-check"></i>
											</div>
											<div class="alert-content">
												<h4 class="alert-title">
													<spring:message code="application.info" />
												</h4>
												<p id="idGlobalSuccessMessage">
													<c:out value='${info}' />
												</p>
											</div>
										</div>
										<c:if test="${not empty error}">
											<div class="alert alert-danger" id="idGlobalError" style="display: block">
												<div class="bg-red alert-icon">
													<i class="glyph-icon icon-times"></i>
												</div>
												<div class="alert-content">
													<h4 class="alert-title">
														<spring:message code="application.error" />
													</h4>
													<p id="idGlobalErrorMessage">
														<span>${error} </span>
													</p>
												</div>
											</div>
										</c:if>
									</div>
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type=""><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="label.companystatus" /></th>
													<th search-type="text"><spring:message code="application.createdby" /></th>
													<th search-type=""><spring:message code="application.createddate" /></th>
													<th search-type="text"><spring:message code="application.modifiedby" /></th>
													<th search-type=""><spring:message code="application.modifieddate" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
										<form method="get" action="companyStatus">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" class="btn btn-plus btn-info top-marginAdminList">
												<spring:message code="companystatus.create" />
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

#tableList td {
	text-align: center;
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
							{
								"processing" : true,
								"deferRender" : true,
								"preDrawCallback" : function(settings) {
									//$('div[id=idGlobalError]').hide();
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
									"url" : getContextPath() + "/admin/companyListData",
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
											"mRender" : function(data, type, row) {
												return '<a href="editCompanyStatus?id=' + row.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id
														+ '\');" data-placement="top" title="Delete"  role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
											},
											"searchable" : false,
											"sortable" : false
										}, {
											"mData" : "companystatus"
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
			link.attr("href", '${pageContext.request.contextPath}/admin/deleteCompanyStatus?id=');
		}

		/* function editLink(id) {
		     var link1 = $("a#id");
			 link1.attr("value",id);
			 $.ajax({
					type : "GET",
					url : "editCompanyStatus",
					data : {id: id},
				
					beforeSend : function(xhr) {
						$('#loading').show();
					},
					complete : function() {
						$('#loading').hide();
					},
					success : function(data) {
						$("#btn-update").prop("disabled", false);
						location.href=data;
					},
					error : function(request, textStatus, errorThrown) {
						$("#saveCompanyStatus").prop("disabled", false);
						alert("id Error "+id+ " Error : "+ request.getResponseHeader('error'));
						console.log(" Error : "+ request.getResponseHeader('error'));
						alert(request.getResponseHeader('error'));
						$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
						$("#idGlobalError").show();
					}
				});
		} */
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
					<label>
						<spring:message code="companystatustype.delete" />
					</label>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deleteCompanyStatus?id=" title="Delete">
						<spring:message code="application.delete" />
					</a>
					<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<%-- <script type="text/javascript" src="<c:url value="/resources/js/view/companyStatus.js"/>"></script> --%>
</body>
</html>
