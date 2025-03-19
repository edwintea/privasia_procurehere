<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('CONTRACT_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="productListDesk" code="application.buyer.product.list" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />
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

.padd-right-15 {
	padding-right: 15px
}

.btn-bg {
	border: 0;
	padding: 0 !important;
	background: transparent !important;
	outline: 0 !important;
}

#defaultTableList td {
	padding: 15px 5px;
}

#defaultTableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#draftContractList td {
	padding: 15px 5px;
}

#draftContractList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#pendingContractList td {
	padding: 15px 5px;
}

#pendingContractList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#newContractTableList td {
	padding: 15px 5px;
}

#newContractTableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#lessThanOneMonthTableList td {
	padding: 15px 5px;
}

#lessThanOneMonthTableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#activeContractTableList td {
	padding: 15px 5px;
}

#activeContractTableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#OneToThreeMonthTableList td {
	padding: 15px 5px;
}

#OneToThreeMonthTableList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#threeToSixMonthExpiredList td {
	padding: 15px 5px;
}

#threeToSixMonthExpiredList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#greaterThanSixMonthExpiredList td {
	padding: 15px 5px;
}

#greaterThanSixMonthExpiredList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#expiredContractList td {
	padding: 15px 5px;
}

#expiredContractList td:nth-child(1) {
	padding: 15px 5px;
	text-align: center;
}

#terminatedContractList td {
	padding: 15px 5px;
}

#terminatedContractList td:nth-child(1) {
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

.box-bottom.width50 {
	width: 50%;
}

.main-div {
	max-width: 1280px;
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
	line-height: 2;
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
	width: 250px;
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
	color: #0000CD;
	display: inline-block;
}

