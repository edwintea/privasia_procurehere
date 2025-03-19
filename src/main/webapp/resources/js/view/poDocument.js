function updateProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
		progress(percentComplete, $('.progressbar'));
		var percentVal = percentComplete + '%';
		//   console.log(percentVal);
	} else {
		// Unable to compute progress information since the total size is
		// unknown
		//   console.log('unable to complete');
	}
}

$(document).ready(function() {

    const clearNotif=()=> {
        $('.text-danger').html('');
        $('div[id=Load_File-error-dialog]').hide();
        $('div[id=idGlobalError]').hide();
        $('div[id=idGlobalSuccess]').hide();
    };

    $('.mega').on('scroll', function() {
        $('.header').css('top', $(this).scrollTop());
    });

    var files = [];
    $(document).on("change", "#poUploadDocument", function(event) {
        files = event.target.files;
    });
    $('.mega').on('scroll', function() {
        $(this).find('.header').css('top', $(this).scrollTop());
    });
    $('#poUploadDocument').click(function(e) {
        $('div[id=Load_File-error-dialog]').hide();
        $('.text-danger').html('');
    });

    //PH-4113
    // upload Po files

    $('#uploadPoDoc').click(function(e) {
        e.preventDefault();
        $('div[id=idGlobalError]').hide();
        $('div[id=idGlobalSuccess]').hide();
        $('.text-danger').html('');
        if (!$('#poDocumentForm').isValid()) {
            $('div[id=Load_File-error-dialog]').show();
            return false;
        }
        if ($('#poUploadDocument').val().length == 0) {
            $('p[id=idGlobalErrorMessage]').html("Please select upload file");
            $('div[id=idGlobalError]').show();
            //$('.fileinput-new.input-group').after('<p style="margin-bottom:10px;" class="text-danger">Please select upload file.</p>');
            return;
        }
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        var file_data = $('#poUploadDocument').prop('files')[0];
        var docDescription = $('#docDescription').val();
        var docType = $('#docType').val();
        //   console.log("docType :" +docType);
        var poId = $('#poId').val();
        var form_data = new FormData();
        form_data.append('file', file_data);
        form_data.append("desc", docDescription);
        form_data.append("docType", docType);
        form_data.append("poId", poId);
        form_data.append("internal", false);
        //   console.log("prId  ::  " + prId);
        var uploadUrl = getContextPath() + '/buyer/po/poDocument';
        $.ajax({
            url : uploadUrl,
            data : form_data,
            cache : false,
            xhr : function() {
                // custom xhr
                myXhr = $.ajaxSettings.xhr();
                // check if upload property exists
                if (myXhr.upload) {
                //	 for handling the progress of the upload
                    myXhr.upload.addEventListener('progress', updateProgress, false);

                }
                return myXhr;
            },

            type : "POST",
            enctype : 'multipart/form-data',
            processData : false,
            contentType : false,
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
                $('#loading').show();
                $('.progressbar').removeClass('flagvisibility');
            },
            success : function(data, textStatus, request) {
                var info = request.getResponseHeader('success');
                //   console.log("Success message : " + info);
                document.getElementById("poUploadDocument").value = "";
                document.getElementById("docDescription").value = "";
                $(".show_name").html('');
                $('p[id=idGlobalSuccessMessage]').html(info);
                $('div[id=idGlobalSuccess]').show();
            //	$('div[id=Load_File-error-dialog]').hide();
                $.jGrowl(info, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-green'
                });

                var table = '';
                $.each(data, function(i, item) {

                    var itemdescription = '&nbsp;';
                    if (item.description != null) {
                        itemdescription = item.description;
                    }
                    var itemDocType = '&nbsp;';
                    if (item.docType != null) {
                        itemDocType = item.docType;
                    }
                    table += '<tr>';
                    table += '<td class="width_200 width_200_fix align-center">';
                    table += '<form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/po/downloadPoDocument/' + item.id + '">';
                    table += '<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;';
                    table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '">';
                    table += '<img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp;';
                    table += '<span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" editDocType="' + itemDocType + '" >';
                    table += '<a href=""><img src="'+ getContextPath() + '/resources/images/edit1.png"></a></span></form></td>';
                    table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>';
                    table += '<td class="width_300 width_300_fix align-left">'+ itemdescription + '</td>';
                    // table += '<td class="width_100 width_100_fix align-left">' + itemDocType + '</td>';
                    table += '<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td></tr>';
                });
                $('#poDocList tbody').html(table);
                $('#loading').hide();
                $('#poUploadDocument').change();
            },
            error : function(request, textStatus, errorThrown) {
                var error = request.getResponseHeader('error');
                $('p[id=idGlobalErrorMessage]').html(error);
                $('div[id=idGlobalError]').show();

                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
                $('#loading').hide();
            },
            complete : function() {
                $('#loading').hide();
                $('.progressbar').addClass('flagvisibility');
                progress(0, $('.progressbar'));
            }
        });
    });



$(document).delegate('.removeDocFile', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteDocument').find('#deleteIdDocument').val($(this).attr('removeDocId'));
	$('#confirmDeleteDocument').modal();
});

