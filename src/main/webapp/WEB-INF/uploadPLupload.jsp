<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%-- <script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>--%>
<link href="${ ctx}/scripts/uploadify/uploadify.css" type="text/css" rel="stylesheet" />


<script src="${ctx}/scripts/pluploader/js/plupload.full.min.js"></script>
<script src="${ctx}/scripts/pluploader/js/i18n/zh_CN.js"></script>

<script src="${ctx}/scripts/fancyBox/lib/jquery.mousewheel-3.0.6.pack.js"></script>

<link href="${ ctx}/scripts/fancyBox/source/jquery.fancybox.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/scripts/fancyBox/source/jquery.fancybox.pack.js"></script>

<link href="${ ctx}/scripts/fancyBox/source/helpers/jquery.fancybox-buttons.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/scripts/fancyBox/source/helpers/jquery.fancybox-buttons.js"></script>


<script type="text/javascript">
 	if(typeof console=="undefined"){
 		console={};
 		console.log=function(msg){
 			
 		};
 	}
	function createImgContainer(cId) {
		var html = '<div class="tupian">\
			<div class="close_btn"></div>\
			<div id="'+cId+'" class="tupian_box">\
			</div>\
			<div class="process">\
				<div class="process_inner" id="pi-'+cId+'" ></div>\
			</div>\
			<div class="notUpload">未上传</div>\
			</div>';
		return html;
	}
	function createErrorInfo(info){
		var html='<div class="picListError">'+info+'</div>';
		return html;
	}
	function Process(innerId) {
		this.innerId = innerId;
		this.inner = $(innerId);
	}
	Process.prototype.start = function() {
		var inner = this.inner;
		inner.css("width", "100%");
		function h() {
			var o = inner;
			var x = o.css("backgroundPositionX")
					|| o.css("background-position").split(" ")[0] || 0;
			x = parseFloat(x);
			x += 1;
			if (x > 30)
				x = 0;
			o.css("background-position", x + "px" + " 0");
		}
		if (this.interval) {
			clearInterval(this.interval);
		}
		this.interval = setInterval(h, 20);
	};
	Process.prototype.complete = function() {
		clearInterval(this.interval);
	};
	Process.prototype.setComplete = function(val) {
		this.inner.css("width", val);
	};
	Process.prototype.hide=function(){
		this.inner.parent().fadeOut(300);
	}
	$(function() {
		var uploader = new plupload.Uploader({
			runtimes : "flash,html5,silverlight,html4",
			browse_button : 'browse', // 绑定到的上传按钮ID
			url : '${ctx}/uploadController/upload;jsessionid=<%=session.getId()%>',
			filters : {
				mime_types : [ {
					title : "压缩文件",
					extensions : "zip,rar"
				} ],
				max_file_size:"30mb"
			},
			// Flash settings
			flash_swf_url : '${ctx}/scripts/pluploader/js/Moxie.swf',
			file_data_name:"file",
			// Silverlight settings
			silverlight_xap_url : '${ctx}/scripts/pluploader/js/Moxie.xap',
			prevent_duplicates: true
		});
		uploader.init();
		uploader.bind("Init", function() {
		});
		//添加图片事件
		uploader.bind("filesAdded", function(up, files) {
			$.each(files, function() {
				var html = createImgContainer(this.id);
				$("#picList .clear_fix").before(html);
				console.log(this);
				if(this.type.indexOf("zip")>0){
					$("#"+this.id).append("<img src='${ctx}/images/z_file_zip.png' title='点击下载'>");
				}else if(this.type==""&&this.name.substring(this.name.length-4)==".rar"){
					$("#"+this.id).append("<img src='${ctx}/images/z_file_rar.png' title='点击下载'>");
				}else{
					var img = new mOxie.Image();
					var file = this;
					img.onload = function() {
						this.embed(document.getElementById(file.id));
					}
					img.load(this.getSource());	
				}
			});
		});
		var processes={};
		uploader.bind("UploadFile",function(up,file){
			$("#pi-"+file.id).parent().fadeIn(300);
			var status=$("#"+file.id).nextAll(".notUpload");
			status.html("上传中");
			var p=new Process("#pi-"+file.id);
			processes[file.id]=p;
			p.start();
			//$("#"+file.id).prev().fadeOut(300);//隐藏.close_btn按钮
		});
		//上传进度事件
		uploader.bind("UploadProgress",function(up,file){
			console.log("UploadProgress",arguments);
			processes[file.id].setComplete(file.percent+"%");
			
		});
		//上传完成事件
		uploader.bind("FileUploaded",function(up,file,info){
			var status=$("#"+file.id).nextAll(".notUpload");
			status.html("已上传");
			status.css("backgroundColor","#ed9c28");
			processes[file.id].complete();
			processes[file.id].hide();
			console.log("FileUploaded",arguments);
			var data=info.response;
			data=eval("("+data+")");
			console.log("data",data.datas);
			var objId="attachment";
			if (data.error==1){
			       alert(data.datas);
			    }else if (data.error == 0 ){    
			      if (true){
			    	  $("#"+file.id).attr("data-filename",data.datas);
				      var oldVal = $("#"+objId+"").val();
				      if (oldVal != null && oldVal != "" ){
				        oldVal = oldVal+",";
				      }
			      	$("#"+objId+"").val(oldVal+data.datas);
			      	//添加文件名称显示
			      	$("#"+file.id).parents(".tupian").append('<div class="filename">'+data.datas+'</div>');
			      	//上传成功，判断是否是最后一个文件
			      	console.log("是否是最后一个文件",hasFile(up)?"否":"是");
			      	if(!hasFile(up)){
			      		addButtonClick();
			      	}
			      }else{
			      	$("#"+objId+"").val(data.datas);
			      }
			 }
		});
		//绑定删除图片按钮事件
		$(".picList").on("click",".close_btn",function(){
				var _this=$(this);
				var div=_this.next();//close_btn后面的div，这个DIV可能包含文件名称
				var fileId=div.attr("id");
				var fileName=div.attr("data-filename");
				var tupian=_this.parent();
				tupian.fadeOut(400,function(){
					_this.parent().remove();
					if(fileId){
						uploader.removeFile(fileId);	
					}
					if(fileName){
						var attachment=","+$("#attachment").val();
						$("#attachment").val(attachment.replace(","+fileName,"").substring(1));
					}
				});
		});
		//错误处理
		uploader.bind("Error",function(up,obj){
			var info="文件:&nbsp;&nbsp;"+obj.file.name+obj.message+"请重新选择！";
			var html=createErrorInfo(info);
			$("#upload_error").show().html(html);
			$("#upload_error").fadeOut(5000);
		});
		//判断是否还有 没有上传的文件
		function hasFile(upl){
			for(var i in upl.files){
				if(upl.files[i].status==plupload.QUEUED){
					return true;
				}
			}
			return false;
		}
		$("#addButton").on("click",function(){
				var hf=hasFile(uploader);
				if(!hf){
					addButtonClick();
				}else{
					uploader.start();	
				}
		});
		/* function delayClearErrorInfo(){
			var next=$("#upload_error");
			next.fadeOut(800);
		} */
		//添加图片查看
		//$('.fancybox').fancybox();
	});
