package com.eeepay.boss.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.axis.impl.AxisCleanCacheServiceImpl;
import com.eeepay.boss.controller.RunnableController;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.md5.Md5;

/**
 * 
 * @author 王帅
 * @date 2014年11月26日11:03:21
 * @see 商户自动审核操作事物控制
 *MRCTransctionService(MerchantRepeatCheckTransctionService)
 */
@Service
public class MRCTransctionService {

	@Resource
	private AxisCleanCacheServiceImpl cleanCacheService;
	
	@Resource
	private MerchantService merchantService;
	
	@Resource
	private Dao dao;
	
	private static final Logger log = LoggerFactory.getLogger(MRCTransctionService.class);
	
	/**
	 * 商户复审通过
	 * @param id
	 * @param createPerson
	 * @param merchant
	 * @return
	 * @throws SQLException
	 */
	public int checkRepeatSucess(Long id, String createPerson, Map<String, Object> merchant)  throws SQLException{
		log.info("MRCTransctionService merchantRepeat checkRepeatSucess start...");
		int repeatSucessCount = 0;
		if(id != null && id > 0){
			if(merchant != null){
				if(merchant.get("open_status") != null && !"".equals(merchant.get("open_status").toString()) && "8".equals(merchant.get("open_status").toString())){
					Connection conn = null;
					try {
						conn = dao.getConnection();
						conn.setAutoCommit(false);
						int  sucessCount = merchantRepeatSF(String.valueOf(id),String.valueOf(1),conn);
						if(sucessCount > 0){
							log.info("MRCTransctionService checkRepeatSucess 修改商户状态成功 !");
							/*String mobileNo = (String) merchant.get("mobile_username");
							String realName = (String) merchant.get("account_name");
							String idcard = (String) merchant.get("id_card_no");
							String password = (String) merchant.get("mobile_password");
							String account_no = (String) merchant.get("account_no");
							String account_name = (String) merchant.get("account_name");
							String bank_name = (String) merchant.get("bank_name");*/
							String pos_type = (String)merchant.get("pos_type"); // 只有移联商通才有激活码
							/*Map<String, String> tem = new HashMap<String, String>();
							tem.put("mobileNo", mobileNo);
							tem.put("password", password);
							tem.put("realName", realName);
							tem.put("idcard", idcard);
							tem.put("account_no", account_no);
							tem.put("account_name", account_name);
							tem.put("bank_name", bank_name);
							tem.put("pos_type", pos_type);*/
							Map<String, Object> map2= merchantService.getMerchantById(Long.valueOf(id));
							RunnableController rc = new RunnableController(map2);
							new Thread(rc).start();
							if ("4".equals(pos_type)) {
								Map<String, Object> codeMap = codeVerification(merchant.get("merchant_no").toString());
								if(codeMap != null){
									log.info("MRCTransctionService checkRepeatSucess 机具类型符号要求，开始激活码激活操作 !");
									// 审核成功，更新激活状态为已使用，将商户编号更新到使用者
									String keycode = (String)codeMap.get("keycode");
									int  keyCount = updateSucessCodeState(keycode, conn);
									if(keyCount > 0){
										log.info("MRCTransctionService checkRepeatSucess 机具类型符号要求，激活码成功激活 !");
									}else{
										log.info("MRCTransctionService checkRepeatSucess 机具类型符号要求，激活码激活失败 !");
										repeatSucessCount = 5;
									}
								}
							}
							
							
							if(repeatSucessCount != 5){
								List<Map<String, Object>> merchantUser = getMerchantUser(String.valueOf(merchant.get("merchant_no")));
								if (merchantUser.size() == 0) {
									Map<String, String> Umap = new HashMap<String, String>();
									Umap.put("merchant_name", merchant.get("merchant_name").toString());
									Umap.put("email", merchant.get("email").toString());
									//添加一个操作员。
									int addUser = merchantUserAdd((String) merchant.get("merchant_no"), createPerson, Umap, conn);
									if(addUser > 0){
										repeatSucessCount = 8; 
									}else{
										repeatSucessCount = 7; 
										conn.rollback();
										return repeatSucessCount;
									}
								} 
								
								  /*龙宝接口同步*/
								if(repeatSucessCount == 8 && merchant != null  && !"".equals(merchant.get("agent_no").toString()) && 
										("6397".equals(merchant.get("agent_no").toString()) || "9982".equals(merchant.get("agent_no").toString()))){
									if(!"".equals(merchant.get("belong_to_agent").toString())){
										Map<String, Object> belongMap = getAgentPhoneByAgentNo(merchant.get("belong_to_agent").toString());
										Map<String, String> longbaoInfo = new HashMap<String, String>();
										if(belongMap != null){
											longbaoInfo.put("parent_phone", belongMap.get("agent_link_tel").toString());
										}else{
											longbaoInfo.put("parent_phone", "0");
										}
										String merchant_id =  merchant.get("id").toString();
										longbaoInfo.put("merchant_id", merchant_id);  //商户ID
										longbaoInfo.put("name", merchant.get("link_name").toString());  //注册人的姓名
										longbaoInfo.put("phone", merchant.get("phone").toString()); //注册人的手机号码
										longbaoInfo.put("email", merchant.get("email").toString()); //注册人的邮箱
										longbaoInfo.put("idcard", merchant.get("id_card_no").toString()); //注册人的身份证号码
										longbaoInfo.put("parent_phone", merchant.get("parent_phone").toString()); //推荐人的手机号码，（没有不填写或者写数字0）
										longbaoInfo.put("card_number", merchant.get("cnaps_no").toString());  //注册人的银行卡号
										longbaoInfo.put("bank_name", merchant.get("bank_name").toString());  //开户行名称，银行的中文名
										longbaoInfo.put("deposit", merchant.get("bank_name").toString()); //开户行地址(省市区+详细地址)
										longbaoInform(longbaoInfo,conn);
									}
								}
								
								if(repeatSucessCount == 8){
									Map<String, String> logMap = new HashMap<String, String>();
									logMap.put("examination_opinions", "");
									logMap.put("open_status", "1");
									int examinatioinL = insertExaminationsLog((String) merchant.get("merchant_no"), createPerson,logMap,conn);
									if(examinatioinL > 0){
										conn.commit();
									}
								}
							}else{
								log.info("MRCTransctionService checkRepeatSucess 激活码激活失败，系统给予回滚操作 !");
								repeatSucessCount = 6; 
								conn.rollback();
								return repeatSucessCount;
							}
						}else{
							log.info("MRCTransctionService checkRepeatSucess 修改商户状态失败 !");
							repeatSucessCount = 4; //修改商户状态失败
							conn.rollback();
						}
					} catch (Exception e) {
						log.info("ERROR MRCTransctionService checkRepeatSucess  Exception = " + e);
						e.printStackTrace();
						repeatSucessCount = 3; //异常
						conn.rollback();
						return repeatSucessCount;
					} finally {
						conn.close();// 关闭连接
					}
						
				}else{
					log.info("Error MRCTransctionService merchantRepeat checkRepeatSucess 商户状态为空，或状态信息不为等待复审，商户ID为="+id);
					repeatSucessCount = 2;
				}
			}else{ //商户信息不存在
				log.info("Error MRCTransctionService merchantRepeat checkRepeatSucess 商户信息不存在，商户ID为="+id);
				repeatSucessCount = 1;
			}
		}
		log.info("MRCTransctionService merchantRepeat checkRepeatSucess End");
		return repeatSucessCount;
	}
	
