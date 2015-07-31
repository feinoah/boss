package com.eeepay.boss.controller;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.ClientMessageService;
import com.eeepay.boss.utils.pub.Sms;
@Controller
@RequestMapping(value = "/clientMessage")
public class ClientMessageController extends BaseController{

	@Resource
	private ClientMessageService clientService;
	
	//查询手机客户端信息
	@RequestMapping(value = "/clientMessageQuery")
	public String clientMessageQuery(final ModelMap model,@RequestParam(value = "p", defaultValue = "1") int cpage,
			HttpServletRequest request,HttpServletResponse response){
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = clientService.getClientMessageList(page);
		model.put("p", cpage);
		model.put("list", list);
		return "/client/clientMessageQuery";
	}
	
	//转入新增手机客户端信息页面
	@RequestMapping(value = "/clientMessageAdd")
	public String clientMessageAdd(final ModelMap model,@RequestParam Map<String, String> param,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		return "/client/clientMessageInput";
	}
	
	
	//新增手机客户端信息
	@RequestMapping(value = "/clientMessageSave")
	public String clientMessageSave(final ModelMap model,HttpServletRequest request,
			HttpServletResponse response,@RequestParam Map<String, String> param,@RequestParam String id,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		if(id==null||id.length()==0){
			try {
				clientService.addMessage(param);
				model.put("flag", "1");
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", param);
			}
		}else{
			try {
				clientService.clientMessageModify(param);   //修改手机客户端信息
				Set set = param.keySet();
				for (Object key : set) {
					if("msg".equals(key)){
						continue;
					}
					boolean boo = false;
					Integer value = Integer.valueOf(param.get(key).toUpperCase());
					if (value==0) {
						boo =false;
						param.put(key.toString(), ""+boo);
					}else if(value==1){
						boo = true;
						param.put(key.toString(), ""+boo);
					}
				}
				model.put("flag", "2");
				model.put("params", param);
			}catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", param);
			}
		}
		
		return "/client/clientMessageInput";
	}
	
	//查询手机客户端信息详情
	@RequestMapping(value = "/messageDetail")
	public String messageDetail(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		Map<String,Object> map = clientService.messageDetail(Long.parseLong(id));
		model.put("params", map);
		return "/client/clientMessageDetail";
	}
	
	//手机客户端信息修改
	@RequestMapping(value = "/messageInput")
	public String messageInput(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		if(id!=null&&id.length()!=0){
			Map<String,Object> blackMap = clientService.getClientMessageById(Long.parseLong(id));
			model.put("params", blackMap);
		}
		return "/client/clientMessageInput";
	}
	
	
	//对手机客户端的开通、禁用操作
	@RequestMapping(value = "/updateClientStatus")
	public String updateClientStatus(final ModelMap model,
	@RequestParam Map<String, String> params,HttpServletRequest request,
	HttpServletResponse response)throws Exception{
		String id=params.get("id");
		System.out.println("id:"+id);
		String valid=params.get("is_valid");
	    clientService.updateMesStatus(id,valid);
		return "redirect:/clientMessage/clientMessageQuery";
	}
	
	
	//删除手机客户端消息
	@RequestMapping(value = "/delMessageQuery")
	public String delMessageQuery(final ModelMap model,
			@RequestParam Map<String, String> params,HttpServletResponse response){
		String id=params.get("id");
		String is_delete=params.get("is_delete");
		System.out.println("id:"+id+"is_delete:"+is_delete);
		clientService.delClientMessage(id,is_delete);
		return "redirect:/clientMessage/clientMessageQuery";
	}
	
	
	
	//转入批量发送短信页面
	@RequestMapping(value = "/toMsgInputAdd")
	public String toMsgInputAdd(final ModelMap model,@RequestParam Map<String, String> param,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		return "/client/smsInput";
	}
	
	
	
	//发送短信
	@RequestMapping(value = "/sendSmsContent")
	public String sendSmsContent(final ModelMap model,HttpServletRequest request,
			HttpServletResponse response,@RequestParam Map<String, String> param,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		String mobileNo = param.get("mobileNo");
		String content = param.get("smsContent");
		Sms.sendMsg(mobileNo, content);
		model.put("flag", "1");
		return "/client/smsInput";
	}
}
