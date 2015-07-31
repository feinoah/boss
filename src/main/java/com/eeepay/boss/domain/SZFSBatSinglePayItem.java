/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;





/**
 * 描述：深圳金融结算中心批量代付明细
 *
 * @author ym 
 * 创建时间：2014年7月28日
 */
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class SZFSBatSinglePayItem {

    // 明细序号
    private String lstno;
    // 外部参考流水号
    private String outid;
    // 协议号
    private String contractno;
    // 客户清算行号
    private String custbankno;
    // 客户开户行号
    private String custaccbankno;
    // 账号
    private String custaccno;
    // 户名
    private String custaccname;
    // 是否替换付款户名
    private String alternameflag;
    /**
     * @return the alternameflag
     */
    public String getAlternameflag() {
      return alternameflag;
    }
    /**
     * @param alternameflag the alternameflag to set
     */
    public void setAlternameflag(String alternameflag) {
      this.alternameflag = alternameflag;
    }
    /**
     * @return the alteredname
     */
    public String getAlteredname() {
      return alteredname;
    }
    /**
     * @param alteredname the alteredname to set
     */
    public void setAlteredname(String alteredname) {
      this.alteredname = alteredname;
    }
    // 替换后付款户名
    private String alteredname;
    // 金额
    private String money;
    // 附言 
    private String memo;
    /**
     * @return the lstno
     */
    public String getLstno() {
      return lstno;
    }
    /**
     * @param lstno the lstno to set
     */
    public void setLstno(String lstno) {
      this.lstno = lstno;
    }
    /**
     * @return the outid
     */
    public String getOutid() {
      return outid;
    }
    /**
     * @param outid the outid to set
     */
    public void setOutid(String outid) {
      this.outid = outid;
    }
    /**
     * @return the contractno
     */
    public String getContractno() {
      return contractno;
    }
    /**
     * @param contractno the contractno to set
     */
    public void setContractno(String contractno) {
      this.contractno = contractno;
    }
    /**
     * @return the custbankno
     */
    public String getCustbankno() {
      return custbankno;
    }
    /**
     * @param custbankno the custbankno to set
     */
    public void setCustbankno(String custbankno) {
      this.custbankno = custbankno;
    }
    /**
     * @return the custaccbankno
     */
    public String getCustaccbankno() {
      return custaccbankno;
    }
    /**
     * @param custaccbankno the custaccbankno to set
     */
    public void setCustaccbankno(String custaccbankno) {
      this.custaccbankno = custaccbankno;
    }
    /**
     * @return the custaccno
     */
    public String getCustaccno() {
      return custaccno;
    }
    /**
     * @param custaccno the custaccno to set
     */
    public void setCustaccno(String custaccno) {
      this.custaccno = custaccno;
    }
    /**
     * @return the custaccname
     */
    public String getCustaccname() {
      return custaccname;
    }
    /**
     * @param custaccname the custaccname to set
     */
    public void setCustaccname(String custaccname) {
      this.custaccname = custaccname;
    }
    /**
     * @return the money
     */
    public String getMoney() {
      return money;
    }
    /**
     * @param money the money to set
     */
    public void setMoney(String money) {
      this.money = money;
    }
    /**
     * @return the memo
     */
    public String getMemo() {
      return memo;
    }
    /**
     * @param memo the memo to set
     */
    public void setMemo(String memo) {
      this.memo = memo;
    }

   
    
    
  }


