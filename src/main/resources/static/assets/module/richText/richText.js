/**
* @author:  张瑞
* @description: 富文本的封装方法
* @param:
* @return:
* @throws:
*/
layui.define(['layer', 'form', 'formSelects', 'admin', 'config', 'layedit'], function (exports) {
	var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form;
    var formSelects = layui.formSelects;
    var admin = layui.admin;
    var config = layui.config;
    var layedit = layui.layedit;
	var richText={
		/**
		* @author:  张瑞
		* @description: 富文本的配置信息
		* @param:
		* @return:
		* @throws:
		*/
		render:function(){
			layedit.set({
	           uploadImage: {
	                url: config.base_server + '/file/uploadFile',//图片上传路径
	                data: {access_token:config.getToken()},//传参
	                accept: 'image',
	                acceptMime: 'image/*',
	                exts: 'jpg|png|gif|bmp|jpeg',
	                size: '10240',
	            }
	            , tool: [
	            	'html'
	                ,'strong' //加粗
				  	,'italic' //斜体
				 	,'underline' //下划线
				  	,'del' //删除线
				  	,'|' //分割线
				  	,'left' //左对齐
				  	,'center' //居中对齐
				  	,'right' //右对齐
				  	,'link' //超链接
				  	,'unlink' //清除链接
				  	,'image' //插入图片
				  	,'help' //帮助
	            ]
	        });
//	        var ieditor = layedit.build(buildID);
//	        $(documentID).click(function () {
//	        	var esitCont = layedit.getContent(ieditor);
//	        	$('#'+buildID).html(esitCont);
//	        	console.log($('#'+buildID).html())
//	        });
		}
	}
    exports('richText', richText);
});
