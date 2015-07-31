package com.eeepay.boss.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.JCEHandler;
import com.eeepay.boss.utils.StringUtil;

/**
 * 交易查询，可以查询代理商及子代理商交易数据
 * 
 * @author dj
 */
@Service
public class TerminalService {
	private static Logger log = LoggerFactory.getLogger(TerminalService.class);
	@Resource
	private Dao dao;
	
	public int savePos(Map<String, String> params)   throws SQLException{
		log.info("TerminalService savePos start...");
		int count = 0;
		if(params != null){
			List<String> list = new ArrayList<String>();
			String sql = "insert into pos_type(pos_type_name,pos_type,pos_model,pos_model_name,pos_status,create_person) values(?,?,?,?,?,?)";
			list.add(params.get("pos_type_name"));
			list.add(params.get("pos_type"));
			list.add(params.get("pos_model"));
			list.add(params.get("pos_model_name"));
			list.add("1");
			list.add(params.get("create_person"));
			count = dao.update(sql, list.toArray());
			log.info("TerminalService savePos SUCCESS");
		}
		log.info("TerminalService savePos End");
		return count;
	}
	
	public List<Map<String, Object>> searchPos(String pos_model_name, String pos_model, String pos_type_name){
		log.info("TerminalService searchPosModel start...");
		List<String> list  = new ArrayList<String>();
		String sql = "select p.pos_model,p.pos_model_name from pos_type p where 1=1 ";
		if(!StringUtils.isEmpty(pos_model_name)){
			sql += " and p.pos_model_name=?";
			list.add(pos_model_name);
		}
		
		if(!StringUtils.isEmpty(pos_model)){
			sql += " and p.pos_model=?";
			list.add(pos_model);
		}
		
		if(!StringUtils.isEmpty(pos_type_name)){
			sql += " and p.pos_type_name=?";
			list.add(pos_type_name);
		}
		
		log.info("TerminalService searchPosModel End");
		return dao.find(sql, list.toArray());
	}
	
	/**
	 * 根据设备加载型号
	 * @param pos_type
	 * @return
	 */
	public List<Map<String, Object>> searchPosModel(String pos_type, String pos_status){
		log.info("TerminalService searchPosModel start...");
		List<String> list = new ArrayList<String>();
		String sql = "select p.pos_model,p.pos_model_name from pos_type p where p.pos_type=?";
		list.add(pos_type);
		if(!"".equals(pos_status)){
			sql += " and p.pos_status in(1,?)";
			list.add(pos_status);
		}
		log.info("TerminalService searchPosModel End");
		return dao.find(sql, list.toArray());
	}
	
	/**
	 * 设备查询
	 * @param params
	 * @param pageRequest 分页
	 * @return 
	 */
	public Page<Map<String, Object>> searchPos(Map<String, String> params,	final PageRequest pageRequest){
		log.info("TerminalService searchPos start...");
		String sql = "select * from pos_type p where 1=1 ";
		List<String> list = new ArrayList<String>();
		if(params != null){
			if(StringUtils.isNotEmpty(params.get("pos_type")) && !"-1".equals(params.get("pos_type").toString())){
				String pos_type = params.get("pos_type").toString();
				sql += " and p.pos_type=?";
				list.add(pos_type);
			}
			
			if(StringUtils.isNotEmpty(params.get("pos_model_name")) && !"-1".equals(params.get("pos_model_name").toString())){
				String pos_model_name = params.get("pos_model_name").toString();
				sql += " and p.pos_model=?";
				list.add(pos_model_name);
			}
			
			if(StringUtils.isNotEmpty(params.get("start_create_time"))){
				String start_create_time = params.get("start_create_time").toString();
				sql += " and p.create_time>=?";
				list.add(start_create_time);
			}
			
			if(StringUtils.isNotEmpty(params.get("end_create_time"))){
				String end_create_time = params.get("end_create_time").toString();
				sql += " and p.create_time<=?";
				list.add(end_create_time);
			}
			
			if(StringUtils.isNotEmpty(params.get("create_person"))){
				String create_person = params.get("create_person").toString().trim();
				sql += " and p.create_person=?";
				list.add(create_person);
			}
			
			if(StringUtils.isNotEmpty(params.get("pos_status")) && !"-1".equals(params.get("pos_status").toString())){
				String pos_status = params.get("pos_status").toString();
				sql += " and p.pos_status=?";
				list.add(pos_status);
			}
		}
		sql += " order by p.create_time desc";
		log.info("TerminalService searchPos  End");
		return dao.find(sql, list.toArray(),pageRequest);
	}

