<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
s<spring:message var="supplierDashboardDesk" code="application.supplier.dashboard" />
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="(hasRole('ROLE_SUPP_BUYER_EDIT') or hasRole('ADMIN')) and hasRole('SUPPLIER')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_SUPP_BUYER_VIEW_ONLY')" var="supplierViewOnly" />

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierDashboardDesk}] });
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
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<style>
.box-bottom.width50 {
	width: 50%;
}

.main-div {
	/* float: left; */
	/* float: left; */
	max-width: 900px;
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	width: 100%;
}

.box-bottom span {
	font-size: 16px;
	width: 100%;
	left: 0;
	position: absolute;
	top: 0;
	line-height: 16;
	height: 100%;
}

.box-bottom div {
	width: 100%;
	line-height: 3;
}

.border-right-shaded {
	border-width: 1px;
	border-left: 0;
	border-style: solid;
	-webkit-border-image: -webkit-gradient(linear, 0 100%, 0 0, from(#CCC),
		to(rgba(0, 0, 0, 0))) 1 100%;
	-webkit-border-image: -webkit-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-moz-border-image: -moz-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-o-border-image: -o-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0)) 1
		100%;
	border-image: linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 100%;
}

.box-main {
	border: 1px solid #eeeff0;
	border-radius: 0;
	margin: 0 1% 30px;
	height: 150px;
	overflow: hidden;
	position: relative;
	width: 200px;
	cursor: pointer;
	display: inline-block;
}

.box-top {
	top: 0;
	color: #fff;
	font-size: 14px;
	line-height: 18px;
	padding: 10px 3%;;
	position: absolute;
	text-align: left;
	text-transform: uppercase;
	width: 100%;
	border-radius: 0;
}

.box-bottom {
	float: left;
	padding: 23% 0 13% 0;
	position: relative;
	text-align: center;
	width: 100%;
	font-size: 34px;
}

.box-top span {
	float: left;
	line-height: 25px;
	margin-left: 3%;
	width: 79%;
}

.yellow {
	background: #F9C851;
	border: 1px solid #F9C851;
	color: #ffffff !important;
}

.yellow-con {
	/* font-size: 60px; */
	color: #F9C851;
	display: inline-block;
}

.orange {
	background: #FFA500;
	border: 1px solid #F9C851;
}

.limegreen {
	background: #32CD32;
	border: 1px solid #32CD32;
}

.green {
	background: #06CCB3;
	border: 1px solid #06CCB3;
}

.crimson {
	background: #ff5b5b;
	border: 1px solid #FF5B5B;
}

.gold {
	background: #FFD700;
	border: 1px solid #FFD700;
}

.navy {
	background: #000080;
	border: 1px solid #000080;
}

.navy-con {
	/* font-size: 60px; */
	color: #0000CD;
	display: inline-block;
}

.gold-con {
	/* font-size: 60px; */
	color: #FFD700;
	display: inline-block;
}

.red {
	background: #FF5B5B;
	border: 1px solid #FF5B5B;
}

.blue {
	background: #627fa7;
	border: 1px solid #627fa7;
}

.sky-blue {
	background: #35b4e9;
	border: 1px soild #35b4e9;
}

.sky-blue-con {
	/* font-size: 60px; */
	color: #35b4e9;
	display: inline-block;
}

.coffi {
	background: #cebf98;
	border: 1px solid #cebf98;
}

.light-blue {
	background: #00d1c6;
	border: 1px solid #00d1c6;
}

.light-blue-con {
	/* font-size: 60px; */
	color: #00d1c6;
	display: inline-block;
}

.light-gray {
	background: #727c88;
	border: 1px solid #727c88;
}

.light-gray-con {
	/* font-size: 60px; */
	color: #727c88;
	display: inline-block;
}

.perpal {
	background: #8809ff;
	border: 1px solid #8809ff;
}

.perpal-con {
	/* font-size: 60px; */
	color: #8809ff;
	display: inline-block;
}

.db-li {
	line-height: 19px !important;
}

.limegreen-con {
	/* font-size: 60px; */
	color: #32CD32;
}

.crimson-con {
	/* font-size: 60px; */
	color: #ff5b5b;
}

.red-con {
	/* font-size: 60px; */
	color: #e93535;
}

.blck-con {
	font-size: 25px;
	color: #333333;
}

.orange-con {
	/* font-size: 60px; */
	color: #FFA500;
	display: inline-block;
}

