1、页面不加入模板渲染，在url加上参数layout=no即可，如http://127.0.0.1:8080/mer/detail?id=3&layout=no
2、在代码中获取登录用户对象，可通过获取session中的user
3、货币格式化：<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params['fee_cap_amount']}" />
4、格式化日期：<fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
5、字符串截取： <u:substring length="8" content="${params['agent_name']}"/>
6、日期文本框 ：<input onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})" type="text" class="input"  style="width:102px" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
7、卡号星号表示：<u:cardcut   content="${params['card_no']}"/>
8、清空表单			$(':input','#merchantQuery').not(':button, :submit, :reset, :hidden') .val('') .removeAttr('checked')   .removeAttr('selected');  
