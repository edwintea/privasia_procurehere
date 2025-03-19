<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('USER_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />

<sec:authentication property="principal.buyer" var="buyer" />
<sec:authentication property="principal.supplier" var="supplier" />
<sec:authentication property="principal.owner" var="owner" />
<%--
<c:if test="${  tf:contains( templateFields, 'fieldName' ) }">style='display:none;'</c:if>
<input type="text" value="${tf:defaultValue( templateFields, 'name' ) }" >
<c:if test="${  tf:readonly( templateFields, 'BASE_CURRENCY' ) }">readonly='readonly'</c:if>
<c:if test="${ ! tf:visibility( templateFields, 'EVENT_NAME' ) }">style='display:none;'</c:if>
<c:if test="${ ! tf:required( templateFields, 'EVENT_NAME' ) }">required</c:if>


--%>
<spring:message var="userListDesk" code="application.user.list" />
<spring:message var="supplierUserListDesk" code="application.supplier.user.list" />
<spring:message var="ownerUserListDesk" code="application.owner.user.list" />
<script type="text/javascript">
zE(function() {
	<c:if test="${buyer != null}">
		 zE.setHelpCenterSuggestions({ labels: [${userListDesk}] });
	</c:if>
	<c:if test="${supplier != null}">
		zE.setHelpCenterSuggestions({ labels: [${supplierUserListDesk}] });
	</c:if>
 	<c:if test="${owner != null}">
		zE.setHelpCenterSuggestions({ labels: [${ownerUserListDesk}] });
	</c:if> 
});
</script>

<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content" view-name="user">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:if test="${buyer != null}">
				<c:url value="/buyer/buyerDashboard" var="dashboard" />
			</c:if>
			<c:if test="${supplier != null}">
				<c:url value="/supplier/supplierDashboard" var="dashboard" />
			</c:if>
			<c:if test="${owner != null}">
				<c:url value="/owner/ownerDashboard" var="dashboard" />
			</c:if>
			<li><a href="${dashboard}"><spring:message code="application.dashboard" /></a></li>
			<li class="active"><spring:message code="user.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="admin.userlist.title" /></h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<header class="form_header">
								<form:errors path="*" cssClass="error" />
								<c:if test="${not empty errors}">
									<span class="error">${errors}</span>
								</c:if>
							</header>
							<div class="row">
								<div class="col-xs-12">
									<%-- 	 <div class="col-md-3 pull-right">
										<div class="row">
											<div class="col-md-12 pull-right">
												<form:form action="${pageContext.request.contextPath}/admin/downloadZip" id="downloadEnvelope">
													<button class="btn float-right btn-success hvr-pop marg-left-10 downloadEnvelopeSubmissionZip" data-toggle="tooltip" data-placement="top" data-original-title="Download entire envelope contents in ZIP format">
														<span class="glyph-icon icon-separator">
															<i class="glyph-icon icon-download"></i>
														</span>
														<span class="button-content">Download Entire Envelope</span>
													</button>
												</form:form>
											</div>
										</div>
									</div> --%>
									<div class="form-group col-md-12 bordered-row">
										<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
									</div>
									<%-- <div class="form-group col-md-12 bordered-row">
										<div class="alert alert-success" id="idGlobalSuccess" style="display: ${!empty info ? 'block' : 'none'}">
											<c:out value='${info}' />
										</div>
									</div> --%>
									<div class="">
										<div class="right_button">
<%-- 											<c:url value="/admin/userTemplate" var="userTemplate" /> --%>
<%-- 											<a href="${userTemplate}"> --%>
											<c:url value="/admin/userTemplateCsv" var="userTemplateCsv" />
											<a href="${userTemplateCsv}">
												<button class="btn float-right btn-success hvr-pop marg-left-10" id="downloadUser">
													<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
													</span> <span class="button-content"><spring:message code="admin.export.user" /></span>
												</button>
											</a>
										</div>
									</div>
									<div class="ph_tabel_wrapper col-md-12 scrolableTable_UserList">
										<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="align-center width_100_fix"><spring:message code="application.action" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.loginid" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.username" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.phone" /></th>
													<th search-type="" class="align-left"><spring:message code="application.createddate" /></th>
													<th search-type="" class="align-left"><spring:message code="application.lastlogin" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.createdby" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.role" /></th>
													<th search-type="text" class="align-left"><spring:message code="application.modifiedby" /></th>
													<th search-type="" class="align-left"><spring:message code="application.modifieddate" /></th>
													<c:if test="${buyer != null}">
														<th search-type="select" search-options="userType" class="align-left"><spring:message code="application.usertype" /></th>
													</c:if>
													<th search-type="select" search-options="lockedStatus" class="align-left"><spring:message code="application.locked" /></th>
													<th search-type="select" search-options="activeStatus" class="align-left"><spring:message code="application.status" /></th>
												</tr>
											</thead>
										</table>
										<spring:url value="/admin/user" var="createUrl" htmlEscape="true" />
										<a href="${createUrl}" class="btn btn-info top-marginAdminList ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}"><spring:message code="user.create" /></a>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="delId" value="" />
</div>
<!-- NEw add -->
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

#tableList th {
	min-width: 100px;
}
#tableList th:nth-child(5) {
    min-width: 165px !important;
}
#tableList th:nth-child(6) {
    min-width: 165px !important;
}
#tableList th:nth-child(10) {
    min-width: 165px !important;
}
#tableList td {
	text-align: center;
}
</style>
<script type="text/javascript">
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
				"url" : getContextPath() + "/admin/userListData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			},
			"order" : [],
			"columns" : [ {
				"mData" : "id",
				"searchable" : false,
				"className" : "align-center",
				"orderable" : false,
				"mRender" : function(data, type, row) {
					var ret = '<a href="editUser?id=' + row.id + '" title=<spring:message code="tooltip.edit" /> ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" role="button" data-toggle="modal">';
					ret += '</a>';
					return ret;
				}
			}, {
				"mData" : "loginId",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "name",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "phoneNumber",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "createdDate",
				"searchable" : false,
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "lastLoginTime",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "createdBy",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "userRole.roleName",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedBy",
				"className" : "align-left",
				"defaultContent" : ""
			},{
				"mData" : "modifiedDate",
				"className" : "align-left",
				"searchable" : false,
				"defaultContent" : ""
			},
			<c:if test="${buyer != null}">
			{
				"mData" : "userType",
				"className" : "align-left",
				"defaultContent" : ""
			},
			</c:if>
			{
				"mData" : "locked",
				"className" : "align-left",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					var ret = row.locked ? 'YES' : 'NO';
					return ret;
				}
			}, {
				"mData" : "active",
				"className" : "align-left",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					var ret = row.active ? 'ACTIVE' : 'INACTIVE';
					return ret;
				}
			} ],
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'lockedStatus') {
						htmlSearch += '<option value="NO"><spring:message code="application.no.case"/></option>';
						htmlSearch += '<option value="YES"><spring:message code="application.yes2"/></option>';
					}
					if (optionsType == 'activeStatus') {
						htmlSearch += '<option value="true"><spring:message code="account.overview.status.active"/></option>';
						htmlSearch += '<option value="false"><spring:message code="prsummary.inacive.status"/></option>';
					}
					if (optionsType == 'userType') {
						<c:forEach items="${userTypeList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		
		$(table.table().container()).on('keyup', 'thead input', debounce(function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				table.column($(this).data('index')).search(this.value).draw();
			}
		},1000));
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
		link.attr("href", '${pageContext.request.contextPath}/admin/deleteUser?id=');
	}
</script>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.confirm.delete" /></h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="user.delete" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deleteUser?id=" title=<spring:message code="tooltip.delete" />><spring:message code="application.delete" /></a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.cancel" /></button>
			</div>
		</div>
	</div>
</div>