</script>
<style>
.tupian {
	width: 132px;
	height: 80px;
	float: left;
	position: relative;
	padding: 6px 0;
	border: #d3d3d3 1px solid;
	margin-right: 12px;
	margin-bottom: 35px;
	text-align: center;
	margin-top: 10px;
}
.tupian img{
	height: 80px!important;
	width:inherit!important;
	
}
.tupian_box{
	overflow: hidden;
}
.notUpload{
	position: absolute;
	text-align: center;
	background-color: #000;
	opacity:0.4;
	filter:alpha(opacity=40);
	color:#fff;
	height: 15px;
	bottom: 0px;
	left: 0px;
	height: 20px;
	width: 100%;
	line-height: 20px;
}
.process {
	display:none;
	position: absolute;
	left: 0px;
	bottom: -10px;
	background: url(${ctx}/images/process_bg.png) repeat-x;
	height: 10px;
	width: 132px;
}

.process_inner {
	height: 10px;
	background: url(${ctx}/images/process.jpg) repeat-x;
	width: 100%;
}
.filename{
	width: 126px;
	border-width: 0 1px 1px 1px;
	border-style: solid;
	border-color: #d3d3d3;
	word-wrap: break-word;
	margin-top: 6px;
	margin-left: -1px;
	padding:3px;
}
.clear_fix {
	clear: both;
}

.close_btn {
	background: url(${ctx}/images/image_upload.png) no-repeat;
	background-position: -100px -182px;
	position: absolute;
	width: 14px;
	height: 14px;
	overflow: hidden;
	right: -6px;
	top: -6px;
	cursor: pointer;
}

.startUpload {
	border: 1px solid red;
	display: inline;
	color: #fff;
	background-color: #428bca;
	border-color: #357ebd;
	padding: 6px 10px 6px 8px;
	cursor: pointer;
	-webkit-user-select: none;
	user-select: none;
	border-radius: 4px;
	margin-left: 12px;
}

.startUpload:hover {
	border-color: #3276b1;
	background-color: #3276b1;
}

.startUpload .flag {
	font-weight: bold;
	display: inline;
	font-size: 16px;
}

.close_btn:HOVER {
	background-position: -100px -200px;
}
.picListError{
	margin-top: 12px;
	background-color: #d2322d;
	padding: 6px 10px 6px 8px;
	color:#fff;
}
	.selecPic {
	border: 1px solid red;
	display: inline;
	color: #fff;
	background-color: #5cb85c;
	border-color: #4cae4c;
	padding: 6px 10px 6px 8px;
	cursor: pointer;
	-webkit-user-select: none;
	user-select: none;
	border-radius: 4px;
}

.selecPic .flag {
	font-weight: bold;
	display: inline;
	font-size: 16px;
}
</style>
