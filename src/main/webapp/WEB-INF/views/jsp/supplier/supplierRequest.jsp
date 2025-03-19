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
<div class="panel" id="requestPanel">
	<div class="marg-top-5">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a id="closeView" data-toggle="collapse" class="accordion" href="#collapseRequest">Supplier Request 
					<p id="supplierStatusId" style="float: right; margin-right: 30px;">
						${supplierRequest.status}
					</p>
				</a>
			</h4>
		</div>
		<div id="collapseRequest" class="panel-collapse collapse in">
			<div class="panel-body">
				<form:form modelAttribute="supplierRequest"  method="post" id="supplierRequestForm">
					<input type="hidden" id="industryCategoryCodes" value="${supplierRequestIndCat}"/>
					<form:input type="hidden" id="requestId" path="id" />
					<form:input type="hidden" id="statusId" path="status" />
					<form:input type="hidden" id="indCat" path="industryCategory" />
					<div class="row">
						<div class="col-md-4">
							<label> <spring:message code="application.catagory" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10" id="pointerEventId">
							<input type="text" id="demo-input-local" name="blah"  data-validation="required"/>
							<div class="col-md-12 selectListAjax"></div>
							<div id="catValErr"></div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<label> <spring:message code="supplier.remarks" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10">
							<form:textarea path="supplierRemark" disabled="true" readonly="true" class="form-control" rows="3"></form:textarea>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<label> <spring:message code="buyer.remarks" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10">
							<form:textarea id="buyerRemarkId" class="form-control"  path="buyerRemark" rows="3" data-validation="length" data-validation-length="0-500" placeholder="Write a comment"></form:textarea>
						</div>
					</div>
					<c:if test="${supplierFormSubmition!=null}">
						<div class="row">
							<div class="col-md-4">
								<label> <spring:message code="supplier.request.prequalify.form" />
								</label>
							</div>
							<div class="col-md-8 marg-bottom-10">
								<p><a href="${pageContext.request.contextPath}/buyer/supplierFormSubView/${supplierFormSubmition.id}">${supplierFormSubmition.name}</a></p>
							</div>
						</div>
					</c:if>
				</form:form>
			</div>
			
			<div class="row buttons-row">
				<c:if test="${supplierFormSubmition!=null}">
					<c:if test="${(supplierRequest.status eq 'PENDING' or supplierRequest.status eq 'REJECTED')  and supplierFormSubmition.status eq 'ACCEPTED'}">
						<div class="col-md-2">
							<a>
								<button id="approveRequest" class="btn btn-info ph_btn hvr-pop">
									<spring:message code="application.approve" />
								</button>
							</a>
						</div>
					</c:if>
					<c:if test="${supplierRequest.status eq 'PENDING' and (supplierFormSubmition.status eq 'ACCEPTED' or supplierFormSubmition.status eq 'REJECTED' or supplierFormSubmition.status eq 'PENDING' or supplierFormSubmition.approvalStatus eq 'REJECTED')}">
						<div class="col-md-2">
							<a>
								<button id="rejectRequest" class="btn btn-warning ph_btn hvr-pop">
									<spring:message code="application.reject" />
								</button>
							</a>
						</div>
					</c:if>
				</c:if>
 
				<c:if test="${supplierFormSubmition ==null}">
					<c:if test="${supplierRequest.status ne 'APPROVED'}">
						<div class="col-md-2">
							<a>
								<button id="approveRequest" class="btn btn-info ph_btn hvr-pop">
									<spring:message code="application.approve" />
								</button>
							</a>
						</div>
					</c:if>
					<c:if test="${supplierRequest.status eq 'PENDING'}">
						<div class="col-md-2">
							<a>
								<button id="rejectRequest" class="btn btn-warning ph_btn hvr-pop">
									<spring:message code="application.reject" />
								</button>
							</a>
						</div>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>
</div>

<!-- PH-1179 reject supplier request -->
<div class="modal fade" id="rejectRequestModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.reject" />
				</h3>
			</div>
			<div class="modal-body">
				<label><spring:message code="reject.supplier.request" /></label> 
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a type="button" href="javascript:void(0);" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.no2" /></a>
				<button id="rejectSupplierRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-left: 0;">
					<spring:message code="application.yes2" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- PH-1179 accept supplier request -->
<div class="modal fade" id="approveRequestModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.approve" />
				</h3>
			</div>
			<div class="modal-body">
				<label><spring:message code="approve.supplier.request" /></label> 
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a type="button" href="javascript:void(0);" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.no2" /></a>
				<button id="approveSupplierRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-left: 0;">
					<spring:message code="application.yes2" />
				</button>
			</div>
		</div>
	</div>
</div>