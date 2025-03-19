<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="poDetailsDesk" code="application.po.create.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDetailsDesk}] });
});
</script>

<!-- cancel po popup  -->
<div class="modal fade modal_cancel_po" id="confirmCancelPo" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.cancel" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPo" method="POST">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="poId" value="${po.id}">
                <div class="col-md-12">
                    <div class="row">
                        <div class="modal-body col-md-12">
                            <label>
                                Are you sure you want to cancel this PO ?
                            </label>
                        </div>

                        <input type="hidden" name="prId" value="${pr.id}" />

                        <div class="form-group col-md-6">
                                <spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
                            <textarea class="width-100"
                                placeholder="${reasonCancel}" rows="3"
                                name="remarks" id="cancelReason"
                                data-validation="required length"
                                data-validation-length="max1000"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
                    <input type="submit" id="cancelPo" class="cancelPo btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
                    <button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
                </div>
            </form>
		</div>
	</div>
</div>

<!-- Revise po popup  -->
<div class="modal fade confirm_revise_po" id="confirmRevisePo" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.suspendPO" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="suspendPoForm" method="post">
				<input type="hidden" name="poId" value="${po.id}">
				<input type="hidden" name="prId" value="${pr.id}">
				 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label style="font-size:90%;"> <spring:message code="posummary.sure.want.suspendPo" /></label>
						</div>
						<br>
						<div class="form-group col-md-6">
							<spring:message code="po.reason.revise.justification.placeholder" var="reasonJestification" />
							<textarea class="width-100" placeholder="${reasonJestification}" rows="3" name="remarks" id="reviseJustification" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="suspendPo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- purchase item form-->
<div id="crateNewSection" class="modal fade modal_purchase_item" role="dialog">
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
					<button class="btn btn-info hvr-pop hvr-rectangle-out  ph_btn_midium" id="savePoSection" type="button" data-dismiss="modal">Create</button>
					<button class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form>
</div>

<div id="crateColumnSection" class="modal fade modal_purchase_item" role="dialog">
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
									<c:if test="${not empty po.field1Label}">
										<tr class="addColumsBlock" data-pos="1">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field1Label" name="field1" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field1Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove1" name="fieldRemove1"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field2Label}">
										<tr class="addColumsBlock" data-pos="2">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field2Label" name="field2" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field2Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove2" name="fieldRemove2"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field3Label}">
										<tr class="addColumsBlock" data-pos="3">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field3Label" name="field3" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field3Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove3" name="fieldRemove3"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field4Label}">
										<tr class="addColumsBlock" data-pos="4">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field4Label" name="field4" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field4Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove4" name="fieldRemove4"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>

									<c:if test="${not empty po.field5Label}">
										<tr class="addColumsBlock" data-pos="5">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field5Label" name="field5" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field5Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove5" name="fieldRemove5"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field6Label}">
										<tr class="addColumsBlock" data-pos="6">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field6Label" name="field6" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field6Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove6" name="fieldRemove6"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field7Label}">
										<tr class="addColumsBlock" data-pos="7">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field7Label" name="field7" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field7Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove7" name="fieldRemove7"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field8Label}">
										<tr class="addColumsBlock" data-pos="8">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field8Label" name="field8" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field8Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove8" name="fieldRemove8"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>
									<c:if test="${not empty po.field9Label}">
										<tr class="addColumsBlock" data-pos="9">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field9Label" name="field9" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field9Label}">
												</div>
											</td>
											<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove9" name="fieldRemove9"> <i aria-hidden="true" class="glyph-icon icon-close"></i>
											</a></td>
										</tr>
									</c:if>

									<c:if test="${not empty po.field10Label}">
										<tr class="addColumsBlock" data-pos="10">
											<td class="width_300 width_300_fix s1-mrg-10">
												<div class="form-group ">
													<input type="text" class="form-control" id="field10Label" name="field10" data-validation="required length" data-validation-length="max32" placeholder='<spring:message code="prtemplate.name.placeholder" />' value="${po.field10Label}">
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

