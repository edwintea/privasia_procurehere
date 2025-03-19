<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="poPurchaseItemDesk" code="application.po.create.purchase.items" />
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
	zE.setHelpCenterSuggestions({ labels: [${poPurchaseItemDesk}] });
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
				<li class="active"><spring:message code="po.purchase.order" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="po.purchase.order" />
					</h2>
					<h2 class="trans-cap pull-right">
                        <spring:message code="application.status" />
                        : ${po.status}
                    </h2>
				</div>
				<<jsp:include page="poHeader.jsp"></jsp:include>
                <div class="clear"></div>
                <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
                <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                <div class="clear"></div>
                <jsp:include page="poTagHeader.jsp"></jsp:include>
                <div class="clear"></div>
                <!-- THIS IS ITEM ID FOR ITEM SELECTION WHEN ADD SUB ITEM -->
                <!-- PH-4113-->
				<input type="hidden" id="productItemId" name="productItemId"  value="" />
                <input type="hidden" id="poId" name="poId"  value="${po.id}" />
				<c:url var="poSupplier" value="/buyer/savePrSupplier" />

				<form:form id="poSupplierForm" action="#" method="post" modelAttribute="pr">
					<input type="hidden" id="eventPermission" value="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}" />
					<form:hidden path="id" value="${pr.id}" />

					<input type="hidden" id="prId"  value="${pr.id}" />
					<input type="hidden" id="contractItemsOnly" value="${pr.template != null && pr.template.contractItemsOnly ? true : false}">

					<div class="white-bg border-all-side float-left width-100 pad_all_15 ${hideSupplier?'disabled':'disabled' }" id="supplierDetail">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<input type="hidden" id="selectedSuppId">
								<input type="hidden" id="poItemExists" value="${poItemExists}">
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
                <form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="po">
                    <section class="s1_white_panel s1_pad_25_all s1_creat_itmeBox hidecolumnoption" id="creat_seaction_form">
                        <input type="hidden" id="bqId" value="${bqId}">
                        <input type="hidden" name="parentId" id="parentId" class="parentId" value="">
                        <input type="hidden" name="itemId" id="itemId" class="itemId" value="">
                         <input type="hidden" name="poDecimalId" id="poDecimalId" value="${po.decimal}">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <h3 class="s1_box_title">Create New Section</h3>
                            </div>
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

                                    <th class="hed_4 width_200 width_200_fix align_center"><spring:message code="label.rftbq.th.itemname" /></th>
                                    <th class="hed_5 width_100 width_100_fix align_center "><spring:message code="label.rftbq.th.uom" /></th>

                                    <th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.quantity" /></th>

                                    <c:if test="${po.status ne 'DRAFT'}">
                                        <th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.locked.quantity" />  </th>
                                        <th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.balance.quantity" /></th>
                                    </c:if>

                                    <th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.unitprice" /> (${po.currency})</th>
                                    <th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.pricePerUnit" /></th>

                                    <c:if test="${not empty po.field1Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field1Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field2Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field2Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field3Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field3Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field4Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field4Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field5Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field5Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field6Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field6Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field7Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field7Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field8Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field8Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field9Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field9Label}</th>
                                    </c:if>
                                    <c:if test="${not empty po.field10Label}">
                                        <th class="hed_4 width_200 width_200_fix align_center extraFieldHeaders">${po.field10Label}</th>
                                    </c:if>
                                    <th class="hed_5 width_120 width_120_fix align_center "><spring:message code="prtemplate.total.amount" /></th>
                                    <th class="hed_5 width_120 width_120_fix align_center "><spring:message code="prtemplate.tax.amount" /></th>
                                    <th class="hed_5 width_200 width_200_fix align_center "><spring:message code="prtemplate.total.amount.tax" /></th>
                                </tr>
                            </table>
                        </div>
                        <div class="marg-for-cq-table ${buyerReadOnlyAdmin? 'disabled' : '' }" style="margin-top: 70px;">
                            <section id="demo">
                                <ol class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="poItemList">
                                    <c:if test="${not empty polist}">
                                        <c:forEach items="${polist}" var="poItem">
                                            <li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${poItem.id}" data-parent="">
                                                <div class="menuDiv">
                                                    <table class="table data ph_table diagnosis_list sorted_table">
                                                        <tr class="sub_item" data-id="${poItem.id}">
                                                            <td class="width_50 width_50_fix move_col">
                                                                <a href="javascript:void(0);"> <i aria-hidden="true" class="glyphicon glyphicon-sort"></i></a>
                                                            </td>
                                                            <td class="width_50 width_50_fix">
                                                                <div class="checkbox checkbox-info">
                                                                    <label> <input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${poItem.id}">
                                                                    </label>
                                                                </div>
                                                            </td>
                                                            <td class="width_100 width_100_fix"><a title="" class="Edit_btn_table" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a></td>
                                                            <td class="width_50 width_50_fix itemLevelOrder sectionNameD"><span>${poItem.level}.0</span></td>
                                                            <td class="width_200 width_200_fix align-center"><span class="item_name sectionNameD">${poItem.itemName}</span>
                                                                <c:if test="${not empty poItem.itemDescription}">
                                                                    <span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"><spring:message code="application.view.description" /></a></span>
                                                                </c:if>
                                                                <p class="s1_tent_tb_description s1_text_small">${poItem.itemDescription}</p>
                                                            </td>
                                                            <td class="align-left width_100_fix">${item.product.uom.uom != null ? item.product.uom.uom : item.unit.uom}</td>
                                                            <td class="align-right width_150 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.quantity}" /></td>
                                                            <c:set var="lockedQuantity" value="${empty item.lockedQuantity ? item.quantity : item.lockedQuantity}" />
                                                            <c:set var="balanceQuantity" value="${empty item.balanceQuantity ?item.quantity:item.balanceQuantity}" />
                                                            <c:set var="pricePerUnit" value="${empty item.pricePerUnit ?item.unitPrice:item.pricePerUnit}" />

                                                            <c:if test="${po.status ne 'DRAFT'}">
                                                                <td class="align-right width_100 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${not empty item.product.uom.uom ? lockedQuantity: item.unit.uom}" /></td>
                                                                <td class="align-right width_100 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${not empty item.product.uom.uom ? balanceQuantity: item.unit.uom}" /></td>
                                                            </c:if>

                                                            <td class="align-right width_150 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.unitPrice}" /></td>
                                                            <td class="align-right width_150 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${not empty item.product.uom.uom ? pricePerUnit: item.unit.uom}" /></td>

                                                            <c:if test="${not empty po.field1Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field2Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field2}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field3Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field3}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field4Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field4}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field5Label}">
                                                                <td class=" align-left width_100 width_100_fix">${item.field5}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field6Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field6}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field7Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field7}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field8Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field8}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field9Label}">
                                                                <td class=" align-left width_100 width_100_fix">${item.field9}&nbsp;</td>
                                                            </c:if>
                                                            <c:if test="${not empty po.field10Label}">
                                                                <td class="align-left width_100 width_100_fix">${item.field10}&nbsp;</td>
                                                            </c:if>

                                                            <td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
                                                                    <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmount}" />
                                                                </c:if>
                                                            </td>
                                                            <td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
                                                                    <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.taxAmount}" />
                                                                </c:if>
                                                            </td>
                                                            <td class="width_250 width_250_fix align-right"><c:if test="${item.order != '0' }">
                                                                    <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmountWithTax}" />
                                                                </c:if>
                                                            </td>

                                                        </tr>
                                                    </table>
                                                </div> <c:if test="${not empty poItem.children}">
                                                    <ol>
                                                        <c:forEach items="${poItem.children}" var="pochild">
                                                            <li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="${pochild.id}" data-parent="${poItem.id}">
                                                                <div class="menuDiv sub-color-change">
                                                                    <table class="table data ph_table diagnosis_list sorted_table">
                                                                        <tr class="sub_item" data-id="${pochild.id}" data-parent="${poItem.id}">
                                                                            <td class="width_50 width_50_fix move_col">
                                                                                <a href="javascript:void(0);"> <i aria-hidden="true" class="glyphicon glyphicon-sort"></i></a>
                                                                            </td>
                                                                            <td class="width_50 width_50_fix">
                                                                                <div class="checkbox checkbox-info">
                                                                                    <label> <input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="${pochild.id}">
                                                                                    </label>
                                                                                </div>
                                                                            </td>

                                                                            <td class="width_100 width_100_fix"><a title="" class="Edit_btn_table" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a></td>
                                                                            <td class="width_50 width_50_fix itemLevelOrder itemNameD"><span>${pochild.level}.${pochild.order}</span></td>
                                                                            <td class="width_200 width_200_fix"><span class="item_name itemNameD">${pochild.itemName}</span> <c:if test="${not empty pochild.itemDescription}">
                                                                                    <span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"><spring:message code="application.view.description" /></a>
                                                                                    </span>
                                                                                </c:if>
                                                                                <p class="s1_tent_tb_description s1_text_small">${pochild.itemDescription}</p>
                                                                            </td>

                                                                            <td class="width_100 width_100_fix align-center">${not empty pochild.unit ? pochild.unit.uom : pochild.product.uom.uom }</td>

                                                                            <td class="align-right width_150 width_150_fix">
                                                                                <c:if test="${po.status eq 'SUSPENDED'}">
                                                                                    <input style="text-align:right" class='form-control txtEditQtyOnSuspended' data-lockedQty="${pochild.lockedQuantity}" data-balanceQty="${pochild.balanceQuantity}"  type="number" value=<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${pochild.quantity}" /> />
                                                                                    <span class='error_qty' style='color:red;font-size:10px;'></span>
                                                                                </c:if>
                                                                                <c:if test="${po.status ne 'SUSPENDED'}">
                                                                                    <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${pochild.quantity}" />
                                                                                </c:if>
                                                                            </td>
                                                                            <!-- set param if null-->

                                                                            <c:set var="childLockedQuantity" value="${empty pochild.lockedQuantity ? pochild.quantity : pochild.lockedQuantity}" />
                                                                            <c:set var="childBalanceQuantity" value="${empty pochild.balanceQuantity ?pochild.quantity:pochild.balanceQuantity}" />
                                                                            <c:set var="childPricePerUnit" value="${empty pochild.pricePerUnit ?pochild.unitPrice:pochild.pricePerUnit}" />

                                                                            <c:if test="${po.status ne 'DRAFT'}">
                                                                                <td class="align-right width_100 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${childLockedQuantity}" /></td>
                                                                                <td class="align-right width_100 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${childBalanceQuantity}" /></td>
                                                                            </c:if>

                                                                            <td class="align-right width_150 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${pochild.unitPrice}" /></td>
                                                                            <td class="align-right width_150 width_150_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${childPricePerUnit}" /></td>

                                                                            <c:if test="${not empty po.field1Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field1}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field2Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field2}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field3Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field3}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field4Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field4}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field5Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field5}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field6Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field6}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field7Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field7}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field8Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field8}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field9Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field9}</td>
                                                                            </c:if>
                                                                            <c:if test="${not empty po.field10Label}">
                                                                                <td class="width_200 width_200_fix align_center">${pochild.field10}</td>
                                                                            </c:if>

                                                                            <td class="width_120 width_120_fix align-center">
                                                                                <!-- Total Amt -->
                                                                                <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${(pochild.unitPrice/childPricePerUnit) * pochild.quantity}" />

                                                                            </td>
                                                                            <td class="width_120 width_120_fix align-center">
                                                                                <!-- Total Tax --> <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${pochild.taxAmount}" />
                                                                            </td>
                                                                            <td class="hed_5 width_200 width_200_fix align_center totalAmountWithTax"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${pochild.totalAmountWithTax}" /></td>
                                                                        </tr>
                                                                    </table>
                                                                </div>
                                                            </li>
                                                        </c:forEach>
                                                    </ol>
                                                </c:if>
                                            </li>
                                        </c:forEach>
                                    </c:if>
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
							<td class="width_100 width_100_fix"><label id="total_bat" class="total_bat"> <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.total}" />
							</label></td>
						</tr>

							<c:choose>
								<c:when test="${po.decimal==1}">
									<c:set var="poDecimalSet" value="0,0.0"></c:set>
								</c:when>
								<c:when test="${po.decimal==2}">
									<c:set var="poDecimalSet" value="0,0.00"></c:set>
								</c:when>
								<c:when test="${po.decimal==3}">
									<c:set var="poDecimalSet" value="0,0.000"></c:set>
								</c:when>
								<c:when test="${po.decimal==4}">
									<c:set var="poDecimalSet" value="0,0.0000"></c:set>
								</c:when>
							</c:choose>
						<tr class="sub_item">
							<td class="width_50 width_50_fix "></td>

							<td class="width_50 width_50_fix"><label class="control-label additionalCharges"><spring:message code="prtemplate.additional.charges" /></label></td>

							<td class="width_50 width_50_fix align-right">
                                <textarea id="taxDescription" class="marg-top-10" row="2" cols="50" data-validation="length" data-validation-length="max150" name="taxDescription" placeholder="e.g. Import Tax">${po.taxDescription}</textarea>
							</td>
							<td class="width_100 width_100_fix align-right"><label class="control-label">(${pr.currency})</label></td>
							<td class="width_100 width_100_fix">
								<%--
								<a id="inline-tax" href="#" data-type="text" data-pk="1" >
								<label id="total_bat" class="total_bat" > <fmt:formatNumber type="number"  minFractionDigits="${po.decimal}" value="${po.additionalTax}" /></label>
							    </a> --%>
							    <fmt:formatNumber var="addtax" type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.additionalTax}" />
							    <input type="text" data-validation="custom" data-validation-regexp="^\d{0,16}(\.\d{1,${po.decimal}})?$" data-validation-ignore="," data-validation-error-msg="Only numbers and ',' allowed, length should be less then 16 and after decimal ${po.decimal} digits allowed" id="additionalTax" name="additionalTax" placeholder='<spring:message code="prtemplate.charges.amount.placeholder"/>'
								value="${po.additionalTax  > 0 ? addtax : '' }" style="width: 100%; text-align: left;" data-sanitize="numberFormat" data-sanitize-number-format="${poDecimalSet}"> <!-- <button id="addTaxButton" class="btn btn-sm btn-primary">
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
							<td class="width_100 width_100_fix"><label id="gTotal" class="gTotal"> <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.grandTotal}" />
							</label></td>
						</tr>
					</table>
				</form>

                <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                <div class="btn-next">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="table_f_action_btn">
                                <c:url var="poDelivery" value="/buyer/poDelivery/${po.id}?prId=${pr.id}" />
                                <a href="${poDelivery}" id="previousButton" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1" value="Previous" name="previous" id="priviousStep"><spring:message code="application.previous" /></a>
                                <c:url var="poPurchaseItemNext" value="/buyer/poPurchaseItemNext/${po.id}?prId=${pr.id}" />
                                <button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out"">
                                    <spring:message code="application.next" />
                                </button>

                                <spring:message code="application.draft" var="draft" />
                                <c:url var="savePoDraft" value="/buyer/savePoDraft/${po.id}" />
                                <a href="${savePoDraft}">
                                    <input type="button" id="submitStep1PoDetailDraft" class="step_btn_1 btn btn-black marg-top-20 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right" value="${draft}" />
                                </a>
                                <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                                    <a href="#confirmCancelPo" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="po.cancel.label" /></a>
                                </c:if>

                            </div>
                        </div>
                    </div>
                </div>
                </c:if>
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
				<input type="hidden" name="poId" id="poId" value="${po.id}">
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
		<form action="${pageContext.request.contextPath}/buyer/resetPoItem" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="application.confirm.reset" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">
					<label>
					    Are you sure want to Reset ? All PO Item data will be lost

					</label>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="hidden" name="poId" id="resetPoId" value="${po.id}">
					<input type="hidden" name="prId" id="resetPrId" value="${pr.id}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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