.gold-con {
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

.sky-blue-dash {
	background: #35b4e9;
	border: 1px soild #35b4e9;
}

.sky-blue-con {
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
	color: #00d1c6;
	display: inline-block;
}

.light-gray {
	background: #727c88;
	border: 1px solid #727c88;
}

.light-gray-con {
	color: #727c88;
	display: inline-block;
}

.perpal {
	background: #8809ff;
	border: 1px solid #8809ff;
}

.perpal-con {
	color: #8809ff;
	display: inline-block;
}

.db-li {
	line-height: 19px !important;
}

.limegreen-con {
	color: #32CD32;
}

.crimson-con {
	color: #ff5b5b;
}

.red-con {
	color: #e93535;
}

.blck-con {
	font-size: 25px;
	color: #333333;
}

.orange-con {
	color: #FFA500;
	display: inline-block;
}

.green-con {
	color: #06ccb3;
	display: inline-block;
}

.gray-con {
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
</style>

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productListDesk}] });
});

	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content" view-name="product-contract" class="white-bg">
	<div class="container col-md-12">
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="product.contracts.list.dashboard" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="product.contracts.list.dashboard" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="Invited-Supplier-List white-bg dashboard-main">
				<div class="main-div marg-top-20">
					<div class="box-main draftContainer">
						<div class="box-top gold">
							<span><spring:message code="product.contract.draft" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/draft-event.png" alt="draft-contract">
						</div>
						<div class="box-bottom" data-target="draftContract">
							<div class="gold-icon">${draftContractCount}</div>
							<span><spring:message code="Product.contract.list" /></span>
						</div>
					</div>
					<div class="box-main">
						<div class="box-top perpal">
							<span><spring:message code="product.contract.pending" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="pending-contract">
						</div>
						<div class="box-bottom" data-target="pendingContract">
							<div class="perpal-con">${pendingContractCount}</div>
							<span><spring:message code="Product.contract.list" /></span>
						</div>
					</div>
					<div class="box-main">
						<div class="box-top navy">
							<span><spring:message code="product.contract.upcoming" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/suspend-event.png" alt="upcoming-contract">
						</div>
						<div class="box-bottom" data-target="myUpcomingNewContract">
							<div class="navy-con newContainer">${newContractCount}</div>
							<span><spring:message code="contract.upcoming.approved" /></span>
						</div>
					</div>
					
					<div class="box-main activeContainer">
						<div class="box-top limegreen">
							<span><spring:message code="buyer.dashboard.active" /> </span>
								<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="urgent-contract">
						</div>
						<div class="box-bottom width50" data-target="myContractActive">
							<div class="limegreen-con border-right-shaded urgentlyExpiring">${activeContractCount}</div>
							<span><spring:message code="buyer.dashboard.active" /></span>
						</div>
						<div class="box-bottom width50" data-target="myContractActive1">
							<div class="crimson-con urgentlyExpiring">${suspendedContractCount}</div>
							<span><spring:message code="buyer.dashboard.suspended" /></span>
						</div>
					</div>
					
					<div class="box-main subsequentlyExpiringContainer">
						<div class="box-top orange">
							<span style="font-size: 12px;"><spring:message code="product.contract.subsequently.expiring" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="subsequently-contract">
						</div>
						<div class="box-bottom width50" data-target="myContSubsequentlyExpire">
							<div class="orange-con border-right-shaded subsequentlyExpiring">${threeToSixMonthExpiredCount}</div>
							<span><spring:message code="contract.subsequently.less.six" /></span>
						</div>
						<div class="box-bottom width50" data-target="myContSubsequentlyExpire1">
							<div class="orange-con subsequentlyExpiring">${greaterThanSixMonthExpiredCount}</div>
							<span><spring:message code="contract.subsequently.greater.six" /></span>
						</div>
					</div>

					<div class="box-main urgentlyExpiringContainer">
						<div class="box-top crimson">
							<span><spring:message code="product.contract.urgently.expiring" /> </span>
								<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="urgent-contract">
						</div>
						<div class="box-bottom width50" data-target="myContractUrgentlyExpiring">
							<div class="crimson-con  border-right-shaded urgentlyExpiring">${oneMonthExpiredCount}</div>
							<span><spring:message code="contract.urgently.less.month" /></span>
						</div>
						<div class="box-bottom width50" data-target="myContractUrgentlyExpiring1">
							<div class="crimson-con urgentlyExpiring">${oneToThreeMonthExpiredCount}</div>
							<span><spring:message code="contract.urgently.less.three" /></span>
						</div>
					</div>

					<div class="box-main expiredContainer">
						<div class="box-top sky-blue-dash">
							<span><spring:message code="product.contract.expired" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/cancel-event.png" alt="expired-contract">
						</div>
						<div class="box-bottom" data-target="myContractExpired">
							<div class="sky-blue-con expiredContractContainer">${expiredContractCount}</div>
							<span><spring:message code="product.contract.expired" /></span>
						</div>
					</div>
					<div class="box-main">
						<div class="box-top light-gray">
							<span><spring:message code="product.contract.terminated" /> </span>
							<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/per-order.png" alt="terminated-contract">
						</div>
						<div class="box-bottom" data-target="terminatedContract">
							<div class="light-gray-con">${terminatedContractCount}</div>
							<span><spring:message code="product.contract.terminated" /></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<!-- Date range filter -->
		<div class="col-md-12 marg-top-10 mb-10">
			<label><spring:message code="application.filter.by.contract.created.date" /></label>
			<div class="row">
				<div class="col-md-5 col-sm-8 col-lg-5" style="padding-right:5px;">
					<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
				</div>
				<div class="col-md-2 col-sm-4 resetBtn-padding" style="padding-left:0px;">
					<spring:message code="application.reset" var="resetLabel" />
					<button type="button" id="resetDate" class="btn btn-sm btn-black"  style="height: 37px; width:37px;" data-toggle="tooltip" data-placement="top" data-original-title="${resetLabel}">
						<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
					</button> 
				</div>
			</div>
		</div>		
		<!-- Draft Contract Table -->
		<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility draftContract">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportForDraft" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.draft.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button id="exportProductReport" type="submit" class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="draftContractList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
		
	<!-- Pending Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility pendingContract">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportForPending" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormForExpired" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.awaiting.approval" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="pendingContractList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="" search-options="statusList" class="align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
		
	<!-- Default Table list Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList table white-bg">
		<div class="Invited-Supplier-List-table add-supplier ">
		<c:url value="/buyer/exportContractCsvReport" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVForm" ModelAttribute="productContractPojo">
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border">
						<div class="ph_tabel_wrapper marg-top-10">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									Contract List
								</div>
								<div class="col-md-6 marg-top-10">
									<button id="exportProductReport1" class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
								</button>
							</div>
						</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="defaultTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
										<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
											<th class="W-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="select" search-options="statusList" class="align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>

	<!-- new Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myUpcomingNewContract">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportForUpComingData" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormForUpComingData" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.new.upcoming.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="newContractTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class="align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
	<!-- Active Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContractActive">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportActive" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.active.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="activeContractTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
										<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>	
	
		<!-- Suspended Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContractActive1">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportSuspended" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormFor3Month" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.suspended.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
							</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="suspendedContractTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>	
			
	<!-- less than one Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContractUrgentlyExpiring">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportFor1Month" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormFor1Month" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.list.less.one.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="lessThanOneMonthTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
										<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
		<!-- one to three Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContractUrgentlyExpiring1">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportFor3Month" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormFor3Month" >
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.list.less.three" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
							</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="OneToThreeMonthTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
		<!-- Three to six Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContSubsequentlyExpire">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReport6Month" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormFor6Month" ModelAttribute="productContractPojo">
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.list.less.six" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="threeToSixMonthExpiredList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
	<!-- Greater then six Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContSubsequentlyExpire1">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportFor10Month" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormFor10Month">
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.list.greater.six" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="greaterThanSixMonthExpiredList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
											<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
	<!-- Expired Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility myContractExpired">
		<div class="Invited-Supplier-List-table add-supplier ">
			<c:url value="/buyer/exportContractCsvReportForExpired" var="downloadCSVReport" />
			<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormForExpired" ModelAttribute="productContractPojo">
				<input type="hidden" name="dateRange" value="">
				<div class=" ph_tabel_wrapper pad_all_15">
					<div class="main_table_wrapper ph_table_border ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="nopad table-heading col-md-6">
									<spring:message code="contract.expired.list" />
								</div>
								<div class="col-md-6 marg-top-10">
									<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
									</button>
								</div>
							</div>
							<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
								<table id="expiredContractList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
									<thead>
										<tr class="tableHeaderWithSearch">
										<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
											<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
											<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
											<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
											<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
											<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
											<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
											<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
											<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	
		<!-- Terminated Contract Table -->
	<div class="Invited-Supplier-List dashboard-main tabulerDataList white-bg flagvisibility terminatedContract">
		<c:if test="${terminatedContractCount > 0}">
			<div class="Invited-Supplier-List-table add-supplier ">
				<c:url value="/buyer/exportContractCsvReportForTerminated" var="downloadCSVReport" />
				<form:form action="${downloadCSVReport}" method="post" id="exportCSVFormForExpired">
					<input type="hidden" name="dateRange" value="">
					<div class=" ph_tabel_wrapper pad_all_15">
						<div class="main_table_wrapper ph_table_border ">
							<div class="ph_tabel_wrapper">
								<div class="col-md-12">
									<div class="nopad table-heading col-md-6">
										<spring:message code="contract.terminated.list" />
									</div>
									<div class="col-md-6 marg-top-10">
										<button class="btn btn-sm btn-success hvr-pop pull-right" data-toggle="tooltip" data-placement="left" data-original-title='<spring:message code="productReport.export.button"/>'>
											<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="productReport.export.button" /></span>
										</button>
									</div>
								</div>
								<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
									<table id="terminatedContractList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
										<thead>
											<tr class="tableHeaderWithSearch">
												<th class="w-100 align-left" search-type=""><spring:message code="application.action" /></th>
												<th search-type="text" class="width_150 align-left" ><spring:message code="product.Contract.Id" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.name" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="product.contract.event.id" /></th>
												<th search-type="text" class="width_200 align-left"><spring:message code="product.Contract.code" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="Productz.favoriteSupplier" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.businessUnit" /></th>
												<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.start" /></th>
												<th search-type="" class="width_200 align-left"><spring:message code="product.Contract.end" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.groupCode" /></th>
												<th search-type="text" class="width_150 align-center"><spring:message code="label.currency" /></th>
												<th search-type="text" class="width_200 align-right"><spring:message code="product.Contract.totalContractValue" /></th>
												<th search-type="text" class="width_150 align-left"><spring:message code="product.Contract.creatorEmail" /></th>
												<th search-type="" class="width_150 align-left"><spring:message code="application.createddate" /></th>
												<th search-type="text" class="width_200 align-left"><spring:message code="application.modifiedby" /></th>
												<th search-type="" class="width_200 align-left"><spring:message code="application.modifieddate" /></th>
												<th search-type="" search-options="statusList" class=" align-left"><spring:message code="application.status" /></th>
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</c:if>
	</div>
	
	
	<div class="row" style="height: 10px;"></div>
	<div class="white-bg" style="padding-left:15px;">
	<sec:authorize access="hasRole('CONTRACT_EDIT') or hasRole('ADMIN')" var="contractCanEdit" />
			<c:url value="/buyer/createBlankContract" var="newContract"></c:url>
			<form action="${ newContract }" id="createFormBlank">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="businessUnitId" value="" />
				
				<c:if test="${idSettingsBasedOnBusinessUnit}">
					<button type="button" id="createBlankContractBu" class="btn btn-plus btn-info top-marginAdminList marg-bottom-10 ${canEdit and !buyerReadOnlyAdmin ? '' : 'disabled' }">
						<spring:message code="contract.create" />
					</button>
				</c:if>
				<c:if test="${!idSettingsBasedOnBusinessUnit}">
					<button type="submit" id="createBlankContract" class="btn btn-plus btn-info top-marginAdminList marg-bottom-10 ${canEdit and !buyerReadOnlyAdmin ? '' : 'disabled' }">
						<spring:message code="contract.create" />
					</button>
				</c:if>
			</form>
		</div>

	</div>
	
