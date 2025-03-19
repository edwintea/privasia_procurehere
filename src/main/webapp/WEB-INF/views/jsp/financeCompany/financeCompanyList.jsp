<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('BUYER_CREATE') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="buyerRegistrationInfoDesk" code="application.owner.buyer.registration.info" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerRegistrationInfoDesk}] });
});
</script>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content">
	<div class="container">
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a>
			</li>
			<li class="active">Finance Company Listing</li>
		</ol>
		<!-- page title block -->
		<div class="page_title_wrapper marg-bottom-20">
			<h3 class="sub_title">
				<i class="glyph-icon icon-list-alt" aria-hidden="true"></i> &nbsp;Finance Company Listing
			</h3>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg ">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="lower-bar-search-contant a_search">
				<div class="lower-bar-search-contant-heading gray-up-bg">
					<div class="row">
						<div class="col-md-3">
							<div class="short-by">
								<div class="col-xs-12">
									<div class="col-xs-offset-2 col-xs-9" style="padding: 0 !important">
										<input type="text" placeholder="Search..." class="form-control idGlobalSearch search" id="idGlobalSearch">
									</div>
									<div class="col-xs-1" style="padding: 0 !important">
										<button class="btn btn-info" type="button" id="idFinanceSearch">Search</button>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-1"></div>
						<div class="col-md-3">
							<div class="short-by">
								<label>Sort By</label>
								<div>
									<select class="custom-select financeOrder" name="financeOrder" data-parsley-id="0644">
										<option>Newest</option>
										<option>Older</option>
									</select>
									<ul id="parsley-id-0644" class="parsley-errors-list">
									</ul>
								</div>
							</div>
						</div>
						<div class="col-md-3">
							<div class="short-by">
								<label>Account Status</label>
								<div>
									<select class="custom-select financeStatus" name="financeStatus" data-parsley-id="0644">
										<option>All</option>
										<!-- <option>Unpaid</option> -->
										<option selected="selected">Pending</option>
										<option>Active</option>
										<option>Suspended</option>
									</select>
									<ul id="parsley-id-0644" class="parsley-errors-list">
									</ul>
								</div>
							</div>
						</div>
						<div class="col-md-1"></div>
						<div class="col-md-1">
							 <spring:url value="/owner/financeCompanyCreation" var="financeCompanyCreation" htmlEscape="true" />
							<form name="form" method="get" action="${financeCompanyCreation}" commandName="financeCompany">
								<button type="submit" class="btn btn-info ph_btn_midium hvr-pop float-right hvr-rectangle-out ${canEdit ? '':'disabled'}" style="float: right;">Add Finance Company</button>
							</form> 
						</div>
					</div>
				</div>
			</div>
			<div class="lower-bar-search-contant-main white-bg pad_all_20 buyerapp ">
				<spring:url value="financeDetails" var="financeDetail" htmlEscape="true" />
				<div id="idfinanceList" class="buylist">
					<div class="row event_listing_section">
						<c:forEach items="${financeCompanyList}" var="by">
							<form:form name="form" method="get" action="${financeDetail}/${by.id}" modelAttribute="financeCompany">
								<div class="col-sm-6 col-md-4 ">
									<article class="event_box pending" >
										<header class="event_box_head">
											<h4>${by.companyName}
												<span class="closed_grp_txt_mod">${by.status}</span>
											</h4>
											<span>${by.registrationOfCountry.countryName}</span>
										</header>
										<div class="box-boder">
											<div class="box_content">
												<!--<label>Registration Date :</label><fmt:formatDate pattern="dd/MM/yyyy HH:mm a" value="${sp.registrationDate}" /><br/>-->
												<label>Registration No:</label>
												${by.companyRegistrationNumber}<br />
												<c:if test="${!empty by.registrationCompleteDate}">
													<label>Registration Completed:</label>
													<fmt:formatDate pattern="dd/MM/yyyy HH:mm a" value="${by.registrationCompleteDate}" />
													<br />
												</c:if>
												<label>Person in Charge :</label>
												${by.fullName}<br />
												<label>Phone No :</label>
												${by.companyContactNumber}<br />
												<label>Address :</label>
												<span class="date_event"> ${by.line1}</span>
												<span class="end_t_txt"> ${by.line2} ${by.city} </span>
											</div>
											<footer class="event_box_footer"> </footer>
										</div>
										<span class="box_overlay">
											<a href="javascript:void(0);" onclick="$(this).closest('form').submit();" class="btn btn-info hvr-pop hvr-rectangle-out">
												Know more <i class="glyph-icon icon-long-arrow-right"></i>
											</a>
										</span>
									</article>
								</div>
							</form:form>
						</c:forEach> 
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- <style>
#financeCompany .event_box .box_content {
	font-size: 16px;
}
</style> -->
<!-- Content box -->
 <script type="text/javascript" src="<c:url value="/resources/js/view/financecompany.js"/>"></script> 
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script>
	$(document).ready(function() {

		$(".toggle").click(function() {
			$(this).parent().toggleClass("highlight");
		});
	});
</script>