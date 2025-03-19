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
                    <div class="content-box-wrapper">
                        <!-- <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" /> -->
                        <form:form class="bordered-row form-horizontal" data-parsley-validate="" method="post"
                            modelAttribute="supplier"
                            action="${pageContext.request.contextPath}/supplier/suppFinancialInformation">
                            <form:hidden path="id" id="supplierId" />
                            <div class="form-horizontal">
                                <h3 class="blue_form_sbtitle">
                                    <spring:message code="supplier.registration.financial.info.label" />
                                </h3>
                                <div class="form-group">
                                    <label for="idCurrencyCode" class="col-sm-3 control-label">
                                        <spring:message code="supplier.currency.code" /> :</label>
                                    <div class="col-sm-6 col-md-5">
                                        <form:select path="currency" id="idCurrencyCode" cssClass="chosen-select"
                                            data-validation="required">
                                            <form:option value="">
                                                <spring:message code="supplier.registration.currency.select" />
                                            </form:option>
                                            <form:options items="${currencyList}" itemLabel="currencyCode"
                                                itemValue="id"></form:options>
                                        </form:select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="idPaidUpCapital" class="col-sm-3 control-label">
                                        <spring:message code="supplier.registration.paidup.capital" /></label>
                                    <div class="col-sm-6 col-md-5">
                                        <form:input type="text" path="paidUpCapital" cssClass="form-control align-right"
                                            id="idPaidUpCapital" placeholder="" data-validation="length required custom"
                                            data-validation-length="0-32" data-validation-regexp="^[0-9,.]{0,32}$" />
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-offset-3 col-sm-6 col-md-5">
                                        <form:button type="submit" value="save" id="saveProfile"
                                            class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
                                            <spring:message code="application.update" />
                                        </form:button>
                                    </div>
                                </div>
                            </div>
                        </form:form>
                        <div class="row">
                            <c:set var="fileType" value="" />
                            <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
                                <c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
                            </c:forEach>
                            <span class="marg-left-10">
								<spring:message code="application.note" />:<br />
								<ul>
									<li>
										<spring:message code="createrfi.documents.max.size" />
										${ownerSettings.fileSizeLimit} MB</li>
									<li>
										<spring:message code="createrfi.documents.allowed.file.extension" />:
										${fileType}.</li>
								</ul>
							</span>
                            <div class="col-sm-12">
                                <h3 class="blue_form_sbtitle">
                                    <spring:message code="supplier.registration.financial.info.label2" />
                                </h3>
                                <div class="col-xs-12 col-sm-6">
                                    <section class="">
                                        <form id="financialDocumentsForm">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <div class="add_file_row">
                                                <div data-provides="fileinput"
                                                    class="fileinput fileinput-new input-group">
                                                    <div data-trigger="fileinput" class="form-control">
                                                        <i class="glyphicon glyphicon-file fileinput-exists"></i>
                                                        <span id="idfinancialDocumentsSpan"
                                                            class="fileinput-filename show_name2"></span>
                                                    </div>
                                                    <span class="input-group-addon btn btn-black btn-file">
                                                        <span class="fileinput-new">Select file</span>
                                                        <span class="fileinput-exists">Change</span>
                                                        <input type="file" data-buttonName="btn-black"
                                                            id="financialDocuments" name="financialDocuments"
                                                            data-buttonName="btn-black"
                                                            data-validation-allowing="${fileType}"
                                                            data-validation-error-msg-container="#Load_File-error-dialog"
                                                            data-validation-max-size="${ownerSettings.fileSizeLimit}M"
                                                            data-validation="extension size"
                                                            data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
                                                            data-validation-error-msg-mime="${mimetypes}" />
                                                    </span>
                                                    <a data-dismiss="fileinput"
                                                        class="input-group-addon btn btn-default fileinput-exists"
                                                        href="#">Remove</a>
                                                </div>
                                            </div>
                                            <div id="Load_File-error-dialog"
                                                style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
                                            <div class="form-group" style="margin-left: 0; margin-right: 0;">
                                                <textarea class="form-control" rows="3" id="financialDocDesc"
                                                    data-validation="length" data-validation-length="0-250"
                                                    name="financialDocDesc" placeholder="Enter Description"></textarea>
                                            </div>
                                            <div class="form-group other_attachemts"
                                                style="margin-left: 0; margin-right: 0;">
                                                <button class="btn btn-gray btn-lg btn-block up_btn" type="button"
                                                    name="financialDocumentsUpload"
                                                    id="financialDocumentsUpload">Upload</button>
                                            </div>
                                        </form>
                                    </section>
                                </div>
                                <div class="col-xs-12 col-sm-6">
                                    <section class="step4_table first-table-marg">
                                        <div class="mega h-140">
                                            <table class="table header" style="min-height: 50px;">
                                                <thead>
                                                    <tr>
                                                        <th class="width-33">File name</th>
                                                        <th class="width-33">Description</th>
                                                        <th class="width-33">Upload Date</th>
                                                    </tr>
                                                </thead>
                                            </table>
                                            <table class="data for-pad-data" id="financialDocumentsDisplay">
                                                <tbody>
                                                    <c:forEach items="${uploadFinancialDocuments}" var="sp">
                                                        <tr>
                                                            <td class="width-33">
                                                                <form:form method="GET">
                                                                    <c:url var="download"
                                                                        value="/downloadFinancialDocuments/${sp.id}" />
                                                                    <a class="word-break" href="${download}">${sp.fileName}</a>
                                                                </form:form>
                                                            </td>
                                                            <td class="width-33">${sp.description}&nbsp;</td>
                                                            <td class="width-33">
                                                                <span class="removeFinancialDocsFile"
                                                                    removeFinancialDocsId='${sp.id}'
                                                                    financialDocsFileName='${sp.fileName}'>
                                                                    <span class="col-sm-10 no-padding">
                                                                        <fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
                                                                    </span>
                                                                    <span class="col-sm-2 no-padding align-right">
                                                                        <a>
                                                                            <i class="fa fa-trash-o"
                                                                                aria-hidden="true"></i>
                                                                        </a>
                                                                    </span>
                                                                </span>
                                                            </td>
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
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/file.js" />"></script>
<script src="<c:url value="/resources/js/view/suppFinancialDetails.js" />"></script>

<style>
    .align-right {
        text-align: right;
    }

    .width-33 {
        width: 33%;
    }

    .no-padding {
        padding: 0;
    }

    .word-break {       
	    word-break: break-all;
    }

</style>


<script>
    $.validate({
        lang: 'en',
        modules: 'file'
    });


</script>