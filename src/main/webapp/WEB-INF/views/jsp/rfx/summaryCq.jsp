<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseSix"> <spring:message code="questionnaire.label" /> </a>
		</h4>
	</div>
	<div id="collapseSix" class="panel-collapse collapse">
		<div class="panel-body pad_all_15">
			<c:forEach items="${cqList}" var="cq">
				<div class="panel sum-accord">
					<div class="panel-heading">
						<h4 class="panel-title"> 
							<a data-toggle="collapse" data-parent="#accordion1" class="accordion" href="#collapse_cq_${cq.id}">${cq.name}</a>
							<button class="sixbtn"></button>
						</h4>
					</div>
					<div id="collapse_cq_${cq.id}" class="panel-collapse collapse in" aria-expanded="true">
						<div class="panel-body pad_all_15">
							<h3>${cq.description}</h3>
							<div class="panel-body-inner main_table_wrapper">
								<table class="table ph_table table-marg-set" width="100%" border="0" cellspacing="0" cellpadding="0">
									<tbody>
										<c:forEach items="${cq.cqItems}" var="item">
											<tr class="<c:if test="${item.order > 0}">sub_item</c:if><c:if test="${item.order == 0}"> border-top-width-1</c:if>">
												<td class="for-left minimizePadding">
													<div class="col-md-12 <c:if test="${item.cqType == 'CHOICE' || item.cqType == 'LIST' || item.cqType == 'CHECKBOX'}">marg-bottom-10</c:if>">
														<div class="row">
															<span class="col-md-1 col-sm-1 col-xs-2 pull-left disablePaddings">${item.optional ? '*' : ''} 
																<c:if test="${item.order == 0}"><h5><b>${item.level}.${item.order}</b></h5></c:if>        <%-- Ph-1660 --%>  
																<c:if test="${item.order > 0}"><b>${item.level}.${item.order}</b></c:if>           <%-- Ph-1660 --%>  
<%-- 															${item.level}.${item.order} --%>
															</span> 
															<span class="col-md-11 col-xs-10 col-sm-11 pull-left disablePaddings">
																<c:if test="${item.order == 0}"><h5><b>${item.itemName}</b></h5></c:if>
																<c:if test="${item.order > 0}"><b>${item.itemName}</b></c:if>
																${item.attachment ? ( item.isSupplierAttachRequired ? ' (Attachment is Required) ' : ' (Attachment is Optional) ' ) : ''} <c:if test="${item.order > 0}">(${item.cqType.value}) </c:if><br>
															<span class="item_detail s1_text_small">${item.itemDescription}</span>
															</span>
														</div>
													</div>
													<c:if test="${item.cqType == 'CHOICE' || item.cqType == 'LIST' || item.cqType == 'CHECKBOX' || item.cqType == 'TEXT' || item.cqType == 'CHOICE_WITH_SCORE' || item.cqType == 'DATE'}">
														<div class="col-md-12">
															<div class="row">
															<span class="col-md-1 col-sm-1 col-xs-2 pull-left disablePaddings">&nbsp;</span>
															<span class="col-md-11 col-xs-10 col-sm-11 pull-left disablePaddings">
															<div class="row">
															<c:if test="${item.cqType == 'CHOICE' || item.cqType == 'LIST'}">
															<ul>
																<c:forEach var="cqOptions" items="${item.cqOptions}">
																	<li > 
																	
																		<span>${cqOptions.value}</span>
																	 </li>
																</c:forEach></ul>
															</c:if>
															<c:if test="${item.cqType == 'CHOICE_WITH_SCORE'}">
																<ul><c:forEach var="cqOptions" items="${item.cqOptions}">
																	<!-- <div class="col-md-2"> -->
																
																	 <li > 
																		<span>${cqOptions.value}</span>
																	 </li>
																	
																	<%-- <label class="select-radio">
																		<input type="radio" id="showAll" name="example-radio" value="${cqOptions.value}" class="custom-radio">
																		<span>${cqOptions.value}/${cqOptions.scoring}</span>
																	</label> --%>
																	<!-- </div> -->
																</c:forEach></ul>
															</c:if>
															<c:if test="${item.cqType == 'CHECKBOX'}">
																<ul><c:forEach var="cqOptions" items="${item.cqOptions}">
																	<!-- <div class="col-md-2"> -->
																<li > 
																		<span>${cqOptions.value}</span>
																	 </li>
																	
																	<%-- <label class="select-checkbox">
																		<input type="checkbox" id="showAll" name="example-checkbox" value="${cqOptions.value}" class="custom-checkbox">${cqOptions.value}
																	</label> --%>
																<!-- 	</div> -->
																</c:forEach></ul>
															</c:if>
															<c:if test="${item.cqType == 'TEXT'}">
																<div class="col-md-5 col-sm-5 col-xs-6">
																	<!-- <div class="col-md-5 col-sm-5 col-xs-6"><b>Ans:</b></div> -->
																</div>
															</c:if>
													 		<c:if test="${item.cqType == 'DATE'}">
																<div class="col-md-5 col-sm-5 col-xs-6">
																
																	<!-- <div class="col-md-5 col-sm-5 col-xs-6"><b>Ans:</b></div> -->
																</div>
															</c:if> 
															</div>
															</span>
															</div>
														</div>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
				<%-- <div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
										<div class="meeting2-heading">
											<h3>${cq.name}</h3>
										</div>
									</div> 
									<div class="import-supplier-inner-first-new global-list form-middle">
										<div class="ph_tabel_wrapper">
											<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
												
											</div>
										</div>
									</div> --%>
			</c:forEach>
			<style>
			.disablePaddings{padding:0;}
			.disablePaddings label{color:#333!important; font-size:12px!important;}
			.disablePaddings label span {
			    font-size: 12px!important;
			}
			.col-md-1.disablePaddings{max-width:50px;}
			.minimizePadding{padding:5px;}
			</style>
		</div>
	</div>
</div>