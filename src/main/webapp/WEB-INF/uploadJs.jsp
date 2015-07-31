<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%-- <script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>--%>
<link href="${ ctx}/scripts/uploadify/uploadify.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="${ ctx}/scripts/uploadify/swfobject.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/uploadify/jquery.uploadify.v2.1.4.min.js"></script>
<script type="text/javascript">
  /**
	 * 
	 * @param fileId  文件元素标识

	 * @param resultId  上传文件结果保存元素id（<input type='hidden' id="uploadFiles" name="uploadFiles"）;
	 *          
	 * @param multi  是否支持多文件上传
	 *          
	 * @param comFlag 是否生成缩略图
	 *          
	 * @param s_width 生成缩略图宽度
	 *           
	 * @param s_height 生成缩略图高度
	 *                    
	 * @param onComplete 上传完成后处理函数        
	 * 
	 */

	function uploadFile(fileId,resultId,multi,comFlag,s_width,s_height,onComplete){
		if (!onComplete){
			onComplete = defaultUploadOnComplete;
		}
 		$(document).ready(function() {
			$('#'+fileId+'').uploadify({
				'uploader'     : '${ ctx}/scripts/uploadify/uploadify.swf?var=' + (new Date()).getTime(),
			    'script'       :  '${ctx}/servlet/upload;jsessionid=<%=session.getId()%>',
				'cancelImg'    : '${ ctx}/scripts/uploadify/cancel.png',
				'auto'         : true ,
				'multi'        : multi,
 				'fileExt'      :'*.jpg;*.gif;*.png;',
				'fileDesc'	   : '请选择jpg,jpeg,gif,png格式', 
				'sizeLimit'	   : 1024 * 500,
				'scriptData'   : {'comFlag':comFlag,'smallWidth':s_width,'smallHeight':s_height},
				'onAllComplete':function(event,data){	} ,
				'onComplete':function(event, ID, fileObj, response, data){
				   var result = eval('(' + response + ')');
				   onComplete(event,result,resultId,multi);
				}
			});
		}); 
		
	}
	 
	function uploadFileZip(fileId,resultId,multi,comFlag,s_width,s_height,onComplete){
			if (!onComplete){
				onComplete = defaultUploadOnComplete;
			}
	 		$(document).ready(function() {
				$('#'+fileId+'').uploadify({
					'uploader'     : '${ ctx}/scripts/uploadify/uploadify.swf?var=' + (new Date()).getTime(),
				    'script'       :  '${ctx}/servlet/upload;jsessionid=<%=session.getId()%>',
					'cancelImg'    : '${ ctx}/scripts/uploadify/cancel.png',
					'auto'         : true ,
					'multi'        : multi,
					'fileExt'      :'*.zip;*.rar',
					'fileDesc'	   : '请选择zip格式',
					'sizeLimit'	   : 1024*1024*30,
					'scriptData'   : {'comFlag':comFlag,'smallWidth':s_width,'smallHeight':s_height},
					'onAllComplete':function(event,data){	} ,
					'onComplete':function(event, ID, fileObj, response, data){
					   var result = eval('(' + response + ')');
					   onComplete(event,result,resultId,multi);
					}
				});
			}); 
			
	}

	function uploadFileDz(fileId,resultId,multi,comFlag,s_width,s_height,onComplete){
		if (!onComplete){
			onComplete = defaultUploadOnComplete;
		}
 		$(document).ready(function() {
			$('#'+fileId+'').uploadify({
				'uploader'     : '${ ctx}/scripts/uploadify/uploadify.swf?var=' + (new Date()).getTime(),
			    'script'       :  '${ctx}/servlet/upload;jsessionid=<%=session.getId()%>',
				'cancelImg'    : '${ ctx}/scripts/uploadify/cancel.png',
				'auto'         : true ,
				'multi'        : multi,
				'sizeLimit'	   : 1024*1024*10,
				'scriptData'   : {'comFlag':comFlag,'smallWidth':s_width,'smallHeight':s_height},
				'onAllComplete':function(event,data){	} ,
				'onComplete':function(event, ID, fileObj, response, data){
				   var result = eval('(' + response + ')');
				   onComplete(event,result,resultId,multi);
				}
			});
		}); 
		
}
	
	function uploadFileExcel(fileId,resultId,multi,comFlag,s_width,s_height,onComplete){
		if (!onComplete){
			onComplete = defaultUploadOnComplete;
		}
 		$(document).ready(function() {
			$('#'+fileId+'').uploadify({
				'uploader'     : '${ ctx}/scripts/uploadify/uploadify.swf?var=' + (new Date()).getTime(),
			    'script'       :  '${ctx}/servlet/upload;jsessionid=<%=session.getId()%>',
				'cancelImg'    : '${ ctx}/scripts/uploadify/cancel.png',
				'auto'         : true ,
				'multi'        : multi,
				'fileExt'      :'*.xls',
				'fileDesc'	   : '请选择Excel文件格式',
				'sizeLimit'	   : 1024*1024*20,
				'scriptData'   : {'comFlag':comFlag,'smallWidth':s_width,'smallHeight':s_height},
				'onAllComplete':function(event,data){	} ,
				'onComplete':function(event, ID, fileObj, response, data){
				   var result = eval('(' + response + ')');
				   onComplete(event,result,resultId,multi);
				}
			});
		}); 
		
	}
	 function uploadFileApp(fileId,resultId,multi,comFlag,s_width,s_height,onComplete){
			if (!onComplete){
				onComplete = defaultUploadOnComplete;
			}
	 		$(document).ready(function() {
				$('#'+fileId+'').uploadify({
					'uploader'     : '${ ctx}/scripts/uploadify/uploadify.swf?var=' + (new Date()).getTime(),
				    'script'       :  '${ctx}/servlet/upload;jsessionid=<%=session.getId()%>',
					'cancelImg'    : '${ ctx}/scripts/uploadify/cancel.png',
					'auto'         : true ,
					'multi'        : multi,
					'fileExt'      :'*.apk;*.ipa',
					'fileDesc'	   : '请选择apk或ipa格式',
					'sizeLimit'	   : 1024*1024*10,
					'scriptData'   : {'comFlag':comFlag,'smallWidth':s_width,'smallHeight':s_height},
					'onAllComplete':function(event,data){	} ,
					'onComplete':function(event, ID, fileObj, response, data){
					   var result = eval('(' + response + ')');
					   onComplete(event,result,resultId,multi);
					}
				});
			}); 
			
	}
	function defaultUploadOnComplete(event,data,objId,multi){
    if (data.error==1){
       alert(data.datas);
    }else if (data.error == 0 ){    
      if (multi){
	      var oldVal = $("#"+objId+"").val();
	      if (oldVal != null && oldVal != "" ){
	        oldVal = oldVal+",";
	      }
      	$("#"+objId+"").val(oldVal+data.datas);
      }else{
      	$("#"+objId+"").val(data.datas);
      }
    }
  }
	
	
</script>
