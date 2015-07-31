/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 描述：深圳金融结算中心批量代付
 *
 * @author ym    
 * 创建时间：2014年7月28日
 */
@XmlRootElement(name = "BATSINGLEPAY")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SZFSBatSinglePay {
//委托日期 YYYYMMDD
  private String subdate;

  //批次号 :委托方 + 报文类型 + 批次号不重复
  private String batno;
//企业代码
  private String corpno;
//业务编码
  private String transcode;
//产品编码
  private String productcode;
//费项代码
  private String feeitem;
//企业清算行
  private String bankno;
//企业开户行
  private String accbankno;
//企业账号
  private String accno;
//企业户名
  private String accname;
//币种
  private String currency;
//总笔数
  private String totalnum;
//总金额
  private String totalmoney;
// 摘要
  private String summary;
  
  @XmlElementWrapper(name = "details", required = true)
  private List<SZFSBatSinglePayItem> detail;
  
  

  /**
   * @return the summary
   */
  public String getSummary() {
    return summary;
  }



  /**
   * @param summary the summary to set
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }



  /**
   * @return the details
   */
  
  public List<SZFSBatSinglePayItem> getDetail() {
    return detail;
  }



  /**
   * @param details the details to set
   */
  public void setDetail(List<SZFSBatSinglePayItem> detail) {
    this.detail = detail;
  }



  
  
  /**
   * @return the subdate
   */
  public String getSubdate() {
    return subdate;
  }



  /**
   * @param subdate the subdate to set
   */
  public void setSubdate(String subdate) {
    this.subdate = subdate;
  }



  /**
   * @return the batno
   */
  public String getBatno() {
    return batno;
  }



  /**
   * @param batno the batno to set
   */
  public void setBatno(String batno) {
    this.batno = batno;
  }



  /**
   * @return the corpno
   */
  public String getCorpno() {
    return corpno;
  }



  /**
   * @param corpno the corpno to set
   */
  public void setCorpno(String corpno) {
    this.corpno = corpno;
  }



  /**
   * @return the transcode
   */
  public String getTranscode() {
    return transcode;
  }



  /**
   * @param transcode the transcode to set
   */
  public void setTranscode(String transcode) {
    this.transcode = transcode;
  }



  /**
   * @return the productcode
   */
  public String getProductcode() {
    return productcode;
  }



  /**
   * @param productcode the productcode to set
   */
  public void setProductcode(String productcode) {
    this.productcode = productcode;
  }



  /**
   * @return the feeitem
   */
  public String getFeeitem() {
    return feeitem;
  }



  /**
   * @param feeitem the feeitem to set
   */
  public void setFeeitem(String feeitem) {
    this.feeitem = feeitem;
  }



  /**
   * @return the bankno
   */
  public String getBankno() {
    return bankno;
  }



  /**
   * @param bankno the bankno to set
   */
  public void setBankno(String bankno) {
    this.bankno = bankno;
  }



  /**
   * @return the accbankno
   */
  public String getAccbankno() {
    return accbankno;
  }



  /**
   * @param accbankno the accbankno to set
   */
  public void setAccbankno(String accbankno) {
    this.accbankno = accbankno;
  }



  /**
   * @return the accno
   */
  public String getAccno() {
    return accno;
  }



  /**
   * @param accno the accno to set
   */
  public void setAccno(String accno) {
    this.accno = accno;
  }



  /**
   * @return the accname
   */
  public String getAccname() {
    return accname;
  }



  /**
   * @param accname the accname to set
   */
  public void setAccname(String accname) {
    this.accname = accname;
  }



  /**
   * @return the currency
   */
  public String getCurrency() {
    return currency;
  }



  /**
   * @param currency the currency to set
   */
  public void setCurrency(String currency) {
    this.currency = currency;
  }



  /**
   * @return the totalnum
   */
  public String getTotalnum() {
    return totalnum;
  }



  /**
   * @param totalnum the totalnum to set
   */
  public void setTotalnum(String totalnum) {
    this.totalnum = totalnum;
  }



  /**
   * @return the totalmoney
   */
  public String getTotalmoney() {
    return totalmoney;
  }



  /**
   * @param totalmoney the totalmoney to set
   */
  public void setTotalmoney(String totalmoney) {
    this.totalmoney = totalmoney;
  }







  

}
