/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年10月27日
 */

public class ErrorVO {
  private String errCode;
  private String errMsg;
  private Object data;
  /**
   * @return the errCode
   */
  public String getErrCode() {
    return errCode;
  }
  /**
   * @return the data
   */
  public Object getData() {
    return data;
  }
  /**
   * @param data the data to set
   */
  public void setData(Object data) {
    this.data = data;
  }
  /**
   * @param errCode the errCode to set
   */
  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }
  /**
   * @return the errMsg
   */
  public String getErrMsg() {
    return errMsg;
  }
  /**
   * @param errMsg the errMsg to set
   */
  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
  
  
  

}
