<%@page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<sec:authorize access="hasRole('UOM_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="uomListDesk" code="application.uom.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${uomListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canEdit() {
		return "${canEdit}";
	}
</script>
<style>
.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0095d5 none repeat scroll 0 0;
	border-color: #0095d5;
}
</style>
<div id="page-content" view-name="idSettings">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<sec:authorize access="hasRole('BUYER')">
				<li>
					<a href="${pageContext.request.contextPath}/buyer/buyerDashboard"">
						<spring:message code="application.dashboard" /> 
					</a>
				</li>
			</sec:authorize>
			<sec:authorize access="hasRole('SUPPLIER')">
				<li>
					<a href="${pageContext.request.contextPath}/supplier/supplierDashboard"">
						<spring:message code="application.dashboard" />
					</a>
				</li>
			</sec:authorize>
			<sec:authorize access="hasRole('OWNER')">
				<li>
					<a href="${pageContext.request.contextPath}/owner/ownerDashboard"">
						<spring:message code="application.dashboard" />
					</a>
				</li>
			</sec:authorize>
			<li class="active">
				<spring:message code="idsettings.list" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="idsettings.list" />
			</h2>
		</div>
		  <div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block"> <header class="form_header"> <form:errors path="*" cssClass="error" /> <c:if test="${not empty errors}">
							<span class="error">${errors}</span>
						</c:if> </header> <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="row">
							<div class="col-xs-12">
								<div class="ph_tabel_wrapper scrolableTable_UserList">
									<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th search-type=""><spring:message code="application.action" /></th>
												<th search-type="text"><spring:message code="label.idtype" /></th> 
												<th search-type="text"><spring:message code="label.idprefix" /></th>
												<th search-type=""><spring:message code="systemsetting.id.delimiter" /></th>
												<th search-type=""><spring:message code="systemsetting.date.pattern" /></th>
<%-- 												<th search-type=""><spring:message code="label.paddingLength" /></th> --%>
												<th search-type="text"><spring:message code="application.modifiedby" /></th>
												<th search-type=""><spring:message code="application.modifieddate" /></th>
												<%-- <th search-type="select" search-options="statusList"><spring:message code="application.status" /></th> --%>
											</tr>
										</thead>
									</table>
									<%-- <spring:url value="/buyer/idSettings" var="createUrl" htmlEscape="true" />
									<a href="${canEdit ? createUrl : '#'}" class="btn btn-info top-marginAdminList ${canEdit ? '' : 'disableEve'} ${!buyerReadOnlyAdmin ? '':'disabled'}">
										<a href="${createUrl}" class="btn btn-info top-marginAdminList "> 
										Create ID
									</a> --%>
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
<input type="hidden" id="delId" value="" />
<!-- NEw add -->
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
</style>
<script type="text/javascript">
	$('document').ready(function() {

		// Setup - add a text input to each footer cell

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
			"ajax" : {
				"url" : getContextPath() + "/buyer/idSettingsListData",
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
			"columns" : [ {
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"mRender" : function(data, type, row) {
					var tImg = "";
					if (canEdit() === "true") {
						/* tImg = '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">'; */
					}
					return '<a href="editIdSettings?id=' + row.id + '"  title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">' + tImg + '</a>';
/* 					return '<a href="editUom?id=' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
 */
				}
			}, {
				"mData" : "idType",
				"defaultContent" : ""
			}, {
				"mData" : "idPerfix",
				"defaultContent" : ""
			}, {
				"mData" : "idDelimiter",
				"searchable" : false,
				"defaultContent" : ""
			},{
				"mData" : "idDatePattern",
				"defaultContent" : ""
			}, /* {
				"mData" : "paddingLength",
				"defaultContent" : ""
			}, */{
				"mData" : "modifiedBy.loginId",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedDate",
				"searchable" : false,
				"defaultContent" : ""
			}],
	
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
			if ($.trim(this.value).length >= 2 || this.value.length == 0) {
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
		link.attr("href", '${pageContext.request.contextPath}/admin/uom/deleteUom?id=');
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
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">�</button>
			</div>
			<div class="modal-body">
				<label>
					<spring:message code="uom.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/uom/deleteUom?id=" title=<spring:message code="tooltip.delete" />>
					<spring:message code="application.delete" />
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
