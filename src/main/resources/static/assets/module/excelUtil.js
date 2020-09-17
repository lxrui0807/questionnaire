/**
 * 导入导出
 *
 */
layui.define(['layer','config','upload','table'], function (exports) {
    var $=layui.jquery;
    var layer = layui.layer;
    var config = layui.config;
    var upload = layui.upload;
    var table = layui.table;
    var importHtml='<div class="layui-card-body" style="padding: 15px 15px;text-align: center;">'+
                        '<button id="file-btn-import" class="layui-btn  layui-btn-normal icon-btn" style="margin-right: 10px;">'+
                            '<i class="layui-icon">&#xe681;</i>上传文件'+
                        '</button>'+
                        '<button id="temExport" class="layui-btn icon-btn" >'+
                            '<i class="layui-icon">&#xe656;</i>下载模版'+
                        '</button>'+
                    '<div>'+
                    '<span style="color: red;padding-top: 20px;display: inline-block;">导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！</span>'+
                    '</div>'+
                    '</div>';
    /** 对外提供的方法 */
    var exp= {
        /**
         * 弹出框
         * @param options 导出地址
         */
        exportExcel:function(options){
                layer.confirm('确定要导出数据吗？', {
                    skin: 'layui-layer-admin'
                }, function (i) {
                    layer.close(i);
                    layer.load(2);
                    location.href=config.base_server+options+"?access_token="+config.getToken();
                    layer.closeAll('loading');
                });
        },
        /**
         * 导入框
         * @param options 导入地址
         */
        importExcel:function(temUrl,importUrl,tableName){
            var layopen=layer.open({
                type: 1,
                area: '400px',
                offset: '65px',
                title:  '导入EXCEL',
                content:importHtml,
                success: function (layero, index) {
                    //下载模版
                    $('#temExport').on('click',function(){
                        layer.load(2);
                        location.href=config.base_server + temUrl+"?access_token="+config.getToken();
                        layer.closeAll('loading');
                    });

                    //导入数据
                    upload.render({
                        elem: '#file-btn-import',
                        accept: 'file',
                        url: config.base_server +importUrl,
                        auto: !0,
                        bindAction: "",
                        field: "file",
                        exts: 'xls|xlsx', //只允许上传压缩文件
                        acceptMime: "",
                        method: "post",
                        data: {access_token:config.getToken()},
                        drag: !0,
                        size: 10240,
                        number: 0,
                        multiple: !0,
                        done: function(res, index, upload){
                            layer.closeAll('loading');
                            layer.alert(res.msg);
                            table.reload(tableName);
                            layer.close(layopen);
                        }
                    });
                }
            });
        }
    };

    exports('excelUtil', exp);
});
