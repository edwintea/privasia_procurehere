<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prPurchaseItemDesk" code="application.pr.create.purchase.items" />
<style>
	.d-flex-bet {
		display: flex;
		justify-content: space-between;
		width: 100%;
	}

	.d-flex-bet h2 {
		font-size: 16px !important;
		font-family: 'open_sansregular';
	}
</style>
<script type="text/javascript">

	zE(function() {
		zE.setHelpCenterSuggestions({ labels: [${prPurchaseItemDesk}] });
	});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="pr.purchase.requisition" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="pr.purchase.requisition" />
					</h2>
				</div>
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<div class="clear"></div>
				<jsp:include page="prHeader.jsp"></jsp:include>

				<div class="clear"></div>
				<div class="white-bg border-all-side float-left width-100 pad_all_15">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tag-line">
								<h2>PR :${pr.name}</h2>
								<br>
								<h2>
									<spring:message code="prtemplate.case.id" />
									:${pr.prId}
								</h2>
								<br>
							</div>
							<div class="d-flex-bet">
								<div>
									<c:if test="${pr.remainingBudgetAmount != null and pr.lockBudget==true}">
										<h2 class="">
											<fmt:formatNumber var="remainingBudgetAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${pr.remainingBudgetAmount}" />
											<spring:message code="label.budget.remaining.budget.amount" />
											: <span><strong>${remainingBudgetAmount} ${pr.budgetCurrencyCode}</strong></span>
										</h2>
									</c:if>
								</div>
								<div class="print-down">
									<label><spring:message code="application.status" /> : </label>${pr.status}
								</div>
							</div>
						</div>
					</div>
				</div>
				<c:url var="prSupplier" value="/buyer/savePrSupplier" />
				<form:form id="prSupplierForm" action="${prSupplier}" method="post" modelAttribute="pr">

					<input type="hidden" id="eventPermission" value="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}" />
					<form:hidden path="id" value="${pr.id}" />
					<input type="hidden" id="contractItemsOnly" value="${pr.template != null && pr.template.contractItemsOnly ? true : false}">
					<form:hidden path="prId" />
					<div class="white-bg border-all-side float-left width-100 pad_all_15 ${hideSupplier?'disabled':'' }" id="supplierDetail">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">



								<input type="hidden" id="selectedSuppId"> <input type="hidden" id="prItemExists" value="${prItemExists}">
								<h4>
									<spring:message code="application.supplier.detail" />
								</h4>
								<div class="row"></div>
								<div class="form-tander1 pad_all_15">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="prtemplate.supplier.selection" /></label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
										<div class="radio-info pull-left marg-right-10 marg_top_15">
											<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" <c:if test="${empty pr.supplierName}">checked="checked"</c:if> class="custom-radio showSupplierBlocks" value="LIST" /> <spring:message code="prtemplate.radio.my.supplier" />
											</label>
										</div>
										<c:if test="${empty pr.hideContractBased || !pr.hideContractBased}">
											<div class="radio-info pull-left marg-left-10 marg_top_15">
												<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" <c:if test="${not empty pr.supplierName}">checked="checked"</c:if> class="custom-radio showSupplierBlocks" value="MANUAL" /> <spring:message code="prtemplate.radio.open.supplier" />
												</label>
											</div>
										</c:if>
									</div>
								</div>


								<div class="showPartMANUAL supplierBoxes">

									<div class="form-tander1 pad_all_15_30">
										<div class="row">
											<div class="col-sm-4 col-md-4 col-xs-4 col-xs-12">
												<div class="form-group">
													<label> <spring:message code="supplier.name" />
													</label>
													<spring:message code="prtemplate.enter.name.placeholder" var="name" />
													<form:input path="supplierName" type="text" placeholder="${name}" data-validation="required length" data-validation-length="max128" class="form-control" />
												</div>

											</div>
											<div class="col-sm-4 col-md-4 col-xs-4 col-xs-12">
												<div class="form-group">
													<label><spring:message code="prdraft.address" /></label>
													<spring:message code="prtemplate.enter.Address.placeholder" var="adds" />
													<form:input path="supplierAddress" maxlength="250" placeholder="${adds}" data-validation="required length" data-validation-length="max250" class="form-control" />
												</div>

											</div>
											<div class="col-sm-4 col-md-4 col-xs-4 col-xs-12">
												<div class="form-group">
													<label><spring:message code="prtemplate.office.fax.number" /></label>
													<spring:message code="prtemplate.fax.number.placeholder" var="faxno" />
													<form:input path="supplierFaxNumber" id="idFax" type="text" placeholder="${faxno}" data-validation="length number" data-validation-ignore="+ " data-validation-length="6-14" data-validation-optional="true" class="form-control" />
												</div>
											</div>
										</div>

										<div class="row">

											<div class="col-sm-4 col-md-4 col-xs-4 col-xs-12">
												<div class="form-group">
													<label> <spring:message code="prtemplate.tax.number" /></label>
													<spring:message code="prtemplate.tax.number.placeholder" var="taxno" />
													<form:input path="supplierTaxNumber" type="text" placeholder="${taxno}" data-validation="length" data-validation-length="0-20" class="form-control" />
												</div>
											</div>
											<div class="col-sm-4 col-md-4 col-xs-12">
												<div class="form-group">
													<label><spring:message code="prtemplate.office.telephone.number" /></label>
													<spring:message code="prtemplate.supplier.telnumber.placeholder" var="telno" />
													<form:input path="supplierTelNumber" type="text" placeholder="${telno}" data-validation="required length" data-validation-length="6-50" class="form-control" />
												</div>
											</div>

											<div class="col-sm-4 col-md-4 col-xs-12">
												<div class="form-group">
													<label> </label>
													<button type="button" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="saveOpenSupplier" style="margin-top: 39px;">
														<spring:message code="application.save" />
													</button>
												</div>
											</div>
										</div>

									</div>

								</div>
								<div class="form_field showPartLIST supplierBoxes">
									<div class="form-group">
										<label><spring:message code="supplier.name" /></label>
										<div>
											<div class="row">
												<div class="form-group">
													<div class="col-lg-6">
														<form:select path="supplier"  class="chosen-select  col-sm-6 col-md-6 col-xs-12" id="chosenSupplier">
															<form:option value="">
																<spring:message code="select.supplier.placeholder" />
															</form:option>
															<c:forEach items="${supplierList}" var="supp">
																<!-- Output values for debugging -->
																<c:out value="Supp ID: ${supp.id}, PR Supplier ID: ${pr.supplier.id}" /><br/>

																<!-- Existing condition -->
																<c:if test="${!empty supp.id and supp.id == pr.supplier.id}">
																	<option value="${supp.id}" selected>${supp.companyName}</option>
																</c:if>
																<c:if test="${empty supp.id || supp.id != pr.supplier.id}">
																	<option value="${supp.id}">${supp.companyName}</option>
																</c:if>
															</c:forEach>
														</form:select>
													</div>

												</div>
											</div>

										</div>

									</div>
								</div>
							</div>
						</div>
					</div>
				</form:form>



				<div class="column_button_bar">
					<div class="left_button">
						<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="s1_tender_addsection_btn">
							<%-- <spring:message code="label.rftbq.button.additem" /> --%>
							<spring:message code="application.add.section.button" />
						</button>
						<button id="s1_tender_delete_btn" class="btn btn-black ph_btn_midium disabled ph_btn_midium hvr-pop hvr-rectangle-out1">
							<spring:message code="label.rftbq.button.delete" />
						</button>
					</div>
					<div class="right_button">
						<button class="btn btn-default ph_btn_midium hvr-pop hvr-rectangle-out3" " id="s1_tender_adddel_btn">
						<spring:message code="label.rftbq.button.deletecolumn" />
						</button>
						<button class="btn btn-danger ph_btn_midium hvr-pop " " id="s1_tender_reset_btn">
						<spring:message code="application.reset" />
						</button>

					</div>
				</div>
				<!-- Add Section -->
				<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="pr">
					<section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox hidecolumnoption" id="creat_seaction_form">
						<input type="hidden" id="bqId" value="${bqId}"> <input type="hidden" name="parentId" id="parentId" value=""> <input type="hidden" name="itemId" id="itemId" value="">
						<input type="hidden" name="prDecimalId" id="prDecimalId" value="${pr.decimal}">
						<div class="row">
							<div class="col-md-12 col-sm-12">
								<h3 class="s1_box_title">Create New Section</h3>
							</div>

								<%-- <form id="addEditSectionForm">
                                </form> --%>
						</div>
					</section>

					<div class="main_table_wrapper float-left width-100">
						<div class="table_fix_header">
							<table class="header_table header" style="position: relative;" width="100%" cellpadding="0" cellspacing="0">
								<tr>

									<th class="hed_1 width_50 width_50_fix align-center"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyphicon glyphicon-sort"></i>
									</a>
										</th0>
									<th class="hed_2 width_50 width_50_fix align-center">
										<div class="checkbox checkbox-info">
											<label> <input type="checkbox" id="inlineCheckbox115" class="custom-checkbox checkallcheckbox">
											</label>
										</div>
									</th>
									<th class="hed_2 width_100 width_100_fix align-center"><spring:message code="application.action1" /></th>
									<th class="hed_2 width_50 width_50_fix itemLevelOrder"><spring:message code="label.rftbq.th.No" /></th>

									<!-- <th class="hed_6 width_300 width_300_fix align_center"></th>
									<th class="hed_2 width_50 width_50_fix "></th> -->

									<th class="hed_4 width_200 width_200_fix align_center"><spring:message code="label.rftbq.th.itemname" /></th>
									<th class="hed_5 width_100 width_100_fix align_center "><spring:message code="label.rftbq.th.uom" /></th>
									<th class="hed_5 width_100 width_100_fix align_center "><spring:message code="label.rftbq.th.quantity.only" /></th>
									<th class="hed_5 width_100 width_100_fix align_center "><spring:message code="label.rftbq.th.unitprice" /></th>
									<th class="hed_5 width_100 width_100_fix align_center "><spring:message code="label.rftbq.th.priceperunit" /></th>
									<c:if test="${not empty pr.field1Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field1Label}</th>
									</c:if>
									<c:if test="${not empty pr.field2Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field2Label}</th>
									</c:if>
									<c:if test="${not empty pr.field3Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field3Label}</th>
									</c:if>
									<c:if test="${not empty pr.field4Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field4Label}</th>
									</c:if>
									<c:if test="${not empty pr.field5Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field5Label}</th>
									</c:if>
									<c:if test="${not empty pr.field6Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field6Label}</th>
									</c:if>
									<c:if test="${not empty pr.field7Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field7Label}</th>
									</c:if>
									<c:if test="${not empty pr.field8Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field8Label}</th>
									</c:if>
									<c:if test="${not empty pr.field9Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field9Label}</th>
									</c:if>
									<c:if test="${not empty pr.field10Label}">
										<th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${pr.field10Label}</th>
									</c:if>
									<th class="hed_5 width_120 width_120_fix align_center "><spring:message code="prtemplate.total.amount" /></th>
									<th class="hed_5 width_120 width_120_fix align_center "><spring:message code="prtemplate.tax.amount" /></th>
									<th class="hed_5 width_200 width_200_fix align_center "><spring:message code="prtemplate.total.amount.tax" /></th>
								</tr>
							</table>
						</div>
						<div class="marg-for-cq-table ${buyerReadOnlyAdmin? 'disabled' : '' }" style="margin-top: 70px;">
							<section id="demo">
								<ol class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="pritemList">
									<c:forEach items="${prlist}" var="prItem">
										<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${prItem.id}" data-parent="">
											<div class="menuDiv">
												<table class="table data ph_table diagnosis_list sorted_table">
													<tr class="sub_item" data-id="${prItem.id}">

														<td class="width_50 width_50_fix move_col"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyphicon glyphicon-sort"></i>
														</a></td>

														<td class="width_50 width_50_fix">
															<div class="checkbox checkbox-info">
																<label> <input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${prItem.id}">
																</label>
															</div>
														</td>

														<td class="width_100 width_100_fix"><a title="" class=" Edit_btn_table" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a></td>

														<td class="width_50 width_50_fix itemLevelOrder sectionNameD"><span>${prItem.level}.0</span></td>
														<td class="width_200 width_200_fix align-center"><span class="item_name sectionNameD">${prItem.itemName}</span> <c:if test="${not empty prItem.itemDescription}">
																<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"><spring:message code="application.view.description" /></a>
																</span>
														</c:if>
															<p class="s1_tent_tb_description s1_text_small">${prItem.itemDescription}</p></td>
														<td class="width_100 width_100_fix align-center">&nbsp;</td>
														<td class="width_100 width_100_fix align-center">&nbsp;</td>
														<td class="width_100 width_100_fix align-center">&nbsp;</td>
														<c:if test="${not empty pr.field1Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field2Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field3Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field4Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field5Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field6Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field7Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field8Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field9Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<c:if test="${not empty pr.field10Label}">
															<td class="width_200 width_200_fix align_center">&nbsp;</td>
														</c:if>
														<td class="width_120 width_120_fix align-center">&nbsp;</td>
														<!-- Total Amt -->
														<td class="width_120 width_120_fix align-center">&nbsp;</td>
														<!-- Tax Amount -->
														<td class="hed_5 width_200 width_200_fix align_center">&nbsp;</td>
														<!-- Total Amt With Tax -->

													</tr>
												</table>
											</div> <c:if test="${not empty prItem.children}">
											<ol>
												<c:forEach items="${prItem.children}" var="prchild">
													<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${prchild.id}" data-parent="${prItem.id}">
														<div class="menuDiv sub-color-change">
															<table class="table data ph_table diagnosis_list sorted_table">
																<tr class="sub_item" data-id="${prchild.id}" data-parent="${prItem.id}">

																	<td class="width_50 width_50_fix move_col"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyphicon glyphicon-sort"></i>
																	</a></td>
																	<td class="width_50 width_50_fix">
																		<div class="checkbox checkbox-info">
																			<label> <input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${prchild.id}">
																			</label>
																		</div>

																	</td>

																	<td class="width_100 width_100_fix"><a title="" class="Edit_btn_table" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a></td>

																	<td class="width_50 width_50_fix itemLevelOrder itemNameD"><span>${prchild.level}.${prchild.order}</span></td>
																	<td class="width_200 width_200_fix"><span class="item_name itemNameD">${prchild.itemName}</span> <c:if test="${not empty prchild.itemDescription}">
																				<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"><spring:message code="application.view.description" /></a>
																				</span>
																	</c:if>
																		<p class="s1_tent_tb_description s1_text_small">${prchild.itemDescription}</p></td>
																	<td class="width_100 width_100_fix align-center">${not empty prchild.unit ? prchild.unit.uom : prchild.product.uom.uom }</td>
																	<td class="width_100 width_100_fix align-center"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.quantity}" /></td>

																	<td class="width_100 width_100_fix align-center"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.unitPrice}" /></td>
																	<td class="width_100 width_100_fix align-center"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.pricePerUnit}" /></td>
																	<c:if test="${not empty pr.field1Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field1}</td>
																	</c:if>
																	<c:if test="${not empty pr.field2Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field2}</td>
																	</c:if>
																	<c:if test="${not empty pr.field3Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field3}</td>
																	</c:if>
																	<c:if test="${not empty pr.field4Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field4}</td>
																	</c:if>
																	<c:if test="${not empty pr.field5Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field5}</td>
																	</c:if>
																	<c:if test="${not empty pr.field6Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field6}</td>
																	</c:if>
																	<c:if test="${not empty pr.field7Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field7}</td>
																	</c:if>
																	<c:if test="${not empty pr.field8Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field8}</td>
																	</c:if>
																	<c:if test="${not empty pr.field9Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field9}</td>
																	</c:if>
																	<c:if test="${not empty pr.field10Label}">
																		<td class="width_200 width_200_fix align_center">${prchild.field10}</td>
																	</c:if>

																	<td class="width_120 width_120_fix align-center">
																		<!-- Total Amt --> <fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.totalAmount}" />
																	</td>
																	<td class="width_120 width_120_fix align-center">
																		<!-- Total Tax --> <fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.taxAmount}" />
																	</td>
																	<td class="hed_5 width_200 width_200_fix align_center totalAmountWithTax"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${prchild.totalAmountWithTax}" /></td>

																</tr>
															</table>
														</div>
													</li>
												</c:forEach>
											</ol>
										</c:if>
										</li>
									</c:forEach>
								</ol>
							</section>
						</div>
					</div>
				</form:form>
				<div class="clear"></div>
				<form id="addTaxForm">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<table class="table data ph_table diagnosis_list sorted_table">
						<tr class="sub_item">
							<td class="width_50 width_50_fix"></td>

							<td class="width_100 width_100_fix align-right"></td>
							<td class="width_200 width_200_fix"></td>

							<td class="width_100 width_100_fix align-right"><label class="control-label">Total (${pr.currency})</label></td>
							<td class="width_100 width_100_fix"><label id="total_bat"> <fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.total}" />
							</label></td>
						</tr>

						<c:choose>
							<c:when test="${pr.decimal==1}">
								<c:set var="prDecimalSet" value="0,0.0"></c:set>
							</c:when>
							<c:when test="${pr.decimal==2}">
								<c:set var="prDecimalSet" value="0,0.00"></c:set>
							</c:when>
							<c:when test="${pr.decimal==3}">
								<c:set var="prDecimalSet" value="0,0.000"></c:set>
							</c:when>
							<c:when test="${pr.decimal==4}">
								<c:set var="prDecimalSet" value="0,0.0000"></c:set>
							</c:when>
						</c:choose>
						<tr class="sub_item">
							<td class="width_50 width_50_fix"></td>

							<%-- <a id="inline-info" href="#" data-type="textarea" data-pk="2" data-title="Enter Additional Note ">
                            <c:if test="${empty pr.taxDescription}">Enter Tax description..</c:if>
                            ${pr.taxDescription}

                        </a> --%> <%-- <input type="text" id="taxDescription" data-validation="required length" data-validation-length="max150" name="taxDescription" placeholder="Enter Tax description.." value="${pr.taxDescription}"> --%>

							<td class="width_50 width_50_fix align-right"><label class="control-label additionalCharges"><spring:message code="prtemplate.additional.charges" /></label></td>
							<td class="width_50 width_50_fix align-right">
								<textarea id="taxDescription" class="marg-top-10" row="2" cols="50" data-validation="length" data-validation-length="max150" name="taxDescription" placeholder="e.g. Import Tax">${pr.taxDescription}</textarea>
							</td>
							<td class="width_100 width_100_fix align-right"><label class="control-label">(${pr.currency})</label></td>
							<td class="width_100 width_100_fix">
								<%-- <a id="inline-tax" href="#" data-type="text" data-pk="1" >
								<label id="total_bat"> <fmt:formatNumber type="number"  minFractionDigits="${pr.decimal}" value="${pr.additionalTax}" /></label>


							</a> --%> <fmt:formatNumber var="addtax" type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.additionalTax}" /> <input type="text" data-validation="custom" data-validation-regexp="^\d{0,16}(\.\d{1,${pr.decimal}})?$" data-validation-ignore="," data-validation-error-msg="Only numbers and ',' allowed, length should be less then 16 and after decimal ${pr.decimal} digits allowed" id="additionalTax" name="additionalTax" placeholder='<spring:message code="prtemplate.charges.amount.placeholder"/>'
																																															 value="${pr.additionalTax  > 0 ? addtax : '' }" style="width: 100%; text-align: left;" data-sanitize="numberFormat" data-sanitize-number-format="${prDecimalSet}"> <!-- <button id="addTaxButton" class="btn btn-sm btn-primary">
									<i class="glyph-icon icon-check"></i>
								</button>
								<button id="resetTaxButton" class="btn btn-sm btn-black">
									<i aria-hidden="true" class="glyph-icon icon-close"></i>
									<i class="glyph-icon icon-cross"></i>
								</button> -->
							</td>
						</tr>
						<tr class="sub_item">
							<td class="width_50 width_50_fix"></td>
							<td class="width_100 width_100_fix align-right"></td>
							<td class="width_200 width_200_fix"></td>

							<td class="width_100 width_100_fix align-right"><label class="control-label"><spring:message code="submission.report.grandtotal" /> (${pr.currency})</label></td>
							<td class="width_100 width_100_fix"><label id="gTotal"> <fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.grandTotal}" />
							</label></td>
						</tr>
					</table>
				</form>
				<div class="btn-next">
					<div class="row">
						<div class="col-md-12">

							<div class="table_f_action_btn">

								<c:url var="prDelivery" value="/buyer/prDelivery/${pr.id}" />
								<a href="${prDelivery}" id="previousButton" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1" value="Previous" name="previous" id="priviousStep"><spring:message code="application.previous" /></a>
								<c:url var="purchaseItemNext" value="/buyer/purchaseItemNext/${pr.id}" />
								<button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out"">
								<spring:message code="application.next" />
								</button>

								<spring:message code="application.draft" var="draft" />
								<c:url var="savePrDraft" value="/buyer/savePrDraft/${pr.id}" />
								<a href="${savePrDraft}"> <input type="button" id="submitStep1PrDetailDraft" class="step_btn_1 btn btn-black marg-top-20 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right" value="${draft}" />
								</a>
								<c:if test="${pr.status eq 'DRAFT' && (isAdmin or eventPermissions.owner)}">
									<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="prtemplate.cancel.pr" /></a>
								</c:if>

							</div>
						</div>
					</div>
				</div>
			</section>
		</div>
	</div>
