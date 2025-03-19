<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<link rel="stylesheet" type="text/css"	href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<sec:authorize access="hasRole('OWNER')" var="owner" />
<input type="hidden" id="secOwner" name="secOwner" value="${owner}">
<div>
	<c:set var="fileType" value="" />
	<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
		<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
	</c:forEach>
	<form action="${pageContext.request.contextPath}/supplierreg/suppNotesUploadDocument" method="POST" enctype="multipart/form-data" id="NotesDocForm">
		<input type="hidden" name="supplierId" id="supplierId" value="${supplier.id}" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<div class="row">
			<div class="col-md-12">
				<div class="col-md-4 pad_all_15">
					<label>Select File</label>
					<div class="fileinput fileinput-new input-group" data-provides="fileinput">
						<spring:message code="meeting.doc.file.length" var="filelength" />
						<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />
						<div data-trigger="fileinput" class="form-control">
							<span class="fileinput-filename show_name"></span>
						</div>
						<span class="input-group-addon btn btn-black btn-file">
							<span class="fileinput-new">
								<spring:message code="application.selectfile" />
							</span>
							<span class="fileinput-exists">
								<spring:message code="event.document.change" />
							</span>
							<input name="supNotesUploadDocument" id="supNotesUploadDocument" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file"
								data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
						</span>
					</div>
					<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
					<div class="progressbar flagvisibility" data-value="0">
						<div class="progressbar-value bg-purple">
							<div class="progress-overlay"></div>
							<div class="progress-label">0%</div>
						</div>
					</div>
					<span>
						<spring:message code="application.note" />:<br />
						<ul>
							<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
							<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
						</ul>
					</span>
				</div>
				<div class="col-md-4 pad_all_15">
					<spring:message code="event.doc.file.descrequired" var="descrequired" />
					<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
					<spring:message code="event.document.filedesc" var="filedesc" />
					<label><spring:message code="podocument.file.description" /></label>
					<input class="form-control" name="docDescription" id="docDescription" data-validation="length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">
				</div>
				<div class="col-md-2 pad_all_15">
					<label>&nbsp;</label> <br />
					<button class="upload_btn btn btn-info  ph_btn_midium hvr-pop hvr-rectangle-out" id="uploadSuppNotesDoc">
						<spring:message code="application.upload" />
					</button>
				</div>
				<div class="toggle_wrap">
					<c:if test="${owner}">
						<div class="toggle-button" style="margin-right: 20px;">
							<input type="hidden" id="visibleDocument" name="visibleDocument" value="0" />
							<button></button>
						</div>
						<div class="toggle_text">
							<label><spring:message code="supplier.visible.to.buyer" /></label>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</form>
	<div class="Invited-Supplier-List-table pad_all_15 add-supplier">
		<div class="ph_table_border">
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<header class="form_header">
								<form:errors path="*" cssClass="error" />
								<c:if test="${not empty errors}">
									<span class="error">${errors}</span>
								</c:if>
								<div class="row">
									<div class="col-xs-12">
										<div class="ph_tabel_wrapper scrolableTable_UserList">
											<table id="tableDocList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type="" class=" width_100"><spring:message code="application.action" /></th>
														<th search-type="text" class="align-left"><spring:message code="event.document.filename" /></th>
														<th search-type="text" class="align-left"><spring:message code="event.document.description" /></th>
														<th search-type="" class="align-left"><spring:message code="event.document.publishdate" /></th>
														<th search-type="text" class="align-left"><spring:message code="supplier.upload.by" /></th>
														<c:if test="${owner}">
															<th search-type="" class="align-left"><spring:message code="supplier.visible.to.buyer" /></th>
														</c:if>
													</tr>
												</thead>
											</table>
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