.green-con {
	/* font-size: 60px; */
	color: #06ccb3;
	display: inline-block;
}

.gray-con {
	/* font-size: 60px; */
	color: #333333;
	display: inline-block;
}

.bottom-text {
	bottom: 5px;
	color: #333;
	float: left;
	font-size: 22px;
	width: 100%;
	margin-top: 30px;
}

.box-top img {
	width: 30px;
	vertical-align: top;
	float: right;
}
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
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/chardinjs.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">

<jsp:useBean id="now" class="java.util.Date" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/supplier/supplierDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="supplier.associated.buyerList" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap tender-request-heading">
					<spring:message code="supplier.associated.buyerList" />
				</h2>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

			<div class="Invited-Supplier-List white-bg dashboard-main">
				<div class="main-div marg-top-20">
					<div class="box-main scheduledContainer" >
						<div class="box-top sky-blue">
							<span><spring:message code="supplier.associated.buyers.label" /> </span> 
						</div>
				
						<div class="box-bottom width50" data-target="associatedBuyerData">
							<div class="sky-blue-con border-right-shaded pendingContainer">${associatedBuyerCount}</div>
							<span><spring:message code="supplier.associated.associatedBuyers" /></span>
						</div>
						<div class="box-bottom width50" data-target="associatedBuyerData1">
							<div class="sky-blue-con pendingContainer">${availableBuyerCount}</div>
							<span><spring:message code="supplier.associated.avaliableBuyers" /></span>
						</div> 
					</div>
					<div class="box-main activeContainer" >
						<div class="box-top orange">
							<span><spring:message code="supplier.associated.myRequests" /> </span> 
						</div>
						<div class="box-bottom width50"  data-target="myRequestedBuyerData">
							<div class="orange-con border-right-shaded pendingContainer">${pendingBuyerCount}</div>
							<span><spring:message code="supplier.associated.pendingBuyers" /></span>
						</div>
						<div class="box-bottom width50" data-target="myRequestedBuyerData1">
							<div class="orange-con pendingContainer">${rejectedBuyerCount}</div>
							<span><spring:message code="supplier.associated.rejectedBuyers" /></span>
						</div>
					</div>
				</div>
			</div>
			<div class="example-box-wrapper wigad-new">
				<div class="Invited-Supplier-List import-supplier white-bg">
					<div class="meeting2-heading">
						<h3>
							<spring:message code="supplier.associated.searchBuyers" />
						</h3>
					</div>
			 <div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="searchBuyerForm" class="form-horizontal" commandName="requestedAssociatedBuyerPojo">
					<div class="row marg-bottom-10">
						<div class="col-md-3 col-sm-4">
							<label class="marg-top-10"> <spring:message code="supplier.associated.companyName" />
							</label>
						</div>
						<div class="col-md-4 col-sm-5">
							<spring:message code="supplier.associated.companyNameplaceholder" var="compname" />
							<form:input path="searchCompanyName" class="form-control" type="text" id="searchCompanyName" placeholder="${compname}" />
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-3 col-sm-4">
							<label class="marg-top-10"> <spring:message code="supplier.associated.country" />
							</label>
						</div>
						<div class="col-md-4 col-sm-5">
							<spring:message code="application.selectcountry" var="regiNo" />
							<form:select path="buyerCountry" class="chosen-select" id="searchCountryName">
								<form:option value="">
									<spring:message code="application.selectcountry" />
								</form:option>
								<form:options items="${countryList}" itemValue="countryName" itemLabel="countryName"></form:options>
							</form:select>
						</div>
					</div>
					<!--  Start test category here -->
					<div class="row marg-bottom-10">
						<div class="col-md-3">&nbsp;</div>
						<div class="col-md-6">
							<input type="button" value='<spring:message code="application.search" />' class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20" id="searchBuyer">
							<input type="button" style="margin-left:10px;" value='<spring:message code="application.reset" />' class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20" id="resetSearchBuyer">
						</div>
					</div>
				</form:form>
			</div> 
			</div>
			</div>
			<div class="lower-bar-search-contant-main white-bg pad-t35" style="overflow: auto;">
				<div class="Invited-Supplier-List dashboard-main tabulerDataList searchBuyerData">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="main_table_wrapper ph_table_border ">
								<table id="searchBuyerList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="supplier.associated.actions"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.companyName"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.country" /></th>
											<th search-type="select" search-options="requestedBuyerStatusList" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.status"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.associationDate"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.requestedDate"/></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- ********* -->
			<!--  ASSOCIATED -->
			<!-- ********* -->
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility associatedBuyerData">
				<c:if test="${associatedBuyerCount > 0}">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="col-md-12">
									<div class="nopad table-heading col-md-4"><spring:message code="supplier.associated.buyers"/></div>
								</div>
								<div class="main_table_wrapper ph_table_border ">
									<table id="associatedBuyerList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="supplier.associated.actions"/></th>
												<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.companyName"/></th>
												<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.country" /></th>
												<th search-type="select" search-options="requestedBuyerStatusList" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.status"/></th>
												<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.associationDate"/></th>
												<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.requestedDate"/></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
				</c:if>
				</div>
			<!-- ********* -->
			<!--  AVAILABLE -->
			<!-- ********* -->
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility associatedBuyerData1">
				<c:if test="${availableBuyerCount > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4"><spring:message code="supplier.available.buyers"/></div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="availableBuyerList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="supplier.associated.actions"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.companyName"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.country" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			<!-- ********* -->
			<!--  PENDING -->
			<!-- ********* -->
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRequestedBuyerData">
				<c:if test="${pendingBuyerCount > 0}">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="col-md-12">
									<div class="nopad table-heading col-md-4"><spring:message code="supplier.associated.request.pending"/></div>
								</div>
								<div class="main_table_wrapper ph_table_border ">
									<table id="pendingBuyerList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th class="align-left width_100_fix" search-type=""><spring:message code="supplier.associated.actions"/></th>
												<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.companyName"/></th>
												<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.country" /></th>
												<th search-type="select" search-options="requestedBuyerStatusList" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.status"/></th>
												<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.requestedDate"/></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
				</c:if>
			</div>
			<!-- ********* -->
			<!--  REJECTED -->
			<!-- ********* -->
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRequestedBuyerData1">
				<c:if test="${rejectedBuyerCount > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-4"><spring:message code="supplier.associated.request.rejected"/></div>
							</div>
							<div class="main_table_wrapper ph_table_border ">
								<table id="rejectedBuyerList" class="data display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="align-left width_100_fix" search-type=""><spring:message code="supplier.associated.actions"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.companyName"/></th>
											<th search-type="text" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.country" /></th>
											<th search-type="select" search-options="requestedBuyerStatusList" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.status"/></th>
											<th search-type="" class="align-left width_200 width_200_fix"><spring:message code="supplier.associated.requestedDate"/></th>
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
<style>
li.filterDashbord {
	margin-left: 10px;
}

