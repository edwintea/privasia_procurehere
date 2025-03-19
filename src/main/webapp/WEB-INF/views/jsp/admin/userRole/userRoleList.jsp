<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('USER_ROLE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.buyer" var="buyer" />
<sec:authentication property="principal.supplier" var="supplier" />
<sec:authentication property="principal.owner" var="owner" />
<spring:message var="userRoleListDesk" code="application.user.role.list" />
<spring:message var="supplierUserRoleListDesk" code="application.supplier.user.role.list" />
<spring:message var="ownerUserRoleListDesk" code="application.owner.user.role.list" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<script>
zE(function() {
		<c:if test="${buyer != null}">
			zE.setHelpCenterSuggestions({ labels: [${userRoleListDesk}] });
		</c:if>
		<c:if test="${supplier != null}">
			zE.setHelpCenterSuggestions({ labels: [${supplierUserRoleListDesk}] });
		</c:if>
	 	<c:if test="${owner != null}">
			zE.setHelpCenterSuggestions({ labels: [${ownerUserRoleListDesk}] });
		</c:if> 
});
</script>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content" view-name="userrole">
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
			<li>
				<a href="${dashboard}"><spring:message code="application.dashboard"/></a>
			</li>
			<li class="active">
				<spring:message code="userrole.list" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="userrole.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList" class="display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr>
													<th search-type="" class="align-left width_100 width_100_fix"><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="userrole.label" /></th>
													<th search-type="text"><spring:message code="application.description" /></th>
													<th search-type="text"><spring:message code="application.createdby" /></th>
													<th search-type=""><spring:message code="application.createddate" /></th>
													<th search-type="text"><spring:message code="application.modifiedby" /></th>
													<th search-type=""><spring:message code="application.modifieddate" /></th>
													<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
												</tr>
											</thead>
										</table>
										<spring:url value="/admin/role" var="createUrl" htmlEscape="true" />
										<a href="${createUrl}" class="btn btn-info top-marginAdminList ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}">
											<spring:message code="application.userrole.create" />
										</a>
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
<!-- NEw add -->
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
</style>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
	$('document').ready(function() {

		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				//$('div[id=idGlobalError]').hide();
				return true;
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/admin/userRoleListData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
				}
			},
			"order" : [],
			"columns" : [ {
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"mRender" : function(data, type, row) {
					console.log("is role read only :" + row.readOnly);
					// hiding edit and delete button for buyer approver User role
					var ret = '';
					if(!row.readOnly){
						ret = '<a href="editRole?id=' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
						if (canEdit() == "true" ) {
							ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
							ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
							ret += '</a>';
						}
					}
					return ret;
				}
			}, {
				"mData" : "roleName",
				"defaultContent" : ""
			}, {
				"mData" : "roleDescription",
				"defaultContent" : ""
			}, {
				"mData" : "createdByName",
				"defaultContent" : ""
			}, {
				"mData" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "modifiedByName",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "status",
				"defaultContent" : ""
			} ],
		

		/*
		$('document').ready(function() {
			var data = eval('${userRoleList}');

			$('#tableList').dataTable({
				'aaData' : data,
				"aoColumns" : [ 
				{
					"mData" : "id",
					"mRender" : function(data, type, row) {
						var ret ='<a href="editRole?id=' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
						ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" role="button" data-toggle="modal">';
						if(canEdit() === "true"){	
							ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
						}
						ret += '</a>'; 
						return ret;
					}
				}, {
					"mData" : "roleName"
				}, {
					"mData" : "roleDescription"
				}, {
					"mData" : "createdBy"
				}, {
					"mData" : "createdDate"
				}, {
					"mData" : "modifiedBy"
				}, {
					"mData" : "modifiedDate"
				}, {
					"mData" : "status"
				} ]
			});
		});
		 */
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
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
		link.attr("href", '${pageContext.request.contextPath}/admin/deleteRole?id=');
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
				<label> <spring:message code="userrole.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deleteRole?id=" title='<spring:message code="tooltip.delete" />'>
					<spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
