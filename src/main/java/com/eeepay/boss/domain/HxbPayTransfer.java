/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年8月20日
 */
@XmlRootElement(name = "package")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class HxbPayTransfer {
  private String submitChannel;
  private String fileId;
  private String duplicateTimeStamp;
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
  /**
   * @return the duplicateTimeStamp
   */
  public String getDuplicateTimeStamp() {
    return duplicateTimeStamp;
  }
  /**
   * @param duplicateTimeStamp the duplicateTimeStamp to set
   */
  public void setDuplicateTimeStamp(String duplicateTimeStamp) {
    this.duplicateTimeStamp = duplicateTimeStamp;
  }
  /**
   * @return the submitChannel
   */
  public String getSubmitChannel() {
    return submitChannel;
  }
  /**
   * @param submitChannel the submitChannel to set
   */
  public void setSubmitChannel(String submitChannel) {
    this.submitChannel = submitChannel;
  }
  
  

}
