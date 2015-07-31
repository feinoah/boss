package com.eeepay.boss.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	private final static int MAXNUM = 9999;
	private static AtomicInteger num = new AtomicInteger(0);

	/**
	 * 获取文件名,文件名由日期时间(精确到秒)+序列组成,序列有最大值,当达到最大值时,恢复初始值.
	 * 
	 * @return 文件名
	 */
	public static String getNewFileName() {
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		sb.append(calendar.get(Calendar.YEAR));
		sb.append(calendar.get(Calendar.MONTH) + 1);
		sb.append(calendar.get(Calendar.DAY_OF_MONTH));
		sb.append(calendar.get(Calendar.HOUR_OF_DAY));
		sb.append(calendar.get(Calendar.MINUTE));
		sb.append(calendar.get(Calendar.SECOND));
		sb.append(num.addAndGet(1));
		if (num.get() > MAXNUM) {
			num.set(0);
		}

		return sb.toString();
	}

	/**
	 * 保存上传文件
	 * 
	 * @param request
	 *            Http请求
	 * @param modelPath
	 *            文件存放的基础目录,各模块的基础目录应该不一样,如:c:/pic/car
	 * @param filePrefix
	 *            文件名前缀,文件名由商户+自定义标识+序列组成,其中filePrefix = 商户 + 自定义标识
	 * @return
	 */
	public static String saveFiles(HttpServletRequest request,
			String modelPath, String filePrefix, String allowType)
			throws IOException {
		String newFileName = "";

		// 定义允许上传的文件扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp",
				"txt", "xls", "zip", "rar" };
		// 最大文件大小
		// long maxSize = 1000000;
		long maxSize = 30 * 1024 * 1024;
		String error = "";
		// 检查目录
		File uploadDir = new File(modelPath);
		if (!uploadDir.isDirectory()) {
			error = getError("上传目录不存在。");
			return error;
		}
		// 检查目录写权限
		if (!uploadDir.canWrite()) {
			error = getError("上传目录没有写权限。");
			return error;
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			log.info("上传文件失败!", e1);
			error = getError("上传文件失败!");
			return error;
		}
		if (items == null) {
			error = getError("上传文件失败!");
			return error;
		}
		String comFlag = getFormFieldValue(items, "comFlag");
		String height = getFormFieldValue(items, "smallHeight");
		String width = getFormFieldValue(items, "smallWidth");
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			long fileSize = item.getSize();
			if (!item.isFormField()) {
				// 检查文件大小
				if (fileSize > maxSize) {
					log.info("上传文件大小超过限制");
					error = getError("上传文件大小超过限制。");
					return error;
				}
				// 检查扩展名
				String fileExt = fileName.substring(
						fileName.lastIndexOf(".") + 1).toLowerCase();
				if (!Arrays.<String> asList(fileTypes).contains(fileExt)) {
					log.info("上传文件扩展名是不允许的扩展名");
					error = getError("上传文件扩展名是不允许的扩展名。");
					return error;
				}
				newFileName = filePrefix + getNewFileName() + "." + fileExt;
				try {
					File uploadedFile = new File(modelPath, newFileName);
					item.write(uploadedFile);
				} catch (Exception e) {
					log.info("上传文件保存失败", e);
					error = getError("上传文件保存失败。");
					return error;
				}

				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("datas", newFileName);
				error = obj.toString();

				if ("yes".equals(comFlag)) {
					InputStream stream = null;
					try {
						// File files = new File(modelPath, newFileName);
						// stream = new FileInputStream(files);
						// ImageUtils.drawMiniImage(stream, modelPath + "s_" +
						// newFileName,
						// Double.valueOf(height).doubleValue(),
						// Double.valueOf(width)
						// .doubleValue());
					} catch (Exception e) {
						log.info("文件压缩失败!", e);
						error = getError("文件压缩失败!");
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
				}
				return error;
			}

		}
		return error;
	}

	/**
	 * 保存上传文件
	 * 
	 * @param request
	 *            Http请求
	 * @param modelPath
	 *            文件存放的基础目录,各模块的基础目录应该不一样,如:c:/pic/car
	 * @param filePrefix
	 *            文件名前缀,文件名由商户+自定义标识+序列组成,其中filePrefix = 商户 + 自定义标识
	 * @return
	 */
	public static String saveFilesForKindedit(HttpServletRequest request,
			String modelPath, String filePrefix, String allowType)
			throws IOException {
		String newFileName = "";

		// 定义允许上传的文件扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp",
				"txt", "xls", "zip", "rar" };
		// 最大文件大小
		// long maxSize = 1000000;
		long maxSize = 30 * 1024 * 1024;
		String error = "";
		// 检查目录
		File uploadDir = new File(modelPath);
		if (!uploadDir.isDirectory()) {
			error = getError("上传目录不存在。");
			return error;
		}
		// 检查目录写权限
		if (!uploadDir.canWrite()) {
			error = getError("上传目录没有写权限。");
			return error;
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			log.info("上传文件失败!", e1);
			error = getError("上传文件失败!");
			return error;
		}
		if (items == null) {
			error = getError("上传文件失败!");
			return error;
		}
		String comFlag = getFormFieldValue(items, "comFlag");
		String height = getFormFieldValue(items, "smallHeight");
		String width = getFormFieldValue(items, "smallWidth");
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			long fileSize = item.getSize();
			if (!item.isFormField()) {
				// 检查文件大小
				if (fileSize > maxSize) {
					log.info("上传文件大小超过限制");
					error = getError("上传文件大小超过限制。");
					return error;
				}
				// 检查扩展名
				String fileExt = fileName.substring(
						fileName.lastIndexOf(".") + 1).toLowerCase();
				if (!Arrays.<String> asList(fileTypes).contains(fileExt)) {
					log.info("上传文件扩展名是不允许的扩展名");
					error = getError("上传文件扩展名是不允许的扩展名。");
					return error;
				}
				newFileName = filePrefix + getNewFileName() + "." + fileExt;
				try {
					File uploadedFile = new File(modelPath, newFileName);
					item.write(uploadedFile);
				} catch (Exception e) {
					log.info("上传文件保存失败", e);
					error = getError("上传文件保存失败。");
					return error;
				}

				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("datas", newFileName);
				//error ="successful|"+newFileName+"."+fileExt;
				error ="successful|"+newFileName;

				if ("yes".equals(comFlag)) {
					InputStream stream = null;
					try {
						// File files = new File(modelPath, newFileName);
						// stream = new FileInputStream(files);
						// ImageUtils.drawMiniImage(stream, modelPath + "s_" +
						// newFileName,
						// Double.valueOf(height).doubleValue(),
						// Double.valueOf(width)
						// .doubleValue());
					} catch (Exception e) {
						log.info("文件压缩失败!", e);
						error = getError("文件压缩失败!");
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
				}
				return error;
			}

		}
		return error;
	}
	
	@SuppressWarnings("unchecked")
	private static String getFormFieldValue(List items, String key) {
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			if (item.isFormField()) {
				if (key.equals(item.getFieldName())) {
					return item.getString();
				}
			}
		}
		return "";
	}

	private static String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("datas", message);
		return obj.toString();
	}

	/**
	 * 
	 * 功能：拷贝文件
	 * 
	 * @param basePath
	 *            目标存放路径
	 * @param tempPath
	 *            临时文件存放路径
	 * @param modulePath
	 *            模块文件存放路径
	 * @param prefix
	 *            文件名前缀
	 * @param srcFileName
	 *            源文件名
	 */
	public static String copyFile(String basePath, String tempPath,
			String modulePath, String prefix, String srcFileName) {

		if (basePath != null && "".equals(basePath)) {
			log.info("保存文件目录不存在!");
		}
		String fileName = "";
		if (!basePath.endsWith("/") && !modulePath.startsWith("/")) {
			basePath = basePath + "/";
		}
		if (!modulePath.endsWith("/")) {
			modulePath = modulePath + "/";
		}
		File file = new File(modulePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (srcFileName != null && !"".equals(srcFileName)) {
			String[] filenames = srcFileName.split(",");
			for (String string : filenames) {
				/* 判断临时文件夹里是否存在文件,如果不存在说明没有重新上传 */
				File srcFile = new File(tempPath + string);
				if (srcFile.isDirectory() || !srcFile.exists()) {
					continue;
				}
				fileName = prefix;
				if (fileName == null || "".equals(fileName)) {
					return null;
				}
				/*
				 * String ext =
				 * srcFileName.substring(srcFileName.lastIndexOf(".") + 1,
				 * srcFileName.length()); ext = ext.toLowerCase(); fileName =
				 * fileName + "." + ext;
				 */
				try {
					File newFile = new File(basePath + modulePath);
					if (!newFile.exists()) {
						newFile.mkdirs();
					}
					moveFile(tempPath + string, basePath + modulePath + string);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return srcFileName;
	}

	/**
	 * 
	 * 功能：移动文件
	 * 
	 * @param src
	 *            源文件路径
	 * @param dest
	 *            目标文件路径
	 * @throws IOException
	 */
	public static void moveFile(String src, String dest) throws IOException {
		File srcFile = new File(src);
		if (!srcFile.isDirectory() && srcFile.exists()) {
			FileCopyUtils.copy(srcFile, new File(dest));
			srcFile.delete();
		}

	}
    
	
	public static void main(String[] args) {
		
	}

}
