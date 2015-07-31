package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eeepay.boss.utils.ALiYunOssUtil;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.FileUtils;


/**
 * 文件上传
 * 
 * @author wg
 * @date 2014年3月27日
 */
@Controller
@RequestMapping("/uploadController")
public class UploadContoller extends BaseController{
	// 定义允许上传的文件扩展名
	private static final Set<String> fileTypes = new HashSet<String>();
	static {
		fileTypes.add("gif");
		fileTypes.add("jpg");
		fileTypes.add("jpeg");
		fileTypes.add("png");
		fileTypes.add("bmp");
		fileTypes.add("txt");
		fileTypes.add("xls");
		fileTypes.add("zip");
		fileTypes.add("rar");
	}

	@RequestMapping(value = "upload")
	public void upload(
			@RequestParam(value = "file") MultipartFile mf,HttpServletResponse response) {
		String datas = null;
		int error = 1;
		try {
			// 最大文件大小
			long maxSize = 30 * 1024 * 1024;
			long size = mf.getSize();
			String originalFileName = mf.getOriginalFilename();
			String suffix = originalFileName.substring(originalFileName
					.lastIndexOf(".") + 1);
			if (size > maxSize) {
				datas = "上传文件大小超过 " + (maxSize / 1024 * 1024) + "MB 限制";
			} else if (!fileTypes.contains(suffix)) {
				datas = "文件格式不正确";
			} else {
				error = 0;
				datas = FileUtils.getNewFileName() + "." + suffix;
				InputStream is = mf.getInputStream();
				ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_TEMP_TUCKET, datas,
						is);
			}
		} catch (IOException e) {
			e.printStackTrace();
			error=1;
			datas="数据操作错误";
		}catch (Exception e) {
			e.printStackTrace();
			error=1;
			datas="未知错误";
		}
		JSONObject obj = new JSONObject();
		obj.put("error", error);
		obj.put("datas", datas);
		outJson(obj.toString(), response);
	}
}
