package com.eeepay.boss.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * 生成POS终端号
 * 
 * @author donjek
 * 
 */
public class GenSyncNo implements ApplicationContextAware {
	private static GenSyncNo instance = null;
	private static ApplicationContext applicationContext; // Spring应用上下文环境

	private GenSyncNo() {

	}

	public static synchronized GenSyncNo getInstance() {
		if (null == instance) {
			instance = new GenSyncNo();
		}
		return instance;
	}

	// 商户编号
	public synchronized String getNextMerchantNo() throws SQLException {
		return getNext("merchant_no_seq", "100000000000000");
	}

	// 终端号
	public synchronized String getNextTerminalNo() throws SQLException {
		return getNext("terminal_no_seq", "10000000");
	}

	// 代理商编号
	public synchronized String getNextAgentNo() throws SQLException {
		return getNext("agent_no_seq", "1000");
	}

	// 案例编号
	public synchronized String getNextCaseNo() throws SQLException {
		return getNext("case_no_seq", "100000");
	}

	// 获取POS机分配的批次号
	public synchronized String getNextPosAllotNo() throws SQLException {
		return getNext("terminal_allot_seq", "1000");
	}

	// 获取POS机参考号
	public synchronized String getNextPosReferNo() throws SQLException {
		return getNext("pos_refer_no", "100000000000");
	}

	// 获取小宝SN编号
	public synchronized String getTerminalSnSeq() throws SQLException {
		return getNext("terminal_sn_seq", "100000000000000");
	}
	
	//获取集群编号
	public synchronized String getGroupCodeSeq() throws SQLException {
		return getNext("group_code_seq", "3000");
	}
	
	//获取设备编号
	public synchronized String getPosTypeSeq() throws SQLException {
		return getNext("pos_type_seq", "7");
	}

	private synchronized String getNext(String seqName, String defValue)
			throws SQLException {
		// Dao dao = applicationContext.getBean(Dao.class);
		Resource rs = applicationContext.getResource("classpath:config"
				+ java.io.File.separator + "jdbc.properties");
		Object t = null;
		try {
			File file = rs.getFile();
			FileInputStream fis = new FileInputStream(file);
			Properties p = new Properties();
			p.load(fis);
			String url = p.getProperty("master.url");
			String driverClassName = p.getProperty("master.driverClassName");
			String username = p.getProperty("master.username");
			String password = p.getProperty("master.password");

			Class.forName(driverClassName);
			Connection conn = DriverManager.getConnection(url, username,
					password);

			Statement stmt = conn.createStatement();
			String sql = "select nextval('" + seqName + "') as t";
			ResultSet rs2 = stmt.executeQuery(sql);
			while (rs2.next()) {
				t = rs2.getObject("t");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		BigInteger v = new BigInteger(defValue);
		if (null != t) {
			String src = t.toString();
			v = new BigInteger(src);
			v.add(new BigInteger("1"));
		}

		return v.toString();
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;

	}

}
