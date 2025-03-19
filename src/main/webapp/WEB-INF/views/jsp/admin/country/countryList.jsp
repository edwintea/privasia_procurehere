<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<spring:message var="countryListDesk" code="application.owner.country.list" />
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${countryListDesk}] });
});
</script>
<div id="page-content" view-name="country">
	<div class="container col-md-12">
		<!-- pageging  block -->
			<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<li class="active">
					<spring:message code="country.register" />
				</li>
			</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="country.register" />
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
									<div class="form-group col-md-12 bordered-row">
										<div class="ph_tabel_wrapper scrolableTable_UserList">
											<table id="tableList" class="display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type=""><spring:message code="application.action" /></th>
														<th search-type="text"><spring:message code="country.code" /></th>
														<th search-type="text"><spring:message code="country.name" /></th>
														<th search-type="text"><spring:message code="application.createdby" /></th>
														<th search-type=""><spring:message code="application.createddate" /></th>
														<th search-type="text"><spring:message code="application.modifiedby" /></th>
														<th search-type=""><spring:message code="application.modifieddate" /></th>
														<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
													</tr>
												</thead>
											</table>
											<spring:url value="/admin/country" var="createUrl" htmlEscape="true" />
 											<a href="${createUrl}" class="btn btn-info top-marginAdminList"><spring:message code="country.create" /></a>
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
<script type="text/javascript" src="<c:url value="/resources/js/view/country.js"/>"></script>
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
			"url" : getContextPath() + "/admin/countryListData",
			"data" : function(d) {
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
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				return '<a href="editCountry/' + row.id + '" data-placement="top" title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id
						+ '\');" role="button" data-toggle="modal" data-placement="top" title="Delete"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
			}						
		}, {
			"mData" : "countryCode"
		}, {
			"mData" : "countryName"
		}, {
			"mData" : "createdBy.loginId", 
			"defaultContent" : ""
		}, {
			"mData" : "createdDate", 
			"searchable" : false,
			"defaultContent" : ""
		}, {
			"mData" : "modifiedBy.loginId", 
			"defaultContent" : ""
		}, {
			"mData" : "modifiedDate", 
			"searchable" : false,
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
				htmlSearch += '<th style="'+$(this).attr("style")+'"><select data-index="'+i+'"><option value="">Search '+title+'</option>';
				if(optionsType == 'statusList'){
					<c:forEach items="${statusList}" var="item">
					htmlSearch += '<option value="${item}">${item}</option>';
					</c:forEach>
				}
				htmlSearch += '</select></th>';
			} else {
				htmlSearch += '<th style="'+$(this).attr("style")+'"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			}
		} else {
			htmlSearch += '<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
		}
	});
	htmlSearch += '</tr>';
	$('#tableList thead').append(htmlSearch);
	$(table.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 1 || this.value.length == 0) {
			table.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(table.table().container()).on('change', 'thead select', function() {
		table.column($(this).data('index')).search(this.value).draw();
	});
	}
});
});
</script>	
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="application.country.delete" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deleteCountry?countryId=" title="Delete"><spring:message code="application.delete" /></a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
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