<c:if test="${idSettingsBasedOnBusinessUnit}">
	<div class="modal fade" id="selectBusinessUnit-Modal" style="width: 550px;margin:auto;" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<c:url value="/buyer/createBlankContract" var="newContract" />
			<form action="${ newContract }" id="createFormBlankWithBu" >
				<div class="modal-content" style="width: 100%; float: left;">
					<div class="modal-header">
						<h4>
							Choose Business Unit
						</h4>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>
					<div class="modal-body">
						<h5 class="marg-bottom-20"><spring:message code="select.business.unit.for.contract" /></h5>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						 <select name="businessUnitId" id="businessUnitId"  data-validation="required" class="chosen-select">
						 	<option value=""><spring:message code="pr.select.business.unit" /></option>
							<c:forEach items="${businessUnit}" var="businessUnit">
								<c:if test="${empty businessUnit.id}">
									<option value="${businessUnit.id}" disabled>${businessUnit.unitName}</option>
								</c:if>
								<c:if test="${!empty businessUnit.id}">
									<option value="${businessUnit.id}">${!empty businessUnit.unitCode ? businessUnit.unitCode : ""}${!empty businessUnit.unitCode ? " - " : ""}${businessUnit.unitName}</option>
								</c:if>
							</c:forEach>
							
						</select>
					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 marg-top-10 ">
						<a href="#" type="button" id="btnNext" class="btn pull-left btn-info ph_btn_small hvr-pop hvr-rectangle-out" >
							<spring:message code="application.confirm" />
						</a>
						<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
							<spring:message code="application.cancel" />
						</button>
						
					</div>
				</div>
			</form>
		</div>
	</div>
