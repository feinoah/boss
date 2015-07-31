package com.eeepay.boss.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import org.slf4j.Logger;

public class LogException {

  /**
   * 将异常堆栈 放置到输出流里边。供打印日志使用。
   */
  public static StringWriter returnExceptionStringWriter(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw;
  }

  public static StringWriter logException(Exception e, Logger log) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    log.info(sw.toString());
    return sw;
  }
  
  /**
   * 将异常堆栈 放置到输出流里边。供打印日志使用。
   */
  public static StringWriter logException2(Exception e, Logger log) {
    return  logException(e,log);
  }


  /**
   * 将异常堆栈 放置到输出流里边。供打印日志使用。并打印参数info。
   */
  public static StringWriter logExceptionAndInfo(Exception e, Logger log,String info) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    log.info(sw.toString());
    log.info(info);
    return sw;
  }
  
  
  /**
   * 打印出该日志信息所在的类，方法，和所在的行。
    */
  public static String logInfo(Logger log, String info) {
    return  logInfo(  log, null,  info);
  }
  
  /**
   * 打印出该日志信息所在的类，方法，和所在的行。
    */
  public static String logInfo(Logger log, String tips,String info) {
     Throwable t=new Throwable();
     StackTraceElement ste=t.getStackTrace()[0] ;
     String exceptionText=ste.getClassName()+"."+ste.getMethodName()+"(...)"+ste.getLineNumber();
     log.info("当前类.方法()和代码行是："+exceptionText );
     log.info(tips+":start:"+info+":end" );
     return exceptionText;
  }
  /**
   * public static void main(String[] arg) { // f1();
   * System.out.print("------------"); f2(); }
   * 
   * 
   *  // 测试方法 public static void f1(){
   * 
   * try{ throw new RuntimeException(); } catch (Exception e) {
   * e.printStackTrace(); StringWriter sw= returnExceptionStringWriter(e);
   * System.out.print(sw.toString()); } } // 测试方法 public static void f2(){
   * 
   * Logger log = LoggerFactory .getLogger(FinanceControllerWill.class);
   * 
   * try{ throw new RuntimeException(); } catch (Exception e) { //
   * e.printStackTrace(); StringWriter sw= logException2(e,log);
   *  } }
   * 
   */
}
