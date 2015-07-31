

/**
 * form提交ajax包装
 * @param formName 需要提交的form名称，必填
 * @param fnCheck 提交时需要进行参数检查的函数名称，可空
 * @param fnSuccess 提交通讯成功后，被回调的函数，可空。该函数有至少有一个参数data，是通讯返回的数据
 * @param fnSuccessAfter 回调函数fnSuccess执行完后，再次回调的函数，可空
 * @param afterParams fnSuccessAfter回调函数的参数，可空
 * @param processStatus 是否包括进度条,可空
 * @return
 **/
function formSubmit(formName, fnCheck, fnSuccess, fnSuccessAfter, afterParams,processStatus) {
  if (formName==null||formName==""){
    formName = 'updateForm';
  }
  formName = '#'+formName;
  if (fnCheck == null){
    fnCheck = nullCheck;
  }
  if (fnSuccess == null){
    fnSuccess = defaultSuccess;
  }
  
  $(formName).ajaxSubmit( {
    beforeSubmit : function(){
    	if (fnCheck()){
    		 if (processStatus){
  					pop_waiting_info("请稍候...");
 				 }
    		 return true;
    	}else{
    		return false;
    	}
    },
    dataType: 'json',
    type: 'POST',
    iframe:false,
    success : function(data){
    	if (processStatus){
    		 pop_waiting_close();
    	}
      var ret = fnSuccess(data);
      if ("OK" == ret && fnSuccessAfter != null){
    	  if(afterParams==null || afterParams==''){
    		  fnSuccessAfter.apply(window);
    	  }else{
    		  fnSuccessAfter.apply(window,afterParams);
    	  }
      }else{
    	  $.dialog.alert("保存失败，请检查数据");
      }
  },
  error : function(data) {
	  $.dialog.alert("保存失败，请检查数据");
  }
    });  
}

/**
 * 私有方法
 * @return
 */
function nullCheck(){
  return true;
}

/**
 * 私有方法
 * @param data
 * @return
 */
function defaultSuccess(data){
	
  var ret = data.msg
  if (ret == "OK") {
	  ret = "OK";
  } else {
    showErrorInfomation(data);
  }
  return ret;
}

/**
 * 提示ajax返回的错误信息。
 * 如果返回的是错误页面，跳转到错误页面；
 * 如果不是错误页面，无回调函数的，提示返回内容；否则调用回调函数
 * @param data 返回内容
 * @param fn 非错误页面时回调的函数。函数形式：fn(data)
 * @return
 */
function showErrorInfomation(data, fn){

	$.dialog.alert("保存失败，请检查数据")

}
