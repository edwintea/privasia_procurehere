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
			<%-- <div id="collapseFour"
	class="panel-collapse collapse ${flag == true ? 'in' : ''}"> --%>
			<div class="tab-main-inner pad_all_15">
				<div class="content-box">
					<h3 class="content-box-header">
						<spring:message code="application.service" /> <small class="sub_text"><spring:message code="supplier.registration.company.administrator" /></small>
					</h3>
					<form:form class="bordered-row form-horizontal" id="track-form1" data-parsley-validate="" method="get" modelAttribute="supplier" action="${pageContext.request.contextPath}/supplier/addSupplierProfileNewTrack">
						<!-- <div class="content-box"> -->
						<form:hidden path="id" />
						<div class="content-box-wrapper">
							<div class="row">
								<div class="col-xs-12 col-sm-12">
									<section class="last_step_left_block trackRecordDataTable">
										<p><spring:message code="supplier.registration.confirm.services" /></p>
										<div class="form-group">
											<spring:message code="supplierprofile.heigh.level.description.placeholder" var="highdesc"/>
											<form:textarea path="supplierTrackDesc" class="form-control" name="textarea1" rows="4" id="idServicesOffered" placeholder="${highdesc}"></form:textarea>
										</div>
									</section>
								</div>
								<%-- <div class="step_button_pan">
							<form:button type="submit" class="btn btn-primary ph_btn_midium  hvr-pop hvr-rectangle-out " style="float:right;" id="idBtnFin">Update</form:button>
							</div> --%>
								<div class="col-xs-12 col-sm-12">
									<section class="last_step_right_block">
										<div class="row">
											<div class="col-xs-1 col-md-1"></div>
											<div class="col-sx-10 col-sm-6">
												<div class="input-group search_box_gray">
													<input type="text" class="form-control" id="searchTrackRecord" name="searchTrackRecord">
													<span class="input-group-btn">
														<button class="btn btn-gray" type="button"></button>
													</span>
												</div>
											</div>
											<div class="col-sx-1 col-sm-1">