$(document).delegate('#confDelDocument', 'click', function(e) {
    e.preventDefault();
    clearNotif()

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var removeDocId = $('#confirmDeleteDocument').find('#deleteIdDocument').val();
    var poId = $('#poId').val();
    var removeUrl = getContextPath() + '/buyer/po/removePoDocument';

    $.ajax({
        url : removeUrl,
        data : ({
            removeDocId : removeDocId,
            poId : poId
        }),
        type : "GET",
        beforeSend : function(xhr) {
            xhr.setRequestHeader(header, token);
            $('#loading').show();
        },
        success : function(data, textStatus, request) {
            var info = request.getResponseHeader('success');
            $('p[id=idGlobalSuccessMessage]').html(info);
            $('div[id=idGlobalSuccess]').show();
            $.jGrowl(info, {
                sticky : false,
                position : 'top-right',
                theme : 'bg-green'
            });

            var table = '';
            $.each(data, function(i, item) {

                var itemdescription = '&nbsp;';
                if (item.description != null) {
                    itemdescription = item.description;
                }
                var itemDocType = '&nbsp;';
                if (item.docType != null) {
                    itemDocType = item.docType;
                }
                table += '<tr>';
                table += '<td class="width_200 width_200_fix align-center">';
                table += '<form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/po/downloadPoDocument/' + item.id + '">';
                table += '<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;';
                table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '">';
                table += '<img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp;';
                table += '<span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" editDocType="' + itemDocType + '">';
                table += '<a href=""><img src="'+ getContextPath() + '/resources/images/edit1.png"></a></span></form></td>';
                table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>';
                table += '<td class="width_300 width_300_fix align-left">'+ itemdescription + '</td>';
                // table += '<td class="width_100 width_100_fix align-left">' + itemDocType + '</td>';
                table += '<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td></tr>';
            });
            $('#poDocList tbody').html(table);
            $('#loading').hide();
//					$('#poUploadDocument').change();
        },
        error : function(request, textStatus, errorThrown) {
            //   console.log(JSON.stringify(request));
            var error = request.getResponseHeader('error');
            $('p[id=idGlobalErrorMessage]').html(error);
            $('div[id=idGlobalError]').show();

            $.jGrowl(error, {
                sticky : false,
                position : 'top-right',
                theme : 'bg-red'
            });
            $('#loading').hide();
        },
        complete : function() {
            $('#confirmDeleteDocument').modal('hide');
            $('#loading').hide();
        }
    });
});

//Edit Document
$(document).delegate('.editDocFile', 'click', function(e) {
	e.preventDefault();
	$("#documentDescriptionPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '50%',
		maxWidth : 400,
		minHeight : 150,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('#documentDescriptionPopup').find('#editIdDocument').val($(this).attr('editDocId'));
	var description = $(this).attr('editDocDec');
	var doctype = $(this).attr('editDocType');
	if(description == 'undefined'){
		description = "";
	}
	if(doctype == 'undefined'){
		doctype = "OTHER";
	}
	$('#documentDescriptionPopup').find('#docDec').val(description);
	$("#documentDescriptionPopup").find('#docTypeUpdate').val(doctype).trigger("chosen:updated");
	$('.ui-dialog-title').text(updateDocumentLabel);
});

$(document).delegate('#confDocDec', 'click', function(e) {
        e.preventDefault();
        $('div[id=idGlobalError]').hide();
        $('div[id=idGlobalSuccess]').hide();
        var docId = $("#documentDescriptionPopup").find('#editIdDocument').val();
        var docDesc = $("#documentDescriptionPopup").find('#docDec').val();
        var docType = $("#documentDescriptionPopup").find('#docTypeUpdate').val();
        var poId = $('#poId').val();
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");
        alert
        $.ajax({
            type : "POST",
            url : getBuyerContextPath('/po/updatePoDocumentDesc'),
            data : {
                docId : docId,
                docDesc : docDesc,
                docType : docType,
                poId : poId,
                internal : false
            },
            beforeSend : function(xhr) {
                $('#loading').show();
                xhr.setRequestHeader(header, token);
                xhr.setRequestHeader("Accept", "application/json");
            },
            success : function(data, textStatus, request) {
                var info = request.getResponseHeader('success');

                var table = '';
                $.each(data, function(i, item) {

                    var itemdescription = '&nbsp;';
                    if (item.description != null) {
                        itemdescription = item.description;
                    }
                    var itemDocType = '&nbsp;';
                    if (item.docType != null) {
                        itemDocType = item.docType;
                    }
                    table += '<tr>';
                    table += '<td class="width_200 width_200_fix align-center">';
                    table += '<form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/po/downloadPoDocument/' + item.id + '">';
                    table += '<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;';
                    table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '">';
                    table += '<img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp;';
                    table += '<span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" editDocType="' + itemDocType + '" >';
                    table += '<a href=""><img src="'+ getContextPath() + '/resources/images/edit1.png"></a></span></form></td>';
                    table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>';
                    table += '<td class="width_300 width_300_fix align-left">'+ itemdescription + '</td>';
                    table += '<td class="width_100 width_100_fix align-left">' + itemDocType + '</td>';
                    table += '<td class="width_150 width_150_fix align-left">' + item.uploadDate + '</td></tr>';
                });
                $('#poDocList tbody').html(table);
                $('#loading').hide();
            },
            error : function(e) {
                console.log(JSON.stringify(request));
                // $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
                $('div[id=idGlobalError]').show();
                $('#loading').hide();
            },
            complete : function() {
                $("#documentDescriptionPopup").dialog('close');
                $('#loading').hide();
            }
        });
    });
});