</div>

<div class="modal fade" id="myModalDeleteColum" role="dialog" style="z-index: 9999;">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="bq.sure.delete.column" /></label> <input type="hidden" name="deleteColpos" id="deleteColpos" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="idConfirmDelete">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalDeleteBQs" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="bq.sure.delete.selected.item" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="prId" id="prId" value="${pr.id}">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idConfirmDeleteBQs">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>


<!-- reset button  -->

<div class="modal fade" id="myModalResetBQs" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/buyer/resetPrItem" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="application.confirm.reset" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">
					<label><spring:message code="prtemplate.sure.want.reset" /></label>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="hidden" name="prId" id="resetPrId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.reset" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>


<!-- cancel pr popup  -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="prtemplate.sure.want.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation" />
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>


<%--
<div class="modal fade" id="crateNewEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<div class="modal-content">
			<div class="modal-header">
				<h3>Conclude Event</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>


			<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">

				<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">
					<form id="addEditSectionForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-md-7 col-sm-7 col-xs-12">
							<div class="s1_creatItem_inlineControl">
								<div class="form-group">
									<input type="hidden" name="sectionId" id="sectionId" /> <input type="text" class="form-control" data-validation="required length" id="sectionName" data-validation-length="max250" placeholder="Enter Section Name" name="sectionName" />
								</div>
							</div>
						</div>
						<div class="col-md-7 col-sm-7 col-xs-12">
							<div class="s1_creatItem_inlineControl">
								<div class="form-group">
									<textarea type="text" class="form-control" id="sectionDescription" name="sectionDescription" data-validation="length" data-validation-length="max300" placeholder="Enter Section Description"></textarea>
									<span class="sky-blue">Max 300 characters only</span>
								</div>
							</div>
						</div>
						<div class="col-md-12 col-sm-12">
							<div class="s1__frmbtn_block">
								<button type="submit" class="btn btn-info ph_btn_midium" id="savePrSection">Save</button>
								<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 prCancelSec">Cancel</button>
							</div>
						</div>
					</form>
				</div>
			</div>

		</div>
	</div>
