<%@page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script
	src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<sec:authorize access="hasRole('UOM_EDIT') or hasRole('ADMIN')"
	var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')"
	var="buyerReadOnlyAdmin" />
<spring:message var="agreementTypeListDesk" code="application.agreement.type.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${agreementTypeListDesk}] });
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

	<form:form id="productListMaintenanceForm" data-parsley-validate="" commandName="productContract" action="saveProductContract?${_csrf.parameterName}=${_csrf.token}" method="post" cssClass="form-horizontal bordered-row" enctype="multipart/form-data">
			<form:hidden path="id" id="id" />
			<input type="hidden" value="${loggedInUserId}" id="loggedInUserId">

<c:if test="${not empty productContract.id}">
			<div class="container col-md-12 marg-top-20">
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="Section-title title_border gray-bg">
					<h2 class="trans-cap manage_icon">
						<spring:message code="contract.item.list" />
					</h2>
				</div>
				<c:if test="${!contractExpire}">
					<button type="submit" class="btn btn-plus btn-info top-marginAdminList" style="margin-bottom: 20px;" id="createContractItemId">
						<spring:message code="contract.item.create" />
					</button>
				</c:if>
				<div class="container-fluid col-md-12">
					<div class="row">
						<div class="col_12">
							<div class="white_box_brd pad_all_15">
								<section class="index_table_block">
									<div class="row">
										<div class="col-xs-12">

											<div class="ph_tabel_wrapper scrolableTable_UserList">
												<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="align-left width_100_fix"><spring:message code="application.action" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.Contract.itemNum" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.itemName" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.itemCode" /></th>
															<th search-type="text" class="align-left"><spring:message code="contract.item.category" /></th>
															<th search-type="text" class="align-left"><spring:message code="storage.uom" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.item.Quantity" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.item.BQuantity" /></th>
															<th search-type="text" class="align-left"><spring:message code="productlist.unitPrice.item" /></th>
															<th search-type="text" class="align-left"><spring:message code="contract.item.tax" /></th>
															<th search-type="select" search-options="itemTypeList"><spring:message code="product.item.type" /></th>
															<th search-type="text" class="align-left"><spring:message code="contract.item.brand" /></th>
															<th search-type="text" class="align-left"><spring:message code="storage.Location" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.businessUnit" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.costcenter" /></th>
															
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>
											</div>
											<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
										</div>
									</div>
								</section>
							</div>
						</div>
					</div>
					<div class="row" style="height: 10px;"></div>
				</div>
			</div>
		</c:if>
		</form:form>
		
		<div id="contractItemModel" class="modal fade" role="dialog">
	<form action="" id="addcontractItemForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
		<input type="hidden" id="productContractId" value="${productContract.id}" />
		<div class="modal-dialog" style="width: 90%; max-width: 60%;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="contract.item" />
					</h3>
					<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="product.Contract.itemNum" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? "disabled" : ""}">
							<spring:message code="product.item.number.placeholder" var="itemNumber" />
							<spring:message code="product.item.number.required" var="required" />
							<spring:message code="item.number.length" var="length" />
							<input id="contractItemNumberId" name="contractItemNumber" data-validation="required length" data-validation-length="1-16" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" class="form-control marg-bottom-10" placeholder="${itemNumber}" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10">Free Text Item</label>
						</div>
						<div class="col-md-4 marg-top-10 ${contractExpire ? "disabled" : ""}">
							<input type="checkbox" id="idFreeTextItem" name="freeTextItemEntered" class="custom-checkbox" title="Free Text Contract Item" />
						</div>
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="product.item" />
							</label>
						</div>
						<div id="idItemFreeText" class="col-md-4 ${contractExpire ? "disabled" : ""}" style="display:none;">
							<input type="text" data-validation="required length" data-validation-length="1-250" id="itemName" name="itemName" class="form-control" placeholder="Enter Item Name">
						</div>
						<div id="idItemList" class="col-md-4 ${contractExpire ? "disabled" : ""}">
							<spring:message code="product.item.empty" var="required" />
							<select name="productItem" id="chosenProductItem" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="product.select.item" />
								</option>
								<c:forEach items="${productItemList}" var="item">
									<c:if test="${empty item.id}">
										<option value="" disabled>${item.itemName}</option>
									</c:if>
									<c:if test="${!empty item.id and item.id != productContract.businessUnit.id}">
										<option value="${item.id}">${item.itemName}</option>
									</c:if>
									<c:if test="${!empty item.id and item.id == productContract.businessUnit.id}">
										<option value="${item.id}" selected>${item.itemName}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>
					
					
					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="product.item.Quantity" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="contract.quantity.empty" var="required" />
							<spring:message code="event.document.quantity" var="quantity" />
							<input type="text" data-validation="required custom" data-validation-error-msg-required="${required}" id="quantityId" name="quantity" class="form-control" id="itemQuantity" placeholder="${quantity}" data-validation-ignore="," data-validation-regexp="^(?:\d+)(?:(?:\d+)|(?:(?:,\d+)?))+(?:\.\d{1,})?$"
								data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>
						<div class="col-md-2">
							<label for="idProductItemType" class="marg-top-10"><spring:message code="product.item.type" /> </label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<select name="productItemType" id="chosenProductItemType" class="chosen-select">
								<c:forEach items="${itemTypeList}" var="type">
									<option value="${type}">${type}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="productlist.unitPrice.item" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="event.document.unitpricing" var="unitpricing" />
							<spring:message code="contract.unit.price.empty" var="required" />
							<input type="text" class="form-control" data-validation="required custom" data-validation-error-msg-required="${required}" name="unitPrice" id="unitPriceId" placeholder="${unitpricing}" data-validation-ignore=","
								data-validation-regexp="^(?:\d+)(?:(?:\d+)|(?:(?:,\d+)?))+(?:\.\d{1,})?$" data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>
						<!-- Tax -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.tax" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="contract.tax.empty" var="required" />
							<spring:message code="contract.tax.price" var="price" />
							<input type="text" name="tax" class="form-control" id="itemTax" placeholder="${price}" data-validation-ignore="," data-validation-regexp="^(?:\d+)(?:(?:\d+)|(?:(?:,\d+)?))+(?:\.\d{1,})?$"
								data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>
					</div>

					<div class="row marg-bottom-20">
						<!--  Brand -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.brand" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="contract.brand.quantity" var="itemBrand" />
							<input type="text" id="contractItemBrand" name="contractBrand" maxlength="64" class="form-control marg-bottom-10" placeholder="${itemBrand}" />
						</div>

						<!-- Category -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.category" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="product.item.empty" var="required" />
							<select name="productItemCategory" id="chosenProductCategory" class="chosen-select">
								<option value="">
									<spring:message code="Product.select.category" />
								</option>
								<c:forEach items="${productCategoryList}" var="pc">
									<c:if test="${pc.id == '-1'}">
										<option value="-1" disabled>${pc.productCode} - ${pc.productName}</option>
									</c:if>
									<c:if test="${pc.id != '-1'}">
										<option value="${pc.id}">${pc.productCode} - ${pc.productName}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="storage.Location" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="product.storage.location.placeholder" var="storageLoc" />
							<spring:message code="product.storage.location.length" var="length" />
							<input name="storageLocation" data-validation="length" data-validation-length="0-32" id="storageLocationId" class="form-control marg-bottom-10" placeholder="${storageLoc}" />
						</div>
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="storage.uom" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="contract.uom.empty" var="required" />
							<select name="uom" id="chosenUom" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="product.select.uom" />
								</option>
								<c:forEach items="${uomList}" var="uom">
									<option value="${uom.id}">${uom.uom}- ${uom.uomDescription}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="label.budget.businessUnit" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="business.unit.empty" var="required" />
							<select name="businessUnit" id="chosenBusinessUnitId" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="pr.select.business.unit" />
								</option>
								<c:forEach items="${businessUnitChild}" var="unitData">
									<c:if test="${empty unitData.id}">
										<option value="" disabled>${unitData.displayName}</option>
									</c:if>
									<c:if test="${!empty unitData.id and unitData.id != productContract.businessUnit.id}">
										<option value="${unitData.id}">${unitData.displayName}</option>
									</c:if>
									<c:if test="${!empty unitData.id and unitData.id == productContract.businessUnit.id}">
										<option value="${unitData.id}" selected>${unitData.displayName}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="label.budget.costCenter" />
							</label>
						</div>
						<div class="col-md-4 ${contractExpire ? 'disabled':''}">
							<spring:message code="contract.cost.center.empty" var="required" />
							<select name="costCenter" id="chosenCostCenter" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="pr.select.cost.center" />
								</option>
								<c:forEach items="${costCenterList}" var="cost">
									<c:if test="${cost.id == '-1'}">
										<option value="-1" disabled>${cost.costCenter} - ${cost.description}</option>
									</c:if>
									<c:if test="${cost.id != '-1' }">
										<option value="${cost.id}">${cost.costCenter} - ${cost.description}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<input type="hidden" id="hiddenId" value="" />
				<div class="modal-footer  text-center">
					<c:if test="${!contractExpire}">
						<button type="button" id="saveContractItem" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal">
							<spring:message code="application.save" />
						</button>
					</c:if>
					<button type="button" id="reminderCan" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form>