	// 商户审核日志插入
		public int insertExaminationsLog(String merchantNo, String createOperator, Map<String, String> params,Connection conn) throws SQLException {
			String examination_opinions = params.get("examination_opinions");
			String open_status = params.get("open_status");
			String sql = "insert into examinations_log(merchant_no,open_status,examination_opinions,operator,create_time) values(?,?,?,?,now())";
			Object[] logParams = { merchantNo, open_status, examination_opinions, createOperator };
			int n = dao.updateByTranscation(sql, logParams,conn);
			try {
				cleanCacheService.cleanAllCache(merchantNo);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchantNo);
				 e.printStackTrace();
				 return n;
			}

			return n;
		}
	
	public List<Map<String, Object>> getMerchantUser(String merchantNo) throws SQLException {
		String sqlHaveUser = "select * from  cust_user u join pos_merchant m on u.user_name=m.merchant_no and u.user_name=?";
		Object[] params = { merchantNo };
		return dao.find(sqlHaveUser, params);
	}
	
	// 商户用户新增
		public int merchantUserAdd(String merchantNo, String createOperator, Map<String, String> merchantInfo, Connection conn) throws SQLException {
			String realName = merchantInfo.get("merchant_name");
			String email = merchantInfo.get("email");
			String password = MD5.toMD5("mer88888");// 原来是"C20BCA0DA652BC34C6B48619A0F33E4A";
			String sql = "insert into cust_user(user_name,real_name,email,status,password,create_operator,create_time) values(?,?,?,?,?,?,now())";
			Object[] params = { merchantNo, realName, email, "1", password, createOperator };
			int n = dao.updateByTranscation(sql, params,conn);
			try {
				cleanCacheService.cleanAllCache(merchantNo);
			} catch (Exception e) {
				log.error(e.getMessage() + "|" + merchantNo);
				e.printStackTrace();
				return n;
			}
			return n;
		}
	
	
	/**
	 * 龙宝注册通知
	 * @author swang
	 * @param params
	 * @see 商户所属代理商为龙宝(东莞吉庆鸿投资有限公司)，审核成功后，同步到龙宝
	 */
	public int longbaoInform(Map<String, String> params, Connection conn) {
		log.info("MRCTransctionService longbaoInform start ...");
		int count = 0;
		Map<String, Object> map = new HashMap<String, Object>();
		String url = SysConfig.value("lb_registerInform_url");
		if(null != params && null != url && !"".equals(url)){
			try {
				String merchant_id =  params.get("id").toString();
				map.put("name", params.get("link_name").toString());  //注册人的姓名
				map.put("phone", params.get("phone").toString()); //注册人的手机号码
				map.put("email", params.get("email").toString()); //注册人的邮箱
				map.put("idcard", params.get("id_card_no").toString()); //注册人的身份证号码
				map.put("parent_phone", params.get("parent_phone").toString()); //推荐人的手机号码，（没有不填写或者写数字0）
				map.put("card_number", params.get("cnaps_no").toString());  //注册人的银行卡号
				map.put("bank_name", params.get("bank_name").toString());  //开户行名称，银行的中文名
				map.put("deposit", params.get("bank_name").toString()); //开户行地址(省市区+详细地址)
				//MD5后字符串   hmac=name+phone+email+idcard+card_number
				StringBuffer hmac = new StringBuffer(params.get("link_name").toString()); 
				hmac.append(params.get("phone").toString());
				hmac.append(params.get("email").toString());
				hmac.append(params.get("id_card_no").toString());
				hmac.append(params.get("cnaps_no").toString());
				hmac.append("18a5eecf5d3a463f0ed0904cfb1912b9");
				System.out.println(Md5.md5Str(hmac.toString()));
				//((params.get("link_name").toString()+params.get("phone").toString()+params.get("email").toString()+params.get("id_card_no").toString()+params.get("cnaps_no").toString()+"18A5EECF5D3A463F0ED0904CFB1912B9")).toLowerCase();
				//map.put("hmac", Md5.md5Str(hmac.toString().toLowerCase())); 
				map.put("hmac", Md5.md5Str(hmac.toString()).toLowerCase());
				String msg = Http.send(url, map, "UTF-8");
				log.info("MRCTransctionService longbaoInform Http Request SUCCESS  = " + msg);
				String ret_code = "";
				String err_msg = "";
				if(null != msg && msg.length() > 0){
					JSONObject jsonobject = JSONObject.fromObject(msg);
					ret_code = jsonobject.getString("ret_code");
					err_msg = jsonobject.getString("err_msg");
				}
				map.put("merchant_id", merchant_id);
				map.put("response_code", ret_code);  
				map.put("response_msg", err_msg);
				count = merchantNotifyAdd(map,"1",conn);
				if(count > 0){
					log.info("MRCTransctionService longbaoInform SUCCESS");
				}else{
					log.info("MRCTransctionService longbaoInform FAIL");
				}
			} catch (Exception e) {
				log.error("MRCTransctionService longbaoInform Exception = " + e);
			}
			log.info("MRCTransctionService longbaoInform end");
		}
		return count;
	}
	
	/**
	 * 商户注册信息同步记录存库-龙宝
	 * @author swang
	 * @param map  同步的信息   类型   响应码等消息
	 * @param notify_type  同步类型  1.龙宝
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int merchantNotifyAdd(Map<String, Object> map,String notify_type,Connection conn) throws SQLException {
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
			count = dao.updateByTranscation(sql, params, conn);
		}
		return count;
	}
	
	/**
	 * 根据代理商编号 获取代理商电话
	 * @author 王帅
	 * @param agent_no 代理商编号
	 * @return 代理商手机号码
	 */
	public Map<String, Object> getAgentPhoneByAgentNo(String agent_no){
		Map<String, Object> maps = new HashMap<String, Object>();
		if(null != agent_no && !"".equals(agent_no)){
			String sql = "select a.agent_link_tel from agent_info a where a.agent_no=? limit 1";
			List<String> list = new ArrayList<String>();
			list.add(agent_no);
			maps = dao.findFirst(sql, list.toArray());
		}
		return maps;
	}
	
	public Map<String,Object> codeVerification(String merchant_no)
			throws Exception {
		String sql="select keycode from activation where code_user="+merchant_no;
		return dao.findFirst(sql);
	}
	
	/**
	    * 审核成功修改激活码状态为已使用，更新使用者
	    * @author ws
	    * @param keycode
	    * @throws Exception
	    */
		public int updateSucessCodeState(String keycode, Connection conn)
				throws Exception {
			String sql = "update activation set state=? where keycode=?";
			Object[] params = { 3,keycode};
			return dao.updateByTranscation(sql, params,conn);
		}
	
	/**
	 * 
	 * @param id
	 * @param failMsg
	 * @param userName
	 * @return
	 * @throws SQLException
	 */
	public int merchantRepeatFail(Long id, String failMsg, String userName,Map<String, Object> merchantInfo) throws SQLException{
		log.info("MRCTransctionService merchantRepeat merchantRepeatFail start...");
		int count = 0;
		if(id > 0 && failMsg != null && userName != null && !"".equals(failMsg) && !"".equals(userName)){
			//Map<String, Object> merchantInfo = getMerchantInfoById(id);
			if(merchantInfo != null){
				 //商户信息存在，验证商户状态是否为等待复审,否则修改失败
				if(merchantInfo.get("open_status") != null && !"".equals(merchantInfo.get("open_status").toString()) && "8".equals(merchantInfo.get("open_status").toString())){
					Connection conn = null;
					try {
						conn = dao.getConnection();
						conn.setAutoCommit(false);
						count = merchantRepeatSF(String.valueOf(id), String.valueOf(3),conn);
						if(count > 0){
							count = 1;//修改成功
						}else {
							count = 2;//修改失败
						}
						Map<String, String> mapLo = new HashMap<String, String>();
						mapLo.put("merchant_no", merchantInfo.get("merchant_no").toString());
						mapLo.put("create_person", userName);
						mapLo.put("merchant_stastu", String.valueOf(3));
						mapLo.put("return_status", String.valueOf(count));
						mapLo.put("repeat_msg", failMsg);
						merchantRepeatLog(mapLo,conn);
						if(count == 1){//复审失败后清除注册时分配的集群信息避免出现脏数据
							//int rmCustuserCount = removeCustuser(merchantInfo.get("merchant_no").toString(), conn); //商户商户用户信息
							//if(rmCustuserCount > 0){
								log.info("MRCTransctionService merchantRepeat remove merchant GROUP start ");
								int removeGroupMerchantCount = delGroupByMerchantNo(mapLo,conn);
								if(removeGroupMerchantCount > 0){//清除集群信息成功，解除机具绑定信息
									log.info("MRCTransctionService merchantRepeat remove merchant GROUP SUCESS  Merchant_No= " + merchantInfo.get("merchant_no").toString() + ";removeGroupMerchantCount= "+ removeGroupMerchantCount);
									log.info("MRCTransctionService merchantRepeat Unterminal start..."); //查询商户机具信息是否为空并检查是否绑定  如果绑定，则执行解绑操作
									if(merchantInfo.get("agent_no") != null && !"".equals(merchantInfo.get("agent_no").toString())){
										log.info("MRCTransctionService merchantRepeat Unterminal Merchant Agent_no = " + merchantInfo.get("agent_no").toString()+", 系统获取开始获取商户终端编号...");
										if(merchantInfo.get("terminal_no") != null && !"".equals(merchantInfo.get("terminal_no").toString())){
											try {
												String[] terminal_no = merchantInfo.get("terminal_no").toString().split(";");
												for(int i=0;i<terminal_no.length;i++){
													Map<String, Object> tmap = getPosTerminalByTerminalNo(terminal_no[i],merchantInfo.get("agent_no").toString()); //查询并验证机具信息
													if(tmap != null){
														unBindTerminal(Integer.parseInt(tmap.get("id").toString()),merchantInfo.get("agent_no").toString(),conn); //调用机具解绑的方法
													}
												}
												log.info("MRCTransctionService merchantRepeat Unterminal SUCESS"); //绑定成功
											} catch (Exception e) {
												log.info("ERROR MRCTransctionService merchantRepeat Unterminal Exception" +e);
												conn.rollback();
												e.printStackTrace();
												count = 7;
												return count;
											}
											log.info("MRCTransctionService merchantRepeat Unterminal End");
										}else{
											log.info("MRCTransctionService merchantRepeat Unterminal 系统未检测到商户编号为 = " +merchantInfo.get("merchant_no").toString()+", 终端编号信息，跳过解绑机具操作。");
										}
									}else{
										log.info("Error MRCTransctionService merchantRepeat Unterminal 商户数据异常: 商户所属代理商编号为NULL，系统未执行解绑操作");
									}
								}else{
									log.info("MRCTransctionService merchantRepeat remove merchant GROUP FAIL ");
									count = 8;
								}
								log.info("MRCTransctionService merchantRepeat remove merchant GROUP end ");
							/*}else {
								log.info("MRCTransctionService merchantRepeat removeCustuser FAIL "); //清楚商户的用户信息失败
								count = 9;
							}*/
						}
						log.info("MRCTransctionService merchantRepeat Sucess count = " + count);
						if(count > 5){
							conn.rollback();
						}else{
							conn.commit();// 提交事务
						}
					} catch (Exception e) {
						log.info("ERROR MRCTransctionService merchantRepeat  Exception = " + e);
						e.printStackTrace();
						conn.rollback();
						count = 6;//系统错误
						return count;
					} finally {
						conn.close();// 关闭连接
					}
				}else{
					count = 4; //商户信息已被修改
				}
			} else {
				count = 5;//商户信息不存在
			}
		}
		log.info("MRCTransctionService merchantRepeat merchantRepeatFail end...");
		return count;
	}
	
	/**
	 * 根据机具编号以及代理商编号查询机具信息
	 * @param terminalNo 机具编号
	 * @param agentNo 代理商编号
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getPosTerminalByTerminalNo(String terminalNo, String agentNo) {
		log.info("MRCTransctionService getPosTerminalByTerminalNo start ...");
		Map<String, Object> mtMap = new HashMap<String, Object>();
		if(terminalNo != null && agentNo != null && !"".equals(terminalNo) && !"".equals(agentNo)){
			List<Object> list = new ArrayList<Object>();
			list.add(agentNo);
			list.add(terminalNo);
			list.add(terminalNo);
			String sql = " select *  FROM pos_terminal where agent_no=? and open_status=2 and (terminal_no =?  or SN = ?)";
			mtMap = dao.findFirst(sql, list.toArray());
			log.info("MRCTransctionService getPosTerminalByTerminalNo SUCESS");
		}
		log.info("MRCTransctionService getPosTerminalByTerminalNo end");
		return mtMap;
	}
	
	/**
	 * 根据商户ID 查询商户信息
	 * @param id 商户ID
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getMerchantInfoById(Long id) {
		log.info("MRCTransctionService getMerchantInfoById start ...");
		Map<String, Object> map = new HashMap<String, Object>();
		if(id > 0){
			String sql = "select * from pos_merchant p where p.id=" + id;
			map = dao.findFirst(sql);
			log.info("MRCTransctionService getMerchantInfoById SUCESS map size = " + map.size());
		}
		log.info("MRCTransctionService getMerchantInfoById End");
		return map;
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
	public int merchantRepeatSF(String merchant_id, String open_status,Connection conn)  throws SQLException {
		log.info("MRCTransctionService merchantRepeatSF start...");
		int repeatCount = 0;
		if(merchant_id != null && open_status != null && !"".equals(merchant_id) && !"".equals(open_status)){
			List<String> list = new ArrayList<String>();
			String sql  = "update pos_merchant p set p.open_status=? where p.id=? and p.open_status=8";
			list.add(open_status);
			list.add(merchant_id);
			repeatCount = dao.updateByTranscation(sql, list.toArray(), conn);
			log.info("MRCTransctionService merchantRepeatSF  SUCESS = " + repeatCount);
		}
		log.info("MRCTransctionService merchantRepeatSF end");
		return repeatCount;
	}
	
	/**
	 * 存储审核失败操作记录
	 * @param map 
	 * @param conn
	 * @return 返回受影响的行数
	 * @throws SQLException
	 */
	public int merchantRepeatLog(Map<String,String> map,Connection conn)  throws SQLException {
		log.info("MRCTransctionService merchantRepeatLog start...");
		int count = 0;
		if(map != null){
			String sql = "insert into merchant_repeat_log(merchant_no,create_person,merchant_stastu,return_status,repeat_msg) values(?,?,?,?,?)";
			List<String> list = new ArrayList<String>();
			list.add(map.get("merchant_no"));
			list.add(map.get("create_person"));
			list.add(map.get("merchant_stastu"));
			list.add(map.get("return_status"));
			list.add(map.get("repeat_msg"));
			count = dao.updateByTranscation(sql, list.toArray(),conn);
			if(count > 0){
				log.info("MRCTransctionService merchantRepeatLog SUCESS");
			}
		}
		log.info("MRCTransctionService merchantRepeatLog end");
		return count;
	}
	
	/**
	  * 根据商户编号  删除所属集群信息
	  * @param params
	  * @return
	  * @throws SQLException
	  */
	 public int delGroupByMerchantNo(Map<String, String> params,Connection conn) throws SQLException {
		 log.info("MRCTransctionService delGroupByMerchantNo start...");
		 int rowsuc = 0;
		 if(params != null){
			String merchant_no = params.get("merchant_no");
		    String insert_pos_terminal = " delete from    trans_route_group_merchant where merchant_no=?    ";
		    List<Object> listPosTerminal = new ArrayList<Object>();
	 	    listPosTerminal.add(merchant_no);
		    rowsuc = dao.updateByTranscation(insert_pos_terminal, listPosTerminal.toArray(),conn);
		 }
	    log.info("MRCTransctionService delGroupByMerchantNo end");
	    return rowsuc;
		}
	
	/**
	 * 机具解绑操作
	 * @param id
	 * @param agentNo
	 * @param conn
	 * @throws SQLException
	 */
	public void unBindTerminal(int id, String agentNo,Connection conn) throws SQLException {
		 log.info("MRCTransctionService unBindTerminal start...");
		 if(id != 0 && agentNo != null && conn != null){
			 if ("2027".equals(agentNo)) {
					// 查询原机具数据
					Map<String, Object> map = getPosTerminalById(id + "");
					String date = id + DateUtils.getCurrentDateTime();
					// 解绑机具
					String sql = "update pos_terminal set merchant_no=null,terminal_no=null,open_status =1,START_TIME=null where id=? and open_status =2";
					Object[] parmas = { id };
					dao.updateByTranscation(sql, parmas,conn);
					// 复制数据
					String insertSql = "insert into pos_terminal(sn,TERMINAL_NO,MERCHANT_NO,PSAM_NO,agent_no,open_status,type,allot_batch,model,"
							+ "tmk,tmk_tpk,tmk_tak,START_TIME,CREATE_TIME)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					Object[] insParmas = { date, map.get("TERMINAL_NO"),
							map.get("MERCHANT_NO"), date, map.get("agent_no"),
							map.get("open_status"), map.get("type"),
							map.get("allot_batch"), map.get("model"), map.get("tmk"),
							map.get("tmk_tpk"), map.get("tmk_tak"),
							map.get("START_TIME"), map.get("CREATE_TIME") };
					dao.updateByTranscation(insertSql, insParmas,conn);
				} else {
					// 解绑机具
					String sql = "update pos_terminal set merchant_no=null,terminal_no=null,open_status =1,START_TIME=null where id=? and open_status =2";
					Object[] parmas = { id };
					dao.updateByTranscation(sql, parmas,conn);
				}
		 }
		 log.info("MRCTransctionService unBindTerminal end");
	}
	
		/**
		 * /根据主键取得终端信息
		 * @param id 
		 * @return
		 */
		public Map<String, Object> getPosTerminalById(String id) {
			log.info("MRCTransctionService getPosTerminalById start...");
			 Map<String, Object> map = new HashMap<String, Object>();
			if(id != null && !"".equals(id)){
				String sql = " select *  FROM pos_terminal where id =? ";
				map = dao.findFirst(sql, id);
			}
			log.info("MRCTransctionService getPosTerminalById end");
			return map;
		}
		
}
