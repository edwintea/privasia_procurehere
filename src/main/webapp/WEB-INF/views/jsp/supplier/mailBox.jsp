<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="col-sm-12 col-md-12 col-xs-12">
	<!-- <label class="col-sm-4 col-md-2 col-xs-6 control-label"> </label> -->
	<div class="width100 pull-left">
		<div class="content-box row">
			<!-- <div class="mail-header pad_all_10">
				<span class="mail-title">Inbox</span>
				<div class="float-right pad0A">
					<div class="input-group">
						<input type="text" class="form-control">
						<div class="input-group-btn">
							<button class="btn btn-default" type="button">
								<i class="glyph-icon icon-search"></i>
							</button>
						</div>
					</div>
				</div>
			</div> -->
			<div class="mail-toolbar clearfix pad_all_10">
				<div class="float-left dd_left">
					<a class="btn btn-default mrg5R" id="reloadMsg" href="javascript:void(0);">
						<i class="glyph-icon font-size-11 icon-refresh"></i>
					</a>
					<a class="btn btn-default" id="compose_new" title="" href="javascript:void(0);" >
						<img src="<c:url value="/resources/assets/images/mail_edit.png"/>" alt="" data-toggle="tooltip" data-placement="top" title="Compose New Mail">
					</a>
				</div>
				<div class="float-right">
					<div class="btn-toolbar">
						<div class="btn-group">
							<div class="size-md mrg10R">
								<label id="fromPage"> 1 </label>
								to
								<label id="mainInPage"> 11 </label>
								of
								<label id="totalMail"> 21 </label>
								entries
							</div>
							<input type="hidden" id="current_page" value="-1">
						</div>
						<div class="btn-group">
							<a class="btn btn-default navigation" data-page="" id="prevMail" href="javascript:void(0);">
								<i class="glyph-icon icon-angle-left"></i>
							</a>
							<a class="btn btn-default navigation" data-page="0" id="nextMail" href="javascript:void(0);">
								<i class="glyph-icon icon-angle-right"></i>
							</a>
						</div>
					</div>
				</div>
			</div>
			<div class="table-responsive width100 borderAllData">
				<table class="mailListTable table table-hover tabaccor padding-none-td">
					<th class="width_50_fix align-left"></th>
					<th class="width_200_fix align-left"><spring:message code="summarydetails.mailbox.sender" /></th>
					<th class="width_200_fix align-left"><spring:message code="summarydetails.mailbox.subject" /></th>
					<th class="width_200_fix align-left"><spring:message code="summarydetails.mailbox.created.at" /></th>
				</table>
				<div id="mails_container"></div>
			</div>
		</div>
	</div>
</div>
<div id="mailCompose" title="Compose New Mail">
	<div class="col-md-12 composer">
		<div class="content-box">
			<form class="form-horizontal width-form-set" id="login-validation">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="form-group row">
					<label class="col-sm-2 control-label" for="inputEmail1"><spring:message code="summarydetails.mailbox.to" />:</label>
					<div class="col-sm-8 disabled" id="selectEventOwner">
                        <select
                            multiple="multiple"
                            tabindex="-1"
                            id="toMsgSelect"
                            class="form-control chosen-select"
                            name="approverList1"
                            data-validation="required">
                            <option value="${po.createdBy.id}" selected>${po.createdBy.name}</option>
                            <c:if test="${not empty teamMember}">
                                <c:forEach items="${teamMember}" var="owner">
                                    <option value="${owner.user.id}" selected>${owner.user.name}</option>
                                </c:forEach>
                            </c:if>
                        </select>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-sm-2 control-label" for="inputEmail4"><spring:message code="summarydetails.mailbox.subject" />:</label>
					<div class="col-sm-8">
						<input data-validation="required length" tabindex="-1" maxlength="250" data-validation-length="max250" type="text" placeholder='<spring:message code="subject.placeholder" />' id="inputEmail4" class="form-control">
					</div>
				</div>
				<div class="editor_wrapper row">
					<label for="inputEmail1" class="col-sm-2 control-label" style="text-align: right;"><spring:message code="summarydetails.mailbox.content" />:</label>
					<div class="col-sm-8">
						<textarea id="composeMAilEditor" name="composeMAilEditor" class="form-control" data-validation="required" maxlength="2000" data-validation-length="max2000">
						</textarea>
					</div>
				</div>
				<div class="form-group row marg-top-20">
					<div class="col-md-2"></div>
					<div class="col-md-8 uploadFile">
						<div data-provides="fileinput" class="fileinput fileinput-new input-group">
							<div data-trigger="fileinput" class="form-control">
								<span class="fileinput-filename show_name" id="show_name"></span>
							</div>

							<span class="input-group-addon btn btn-black btn-file">
								<span class="fileinput-new"> <spring:message code="summarydetails.mailbox.attach.file" /> </span>
								<span class="fileinput-exists"> <spring:message code="summarydetails.mailbox.attach.file" /> </span>
								<c:set var="fileType" value="" />
								<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
									<c:set var="fileType" value="${fileType} ${index.first ? '': ','} ${type}" />
								</c:forEach>
								<input data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialog"
									data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="uploadDocx" id="uploadDocx"
									data-validation-error-msg-mime="${mimetypes}"
									accept="${mimetypes}"
									data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.more.search.field" />' />
							</span>
						</div>
						<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
						<div style='margin-left:2%'>
                            <span>
                                <spring:message code="application.note" />:<br />
                                <div style='margin-left:10%'>
                                    <ul>
                                        <li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
                                        <li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
                                    </ul>
                                </div>
                            </span>
                        </div>
						<div class="progressbar flagvisibility" data-value="0">
							<div class="progressbar-value bg-purple">
								<div class="progress-overlay"></div>
								<div class="progress-label">0%</div>
							</div>
						</div>
					</div>
				</div>
				<div class="button-pane">
					<input type="reset" id="resetMailCOmposeForm" class="flagvisibility" />
					<a id="sendmail" href="javascript:void(0);" class="btn btn-info hvr-pop hvr-rectangle-out "><spring:message code="summarydetails.mailbox.send.message" /></a>
					<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 cancel" id="cancelMailCompose"><spring:message code="application.cancel" /></button>
				</div>
			</form>
		</div>
	</div>
