<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('SUPPLIER_APPROVAL') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="supplierRegistrationInfoDesk" code="application.owner.supplier.registration.info" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRegistrationInfoDesk}] });
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
			<li class="active">Supplier Registration Information</li>
		</ol>
		<!-- page title block -->
		<div class="page_title_wrapper marg-bottom-20">
			<h3 class="sub_title">
				<i class="glyph-icon icon-list-alt" aria-hidden="true"></i> Supplier Registration Information
			</h3>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg ">
			<div class="import-supplier-inner-first pad_all_15" style="display: none;">
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">Search in:</label>
					</div>
					<div class="col-md-3">
						<div>
							<select class="custom-select" name="" data-parsley-id="0644">
								<option>Newest</option>
								<option>Oldest</option>
							</select>
							<ul id="parsley-id-0644" class="parsley-errors-list">
							</ul>
						</div>
						name
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">Company Name:</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="Company Name">
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"><spring:message code="supplier.registration.company.number" /> :</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="Company Registration Number">
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">Category Code:</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="Category Code">
					</div>
					<div class="col-md-1 pad10">
						<a href="#">
							<img src="<c:url value="/resources/images/dot.png"/>" />
						</a>
					</div>
					name
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">Project Name :</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="Project Name">
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">Project Name :</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="Project Name">
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10">CIDB Code :</label>
					</div>
					<div class="col-md-3">
						<input id="" class="form-control" type="text" placeholder="CIDB Code">
					</div>
					<div class="col-md-1 pad10">
						<a href="#">
							<img src="<c:url value="/resources/images/dot.png"/>" />
						</a>
					</div>
				</div>
				<div class="row marg-bottom-10">
					<div class="col-md-3">
						<label class="marg-top-10"></label>
					</div>
					<div class="col-md-6">
						<button class="btn btn-primary ph_btn">Search Supplier</button>
						<button class="btn btn-black for-form-back ph_btn marg-left-10" href="#">Previous</button>
					</div>
				</div>
			</div>
			<div class="lower-bar-search-contant a_search search">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="lower-bar-search-contant-heading gray-up-bg">
					<div class="row">
						<div class="col-md-3">
							<div class="short-by">
								<div class="col-xs-12">
									<div class="col-xs-offset-2 col-xs-9" style="padding: 0 !important">
										 <input type="text" placeholder="Search..." class="form-control idGlobalSearch search" id="idGlobalSearch">
									</div>
									<div class="col-xs-1" style="padding: 0 !important">
										 <button class="btn btn-info" type="button" id="idTxtSearch">Search</button> 
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-2"></div>
						
						
						<div class="col-md-3">
							<div class="short-by">
								<label>Sort By</label>
								<div>
									<select class="custom-select supplierRegisterOrder" name="supplierRegisterOrder" data-parsley-id="0644">
										<option>Ascending</option>
										<option>Descending</option>
									</select>
									<ul id="parsley-id-0644" class="parsley-errors-list">
									</ul>
								</div>
							</div>
						</div>
						
						
						
					</div>
				</div>
			</div>
			<div class="lower-bar-search-contant-main white-bg pad_all_20 pad-t35">
				<div id="idSuppList" class="supplist">
					<div class="row event_listing_section">
						<c:forEach items="${supplierList}" var="financePo">
							<spring:url value="/finance/financeSupplierDetails" var="url" htmlEscape="true" />
							<form:form name="form" method="get" action="${url}/${financePo.supplier.id}" modelAttribute="formSupplier">
								<div class="col-sm-6 col-md-4 col-xs-6">
								<article class="event_box pending" >
								<header class="event_box_head">
									<h4>
										${financePo.supplier.companyName}
										
									</h4>
									<span>${financePo.supplier.registrationOfCountry.countryName}</span>
								</header>
								<div class="box-boder">
									<div class="box_content">
									
									<label><spring:message code="supplier.registeration.date" /> :</label><fmt:formatDate pattern="dd/MM/yyyy HH:mm a" value="${financePo.supplier.registrationDate}" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/><br>
									<label>Registration No:</label>${financePo.supplier.companyRegistrationNumber}<br/>
									<label>Person in Charge :</label>${financePo.supplier.fullName}<br/>
									<label>Phone No :</label>${financePo.supplier.mobileNumber}<br/>
								<%-- 	<label>Address :</label>
										   <span class="date_event">
										   	   ${sp.line1}

										   </span>
                                           <span class="end_t_txt">
												${sp.line2}
										   </span> --%>
										   
									</div>
									
								</div>	
								<span class="box_overlay">
								<a href="javascript:void(0);" onclick="$(this).closest('form').submit();" class="idKnowMore btn btn-info hvr-pop hvr-rectangle-out">KNOW MORE</a>
								</span>
								<div class="lower-bar-search-contant-main-contant  pad_all_10 buttons">
											</div>
								
										<%-- 											<div class="dwonloadico">
												<a class=" btn-tooltip" title="" data-placement="top"
													data-toggle="tooltip" href="#"
													data-original-title="Download Profile"> <img width="20"
													src="<c:url value="/resources/images/dwonload.png"/>"
													alt="Profile image"></a>
											</div> --%>
									
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
<div class="modal fade" id="rejectModel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content" style="width: 135%">
			<div class="modal-header">
				<h3>Reject</h3>
				<input id="rejectId" name="rejectId" type="hidden" value="">
				<button class="close for-absulate" data-dismiss="modal" type="button">×</button>
			</div>
			<div class="model_pop_mid">
				<form class="bordered-row has-validation-callback" id="demo-form" name="demo-form">
					<div class="sup_pop_row">
						<div class="remark_text">Remarks</div>
						<div class="remark_field">
							<textarea class="form-control rejectRemark" data-validation="legnth" data-validation-length="0-500"></textarea>
							<span class="sky-blue">Max 500 characters only</span>
						</div>
					</div>
					<div class="sup_pop_row">
						<div class="col-md-offset-2 col-md-10">
							<div class="col-md-5" style="padding-right: 2px">
								<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out dis" id="idReject" type="submit">Reject</button>
							</div>
							<div class="col-md-5" style="padding-left: 2px">
								<button type="button" class="btn btn-black btn-block hvr-pop hvr-rectangle-out1" id="idRejectClose">Cancel</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/js/view/financeSupplierList.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
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
<script>
	$.validate({
		lang : 'en'
	});
</script>
<style>
 
 .event_box_footer button {
    margin-left: 2%;
    width: 46%;
    z-index: 3;
}
</style>