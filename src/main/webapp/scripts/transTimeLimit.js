// 交易查询中设置查询时间7天内限制 
// author 袁鹏

Date.prototype.Format = function(fmt) { //author: meizz 
	var o = {
		"M+" : this.getMonth() + 1, //月份 
		"d+" : this.getDate(), //日 
		"H+" : this.getHours(), //小时 
		"m+" : this.getMinutes(), //分 
		"s+" : this.getSeconds(), //秒 
		"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
		"S" : this.getMilliseconds()
	//毫秒 
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

$(function() {
	var hasPermission = $('#transTimeLimit').attr('data');
	
	// ------------------日期控件处理开始------------------
	$('#createTimeBegin')
			.on(
					'focus',
					function() {
						var config = {
							isShowClear : false,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							readOnly : true,
							maxDate : '#F{$dp.$D(\'createTimeEnd\')}'
						};
						
						if(hasPermission !== '1'){
							config.onpicked = function() {
								var timeBegin = $dp.$('createTimeBegin').value, timeEnd = $dp
										.$('createTimeEnd').value, timeBeginMillis = Date
										.parse(timeBegin.replace(/-/g, '/')), timeEndMillis = Date
										.parse(timeEnd.replace(/-/g, '/')), distance = (timeEndMillis - timeBeginMillis)
										/ (1000 * 60 * 60 * 24);

								if (distance > 7) {
									$dp.$('createTimeEnd').value = new Date(
											timeBeginMillis
													+ (1000 * 60 * 60 * 24 * 7 - 1000))
											.Format('yyyy-MM-dd HH:mm:ss');
								}
							};
						}
						
						WdatePicker(config);
					});

	$('#createTimeEnd')
			.on(
					'focus',
					function() {
						var config = {
							isShowClear : false,
							dateFmt : 'yyyy-MM-dd HH:mm:ss',
							readOnly : true,
							minDate : '#F{$dp.$D(\'createTimeBegin\')}',
							maxDate : '%y-%M-%d 23:59:59'
						};
						
						if(hasPermission !== '1'){
							config.onpicked = function() {
								var timeBegin = $dp.$('createTimeBegin').value, timeEnd = $dp
										.$('createTimeEnd').value, timeBeginMillis = Date
										.parse(timeBegin.replace(/-/g, '/')), timeEndMillis = Date
										.parse(timeEnd.replace(/-/g, '/')), distance = (timeEndMillis - timeBeginMillis)
										/ (1000 * 60 * 60 * 24);

								if (distance > 7) {
									$dp.$('createTimeBegin').value = new Date(
											timeEndMillis
													- (1000 * 60 * 60 * 24 * 7 - 1000))
											.Format('yyyy-MM-dd HH:mm:ss');
								}
							}
						}
						
						WdatePicker(config);
					});
	// ------------------日期控件处理结束------------------
});