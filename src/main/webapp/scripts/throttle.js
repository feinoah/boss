// 防止表单重复提交
$(function() {
	/*var submitBtn = $('input[type="submit"]'), count = 3;
	submitBtn.attr('value', '  ' + (count) + '  ');
	submitBtn.attr('disabled', true);
	var timer = setInterval(function() {
		submitBtn.attr('value', '  ' + (--count) + '  ');

		if (count === 0) {
			submitBtn.attr( {
				'disabled' : false,
				'value' : '查询'
			});
			clearInterval(timer);
		}
	}, 1000);*/

	$('input[type="submit"]').on('click', function(event) {
		event.preventDefault();
		
		$(this).attr({'disabled' : true,'value' : '加载中...'})
			   .removeClass('blue')
			   .addClass('gray');
		$('form:first').submit();
	});
});

function getRootPath_web() {
	var curWwwPath = window.document.location.href;
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	var localhostPaht = curWwwPath.substring(0, pos);
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return (localhostPaht + projectName);
}


function exportXls(actionUrl, pageNum, needPageLimit){

	var action= $("form:first").attr("action");

	//根据当前页查询的总页数来判断是否导出，必须先进行查询
	if(pageNum <= 0 && needPageLimit){
		$.dialog.alert("<pre>没有需要导出的数据！</pre>");
	} else if(pageNum > 100 && needPageLimit){
		$.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
	} else {
		$.get(getRootPath_web() + '/getExportTime', function(msg){
			if(msg === '1'){
				$("form:first").attr("action",actionUrl).submit();
				$("form:first").attr("action",action);
			}else if(msg === '0'){
				$.dialog.alert("<pre>非导出数据时间，请于早上8点之前，晚上18点以后导出！</pre>");
			}else{
				$.dialog.alert("<pre>系统异常，请重试！</pre>");
			}
		});
	}
}