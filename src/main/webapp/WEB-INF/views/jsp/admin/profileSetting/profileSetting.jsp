<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('USER_ROLE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('BUYER')">
<spring:message var="userProfileDesk" code="application.user.profile" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${userProfileDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('SUPPLIER')">
<spring:message var="supplierUserProfileDesk" code="application.supplier.user.profile" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierUserProfileDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('OWNER')">
	<spring:message var="ownerUserProfileDesk"
		code="application.owner.user.profile" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${ownerUserProfileDesk}] });
});
</script>
</sec:authorize>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/${dashboard}">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active"><spring:message code="profilesetting.label" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap profile_icon"><spring:message code="profilesetting.label" /></h2>
			</div>
			<div class="clear"></div>
			<form:form class="form-horizontal" action="profileSetting?${_csrf.parameterName}=${_csrf.token}" id="idProfileSetting" commandName="userObj" method="post" enctype="multipart/form-data">
				<div class="Invited-Supplier-List create_sub marg-bottom-20 default-setting profile-box">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="row pad_all_15 profileSettingPage">
						<form:hidden path="password" value="${userObj.password}" id="abcPassed" />
						<div class="col-md-4 col-xs-6 text-center">
							<div class="profile_edit">
								<c:if test="${empty profPic}">
									<img id="profileImageHolder" class="profile_picture profile_picture_edit" src="${pageContext.request.contextPath}/resources/images/profile_setting_image.png" alt="profile setting" onclick="$('#prfPic').click()" />
									<a href="javascript:" onclick="$('#prfPic').click()"><spring:message code="profilesetting.change.profile.photo" /></a>
								</c:if>
								<c:if test="${not empty profPic}">
									<img id="profileImageHolder" class="profile_picture" src="data:image/jpeg;base64,${profPic}" alt="profile setting" onclick="$('#prfPic').click()" />
									<a href="javascript:" onclick="$('#prfPic').click()"><spring:message code="profilesetting.change.profile.photo" /></a>
								</c:if>
								<form:input type="file" accept=".jpg,.jpeg,.png" onchange="validateFileNameExtension()" style="visibility: hidden" name="prfPic" id="prfPic" path="" />
							</div>
							<div class="col-md-12 pad0">
								<input class="btn btn-info btnprofile hvr-pop hvr-rectangle-out changePass" type="button" value='<spring:message code="profilesetting.change.password" />'>
							</div>
						</div>
						<div class="col-md-6 col-xs-6 profile_setting">
							<div class="form-group pad-b15">
								<form:label path="loginId" class="col-md-12 col-xs-12 pad-left-0"><spring:message code="application.loginid" /></form:label>
								<div class=" file-input pad0">
									<spring:message code="profilesetting.login.emailid.placeholder" var="loginid" />
									<form:input path="loginId" readonly="true" data-validation="required length" data-validation-length="5-50" data-validation-error-msg-length="Login id values must be between 5 to 50." id="idLoginId" class="form-control"
										placeholder="${loginid}"></form:input>
								</div>
							</div>
							<div class="form-group pad-b15">
								<form:label path="name" class="col-md-12 col-xs-12 pad-left-0"><spring:message code="application.name" /></form:label>
								<div class=" file-input pad0">
									
									<spring:message code="profilesetting.enter.name.placeholder" var="name" />
									<form:input path="name" data-validation="required length" data-validation-length="4-160" cssClass="form-control" data-validation-error-msg-length="User name value must be between 4 to 160." id="idName" placeholder="${name}" />
									<form:errors path="name" cssClass="error" />
								</div>
							</div>
							<div class="form-group pad-b15">
								<form:label path="designation" class="col-md-12 col-xs-12 pad-left-0"><spring:message code="application.designation" /></form:label>
								<div class=" file-input pad0">
									<spring:message code="profilesetting.operation.executive.placeholder" var="operationexec"/>
									<form:input path="designation" data-validation="required,length,alphanumeric" data-validation-length="1-64" cssClass="form-control" data-validation-allowing="- " id="idDsgn" placeholder="${operationexec}" />
									<form:errors path="designation" cssClass="error" />
								</div>
							</div>
							<div class="form-group pad-b15">
								<form:label path="phoneNumber" class="col-md-12 col-xs-12 pad-left-0"><spring:message code="application.contact2" /></form:label>
								<div class=" file-input pad0">
									<spring:message code="profilesetting.contact.number.placeholder" var="contact" />
									<form:input path="phoneNumber" data-validation="length number" data-validation-optional="true" data-validation-ignore="+ " data-validation-length="6-14" cssClass="form-control" data-validation-allowing="- " id="idPhNo" placeholder="${contact}" />
									<form:errors path="phoneNumber" cssClass="error" />
								</div>
							</div>
							<div class="form-group pad-b15">
								<form:label path="communicationEmail" class="col-md-12 col-xs-12 pad-left-0"><spring:message code="buyer.profilesetup.commemail" /></form:label>
								<div class=" file-input pad0">
									<spring:message code="profilesetting.email.placeholder" var="email" />
									<spring:message code="supplier.profilesetup.commemail" var="emailPlaceH"/>
									<form:input path="communicationEmail" data-validation="required,length, email" data-validation-length="1-64" cssClass="form-control" data-validation-allowing="- " id="idEmail" placeholder="${emailPlaceH}" />
									<form:errors path="communicationEmail" cssClass="error" />
								</div>
							</div>
							<div class="updateprofile form-group pad-b15">
								<button class="btn btn-info btnprofile hvr-pop hvr-rectangle-out" type="submit"><spring:message code="profilesetting.update.profile" /></button>
							</div>
						</div>
					</div>
					<div class="divider"></div>
					<div class="row pad_all_15">
						<label class="col-md-2 col-sm-2"><spring:message code="application.role" /> :</label>
						<div class="col-md-4 col-sm-6 line-height">${userObj.userRole.roleName}</div>
					</div>
					<sec:authorize access="hasRole('BUYER')">
						<div class="row pad_all_15">
							<label class="col-md-2 col-sm-2"><spring:message code="profilesetting.assigned.rfx.templates" /> :</label>
							<div class="col-md-4 col-sm-6 line-height">
								<c:forEach items="${userObj.assignedTemplates}" var="rfxTmp">
							 ${rfxTmp.templateName}<br />
								</c:forEach>
							</div>
						</div>
						<div class="row pad_all_15">
							<label class="col-md-2 col-sm-2"><spring:message code="profilesetting.assigned.pr.templates" /> :</label>
							<div class="col-md-4 col-sm-6 line-height">
								<c:forEach items="${userObj.assignedPrTemplates}" var="prTmp">
							 ${prTmp.templateName}<br />
								</c:forEach>
							</div>
						</div>
					</sec:authorize>
				</div>
			</form:form>
			<!--  ********         FOR CHANGE pASSWORD BOOTSTARP MODEL    *************** -->
		</div>
	</div>