</div>
<div id="dialogReply">
	<div class="col-md-12">
		<div class="content-box">
			<div class="col-md-1 profile_pic">
				<!--
				<a href="#"><img src="images/profuile_pic.png"></a>
				-->
			</div>
			<div class="pad_all_10">
				<div class="detail-wrapper">
					<div class="mail_detail">
						<ul>
							<li><div class="" style="display: flex;">
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.from" /> : </span>
									<label id="frommsg" class="customCol"></label>
								</div>
                            </li>
							<li><div style="display: flex;">
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.to" /> : </span>
                                    <label id="tosendmsg" class="customCol"></label>
								</div>
                            </li>
							<li><div style="display: flex;"	>
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.subject" /> : </span>
									<label id="subsendmsg" class="customCol"></label>
								</div>
                            </li>
						</ul>
						<input type="hidden" id="parentId">
					</div>
					<div class="option_date">
                        <span id="createdDate"></span>
                        <div class="reply-dd-wrapper">
                            <div class="reply">
                                <a class="repall" href="javascript:void(0);">
                                    <img src="<c:url value="/resources/assets/images/reply.png"/>">
                                </a>
                            </div>
                            <div class="reply-dd">
                                <%-- <a href="javascript:void(0);">
                                    <img src="<c:url value="/resources/assets/images/reply_dd.png"/>">
                                </a> --%>
                                <ul>
                                    <li class="reply"><a class="repall" href="javascript:void(0);"><spring:message code="summarydetails.mailbox.reply" /></a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
				</div>
				<div id="message_body" style="border: 1px solid #ccc; padding: 5px 10px; min-height: 100px; max-height: 200px; overflow-y: auto;" class="pull-left"></div>
				<div id="attchMentDatas"></div>
				<div class="image_sec"></div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group mailtext" id="replayWithoutOk">
							<div class="amn staticBlock" style="border: 1px solid #ccc; padding: 5px 10px; margin-bottom: 40px; height: 50px;">
								<spring:message code="summarydetails.click.here.to" />
								<span id="replaymail" class="ams" role="link"><spring:message code="summarydetails.mailbox.reply" /></span>
							</div>
							<ul id="parsley-id-6675" class="parsley-errors-list">
							</ul>
						</div>
						<div class="attachmentOnNoReply pull-left"></div>
						<button type="button" id="ReplayOkWithSame" class="hidden btn btn-info hvr-pop hvr-rectangle-out pull-right"><spring:message code="summarydetails.mailbox.ok" /></button>
					</div>
				</div>
				<div class="row ckBlockReplay" style="display: none;">
					<div class="col-md-12 marg-top-20">
						<div class="form-group mailtext">
							<div id="repltTO"></div>
							<form id="replyForm">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="amn" style="border: 1px solid #ccc; padding: 0; margin-bottom: 40px; position: relative; height: 202px;">
									<textarea name="description" id="description" class="form-control" data-validation="required">
									</textarea>
								</div>
								<div class="form-group row marg-top-20">
									<div class="col-md-12 uploadFile">
										<div data-provides="fileinput" class="fileinput fileinput-new input-group">
											<div data-trigger="fileinput" class="form-control">
												<span class="fileinput-filename show_name" id="show_name"></span>
											</div>
											<span class="input-group-addon btn btn-black btn-file">
												<span class="fileinput-new"> <spring:message code="summarydetails.mailbox.attach.file" /> </span>
												<span class="fileinput-exists"> <spring:message code="summarydetails.mailbox.attach.file" /> </span>
												<c:set var="fileType" value="" />
												<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
													<c:set var="fileType" value="${fileType} ${index.first ? '': ','} ${type}" />
												</c:forEach>
												<input data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialogReplyFile"
													data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="uploadReplyFile" id="uploadReplyFile"
													data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.more.search.field" />' />
											</span>

										</div>
										<div id="Load_File-error-dialogReplyFile" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>

                                        <div style="margin-left:2%">
                                            <span>
                                                <spring:message code="application.note" />:<br />
                                                <div style="margin-left:5%">
                                                    <ul>
                                                        <li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
                                                        <li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
                                                    </ul>
                                                </div>
                                            </span>
                                        </div>

										<div class="progressbar1 flagvisibility" data-value="0">
											<div class="progressbar-value bg-purple">
												<div class="progress-overlay"></div>
												<div class="progress-label">0%</div>
											</div>
										</div>
									</div>
								</div>
								<input type="reset" id="resetMailReplyForm" class="flagvisibility" />
								<button type="button" class="btn btn-info hvr-pop hvr-rectangle-out hvr-pop hvr-rectangle-out" id="sendReply"><spring:message code="summarydetails.mailbox.send.message" /></button>
								<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1" id="cancelReply"><spring:message code="application.cancel" /></button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
	CKEDITOR.env.isCompatible = true;
	CKEDITOR.config.fillEmptyBlocks = false;
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<style>
/* Global Styles */

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f9f9f9;
}

