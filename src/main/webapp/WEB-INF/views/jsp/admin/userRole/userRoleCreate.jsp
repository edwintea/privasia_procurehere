<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('USER_ROLE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('BUYER')">
<spring:message var="userRoleDesk" code="application.user.role" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${userRoleDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('SUPPLIER')">
	<spring:message var="supplierUserRoleCreateDesk" code="application.supplier.user.create.role" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierUserRoleCreateDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('OWNER')">
	<spring:message var="ownerUserRoleCreateDesk" code="application.owner.user.create.role" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${ownerUserRoleCreateDesk}] });
});
</script>
</sec:authorize>
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<link rel="stylesheet" href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>">
<style type="text/css">
.leftSideOfCheckbox {
	width: 47%;
	float: left;
	border-right: 1px solid #d8d8d8;
	margin: 0 2% 0 0;
	height: 300px;
	overflow-y: auto;
}

.rightSideOfCheckbox {
	width: 50%;
	float: left;
	height: 300px;
	overflow-y: auto;
}

.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.show-message {
    display: block;
}

.hide-message {
    display: none;
}

</style>
<div id="page-content" view-name="userrole">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<li>
				<a href="#">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<li>
			    <spring:message code="users.role.label" />
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="userrole.label" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="userrole.administration" />
			</h2>
		</div>
		<!-- page title block -->
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="userrole.label" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<form:form id="userRoleRegistration" method="post" modelAttribute="userRoleObj" action="role">
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label cssClass="marg-top-10" path="roleName">
								<spring:message code="userrole.role" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:hidden path="id" />
							<spring:message code="rolename.placeholder" var="place" />
							<spring:message code="this.required" var="required" />
							<spring:message code="userrole.name.length" var="length" />
							<form:input type="text" path="roleName" id="idRoleName" data-validation="required length alphanumeric" readonly="${ice}" data-validation-allowing=" _-" data-validation-length="5-64"
								data-validation-error-msg-length="${length}" data-validation-error-msg-required="${required}" class="form-control" placeholder="${place}"></form:input>
							<form:errors path="roleName" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<spring:message code="rolename.description" var="descplace" />
							<form:label path="roleDescription" cssClass="marg-top-10">
								<spring:message code="application.description" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:textarea path="roleDescription" data-validation="length" data-validation-length="0-350" cssClass="form-control" id="idRoleDescription" placeholder="${descplace}" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select" id="idStatus">
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<h3 class="blue_form_sbtitle p_t20">
						<spring:message code="userrole.access" />
					</h3>
					<div id="acl-error-dialog"></div>
<!--start of checkbox code-->
<div class="chk_scroll_box acc-1">
    <div class="scroll_box_inner industry pad-top-bottom-15 tree-multiselect">
        <ul id="search_ul" class="tree animated-search-filter search_ul">
            <c:forEach items="${userRoleAccess}" var="sc">
                <li>
                    <span class="nvigator">
                        <i class="
                            <c:if test="${not empty sc.children}">fa fa-minus</c:if>
                            <c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"> </i>
                    </span>
                    <form:checkbox path="accessControlList" value="${sc.aclValue}" id="checkbox_${sc.aclValue}" class="first" data-validation="checkbox_group" data-validation-qty="min1"
                        data-validation-error-msg-container="#acl-error-dialog" />
                    <span class="number tree_heading">${sc.aclName}</span>

                    <!-- Conditional Message -->
                    <p id="message_ROLE_PROC_TO_PAY" class="hide-message">Your message here</p>

                    <c:if test="${not empty sc.children}">
                        <c:forEach items="${sc.children}" var="child">
                            <ul>
                                <li>
                                    <span class="nvigator">
                                        <i class="
                                            <c:if test="${not empty child.children}">fa fa-minus</c:if>
                                            <c:if test="${empty child.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
                                    </span>
                                    <form:checkbox path="accessControlList" value="${child.aclValue}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#acl-error-dialog" />
                                    <span class="number">${child.aclName}</span>

                                    <c:if test="${not empty child.children}">
                                        <c:forEach items="${child.children}" var="subChild">
                                            <ul>
                                                <li>
                                                    <span class="nvigator">
                                                        <i class="
                                                            <c:if test="${not empty subChild.children}">fa fa-minus</c:if>
                                                            <c:if test="${empty subChild.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
                                                    </span>
                                                    <form:checkbox path="accessControlList" value="${subChild.aclValue}" />
                                                    <span class="number">${subChild.aclName}</span>

                                                    <c:if test="${not empty subChild.children}">
                                                        <c:forEach items="${subChild.children}" var="subSubChild">
                                                            <ul>
                                                                <li>
                                                                    <span class="nvigator">
                                                                        <i class="
                                                                            <c:if test="${not empty subSubChild.children}">fa fa-minus</c:if>
                                                                            <c:if test="${empty subSubChild.children}">fa fa-plus</c:if>"
                                                                            aria-hidden="true"></i>
                                                                    </span>
                                                                    <form:checkbox path="accessControlList" value="${subSubChild.aclValue}" />
                                                                    <span class="number">${subSubChild.aclName}</span>

                                                                    <c:if test="${not empty subSubChild.children}">
                                                                        <c:forEach items="${subSubChild.children}" var="subSubSubChild">
                                                                            <ul>
                                                                                <li>
                                                                                    <span class="nvigator">
                                                                                        <i class="
                                                                                            <c:if test="${not empty subSubSubChild.children}">fa fa-minus</c:if>
                                                                                            <c:if test="${empty subSubSubChild.children}">fa fa-plus</c:if>"
                                                                                            aria-hidden="true"></i>
                                                                                    </span>
                                                                                    <form:checkbox path="accessControlList" value="${subSubSubChild.aclValue}" />
                                                                                    <span class="number">${subSubSubChild.aclName}</span>
                                                                                </li>
                                                                            </ul>
                                                                        </c:forEach>
                                                                    </c:if>
                                                                </li>
                                                            </ul>
                                                        </c:forEach>
                                                    </c:if>
                                                </li>
                                            </ul>
                                        </c:forEach>
                                    </c:if>
                                </li>
                            </ul>
                        </c:forEach>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
<!--end of checkbox code-->
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<form:button type="submit" id="saveUserRole" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}">${btnValue}</form:button>
							<c:url value="/admin/listRole" var="cancelUrl" />
							<a href="${cancelUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open1">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<style>
div[id^="uniform-"], div[id^="uniform-"] span, div[id^="uniform-"] input
	{
	cursor: pointer;
	display: block;
	float: left;
	height: 13px;
	line-height: 18px;
	margin: 1px 3px 0 0;
	padding: 0;
	width: 13px;
}

div[id^="uniform-"] span.checked i {
	display: block;
	font-size: 10px;
	height: 13px;
	line-height: 11px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/userRole.js?1"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
	
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
	
</script>
<script type="text/javascript">
    var userHasSupplierRole = false;
    var userHasBuyerRole = false;

    <sec:authorize access="hasRole('SUPPLIER')">
        userHasSupplierRole = true;
    </sec:authorize>

    <sec:authorize access="hasRole('BUYER')">
        userHasBuyerRole = true;
    </sec:authorize>
</script>