</div>
<div id="changePasswordModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width: 90%; max-width: 800px;">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3 class="modal-title text-center"><spring:message code="profilesetting.change.password" /></h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<form:form class="form-horizontal" action="changeUserPassword" id="idChangePass" commandName="passwordObj" method="post">
				<div class="modal-body">
					<div class="alert alert-danger" id="idDialogError" style="display: none">
						<div class="bg-red alert-icon">
							<i class="glyph-icon icon-times"></i>
						</div>
						<div class="alert-content">
							<h4 class="alert-title">Error</h4>
							<p id="idDialogErrorMessage">
								Information message box using the
								<code>.alert-danger</code>
								color scheme.
								<a title="Link" href="#">Link</a>
							</p>
						</div>
					</div>
					<input type="hidden" name="id" id="id" value="" />
					<div class="row d-flex">
						<div class="col-md-4 margin-10-top">
							<label><spring:message code="changepassword.old" /></label>
						</div>
						<div class="col-md-8">
							<spring:message code="changepassword.custom" var="custom" />
							<spring:message code="application.old.password.placeholder" var="capital" />
							<form:input path="oldPassword" type="password" class="form-control marg-bottom-10" id="oldPass" data-validation="required length" data-validation-length="max15" placeholder="${capital}" />
						</div>
					</div>
					<div class="row d-flex">
						<div class="col-md-4 margin-10-top">
							<label><spring:message code="changepassword.new" /></label>
						</div>
						<div class="col-md-8">
							<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
							<form:input path="newPassword" type="password" class="form-control marg-bottom-10 pwd" id="newPass" data-validation="required custom length" data-validation-length="max64" placeholder="Enter new password" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-custom="${!empty regex.message? regex.message :''}" />
<%-- 							<p id="passwordPlaceHolder">${!empty regex.message? regex.message :''}</p> --%>
						</div>
					</div>
					<div class="row d-flex">
						<div class="col-md-4 margin-10-top">
							<label>Confirm Password</label>
						</div>
						<div class="col-md-8">
							
							<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
							<form:input path="confirmPassword" type="password" class="form-control marg-bottom-10" id="newPass2" data-validation="required custom length" data-validation-length="max64" placeholder="Enter Confirm password" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-custom="${!empty regex.message? regex.message :''}" />
						</div>
						<input type="hidden" id="regex" value="${!empty regex.message? regex.message :''}">
					</div>
				</div>
				<div class="modal-footer  text-center">
					<input class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium changeThePass" id="changeThePass" value="Change Password" type="button" />
					<button id="cancelChangePass" class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium" type="button" onclick="javascript:$('#idChangePass').get(0).reset();" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form:form>
		</div>
	</div>
</div>

<style>
.d-flex {
	display: flex;
	align-items: center;
}

.margin-10-top{
     margin-top: 14px;   
}

/*  .margin-10-top{ */
/*        margin-bottom: 15px; */
/*  }  */
  
@media ( min-width : 768px) and (max-width: 1366px) {
	.modal-dialog {
		width: 550px !important;
	}
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/profileSetting.js"/>"></script>
<script type="text/javascript">
	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});
	
	});
</script>
<script>
	$(function() {
		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({
			source : availableTags
		});
		$("#tagres").autocomplete({
			source : availableTags
		});
	});
</script>
<%-- <script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
</script> --%>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	function validateFileNameExtension() {
		var fileInput = document.getElementById('prfPic');
		var filePath = fileInput.value;
		var allowedExtensions = /(\.jpg|\.jpeg|\.png)$/i;

		if (!allowedExtensions.exec(filePath)) {
			alert('Invalid file format. Only JPG, PNG, and JPEG files are allowed.');
			fileInput.value = '';
			return false;
		}
		return true;
	}
</script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>