/* Utility Classes */

.disabled {
  opacity: 0.5;
  pointer-events: none;
}

.nopad {
  padding: 0;
}

.pt-0 {
  padding-top: 0 !important;
}

.mt-2 {
  margin-top: 2px;
}

.mt-10 {
  margin-top: 10px !important;
}

.ml-10 {
  margin-left: 7px !important;
}

.pl-6 {
  padding-left: 6px;
}

.pl-9 {
  padding-left: 9px;
}

.pl-32 {
  padding-left: 32px;
}

.txt-right-p-0 {
  padding-right: 0;
  text-align: right;
}

.inline-flex {
  padding: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.w-100-b-word {
  width: 100%;
  word-break: break-all;
}

/* Component Styles */

#dialogReply, #mailCompose, #cke_editor2 {
  display: none;
}

#messageList {
  width: 100%;
  margin-bottom: 15px;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  -ms-overflow-style: -ms-autohiding-scrollbar;
  border: 1px solid #DDD;
}

.mailListTable, .table-reply {
  table-layout: fixed;
  margin-bottom: 0 !important;
}

.mailListTable th {
  text-align: center;
}

.allreply {
  padding-left: 5%;
}

.viewDetailMsg:hover {
  cursor: pointer;
}

#message_body {
  max-height: 200px;
  overflow-y: auto;
  width: 100%;
}

#repRecList {
  background: rgba(0, 0, 0, 0) none repeat scroll 0 0;
  cursor: not-allowed;
  opacity: 0;
}

#cke_composeMAilEditor .cke_bottom {
  bottom: 0;
  overflow: hidden !important;
  position: relative;
  width: 100%;
}

/* Dialog Styles */

.ui-dialog {
  background: transparent none repeat scroll 0 0;
  border-radius: 10px;
}

.ui-dialog .ui-dialog-content {
  background: #fff none repeat scroll 0 0;
}

.ui-dialog .ui-dialog-titlebar {
  position: relative;
}

.ui-widget-overlay {
  background-color: #000;
  opacity: 0.5;
}

/* Responsive Styles */