	/**
	 * 根据机具SN或PSAM编号获取机具信息
	 * @param terminalNo 拼接要验证的SN列表
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getTerminalByTerNo(String terminalNo){
		List<Map<String, Object>> terList = new ArrayList<Map<String,Object>>();
		if(null != terminalNo && !"".equals(terminalNo)){
			String sql = "select p.* from pos_terminal p where  p.SN = ? or p.PSAM_NO=?";
			List<String> list = new ArrayList<String>();
			list.add(terminalNo);
			list.add(terminalNo);
			terList = dao.find(sql.toString(), list.toArray());
		}
		return terList;
	}
	
	/**
	 * 验证商户录入的SN是否存在和已分配
	 * @param queryCondition 拼接要验证的SN列表
	 * @param terminalNo  要验证SN编号
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> checkTerminalByTerNo(String queryCondition, String agent_no){
		List<Map<String, Object>> terList = new ArrayList<Map<String,Object>>();
		if(null != queryCondition && !"".equals(queryCondition) && !"".equals(agent_no)){
			StringBuffer sql = new StringBuffer("select p.open_status,p.* from pos_terminal p where p.open_status=1 and ");
			sql.append("p.MERCHANT_NO is NULL and p.agent_no=? and p.SN in(");
			sql.append(queryCondition);
			sql.append(")");
			List<String> list = new ArrayList<String>();
			list.add(agent_no);
			terList = dao.find(sql.toString(), list.toArray());
		}
		return terList;
	}

	// 组合条件查询交易数据
	public Page<Map<String, Object>> getTerList(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();

		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 商户no或者name查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
			/*	String merchant = "%" + params.get("merchant") + "%";
				sb.append(" and (  t.merchant_no like ? or  m.merchant_name like ?) ");*/
				String merchant =params.get("merchant");
				sb.append(" and (  t.merchant_no = ? or  m.merchant_name = ?) ");
				list.add(merchant);
				list.add(merchant);
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentNo);
				}
			}

			// 机器SN号
			if (StringUtils.isNotEmpty(params.get("sN"))) {
				String sN = "%" + params.get("sN") + "%";
				sb.append(" and t.sn like ? ");
	/*			String sN =params.get("sN");
				sb.append(" and t.sn = ? ");*/
				list.add(sN);
			}

			if (StringUtils.isNotEmpty(params.get("psamNo"))
					&& StringUtils.isNotEmpty(params.get("psamNo1"))) {
				String psamNo = params.get("psamNo");
				String psamNo1 = params.get("psamNo1");
				sb.append(" and t.psam_no between ? and ? ");
				list.add(psamNo);
				list.add(psamNo1);
			} else // psanm编号
			if (StringUtils.isNotEmpty(params.get("psamNo"))) {
				String psamNo = params.get("psamNo");
				sb.append(" and t.psam_no =? ");
				list.add(psamNo);
			}

			// 终端号
			if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
				String terminalNo = params.get("terminalNo");
				sb.append(" and t.terminal_no =? ");
				list.add(terminalNo);
			}

			// 开通状态
			if (StringUtils.isNotEmpty(params.get("openStatus"))) {
				String openStatus = params.get("openStatus");
				if (!"-1".equals(openStatus)) {
					sb.append(" and t.open_status =? ");
					list.add(openStatus);
				}
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and t.create_time <=? ");
				list.add(createTimeEnd);
			}

			if (StringUtils.isNotEmpty(params.get("allot_batch"))) {
				String allot_batch = params.get("allot_batch");
				sb.append(" and t.allot_batch =? ");
				list.add(allot_batch);
			}
		}
	/*	String sql = "select a.agent_name,t.* ,m.merchant_name  from pos_terminal t left join agent_info a on a.agent_no = t.agent_no  "
				+ "left join pos_merchant m on t.merchant_no = m.merchant_no  where (LENGTH(t.sn)=20 or LENGTH(t.sn)=8 or LENGTH(t.sn)=9 or LENGTH(t.sn)=16 or LENGTH(t.sn)=15 or LENGTH(t.sn)=14  or LENGTH(t.sn)=13 or LENGTH(t.sn)=12)"
				+ sb.toString() + " order by t.sn desc";*/
		String sql = "select CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,t.* ,m.merchant_name  from pos_terminal t left join agent_info a on a.agent_no = t.agent_no  "
				+ "left join pos_merchant m on t.merchant_no = m.merchant_no  where 1=1 "
				+ sb.toString() + " order by t.sn desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	
	/*    // 组合条件查询交易数据
		public Page<Map<String, Object>> getTerList(Map<String, String> params,
				final PageRequest pageRequest) {
			List<Object> list = new ArrayList<Object>();

			StringBuffer sb = new StringBuffer();
			if (null != params) {

				// 商户no或者name查询
				if (StringUtils.isNotEmpty(params.get("merchant"))) {
					String merchant = "%" + params.get("merchant") + "%";
					sb.append(" and (  t.merchant_no like ? or  m.merchant_name like ?) ");
					list.add(merchant);
					list.add(merchant);
				}

				// 代理商名称查询
				if (StringUtils.isNotEmpty(params.get("agentNo"))) {
					if (!params.get("agentNo").equals("-1")) {
						String agentNo = params.get("agentNo");
						sb.append(" and a.agent_no = ? ");
						list.add(agentNo);
					}
				}

				// 机器SN号
				if (StringUtils.isNotEmpty(params.get("sN"))) {
					String sN = "%" + params.get("sN") + "%";
					sb.append(" and t.sn like ? ");
					list.add(sN);
				}

				if (StringUtils.isNotEmpty(params.get("psamNo"))
						&& StringUtils.isNotEmpty(params.get("psamNo1"))) {
					String psamNo = params.get("psamNo");
					String psamNo1 = params.get("psamNo1");
					sb.append(" and t.psam_no between ? and ? ");
					list.add(psamNo);
					list.add(psamNo1);
				} else // psanm编号
				if (StringUtils.isNotEmpty(params.get("psamNo"))) {
					String psamNo = params.get("psamNo");
					sb.append(" and t.psam_no =? ");
					list.add(psamNo);
				}

				// 终端号
				if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
					String terminalNo = params.get("terminalNo");
					sb.append(" and t.terminal_no =? ");
					list.add(terminalNo);
				}

				// 开通状态
				if (StringUtils.isNotEmpty(params.get("openStatus"))) {
					String openStatus = params.get("openStatus");
					if (!"-1".equals(openStatus)) {
						sb.append(" and t.open_status =? ");
						list.add(openStatus);
					}
				}

				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin") + ":00";
					sb.append(" and t.create_time >=? ");
					list.add(createTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd") + ":59";
					sb.append(" and t.create_time <=? ");
					list.add(createTimeEnd);
				}

				if (StringUtils.isNotEmpty(params.get("allot_batch"))) {
					String allot_batch = params.get("allot_batch");
					sb.append(" and t.allot_batch =? ");
					list.add(allot_batch);
				}
			}

			String sql="select t.id,t.sn,t.psam_no,t.terminal_no,t.open_status,t.type,t.model,t.allot_batch, "+
				"case when b.agent_name !='' then b.agent_name else a.agent_name end as  agent_name, "+
				"case when t.belong_agent_no !='' then t.belong_agent_no else t.agent_no end as agent_no, "+
				"m.merchant_name  from pos_terminal t left join agent_info a on a.agent_no = t.agent_no "+
				"LEFT JOIN agent_info  b on b.agent_no=t.belong_agent_no "+ 
				"left join pos_merchant m on t.merchant_no = m.merchant_no "+  
				"where (LENGTH(t.sn)=20 or LENGTH(t.sn)=8 or LENGTH(t.sn)=9 or LENGTH(t.sn)=16 or LENGTH(t.sn)=15 or LENGTH(t.sn)=14  or LENGTH(t.sn)=13 ) "
				+sb.toString() + "order by t.sn desc";
			return dao.find(sql, list.toArray(), pageRequest);
		}*/
	
	
	

	// 给代理商批量分配机器
	public int allotAgent(String agentNo, Map<String, Integer> posType,
			String allotBatch, BossUser bu) throws SQLException {
		// 查询可用的总数是否大于要分配的机器,5不需要分配，3无可用机器，
		int rev = 5;
		int count = 0;
		for (Map.Entry<String, Integer> entry : posType.entrySet()) {
			count = count + entry.getValue();
		}

		String sql = "select id  from pos_terminal where open_status = 0 limit "
				+ count;
		List<Map<String, Object>> list = dao.find(sql);
		if (list.size() < count) {
			log.info(
					"batch allot agent error agent_no:{},db count:{},allot count:{}",
					new Object[] { agentNo, list.size(), count });
			rev = 3;
		} else {
			// 库存有空闲的机器
			int i = 0;
			int lastCount = 0;
			for (Map.Entry<String, Integer> entry : posType.entrySet()) {
				String type = entry.getKey();
				count = entry.getValue();
				StringBuffer sb = new StringBuffer();
				for (Map<String, Object> m : list.subList(i * lastCount,
						lastCount + count)) {
					String id = String.valueOf(m.get("id"));
					sb.append(id);
					sb.append(",");
				}
				if (sb.length() > 0) {
					sb.setLength(sb.length() - 1);
					sql = "update pos_terminal set agent_no='" + agentNo
							+ "',open_status=1,type='" + type
							+ "',allot_batch=" + allotBatch + " where id in("
							+ sb + ")";
					rev = dao.update(sql);
					// 加入历史记录
					if (rev > 0) {
						saveAllotHistory(agentNo, allotBatch, count, type,
								bu.getUserName());
					}
					rev = rev + 100;
				}

				i++;
				lastCount = count;
			}

		}
		return rev;
	}

	// 保存分配历史记录
	public void saveAllotHistory(String agentNo, String batchNo, int count,
			String type, String operator) throws SQLException {
		String sql = "insert into pos_allot_history(agent_no,batch_no,terminal_count,terminal_type,operator,create_time) values(?,?,?,?,?,now())";
		Object[] params = { agentNo, batchNo, count, type, operator };
		dao.update(sql, params);

	}

	public Page<Map<String, Object>> getPosAllotHistory(
			Map<String, String> params, final PageRequest pageRequest) {
		String agent_no = params.get("agent_no");
		List<Object> list = new ArrayList<Object>();
		list.add(agent_no);
		String sql = "select * from pos_allot_history where agent_no=?";

		return dao.find(sql, list.toArray(), pageRequest);
	}

	public int terminalAdd(Map<String, String> params) throws SQLException {
		log.info("TerminalService terminalAdd START");
		String sN = params.get("sN");
		String psamNo = params.get("psamNo");
		String model = params.get("model");
		String type = params.get("type");
		String pos_type = params.get("pos_type");
		String insert_pos_terminal = "insert into pos_terminal(sn,psam_no,open_status,model,type,create_time,pos_type) values(?,?,?,?,?,now(),?)";

		List<Object> listPosTerminal = new ArrayList<Object>();
		listPosTerminal.add(sN);
		listPosTerminal.add(psamNo);
		listPosTerminal.add("0");
		listPosTerminal.add(model);
		listPosTerminal.add(type);
		listPosTerminal.add(pos_type);
		int rowsuc = dao.update(insert_pos_terminal, listPosTerminal.toArray());

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		log.info("TerminalService terminalAdd END");
		return rowsuc;
	}

	public void terminalUpdate(Map<String, String> params, String id)
			throws SQLException {
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			String sql = "UPDATE pos_terminal SET SN=?,PSAM_NO=?,model=?,pos_type=?  WHERE id=?";
			Object[] ter = { params.get("sN"), params.get("psamNo"),	params.get("model"), params.get("pos_type"),id };
			dao.updateByTranscation(sql, ter, conn);
			
			addPosTerminalLog(Integer.parseInt(id), "修改机具", "4", conn);

			conn.commit();
		
		} catch (Exception e) {
			conn.rollback();
			log.error("修改机具未知异常=" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("修改机具关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public void setAgentNo(String agentNo, int id, String type,
			String allotBatch) throws SQLException {
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			String sql = "update pos_terminal set allot_batch=?, type=?, agent_no=?,open_status =1  where id=?";
			Object[] parmas = { allotBatch, type, agentNo, id };
			dao.updateByTranscation(sql, parmas, conn);
	
			addPosTerminalLog(id, "分配代理商", "3", conn);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("分配代理商未知异常=" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("分配代理商关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}
	
	/**
	 * 根据SN分配机具
	 * @param agentNo 代理商编号
	 * @param sn 机具SN号
	 * @param type 机具类型
	 * @param allotBatch 批次
	 * @throws SQLException
	 */
	public int setAgentNo(String agentNo, String sn, String model,String allotBatch) throws SQLException {
		String sql = "update pos_terminal set allot_batch=?, model=?, agent_no=?,open_status =1  where sn=?";
		Object[] parmas = { allotBatch, model, agentNo, sn };
		return dao.update(sql, parmas);
	}
	
	public int setTerminalBind(String merchantNo, int id, int agentNo) throws SQLException {
		int row = 0;
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			String sql;
	
			if (agentNo == 3124) {
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='231B3A233123A222',tmk_tpk='6DB679970ACD9EA7',tmk_tak='8CA188CD65691AA2'  where id=?";
			} else if(agentNo == 13279){
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='47A136CF17F36659',tmk_tpk='27F0551BB23CB1D1',tmk_tak='8212D71B94CE4466'  where id=?";
			} else {
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='9827400AB3001200',tmk_tpk='19834AFF6F3274D5',tmk_tak='927F27A67BBDD23F'  where id=?";
			}
			Object[] parmas = { merchantNo.trim(),
					GenSyncNo.getInstance().getNextTerminalNo(), id };
			row = dao.updateByTranscation(sql, parmas, conn);
	
			addPosTerminalLog(id, "绑定商户", "2", conn);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("绑定商户未知异常=" + e.getMessage());
			e.printStackTrace();
			return row;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("绑定商户关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return row;
	}

	public void setMerchantNo(String merchantNo, int id, int agentNo)
			throws SQLException {
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			String sql;
	
			if (agentNo == 3124) {
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='231B3A233123A222',tmk_tpk='6DB679970ACD9EA7',tmk_tak='8CA188CD65691AA2'  where id=?";
			} else if(agentNo == 13279){
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='47A136CF17F36659',tmk_tpk='27F0551BB23CB1D1',tmk_tak='8212D71B94CE4466'  where id=?";
			} else {
				sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='9827400AB3001200',tmk_tpk='19834AFF6F3274D5',tmk_tak='927F27A67BBDD23F'  where id=?";
			}
			Object[] parmas = { merchantNo.trim(),
					GenSyncNo.getInstance().getNextTerminalNo(), id };
			dao.updateByTranscation(sql, parmas, conn);
	
			addPosTerminalLog(id, "绑定商户", "2", conn);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("绑定商户未知异常=" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("绑定商户关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public void unDistributeTerminal(int id) throws SQLException {
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			addPosTerminalLog(id, "解除分配代理商", "0", conn);
		
			String sql = "update pos_terminal set  allot_batch =null,agent_no=null,belong_agent_no=null,belong_three_agent_no=null,merchant_no=null,type=null,open_status =0 where id=? and open_status =1";
			Object[] parmas = { id };
			dao.updateByTranscation(sql, parmas, conn);

			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("解除分配代理商未知异常=" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("解除分配代理商关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
	
	private void addPosTerminalLog(int id, String oper_desc, String oper_type, Connection conn) throws SQLException{
		String insertSql = "insert into pos_terminal_log(sn,terminal_no,merchant_no,psam_no,belong_agent_no,belong_three_agent_no,agent_no,open_status,type,allot_batch,model,tmk,tmk_tpk,tmk_tak,start_time,create_time,operate_time,oper_desc,oper_user_id,oper_user_name,oper_type) " + 
				"select sn,terminal_no,merchant_no,psam_no,belong_agent_no,belong_three_agent_no,agent_no,open_status,type,allot_batch,model,tmk,tmk_tpk,tmk_tak,start_time,create_time,now(),?,?,?,? from pos_terminal pt where pt.id = ? ";
		
		List<Object> list = new ArrayList<Object>();
		
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(oper_desc);
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(oper_type);
		
		list.add(id);
		
		dao.updateByTranscation(insertSql,list.toArray(), conn);
	}

	public void unBindTerminal(int id, String agentNo) throws SQLException {
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			addPosTerminalLog(id, "解除绑定商户", "1", conn);
	
			if ("2027".equals(agentNo)) {
				// 查询原机具数据
				Map<String, Object> map = getPosTerminalById(id + "");
				String date = id + DateUtils.getCurrentDateTime();
	
				// 解绑机具
				String sql = "update pos_terminal set merchant_no=null,terminal_no=null,open_status =1,START_TIME=null where id=? and open_status =2";
				Object[] parmas = { id };
				dao.updateByTranscation(sql, parmas, conn);
				// 复制数据
				String insertSql = "insert into pos_terminal(sn,TERMINAL_NO,MERCHANT_NO,PSAM_NO,agent_no,open_status,type,allot_batch,model,"
						+ "tmk,tmk_tpk,tmk_tak,START_TIME,CREATE_TIME) "
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
				Object[] insParmas = { date, map.get("TERMINAL_NO"),
						map.get("MERCHANT_NO"), date, map.get("agent_no"),
						map.get("open_status"), map.get("type"),
						map.get("allot_batch"), map.get("model"), map.get("tmk"),
						map.get("tmk_tpk"), map.get("tmk_tak"),
						map.get("START_TIME"), map.get("CREATE_TIME") };
				dao.updateByTranscation(insertSql, insParmas, conn);
			} else {
	
				// 解绑机具
				String sql = "update pos_terminal set merchant_no=null,terminal_no=null,open_status =1,START_TIME=null where id=? and open_status =2";
				Object[] parmas = { id };
				dao.updateByTranscation(sql, parmas, conn);
			}
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("解除绑定商户未知异常=" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("解除绑定商户关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	// 机具明细
	public Map<String, Object> getTerDetail(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select m.merchant_short_name,m.merchant_no,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,a.agent_no,t.* from pos_terminal t left join pos_merchant m on t.merchant_no = m.merchant_no left join agent_info a on a.agent_no=t.agent_no  where t.id = ? ";
		Map<String, Object> terInfo = dao.findFirst(sql, list.toArray());
		return terInfo;
	}

	// 根据终端号取得终端信息
	public Map<String, Object> getPosTerminalByTerminalNo(String terminalNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(terminalNo);
		String sql = " select *  FROM pos_terminal where terminal_no =? ";
		Map<String, Object> posTerminal = dao.findFirst(sql, list.toArray());
		return posTerminal;
	}
	
	public Map<String, Object> getPosTerminalByTerminalNo(String terminalNo, String agentNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(terminalNo);
		String sql = " select *  FROM pos_terminal where terminal_no =?  and agent_no=? and open_status=2";
		Map<String, Object> posTerminal = dao.findFirst(sql, list.toArray());
		return posTerminal;
	}

	// 根据主键取得终端信息
	public Map<String, Object> getPosTerminalById(String id) {
		String sql = " select *  FROM pos_terminal where id =? ";
		return dao.findFirst(sql, id);
	}

	// 根据主键和psam编号取得终端信息
	public Map<String, Object> checkPosTerminalPasmNo(String psam_no, String id) {
		String sql = " select count(*) count FROM pos_terminal where psam_no =? and id != ?";
		Object[] ter = { psam_no, id };
		return dao.findFirst(sql, ter);
	}

	// 根据主键和SN取得终端信息
	public Map<String, Object> checkPosTerminalSn(String sn, String id) {
		String sql = " select count(*) count FROM pos_terminal where sn =? and id != ?";
		Object[] ter = { sn, id };
		return dao.findFirst(sql, ter);
	}

	// 根据psam编号取得终端信息
	public Map<String, Object> checkPasmNo(String psam_no) {
		String sql = " select count(*) count FROM pos_terminal where psam_no =?";
		return dao.findFirst(sql, psam_no);
	}

	// 根据sn编号取得终端信息
	public Map<String, Object> checkSn(String sn) {
		String sql = " select count(*) count FROM pos_terminal where sn =?";
		return dao.findFirst(sql, sn);
	}
	
	public Map<String, Object> getTerminalInfo(String sn, String psam_no) {
		String sql = " select *  FROM pos_terminal where sn =? and psam_no=?";
		Object[] ter = { sn, psam_no };
		return dao.findFirst(sql, ter);
	}
	
	public List<String> addTerminal(List<String> terminalList, String model, String userName, String agent_no,String userId,String pos_type)  throws SQLException{
		log.info("TerminalService addTerminal start ...");
		List<String> failList = new ArrayList<String>();
		if(terminalList != null && terminalList.size() > 0){
			for (int i = 0; i < terminalList.size(); i++) {
				System.out.println(terminalList.get(i).toString());
				String[] snAndPsam = terminalList.get(i).toString().split(":");
				Map<String, Object> terminalInfo = getTerminalInfo(snAndPsam[0],snAndPsam[1]);
				String oper_desc = "";
				String oper_type = "";
				Connection conn = null;
				try {
					conn = dao.getConnection();
					conn.setAutoCommit(false);
					List<String> terminalValueList = new ArrayList<String>();
					boolean addCheck = false;
					String open_status = "0";
					if(terminalInfo != null && !terminalInfo.isEmpty()){
						//机具已存在,避免系统状态异常，已使用的机具不支持再次批量导入分配。且存在的机具批量导入需要选择要分配的代理商
						if(!"2".equals(terminalInfo.get("open_status").toString()) && !"".equals(agent_no) && !"-1".equals(agent_no)){
							terminalValueList.add(agent_no);
							terminalValueList.add(model);
							terminalValueList.add(pos_type);
							terminalValueList.add("1");
							terminalValueList.add(snAndPsam[0]);
							terminalValueList.add(snAndPsam[1]);
							String setTerminalAgentSQL = "update pos_terminal p set p.agent_no=?,p.model=?,pos_type=?,p.open_status=? where p.SN=? and p.PSAM_NO=? and p.open_status!=2";
							int setTerminalAgentCount = dao.updateByTranscation(setTerminalAgentSQL, terminalValueList.toArray(), conn);
							if(setTerminalAgentCount > 0){
								oper_desc = "机具导入-已入库的机具批量分配代理商";
								oper_type = "5";
								addCheck = true;
							}
						}
					}else{
						//机具不存在,如果没有选择代理商，则仅做入库，如果选择了代理商，则入库分配代理商
						if(!"".equals(agent_no) && !"-1".equals(agent_no)){
							terminalValueList.add(snAndPsam[0]);
							terminalValueList.add(snAndPsam[1]);
							terminalValueList.add("1");
							terminalValueList.add(model);
							terminalValueList.add(agent_no);
							terminalValueList.add(pos_type);
							String addTerminalSQL = "insert into pos_terminal (sn,psam_no,open_status,model,create_time,agent_no,pos_type) values(?,?,?,?,now(),?,?)";
							int addTerminalCount = dao.updateByTranscation(addTerminalSQL, terminalValueList.toArray(), conn) ;
							if(addTerminalCount > 0){
								oper_desc = "机具导入-批量导入并分配代理商";
								oper_type = "6";
								addCheck = true;
							}
						}else{
							terminalValueList.add(snAndPsam[0]);
							terminalValueList.add(snAndPsam[1]);
							terminalValueList.add("0");
							terminalValueList.add(model);
							terminalValueList.add("");
							terminalValueList.add(pos_type);
							String addTerminalSQL = "insert into pos_terminal (sn,psam_no,open_status,model,create_time,agent_no,pos_type) values(?,?,?,?,now(),?,?)";
							int addTerminalCount = dao.updateByTranscation(addTerminalSQL, terminalValueList.toArray(), conn) ;
							if(addTerminalCount > 0){
								oper_desc = "机具导入-批量入库";
								oper_type = "7";
								addCheck = true;
							}
						}
					}
					if(addCheck){
						List<String> addTerminalLogList = new ArrayList<String>();
						addTerminalLogList.add(snAndPsam[0]);
						addTerminalLogList.add(snAndPsam[1]);
						addTerminalLogList.add(open_status);
						addTerminalLogList.add(model);
						if(oper_type.equals("7")){
							addTerminalLogList.add("");
						}else{
							addTerminalLogList.add(agent_no);
						}
						addTerminalLogList.add(userName);
						addTerminalLogList.add(userId);
						addTerminalLogList.add(oper_desc);
						addTerminalLogList.add(oper_type);
						String addTerminalLogSQL = "";
						if(oper_type.equals("5")){
							addTerminalLogList.add(terminalInfo.get("create_time").toString());
							addTerminalLogSQL = "insert into pos_terminal_log (sn,psam_no,open_status,model,operate_time,agent_no,oper_user_name,oper_user_id,oper_desc,oper_type,create_time) values(?,?,?,?,now(),?,?,?,?,?,?)";
						}else{
							addTerminalLogSQL = "insert into pos_terminal_log (sn,psam_no,open_status,model,operate_time,agent_no,oper_user_name,oper_user_id,oper_desc,oper_type,create_time) values(?,?,?,?,now(),?,?,?,?,?,now())";
						}
						int addTerminalLogCount = dao.updateByTranscation(addTerminalLogSQL, addTerminalLogList.toArray(), conn);
						if(addTerminalLogCount > 0){
							conn.commit();
						}else{
							conn.rollback();
							failList.add(terminalList.get(i).toString());
						}
					}else{
						conn.rollback();
						failList.add(terminalList.get(i).toString());
					}
				} catch (Exception e) {
					log.error("TerminalService addTerminal Exception = " + e.getMessage());
					failList.add(terminalList.get(i).toString());
					conn.rollback();
				}finally{
					conn.close();
				}
			}
		}
		log.info("TerminalService addTerminal End");
		return failList;
	}

	/**
	 * 导入机具excel
	 * 
	 * @param params
	 * @param urlTemp
	 * @return
	 * @throws Exception
	 */
	public int terminalImp(Map<String, String> params, String urlTemp)
			throws Exception {

		String fileName = params.get("excelFileName");
		String model = params.get("model");
		String url = urlTemp + fileName;
		Object[][] objs = null;
		Object[][] sucobjs = null;
		List<Map<String, Object>> terminalLis = getTerminalList();
		int j = 0;
		try {
			// 构建Workbook对象, 只读Workbook对象
			// 直接从本地文件创建Workbook
			// 从输入流创建Workbook
			InputStream is = new FileInputStream(url);
			Workbook rwb = Workbook.getWorkbook(is);

			// 获取第一张Sheet表
			Sheet rs = (Sheet) rwb.getSheet(0);
			Cell c;

			objs = new Object[rs.getRows() - 1][4];

			boolean flag = true;
			for (int i = 1; i < rs.getRows(); i++) {
				c = ((Sheet) rs).getCell(0, i);
				String snStr = c.getContents();
				c = ((Sheet) rs).getCell(1, i);
				String psamStr = c.getContents();

				if (!StringUtil.isEmpty(snStr) && !StringUtil.isEmpty(psamStr)) {

					for (Map<String, Object> termap : terminalLis) {
						String sn = (String) termap.get("sn");
						String psam_no = (String) termap.get("psam_no");

						if ((snStr.trim()).equals(sn) || (psamStr.trim()).equals(psam_no)) {
							flag = false;
							break;
						}
					}
					if (!flag) {
						flag = true;
						continue;
					}
					objs[j][0] = snStr.trim();
					objs[j][1] = psamStr.trim();
					objs[j][2] = "0";
					objs[j][3] = model;
					j++;

				}
			}
			// 当你完成对Excel电子表格数据的处理后，一定要使用close()方法来关闭先前创建的对象，
			// 以释放读取数据表的过程中所占用的内存空间，在读取大量数据时显得尤为重要。参考如下代码片段：
			// 操作完成时，关闭对象，释放占用的内存空间
			rwb.close();

			if (j > 0) {
				sucobjs = new Object[j][4];
				int row = 0;
				for (int i = 0; i < j; i++) {
					Object[] objects = objs[i];
					if (objects.length > 0) {
						System.arraycopy(objects, 0, sucobjs[row], 0,objects.length);
						row++;
					}
				}
			}

		} catch (Exception e) {
			throw e;
		}

		String insert_pos_terminal = "insert into pos_terminal"
				+ "(sn,psam_no,open_status,model,create_time) "
				+ "values(?,?,?,?,now())";

		if (j > 0) {
			int lin[] = dao.batchUpdate(insert_pos_terminal, sucobjs);
			return lin.length;
		} else {
			return 0;
		}

	}
	
	/**
	 * 获取导入的机具号
	 * @param params
	 * @param urlTemp
	 * @return
	 * @throws Exception
	 */
	public Map getTerminalImp(Map<String, String> params, String urlTemp)
	throws Exception {
		
		String fileName = params.get("excelFileName");
		String url = urlTemp + fileName;
		Map map = new HashMap();
		List<Map<String, Object>> terminalLis = getTerminalList();
		try {
			// 构建Workbook对象, 只读Workbook对象
			// 直接从本地文件创建Workbook
			// 从输入流创建Workbook
			InputStream is = new FileInputStream(url);
			Workbook rwb = Workbook.getWorkbook(is);
			
			// 获取第一张Sheet表
			Sheet rs = (Sheet) rwb.getSheet(0);
			Cell c;
			boolean flag = true;
			for (int i = 1; i < rs.getRows(); i++) {
				c = ((Sheet) rs).getCell(0, i);
				String snStr = c.getContents();
				c = ((Sheet) rs).getCell(1, i);
				String psamStr = c.getContents();
				map.put(snStr,psamStr);
				
			}
			// 当你完成对Excel电子表格数据的处理后，一定要使用close()方法来关闭先前创建的对象，
			// 以释放读取数据表的过程中所占用的内存空间，在读取大量数据时显得尤为重要。参考如下代码片段：
			// 操作完成时，关闭对象，释放占用的内存空间
			rwb.close();
			
		} catch (Exception e) {
			throw e;
		}
		
		return map;
		
	}

	// 查询机具
	public List<Map<String, Object>> getTerminalList() {
		String sql = "select t.SN , t.PSAM_NO  from pos_terminal t ";
		return dao.find(sql);
	}

	public int setAgentAndBind(String agentNo, String psamno, String type,
			String allotBatch, String merchantNo) throws SQLException {
		String sql = "update pos_terminal set allot_batch=?, type=?, agent_no=?, merchant_no=?,"
				+ "open_status =2,START_TIME=now(),terminal_no=?,tmk='9827400AB3001200',"
				+ "tmk_tpk='19834AFF6F3274D5',tmk_tak='927F27A67BBDD23F' ,open_status =2  "
				+ "where (open_status=0||open_status=1) and (PSAM_NO=?||SN=?) ";
		Object[] parmas = { allotBatch, type, agentNo, merchantNo,
				GenSyncNo.getInstance().getNextTerminalNo(), psamno, psamno };
		int row = dao.update(sql, parmas);
		return row;

	}

	// 查询机具表
	public List<Map<String, Object>> getPosTerminalByMno(String merchantNo) {
		String sql = " select *  FROM pos_terminal where MERCHANT_NO =? ";
		return dao.find(sql, merchantNo);
	}

	// 表密钥和机具插入
	public void addKeyAndTerminals(List<Map<String, String>> list)
			throws Exception {
		Connection conn = dao.getConnection();
		conn.setAutoCommit(false);
		try {

			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Map<String, String> map = (Map<String, String>) iterator.next();
				keysSave(map, conn);
				terminalSave(map, conn);
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	public int keysSave(Map<String, String> params, Connection conn)
			throws SQLException {
		String device_id = params.get("device_id");
		String key_content = params.get("key_content");
		String device_type = params.get("device_type");
		String check_value = params.get("check_value");
		String key_type = params.get("key_type");
		String sql = "insert into secret_key(device_id,key_content,device_type,check_value,key_type,create_time) values(?,?,?,?,?,now())";
		int num = dao.updateByTranscation(sql, new Object[] { device_id,
				key_content, device_type, check_value, key_type }, conn);
		return num;
	}

	public int terminalSave(Map<String, String> params, Connection conn)
			throws SQLException {
		String sn = params.get("device_id");
		String device_type = params.get("device_type");
		String sql = "insert into pos_terminal"
				+ "(sn,psam_no,open_status,model,create_time) "
				+ "values(?,?,?,?,now())";
		int num = dao.updateByTranscation(sql, new Object[] { sn, sn, "0",
				device_type }, conn);
		return num;
	}

	/**
	 * 把接受的全部文件打成压缩包
	 * 
	 * @param List
	 *            <File>;
	 * @param org
	 *            .apache.tools.zip.ZipOutputStream
	 */
	public void zipFile(List files, ZipOutputStream outputStream) {
		int size = files.size();
		for (int i = 0; i < size; i++) {
			File file = (File) files.get(i);
			zipFile(file, outputStream);
		}
	}

	public HttpServletResponse downloadZip(File file,
			HttpServletResponse response) {
		try {
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(
					file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();

			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ file.getName());
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				File f = new File(file.getPath());
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	/**
	 * 根据输入的文件与输出流对文件进行打包
	 * 
	 * @param File
	 * @param org
	 *            .apache.tools.zip.ZipOutputStream
	 */
	public void zipFile(File inputFile, ZipOutputStream ouputStream) {
		try {
			if (inputFile.exists()) {
				/**
				 * 如果是目录的话这里是不采取操作的， 至于目录的打包正在研究中
				 */
				if (inputFile.isFile()) {
					FileInputStream IN = new FileInputStream(inputFile);
					BufferedInputStream bins = new BufferedInputStream(IN, 512);
					// org.apache.tools.zip.ZipEntry
					ZipEntry entry = new ZipEntry(inputFile.getName());
					ouputStream.putNextEntry(entry);
					// 向压缩文件中输出数据
					int nNumber;
					byte[] buffer = new byte[512];
					while ((nNumber = bins.read(buffer)) != -1) {
						ouputStream.write(buffer, 0, nNumber);
					}
					// 关闭创建的流对象
					bins.close();
					IN.close();
				} else {
					try {
						File[] files = inputFile.listFiles();
						for (int i = 0; i < files.length; i++) {
							zipFile(files[i], ouputStream);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成密文密钥
	 * 
	 * @return
	 */
	public String getKeys1(String maker_key) {
		UUID uuid = UUID.randomUUID();
		String key = uuid.toString().replace("-", "").toUpperCase();
		String cv = JCEHandler.encryptData("00000000000000000000000000000000",key );

		key = JCEHandler.encryptData(key, maker_key);


		return key + "==" + cv;
	}

	/**
	 * 写入密钥
	 * 
	 * @param os
	 * @param request
	 * @param list
	 */
	public void writeToTxt(OutputStream os, HttpServletRequest request,
			List list) throws Exception {
		String path = System.getProperty("user.dir");
		String fileTemp = "/upordown/";
		File f = new File(path + fileTemp + File.separator + "magazinePub.txt");
		File parentFile = f.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		String enter = "\r\n";
		StringBuffer write;
		try {
			for (int i = 0; i < list.size(); i++) {
				write = new StringBuffer();
				write.append(list.get(i) + enter);
				os.write(write.toString().getBytes("UTF-8"));
			}
			os.flush();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writeToTxt2(String filePath, HttpServletRequest request,
			List list) throws Exception {

		String enter = "\r\n";
		StringBuffer write;
		FileWriter fw = new FileWriter(filePath);
		try {
			for (int i = 0; i < list.size(); i++) {
				fw.write(list.get(i) + enter);
				fw.flush();
			}
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
     * 批量新增密钥
     */
    public int scretKeyAdd(Map<String, String> params,List<Map<String, String>> list) throws Exception {
          Connection conn = dao.getConnection();
          conn.setAutoCommit(false);
          int[] lin = new int[]{};
          try{
                String device_type=params.get("device_type");//设备类型
                String key_type=params.get("key_type");//设备类型
                int j=0;
                Object[][] objs = new Object[list.size()][5];;
                for (int i = 0; i < list.size(); i++) {
                      Map<String,String> map = list.get(i);
                      objs[j][0] = map.get("device_id");
                      objs[j][1] = map.get("key");
                      objs[j][2] = device_type;
                      objs[j][3] = map.get("cv");
                      objs[j][4] = key_type;
                      j++;
                }
                String sql = "insert into secret_key(device_id,key_content,device_type,check_value,key_type,create_time)" +
                            " values(?,?,?,?,?,now())";
    
                if(j>0){
                      lin = dao.batchUpdate(sql, objs,conn);
                }
                conn.commit();
          }catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
          }finally{
                conn.close();
          }
          return lin.length;
    }
}
