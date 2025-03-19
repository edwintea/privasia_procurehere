   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
   <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
   <div id="page-wrapper">
 <div id="page-content-wrapper">
                <div id="page-content">
                    <div class="container">
                        <!-- pageging  block -->

                        <ol class="breadcrumb">
                            <li><a href="#"><spring:message code="application.dashboard" /></a></li>
                            <li class="active"><spring:message code="application.newtender" /></li>
                        </ol>
                        <section class="create_list_sectoin">
                            <div class="Section-title title_border gray-bg mar-b20">
                                <h2 class="trans-cap tender-request-heading"><spring:message code="eventdescription.createtender" /></h2>

                            </div>


                            <div class="example-box-wrapper wigad-new">
                                <div id="form-wizard-2" class="form-wizard">
                                    <ul>
                                        <li class="tb_1 active">
                                            <a href="#step-1" data-toggle="tab">
                                                <label class="wizard-step"> <span class="inner_circle"> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" /> </span> </label>
                                                <span class="wizard-description"><spring:message code="eventsummary.eventdetail.title" /></span> </a>
                                        </li>

                                        <li class="tb_2 ">
                                            <a href="#step-2" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">2</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark"/> </span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="application.document" /> </span> </a>
                                        </li>
                                        <li class="tb_3">
                                            <a href="#step-3" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">3</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /> </span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="application.supplier.detail" /> </span> </a>
                                        </li>
                                        <li class="tb_4">
                                            <a href="#step-4" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">4</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /> </span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="label.meeting" /></span> </a>
                                        </li>
                                        <li class="tb_5">
                                            <a href="#step-5" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">5</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /> </span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="questionnaire.label" /></span> </a>
                                        </li>

                                        <li class="tb_5">
                                            <a href="#step-6" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">6</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /></span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="eventdescription.billofquantity.label" /></span> </a>
                                        </li>

                                        <li class="tb_5">
                                            <a href="#step-7" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">7</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /></span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="application.submission" /></span> </a>
                                        </li>

                                        <li class="tb_5">
                                            <a href="#step-8" data-toggle="tab">
                                                <label class="wizard-step"><span class="inner_circle"><span class="step_num">8</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" /> </span>
                                                </label>
                                                <span class="wizard-description"><spring:message code="application.summary" /></span> </a>
                                        </li>
                                    </ul>
                                    <div class="tab-content">
          	                            <input type="hidden" id="eventId" value="${rftEvent.id}">
										<div class="tab-pane active marg-bottom-15" id="step-1">
											<div class="upload_download_wrapper clearfix mar-t20 event_info">
												<jsp:include page="eventDetails.jsp"></jsp:include>
											</div>
										</div>
										
                                        <div class="tab-pane" id="step-2">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                            	<jsp:include page="eventDocuments.jsp"></jsp:include>
                                            </div>
                                        </div>
                                        <div class="tab-pane" id="step-3">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <h4>Step 3 Content Goes Here </h4></div>
                                        </div>
                                        <div class="tab-pane" id="step-4">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <jsp:include page="eventMeetings.jsp"></jsp:include></div>
                                        </div>
                                        <div class="tab-pane" id="step-5">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <h4>Step 5 Content Goes Here </h4></div>
                                        </div>
                                        <div class="tab-pane" id="step-6">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <h4>Step 6 Content Goes Here </h4></div>
                                        </div>
                                        <div class="tab-pane" id="step-7">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <h4>Step 7 Content Goes Here </h4></div>
                                        </div>
                                        <div class="tab-pane" id="step-8">
                                            <div class="upload_download_wrapper clearfix mar-t20 event_info">
                                                <h4>Step 8 Content Goes Here </h4></div>
                                        </div>
                                        

                                    </div>

                                </div>

							</div>



                        </section>
                        </div>
                    </div>
                
                </div>
            </div>
          <!-----------popup-------------------->
            <div class="modal fade" id="myModal6" role="dialog">
                <div class="modal-dialog for-delete-all reminder">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3><spring:message code="label.add.reminder" /></h3>
                            <button class="close for-absulate" type="button" data-dismiss="modal">×</button>
                        </div>


                        <div class="modal-body">
                            <label><spring:message code="label.remind.me" /></label>
                            <input type="text" class="form-control" />
                            <select class="custom-select">
                                <option><spring:message code="rfaevent.hours" /></option>
                                <option><spring:message code="label.minute" /></option>
                                <option><spring:message code="application.second" /></option>


                            </select>

                        </div>
                        <div class="modal-footer pad_all_15">
                            <button type="button"  class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal"><spring:message code="application.save" /></button>
                            <button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.reset" /></button>
                        </div>
                    </div>

                </div>
            </div>

            <!-----------popup-------------------->
          
            </div>
            <style>
            	ul#industryCategoryList {
				    list-style: none;
				    padding: 0;
				    position: absolute;
				    z-index: 9;
				    background: #fff;
				    border-left: 1px solid #ccc;
				    width: 100%;
				    border-right: 1px solid #ccc;
				    max-height: 200px;
    				overflow: auto;
				}
				#industryCategoryList li:first-child{
					border-top: 1px solid #ccc;
				}
				#industryCategoryList li {
				    border-bottom: 1px solid #ccc;
				    padding: 10px;
				    cursor: pointer;
				}
				#industryCategoryList li:hover {
				    background: #0cb6ff;
				    color: #fff;
				}
            </style>
  <script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
  <script type="text/javascript">
            /* Datepicker bootstrap */

            $(function () {
                "use strict";
                $('.bootstrap-datepicker').bsdatepicker({
                    format: 'dd/mm/yyyy'
                });


                $(document).on("click", ".step_btn_1", function () {
                    $("#demo-form").submit();
                });
            });
        </script>	
      <script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
	  <script>
      $.validate({
      	lang: 'en',
      	modules : 'date'
      });
	</script>
		
		<!-- daterange picker js and css start -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
   <!-- EQul height js-->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
    <!-- Theme layout -->
    <%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>" ></script>  --%>  

         
        <!-----------popup Add Reminder-------------------->
         <div class="modal fade" id="myModal6" role="dialog">
        <div class="modal-dialog for-delete-all reminder">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <h3><spring:message code="label.add.reminder" /></h3>
                    <button class="close for-absulate" type="button" data-dismiss="modal">×</button>
                </div>
                <div class="modal-body">
                    <label><spring:message code="label.remind.me" /></label>
                    <input type="text" class="form-control" />
                    <select class="custom-select">
                        <option><spring:message code="rfaevent.hours" /></option>
                        <option><spring:message code="label.minute" /></option>
                        <option><spring:message code="application.second" /></option>
                    </select>
                    <div class="before-time-msg"><spring:message code="label.event.start.date.time" /> &amp; <spring:message code="application.time2" /></div>
                </div>

                <div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
                    <button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal"><spring:message code="application.save" /></button>
                    <button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.reset" /></button>
                </div>
            </div>
        </div>
    </div>