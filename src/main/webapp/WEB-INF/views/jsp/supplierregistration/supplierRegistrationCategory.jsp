
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<form:form class="bordered-row form-horizontal" id="demo-form2" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
	
	<div class="content-box">
		<h3 class="content-box-header">
			Category <small class="sub_text">As an Administrator, you may view and edit information freely.</small>
		</h3>
		<div class="content-box-wrapper">
			<section class="step_2_content">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<h3 class="blue_form_sbtitle p_t20"><spring:message code="application.industry" /></h3>
				<div class="sub_txt_step2"><spring:message code="supplier.registration.confirm.contact" /></div>
				<div class="row">
					<div class="col-xs-12 col-sm-12 col-md-12">
						<div class="input-group search_box_gray">
							<input id="search_textbox" type="text" class="form-control animated-search-filter search_textbox searchListCheck" data-from="" data-relclass="industryCategoriesListBox" data-inpname="naicsCodes"> <span class="input-group-btn">
								<button type="button" class="btn btn-gray"></button>
							</span>
						</div>
						<div id="naicsCodes-error-dialog"></div>
						<div class="chk_scroll_box acc-1">
							<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
								<div class="leftSideOfCheckbox">
								<div class ="leftSideOfCheckbox1">
									<ul id="search_ul" class="tree animated-search-filter search_ul industryCategoriesListBox">
										<c:forEach items="${categories}" var="sc">
											<li><span class="nvigator" data-id="${sc.id}" data-level="${sc.level}"> <i class="
														<c:if test="${not empty sc.children}">fa fa-minus</c:if>
														<c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"> </i>
											</span> <form:checkbox path="naicsCodes" value="${sc.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#naicsCodes-error-dialog" /> <span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span> <c:if
													test="${not empty sc.children}">
													<!-- AND SHOULD CHECK HERE -->
													<ul>
														<c:forEach items="${sc.children}" var="child">
															<li><span class="nvigator" data-id="${child.id}" data-level="${child.level}"> <i class="<c:if test="${not empty sc.children}">fa fa-minus</c:if>
																				<c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
															</span> <form:checkbox path="naicsCodes" value="${child.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#naicsCodes-error-dialog" /> <span class="number">${child.categoryCode} - ${child.categoryName}</span> <!-- SHOULD CHECK HERE -->
																<c:if test="${not empty child.children}">
																	<!-- AND SHOULD CHECK HERE -->
																	<ul>
																		<c:forEach items="${child.children}" var="subChild">
																			<li><span class="nvigator" data-id="${subChild.id}" data-level="${subChild.level}"> <i class="<c:if test="${not empty sc.children}">fa fa-minus</c:if>
																									<c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
																			</span> <form:checkbox path="naicsCodes" value="${subChild.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#naicsCodes-error-dialog" /> <span class="number">${subChild.categoryCode} - ${subChild.categoryName}</span> <c:if
																					test="${not empty subChild.children}">
																					<!-- AND SHOULD CHECK HERE -->
																					<ul>
																						<c:forEach items="${subChild.children}" var="subSubChild">
																							<li><span class="nvigator" data-id="${subSubChild.id}" data-level="${subSubChild.level}"> <i class="<c:if test="${not empty sc.children}">fa fa-minus</c:if>
																													<c:if test="${empty sc.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
																							</span> <form:checkbox path="naicsCodes" value="${subSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#naicsCodes-error-dialog" /> <span class="number">${subSubChild.categoryCode} - ${subSubChild.categoryName}</span> <c:if
																									test="${not empty subSubChild.children}">
																									<!-- AND SHOULD CHECK HERE -->
																									<ul>
																										<c:forEach items="${subSubChild.children}" var="subSubSubChild">
																											<li><span class="nvigator" data-id="${subSubSubChild.id}" data-level="${subSubSubChild.level}"> <i class="<c:if test="${not empty sc.children}">fa fa-minus</c:if>
																																	<c:if test="${empty sc.children}">fa fa-plus</c:if>"
																													aria-hidden="true"></i>
																											</span> <form:checkbox path="naicsCodes" value="${subSubSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#naicsCodes-error-dialog" /> <span class="number">${subSubSubChild.categoryCode} -
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
								</div>
								<div class="rightSideOfCheckbox"></div>
							</div>
						</div>
					</div>
					<div class="col-xs-12 col-sm-12 col-md-12">
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
								<div class ="leftSideOfCheckbox2">
									<ul class="tree animated-search-filter1 search_ul_1 coverCountryStateList" id="tree">
										<c:forEach items="${coverageCountry}" var="country">
											<li><span class="nvigator-place"> <i class="
															<c:if test="${not empty country.children}">fa fa-minus</c:if>
															<c:if test="${empty country.children}">fa fa-plus</c:if>" aria-hidden="true"> </i>
											</span> <form:checkbox  path="coverages" value="${country.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#coverages-error-dialog" /> <span class="number tree_heading">${country.name}</span> <c:if
													test="${not empty country.children}">
													<!-- AND SHOULD CHECK HERE -->
													<c:forEach items="${country.children}" var="state">
														<ul>
															<li><span class="nvigator-place"> <i class="fa fa-minus" aria-hidden="true"></i>
															</span> <form:checkbox path="coverages" value="${state.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#coverages-error-dialog" /> <span class="number">${state.name}</span></li>
														</ul>
													</c:forEach>
												</c:if></li>
										</c:forEach>
									</ul>
									</div>
								</div>
								<div class="rightSideOfCheckbox"></div>
							</div>
						</div>
						<div class="step_button_pan">
							<button type="button" id="idBtnPrevious2" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open11">Back</button>
							<button type="button" id="idBtnNext2" class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium btn-lg button-next open3">Next</button>
						</div>
					</div>
				</div>
			</section>
		</div>
	</div>
</form:form>