<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="supplierProfileDetails.jsp" />
			<div class="tab-main-inner pad_all_15">
				<form:form class="bordered-row form-horizontal" id="demo-form2" data-parsley-validate="" method="post" modelAttribute="supplier" action="${pageContext.request.contextPath}/supplier/supplierProductCategory">
					<input type="hidden" id="supplierStep" value="2">
					<form:hidden path="id" />
					<input type="hidden" id="projId" value="">
					<div class="content-box">
						<h3 class="content-box-header">
							<spring:message code="application.catagory" /> <small class="sub_text"><spring:message code="supplier.registration.company.administrator" /></small>
						</h3>
						<div class="content-box-wrapper">
							<h3 class="blue_form_sbtitle p_t20"><spring:message code="application.industry" /></h3>
							<spring:message code="supplier.registration.confirm.contact" />
						</div>
						<div class="row tab-dummy">
							<div class="col-xs-2 col-sm-2 col-md-2"></div>
							<div class="col-xs-8 col-sm-8 col-md-8">
								<div class="input-group search_box_gray">
									<input id="search_textbox" type="text" class="form-control animated-search-filter search_textbox searchListCheck" data-from="" data-relclass="industryCategoriesListBox" data-inpname="naicsCodes"> <span class="input-group-btn">
										<button type="button" class="btn btn-gray"></button>
									</span>
								</div>
								<div id="serviceIndustry-error-dialog"></div>
								<div class="chk_scroll_box acc-1">
									<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
										<div class="leftSideOfCheckbox">
											<ul id="search_ul" class="tree animated-search-filter search_ul industryCategoriesListBox">
												<c:forEach items="${categories}" var="sc">
													<li><span class="nvigator" data-id="${sc.id}" data-level="${sc.level}"> <i class=" 
																					<c:if test="${not empty sc.children}">fa fa-minus</c:if>
																					<c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"> </i>
													</span> <form:checkbox path="naicsCodes" value="${sc.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span> <c:if
															test="${not empty sc.children}">
															<!-- AND SHOULD CHECK HERE -->
															<ul>
																<c:forEach items="${sc.children}" var="child">
																	<li><span class="nvigator" data-id="${child.id}" data-level="${child.level}"> <i class="<c:if test="${empty child.children}">fa fa-plus</c:if><c:if test="${not empty child.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																	</span> <form:checkbox path="naicsCodes" value="${child.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${child.categoryCode} - ${child.categoryName}</span> <!-- SHOULD CHECK HERE -->
																		<c:if test="${not empty child.children}">
																			<!-- AND SHOULD CHECK HERE -->
																			<ul>
																				<c:forEach items="${child.children}" var="subChild">
																					<li><span class="nvigator" data-id="${subChild.id}" data-level="${subChild.level}"> <i class="<c:if test="${empty subChild.children}">fa fa-plus</c:if><c:if test="${not empty subChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																					</span> <form:checkbox path="naicsCodes" value="${subChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${subChild.categoryCode} - ${subChild.categoryName}</span> <c:if
																							test="${not empty subChild.children}">
																							<!-- AND SHOULD CHECK HERE -->
																							<ul>
																								<c:forEach items="${subChild.children}" var="subSubChild">
																									<li><span class="nvigator" data-id="${subSubChild.id}" data-level="${subSubChild.level}"> <i class="<c:if test="${empty subSubChild.children}">fa fa-plus</c:if><c:if test="${not empty subSubChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																									</span> <form:checkbox path="naicsCodes" value="${subSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${subSubChild.categoryCode} - ${subSubChild.categoryName}</span> <c:if
																											test="${not empty subSubChild.children}">
																											<!-- AND SHOULD CHECK HERE -->
																											<ul>
																												<c:forEach items="${subSubChild.children}" var="subSubSubChild">
																													<li><span class="nvigator" data-id="${subSubSubChild.id}" data-level="${subSubSubChild.level}"> <i class="fa fa-minus" aria-hidden="true"></i>
																													</span> <form:checkbox path="naicsCodes" value="${subSubSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${subSubSubChild.categoryCode} -
																															${subSubSubChild.categoryName}</span></li>
																												</c:forEach>
																											</ul>
																										</c:if></li>
																								</c:forEach>
																							</ul>
																						</c:if></li>
																				</c:forEach>
																			</ul>
																		</c:if></li>
																</c:forEach>
															</ul>
														</c:if></li>
												</c:forEach>
											</ul>
										</div>
										<div class="rightSideOfCheckbox"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-xs-2 col-sm-2 col-md-2"></div>
							<div class="col-xs-8 col-sm-8 pad_all_10 col-md-8">
								<!-- All regian Block -->
								<h3 class="blue_form_sbtitle p_t20"><spring:message code="application.geographical" /></h3>
								<div class="input-group search_box_gray">
									<input type="text" class="form-control animated-search-filter1 search_textbox_1 searchListCheckCountry" data-from="" data-relclass="coverCountryStateList" data-inpname="coverages"> <span class="input-group-btn">
										<button type="button" class="btn btn-gray"></button>
									</span>
								</div>
								<div id="coverages-error-dialog"></div>
								<div class="chk_scroll_box  acc-2">
									<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
										<div class="leftSideOfCheckbox">
											<ul class="tree animated-search-filter1 search_ul_1 coverCountryStateList" id="tree">
												<c:forEach items="${coverageCountry}" var="country">
													<li><span class="nvigator-place"> <i class="
																					<c:if test="${not empty country.children}">fa fa-minus</c:if>
																					<c:if test="${empty country.children}">fa fa-plus</c:if>" aria-hidden="true"> </i>
													</span> <form:checkbox path="coverages" value="${country.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#coverages-error-dialog" /> <span class="number tree_heading">${country.name}</span> <c:if
															test="${not empty country.children}">
															<!-- AND SHOULD CHECK HERE -->
															<c:forEach items="${country.children}" var="state">
																<ul>
																	<li><span class="nvigator-place"> <i class="fa fa-plus" aria-hidden="true"></i>
																	</span> <form:checkbox path="coverages" value="${state.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#coverages-error-dialog" /> <span class="number">${state.name}</span></li>
																</ul>
															</c:forEach>
														</c:if></li>
												</c:forEach>
											</ul>
										</div>
										<div class="rightSideOfCheckbox"></div>
									</div>
								</div>
								<div class="step_button_pan">
									<form:button type="submit" value="save" id="saveProductCategory" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out "><spring:message code="application.update" /></form:button>
									<!-- <button type="button" id="idBtnPrevious2" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open11">Update</button> -->
								</div>
							</div>
						</div>
					</div>
				</form:form>

			</div>
		</div>
	</div>
</div>

<%-- in header already included  <script src="<c:url value="/resources/js/view/supplierProfile.js"/>"></script> --%>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>