li.filterDashbord label {
	line-height: 1;
}
</style>
<script type="text/javascript">
var searchBuyerData;
$('document').ready(function() {
	var search =false;
	$("#searchBuyer").click(function(e) {
		var companyName=$.trim($('#searchCompanyName').val());
		var countryName=$.trim($('#searchCountryName').val());
		if((companyName!='' && companyName.length > 2) || (countryName!='')){
			search=true;
		}
	 
	});
	searchBuyerData = $('#searchBuyerList').DataTable({
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
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/searchBuyer",
			"data" : function(d) {
				var companyName='';
				var countryName='';
				if(search==true){
					companyName=$.trim($('#searchCompanyName').val());;
					countryName=$.trim($('#searchCountryName').val());
				}
				d.searchCompanyName = companyName;
				d.searchCountryName = countryName;
			
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				if(row.associatedDate==null && (row.favStatus=='ACTIVE' ||  row.favStatus=='INACTIVE' ||  row.favStatus=='BLACKLISTED' ||  row.favStatus=='SUSPENDED') ){
					return null;
				}else{
					if((row.favStatus==null && row.requestedDate==null)){
						if(canEdit()=='true'){
							return '<a href="${pageContext.request.contextPath}/supplier/requestToAssociateBuyer/'+row.buyerId+'" data-placement="top" title="View"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/add.png"> </a>'
						}else{
							return null;
						}
					}else{
						return '<a href="${pageContext.request.contextPath}/supplier/viewRequestedAssociateBuyer/'+row.buyerId+'" data-placement="top" title="View"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
					}
				}
				
			},
		}, {
			"data" : "buyerCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "countryName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "favStatus",
			"orderable" : false,
			"className" : "align-left",
			"defaultContent" : "",
			"render" : function(data, type, row) {
				if(row.favStatus=='ACTIVE' ||  row.favStatus=='INACTIVE' ||  row.favStatus=='BLACKLISTED' ||  row.favStatus=='SUSPENDED'){
					return 'ASSOCIATED';
				}else{
					return row.favStatus;
				}
			}
			
		},{
			"data" : "associatedDate",
			"orderable" : false,
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : "",
			"render" : function(data, type, row) {
				if(row.favStatus=='ACTIVE'){
					if(row.associatedDate!=null){
						return row.associatedDate;
					}else{
						return row.requestedDate;
					}
				}else{
					return row.associatedDate;
				}
			},
		},{
			"data" : "requestedDate",
			"orderable" : false,
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : "",
			"render" : function(data, type, row) {
				if(row.favStatus=='ACTIVE'){
					if(row.associatedDate!=null){
						return row.requestedDate;
					}else{
						return null;
					}
				}else{
					return row.requestedDate;
				}
			}
		} ],

	"initComplete": function(settings, json) {
	 var htmlBuyerPendingSearch = '<tr class="tableHeaderWithSearch">';
	 $('#searchBuyerList thead tr:nth-child(1) th').each(function(i) {
		 var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				} else {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlBuyerPendingSearch += '</tr>';
		$('#searchBuyerList thead').append(htmlBuyerPendingSearch);
		$(searchBuyerData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				searchBuyerData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(searchBuyerData.table().container()).on('change', 'thead select', function() {
			searchBuyerData.column($(this).data('index')).search(this.value).draw();
		});
	}
 });

	
});

<c:if test="${associatedBuyerCount > 0}">
var associatedBuyerData;
$('document').ready(function() {
	associatedBuyerData = $('#associatedBuyerList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/associatedBuyerList",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				if(row.associatedDate!=null){
					return '<a href="${pageContext.request.contextPath}/supplier/viewRequestedAssociateBuyer/'+row.buyerId+'" data-placement="top" title="View"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
				}else{
					return null;
				}
			},
		}, {
			"data" : "buyerCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "countryName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "favStatus",
			"orderable" : false,
			"className" : "align-left",
			"defaultContent" : "",
			"render" : function(data, type, row) {
				return 'ASSOCIATED';
			}
		},{
			"data" : "associatedDate",
			"orderable" : false,
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : "",
			"render" : function(data, type, row) {
				if(row.associatedDate!=null){
					return row.associatedDate;
				}else{
					return row.requestedDate;
				}
			},
		},{
			"data" : "requestedDate",
			"orderable" : false,
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : "",
			"render" : function(data, type, row) {
				if(row.associatedDate!=null){
					return row.requestedDate;
				}else{
					return null;
				}
			}
		} ],

	"initComplete": function(settings, json) {
	 var htmlBuyerPendingSearch = '<tr class="tableHeaderWithSearch">';
	 $('#associatedBuyerList thead tr:nth-child(1) th').each(function(i) {
		 var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				} else {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlBuyerPendingSearch += '</tr>';
		$('#associatedBuyerList thead').append(htmlBuyerPendingSearch);
		$(associatedBuyerData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				associatedBuyerData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(associatedBuyerData.table().container()).on('change', 'thead select', function() {
			associatedBuyerData.column($(this).data('index')).search(this.value).draw();
		});
	}
 });
	
});
</c:if>
<c:if test="${availableBuyerCount > 0}">
var associatedBuyerData1;
$('document').ready(function() {
	associatedBuyerData1 = $('#availableBuyerList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/availableBuyerList",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				if (canEdit() === "true") {
					return '<a href="${pageContext.request.contextPath}/supplier/requestToAssociateBuyer/'+row.buyerId+'" data-placement="top" title="View"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/add.png"> </a>'
				}else{
					return null
				}
			},
		}, {
			"data" : "buyerCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "countryName",
			"className" : "align-left",
			"defaultContent" : ""
		} ],

	"initComplete": function(settings, json) {
	 var htmlBuyerPendingSearch = '<tr class="tableHeaderWithSearch">';
	 $('#availableBuyerList thead tr:nth-child(1) th').each(function(i) {
		 var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				} else {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlBuyerPendingSearch += '</tr>';
		$('#availableBuyerList thead').append(htmlBuyerPendingSearch);
		$(associatedBuyerData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				associatedBuyerData1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(associatedBuyerData1.table().container()).on('change', 'thead select', function() {
			associatedBuyerData1.column($(this).data('index')).search(this.value).draw();
		});
	
	}
	});

	
});
</c:if>

