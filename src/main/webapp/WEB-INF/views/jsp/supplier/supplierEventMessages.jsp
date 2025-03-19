<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<sec:authentication property="principal.tenantId" var="tenantId" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxMessagesDesk" code="application.supplier.rfx.messages" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxMessagesDesk}] });
});
</script>
<script type="text/javascript">
	var stompClient = null;
	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
				var redirectUrl = getContextPath() + "/supplier/viewSupplierEventMessages/${eventType}/${event.id}";
				if (messageOutput.body == 'CLOSED') {
					window.location.href = redirectUrl;
				} else if (messageOutput.body == 'SUSPENDED') {
					document.getElementById("statusHeader").innerHTML = "SUSPENDED";
					$(".counterForTime").hide();
					//$(".rowForSuspend").hide();
				} else if (messageOutput.body == 'RESUME') {
					window.location.href = redirectUrl;
				}
			});
		});
	}

	var errorHandler = function() {
		console.log("Connection lost..... reconnecting...");
	    setTimeout(function(){ connect(); }, 5 * 1000); // retry connection in 5 secs
	}

	function disconnect() {
		if (stompClient != null && stompClient.ws.readyState === stompClient.ws.OPEN) {
			stompClient.disconnect();
		}
		console.log("Disconnected");
	}

	$(window).unload(function(){
		if(stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});
	
	$(document).ready(function() {
		connect();
	});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/supplierregistration/supplierSubmissionHeader.jsp" />
			<div class="tab-main-inner pad_all_15">
				<div id="tab-5" class="tab-content">
					<div class="doc-fir-inner1">
						<div class="col-sm-12 col-md-12 col-xs-12">
							<div class="content-box row">
								<div class="mail-header pad_all_10">
									<span class="mail-title"><spring:message code="supplierevent.inbox"/></span>
									<div class="float-right pad0A">
										<div class="input-group">
											<!-- <input type="text" class="form-control" id="txtSearch">
											<div class="input-group-btn">
												<button class="btn btn-default" id="btnSearch" type="button">
													<i class="glyph-icon icon-search"></i>
												</button>
											</div> -->
										</div>
									</div>
								</div>
								<div class="mail-toolbar clearfix pad_all_10">
									<div class="float-left dd_left">
										<a class="btn btn-default mrg5R" id="reloadMsg" href="javascript:void(0);"> <i class="glyph-icon font-size-11 icon-refresh"></i>
										</a> <a class="btn btn-default" id="compose_new" title='<spring:message code="tooltip.compose.new.msg"/>' href="javascript:void(0);"> <img src="<c:url value="/resources/assets/images/mail_edit.png"/>" alt="" data-placement="top" title="Compose New Message">
										</a>
									</div>
									<div class="float-right">
										<div class="btn-toolbar">
											<div class="btn-group">
												<div class="size-md mrg10R">
													<label id="fromPage"> 0 </label> <spring:message code="application.to1"/> <label id="mainInPage"> 0 </label> <spring:message code="application.of1"/> <label id="totalMail"> 0 </label> <spring:message code="application.entries"/>
												</div>
												<input type="hidden" id="current_page" value="-1">
											</div>
											<div class="btn-group">
												<a class="btn btn-default navigation" data-page="" id="prevMail" href="javascript:void(0);"> <i class="glyph-icon icon-angle-left"></i>
												</a> <a class="btn btn-default navigation" data-page="0" id="nextMail" href="javascript:void(0);"> <i class="glyph-icon icon-angle-right"></i>
												</a>
											</div>
										</div>
									</div>
								</div>
								<div class="table-responsive width100 borderAllData">
									<table class="mailListTable table table-hover tabaccor padding-none-td" width="100%">
										<th class="width_50_fix align-left"></th>
										<th class="width_200_fix align-left"><spring:message code="summarydetails.mailbox.sender"/></th>
										<th class="width_200_fix align-left"><spring:message code="summarydetails.mailbox.subject"/></th>
										<th class="width_200_fix align-left"><spring:message code="supplierevent.message.date"/></th>
									</table>
									<div id="mails_container"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="bqlistDetails"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="mailCompose" title="Compose New Message">
	<div class="col-md-12 composer">
		<div class="content-box">
			<form class="form-horizontal" id="login-validation">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="form-group row">
					<label class="col-sm-2 control-label" for="inputEmail1">To:</label>
					<div class="col-sm-9">
						<!--   <input required type="email" placeholder="" id="inputEmail1" class="form-control"> -->
						<label>${event.eventOwner} [${event.companyName}]</label>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-sm-2 control-label" for="inputEmail4">Subject:</label>
					<div class="col-sm-9">
						<input tabIndex="-1" data-validation="required length" maxlength="250" data-validation-length="max250" type="text" placeholder='<spring:message code="subject.placeholder"/>' id="inputEmail4" class="form-control">
					</div>
				</div>
				<div class="editor_wrapper row">
					<label for="inputEmail1" class="col-sm-2 control-label" style="text-align: right;"><spring:message code="summarydetails.mailbox.content"/>:</label>
					<div class="col-sm-9">
						<textarea name="composeMAilEditor" id="composeMAilEditor" class="form-control" data-validation="required">
									</textarea>
					</div>
				</div>
				<div class="form-group row marg-top-10">
					<div class="col-md-2"></div>
					<div class="col-md-9 uploadFile">
						<div data-provides="fileinput" class="fileinput fileinput-new input-group">
							<div data-trigger="fileinput" class="form-control">
								<span class="fileinput-filename show_name" id="show_name"></span>
							</div>
							<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="summarydetails.mailbox.attach.file"/> </span> <span class="fileinput-exists"> <spring:message code="summarydetails.mailbox.attach.file"/> </span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
									<c:set var="fileType" value="${fileType} ${index.first ? '': ','} ${type}" />
								</c:forEach> <input data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="uploadDocx" id="uploadDocx" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.more.search.field"/>' />
							</span>
						</div>
						<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
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
					<button id="sendmail" type="button" class="btn btn-info hvr-pop hvr-rectangle-out "><spring:message code="supplierevent.send.message.case" /></button>
					<button type="button" class="btn  btn-black marg-left-10 font-gray-dark cancel"><spring:message code="application.cancel" /></button>
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
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.from" /> : </span> <label id="frommsg" class="customCol"></label>
								</div></li>
							<li>
								<div style="display: flex;">
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.to" /> : </span> <label id="tosendmsg" class="customCol"></label>
								</div>
							</li>
							<li><div style="display: flex;">
									<span class=" customCol align-right" style="padding-right: 0px; width: 72px !important; margin-right: 10px;"><spring:message code="summarydetails.mailbox.subject"/> : </span> <label id="subsendmsg" class="customCol"></label>
								</div></li>
						</ul>
						<input type="hidden" id="parentId">
					</div>
					<div class="option_date">
						<span id="createdDate"></span>
						<div class="reply-dd-wrapper">
							<div class="reply">
								<a class="repall" href="javascript:void(0);"> <img src="<c:url value="/resources/assets/images/reply.png"/>">
								</a>
							</div>
							<div class="reply-dd">
								<%-- <a href="javascript:void(0);">
									<img src="<c:url value="/resources/assets/images/reply_dd.png"/>">
								</a> --%>
								<ul>
									<li class="reply"><a class="repall" href="javascript:void(0);"><spring:message code="summarydetails.mailbox.reply"/></a></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div id="message_body" style="border: 1px solid #ccc; padding: 5px 10px; min-height: 100px; max-height: 200px; overflow-y: auto;"></div>
				<div id="attchMentDatas"></div>
				<div class="image_sec"></div>
				<div class="image_sec"></div>
				<div class="row">
					<div class="col-md-12 ">
						<div class="form-group mailtext" id="replayWithoutOk">
							<div class="amn staticBlock" style="border: 1px solid #ccc; padding: 5px 10px; margin-bottom: 40px; height: 50px;">
								<spring:message code="summarydetails.click.here.to"/> <span id="replaymail" class="ams" role="link"><spring:message code="summarydetails.mailbox.reply"/></span>
							</div>
							<ul id="parsley-id-6675" class="parsley-errors-list">
							</ul>
						</div>
						<div class="attachmentOnNoReply pull-left"></div>
						<button type="button" id="ReplayOkWithSame" class="btn btn-info hvr-pop hvr-rectangle-out pull-right"><spring:message code="summarydetails.mailbox.ok"/></button>
					</div>
				</div>
				<div class="row ckBlockReplay" style="display: none;">
					<div class="col-md-12 marg-top-20">
						<div class="form-group mailtext">
							<div class="replyId hide"></div>
							<div class="replyName hide"></div>
							<div id="repltTO"></div>
							<form id="replyForm">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="amn" style="border: 1px solid #ccc; padding: 0; margin-bottom: 40px; position: relative; height: 202px;">
									<textarea id="description" name="description" class="form-control" data-validation="required">
									</textarea>
								</div>
								<div class="form-group row marg-top-10">
									<div class="col-md-12 uploadFile">
										<div data-provides="fileinput" class="fileinput fileinput-new input-group">
											<div data-trigger="fileinput" class="form-control">
												<span class="fileinput-filename show_name" id="show_name"></span>
											</div>
											<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new">  <spring:message code="summarydetails.mailbox.attach.file"/>  </span> <span class="fileinput-exists">  <spring:message code="summarydetails.mailbox.attach.file"/>  </span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
													<c:set var="fileType" value="${fileType} ${index.first ? '': ','} ${type}" />
												</c:forEach> <input data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialogReplyFile" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="uploadReplyFile" id="uploadReplyFile" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.more.search.field"/>'  />
											</span>
										</div>
										<div id="Load_File-error-dialogReplyFile" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
										<div class="progressbar1 flagvisibility" data-value="0">
											<div class="progressbar-value bg-purple">
												<div class="progress-overlay"></div>
												<div class="progress-label">0%</div>
											</div>
										</div>
									</div>
								</div>
								<input type="reset" id="resetMailReplyForm" class="flagvisibility" />
								<button type="button" class="btn btn-info hvr-pop hvr-rectangle-out hvr-pop hvr-rectangle-out" id="sendReply"><spring:message code="summarydetails.mailbox.send.message"/></button>
								<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out hvr-pop hvr-rectangle-out1" id="cancelReply"><spring:message code="application.cancel" /></button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<c:set value="${event.eventOwner}" var="eventOwner" />
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierBqSubmission.js?3"/>"></script>
<!--
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/summernote/summernote.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/summernote/summernote.css"/>"> -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
	CKEDITOR.env.isCompatible = true;
	CKEDITOR.config.fillEmptyBlocks = false;	
</script>
<style>
.col-md-2.customCol {
	width: 10.666667% !important;
}

.col-md-10.customCol {
	width: 89.333333% !important;
}

#dialogReply, #mailCompose, #cke_editor2 {
	display: none;
}

