<%@ page isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message code="supplier.onboarding.only" var="display" />
<sec:authorize access="hasRole('SUPPLIER')" var="sup" />
<sec:authorize access="hasRole('BUYER')" var="buy" />
<sec:authorize access="hasRole('OWNER')" var="own" />
<sec:authorize access="hasRole('FINANCE')" var="finance" />
<sec:authorize access="hasRole('ROLE_FINANCE_PO')" var="financePo" />
<sec:authorize access="hasRole('ROLE_FINANCE_SUPPLIER')" var="financeSupplier" />
<sec:authentication property="principal.username" var="loginId" />
<sec:authentication property="principal.id" var="userId" />
<sec:authentication property="principal.languageCode" var="languageCode" />

<script>
	window.onload = function() {
		get_notifications();
	}
</script>

<div id="sb-site">
	<div id="page-wrapper">
		<!-- PAGE HEADER BLOCK -->
		<div id="page-header" class="bg-gradient-9">
			<div id="mobile-navigation">
				<button data-target="#page-sidebar" data-toggle="collapse" class="collapsed" id="nav-toggle">
					<span></span>
				</button>
				<!-- <a class="logo-content-small" title="Procurehere"></a> -->
			</div>
			<div id="header-logo" class="logo-bg">
				<a class="logo-content-big" title='<spring:message code="user.title"/>' href="${pageContext.request.contextPath}${sup ? '/supplier/supplierDashboard' : (buy ? '/buyer/buyerDashboard' : (own ? '/owner/ownerDashboard' : ''))}"> Procurehere </a>
				<!-- <a class="logo-content-small" title="Procurehere"> Procurehere </a> -->
				<a id="close-sidebar" href="#" title='<spring:message code="tooltip.close.sidebar"/>'> <i class="glyph-icon icon-angle-left"></i>
				</a>
			</div>
			<div id="header-nav-left" class="lang-desk">
				<a class="multiLangEn ${languageCode eq 'en' ? 'activeLanguage' : '' }" href="javascript:void(0);" title="English" >EN</a><span class="languagePipe"> | </span><a class="multiLangMs ${languageCode eq 'en' ? '' : 'activeLanguage' }" href="javascript:void(0);" title="Bahasa Malaysia">BM</a>
			</div>
			<div id="header-nav-left" class="lang-mb">
				<a class="multiLangEn" href="javascript:void(0);" title="English">EN</a><span class="languagePipe"> | </span><a class="multiLangMs" href="javascript:void(0);" title="Bahasa Malaysia">BM</a>
			</div>
			<!-- div id="header-nav-left" ${display == 'true' and sup ? 'style="display:none"' : ''}>
				<div class="email-account-btn">
					<a href="#" title="My Account" class="email-inbox clearfix" data-toggle="dropdown"> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/mail.png" /> <span>My Mailbox</span>
					</a>
				</div>
			</div -->
			<!-- #header-nav-left -->
			<div id="header-nav-right">
				<sec:authorize access="!hasRole('ADMIN_READONLY') and hasRole('BUYER') and (hasRole('RFI_CREATE') or hasRole('ADMIN') or hasRole('RFQ_CREATE') or hasRole('RFP_CREATE') or hasRole('RFT_CREATE') or hasRole('RFA_CREATE') or hasRole('PR_CREATE') or hasRole('REQUEST_CREATE'))">
					<div class="creat_btn dropdown" ${display == 'true' and sup ? 'style="display:none"' : ''} data-intro="You can click here to create events" data-position="bottom">
						<div data-toggle="tooltip" title='<spring:message code="tooltip.create.events"/>' data-placement="right" class="creat_btn ">
							<a href="#" data-toggle="dropdown" class="dropdown-toggle" aria-expanded="false"> <span class="round_circle"> <i aria-hidden="true" class="glyph-icon icon-plus"></i>
							</span> <span class="cr-text"><spring:message code="application.create" /></span>
							</a>
						</div>
						<ul class="dropdown-menu" aria-labelledby="dLabel">
							<sec:authorize access="hasRole('RFI_CREATE') or hasRole('ADMIN')">
								<li><a href="${pageContext.request.contextPath}/buyer/createEvent/RFI"> <span class="pull-left"><spring:message code="header.req.for.info" /></span> <span class="pull-right">(RFI)</span>
								</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('RFQ_CREATE') or hasRole('ADMIN')">
								<li><a href="${pageContext.request.contextPath}/buyer/createEvent/RFQ"> <span class="pull-left"><spring:message code="header.req.for.quotation" /></span> <span class="pull-right">(RFQ)</span>
								</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('RFP_CREATE') or hasRole('ADMIN')">
								<li><a href="${pageContext.request.contextPath}/buyer/createEvent/RFP"> <span class="pull-left"><spring:message code="header.req.for.proposal" /> </span> <span class="pull-right">(RFP)</span>
								</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('RFT_CREATE') or hasRole('ADMIN')">
								<li><a href="${pageContext.request.contextPath}/buyer/createEvent/RFT" id="createRftButton"> <span class="pull-left"><spring:message code="header.req.for.tender" /></span> <span class="pull-right">(RFT)</span>
								</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('RFA_CREATE') or hasRole('ADMIN')">
								<li><a href="${pageContext.request.contextPath}/buyer/createEvent/RFA"> <span class="pull-left"><spring:message code="header.req.for.auction" /> </span> <span class="pull-right">(RFA)</span>
								</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('PR_CREATE')">
								<li><a href="${pageContext.request.contextPath}/buyer/createPr"> <span class="pull-left"><spring:message code="header.purchase.requisition" /></span> <span class="pull-right">(PR)</span>
								</a></li>
								</sec:authorize>
							<sec:authorize access="hasRole('REQUEST_CREATE')">
								<li><a href="${pageContext.request.contextPath}/buyer/createSourcingFormRequest"> <span class="pull-left"><spring:message code="header.request.for.sourcing" /></span> <span class="pull-right">(RFS)</span>
								</a></li>
							</sec:authorize>
						</ul>
					</div>
				</sec:authorize>
				<div class="search_web_disply" ${display == 'true' and sup ? 'style="display:none"' : ''} data-intro="You can search events etc from here" data-position="bottom">
					<form id="searchGlobal" method="post" action="${pageContext.request.contextPath}/search/searchGlobal">
						<div class="input-group">
							<input type="text" class="form-control" name="searchVal" value="${searchVal}" placeholder='<spring:message code="supplierdashboard.search"/>'>
							<div class="input-group-btn search-panel">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
									<span id="search_concept" ${own ? 'style="display:none"' : ''|| finance ? 'style="display:none"' : ''}><spring:message code="header.events" /></span> <span id="search_concept" ${own ? 'style="display:none"' : ''|| sup ? 'style="display:none"' : ''|| buy ? 'style="display:none"' : ''}>Suppliers</span> <span id="search_concept" ${own ? 'style="display"' : '' || sup ? 'style="display:none"' : '' || finance ? 'style="display:none"' : '' || buy ? 'style="display:none"' : ''}><spring:message code="header.buyers" /></span> <span
										class="caret"></span>
								</button>
								<ul class="width_100_fix dropdown-menu box-radio pull-right" role="menu">
									<li>
										<div class="form-horizontal">
											<div class="row">
												<div class="col-xs-12">
													<label class="search-radio-inline" ${own ? 'style="display:none"' : '' || finance ? 'style="display:none"' : ''}> <input ${buy ? 'checked' : '' || sup ? 'checked' : ''} type="radio" class="searchRadio" data-text="Events" value="option1" name="opVal" id=""> <spring:message code="header.search.events" />
													</label> <label class="search-radio-inline" ${sup ? 'style="display:none"' : '' || finance ? 'style="display:none"' : '' || buy ? 'style="display:none"' : ''}> <input ${own ? 'checked' : ''} type="radio" class="searchRadio" data-text="Buyers" value="option2" name="opVal" id=""> <spring:message code="header.search.buyers" />
													</label> <label class="search-radio-inline" ${sup || (finance && !financeSupplier) ? 'style="display:none"' : ''  }> <input type="radio" ${finance ? 'checked' : ''} class="searchRadio" data-text="Suppliers" value="option3" name="opVal" id=""> <spring:message code="header.search.suppliers" />
													</label> <label class="search-radio-inline" ${ own ? 'style="display:none"' : '' ||  sup ? 'style="display:none"' : ''  ||  finance ? 'style="display:none"' : ''}> <input type="radio" class="searchRadio" data-text="PR/PO" value="option4" name="opVal" id=""> <spring:message code="header.search.pr.po" />
													</label> <label class="search-radio-inline" ${sup ? 'style="display:none"' : '' || own ? 'style="display:none"' : '' ||  (finance && !financePo) ? 'style="display:none"' : '' ||buy ? 'style="display:none"' : '' }> <input type="radio" class="searchRadio" data-text="PR/PO" value="option4" name="opVal" id=""> ${finance ? 'Search PO' : 'Search PR/PO'}
													</label> <label class="search-radio-inline" ${ own ? 'style="display:none"' : ''  || buy ? 'style="display:none"' : '' || finance ? 'style="display:none"' : ''}> <input type="radio" class="searchRadio" data-text="PO" value="option4" name="opVal" id=""> <spring:message code="header.search.po" />
													</label> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</div>
											</div>
										</div>
									</li>
								</ul>
							</div>
							<span class="input-group-btn search-btn">
								<button class="btn btn-default" type="submit">
									<span class="glyphicon glyphicon-search"></span>
								</button>
							</span>
						</div>
					</form>
					<!-- /input-group -->
				</div>
				<div class="search_mob_disply dropdown " id="dashnav-btn" ${display == 'true' and sup ? 'style="display:none"' : ''}>
					<a href="#" class="popover-button-header tooltip-button" title='<spring:message code="tooltip.dashboard.quick.menu"/>'> <img class="mrg_none" width="20" src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/search.png" style="margin: 8px !important" alt="search image">
					</a>
				</div>
				<div class="notification_display dropdown" id="notifications-btn" ${display == 'true' and sup ? 'style="display:none"' : ''} data-intro="You can view your notifications here" data-position="bottom">
					<a data-toggle="dropdown" href="#" title=""> <span class="notificationCount bs-badge badge-danger"></span> <span class="small-badge bg-yellow"></span> <img class="mrg_none" width="19" src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/bell.png" style="margin: 8px !important" alt="Profile image">
					</a>
					<div class="dropdown-menu float-right menu-width-350">
						<div class="popover-title display-block clearfix pad10A">
							<spring:message code="header.notifications" />
						</div>
						<div class="scrollable-content scrollable-slim-box">
							<ul id="idNotifications" class="no-border notifications-box dd_comman_scroll">
								<li><span class="bg-blue icon-notification glyph-icon icon-bullhorn"></span> <span class="notification-text"><spring:message code="header.yay.nothing.workon" /></span></li>
							</ul>
						</div>
						<div class="pad10A text-center" style="border-top: 1px solid #ebebeb">
							<button class="btn btn-primary ClearAllNotifications marg-top-10" title='<spring:message code="tooltip.clear.all.notifications"/>'>
								<spring:message code="header.clear.all.notifications" />
							</button>
						</div>
						<%-- <div class="pad10A button-pane button-pane-alt text-center">
							<a href="${pageContext.request.contextPath}${sup ? '/supplier' : (buy ? '/buyer' : (own ? '/owner' : ''))}/setAllMarkRead" class="btn btn-primary" class="btn btn-primary" title="Clear all notifications">Clear all notifications</a>
						</div> --%>
					</div>
				</div>
				<a class="header-btn" id="logout-btn" href="${pageContext.request.contextPath}/logout" title='<spring:message code="tooltip.logout"/>'> <img class="mrg_none" width="15" src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/login.png" style="margin: 8px !important" alt="Profile image">
				</a>
			</div>
			<!-- #header-nav-right -->
		</div>
	</div>
