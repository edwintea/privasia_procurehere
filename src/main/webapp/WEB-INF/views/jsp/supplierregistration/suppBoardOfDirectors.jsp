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
                <div class="content-box">
                    <!-- <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" /> -->
                    <h3 class="content-box-header">
                        <spring:message code="application.org.details" />
                    </h3>
                    <form:form id="boardOfDirectorForm" class="form-horizontal" data-parsley-validate="" method="post"
                        modelAttribute="boardOfDirector"
                        action="${pageContext.request.contextPath}/supplier/supplierOrganizationalDetails">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="form-group" style="display: none;">
                            <label for="id" class="col-sm-3 control-label">Id</label>
                            <div class="col-sm-6 col-md-5">
                                <form:input type="text" path="id" cssClass="form-control" name="id" placeholder=""
                                    id="directorId" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dirName" class="col-sm-3 control-label">Director's Name</label>
                            <div class="col-sm-6 col-md-5">
                                <form:input type="text" path="directorName" cssClass="form-control" id="idDirectorName"
                                    name="idDirectorName" placeholder="" data-validation="length required custom"
                                    data-validation-length="0-64" data-validation-regexp="^[A-Za-z-\/'' '.]{0,64}$" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="idType" class="col-sm-3 control-label">Identification Type :</label>
                            <div class="col-sm-6 col-md-5">
                                <form:select path="idType" id="idType" cssClass="chosen-select"
                                    data-validation="required">
                                    <form:option value="">Select Identification Type</form:option>
                                    <form:option value="National Identification Card (IC)">National Identification Card
                                        (IC)</form:option>
                                    <form:option value="Passport">Passport</form:option>
                                    <form:option value="Social Security Card">Social Security Card</form:option>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="idNumber" class="col-sm-3 control-label">Identification Number :</label>
                            <div class="col-sm-6 col-md-5">
                                <form:input type="text" path="idNumber" cssClass="form-control" id="idNumber"
                                    name="idNumber" placeholder="" data-validation="length required custom"
                                    data-validation-length="0-32" data-validation-regexp="^[A-Za-z0-9-\/]{0,32}$" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dirType" class="col-sm-3 control-label">Type of Director :</label>
                            <div class="col-sm-6 col-md-5">
                                <form:select path="dirType" id="idDirType" cssClass="chosen-select"
                                    data-validation="required">
                                    <form:option value="">Select Type of Director</form:option>
                                    <form:option value="Executive">Executive</form:option>
                                    <form:option value="Non-Executive">Non-Executive</form:option>
                                    <form:option value="Managing">Managing</form:option>
                                    <form:option value="Independent">Independent</form:option>
                                    <form:option value="Others">Others</form:option>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="idNumber" class="col-sm-3 control-label">Email Address :</label>
                            <div class="col-sm-6 col-md-5">
                                <form:input type="email" path="dirEmail" cssClass="form-control" id="idDirEmail"
                                    name="dirEmail" placeholder="" data-validation="length email"
                                    data-validation-length="0-64" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dirContact" class="col-sm-3 control-label">Contact Number </label>
                            <div class="col-sm-6 col-md-5">
                                <form:input type="text" path="dirContact" cssClass="form-control" id="idDirContact"
                                    name="dirContact" placeholder="" data-validation="length custom"
                                    data-validation-length="0-24" data-validation-regexp="^[0-9-+]{0,24}$" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"></label>
                            <div class="col-sm-6 col-md-5">
                                <form:button type="submit"
                                    class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium" id="addNewDir">Add
                                    New</form:button>
                                <form:button type="submit"
                                    class="btn ph_btn_midium hvr-pop hvr-rectangle-out disabled" disabled="true"
                                    id="saveDir">Save
                                </form:button>
                            </div>
                        </div>
                    </form:form>
                    <div>
                        <div>
                            <section class="step4_table">
                                <div class="mega">
                                    <table class="table" id="directorsDisplay">
                                        <thead>
                                            <tr>
                                                <th>Action</th>
                                                <th>No.</th>
                                                <th>Director's Name</th>
                                                <th>Identification type</th>
                                                <th>Identification Number</th>
                                                <th>Type of Director</th>
                                                <th>Email Address</th>
                                                <th>Contact Number</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${boardOfDirectors}" var="sp" varStatus="loop">
                                                <tr>
                                                    <td>
                                                        <div>
                                                            <span class="col-sm-6 p-l-0 no-padding" id="showConfirmDeletePopUp"
                                                                delete-id="${sp.id}" delete-name="${sp.directorName}">
                                                                <a>
                                                                    <i class="fa fa-trash-o" aria-hidden="true"></i>
                                                                </a>
                                                            </span>
                                                            <span class="col-sm-6 p-l-0 no-padding" id="editDirector"
                                                                edit-id="${sp.id}">
                                                                <a>
                                                                    <i class="fa fa-edit" aria-hidden="true"></i>
                                                                </a>
                                                            </span>
                                                        </div>
                                                    </td>
                                                    <td>${loop.count}</td>
                                                    <td>${sp.directorName}</td>
                                                    <td>${sp.idType}</td>
                                                    <td>${sp.idNumber}</td>
                                                    <td>${sp.dirType}</td>
                                                    <td>${sp.dirEmail}</td>
                                                    <td>${sp.dirContact}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </section>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade hideModal" id="confirmDeleteDirector" role="dialog">
                <div class="modal-dialog for-delete-all reminder documentBlock">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>
                                <spring:message code="application.confirm.delete" />
                            </h3>
                            <button class="close for-absulate" id="confirmDeleteDirectorDismiss" type="button"
                                data-dismiss="modal">X</button>
                        </div>
                        <div class="modal-body">
                            <label>
                                Are you sure you want to delete this record ?
                            </label>
                            <input type="hidden" />
                        </div>
                        <div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
                            <button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"
                                id="confDelDir">
                                <spring:message code="application.delete" />
                            </button>
                            <button type="button" id="confirmDeleteDirectorClose"
                                class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right"
                                data-dismiss="modal">
                                <spring:message code="application.cancel" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<style>
    .showModal {
        display: block;
        background: #ffffffa6;
    }

    .hideModal {
        display: none;
    }

    .no-padding
    {
        padding: 0;
    }
</style>
<script src="<c:url value="/resources/js/view/suppBoardOfDirectors.js"/>"></script>
<script>
    $.validate({
        lang: 'en',
        modules: ''
    });
</script>