</div>

<script>
<c:if test="${buyerReadOnlyAdmin and !canEdit}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink, #listLink,.pagination';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

$('document').ready(function() {
var contractId = $("#idcontract").val()

var header = $("meta[name='_csrf_header']").attr("content");
var token = $("meta[name='_csrf']").attr("content");

var table = $('#tableList').DataTable({
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
		"url" : getContextPath() + "/buyer/productContractListItem",
		"data" : function(d) {
			d.id = contractId
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
		if(${!contractExpire && canEdit}) {
			var ret = '<a href="#contractItemModel" onClick="javascript:editContractItem(\'' + row.id + '\');"  title=<spring:message code="tooltip.edit" />  role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
			var startDate = row.contractStartDate;
			if(Date.parse(new Date()) < Date.parse(startDate)){
				ret += '&nbsp;<a href="#deleteContractItem" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" />  role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
				ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
			}
			return ret;
		} else {
			return '';
		}
	}
	},{
		"data" : "contractItemNumber"
	},{
		"data" : "itemName",
		"defaultContent" : ""
	},{
		"data" : "itemCode",
		"defaultContent" : ""
	},{
		"data" :"productCategory",
		"defaultContent" : "",
		"render" : function(data, type, row) {
			return row.productCode +' - '+ row.productName; 
		}, 
	},{
		"data" :"uom",
		"defaultContent" : ""
	},{
		"data" : "quantity",
		"defaultContent" : "",
		"mRender" : function(data, type, row) {
			return ReplaceNumberWithCommasFormat(row.quantity, row.decimal);
		}
	},{
		"data" : "balanceQuantity",
		"defaultContent" : "",
		"mRender" : function(data, type, row) {
			return ReplaceNumberWithCommasFormat(row.balanceQuantity, row.decimal);
		}
	},{
		"data" :"unitPrice",
		"defaultContent" : "",
		"mRender" : function(data, type, row) {
			return ReplaceNumberWithCommasFormat(row.unitPrice, row.decimal);
		}
	},{
		"data" :"tax",
		"defaultContent" : "",
		"mRender" : function(data, type, row) {
			return ReplaceNumberWithCommasFormat(row.tax, row.decimal);
		}
	},{
		"data":"itemType",
		"defaultContent" : ""
	}, {
		"data" :"brand",
		"defaultContent" : ""
	},{
		"data" :"storageLoc",
		"defaultContent" : ""
	},{
		"data":"businessUnit",
		"defaultContent" : ""
	},{
		"data":"costCenter",
		"defaultContent" : "",
		"render" : function(data, type, row) {
		if(row.description != null) {
		    return row.costCenter +' - '+ row.description; 
		} else {
			return row.costCenter;
		}
	}, 
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
					if(optionsType == 'itemTypeList'){
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


// Disable the Business Unit at Contract level if there are Contract Items in this contract
table.on( 'draw', function () {
    console.log('Table rendering done....', table.page.info().recordsTotal);
	if(table.page.info().recordsTotal > 0) {
		$('#idBusinessUnitDiv').addClass('disabled')
	} else {
		$('#idBusinessUnitDiv').removeClass('disabled')
	}
    
});

});

function ReplaceNumberWithCommasFormat(yourNumber, decimaltoFormate) {
	if(yourNumber!='' && yourNumber!=undefined){
		yourNumber = parseFloat(yourNumber).toFixed(decimaltoFormate);
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return n.join(".");
	}
	return yourNumber; 
}


$( "#chosenProductItem" ).change(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	  
	  var productItemId = $('#chosenProductItem').val();
	  $.ajax({
			url: getBuyerContextPath('itemDetailsOnProductBase'),
			data: {
				'productItemId' :productItemId
			},
			type: 'GET',
			dataType: 'json',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				$("#addcontractItemForm").find('#contractItemBrand').val(data.brand);
				$("#addcontractItemForm").find('#balanceQuantity').val(data.balanceQuantity);
				$("#addcontractItemForm").find('#chosenUom').val(data.uom != null ? data.uom : '').trigger("chosen:updated");
				$("#addcontractItemForm").find('#itemTax').val(data.tax);
				$("#addcontractItemForm").find('#chosenProductCategory').val(data.productCategory != null ? data.productCategory : '').trigger("chosen:updated");
				$("#addcontractItemForm").find('#chosenProductItemType').val(data.productItemType != null ? data.productItemType : '').trigger("chosen:updated");
			},

			error: function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete: function() {
				$('#loading').hide();
			}
		});
});

var fileSizeLimit = ${ownerSettings.fileSizeLimit};
var mimetypes = '<spring:message code="meeting.doc.file.mimetypes" />';
var fileType = "${fileType}";


$('#idFreeTextItem').on('click', function(e) {
	console.log('Checkbox : ', $(this).prop("checked"));
	if($(this).prop("checked")) {
		$('#idItemFreeText').show();
		$('#itemName').removeAttr('data-validation-optional');
		$('#idItemList').hide();
		$('#chosenProductItem').attr('data-validation-optional', 'true');
	} else {
		$('#idItemFreeText').hide();
		$('#itemName').attr('data-validation-optional', 'true');
		$('#idItemList').show();
		$('#chosenProductItem').removeAttr('data-validation-optional');
	}
});

</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/productContractUpdate.js?2"/>"></script>
