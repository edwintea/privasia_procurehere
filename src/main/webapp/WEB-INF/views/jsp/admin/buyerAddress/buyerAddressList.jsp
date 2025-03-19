<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('BUYER_ADDRESS_EDIT') or (hasRole('BUYER') and hasRole('ADMIN'))" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="buyerAddrListDesk" code="application.buyer.address.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerAddrListDesk}] });
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
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>
			<li class="active"><spring:message code="buyeraddress.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="buyeraddress.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="upload_download_wrapper">
						<div class="right_button">
<%-- 							<c:url value="/buyer/buyerAddressTemplate" var="buyerAddressTemplate" /> --%>
<%-- 							<a href="${buyerAddressTemplate}"> --%>
<!-- 								<button class="btnAddresslist btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate"> -->
<%-- 									<i class="excel_icon"></i> <spring:message code="application.download.excel.button" /> --%>
<!-- 								</button> -->
<!-- 							</a> -->
							
							<c:url value="/buyer/exportBuyerAddress" var="buyerAddressCsv" />
							<a href="${buyerAddressCsv}">
								<button id="downloadTemplate" class="btnAddresslist btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="export.address.button"/>'>
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
									</span> <span class="button-content"><spring:message code="export.address.button" /></span>
								</button>
							</a>
						</div>
					</div>
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList" class="table table-striped table-bordered dataTable">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type=""><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="buyeraddress.caption.title" /></th>
													<th search-type="text"><spring:message code="buyeraddress.caption.line1" /></th>
													<th search-type="text"><spring:message code="application.city" /></th>
													<th search-type="text"><spring:message code="label.state" /></th>
													<th search-type="text"><spring:message code="application.createdby" /></th>
													<th search-type="" class="width_200 width_200_fix "><spring:message code="application.createddate" /></th>
													<th search-type="text"><spring:message code="application.modifiedby" /></th>
													<th search-type="" class="width_200 width_200_fix"><spring:message code="application.modifieddate" /></th>
													<th search-type="select" search-options="statusList"><spring:message code="Productz.status" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
										<form method="get" action="buyerAddress">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" class="btn btn-plus btn-info top-marginAdminList ${!buyerReadOnlyAdmin ? '' : 'disabled' }">
												<spring:message code="buyeraddress.list.create" />
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
<!-- NEw add -->
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
</style>
<style>
.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0cb6ff none repeat scroll 0 0 !important;
	color: #fff;
	border-color: #0095d5;
}
</style>
<script type="text/javascript">
	$("#test-select").treeMultiselect({ enableSelectAll : true, sortable : true });
</script>
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
			/* $('div[id=idGlobalError]').hide();
			$('#loading').show(); */
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
			"url" : getContextPath() + "/buyer/buyerAddressData",
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
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"render" : function(data, type, row) {
				var ret = '<a href="buyerAddressEdit?id=' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
				ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
				if(canEdit() === "true"){	
					ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
				}
				ret += '</a>';
				return ret;
			}
		}, {
			"data" : "title"
		}, {
			"data" : "line1",
			"defaultContent" : "",
			"className": "width_200 width_200_fix"
		}, {
			"data" : "city",
			"defaultContent" : ""
		}, {
			"data" : "state.stateName",
			"defaultContent" : ""
		},{ "data" : "createdBy.loginId", "defaultContent" : "" },
		  { "data" : "createdDate", "defaultContent" : "" },
		  { "data" : "modifiedBy.loginId", "defaultContent" : "" },
		  { "data" : "modifiedDate", "defaultContent" : "" },
		  { "data" : "status", "defaultContent" : "" }],
	
	"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#tableList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
			if($(this).attr('search-type') == 'select'){
				var optionsType = $(this).attr('search-options');
				htmlSearch += '<th><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> '+title+'</option>';
				if(optionsType == 'statusList'){
					<c:forEach items="${statusList}" var="item">
					htmlSearch += '<option value="${item}">${item}</option>';
					</c:forEach>
				}
				htmlSearch += '</select></th>';
			} else {
				htmlSearch += '<th><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
			}
			} else {
			htmlSearch += '<th>&nbsp;</th>';
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
		link.attr("href", '${pageContext.request.contextPath}/buyer/buyerAddressDelete?id=');
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
				<label>
					<spring:message code="buyeraddress.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/buyerAddressDelete?id=" title=<spring:message code="tooltip.delete" />>
					<spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
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

.btnAddresslist {
	line-height: 38px;
	height: 38px;
	min-width: 36px;
	float: right;
	margin-right: 20px;
	margin-top: 10px;
}
</style>
