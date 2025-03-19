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
<div class="panel">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" data-parent="#accordion" href="#collapseForm"><spring:message code="defultMenu.suppliers.forms" /> </a>
		</h4>
	</div>
	<div id="collapseForm" class="panel-collapse collapse">
	<div class="panel-body">
	<input type="hidden" id="idSupplier" value="${supplier.id}" />
		<div class="create_sub note marg-bottom-20">
			<div class="col-md-8">
				<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
				<input type="hidden" id="idSupplier" value="${supplier.id}" />
				<h3 class="blue_form_sbtitle pad-tb-30 add_file text-black"></h3>
				<c:if test="${not empty errors }">
					<div class="alert alert-danger" id="idGlobalError">
						<div class="bg-red alert-icon">
							<i class="glyph-icon icon-times"></i>
						</div>
						<div class="alert-content">
							<h4 class="alert-title">Error</h4>
							<p id="idGlobalErrorMessage">
								<c:forEach var="error" items="${errors}">
										${error}<br />
								</c:forEach>
							</p>
						</div>
					</div>
				</c:if>
				<c:if test="${isFavSupplier}">
					<form action="${pageContext.request.contextPath}/buyer/sendFormToSupplier" method="POST"  id="sendFormToSupplier">
					<input type="hidden" name="supplierId" id="supplierId" value="${supplier.id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="row marg-bottom-20" id="selectFormDiv">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="supplier.assign.form.label" /> :</label>
						</div>
						<div class="col-md-9 col-sm-9">
							<div class="input-group search_box_gray">
							<select class="chosen-select chosen supplierFormSelect" name="" id="idSupplierFormSelect" data-validation="required" multiple data-placeholder="Select Supplier Form">
								<option value="">
									<spring:message code="buyer.select.supplier.form" var="selectForm" />
								</option>
								<c:forEach items="${supplierFormList}" var="supplierForm">
									<option value="${supplierForm.id}">${supplierForm.name}</option>
								</c:forEach>
							</select>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-3"></div>
						<section class="step4_form selectfile">
							<div class="col-md-3 marg-bottom-20 addnote marg-top-20" style="padding-left: 0px;">
								<button class="btn btn-info ph_btn_midium btn-margin-top hvr-pop hvr-rectangle-out" type="button" id="sendForm">
									<spring:message code="supplier.send.form.btn" />
								</button>
							</div>
						</section>
					</div>
				</form>
			</c:if>
			</div>
		</div>
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
													<table id="tableFormList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class=" width_100"><spring:message code="application.action" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplierForm.name.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplierForm.description.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.requested.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.request.owner" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.submitted.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.submit.owner" /></th>
																<th search-type="select"  class="align-left" search-options="supplierFormStatusList"><spring:message code="application.status" /></th>
																
															</tr>
														</thead>
													</table>
												</div>
												<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
											</div>
										</div>
										</header>
								</section>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="mydeleteFormModal" role="dialog">
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
				<label> <spring:message code="supplierForm.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idFormConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="#" title="Delete" data-id=''> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
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
		modules : 'date'
	});
	var supplierFormsData1='';
	$('document').ready(function() {

		// Setup - add a text input to each footer cell
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		 supplierFormsData1 = $('#tableFormList').DataTable({
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
				$("#poReportId").val(false);
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/supplierFormListDataById",
				"data" : {
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
				"mRender":function(data,type,row){
					var tImg="";
					var ret='';
					tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
					ret += '<a href="${pageContext.request.contextPath}/buyer/supplierFormSubView/' + row.id + '" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'; 
					if(row.status=='PENDING'){
						ret += '<a href="#mydeleteFormModal" onClick="javascript:updateFormLink(\'' + row.id +'\');" title="Delete" role="button"  data-toggle="modal">' + tImg +'</a>';
					}
					return ret;
				}
			},{
				"data" : "name",
				"defaultContent" : ""
			},{
				"data" : "description",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "requestedDate",
				"searchable" : false,
				"type" : 'custom-date',
				"defaultContent" : ""
			},{
				"data" : "requestedBy",
				"defaultContent" : ""
			},{
				"data" : "submitedDate",
				"searchable" : false,
				"type" : 'custom-date',
				"defaultContent" : ""
			}, {
				"data" : "submittedBy",
				"defaultContent" : ""
			},{
				"data" : "status",
				"defaultContent" : ""
			}],
			"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableFormList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="' + classStyle + '"><select data-index="'+i+'"><option value="">Search ' + title + '</option>';
					if (optionsType == 'supplierFormStatusList') {
						<c:forEach items="${supplierFormStatusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				}else{
					htmlSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableFormList thead').append(htmlSearch);
		$(supplierFormsData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				supplierFormsData1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(supplierFormsData1.table().container()).on('change', 'thead select', function() {
			supplierFormsData1.column($(this).data('index')).search(this.value).draw();
		});
		}
		});

	});	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierForm.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/eventCq.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/cq_form.js"/>"></script>
<script	src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript">
function updateFormLink(id){
	var link=$("a#idFormConfirmDelete");
	link.data('data-id', link.attr("data-id"));
	link.attr("data-id", link.data('data-id') + '' + id);
}
function doCancel() {
	var link = $("a#idFormConfirmDelete");
	link.data('data-id', link.attr("data-id"));
	link.attr("data-id", '');
}
</script>
			
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">