</div>
<div style="display: none;" id="notificationCmpl">
	<div class="col-md-12">
		<div class="content-box">
			<div class="pad_all_10 notiMessage"></div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-5"></div>
		<div class="col-md-2  marg-left-20 marg-bottom-10">
			<a class="btn btn-info margin-bottom-10  notiInfoLink" href=""> <spring:message code="header.view.button" />
			</a>
		</div>
	</div>
</div>
<style>
.notificationDialog .ui-widget-overlay {
	background-color: #000;
	opacity: 0.5
}

.dd_comman_scroll {
	margin: 0;
	overflow: scroll;
	overflow-x: hidden;
	height: 300px;
}
</style>
<script>
	if (typeof (Storage) !== "undefined") {
		sessionStorage.setItem("uniqueIdSessionStorage", "${loginId}");
	}

	$(document).delegate(".notification", 'click', function() {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var messageId = $(this).data("msgid");
		var url = $(this).data("url");

		if ($(this).data("read") == false) {
			$.ajax({
				type : "POST",
				url : getContextPath() + "${sup ? '/supplier' : (buy ? '/buyer' : (own ? '/owner' : ''))}/markRead/" + messageId,
				data : {},
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
				},
				success : function(data) {
					// console.log(data);
					get_notifications();
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
				},
				complete : function() {
				}
			});
		}

		if (url != undefined && url != '') {
			window.location.href = $(this).data("url");
		} else {
			if ($(this).data("read") == false) {
				//get_notifications();
			}
			$("#notificationCmpl").find(".notiSubject").html($(this).find(".msgSubj").html());
			$("#notificationCmpl").find(".notiMessage").html($(this).find(".msgBody").html());
			$("#notificationCmpl").find(".notiInfoLink").attr("href", $(this).data("url"));
			$("#notificationCmpl").dialog({
				modal : true,
				width : 720, // overcomes width:'auto' and maxWidth bug
				maxWidth : '90%',
				height : 'auto',
				modal : true,
				fluid : true, //new option
				dialogClass : "notificationDialog",
				resizable : false,
				title : $(this).find(".msgSubj").text(),
				open : function(event, ui) {
					$('.ui-widget-header').css({
						"background-color" : "#ffffff"
					});
					$('.ui-dialog-title').css({
						"color" : "#7f7f7f"
					});
					$('.ui-dialog-content').css({
						"width" : "100% !important",
						"background" : "#ffffff"
					});
					$('.ui-widget-overlay').css({
						"background-color" : "#000",
						"opacity" : "0.5"
					});
				}
			});
			$(".notificationDialog").css({
				'max-width' : '90%'
			});
		}

		//alert(messageId);

	});

	function isJsonString(str) {
		try {
			JSON.parse(str);
		} catch (e) {
			return false;
		}
		return true;
	}

	function get_notifications() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "GET",
			url : getContextPath() + "${sup ? '/supplier' : (buy ? '/buyer' : (own ? '/owner' : (finance ? '/finance' : '')))}/getNotifications",
			data : {},
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data) {
				//console.log(data);
				var html = '';
				var msgCount = 0;
				var readCount = 0;

				//console.log('Notification Data : ', data);

				try {
					$.each(data, function(i, message) {
						//console.log(decodeURIComponent(message.message));
						var subject_a = message.subject;

						if (subject_a.length > 30) {
							subject_a = subject_a.substr(0, 30) + "...";
							//subject_a  = subAr[0]+"...";
							// console.log(message.subject);
						}

						var message_act = message.message; //$(decodeURIComponent(message.message)).text();

						/*if (message_act.length > 60) {
							message_act = message_act.substr(0, 60) + "...";

						}*/
						var message_url = message.url;
						if (typeof message.url === 'undefined' || message.url === '') {
							message_url = "#";
						}
						//	console.log(message_url);
						html += '<li class="notification" data-read="' + message.processed + '" data-msgId="'+message.id+'" data-url="'+message_url+'">';
						html += '<span class="float-right date-set notification-text">' + message.createdDate + '</span>';
						html += '<span class="icon-notification fa ' + (message.processed ? 'fa-envelope-open-o' : 'fa-envelope') + '"></span>';
						html += '<span  class="notification-text"><div class="clearfix"><span classs="align-left"><strong>' + subject_a + '</strong></span>';
						html += '</div></span>';
						html += '<span  class="notification-text msg_box_notify">';
						html += '<span class="messages">'
						html += '' + message_act + '</span></span>';

						/* html += '<span  class="notification-text"><div class="clearfix"><span classs="align-left"><strong>' + subject_a + '</strong></span>';
						html += '</div>';
						html += '<span class="messages">'
						html += '' + message_act + '</span></span>'; */

						html += '<div class="hide"><span class="msgSubj">' + message.subject + '</span><span class="msgBody">' + message.message + '</span></div>';
						html += '</li>';
						msgCount++;
						if (message.processed == false) {
							readCount = readCount + 1;
						}
						//html += '<option value="' + message.id + '">' + message.subject + '</option>';
					});
				} catch (e) {
					console.log('Error parsing notification data...');
				}
				if (msgCount == 0) {
					html += '<li>';
					html += '<span class="notification-text" style="margin:0 auto;float:none;display:table;">Yay! Nothing to work on!</span>';
					html += '</li>';
					$('.ClearAllNotifications').addClass('disabled');
				} else {
					$('.ClearAllNotifications').removeClass('disabled');
				}

				if (readCount == 0) {
					$('.notificationCount').html(readCount).addClass('active');
					$('.small-badge').removeClass('bg-yellow');
				} else {
					$('.notificationCount').html(readCount).removeClass('active');
					$('.small-badge').addClass('bg-yellow');
				}

				$('#idNotifications').html(html);
			},
			error : function(request, textStatus, errorThrown) {
			},
			complete : function() {
			}
		});
	}

	$(document).ready(function() {

		$('.multiLangEn').click(function(e) {
			e.preventDefault();
			$.ajax({
				type : "GET",
				url : '${pageContext.request.contextPath}/admin/changeLocal?lang=en',
				success : function(data) {
					window.location.href = window.location.href;
				},
			});
		});

		$('.multiLangMs').click(function(e) {
			e.preventDefault();
			$.ajax({
				type : "GET",
				url : '${pageContext.request.contextPath}/admin/changeLocal?lang=ms',
				success : function(data) {
					window.location.href = window.location.href;
				},
			});
		});

		//get_notifications();

		$(".searchRadio").change(function() {

			//	$("#searchGlobal").submit();
		});

		$(".toggle").click(function() {
			$(this).parent().toggleClass("highlight");
		});

		//get_notifications(); //Call get_notifications() function when DOM is Ready
		//setInterval(get_notifications, 60000); // every minute
	});

	function trim(s) {
		s = s.replace(/(^\s*)|(\s*$)/gi, "");
		s = s.replace(/[ ]{2,}/gi, " ");
		s = s.replace(/\n /, "\n");
		return s;
	}

	//Mark All Notification as read

	// Fetch envelope data
	$('.ClearAllNotifications').click(function(e) {
		e.preventDefault();
		var loggedInUserId = '${loggedInUserId}';
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			type : "GET",
			url : getContextPath() + "${sup ? '/supplier' : (buy ? '/buyer' : (own ? '/owner' : ''))}/setAllMarkRead",
			data : {},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data) {
				get_notifications();
			},
			error : function(request, textStatus, errorThrown) {
				console.log("error");
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
</script>
<!-- <script id="wpcp_disable_selection" type="text/javascript">
	var image_save_msg = 'You Can Not Save images!';
	var no_menu_msg = 'Context Menu disabled!';
	var smessage = "Content is protected !!";

	function disableEnterKey(e) {
		if (e.ctrlKey) {
			var key;
			if (window.event) {
				key = window.event.keyCode;
			} else {
				key = e.which;
			}
			if (key == 97 || key == 65 || key == 67 || key == 99 || key == 88 || key == 120 || key == 26 || key == 85 || key == 86 || key == 83 || key == 43) {
				return false;
			} else
				return true;
		}
	}

	function disable_copy(e) {
		var elemtype = e.target.nodeName;
		var isSafari = /Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor);
		elemtype = elemtype.toUpperCase();
		var checker_IMG = '';
		if (elemtype == "IMG" && checker_IMG == 'checked' && e.detail >= 2) {
			show_wpcp_message(alertMsg_IMG);
			return false;
		}
		if (elemtype != "TEXT" && elemtype != "TEXTAREA" && elemtype != "INPUT" && elemtype != "PASSWORD" && elemtype != "SELECT") {
			if (smessage !== "" && e.detail == 2)
				//show_wpcp_message(smessage);

				if (isSafari)
					return true;
				else
					return false;
		}
	}
	function disable_copy_ie() {
		var elemtype = window.event.srcElement.nodeName;
		elemtype = elemtype.toUpperCase();
		if (elemtype == "IMG") {
			show_wpcp_message(alertMsg_IMG);
			return false;
		}
		if (elemtype != "TEXT" && elemtype != "TEXTAREA" && elemtype != "INPUT" && elemtype != "PASSWORD" && elemtype != "SELECT") {
			return false;
		}
	}
	function reEnable() {
		return true;
	}
	document.onkeydown = disableEnterKey;
	document.onselectstart = disable_copy_ie;
	if (navigator.userAgent.indexOf('MSIE') == -1) {
		document.onmousedown = disable_copy;
		document.onclick = reEnable;
	}
	function disableSelection(target) {
		if (typeof target.onselectstart != "undefined")
			target.onselectstart = disable_copy_ie;
		else if (typeof target.style.MozUserSelect != "undefined") {
			target.style.MozUserSelect = "none";
		} else
			target.onmousedown = function() {
				return false
			}
		target.style.cursor = "default";
	}
	window.onload = function() {
		disableSelection(document.body);
	};
</script>
<script id="wpcp_disable_Right_Click" type="text/javascript">
	document.ondragstart = function() {
		return false;
	}
	function nocontext(e) {
		return false;
	}
	document.oncontextmenu = nocontext;
</script>
<script id="wpcp_css_disable_selection" type="text/javascript">
	var e = documnt.getElementsByTagName('body')[0];
	if (e) {
		e.setAttribute('unselectable', 'on');
	}
</script>
 -->
<style>
.user_mail1 {
	color: rgba(255, 255, 255, 1);
	margin: 5px 0px;
	font-family: 'open_sansregular', "Helvetica Neue", Helvetica, Arial,
		sans-serif;
	font-weight: 400;
	font-size: 12px;
	margin-top: 0.5px;
}

#header-nav-left {
	display: flex;
	align-items: center;
	height: 100%;
}

.lang-mb {
	display: none !important;
}

@media only screen and (max-width: 767px) {
	.header-nav-left a {
		color: #fff;
	}
	.lang-desk {
		display: none !important;
	}
	.lang-mb {
		display: block !important;
		position: relative;
		top: 27px;
	}
	#mobile-navigation {
		width: 65px;
	}
	#mobile-navigation #nav-toggle span {
		left: 10px;
		bottom: 36px;
	}
	#header-nav-left {
		margin-right: 0;
		margin-left: 5px;
	}
	#header-nav-right {
    	margin-top: 18px;
    	margin-right: 12px;
    }
	#header-nav-right .creat_btn.dropdown {
		margin: 0px 15px 0 10px;
	}
}

#header-nav-left a {
	color: #fff;
	font-size: 15px;
	margin-right: 3px;
}

.creat_btn .dropdown-menu>li>a {
	width: 240px;
}
</style>

<script type="text/javascript">
	$(function() {
		$("[data-toggle='tooltip']").tooltip();
	});
</script>