</div>
 --%>


<div id="crateNewSection" class="modal fade" role="dialog">
	<form action="" id="addEditSectionForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center" id="sectionTitle">
						<spring:message code="application.add.section.button" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">
					<input type="hidden" name="supplierId" id="supplierId" value="" />
					<div class="row marg-bottom-10">
						<div class="col-md-4">
							<label> <spring:message code="prtemplate.section.name" />
							</label>
						</div>
						<div class="col-md-8">
							<input type="text" class="form-control" data-validation="required length" id="sectionName" data-validation-length="max250" placeholder='<spring:message code="prtemplate.section.name.placeholder" />' name="sectionName" />
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-4">
							<label> <spring:message code="prtemplate.section.description" />
							</label>
						</div>
						<div class="col-md-8">
							<textarea type="text" class="form-control" id="sectionDescription" name="sectionDescription" data-validation="length" data-validation-length="max300" placeholder='<spring:message code="prtemplate.section.description.placeholder" />'></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer  text-center">
					<button class="btn btn-info hvr-pop hvr-rectangle-out  ph_btn_midium" id="savePrSection" type="button" data-dismiss="modal">Create</button>
					<button class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form>
</div>


<div id="crateNewItem" class="modal fade" role="dialog">

	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="modal-dialog" style="width: 90%; max-width: 800px;">
		<!-- Modal content-->
		<form name="addNewItems" id="addNewItems" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center" id="itemTitle">
						<spring:message code="label.rftbq.button.additem" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">

					<section class="hidecolumnoption" id="creat_subitem_form" style="padding-right: 10px; padding-left: 10px;">
						<input type="hidden" name="prId" id="prId" value="${pr.id}"> <input type="hidden" name="parentId" id="parentId" value=""> <input type="hidden" name="itemId" id="itemId" value="">
						<div class="row">


							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<spring:message code="event.document.itemname" var="itemName" />
							<spring:message code="event.document.quantity" var="quantity" />
							<spring:message code="event.document.unitpricing" var="unitpricing" />
							<spring:message code="event.document.priceperunit" var="priceperunit" />
							<spring:message code="event.document.description" var="description" />
							<div class="row">
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group" id="productCategoryDiv">
										<label><spring:message code="product.name" /></label> <select name="productCategory" data-validation="required" class="chosen-select" id="productCategory">
										<option value="" label="Search Product category.." />
										<c:forEach items="${productCategoryList}" var="productCategory">
											<option value="${productCategory.id}">${productCategory.productCode}-${ productCategory.productName}</option>
										</c:forEach>
									</select>
									</div>
								</div>

								<div class="col-md-6 col-sm-6 col-xs-12">


									<div class="row">
										<div class="col-md-${(not empty pr.hideContractBased and !pr.hideContractBased) ?'9':'12'} col-sm-${(not empty pr.hideContractBased and !pr.hideContractBased) ?'9':'12'} col-xs-12">
											<div class="form-group" id="inputPickerDiv" style="display: none;">
												<label><spring:message code="label.rftbq.th.itemname" /></label> <input type="text" name="itemName" class="form-control " autocomplete="off" id="itemName" placeholder='<spring:message code="prtemplate.start.search.placeholder" />'>
											</div>
											<div class="form-group" id="textInputPickerDiv" style="display: block;">
												<label><spring:message code="label.rftbq.th.itemname" /></label> <input type="text" name="itemName" class="form-control itemNameText" autocomplete="off" data-validation="required length" data-validation-length="max1000"  id="itemNameText" placeholder='<spring:message code="prtemplate.start.search.placeholder" />'>
											</div>
										</div>
										<div class="col-md-3 col-sm-3 col-xs-12">
											<c:if test="${(not empty pr.hideContractBased and !pr.hideContractBased) and (pr.template != null && pr.template.contractItemsOnly ? false : true)}">
												<div class="form-group">
													<label><spring:message code="prtemplate.free.text" /></label>
													<div class="checker" id="uniform-freeTextItemEntered" style="margin-top: 10px;">
														<span> <input id="freeTextItemEntered" name="freeTextItemEntered" class="custom-checkbox" type="checkbox" value="true"><i class="glyph-icon icon-check"></i>
														</span>
													</div>
												</div>
											</c:if>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group" id="uomTextDiv">
										<label><spring:message code="label.rftbq.th.unit" /></label> <select name="uomText" id="uomText" data-validation="required" class="chosen-select">
										<option value="" label="Search UMO.." />
										<c:forEach items="${uomList}" var="uom">
											<option value="${uom.id}">${uom.uom}</option>
										</c:forEach>
									</select>
										<!-- <input type="text" name="uomText" class="form-control" id="uomText" disabled="disabled" value=""> -->
									</div>
								</div>
								<c:choose>
									<c:when test="${pr.decimal==1}">
										<c:set var="decimalSet" value="0,0.0"></c:set>
									</c:when>
									<c:when test="${pr.decimal==2}">
										<c:set var="decimalSet" value="0,0.00"></c:set>
									</c:when>
									<c:when test="${pr.decimal==3}">
										<c:set var="decimalSet" value="0,0.000"></c:set>
									</c:when>
									<c:when test="${pr.decimal==4}">
										<c:set var="decimalSet" value="0,0.0000"></c:set>
									</c:when>
								</c:choose>
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group">
										<label><spring:message code="label.rftbq.th.quantity" /></label> <input type="text" data-validation="required" data-validation-regexp="^\d{1,16}(\.\d{1,${pr.decimal}})?$" data-validation-ignore=",." name="quantity" class="validate form-control itemValue" id="itemQuantity" placeholder='<spring:message code="event.document.quantity" />' data-validation-length="1-22">
									</div>
								</div>
							</div>
							<div class="row">

								<div class="col-md-6 col-sm-6 col-xs-12">

									<div class="form-group">

										<label><spring:message code="product.list.tax" />&nbsp;(%)</label> <input type="text" data-validation="required" data-validation-regexp="^\d{1,16}(\.\d{1,${pr.decimal}})?$" data-validation-ignore=",." id="itemTax" name="itemTax" placeholder='<spring:message code="prtemplate.enter.tax.placeholder" />' class="validate form-control itemValue" data-validation-length="1-22" />
									</div>
								</div>


								<div class="col-md-6 col-sm-6 col-xs-12 " id="itemUnitPriceDiv">
									<div class="form-group">

										<label class="marg-bottom-10"><spring:message code="product.unit.price" /></label> <input type="text" data-validation="required" data-validation-regexp="^\d{1,16}(\.\d{1,${pr.decimal}})?$" data-validation-ignore=",." class="validate form-control itemValue" name="unitPrice" id="itemUnitPrice" placeholder='<spring:message code="event.document.unitpricing" />' data-validation-length="1-22">
									</div>
								</div>

								
							</div>


							<div class="row">

								<div class="col-md-6 col-sm-6 col-xs-12">

									<div class="form-group" id="idDescriptionDiv">
										<label class="marg-bottom-10"><spring:message code="application.description" /></label>
										<textarea class="form-control" id="itemDescription" name="itemDescription"
												  placeholder='<spring:message code="event.document.description" />'
												  data-validation="length" data-validation-length="max2000"></textarea>
										<span class="sky-blue"><spring:message
												code="createrfi.event.description.max.chars"/></span>
									</div>
								</div>

								<div class="col-md-6 col-sm-6 col-xs-12" id="itemPricePerUnitDiv">
									<div class="form-group">
										<label class="marg-bottom-10"><spring:message code="product.price.per.unit" /></label>
										<input type="text"
											   data-validation="required custom"
											   data-validation-regexp="^(?!0+(\.0+)?$)(\d{1,22}(\.\d{1,${pr.decimal}})?)$"
											   data-validation-ignore=".,"
											   data-validation-error-msg-required="This is a required field"
											   data-validation-error-msg-custom="The input value is incorrect"
											   class="validate form-control itemValue"
											   name="pricePerUnit"
											   id="itemPricePerUnit"
											   placeholder='<spring:message code="event.document.priceperunit" />'
											   data-validation-length="1-22">
									</div>
								</div>
							</div>
							<div class="row extraFields" id="extraFields"></div>
						</div>
					</section>



				</div>
				<div class="modal-footer  text-center">
					<%-- <button class="btn btn-info hvr-pop hvr-rectangle-out  ph_btn_midium" id="savePrSection" type="button" data-dismiss="modal">Create</button>
	<button class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium" type="button" data-dismiss="modal">
		<spring:message code="application.cancel" />
	</button>
	 --%>


					<button type="button" class="btn btn-info ph_btn_midium" id="itemSave" data-dismiss="modal">
						<spring:message code="label.rftbq.button.save" />
					</button>
					<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 " data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>

				</div>
			</div>
		</form>
	</div>
