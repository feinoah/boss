package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.axis.impl.AxisCleanCacheServiceImpl;
import com.eeepay.boss.common.bean.KeyPair;
import com.eeepay.boss.common.bean.MerchantApiAddBean;
import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.domain.PosMerchant;
import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.enums.MerchantApiStatus;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.KeyGen;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.report.Report;
import com.eeepay.report.ReportImpl;


/**
 * 商户业务操作
 * 
 * @author zhanghw
 */
/**
 * @author Administrator
 *
 */
@Service
public class MerchantService {
	@Resource
	private Dao dao;

	private static final Logger log = LoggerFactory.getLogger(MerchantService.class);

	@Resource
	private AxisCleanCacheServiceImpl cleanCacheService;
	
	public Map<String, Object> getFirstMerchantInfoByMerchantNo(String merchantNo) {
		String sql = "select * from pos_merchant where merchant_no = ?";
		return dao.findFirst(sql, new Object[]{merchantNo});
	}
	
	public Map<String, Object> getHlfAssembleFailReason(String merchantNo) {
		String sql = "select * from hlf_synchro_take where merchant_no = ? order by create_time desc";
		return dao.findFirst(sql, new Object[]{merchantNo});
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
	
	/**
	 * 根据商户编号， 自动绑定机具、自动将商户状态变更为“正常”(录入机具数量超过20个不处理)
	 * @param merchant_no 商户编号
	 * @return true：操作成功
	 */
	public void openMerchantAndTerminal(Map<String, Object> map, Connection conn) throws SQLException  {
		String merchant_no = map.get("merchant_no").toString();
		String agent_no = map.get("agent_no").toString();
		String pos_type = map.get("pos_type").toString();
		String terminal_no = map.get("terminal_no").toString();
		
		if(null != pos_type &&  "3".equals(pos_type) || "4".equals(pos_type)  || "5".equals(pos_type)){ //校验设备类型
			if(!"".equals(terminal_no) && !"".equals(agent_no)){ 
				String[] merchantTerminalInfo = terminal_no.split(";");
				if(merchantTerminalInfo.length > 0 && merchantTerminalInfo.length < 21){  //机具超过20个不处理
					StringBuffer queryConditions = new StringBuffer();
					for(String merchantTerminalNo : merchantTerminalInfo){
						queryConditions.append("'").append(merchantTerminalNo).append("'").append(",");
					}
					if(queryConditions.length() > 0)
						queryConditions = queryConditions.deleteCharAt(queryConditions.lastIndexOf(","));
					
					
					//根据商户所属代理商编号和录入的SN 编号匹配所录入的机具有效性
					List<Map<String, Object>> terminalList = new ArrayList<Map<String,Object>>();
					if(null != queryConditions && !"".equals(queryConditions) && !"".equals(agent_no)){
						StringBuffer sql = new StringBuffer("select p.open_status,p.* from pos_terminal p where p.open_status=1 and ");
						sql.append("p.MERCHANT_NO is NULL and p.agent_no=? and p.SN in(");
						sql.append(queryConditions);
						sql.append(")");
						List<String> list = new ArrayList<String>();
						list.add(agent_no);
						terminalList = dao.find(sql.toString(), list.toArray());
					}
					
					//验证数量是否匹配
					if(terminalList != null && terminalList.size() == merchantTerminalInfo.length){
						//根据商户编号绑定机具
						for(Map<String, Object> terminal : terminalList){
							String sql;
							if ("3124".equals(agent_no)) {
								sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='231B3A233123A222',tmk_tpk='6DB679970ACD9EA7',tmk_tak='8CA188CD65691AA2'  where id=?";
							} else if("13279".equals(agent_no)){
								sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='47A136CF17F36659',tmk_tpk='27F0551BB23CB1D1',tmk_tak='8212D71B94CE4466'  where id=?";
							} else {
								sql = "update pos_terminal set merchant_no=?,open_status =2,START_TIME=now(),terminal_no=?,tmk='9827400AB3001200',tmk_tpk='19834AFF6F3274D5',tmk_tak='927F27A67BBDD23F'  where id=?";
							}
							Object[] parmas = { merchant_no.trim(), GenSyncNo.getInstance().getNextTerminalNo(), Integer.parseInt(terminal.get("id").toString()) };
							dao.updateByTranscation(sql, parmas, conn);
							
							// 加入机具绑定日志
							addPosTerminalLog(Integer.parseInt(terminal.get("id").toString()), "绑定商户", "2", conn);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 自动绑定
	 * @return
	 */
	public int getHLFAssembleMerchantNo(String realName){
		log.info("MerchantService getHLFAssembleMerchantNo START...");
		int hlfAssembleCount = 0;
		String hlfAssembleMerchantNoSQL = "select p.merchant_no,p.merchant_name,p.open_status,p.terminal_no,p.pos_type,p.agent_no,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount, "
										+ " f.fee_single_amount,f.ladder_fee from pos_merchant p,pos_merchant_fee f "
										+ " where p.merchant_no=f.merchant_no  and f.fee_type='RATIO' and (p.send_hlf=200 or p.send_hlf = 2002)"; //当前只支持扣率不支持封顶以及阶梯
		
		List<Map<String, Object>> list = dao.find(hlfAssembleMerchantNoSQL);//读取需要绑定的商户信息
		
		//验证是否存在需要绑定的记录
		if(list != null){
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map<String, Object>)list.get(i);
				if(map != null && null != map.get("merchant_no") && !"".equals(map.get("merchant_no").toString())){
					Connection conn = null;
					int modifyMerchantCount = 0;
					int addAcqTerminaCount = 0;
					int addAcqMerchantCount = 0;
					boolean validateMT = false;
					String sendHLF = "2001";
					try {
						conn = dao.getConnection();
						conn.setAutoCommit(false);
						String aqcMerchantSQL = "select count(a.id) as aqcCount  from acq_merchant a where a.merchant_no=?";
						Map<String, Object> aqcMap = dao.findFirst(aqcMerchantSQL, new Object[] {map.get("merchant_no").toString()});
						List<String> addSynchroTakeList = new ArrayList<String>();
						addSynchroTakeList.add(map.get("merchant_no").toString());
						String acqMerchantNo = "";
						String acqTerminalNo = "";
						String message = "收单商户已存在";
						String state = "2001";
						if(null != aqcMap && null != aqcMap.get("aqcCount") && "0".equals(aqcMap.get("aqcCount").toString())){
							HttpClient httpclient = new DefaultHttpClient();
							HttpPost httppost = new HttpPost("http://183.63.52.142/fpos/services/merchants/queryIssuedMerchantId");
							JSONObject jsonObject = new JSONObject();//  810025554137092
							jsonObject.put("yxbMerchantId", map.get("merchant_no").toString());
							StringEntity se = new StringEntity(jsonObject.toString());
							se.setContentType("application/json");
							httppost.setEntity(se);//设置请求
							HttpResponse response = httpclient.execute(httppost);
							//System.out.println(response.getStatusLine().getStatusCode());
							if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
								HttpEntity entity = response.getEntity();
								if (entity != null) {
									try {
										//String j = "{'message':'','state':'0','merchantId':'21011000012029"+i+"','terminalId':'ABC123"+i+"'}";
										//JSONObject json = JSONObject.fromObject(j);
										JSONObject json = JSONObject.fromObject(EntityUtils.toString(entity));
										System.out.println(json.toString());
										if("0".equals(json.get("state").toString()) ){
											if(null !=  json.get("merchantId") && null !=  json.get("terminalId") && !"".equals(json.get("merchantId").toString()) && !"".equals(json.get("terminalId").toString())){
												acqMerchantNo = json.get("merchantId").toString();
												acqTerminalNo = json.get("terminalId").toString();
												String acqTerminalValidateSQL = "select count(id) tc from acq_terminal where acq_merchant_no=? or acq_terminal_no = ?";
												Map<String, Object> terMap = dao.findFirst(acqTerminalValidateSQL,new Object[] {acqMerchantNo,acqTerminalNo});
												if(terMap != null && null != terMap.get("tc") && "0".equals(terMap.get("tc").toString())){
													List<String> addAcqMerchantList = new ArrayList<String>();
													addAcqMerchantList.add("halpay");//所属收单机构名称
													addAcqMerchantList.add(acqMerchantNo);//收单机构商户编号
													addAcqMerchantList.add(map.get("merchant_name").toString());//收单机构商户名称
													//addAcqMerchantList.add(acqTerminalNo);//收单机构终端编号
													addAcqMerchantList.add(map.get("merchant_no").toString());//商户编号
													addAcqMerchantList.add(map.get("agent_no").toString());//所属代理商
													addAcqMerchantList.add("5411");//MCC码
													addAcqMerchantList.add(map.get("fee_type").toString());//扣率类型
													String fee_rate = Double.parseDouble(map.get("fee_rate").toString())+""; //大商户扣率
													int fee_rate2 = (int)(Double.parseDouble(fee_rate) * 10000);
													String merchant_rate = ""; //比例
													if(fee_rate2 >= 38  && fee_rate2 <= 50){
														merchant_rate = "0.0037";
													}else if(fee_rate2 >= 70  && fee_rate2 <= 78){
														merchant_rate = "0.0067";
													}else if(fee_rate2 >= 100  && fee_rate2 <= 125){
														merchant_rate = "0.0107";
													}else{
														log.info("比例超出范围，系统跳出当前操作执行下一操作，商户编号【"+map.get("merchant_no").toString()+"】操作人="+realName);
														continue;
													}
													addAcqMerchantList.add(merchant_rate);//大商户扣率=商户扣率
													String fee_cap_amount = null;
													String fee_max_amount = null;
													if(map.get("fee_type").toString().equals("CAPPING")){
														fee_cap_amount = map.get("fee_cap_amount").toString();
														fee_max_amount = map.get("fee_max_amount").toString();
													}
													addAcqMerchantList.add(fee_cap_amount);
													addAcqMerchantList.add(fee_max_amount);
													addAcqMerchantList.add("0");//是否A类标志
													addAcqMerchantList.add("1");//是否代付
													addAcqMerchantList.add(fee_rate);//比例
													//TODO 大商户扣率问题
													String addAcqMerchantSQL = "insert into acq_merchant(acq_enname,acq_merchant_no,acq_merchant_name,merchant_no,agent_no,mcc,";
													//addAcqMerchantSQL += "fee_type,fee_rate,large_small_flag,rep_pay,create_time,merchant_rate) values(?,?,?,?,?,?,?,?,?,?,now(),?)";
													addAcqMerchantSQL += "fee_type,fee_rate,fee_cap_amount,fee_max_amount,large_small_flag,rep_pay,create_time,merchant_rate) values(?,?,?,?,?,?,?,?,?,?,?,?,now(),?)";
													addAcqMerchantCount = dao.updateByTranscation(addAcqMerchantSQL, addAcqMerchantList.toArray(), conn);
													if(addAcqMerchantCount > 0){
														List<String> addAcqTerminaList = new ArrayList<String>();
														addAcqTerminaList.add("halpay");
														addAcqTerminaList.add(acqMerchantNo);
														addAcqTerminaList.add(acqTerminalNo);
														addAcqTerminaList.add("000000");
														addAcqTerminaList.add("000000");
														addAcqTerminaList.add("1");
														String addAcqTerminaSQL = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,last_update_time,status) values(?,?,?,?,?,now(),?)";
														addAcqTerminaCount = dao.updateByTranscation(addAcqTerminaSQL, addAcqTerminaList.toArray(), conn);
														validateMT = true;
													}
													
													// 机具绑定状态商户自动绑定机具
													if("5".equals(map.get("open_status").toString())){
														openMerchantAndTerminal(map, conn);
													}
												}else{
													message = "收单机构终端编号已存在！";
													state = "3001";
												}
											}else{
												message = "好乐付返回收单机构商户编号或终端编号为空";
												state = "2002";
											}
										}else{
											sendHLF = "2002";
											message = json.get("message").toString();
											state = json.get("state").toString();
										}
									} catch (Exception e) {
										log.error("MerchantService getHLFAssembleMerchantNo 未知异常2=" + e.getMessage());
										message = "系统异常!";
										state = "2001";
									}
								}
							}else{
								message = response.getStatusLine().getReasonPhrase();
								state = response.getStatusLine().getStatusCode()+"";
							}
						}
						String modifyMerchantSQL = "update pos_merchant p set p.send_hlf=? where p.merchant_no=?";
						if(addAcqTerminaCount > 0 && addAcqMerchantCount > 0 && validateMT){
							sendHLF = "2000";
							modifyMerchantSQL = "update pos_merchant p set p.open_status='1',p.send_hlf=? where p.merchant_no=?";
						
						}else{
							if(validateMT){
								message = "创建收单商户或绑定终端编号失败!";
								state = "2003";
							}
							conn.rollback();
						}
						modifyMerchantCount = dao.updateByTranscation(modifyMerchantSQL, new Object[] {sendHLF,map.get("merchant_no").toString()}, conn);
						if(modifyMerchantCount > 0 && addAcqTerminaCount > 0 && addAcqMerchantCount > 0 && validateMT){
							state = "2000";
							message = "操作成功!";
							
						}else{
							if(validateMT){
								message = "更新商户状态为正常、收单机构终端编号绑定失败!";
								conn.rollback();
							}
						}
						
						addSynchroTakeList.add(acqMerchantNo);
						addSynchroTakeList.add(acqTerminalNo);
						addSynchroTakeList.add(realName);
						addSynchroTakeList.add(state);
						addSynchroTakeList.add(message);
						int addSynchroTakeCount = 0;
						String addSynchroTakeSQL = "insert into hlf_synchro_take(merchant_no,acq_merchant_no,acq_termina_no,create_person,response_state,response_msg) values(?,?,?,?,?,?)";
						addSynchroTakeCount = dao.updateByTranscation(addSynchroTakeSQL, addSynchroTakeList.toArray(), conn);
						if(addSynchroTakeCount > 0 && modifyMerchantCount > 0){
							conn.commit();
						}else{
							log.info("MerchantService getHLFAssembleMerchantNo 添加好乐付同步记录失败， 操作人="+realName);
							conn.rollback();
						}
					} catch (Exception e) {
						log.error("MerchantService getHLFAssembleMerchantNo 未知异常=" + e.getMessage());
						e.printStackTrace();
						return hlfAssembleCount;
					}finally{
						try {
							conn.close();
						} catch (SQLException e) {
							log.error("MerchantService getHLFAssembleMerchantNo 关闭连接异常，原因="+e.getMessage());
							e.printStackTrace();
							hlfAssembleCount=1001;
							return hlfAssembleCount;
						}
					}
				}
			}
		}
		log.info("MerchantService getHLFAssembleMerchantNo End...");
		return hlfAssembleCount;
	}
	
	
	/**
	 * 同步好乐付修改商户信息
	 * @author 王帅
	 * @see 同步好乐付成功后调用此方法根据商户编号修改商户为：否优质商户、否钱包结算、开启商户，并将该商户从当前集群中删除，且记录操作人、操作时间以及商户编号
	 * @param merchant_no 商户编号
	 * @param hlfSendCode 好乐付返回状态码
	 * @param realName 操作人
	 * @param mGroupCount 是否存在集群关系
	 * @return 返回操作影响的条数
	 * @throws SQLException
	 */
	public int updateMerchantSendHLFCode(String merchant_no, String hlfSendCode, String realName, int mGroupCount)    throws SQLException {
		log.info("MerchantService updateMerchantSendHLFCode start ...");
		int code = 0;
		if(null != merchant_no && null != hlfSendCode && null != realName && !"".equals(merchant_no) && !"".equals(hlfSendCode)  && !"".equals(realName)){
			Connection conn2 = null;
			try {
				conn2 = dao.getConnection();
				conn2.setAutoCommit(false);
				//根据商户编号修改商户为：否优质商户、否钱包结算、开启商户
				//String sql = "update pos_merchant t set t.my_settle=0,t.bag_settle='0',assemble_hlf=202,t.send_hlf=? where t.merchant_no=?";
				String sql = "update pos_merchant t set t.my_settle=0,t.send_hlf=?,t.real_flag = '1' where t.merchant_no=?";
				List<String> list = new ArrayList<String>();
				list.add(hlfSendCode);
				list.add(merchant_no);
				code = dao.updateByTranscation(sql, list.toArray(),conn2);
				if(code > 0){
					if(mGroupCount > 0){
						int deleteGroupMerchantCount = 0;
						List<String> deleteGroupMerchant = new ArrayList<String>();
						deleteGroupMerchant.add(merchant_no);
						//根据商户编号将该商户从当前集群中删除
						String modifyMerchantSQL = "delete from trans_route_group_merchant where merchant_no=?";
						deleteGroupMerchantCount = dao.updateByTranscation(modifyMerchantSQL, deleteGroupMerchant.toArray(),conn2);
						if(deleteGroupMerchantCount  == 0){
							log.info("从集群删除商户操作失败，商户编号["+merchant_no+"],操作人："+realName);
							conn2.rollback();
							code = 0;
							return code;
						}
					}
					int addSendMonitorCount = 0;
					List<String> addSendMonitor = new ArrayList<String>();
					//根据商户编号记录操作人、操作时间以及商户编号
					String addSendMonitorSQL = "insert into send_hlf_monitor(merchant_no,create_person) values(?,?)";
					addSendMonitor.add(merchant_no);
					addSendMonitor.add(realName);
					addSendMonitorCount = dao.updateByTranscation(addSendMonitorSQL, addSendMonitor.toArray(),conn2);
					if(addSendMonitorCount > 0){
						conn2.commit();
					}else{
						conn2.rollback();
						code = 0;
					}
				}else{
					conn2.rollback();
					code = 0;
				}
			} catch (Exception e) {
				log.info("Exception MerchantService updateMerchantSendHLFCode MerchantNo["+merchant_no+"]error ="+e.getMessage());
				e.printStackTrace();
				conn2.rollback();
				code = 0;
				return code;
			}finally{
				conn2.close();//关闭连接
			}
		}
		log.info("MerchantService updateMerchantSendHLFCode End");
		return code;
	}
	
	/**
	 * 根据商户编号修改同步好乐付状态
	 * @param merchant_no
	 * @param hlfSendCode
	 * @return
	 * @throws SQLException
	 */
	public int updateMerchantSendHLFCode(String merchant_no, String hlfSendCode)    throws SQLException {
		log.info("MerchantService updateMerchantSendHLFCode start ...");
		int code = 0;
		if(null != merchant_no && null != hlfSendCode && !"".equals(merchant_no) && !"".equals(hlfSendCode)){
			String sql = "update pos_merchant p set p.send_hlf=? where p.merchant_no=?"; 
			List<String> list = new ArrayList<String>();
			list.add(hlfSendCode);
			list.add(merchant_no);
			code = dao.update(sql, list.toArray());
		}
		log.info("MerchantService updateMerchantSendHLFCode End");
		return code;
	}
	
	/**
	 * 根据商户ID查询商户信息，返回当前商户的所有信息
	 * @param id 商户ID
	 * @return 
	 */
	public Map<String, Object> getMerchantById(Long id){
		log.info("MerchantService getMerchantInfoById start ...");
		String sql = "select * from pos_merchant where id="+id;
		log.info("MerchantService getMerchantInfoById End");
		return dao.findFirst(sql);
	}
	
	/**
	 * 转账导出
	 * @author 王帅
	 * @date 2015年1月22日14:31:50
	 * @see 转账详情的ID编号
	 * @param id  ID编号
	 * @return Map<String,Object> 转账详情信息
	 */
	public Page<Map<String,Object>> transferAccountsExport(Map<String, String> params, final PageRequest pageRequest){
		log.info("MerchantService transferAccountsDetail start ...");
		List<String> param = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("select i.order_no,i.merchant_no,i.amount,a.agent_no,a.agent_name,i.payfor_account_no,i.id_card_no,i.payee_account_no,i.fee,i.payfor_account_name," +
				"i.payee_account_name,i.order_status,i.create_time from increment_transfer_acc i,pos_merchant p,agent_info a where " +
				"i.merchant_no=p.merchant_no and p.agent_no=a.agent_no");
		//转账人卡号
		if(params.get("payfor_account_no") != null && !"".equals(params.get("payfor_account_no").toString())){
			sql.append(" and i.payfor_account_no=?");
			param.add(params.get("payfor_account_no").toString());
		}
		
		//转账状态
		if(params.get("order_status") != null && !"".equals(params.get("order_status").toString())){
			sql.append(" and i.order_status=?");
			param.add(params.get("order_status").toString());
		}
		
		//交易商户编号
		if(params.get("merchant_no") != null && !"".equals(params.get("merchant_no").toString())){
			sql.append(" and i.merchant_no=?");
			param.add(params.get("merchant_no").toString());
		}
		
		//转账起始时间
		if(params.get("start_time") != null && !"".equals(params.get("start_time").toString())){
			sql.append(" and i.create_time >=?");
			param.add(params.get("start_time").toString());
		}
		
		//转账截止时间
		if(params.get("end_time") != null && !"".equals(params.get("end_time").toString())){
			sql.append(" and i.create_time <=?");
			param.add(params.get("end_time").toString());
		}
		
		//代理商编号/名称
		if(params.get("agent_non") != null && !"".equals(params.get("agent_non").toString())){
			sql.append(" and (a.agent_no=? or a.agent_name=?)");
			param.add(params.get("agent_non").toString());
			param.add(params.get("agent_non").toString());
		}
		//根据转账时间倒序
		sql.append(" order by i.create_time desc"); 
		log.info("MerchantService transferAccountsDetail End");
		return dao.find(sql.toString(), param.toArray(), pageRequest);
	}
	
	/**
	 * 转账详情
	 * @author 王帅
	 * @date 2015年1月22日14:31:50
	 * @see 转账详情的ID编号
	 * @param id  ID编号
	 * @return Map<String,Object> 转账详情信息
	 */
	public Map<String,Object> transferAccountsDetail(String id){
		log.info("MerchantService transferAccountsDetail start ...");
		Map<String,Object> param = new HashMap<String, Object>();
		//转账人卡号
		if(id != null && !"".equals(id)){
			String sql = "select i.order_no,i.merchant_no,i.amount,a.agent_no,a.agent_name,i.payfor_account_no,i.id_card_no,i.payee_account_no,i.fee,i.payfor_account_name," +
					"i.payee_account_name,i.order_status,i.create_time from increment_transfer_acc i,pos_merchant p,agent_info a where " +
					"i.merchant_no=p.merchant_no and p.agent_no=a.agent_no and i.id=?";
			List<String> list = new ArrayList<String>();
			list.add(id);
			param = dao.findFirst(sql, list.toArray());
			log.info("MerchantService transferAccountsDetail SUCESS");
		}
		log.info("MerchantService transferAccountsDetail End");
		return param;
	}
	
	/**
	 * 转账查询
	 * @author 王帅
	 * @date 2015年1月21日15:39:50
	 * @see 转账查询需传入转账查询组合条件与分页信息
	 * @param params  查询条件信息
	 * @param pageRequest 分页信息
	 * @return Page<Map<String,Object>> 带分页信息的结果集
	 */
	public Page<Map<String,Object>> transferAccountsQuery(Map<String, String> params, final PageRequest pageRequest){
		log.info("MerchantService transferAccountsQuery start ...");
		StringBuffer sql = new StringBuffer("select i.id,i.payfor_account_no,i.payee_account_no,a.agent_no,i.merchant_no,i.amount,i.order_status,i.create_time");
		sql.append(" from increment_transfer_acc i,agent_info a,pos_merchant p where  i.merchant_no=p.merchant_no and p.agent_no=a.agent_no ");
		List<String> param = new ArrayList<String>();
		//转账人卡号
		if(params.get("payfor_account_no") != null && !"".equals(params.get("payfor_account_no").toString())){
			sql.append(" and i.payfor_account_no=?");
			param.add(params.get("payfor_account_no").toString());
		}
		
		//转账状态
		if(params.get("order_status") != null && !"".equals(params.get("order_status").toString())){
			sql.append(" and i.order_status=?");
			param.add(params.get("order_status").toString());
		}
		
		//交易商户编号
		if(params.get("merchant_no") != null && !"".equals(params.get("merchant_no").toString())){
			sql.append(" and i.merchant_no=?");
			param.add(params.get("merchant_no").toString());
		}
		
		//转账起始时间
		if(params.get("start_time") != null && !"".equals(params.get("start_time").toString())){
			sql.append(" and i.create_time >=?");
			param.add(params.get("start_time").toString());
		}
		
		//转账截止时间
		if(params.get("end_time") != null && !"".equals(params.get("end_time").toString())){
			sql.append(" and i.create_time <=?");
			param.add(params.get("end_time").toString());
		}
		
		//代理商编号/名称
		if(params.get("agent_non") != null && !"".equals(params.get("agent_non").toString())){
			sql.append(" and (a.agent_no=? or a.agent_name=?)");
			param.add(params.get("agent_non").toString());
			param.add(params.get("agent_non").toString());
		}
		//根据转账时间倒序
		sql.append(" order by i.create_time desc"); 
		
		log.info("MerchantService transferAccountsQuery End");
		return dao.find(sql.toString(), param.toArray(),pageRequest);
	}
	
	/**
	 * 商户钱包就绪
	 * @param app_no 客户端类型编号
	 * @param moblie_no  钱包账户手机号
	 * @return 受影响的行数
	 * @throws SQLException 异常信息
	 */
	public int merchantWalletReady(String app_no, String moblie_no)   throws SQLException {
		log.info("MerchantService merchantWalletReady start...");
		int updateCount = 0;
		if(app_no != null && moblie_no != null){
			if(!"".equals(moblie_no) && !"".equals(app_no)){
				List<String> params = new ArrayList<String>();
				String sql = "update pos_merchant p set p.bag_prepare=1 where p.mobile_username=? and p.app_no=? and  p.bag_prepare=0";
				params.add(moblie_no);
				params.add(app_no);
				updateCount = dao.update(sql, params.toArray());
				log.info("MerchantService merchantWalletReady  SUCESS = " + updateCount);
			}
		}
		log.info("MerchantService merchantWalletReady End");
		return updateCount;
	}
	
	/**
	 * 
	 * 根据客户端类型、登陆手机号查询商户信息
	 * @author 王帅
	 * @data 2015年1月13日11:18:55
	 * @see 钱包查询商户信息用到的方法
	 * @param app_no 客户端类型
	 * @param moblie_no 登陆手机号
	 * @return list<map<String, object>>
	 */
	public List<Map<String, Object>> getMerchantInfoByAppNoAndMobile(String app_no, String moblie_no){
		log.info("MerchantService getMerchantInfoByAppNoAndMobile start ...");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(app_no != null && moblie_no != null){
			if(!"".equals(app_no) && !"".equals(moblie_no)){
				List<String> param = new ArrayList<String>();
				param.add(app_no);
				param.add(moblie_no);
				String sql = "select p.*,a.agent_name from pos_merchant p,agent_info a where p.app_no=? and p.mobile_username=? and a.agent_no = p.agent_no";
				list = dao.find(sql, param.toArray());
				log.info("MerchantService getMerchantInfoByAppNoAndMobile  SUCESS  size="+list.size());
			}
		}
		log.info("MerchantService getMerchantInfoByAppNoAndMobile End");
		return list;
	}
	
	
	/**
	 * 商户复审通过/失败
	 * @author 王帅
	 * @date 2014年11月24日11:49:26
	 * @see 根据商户ID以及商户状态为等待复审 的商户复审通过
	 * @param merchant_id 商户ID编号
	 * @param open_status 要修改的商户状态
	 * @return 返回受影响的行数
	 * @throws SQLException
	 */
	public int merchantRepeatSF(String merchant_id, String open_status)  throws SQLException {
		log.info("MerchantService merchantRepeatSF start...");
		int repeatCount = 0;
		if(merchant_id != null && open_status != null && !"".equals(merchant_id) && !"".equals(open_status)){
			List<String> list = new ArrayList<String>();
			String sql  = "update pos_merchant p set p.open_status=? where p.id=? and p.open_status=8";
			list.add(open_status);
			list.add(merchant_id);
			repeatCount = dao.update(sql, list.toArray());
			log.info("MerchantService merchantRepeatSF  Sucess = " + repeatCount);
		}
		log.info("MerchantService merchantRepeatSF end");
		return repeatCount;
	}
	
	
	/**
	 * 商户注册信息同步记录存库-龙宝
	 * @author swang
	 * @param map  同步的信息   类型   响应码等消息
	 * @param notify_type  同步类型  1.龙宝
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int merchantNotifyAdd(Map<String, Object> map,String notify_type) throws SQLException {
		int count = 0;
		if(null != map){
			String merchant_id = map.get("merchant_id").toString();
			String merchant_login_name = map.get("name").toString();  //注册人的姓名
			String phone = map.get("phone").toString(); //注册人的手机号码
			String email = map.get("email").toString(); //注册人的邮箱
			String id_card = map.get("idcard").toString(); //注册人的身份证号码
			String card_number = map.get("card_number").toString();  //注册人的银行卡号
			String bank_name = map.get("bank_name").toString();  //开户行名称，银行的中文名  
			String response_code = map.get("response_code").toString(); //响应码
			String response_msg = map.get("response_msg").toString();   //响应消息
			//MD5后字符串   hmac=name+phone+email+idcard+card_number
			String md5_key = map.get("hmac").toString(); 
			String sql = "insert into longbao_merchant_notify(merchant_id,merchant_login_name,phone,email,id_card,card_number,bank_name,response_code,response_msg,notify_type,md5_key,create_time) values(?,?,?,?,?,?,?,?,?,?,?,now())";
			Object[] params = {merchant_id,merchant_login_name, phone, email, id_card, card_number, bank_name,response_code,response_msg,notify_type,md5_key };
			count = dao.update(sql, params);
		}
		return count;
	}
	
	/**
	 * 根据审核人以及审核时间查询审核记录
	 * @author swang
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> auditingQueryDetailPage(Map<String, String> params, final PageRequest pageRequest){
		List<String> list = new ArrayList<String>();
		String sql = "select ai.agent_name,p.* from agent_info ai inner join (" +
				"select pm.merchant_no,pm.create_time as pct,pm.merchant_short_name,el.open_status,pm.agent_no,el.create_time," +
				"el.examination_opinions from pos_merchant pm inner join (select el.merchant_no,el.open_status,el.create_time,el.examination_opinions from examinations_log el inner join boss_user bu on el.operator = bu.user_name  where  1=1";
		if(null != params){
			if(!StringUtil.isEmpty(params.get("detail_operator"))){ //审核人名称
				String operator = params.get("detail_operator").toString();
				sql+=" and bu.real_name=?";
				list.add(operator);
			}
			
			if(!StringUtil.isEmpty(params.get("startQDTime"))){ //审核开始时间
				String startQDTime = params.get("startQDTime").toString();
				sql +=" and el.create_time >= ?";
				list.add(startQDTime);
			}
			
			if(!StringUtil.isEmpty(params.get("endQDTime"))){ //审核截止时间
				String endQDTime = params.get("endQDTime").toString();
				sql +=" and el.create_time <= ?";
				list.add(endQDTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detailStatus"))){ //审核截止时间
				String open_status = params.get("detailStatus").toString();
				sql +=" and el.open_status = ?";
				list.add(open_status);
			}
		}
		sql +=") el  where pm.merchant_no=el.merchant_no";
		
		if(null != params && !StringUtil.isEmpty(params.get("pos_type"))){// 设备类型
			String pos_type = params.get("pos_type").toString();
			sql+=" and pos_type = ? ";
			list.add(pos_type);
		}
		
		sql += ") p where ai.agent_no=p.agent_no";
		
		return  dao.find(sql.toString(), list.toArray(), pageRequest);
	}
	
	/**
	 * 商户审核统计
	 * @author swang
	 * @param params 查询条件
	 * @param pageRequest 页码
	 * @return 返回商户审核统计
	 */
	public Page<Map<String, Object>> auditingQueryPage(Map<String, String> params, final PageRequest pageRequest){
		/*StringBuffer sql = new StringBuffer("select * from (select count(*) as ct,e.operator,bu.real_name,e.create_time from boss_user bu");
		sql.append(" inner join (select el.* from examinations_log el ");
		List<String> list = new ArrayList<String>();
		
		if(!StringUtil.isEmpty(params.get("pos_type")) && !"-1".equals(params.get("pos_type"))){ // 设备类型
			String pos_type = params.get("pos_type").toString();
			sql.append(" inner join pos_merchant pm on pm.merchant_no = el.merchant_no where pm.pos_type = ?");
			list.add(pos_type);
		}else{
			sql.append(" where 1=1 ");
		}
		
		if(!StringUtil.isEmpty(params.get("open_status"))){ //审核状态
			String open_status = params.get("open_status").toString();
			sql.append(" and el.open_status = ?");
			list.add(open_status);
		}
		
		if(!StringUtil.isEmpty(params.get("start_time"))){ //审核开始时间
			String start_time = params.get("start_time").toString();
			sql.append(" and el.create_time >= ?");
			list.add(start_time);
		}
		
		if(!StringUtil.isEmpty(params.get("end_time"))){ //审核截止时间
			String end_time = params.get("end_time").toString();
			sql.append(" and el.create_time <= ?");
			list.add(end_time);
		}
		
		sql.append(" order by el.create_time desc) e where bu.user_name=e.operator");
		if(!StringUtil.isEmpty(params.get("real_name"))){ //审核人名称
			String real_name = params.get("real_name").toString();
			sql.append(" and real_name=?");
			list.add(real_name);
		}
		sql.append(" group by e.operator order by e.create_time desc) t");
		return  dao.find(sql.toString(), list.toArray(), pageRequest);*/
		
		StringBuffer sql = new StringBuffer();
		List<String> list = new ArrayList<String>();
		sql.append("select t1.*, bu.real_name, pt.pos_type_name from ( ");
		sql.append("	select el.operator, pm.pos_type, count(el.id) checked, max(el.create_time) last_check_time ");
		sql.append("	from examinations_log el ");
		sql.append("	inner join pos_merchant pm on el.merchant_no = pm.merchant_no ");
		sql.append("	where 1 = 1 ");
		
		String start_time = params.get("start_time");
		if(!StringUtil.isEmpty(start_time)){ //审核开始时间
			sql.append(" and el.create_time >= ? ");
			list.add(start_time);
		}
		
		String end_time = params.get("end_time");
		if(!StringUtil.isEmpty(end_time)){ //审核截止时间
			sql.append(" and el.create_time <= ? ");
			list.add(end_time);
		}
		
		String open_status = params.get("open_status");
		if(!StringUtil.isEmpty(open_status)){ //审核状态
			sql.append(" and el.open_status = ? ");
			list.add(open_status);
		}
		
		String pos_type = params.get("pos_type");
		if(!StringUtil.isEmpty(pos_type) && !"-1".equals(pos_type)){ // 设备类型
			sql.append(" and pm.pos_type = ? ");
			list.add(pos_type);
		}

		sql.append("	group by el.operator, pm.pos_type ) t1 ");
		sql.append("	inner join boss_user bu on t1.operator = bu.user_name ");
		sql.append("	inner join (select pt1.pos_type_name,pt1.pos_type from pos_type pt1 group by pt1.pos_type) pt on t1.pos_type = pt.pos_type ");
		sql.append("	where 1 = 1 ");

		String real_name = params.get("real_name");
		if(!StringUtil.isEmpty(real_name)){ //审核人名称
			sql.append(" and bu.real_name = ? ");
			list.add(real_name);
		}

		return dao.find(sql.toString(), list.toArray(), pageRequest);
	}
	
	public List<Map<String, Object>> auditingRemainQuery(){
		String sql = "select pm.checker,pm.pos_type,count(pm.id) remain from pos_merchant pm where pm.open_status = '2' group by pm.checker,pm.pos_type";
		return dao.find(sql);
	}
	
	/**
	 * 审核信息导出
	 * */
	public Page<Map<String, Object>> merAuditingExportExcel(Map<String, String> params, final PageRequest pageRequest){
		List<String> list = new ArrayList<String>();
		String sql = "select ai.agent_name,ai.sale_name,p.* from agent_info ai inner join (" +
				"select pm.merchant_no,pm.create_time as pct,pm.merchant_short_name,pm.pos_type,pm.terminal_count,pm.terminal_no,el.open_status,pm.agent_no,el.create_time," +
				"el.examination_opinions,el.real_name from pos_merchant pm inner join (" +
				"select el1.merchant_no,el1.open_status,el1.create_time,el1.examination_opinions,bu.real_name AS real_name from examinations_log el1 inner join boss_user bu on bu.user_name=el1.operator where  1=1";
		if(null != params){
			if(!StringUtil.isEmpty(params.get("detail_operator"))){ //审核人名称
				String operator = params.get("detail_operator").toString();
				sql+=" and el1.operator=?";
				list.add(operator);
			}

			if(!StringUtil.isEmpty(params.get("real_name"))){ //审核人名称
				String operator = params.get("real_name").toString();
				sql+=" and bu.real_name=?";
				list.add(operator);
			}
			
			if(!StringUtil.isEmpty(params.get("startQDTime"))){ //审核开始时间
				String startQDTime = params.get("startQDTime").toString();
				sql +=" and el1.create_time >= ?";
				list.add(startQDTime);
			}
			
			if(!StringUtil.isEmpty(params.get("endQDTime"))){ //审核截止时间
				String endQDTime = params.get("endQDTime").toString();
				sql +=" and el1.create_time <= ?";
				list.add(endQDTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detailStatus"))){ //审核截止时间
				String open_status = params.get("detailStatus").toString();
				sql +=" and el1.open_status = ?";
				list.add(open_status);
			}
		}
		sql +=") el  where pm.merchant_no=el.merchant_no";
		
		if(null != params && !StringUtil.isEmpty(params.get("pos_type")) && !"-1".equals(params.get("pos_type"))){// 设备类型
			String pos_type = params.get("pos_type").toString();
			sql+=" and pm.pos_type = ? ";
			list.add(pos_type);
		}
		
		sql += ") p where ai.agent_no=p.agent_no";
		
		return  dao.find(sql.toString(), list.toArray(), pageRequest);
	}
	
	/**
	 * 根据商户编号获取非正常状态下对应的商户信息
	 * @param merchant_no 商户编号
	 * @return 返回对应的商户信息
	 */
	public Map<String, Object>  getMerchantInfoByMerchantNo(String merchant_no){
		List<String> list = new ArrayList<String>();
		String sql  = "select * from pos_merchant p where p.open_status != 1 and p.merchant_no=? limit 1;";
		list.add(merchant_no);
		return dao.findFirst(sql, list.toArray());
	}

	public Map<String, Object> countMerchantNum(String date) {
		String sql = "select count(*) num from pos_merchant where DATE_FORMAT(create_time,'%Y-%m-%d')=?";
		return dao.findFirst(sql, date);
	}

	// 根据Id查询商户信息
	public Map<String, Object> queryMerchantInfoById(Long id) {
		String sql = "select m.*,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee,r.single_max_amount,r.ed_max_amount, r.ed_card_max_amount, r.ed_card_max_items, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join pos_merchant_fee f on m.merchant_no = f.merchant_no   left join pos_merchant_trans_rule r on m.merchant_no = r.merchant_no "
				+ " left join agent_info a on m.agent_no = a.agent_no where m.id =" + id;
		return dao.findFirst(sql);
	}
	
	/**
	 * 根据商户编号获取商户信息
	 * @param merchant_no 商户编号
	 * @return 返回对应的商户信息
	 */
	public Map<String, Object>  getMerchantInfoByMNo(String merchant_no){
		List<String> list = new ArrayList<String>();
		String sql  = "select * from pos_merchant p where  p.merchant_no=? limit 1 ";
		list.add(merchant_no);
		return dao.findFirst(sql, list.toArray());
	}
	
	public int merchantRepeatLog(Map<String,String> map)  throws SQLException {
		String sql = "insert into merchant_repeat_log(merchant_no,create_person,merchant_stastu,return_status,repeat_msg) values(?,?,?,?,?)";
		List<String> list = new ArrayList<String>();
		list.add(map.get("merchant_no"));
		list.add(map.get("create_person"));
		list.add(map.get("merchant_stastu"));
		list.add(map.get("return_status"));
		list.add(map.get("repeat_msg"));
		return dao.update(sql, list.toArray());
	}
	
		// 根据Id查询商户信息
		public Map<String, Object> getMerchantInfoById(Long id) {
			String sql = "select * from pos_merchant p where p.id=" + id;
			return dao.findFirst(sql);
		}

	// 根据id获取商户
	public Map<String, Object> queryMerchantById(Long id) {
		String sql = "SELECT a.id as agentid ,a.agent_name, m.* from pos_merchant m , agent_info a where m.agent_no=a.agent_no and m.id="+ id;
		return dao.findFirst(sql);
	}

	// 根据商户号查找商户信息
	public Map<String, Object> queryMerchantMsgByNo(String merchantNo) {
		String sql = "select m.*,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,a.id as agentid from pos_merchant m left join agent_info a on m.agent_no = a.agent_no where m.merchant_no ='" + merchantNo + "'";
		return dao.findFirst(sql);
	}

	// 根据id更新商户信息
	public void updatePosMerchant(String merchantNo, Map<String, String> params) throws SQLException {

		String terminal_count = params.get("terminal_count");
		if (terminal_count == null || "".equals(terminal_count)) {
			terminal_count = "0";
		}
		
		//获得所属代理商编号
		String belong_to_agent = params.get("belong_to_agent");
	    Map<String, Object> nodeMap=findAgentNoId(belong_to_agent);
		//取到所属代理商的self_node，将self_node保存到parent_node里面
		String self_node=(String)nodeMap.get("self_node");
		
		String bag_settle=params.get("bag_settle");
		String trans_cancel=params.get("trans_cancel");
		
		if("".equals(bag_settle)||bag_settle==null){
			bag_settle="0";
		}
		
		if("1".equals(bag_settle)){
			trans_cancel="0";
		}
		
		String trans_time_start = "1970-10-01 " + params.get("trans_time_start");
		String trans_time_end = "1970-10-01 " + params.get("trans_time_end");

		String sql = "update pos_merchant set province=?,city=?,sale_address=?,mobile_username=?," + " sale_name=?,pos_type=?,open_status=?,settle_cycle=?,mcc=?,phone=?,link_name=? , examination_opinions=?,real_flag =?," + "id_card_no =?,terminal_count =?  ,trans_time_start =?,trans_time_end =? ,my_settle=?,clear_card_no=? ,trans_cancel=?,pay_method=?,parent_node=?,agent_no=?,belong_to_agent=?,bag_settle=? " + " where    merchant_no='" + merchantNo + "'";
		Object[] term = { params.get("province"), params.get("city"), params.get("sale_address"), params.get("mobile_username"), params.get("sale_name"),params.get("pos_type"), params.get("open_status"), params.get("settle_cycle"), params.get("mcc"), params.get("phone"), params.get("link_name"), params.get("examination_opinions"), params.get("real_flag"), params.get("id_card_no"), terminal_count, trans_time_start,
				trans_time_end, params.get("my_settle"), params.get("clear_card_no"), trans_cancel,params.get("pay_method"),self_node,params.get("agent_no"),belong_to_agent,params.get("bag_settle")};
		System.out.println("merchantNo---->"+merchantNo+"---------my_settle--->"+params.get("my_settle"));
		dao.update(sql, term);

		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchantNo);
			// e.printStackTrace();
		}
	}

	// 根据id更新商户开通状态
	public void updatePosMerchantOpenStatus(String merchantNo, Map<String, String> params) throws SQLException {
		
		
		String sql = "update pos_merchant set open_status=? , examination_opinions=? where merchant_no='" + merchantNo + "'";
		Object[] term = { params.get("open_status"), params.get("examination_opinions") };
		dao.update(sql, term);

		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchantNo);
			// e.printStackTrace();
		}
	}