<div id="crateNewItem" class="modal fade modal_purchase_item" role="dialog">
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
						<input type="hidden" name="prId" id="prId" value="${pr.id}">
						<input type="hidden" name="parentId" id="parentId" class="parentId" value="">
						<input type="hidden" name="itemId" id="itemId" class="itemId" value="">
						<div class="row">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<spring:message code="event.document.itemname" var="itemName" />
							<spring:message code="event.document.quantity" var="quantity" />
							<spring:message code="event.document.unitpricing" var="unitpricing" />
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
												<label><spring:message code="label.rftbq.th.itemname" /></label>
												<input type="text" name="itemName" class="form-control " autocomplete="off" id="itemName" data-validation="required length" data-validation-length="max1000" placeholder='<spring:message code="prtemplate.start.search.placeholder" />'>
											</div>
											<div class="form-group" id="textInputPickerDiv" style="display: block;">
												<label><spring:message code="label.rftbq.th.itemname" /></label>
												<input type="text" name="itemName" class="form-control itemNameText" autocomplete="off" data-validation="required length" data-validation-length="max1000"  id="itemNameText" placeholder='<spring:message code="prtemplate.start.search.placeholder" />'>
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
									</div>
								</div>
								<c:choose>
									<c:when test="${po.decimal==1}">
										<c:set var="decimalSet" value="0,0.0"></c:set>
									</c:when>
									<c:when test="${po.decimal==2}">
										<c:set var="decimalSet" value="0,0.00"></c:set>
									</c:when>
									<c:when test="${po.decimal==3}">
										<c:set var="decimalSet" value="0,0.000"></c:set>
									</c:when>
									<c:when test="${po.decimal==4}">
										<c:set var="decimalSet" value="0,0.0000"></c:set>
									</c:when>
								</c:choose>
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group">
										<label><spring:message code="label.rftbq.th.quantity" /></label>
										<input type="text" data-validation="required length" data-validation-regexp="^\d{1,16}(\.\d{1,${po.decimal}})?$" data-validation-ignore=",." name="quantity" class="validate form-control itemValue" id="itemQuantity" placeholder='<spring:message code="event.document.quantity" />' data-validation-length="1-22">
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group">
										<label><spring:message code="product.list.tax" />&nbsp;(%)</label>
										<input type="text"
										    data-validation="required length"
										    data-validation-regexp="^\d{1,16}(\.\d{1,${po.decimal}})?$"
										    data-validation-ignore=",."
										    id="itemTax"
										    name="itemTax"
										    placeholder='<spring:message code="prtemplate.enter.tax.placeholder" />'
										    class="validate form-control itemValue"
										    data-validation-length="1-22" />
									</div>
									<div class="form-group" id="idDescriptionDiv">
                                        <label class="marg-bottom-10"><spring:message code="application.description" /></label>
                                        <textarea class="form-control" id="itemDescription" name="itemDescription"
                                                  placeholder='<spring:message code="event.document.description" />'
                                                  data-validation="length" data-validation-length="max2000"></textarea>
                                        <span class="sky-blue"><spring:message
                                                code="createrfi.event.description.max.chars"/></span>
                                    </div>
								</div>
								<div class="col-md-6 col-sm-6 col-xs-12 " id="itemUnitPriceDiv">
									<div class="form-group">
										<label class="marg-bottom-10"><spring:message code="product.unit.price" /></label>
										<input type="text" data-validation="required length" data-validation-regexp="^\d{1,16}(\.\d{1,${po.decimal}})?$" data-validation-ignore=",." class="validate form-control itemValue" name="unitPrice" id="itemUnitPrice" placeholder='<spring:message code="event.document.unitpricing" />' data-validation-length="1-22">
									</div>
									<div class="form-group">
                                        <label class="marg-bottom-10"><spring:message code="product.price.per.unit" /></label>
                                        <input type="text"
                                            data-validation="number required length"
                                            data-validation-regexp="^\d{1,16}(\.\d{1,${po.decimal}})?$"
                                            data-validation-ignore=",."
                                            id="pricePerUnit"
                                            name="pricePerUnit"
                                            placeholder='<spring:message code="event.document.priceperunit" />'
                                            class="validate form-control itemValue"
                                            data-validation-length="1-22"
                                            value=${pricePerUnit}
                                            />
                                    </div>
								</div>

							</div>

							<div class="row extraFields" id="extraFields"></div>

						</div>
					</section>
				</div>
				<div class="modal-footer  text-center">
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

<!-- end purchase item form-->