<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('PRODUCT_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="productListDesk" code="application.buyer.product.list" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productListDesk}] });
});
</script>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content" view-name="product">
	<div class="container col-md-12">
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="Productz.list.maintenance" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="Productz.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<c:url value="/buyer/exportProductItemCsv" var="exportCsvFormat" />
							 <div class="row">
								<div class="col-xs-12">
									<div class="upload_download_wrapper">
										<div class="right_button">
											<a href="${exportCsvFormat}">
												<button id="exportCsvReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="download.product.item"/>'>
													<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
													</span> <span class="button-content"><spring:message code="download.product.item" /></span>
											    </button>
											</a> 
											
<%-- 											<c:url value="/buyer/productTemplate" var="productTemplate" /> --%>
<%-- 											<a href="${productTemplate}"> --%>

											<c:url value="/buyer/productItemTemplate" var="productItemTemplate" />
											<a href="${productItemTemplate}">
												<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate" data-target="#myModal10" data-toggle="modal">
													<i class="excel_icon"></i>
													<spring:message code="application.download.excel.button" />
												</button>
											</a> 
											<button class="btn green-btn hvr-pop hvr-rectangle-out2" id="uploadProductItem">
												<i class="upload_icon"></i>
												<spring:message code="application.upload.listitem.button" />
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
												</span> <input type="file" data-buttonName="btn-black" id="uploadProductItemFile" name="uploadProductItemFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
												</span>
											</div>
											<button style="visibility: hidden" id="uploadProductItemFile"></button>
										</div>
									</div>
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th class="w-100" search-type=""><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="productz.code" /></th>
													<th search-type="text"><spring:message code="productz.name" /></th>
													<th search-type="text"><spring:message code="product.name" /></th>
													<th search-type="select" search-options="itemTypeList"><spring:message code="product.item.type" /></th>
													<th search-type="text"><spring:message code="Productz.purcahseGroupCode" /></th>
													<th search-type="text"><spring:message code="interface.code" /></th>
													<th search-type="text"><spring:message code="Productz.favoriteSupplier" /></th>
													<th search-type="text"><spring:message code="application.createdby" /></th>
													<th search-type=""><spring:message code="application.createddate" /></th>
													<th search-type="text"><spring:message code="application.modifiedby" /></th>
													<th search-type=""><spring:message code="application.modifieddate" /></th>
													<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
										<form method="get" action="productListMaintenance">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" class="btn btn-plus btn-info top-marginAdminList ${canEdit and !buyerReadOnlyAdmin ? '' : 'disabled' }">
												<spring:message code="Productz.list.create" />
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
			<div class="row" style="height: 10px;"></div>
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList upload_download_wrapper" style="margin-top: 11px;">
										<div class="d-flex">
											<div>
												<h2 class="left_button">
													<spring:message code="Productz.bucket.list" />
												</h2>
											</div>
											<div >
												<form method="post" action="exportSelectedProductList">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
													<input type="hidden" name="itemIds" id="itemIds" value="" />
													<input type="hidden" name="downloadtype" id="idDownloadtype" value="" />
													<button type="button" class="btn btn-default hvr-pop hvr-rectangle-out3" data-target="#confirmRemove" data-toggle="modal">Clear List</button>
													<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="exportBqFormatTemplate" >
														<i class="excel_icon"></i><spring:message code="productz.export.to.rfa.bq.format.template"></spring:message>
													</button>
													<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="exportRfsBqFormatTemplate" >
														<i class="excel_icon"></i><spring:message code="productz.export.to.rfs.bq.format.template"></spring:message>
													</button>
												</form>
											</div>
										</div>
										<table id="tableBucketList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type=""><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="productz.code" /></th>
													<th search-type="text"><spring:message code="productz.name" /></th>
													<th search-type="text"><spring:message code="product.name" /></th>
													<th search-type="text"><spring:message code="Productz.purcahseGroupCode" /></th>
													<th search-type="text"><spring:message code="Productz.favoriteSupplier" /></th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
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

.upload_download_wrapper {
	border: none;
}

.cart {
	width: 24px;
	margin-right: 3px;
}

.d-flex {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
}

.right_button {
	float: right;
}

.w-100 {
	width: 100px;
}

.btn-bg {
	border: 0;
	padding: 0 !important;
	background: transparent !important;
	outline: 0 !important;
}

#tableList td {
	padding: 15px 5px;
}

#tableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#tableBucketList td {
	padding: 15px 5px;
	background: #fff none repeat scroll 0 0;
	border-top: 1px solid #ddd;
	color: #5d5d5d;
	font-size: 14px;
}

#tableBucketList th {
	font-family: 'Open Sans', sans-serif;
	font-weight: 500;
}

#tableBucketList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#tableBucketList input::-webkit-input-placeholder {
	/* WebKit browsers */
	color: #5d5d5d;
	text-align: left;
}

#tableBucketList input:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
	color: #5d5d5d;
	text-align: center;
}

#tableBucketList input::-moz-placeholder { /* Mozilla Firefox 19+ */
	color: #5d5d5d;
	text-align: center;
}

#tableBucketList input:-ms-input-placeholder {
	/* Internet Explorer 10+ */
	color: #5d5d5d;
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
var table = '';
var bucketTable = '';
	$('document').ready(function() {

		// Setup - add a text input to each footer cell

		/* var firstRow = $('#tableList thead tr:nth-child(1)').html();
		var secondRow = $('#tableList thead tr:nth-child(2)').html();
		$('#tableList thead tr:nth-child(1)').html(secondRow);
		$('#tableList thead tr:nth-child(2)').html(firstRow); */
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 3000);
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
				"url" : getContextPath() + "/buyer/productListMaintenanceData",
				"data" : function(d) {
					//var table = $('#tableList').DataTable()
					//d.page = (table != undefined) ? table.page.info().page : 0;
					//d.size = (table != undefined) ? table.page.info().length : 10;
					//d.sort = d.columns[d.order[0].column].data + ',' + d.order[0].dir;
				},
				beforeSend : function(xhr) {
					$('#loading').show();
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
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					
					var ret = '<a href="productListEdit?id=' + row.id + '"  title=<spring:message code="tooltip.edit" /> style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>';
					ret += '&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					if (canEdit() === "true") {
						ret += '<i class="fa fa-lg fa-trash-o" aria-hidden="true"></i>';
					}
					ret += '</a>&nbsp;';
					ret +='<button class="btn-bg" title="Add to bucket list"><i class="fa fa-lg fa-cart-arrow-down" aria-hidden="true"></i></button>';
					return ret;
				}
			},{
				"data" : "itemCode"
			}, {
				"data" : "itemName",
				"defaultContent" : ""
			},  {
				"data" : "itemCategory",
				"defaultContent" : ""
			},{
				"data" : "itemType",
				"defaultContent" : ""
			},{
				"data" :"purchaseGroupCode",
				"defaultContent" : ""
			},{
				"data" : "interfaceCode",
				"defaultContent" : ""
			},{
				"data" : "favoriteSupplier",
				"defaultContent" : ""
			}, {
				"data" : "createBy",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "modifiedBy",
				"defaultContent" : ""
			}, {
				"data" : "modifiedDate",
				"searchable" : false,
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
					if (optionsType == 'itemTypeList') {
						<c:forEach items="${itemTypeList}" var="item">
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


		var sessionDate = eval('${bucketItemJson}');
		bucketTable = $('#tableBucketList').DataTable({
			"order" : [],
			"data" : sessionDate,
			"columns": [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<button  class="btn-bg" title="Remove from bucket list" ><i class="fa fa-lg fa-trash-o" aria-hidden="true"></i></button>';
					return ret;
				}
			}, {
				"data" : "itemCode"
			}, {
				"data" : "itemName",
				"defaultContent" : ""
			},  {
				"data" : "itemCategory",
				"defaultContent" : ""
			},{
				"data" :"purchaseGroupCode",
				"defaultContent" : ""
			},{
				"data" : "favoriteSupplier",
				"defaultContent" : ""
 			}]
	    });
		
		htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableBucketList thead tr:nth-child(1) th').each(function(i) {
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
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" style="width:100%;" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableBucketList thead').append(htmlSearch);
		$(bucketTable.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				bucketTable.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(bucketTable.table().container()).on('change', 'thead select', function() {
			bucketTable.column($(this).data('index')).search(this.value).draw();
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
		link.attr("href", '${pageContext.request.contextPath}/productListDelete?id=');
	}
	
	
	var bucketData = [];

	$('#tableList tbody').on('click', 'button', function () {
	    var jsonData = table.row( $(this).parents('tr') ).data();
	    console.log(">>>> " + JSON.stringify(jsonData));
	    var tabData = bucketTable.rows().data();
	    var isExisit;
		var data = {};
		tabData.each(function (value, index) {
	    	if(jsonData.id === value.id){
	    		isExisit = true;
	    		return;
	    	}
	    });
	    if(!isExisit){
    		data["id"] = jsonData.id;
    		data["itemCode"] = jsonData.itemCode;
    		data["itemName"] = jsonData.itemName;
    		data["itemCategory"] = jsonData.itemCategory;
    		data["purchaseGroupCode"] = jsonData.purchaseGroupCode;
    		data["favoriteSupplier"] = jsonData.favoriteSupplier;
    		var header = $("meta[name='_csrf_header']").attr("content");
    		var token = $("meta[name='_csrf']").attr("content");
	    	$.ajax({
				url : getContextPath() + "/buyer/addToBucketList",
				data : JSON.stringify(data),
				type : "POST",
				contentType : "application/json",
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					console.log("jsonData : " + jsonData.id);
			    	bucketTable.row.add(jsonData).draw();
					 $.jGrowl(request.getResponseHeader('success'), {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green',
							header : 'Success'
						});
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					 $.jGrowl(request.responseJSON.error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green',
							header : 'Success'
						});
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			});
	    } else {
			 $.jGrowl('Item already available in bucket list', {
					sticky : false,
					position : 'top-right',
					theme : 'bg-orange',
					header : 'Duplicate'
				});
	    }
	 } );

	$('#tableBucketList').on( 'click', 'button',  function () {
		var row = $(this).parents('tr');
		  var jsonData = bucketTable.row( row ).data();
			console.log("ID : " + jsonData.id);	
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + "/buyer/removeBucketListById/"+jsonData.id,
				type : "POST",
				contentType : "application/json",
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					bucketTable.row(row).remove().draw(false);
					 $.jGrowl(data.success, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green',
							header : 'Success'
						});
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					 $.jGrowl(request.responseJSON.error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green',
							header : 'Fail'
						});
					$('#loading').hide();
				},
		  		complete : function() {
					$('#loading').hide();
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
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="ProductList.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn pull-left btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/productListDelete?id=" title="Delete"> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmRemove" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="bucketList.clear.confirm" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="bucketList.delete" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button class="btn pull-left btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="exportBqFormatTemplateClear" type="button">
					<spring:message code="application.clear" />
				</button>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">



$('#uploadProductItem').click(function(e) {
	e.preventDefault();
	$('#uploadProductItemFile').trigger("click");

});

var files = [];
$(document).on("change", "#uploadProductItemFile", function(event) {
	files = event.target.files;
});


$("#exportBqFormatTemplateClear").on("click", function(){
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + "/buyer/removeBucketList",
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			bucketTable.clear().draw();
		    $('#confirmRemove').modal('hide');
			$('#loading').hide();
			 $.jGrowl(data.success, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green',
					header : 'Success'
				});
		},
		error : function(request, textStatus, errorThrown) {
			 $.jGrowl(request.responseJSON.error, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green',
					header : 'Fail'
				});
			$('#loading').hide();
		},
  		complete : function() {
		    $('#confirmRemove').modal('hide');
			$('#loading').hide();
		}
	});
	
});

$("#exportBqFormatTemplate").on("click", function(){
	console.log("Calling export Rfa Bq format..");	
	setExportData();
	$('#idDownloadtype').val('1');
});
 
$("#exportRfsBqFormatTemplate").on("click", function(){
	console.log("Calling export Rfs Bq format..");	
	setExportData();
	$('#idDownloadtype').val('2');
});

function setExportData() {
    var data = bucketTable.rows().data();
    console.log(data);
    if(data === null || data === undefined){
    	return false;
    }
    var ids = "";
    data.each(function (value, index) {
 		console.log(" >>>>>>>>>>>>>> " + value.id);
 		ids += value.id +",";
    });

	$('#itemIds').val(ids);

}

$("#uploadProductItemFile").on("change", function() {
	if ($(this).val() == "") {
		return;
	}

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	if ($('#uploadProductItemFile').val().length == 0) {
		$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
		$('div[id=idGlobalError]').show();
		return;
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
			
	var myForm = new FormData();
	myForm.append("file", $('#uploadProductItemFile')[0].files[0]);
	$.ajax({
		url : getContextPath()+  '/buyer/uploadProductItem',
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