	// 根据商户号更新商户费率信息
	public void updatePosMerchantFee(String merchantNo, Map<String, String> params) throws SQLException {

		List<Object> list = new ArrayList<Object>();
		StringBuffer sf = new StringBuffer();
		String feeType = params.get("fee_type");
		list.add(feeType);
		if (params != null) {
			if ("RATIO".equals(feeType)) {
				String feeRate = params.get("fee_rate");
				sf.append(" ,fee_rate = ? ");
				list.add(feeRate == null ? null : new BigDecimal(feeRate).movePointLeft(2));
				sf.append(" ,fee_cap_amount = null ");
				sf.append(" ,fee_max_amount = null");
				sf.append(" ,ladder_fee = null");

			} else if ("CAPPING".equals(feeType)) {
				String feeRate = params.get("fee_rate");
				String feeMaxAmount = params.get("fee_max_amount");
				sf.append(" ,fee_rate=? ");
				list.add(feeRate == null ? null : new BigDecimal(feeRate).movePointLeft(2));

				// String feeCapAmount = params.get("fee_cap_amount");
				String feeCapAmount = new BigDecimal(feeMaxAmount).divide(new BigDecimal(feeRate).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP).toString();
				sf.append(" ,fee_cap_amount=? ");
				list.add(feeCapAmount);

				sf.append(" ,fee_max_amount=? ");
				list.add(feeMaxAmount);
				sf.append(" ,ladder_fee = null");

			} else if ("LADDER".equals(feeType)) {
				sf.append(" ,fee_rate = null");
				sf.append(" ,fee_cap_amount = null ");
				sf.append(" ,fee_max_amount = null");

				String ladder_min = params.get("ladder_min");
				String ladder_value = params.get("ladder_value");
				String ladder_max = params.get("ladder_max");

				ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
				ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

				String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;
				sf.append(" ,ladder_fee=? ");
				list.add(ladder_fee);

			}
		}
		String sql = "update pos_merchant_fee set fee_type =? " + sf.toString() + " where merchant_no='" + merchantNo + "'";
		dao.update(sql, list.toArray());

		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage());
			// e.printStackTrace();
		}

	}

	// 根据商户号更新商户交易限额信息
	public void updatePosMerchantTransRule(String merchantNo, Map<String, String> params) throws SQLException {
		String sql = "update pos_merchant_trans_rule set single_max_amount=?,ed_max_amount=?,ed_card_max_items=?,ed_card_max_amount=? where merchant_no='" + merchantNo + "'";
		Object[] term = { params.get("single_max_amount"), params.get("ed_max_amount"), params.get("ed_card_max_items"), params.get("ed_card_max_amount") };
		dao.update(sql, term);

		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchantNo);
			// e.printStackTrace();
		}
	}

	// 根据商户号查询商户及费率和代理商信息
	public Map<String, Object> queryMerchantInfoByNo(String merchantNo) {

		String sql = "select a.id,a.parent_id, a.agent_no,a.agent_name, m.merchant_name, m.merchant_short_name, CONCAT(m.province,m.city,m.address) as address2,m.real_flag,f.fee_type, f.fee_rate, " + "f.fee_cap_amount, f.fee_max_amount, f.fee_single_amount,f.ladder_fee from pos_merchant m , pos_merchant_fee f , " + "agent_info a where m.merchant_no = f.merchant_no and m.agent_no = a.agent_no  and f.merchant_no=?";
		/*String sql = "select a.id,a.parent_id, a.agent_no,a.agent_name, m.merchant_name, m.merchant_short_name, m.real_flag,f.fee_type, f.fee_rate, " + "f.fee_cap_amount, f.fee_max_amount, f.fee_single_amount,f.ladder_fee from pos_merchant m , pos_merchant_fee f , " + "agent_info a where m.merchant_no = f.merchant_no and m.belong_to_agent = a.agent_no  and f.merchant_no=?";*/
		return dao.findFirst(sql, merchantNo);
	}
	
		/**
		 * 商户复核查询
		 * @param params
		 * @param pageRequest
		 * @return
		 */
		public Page<Map<String, Object>> queryCheckRepeatMerchant(Map<String, String> params, final PageRequest pageRequest) {
			List<Object> list = new ArrayList<Object>();
			StringBuffer sf = new StringBuffer();
			if (params != null) {

				// 商户名称查询
				if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = "%" + params.get("merchant") + "%";
					sf.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
					list.add(merchant_name);
					list.add(merchant_name);
					list.add(merchant_name);
				}

				// 代理名称
				if (StringUtils.isNotEmpty(params.get("agent_no"))) {
					if (!params.get("agent_no").equals("-1")) {
						String agent_no = params.get("agent_no");
						sf.append(" and a.agent_no = ? ");
						list.add(agent_no);
					}
				}
				
				// 进件类型
				if (StringUtils.isNotEmpty(params.get("add_type"))) {
					if (!params.get("add_type").equals("-1")) {
						String add_type = params.get("add_type");
						sf.append(" and m.add_type = ? ");
						list.add(add_type);
					}
				}
				
				// 设备类型
				if (StringUtils.isNotEmpty(params.get("pos_type"))) {
					if (!params.get("pos_type").equals("-1")) {
						String pos_type = params.get("pos_type");
						sf.append(" and m.pos_type = ? ");
						list.add(pos_type);
					}
				}
				
				// 审核人
				if (StringUtils.isNotEmpty(params.get("param_value"))) {
					if (!params.get("param_value").equals("-1")) {
						String param_value = params.get("param_value");
						sf.append(" and m.checker = ? ");
						list.add(param_value);
					}
				}

				if (StringUtils.isNotEmpty(params.get("create_time_begin"))) {
					String createTimeBegin = params.get("create_time_begin");
					sf.append(" and m.create_time >=? ");
					list.add(createTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("create_time_end"))) {
					String createTimeEnd = params.get("create_time_end");
					sf.append(" and m.create_time <=? ");
					list.add(createTimeEnd);
				}
			}
			String sql = "select m.*,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name ,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee " + "from pos_merchant m , agent_info a,pos_merchant_fee f" + " where m.agent_no = a.agent_no and m.merchant_no = f.merchant_no and m.open_status =8 " + sf.toString() + " order by m.id ";
			return dao.find(sql, list.toArray(), pageRequest);
		}

	
	// 检索待审核的商户信息
	public Page<Map<String, Object>> queryCheckMerchant(Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sf = new StringBuffer();
		if (params != null) {

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
			String merchant_name = "%" + params.get("merchant") + "%";
				sf.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
				/*String merchant_name =params.get("merchant");
				sf.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");*/
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			// 代理名称
			if (StringUtils.isNotEmpty(params.get("agent_no"))) {
				if (!params.get("agent_no").equals("-1")) {
					String agent_no = params.get("agent_no");
					sf.append(" and a.agent_no = ? ");
					list.add(agent_no);
				}
			}
			
			// 进件类型
			if (StringUtils.isNotEmpty(params.get("add_type"))) {
				if (!params.get("add_type").equals("-1")) {
					String add_type = params.get("add_type");
					sf.append(" and m.add_type = ? ");
					list.add(add_type);
				}
			}
			
			// 设备类型
			if (StringUtils.isNotEmpty(params.get("pos_type"))) {
				if (!params.get("pos_type").equals("-1")) {
					String pos_type = params.get("pos_type");
					sf.append(" and m.pos_type = ? ");
					list.add(pos_type);
				}
			}
			
			// 审核人
			if (StringUtils.isNotEmpty(params.get("param_value"))) {
				if (!params.get("param_value").equals("-1")) {
					String param_value = params.get("param_value");
					sf.append(" and m.checker = ? ");
					list.add(param_value);
				}
			}

			if (StringUtils.isNotEmpty(params.get("create_time_begin"))) {
				String createTimeBegin = params.get("create_time_begin");
				sf.append(" and m.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("create_time_end"))) {
				String createTimeEnd = params.get("create_time_end");
				sf.append(" and m.create_time <=? ");
				list.add(createTimeEnd);
			}
			
			
		}

		String sql = "select m.*,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name ,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee " + "from pos_merchant m , agent_info a,pos_merchant_fee f" + " where m.agent_no = a.agent_no and m.merchant_no = f.merchant_no and m.open_status =2 " + sf.toString() + " order by m.id ";
        //System.out.println("\n"+sql+"\n");
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 查询商户被审核的信息
	public Page<Map<String, Object>> merchantShenheListByMerchantno(Map<String, String> params, final PageRequest pageRequest) {

		List<Object> list = new ArrayList<Object>();
		String merchant_no = params.get("merchant_no");

		// StringBuffer sb = new StringBuffer();
		String sql = "select  id, merchant_no, open_status, examination_opinions, operator, create_time " + "from   examinations_log " + " where merchant_no=?";
		list.add(merchant_no);
		return dao.find(sql, list.toArray(), pageRequest);
	}

   
	//查询未交易商户数据
	public Page<Map<String,Object>>  getMerNotransList(Map<String, String> params,final PageRequest pageRequest){
		List<Object> list=new ArrayList<Object>();
		StringBuffer sb=new StringBuffer();
		if (null !=params) {
    		//商户名称
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
				String agent_no =params.get("agentNo");
				sb.append("(m.agent_no=?) where not exists" +
							"(select 1 from trans_info i where i.agent_no=? and m.merchant_no = i.merchant_no) and a.agent_no=?");				
				list.add(agent_no);
				list.add(agent_no);
				list.add(agent_no);
				}else {									
					sb.append("(m.agent_no=a.agent_no) where not exists" +
							"(select 1 from trans_info i where i.agent_no=m.agent_no and m.merchant_no = i.merchant_no)");
				}
			}else {	
				sb.append("(m.agent_no=a.agent_no) where not exists" +
			            	"(select 1 from trans_info i where i.agent_no=m.agent_no and m.merchant_no = i.merchant_no)");
			}	
			//开始时间
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and m.create_time >=? ");
				list.add(createTimeBegin);
			}					
			//结束时间
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and m.create_time <=? ");
				list.add(createTimeEnd);
			}	
			}				
		String sql ="select m.id,m.open_status,m.merchant_no,m.merchant_short_name,m.create_time,a.agent_name from pos_merchant m inner join agent_info a on"
			+sb.toString()+"order by m.id desc";
		return dao.find(sql, list.toArray(), pageRequest);   
	}
	
	/**
	 * 商户查询 内敛版
	 * @author 王帅
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getMerListN(Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = "%" + params.get("merchant") + "%";
				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			
			if(StringUtils.isNotEmpty(params.get("checker")) && !"-1".equals(params.get("checker"))){
				String checker =params.get("checker").trim();
				sb.append(" and m.checker = ? ");
				list.add(checker);		
			}

			//手机号码
			if (StringUtils.isNotEmpty(params.get("phone"))) {
				String phone =params.get("phone").trim();
				sb.append(" and m.mobile_username = ? ");
				list.add(phone);		
			}
			
			//持卡人姓名
			if (StringUtils.isNotEmpty(params.get("account_name"))) {
				String account_name =params.get("account_name");
				sb.append(" and m.account_name = ? ");
				list.add(account_name);		
			}
			
			//省份
			if (StringUtils.isNotEmpty(params.get("province"))) {
				String province =params.get("province");
				sb.append(" and m.province = ? ");
				list.add(province);		
			}
		
			//市区
			if (StringUtils.isNotEmpty(params.get("city"))) {
				String city =params.get("city");
				sb.append(" and m.city = ? ");
				list.add(city);		
			}
			
			
			// 代理名称
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentCode = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentCode);
				}
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 开通状态
			if (StringUtils.isNotEmpty(params.get("openStatus"))) {
				String openStatus = params.get("openStatus");
				if (!"-1".equals(openStatus)) {
					sb.append(" and m.open_status =? ");
					list.add(openStatus);
				}
			}
			
			// 设备类型
			if (StringUtils.isNotEmpty(params.get("pos_type"))) {
				if (!params.get("pos_type").equals("-1")) {
					String pos_type = params.get("pos_type");
					sb.append(" and m.pos_type = ? ");
					list.add(pos_type);
				}
			}

			// sale-name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");

				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);

				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);

			}

			// 商户所属的“销售”只能是当前的登录人员。
			// belongTo_sale_name
			// if (StringUtils.isNotEmpty(params.get("belongTo_sale_name"))) {
			// String belongTo_sale_name = params.get("belongTo_sale_name");
			// sb.append("  and  m.sale_name =?  ");
			// list.add(belongTo_sale_name);
			// }

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and m.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and m.create_time <=? ");
				list.add(createTimeEnd);
			}
			
			
			// 开通状态
			if (StringUtils.isNotEmpty(params.get("open_type"))) {
				String open_type = params.get("open_type");
				if (!"-1".equals(open_type)) {
					sb.append(" and m.open_type = ? ");
					list.add(open_type);
				}
			}

		}
		String sql = "select m.* , CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join agent_info a  on  m.agent_no = a.agent_no where 1=1 " + sb.toString() +
				" and length(m.merchant_name)>=18 and  length(a.agent_name)>=18 and length(m.merchant_short_name) >= 18 order by m.id desc";

		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	
	// 查询商户数据
	public Page<Map<String, Object>> getMerList(Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = "%" + params.get("merchant") + "%";
				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			
			if(StringUtils.isNotEmpty(params.get("checker")) && !"-1".equals(params.get("checker"))){
				String checker =params.get("checker").trim();
				sb.append(" and m.checker = ? ");
				list.add(checker);		
			}

			//手机号码
			if (StringUtils.isNotEmpty(params.get("phone"))) {
				String phone =params.get("phone").trim();
				sb.append(" and m.mobile_username = ? ");
				list.add(phone);		
			}
			
			//持卡人姓名
			if (StringUtils.isNotEmpty(params.get("account_name"))) {
				String account_name =params.get("account_name");
				sb.append(" and m.account_name = ? ");
				list.add(account_name);		
			}
			
			//身份证号
			if (StringUtils.isNotEmpty(params.get("id_card_no"))) {
				String id_card_no =params.get("id_card_no");
				sb.append(" and m.id_card_no = ? ");
				list.add(id_card_no);		
			}
			
			//省份
			if (StringUtils.isNotEmpty(params.get("province"))) {
				String province =params.get("province");
				sb.append(" and m.province = ? ");
				list.add(province);		
			}
		
			//市区
			if (StringUtils.isNotEmpty(params.get("city"))) {
				String city =params.get("city");
				sb.append(" and m.city = ? ");
				list.add(city);		
			}
			
			
			// 代理名称
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentCode = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentCode);
				}
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 开通状态
			if (StringUtils.isNotEmpty(params.get("openStatus"))) {
				String openStatus = params.get("openStatus");
				if (!"-1".equals(openStatus)) {
					sb.append(" and m.open_status =? ");
					list.add(openStatus);
				}
			}
			
			// 设备类型
			if (StringUtils.isNotEmpty(params.get("pos_type"))) {
				if (!params.get("pos_type").equals("-1")) {
					String pos_type = params.get("pos_type");
					sb.append(" and m.pos_type = ? ");
					list.add(pos_type);
				}
			}

			// sale-name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");

				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);

				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);

			}

			// 商户所属的“销售”只能是当前的登录人员。
			// belongTo_sale_name
			// if (StringUtils.isNotEmpty(params.get("belongTo_sale_name"))) {
			// String belongTo_sale_name = params.get("belongTo_sale_name");
			// sb.append("  and  m.sale_name =?  ");
			// list.add(belongTo_sale_name);
			// }

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and m.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and m.create_time <=? ");
				list.add(createTimeEnd);
			}
			
			
			// 开通状态
			if (StringUtils.isNotEmpty(params.get("open_type"))) {
				String open_type = params.get("open_type");
				if (!"-1".equals(open_type)) {
					sb.append(" and m.open_type = ? ");
					list.add(open_type);
				}
			}

			// 收单机构商户编号
			if(StringUtils.isNotEmpty(params.get("acq_merchant_no"))){
				String acq_merchant_no = params.get("acq_merchant_no");
				sb.append(" and (");
				sb.append(" exists(select gm.group_code from trans_route_group_merchant gm, trans_route_group rg where gm.group_code=rg.group_code and gm.merchant_no=m.merchant_no and gm.group_code = ?) ");
				sb.append(" or ");
				sb.append(" exists(select acq_merchant_no from acq_merchant where merchant_no=m.merchant_no and m.real_flag = 1 and acq_merchant_no = ?) ");
				sb.append(" or ");
				sb.append(" exists(select a.acq_merchant_no from trans_route_info r, acq_merchant a where r.acq_merchant_no=a.acq_merchant_no and r.merchant_no=m.merchant_no and m.real_flag = 0 and a.acq_merchant_no = ?)) ");
				list.add(acq_merchant_no);
				list.add(acq_merchant_no);
				list.add(acq_merchant_no);
			}
			
			// 同步好乐付
			String send_hlf1 = params.get("send_hlf1");
			String send_hlf2 = params.get("send_hlf2");
			if(StringUtils.isNotEmpty(send_hlf1) && !"-1".equals(send_hlf1)){
				if("0".equals(send_hlf1)){
					sb.append(" and m.send_hlf = '0' ");
				}else if("1".equals(send_hlf1)){
					sb.append(" and m.send_hlf <> '0' ");
				}
			}
			
			// 绑定情况
			if(StringUtils.isNotEmpty(send_hlf2) && !"-1".equals(send_hlf2)){
				if("0".equals(send_hlf2)){
					sb.append(" and m.send_hlf = '2000' ");
				}else if("1".equals(send_hlf2)){
					sb.append(" and m.send_hlf = '2001' ");
				}else if("2".equals(send_hlf2)){
					sb.append(" and (m.send_hlf = '2002' or m.send_hlf = '200' )");
				}else if("3".equals(send_hlf2)){
					sb.append(" and m.send_hlf = '-1' ");
				}
			}
			
			
		}
		String sql = "select m.* , CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join agent_info a  on  m.agent_no = a.agent_no where 1=1 " + sb.toString() + " order by m.id desc";

		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	public Map<String, Object> getMerDetail(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = " select p.* ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,a.sale_name as self_sale_name from pos_merchant p left join  agent_info a on  p.agent_no=a.agent_no   where p.id = ?";
		Map<String, Object> transInfo = dao.findFirst(sql, list.toArray());	
		return transInfo;
	}

	// 根据商户号查询商户及费率
	public Map<String, Object> queryMerchantFee(String merchantNo) {
		String sql = "select  m.merchant_name, m.merchant_short_name, f.fee_type, f.fee_rate, " + "f.fee_cap_amount, f.fee_max_amount, f.fee_single_amount,f.ladder_fee from pos_merchant m , pos_merchant_fee f " + " where m.merchant_no = f.merchant_no and f.merchant_no=?";
		return dao.findFirst(sql, merchantNo);
	}

	// 根据主键查询商户详情
	public Map<String, Object> merchantQueryById(Long id) throws SQLException {
		String sql = "select "
				// +
				// " id,merchant_no,merchant_name,merchant_short_name,mobile_username,mobile_password,open_status,agent_no,address,sale_address,province,city,link_name,phone,mcc,sale_name,settle_cycle,create_time"
				+ " id,merchant_no,merchant_name,merchant_short_name,lawyer,merchant_type,mobile_username,mobile_password," + " open_status,agent_no,address,sale_address,province,city,link_name,phone,email,mcc,sale_name,settle_cycle,app_no," + " bank_name,account_no,account_name,cnaps_no,account_type,attachment,create_time, "
				+ " examination_opinions,real_flag,id_card_no,terminal_count,main_business,remark,code_word ,belong_to_agent," + "trans_time_start,trans_time_end,my_settle,clear_card_no,pos_type,add_type,terminal_no,trans_cancel,pay_method,bag_settle,bus_license_no,agent_lock,freeze_status" + "  from pos_merchant" + " where id=?";
		return dao.findFirst(sql, id);
	}

	public Map<String, Object> merchantFeeQueryByMerchantNoToMap(String merchantNo) throws SQLException {
		String sql = "select " + " id,merchant_no,fee_type,fee_rate,fee_cap_amount,fee_max_amount,fee_single_amount,ladder_fee" + "  from pos_merchant_fee" + " where merchant_no=?";
		return dao.findFirst(sql, merchantNo);
		
	}

	public Map<String, Object> merchantTransRuleQueryByMerchantNoToMap(String merchantNo) throws SQLException {
		String sql = "select " + " merchant_no,single_max_amount,ed_max_amount,ed_card_max_items,ed_card_max_amount,create_time" + "  from pos_merchant_trans_rule" + " where merchant_no=?";
		return dao.findFirst(sql, merchantNo);
	}

	
	
	
	
	
	
	// 商户修改
	public String merchantModify(Map<String, String> params) throws SQLException {

		
		String id = params.get("id");
		String merchant_type = params.get("merchant_type");
		String merchant_no = params.get("merchant_no").trim();
		String merchant_name = params.get("merchant_name").trim();
		String merchant_short_name = params.get("merchant_short_name").trim();
		String mobile_username = params.get("mobile_username").trim();
		String address = params.get("address").trim();
		// String sale_address = params.get("sale_address");
		String province = params.get("province");
		String city = params.get("city");
		String link_name = params.get("link_name").trim();
		String phone = params.get("phone").trim();
		String sale_name = params.get("sale_name").trim();
		String settle_cycle = params.get("settle_cycle");
		String fee_type = params.get("fee_type");
		// String fee_rate = params.get("fee_rate");
		// String fee_cap_amount = params.get("fee_cap_amount");
		// String fee_max_amount = params.get("fee_max_amount");
		// String fee_single_amount = params.get("fee_single_amount");
		String agent_no = params.get("agent_no");
		String lawyer = params.get("lawyer").trim();
		String account_no = params.get("account_no").trim();
		String bank_name = params.get("bank_name").trim();
		String account_name = params.get("account_name").trim();
		String attachment = params.get("attachment");
		String open_status = params.get("open_status");

		String cnaps_no = params.get("cnaps_no").trim();
		String account_type = params.get("account_type");

		String rate1 = params.get("rate1");
		String rate2 = params.get("rate2");
		String email = params.get("email").trim();

		String ladder_min = params.get("ladder_min");
		String ladder_value = params.get("ladder_value");
		String ladder_max = params.get("ladder_max");

		String ed_max_amount = params.get("ed_max_amount");
		String single_max_amount = params.get("single_max_amount");
		String ed_card_max_amount = params.get("ed_card_max_amount");
		String ed_card_max_items = params.get("ed_card_max_items");

		String real_flag = params.get("real_flag");
		String id_card_no = params.get("id_card_no");
		String terminal_count = params.get("terminal_count");
		String main_business = params.get("main_business").trim();
		String remark = params.get("remark");
		String code_word = params.get("code_word");
		String belong_to_agent = params.get("belong_to_agent");
		String trans_time_start = "1970-10-01 " + params.get("trans_time_start");
		String trans_time_end = "1970-10-01 " + params.get("trans_time_end");
		String my_settle = params.get("my_settle");
		String clear_card_no = params.get("clear_card_no");
		String pos_type = params.get("pos_type");
		String add_type = params.get("add_type");
		String terminalNo = params.get("terminalNo");
		String trans_cancel = params.get("trans_cancel");
		String pay_method = params.get("pay_method");
		String app_no=params.get("app_no");
		String bus_license_no = params.get("bus_license_no");
		
		//自身所属销售
		String self_sale_name = params.get("self_sale_name");
		String old_agent_no = params.get("old_agent_no");
		String old_belong_to_agent = params.get("old_belong_to_agent");
		
		//是否钱包结算
		String bag_settle = params.get("bag_settle");
		
		if (bag_settle==null||"".equals(bag_settle)) {
			bag_settle="0";
		}
		
		//若是钱包结算，这不允许撤销交易
		if ("1".equals(bag_settle)) {
			trans_cancel="0";
		}
		
		//如果老的一级代理商没有值，表示没有修改一级代理商的权限，也就不用继续进行判断
		//如果老的一级代理商有值，且不等于新的代理商的值，表名需要进行下个月启用代理商程序控制
		boolean agentChanged = !StringUtil.isEmpty(old_agent_no) && !old_agent_no.equals(agent_no);
		//所属代理商是否被修改
		boolean belongToAgentChanged = !StringUtil.isEmpty(old_belong_to_agent) && !old_belong_to_agent.equals(belong_to_agent);
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			List<Object> listPosMerchant = new ArrayList<Object>();
			listPosMerchant.add(merchant_name);
			listPosMerchant.add(merchant_short_name);
			listPosMerchant.add(lawyer);
			listPosMerchant.add(merchant_type);
			listPosMerchant.add(mobile_username);
			listPosMerchant.add(open_status);
			listPosMerchant.add(address);
			listPosMerchant.add(address);
			listPosMerchant.add(province);
			listPosMerchant.add(city);
			listPosMerchant.add(link_name);
			listPosMerchant.add(phone);
			listPosMerchant.add(email);
			listPosMerchant.add(sale_name);
			listPosMerchant.add(settle_cycle);
			listPosMerchant.add(bank_name);
			listPosMerchant.add(account_no);
			listPosMerchant.add(account_name);
			listPosMerchant.add(cnaps_no);
			listPosMerchant.add(account_type);
			listPosMerchant.add(attachment);
			listPosMerchant.add(real_flag);
			listPosMerchant.add(id_card_no);
			listPosMerchant.add(terminal_count);
			listPosMerchant.add(main_business);
			listPosMerchant.add(remark);
			listPosMerchant.add(code_word);
			listPosMerchant.add(trans_time_start);
			listPosMerchant.add(trans_time_end);
			listPosMerchant.add(my_settle);
			listPosMerchant.add(clear_card_no);
			listPosMerchant.add(pos_type);
			listPosMerchant.add(add_type);
			listPosMerchant.add(terminalNo);
			listPosMerchant.add(trans_cancel);
			listPosMerchant.add(pay_method);
			listPosMerchant.add(bag_settle);
			listPosMerchant.add(bus_license_no);
			listPosMerchant.add(app_no);

			String extraParam = "";
			if(!agentChanged){
				Map<String, Object> nodeMap=findAgentNoId(belong_to_agent);
				//取到所属代理商的self_node，将self_node保存到parent_node里面
				String self_node=(String)nodeMap.get("self_node");
				listPosMerchant.add(self_node);
				listPosMerchant.add(agent_no);
				listPosMerchant.add(belong_to_agent);
				
				extraParam = " ,parent_node=?,agent_no = ?,belong_to_agent=?";
			}
			
			listPosMerchant.add(id);
			
			String update_pos_merchant = "update pos_merchant set " 
				+ " merchant_name = ?,merchant_short_name = ?," 
				+ " lawyer = ?,merchant_type = ?,mobile_username = ?," 
				+ " open_status = ?,address = ?,sale_address = ?," 
				+ " province = ?,city = ?,link_name = ?,phone = ?,email = ?," 
				+ " sale_name = ?,settle_cycle = ?,bank_name = ?,account_no = ?,"
				+ " account_name = ?,cnaps_no = ?,account_type = ? , attachment = ?,real_flag = ?,id_card_no = ?,terminal_count = ? ,main_business = ?,remark = ?,code_word=?," 
				+ " trans_time_start =?,trans_time_end =? ,my_settle=?,clear_card_no=? , pos_type=? ,add_type=? ,terminal_no=? , trans_cancel=? ,pay_method=?,bag_settle=?,bus_license_no=?,app_no=?" 
				+ extraParam
				+ " where id = ?";

			
			dao.updateByTranscation(update_pos_merchant, listPosMerchant.toArray(), conn);
			
			// 如果一级代理商被修改了，进行下个月生效程序控制
			if(agentChanged){
				String select_agent4quartz = "select merchant_no from pos_merchant_agent4quartz where merchant_no = ?";
				String update_agent4quartz = "update pos_merchant_agent4quartz set agent_no = ?, belong_to_agent = ?, sale_name =?, update_time = now() where merchant_no = ?";
				String insert_agent4quartz = "insert into pos_merchant_agent4quartz(merchant_no, agent_no, belong_to_agent, sale_name, create_time) values(?, ?, ?, ?, now())";
				String insert_agent4log = "insert into pos_merchant_agent4log(merchant_no, oper_id, oper_name, content, oper_time) values (?, ?, ?, ?, now())";
			
				Map<String, Object> map = dao.findFirst(select_agent4quartz, new Object[]{merchant_no});
					
				if(map != null){
					dao.updateByTranscation(update_agent4quartz, new Object[]{agent_no, belong_to_agent, sale_name, merchant_no}, conn);
				}else{
					dao.updateByTranscation(insert_agent4quartz, new Object[]{merchant_no, agent_no, belong_to_agent, sale_name}, conn);
				}
				
				BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				String content = "操作员ID:" + bu.getId() + " " + bu.getRealName() + "修改商户：" + merchant_no + "代理商为：" + agent_no + "二级代理商为：" + belong_to_agent + "所属销售为：" + sale_name;
				dao.updateByTranscation(insert_agent4log, new Object[]{merchant_no, bu.getId(), bu.getRealName(), content}, conn);
			} else if (!agentChanged && belongToAgentChanged) {
				// 一代没有修改，所属代理商修改马上生效，并且去删除可能存在次月生效的的定时任务
				String delete_agent4quartz = "delete from pos_merchant_agent4quartz where merchant_no = ?";
				dao.updateByTranscation(delete_agent4quartz, new Object[]{merchant_no}, conn);
				
				// 记录操作日志
				BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				String insert_agent4log = "insert into pos_merchant_agent4log(merchant_no, oper_id, oper_name, content, oper_time) values (?, ?, ?, ?, now())";
				String content = "操作员ID:" + bu.getId() + " 操作人" + bu.getRealName() + ",删除商户：" + merchant_no + "转移代理商定时任务";
				dao.updateByTranscation(insert_agent4log, new Object[]{merchant_no, bu.getId(), bu.getRealName(), content}, conn);
			}
			/*else{
	            // 一级代理商没有被修改，则更新自身所属销售
				String update_agent_info = "update agent_info set  sale_name = ? where agent_no = ?";
				List<Object> agentList= new ArrayList<Object>();
				agentList.add(self_sale_name);
				agentList.add(agent_no);
				dao.updateByTranscation(update_agent_info, agentList.toArray(), conn);
			}*/

			String update_pos_merchant_fee = "update  pos_merchant_fee set " + " fee_type = ?,fee_rate = ?,fee_cap_amount = ?,fee_max_amount = ?,ladder_fee = ?" + " where  merchant_no =?";

			List<Object> listPosMerchantFee = new ArrayList<Object>();

			listPosMerchantFee.add(fee_type);

			if ("RATIO".equals(fee_type)) {
				listPosMerchantFee.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
				listPosMerchantFee.add(null);
				listPosMerchantFee.add(null);
				listPosMerchantFee.add(null);

			} else if ("CAPPING".equals(fee_type)) {
				listPosMerchantFee.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
				listPosMerchantFee.add((rate1 == null || rate2 == null) ? null : new BigDecimal(rate2).divide(new BigDecimal(rate1).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP));
				listPosMerchantFee.add((rate2 == null || rate2.trim().length() == 0) ? null : new BigDecimal(rate2));
				listPosMerchantFee.add(null);

			} else if ("LADDER".equals(fee_type)) {

				ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
				ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

				String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;

				listPosMerchantFee.add(null);
				listPosMerchantFee.add(null);
				listPosMerchantFee.add(null);
				listPosMerchantFee.add(ladder_fee);

			}

			listPosMerchantFee.add(merchant_no);
			dao.updateByTranscation(update_pos_merchant_fee, listPosMerchantFee.toArray(), conn);

			// pos_merchant_trans_rule

			String update_pos_merchant_trans_rule = "update pos_merchant_trans_rule set" + " single_max_amount = ?,ed_max_amount = ?,ed_card_max_items = ?,ed_card_max_amount = ?,last_update_time=now()" + "  where merchant_no = ? ";

			List<Object> listPosMerchantTransRule = new ArrayList<Object>();

			listPosMerchantTransRule.add((single_max_amount == null || single_max_amount.trim().length() == 0) ? null : new BigDecimal(single_max_amount));
			listPosMerchantTransRule.add((ed_max_amount == null || ed_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_max_amount));
			listPosMerchantTransRule.add((ed_card_max_items == null || ed_card_max_items.trim().length() == 0) ? null : new BigDecimal(ed_card_max_items));
			listPosMerchantTransRule.add((ed_card_max_amount == null || ed_card_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_card_max_amount));
			listPosMerchantTransRule.add(merchant_no);
			dao.updateByTranscation(update_pos_merchant_trans_rule, listPosMerchantTransRule.toArray(), conn);

			// 增加商户修改操作日志
			params.put("oper_type", "1"); // 操作类别：1修改，2删除
			addPosMerchantLog(params, conn);

			conn.commit();// 提交事务
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();// 回滚事务
			throw new RuntimeException();
		} finally {
			conn.close();// 关闭连接
		}
		try {
			cleanCacheService.cleanAllCache(merchant_no);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchant_no);
			// e.printStackTrace();
		}

		return merchant_no;

	}

	public List<Map<String, Object>> queryMerchantByAgentNo(String agent_no) {
		String sql = "select merchant_no,merchant_name,merchant_short_name,agent_no from pos_merchant where agent_no = ? and ( open_status = 5 or open_status = 1) " + " order by  convert(merchant_name using utf8)";
		/*String sql = "select * from pos_merchant where belong_to_agent = ? and ( open_status = 5 or open_status = 1) " + " order by  convert(merchant_name using gb2312)";*/
		List<Map<String, Object>> list = dao.find(sql, agent_no);
		return list;
	}

	// 商户用户新增
	public int merchantUserAdd(String merchantNo, String createOperator, Map<String, String> merchantInfo) throws SQLException {

		String realName = merchantInfo.get("merchant_name");
		String email = merchantInfo.get("email");
		String password = MD5.toMD5("mer88888");// 原来是"C20BCA0DA652BC34C6B48619A0F33E4A";
		String sql = "insert into cust_user(user_name,real_name,email,status,password,create_operator,create_time) values(?,?,?,?,?,?,now())";
		Object[] params = { merchantNo, realName, email, "1", password, createOperator };
		int n = dao.update(sql, params);
		//
		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchantNo);
			// e.printStackTrace();
		}

		return n;
	}
	
	/**
	 * 根据商户ID 以及编号  将是否优质商户、是否撤销交易、是否钱包结算都改为否
	 * @param id
	 * @param merchant_no
	 * @throws SQLException
	 */
	public int updateSettleCancel(String id, String merchant_no) throws SQLException{
		int count = 0;
		String sql = "update pos_merchant set trans_cancel=0,bag_settle=0 where id=? and merchant_no=?";
		count = dao.update(sql, new Object[]{id,merchant_no});
		return count;
	}

	//商户上传图片，将其状态改为“额度提升”也就是open_status=2,并保存上传的图片名
	public void updateOpenStatus(String open_status,String attachment,String merchant_no) throws SQLException{
		String sql = "update pos_merchant set open_status=?,attachment=? where merchant_no=?";
		dao.update(sql, new Object[]{open_status,attachment,merchant_no});
	}
	
	//商户上传图片，将其状态改为“额度提升”也就是lift_status=1,并保存上传的图片名
	public void updateLiftStatus(String lift_status,String attachment,String merchant_no) throws SQLException{
		String sql = "update pos_merchant set lift_status=?,attachment=? where merchant_no=?";
		dao.update(sql, new Object[]{lift_status,attachment,merchant_no});
	}
	
	
	//根据merchant_no查询商户
	public PosMerchant selectPosMerchant(String merchant_no){
		String sql = "select * from pos_merchant where merchant_no=?";
		return dao.findFirst(PosMerchant.class, sql, merchant_no);
	}
	
	// 是否已经存在商户对应的操作员
	public List<Map<String, Object>> getMerchantUser(String merchantNo) throws SQLException {
		String sqlHaveUser = "select * from  cust_user u join pos_merchant m on u.user_name=m.merchant_no and u.user_name=?";
		Object[] params = { merchantNo };
		return dao.find(sqlHaveUser, params);
	}

	// 商户审核日志插入
	public int insertExaminationsLog(String merchantNo, String createOperator, Map<String, String> params) throws SQLException {

		String examination_opinions = params.get("examination_opinions");
		String open_status = params.get("open_status");
		String sql = "insert into examinations_log(merchant_no,open_status,examination_opinions,operator,create_time) values(?,?,?,?,now())";
		Object[] logParams = { merchantNo, open_status, examination_opinions, createOperator };
		int n = dao.update(sql, logParams);

		try {
			cleanCacheService.cleanAllCache(merchantNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchantNo);
			// e.printStackTrace();
		}

		return n;
	}

	// 商户修改
	public int merchantSetNormal(Map<String, String> params) throws SQLException {

		String merchant_no = params.get("merchant_no");
		String status = params.get("status");
		String update_pos_merchant = "update pos_merchant set " + " open_status = ?,open_time=now()" + " where merchant_no = ?";
//		String update_pos_merchant_log = "insert into pos_merchant_log(merchant_no,open_status,oper_user_id,oper_user_name,oper_type,operate_time) values(?,?,?,?,?,now())";
		
//		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			int updateRows = dao.updateByTranscation(update_pos_merchant, new Object[]{status, merchant_no}, conn);
//			dao.updateByTranscation(update_pos_merchant_log, new Object[]{merchant_no, status, bu.getId(), bu.getRealName(), "1".equals(status) ? "6" : "5"}, conn);
			addPosMerchantLogNew(merchant_no, conn, "1".equals(status) ? "6" : "5");
			
			conn.commit();// 提交事务
			
			try {
				cleanCacheService.cleanAllCache(merchant_no);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchant_no);
			}
			
			return updateRows;
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();// 回滚事务
			return 0;
		} finally {
			conn.close();// 关闭连接
		}
		
	}

	// 修改商户状态
	public int merUpdateStatus(Map<String, String> params) throws SQLException {

		String merchant_no = params.get("merchant_no");
		String status = params.get("status");
		String update_pos_merchant = "update pos_merchant set " + " open_status = ? where merchant_no = ?";
		List<Object> listPosMerchant = new ArrayList<Object>();
		listPosMerchant.add(status);
		listPosMerchant.add(merchant_no);
		int updateRows = dao.update(update_pos_merchant, listPosMerchant.toArray());
		try {
			cleanCacheService.cleanAllCache(merchant_no);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchant_no);
			// e.printStackTrace();
		}
		return updateRows;
	}

	// 商户删除，事务控制
	public void merchantDel(String id, String merchant_no) throws SQLException {
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);

			String pos_merchat_sql = "delete from pos_merchant where id = ?";
			dao.updateByTranscation(pos_merchat_sql, new Object[] { id }, conn);

			String pos_merchat_fee_sql = "delete from pos_merchant_fee where merchant_no = ?";
			dao.updateByTranscation(pos_merchat_fee_sql, new Object[] { merchant_no }, conn);

			String pos_merchat_trans_rule_sql = "delete from pos_merchant_trans_rule where merchant_no = ?";
			dao.updateByTranscation(pos_merchat_trans_rule_sql, new Object[] { merchant_no }, conn);

			conn.commit();// 提交事务
		} catch (Exception e) {
			conn.rollback();// 回滚事务
			throw new RuntimeException();
		} finally {
			conn.close();// 关闭连接
		}

		try {
			cleanCacheService.cleanAllCache(merchant_no);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + merchant_no);
			// e.printStackTrace();
		}
	}

	public Page<Map<String, Object>> getMobileList(Map<String, String> params, PageRequest page) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			if (StringUtils.isNotEmpty(params.get("mobile"))) {
				String mobile = params.get("mobile");
				list.add(mobile);
				sb.append(" and mobile=?");
			}

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
			/*	String merchant_name = "%" + params.get("merchant") + "%";
				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");*/
				String merchant_name =params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			
			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
				String merchant_no = params.get("merchant_no");
				list.add(merchant_no);
				sb.append(" and merchant_no=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("mobileStatus"))) {
				String mobileStatus = params.get("mobileStatus");
				list.add(mobileStatus);
				sb.append(" and status=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("terminal_no"))) {
				String terminal_no = params.get("terminal_no");
				list.add(terminal_no);
				sb.append(" and terminal_no=?");
			}
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and create_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select * from increment_mobile  where 1=1" + sb.toString() + " order by create_time desc";
		return dao.find(sql, list.toArray(), page);

	}

	public Map<String, Object> getMobileDetailList(Long id) {
		String sql = "select m.* , o.trans_status   from increment_mobile m LEFT JOIN increment_order o " +
				"ON m.journal=o.order_no where m.id=" + id;
		return dao.findFirst(sql);
	}

	//违章罚款列表查询
	public Page<Map<String, Object>> getWzfkList(Map<String, String> params, PageRequest page) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			if (StringUtils.isNotEmpty(params.get("order_no"))) {
				String order_no = params.get("order_no");
				list.add(order_no);
				sb.append(" and order_no=?");
			}

			// 商户名称查询
