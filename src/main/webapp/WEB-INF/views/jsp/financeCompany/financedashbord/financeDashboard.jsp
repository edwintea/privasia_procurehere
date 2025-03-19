<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication var="lastLoginTime" property="principal.lastLoginTime" />
<spring:message var="buyerDashboardDesk" code="application.buyer.dashboard" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerDashboardDesk}] });
});
</script>
<style>
.border-all-side.document-table span {
	display: block;
	width: 100%;
}

.w20 {
	border: 1px solid #adadad;
	border-radius: 3px;
	height: 23px;
	width: 40px;
}
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/chardinjs.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap Events-Listing-heading">My Dashboard</h2>
			</div>
			<!-- page title block -->
			<div class="row clearfix">
				<div class="col-sm-12"><jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" /></div>
			</div>
			<div class="Section-title title_border white-bg">
				<!-- <div class="Section-title title_border gray-bg">
					<h2 class="trans-cap Events-Listing-heading">Events Listing</h2>
				</div> -->
				<sec:authorize access="hasRole('FINANCE') and hasRole('ROLE_FINANCE_PO')">
				<div class="Invited-Supplier-List dashboard-main">
					<jsp:include page="/WEB-INF/views/jsp/financeCompany/financedashbord/dashboard.jsp" />
				</div>
				</sec:authorize>
			</div>
		</div>
		<div class="container">
			<!-- My Tasks -->
			<sec:authorize access="hasRole('FINANCE') and hasRole('ROLE_FINANCE_PO')">
			<div class="Invited-Supplier-List dashboard-main tabulerDataList newPoData">
				<c:if test="${newPo > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Shared PO</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="newPoList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.name" /></th>
											<th search-type="text" class="align-left width_200 width_200_fix">PO Title</th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="" class="align-left width_200 width_200_fix">Finance Status</th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>

										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>

				</c:if>

			</div>
			
			
			
			<div class="Invited-Supplier-List dashboard-main tabulerDataList newPoData">
				<c:if test="${newRequestedPo > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Requested PO</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="newRequestedPoList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.name" /></th>

											<th search-type="text" class="align-left width_200 width_200_fix">PO Title</th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="application.description"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type=""  class="align-left width_200 width_200_fix">Finance Status</th>
											<th search-type=""  class="align-left width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>

										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>

				</c:if>

			</div>

			</sec:authorize>
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility submittedData">
				<c:if test="${submitted > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Submitted</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="submittedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text"><spring:message code="supplier.name" /></th>

											<th search-type="text">PO Title</th>
											<th search-type="text"><spring:message code="application.description"/></th>
											<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="">Finance Status</th>
											<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>

										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>

			</div>






			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility bankSettleData">
				<c:if test="${bankSettle > 0 }">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Bank Settle</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="bankSettleList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text"><spring:message code="supplier.name" /></th>

											<th search-type="text">PO Title</th>
											<th search-type="text"><spring:message code="application.description"/></th>
											<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="">Finance Status</th>
											<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>

			</div>




			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility bankRejectedData">

				<c:if test="${bankRejected > 0 }">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Bank Rejected</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="bankRejectedList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text"><spring:message code="supplier.name" /></th>

											<th search-type="text">PO Title</th>
											<th search-type="text"><spring:message code="application.description"/></th>
											<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="">Finance Status</th>
											<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</c:if>

			</div>

			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility financedData">
				<c:if test="${financed > 0 }">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">FINANCED</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="financedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<th search-type="" class="width_100_fix">
											<spring:message code="application.action" />
										</th>
										<th search-type="text"><spring:message code="supplier.name" /></th>

										<th search-type="text">PO Title</th>
										<th search-type="text"><spring:message code="application.description"/></th>
										<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
										<th search-type="" >Finance Status</th>
										<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
										<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>

			</div>
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility bankCollectedData">
				<c:if test="${bankCollected > 0 }">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Bank Collected</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="bankCollectedList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
												<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text"><spring:message code="supplier.name" /></th>

											<th search-type="text">PO Title</th>
											<th search-type="text"><spring:message code="application.description"/></th>
											<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="">Finance Status</th>
											<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>

										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			<div class="Invited-Supplier-List dashboard-main tabulerDataList  flagvisibility financeSettledData">
				<c:if test="${financeSettled > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4">Finance Settled</div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="financeSettledList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
												<th search-type="" class="width_100_fix">
												<spring:message code="application.action" />
											</th>
											<th search-type="text"><spring:message code="supplier.name" /></th>

											<th search-type="text">PO Title</th>
											<th search-type="text"><spring:message code="application.description"/></th>
											<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
											<th search-type="">Finance Status</th>
											<th search-type="" class="width_200 width_200_fix"><spring:message code="buyer.dashboard.po.createddate"/></th>
											<th search-type="" class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.po.grandtotal"/></th>

										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
		
	</div>
	
</div>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>

