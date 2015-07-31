	
/*
 **************** 以下为接口函数,供外部调用 *******************
 */

/* input输入框响应默认值
<input type="text" name="" id="" value="提示信息" onFocus="OnEnter(this)" onBlur="OnExit(this)"/>
*/
function OnEnter( field ) { if( field.value == field.defaultValue ) { field.value = ""; } }
function OnExit( field ) { if( field.value == "" ) { field.value = field.defaultValue; } }

/* 
 * 功能: 通过url从服务器获取数据,并显示到相应区域
 * 说明: 使用ajax方式请求
 * 参数:
 *     url: 获取数据的url.
 *     area_id: 数据显示区域.
 *              如显示区域定义为<div id="server_data"></div>,则area_id=server_data
 *     param: url请求的参数,js类.格式如{order_id:'',user_id:''}
 *     callBackFun: 函数名,数据导入后回调的方法
 */
function ajax_data(url, area_id, param, callBackFun){
  area_id = '#' + area_id;
  $(area_id).load(url,param,function(responseText){
  	if (responseText.indexOf("错误提示") != -1){
  		$(area_id).empty();
  		parseErrorPage(responseText);
  	}else{
  		if (callBackFun instanceof Function){
  			callBackFun.call(this,responseText);
  		}
  	}
  });
}
/*
 * 功能:ajax post请求,提交内容或执行处理
 * url:需要请求的后台地址
 * param;处理的相关内容或参数
 * callBackFun:回调的函数
 * 
 * */
function ajax_post(url,param,callBackFun){
  $.post(url,param,function(responseText){
  	if (responseText.indexOf("错误提示") != -1){
  		parseErrorPage(responseText);
  	}else{
  		if (callBackFun instanceof Function){
  			callBackFun.call(this,responseText);
  		}
  	}
  });
}

/**
 * 功能:form ajax提交 
 * @param {} formId 需要提交的form Id
 * @param {} callBackFun 回调函数
 * @param {} options 执行可选项,如:target:#testDiv,回显div
 */
function ajax_submit(formId,callBackFun,options){
	var formId = "#" + formId;
	if (!options || !options instanceof Object){
		options = {};
	}
	options.success = function(responseText){
  	if (responseText.indexOf("错误提示") != -1){
  		parseErrorPage(responseText);
  	}else{
  		if (callBackFun instanceof Function){
  			callBackFun.call(this,responseText);
  		}
  	}
  },
	$(formId).ajaxSubmit(options);
}

function parseErrorPage(pageInfo){
	var eCode,eMsg,eLocal,eActive;
//	var reg=/(.+<div id="eCode">)(.+)(<[/]div>.+)/;
//	if(reg.test(pageInfo)){
//	 	eCode = RegExp.$2;
//	}
	var reg=/(.+<q id="eMsg">)(.+)(<[/]q>.+)/;
	if(reg.test(pageInfo)){
	 	eMsg = RegExp.$2;
	}
//	reg=/(.+<div id="eLocal">)(.+)(<[/]div>.+)/;
//	if(reg.test(pageInfo)){
//	 	eLocal = RegExp.$2;
//	}
//	reg=/(.+<div id="eActive">)(.+)(<[/]div>.+)/;
//	if(reg.test(pageInfo)){
//	 	eActive = RegExp.$2;
//	}
	ajax_popup_hint('{height:400, width:500}', combHintInfo(eMsg));
	//alert(eMsg);
	//alert(eCode+"\r\n"+eMsg+"\r\n"+eLocal+"\r\n"+eActive);
}
/* 
 * 功能: 通过url从服务器获取数据,通过弹出窗口展示
 * 说明: 使用ajax方式请求
 * 参数:
 *     url: 获取数据的url.
 *     postion:弹出窗口的大小参数，务必需要准确填入，否则弹出窗口显示位置不能居中，例如：{height:400, width:500}
 *     param:url请求的参数,js类.格式如{order_id:'',user_id:''}
 *     callBackFun:函数名,弹出框弹出后调用的方法
 */
function ajax_data_popup(url, postion, param, callBackFun){
  
  initPostion(postion);

  //在本页面上添加弹出窗口容器div等
  html_container_new();

  //取服务器数据,约定服务器数据放在: <div id="server_data"></div>
  ajax_data(url,'win_popup_show', param);

  //显示遮罩层  									 
  show_layer_mask("win_popup_overlay");
  //显示弹出窗口层
  show_pop_center("win_popup_show");

	if (callBackFun instanceof Function){
  	callBackFun.call();
  }
}
function ajax_popup_hint( postion, content){
  initPostion(postion);

  //在本页面上添加弹出窗口容器div等
  html_container_new();

  //取服务器数据,约定服务器数据放在: <div id="server_data"></div>
  $('#win_popup_show').html(content);

  //显示遮罩层  									 
  show_layer_mask("win_popup_overlay");
  //显示弹出窗口层
  show_pop_center("win_popup_show");

}