//			if (StringUtils.isNotEmpty(params.get("merchant"))) {
//				String merchant_name = "%" + params.get("merchant") + "%";
//				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
//				list.add(merchant_name);
//				list.add(merchant_name);
//				list.add(merchant_name);
//			}
			
			if (StringUtils.isNotEmpty(params.get("carnumber"))) {
				String carnumber = params.get("carnumber");
				list.add(carnumber);
				sb.append(" and carnumber=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("status"))) {
				String status = params.get("status");
				list.add(status);
				sb.append(" and status=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and create_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select * from increment_wzdb  where 1=1" + sb.toString() + " order by create_time desc";
		return dao.find(sql, list.toArray(), page);

	}
	
	//违章罚款详情
	public Map<String, Object> getWzfkDetailList(Long id) {
		String sql = "select m.* , o.trans_status   from increment_wzdb m LEFT JOIN increment_order o " +
				"ON m.order_no=o.order_no where m.id=" + id;
		return dao.findFirst(sql);
	}
	
	
	
	// 信用卡还款查询
	public Page<Map<String, Object>> getCreditList(Map<String, String> params, PageRequest page) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			if (StringUtils.isNotEmpty(params.get("credit_no"))) {
				String credit_no = params.get("credit_no");
				list.add(credit_no);
				sb.append(" and credit_no=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("creditStatus"))) {
				String creditStatus = params.get("creditStatus");
				list.add(creditStatus);
				sb.append(" and status=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("amount"))) {
				String amount = params.get("amount");
				list.add(amount);
				sb.append(" and amount=?");
			}
			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
				String merchant_no = params.get("merchant_no");
				list.add(merchant_no);
				sb.append(" and merchant_no=?");
			}
			if (StringUtils.isNotEmpty(params.get("terminl_no"))) {
				String terminl_no = params.get("terminl_no");
				list.add(terminl_no);
				sb.append(" and terminl_no=?");
			}
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and create_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select * from increment_credit where 1=1" + sb.toString() + " order by create_time desc";
		return dao.find(sql, list.toArray(), page);

	}

	// 信用卡还款详情
	public Map<String, Object> getCreditDetail(Long id) {
		String sql = "select m.* , o.trans_status from increment_credit m LEFT JOIN increment_order o ON m.journal=o.order_no where m.id=" + id;
		return dao.findFirst(sql);
	}

	// 增加商户修改日志
	public void addPosMerchantLog(Map<String, String> params, Connection conn) throws SQLException {
		List<Object> list = new ArrayList<Object>();
		list.add(params.get("merchant_no"));
		list.add(params.get("merchant_name"));
		list.add(params.get("merchant_short_name"));
		list.add(params.get("lawyer"));
		list.add(params.get("merchant_type"));
		list.add(params.get("mobile_username"));
		list.add(params.get("open_status"));
		list.add(params.get("agent_no"));
		list.add(params.get("address"));
		list.add(params.get("sale_address"));
		list.add(params.get("province"));
		list.add(params.get("city"));
		list.add(params.get("link_name"));
		list.add(params.get("phone"));
		list.add(params.get("email"));
		list.add(params.get("mcc"));
		list.add(params.get("sale_name"));
		list.add(params.get("settle_cycle"));
		list.add(params.get("bank_name"));
		list.add(params.get("account_no"));
		list.add(params.get("account_name"));
		list.add(params.get("cnaps_no"));
		list.add(params.get("account_type"));
		list.add(params.get("attachment"));
		list.add(params.get("real_flag"));
		list.add(params.get("id_card_no"));
		list.add(params.get("terminal_count"));
		list.add(params.get("main_business"));
		list.add(params.get("remark"));
		list.add(params.get("code_word"));
		list.add(params.get("belong_to_agent"));
		String trans_time_start = "1970-10-01 " + params.get("trans_time_start");
		String trans_time_end = "1970-10-01 " + params.get("trans_time_end");
		list.add(trans_time_start);
		list.add(trans_time_end);
		// 费率
		String fee_type = params.get("fee_type");
		String rate1 = params.get("rate1");
		String rate2 = params.get("rate2");
		String ladder_min = params.get("ladder_min");
		String ladder_value = params.get("ladder_value");
		String ladder_max = params.get("ladder_max");

		list.add(fee_type);
		if ("RATIO".equals(fee_type)) {
			list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
			list.add(null);
			list.add(null);
			list.add(params.get("fee_single_amount"));
			list.add(null);

		} else if ("CAPPING".equals(fee_type)) {
			list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
			list.add((rate1 == null || rate2 == null) ? null : new BigDecimal(rate2).divide(new BigDecimal(rate1).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP));
			list.add((rate2 == null || rate2.trim().length() == 0) ? null : new BigDecimal(rate2));
			list.add(params.get("fee_single_amount"));
			list.add(null);

		} else if ("LADDER".equals(fee_type)) {

			ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
			ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

			String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;

			list.add(null);
			list.add(null);
			list.add(null);
			list.add(params.get("fee_single_amount"));
			list.add(ladder_fee);

		} else {
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
		}
		/*
		 * list.add(params.get("fee_type")); list.add(params.get("fee_rate"));
		 * list.add(params.get("fee_cap_amount"));
		 * list.add(params.get("fee_max_amount"));
		 */
		// list.add(params.get("fee_single_amount"));
		// list.add(params.get("ladder_fee"));
		list.add(params.get("terminal_no"));
		// 交易规则
		list.add(params.get("single_max_amount"));
		String ed_max_amount = params.get("ed_max_amount");
		String single_max_amount = params.get("single_max_amount");
		String ed_card_max_amount = params.get("ed_card_max_amount");
		String ed_card_max_items = params.get("ed_card_max_items");
		list.add((single_max_amount == null || single_max_amount.trim().length() == 0) ? null : new BigDecimal(single_max_amount));
		list.add((ed_max_amount == null || ed_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_max_amount));
		list.add((ed_card_max_items == null || ed_card_max_items.trim().length() == 0) ? null : new BigDecimal(ed_card_max_items));
		list.add((ed_card_max_amount == null || ed_card_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_card_max_amount));

		// list.add(params.get("ed_max_amount"));
		// list.add(params.get("ed_card_max_items"));
		// list.add(params.get("ed_card_max_amount"));
		// list.add(params.get("ed_total_amount"));
		// list.add(params.get("operate_time"));
		// 操作员信息
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(params.get("oper_desc"));
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(params.get("oper_type"));
		list.add(params.get("my_settle"));
		
		list.add(params.get("clear_card_no"));
		list.add(params.get("pos_type"));
		list.add(params.get("add_type"));
		list.add(params.get("trans_cancel"));
		list.add(params.get("pay_method"));
		list.add(params.get("bag_settle"));
		list.add(params.get("bus_license_no"));
		list.add(params.get("app_no"));
		
		String sql = "INSERT INTO pos_merchant_log (" + "merchant_no, " + "merchant_name, " + "merchant_short_name, " + "lawyer, " + "merchant_type, " + "mobile_username, " + "open_status, " + "agent_no, " + "address, " + "sale_address, " + "province, " + "city, " + "link_name, " + "phone, " + "email, " + "mcc, " + "sale_name, " + "settle_cycle, " + "bank_name, " + "account_no, "
				+ "account_name, " + "cnaps_no, " + "account_type, " + "attachment, " + "real_flag, " + "id_card_no, " + "terminal_count, " + "main_business, " + "remark, " + "code_word, " + "belong_to_agent, " + "trans_time_start, " + "trans_time_end, " + "fee_type, " + "fee_rate, " + "fee_cap_amount, " + "fee_max_amount, " + "fee_single_amount, " + "ladder_fee, " + "terminal_no, "
				+ "single_max_amount, " + "ed_max_amount, " + "ed_card_max_items, " + "ed_card_max_amount, " + "ed_total_amount, " + "operate_time, " + "oper_desc, " + "oper_user_id, " + "oper_user_name, " + "oper_type, " + "my_settle,"
				+ "clear_card_no, pos_type, add_type, trans_cancel, pay_method, bag_settle, bus_license_no, app_no "
				+")" + "VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";

		dao.updateByTranscation(sql, list.toArray(), conn);
	}

	/**
	 * 根据商户名称查询没开通接口的商户
	 * 
	 * @param name
	 * @return id,merchant_name
	 */
	public List<Map<String, Object>> findMerchantByName(String name) {
		/*String sql = "select pm.id,pm.merchant_name from pos_merchant pm where pm.merchant_name like ? and (pm.merchant_api_used=0 or pm.merchant_api_used is null)";
		return dao.find(sql, new Object[] { "%" + name + "%" });*/
		String sql = "select pm.id,pm.merchant_name from pos_merchant pm where pm.merchant_name = ? and (pm.merchant_api_used=0 or pm.merchant_api_used is null)";
		return dao.find(sql, new Object[] {name});
	}

	/**
	 * 根据商户名称查询没开通sdk接口的商户
	 * 
	 * @param name
	 * @return id,merchant_name
	 */
	public List<Map<String, Object>> findMerchantByName4sdk(String name) {
/*		String sql = "select pm.id,pm.merchant_name from pos_merchant pm where pm.merchant_name like ? and (pm.merchant_sdk_api_used=0 or pm.merchant_sdk_api_used is null)";
		return dao.find(sql, new Object[] { "%" + name + "%" });*/
		String sql = "select pm.id,pm.merchant_name from pos_merchant pm where pm.merchant_name = ? and (pm.merchant_sdk_api_used=0 or pm.merchant_sdk_api_used is null)";
		return dao.find(sql, new Object[] {name});
	}

	/**
	 * 添加商户接口,并且修改商户接口开通状态为开通
	 * 
	 * @param maab
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public void addMerchantApi(MerchantApiAddBean maab, Long userId) throws SQLException {
		String sql = "insert into merchant_api(merchant_id,public_key_base64,private_key_base64,linkman_name,linkman_phone,allow_ip,create_time,create_user_id)" + "values(?,?,?,?,?,?,?,?)";
		KeyPair kp = KeyGen.genKeyPair();
		Connection conn = dao.getConnection();
		conn.setAutoCommit(false);
		try {
			dao.updateByTranscation(sql, new Object[] { maab.getMerchantId(), kp.getPublicKey(), kp.getPrivateKey(), maab.getLinkmanName(), maab.getLinkmanPhone(), maab.getIps(), new Date(), userId }, conn);
			updateMerchantApiUsedStatus(MerchantApiStatus.USED, Long.valueOf(maab.getMerchantId()), conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * 添加商户sdk接口,并且修改商户接口开通状态为开通
	 * 
	 * @param maab
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public void addMerchantSdkApi(MerchantApiAddBean maab, Long userId) throws SQLException {
		String sql = "insert into merchant_sdk_api(merchant_id,authorization_key,linkman_name,linkman_phone,allow_ip,create_time,create_user_id)" + "values(?,?,?,?,?,?,?)";
		String authorizationKey = DigestUtils.shaHex(String.valueOf(System.nanoTime()));
		Connection conn = dao.getConnection();
		conn.setAutoCommit(false);
		try {
			dao.updateByTranscation(sql, new Object[] { maab.getMerchantId(), authorizationKey, maab.getLinkmanName(), maab.getLinkmanPhone(), maab.getIps(), new Date(), userId }, conn);
			updateMerchantSdkApiUsedStatus(MerchantApiStatus.USED, Long.valueOf(maab.getMerchantId()), conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * 修改商户api开通状态
	 * 
	 * @throws SQLException
	 */
	public void updateMerchantApiUsedStatus(MerchantApiStatus mas, long merchantId, Connection conn) throws SQLException {
		String sql = "update pos_merchant pm set pm.merchant_api_used=? where pm.id=?";
		dao.updateByTranscation(sql, new Object[]{mas.getStauts(), merchantId}, conn);
	}

	/**
	 * 修改商户api开通状态
	 * 
	 * @throws SQLException
	 */
	public void updateMerchantSdkApiUsedStatus(MerchantApiStatus mas, long merchantId, Connection conn) throws SQLException {
		String sql = "update pos_merchant pm set pm.merchant_sdk_api_used=? where pm.id=?";
		dao.updateByTranscation(sql, new Object[]{mas.getStauts(), merchantId}, conn);
	}

	/**
	 * 启用禁用api
	 * 
	 * @param status
	 *            接口是否被禁用 1禁用 0正常
	 * @param merchantId
	 * @throws SQLException
	 */
	public void updateMerchantApiUsedStatus(int status, long merchantId) throws SQLException {
		String sql = "update merchant_api ma set ma.forbidden=? where ma.merchant_id=?";
		dao.update(sql, new Object[] { status, merchantId });
	}

	/**
	 * 启用禁用api
	 * 
	 * @param status
	 *            接口是否被禁用 1禁用 0正常
	 * @param merchantId
	 * @throws SQLException
	 */
	public void updateMerchantSdkApiUsedStatus(int status, long merchantId) throws SQLException {
		String sql = "update merchant_sdk_api ma set ma.forbidden=? where ma.merchant_id=?";
		dao.update(sql, new Object[] { status, merchantId });
	}

	public Page<Map<String, Object>> findMerchantApiList(Map<String, String> map, PageRequest pageRequest) {
		String sql = "select ma.*,pm.merchant_name,pm.merchant_no from  pos_merchant pm, merchant_api ma where pm.id=ma.merchant_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> condition = new ArrayList<Object>();
		String merchantName = map.get("merchantName");
		if (StringUtils.isNotEmpty(merchantName)) {
		/*	sb.append(" and (pm.merchant_name like ? or pm.merchant_no=?)");
			condition.add("%" + merchantName + "%");*/
			sb.append(" and (pm.merchant_name =? or pm.merchant_no=?)");
			condition.add(merchantName);
			condition.add(merchantName);
		}
		String createTimeBegin = map.get("createTimeBegin");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat mysqlSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isNotEmpty(createTimeBegin)) {
			try {
				Date date = sdf.parse(createTimeBegin);
				sb.append(" and ma.create_time>=?");
				condition.add(mysqlSdf.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String createTimeEnd = map.get("createTimeEnd");
		if (StringUtils.isNotEmpty(createTimeEnd)) {
			try {
				Date date = sdf.parse(createTimeEnd);
				sb.append(" and ma.create_time<=?");
				condition.add(mysqlSdf.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append(" order by ma.create_time desc");
		return dao.find(sb.toString(), condition.toArray(), pageRequest);
	}

	public Page<Map<String, Object>> findMerchantSdkApiList(Map<String, String> map, PageRequest pageRequest) {
		String sql = "select ma.*,pm.merchant_name,pm.merchant_no from  pos_merchant pm, merchant_sdk_api ma where pm.id=ma.merchant_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> condition = new ArrayList<Object>();
		String merchantName = map.get("merchantName");
		if (StringUtils.isNotEmpty(merchantName)) {
	/*		sb.append(" and (pm.merchant_name like ? or pm.merchant_no=?)");
			condition.add("%" + merchantName + "%");*/
			sb.append(" and (pm.merchant_name =? or pm.merchant_no=?)");
			condition.add(merchantName);
			condition.add(merchantName);
			
		}
		String createTimeBegin = map.get("createTimeBegin");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat mysqlSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isNotEmpty(createTimeBegin)) {
			try {
				Date date = sdf.parse(createTimeBegin);
				sb.append(" and ma.create_time>=?");
				condition.add(mysqlSdf.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String createTimeEnd = map.get("createTimeEnd");
		if (StringUtils.isNotEmpty(createTimeEnd)) {
			try {
				Date date = sdf.parse(createTimeEnd);
				sb.append(" and ma.create_time<=?");
				condition.add(mysqlSdf.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append(" order by ma.create_time desc");
		return dao.find(sb.toString(), condition.toArray(), pageRequest);
	}

	public void delMerchantApi(long id) throws SQLException {
		Connection conn = dao.getConnection();
		conn.setAutoCommit(false);
		String sql = "update pos_merchant pm,merchant_api ma set pm.merchant_api_used=0 where ma.merchant_id=pm.id and ma.id=?";
		try {
			dao.updateByTranscation(sql, new Object[] { id }, conn);
			sql = "delete ma from merchant_api ma where ma.id=?";
			dao.updateByTranscation(sql, new Object[] { id }, conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	public void delMerchantSdkApi(long id) throws SQLException {
		Connection conn = dao.getConnection();
		conn.setAutoCommit(false);
		try {
			String sql = "update pos_merchant pm,merchant_sdk_api msa set pm.merchant_sdk_api_used=0 where msa.merchant_id=pm.id and msa.id=?";
			dao.updateByTranscation(sql, new Object[] { id }, conn);
			sql = "delete ma from merchant_sdk_api ma where ma.id=?";
			dao.updateByTranscation(sql, new Object[] { id }, conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	public Map<String, Object> findMerchantApiById(long id) {
		String sql = "select ma.*,pm.merchant_name,pm.merchant_no from merchant_api ma,pos_merchant pm where ma.merchant_id=pm.id and ma.id=? ";
		return dao.findFirst(sql, id);
	}

	public Map<String, Object> findMerchantSdkApiById(long id) {
		String sql = "select ma.*,pm.merchant_name,pm.merchant_no from merchant_sdk_api ma,pos_merchant pm where ma.merchant_id=pm.id and ma.id=? ";
		return dao.findFirst(sql, id);
	}

	public void updateMerchantSdkApi(MerchantApiAddBean maab) throws SQLException {
		String sql = "update merchant_sdk_api ma set ma.allow_ip=?,ma.linkman_name=?,ma.linkman_phone=? where ma.id=?";
		dao.update(sql, new Object[] { maab.getIps(), maab.getLinkmanName(), maab.getLinkmanPhone(), maab.getMerchantId() });
	}

	public void updateMerchantApi(MerchantApiAddBean maab) throws SQLException {
		String sql = "update merchant_api ma set ma.allow_ip=?,ma.linkman_name=?,ma.linkman_phone=? where ma.id=?";
		dao.update(sql, new Object[] { maab.getIps(), maab.getLinkmanName(), maab.getLinkmanPhone(), maab.getMerchantId() });
	}
	
	
	/**
	 * 查找商户对应的大商户
	 * @param merchantNo
	 * @return
	 */
	public List<Map<String, Object>> getluyouMerNo(Map<String, Object> merInfo)throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		String merchantNo = merInfo.get("merchant_no").toString();
		String realFlag = merInfo.get("real_flag").toString();
		Map<String, Object> rgm = findRouteGroupMerchant(merchantNo);
		if (null != rgm) {
			// 查找收单商户。
			// list = findGroupAcqMerchant(rgm.get("group_code").toString());
			rgm.put("acq_merchant_no", rgm.get("group_code")) ;
			rgm.put("acq_merchant_name", rgm.get("group_name")) ;
			list.add(rgm);
			return list;
			
		}
		
		// 实名路由信息
		if ("1".equals(realFlag)) {
			list = findAcqMerchantByMerchantNo(merchantNo);
		
		} else {
			list = findTransRouteInfoMerchantNo(merchantNo);

		}
		
		return list;
	}
	
	
	public Map<String, Object> findRouteGroupMerchant(String merchantNo)
			throws SQLException {
		String sql = "select gm.* , rg.group_name from trans_route_group_merchant gm , trans_route_group rg  " +
				"where gm.group_code=rg.group_code and  merchant_no=?";
		return dao.findFirst(sql, merchantNo);
	}
	
	public List<Map<String, Object>> findGroupAcqMerchant(String merchantNo)
			throws SQLException {
		String sql = "select a.acq_merchant_no , a.acq_merchant_name from trans_route_group_rel r , acq_merchant a " +
				"where r.acq_merchant_no=a.acq_merchant_no and  r.group_code=?";
		return dao.find(sql, merchantNo);
	}
	
	/**
	 * 根据商户号查询到该商户对应的注册收单机构商户号
	 * 
	 * @throws SQLException
	 * */
	
	public List<Map<String, Object>> findAcqMerchantByMerchantNo(String merchantNo)
			throws SQLException {
		String sql = "select * from acq_merchant where merchant_no=?";
		return dao.find(sql, merchantNo);
	}
	
	/**
	 * 根据商户号查询大套小
	 * 
	 * @throws SQLException
	 * 
	 * */
	public List<Map<String, Object>> findTransRouteInfoMerchantNo(String merchantNo)
			throws SQLException {
		String sql = "select a.acq_merchant_no , a.acq_merchant_name  from trans_route_info r, acq_merchant a  " +
				"where r.acq_merchant_no=a.acq_merchant_no and r.merchant_no=?";
		return dao.find(sql, merchantNo);
	}
	
	public Long queryMerTransCount(String merchant_no) throws Exception{
		String sql = "select count(*) count from trans_info where merchant_no="+merchant_no;
		return (Long) dao.findFirst(sql).get("count");
	}
	
	/**
	 * 获取审核人-新
	 * @return  List<Map<String,Object>>
	 */
	public List<Map<String,Object>> getCheckerListForTag2()  {
		String sql  = "select b.real_name from merchant_check_person m,boss_user b where m.user_id=b.id and b.`status`=1 group by m.user_id";
		return dao.find(sql.toString());
	}
	
	/**
	 * 获取审核人
	 * @return
	 */
	public String[] getCheckerListForTag()  {
	     StringBuffer sql_sb = new StringBuffer(""); 
	     sql_sb.append(" select s.PARAM_VALUE as param_value from sys_config s where s.PARAM_KEY = 'checker' ");
	     List<Map<String, Object>> list =  dao.find(sql_sb.toString());
	     String[] param = null;
	     if(list.size()>0){
	    	 String param_value = (String)list.get(0).get("param_value");
	    	 param = param_value.split(";");
	     }
	     TreeSet<String> tr = new TreeSet<String>();
	     for(int i=0;i<param.length;i++){
	    	 tr.add(param[i]);
	     }
	     String[] param_values= new String[tr.size()];
	     for(int i=0;i<param_values.length;i++){
	    	 param_values[i]=tr.pollFirst(); //从TreeSet中取出元素重新赋给数组
	     }
	   return param_values;
	 }
	
	
	public List<Map<String,Object>> merchantQuery(Map<String, String> params) throws SQLException
	{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select * from pos_merchant where 1=1 ");
		String id = params.get("id");
		String merchant_name = params.get("merchant_name");
		String merchant_no = params.get("merchant_no");
		String merchant_short_name = params.get("merchant_short_name");
		String mobile_username = params.get("mobile_username");
		String pos_type = params.get("pos_type");
		String app_no = params.get("app_no");
		String agent_no = params.get("agent_no");
		String id_card_no = params.get("id_card_no");
		String account_no = params.get("account_no");
		
		if(StringUtils.isNotEmpty(id)){
			sql.append(" and id = ? ");
			list.add(id);
		}
		
		if(StringUtils.isNotEmpty(id_card_no)){
			sql.append(" and id_card_no = ? ");
			list.add(id_card_no);
		}
		
		if(StringUtils.isNotEmpty(id_card_no)){
			sql.append(" and id_card_no = ? ");
			list.add(id_card_no);
		}
		
		if(StringUtils.isNotEmpty(merchant_no)){
			sql.append(" and merchant_no = ? ");
			list.add(merchant_no);
		}
		
		if(StringUtils.isNotEmpty(account_no)){
			sql.append(" and account_no = ? ");
			list.add(account_no);
		}
		
		if (StringUtils.isNotEmpty(merchant_name)) {
			
			sql.append(" and merchant_name =? ");
			list.add(merchant_name);
		}
		
		if (StringUtils.isNotEmpty(merchant_short_name)) {
			
			sql.append(" and merchant_short_name =? ");
			list.add(merchant_short_name);
		}
		
		if (StringUtils.isNotEmpty(mobile_username)) {
			
			sql.append(" and mobile_username =? ");
			list.add(mobile_username);
		}
		
		if(StringUtils.isNotEmpty(agent_no)){
			sql.append(" and agent_no =? ");
			list.add(agent_no);
		}
		
		if(StringUtils.isNotEmpty(app_no)){
			sql.append(" and app_no =? ");
			list.add(app_no);
		}
		
		if (StringUtils.isNotEmpty(pos_type)) {
			sql.append(" and pos_type =? ");
			list.add(pos_type);
		}
		
		
		
		return dao.find(sql.toString(), list.toArray());
	}
	
	
	public PosMerchant merchantQueryByMerchantNo(String merchantNo) throws SQLException {
		String merchant_no = merchantNo;
		String select_pos_merchant = "select "
				+ " id,merchant_no,merchant_name,merchant_short_name,mobile_username,mobile_password,open_status,agent_no,address,sale_address,province,city,link_name,phone,email,mcc,sale_name,settle_cycle,attachment,create_time,real_flag,id_card_no,app_no,terminal_count "
				+ "  from pos_merchant" + " where merchant_no=?";

		List<Object> listPosMerchant = new ArrayList<Object>();
		listPosMerchant.add(merchant_no);

		PosMerchant posMerchant = dao.findFirst(PosMerchant.class,
				select_pos_merchant, listPosMerchant.toArray());

		return posMerchant;
	}
	
	
	
	
	
	/*public boolean merchantQuery(String mobile_username,String  id) throws SQLException
	{
		
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select id,merchant_no from pos_merchant where ");
		sql.append(" mobile_username =? ");
		Map<String, Object> retMap = dao.findFirst(sql.toString(),mobile_username);
		if(retMap == null || retMap.size()==0){
			//手机号可以使用
			return true;
		}
		String curr_id = ""+retMap.get("id");
		if(id.equals(curr_id)){
			//手机号可以使用
			return true;
		}else{
			//已经被使用，请输入新手机号
			return false;
		}
	}*/
	
    // 修改商户手机登陆密码为默认888888
	public boolean changePassword(String merchantNo)
			throws Exception {
		String password = "888888";
		password = MD5.toMD5(password);
		String sql = "update pos_merchant set mobile_password=? where merchant_no=?";
		Object[] params = { password,merchantNo };
		int result = dao.update(sql, params);
		if (result == 1)
			return true;
		return false;
	}
	
	
	
    /**
     * 根据商户编号查询激活码
     * @author LJ
     * @param merchant_no
     * @return
     * @throws Exception
     */
	public Map<String,Object> codeVerification(String merchant_no)
			throws Exception {
		/*String sql = "SELECT a.keycode from pos_merchant m,activation a  "
				    + "where m.merchant_no=a.`user`  and m.merchant_no="+merchant_no;*/
		String sql="select keycode from activation where code_user="+merchant_no;
		return dao.findFirst(sql);
	}
	
	
	   /**
	    * 审核成功修改激活码状态为已使用，更新使用者
	    * @author LJ
	    * @param keycode
	    * @throws Exception
	    */
		public void updateSucessCodeState(String keycode)
				throws Exception {
			String sql = "update activation set state=? where keycode=?";
			Object[] params = { 3,keycode};
			dao.update(sql, params);
		}
	
		 /**
		  * 审核失败修改激活码状态为已激活
		  * @author LJ
		  * @param keycode
		  * @throws Exception
		  */
		public void updatefaidCodeState(String keycode)
						throws Exception {
				String sql = "update activation set state=?,code_user=null where keycode=?";
				Object[] params = { 1, keycode};
				dao.update(sql, params);
		}
		
		 /**
		  * 审核失败修改激活码状态为已激活
		  * @author LJ
		  * @param keycode
		  * @throws Exception
		  */
		/*public boolean cardVerification(String lawyer,String id_card_no)
						throws Exception {
		    	Report report = new ReportImpl();
		    	boolean flag=report.queryReport(lawyer, id_card_no);
		    	log.info(lawyer+":"+id_card_no+";验证结果:"+flag);
		    	return flag;
		}*/
		
		public Map<String, String> cardVerificationNew(String lawyer,String id_card_no)	throws Exception {
				Map<String, String> map = new HashMap<String, String>();
		    	Report report = new ReportImpl();
		    	map=report.queryReport(lawyer, id_card_no);
		    	log.info(lawyer+":"+id_card_no+";验证结果:"+map.get("reportStatus"));
		    	return map;
		}
		
		/**
		 * @author 王帅
		 * @date 2014年11月19日21:41:53
		 * @see 获取本地已经验证的数据信息
		 * @param uname 验证对象姓名
		 * @param idCard 验证对象身份证号
		 * @return 返回 验证结果
		 */
		public int checkMerchantIndenti(String uname, String idCard){
			log.info("MerchantService checkMerchantIndenti start ...");
			int reportStatus = 0;
			if(uname != null && null != idCard && !"".equals(uname) && !"".equals(idCard)){
				List<String> list = new ArrayList<String>();
				Map<String, Object> map = new HashMap<String, Object>();
				String sql  = "select a.identi_status from py_identification a where a.ident_name=? and a.id_card=?";
				list.add(uname);
				list.add(idCard);
				map = dao.findFirst(sql, list.toArray());
				log.info("MerchantService checkMerchantIndenti SUCCESS");
				if(map != null){
					if(!"".equals(map.get("identi_status"))){
						reportStatus = Integer.parseInt(map.get("identi_status").toString());
					}
				}
			}
			log.info("MerchantService checkMerchantIndenti End");
			return reportStatus;
		}
		
		/**
		 * @author 王帅
		 * @date 2014年11月19日17:41:44
		 * @see 保存鹏元征信身份认证流水信息
		 * @param map 需要保存的信息
		 * @return 影响的行数
		 */
		public int savePYidentiInfo(Map<String, String> map){
			log.info("MerchantService savePYidentiInfo start ...");
			int saveCount = 0;
			if(map != null){
				try {
					String sql = "insert into py_identification(bat_No,report_id,ident_name,id_card,identi_status,by_system,create_person) values(?,?,?,?,?,?,?);";
					List<String> list = new ArrayList<String>();
					list.add(map.get("batNo"));
					list.add(map.get("reportID"));
					list.add(map.get("uname"));
					list.add(map.get("idCard"));
					list.add(map.get("reportStatus"));
					list.add(map.get("bySystem"));
					list.add(map.get("createPerson"));
					saveCount = dao.update(sql, list.toArray());
					log.info("MerchantService savePYidentiInfo SUCCESS saveCount=" + saveCount);
				} catch (Exception e) {
					log.error("Error MerchantService savePYidentiInfo Exception=" + e);
					return saveCount;
				}
			}
			log.info("MerchantService savePYidentiInfo End");
			return saveCount;
		}

		
		
		/**
		 * 根据激活码查询是否已经被使用
		 * @param keycode
		 * @return
		 */
		public Map<String, Object> findCode(String keycode) {
			List<Object> list = new ArrayList<Object>();
			list.add(keycode);
			String sql = "select  *   from  activation   where  keycode=?";
			Map<String, Object> keycodeMap = dao.findFirst(sql, list.toArray());
			return keycodeMap;
		}
		
		
		
		
		
	/*	public static void main(String[] args) {
			Report report = new ReportImpl();
			String 	merchant_name = "";
	        String 	id_card_no = "";
	    	boolean flag=report.queryReport(merchant_name, id_card_no);
	    	System.out.println(flag);
		}*/
		
		
		/**
		 * 商户修改，移联商通绑定激活码
		 * @param keycode
		 * @param merchant_no
		 * @throws Exception
		 */
		public void bindkeycode(String keycode,String merchant_no,String open_status)
						throws Exception {
			  if("5".equals(open_status) || "1".equals(open_status)){   //商户修改时，状态为机具绑定、正常的可绑定激活码
				    String sql = "update activation set code_user=?,state=?  where keycode=?";
					Object[] params = { merchant_no,3,keycode};
					dao.update(sql, params);
			  }
		}
		
		/**
		 * 根据代理商编号查询
		 * @param agent_no
		 * @author LJ
		 * @return
		 * @throws SQLException
		 */
		public Map<String, Object> getAgentNameByNo(String  agent_no) throws SQLException {
			String sql = "select  a.*,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name  from agent_info a  where agent_no=?";
			return dao.findFirst(sql, agent_no);
		}

		public Map<String, Object> getAgentName(String agent_no) throws  SQLException{
			return dao.findFirst("select agent_name from agent_info where agent_no = ? ", agent_no);
		}

		/**
		 * 根据代理商名称查询
		 * @param agent_no
		 * @author LJ
		 * @return
		 * @throws SQLException
		 */
		public Map<String, Object> getAgentNoByNo(String  agent_name) throws SQLException {
			String sql = "select *  from agent_info   where agent_name=?";
			return dao.findFirst(sql, agent_name);
		}
		
		
		// 根据主键查询商户详情
		public Map<String, Object> findSaleNameByAgentNo(String agent_no) throws SQLException {
			String sql = "select  a.sale_name  as self_sale_name  from  agent_info a  where  a.agent_no=?";
			return dao.findFirst(sql, agent_no);
		}
		
		/**
		 * 根据代理商编号查询
		 * @param agentNo
		 * @return
		 * @throws SQLException
		 */
		public Map<String, Object> findAgentNoId(String agentNo) throws SQLException {
			String sql = "select  *  from  agent_info    where  agent_no=?";
			return dao.findFirst(sql, agentNo);
		}
		/**
		 * 根据代理商id查询
		 * @param agentNo
		 * @return
		 * @throws SQLException
		 */
		public Map<String, Object> findAgentNoIdById(String id) throws SQLException {
			String sql = "select  *  from  agent_info    where  id=?";
			return dao.findFirst(sql, id);
		}
		
		/**
		 * 是否锁定，是否能修改一级代理商（0为锁定，1为已解锁）
		 * @param merchantNo
		 * @param agentLock
		 * @throws SQLException
		 */
		public void updateMerchantAgentLock(String merchantNo,String agentLock) throws SQLException{
			String sql = "update pos_merchant set agent_lock=? where merchant_no=?";
			dao.update(sql, new Object[]{agentLock,merchantNo});
		}
		
		/**
		 * 记录解锁记录
		 * @param merchant_no
		 * @param checker
		 * @param lockStr
		 * @throws SQLException
		 */
		public void insertLockRecord(String merchant_no,String checker,String lockStr,String result) throws SQLException{
			String insertLockRecordSql = "insert into lock_record(merchant_no,operator,operation,create_time,operation_result) values(?,?,?,?,?)";
			dao.update(insertLockRecordSql, new Object[]{merchant_no,checker,lockStr,new Date(),result});
		}
		/**
		 * 查询所有的appPosType记录
		 * @return
		 */
		public List<Map<String,Object>> findAppPosTypeAll(){
			String sql="select * from app_pos_type where app_name!='传统POS' group by app_no";
			return dao.find(sql);
		}
		/**
		 * 根据app_no查询对应的app名称
		 * @param appNo
		 * @return
		 */
		public String findAppPosTypeByAppNo(int appNo){
			String sql="select app_name from app_pos_type where app_no=?";
			Map<String,Object> appNameMap=dao.findFirst(sql, appNo);
			if(appNameMap==null){
				return "传统POS";
			}
			return (String) appNameMap.get("app_name");
		}
		
		// 查询手机流量充值列表
		public Page<Map<String, Object>> getFlowRechargeList(Map<String, String> params, PageRequest page) {
			List<Object> list = new ArrayList<Object>();
			StringBuffer sb = new StringBuffer();
			if (params != null) {
				if (StringUtils.isNotEmpty(params.get("mobile"))) {
					String mobile = params.get("mobile");
					list.add(mobile);
					sb.append(" and mobile=?");
				}

				// 商户名称查询
				if (StringUtils.isNotEmpty(params.get("merchant"))) {
					String merchant_name =params.get("merchant");
					sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
					list.add(merchant_name);
					list.add(merchant_name);
					list.add(merchant_name);
				}
				
				if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
					String merchant_no = params.get("merchant_no");
					list.add(merchant_no);
					sb.append(" and merchant_no=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("mobileStatus"))) {
					String mobileStatus = params.get("mobileStatus");
					list.add(mobileStatus);
					sb.append(" and status=?");
				}

				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin");
					sb.append(" and create_time >=? ");
					list.add(createTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd");
					sb.append(" and create_time <=? ");
					list.add(createTimeEnd);
				}
				
				if (StringUtils.isNotEmpty(params.get("amount"))) {
					String mobileStatus = params.get("amount");
					list.add(mobileStatus);
					sb.append(" and amount=?");
				}
			}
			String sql = "select * from increment_flow_charge  where 1=1" + sb.toString() + " order by create_time desc";
			return dao.find(sql, list.toArray(), page);

		}

		public Map<String, Object> getFlowRechargeDetail(Long id) {
			String sql = "select m.* , o.trans_status   from increment_flow_charge m LEFT JOIN increment_order o " +
					"ON m.journal=o.order_no where m.id=" + id;
			return dao.findFirst(sql);
		}
		
		public void enableAgentChange(){
			String sql = "select * from pos_merchant_agent4quartz";
			List<Map<String, Object>> list = dao.find(sql);
			if(list != null && !list.isEmpty()){
				for(Map<String, Object> map : list){
					String merchant_no = (String)map.get("merchant_no");
					String agent_no = (String)map.get("agent_no");
					String belong_to_agent = (String)map.get("belong_to_agent");
					String sale_name = (String)map.get("sale_name");

					try {
						enableMerchantAgent(merchant_no, agent_no, belong_to_agent, sale_name);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("商户号" + merchant_no + "启用新的一级代理商失败！");
					}
				}
			}else{
				log.info("没有需要激活的商户代理商修改");
			}
		}
		
		public void enableMerchantAgent(String merchant_no, String agent_no, String belong_to_agent, String sale_name) throws SQLException{
			
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);
				
				Map<String, Object> map= getMerchantInfoByMNo(merchant_no);
				Map<String, Object> nodeMap = findAgentNoId(belong_to_agent);
				Map<String, Object> agent = getAgentName(agent_no);//增加同步代理商名称
				if(map != null){
					log.info("=================修改一级代理商同步钱包==============================");
					// 同步到钱包
					String mobileNo = map.get("mobile_username") != null ? map.get("mobile_username").toString() : "";
					String agentNo = map.get("agent_no") != null ? map.get("agent_no").toString() : "";
					String appNo = map.get("app_no") != null ? map.get("app_no").toString() : "";
					String parentNode = nodeMap.get("self_node") != null ? nodeMap.get("self_node").toString() : "";
					
					map.put("mobile_username", mobileNo);
					map.put("agent_no", agent_no);
					map.put("app_no", appNo);
					map.put("parent_node", parentNode);
					map.put("agent_name", agent.get("agent_name"));
					map.put("sale_name", sale_name);
					String hmac = Md5.md5Str(mobileNo + appNo + agent_no + parentNode + Constants.BAG_HMAC);
					map.put("hmac", hmac);
					
					Http.send(SysConfig.value("transferAgent2Bag"), map, "UTF-8");
					log.info("=================修改一级代理商同步钱包结束==============================");
					
					String sql = "update pos_merchant set parent_node = ?, agent_no = ?, belong_to_agent = ?, sale_name = ? where merchant_no = ?";
					List<Object> listPosMerchant= new ArrayList<Object>();
					
					//取到所属代理商的self_node，将self_node保存到parent_node里面
					String self_node=(String)nodeMap.get("self_node");
					listPosMerchant.add(self_node);
					listPosMerchant.add(agent_no);
					listPosMerchant.add(belong_to_agent);
					listPosMerchant.add(sale_name);
					listPosMerchant.add(merchant_no);
					dao.updateByTranscation(sql, listPosMerchant.toArray(), conn);
					
					// 记录日志
					String insert_agent4log = "insert into pos_merchant_agent4log(merchant_no, oper_id, oper_name, content, oper_time) values (?, ?, ?, ?, now())";
					String content = "系统触发次月生效操作：修改商户：" + merchant_no + "代理商为：" + agent_no + "二级代理商为：" + belong_to_agent + "所属销售为：" + sale_name;
					dao.updateByTranscation(insert_agent4log, new Object[]{merchant_no, 0, "系统", content}, conn);
					
					// 删除记录
					String delete_agent4quartz = "delete from pos_merchant_agent4quartz where merchant_no = ?";
					dao.updateByTranscation(delete_agent4quartz, new Object[]{merchant_no}, conn);
					
					log.info("商户号：" + merchant_no + "启用新一级代理商成功！");
					conn.commit();// 提交事务
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("商户号：" + merchant_no + "启用新一级代理商失败！");
				conn.rollback();// 回滚事务
				throw new RuntimeException();
			} finally {
				conn.close();// 关闭连接
			}
		}
		
		// 检索待提升额度的商户信息
		public Page<Map<String, Object>> queryLiftMerchant(Map<String, String> params, final PageRequest pageRequest) {
			List<Object> list = new ArrayList<Object>();
			StringBuffer sf = new StringBuffer();
			if (params != null) {

				// 商户名称查询
				if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = "%" + params.get("merchant") + "%";
					sf.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");
					/*String merchant_name =params.get("merchant");
					sf.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");*/
					list.add(merchant_name);
					list.add(merchant_name);
					list.add(merchant_name);
				}

				// 代理名称
				if (StringUtils.isNotEmpty(params.get("agent_no"))) {
					if (!params.get("agent_no").equals("-1")) {
						String agent_no = params.get("agent_no");
						sf.append(" and a.agent_no = ? ");
						list.add(agent_no);
					}
				}
				
				// 进件类型
				if (StringUtils.isNotEmpty(params.get("add_type"))) {
					if (!params.get("add_type").equals("-1")) {
						String add_type = params.get("add_type");
						sf.append(" and m.add_type = ? ");
						list.add(add_type);
					}
				}
				
				// 设备类型
				if (StringUtils.isNotEmpty(params.get("pos_type"))) {
					if (!params.get("pos_type").equals("-1")) {
						String pos_type = params.get("pos_type");
						sf.append(" and m.pos_type = ? ");
						list.add(pos_type);
					}
				}
				
				// 审核人
				if (StringUtils.isNotEmpty(params.get("param_value"))) {
					if (!params.get("param_value").equals("-1")) {
						String param_value = params.get("param_value");
						sf.append(" and m.checker = ? ");
						list.add(param_value);
					}
				}

				if (StringUtils.isNotEmpty(params.get("create_time_begin"))) {
					String createTimeBegin = params.get("create_time_begin");
					sf.append(" and m.create_time >=? ");
					list.add(createTimeBegin);
				}

				if (StringUtils.isNotEmpty(params.get("create_time_end"))) {
					String createTimeEnd = params.get("create_time_end");
					sf.append(" and m.create_time <=? ");
					list.add(createTimeEnd);
				}
			}

			String sql = "select m.*,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name ,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee " + "from pos_merchant m , agent_info a,pos_merchant_fee f" + " where m.agent_no = a.agent_no and m.merchant_no = f.merchant_no and m.lift_status =1 " + sf.toString() + " order by m.id ";
			return dao.find(sql, list.toArray(), pageRequest);
		}
		
		/**
		 * 根据商户号更新商户信息(额度提升) 
		 * */
		public void liftPosMerchant(String merchantNo) throws SQLException {

			String sql = "update pos_merchant set lift_status = null where merchant_no = ?";
			dao.update(sql, new Object[]{merchantNo});

			try {
				cleanCacheService.cleanAllCache(merchantNo);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchantNo);
			}
		}
		
		/** 
		 * 根据商户号更新商户交易限额信息 
		 * */
		public void liftPosMerchantTransRule(String merchantNo, Map<String, String> params) throws SQLException {
			String sql = "update pos_merchant_trans_rule set single_max_amount=?,ed_max_amount=?,ed_card_max_items=?,ed_card_max_amount=? where merchant_no='" + merchantNo + "'";
			Object[] term = { params.get("single_max_amount"), params.get("ed_max_amount"), params.get("ed_card_max_items"), params.get("ed_card_max_amount") };
			dao.update(sql, term);

			try {
				cleanCacheService.cleanAllCache(merchantNo);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchantNo);
			}
		}
		
		/** 
		 * 额度提升日志插入 
		 * */
		public int insertLiftLog(String merchantNo, String createOperator, Map<String, String> params) throws SQLException {
			String examination_opinions = params.get("examination_opinions");
			String lift_status = params.get("lift_status");
			String sql = "insert into merchant_lift_log(merchant_no,lift_status,examination_opinions,operator,create_time) values(?,?,?,?,now())";
			Object[] logParams = {merchantNo, lift_status, examination_opinions, createOperator };
			int n = dao.update(sql, logParams);

			try {
				cleanCacheService.cleanAllCache(merchantNo);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchantNo);
			}
			return n;
		}
		
		public Page<Map<String, Object>> getIncrementList(Map<String, String> params, final PageRequest pageRequest) {
			
			List<Object> list = new ArrayList<Object>();
			StringBuffer sb = new StringBuffer();
			StringBuffer union1Str = new StringBuffer();
			StringBuffer union2Str = new StringBuffer();
			String originTable = "increment_mobile";
			String originJoinStr = " i.journal = u.order_no ";

			if (params != null) {
				// pos支付 时间限制
				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin");
					union1Str.append(" and io.create_time >=? ");
					list.add(createTimeBegin);
				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd");
					union1Str.append(" and io.create_time <=? ");
					list.add(createTimeEnd);
				}
				// 快捷支付时间限制
				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin");
					union2Str.append(" and io.create_time >=? ");
					list.add(createTimeBegin);
				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd");
					union2Str.append(" and io.create_time <=? ");
					list.add(createTimeEnd);
				}
				// 增值业务时间限制
				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin");
					sb.append(" and i.create_time >=? ");
					list.add(createTimeBegin);
				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd");
					sb.append(" and i.create_time <=? ");
					list.add(createTimeEnd);
				}
				
				if (StringUtils.isNotEmpty(params.get("mobile"))) {
					String mobile = params.get("mobile");
					list.add(mobile);
					sb.append(" and mobile=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
					String merchant_no = params.get("merchant_no");
					list.add(merchant_no);
					sb.append(" and i.merchant_no=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("mobileStatus"))) {
					String mobileStatus = params.get("mobileStatus");
					list.add(mobileStatus);
					sb.append(" and i.status=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("status"))) {
					String status = params.get("status");
					list.add(status);
					sb.append(" and i.status=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("credit_no"))) {
					String credit_no = params.get("credit_no");
					list.add(credit_no);
					sb.append(" and i.credit_no=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("creditStatus"))) {
					String creditStatus = params.get("creditStatus");
					list.add(creditStatus);
					sb.append(" and i.status=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("amount"))) {
					String amount = params.get("amount");
					list.add(amount);
					sb.append(" and i.trans_amount=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("order_no"))) {
					String order_no = params.get("order_no");
					list.add(order_no);
					sb.append(" and i.order_no=?");
				}
				
				if (StringUtils.isNotEmpty(params.get("carnumber"))) {
					String carnumber = params.get("carnumber");
					list.add(carnumber);
					sb.append(" and i.carnumber=?");
				}
				
				// 判断增值业务类型
				String trans_type = params.get("trans_type");
				if("mobile".equals(trans_type)){
					originTable = "increment_mobile";
				}else if("dataflow".equals(trans_type)){
					originTable = "increment_flow_charge";
				}else if("credit".equals(trans_type)){
					originTable = "increment_credit";
				}else if("traffic".equals(trans_type)){
					originTable = "increment_wzdb";
					originJoinStr = " i.order_no = u.order_no ";
				}
			}
			
			// 用增值订单表和pos支付和快捷支付的两张表左连接
			// 然后进行union操作，查询到增值订单表对应的交易记录，包括收单机构等信息
			String sql = "select i.*,pm.merchant_name,ao.acq_cnname,ai.agent_name,u.order_no,u.pay_order_no,u.trans_status " + 
						 " from " + originTable + " i " + 
						 " left join pos_merchant pm on i.merchant_no = pm.merchant_no " +
						 " left join ( " +
						 "select io.order_no,io.pay_order_no,io.trans_status,fti.acq_enname,fti.order_no as order_id " +
						 " from increment_order io "+
						 " left join fastpay_trans_info fti on io.pay_order_no = fti.order_no" +
						 " where 1=1 and io.pay_method = 'QUICK' "+
						 union1Str.toString() +
						 " union all "+
						 "select io.order_no,io.pay_order_no,io.trans_status,ti.acq_enname,ti.id as order_id " +
						 " from increment_order io "+
						 " left join trans_info ti on io.pay_order_no = ti.id " +
						 " where 1=1 and io.pay_method = 'POS' " +
						 union2Str.toString() +
						 ") u on " +
						 originJoinStr +
						 " left join acq_org ao on u.acq_enname = ao.acq_enname " + 
						 " left join agent_info ai on pm.agent_no = ai.agent_no " +
						 " where 1=1 " + 
						 sb.toString() + 
						 " order by i.create_time desc";
			return dao.find(sql, list.toArray(), pageRequest);
		}
		
		
		//将已冻结的状态改为成功的状态的定时任务
		public void freezeStatusChange(String nowDate,String nowTime) throws SQLException{
			String sql = "  select trans_id from trans_info_freeze_log WHERE freeze_settle_time = CURDATE() AND trans_id IS NOT NULL GROUP BY trans_id ORDER BY create_time desc ";
			String sqlf = " SELECT fast_id FROM trans_info_freeze_log WHERE freeze_settle_time = CURDATE() AND fast_id IS NOT NULL GROUP BY fast_id ORDER BY create_time desc";

			List<Map<String, Object>> mapt = dao.find(sql);
			for (Map<String, Object> m : mapt ) {
				String sql2 = "update trans_info set freeze_status='2',merchant_settle_date =?  where freeze_status='1' and bag_settle != 1 and id = ?";
				dao.update(sql2, new Object[]{nowDate+" "+nowTime,m.get("trans_id")});
					
			}
			List<Map<String, Object>> mapf = dao.find(sqlf);
			for (Map<String, Object> m : mapf ) {
				String sql3 = "update fastpay_trans_info set freeze_status='2',settle_date = ? where freeze_status='1'  and bag_settle != 1 " +
						"and id = ?";
				dao.update(sql3,new Object[]{nowDate,m.get("fast_id")});
					
			}
			
		}
		

		//将已冻结的状态改为成功的状态的定时任务
		public void freezeStatusChangeNew(String nowDate,String nowTime) throws SQLException{
			String sql = " select tf.trans_id from trans_info_freeze_new_log tf INNER JOIN (select MAX(id) id,trans_id from trans_info_freeze_new_log where oper_type = 0 and trans_type = 0 group by trans_id) t on t.id = tf.id where settle_time = ?";
			String sqlf = " select tf.trans_id from trans_info_freeze_new_log tf INNER JOIN (select MAX(id) id,trans_id from trans_info_freeze_new_log where oper_type = 0 and trans_type = 1 group by trans_id) t on t.id = tf.id where settle_time = ?";
			
			Connection conn = dao.getConnection();
			conn.setAutoCommit(false);
			try {
				List<Map<String, Object>> mapt = dao.find(sql,nowDate);
				for (Map<String, Object> m : mapt ) {
					String sql2 = "update trans_info set freeze_status='2',merchant_settle_date =?  where freeze_status='1' and bag_settle != 1 and id = ?";
					int res = dao.updateByTranscation(sql2, new Object[]{nowDate+" "+nowTime,m.get("trans_id")},conn);
					if(res > 0){
						String insertSql = "insert into trans_info_freeze_new_log (trans_id,trans_type,oper_type,oper_reason,freeze_way,freeze_day,oper_time,oper_id,oper_name,settle_time) values (?,?,?,?,?,?,?,?,?,?)";
						Object[] o = {m.get("trans_id"),0,1,"系统自动解冻",null,null,DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"),0,"sys",DateUtils.format(DateUtils.addDate(new Date(),1),"yyyy-MM-dd")};
						dao.updateByTranscation(insertSql,o,conn);
					}
				}
				List<Map<String, Object>> mapf = dao.find(sqlf,nowDate);
				for (Map<String, Object> m : mapf ) {
					String sql3 = "update fastpay_trans_info set freeze_status='2',settle_date = ? where freeze_status='1'  and bag_settle != 1 " +
							"and id = ?";
					int res = dao.updateByTranscation(sql3,new Object[]{nowDate,m.get("trans_id")},conn);
					if(res > 0){
						String insertSql = "insert into trans_info_freeze_new_log (trans_id,trans_type,oper_type,oper_reason,freeze_way,freeze_day,oper_time,oper_id,oper_name,settle_time) values (?,?,?,?,?,?,?,?,?,?)";
						Object[] o = {m.get("trans_id"),1,1,"系统自动解冻",null,null,DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"),0,"sys",DateUtils.format(DateUtils.addDate(new Date(),1),"yyyy-MM-dd")};
						dao.updateByTranscation(insertSql,o,conn);
					}
				}
				conn.commit();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				conn.rollback();
			}
			
			
		}
		
		
		/**
		 * 查询额度修改记录
		 * @param params
		 * @param pageRequest
		 * @return
		 */
		public Page<Map<String, Object>> getLimitChangeList(Map<String, String> params, final PageRequest pageRequest) {
			StringBuffer sb = new StringBuffer();
			List<String> list = new ArrayList<String>();
			
			String create_time_begin = params.get("create_time_begin");
			if(!StringUtil.isEmpty(create_time_begin)){
				list.add(create_time_begin);
				sb.append(" and create_time >= ? ");
			}
			
			String create_time_end = params.get("create_time_end");
			if(!StringUtil.isEmpty(create_time_end)){
				list.add(create_time_end);
				sb.append(" and create_time <= ? ");
			}
			
			String agent_no = params.get("agent_no");
			if(!StringUtil.isEmpty(agent_no)){
				list.add("%" + agent_no + "%");
				sb.append(" and agent_no like ? ");
			}
			
			String merchant_no = params.get("merchant_no");
			if(!StringUtil.isEmpty(merchant_no)){
				list.add("%" + merchant_no + "%");
				sb.append(" and merchant_no like ? ");
			}
			
			String sql = "select * from limit_change_log where 1 = 1 " + sb.toString();
			return dao.find(sql, list.toArray(), pageRequest);
		}


		/**
		 * 增加限额修改记录
		 * @param params
		 */
		public void limitChangeCreate(Map<String, String> params) {
			String agent_no = params.get("agent_no");
			String merchant_no = params.get("merchant_no");
			String ed_max_amount = params.get("ed_max_amount");
			String single_max_amount = params.get("single_max_amount");
			String ed_card_max_amount = params.get("ed_card_max_amount");
			String ed_card_max_items = params.get("ed_card_max_items");
			String remark = params.get("remark");
			
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);
				
				log.info("========修改代理商旗下商户限额开始===========");
				if(StringUtils.isNotEmpty(agent_no)){
					
					List<String> agentArray = new ArrayList<String>();
					
					String cond1Str ="";
					if(StringUtils.isNotEmpty(ed_max_amount)){
						agentArray.add(ed_max_amount);
						cond1Str += " ed_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(single_max_amount)){
						agentArray.add(single_max_amount);
						cond1Str += " single_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(ed_card_max_amount)){
						agentArray.add(ed_card_max_amount);
						cond1Str += " ed_card_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(ed_card_max_items)){
						agentArray.add(ed_card_max_items);
						cond1Str += " ed_card_max_items = ?, ";
					}
					cond1Str = cond1Str.substring(0, cond1Str.lastIndexOf(","));
					
//					String cond2Str = "";
//					for(String agentNo : agent_no.split(";")){
//						cond2Str += "?,";
//						agentArray.add(agentNo);
//					}
//					cond2Str = cond2Str.substring(0, cond2Str.lastIndexOf(","));

					String merchantNoStr = buildMerchantNoStr(agentArray, agent_no);
					if(StringUtils.isNotBlank(merchantNoStr)){
						String agent_sql = "update pos_merchant_trans_rule set " + cond1Str + " where merchant_no in (" + merchantNoStr + ")";
						dao.updateByTranscation(agent_sql, agentArray.toArray(), conn);
					}
				}
				log.info("========修改代理商旗下商户限额结束===========");
				
				log.info("========修改商户限额开始===========");
				if(StringUtils.isNotEmpty(merchant_no)){
				
					List<String> merchantArray = new ArrayList<String>();
					
					String cond1Str ="";
					if(StringUtils.isNotEmpty(ed_max_amount)){
						merchantArray.add(ed_max_amount);
						cond1Str += " ed_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(single_max_amount)){
						merchantArray.add(single_max_amount);
						cond1Str += " single_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(ed_card_max_amount)){
						merchantArray.add(ed_card_max_amount);
						cond1Str += " ed_card_max_amount = ?, ";
					}
					if(StringUtils.isNotEmpty(ed_card_max_items)){
						merchantArray.add(ed_card_max_items);
						cond1Str += " ed_card_max_items = ?, ";
					}
					cond1Str = cond1Str.substring(0, cond1Str.lastIndexOf(","));
					
					String cond2Str = "";
					for(String merchantNo : merchant_no.split(";")){
						cond2Str += "?,";
						merchantArray.add(merchantNo);
					}
					cond2Str = cond2Str.substring(0, cond2Str.lastIndexOf(","));
					
					String merchant_sql = "update pos_merchant_trans_rule set " + cond1Str + " where merchant_no in (" + cond2Str + ")";
					dao.updateByTranscation(merchant_sql, merchantArray.toArray(), conn);	
				}
				log.info("========修改商户限额结束===========");
				
				log.info("========记录商户限额修改开始===========");
				List<String> logArray = new ArrayList<String>();
				logArray.add(agent_no);
				logArray.add(merchant_no);
				
				String cond1Str ="";
				String cond2Str ="";
				if(StringUtils.isNotEmpty(ed_max_amount)){
					logArray.add(ed_max_amount);
					cond1Str += " ed_max_amount, ";
					cond2Str += " ?, ";
				}
				if(StringUtils.isNotEmpty(single_max_amount)){
					logArray.add(single_max_amount);
					cond1Str += " single_max_amount, ";
					cond2Str += " ?, ";
				}
				if(StringUtils.isNotEmpty(ed_card_max_amount)){
					logArray.add(ed_card_max_amount);
					cond1Str += " ed_card_max_amount, ";
					cond2Str += " ?, ";
				}
				if(StringUtils.isNotEmpty(ed_card_max_items)){
					logArray.add(ed_card_max_items);
					cond1Str += " ed_card_max_items, ";
					cond2Str += " ?, ";
				}
				
				BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				logArray.add(bu.getId().toString());
				logArray.add(bu.getRealName());
				
				logArray.add(remark);

				String log_sql = "insert into limit_change_log(agent_no,merchant_no," + cond1Str + "oper_id,oper_name,create_time,remark) values(?,?," + cond2Str + "?,?,now(),?)";
				dao.updateByTranscation(log_sql, logArray.toArray(), conn);
				log.info("========记录商户限额修改结束===========");
				
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					conn.rollback();
					log.error("========商户限额修改失败，全部操作回滚===========");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				throw new RuntimeException();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		private String buildMerchantNoStr(List<String> agentArray, String agent_no){

			List<String> agentNoList = new ArrayList<String>();
			String agentNoStr = "";
			for(String agentNo : agent_no.split(";")){
				agentNoStr += " ?, ";
				agentNoList.add(agentNo);
			}
			agentNoStr = agentNoStr.substring(0, agentNoStr.lastIndexOf(","));

			String sql = "select merchant_no from pos_merchant where agent_no in (" + agentNoStr + ")";
			List<Map<String, Object>> merchantNoList = dao.find(sql, agentNoList.toArray());

			if(merchantNoList != null && merchantNoList.size() > 0){
				String merchantNoStr = "";
				for(Map<String, Object> merchantNo : merchantNoList){
					if(merchantNo.get("merchant_no") != null){
						merchantNoStr += " ?, ";
						agentArray.add(merchantNo.get("merchant_no").toString());
					}
				}
				return  merchantNoStr.substring(0, merchantNoStr.lastIndexOf(","));
			}

			return "";
		}
		
		/**
		 * 根据记录的id获取修改详情
		 * @param id
		 * @return
		 * @throws SQLException
		 */
		public Map<String, Object> getLimitChangeById(Long id) {
			String sql = "select * from limit_change_log where id = ?";
			return dao.findFirst(sql, id);
		}


		public int addRealNameAuthentication(Map<String, String> treeMap) throws SQLException {
			String merName = treeMap.get("merName");
			String merId = treeMap.get("merId");
			String orderTime = treeMap.get("orderTime");
			String orderNumber = treeMap.get("orderNumber");
			String cardNum = treeMap.get("cardNum");
			String userName = treeMap.get("userName");
			String idCard = treeMap.get("idCard");
			String phoneNum = treeMap.get("phoneNum");
			
			String sql = "insert into real_name_authentication(merName, merId, orderTime, orderNumber, cardNum, userName, idCard, phoneNum) values (?,?,?,?,?,?,?,?)";
		
			return dao.update(sql, new Object[]{merName, merId, orderTime, orderNumber, cardNum, userName, idCard, phoneNum});
		}


		public Map<String, Object> findRealNameAuthentication(Map<String, String> treeMap) {
			String cardNum = treeMap.get("cardNum");
			String userName = treeMap.get("userName");
			String idCard = treeMap.get("idCard");
			String phoneNum = treeMap.get("phoneNum");
			
			String sql = "select * from real_name_authentication  where cardNum = ? and userName = ? and idCard = ? and phoneNum = ? ";
			
			return dao.findFirst(sql, new Object[]{cardNum, userName, idCard, phoneNum});
		}


		public int updateRealNameAuthentication(String orderNumber, String respCode, String respMsg) throws SQLException {
			String sql = "update real_name_authentication set respCode = ?, respMsg = ? where orderNumber = ?";
			return dao.update(sql, new Object[]{respCode, respMsg, orderNumber});
		}
		
		/**
		 * 获取需要商户限额审核的记录
		 * @param params
		 * @param pageRequest
		 * @return
		 */
		public Page<Map<String, Object>> getRaiseCheckList(Map<String, String> params, final PageRequest pageRequest) {
			StringBuffer sb = new StringBuffer();
			List<String> list = new ArrayList<String>();
			
			String create_time_begin = params.get("create_time_begin");
			if(!StringUtil.isEmpty(create_time_begin)){
				list.add(create_time_begin);
				sb.append(" and mr.create_time >= ? ");
			}
			
			String create_time_end = params.get("create_time_end");
			if(!StringUtil.isEmpty(create_time_end)){
				list.add(create_time_end);
				sb.append(" and mr.create_time <= ? ");
			}
			
			String agent_no = params.get("agent_no");
			if(!StringUtil.isEmpty(agent_no) && !"-1".equals(agent_no)){
				list.add("%" + agent_no + "%");
				sb.append(" and mr.agent_no like ? ");
			}
			
			String merchant_no = params.get("merchant_no");
			if(!StringUtil.isEmpty(merchant_no)){
				list.add("%" + merchant_no + "%");
				sb.append(" and mr.merchant_no like ? ");
			}
			
			String check_status = params.get("check_status");
			if(!StringUtil.isEmpty(check_status) && !"-1".equals(check_status)){
				list.add(check_status);
				sb.append(" and mr.check_status = ? ");
			}
			
			// 审核人
			if (StringUtils.isNotEmpty(params.get("checker"))) {
				if (!params.get("checker").equals("-1")) {
					String checker = params.get("checker");
					sb.append(" and mr.checker = ? ");
					list.add(checker);
				}
			}
			
			String sql = "select mr.id,mr.merchant_no,mr.agent_no,mr.checker,pm.merchant_short_name,pm.merchant_name,pm.real_flag,ai.agent_name,mr.check_status,mr.create_time from merchant_raise mr inner join pos_merchant pm on mr.merchant_no = pm.merchant_no inner join agent_info ai on mr.agent_no = ai.agent_no where mr.check_status in(3, 4, 5) " + sb.toString();
			return dao.find(sql, list.toArray(), pageRequest);
		}
		
		public Map<String, Object> queryMerchantInfoByMerchantNo(String merchantNo) {
			String sql = "select m.*,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee,r.single_max_amount,r.ed_max_amount, r.ed_card_max_amount, r.ed_card_max_items, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join pos_merchant_fee f on m.merchant_no = f.merchant_no   left join pos_merchant_trans_rule r on m.merchant_no = r.merchant_no "
					+ " left join agent_info a on m.agent_no = a.agent_no where m.merchant_no =" + merchantNo;
			return dao.findFirst(sql);
		}
		
		public Map<String, Object> getMerchantRaise4CheckById(Long id){
			String sql = "select mr.id as mrid,mr.merchant_no,mr.agent_no,mr.batch_no,mro.agent_opinion,mro.check_opinion from merchant_raise mr left join merchant_raise_opinion mro on mr.batch_no = mro.batch_no and mr.id = mro.raise_id where mr.id = ?";
			return dao.findFirst(sql, id);
		}

		public List<Map<String, Object>> getRaiseCheckDetailList(Long id) {
			String sql = "select id, raise_key, raise_content, raise_status, remark from merchant_raise_detail where raise_id = ?";
			return dao.find(sql, id);
		}
		
		public List<Map<String, Object>> getRaiseCheckOpinionList(Long id){
			String sql = "select id, check_opinion, agent_opinion, batch_no from merchant_raise_opinion where raise_id = ?";
			return dao.find(sql, id);
		}

		public void raiseCheck(Map<String, String> params) throws SQLException {
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);
				
				String merchantRaiseCategory = SysConfig.value("merchantRaiseCategory");
				String mrid = params.get("mrid");
				String id = params.get("id");
				String merchantNo = params.get("merchant_no");
				String batchNo = params.get("batch_no");
				String[] categories = merchantRaiseCategory.split(",");
				
				// 修改对应的选择是否通过
				String detailSql = "update merchant_raise_detail set raise_status = ? where raise_key = ?";
				int failNum = 0;
				for(String category : categories){
					String raise_status = params.get(category);
					if(StringUtils.isNotBlank(raise_status)){
						dao.updateByTranscation(detailSql, new Object[]{raise_status, category}, conn);
						if(!"0".equals(raise_status))
							failNum += 1;
					}
				}
				
				// 修改商户限额
				String merchantTransRuleSql = "update pos_merchant_trans_rule set single_max_amount=?,ed_max_amount=?,ed_card_max_items=?,ed_card_max_amount=? where merchant_no= ? ";
				Object[] term = { params.get("single_max_amount"), params.get("ed_max_amount"), params.get("ed_card_max_items"), params.get("ed_card_max_amount") , merchantNo};
				dao.updateByTranscation(merchantTransRuleSql, term, conn);
				
				
				// 查询是否所有选项全部通过
				//String checkStatusSql = "select count(id) as total from merchant_raise_detail where raise_id = ? and raise_status = '0' ";
				//Map<String, Object> checkStatusMap = dao.findFirst(checkStatusSql, mrid);
				
				String checkStatus = failNum == 0 ? "5" : "4";
				
				// 修改审核限额状态
				String updateMerchantRaise = "update merchant_raise set check_status = ? where id = ? ";
				String updateMerchant = "update pos_merchant set lift_status = ? where id = ? ";
				
				dao.updateByTranscation(updateMerchantRaise, new Object[]{checkStatus, mrid}, conn);
				dao.updateByTranscation(updateMerchant, new Object[]{checkStatus, id}, conn);
				
				// 记录审核意见
				String check_opinion = params.get("check_opinion");
				String opinionSql = "update merchant_raise_opinion set check_opinion = ? where raise_id = ? and batch_no = ?";
				dao.updateByTranscation(opinionSql, new Object[]{check_opinion, mrid, batchNo}, conn);
				
				conn.commit();// 提交事务
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();// 回滚事务
				throw new RuntimeException();
			} finally {
				conn.close();// 关闭连接
			}
			
		}
		
		// 冻结/解冻商户
		public int merFreeze(Map<String, String> params) throws SQLException {

			String merchant_no = params.get("merchant_no");
			String freeze_status = params.get("freeze_status");
//			String open_status = params.get("open_status");
			String update_pos_merchant = "update pos_merchant set freeze_status = ? where merchant_no = ?";
//			String update_pos_merchant_log = "insert into pos_merchant_log(merchant_no,open_status,freeze_status,oper_user_id,oper_user_name,oper_type,operate_time) values(?,?,?,?,?,?,now())";
			
//			BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
			
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);
			
				int updateRows = dao.updateByTranscation(update_pos_merchant, new Object[]{freeze_status, merchant_no}, conn);
//				dao.updateByTranscation(update_pos_merchant_log, new Object[]{merchant_no, open_status, freeze_status, bu.getId(), bu.getRealName(), "1".equals(freeze_status) ? "3" : "4"}, conn);
				addPosMerchantLogNew(merchant_no, conn, "1".equals(freeze_status) ? "3" : "4");
				
				conn.commit();// 提交事务
				
				try {
					cleanCacheService.cleanAllCache(merchant_no);
				} catch (Exception e) {
					log.error(e.getMessage() + "|" + merchant_no);
				}
				
				return updateRows;
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();// 回滚事务
				return 0;
			} finally {
				conn.close();// 关闭连接
			}
				
		}
		
		// 根据Id查询商户信息
		public void addPosMerchantLogNew(String merchant_no, Connection conn, String oper_type) throws SQLException {
			String sql = "select m.*,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee,r.single_max_amount,r.ed_max_amount, r.ed_card_max_amount, r.ed_card_max_items, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join pos_merchant_fee f on m.merchant_no = f.merchant_no   left join pos_merchant_trans_rule r on m.merchant_no = r.merchant_no "
					+ " left join agent_info a on m.agent_no = a.agent_no where m.merchant_no = ? ";
			Map<String, Object> params = dao.findFirst(sql, new Object[]{merchant_no});
			
			List<Object> list = new ArrayList<Object>();
			list.add(params.get("merchant_no"));
			list.add(params.get("merchant_name"));
			list.add(params.get("merchant_short_name"));
			list.add(params.get("lawyer"));
			list.add(params.get("merchant_type"));
			list.add(params.get("mobile_username"));
			list.add(params.get("open_status"));
			list.add(params.get("agent_no"));
			list.add(params.get("address"));
			list.add(params.get("sale_address"));
			list.add(params.get("province"));
			list.add(params.get("city"));
			list.add(params.get("link_name"));
			list.add(params.get("phone"));
			list.add(params.get("email"));
			list.add(params.get("mcc"));
			list.add(params.get("sale_name"));
			list.add(params.get("settle_cycle"));
			list.add(params.get("bank_name"));
			list.add(params.get("account_no"));
			list.add(params.get("account_name"));
			list.add(params.get("cnaps_no"));
			list.add(params.get("account_type"));
			list.add(params.get("attachment"));
			list.add(params.get("real_flag"));
			list.add(params.get("id_card_no"));
			list.add(params.get("terminal_count"));
			list.add(params.get("main_business"));
			list.add(params.get("remark"));
			list.add(params.get("code_word"));
			list.add(params.get("belong_to_agent"));
			String trans_time_start = params.get("trans_time_start") != null ? params.get("trans_time_start").toString() : null;
			String trans_time_end = params.get("trans_time_end") != null ? params.get("trans_time_end").toString() : null;
			list.add(trans_time_start);
			list.add(trans_time_end);
			// 费率
			String fee_type = params.get("fee_type") != null ? params.get("fee_type").toString() : "";
			String rate1 = params.get("rate1") != null ? params.get("rate1").toString() : null;
			String rate2 = params.get("rate2") != null ? params.get("rate2").toString() : null;
			String ladder_min = params.get("ladder_min") != null ? params.get("ladder_min").toString() : "";
			String ladder_value = params.get("ladder_value") != null ? params.get("ladder_value").toString() : "";
			String ladder_max = params.get("ladder_max") != null ? params.get("ladder_max").toString() : "";

			list.add(fee_type);
			if ("RATIO".equals(fee_type)) {
				list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
				list.add(null);
				list.add(null);
				list.add(params.get("fee_single_amount"));
				list.add(null);

			} else if ("CAPPING".equals(fee_type)) {
				list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
				list.add((rate1 == null || rate2 == null) ? null : new BigDecimal(rate2).divide(new BigDecimal(rate1).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP));
				list.add((rate2 == null || rate2.trim().length() == 0) ? null : new BigDecimal(rate2));
				list.add(params.get("fee_single_amount"));
				list.add(null);

			} else if ("LADDER".equals(fee_type)) {

				ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
				ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

				String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;

				list.add(null);
				list.add(null);
				list.add(null);
				list.add(params.get("fee_single_amount"));
				list.add(ladder_fee);

			} else {
				list.add(null);
				list.add(null);
				list.add(null);
				list.add(null);
				list.add(null);
			}
			/*
			 * list.add(params.get("fee_type")); list.add(params.get("fee_rate"));
			 * list.add(params.get("fee_cap_amount"));
			 * list.add(params.get("fee_max_amount"));
			 */
			// list.add(params.get("fee_single_amount"));
			// list.add(params.get("ladder_fee"));
			list.add(params.get("terminal_no"));
			// 交易规则

			list.add(params.get("single_max_amount"));
			String ed_max_amount = params.get("ed_max_amount") != null ? params.get("ed_max_amount").toString() : "";
			String single_max_amount = params.get("single_max_amount") != null ? params.get("single_max_amount").toString() : "";
			String ed_card_max_amount = params.get("ed_card_max_amount") != null ? params.get("ed_card_max_amount").toString() : "";
			String ed_card_max_items = params.get("ed_card_max_items") != null ? params.get("ed_card_max_items").toString() : "";
			list.add((single_max_amount == null || single_max_amount.trim().length() == 0) ? null : new BigDecimal(single_max_amount));
			list.add((ed_max_amount == null || ed_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_max_amount));
			list.add((ed_card_max_items == null || ed_card_max_items.trim().length() == 0) ? null : new BigDecimal(ed_card_max_items));
			list.add((ed_card_max_amount == null || ed_card_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_card_max_amount));

			// list.add(params.get("ed_max_amount"));
			// list.add(params.get("ed_card_max_items"));
			// list.add(params.get("ed_card_max_amount"));
			// list.add(params.get("ed_total_amount"));
			// list.add(params.get("operate_time"));
			// 操作员信息
			BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
			list.add(params.get("oper_desc"));
			list.add(bu.getId());
			list.add(bu.getRealName());
			list.add(oper_type);
			list.add(params.get("my_settle"));
			
			list.add(params.get("clear_card_no"));
			list.add(params.get("pos_type"));
			list.add(params.get("add_type"));
			list.add(params.get("trans_cancel"));
			list.add(params.get("pay_method"));
			list.add(params.get("bag_settle"));
			list.add(params.get("bus_license_no"));
			list.add(params.get("app_no"));
			
			String insert = "INSERT INTO pos_merchant_log (" + "merchant_no, " + "merchant_name, " + "merchant_short_name, " + "lawyer, " + "merchant_type, " + "mobile_username, " + "open_status, " + "agent_no, " + "address, " + "sale_address, " + "province, " + "city, " + "link_name, " + "phone, " + "email, " + "mcc, " + "sale_name, " + "settle_cycle, " + "bank_name, " + "account_no, "
					+ "account_name, " + "cnaps_no, " + "account_type, " + "attachment, " + "real_flag, " + "id_card_no, " + "terminal_count, " + "main_business, " + "remark, " + "code_word, " + "belong_to_agent, " + "trans_time_start, " + "trans_time_end, " + "fee_type, " + "fee_rate, " + "fee_cap_amount, " + "fee_max_amount, " + "fee_single_amount, " + "ladder_fee, " + "terminal_no, "
					+ "single_max_amount, " + "ed_max_amount, " + "ed_card_max_items, " + "ed_card_max_amount, " + "ed_total_amount, " + "operate_time, " + "oper_desc, " + "oper_user_id, " + "oper_user_name, " + "oper_type, " + "my_settle,"
					+ "clear_card_no, pos_type, add_type, trans_cancel, pay_method, bag_settle, bus_license_no, app_no "
					+")" + "VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";

			dao.updateByTranscation(insert, list.toArray(), conn);
		}
		
		/**
		 * 根据代理商编号查询是否从属关系
		 * @param agentNo
		 * @return
		 * @throws SQLException
		 */
		public boolean checkAgentExistsAndSatisfied(String agent_no, String belong_to_agent) throws SQLException {
			String sql = "select id from agent_info where self_node like (select concat(self_node,'%') from agent_info where agent_no = ?) and agent_no = ?";
			List<Map<String, Object>> list = dao.find(sql, new Object[]{agent_no, belong_to_agent});
			return list != null && list.size() > 0;
		}

}