</c:if>
	
</div>
<!-- NEw add -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>

<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});

	var table = '';
	var bucketTable = '';

	$('document').ready(function() {

		$.validate({
			lang: 'en',
		});

		$(document).on("keyup", "#businessUnitId_chosen .chosen-search input", keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37:
					return false;
					break;
				case 38:
					return false;
					break;
				case 39:
					return false;
					break;
				case 40:
					return false;
					break;
			}
			var businessUnit = $.trim(this.value);
			if (businessUnit.length > 2 || businessUnit.length == 0 || e.keyCode == 8) {
				reloadBusinessUnitList();
			}
		}, 650));

		
		function reloadBusinessUnitList() {
			var searchUnit = $.trim($('#businessUnitId_chosen .chosen-search input').val());
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url: getContextPath() + '/buyer/searchBusinessUnit',
				data: {
					'searchUnit': searchUnit,
				},
				type: 'POST',
				dataType: 'json',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function(data) {
					var html = '';
					if (data != '' && data != null && data.length > 0) {
						$('#businessUnitId').find('option:not(:first)').remove();
						$.each(data, function(key, value) {
							if (value.id == null || value.id == '') {
								html += '<option value="" disabled>' + value.unitName + '</option>';
							} else {
								html += '<option value="' + value.id + '">' + (value.unitCode ? (value.unitCode + ' - ')  : '') + value.unitName + '</option>';
							}
						});
					}
					$('#businessUnitId').append(html);
					$("#businessUnitId").trigger("chosen:updated");
					$('#businessUnitId_chosen .chosen-search input').val(searchUnit);
					$('#loading').hide();
				},
				error: function(error) {
					console.log(error);
				}
			});
		}

		
		$('#btnNext').on('click', function(e) {
			e.preventDefault();
			if($('#createFormBlankWithBu').isValid()) {
				$('#createFormBlankWithBu').submit();
			}
		});
		
		$('#createBlankContractBu').on('click', function() {
			console.log('createBlankContractBu');
			$('#selectBusinessUnit-Modal').modal();
			
		});

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		table = $('#defaultTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#select-all').prop('checked', false); //to uncheck the checkbox on next page
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/productContractListData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
			"columns" : [
				{
		         	'searchable': false,
		         	'orderable': false,
		         	'className': 'checkbox-stylling',
		         	'render': function (data, type, row){
		             return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="eventIds" name="eventIds">';
		        	 }
				},
				{
				"data" : "id",
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '';
					if(!(row.status == 'DRAFT' )) {
						ret += '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
					}
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}
					
					if(!(row.status == 'TERMINATED' || row.status == 'EXPIRED' || row.status == 'PENDING' || row.status == 'APPROVED' || row.status == 'CANCELLED')) {
						ret += '<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>&nbsp;&nbsp;';
					}
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"></a>';
					return ret;
				}
			}, {
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"defaultContent" : "",
				"className" : "align-center"
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
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
		$('#defaultTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if($(this).attr('search-type') == 'select'){
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select name="'+(title.replace(/ /g,"")).toLowerCase()+'" data-index="'+i+'"><option value="">ALL</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="'+$(this).attr("style")+'"><input type="text" name="'+(title.replace(/ /g,"")).toLowerCase()+'" placeholder="<spring:message code="buyercreation.search.case"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch +='<th class="align-left" style="'+$(this).attr("style")+'"><div style="visibility:hidden;'+$(this).attr("style")+'"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#defaultTableList thead').append(htmlSearch);
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
			}Previous1234Next

		});
		$(bucketTable.table().container()).on('change', 'thead select', function() {
			bucketTable.column($(this).data('index')).search(this.value).draw();
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
	
	function updateLink(id) {
		var link = $("a#id ");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}

	function doCancel() {
		var link = $("a#idConfirmEdit");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/productContractListEdit?id=');
	}

	function updateEditLink(id) {
		var link = $("a#idConfirmEdit ");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}

	var bucketData = [];

	$('#defaultTableList tbody').on('click', 'button', function () {
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
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">\D7</button>
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

<!-- Confirm Edit pop-up  -->
<div class="modal fade" id="confirmEdit" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="confirm.suspend" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label> 
					<spring:message code="contract.suspend.confirm.message" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmEdit" class="btn pull-left btn-warning ph_btn_small hvr-pop " href="${pageContext.request.contextPath}/buyer/suspendProductContract?id=" title="Suspend Contract"> 
					<spring:message code="contract.suspend" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn pull-right btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.no" />
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
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">\D7</button>
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
	console.log("Calling export Bq format..");	
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
 });

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

var myDraftContract = '';
	$('document').ready(function() {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myDraftContract = $('#draftContractList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/contractDraftData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}

					var ret= '&nbsp;&nbsp;<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			}, {
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
				
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			},{
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			},{
				"data" : "status",
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#draftContractList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#draftContractList thead').append(htmlSearch);
		$(myDraftContract.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myDraftContract.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myDraftContract.table().container()).on('change', 'thead select', function() {
			myDraftContract.column($(this).data('index')).search(this.value).draw();
		});
		}
	});
		
		
		
});