.nopad {
	padding: 0;
}

.ui-widget-overlay {
	background-color: #000;
	opacity: 0.5;
}

.ui-dialog-titlebar.ui-widget-header.ui-corner-all.ui-helper-clearfix.ui-draggable-handle
	{
	position: relative;
}

.cke_textarea_inline {
	height: auto;
	width: auto;
	min-height: 100px;
	min-width: 200px;
}

.cke_button__send_label {
	display: block;
}

#mailbox ol {
	list-style: outside none none;
	padding-left: 0;
}

#message_body {
	max-height: 200px;
	overflow-y: auto;
	width: 100%;
}

#cke_description, .cke_contents {
	width: 100%;
	padding: 0;
	border: 0;
	border-radius: 0;
}

.cke_bottom {
	position: absolute;
	bottom: 0;
	width: 100%;
}

.ui-dialog {
	background: transparent none repeat scroll 0 0;
}

.ui-dialog .ui-dialog-content {
	background: #fff none repeat scroll 0 0;
	padding-top: 15px;
}

.ui-draggable .ui-dialog-titlebar {
	position: relative;
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

.dd_left a {
	float: left;
	margin-right: 10px;
}

#login-validation {
	padding: 10px;
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

.ui-dialog .ui-dialog-title {
	/* color: #7f7f7f; */
	
}

#dialogReply {
	padding-top: 20px;
}