<script type="text/javascript">
<c:if test="${newPo > 0 }">
var newPoData;
$('document').ready(function() {
	newPoData = $('#newPoList').DataTable({
		"processing" : true,
		"serverSide" : false,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"ajax" : {
			"url" : getContextPath() + "/finance/newPoData",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#newPoList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th class="' + classStyle + '" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th class="' + classStyle + '" style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#newPoList thead').append(htmlEventPoSearch);
		$(newPoData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				newPoData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(newPoData.table().container()).on('change', 'thead select', function() {
			newPoData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>


<c:if test="${newRequestedPo > 0 }">
var newRequestedPo;
$('document').ready(function() {
	newRequestedPo = $('#newRequestedPoList').DataTable({
		"processing" : true,
		"serverSide" : false,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"language": {
			"emptyTable": "No data"
		},
		"ajax" : {
			"url" : getContextPath() + "/finance/newRequestedPoData",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '"   data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	
	
	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#newRequestedPoList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th  class="' + classStyle + '" ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th   class="' + classStyle + '" style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#newRequestedPoList thead').append(htmlEventPoSearch);
		$(newRequestedPo.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				newRequestedPo.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(newRequestedPo.table().container()).on('change', 'thead select', function() {
			newRequestedPo.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>



	

<c:if test="${submitted > 0}">
var submittedData;
$('document').ready(function() {
	submittedData = $('#submittedList').DataTable({
		"processing" : true,
		"deferRender" : true,
		"deferLoading": 0,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
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
			"url" : getContextPath() + "/finance/submittedPoData",
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
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#submittedList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#submittedList thead').append(htmlEventPoSearch);
		$(submittedData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				submittedData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(submittedData.table().container()).on('change', 'thead select', function() {
			submittedData.column($(this).data('index')).search(this.value).draw();
		});
});

</c:if>
	
 <c:if test="${bankRejected > 0}">
 
 var bankRejectedData;
 $('document').ready(function() {
	 bankRejectedData = $('#bankRejectedList').DataTable({
 		"processing" : true,
 		"deferRender" : true,
 		"deferLoading": 0,
 		"language": {
 			"emptyTable": "No data"
 		},
 		"preDrawCallback" : function(settings) {
 		//	$('div[id=idGlobalError]').hide();
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
 			"url" : getContextPath() + "/finance/bankRejectedData",
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
 		//"order" : [[ 6, "desc" ]],
 		"order" : [],
 		"columns" : [ {
 			"mData" : "id",
 			"searchable" : false,
 			"orderable" : false,
 			"mRender" : function(data, type, row) {
 				var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
 				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
 				return action;
 			}
 		}, {

 			// supplier.fullName is an company name here.........
 			"data" : "supplier.companyName",
 			"defaultContent" : ""
 		}, {
 			"data" : "po.name",
 			"defaultContent" : ""
 		}, {
 			"data" : "po.description",
 			"defaultContent" : ""
 		}, {
 			"data" : "po.poNumber",
 			"defaultContent" : ""
 		}, 
 		 {
 			"data" : "statusValue",
 			"orderable" : false,
 			"defaultContent" : ""
 		}, 
 		{
 			"data" : "po.createdDate",
 			"searchable" : false,
 			"type" : 'custom-date',
 			"defaultContent" : ""
 		}, {
 			"data" : "po.grandTotal",
 			"orderable" : true,
 			"className" : "align-right",
 			"defaultContent" : "",
 			"mRender" : function(data, type, row) {
 				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

 			}
 		} ]
 	});
 	

 	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
 		$('#bankRejectedList thead tr:nth-child(1) th').each(function(i) {
 			var title = $(this).text();
 		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
 			var classStyle =  $(this).attr("class");
 			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
 				classStyle = classStyle.replace('sorting','');
 			}
 			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
 				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
 			} else {
 				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
 			} */
 			if (!(title == "Actions") && $(this).attr('search-type') != '') {
 				if ($(this).attr('search-type') == 'select') {
 					var optionsType = $(this).attr('search-options');
 					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
 					if (optionsType == 'poStatusList') {
 						<c:forEach items="${poStatusList}" var="item">
 						htmlEventPoSearch += '<option value="${item}">${item}</option>';
 						</c:forEach>
 					}
 					htmlEventPoSearch += '</select></th>';
 				} else {
 					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
 				}
 			} else {
 				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
 			}
 			
 			
 			
 		});
 		htmlEventPoSearch += '</tr>';
 	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
 		$('#bankRejectedList thead').append(htmlEventPoSearch);
 		$(bankRejectedData.table().container()).on('keyup', 'thead input', function() {
 			if ($.trim(this.value).length > 2 || this.value.length == 0) {
 				bankRejectedData.column($(this).data('index')).search(this.value).draw();
 			}
 		});
 		$(bankRejectedData.table().container()).on('change', 'thead select', function() {
 			bankRejectedData.column($(this).data('index')).search(this.value).draw();
 		});
 });

</c:if>
 
<c:if test="${financed > 0 }">

var financedData;
$('document').ready(function() {
	financedData = $('#financedList').DataTable({
		"processing" : true,
		"deferRender" : true,
		"deferLoading": 0,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
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
			"url" : getContextPath() + "/finance/financedData",
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
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#financedList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#financedList thead').append(htmlEventPoSearch);
		$(financedData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				financedData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(financedData.table().container()).on('change', 'thead select', function() {
			financedData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>

<c:if test="${bankCollected > 0}">
var bankCollectedData;
$('document').ready(function() {
	bankCollectedData = $('#bankCollectedList').DataTable({
		"processing" : true,
		"deferRender" : true,
		"deferLoading": 0,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			/// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/finance/bankCollectedData",
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
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '" data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"   data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#bankCollectedList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#bankCollectedList thead').append(htmlEventPoSearch);
		$(bankCollectedData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				bankCollectedData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(bankCollectedData.table().container()).on('change', 'thead select', function() {
			bankCollectedData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>
	
<c:if test="${financeSettled > 0}">
	
var financeSettledData;
$('document').ready(function() {
	financeSettledData = $('#financeSettledList').DataTable({
		"processing" : true,
		"deferRender" : true,
		"deferLoading": 0,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
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
			"url" : getContextPath() + "/finance/financeSettledData",
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
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#financeSettledList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			/* if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} */
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#financeSettledList thead').append(htmlEventPoSearch);
		$(financeSettledData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				financeSettledData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(financeSettledData.table().container()).on('change', 'thead select', function() {
			financeSettledData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>
	
	
<c:if test="${bankSettle >0 }">
var bankSettleData;
$('document').ready(function() {
	bankSettleData = $('#bankSettleList').DataTable({
		"processing" : true,
		"deferRender" : true,
		"deferLoading": 0,
		"language": {
			"emptyTable": "No data"
		},
		"preDrawCallback" : function(settings) {
		//	$('div[id=idGlobalError]').hide();
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
			"url" : getContextPath() + "/finance/bankSettleData",
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
		//"order" : [[ 6, "desc" ]],
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender" : function(data, type, row) {
				var action = '<a href="financePOView/' + row.po.id + '" data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
				return action;
			}
		}, {

			// supplier.fullName is an company name here.........
			"data" : "supplier.companyName",
			"defaultContent" : ""
		}, {
			"data" : "po.name",
			"defaultContent" : ""
		}, {
			"data" : "po.description",
			"defaultContent" : ""
		}, {
			"data" : "po.poNumber",
			"defaultContent" : ""
		}, 
		 {
			"data" : "statusValue",
			"orderable" : false,
			"defaultContent" : ""
		}, 
		{
			"data" : "po.createdDate",
			"searchable" : false,
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "po.grandTotal",
			"orderable" : true,
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommas(row.po.grandTotal.toFixed(row.po.decimal));

			}
		} ]
	});
	

	 var htmlEventPoSearch = '<tr class="tableHeaderWithSearch">';
		$('#bankSettleList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
		//	console.log("Title : " + title + " Class : " + $(this).attr("class"));
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}/*
			 if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlEventPoSearch += '<th class="' + classStyle + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlEventPoSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			} 
			*/
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlEventPoSearch += '<th ><select data-index="'+i+'">';
					if (optionsType == 'poStatusList') {
						<c:forEach items="${poStatusList}" var="item">
						htmlEventPoSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlEventPoSearch += '</select></th>';
				} else {
					htmlEventPoSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlEventPoSearch +='<th style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
			
			
			
		});
		htmlEventPoSearch += '</tr>';
	//	console.log("htmlEventPoSearch : " + htmlEventPoSearch);
		$('#bankSettleList thead').append(htmlEventPoSearch);
		$(bankSettleData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				bankSettleData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(bankSettleData.table().container()).on('change', 'thead select', function() {
			bankSettleData.column($(this).data('index')).search(this.value).draw();
		});
});
</c:if>
 
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('input[name="days"]').mask('000');
		/* $("input[name='days']").keypress(function (e) {
		   if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		      //$("#errmsg").html("Digits Only").show().fadeOut("slow");
		      return false;
		  } else if($(this).val().length > 2){
		  	return false;
		  }
		 }); */
		 
	});
	
/* 	
	<c:if test="${empty lastLoginTime}">	
	 $('body').chardinJs('start')
	</c:if>
 */	 
	 function ReplaceNumberWithCommas(yourNumber) {
			// Seperates the components of the number
			var n = yourNumber.toString().split(".");
			// Comma-fies the first part
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			// Combines the two sections
			return n.join(".");
		}
	 
</script>
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.ph_table td, .ph_table th {
	text-align: -moz-center;
}

.nopad {
	padding: 10px 0 10px 0 !important;
}

.noti-icon-inputfix, .noti-icon-messagefix {
	width: auto;
}

#prDraftList th {
	text-align: left;
}
</style>