var myPendingContract = '';
	$('document').ready(function() {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myPendingContract = $('#pendingContractList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/contractPendingData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
						ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
				
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			},{
				"data" : "status",
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#pendingContractList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#pendingContractList thead').append(htmlSearch);
		$(myPendingContract.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myPendingContract.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myPendingContract.table().container()).on('change', 'thead select', function() {
			myPendingContract.column($(this).data('index')).search(this.value).draw();
		});
		}
	});
		
		
});

var myTerminatedContract = '';
	$('document').ready(function() {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myTerminatedContract = $('#terminatedContractList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/contractTerminatedData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			}, {
				"data" : "companyName",
				"defaultContent" : ""
			}, {
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
				
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			},{
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#terminatedContractList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#terminatedContractList thead').append(htmlSearch);
		$(myTerminatedContract.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myTerminatedContract.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myTerminatedContract.table().container()).on('change', 'thead select', function() {
			myTerminatedContract.column($(this).data('index')).search(this.value).draw();
		});
		}
	});
		
		
});

var myUpcomingNewContract = '';
	$('document').ready(function() {


		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myUpcomingNewContract = $('#newContractTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/contractUpcomingNewData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
				
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			},{
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#newContractTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#newContractTableList thead').append(htmlSearch);
		$(myUpcomingNewContract.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myUpcomingNewContract.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myUpcomingNewContract.table().container()).on('change', 'thead select', function() {
			myUpcomingNewContract.column($(this).data('index')).search(this.value).draw();
		});
		
		}
	});
		
		
});
	