function combHintInfo(content){
	var hintInfo = new Array();
	hintInfo.push('<div id="error"> ');
	hintInfo.push('<div class="error-info"> ');
	hintInfo.push('<div class="error-fail">&nbsp;</div> ');
	hintInfo.push('<ul class="error-text"> ');
	hintInfo.push('<li>');
	hintInfo.push(content);
	hintInfo.push('</li> ');
	hintInfo.push(' </ul> ');
	hintInfo.push('</div> ');
	hintInfo.push('<div class="error-back"><input name="" type="button" value="关闭" class="error-button" onclick="win_popup_close();"/></div>');
	hintInfo.push('</div> ');
	
	hintInfo.push('</div>');
	
	return hintInfo.join('');
}

/* 
 * 功能: 关闭弹出窗口
 * 说明: 不再使用div关闭,直接清空容器内html代码
 * 参数callBackFun 函数方法名,关闭弹出框后调用的方法
 */
function win_popup_close(callBackFun){
  var win_container = document.getElementById("win_container");
  if (win_container == null || typeof win_container == 'undefined'){
  }else{
    $('#win_container').empty();
    if (callBackFun instanceof Function){
    	callBackFun.call();
    }
  }
  
  pop_waiting_close();
}

/*
 * 功能: 弹出等待窗口
 * 参数info，等待的提示信息，不要太长
 */
function pop_waiting_info(info){

  //增加弹出外框
  var pop_waiting_frame = document.getElementById("pop_waiting_frame");
  if (pop_waiting_frame == null || typeof pop_waiting_frame == 'undefined'){ 
    $('body').append('<div id="pop_waiting_frame"></div>');
  }
  $('#pop_waiting_frame').empty();
  
  //增加弹出内框
  var html=new Array();
  html.push('  <div id="pop_waiting_indicator"></div>');
  $('#pop_waiting_frame').append(html.join(''));
  
  //设置外框样式
  pop_waiting_frame = document.getElementById("pop_waiting_frame");
  pop_waiting_frame.style.position = "absolute";   
  pop_waiting_frame.style.left = $(document).scrollLeft() + document.documentElement.clientWidth/2 - 250/2 + "px";   
  pop_waiting_frame.style.top = $(document).scrollTop() + document.documentElement.clientHeight/2 - 100/2 + "px";   
  pop_waiting_frame.style.border = "3px solid #B2D0F7";   
  pop_waiting_frame.style.background = "white";   
  pop_waiting_frame.style.padding = "10px";   
  pop_waiting_frame.style.font = "bold 14px verdana,tahoma,helvetica";    
  pop_waiting_frame.style.color = "#003366";   
  pop_waiting_frame.style.width = "250px";   
  pop_waiting_frame.style.zIndex = "99998";
  
  //设置内框样式
  var pop_waiting_indicator = document.getElementById("pop_waiting_indicator");   
  pop_waiting_indicator.style.height = "18px";   
  pop_waiting_frame.style.zIndex = "99999";
  
  //滚动居中
  function setWaitCenter()
  { 
    pop_waiting_frame.style.left = $(document).scrollLeft() + document.documentElement.clientWidth/2 - 250/2 + "px";   
    pop_waiting_frame.style.top = $(document).scrollTop() + document.documentElement.clientHeight/2 - 100/2 + "px";       
  }
  if(document.all)
  {
    window.attachEvent("onscroll",setWaitCenter);
  }
  else
  {
    window.addEventListener('scroll',setWaitCenter,false);
  }
  
  //显示
  pop_waiting_frame.style.display = "block";
  pop_waiting_indicator.style.display = "block";
  
  //等待动态效果
  pop_waiting_show_info(info, '0');
}

/* 
 * 功能: 关闭弹出等待窗口
 */
function pop_waiting_close(){
  var pop_waiting_frame = document.getElementById("pop_waiting_frame");
  if (pop_waiting_frame == null || typeof pop_waiting_frame == 'undefined'){ 
  }else{
    pop_waiting_frame.style.display = "none";
    $('#pop_waiting_frame').empty();
  }
}



/*
 * 获取鼠标坐标
 * 参数ev:事件event
 */
function mousePosition(ev){
	ev = ev || window.event;
	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + $(document).scrollLeft(), 
		y:ev.clientY + $(document).scrollTop() 
	}; 
}

/*
 * 鼠标移动到某对象上弹出提示信息
 * 参数: ev为事件event
 *      url和innerHtml二选一;
 *        url为js对象,参数url:请求url地址,param请求参数为js类,例url={url:"", param:{a:"a",b:"b"}}
 *        innerHtml为字符串
 */
function mouseMoveShowDiv(ev,url,innerHtml){
	var div = document.createElement("DIV");
	div.id = "div_mouse_move_show";
	document.body.appendChild(div);
	var mousePos = mousePosition(ev);
	var div_show = document.getElementById("div_mouse_move_show");
	div_show.style.position = "absolute";
	div_show.style.zIndex = 10;
	div_show.style.top = (mousePos.y+20) + "px";
	div_show.style.left = (mousePos.x) + "px";
	div_show.style.display='block';
	if(url){
		//$(div_mouse_move_show).load(url.url, url.param);
		ajax_data(url.url, "div_mouse_move_show", url.param);
	}else if(innerHtml){
		div_show.innerHTML = innerHtml;
	}
}
/*鼠标移出对象后调用关闭弹出框*/
function mouseOutHiddenDiv(){
	var div_showDailyPrice = document.getElementById("div_mouse_move_show");
	if(div_showDailyPrice){
		div_showDailyPrice.innerHTML = "";
		div_showDailyPrice.style.display='none'; 
	}
}

