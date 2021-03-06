package com.eeepay.boss.utils;

import java.util.regex.Pattern;


public class StringUtil {
	
	/**
	 * 字符串左补0
	 * */
	public static String stringFillLeftZero(String str, int len) {
		if (str.length() < len) {
			StringBuffer sb = new StringBuffer(len);
			for (int i = 0; i < len - str.length(); i++)
				sb.append('0');
			sb.append(str);
			return new String(sb);
		} else
			return str;
	}
	
	/**
	   * 功能：不定长参数,其中一个参数为null或空则返回true,负责返回false
	   * 
	   * @param str
	   * @return boolean
	   */
	  public static boolean isEmpty(String... str) {
	    for (String s : str) {
	      if (org.apache.commons.lang.StringUtils.isEmpty(s)) {
	        return true;
	      }
	    }
	    return false;
	  }

	  /**
	   * 功能：不定长参数,其中一个参数为null或空或为空格字符串则返回true,负责返回false
	   * 
	   * @param str
	   * @return boolean
	   */
	  public static boolean isBlank(String... str) {
	    for (String s : str) {
	      if (org.apache.commons.lang.StringUtils.isBlank(s))
	        return true;
	    }
	    return false;
	  }

	  /**
	   * 功能：判断字符串是否是数值. 默认允许有正负号,默认允许有小数点
	   * 
	   * @param str
	   * @return
	   */
	  public static boolean isNumeric(String str) {
	    boolean sign = true;
	    int point_bef = Integer.MAX_VALUE;// 小数点前有几位
	    int point_aft = Integer.MAX_VALUE;// 小数点后有几位
	    return isNumeric(str, sign, point_bef, point_aft);
	  }

	  /**
	   * 功能：判断字符串是否是数值
	   * 
	   * @param str
	   * @param sign
	   *          是否允许有正负号
	   * @param point
	   *          是否允许有小数点
	   * @return
	   */
	  public static boolean isNumeric(String str, boolean sign, boolean point) {
	    int point_bef = Integer.MAX_VALUE;// 小数点前有几位
	    int point_aft = Integer.MAX_VALUE;// 小数点后有几位
	    if (!point)
	      point_aft = 0;

	    return isNumeric(str, sign, point_bef, point_aft);
	  }

	  /**
	   * 功能：判断字符串是否是数值
	   * 
	   * @param str
	   * @param sign
	   *          是否允许有正负号
	   * @param point_bef
	   *          精度,小数点前有几位
	   * @param point_aft
	   *          精度,小数点后有几位,如果为0,则为整数
	   * 
	   * @return
	   */
	  public static boolean isNumeric(String str, boolean sign, int point_bef,
	      int point_aft) {
	    if (StringUtil.isBlank(str)) {
	      return false;
	    }
	    boolean point = true;// 是否允许小数点
	    if (point_aft == 0) {
	      point = false;// 不允许有小数点
	    } else {
	      point = true;
	    }
	    StringBuffer pat = new StringBuffer();
	    if (sign) {
	      pat.append("[+|-]?");
	    }
	    if (point_bef == 0) {
	      pat.append("[0]");
	    } else {
	      pat.append("[0-9]{1,");
	      pat.append(point_bef);
	      pat.append("}");
	    }
	    if (point && str.indexOf(".") != -1) {// 允许小数点,并且有小数点
	      pat.append("[.]");
	      pat.append("[0-9]{1,");// 小数点后必须有一位
	      pat.append(point_aft);
	      pat.append("}");
	    }
	    Pattern pattern = Pattern.compile(pat.toString());
	    if (!pattern.matcher(str).matches()) {
	      return false;
	    } else {// 排除如00.1,返回false
	      if (str.indexOf(".") != -1
	          && str.substring(0, str.indexOf(".")).length() > 1
	          && Integer.valueOf(str.substring(0, str.indexOf("."))) == 0) {
	        return false;
	      } else {
	        return true;
	      }
	    }
	  }

	  /**
	   * 功能：查看字符串是否有这个子字符串
	   * 
	   * @param str
	   *          主字符串
	   * @param substr
	   *          字字符串
	   * @return
	   */
	  public static boolean hasSubstring(String str, String substr) {
	    if (str == null || substr == null)
	      return false;
	    int strLen = str.length();
	    int substrLen = substr.length();
	    for (int i = 0; (i + substrLen) <= strLen; i++) {
	      if (str.substring(i, i + substrLen).equalsIgnoreCase(substr)) {
	        return true;
	      }
	    }
	    return false;
	  }

	  /**
	   * 功能：验证是否是正确的手机号
	   * 
	   * @param mobile
	   * @return
	   */
	  public static boolean isMobile(String mobile) {
	    if (StringUtil.isBlank(mobile))
	      return false;
	    return Pattern.matches("^(1[3|5|8])\\d{9}$", mobile);
	  }

	  /**
	   * 功能：字符串不以"/"结尾，则在串尾加"/"
	   * 
	   * @param s
	   * @return
	   */
	  public static String addSlashInEnd(String s) {
	    if (s != null) {
	      s = s.trim();
	      if (!s.endsWith("/")) {
	        s = s + "/";
	      }
	    } else {
	      s = "";
	    }
	    return s;
	  }

	  /**
	   * 功能：字符串不以"/"结尾，则在串尾加"/";字符串如果以/开头,则去掉第一个/
	   * 
	   * @return
	   */
	  public static String dealSlash(String s) {
	    if (s != null) {
	      s = s.trim();
	      if (!s.endsWith("/")) {
	        s = s + "/";
	      }
	      if (s.startsWith("/")) {
	        s = s.substring(1, s.length());
	      }
	    } else {
	      s = "";
	    }
	    return s;

	  }

	  /**
	   * 功能：传入一个数字类型的参数，返回一个小数点后两位的小数
	   * 
	   * @param parm
	   */
	  public static String ConverDouble(String parm) {
	    if (isNumeric(parm, false, true)) {
	      if (parm.indexOf(".") >= 0) {
	        String value = parm.substring(parm.indexOf(".") + 1);
	        if (value.length() == 1) {
	          return parm + "0";
	        } else if (value.length() > 2) {
	          return parm.substring(0, parm.indexOf(".") + 1)
	              + value.substring(0, 2);
	        } else {
	          return parm;
	        }
	      } else {
	        return parm + ".00";
	      }
	    }
	    return null;
	  }
	  
	  
	  /**
	   * 
	   * @param obj
	   * @return String
	   * @obj==null,或obj是空字符串，就返回参数ifEmptyThen，否则返回obj.toString。
	   */

	  public static String ifEmptyThen(Object obj,String ifEmptyThen) {
      String ret="";
      if(obj==null||String.valueOf(obj)==""){
        ret=ifEmptyThen;
      }else{
        ret=obj.toString();
      }
      return ret;
    }

	  /**
		 * 功能：传入一个对象，如果为null，则输出为"",如果不为null,就调用toString()方法
		 * 
		 * @param parm
		 */
		public static String filterNull(Object s) {
			if (s == null) {
				return "";
			} else {
				return s.toString();
			}
		}
	
}
