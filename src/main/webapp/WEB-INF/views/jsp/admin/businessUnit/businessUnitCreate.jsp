<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('BUSINESS_UNIT_LIST') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="businessUnitDesk" code="application.buyer.business.unit" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${businessUnitDesk}] });
});
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li>
				<a id="dashboardLink" href="${dashboardUrl}">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/buyer/listBusinessUnit" var="listUrl" />
			<li>
				<a id="listLink" href="${listUrl}">
					<spring:message code="businessUnit.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="label.businessUnit" />
			</li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap line_icon">
				<spring:message code="label.businessUnit" />
			</h2>
		</div>
		<div class="col_12 graph">
			<div class="white_box_brd pad_all_15">
				<section class="index_table_block">
					<div class="row">
						<div class="col-xs-12 ">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="Invited-Supplier-List import-supplier white-bg">
								<div class="meeting2-heading">
									<h3>
										<c:out value='${btnValue}' />
										<spring:message code="label.businessUnit" />
									</h3>
								</div>
								<div class="import-supplier-inner-first-new pad_all_15 global-list">
									<form:form cssClass="form-horizontal" action="businessUnit?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="businessUnit" id="idBusinessUnit" enctype="multipart/form-data">
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="unitName" cssClass="marg-top-10">
													<spring:message code="label.businessUnitName" />
												</form:label>
											</div>
											<div class="col-md-5">
												<form:hidden path="id" id ="id" name="id" />
												<form:hidden path="budgetCheckOld" />
												<form:hidden path="spmIntegrationOld" />
												<spring:message code="bu.enter.unitname.placeholder" var="unitnameplace" />
												<form:input path="unitName" type="text" data-validation-length="1-60" data-validation="required length" cssClass="form-control" placeholder="${unitnameplace}" />
											</div>
										</div>
										
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<label class="marg-top-10">
													<spring:message code="systemsetting.bu.unitcode" />
												</label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.unitcode.placeholder" var="unitcodeplace" />
												<form:input path="unitCode" id="unitCode" type="text" data-validation-length="1-12" data-validation="${codeRequerd ? 'required' :'' }  length"  data-validation-optional="true" cssClass="form-control" placeholder="${unitcodeplace}" />
											</div>
										</div>
										
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="displayName" cssClass="marg-top-10">
													<spring:message code="label.businessDisplayName" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.displayname.placeholder" var="displaynameplace" />
												<form:input path="displayName" type="text" data-validation-length="1-60" data-validation="required length" cssClass="form-control" placeholder="${displaynameplace}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="status" cssClass="marg-top-10">
													<spring:message code="label.parentBusinessUnit" />
												</form:label>
											</div>
											<div class="col-md-5">
												<form:select path="parent" cssClass="form-control chosen-select" id="idParent">
													<form:option value=""><spring:message code="pr.select.business.unit"/></form:option>
													<form:options items="${parentList}" itemValue="id" itemLabel="unitName" />
												</form:select>
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line1" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line1" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line1.placeholder" var="buline1"/>
												<form:input path="line1" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline1}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line2" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line2" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line2.placeholder" var="buline2"/>
												<form:input path="line2" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline2}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line3" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line3" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line3.placeholder" var="buline3"/>
												<form:input path="line3" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline3}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line4" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line4" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line4.placeholder" var="buline4"/>
												<form:input path="line4" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline4}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line5" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line5" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line5.placeholder" var="buline5"/>
												<form:input path="line5" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline5}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line6" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line6" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line6.placeholder" var="buline6"/>
												<form:input path="line6" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline6}" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="line7" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.line7" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="bu.enter.line7.placeholder" var="buline7"/>
												<form:input path="line7" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control" placeholder="${buline7}" />
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<label Class="marg-top-10"> <spring:message code="systemsetting.bu.logo" /> </label>
											</div>
											<div class="col-md-5">
												<div class="profile">
													<c:if test="${empty logoImg}">
														<img id="logoImageHolder" src="${pageContext.request.contextPath}/resources/images/logo-image.png" alt="Logo" onclick="$('#logoImg').click()" />
														<div class="col-md-8">
															<a href="javascript:" onclick="$('#logoImg').click()" ><spring:message code="systemsetting.bu.upload.logo" /></a>
														</div>
														<div class="col-md-4">
															<a href="javascript:" id="removeLogo" ><spring:message code="systemsetting.bu.remove.logo" /></a>
														</div>
													</c:if>
													<c:if test="${not empty logoImg}">
														<img id="logoImageHolder" src="data:image/jpeg;base64,${logoImg}" alt="Logo" onclick="$('#logoImg').click()" />
														<div class="col-md-8">
															<a href="javascript:" onclick="$('#logoImg').click()" ><spring:message code="systemsetting.bu.upload.logo" /></a>
														</div>
														<div class="col-md-4">
															<a href="javascript:" id="removeLogo" ><spring:message code="systemsetting.bu.remove.logo" /></a>
														</div>
														</c:if>
													<form:input type="file" accept="image/*" style="visibility: hidden" name="logoImg" id="logoImg" path="" />
													<input type="hidden" id="removeFile" name="removeFile" value="false">
												</div>

											</div>
										</div>
										<input type="hidden" id="recursive" path="recursive" value="FALSE" name="recursive" >
										<input type="hidden" id="spmIntegrationRecursive" path="spmIntegrationRecursive" value="FALSE" name="spmIntegrationRecursive" > 
										
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="status" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.budget.check" />
												</form:label>
											</div>
											<div class="col-md-5 marg-top-10">
												<form:checkbox id="idBudgetCheck" value="budgetCheck" path="budgetCheck" class="custom-checkbox" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="status" cssClass="marg-top-10">
													<spring:message code="systemsetting.bu.spm.integration" />
												</form:label>
											</div>
											<div class="col-md-5 marg-top-10">
												<form:checkbox id="idSpmIntegrationCheck" value="spmIntegration" path="spmIntegration" class="custom-checkbox" />
											</div>
										</div>										
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="status" cssClass="marg-top-10">
													<spring:message code="application.status" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="application.status.required" var="required" />
												<form:select path="status" cssClass="form-control chosen-select" id="idStatus" data-validation="required" data-validation-error-msg-required="${required}">
													<form:options items="${statusList}" />
												</form:select>
											</div>
										</div>
										
										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											<div class="meeting2-heading">
												<h3>
													<spring:message code="label.budget.costCenter" />
												</h3>
											</div>
											<div class="import-supplier-inner-first-new pad_all_15 global-list">
												<div class="row">
													<div class="col-sm-4 col-md-3">
														<label class="marg-top-10"><spring:message code="assign.cost.center" /></label>
													</div>
													<div class="col-md-4 col-sm-6 chang-width">
														<div class="input-group search_box_gray disp-flex">
															<select class="chosen-select cost-list" id="selectedCostList" selected-id="data-value">
																<option value=""><spring:message code="placeholder.search.cost.center" /></option>
																<c:forEach items="${costCenterList}" var="costList">
																	<c:if test="${costList.id == '-1'}">
																		<option value="-1" disabled>${costList.costCenter} - ${costList.description}</option>
																	</c:if>
																	<c:if test="${costList.id != '-1' }">
																		<option value="${costList.id}">${costList.costCenter} - ${costList.description}</option>
																	</c:if>
																</c:forEach>
															</select> <span class="col-md-2 col-sm-2">
																<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreCost">
																	<i class="fa fa-plus" aria-hidden="true"></i>
																</button>
															</span>
														</div>
														<div class="error costError" hidden>
															<spring:message code="please.select.cost" />
														</div>
														<div class="error costErrorDisp" hidden>
															<spring:message code="please.assign.cost.center" />
														</div>
														<c:if test="${count > 0}">
														<div class="error inactiveCostErrorDisp">
															<spring:message code="please.remove.inactive.cost.center" />
														</div>
														</c:if>
													</div>
												</div>
					
												<div class="clear"></div>
												<div class="container-fluid">
													<div class="row">
													
													<div class="col_12" style="margin-top:20px;">
										              <div class="white_box_brd pad_all_15">
											           <section class="index_table_block">
											                <div class="row">
												              <div class="col-xs-12">
													           <div class="ph_tabel_wrapper scrolableTable_UserList">
														        <table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
															     <thead>
																   <tr class="tableHeaderWithSearch">
																	<th search-type=""><spring:message code="application.action" /></th>
																	<th search-type="text"><spring:message code="label.costcenter" /></th>
																   </tr>
															     </thead>
														       </table>
													          </div>
													         <div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
												           </div>
											              </div>
											            </section>
										               </div>
									                 </div>
									                 <input type="hidden" name="costId" id="costId"/>
													</div>
												</div>
											</div>
										</div>

										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											<div class="meeting2-heading">
												<h3>
													<spring:message code="label.groupCode" />
												</h3>
											</div>
											<div class="import-supplier-inner-first-new pad_all_15 global-list">
												<div class="row">
													<div class="col-sm-4 col-md-3">
														<label class="marg-top-10"><spring:message code="assign.group.Code" /></label>
													</div>
													<div class="col-md-4 col-sm-6 chang-width">
														<div class="input-group search_box_gray disp-flex">
															<select class="chosen-select grc-list" id="selectedGrCList" selected-id="data-value">
																<option value=""><spring:message code="placeholder.search.group.Code" /></option>
																<c:forEach items="${groupCodeList}" var="grc">
																	<c:if test="${grc.id == '-1'}">
																		<option value="-1" disabled>${grc.groupCode} - ${grc.description}</option>
																	</c:if>
																	<c:if test="${grc.id != '-1' }">
																		<option value="${grc.id}">${grc.groupCode} - ${grc.description}</option>
																	</c:if>
																</c:forEach>
															</select> 
															<span class="col-md-2 col-sm-2">
																<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreGrpCd">
																	<i class="fa fa-plus" aria-hidden="true"></i>
																</button>
															</span>
														</div>
														<div class="error grcError" hidden>
															<spring:message code="please.select.group.Code" />
														</div>
															<div class="error grcErrorDisp" hidden>
																<spring:message code="please.assign.group.Code" />
															</div>
														<c:if test="${grcCount > 0}">
															<div class="error inactiveGrcErrorDisp">
																<spring:message code="please.remove.inactive.group.Code" />
															</div>
														</c:if>
													</div>
												</div>
												<div class="clear"></div>
												<div class="container-fluid">
													<div class="row">
														<div class="col_12" style="margin-top: 20px;">
															<div class="white_box_brd pad_all_15">
																<section class="index_table_block">
																	<div class="row">
																		<div class="col-xs-12">
																			<div class="ph_tabel_wrapper scrolableTable_UserList">
																				<table id="groupCodetableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
																					<thead>
																						<tr class="tableHeaderWithSearch">
																							<th search-type=""><spring:message code="application.action" /></th>
																							<th search-type="text"><spring:message code="label.groupCode" /></th>
																						</tr>
																					</thead>
																				</table>
																			</div>
																			<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
																		</div>
																	</div>
																</section>
															</div>
														</div>
														<input type="hidden" name="grcId" id="grcId" />
													</div>
												</div>
											</div>
										</div>

										<!-- Assign Agreement Type -->
										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											<div class="meeting2-heading">
												<h3>
													<spring:message code="label.agreement.type" />
												</h3>
											</div>
											<div class="import-supplier-inner-first-new pad_all_15 global-list">
												<div class="row">
													<div class="col-sm-4 col-md-3">
														<label class="marg-top-10"><spring:message code="assign.agreement.type" /></label>
													</div>
													<div class="col-md-4 col-sm-6 chang-width">
														<div class="input-group search_box_gray disp-flex">
															<select class="chosen-select agrTyp-list" id="selectedAgrTypList" selected-id="data-value">
																<option value=""><spring:message code="placeholder.search.agreement.type" /></option>
																<c:forEach items="${agreementTypeList}" var="agrTyp">
																	<c:if test="${agrTyp.id == '-1'}">
																		<option value="-1" disabled>${agrTyp.agreementType} - ${agrTyp.description}</option>
																	</c:if>
																	<c:if test="${agrTyp.id != '-1' }">
																		<option value="${agrTyp.id}">${agrTyp.agreementType} - ${agrTyp.description}</option>
																	</c:if>
																</c:forEach>
															</select> 
															<span class="col-md-2 col-sm-2">
																<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreAgrTyp">
																	<i class="fa fa-plus" aria-hidden="true"></i>
																</button>
															</span>
														</div>
														<div class="error agrTypError" hidden>
															<spring:message code="please.select.agreement.type" />
														</div>
														<c:if test="${agrTypCount > 0}">
															<div class="error inactiveAgrTypErrorDisp">
																<spring:message code="please.remove.inactive.agreement.type" />
															</div>
														</c:if>
													</div>
												</div>
												<div class="clear"></div>
												<div class="container-fluid">
													<div class="row">
														<div class="col_12" style="margin-top: 20px;">
															<div class="white_box_brd pad_all_15">
																<section class="index_table_block">
																	<div class="row">
																		<div class="col-xs-12">
																			<div class="ph_tabel_wrapper scrolableTable_UserList">
																				<table id="agreementTypetableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
																					<thead>
																						<tr class="tableHeaderWithSearch">
																							<th search-type=""><spring:message code="application.action" /></th>
																							<th search-type="text"><spring:message code="label.agreement.type" /></th>
																						</tr>
																					</thead>
																				</table>
																			</div>
																			<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
																		</div>
																	</div>
																</section>
															</div>
														</div>
														<input type="hidden" name="agrTypId" id="agrTypId" />
													</div>
												</div>
											</div>
										</div>

										<div class="clear"></div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<label class="marg-top-10"></label>
											</div>
											<div class="col-md-9 dd sky mar_b_15">
												<input type="button" value="${btnValue}" id="saveBusinessUnit" class=" ${ !canEdit ? 'disabled' : '' } btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">
												<c:url value="/buyer/listBusinessUnit" var="listUrl" />
												<a href="${listUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
													<spring:message code="application.cancel" />
												</a>
											</div>
										</div>
									</form:form>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="removeTemplateUserListPopupEnable" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="tooltip.remove.bussiness.user" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body body-font">
				<label><spring:message code="bussinessUnit.confirm.budget" />
				<br/>
				<br/>
				<spring:message code="bussinessUnit.enable.budget" /> ?</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			    <button id="yesUnit" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out widthset-100 setbackground">
					<spring:message code="bussiness.yes" />
				</button>
				<button id="noUnit" type="button" class="widthset-100 btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="bussiness.no" />
				</button>
			</div>
		</div>
	</div>
