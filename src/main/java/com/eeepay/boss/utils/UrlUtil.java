package com.eeepay.boss.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.controller.AgentController;

public class UrlUtil {

  // private static Logger logger =Logger.getLogger(UrlUtil.class);

  private static final Logger logger = LoggerFactory
      .getLogger(AgentController.class);

  /**
   * url拆解成map.
   */
  public static Map<Object, Object> urlParamsToMap(String url) {
    logger.info("urlParamsToMap:url" + url);
    Map<Object, Object> m = new HashMap<Object, Object>();

    if (url != null && url.indexOf("?") != -1) {
      logger.info("url!=null&&url.indexOf(?)!=-1");
      url = url.substring(url.indexOf("?") + 1, url.length());
      logger.info("url=url.substring(url.indexOf(?)+1, url.length());");
    }

    logger.info("url=url.trim();");
    url = url.trim();

    logger.info("  String paramsArr[]=url.split(&);");
    String paramsArr[] = url.split("\\&");
    for (int i = 0; i < paramsArr.length; i++) {
      logger.info("for -------------");
      // System.out.println(paramsArr[i]);
      // System.out.println(paramsArr[i].split("=")[0]+"----"+(paramsArr[i].split("=").length>1?paramsArr[i].split("=")[1]:""));
      m.put(paramsArr[i].split("\\=")[0],
          paramsArr[i].split("\\=").length > 1 ? paramsArr[i].split("\\=")[1]
              : "");
    }
    logger.info("for ------end-------");
    return m;
  }

  public static void main(String[] arg) {
    // String url =
    // "divNo=2031000021000049&Div_random=5706136806528251549E013A35B8DC106&terminalNo=&trackMsg=A4B71E357DC63538ECA5EFD8FD705659987A9AF14423C078F17EC6D23FAF825D98DB198C9CF5EE1DA2E917893237C002D5489CE9132EBECE795C8603F4D7DF02795C8603F4D7DF02337BE34BB32B5FEC&cardPwd=3405EE9CB2EFFBEF&amount=49.00&transTime=20130509103433&transMac=64BE837A475B8855ACD38427071C0549&track2Length=48&track3Length=96&transTime=130509102904&mobjournal=130509102904&userNumber=13192086001&billValue=50.00";
    String url = "flag=0&msg=交易失败，响应码为：32,错误原因:帐号密码有误";

    Map<Object, Object> m = urlParamsToMap(url);

    String flag = StringUtil.ifEmptyThen(m.get("flag"), "");
    String msg = StringUtil.ifEmptyThen(m.get("msg"), "");
    String bjournal = StringUtil.ifEmptyThen(m.get("bjournal"), "");// 银盛流水号
    String cardNoAfterJieXi = StringUtil.ifEmptyThen(m.get("cardNo"), "");

    System.out.println(m);
  }

}
