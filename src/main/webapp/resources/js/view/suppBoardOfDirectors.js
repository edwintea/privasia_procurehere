$(document).delegate('#showConfirmDeletePopUp', 'click', function (e) {
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    $('#confirmDeleteDirector').removeClass('fade');
    $('#confirmDeleteDirector').removeClass('hideModal');
    $('#confirmDeleteDirector').addClass('showModal');
    var deleteId = $(this).attr('delete-id');
    var deleteName = $(this).attr('delete-name');
    $('#confirmDeleteDirector').attr('delete-id', deleteId);
    $('#confirmDeleteDirector').attr('delete-name', deleteName);

});

$('#confDelDir').click(function () {
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    var deleteId = $('#confirmDeleteDirector').attr('delete-id');
    var deleteName = $('#confirmDeleteDirector').attr('delete-name');
    $('#confirmDeleteDirector').removeClass('showModal');
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    $.ajax({
        url: getContextPath() + "/removeDirector",
        type: "GET",
        data: {
            id: deleteId,
            name: deleteName
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            $('#loading').show();
        },
        success: function (data, textStatus, request) {
            var table = '';
            var info = request.getResponseHeader('error');
            if (info) {
                $('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
                $('div[id=idGlobalSuccess]').show();
                if (document.querySelectorAll("#idGlobalSuccess")[0]) {
                    document.querySelectorAll("#idGlobalSuccess")[0].style.display = "none"
                }
            }
            $.each(data, function (i, item) {
                table += '<tr>' +
                    '<td><div><span class="col-sm-6 p-l-0 no-padding" id="showConfirmDeletePopUp" delete-id="' + item.id + '" delete-name="' + item.directorName + '"><a><i class="fa fa-trash-o" aria-hidden="true"></i></a></span>' +
                    '<span class="col-sm-6 p-l-0 no-padding" id="editDirector" edit-id=' + item.id + '><a><i class="fa fa-edit" aria-hidden="true"></i></a></span></div></td>' +
                    '<td>' + (i + 1) + '</td>' +
                    '<td>' + item.directorName + '</td>' +
                    '<td>' + item.idType + '</td>' +
                    '<td>' + item.idNumber + '</td>' +
                    '<td>' + item.dirType + '</td>' +
                    '<td>' + (item.dirEmail != undefined ? item.dirEmail : item.dirEmail != null ? item.dirEmail : "") + '</td>' +
                    '<td>' + (item.dirContact != undefined ? item.dirContact : item.dirContact != null ? item.dirContact : "") + '</td>' +
                    '</tr>'
            });
            $('#directorsDisplay tbody').html(table);
        },
        error: function (request, textStatus, errorThrown) {
            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
            $('div[id=idGlobalError]').show(); if (document.querySelectorAll("#idGlobalError")[0]) {
                document.querySelectorAll("#idGlobalError")[0].style.display = "none"
            }
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });
})

$('#confirmDeleteDirectorClose').click(function () {
    $('#confirmDeleteDirector').removeClass('showModal');
    $('#confirmDeleteDirector').addClass('hideModal fade');
})

$('#confirmDeleteDirectorDismiss').click(function () {
    $('#confirmDeleteDirector').removeClass('showModal');
    $('#confirmDeleteDirector').addClass('hideModal fade');
})

$(document).delegate('#editDirector', 'click', function (e) {
    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var editId = $(this).attr('edit-id');
    $.ajax({
        url: getContextPath() + "/editDirector",
        type: "GET",
        data: {
            id: editId,
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            $('#loading').show();
        },
        success: function (data, textStatus, request) {
            $('#directorId').val(data.id);
            $('#idDirectorName').val(data.directorName);
            $('#idType').val(data.idType).trigger("chosen:updated");
            $('#idNumber').val(data.idNumber);
            $("#idDirType").val(data.dirType).trigger("chosen:updated");
            $('#idDirEmail').val(data.dirEmail);
            $('#idDirContact').val(data.dirContact);
            $('#saveDir').prop("disabled", false);
            $('#addNewDir').prop("disabled", true);
            $('#saveDir').addClass('btn-primary');
            $('#addNewDir').addClass('disabled')
            $('#saveDir').removeClass('disabled');
            $('#addNewDir').removeClass('btn-black');
            $('#boardOfDirectorForm').isValid();

        },
        error: function (request, textStatus, errorThrown) {
            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
            $('div[id=idGlobalError]').show();
            $('#loading').hide();
        },
        complete: function () {
            $('#loading').hide();
        }
    });

});
