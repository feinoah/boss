package com.eeepay.boss.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 系统配置表信息读取
 * 
 * @author dj
 * 
 */
public class SysConfig implements ApplicationContextAware {
	private static ApplicationContext applicationContext; // Spring应用上下文环境
	// 系统配置缓存
	private static final Map<String, String> cache = new ConcurrentHashMap<String, String>();

	/**
	 * 根据key取出value
	 * 
	 * @param key
	 * @return value
	 */
	public static String value(String key) {
		String value = cache.get(key);
		if (value == null) {
			String sql = "select PARAM_VALUE from sys_config where PARAM_KEY=?";
			value = getDao().findBy(sql, "PARAM_VALUE", key).toString();
			cache.put(key, value);
		}
		return value;
	}

	
  /**
   * 根据key取出value,实时查询，非缓存
   * 
   * @param key
   * @return value
   */
  public static String valueNoCache(String key) {
    String sql = "select PARAM_VALUE from sys_config where PARAM_KEY=?";
    String result = "";
    Object resultObject = getDao().findBy(sql, "PARAM_VALUE", key);
    if (resultObject == null) {
      return result;
    }
    result = resultObject.toString();

    return result;
  }
	
	
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;
	}

	public static Dao getDao() {
		return applicationContext.getBean(Dao.class);
	}
}
