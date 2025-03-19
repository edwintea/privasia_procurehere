<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('SUPPLIER')" var="sup" />
<sec:authorize access="hasRole('BUYER')" var="buy" />
<sec:authorize access="hasRole('OWNER')" var="own" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />

<div id="page-content">
	<div class="container">
		<ol class="breadcrumb">
			<sec:authorize access="hasRole('BUYER')">
				<li>
					<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">Dashboard</a>
				</li>
			</sec:authorize>
			<sec:authorize access="hasRole('OWNER')">
				<li>
					<a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a>
				</li>
			</sec:authorize>
			<li class="active">Global Search List </li>
		</ol>
		<!-- page title block -->
		<div class="page_title_wrapper marg-bottom-20">
			<h3 class="sub_title">
				<i class="glyph-icon icon-list-alt" aria-hidden="true"></i> &nbsp;Global Search List
			</h3>
			<c:if test="${opVal == 'option4'}">
				<sec:authorize access="hasRole('BUYER')">
				<div class="spd_top">
					<form id="searchValue" method="post" action="${pageContext.request.contextPath}/search/searchPrPo">
						<div class="spd_filter dropdown">
						<c:if test="${flag }">
							<a href="#" class="hvr-pop dropdown-toggle" id="dropdownMenuFilter" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
								<img src="${pageContext.request.contextPath}/resources/assets/images/filter_icon.png" alt="filter" style="width: 40px;">
							</a>
							</c:if>
							<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuFilter" id="supplierFilter" style="width: 300px;">
								<li>&nbsp;</li>
								<li class="filterDashbord">
									<input id="daterangepicker-example" name="dateRangePrPo" class="form-control for-clander-view for-clander-view pull-right" type="text" data-validation-format="dd/mm/yyyy - dd/mm/yyyy" placeholder="Select date range"
										style="float: none !important;" />
								</li>
								<li role="separator" class="divider"></li>

								<li class="dropdown-header">Select Status</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="DRAFT" class="custom-checkbox" id="status" name="status">
										DRAFT
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="PENDING" class="custom-checkbox" id="status" name="status">
										PENDING
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="APPROVED" class="custom-checkbox" id="status" name="status">
										APPROVED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="TRANSFERRED" class="custom-checkbox" id="status" name="status">
										TRANSFERRED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="DELIVERED" class="custom-checkbox" id="status" name="status">
										DELIVERED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="CANCELED" class="custom-checkbox" id="status" name="status">
										CANCELED
									</label>
								</li>
								
								<!-- Sort PR/PO -->
								<!-- <li role="separator" class="divider"></li> 
								<li class="dropdown-header">Sort by</li>
								<li class="filterDashbord">
									<label>
										<input type="radio" value="CANCELED" class="custom-radio" id="sortBy" name="sortBy">
										Title (A-Z)
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="radio" value="CANCELED" class="custom-radio" id="sortBy" name="sortBy">
										Title (Z-A)
									</label>
								</li> -->
							</ul>
							<c:if test="${flag }">
							<button class="btn btn-default" type="submit">GO</button>
							</c:if>
						</div>
						<div class="spd_search">
						<c:if test="${flag}">
							<input type="text" class="form-control" name="searchVal" value="${searchVal}" placeholder="Search PR/PO">
							</c:if>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</div>
					</form>
				</div>
			</sec:authorize>
			<sec:authorize access="hasRole('SUPPLIER')">
				<div class="spd_top">
					<form id="searchValue" method="post" action="${pageContext.request.contextPath}/search/searchPo">
						<div class="spd_filter dropdown">
						<c:if test="${flag }">
							<a href="#" class="hvr-pop dropdown-toggle" id="dropdownMenuFilter" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
								<img src="${pageContext.request.contextPath}/resources/assets/images/filter_icon.png" alt="filter" style="width: 40px;">
							</a>
							</c:if>
							<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuFilter" id="supplierFilter" style="width: 300px;">
								<li>&nbsp;</li>
								<li class="filterDashbord">
									<input id="daterangepicker-example" name="dateRangePrPo" class="form-control for-clander-view for-clander-view pull-right" type="text" data-validation-format="dd/mm/yyyy - dd/mm/yyyy" placeholder="Select date range"
										style="float: none !important;" />
								</li>
								<li role="separator" class="divider"></li>
								<li class="dropdown-header">Select Status</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="ORDERED" class="custom-checkbox" id="status" name="status">
										ORDERED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="ACCEPTED" class="custom-checkbox" id="status" name="status">
										ACCEPTED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="DECLINED" class="custom-checkbox" id="status" name="status">
										DECLINED
									</label>
								</li>
								<li class="filterDashbord">
									<label>
										<input type="checkbox" value="CANCELLED" class="custom-checkbox" id="status" name="status">
										CANCELLED
									</label>
								</li>
							</ul>
							<c:if test="${flag }">
							<button class="btn btn-default" type="submit">GO</button>
							</c:if>
						</div>
						<div class="spd_search">
						<c:if test="${flag}">
							<input type="text" class="form-control" name="searchVal" value="${searchVal}" placeholder="Search PO">
							</c:if>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</div>
					</form>
				</div>
			</sec:authorize>
			</c:if>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg ">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div></div>
			<c:if test="${not empty event}">

				<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
					<div class="row">
						<c:forEach items="${event}" var="eve">
							<c:if test="${((eve.eventPermissions.owner or eve.eventPermissions.viewer or eve.eventPermissions.editor) and (eve.status eq 'DRAFT' or eve.status eq 'SUSPENDED'))}">
								<c:set var="buyerUrl" value="${pageContext.request.contextPath}/buyer/${eve.type}/createEventDetails/${eve.id}" />
							</c:if>

							<c:if test="${eve.status eq 'SUSPENDED'  and !(eve.eventPermissions.owner or eve.eventPermissions.viewer or eve.eventPermissions.editor)}">
								<c:set var="buyerUrl" value="${pageContext.request.contextPath}/buyer/${eve.type}/eventSummary/${eve.id}" />
							</c:if>

							<c:if test="${eve.status ne 'DRAFT' and eve.status ne 'SUSPENDED'}">
								<c:set var="buyerUrl" value="${pageContext.request.contextPath}/buyer/${eve.type}/eventSummary/${eve.id}" />
							</c:if>
						
							<c:if test="${eve.status ne 'DRAFT' && eve.status ne 'SUSPENDED'}">
								<c:set var="supplierUrl" value="${pageContext.request.contextPath}/supplier/supplierEvent/${eve.type}/${eve.id}" />
							</c:if>
							<form name="form" method="${buy ? 'get': 'get'}" action="${buy ? buyerUrl : (sup ? supplierUrl : '')}">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="col-sm-6 col-md-4 ">
									<article class="event_box" style="height: 262px;">
										<header class="event_box_head">
											<h4>${eve.eventOwner}
												<span class="closed_grp_txt_mod">${sup and (eve.status eq 'CLOSED' or eve.status eq 'COMPLETE' or eve.status eq 'FINISHED') ? 'CLOSED': eve.status}</span>
											</h4>
											<span></span>
										</header>
										<div class="box-boder">
											<div class="box_content">
												<label>Referance No: </label>
												${eve.referenceNumber}<br />
												<label>Business Unit: </label>
												${eve.unitName}<br />
												<label>Event ID: </label>
												${eve.eventId}<br />
												<label>Event Name: </label>
												${eve.eventName}<br />
												<label>Event Type: </label>
												${eve.type}<br />
												<label>Event Start : </label>
												<fmt:formatDate value="${eve.eventStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
												<br />
												<label>Event End : </label>
												<fmt:formatDate value="${eve.eventEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
												<br />
											</div>
											<footer class="event_box_footer"> </footer>
										</div>
										<span class="box_overlay"  >
											<c:if test="${eve.visible}">
												<a class="btn btn-info hvr-pop hvr-rectangle-out" href="#" onclick="$(this).closest('form').submit()" id="idKnowMore"  >
													Know more <i class="glyph-icon icon-long-arrow-right"></i>
												</a>
											</c:if>
											<c:if test="${!eve.visible}">
	 											<span class="btn btn-info hvr-pop hvr-rectangle-out">
													Not authorized 
												</span>
	  										</c:if>
 										</span>
									</article>
								</div>
							</form>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty buyerSearchList}">
				<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
					<div class="row">
						<c:forEach items="${buyerSearchList}" var="buyList">
							<div class="col-sm-6 col-md-4 ">
								<article class="event_box" style="height: 262px;">
									<header class="event_box_head">
										<h4>${buyList.companyName}
											<span class="closed_grp_txt_mod">${buyList.status}</span>
										</h4>
										<span>${buyList.registrationOfCountry.countryName}</span>
									</header>
									<div class="box-boder">
										<div class="box_content">
											<!--<label>Registration Date :</label><fmt:formatDate pattern="dd/MM/yyyy HH:mm a" value="${sp.registrationDate}" /><br/>-->
											<label>Registration No:</label>${buyList.companyRegistrationNumber}<br />
											<label>Person in Charge :</label>${buyList.fullName}<br />
											<label>Phone No :</label>${buyList.companyContactNumber}<br />
											<label>Address :</label>
											<span class="date_event">${buyList.line1}</span>
											<span class="end_t_txt">${buyList.line2} ${buyList.city} </span>
										</div>
										<footer class="event_box_footer"> </footer>
									</div>
									<span class="box_overlay">
										<a href="${pageContext.request.contextPath}/owner/buyerDetails/${buyList.id}" class="btn btn-info hvr-pop hvr-rectangle-out">
											Know more <i class="glyph-icon icon-long-arrow-right"></i>
										</a>
									</span>
								</article>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty supplierSearchList}">
				<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
					<div class="row">
						<c:forEach items="${supplierSearchList}" var="supplierList">
							<div class="col-sm-6 col-md-4 ">
								<article class="event_box" style="height: 262px;">
									<header class="event_box_head">
										<h4>
											${supplierList.companyName}
											<span class="closed_grp_txt_mod">${supplierList.status}</span>
										</h4>
										<span>${supplierList.registrationOfCountry.countryName}</span>
									</header>
									<div class="box-boder">
										<div class="box_content">
											<label>Registration Date:</label>${supplierList.registrationDate}<br />
											<label>Registration No:</label>${supplierList.companyRegistrationNumber}<br />
											<label>Person in Charge :</label>${supplierList.fullName}<br />
											<label>Phone No :</label>${supplierList.companyContactNumber}<br />
										</div>
										<footer class="event_box_footer"> </footer>
									</div>


									<sec:authorize access="hasRole('SUPPLIER') or hasRole('BUYER') or hasRole('OWNER')">
										<span class="box_overlay">
											<a href="${pageContext.request.contextPath}/supplierreg/supplierDetails/${supplierList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
											</a>
										</span>
									</sec:authorize>
									<sec:authorize access="hasRole('FINANCE')">
									<span class="box_overlay">
											<a href="${pageContext.request.contextPath}/finance/financeSupplierDetails/${supplierList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
										</a>
									</span>
									</sec:authorize>

								</article>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty prSearchList}">
				<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
					<div class="row">
						<c:forEach items="${prSearchList}" var="prList">
							<div class="col-sm-6 col-md-4 ">
								<article class="event_box" style="height: 262px;">
									<header class="event_box_head">
										<h4>
											${prList.name}
											<sec:authorize access="hasRole('SUPPLIER') or hasRole('BUYER') or hasRole('OWNER')">
											<span class="closed_grp_txt_mod">${prList.status}</span>
											</sec:authorize>
										</h4>
										<span></span>
									</header>
									<div class="box-boder">
										<div class="box_content">
											<label>Reference No:</label>${prList.referenceNumber}<br />
											<sec:authorize access="hasRole('BUYER')">
											<label>Person in Charge :</label>${prList.name}<br />
											</sec:authorize>
											<label><spring:message code="application.description"/> :</label>${prList.description}<br />
											<c:if test="${prList.status == 'DRAFT' || prList.status == 'PENDING'}">
												<label>PR ID :</label>${prList.prId}<br />
											</c:if>
											<c:if test="${not empty prList.poNumber}">
												<label>PO Number :</label>${prList.poNumber}<br />
											</c:if>
											<c:if test="${prList.status == 'DRAFT' || prList.status == 'PENDING'}">
												<label>PR Created Date :</label>
												<fmt:formatDate value="${prList.prCreatedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
												<br />
											</c:if>
											<c:if test="${not empty prList.poCreatedDate}">
												<label>PO Created Date :</label>
												<fmt:formatDate value="${prList.poCreatedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
												<br />
											</c:if>
											<sec:authorize access="hasRole('BUYER')">
											<label>PR Supplier :</label>${not empty prList.supplierName ? prList.supplierName : prList.supplier.fullName}<br />
											<label>Business Unit :</label>${prList.businessUnit.unitName}<br />
											</sec:authorize>
											<sec:authorize access="hasRole('SUPPLIER')">
											<label>Business Unit :</label>${prList.businessUnit.displayName}<br />
											</sec:authorize>
										</div>
										<footer class="event_box_footer"> </footer>
									</div>
									<c:set var="poBuyerUrl" value="${pageContext.request.contextPath}/buyer/${prList.status == 'DRAFT' ? 'prSummary' : 'prView' }/${prList.id}" />
									<c:set var="poSupplierUrl" value="${pageContext.request.contextPath}/supplier/supplierPrView/${prList.id}" />
							
									<span class="box_overlay">
										<%-- <a href="${buy ? poBuyerUrl : (sup ? poSupplierUrl : '')}"  class="btn btn-info hvr-pop hvr-rectangle-out">
											Know more <i class="glyph-icon icon-long-arrow-right"></i>
										</a> --%>
										
														<sec:authorize access="hasRole('FINANCE')">
											<span class="box_overlay">
												<a href="${pageContext.request.contextPath}/finance/financePOView/${prList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
												</a>
											</span>
										</sec:authorize>
										<sec:authorize access="hasRole('SUPPLIER') or hasRole('BUYER') or hasRole('OWNER')">

											<a href="${pageContext.request.contextPath}/buyer/${prList.status == 'DRAFT' ? 'prSummary' : 'prView' }/${prList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
											</a>
										</sec:authorize> 
									
									</span> 
								</article>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty poSearchList}">
				<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
					<div class="row">
						<c:forEach items="${poSearchList}" var="poList">
							<div class="col-sm-6 col-md-4 ">
								<article class="event_box" style="height: 262px;">
									<header class="event_box_head">
										<h4>
											${poList.name}
											<sec:authorize access="hasRole('SUPPLIER') or hasRole('BUYER') or hasRole('OWNER')">
												<span class="closed_grp_txt_mod">${poList.status}</span>
											</sec:authorize>
										</h4>
										<span></span>
									</header>
									<div class="box-boder">
										<div class="box_content">
											<label>Reference No:</label>${poList.referenceNumber}<br />
											<sec:authorize access="hasRole('BUYER')">
												<label>Person in Charge :</label>${poList.name}<br />
											</sec:authorize>
											<label><spring:message code="application.description"/> :</label>${poList.description}<br />
											<c:if test="${not empty poList.poNumber}">
												<label>PO Number :</label>${poList.poNumber}<br />
											</c:if>
											<c:if test="${not empty poList.createdDate}">
												<label>PO Created Date :</label>
												<fmt:formatDate value="${poList.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
												<br />
											</c:if>
											<sec:authorize access="hasRole('BUYER')">
												<label>PR Supplier :</label>${not empty poList.supplierName ? poList.supplierName : poList.supplier.fullName}<br />
												<label>Business Unit :</label>${poList.businessUnit.unitName}<br />
											</sec:authorize>
											<sec:authorize access="hasRole('SUPPLIER')">
												<label>Business Unit :</label>${poList.businessUnit.displayName}<br />
											</sec:authorize>
										</div>
										<footer class="event_box_footer"> </footer>
									</div>
									<span class="box_overlay">
										<sec:authorize access="hasRole('FINANCE')">
											<span class="box_overlay">
												<a href="${pageContext.request.contextPath}/finance/financePOView/${poList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
												</a>
											</span>
										</sec:authorize>
										<sec:authorize access="hasRole('BUYER') or hasRole('OWNER')">
											<a href="${pageContext.request.contextPath}/buyer/poView/${poList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
											</a>
										</sec:authorize> 
										<sec:authorize access="hasRole('SUPPLIER')">
											<a href="${pageContext.request.contextPath}/supplier/supplierPrView/${poList.id}" class="btn btn-info hvr-pop hvr-rectangle-out"> Know more <i class="glyph-icon icon-long-arrow-right"></i>
												</a>
										</sec:authorize>
									</span> 
								</article>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			
		</div>
	</div>
</div>
</div>
<style>
#buyerObj .event_box .box_content {
	font-size: 16px;
}

.event_box_head h4 {
	line-height: 25px !important;
}

li.filterDashbord {
	margin-left: 10px;
	margin-right: 10px;
}

li.filterDashbord label {
	line-height: 1;
}

/* .dropdown-header {
	font-size: 18px;
	color: #0a0a0a;
} */
div.radio[id^='uniform-'] > span {
     margin-top: 0px; 
}
</style>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>


<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script>
	$(document).ready(function() { //
	 
		$(".searchRadio").change(function() {
			//$(this).form.submit();
			$(this).closest('form').submit();
			
		})

		$(".toggle").click(function() {
			$(this).parent().toggleClass("highlight");
		});

		/* $(".custom-checkbox-type").change(function() {
			//alert();
		})
		 */
		/* $('#dataRangePrPo').on('apply.daterangepicker', function(e, picker) {
			e.preventDefault();
			table.ajax.reload();
		}); */
		/* $(".applyBtn").click(function(e) {
			e.preventDefault();
			alert();
		}); */
	});
</script>