</div>


<div class="modal fade" id="removeSpmIntegrationPopupEnable" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="tooltip.remove.bussiness.user" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body body-font">
				<label><spring:message code="bussinessUnit.confirm.spm.integration" />
				<br/>
				<br/>
				<spring:message code="bussinessUnit.enable.spm.integration" /> ?</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			    <button id="yesSpmIntegration" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out widthset-100 setbackground">
					<spring:message code="bussiness.yes" />
				</button>
				<button id="noSpmIntegration" type="button" class="widthset-100 btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="bussiness.no" />
				</button>
			</div>
		</div>
	</div>
</div>

<!--For Disable-->

<div class="modal fade" id="removeTemplateUserListPopupDisable" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="tooltip.remove.bussiness.user" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body body-font">
				<label>
					<spring:message code="bussinessUnit.confirm.budget" />
					<br/>
					<br/>
					<spring:message code="bussinessUnit.disable.budget" /> ?
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			    <button id="yesUnit" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out widthset-100 setbackground">
					<spring:message code="bussiness.yes" />
				</button>
				<button id="noUnit" type="button" class="widthset-100 btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="bussiness.no" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="removeSpmIntegrationPopupDisable" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="tooltip.remove.bussiness.user" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body body-font">
				<label>
					<spring:message code="bussinessUnit.confirm.spm.integration" />
					<br/>
					<br/>
					<spring:message code="bussinessUnit.disable.spm.integration" /> ?
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			    <button id="yesSpmIntegration" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out widthset-100 setbackground">
					<spring:message code="bussiness.yes" />
				</button>
				<button id="noSpmIntegration" type="button" class="widthset-100 btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="bussiness.no" />
				</button>
			</div>
		</div>
	</div>