</div>




<div id="crateColumnSection" class="modal fade" role="dialog">
	<form id="newFieldForm">
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="label.rftbq.button.deletecolumn" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">


					<section class=" s1_del_addcolumn hidecolumnoption" id="add_delete_column">
						<h3 class="s1_box_title">
							<spring:message code="prtemplate.note.max.column" />
						</h3>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="table-responsive">
							<table class="table">
								<thead>
								<tr>
									<th class="" style="width: 100%"><spring:message code="bq.table.column.name" /></th>
									<th class="width_100"><spring:message code="application.action1" /></th>
								</tr>
								</thead>
								<tbody>
								<c:if test="${not empty pr.field1Label}">
									<tr class="addColumsBlock" data-pos="1">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field1Label" name="field1" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field1Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove1" name="fieldRemove1"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field2Label}">
									<tr class="addColumsBlock" data-pos="2">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field2Label" name="field2" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field2Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove2" name="fieldRemove2"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field3Label}">
									<tr class="addColumsBlock" data-pos="3">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field3Label" name="field3" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field3Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove3" name="fieldRemove3"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field4Label}">
									<tr class="addColumsBlock" data-pos="4">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field4Label" name="field4" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field4Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove4" name="fieldRemove4"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>

								<c:if test="${not empty pr.field5Label}">
									<tr class="addColumsBlock" data-pos="1">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field5Label" name="field5" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field5Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove5" name="fieldRemove5"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field6Label}">
									<tr class="addColumsBlock" data-pos="4">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field6Label" name="field6" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field6Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove6" name="fieldRemove6"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field7Label}">
									<tr class="addColumsBlock" data-pos="2">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field7Label" name="field7" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field7Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove7" name="fieldRemove7"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field8Label}">
									<tr class="addColumsBlock" data-pos="3">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field8Label" name="field8" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field8Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove8" name="fieldRemove8"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<c:if test="${not empty pr.field9Label}">
									<tr class="addColumsBlock" data-pos="4">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field9Label" name="field9" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field9Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove9" name="fieldRemove9"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>

								<c:if test="${not empty pr.field10Label}">
									<tr class="addColumsBlock" data-pos="3">
										<td class="width_300 width_300_fix s1-mrg-10">
											<div class="form-group ">
												<input type="text" class="form-control" id="field10Label" name="field10" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${pr.field10Label}">
											</div>
										</td>
										<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove10" name="fieldRemove10"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
										</a></td>
									</tr>
								</c:if>
								<tr>
									<td colspan="4">
										<button class="btn btn-black ph_btn_midium s1_mrg-r-20 " type="button" id="AddColumnsToList">Add columns</button>
										<button type="button" class="btn btn-info ph_btn_midium" id="newFieldsSave" data-dismiss="modal" R>
											<spring:message code="label.rftbq.button.save" />
										</button>

										<button class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium pull-right" type="button" data-dismiss="modal">
											<spring:message code="application.cancel" />
										</button>
									</td>
								</tr>
								</tbody>
							</table>
						</div>
					</section>

				</div>

			</div>
		</div>
	</form>
