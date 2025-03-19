<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreateDocuments" code="application.rfx.create.documents" />
<script>
    zE(function() {
        zE.setHelpCenterSuggestions({ labels: [${rfxCreateDocuments}] });
    });
</script>
    <div id="page-content-wrapper">
        <div id="page-content">
            <div class="container">
                <!-- pageging  block -->
                <ol class="breadcrumb">
                    <li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
                        <spring:message code="application.dashboard" />
                    </a></li>
                    <li class="active">${eventType.value}</li>
                </ol>
                <section class="create_list_sectoin">
                    <div class="Section-title title_border gray-bg mar-b20">
                        <h2 class="trans-cap tender-request-heading"><spring:message code="rfxTemplate.administration" /></h2>
                        <h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${event.status}</h2>
                    </div>
                    <jsp:include page="rfxTemplateHeader.jsp" />
                    <div class="tab-pane" style="display: block">
                    <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
                    <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                    <div class="Invited-Supplier-List white-bg">
                        <h4>Documents</h4>
                        <c:set var="fileType" value="" />
                        <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
                            <c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
                        </c:forEach>
                        <form:form class="form-horizontal" id="rftDocumentForm">
                            <input type="hidden" value="${event.id}" id="eventId" name="eventId">
                            <input type="hidden" name="${_csrf.parameterName}"
                                   value="${_csrf.token}" />
                            <div class="row">
                                <div class="col-md-12 ${isTemplateUsed ? 'disabled':''}">
                                    <div class="col-md-4 pad_all_15">
                                        <div data-provides="fileinput" class="fileinput fileinput-new input-group">
                                            <spring:message code="event.doc.file.required" var="required" />
                                            <spring:message code="event.doc.file.length" var="filelength" />
                                            <spring:message code="event.doc.file.mimetypes" var="mimetypes" />
                                            <div data-trigger="fileinput" class="form-control">
                                                <span class="fileinput-filename show_name"></span>
                                            </div>
                                            <span class="input-group-addon btn btn-black btn-file">
												<span class="fileinput-new">
													<spring:message code="event.document.selectfile" />
												</span>
												<span class="fileinput-exists">
													<spring:message code="event.document.change" />
												</span>
												<input data-validation-allowing="${fileType}"
                                                       data-validation="extension size"
                                                       data-validation-error-msg-container="#Load_File-error-dialog"
                                                       data-validation-max-size="${ownerSettings.fileSizeLimit}M"
                                                       type="file" name="rftUploadDocument"
                                                       id="rftUploadDocument"
                                                       data-validation-error-msg-required="${required}"
                                                       data-validation-error-msg-size="You cannot upload file larger than ${ownerSettings.fileSizeLimit}MB"
                                                       data-validation-error-msg-mime="${mimetypes}">
											</span>
                                        </div>
                                        <div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
                                        <div class="progressbar flagvisibility" data-value="0">
                                            <div class="progressbar-value bg-purple">
                                                <div class="progress-overlay"></div>
                                                <div class="progress-label">0%</div>
                                            </div>
                                        </div>
                                        <span>
											<spring:message code="application.note" />:<br />
											<ul>
												<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
												<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
                                                <li>Max number of pre-set files allowed is 5</li>
											</ul>
										</span>
                                    </div>
                                    <div class="col-md-5 pad_all_15">
                                        <spring:message code="event.doc.file.descrequired" var="descrequired" />
                                        <spring:message code="event.doc.file.maxlimit" var="maxlimit" />
                                        <spring:message code="event.document.filedesc" var="filedesc" />
                                        <input class="form-control"
                                               name="docDescription" id="docDescription"
                                               data-validation="length" data-validation-length="max250"
                                               type="text" placeholder="${filedesc} ${maxlimit}">
                                    </div>
                                    <div class="col-md-1 pad_all_15">
                                        <button class="upload_btn btn btn-info   hvr-pop hvr-rectangle-out" type="button" name="uploadRftDoc" id="uploadRftDoc">
                                            <spring:message code="event.document.upload" />
                                        </button>
                                    </div>
                                    <div class="col-md-2 pad_all_15" style="">
                                        <label style="margin-top: 5%;"> <spring:message code="eventDocument.internal.document" /></label> &nbsp;&nbsp;
                                        <input id="internal" value="internal" class="internal" type="checkbox" ${isChecked ? 'checked' : ''} />
                                    </div>
                                </div>
                            </div>
                        </form:form>
                        <div class="Invited-Supplier-List-table pad_all_15 add-supplier">
                            <div class="ph_table_border">
                                <div class="mega">
                                    <table class="header ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <thead>
                                        <tr>
                                            <th class="width_200 width_200_fix align-center"><spring:message code="application.action" /></th>
                                            <th class="width_200 width_200_fix align-left"><spring:message code="event.document.filename" /></th>
                                            <th class="width_300 width_300_fix align-left"><spring:message code="event.document.description" /></th>
                                            <th class="width_100 width_100_fix align-left"><spring:message code="event.document.fileSize" /></th>
                                            <th class="width_200 width_200_fix align-left"><spring:message code="event.document.publishdate" /></th>
                                            <th class="width_200 width_200_fix align-left"><spring:message code="event.document.uploadby" /></th>
                                            <th class="width_100 width_100_fix align-left"><spring:message code="event.document.internal" /></th>
                                        </tr>
                                        </thead>
                                    </table>
                                    <table class="data ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0" id="rftDocList">
                                        <tbody>
                                        <c:forEach var="doc" items="${event.documents}">
                                            <tr>
                                                <td class="width_200 width_200_fix align-center">
                                                    <form method="GET">
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                        <a id="downloadDocument" href="${pageContext.request.contextPath}/buyer/downloadRfxTemplateDocument/${doc.id}" data-placement="top" title='<spring:message code="tooltip.download2" />'>
                                                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/resources/images/download.png">
                                                        </a>
                                                        <c:if test="${!isTemplateUsed}">
                                                        <span class="removeDocFile" removeDocId=" ${doc.id}">
																<a href="" data-placement="top" title='<spring:message code="tooltip.delete" />'>
																	<img src="${pageContext.request.contextPath}/resources/images/delete.png">
																</a>
															</span>
                                                        &nbsp;
                                                        <span class="editDocFile" editDocId=" ${doc.id}" eventDocDesc="<c:out value='${doc.description}'/>" eventDocInternal="${doc.internal}">
																<a href="" data-placement="top" title='<spring:message code="tooltip.edit" />'>
																	<img src="${pageContext.request.contextPath}/resources/images/edit1.png">
																</a>
															</span>
                                                    </c:if>
                                                    </form>
                                                </td>
                                                <td class="width_200 width_200_fix align-left">${doc.fileName}</td>
                                                <td class="width_300 width_300_fix align-left" id="desc">${doc.description}</td>
                                                <td class="width_100 width_100_fix align-left" id="size">
                                                    <fmt:formatNumber type="number" maxFractionDigits="1" value="${doc.fileSizeInKb}" />KB
                                                </td>
                                                <td class="width_200 width_200_fix align-left">
                                                    <fmt:formatDate value="${doc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
                                                </td>
                                                <td class="width_200 width_200_fix align-left">${doc.uploadBy.name}</td>
                                                <td class="width_100 width_100_fix align-left">
                                                    <c:if test="${doc.internal == true}">
                                                        <spring:message code="eventDocument.document.internal" />
                                                    </c:if>
                                                    <c:if test="${doc.internal == false}">
                                                        <spring:message code="event.document.external" />
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
                    <div class="marg-top-20 btns-lower">
                        <div class="row">
                            <div class="col-md-12 col-xs-12 col-ms-12">
                                <a href="<c:url value="/buyer/rfxTemplate/editRfxTemplate?id=${event.id}" />" id="previousButton"
                                      class="btn1 step_btn_1 btn btn-black ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1 pull-left" style="margin-right: 10px;">
                                       <spring:message code="application.previous" />
                                   </a>
                                <a href="<c:url value="/buyer/rfxTemplate/finishTemplate/${event.id}" />" id="finishButton"
                                   class="btn1 step_btn_1 btn btn-info ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1 ">
                                    <spring:message code="application.finish" />
                                </a>
                                <a href="<c:url value="/buyer/rfxTemplate/rfxTemplateList" />" id="cancelButton"
                                   class="btn1 step_btn_1 btn btn-black ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1 pull-right">
                                    <spring:message code="application.cancel" />
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="modal fade" id="confirmDeleteDocuemnt" role="dialog">
    <div class="modal-dialog for-delete-all reminder documentBlock">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3>
                    <spring:message code="application.confirm.delete" />
                </h3>
                <button class="close for-absulate" type="button" data-dismiss="modal">x</button>
            </div>
            <div class="modal-body">
                <label><spring:message code="rfaevent.confirm.delete.document" /> </label> <input type="hidden" id="deleteIdDocument" />
            </div>
            <div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
                <button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDocument">
                    <spring:message code="application.delete" />
                </button>
                <button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
                    <spring:message code="application.cancel" />
                </button>
            </div>
        </div>
    </div>
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelEvent" role="dialog">
    <div class="modal-dialog for-delete-all reminder">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3><spring:message code="eventsummary.confirm.cancel" /></h3>
                <button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
            </div>
        </div>
    </div>