.table-reply {
	margin-bottom: 0px !important;
}

.amn {
	margin-bottom: 10px !important;
}

#mails_container li {
	list-style: none;
}

#mails_container ol {
	padding-left: 0 !important;
}

#repRecList {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0;
	cursor: not-allowed;
	opacity: 0;
}

.viewDetailMsg:hover {
	cursor: pointer
}

textarea[name="composeMAilEditor"] {
	height: 230px;
}

#cke_composeMAilEditor .cke_bottom {
	bottom: 0;
	overflow: hidden !important;
	position: relative;
	width: 100%;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	var tenantId = '${tenantId}';
	<c:if test="${eventPermissions.viewer}">
	$(window).bind('load', function() {
		var allowedFields = '.tab-link > a,#reloadMsg,#txtSearch,#btnSearch,.navigation,#idDutchConsole,#idEnglishConsole';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	// on window resize run function
	$(window).resize(function() {
		fluidDialog();
	});

	// catch dialog if opened within a viewport smaller than the dialog width
	$(document).on("dialogopen", ".ui-dialog", function(event, ui) {
		fluidDialog();
	});

	$(document).delegate('#replaymail', 'click', function() {
		$('.ckBlockReplay').show();
		$('.staticBlock').hide();
		if (CKEDITOR.instances.description) {
			CKEDITOR.instances.description.destroy();
		}
		$('#resetMailReplyForm').click();
		editor = CKEDITOR.replace('description', {
			toolbarLocation : 'bottom',
			width : '100%',
			height : '200px',
			removePlugins : 'dragdrop,basket,elementspath,resize',
			toolbar : [ {
				name : 'basicstyles',
				items : [ 'Bold', 'Italic' ]
			}, {
				name : 'paragraph',
				items : [ 'NumberedList', 'BulletedList' ]
			}, {
				name : 'styles',
				items : [ 'Styles', 'Format' ]
			}, {
				name : 'colors',
				items : [ 'TextColor', 'BGColor' ]
			}, {
				name : 'custom',
				items : [ 'Send' ]
			}, {
				name : 'custom2',
				items : [ 'Delete' ]
			}, ]
		});
		CKEDITOR.instances['description'].setData('');
		editor.on('instanceReady', function(ev) {
			// Prevent drag-and-drop.
			ev.editor.document.on('drop', function(ev) {
				ev.data.preventDefault(true);
			});
		});

		supIds = $("#dialogReply").find(".replyId").html();
		supNms = $("#dialogReply").find(".replyName").html();
		/* supIds = $(this).find(".hmsgReplyId").html();
		supNms = $(this).find(".hmsgReplyName").html(); */
	/* 	console.log(supIds);
		console.log(supNms); */
		recHtml = '<select disabled multiple id="repRecList" class="chosen-select" name="repRecList" cssClass="form-control chosen-select">';
		recHtml += '<option selected value="'+supIds+'">' + supNms + '</option>';
		recHtml += '</select>';
		/* console.log(recHtml); */
		$("#repltTO").html(recHtml);
		$("#repRecList").chosen();
	});
	$(".repall").click(function() {
		$("#replaymail").trigger("click");
	});

	function fluidDialog() {
		var $visible = $(".ui-dialog:visible");
		// each open dialog
		$visible.each(function() {
			var $this = $(this);
			var dialog = $this.find(".ui-dialog-content").data("ui-dialog");
			// if fluid option == true
			if (dialog.options.fluid) {
				var wWidth = $(window).width();
				// check window width against dialog width
				if (wWidth < (parseInt(dialog.options.maxWidth) + 50)) {
					// keep dialog from filling entire screen
					$this.css("max-width", "90%");
				} else {
					// fix maxWidth bug
					$this.css("max-width", dialog.options.maxWidth + "px");
				}
				//reposition dialog
				dialog.option("position", dialog.options.position);
			}
		});

	}

	$(document).ready(function() {

        <c:if test="${not empty messagePermission}">
			if (!${messagePermission}) {
				$('#compose_new').hide();
			}
		</c:if>

		$("#compose_new").click(function() {
			$('#resetMailCOmposeForm').click();
			$('#composeMAilEditor').parent().removeClass('has-error').find('.form-error').remove();
			/* $("#mailCompose").dialog({
				modal : true,
				width : 'auto', // overcomes width:'auto' and maxWidth bug
				maxWidth : 720,
				height : 'auto',
				modal : true,
				fluid : true, //new option
				dialogClass : "mailComposeLoaded",
				resizable : false,
				position: { my: "center", at: "center", of: window  }
			}); */
			/* $(".mailComposeLoaded").css({
				"position" : "absolute",
				top : 100
			}); */
			if (CKEDITOR.instances.composeMAilEditor) {
				CKEDITOR.instances.composeMAilEditor.destroy();
			}
			editorcmps = CKEDITOR.replace('composeMAilEditor', {
				toolbarLocation : 'bottom',
				width : '100%',
				height : '200px',
				removePlugins : 'dragdrop,basket,elementspath,resize,images',
				toolbar : [ {
					name : 'basicstyles',
					items : [ 'Bold', 'Italic' ]
				}, {
					name : 'paragraph',
					items : [ 'NumberedList', 'BulletedList' ]
				}, {
					name : 'styles',
					items : [ 'Styles', 'Format' ]
				}, {
					name : 'colors',
					items : [ 'TextColor', 'BGColor' ]
				}, {
					name : 'custom',
					items : [ 'Send' ]
				}, {
					name : 'custom2',
					items : [ 'Delete' ]
				}, ]
			});
			CKEDITOR.instances['composeMAilEditor'].setData('');
			editorcmps.on('instanceReady', function(ev) {
				// Prevent drag-and-drop.
				ev.editor.document.on('drop', function(ev) {
					ev.data.preventDefault(true);
				});
			});
			$("#mailCompose").dialog({
				modal : true,
				width : 'auto', // overcomes width:'auto' and maxWidth bug
				maxWidth : 720,
				height : 'auto',
				modal : true,
				fluid : true, //new option
				dialogClass : "mailComposeLoaded",
				resizable : false,
				position : {
					my : "center",
					at : "center",
					of : window
				}
			});
		});

		$(".cancel").click(function() {
			$(".ui-dialog-content").dialog("close");
		});

		$(document).delegate(".viewDetailMsg", 'click', function(e) {

			<c:if test="${not empty messagePermission}">
				if (!${messagePermission}) {
					e.preventDefault();
					return;
				}
			</c:if>

			$('#cancelReply').click();
			$('#resetMailReplyForm').click();
			$("#dialogReply").dialog({
				modal : true,
				width : 'auto', // overcomes width:'auto' and maxWidth bug
				maxWidth : 720,
				height : 'auto',
				modal : true,
				fluid : true, //new option
				dialogClass : "mailBoxLoaded",
				resizable : false,
				title : $(this).find(".hmsgSubject").html()
			});
			$('#replayWithoutOk, #ReplayOkWithSame, .reply-dd-wrapper, #attchMentDatas, .attachmentOnNoReply').hide();
			if ($(this).find('td.email-title').attr('data-fromId') != tenantId) {
				$('#replayWithoutOk, .reply-dd-wrapper, #attchMentDatas').show();
			} else {
				$('#ReplayOkWithSame, .attachmentOnNoReply').show();
			}
			/* $(".mailBoxLoaded").css({
				"position" : "absolute",
				top : 200
			}) */
			/* $("#message_body").html($(this).find(".hmsgBody").html());
			$("#tosendmsg").html($(this).find(".hmsgFrom").html());
			$("#createdDate").html($(this).find(".hmsgcreatedDate").html());
			$("#frommsg").html($(this).find(".hmsgRecieveer").html());
			//var hmsgRecieveer = $(this).find(".hmsgRecieveer");
			//var hmsgReplyId = $(this).find(".hmsgReplyId");
			//var hmsgReplyName = $(this).find(".hmsgReplyName");
			$("#dialogReply").find('.extraInfoCustom').remove();
			$("#dialogReply").append('<div class="extraInfoCustom hide">'+$(this).find('.email-body').html()+'</div>');
			//$("#dialogReply").append(hmsgReplyId);
			//$("#dialogReply").append(hmsgReplyName);
			//$("#dialogReply").append($(this).find(".hmsgReplyName"));
			$("#parentId").val($(this).find('a').data("msgid")); */
			$("#message_body").html($(this).find(".hmsgBody").html());
			var attachDataVal = '';
			if ($(this).find('.attchMentValus').length > 0) {
				attachDataVal = $(this).find('.attchMentValus').html();
			}
			$('#attchMentDatas').html(attachDataVal);
			$("#frommsg").html($(this).find(".hmsgFrom").html());
			$("#createdDate").html($(this).find(".hmsgcreatedDate").html());
			$("#tosendmsg").html($(this).find(".hmsgRecieveer").html());
			$("#subsendmsg").html($(this).find(".hmsgSubject").html());
			
			$(".replyId").html($(this).find(".hmsgReplyId").html());
			/* console.log($(this).find(".hmsgReplyId").html() + "------------"); */
			$(".replyName").html($(this).find(".hmsgReplyName").html());
			
			$("#dialogReply").find('.extraInfoCustom').remove();
			$("#dialogReply").append('<div class="extraInfoCustom hide">' + $(this).find('.email-body').html() + '</div>');
			$('.attachmentOnNoReply').html($('#attchMentDatas').html());
			//$("#dialogReply").append($(this).find(".hmsgRecieveer"));	
			$("#parentId").val($(this).find('a').data("msgid"));
		});
		$('#ReplayOkWithSame').click(function() {
			$("#dialogReply").dialog('close');
		});
		$("#sendReply").click(function() {

			if (!$('#replyForm').isValid()) {
				return false;
			} else if (CKEDITOR.instances['description'].getData() == '') {
				$('#description').parent().removeClass('has-error').find('.form-error').remove();
				$('#description').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}

			/* 	 var SendData = {
					'message' : CKEDITOR.instances['description'].getData(),
					'suppliers' : [],
					'subject' : $(".ui-dialog-title").html(),
					'parentId' : $("#parentId").val(),
					'page' : 0,
					'size' : 5,
					'search' : ''
				};

				$('#repRecList :selected').each(function(i, selected) {
					SendData.suppliers.push({
						"id" : $(selected).val()
					});
				});  */

			/*  var suppliers = [];
			$('#repRecList :selected').each(function(i, selected) {
			suppliers.push($(selected).val());
			}); */
			var file_data = $('#uploadReplyFile').prop('files')[0];

			var form_data = new FormData();
			form_data.append('file', file_data);
			form_data.append('message', CKEDITOR.instances['description'].getData().replace(/&Acirc;&#160;/g, ''));
			/* $(suppliers).each(function(i, value) {
			form_data.append('suppliers', value);
			}); */
			form_data.append('subject', $(".ui-dialog-title").html());
			form_data.append('parentId', $("#parentId").val());
			form_data.append('page', 0);
			form_data.append('size', 5);
			form_data.append('search', '');

			$.ajax({
				url : getContextPath() + '/supplier/sendMessage/' + getEventType() + '/${event.id}',
				data : form_data,
				cache : false,
				xhr : function() { // custom xhr
					myXhr = $.ajaxSettings.xhr();
					if ($('#uploadReplyFile').val().length > 0) {
						if (myXhr.upload) { // check if upload property
							// exists
							myXhr.upload.addEventListener('progress', updateProgress1, false); // for
							// handling
							// the
							// progress
							// of
							// the
							// upload
						}
					}
					return myXhr;
				},
				type : 'POST',
				enctype : 'multipart/form-data',
				processData : false,
				contentType : false,
				beforeSend : function(xhr) {
					$('#loading').show();
					if ($('#uploadReplyFile').val().length > 0) {
						$('.progressbar1').removeClass('flagvisibility');
					}
					xhr.setRequestHeader($('meta[name=\'_csrf_header\']').attr('content'), $('meta[name=\'_csrf\']').attr('content'));
				},
				success : function(data) {
					template = renderMailList(data.data);
					$("#current_page").val(0);
					renderMailInfo(data.recordsTotal, 5, data.draw);
					$('#mails_container').html(template);
					$('#loading').hide();
					$(".ui-dialog-content").dialog("close");
					$('#loading').hide();
					$('.progressbar1').addClass('flagvisibility');
				},
				error : function(request, textStatus, errorThrown) {
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		})
		$("#cancelReply").click(function() {
			$('.ckBlockReplay').hide();
			$('.staticBlock').show();
			$('#resetMailCOmposeForm').click();
		});

		$('#uploadDocx').change(function() {
			if (!$('#login-validation').isValid()) {
				return false;
			}
		});

		$('#uploadReplyFile').change(function() {
			if (!$('#replyForm').isValid()) {
				return false;
			}
		});

		$("#sendmail").click(function() {
			
			if (!$('#login-validation').isValid()) {
				return false;
			} else if (CKEDITOR.instances['composeMAilEditor'].getData() == '') {
				$('#composeMAilEditor').parent().removeClass('has-error').find('.form-error').remove();
				$('#composeMAilEditor').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			}

			/*  var suppliers = []; */
			/* var SendData = {
				'message' : CKEDITOR.instances['composeMAilEditor'].getData(),
				'suppliers' : [],
				'subject' : $("#inputEmail4").val(),
				'page' : 0,
				'size' : 5,
				'search' : ''
			};

			$('#toMsg :selected').each(function(i, selected) {
				SendData.suppliers.push({
					"id" : $(selected).val()
				});
			});  */
			/* $('#toMsg :selected').each(function(i, selected) {
				suppliers.push($(selected).val());
			}); */

			var file_data = $('#uploadDocx').prop('files')[0];

			var form_data = new FormData();
			form_data.append('file', file_data);
			form_data.append('message', CKEDITOR.instances['composeMAilEditor'].getData().replace(/&Acirc;&#160;/g, ''));
			/* $(suppliers).each(function(i, value) {
			form_data.append('suppliers', value);
			}); */
			form_data.append('subject', $("#inputEmail4").val());
			form_data.append('page', 0);
			form_data.append('size', 5);
			form_data.append('search', '');

			$.ajax({
				url : getContextPath() + '/supplier/sendMessage/' + getEventType() + '/${event.id}',
				data : form_data, //(SendData),
				cache : false,
				xhr : function() { // custom xhr
					myXhr = $.ajaxSettings.xhr();
					if ($('#uploadDocx').val().length > 0) {
						if (myXhr.upload) { // check if upload property
							// exists
							myXhr.upload.addEventListener('progress', updateProgress, false); // for
							// handling
							// the
							// progress
							// of
							// the
							// upload
						}
					}
					return myXhr;
				},
				type : 'POST',
				enctype : 'multipart/form-data',
				processData : false,
				contentType : false,
				beforeSend : function(xhr) {
					$('#loading').show();
					if ($('#uploadDocx').val().length > 0) {
						$('.progressbar').removeClass('flagvisibility');
					}
					xhr.setRequestHeader($('meta[name=\'_csrf_header\']').attr('content'), $('meta[name=\'_csrf\']').attr('content'));
				},
				success : function(data) {
					
					template = renderMailList(data.data);
					$("#current_page").val(0);
					renderMailInfo(data.recordsTotal, 5, data.draw)
					$('#mails_container').html(template);
					$('#loading').hide();
					$(".ui-dialog-content").dialog("close");
					$('#loading').hide();
					$('.progressbar').addClass('flagvisibility');
				},
				error : function(request, textStatus, errorThrown) {
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			});

		});

		$("#prevMail , #nextMail").click(function() {

			var ajaxUrl = getContextPath() + '/supplier/eventMessages/' + getEventType() + '/${event.id}';
			current_page = $("#current_page").val();

			var next_page;
			if ($(this).attr("id") == "nextMail") {
				next_page = parseInt(current_page) + 1;

			} else {
				next_page = parseInt(current_page) - 1;
			}
			if (next_page < 0) {
				return;
			}
			totalPages = parseInt(parseInt($("#totalMail").html()) / 5) + 1;
			if (totalPages <= next_page) {
				return;
			}

			$.ajax({
				url : ajaxUrl,
				data : {
					'page' : next_page,
					'size' : 5,
					'search' : ''
				},
				type : "POST",
				beforeSend : function(xhr) {

					$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					
					$("#current_page").val(next_page);
					template = renderMailList(data.data);
					renderMailInfo(data.recordsTotal, 5, data.draw);
					$('#mails_container').html(template);

					//$("#current_page").val(next_page);
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		});

		$("#current_page").val(-1);
		$("#nextMail").trigger('click'); // on page load

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

				html += "<div  class='hmsgFrom hide'>" + message.createdBy.name + " [ " + (message.sentByBuyer ? message.buyer.companyName : message.suppliers[0].companyName) + " ]" + "</div>";

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
				console.log(">>>>>>>>>>>>>>>>>>>>"+eventOwner);
				allSup = message.suppliers;
				allBuy = message.buyer;
				if (message.sentByBuyer) {
				
					$(allSup).each(function(i, supp_S) {
						suppIds += supp_S.id + ",";
						suppNames += supp_S.fullName + " [ " + supp_S.companyName + " ]" + ((allSup.length - 1 != i) ? ", " : "");
						//console.log(suppNames);
					});
				} else {
					$(allBuy).each(function(i, supp_S) {
						
						
						suppIds += supp_S.id + ",";
						
						suppNames += eventOwner + " [ " + supp_S.companyName + " ]" + ((allBuy.length - 1 != i) ? ", " : "");
						//console.log(suppNames);
					});
					
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
						html += '<li><table class="table table-reply  table-hover text-center"><tr class="viewDetailMsg" style=" background-color: #f5f5f5;" data-msgid="' + message.id + '">';
						html += '<td class="width_50_fix align-left email-title" data-fromId="'+reply.createdBy.tenantId+'" >';
						html += "<div class='hmsgFrom hide'>" + reply.createdBy.name + " [ " + message.buyer.companyName + " ] </div>";
						html += "<div class='hmsgRecieveer hide'>" + message.suppliers[0].fullName + " [ " + message.suppliers[0].companyName + " ] </div>";
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

		function renderMailInfo(recordsTotal, limit, eventTarget) {

			totalPages = parseInt(recordsTotal / limit) + 1;
			current_page = parseInt($("#current_page").val()) + 1;

			from_m = (limit * current_page) - (limit - 1);
			if (recordsTotal == 0) {
				from_m = 0;
			}
			to_m = limit * current_page;
			if (current_page == totalPages) {
				to_m = recordsTotal;

			}
			$("#fromPage").html(from_m);
			$("#mainInPage").html(to_m);
			$("#totalMail").html(recordsTotal);
		}

		$(document).delegate(".allreply", 'click', function() {
			$(this).html('<i class="glyph-icon icon-minus"></i>');
			if ($(this).hasClass("collapsed")) {
				$(this).html('<i class="glyph-icon icon-plus"></i>');
			}

		});
	});
</script>
<script>



$( "#inputEmail4" ).keyup(function() {
	/*   alert()
 */	});

</script>
<script>
	// set the date we're counting down to

	// get tag element
	//var countdown = document.getElementById('countdown');
	//alert(countdown);
	// update the tag with id "countdown" every 1 second
	counterMeetings();
	setInterval(function() {

		counterMeetings();

	}, 60000);
	function counterMeetings() {
		$('.countDownTimer1').each(
				function() {

					var target_date = new Date($(this).attr('data-date')).getTime();

					// variables for time units
					var days, hours, minutes, seconds;

					// find the amount of "seconds" between now and target
					var current_date = new Date().getTime();
					var seconds_left = (target_date - current_date) / 1000;

					// do some time calculations
					days = parseInt(seconds_left / 86400);
					seconds_left = seconds_left % 86400;

					hours = parseInt(seconds_left / 3600);
					seconds_left = seconds_left % 3600;

					minutes = parseInt(seconds_left / 60);
					seconds = parseInt(seconds_left % 60);

					// format countdown string + set tag value
					htmldata = '';
					if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
						htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours + '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds
								+ '</span></span>';
					}//$('#countdown').html(htmldata);
					$(this).html(htmldata);

				});
	}

	counterMeetings1();
	setInterval(function() {
		counterMeetings1();
	}, 60000);
	function counterMeetings1() {
		$('.countDownTimer').each(function() {

			var target_date = new Date($(this).attr('data-date')).getTime();

			// variables for time units
			var days, hours, minutes, seconds;

			// find the amount of "seconds" between now and target
			var current_date = new Date().getTime();
			var seconds_left = (target_date - current_date) / 1000;

			// do some time calculations
			days = parseInt(seconds_left / 86400);
			seconds_left = seconds_left % 86400;

			hours = parseInt(seconds_left / 3600);
			seconds_left = seconds_left % 3600;

			minutes = parseInt(seconds_left / 60);
			seconds = parseInt(seconds_left % 60);

			// format countdown string + set tag value
			htmldata = '';
			if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
				htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes + '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
			}//$('#countdown').html(htmldata);
			$(this).html(htmldata);

		});
	}

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
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">