</div>




<style>
	.modal-body {
		padding: 15px 30px !important;
	}

	.extraEachBlock {
		position: relative;
		margin-bottom: 10px;
	}

	.extraEachBlock .help-block {
		position: absolute;
		top: 55px;
	}

	.modal-body input {
		margin: 0 0 0 0;
	}

	.custom-icon {
		font-size: 22px;
		padding-right: 10px;
		/* line-height: 40px; */
		/* float: left; */
		/* width: 40px; */
		/* height: 40px; */
		/* margin: 10px; */
		/* text-align: center; */
		color: #92A0B3;
		/* border: 1px solid rgba(220, 233, 255, 0.54); */
		/* border-radius: 3px; */
	}

	.addColumsBlock .checkbox.checkbox-info label {
		padding: 0;
	}

	#pritemList .ph_table td {
		text-align: center;
		padding-left: 18px;
	}

	#pritemList .ph_table .itemLevelOrder {
		text-align: right;
	}

	.header_table .itemLevelOrder {
		text-align: right;
	}

	#total_table {
		width: 85%
	}

	#total_table td {
		float: left;
		margin-bottom: 5px;
		padding: 10px 10px 0;
		width: 50%;
	}

	#total_table .click.active {
		float: left;
		margin-left: 65px;
		width: auto;
		cursor: pointer;
	}

	#total_table .inputInlineEditImg {
		padding-left: 10px;
	}

	#total_table .inputInlineEdit {
		width: 60px;
	}

	.not_visible {
		visibility: hidden;
	}

	.itemNameD {
		color: #3b3b3b;
		font-family: 'open_sansregular', "Helvetica Neue", Helvetica, Arial, sans-serif;
		font-size: 14px;
	}

	.sectionNameD {
		color: #3b3b3b;
		font-family: 'open_sanssemibold';
		font-size: 14px;
	}

	.editable-input {
		display: inline-block;
		vertical-align: top;
		white-space: normal;
		width: auto;
	}

	.editable-click, a.editable-click, a.editable-click:hover {
		border-bottom: 1px dashed #08c;
		text-decoration: none;
	}

	.editable-buttons {
		display: inline-block;
		margin-left: 7px;
		vertical-align: top;
	}

	.align-right {
		text-align: right !important;
	}

	.editable-cancel {
		display: none;
	}

	/* .ph_table td a {
        color: #0095d5 !important;
    } */
	#pritemList td:nth-child(2) {
		padding-left: 10px !important;
	}

	.disabled div {
		opacity: 1;
	}

	.lbl-add-chrg {
		position: relative;
		bottom: 20px;
		right: 20px;
	}

	@media only screen and (min-width: 1703px) {
		.additionalCharges {
			margin-right: -59%;
		}
	}

	.alert-lbl {
		color: red;
		margin-top: 10px;
		padding: 7px 5px;
		font-size: 14px;
		font-weight: bold;
	}
