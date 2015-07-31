/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年10月8日
 */

public class SZFSError {
private String success;
private String status;
private String proCode;
private String remark;



/**
 * @param success
 * @param status
 * @param proCode
 * @param remark
 */
public SZFSError() {
  super();
  this.success = "false";
  this.status = "";
  this.proCode = "";
  this.remark = "";
}


/**
 * @return the success
 */
public String getSuccess() {
  return success;
}


/**
 * @param success the success to set
 */
public void setSuccess(String success) {
  this.success = success;
}


/**
 * @return the status
 */
public String getStatus() {
  return status;
}
/**
 * @param status the status to set
 */
public void setStatus(String status) {
  this.status = status;
}
/**
 * @return the proCode
 */
public String getProCode() {
  return proCode;
}
/**
 * @param proCode the proCode to set
 */
public void setProCode(String proCode) {
  this.proCode = proCode;
}
/**
 * @return the remark
 */
public String getRemark() {
  return remark;
}
/**
 * @param remark the remark to set
 */
public void setRemark(String remark) {
  this.remark = remark;
}


}