</div>

<!--Delete Cost Center pop up -->
<div class="flagvisibility dialogBox" id="removeCostCenterPopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="unit.confirm.to.remove.cost" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteCost" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!--Delete Group Code pop up -->
<div class="flagvisibility dialogBox" id="removeGroupCodePopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="unit.confirm.to.remove.gpc" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteGpc" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<!--Delete Agreement Type pop up -->
<div class="flagvisibility dialogBox" id="removeAgreementTypePopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="unit.confirm.to.remove.agrTyp" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteAgrTyp" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<!-- <style>
.profile_padding {
padding: 5px 11px 8px !important;
}
</style> -->
<script type="text/javascript">

$('document').ready(function(){

	var costCenterTable;
	var selectedCostCenter = [];
	var removeCostCenter = [];
	var selectedCostCenterIds=[];
	var removeCostCenterIds=[];
	var updatedCostCenterIds=[];

	$('document').ready(function(){
		costCenterTable=$('#tableList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"preDrawCallback" : function(settings){
					$('#loading').show();
					return true;
			},
			"drawCallback":function(){
					$('#loading').hide();
			},
			"ajax":{
				"url":getContextPath()+"/buyer/costCenterListData",
				"data":function(d){
					d.id=$('#id').val();
					d.costCenterIds = selectedCostCenter;
					d.removeIds = removeCostCenter;
				},
				"error":function(request,textStatus,errorThrown){
					var error=request.getResponseHeader('error');
					if(error!=undefined){
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order":[],
			"columns":[{
					"mData":"id",
					"searchable":false,
					"orderable":false,
					"mRender":function(data,type,row){
						var tImg="";
						var ret='';
						tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
						ret += '<a href="#myModal" class="deleteCostCenter" id="' + row.id + '" data-value="' + row.id + '" onClick="javascript:void(0);" title="Delete" role="button"  data-toggle="modal">' + tImg +'</a>';
						return ret;
					}
				},{
					"mData":"costCenter",
					"searchable" : true,
					"orderable" : true,
					"mRender" : function(data, type, row) {
						if(row.description == undefined){
				    		return row.costCenter;
				    	}else {
				            return row.costCenter + " - " + row.description;
				    	}
					},
					"className" : "align-left"
				}]
		});
		
		 var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(costCenterTable.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				costCenterTable.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(costCenterTable.table().container()).on('change', 'thead select', function() {
			costCenterTable.column($(this).data('index')).search(this.value).draw();
		}); 
		
	$('#addMoreCost').on('click', function (e) {
	 	$('.costErrorDisp').attr('hidden','');
		e.preventDefault();
		var selectedId=$("#selectedCostList option:selected").val();
	 	if(selectedId.length > 0){
	 		selectedCostCenterIds.push(selectedId.toString().trim());
	 		updatedCostCenterIds.push(selectedId.toString().trim());
	 		selectedCostCenter=selectedCostCenterIds.toString();
	 		$('#selectedCostList').find('option[value="' + selectedId + '"]').remove();
			$('#selectedCostList').trigger("chosen:updated");
			$('.costError').attr('hidden','');
			for(var i in updatedCostCenterIds){
				if(removeCostCenterIds.includes(updatedCostCenterIds[i])){
					var index = removeCostCenterIds.indexOf(updatedCostCenterIds[i]);
					removeCostCenterIds.splice(index, 1);
				}
			}
			removeCostCenter=removeCostCenterIds.toString();
		 	costCenterTable.ajax.reload();
		 	$('#costId').val('');
		 	$('#costId').val(updatedCostCenterIds);
	 	} else {
	 		$('.costError').removeAttr('hidden');	
	 	}
	 });
});
	
	 <c:forEach items="${assignedCostList}" var="cost">
	 updatedCostCenterIds.push('${cost.id}');
	$('#costId').val('');
	$('#costId').val(updatedCostCenterIds);
	</c:forEach>
	
  $(document).delegate('.deleteCostCenter', 'click', function(e) {
	        text2= $(this).parent().next('td').text();
	        id2=$(this).data("value");
			$("#removeCostCenterPopup").dialog({
				modal : true,
				maxWidth : 400,
				minHeight : 100,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded2"
			});
			$('.ui-widget-overlay').addClass('bg-white opacity-60');
			$('.ui-dialog-title').addClass('title-ellipsis');
			$('.title-ellipsis').text('Remove Cost Center');
		});

	 $(document).delegate('#deleteCost', 'click', function(e){
			e.preventDefault();
		    var id=id2;
	 		var text = text2;
	 		removeCostCenterIds.push(id.toString().trim());
	 		removeCostCenter=removeCostCenterIds.toString();
			$("#removeCostCenterPopup").dialog('close');
			var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
 			$('#selectedCostList option:selected').after(newRow);
 		    $('#selectedCostList').trigger("chosen:updated");
		 	costCenterTable.ajax.reload();
		 	var index = updatedCostCenterIds.indexOf(id);
		    if (index !== -1) updatedCostCenterIds.splice(index, 1);
		    var index = selectedCostCenterIds.indexOf(id);
		    if (index !== -1) selectedCostCenterIds.splice(index, 1);
		    $('#costId').val(updatedCostCenterIds);
		});
});
</script>
<script>
	//for image select
	var costList2=[];
	var costId2=[];
	$(document).ready(function() {
		$("#image-holder").on('click', '.thumb-image', function() {
			$(this).toggleClass("selectedItem");
		});

		$("#selectedItem").on("click", function() {
			$(".selectedItem").remove();
		});

		$("#logoImg").on('change', function() {
			if (typeof (FileReader) == null) {
				var image_holder = document.getElementById("logoImageHolder").src;
				console.log("=====");
				image_holder.attr('src', getContextPath() + '/resources/images/logo-image.png');
			} else if (typeof (FileReader) != "undefined") {
				$("#removeFile").val(false);
				var image_holder = $("#logoImageHolder");
				image_holder.empty();
				var reader = new FileReader();
				reader.onload = function(e) {
					image_holder.attr('src', e.target.result);
				}
				//console.log("=====");
				image_holder.show();
				reader.readAsDataURL($(this)[0].files[0]);
			} else {
				//alert("This browser does not support FileReader.");
			}
		});

		$("#removeLogo").click(function() {
			$("#logoImg").val("");
			$("#removeFile").val(true);
			$('#logoImageHolder').attr('src', getContextPath() + '/resources/images/logo-image.png')
		});
		
		/* $(document).delegate('.cost-list.chosen-select', "chosen:hiding_dropdown", function(e) {
			reloadcostCenterList('', $(this));
		}); */
		
		var groupCodeTable;
		var selectedGroupCode = [];
		var removeGroupCode = [];
		var selectedGroupCodeIds=[];
		var removeGroupCodeIds=[];
		var updatedGroupCodeIds=[];
		
		 <c:forEach items="${assignedGrcList}" var="grc">
			 updatedGroupCodeIds.push('${grc.id}');
			/* selectedGroupCodeIds.push('${grc.id}'); */
			$('#grcId').val('');
			$('#grcId').val(updatedGroupCodeIds);
		</c:forEach>

/* 		if(selectedGroupCodeIds != null && selectedGroupCode.length == 0){
			selectedGroupCode = selectedGroupCodeIds.toString();
		}	
 */		
		groupCodeTable=$('#groupCodetableList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"preDrawCallback" : function(settings){
					$('#loading').show();
					return true;
			},
			"drawCallback":function(){
					$('#loading').hide();
			},
			"ajax":{
				"url":getContextPath()+"/buyer/groupCodeBUListData",
				"data":function(d){
					d.id=$('#id').val();
					d.groupCodeIds = selectedGroupCode;
					d.removeIds = removeGroupCode;
				},
				"error":function(request,textStatus,errorThrown){
					var error=request.getResponseHeader('error');
					if(error!=undefined){
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order":[],
			"columns":[
				{
					"mData":"id",
					"searchable":false,
					"orderable":false,
					"mRender":function(data,type,row){
						var tImg="";
						var ret='';
						tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
						ret += '<a href="#myModal" class="deleteGroupCode" id="' + row.id + '" data-value="' + row.id + '" onClick="javascript:void(0);" title="Delete" role="button"  data-toggle="modal">' + tImg +'</a>';
						return ret;
					}
				},{
				    "mData": function (data, type, row) {
					    	if(data.description == undefined){
					    		return data.groupCode;
					    	}else {
					            return data.groupCode + " - " + data.description;
					    	}
				    },
					"className" : "align-left"
				}]
		});
		
		 var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#groupCodetableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		
		htmlSearch += '</tr>';
		$('#groupCodetableList thead').append(htmlSearch);
		$(groupCodeTable.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				groupCodeTable.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(groupCodeTable.table().container()).on('change', 'thead select', function() {
			groupCodeTable.column($(this).data('index')).search(this.value).draw();
		}); 
		
		
		$(document).delegate(".cost-list ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
			var selectElement = $(this).parents().eq(3).closest('div').find('select');
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37:
				return false;
				break;
			case 38:
				return false;
				break;
			case 39:
				return false;
				break;
			case 40:
				return false;
				break;
			}
			var costCent = $.trim(this.value);
			if (costCent.length > 2 || costCent.length == 0 || e.keyCode == 8) {
				reloadcostCenterList(costCent, selectElement);
			}
		}, 650));
		
		function keyDebounceDelay(callback, ms) {
			var timer = 0;
			return function() {
				var context = this, args = arguments;
				clearTimeout(timer);
				timer = setTimeout(function() {
					callback.apply(context, args);
				}, ms || 0);
			};
		}
		
		function reloadcostCenterList(costCent, selectElement) {
			var id=$('#id').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + '/buyer/searchMoreCostCenter',
				data : {
					'id' : id,
					'searchCost' : costCent,
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var html = '';

					if (data != '' && data != null && data.length > 0) {
						if (selectElement.attr('multiple') !== undefined) {
							console.log('Clearing non selected items...');
							selectElement.find('option').not(':selected').remove();
						} else {
							if (selectElement.find('option:first').val() === '') {
								console.log('Clearing all except first...');

								selectElement.find('option').each(function() {
									if (this.value == '' || this.value === selectElement.val()) {
										console.log(' Not Removing ', this);
									} else {
										this.remove();
									}
								});

								// selectElement.find('option:not(:first)').remove();
								// selectElement.find('option').not(':selected').remove();
							} else {
								console.log('Clearing all items...');
								selectElement.find('option').not(':selected').remove();
							}
						}
						$.each(data, function(key, value) {

							var selectedIds = selectElement.attr("selected-id");
							if (value.id == null || value.id == '') {
								if(value.description == undefined){
								html += '<option value="" disabled>' + value.costCenter + '</option>';
								}else{
									html += '<option value="" disabled>' + value.costCenter + ' - ' + value.description + '</option>';
								}
							} else if (value.id == '-1') {
								if(value.description == undefined){
								html += '<option value="-1" disabled>' + value.costCenter + '</option>';
								}else{
								html += '<option value="-1" disabled>' + value.costCenter + ' - ' + value.description + '</option>';
								}
							} else {
								if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
									var found = false;
									if (selectedIds !== undefined) {
										$('[' + selectedIds + ']').each(function(index) {
											if ($(this).attr(selectedIds) === value.id) {
												// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
												found = true;
												return false;
											}
										});
									}
									if (!found) {
										if(value.description == undefined){
										html += '<option value="' + value.id + '" data-name="' + value.costCenter + '">' + value.costCenter  + '</option>';
										}else{
										html += '<option value="' + value.id + '" data-name="' + value.costCenter + '">' + value.costCenter + ' - ' + value.description  + '</option>';	
										}
									}
								}
							}
						});
					}

					selectElement.append(html);
					selectElement.trigger("chosen:updated")
					$('#loading').hide();
				},
				error : function(error) {
					console.log(error);
					$('#loading').hide();
				}
			});
		}
		
		$(document).delegate(".grc-list ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
			var selectElement = $(this).parents().eq(3).closest('div').find('select');
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37:
				return false;
				break;
			case 38:
				return false;
				break;
			case 39:
				return false;
				break;
			case 40:
				return false;
				break;
			}
			var grCode = $.trim(this.value);
			if (grCode.length > 2 || grCode.length == 0 || e.keyCode == 8) {
				reloadGroupCodesList(grCode, selectElement);
			}
		}, 650));
		
		function reloadGroupCodesList(groupCode, selectElement) {
			var id=$('#id').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + '/buyer/searchMoreGroupCodes',
				data : {
					'id' : id,
					'groupCode' : groupCode,
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var html = '';

					if (data != '' && data != null && data.length > 0) {
						if (selectElement.attr('multiple') !== undefined) {
							console.log('Clearing non selected items...');
							selectElement.find('option').not(':selected').remove();
						} else {
							if (selectElement.find('option:first').val() === '') {
								console.log('Clearing all except first...');

								selectElement.find('option').each(function() {
									if (this.value == '' || this.value === selectElement.val()) {
										console.log(' Not Removing ', this);
									} else {
										this.remove();
									}
								});

							} else {
								console.log('Clearing all items...');
								selectElement.find('option').not(':selected').remove();
							}
						}
						$.each(data, function(key, value) {

							var selectedIds = selectElement.attr("selected-id");
							if (value.id == null || value.id == '') {
								if(value.description == undefined){
								html += '<option value="" disabled>' + value.groupCode + '</option>';
								}else{
									html += '<option value="" disabled>' + value.groupCode + ' - ' + value.description + '</option>';
								}
							} else if (value.id == '-1') {
								if(value.description == undefined){
								html += '<option value="-1" disabled>' + value.groupCode + '</option>';
								}else{
								html += '<option value="-1" disabled>' + value.groupCode + ' - ' + value.description + '</option>';
								}
							} else {
								if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
									var found = false;
									if (selectedIds !== undefined) {
										$('[' + selectedIds + ']').each(function(index) {
											if ($(this).attr(selectedIds) === value.id) {
												// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
												found = true;
												return false;
											}
										});
									}
									if (!found) {
										if(value.description == undefined){
										html += '<option value="' + value.id + '" data-name="' + value.groupCode + '">' + value.groupCode  + '</option>';
										}else{
										html += '<option value="' + value.id + '" data-name="' + value.groupCode + '">' + value.groupCode + ' - ' + value.description  + '</option>';	
										}
									}
								}
							}
						});
					}

					selectElement.append(html);
					selectElement.trigger("chosen:updated")
					$('#loading').hide();
				},
				error : function(error) {
					console.log(error);
					$('#loading').hide();
				}
			});
		}
		
		$('#addMoreGrpCd').on('click', function (e) {
		 	$('.grcErrorDisp').attr('hidden','');
			e.preventDefault();
			var selectedId=$("#selectedGrCList option:selected").val();
		 	if(selectedId.length > 0){
		 		selectedGroupCodeIds.push(selectedId.toString().trim());
		 		updatedGroupCodeIds.push(selectedId.toString().trim());
		 		selectedGroupCode= selectedGroupCodeIds.toString();
		 		$('#selectedGrCList').find('option[value="' + selectedId + '"]').remove();
				$('#selectedGrCList').trigger("chosen:updated");
				$('.grcError').attr('hidden','');
				for(var i in updatedGroupCodeIds){
					if(removeGroupCodeIds.includes(updatedGroupCodeIds[i])){
						var index = removeGroupCodeIds.indexOf(updatedGroupCodeIds[i]);
						removeGroupCodeIds.splice(index, 1);
					}
				}
				removeGroupCode=removeGroupCodeIds.toString();
				console.log("selectedGroupCodeIds " + selectedGroupCode + " removeGroupCode : " + removeGroupCode);
				groupCodeTable.ajax.reload();
			 	$('#grcId').val('');
			 	$('#grcId').val(updatedGroupCodeIds);
		 	} else {
		 		$('.grcError').removeAttr('hidden');	
		 	}
		 });

		  $(document).delegate('.deleteGroupCode', 'click', function(e) {
		        text2= $(this).parent().next('td').text();
		        id2=$(this).data("value");
				$("#removeGroupCodePopup").dialog({
					modal : true,
					maxWidth : 400,
					minHeight : 100,
					dialogClass : "",
					show : "fadeIn",
					draggable : true,
					resizable : false,
					dialogClass : "dialogBlockLoaded2"
				});
				$('.ui-widget-overlay').addClass('bg-white opacity-60');
				$('.ui-dialog-title').addClass('title-ellipsis');
				$('.title-ellipsis').text('Remove Group Code');
			});

		 $(document).delegate('#deleteGpc', 'click', function(e){
				e.preventDefault();
			    var id=id2;
		 		var text = text2;
		 		removeGroupCodeIds.push(id.toString().trim());
		 		removeGroupCode=removeGroupCodeIds.toString();
				$("#removeGroupCodePopup").dialog('close');
				var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
	 			$('#selectedGrCList option:selected').after(newRow);
	 		    $('#selectedGrCList').trigger("chosen:updated");
	 		  	groupCodeTable.ajax.reload();
			 	var index = updatedGroupCodeIds.indexOf(id);
			    if (index !== -1) updatedGroupCodeIds.splice(index, 1);
			    var index = selectedGroupCodeIds.indexOf(id);
			    if (index !== -1) selectedGroupCodeIds.splice(index, 1);
			    $('#grcId').val(updatedGroupCodeIds);
			});
		
	// AGREEMENT TYPE START 	
			var agreementTypeTable;
			var selectedAgreementType = [];
			var removeAgreementType = [];
			var selectedAgreementTypeIds=[];
			var removeAgreementTypeIds=[];
			var updatedAgreementTypeIds=[];
			
			 <c:forEach items="${assignedAgreementTypeList}" var="agt">
			 	updatedAgreementTypeIds.push('${agt.id}');
				$('#agrTypId').val('');
				$('#agrTypId').val(updatedAgreementTypeIds);
			</c:forEach>

	 		agreementTypeTable=$('#agreementTypetableList').DataTable({
				"processing" : true,
				"serverSide" : true,
				"pageLength" : 10,
				"searching" : true,
				"preDrawCallback" : function(settings){
						$('#loading').show();
						return true;
				},
				"drawCallback":function(){
						$('#loading').hide();
				},
				"ajax":{
					"url":getContextPath()+"/buyer/agreementTypeBUListData",
					"data":function(d){
						d.id=$('#id').val();
						d.agreementTypeIds = selectedAgreementType;
						d.removeIds = removeAgreementType;
					},
					"error":function(request,textStatus,errorThrown){
						var error=request.getResponseHeader('error');
						if(error!=undefined){
							$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
							$('div[id=idGlobalError]').show();
						}
						$('#loading').hide();
					}
				},
				"order":[],
				"columns":[
					{
						"mData":"id",
						"searchable":false,
						"orderable":false,
						"mRender":function(data,type,row){
							var tImg="";
							var ret='';
							tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
							ret += '<a href="#myModal" class="deleteAgreementType" id="' + row.id + '" data-value="' + row.id + '" onClick="javascript:void(0);" title="Delete" role="button"  data-toggle="modal">' + tImg +'</a>';
							return ret;
						}
					},{
					    "mData": function (data, type, row) {
						    	if(data.description == undefined){
						    		return data.agreementType;
						    	}else {
						            return data.agreementType + " - " + data.description;
						    	}
					    },
						"className" : "align-left"
					}]
			});
			
			 var htmlSearch = '<tr class="tableHeaderWithSearch">';
			$('#agreementTypetableList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
				}
			});
			
			htmlSearch += '</tr>';
			$('#agreementTypetableList thead').append(htmlSearch);
			$(agreementTypeTable.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					agreementTypeTable.column($(this).data('index')).search(this.value).draw();
				}
			});
			$(agreementTypeTable.table().container()).on('change', 'thead select', function() {
				agreementTypeTable.column($(this).data('index')).search(this.value).draw();
			}); 
			
			$(document).delegate(".agrTyp-list ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
				var selectElement = $(this).parents().eq(3).closest('div').find('select');
				// ignore arrow keys
				switch (e.keyCode) {
				case 17: // CTRL
					return false;
					break;
				case 18: // ALT
					return false;
					break;
				case 37:
					return false;
					break;
				case 38:
					return false;
					break;
				case 39:
					return false;
					break;
				case 40:
					return false;
					break;
				}
				var agrType = $.trim(this.value);
				if (agrType.length > 2 || agrType.length == 0 || e.keyCode == 8) {
					reloadAgreementTypeList(agrType, selectElement);
				}
			}, 650));
			
			function reloadAgreementTypeList(agreementType, selectElement) {
				var id=$('#id').val();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					url : getContextPath() + '/buyer/searchMoreAgreementType',
					data : {
						'id' : id,
						'agreementType' : agreementType,
					},
					type : 'POST',
					dataType : 'json',
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success : function(data) {
						var html = '';

						if (data != '' && data != null && data.length > 0) {
							if (selectElement.attr('multiple') !== undefined) {
								console.log('Clearing non selected items...');
								selectElement.find('option').not(':selected').remove();
							} else {
								if (selectElement.find('option:first').val() === '') {
									console.log('Clearing all except first...');

									selectElement.find('option').each(function() {
										if (this.value == '' || this.value === selectElement.val()) {
											console.log(' Not Removing ', this);
										} else {
											this.remove();
										}
									});

								} else {
									console.log('Clearing all items...');
									selectElement.find('option').not(':selected').remove();
								}
							}
							$.each(data, function(key, value) {

								var selectedIds = selectElement.attr("selected-id");
								if (value.id == null || value.id == '') {
									if(value.description == undefined){
									html += '<option value="" disabled>' + value.agreementType + '</option>';
									}else{
										html += '<option value="" disabled>' + value.agreementType + ' - ' + value.description + '</option>';
									}
								} else if (value.id == '-1') {
									if(value.description == undefined){
									html += '<option value="-1" disabled>' + value.agreementType + '</option>';
									}else{
									html += '<option value="-1" disabled>' + value.agreementType + ' - ' + value.description + '</option>';
									}
								} else {
									if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
										var found = false;
										if (selectedIds !== undefined) {
											$('[' + selectedIds + ']').each(function(index) {
												if ($(this).attr(selectedIds) === value.id) {
													// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
													found = true;
													return false;
												}
											});
										}
										if (!found) {
											if(value.description == undefined){
											html += '<option value="' + value.id + '" data-name="' + value.agreementType + '">' + value.agreementType  + '</option>';
											}else{
											html += '<option value="' + value.id + '" data-name="' + value.agreementType + '">' + value.agreementType + ' - ' + value.description  + '</option>';	
											}
										}
									}
								}
							});
						}

						selectElement.append(html);
						selectElement.trigger("chosen:updated")
						$('#loading').hide();
					},
					error : function(error) {
						console.log(error);
						$('#loading').hide();
					}
				});
			}
			
			$('#selectedAgrTypList').on('change', function(){
				var selectedId=$("#selectedAgrTypList option:selected").val();
				if(selectedId.length > 0){
					$('.agrTypError').attr('hidden','');
				}
			});
			
			
			$('#addMoreAgrTyp').on('click', function (e) {
			 	$('.agrTypError').attr('hidden','');
				e.preventDefault();
				var selectedId=$("#selectedAgrTypList option:selected").val();
			 	if(selectedId.length > 0){
			 		selectedAgreementTypeIds.push(selectedId.toString().trim());
			 		updatedAgreementTypeIds.push(selectedId.toString().trim());
			 		selectedAgreementType= selectedAgreementTypeIds.toString();
			 		$('#selectedAgrTypList').find('option[value="' + selectedId + '"]').remove();
					$('#selectedAgrTypList').trigger("chosen:updated");
					$('.agrTypError').attr('hidden','');
					for(var i in updatedAgreementTypeIds){
						if(removeAgreementTypeIds.includes(updatedAgreementTypeIds[i])){
							var index = removeAgreementTypeIds.indexOf(updatedAgreementTypeIds[i]);
							removeAgreementTypeIds.splice(index, 1);
						}
					}
					removeAgreementType=removeAgreementTypeIds.toString();
					console.log("selectedAgreementTypeIds " + selectedAgreementType + " removeAgreementType : " + removeAgreementType);
					agreementTypeTable.ajax.reload();
				 	$('#agrTypId').val('');
				 	$('#agrTypId').val(updatedAgreementTypeIds);
			 	} else {
			 		$('.agrTypError').removeAttr('hidden');	
			 	}
			 });

			  $(document).delegate('.deleteAgreementType', 'click', function(e) {
			        text2= $(this).parent().next('td').text();
			        id2=$(this).data("value");
					$("#removeAgreementTypePopup").dialog({
						modal : true,
						maxWidth : 400,
						minHeight : 100,
						dialogClass : "",
						show : "fadeIn",
						draggable : true,
						resizable : false,
						dialogClass : "dialogBlockLoaded2"
					});
					$('.ui-widget-overlay').addClass('bg-white opacity-60');
					$('.ui-dialog-title').addClass('title-ellipsis');
					$('.title-ellipsis').text('Remove Agreement Type');
				});

			 $(document).delegate('#deleteAgrTyp', 'click', function(e){
					e.preventDefault();
				    var id=id2;
			 		var text = text2;
			 		removeAgreementTypeIds.push(id.toString().trim());
			 		removeAgreementType=removeAgreementTypeIds.toString();
					$("#removeAgreementTypePopup").dialog('close');
					var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
		 			$('#selectedAgrTypList option:selected').after(newRow);
		 		    $('#selectedAgrTypList').trigger("chosen:updated");
		 		  	agreementTypeTable.ajax.reload();
				 	var index = updatedAgreementTypeIds.indexOf(id);
				    if (index !== -1) updatedAgreementTypeIds.splice(index, 1);
				    var index = selectedAgreementTypeIds.indexOf(id);
				    if (index !== -1) selectedAgreementTypeIds.splice(index, 1);
				    $('#agrTypId').val(updatedAgreementTypeIds);
				});
		// AGREEMENT TYPE END

	});
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>

	
	$.formUtils.addValidator({
		name : 'unitCode',
		validatorFunction : function(value, $el, config, language, $form) {
			var unitCode = $('#unitCode').val();
			var response = true;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			//alert(loginId);
			$.ajax({
				url : getContextPath() + "/buyer/checkUnitCode",
				data : {
					'unitCode' : unitCode
				},
				async : false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					console.log(data);
					response = true;
				},
				error : function(request, textStatus, errorThrown) {
					console.log(textStatus);
					response = false;
				}
			});
			return response;
		},
		errorMessage : 'Unit code already used in the system',
		errorMessageKey : 'Unit code'
	});
	
	
	$(document).delegate('#saveBusinessUnit', 'click', function(e) {
		e.preventDefault();
		var costVal=$('#costId').val();
		var grcVal=$('#grcId').val();
		var agrTypVal=$('#agrTypId').val();
		if (!$('#idBusinessUnit').isValid() && costVal == '' && grcVal == '' ) {
			$('.costErrorDisp').removeAttr('hidden');	
			$('.costError').attr('hidden','');
			
			$('.grcErrorDisp').removeAttr('hidden');	
			$('.grcError').attr('hidden','');
			
			return false;
		}
		if(costVal === ''){
		    $('.costErrorDisp').removeAttr('hidden');	
		    $('.costError').attr('hidden','');
			return false;
		}
		if(grcVal === ''){
		    $('.grcErrorDisp').removeAttr('hidden');	
		    $('.grcError').attr('hidden','');
			return false;
		}
/* 		if(agrTypVal === ''){
		    $('.agrTypErrorDisp').removeAttr('hidden');	
		    $('.agrTypError').attr('hidden','');
			return false;
		}
 */		var idParent = $("#idParent").val();
		var id = $("#id").val();

		var budgetCheckOld = $("#budgetCheckOld").val().toString();
		var idBudgetCheck = $("#idBudgetCheck").prop('checked').toString() ;

		console.log('checking Budget');
		if(idParent == '' && id != '' && budgetCheckOld  != idBudgetCheck) {
			console.log('Budget flag is changed');
			if(idBudgetCheck == "true"){
				$('#removeTemplateUserListPopupEnable').modal('show');
			} else{
				$('#removeTemplateUserListPopupDisable').modal('show');
			}
		} else {
			console.log('checking SPM');
			checkSpmIntegration();
		}
		
	});
	
	function checkSpmIntegration() {
		var id = $("#id").val();
		var idParent = $("#idParent").val();
		var spmIntegrationOld = $("#spmIntegrationOld").val().toString();
		var idSpmIntegration = $("#idSpmIntegrationCheck").prop('checked').toString() ;
		console.log('spmIntegrationOld', spmIntegrationOld, 'idSpmIntegration', idSpmIntegration, 'idParent', idParent, 'id', id);
		if(idParent == '' && id != '' && spmIntegrationOld  != idSpmIntegration) {
			console.log('SPM flag is changed');
			if(idSpmIntegration == "true"){
				$('#removeSpmIntegrationPopupEnable').modal('show');
			} else{
				$('#removeSpmIntegrationPopupDisable').modal('show');
			}
		} else {
			$('#loading').show();
			$("#idBusinessUnit").submit()
		}
	}
	
	$(document).delegate('#yesUnit', 'click', function(e){
		e.preventDefault();
		$("#recursive").val(true);
		$('#removeTemplateUserListPopupEnable').modal('hide');
		$('#removeTemplateUserListPopupDisable').modal('hide');
		checkSpmIntegration();
	});
	

	$(document).delegate('#noUnit', 'click', function(e){
		e.preventDefault();
		$("#recursive").val(false);
		$('#removeTemplateUserListPopupEnable').modal('hide');
		$('#removeTemplateUserListPopupDisable').modal('hide');
		checkSpmIntegration();
	});

	$(document).delegate('#yesSpmIntegration', 'click', function(e){
		e.preventDefault();
		$("#spmIntegrationRecursive").val(true);
		$('#loading').show();
		$("#idBusinessUnit").submit();
	});
	

	$(document).delegate('#noSpmIntegration', 'click', function(e){
		e.preventDefault();
		$("#spmIntegrationRecursive").val(false);
		$('#loading').show();
		$("#idBusinessUnit").submit();
	});

	
	$.validate({
		lang : 'en'
	});