@media only screen and (max-width: 767px) {
  .mb-l-15 {
    margin-left: 15px;
  }
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">

<c:set value="${poEvent.id}" var="eventId" />
<c:set value="${event.eventOwner.name}" var="eventOwner" />
<c:set value="${teamMember}" var="teamMember" />
<c:set value="${memberName}" var="memberName"/>
<c:set value="${memberId}" var="memberId"/>
<script>
$(function(){
	// Variables
    var tenantId = '${tenantId}';
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var supplier = $('#toMsg').data('supplier');
	var teamMember = '${teamMember}';
    var eventOwner = '${eventOwner}';


    // Functions
    function fluidDialog() {
        var $visible = $(".ui-dialog:visible");
        $visible.each(function() {
            var $this = $(this);
            var dialog = $this.find(".ui-dialog-content").data("ui-dialog");
            if (dialog && dialog.options.fluid) {
                var wWidth = $(window).width();
                if (wWidth < (parseInt(dialog.options.maxWidth) + 50)) {
                    $this.css("max-width", "97%");
                } else {
                    $this.css("max-width", dialog.options.maxWidth + "px");
                }
                dialog.option("position", dialog.options.position);
            }
        });
    }

    // Event Listeners
    $(window).resize(fluidDialog);
    $(document).on("dialogopen", ".ui-dialog", fluidDialog);

    // Replay Mail Functionality
    $(document).delegate('#replaymail', 'click', function() {
        $('.ckBlockReplay').show();
        $('.staticBlock').hide();
        $('#ReplayOkWithSame').hide();
        if (CKEDITOR.instances.description) {
            CKEDITOR.instances.description.destroy();
        }
        $('#resetMailReplyForm').click();
        var editor = CKEDITOR.replace('description', {
            toolbarLocation: 'bottom',
            width: '100%',
            height: '200px',
            removePlugins: 'dragdrop,basket,elementspath,resize,images',
            toolbar: [
                { name: 'basicstyles', items: ['Bold', 'Italic'] },
                { name: 'paragraph', items: ['NumberedList', 'BulletedList'] },
                { name: 'styles', items: ['Styles', 'Format'] },
                { name: 'colors', items: ['TextColor', 'BGColor'] },
                { name: 'custom', items: ['Send'] },
                { name: 'custom2', items: ['Delete'] }
            ]
        });
        CKEDITOR.instances['description'].setData('');
        editor.on('instanceReady', function(ev) {
            ev.editor.document.on('drop', function(ev) {
                ev.data.preventDefault(true);
            });
        });

        let recHtml = `
            <select disabled multiple="multiple" tabindex="-1" id="repRecList" class="form-control chosen-select" name="repRecList" data-validation="required">
                <option value="${po.createdBy.id}" selected>${po.createdBy.name}</option>
                <c:if test="${not empty teamMember}">
                    <c:forEach items="${teamMember}" var="owner">
                        <option value="${owner.user.id}" selected>${owner.user.name}</option>
                    </c:forEach>
                </c:if>
            </select>
        `;

        $("#repltTO").html(recHtml);
        $("#repRecList").chosen();

    });

    // Rep All Functionality
    $(".repall").click(function() {
        $("#replaymail").trigger("click");
    });

	// Fluid Dialog Function
    function fluidDialog() {
        // Get all visible dialogs
        var $visibleDialogs = $(".ui-dialog:visible");

        // Iterate over each visible dialog
        $visibleDialogs.each(function() {
            var $dialog = $(this);
            var dialogData = $dialog.find(".ui-dialog-content").data("ui-dialog");

            // Check if dialog has fluid option enabled
            if (dialogData && dialogData.options.fluid) {
                var windowWidth = $(window).width();

                // Check if window width is less than dialog width
                if (windowWidth < (parseInt(dialogData.options.maxWidth) + 50)) {
                    // Set max-width to 97% to prevent dialog from filling entire screen
                    $dialog.css("max-width", "97%");
                } else {
                    // Fix maxWidth bug by setting max-width to dialog's maxWidth
                    $dialog.css("max-width", dialogData.options.maxWidth + "px");
                }

                // Reposition dialog
                dialogData.option("position", dialogData.options.position);
            }
        });
    }


    // Compose New Email Functionality
    $("#compose_new").click(function() {

        // Reset fields
        $(".form-error").remove();
        $("#toMsg").removeClass("error");
        $("#inputEmail4").removeClass("error");
        $("#inputEmail4").parent().removeClass("has-error");
        $('#inputEmail4').css("border-color", "");
        $('span[id="show_name"]').html('');
        $('#uploadDocx').val('');
        $('#resetMailCOmposeForm').click();
        $('#composeMAilEditor').parent().removeClass('has-error').find('.form-error').remove();

        // Destroy CKEditor instance if it exists
        if (CKEDITOR.instances.composeMAilEditor) {
            CKEDITOR.instances.composeMAilEditor.destroy();
        }

        // Create new CKEditor instance
        var editorcmps = CKEDITOR.replace('composeMAilEditor', {
            toolbarLocation: 'bottom',
            width: '100%',
            height: '200px',
            removePlugins: 'dragdrop,basket,elementspath,resize,images',
            toolbar: [
                { name: 'basicstyles', items: ['Bold', 'Italic'] },
                { name: 'paragraph', items: ['NumberedList', 'BulletedList'] },
                { name: 'styles', items: ['Styles', 'Format'] },
                { name: 'colors', items: ['TextColor', 'BGColor'] },
                { name: 'custom', items: ['Send'] },
                { name: 'custom2', items: ['Delete'] }
            ]
        });

        // Set data for CKEditor instance
        CKEDITOR.instances['composeMAilEditor'].setData('');

        // Prevent drag-and-drop in CKEditor instance
        editorcmps.on('instanceReady', function(ev) {
            ev.editor.document.on('drop', function(ev) {
                ev.data.preventDefault(true);
            });
        });

        // Open mail compose dialog
        $("#mailCompose").dialog({
            modal: true,
            width: 'auto',
            maxWidth: 740,
            height: 'auto',
            modal: true,
            fluid: true,
            dialogClass: "mailComposeLoaded",
            resizable: false
        });
    });

    // Cancel Button Functionality
    $(".cancel").click(function() {
        $(".ui-dialog-content").dialog("close");
    });

    // View Detail Message Functionality
    $(document).delegate(".viewDetailMsg", 'click', function(e) {
        // Check if message permission is not empty and not allowed
        <c:if test="${not empty messagePermission}">
            if (!${messagePermission}) {
                e.preventDefault();
                return;
            }
        </c:if>

        e.preventDefault();
        $('#cancelReply').click();

        // Get message details
        var subjectData = $(this).find(".hmsgSubject").html();
        var bdTxt = $(this).find(".hmsgBody").html();
        var fromId = $(this).find('td.email-title').attr('data-fromId');
        var parentId = $(this).data('msgid');

        // Reset mail reply form
        $('#resetMailReplyForm').click();

        // Open dialog reply
        $("#dialogReply").dialog({
            modal: true,
            width: 'auto',
            maxWidth: 720,
            height: 'auto',
            modal: true,
            fluid: true,
            dialogClass: "mailBoxLoaded",
            resizable: false,
            title: subjectData
        });

        // Hide/show reply options based on fromId
        if (fromId != tenantId) {
            $('#replayWithoutOk, .reply-dd-wrapper, #attchMentDatas').show();
            $('#ReplayOkWithSame, .attachmentOnNoReply').hide();
        } else {
             $('#replayWithoutOk, .reply-dd-wrapper').hide();
             $('#ReplayOkWithSame, .attachmentOnNoReply, #attchMentDatas').show();
        }

        // Set message details in dialog reply
        $("#message_body").html(bdTxt);
        var attachDataVal = $(this).find('.attchMentValus').html();

        if (attachDataVal === undefined || attachDataVal === null) {
            $('#attchMentDatas').html('');
        } else {
            $('#attchMentDatas').html(attachDataVal);
        }
        $("#frommsg").html($(this).find(".hmsgFrom").html());
        $("#createdDate").html($(this).find(".hmsgcreatedDate").html());
        $("#tosendmsg").html($(this).find(".hmsgRecieveer").html());
        $("#subsendmsg").html($(this).find(".hmsgSubject").html());
        $("#parentId").val(parentId);

        // Add extra info to dialog reply
        $("#dialogReply").find('.extraInfoCustom').remove();
        $("#dialogReply").append('<div class="extraInfoCustom hide">' + $(this).find('.email-body').html() + '</div>');
        //$('.attachmentOnNoReply').html($('#attchMentDatas').html());
    });

    // Replay Ok With Same Functionality
    $('#ReplayOkWithSame').click(function() {
        $("#dialogReply").dialog('close');
    });

    /*
    ... Send Replies...
     */
    // Send Reply Functionality
    $("#sendReply").click(function(e) {
        e.preventDefault();

        // Check if form is valid
        if (!$('#replyForm').isValid()) {
            return false;
        }

        // Check if description is empty
        if (CKEDITOR.instances['description'].getData() == '') {
            $('#description').parent().removeClass('has-error').find('.form-error').remove();
            $('#description').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
            return false;
        }



        // Get file data
        var file_data = $('#uploadReplyFile').prop('files')[0];

        // Create form data
        var form_data = new FormData();
        form_data.append('file', file_data);
        form_data.append('message', CKEDITOR.instances['description'].getData().replace(/&Acirc;&#160;/g, ''));
        form_data.append('suppliers',supplier);
        form_data.append('subject', $(".ui-dialog-title").html());
        form_data.append('parentId', $("#parentId").val());
        form_data.append('page', 0);
        form_data.append('size', 5);
        form_data.append('search', '');

        // Send AJAX request
        $.ajax({
            url: getContextPath() + '/supplier/sendMessage/' + getEventType() + '/${eventId}',
            data: form_data,
            cache: false,
            xhr: function() {
                myXhr = $.ajaxSettings.xhr();
                if ($('#uploadReplyFile').val().length > 0) {
                    if (myXhr.upload) {
                        myXhr.upload.addEventListener('progress', updateProgress1, false);
                    }
                }
                return myXhr;
            },
            type: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            beforeSend: function(xhr) {
                $('#loading').show();
                if ($('#uploadReplyFile').val().length > 0) {
                    $('.progressbar1').removeClass('flagvisibility');
                }
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr('content'), $("meta[name='_csrf']").attr('content'));
            },
            success: function(data) {
                template = renderMailList(data.data);
                $("#current_page").val(0);
                renderMailInfo(data.recordsTotal, 5, data.draw);

                $("#mails_container").html(template);
                $('#loading').hide();
                $(".ui-dialog-content").dialog("close");
                $('.progressbar1').addClass('flagvisibility');
                $('#loading').hide();
            },
            error: function(request, textStatus, errorThrown) {
                $('#loading').hide();
            },
            complete: function() {
                $('#loading').hide();
            }
        });
    });

    // Cancel Reply Functionality
    $("#cancelReply").click(function() {
        $('.ckBlockReplay').hide();
        $('.staticBlock').show();
        $('#ReplayOkWithSame').show();
    });

    // Cancel Mail Compose Functionality
    $("#cancelMailCompose").click(function() {
        $('#resetMailCOmposeForm').click();
        $('#mailCompose').hide();
    });

    // Upload Docx Functionality
    $('#uploadDocx').change(function() {
        if (!$('#login-validation').isValid()) {
            return false;
        }
    });

    // Upload Reply File Functionality
    $('#uploadReplyFile').change(function() {
        if (!$('#replyForm').isValid()) {
            return false;
        }
    });

    // Send Mail Functionality
    $("#sendmail").click(function(e) {
        e.preventDefault();

        // Check if form is valid
        if (!$('#login-validation').isValid()) {
            return false;
        }

        // Check if compose mail editor is empty
        if (CKEDITOR.instances['composeMAilEditor'].getData() == '') {
            $('#composeMAilEditor').parent().removeClass('has-error').find('.form-error').remove();
            $('#composeMAilEditor').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
            return false;
        }

        // Get file data
        var file_data = $('#uploadDocx').prop('files')[0];

        // Create form data
        var form_data = new FormData();
        form_data.append('file', file_data);
        form_data.append('message', CKEDITOR.instances['composeMAilEditor'].getData().replace(/&Acirc;&#160;/g, ''));
        form_data.append('suppliers',supplier);
        form_data.append('subject', $("#inputEmail4").val());
        form_data.append('page', 0);
        form_data.append('size', 5);
        form_data.append('search', '');

        // Send AJAX request
        $.ajax({
            url: getContextPath() + '/supplier/sendMessage/' + getEventType() + '/${eventId}',
            data: form_data,
            cache: false,
            xhr: function() {
                myXhr = $.ajaxSettings.xhr();
                if ($('#uploadDocx').val().length > 0) {
                    if (myXhr.upload) {
                        myXhr.upload.addEventListener('progress', updateProgress, false);
                    }
                }
                return myXhr;
            },
            type: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            beforeSend: function(xhr) {
                $('#loading').show();
                if ($('#uploadDocx').val().length > 0) {
                    $('.progressbar').removeClass('flagvisibility');
                }
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr('content'), $("meta[name='_csrf']").attr('content'));
            },
            success: function(data) {
                template = renderMailList(data.data);
                $("#current_page").val(0);
                renderMailInfo(data.recordsTotal, 5, data.draw);
                $("#mails_container").html(template);
                $('#loading').hide();
                $(".ui-dialog-content").dialog("close");
                $('#loading').hide();
                $('.progressbar').addClass('flagvisibility');
            },
            error: function(request, textStatus, errorThrown) {
                $('#loading').hide();
            },
            complete: function() {
                $('#loading').hide();
            }
        });
    });

    // Next/Prev Mail Functionality
    $("#nextMail, #prevMail").click(function() {
        var ajaxUrl = getContextPath() + '/supplier/eventMessages/' + getEventType() + '/${eventId}';
        var currentPage = $("#current_page").val();
        var nextPage;

        if ($(this).attr("id") == "nextMail") {
            nextPage = parseInt(currentPage) + 1;
        } else {
            nextPage = parseInt(currentPage) - 1;
        }

        if (nextPage < 0) {
            return;
        }

        var totalPages = parseInt(parseInt($("#totalMail").html()) / 5) + 1;
        if (totalPages <= nextPage) {
            return;
        }

        $.ajax({
            url: ajaxUrl,
            data: {
                'page': nextPage,
                'size': 5,
                'search': ''
            },
            type: "POST",
            beforeSend: function(xhr) {
                $('#loading').show();
                xhr.setRequestHeader(header, token);
            },
            success: function(data) {
                $("#current_page").val(nextPage);
                var template = renderMailList(data.data);
                renderMailInfo(data.recordsTotal, 5, data.draw);
                $("#mails_container").html(template);
                $('#loading').hide();
            },
            error: function(request, textStatus, errorThrown) {
                $('#loading').hide();
            },
            complete: function() {
                $('#loading').hide();
            }
        });
    });

    // Initialize Current Page
    $("#current_page").val(-1);
    $("#nextMail").trigger('click'); // on page load

    // Reload Messages Functionality
    $("#reloadMsg").click(function() {
    $("#current_page").val(-1);
    $("#nextMail").trigger('click');
    });

    function renderMailList(data) {
        var html = "";
        $(data).each(function(i, message) {
            var shtml = "";

            html += '<ol><li><table class="mailListTable table table-hover text-center"><tbody><tr class="viewDetailMsg">';
            html += '<td class="align-left width_50_fix">';
            if (message.fileName != '' && message.fileName != undefined) {
                html += '<img src="' + getContextPath() + '/resources/images/attach_file.png"/><div class="attchMentValus hide">';
                html += '<div class="image_sec marg-top-10 border-none"><a href="' + getContextPath() + '/supplier/downloadMessageAttachment/' + getEventType() + '/' + message.id + '"><img src="' + getContextPath() + '/resources/images/attach_file.png"/> Attachment : <span>' + message.fileName + '</span></a></div>';
                html += '</div>';
            }
            html += '</td>';
            html += '<td class="align-left email-title width_200_fix" data-fromId="'+message.createdBy.tenantId+'">';
            html += message.createdBy.name;
            html += '</td><td   class=" align-left width_200_fix email-body " >';
            //if (!(typeof message.replies === 'undefined') && message.replies.length != 0) {
            //html += "<a data-msgId='" + message.id + "' href='javascript:void(0)'>";
            //}
            html += message.subject;
            //if (!(typeof message.replies === 'undefined') && message.replies.length != 0) {
            //html += "</a>";
            //}
            html += "<div  class='hmsgBody hide'>" + message.message + "</div>";

            html += "<div  class='hmsgFrom hide'>" + (message.sentByBuyer ? message.createdBy.name : message.suppliers[0].companyName) + " [ " + (message.sentByBuyer ? message.buyer.companyName : message.suppliers[0].companyName) + " ]" + "</div>";

            shtml += "<div  class='hmsgcreatedDate hide'>" + message.createdDate + "</div>";
            /* var suppIds = "";
            var suppNames = "";
            allSup = message.suppliers;
            $(allSup).each(function(i, supp_S) {
                suppIds += supp_S.id + ",";
                suppNames += supp_S.fullName + ",";
            }); */
            var suppIds = "";
            var suppNames = "";
            var eventOwner = '${eventOwner}';
            allSup = message.suppliers;
            allBuy = message.buyer;
            if (message.sentByBuyer) {

                $(allSup).each(function(i, supp_S) {
                    suppIds += supp_S.id + ",";
                    suppNames += supp_S.companyName + " [ " + supp_S.companyName + " ]" + ((allSup.length - 1 != i) ? ", " : "");
                    //console.log(suppNames);
                });
            } else {

				suppIds += '${po.createdBy.id}' +'${memberId}';
				suppNames+='${po.createdBy.name}'+ " [ " + '${po.createdBy.buyer.companyName}' + " ]" +"${memberName}";

            }
            shtml += "<div data-suppliers='"+suppIds+"' class='hmsgRecieveer hide'>" + suppNames + "</div>";
            shtml += "<div  class='hmsgSubject hide'>" + message.subject + "</div>";
            html += "<div  class='hmsgReplyId hide'>" + message.createdBy.tenantId + "</div>";
            html += "<div  class='hmsgReplyName hide'>" + message.createdBy.name + "</div>";
            html = html + shtml + '</td>';
            html += '<td class="align-left width_200_fix">' + message.createdDate + ' </td>';

            html += '</tr></tbody></table>';

            if (!(typeof message.replies === 'undefined') && message.replies.length != 0) {
                html += '<li id="allreply'+message.id+'" ><ol> ';
                $(message.replies).each(function(i, reply) {

                    suppNames = "";
                    if (reply.sentByBuyer) {
                        $(allSup).each(function(i, supp_S) {
                            suppIds += supp_S.id + ",";
                            suppNames += supp_S.companyName + " [ " + supp_S.companyName + " ]" + ((allSup.length - 1 != i) ? ", " : "");
                        });
                    } else {
                            suppIds += '${po.createdBy.id}' +'${memberId}';
                            suppNames+='${po.createdBy.name}'+ " [ " + '${po.createdBy.buyer.companyName}' + " ]" +"${memberName}";
                    }

                    html += '<li><table class="table table-reply  table-hover text-center"><tr class="viewDetailMsg" style=" background-color: #f5f5f5;" data-msgid="' + message.id + '">';
                    html += '<td class="width_50_fix align-left email-title" data-fromId="'+reply.createdBy.tenantId+'" >';
                    html += "<div  class='hmsgFrom hide'>" + reply.createdBy.name + " [ " + (reply.sentByBuyer ? reply.buyer.companyName : message.suppliers[0].companyName) + " ]" + "</div>";
                    html += "<div data-suppliers='" + suppIds + "' class='hmsgRecieveer hide'>" + suppNames + "</div>";
                    html += "<div  class='hmsgReplyId hide'>" + reply.createdBy.id + "</div>";
                    html += "<div  class='hmsgReplyName hide'>" + reply.createdBy.name + "</div>";
                    html += '<i class="glyphicon glyphicon-share-alt"></i>';
                    //html += '<img src="<c:url value="/resources/assets/images/reply.png"/>">';
                    html += '</td>';
                    html += '<td class="align-left width_50_fix">';
                    if (reply.fileName != '' && reply.fileName != undefined) {
                        html += '<img src="' + getContextPath() + '/resources/images/attach_file.png"/><div class="attchMentValus hide">';
                        html += '<div class="image_sec marg-top-10 border-none"><a href="' + getContextPath() + '/supplier/downloadMessageAttachment/' + getEventType() + '/' + reply.id + '"><img src="' + getContextPath() + '/resources/images/attach_file.png"/> Attachment : <span>' + reply.fileName + '</span></a></div>';
                        html += '</div>';
                    }
                    html += '</td>';
                    html += '<td class=" width_200_fix align-left">' + reply.createdBy.name + '</td>';
                    html += '<td class=" width_200_fix  align-left">';
                    //html += "<a href='javascript:void(0)'>" +  + "</a>";
                    html += reply.subject;
                    html += "<div  class='hmsgBody hide'>" + reply.message + "</div>";
                    html += shtml + '</td>';
                    html += '<td class=" width_200_fix align-left">' + reply.createdDate + '</td>';
                    html += '</tr></table></li>';
                });

                html += '</li></ol>';
            }

            html += '</ol>';

        });
        return html;
    }

    // Render Mail Info Function
    function renderMailInfo(recordsTotal, limit, eventTarget) {
        var totalPages = Math.ceil(recordsTotal / limit);
        var currentPage = parseInt($("#current_page").val()) + 1;

        var from = (limit * currentPage) - (limit - 1);
        if (recordsTotal == 0) {
            from = 0;
        }

        var to = limit * currentPage;
        if (currentPage == totalPages) {
            to = recordsTotal;
        }

        $("#fromPage").html(from);
        $("#mainInPage").html(to);
        $("#totalMail").html(recordsTotal);
    }

    $(document).delegate(".allreply",'click',function() {
        $(this).html('<i class="glyph-icon icon-minus"></i>');
        if ($(this).hasClass("collapsed")) {
            $(this).html('<i class="glyph-icon icon-plus"></i>');
        }
    });

    function updateProgress(evt) {
        if (evt.lengthComputable) {
            var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
            progress(percentComplete, $('.progressbar'));
            var percentVal = percentComplete + '%';
            console.log(percentVal);
        } else {
            // Unable to compute progress information since the total size is
            // unknown
            console.log('unable to complete');
        }
    }

    function updateProgress1(evt) {
        if (evt.lengthComputable) {
            var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
            progress(percentComplete, $('.progressbar1'));
            var percentVal = percentComplete + '%';
            console.log(percentVal);
        } else {
            // Unable to compute progress information since the total size is
            // unknown
            console.log('unable to complete');
        }
    }

});
</script>