<!-- Update SuppNotes DOCUMENT -->
<div class="flagvisibility dialogBox" id="documentDescriptionPopup" title="SuppNotes Document Decription">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<form action="${pageContext.request.contextPath}/supplierreg/updateSuppNotesDocumentDesc" method="POST">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="form-group col-md-6">
						<label><spring:message code="podocument.file.description" /></label>
						<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
						<textarea class="width-100 form-control" id="docDec" name="docDec" value="" placeholder="${filedesc}" rows="3" data-validation="length" data-validation-length="max250" maxlength="250"></textarea>
						<spring:message code="event.document.filedesc" var="filedesc" />
						<span class="sky-blue">${maxlimit}</span>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="align-center">
							<input type="hidden" id="editIdDocument" name="docId" />
							<input type="hidden" name="supplierId" id="supplierId" value="${supplier.id}" />
							<!-- <a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out"
							 data-original-title="Delete">Update</a> -->
							<button type="button" id="confUpdSuppNotesDoc" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="application.update"/></button>
							<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" id="confDelSuppNotesDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="application.confirm.message.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form action="${pageContext.request.contextPath}/removeSuppNotesDocument" method="POST">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="supplierId" id="supplierId" value="${supplier.id}" />
					<input type="hidden" name="removeDocId" id="deleteIdDocument" />
					<button id="confDelDocument" type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
						<spring:message code="application.delete" />
					</button>
				</form>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin}">
	$(window).bind('load',function() {
						var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton';
						//var disableAnchers = ['#reloadMsg'];        
						disableFormFields(allowedFields);
					});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	var supplierNoteDocTable =  '';
	
	function dataTableCallForSupplierNotesDoc(){
		if($('#secOwner').val() == 'true'){
			console.log("1"+$('#secOwner').val());
				 supplierNoteDocTable = $('#tableDocList').DataTable({
						"oLanguage":{
							"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
					},
					 
				"processing" : true,
				"deferRender" : true,
				"preDrawCallback" : function(settings) {
					$('#loading').show();
					/* console.log("preDrawCallback"); */
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
					"url" : getContextPath() + "/supplierreg/suppNotesListData",
					data : {
						'suppId' : $('#idSupplier').val()
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
						var imgHtml = '';
						imgHtml +=  '<a href="${pageContext.request.contextPath}/supplierreg/updateSuppNotesDocumentDesc/'+row.id+'" data-id="'+ row.id +'" data-desc="' + row.description +'"  data-placement="top" title="Edit" class="editNoteSuppDocFile" >' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/edit1.png">' + '</a>';
						imgHtml += '<a href="${pageContext.request.contextPath}/supplierreg/removeSuppNotesDocument/'+row.id+'"  title="Delete" removeDocId="'+ row.id +'" class="removeNoteSuppDocFile" >' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/delete.png">' +'</a>';
						imgHtml += '<a href="${pageContext.request.contextPath}/supplierreg/downloadSuppNotesDoc/'+row.id+'" data-id="'+ row.id +'"  data-placement="top" title="Download">' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/download.png">' +'</a>';
						return imgHtml;	
					}
				}, {
					"mData" : "fileName",
					"defaultContent" : ""
				}, {
					"mData" : "description",
					"defaultContent" : ""
				}, {
					"mData" : "uploadDate",
					"searchable" : false,
					"defaultContent" : ""
				}, {
					"mData" : "userName",
					"defaultContent" : ""
				},{
					"mData" : "visible",
					"defaultContent" : "",
					"mRender" : function(data, type, row) {
						return row.visible ? 'Yes' : 'No';
					}
				}],
		
				"initComplete": function(settings, json) {
			 var htmlSearch = '<tr class="tableHeaderWithSearch">';
			$('#tableDocList thead tr:nth-child(1) th').each(function(i) {
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
			$('#tableDocList thead').append(htmlSearch);
			$(supplierNoteDocTable.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					supplierNoteDocTable.column($(this).data('index')).search(this.value).draw();
				}
			});
			$(supplierNoteDocTable.table().container()).on('change', 'thead select', function() {
				supplierNoteDocTable.column($(this).data('index')).search(this.value).draw();
			});
				}
				 });
		}else{
			console.log("2"+$('#secOwner').val());
		 supplierNoteDocTable = $('#tableDocList').DataTable({
			 "oLanguage":{
					"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			 "processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#loading').show();
				/* console.log("preDrawCallback"); */
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
				"url" : getContextPath() + "/supplierreg/suppNotesListData",
				data : {
					'suppId' : $('#idSupplier').val()
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
					
					console.log("*** tenantType :" + row.tenantType);
					var imgHtml = '';
					if(row.tenantType == 'BUYER'){
					imgHtml +=  '<a href="${pageContext.request.contextPath}/supplierreg/updateSuppNotesDocumentDesc/'+row.id+'" data-id="'+ row.id +'" data-desc="' + row.description +'"  data-placement="top" title="Edit" class="editNoteSuppDocFile" >' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/edit1.png">' + '</a>';
					imgHtml += '<a href="${pageContext.request.contextPath}/supplierreg/removeSuppNotesDocument/'+row.id+'"  data-placement="top" title="Delete" removeDocId="'+ row.id +'" class="removeNoteSuppDocFile" >' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/delete.png">' +'</a>';
					}
					imgHtml += '<a href="${pageContext.request.contextPath}/supplierreg/downloadSuppNotesDoc/'+row.id+'" data-id="'+ row.id +'"  data-placement="top" title="Download">' + '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/download.png">' +'</a>';
					return imgHtml;
				}
			}, {
				"mData" : "fileName",
				"defaultContent" : ""
			}, {
				"mData" : "description",
				"defaultContent" : ""
			}, {
				"mData" : "uploadDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "userName",
				"defaultContent" : ""
			}],

			"initComplete": function(settings, json) {
		 var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableDocList thead tr:nth-child(1) th').each(function(i) {
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
					htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableDocList thead').append(htmlSearch);
		$(supplierNoteDocTable.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				supplierNoteDocTable.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(supplierNoteDocTable.table().container()).on('change', 'thead select', function() {
			supplierNoteDocTable.column($(this).data('index')).search(this.value).draw();
		});
			}
		 });
	}
	}
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierNotesDocument.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/eventCq.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/cq_form.js"/>"></script>
<script	src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript">
	$('document').ready(function() {
		dataTableCallForSupplierNotesDoc();
		// Setup - add a text input to each footer cell
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
			
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">

