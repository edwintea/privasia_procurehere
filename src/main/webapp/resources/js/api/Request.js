var Request =   {
    header  : $("meta[name='_csrf_header']").attr("content"),
    token   : $("meta[name='_csrf']").attr("content"),
    context : getContextPath(),
    datas   :   [],
    response:{
        success : (request)=>{
            var success = request.getResponseHeader('success');
            var info = request.getResponseHeader('info');
            if (success != undefined) {
                $('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
                $('.alert').hide();
                $('.alert-danger').hide();
                $('div[id=idGlobalSuccess]').show();

            }
            if (info != undefined) {
                $('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
                $('.alert').hide();
                $('.alert-danger').hide();
                $('div[id=idGlobalInfo]').show();
            }
        },
        error : ()=>{
            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
            $('.alert').hide();
            $('.alert-danger').hide();
            $('div[id=idGlobalError]').show();
        }
    },
    endpoint : {
        po:{
            viewItem : '/buyer/viewPoItems'
        }
    },
    send    :   (ep,data,f)=>{
        $.ajax({
            url : Request.context + ep,
            data : JSON.stringify(data),
            type : "POST",
            contentType : "application/json",
            dataType : 'json',
            beforeSend : function(xhr) {
                xhr.setRequestHeader(Request.header, Request.token);
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
            },
            success : function(data, textStatus, request) {
                Request.datas=data;
                Request.response.success();
                f();
            },
            error : function(request, textStatus, errorThrown) {
                Request.response.error();
                $('#loading').hide();
                f();
            },
        });
    }
}