<%--												<c:url var="addSupplierProfile" value="/supplier/addSupplierProfileNewTrack" />--%>
												<a href="javascript:void(0);" id="idTrackrecord001" class="btn btn-primary ph_btn_midium  hvr-pop hvr-rectangle-out" onclick="submitTrackForm()"><spring:message code="application.addnewrecord" /></a>
											</div>
										</div>
										<div class="row">
											<div class="col-xs-1 col-md-1"></div>
											<div class="col-xs-6 col-md-6">
												<div class="last_step_table step_table mega ">
													<table class="table header table-admin">
														<thead>
															<tr>
																<th class="width_50"><spring:message code="supplier.registration.track.year" /></th>
																<th class="width_200"><spring:message code="supplier.registration.track.project" /></th>
																<th class="width_150 width_150_fix" style="padding-left: 1%;"><spring:message code="supplier.registration.track.contract" /></th>
																<th class="width_50 width_50_fix align-center" style="color: transparent;"><spring:message code="application.remove" /></th>
															</tr>
														</thead>
													</table>
													<!-- <table class="table  table-admin" id="addProjectTrackRecord"> -->
													<table class="data for-pad-data" id="addProjectTrackRecord">
														<tbody>
															<c:forEach items="${projects}" var="supProj">
																<tr>
																	<td class="width_50 ">${supProj.year}</td>
																	<c:url var="updateTrackRecord" value="/supplier/updateSupplierProfileTrackRecord" />
																	<td class="width_200 ">
																		<%-- <a href="#" editid="${supProj.id}"> --%>
																		<a href="${pageContext.request.contextPath}/supplier/updateSupplierProfileTrackRecord/${supProj.id}">${supProj.projectName} </a>
																		<input type="hidden" id="editId" value="${supProj.id}" />
																	</td>
																	<td class="numeric width_150 width_150_fix">${supProj.contactValue}</td>
																	<td class="width_50 width_50_fix align-center">
																		<a href="${pageContext.request.contextPath}/supplier/removeSupplierProfileTrackRecord/${supProj.id}">
																			<i class="fa fa-trash-o" aria-hidden="true"></i>
																		</a>
																	</td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
											<div class="col-xs-1 col-md-1"></div>
										</div>
									</section>
								</div>
								<div class="clear"></div>
								<div class="col-xs-12 col-sm-6 clearfix"></div>
							</div>
						</div>
					</form:form>
				</div>
				<div id="extrFrm" class=" content-box  collapse ${flag == true ? 'in' : ''}">
					<h3 class="content-box-header">
						<spring:message code="application.service" /> <small class="sub_text"><spring:message code="supplier.registration.company.administrator" /></small>
					</h3>
					<c:url var="editSupplierTrackRecord" value="/supplier/editSupplierTrackRecord" />
					<form:form cssClass="form-horizontal bordered-row" id="demo-form2" data-parsley-validate="" modelAttribute="supplierProject" action="${editSupplierTrackRecord}" method="post">
						<input type="hidden" id="supplierStep" value="2">
						<form:hidden path="id" id="projId" name="projId" />
						<form:hidden path="supplier.id" id="id" name="id" />
						<input type="hidden" id="trackDet" value="${flag}">
						<div class="content-box-wrapper">
							<div class="form-horizontal">
								<h3 class="blue_form_sbtitle"><spring:message code="supplier.registration.project.info" /></h3>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.name" /></label>
									<div class="col-sm-6 col-md-5">
										<spring:message code="suppliers.project.name.placeholder" var="projname"/>
										<form:input type="text" class="form-control" path="projectName" placeholder="${projname}" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="10-250" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.client" /></label>
									<div class="col-sm-6 col-md-5">
										<spring:message code="supplierprofile.client.name" var="clientname"/>
										<form:input class="form-control" path="clientName" placeholder="${clientname}" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="1-200" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.complete" /></label>
									<div class="col-sm-6 col-md-5">
										<spring:message code="supplierprofile.year.established" var="yearestablished"/>
										<form:input path="year" cssClass="form-control" id="idYearE" placeholder="${yearestablished}" data-validation="required length number year_comp" data-validation-length="min4-max4" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.currency" /></label>
									<div class="col-sm-6 col-md-5">
										<form:select path="currency" id="idCurrency" cssClass="chosen-select" data-validation="required">
											<form:option value=""><spring:message code="currency.select" /> </form:option>
											<form:options items="${currency}" itemValue="id" itemLabel="currencyName" />
										</form:select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.contract" /></label>
									<div class="col-sm-6 col-md-5">
										<spring:message code="supplier.registration.track.contract.plac" var="trackcontract"/>
										<form:input class="form-control" path="contactValue" id="" placeholder="${trackcontract}" data-validation="required length " data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" data-validation-length="max16" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label"><spring:message code="supplier.registration.project.email" /></label>
									<div class="col-sm-6 col-md-5">
										<spring:message code="supplierprofile.client.email.adds" var="clientemail"/>
										<form:input type="text" class="form-control" path="clientEmail" id="" placeholder="${clientemail}" data-validation="required length email" data-validation-length="max160" />
									</div>
								</div>
							</div>
							<h3 class="blue_form_sbtitle p_t20"><spring:message code="application.industry" /></h3>
							<!-- 	<div class="sub_txt_step2"> -->
							<spring:message code="supplier.registration.confirm.contact" />
						</div>
						<div class="row tab-dummy">
							<div class="col-xs-2 col-sm-2 col-md-2"></div>
							<div class="col-xs-8 col-sm-8 col-md-8">
								<div class="input-group search_box_gray">
									<input id="search_textbox" type="text" class="form-control animated-search-filter search_textbox searchListCheck" data-from="" data-relclass="industryCategoriesListBox" data-inpname="projectIndustries">
									<span class="input-group-btn">
										<button type="button" class="btn btn-gray"></button>
									</span>
								</div>
								<%-- <div class="input-group search_box_gray">
									<input type="text" class="form-control searchListCheck" data-from="" data-relclass="projectIndustryList" data-inpname="projectIndustries"> <span class="input-group-btn"> <form:button type="button" class="btn btn-gray"></form:button>
									</span>
								</div> --%>
								<div id="serviceIndustry-error-dialog"></div>
								<div class="chk_scroll_box">
									<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
										<div class="leftSideOfCheckbox">
											<ul id="search_ul" class="tree animated-search-filter search_ul industryCategoriesListBox">
												<!-- <ul class="tree projectIndustryList" id="tree"> -->
												<c:forEach items="${projectCategories}" var="sc">
													<li><span class="nvigator" data-id="${sc.id}" data-level="${sc.level}"> <i class="<c:if test="${empty sc.children}">fa fa-plus</c:if><c:if test="${not empty sc.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
													</span> <form:checkbox path="projectIndustries" value="${sc.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span> <c:if test="${not empty sc.children}">
															<!-- AND SHOULD CHECK HERE -->
															<ul>
																<c:forEach items="${sc.children}" var="child">
																	<li><span class="nvigator" data-id="${child.id}" data-level="${child.level}"> <i class="<c:if test="${empty child.children}">fa fa-plus</c:if><c:if test="${not empty child.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																	</span> <form:checkbox path="projectIndustries" value="${child.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${sc.categoryCode} - ${child.categoryName}</span> <!-- SHOULD CHECK HERE --> <c:if test="${not empty child.children}">
																			<!-- AND SHOULD CHECK HERE -->
																			<ul>
																				<c:forEach items="${child.children}" var="subChild">
																					<li><span class="nvigator" data-id="${subChild.id}" data-level="${subChild.level}"> <i class="<c:if test="${empty subChild.children}">fa fa-plus</c:if><c:if test="${not empty subChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																					</span> <form:checkbox path="projectIndustries" value="${subChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${sc.categoryCode} - ${subChild.categoryName}</span> <c:if test="${not empty subChild.children}">
																							<!-- AND SHOULD CHECK HERE -->
																							<ul>
																								<c:forEach items="${subChild.children}" var="subSubChild">
																									<li><span class="nvigator" data-id="${subSubChild.id}" data-level="${subSubChild.level}"> <i class="<c:if test="${empty subSubChild.children}">fa fa-plus</c:if><c:if test="${not empty subSubChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																									</span> <form:checkbox path="projectIndustries" value="${subSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${subSubChild.categoryCode} - ${subSubChild.categoryName}</span> <c:if
																											test="${not empty subSubChild.children}">
																											<!-- AND SHOULD CHECK HERE -->
																											<ul>
																												<c:forEach items="${subSubChild.children}" var="subSubSubChild">
																													<li><span class="nvigator" data-id="${subSubSubChild.id}" data-level="${subSubSubChild.level}"> <i class="fa fa-minus" aria-hidden="true"></i>
																													</span> <form:checkbox path="projectIndustries" value="${subSubSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#serviceIndustry-error-dialog" /> <span class="number">${subSubSubChild.categoryCode} - ${subSubSubChild.categoryName}</span></li>
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
									<input type="text" class="form-control animated-search-filter1 search_textbox_1 searchListCheckCountry" data-from="" data-relclass="coverCountryStateList" data-inpname="tracRecordCoverages">
									<span class="input-group-btn">
										<button type="button" class="btn btn-gray"></button>
									</span>
								</div>
								<%-- <div class="input-group search_box_gray">
									<input type="text" class="form-control searchListCheckCountry" data-from="" data-relclass="projectCountyList" data-inpname="tracRecordCoverages"> <span class="input-group-btn"> <form:button class="btn btn-gray" type="button"></form:button>
									</span>
								</div> --%>
								<div id="tracRecordCoverages-error-dialog"></div>
								<div class="chk_scroll_box">
									<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
										<div class="leftSideOfCheckbox">
											<!-- <ul class="tree projectCountyList" id="tree"> -->
											<ul class="tree animated-search-filter1 search_ul_1 coverCountryStateList" id="tree">
												<c:forEach items="${registeredTrackCountry}" var="country">
													<li><span class="nvigator-place"> <i class="<c:if test="${empty country.children}">fa fa-plus</c:if><c:if test="${not empty country.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
													</span> <form:checkbox path="tracRecordCoverages" value="${country.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#tracRecordCoverages-error-dialog" /> <span class="number tree_heading">${country.name}</span> <c:if test="${not empty country.children}">
															<!-- AND SHOULD CHECK HERE -->
															<c:forEach items="${country.children}" var="state">
																<ul>
																	<li><span class="nvigator-place"> <i class="fa fa-minus" aria-hidden="true"></i>
																	</span> <form:checkbox path="tracRecordCoverages" value="${state.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#tracRecordCoverages-error-dialog" /> <span class="number">${state.name}</span></li>
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
									<c:if test="${empty supplierProject.id}">
										<form:button type="submit" class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium btn-lg button-next" id="projectAddSupplier"><spring:message code="application.save" /></form:button>
									</c:if>
									<c:if test="${not empty supplierProject.id}">
										<form:button type="submit" class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium btn-lg button-next" id="projectUpdateSupplier"><spring:message code="application.update" /></form:button>
									</c:if>
									<form:button type="button" data-toggle="collapse" data-target="#extrFrm" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous "><spring:message code="application.cancel" /> </form:button>
								</div>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script src="<c:url value="/resources/js/view/supplierProfile.js"/>"></script>
<script>
	$.formUtils
			.addValidator({
				name : 'year_established',
				validatorFunction : function(value, $el, config, language,
						$form) {
					var response = true;
					var currentYear = new Date().getFullYear();
					console.log(currentYear);
					if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
						response = false;
					}
					return response;
				},
				errorMessage : 'Year established is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
				errorMessageKey : 'badYearEstablished'
			});

	$.formUtils
			.addValidator({
				name : 'year_comp',
				validatorFunction : function(value, $el, config, language,
						$form) {
					var response = true;
					var currentYear = new Date().getFullYear();
					console.log(currentYear);
					if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
						response = false;
					}
					return response;
				},
				errorMessage : 'Year completion is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
				errorMessageKey : 'badYearCompletion'
			});

	$(document).ready(function() {
		$('#idFax').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"
		});
		$('#idTelPhone').mask('+00 00000000000', {
			placeholder : "e.g. +60 322761533"
		});
		$('#idYearEst').mask('0000', {
			placeholder : "e.g. 1989"
		});
		$('#idYearE').mask('0000', {
			placeholder : "e.g. 1989"
		});
		//$('#idCompanyWebsite').mask('http://www.company.com', {placeholder: "http://www.company.com"});
	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	function submitTrackForm() {
		$('#track-form1').submit();
	}
</script>