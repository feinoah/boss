package com.eeepay.boss.domain;

public class SmsBean {

    private String mobile;
    private String agent_no;
    private String pos_type;
    private String send_for;
    private String[] placeHolder;

    public SmsBean(String mobile, String agent_no, String pos_type, String send_for, String[] placeHolder) {
        this.mobile = mobile;
        this.agent_no = agent_no;
        this.pos_type = pos_type;
        this.send_for = send_for;
        this.placeHolder = placeHolder;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAgent_no() {
        return agent_no;
    }

    public void setAgent_no(String agent_no) {
        this.agent_no = agent_no;
    }

    public String getPos_type() {
        return pos_type;
    }

    public void setPos_type(String pos_type) {
        this.pos_type = pos_type;
    }

    public String getSend_for() {
        return send_for;
    }

    public void setSend_for(String send_for) {
        this.send_for = send_for;
    }

    public String[] getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String[] placeHolder) {
        this.placeHolder = placeHolder;
    }
}