var myContractActive = '';
	$('document').ready(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContractActive = $('#activeContractTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
				$('#loading').show();
				return true;constructContractListByExpiredDaysBetweenForTenant
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/activeContractData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
			"columns" : [ 
				{
				"data" : "id",
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}
					
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
					ret += '&nbsp;&nbsp;<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			},{
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			}, {
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#activeContractTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#activeContractTableList thead').append(htmlSearch);
		$(myContractActive.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContractActive.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContractActive.table().container()).on('change', 'thead select', function() {
			myContractActive.column($(this).data('index')).search(this.value).draw();
		});
		
		}
	});
});

var myContractActive1 = '';
	$('document').ready(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContractActive1 = $('#suspendedContractTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
				$('#loading').show();
				return true;constructContractListByExpiredDaysBetweenForTenant
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/suspendedContractData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
			"columns" : [ 
				{
				"data" : "id",
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}
					
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
					ret += '&nbsp;&nbsp;<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			},{
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			}, {
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#suspendedContractTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#suspendedContractTableList thead').append(htmlSearch);
		$(myContractActive1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContractActive1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContractActive1.table().container()).on('change', 'thead select', function() {
			myContractActive1.column($(this).data('index')).search(this.value).draw();
		});
		
		}
	});
});

var myContractUrgentlyExpiring = '';
	$('document').ready(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContractUrgentlyExpiring = $('#lessThanOneMonthTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
				$('#loading').show();
				return true;constructContractListByExpiredDaysBetweenForTenant
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/oneMonthContractExpiredData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
			"columns" : [ 
				{
				"data" : "id",
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}
					
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
					ret += '&nbsp;&nbsp;<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>';
					ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			},{
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			}, {
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#lessThanOneMonthTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class=" align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#lessThanOneMonthTableList thead').append(htmlSearch);
		$(myContractUrgentlyExpiring.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContractUrgentlyExpiring.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContractUrgentlyExpiring.table().container()).on('change', 'thead select', function() {
			myContractUrgentlyExpiring.column($(this).data('index')).search(this.value).draw();
		});
		
		}
	});
});
	
	
	