</div>
<!-- EVENT DOCUMENT DESCRIPTION -->
<div class="flagvisibility dialogBox" id="documentDescriptionPopup" title="Event Document Decription">
    <div class="float-left width100 pad_all_15 white-bg">
        <div class="row">
            <input type="hidden" id="editIdDocument" name="docId" />
            <div class="col-md-12">
                <div class="form-group col-md-6  marg-top-20">
                    <spring:message code="event.doc.file.maxlimit" var="maxlimit" />
                    <textarea class="width-100 form-control" id="docDec" value="" placeholder="${filedesc}"
                              rows="3" data-validation="length" data-validation-length="max250" maxlength="250"></textarea>
                    <spring:message code="event.document.filedesc" var="filedesc" />
                    <div class="max-limit-tag">${maxlimit}</div>
                </div>
                <div class="form-group col-md-6">
                    <label> <spring:message code="eventDocument.internal.document" /></label> &nbsp;&nbsp;
                    <input id="internal_1" value="internal" class="internal_1" type="checkbox" data-internal="${doc.internal}"/>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="align-center">
                        <a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out" id="confDocDec" data-original-title="Delete"><spring:message code="application.update"/></a>
                        <button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
<script type="text/javascript">
    $("#test-select").treeMultiselect({ enableSelectAll : true,sortable : true });

    <%--<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
    $(window).bind('load', function() {
        var allowedFields = '#nextStep,#priviousStep,#bubble, #dashboardLink, #downloadDocument';
        //var disableAnchers = ['#reloadMsg'];
        disableFormFields(allowedFields);
        $('#page-content').find('a').not(allowedFields).parent('span').addClass('disabled');
    });
    </c:if>--%>

    /* Datepicker bootstrap */
    $(function() {
        $('#nextStep').click(function(e) {
            e.preventDefault();
            $('#rftDocumentForm').submit();
        });

        $('#rfxCancelEvent').click(function() {
            $(this).addClass('disabled');
        });

    });
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
    $.validate({ lang : 'en',
        modules : 'file' });
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/rfxTemplateDocument.js?4"/>"></script>