<c:if test="${pendingBuyerCount > 0}">
var myRequestedBuyerData;
$('document').ready(function() {
	myRequestedBuyerData = $('#pendingBuyerList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : true,
		"deferLoading": true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/pendingRequestBuyerList",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				return '<a href="${pageContext.request.contextPath}/supplier/viewRequestedAssociateBuyer/'+row.buyerId+'" data-placement="top" title="View"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "buyerCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "countryName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "favStatus",
			"orderable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "requestedDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		} ],

	"initComplete": function(settings, json) {
	 var htmlBuyerPendingSearch = '<tr class="tableHeaderWithSearch">';
	 $('#pendingBuyerList thead tr:nth-child(1) th').each(function(i) {
		 var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				} else {
					htmlBuyerPendingSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlBuyerPendingSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlBuyerPendingSearch += '</tr>';
		$('#pendingBuyerList thead').append(htmlBuyerPendingSearch);
		$(myRequestedBuyerData.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myRequestedBuyerData.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myRequestedBuyerData.table().container()).on('change', 'thead select', function() {
			myRequestedBuyerData.column($(this).data('index')).search(this.value).draw();
		});
	
	}
	});

	
});
</c:if>

<c:if test="${rejectedBuyerCount > 0}">
var myRequestedBuyerData1;
$('document').ready(function() {
	myRequestedBuyerData1 = $('#rejectedBuyerList').DataTable({
			"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"serverSide" : false,
		"deferLoading": 0,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/supplier/rejectedRequestBuyerList",
			"data" : function(d) {
			}
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
			"sClass" : "for-left pad-left-10",
			"render" : function(data, type, row) {
				return '<a href="${pageContext.request.contextPath}/supplier/viewRequestedAssociateBuyer/'+row.buyerId+'" data-placement="top" title="view"> <img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png"> </a>'
			},
		}, {
			"data" : "buyerCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "countryName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "favStatus",
			"orderable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "requestedDate",
			"className" : "align-left",
			"type": 'custom-date',
			"defaultContent" : ""
		} ],
	
	"initComplete": function(settings, json) {
	 var htmlBuyerRejectedSearch = '<tr class="tableHeaderWithSearch">';
	 
	 $('#rejectedBuyerList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			var classStyle =  $(this).attr("class");
			if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
				classStyle = classStyle.replace('sorting','');
			}
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				
				if ($(this).attr('search-type') == 'select') {
					htmlBuyerRejectedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
				} else {
					htmlBuyerRejectedSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlBuyerRejectedSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + classStyle + '"></div></th>';
			}
		});
		htmlBuyerRejectedSearch += '</tr>';
		$('#rejectedBuyerList thead').append(htmlBuyerRejectedSearch);
		$(myRequestedBuyerData1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myRequestedBuyerData1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myRequestedBuyerData1.table().container()).on('change', 'thead select', function() {
			myRequestedBuyerData1.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
});
</c:if>
</script>
<script>
$(window).bind('load', function() {
	$('.box-bottom').click(function(e) {
		e.preventDefault();
		var targetElm = $(this).attr('data-target');
		$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
		var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.' + targetElm);

		// Max 3 tables in one group. Try each
		if (typeof window[targetElm] !== 'undefined') {
			window[targetElm].ajax.reload();
		}
		
		showTable.removeClass('flagvisibility');
		//JS error in pending events error fixed
		if (showTable.length) {

			$('html,body').animate({
				scrollTop : showTable.offset().top
			}, 'slow');
		}
	});
	$("#searchBuyer").click(function(e) {
		e.preventDefault();
		// orderedSearch();
		var companyName=$.trim($('#searchCompanyName').val());
		var countryName=$.trim($('#searchCountryName').val());
		if((companyName!='' && companyName.length > 2) || (countryName!='')){
			$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
			var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.searchBuyerData');
			window['searchBuyerData'].ajax.reload();
			
				showTable.removeClass('flagvisibility');
				//JS error in pending events error fixed
				if (showTable.length) {
	
					$('html,body').animate({
						scrollTop : showTable.offset().top
					}, 'slow');
				}
		}
	});

});
$("#resetSearchBuyer").click(function(event) {
	$('#searchCompanyName').val('');
	$('#searchCountryName').val('');
	$('#searchCountryName').trigger("chosen:updated");
	searchBuyerData.ajax.reload();
});

</script>


<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>