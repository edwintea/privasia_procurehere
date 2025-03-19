$('#financialDocuments').change(function () {
    console.log('$(this).val() : ', $(this).val());
    $(".show_name2").html($(this).val());
    $(".up_btn").removeClass('btn-gray').addClass('btn-blue');
});

$("#idPaidUpCapital").change(function () {
    var value = document.getElementById("idPaidUpCapital").value;
    if (value) {
        var existingValue = document.getElementById("idPaidUpCapital").value
        var num = Number(existingValue.replace(/,/g, '')).toFixed(2)
        // num is string
        if (num != NaN) {
            document.getElementById("idPaidUpCapital").value = Number(num).toLocaleString(undefined, { minimumFractionDigits: 2 });
        }
    }
});

$('#financialDocumentsUpload').click(function (e) {
    console.log('..... Uploading document.....');

    e.preventDefault();
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();

    if (!$('#financialDocumentsForm').isValid()) {
        return false;
    }

    if ($('#financialDocuments').val().length == 0) {
        $('p[id=idGlobalErrorMessage]').html("Please select company profile upload file.");
        $('div[id=idGlobalError]').show();
        return;
    }

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var financialDocDesc = $('#financialDocDesc').val();
    var oMyForm = new FormData();
    oMyForm.append("financialDocumentsFile", $('#financialDocuments')[0].files[0]);
    oMyForm.append("desc", financialDocDesc);
    console.log(oMyForm);
    $.ajax({
        url: getContextPath() + "/financialDocumentsUpload",
        data: oMyForm,
        type: "POST",
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            $('#loading').show();
        },
        success: function (data, textStatus, request) {
            document.getElementById("financialDocuments").value = "";
            var info = request.getResponseHeader('error');
            $('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
            $('div[id=idGlobalSuccess]').show();
            $('span[id=idfinancialDocumentsSpan]').text('');
            $("#financialDocumentsUpload").removeClass('btn-blue').addClass('btn-gray');
            $('#financialDocDesc').val(null)
            var table = '';
            $.each(data, function (i, item) {
                var itemdescription = '&nbsp;';
                if (item.description != null) {
                    itemdescription = item.description;
                }
                table += '<tr>' + '<td class="width-33"><form:form method="GET">' + '<a class="word-break" href="' + getContextPath() + '/downloadFinancialDocuments/' + item.id + '">' + item.fileName + '</a>' + '</form:form>' + '</td>'
                    + '<td class="width-33">' + itemdescription + '</td>' + '<td class="width-33"><span class="removeFinancialDocsFile" removeFinancialDocsId="' + item.id + '" financialDocsFileName="' + item.fileName
                    + '"><span class="col-sm-10 no-padding">' + item.uploadDate + '</span><span class="col-sm-2 no-padding align-right"><a><i class="fa fa-trash-o" aria-hidden="true"></a></i></span></span></td>' + '</tr>';
            });
            $('#financialDocumentsDisplay tbody').html(table);
        },
        error: function (request, textStatus, errorThrown) {
            if (request.getResponseHeader('error')) {
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
                $('div[id=idGlobalError]').show();
            }
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });

});

$(document).delegate('.removeFinancialDocsFile', 'click', function (e) {
    e.preventDefault();
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var removeOtherId = $(this).attr('removeFinancialDocsId');
    var otherCredFile = $(this).attr('financialDocsFileName');
    $.ajax({
        url: getContextPath() + "/removeFinancialDoc",
        data: {
            id: removeOtherId,
            file: otherCredFile
        },
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            $('#loading').show();
        },
        success: function (data, textStatus, request) {
            var info = request.getResponseHeader('error');
            $('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
            $('div[id=idGlobalSuccess]').show();

            var table = '';
            $.each(data, function (i, item) {
                var itemdescription = '&nbsp;';
                if (item.description != null) {
                    itemdescription = item.description;
                }
                table += '<tr>' + '<td class="width-33"><form:form method="GET">' + '<a class="word-break" href="' + getContextPath() + '/downloadFinancialDocuments/' + item.id + '">' + item.fileName + '</a>' + '</form:form>' + '</td>'
                    + '<td class="width-33">' + itemdescription + '</td>' + '<td class="width-33"><span class="removeFinancialDocsFile" removeFinancialDocsId="' + item.id + '" financialDocsFileName="' + item.fileName
                    + '"><span class="col-sm-10 no-padding">' + item.uploadDate + '</span><span class="col-sm-2 no-padding align-right"><a><i class="fa fa-trash-o" aria-hidden="true"></a></i></span></span></td>' + '</tr>';
            });
            $('#financialDocumentsDisplay tbody').html(table);
        },
        error: function (request, textStatus, errorThrown) {
            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
            $('div[id=idGlobalError]').show();
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });

});