var myContractUrgentlyExpiring1 = '';
	$('document').ready(function() {


		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContractUrgentlyExpiring1 = $('#OneToThreeMonthTableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/oneToThreeMonthContractExpiredData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}

					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
					ret += '<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>&nbsp;&nbsp;';
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
				
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			},{
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#OneToThreeMonthTableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#OneToThreeMonthTableList thead').append(htmlSearch);
		$(myContractUrgentlyExpiring1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContractUrgentlyExpiring1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContractUrgentlyExpiring1.table().container()).on('change', 'thead select', function() {
			myContractUrgentlyExpiring1.column($(this).data('index')).search(this.value).draw();
		});
		
		}
			
		
	});


});
var myContSubsequentlyExpire = '';
	$('document').ready(function() {


		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContSubsequentlyExpire = $('#threeToSixMonthExpiredList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/threeToSixMonthContractExpiredData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}

					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
					ret += '<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>&nbsp;&nbsp;';
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			}, {
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
				"defaultContent" : ""
			},{
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			},{
				"data" : "modifiedBy",
				"defaultContent" : ""
			},{
				"data" : "modifiedDate",
				"searchable" : false,
				"defaultContent" : ""
			},{
				"data" : "status",
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#threeToSixMonthExpiredList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#threeToSixMonthExpiredList thead').append(htmlSearch);
		$(myContSubsequentlyExpire.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContSubsequentlyExpire.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContSubsequentlyExpire.table().container()).on('change', 'thead select', function() {
			myContSubsequentlyExpire.column($(this).data('index')).search(this.value).draw();
		});
		
		}
			
	});
		
});

var myContSubsequentlyExpire1 = '';
	$('document').ready(function() {


		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContSubsequentlyExpire1 = $('#greaterThanSixMonthExpiredList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/greaterSixMonthContractExpiredData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var href = "";
					if(row.status == 'DRAFT' || row.status == 'SUSPENDED') {
						href = '${pageContext.request.contextPath}/buyer/productContractListEdit?id=' + row.id;
					} else {
						href = '#confirmEdit';
					}

					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>&nbsp;&nbsp;';
					ret += '<a href="' + href + '" onClick="javascript:updateEditLink(\'' + row.id + '\');" title=<spring:message code="tooltip.edit" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);"><i class="fa fa-lg fa-pencil-square-o" aria-hidden="true"></i></a>&nbsp;&nbsp;';
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			},{
				"data" : "contractId",
				"defaultContent" : ""
			},{
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"defaultContent" : "",
				"className" : "align-right",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			},{
				"data" : "createdBy",
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#greaterThanSixMonthExpiredList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#greaterThanSixMonthExpiredList thead').append(htmlSearch);
		$(myContSubsequentlyExpire1.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContSubsequentlyExpire1.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContSubsequentlyExpire1.table().container()).on('change', 'thead select', function() {
			myContSubsequentlyExpire1.column($(this).data('index')).search(this.value).draw();
		});
		
		}
			
			
	});
		
		
});
var myContractExpired = '';
	$('document').ready(function() {


		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		myContractExpired = $('#expiredContractList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();
				}, 5000);
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
				"url" : getContextPath() + "/buyer/contractExpiredData",
				"data" : function(d) {
					d.dateTimeRange = $("input[name='dateTimeRange']").val();
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
				"className" : "align-left",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {
					var ret = '<a href="${pageContext.request.contextPath}/buyer/productContractSummary/' + row.id + '" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.view" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" style="color: rgb(93,93,93);"><img height="18" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';
						ret += '&nbsp;&nbsp;<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal" style="color: rgb(93,93,93);">';
					return ret;
				}
			}, {
				"data" : "contractId",
				"defaultContent" : ""
			}, {
				"data" : "contractName",
				"defaultContent" : ""
			},{
				"data" : "eventId",
				"defaultContent" : ""
			},{
				"data" : "contractReferenceNumber",
				"defaultContent" : ""
			},{
				"data" : "companyName",
				"defaultContent" : ""
			},{
				"data" : "businessUnit",
				"defaultContent" : ""
			},{
				"data" : "contractStartDate",
				"defaultContent" : ""
			},{
				"data" :"contractEndDate",
				"defaultContent" : ""
			},{
				"data" : "groupCodeStr",
				"defaultContent" : ""
			},{
				"data" : "currencyCode",
				"className" : "align-center",
				"defaultContent" : ""
			},{
				"data" : "contractValue",
				"className" : "align-right",
				"defaultContent" : "",
				"render" : function(data, type, row) {
					return ReplaceNumberWithCommasFormat(row.contractValue, row.decimal);
				}
			}, {
				"data" : "createdBy",
				"defaultContent" : ""
			},{
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
				"searchable" : false,
				"defaultContent" : ""
			} ],

		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#expiredContractList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#expiredContractList thead').append(htmlSearch);
		$(myContractExpired.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				myContractExpired.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(myContractExpired.table().container()).on('change', 'thead select', function() {
			myContractExpired.column($(this).data('index')).search(this.value).draw();
		});
		
		}
			
	});
		
});


