String.prototype.format = function () {
    if (arguments.length == 0) return this;
    var param = arguments[0];
    var s = this;
    if (typeof (param) == 'object') {
        for (var key in param)
            s = s.replace(new RegExp("\\{" + key + "\\}", "g"), param[key]);
        return s;
    } else {
        for (var i = 0; i < arguments.length; i++)
            s = s.replace(new RegExp("\\{" + i + "\\}", "g"), arguments[i]);
        return s;
    }
}
layui.define(['layer', 'form', 'element', 'table'], function (exports) {
    var table = layui.table, $ = layui.$;
    layui.link('/static/module/global.css');

    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('req_type', 'async');
        },
        dataFilter: function (data, type) {
            // 拦截无权限的操作
            if (data && data[0] === '{' && data.indexOf('"success":false') >= 0) {
                var content = JSON.parse(data);
                // layer.msg(content.message, {
                //     time:2000
                // });
                layer.alert(content.message, function (index) {
                    layer.close(index);
                });
                return;
            }
            return data;
        },
        complete: function (xhr, textStatus) {
            var redirect = xhr.getResponseHeader("redirect");
            if (redirect) { //若HEADER中含有REDIRECT说明后端想重定向
                var win = window;
                // while(win != win.top){
                //     win = win.top;
                // }
                win.location.href = redirect;//将后端重定向的地址取出来,使用win.location.href去实现重定向的要求
            }
        },
        cache: false
    });

    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        // url添加时间戳
        var timestamp = new Date().getTime();
        var idx = options.url.indexOf('?');
        if (idx < 0) {
            options.url = options.url + '?_t=' + timestamp;
        } else if (idx == options.url.length - 1) {
            options.url = options.url + '_t=' + timestamp;
        } else {
            options.url = options.url + '&_t=' + timestamp;
        }
    });
    window.calcTablePos = function (tableItem) {
        var offset = 10;
        var _top = $(tableItem).offset().top - $(window).scrollTop();
        return 'full-' + (_top + offset);
    }
    window.calcTablePosWithOffset = function (tableItem, offset) {
        if (offset == null) {
            offset = 10;
        }
        var _top = $(tableItem).offset().top - $(window).scrollTop();
        return 'full-' + (_top + offset);
    }
    window.colorFormat = function (item, color, condition) {
        if (condition) {
            return '<span style="color: ' + color + ';">' + item + '</span>';
        } else {
            return item;
        }
    }
    table.set({
        request: {
            pageName: 'current' //页码的参数名称，默认：page
            , limitName: 'size' //每页数据量的参数名，默认：limit
        },
        response: {
            statusCode: 0 //成功的状态码，默认：0
        },
        height: 'full',
        parseData: function (data) {
            var dataTemp = {};
            dataTemp[this.response.statusName] = data.code;
            dataTemp[this.response.msgName] = data.message;
            if (data.success) {
                dataTemp[this.response.countName] = data.data.total;
                dataTemp[this.response.dataName] = data.data.records;
            }
            return dataTemp;
        },
        // headers: {
        //     Authorization: layui.data('aigp').Authorization
        // },
        async: false
    });
    exports('global', {}); //注意，这里是模块输出的核心，模块名必须和use时的模块名一致
});