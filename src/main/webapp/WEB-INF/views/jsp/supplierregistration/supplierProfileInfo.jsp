<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<style>
	.font-size{ 
	font-size:11px
	}
	</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="supplierProfileDetails.jsp" />

			<div class="tab-main-inner pad_all_15">
				<div id="collapseOne" class="panel-collapse collapse ${flag == true ? '' : 'in'}">
					<form:form class="bordered-row form-horizontal" id="demo-form1" data-parsley-validate="" method="post" modelAttribute="supplier" action="${pageContext.request.contextPath}/supplier/supplierProfileInfo">
						<form:hidden path="id" id="supplierId"/>
						<div class="content-box">
							<h3 class="content-box-header">
								<spring:message code="application.generalinfo" /> <small class="sub_text"><spring:message code="supplier.registration.company.administrator" /></small>
							</h3>
							<div class="content-box-wrapper">
								<div class="form-horizontal">
									<h3 class="blue_form_sbtitle"><spring:message code="supplier.registration.label" /></h3>
									<div class="form-group">
										<label for="idCompanyName" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.name" /></label> 
										<div class="col-sm-6 col-md-5 disabled">
											<form:input path="companyName" cssClass="form-control" id="idCompanyName" placeholder="" data-validation="required length alphanumeric company_name" 
											data-validation-allowing=",-_ &.()\/" 
											data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_ &.()\/ and spaces"
											data-validation-length="4-124"/>
<%-- 											<label class="font-size" >*<spring:message code="supplierprofile.companyname.note1" /></label> --%>
										</div>
										<c:if test="${supplier.formerCompanyName != null}">
											<div class="col-sm-4" >(formerly known as ${supplier.formerCompanyName})</div>
										</c:if>
									</div>
									<div class="form-group">
                                        <label for="idCompRegNum" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.number" /> :</label>
                                        <div class="col-sm-6 col-md-5 disabled">
                                        	<form:input path="companyRegistrationNumber" cssClass="form-control" id="idCompRegNum" placeholder="" data-validation="required length alphanumeric crn_number" 
                                        	 data-validation-allowing="&.,/()_- " 
                                        	 data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_&.()/ and spaces"
                                        	 data-validation-length="2-124"/>
                                    		<label class="font-size">*<spring:message code="supplierprofile.compnamenNdRegNo.note3" /></label>
                                    	</div>
                                    	<c:if test="${supplier.formerRegistrationNumber != null}">
	                                   		<div class="col-sm-4">(formerly registered as ${supplier.formerRegistrationNumber})</div>
                                    	</c:if>
                                    </div>
									<div class="form-group">
										<label class="col-sm-3 control-label"> <spring:message code="supplier.registration.company.established" /></label>
										<div class="col-sm-6 col-md-5">
											<form:input path="yearOfEstablished" cssClass="form-control" id="idYearEst" data-validation="required length number year_established" data-validation-length="min4-max4" />
										</div>
									</div>
									<div class="form-group">
										<label for="idTelPhone" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.phone" /></label>
										<div class="col-sm-6 col-md-5">
											<form:input type="tel" path="companyContactNumber" cssClass="form-control" id="idTelPhone" placeholder="" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
										</div>
									</div>
									<div class="form-group">
										<label for="idFax" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.fax" /></label>
										<div class="col-sm-6 col-md-5">
											<form:input path="faxNumber" cssClass="form-control" name="faxm" id="idFax" placeholder="" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
										</div>
									</div>
									 <div class="form-group">
										<label for="idAdMoNo" class="col-sm-3 control-label"><spring:message code="suplier.primaryMobileNo" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="mobileNumber" cssClass="form-control" id="idAdMoNo" placeholder="" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
										</div>
									</div>
									<div class="form-group">
										<label for="idCompanyWebsite" class="col-sm-3 control-label"><spring:message code="supplier.company.website" /> :</label>
										<div class="col-sm-6 col-md-5">
											<spring:message code="supplierprofile.website.placeholder" var="site"/>
											<form:input path="companyWebsite" cssClass="form-control" id="idCompanyWebsite" placeholder="${site}" data-validation="domain length" data-validation-length="0-128" data-validation-optional="true" />
										</div>
									</div>
									<div class="form-group">
										<label for="idCompanyEmail" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.email" /></label>
										<div class="col-sm-6 col-md-5">
											<form:input path="communicationEmail" cssClass="form-control" id="idCompanyEmail" placeholder="" data-validation="required length email" data-validation-length="max128" />
										</div>
									</div>
									 <div class="form-group">
										<label for="idCompanyStatus" class="col-sm-3 control-label"><spring:message code="label.companystatus" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:select path="companyStatus" id="idCompanyStatus" cssClass="chosen-select" data-validation="required">
												<form:option value=""><spring:message code="supplier.registration.company.select" /></form:option>
												<form:options items="${companyStatusList}" itemLabel="companystatus" itemValue="id"></form:options>
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<label for="taxRegistrationNumber" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.tax.reg.no" /></label>
										<div class="col-sm-6 col-md-5">
											<form:input path="taxRegistrationNumber" cssClass="form-control" id="idtaxRegistrationNumber" placeholder="" data-validation="length custom" data-validation-length="0-17" data-validation-regexp="^[A-Za-z0-9-\/]{0,17}$"/>
										</div>
									</div>
									<h3 class="blue_form_sbtitle p_t20"><spring:message code="supplier.registration.company.address" /></h3>
									<div class="form-group">
										<label for="idAdressOne" class="col-sm-3 control-label"><spring:message code="supplierprofile.adds.line1" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="line1" cssClass="form-control" id="idAdressOne" placeholder="" data-validation="required length" data-validation-allowing="- " data-validation-length="2-250" />
										</div>
									</div>
									<div class="form-group">
										<label for="idAdressTwo" class="col-sm-3 control-label"><spring:message code="supplierprofile.adds.line2" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="line2" cssClass="form-control" id="idAdressTwo" placeholder="" data-validation="required length" data-validation-allowing="- " data-validation-length="2-250" />
										</div>
									</div>
									<div class="form-group">
										<label for="idCityTwon" class="col-sm-3 control-label"><spring:message code="application.city" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="city" cssClass="form-control" id="idCityTwon" placeholder="" data-validation="required custom" data-validation-regexp="^([a-zA-Z\\-\\s]+){2,200}$" />
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label"> <spring:message code="suplier.postalCode" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="postalCode" cssClass="form-control" id="idposCode" data-validation="required length alphanumeric" data-validation-allowing="\ " data-validation-length="2-15" />
										</div>
									</div>
									<div class="form-group">
										<label for="idRegCountry" class="col-sm-3 control-label"><spring:message code="application.country1" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:input path="registrationOfCountry" cssClass="form-control" id="idRegCountry" placeholder="" readonly="true" />
											<form:input path="registrationOfCountry.id" type="hidden" cssClass="form-control" id="regCountryId" placeholder="" readonly="true" />
										</div>
									</div>
									<div class="form-group">
										<label for="idState" class="col-sm-3 control-label"><spring:message code="application.state" /> :</label>
										<div class="col-sm-6 col-md-5">
											<form:select path="state" class="chosen-select" id="idState" data-validation="required">
												<form:option value=""><spring:message code="application.select.state" /></form:option>
												<form:options items="${states}" itemValue="id" itemLabel="stateName"></form:options>
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-3 col-sm-6 col-md-5">
											<form:button type="submit" value="save" id="saveProfile" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out "><spring:message code="application.update" /></form:button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
	
	</script>