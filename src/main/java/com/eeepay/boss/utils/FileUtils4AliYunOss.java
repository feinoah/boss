package com.eeepay.boss.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class FileUtils4AliYunOss {
	/**
	 * 从临时目录拷贝文件,并删除临时目录的文件
	 * @param attachment
	 * @return 成功拷贝的文件
	 */
	public static void copyFile(String attachment) {
		String[] fileNames = attachment.split(",");
		try {
			for (String fileName : fileNames) {
				if(StringUtils.isBlank(fileName)){
					continue;
				}
				if (ALiYunOssUtil.exists(Constants.ALIYUN_OSS_TEMP_TUCKET,
						fileName)) {
					ALiYunOssUtil.cp(Constants.ALIYUN_OSS_TEMP_TUCKET,
							fileName, Constants.ALIYUN_OSS_ATTCH_TUCKET,
							fileName);
					ALiYunOssUtil.rm(Constants.ALIYUN_OSS_TEMP_TUCKET, fileName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
