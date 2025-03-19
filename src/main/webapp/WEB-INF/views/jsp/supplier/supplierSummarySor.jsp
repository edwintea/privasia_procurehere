<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${! empty sorList}">
    <div class="panel sum-accord">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" class="accordion" href="#collapseSor"> <spring:message
                        code="eventdescription.schedule.rate.label"/>
                </a>
            </h4>
        </div>
        <div id="collapseSor" class="panel-collapse collapse">
            <div class="panel-body pad_all_15">
                <c:forEach items="${sorList}" var="suppBq">
                    <div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
                        <div class="meeting2-heading">
                            <h3>${suppBq.name}</h3>
                        </div>
                        <div class="import-supplier-inner-first-new global-list form-middle">
                            <div class="ph_tabel_wrapper">
                                <div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
                                    <table class="ph_table_sor border-none header" width="100%" border="0" cellspacing="0"
                                           cellpadding="0" style="top: 0px;">
                                        <thead>
                                        <tr>
                                            <th class="for-left width_50 width_50_fix"><spring:message
                                                    code="application.no2"/></th>
                                            <th class="for-left width_100 width_100_fix"><spring:message
                                                    code="label.rftbq.th.itemname"/></th>
                                            <th class="for-left width_100 width_100_fix"><spring:message
                                                    code="label.uom"/></th>
                                            <th class=" align-center width_100 width_100_fix">Rate</th>
                                            <c:if test="${not empty suppBq.field1Label and suppBq.field1ToShowSupplier}">
                                                <th class=" align-center width_100 width_100_fix">${suppBq.field1Label}</th>
                                            </c:if>
                                        </tr>
                                        </thead>
                                    </table>

                                    <table class="ph_table_sor data border-none" width="100%" border="0" cellspacing="0"
                                           cellpadding="0">
                                        <tbody>
                                        <c:forEach items="${suppBq.supplierSorItems}" var="item">
                                            <tr>
                                                <td class="for-left width_50 width_50_fix">
                                                    <c:if test="${item.order== 0}">
                                                        <h5>${item.level}.${item.order}</h5></c:if>
                                                    <c:if test="${item.order > 0}">${item.level}.${item.order} </c:if>
                                                </td>
                                                <td class="for-left width_100 width_100_fix" align="center">
													<span class="item_name">
														<c:if test="${item.order == 0}"><h5>${item.itemName}</h5></c:if>
														<c:if test="${item.order > 0}">${item.itemName} </c:if>
														</span>
                                                    <c:if test="${not empty item.itemDescription}">
                                                        <span data-toggle="collapse" data-target="#demo-${item.id}"
                                                              class="s2_view_desc"><spring:message
                                                                code="application.view.description"/></span>
                                                    </c:if></td>

                                                <td class="for-left width_100 width_100_fix"
                                                    align="center">${item.uom.uom}</td>
                                                <td class=" align-center width_100 width_100_fix"><c:if
                                                        test="${item.order != '0' }">
                                                    <fmt:formatNumber type="number" minFractionDigits="${event.decimal}"
                                                                      maxFractionDigits="${event.decimal}"
                                                                      value="${item.totalAmount}"/>
                                                </c:if>
                                                </td>
                                                <c:if test="${not empty suppBq.field1Label and suppBq.field1ToShowSupplier}">
                                                    <td class=" align-center width_100 width_100_fix">${item.field1}&nbsp;</td>
                                                </c:if>
                                            </tr>
                                            <tr id="#demo-${item.id}" style="display: none;" class="collapse">
                                                <td style="border-top: 0 !important;"></td>
                                                <td colspan="${suppBq.headerCount}" style="border-top: 0 !important;">
                                                    <span class="item_detail s2_text_small">${item.itemDescription}</span>
                                                </td>

                                            </tr>

                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>
<style>
    .ph_table_sor tr:first-child {

    }

    .ph_table_sor.data td:first-child {
        padding: 30px 10px 15px 10px;
    }

    .ph_table_sor.data td:nth-child(2) {
        padding: 30px 10px 15px 10px;
    }

    .s2_text_small {
        margin: 5px 0 0 0;
        font-size: 11px;
        display: none;
        max-height: 150px;
        text-align: left;
        margin-top: -1%;
    }

    .ph_table_sor.header th {
        padding: 17px 10px;
        font-family: 'Open Sans', sans-serif;
        font-weight: 600;
    }

    .pad_all_12 {
        padding: 12px;
    }
</style>