/*
 **************** 以下为内部函数 *******************
 */

var width = 800;//弹出层的宽
var height = 500;//弹出层高
var iTop = 20;//谈出层距离上面位置
var left = 240;//谈出层距离左边位置
var _scrollWidth;//覆盖层宽
var _scrollHeight;//覆盖层高

/*
 * 功能: 设置弹出窗口位置
 */
function initPostion(postion){
  _scrollWidth = Math.max(document.body.scrollWidth,document.documentElement.scrollWidth);
  _scrollHeight = Math.max(document.body.scrollHeight,document.documentElement.scrollHeight);
  
  if(postion){
  	if(typeof postion != 'object'){
	    postion = eval('('+postion+')');
  	}
    if(postion.width){
      width = Number(postion.width);
    }
    if(postion.height){
      height = Number(postion.height);
    }
  }
  
  iTop = $(document).scrollTop() + document.documentElement.clientHeight/2 - height/2;
  left = $(document).scrollLeft() + document.documentElement.clientWidth/2 - width/2;
  if(iTop <= 0) iTop = 20;
  if(left <= 0) left = 20;
}

/*
 * 功能: 显示遮罩层
 * 说明: 
 */
function show_layer_mask(element){
  var aDiv = document.getElementById(element);
  aDiv.style.position = "absolute";
  aDiv.style.zIndex = "997";
  aDiv.style.width = _scrollWidth + "px";
  aDiv.style.height = _scrollHeight + "px";
  aDiv.style.top = "0px";
  aDiv.style.left = "0px";
  aDiv.style.background = "#000000";
  aDiv.style.filter = "alpha(opacity=50)";
  aDiv.style.opacity = "0.50";
  aDiv.style.display = "block";
}

/*
 * 功能: 显示弹出窗口层
 * 说明: 位置居中 
 */
function show_pop_center(element){
  var sDiv = document.getElementById(element);
  sDiv.style.position = "absolute";
  sDiv.style.zIndex = "9999";
  sDiv.style.top = iTop + "px";
  sDiv.style.left = left + "px";
  sDiv.style.width = width + "px";
  sDiv.style.height = height + "px";
  sDiv.style.display = "block";  

  //弹出层滚动居中
  function setCenter()
  { 
    initPostion();
    sDiv.style.top = iTop + "px";
    sDiv.style.left = left + "px";
  }
  if(document.all)
  {
    window.attachEvent("onscroll",setCenter);
  }
  else
  {
    window.addEventListener('scroll',setCenter,false);
  }
}

/* 
 * 功能: 生成div:win_container及其他弹出窗口用到的div
 * 说明: 
 *      1.div: win_popup_overlay 隔离层,需要有css控制
 *      2.div: win_popup_show 数据显示层,需要有css控制
 *      3.div: server_data 服务器数据块层,不需要有css控制,由数据块自己的css决定
 */
function html_container_new(){

  //在本页面上添加弹出窗口容器,如<div id="win_container"></div>
  var win_container = document.getElementById("win_container");
  if (win_container == null || typeof win_container == 'undefined'){ 
    $('body').append('<div id="win_container"></div>');
  }

  //在容器内添加弹出元素(使用数组速度更快!)
  $('#win_container').empty();
  var html=new Array();
  html.push('<div id="win_popup_overlay"><iframe src="" style="position:absolute;visibility:inherit; top:0px; left:0px; width:'+_scrollWidth+'px; height:'+_scrollHeight+'px; z-index:-1;opacity:.0;filter: alpha( opacity=0 ); -moz-opacity: 0;"></iframe></div>');
  //html.push('  <div id="win_popup_close">');//此处添加关闭事件,可根据需要修改
  //html.push('      <h3><a href="#" onclick="win_popup_close()">X</a></h3>');
  //html.push('  </div>');
  html.push('  <div id="win_popup_show"></div>');//此处添加数据
  $('#win_container').append(html.join(''));
}

/*
 * 功能: 显示等待信息
 */
function pop_waiting_show_info(info, flag){
  var msg = document.getElementById("pop_waiting_indicator");
  if (msg == null || typeof msg == 'undefined'){ 
    return;
  }
  if(flag==0)
  {
    flag=1;
    msg.innerHTML = "&nbsp;&nbsp;" + info+" .";
  }
  else if(flag==1)
  {
    flag=2;
    msg.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;" + info+" . .";    
  }
  else
  {
    flag=0;
    msg.innerHTML ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + info+" . . .";    
  }
  
  window.setTimeout(
    function(){pop_waiting_show_info(info, flag);}, 
    500
  );  
}


