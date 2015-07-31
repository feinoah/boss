package com.eeepay.boss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import net.sf.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.NoticeSendService;
import com.eeepay.boss.utils.FileUtils4AliYunOss;
import com.jspsmart.upload.SmartUpload;


@Controller
@RequestMapping(value = "/agentNoticeSend")
public class NoticeSendController extends BaseController {

	@Resource
	private NoticeSendService noticeService;
	
	
	
	//查询代理商通告信息
	@RequestMapping(value = "/agentNoticeQuery")
	public String clientMessageQuery(final ModelMap model,@RequestParam Map<String, String> params,
			HttpServletRequest request,@RequestParam(value = "p", defaultValue = "1") int cpage) throws Exception {
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = noticeService.getAgentNoticeQuery(params,page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/notice/agentNoticeQuery";
	}
	
	//转入代理商通告信息页面
	@RequestMapping(value = "/agentNoticeAdd")
	public String clientMessageAdd(final ModelMap model,@RequestParam Map<String, String> param,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		return "/notice/agentNoticeInput";
	}
		
	
	//新增通告，上传图片
	@RequestMapping(value = "/saveImg")
	public void saveImg(final ModelMap model,@RequestParam Map<String, String> params,
			HttpServletResponse response,
			HttpServletRequest request,@RequestParam(value = "p", defaultValue = "1") int cpage) throws Exception {

		Map<String, String> okMap = new HashMap<String, String>();

		String fileName = "newsImg"; 
		
		String path = request.getSession().getServletContext().getRealPath("\\uploads\\noticeImage");
	 
		SmartUpload upload = new SmartUpload();
		try {
			//PageContext pageContext=request.getSession().getServletContext().ge
			//PageContext context = JspFactory.getDefaultFactory().getPageContext(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
			PageContext pageContext = JspFactory.getDefaultFactory().getPageContext(request.getSession().getServletContext().getServlet(""), request, response, null, true, 8*1024, true);
			//PageContext pageContext=request.getSession().getServletContext().
			upload.initialize(pageContext);
			upload.setAllowedFilesList("jpg,gif,bmp,png");
			
			upload.upload();
			upload.save(path);
			String sourceName = "a.jpg";
			for (int i = 0; i < upload.getFiles().getCount(); i++) {
				com.jspsmart.upload.File file = upload.getFiles().getFile(i);
				String ext = file.getFileExt();
				fileName = fileName +System.currentTimeMillis()+ "."+ext;
				sourceName = file.getFileName();
				file.saveAs(path + fileName,SmartUpload.SAVE_PHYSICAL);
			}
//			File source = new File(path+sourceName);//删除源上传文件
//			if (source.exists()){
//				source.delete();
//			}

		} catch ( Exception e) {
			e.printStackTrace();
//			this.logPrint("上传文件失败!"+e);
//			this._response.getWriter().print("上传文件失败!");
			return;
		}
		
	 
		okMap.put("error", "0");
		okMap.put("url", "http://www.baidu.com/img/bdlogo.gif");
		okMap.put("message", "sdfsdfsdf");
	 
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		System.out.println("ddddddddd");
		 
	}
	
	
	//保存新增代理商通告信息
	@RequestMapping(value = "/agentNoticeSave")
	public String agentNoticeSave(final ModelMap model,HttpServletRequest request,
			@RequestParam("attachment") Object uploadFile,
			HttpServletResponse response,@RequestParam Map<String, String> param,@RequestParam String id,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		    if(id==null||id.length()==0){
				try {
					/*if (uploadFile instanceof MultipartFile ) {
						
						MultipartFile file = (MultipartFile) uploadFile;
						
						System.out.println(file);
					}*/
					String attachment = param.get("attachment");
					FileUtils4AliYunOss.copyFile(attachment);
					param.put("attachment", attachment);
					noticeService.addAgentNotice(param);
					model.put("flag", "1");
					} catch (Exception e) {
						model.put("flag", "0");
						model.put("errorMessage",e.getMessage());
						model.put("params", param);
					}
		    }
		    return "/notice/agentNoticeInput";
	}
	
	
	//保存修改代理商通告信息
	@RequestMapping(value = "/updateAgentNoticeSave")
	public String updateAgentNoticeSave(final ModelMap model,HttpServletRequest request,@RequestParam("attachment") Object uploadFile,		
			HttpServletResponse response,@RequestParam Map<String, String> params,@RequestParam String id,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
			try {
				String attachment = params.get("attachment");
				FileUtils4AliYunOss.copyFile(attachment);
				params.put("attachment", attachment);
				noticeService.agentNoticeModify(params);   //保存修改代理商通告信息
				model.put("flag", "2");
				model.put("params", params);
			}catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
			return "/notice/agentNoticeCheck";

	}
	
	
	
	//查询代理商通告信息详情
	@RequestMapping(value = "/noticeDetail")
	public String noticeDetail(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		Map<String,Object> map = noticeService.noticeDetail(Long.parseLong(id));
		String attachments = (String)map.get("attachment")+"";
		String[] attachment = attachments.split(",");
		List<String> picList = new ArrayList<String>();
		for (int i = 0; i < attachment.length; i++) {
			String pic = attachment[i];
			picList.add(pic);
		}
		model.put("picList", picList);
		model.put("params", map);
		return "/notice/agentNoticeDetail";
	}
	
	
	//代理商通告信息修改
	@RequestMapping(value = "/agentNoticeInput")
	public String messageInput(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		if(id!=null&&id.length()!=0){
			Map<String,Object> blackMap = noticeService.noticeDetail(Long.parseLong(id));
			String attachments = (String)blackMap.get("attachment")+"";
			String[] attachment = attachments.split(",");
			List<String> picList = new ArrayList<String>();
			for (int i = 0; i < attachment.length; i++) {
				String pic = attachment[i];
				picList.add(pic);
			}
			model.put("picList", picList);
			model.put("params", blackMap);
		}
		return "/notice/agentNoticeCheck";
	}
	

	//对代理商通告信息的开通、禁用操作
	@RequestMapping(value = "/updateNoticeStatus")
	public String updateClientStatus(final ModelMap model,
	@RequestParam Map<String, String> params,HttpServletRequest request,
	HttpServletResponse response)throws Exception{
		String id=params.get("id");
		System.out.println("id:"+id);
		String notice_is_valid=params.get("notice_is_valid");
		noticeService.updateNoticeStatus(id,notice_is_valid);
		return "redirect:/agentNoticeSend/agentNoticeQuery";
	}
	
	
	
	//删除代理商通告信息
	@RequestMapping(value = "/delNotice")
	public void delNotice(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,@RequestParam("id") int id) throws Exception{
		Map<String, String> okMap = new HashMap<String, String>();
		//String id=params.get("id");
		try{
			//noticeService.delNotice(Long.parseLong(id));
			noticeService.delNotice(id);
			okMap.put("msg", "OK");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}
}