$(window).bind('load', function() {
	$('.box-bottom').click(function(e) {
		$("[type=checkbox]").each(function() {
			$(this).prop('checked', false);
			$.uniform.update($(this));
		});
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
});

$('.custom-checkAllbox').on('change', function() {
	var check = this.checked;
	$("[type=checkbox]").each(function() {
		$(".custom-checkbox1").prop('checked', check);
		$.uniform.update($(this));
	});
});

$("#exportProductReport1").click(function(e) {
	e.preventDefault();

	$('.error-range.text-danger').remove();
	var val = [];
	$('.custom-checkbox1:checked').each(
		function(i) {
			val[i] = $(this).val();
	});
	console.log(val + "val");

	if (typeof val === 'undefined' || val == '') {
		console.log("Error");
		$('#exportProductReport1').after('<p class="error-range text-danger marg-top-10 marg-right-10 pull-right"> Please select at least one contract to export</p>');
		return false;
	} else {
		$('#exportCSVForm').submit();
	}
});

/* $("#resetDate").click(
function(e) {
	e.preventDefault();
	if ($("#datepicker-date-time-nodisable").val() !== '') {
		location.reload();
	}
	$("#datepicker-date-time-nodisable").val('');
}); */


function ReplaceNumberWithCommasFormat(yourNumber, decimaltoFormate) {
	if(yourNumber!='' && yourNumber!=undefined){
		yourNumber = parseFloat(yourNumber).toFixed(decimaltoFormate);
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return n.join(".");
	}
	return yourNumber; 
}


$('#idConfirmEdit').click(function(){
	$('#loading').show();
});

$('#createBlankContract').click(function(){
	$('#loading').show();
});

$('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {
	e.preventDefault();
	$('.error-range.text-danger').remove();
	$('[name="dateRange"]').val($(this).val());
	table.ajax.reload();
	myDraftContract.ajax.reload();
	myPendingContract.ajax.reload();
	myTerminatedContract.ajax.reload();
	myUpcomingNewContract.ajax.reload();
	myContractUrgentlyExpiring.ajax.reload();
	myContractUrgentlyExpiring1.ajax.reload();
	myContSubsequentlyExpire.ajax.reload();
	myContSubsequentlyExpire1.ajax.reload();
	myContractExpired.ajax.reload();
	myContractActive.ajax.reload();
	myContractActive1.ajax.reload();
});

$("#resetDate").click(function(e) {
	e.preventDefault();
	if ($("#datepicker-date-time-nodisable").val() !== '') {
		$("#datepicker-date-time-nodisable").val('');
		$('[name="dateRange"]').val('');
		$("input[name='dateTimeRange']").data('daterangepicker').setStartDate(new Date());
		$("input[name='dateTimeRange']").data('daterangepicker').setEndDate(new Date());
		$("input[name='dateTimeRange']").val('');
		
		table.ajax.reload();
		myDraftContract.ajax.reload();
		myPendingContract.ajax.reload();
		myTerminatedContract.ajax.reload();
		myUpcomingNewContract.ajax.reload();
		myContractUrgentlyExpiring.ajax.reload();
		myContractUrgentlyExpiring1.ajax.reload();
		myContSubsequentlyExpire.ajax.reload();
		myContSubsequentlyExpire1.ajax.reload();
		myContractExpired.ajax.reload();
		myContractActive.ajax.reload();
		myContractActive1.ajax.reload();
	}
});


</script>
<style>
#exportProductReport {
	margin-left: 66%;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