<jsp:include page="poModal.jsp"></jsp:include>
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

#poItemList .ph_table td {
	text-align: center;
	padding-left: 18px;
}

#poItemList .ph_table .itemLevelOrder {
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
#poItemList td:nth-child(2) {
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
<script>


$('document').ready(function() {

	$('#savePoSection').click(function (){
		if(!$('#addEditSectionForm').isValid()){
			return false;
			}
		});
    });

    const checkEditAvailability=(o,val,locked,balance)=>{
        if(val >  locked){
            $('.error_qty').eq(o).text('PO Quantity can not be more than Locked Quantity');
        }
        if(val >  balance){
            $('.error_qty').eq(o).text('PO Quantity can not be more than Balance Quantity');
        }

        console.log("Value : "+val+" Locked qty " + locked + " with balanceQty : " + balance)
    }

    $.each($('.txtEditQtyOnSuspended'), function(i, e) {
        $(e).on('change keyup', function() {
            const lockedQty = parseInt($(e).data('lockedqty'));
            const balanceQty = parseInt($(e).data('balanceqty'));
            const val = parseInt($(this).val());
            checkEditAvailability(i,val,lockedQty,balanceQty)
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
	var decimalLimit = ${po.decimal};
</script>	

<script type="text/javascript" src="<c:url value="/resources/js/api/Request.js?10"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prTemplate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.inputpicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/generic/generic.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/poSupplier.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/poPurchaseItem.js?12"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js?10"/>"></script>