</style>
<%-- <form method="post">
  <input id="myAutocomplete" name="autotxt" type="text" />
  <input type="submit" name="sub" value="Submit"/>
</form> --%>
<script src="<c:url value="/resources/assets/widgets/autocompletedropdown/jquery.autocomplete.multiselect.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<link href="<c:url value="/resources/css/bootstrap-editable.css"/>" rel="stylesheet" />
<link href="<c:url value="/resources/css/jquery.inputpicker.css"/>" rel="stylesheet" />
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<%-- <script src="<c:url value="/resources/js/xeditable.js"/>"></script> --%>
<script>


	$('document').ready(function() {

		$('#cancelPr').click(function() {
			$(this).addClass('disabled');
		});


		$('#savePrSection').click(function (){
			if(!$('#addEditSectionForm').isValid()){
				return false;
			}
		});

	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble,#supplierChoice';
		//var disableAnchers = ['#reloadMsg'];
		disableFormFields(allowedFields);
	});
	</c:if>
	/* $.validate();

	$('.click').click(function() {
		if ($(this).children().length == 0) {
			var editText = $(this).text();
			$(this).addClass('active');
			var htmlData = '<input type="text" class="form-control inputInlineEdit" id="editInlineText" value="'+editText+'"/>';
			htmlData += '<span id="saveInlineEdit" class="inputInlineEditImg"><i class="fa fa-floppy-o" aria-hidden="true" style="font-size:22px; color:#424242;"></i></span>';
			$(this).html(htmlData);
		}
	}); */



</script>
<script type="text/javascript">
	var decimalLimit = ${pr.decimal};
</script>

<script type="text/javascript" src="<c:url value="/resources/js/view/prTemplate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.inputpicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/generic/generic.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prSupplier.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prPurchaseItem.js?12"/>"></script>