</script>
<style>
.widthset-100 {
	width: 100px;
}
.setbackground {
background: #0095d5 !important;
}
.body-font{
font-size: 16px !important;
}
.disp-flex {
	display: flex;
}
.dialogBlockLoaded2 {
	border: 1px solid rgba(0, 0, 0, .2)!;
	-webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	width: 312px !important;
}
.error {
    color: #ff5757 !important;
}
#tableList th {
	text-align: left;
    color: #424242;
    font-family: 'open_sanssemibold';
    font-weight: normal;
}
@media (min-width: 992px){
.chang-width {
    width: 43.333333% !important;
}
}
.costError {
    color: #ff5757 !important;
}
.costErrorDisp {
    color: #ff5757 !important;
}
.inactiveCostErrorDisp{
 color: #ff5757 !important;
}

.grcError {
    color: #ff5757 !important;
}
.grcErrorDisp {
    color: #ff5757 !important;
}
.inactiveGrcErrorDisp{
 color: #ff5757 !important;
}
#groupCodetableList th {
	text-align: left;
    color: #424242;
    font-family: 'open_sanssemibold';
    font-weight: normal;
}

.agrTypError {
    color: #ff5757 !important;
}
.inactiveAgrTypErrorDisp{
 color: #ff5757 !important;
}
#agreementTypetableList th {
	text-align: left;
    color: #424242;
    font-family: 'open_sanssemibold';
    font-weight: normal;
}
</style>

	<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>