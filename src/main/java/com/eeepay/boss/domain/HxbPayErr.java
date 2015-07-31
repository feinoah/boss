package com.eeepay.boss.domain;

/**
 *  描述：
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */

public class HxbPayErr {
	private String errCode;
	private String errMsg;
	private String data;
	private String fileId;
	
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
  /**
   * @return the data
   */
  public String getData() {
    return data;
  }
  /**
   * @param data the data to set
   */
  public void setData(String data) {
    this.data = data;
  }
  /**
   * @return the fileId
   */
  public String getFileId() {
    return fileId;
  }
  /**
   * @param fileId the fileId to set
   */
  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
	
  
  

}
