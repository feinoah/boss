/**
 * 版权 (c) 2011 移付宝
 * 保留所有权利。
 */

package com.eeepay.boss.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.utils.FileUtils;
import com.eeepay.boss.utils.LogException;
import com.eeepay.boss.utils.SysConfig;

/**
 * 描述：
 * 
 */

public class UploadServletForKindeditor extends HttpServlet {

	private static final Logger log = LoggerFactory
			.getLogger(UploadServletForKindeditor.class);

	/**
   * 
   */
	private static final long serialVersionUID = -5659995315515736376L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String savePath = this.getServletConfig().getServletContext()
				.getRealPath("");
		String tempurl = SysConfig.value("uploadtemp");
		String fileSavePath = savePath + tempurl;

		String result = "";
		if (fileSavePath == null || "".equals(fileSavePath)) {
			JSONObject obj = new JSONObject();
			obj.put("error", 1);
			obj.put("datas", "上传文件临时文件目录");
			result = obj.toString();
			log.info("上传文件临时文件目录不存在!");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().print(result);
			resp.getWriter().flush();
			return;
		}
		if (fileSavePath.lastIndexOf("/") == -1) {
			fileSavePath = fileSavePath + "/";
		}
		File temp = new File(fileSavePath);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		// 保存图片文件到物理磁盘
		try {
			log.info("start---------------------------------------------------");
			result = FileUtils.saveFilesForKindedit(req, fileSavePath, "", "");
			log.info("end---------------------------------------------------");
			log.info("result---1--="+result);
		} catch (Exception e) {
			LogException.logExceptionAndInfo(e, log, "保存图片文件到物理磁盘 catch");
			JSONObject obj = new JSONObject();
			obj.put("error", 1);
			obj.put("message", "上传文件失败");
			result = obj.toString();
		}
	 
		log.info("result---2--="+result);
		
		JSONObject obj = new JSONObject();
		if(result.contains("successful")){
			LogException.logInfo(log, "if");
			obj.put("error", "0");
			obj.put("message", "成功");
			obj.put("url", req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath()+  SysConfig.value("uploadtemp")+result.substring(11 ));
			result = obj.toString();
		}else{
			LogException.logInfo(log, "else");
			obj.put("error", 1);
			obj.put("message", "上传文件失败");
			result = obj.toString();
		}
		log.info("result---3--="+result);
		try {
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().print(result);
			resp.getWriter().flush();
		} catch (IOException e) {
			LogException.logExceptionAndInfo(e, log, "utf-8  catch");
		}
		LogException.logInfo(log, "end-");
	}
}
