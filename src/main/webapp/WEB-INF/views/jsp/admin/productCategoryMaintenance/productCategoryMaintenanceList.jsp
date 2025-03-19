<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('PRODUCT_CATEGORY_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="productCatgListDesk" code="application.buyer.product.category.list" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productCatgListDesk}] });
});
</script>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content" view-name="productCategory">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<li class="active">
				<spring:message code="Product.pcl" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="Product.pcl" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block"> </header> <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="row">
							<div class="col-xs-12">
									<div class="upload_download_wrapper">
										<div class="right_button">
											<c:url value="/buyer/productCategoryTemplate" var="productCategoryTemplate" />
<%-- 											<a href="${productCategoryTemplate}"> --%>
<!-- 												<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal"> -->
<%-- 													<i class="excel_icon"></i><spring:message code="application.download.excel.button" /> --%>
<!-- 												</button> -->
<!-- 											</a> -->

											<c:url value="/buyer/productCategoryCsv" var="productCategoryCsv" />
											<a href="${productCategoryCsv}">
<!-- 												<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal"> -->
<%-- 													<i class="excel_icon"></i><spring:message code="export.prod.cat.csv.button" /> --%>
<!-- 												</button> -->
												
												<button id="downloadTemplate" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="export.prod.cat.csv.button"/>'>
													<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
													</span> <span class="button-content"><spring:message code="export.prod.cat.csv.button" /></span>
												</button>
											</a>
											
											<c:url value="/buyer/productCategoryXlTemplate" var="productCategoryXlTemplate" />
											<a href="${productCategoryXlTemplate}">
												<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal">
													<i class="excel_icon"></i><spring:message code="application.download.excel.button" />
												</button>
											</a>
											<button class="btn green-btn hvr-pop hvr-rectangle-out2" id="uploadProductCategory">
												<i class="upload_icon"></i> <spring:message code="application.upload.listitem.button" />
											</button>
											<div data-provides="fileinput" class="fileinput hide fileinput-new input-group">
												<%-- <spring:message code="event.doc.file.required" var="required" />
											<spring:message code="event.doc.file.length" var="filelength" />
											<spring:message code="event.doc.file.mimetypes" var="mimetypes" /> --%>
												<div data-trigger="fileinput" class="form-control">
													<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename show_name"></span>
												</div>
												<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
												</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
												</span> <input type="file" data-buttonName="btn-black" id="uploadProductCategoryFile" name="uploadProductCategoryFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
												</span>
											</div>
											<button style="visibility: hidden" id="uploadProductCategoryFile"></button>
										</div>
									</div>
									<div class="ph_tabel_wrapper scrolableTable_UserList">
									<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th search-type=""><spring:message code="application.action" /></th>
												<th search-type="text"><spring:message code="product.code" /></th>
												<th search-type="text"><spring:message code="product.name" /></th>
												<th search-type="text"><spring:message code="application.createdby" /></th>
												<th search-type=""><spring:message code="application.createddate" /></th>
												<th search-type="text"><spring:message code="application.modifiedby" /></th>
												<th search-type=""><spring:message code="application.modifieddate" /></th>
												<th search-type="select" search-options="statusList"><spring:message code="Productz.status" /></th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
									<form method="get" action="productCategoryMaintenance">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" class="btn btn-plus btn-info top-marginAdminList ${canEdit and !buyerReadOnlyAdmin ? '' : 'disabled'}" >
											<spring:message code="Productz.create" />
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
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
var table = '';

	$('document').ready(function() {

		// Setup - add a text input to each footer cell

		table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				return true;
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				//$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/productCategoryData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					console.log('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
				}
			},
			"order" : [],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<a href="productCategoryEdit?id=' + row.id + '"  title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
					if (canEdit() === "true") {
						ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
					}
					ret += '</a>';
					return ret;
				}
			}, {
				"data" : "productCode"
			}, {
				"data" : "productName",
				"defaultContent" : ""
			}, {
				"data" : "createdBy.loginId",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"defaultContent" : ""
			}, {
				"data" : "modifiedBy.loginId",
				"defaultContent" : ""
			}, {
				"data" : "modifiedDate",
				"defaultContent" : ""
			}, {
				"data" : "status",
				"defaultContent" : ""
			} ],
		
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
		link.attr("href", '${pageContext.request.contextPath}/admin/stateDelete?id=');
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
			<div class="modal-body" style="padding: 12px;">
			<label>
					<spring:message code="Productz.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn pull-left btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/productCategoryDelete?id=" title=<spring:message code="tooltip.delete" />>
					<spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

$('#uploadProductCategory').click(function(e) {
	e.preventDefault();
	$('#uploadProductCategoryFile').trigger("click");

});

var files = [];
$(document).on("change", "#uploadProductCategoryFile", function(event) {
	files = event.target.files;
});

$("#uploadProductCategoryFile").on("change", function() {
	if ($(this).val() == "") {
		return;
	}

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	if ($('#uploadProductCategoryFile').val().length == 0) {
		$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
		$('div[id=idGlobalError]').show();
		return;
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
			
	var myForm = new FormData();
	myForm.append("file", $('#uploadProductCategoryFile')[0].files[0]);

	$.ajax({
		url : getContextPath()+ '/buyer/uploadProductCategory',
		data : myForm,
		type : "POST",
		enctype : 'multipart/form-data',
		processData : false,
		contentType : false,
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalInfo]').show();
			}
			//reload only datatable 
			table.ajax.reload();
			
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
	$(this).val("");
});


</script>

<style>
#tableList td {
	text-align: center;
}


.upload_download_wrapper {
	border: none;
}

.right_button {
	float: right;
}
</style>