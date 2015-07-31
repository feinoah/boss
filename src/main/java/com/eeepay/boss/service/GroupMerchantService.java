package com.eeepay.boss.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 商户分组管理
 * 
 * @author zxm
 * 
 */
@Service
public class GroupMerchantService {
	@Resource
	private Dao dao;
	
	private static final Logger log = LoggerFactory.getLogger(GroupMerchantService.class);
	
	/**
	 * 根据商户编号获取当前所在集群信息
	 * @param merchant_no 商户编号
	 * @return 查询结果为第一条记录
	 */
	public Map<String, Object> getGroupMerchantInfo(String merchant_no){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != merchant_no && !"".equals(merchant_no)){
			List<String> list = new ArrayList<String>();
			String sql = "select * from trans_route_group_merchant t where t.merchant_no=?";
			list.add(merchant_no);
			map = dao.findFirst(sql, list.toArray());
		}
		return map;
	}
	
	/**
	 * 根据设备类型获取当前集群中拥有的扣率类型
	 * @author 王帅
	 * @date 2014年10月21日17:03:44
	 * @see 集群转移-根据设备类型获取当前集群中拥有的扣率类型
	 * @param pos_type 设备类型
	 * @return List 设备类型
	 */
	public List<Map<String, Object>> getMerchantFeeTypeGroup(String pos_type){
		log.info("GroupMerchantService getMerchantFeeTypeGroup start ...");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(null != pos_type && !"".equals(pos_type) && !"-1".equals(pos_type)  && !"0".equals(pos_type)){
			List<String> typeList = new ArrayList<String>();
			StringBuffer sql  = new StringBuffer("select f.fee_type from pos_merchant p, pos_merchant_fee f,trans_route_group_merchant t where ");
			sql.append("p.merchant_no=f.merchant_no and t.merchant_no=p.merchant_no and p.open_status !=2 and p.pos_type=? group by f.fee_type ");
			typeList.add(pos_type);
			list = dao.find(sql.toString(), typeList.toArray());
		}
		log.info("GroupMerchantService getMerchantFeeTypeGroup end");
		return list;
	}
	
	/**
	 * 根据扣率类型 获取集群中对应的扣率信息(因为当前没有费率对应的表，且扣率种类繁多才在这个表来统计)
	 * @author 王帅
	 * @param feeType 扣率类型
	 * @return  List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getGroupMerchantFeeType(Map<String, String> param){
		log.info("GroupMerchantService getGroupMerchantFeeType start ...");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(null != param){
			if(null != param.get("fee_type") && !"".equals(param.get("fee_type"))  && !"-1".equals(param.get("fee_type"))){
				String fee_type = param.get("fee_type").toString();
				List<String> list2 = new ArrayList<String>();
				StringBuffer sql = new StringBuffer("select pf.* from pos_merchant_fee pf,pos_merchant p,trans_route_group_merchant g where p.merchant_no=pf.merchant_no and p.merchant_no=g.merchant_no ");
				if(null != param.get("pos_type") && !"".equals(param.get("pos_type"))  && !"-1".equals(param.get("pos_type"))){
					String pos_type = param.get("pos_type").toString();
					sql.append(" and  p.pos_type=?");
					list2.add(pos_type);
				}
				
				sql.append("  and p.open_status !=2 and pf.fee_type=?");
				if("RATIO".equals(fee_type)){ //扣率
					sql.append("  GROUP BY pf.fee_rate");
				}else if("CAPPING".equals(fee_type)){ //封顶
					sql.append("  GROUP BY pf.fee_rate,pf.fee_max_amount");
				}else if("LADDER".equals(fee_type)){ //阶梯
					sql.append("  GROUP BY pf.ladder_fee");
				}
				list2.add(fee_type);
				list = dao.find(sql.toString(), list2.toArray());
			}
		}
		log.info("GroupMerchantService getGroupMerchantFeeType end");
		return list;
	}
	
	/**
	 * 根据集群编号 查询该集群下所有普通商户
	 * @param code 集群编号
	 * @return List<Map<String, Object>> 
	 */
	public List<Map<String, Object>> getGroupMerchantByCode(String code, String real_flag, String merchantNo){
		List<Object> param = new ArrayList<Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		/*StringBuffer sql = new StringBuffer("SELECT  r.id, p.merchant_no,p.merchant_name ,a.group_code ,a.group_name,");
		sql.append("CONCAT(IFNULL(ai.agent_name,''),' ', IFNULL(ai.brand_type,'') ) as agent_name,p.pos_type,");
		sql.append("f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee,p.my_settle,p.real_flag from  pos_merchant p,");
		sql.append("trans_route_group a ,trans_route_group_merchant r,agent_info ai,pos_merchant_fee f where p.merchant_no=r.merchant_no and p.agent_no=ai.agent_no and p.merchant_no=f.merchant_no");*/
		StringBuffer sql = new StringBuffer("select p.merchant_no,p.merchant_name,g.group_code,g.group_name, a.ACQ_CNNAME,");
		sql.append("CONCAT(IFNULL(i.agent_name,''),' ', IFNULL(i.brand_type,'')) as  agent_name,p.province,p.city,p.address,");
		sql.append("p.pos_type,f.fee_type,f.fee_rate,f.fee_max_amount,p.my_settle,p.bag_settle,p.real_flag ");
		sql.append("from pos_merchant p,pos_merchant_fee f,trans_route_group_merchant gm,trans_route_group g,acq_org a,agent_info i  ");
		sql.append(" where p.merchant_no=f.merchant_no and p.merchant_no=gm.merchant_no and gm.group_code=g.group_code and ");
		sql.append(" p.agent_no=i.agent_no and g.acq_no=a.ACQ_ENNAME");
		boolean flag = false;
		if(null != code && !"".equals(code)){
			sql.append(" and gm.group_code=?");
			param.add(code);
			flag = true;
		}
		
		if(null != real_flag && !"".equals(real_flag) && !"-1".equals(real_flag)){
			sql.append(" and p.real_flag=?");
			param.add(real_flag);
			flag = true;
		}
		
		if(null != merchantNo && !"".equals(merchantNo) && !"-1".equals(merchantNo)){
			sql.append(" and p.merchant_no=?");
			param.add(merchantNo);
			flag = true;
		}
		if(flag){
			list = dao.find(sql.toString(), param.toArray());
		}
		
		return list;
	}
	
	/**
	 * 查询所有状态为正常的路由集群信息集合
	 * @author swang
	 * @return 返回所有状态为正常的集群名称和集群编号(非集群ID)
	 */
	public List<Map<String, Object>> getGroupInfoList(){
		String sql  = "select group_name,group_code  from trans_route_group where status=0";
		return dao.find(sql);
	}
	
	/**
	 * 查询所有状态为正常的路由集群信息集合
	 * @author swang
	 * @return 返回所有状态为正常的集群名称和集群编号(非集群ID)
	 */
	public Map<String, Object> getGroupInfo(String groupCode){
		String sql  = "select *  from trans_route_group where status=0 and group_code=?";
		List<String> list = new ArrayList<String>();
		list.add(groupCode);
		return dao.findFirst(sql,list.toArray());
	}
	
	/**
	 * 根据集群编号 获取该集群下普通商户记录数
	 * @param params 集群编号
	 * @return 记录数
	 */
	public int getGroupsMerchantByCode(Map<String, String> params) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		int count = 1;
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
 					String group_code = params.get("group_code");
					sb.append(" and a.group_code = ? ");
					list.add(group_code);
			}
			String sql = "select count(t.id) as c from (SELECT  r.id, p.merchant_no,p.merchant_name ,a.group_code ,a.group_name " +
					" from  pos_merchant p,trans_route_group a ,trans_route_group_merchant r  " +
					"where 1=1  and p.merchant_no=r.merchant_no " +
					"and r.group_code=a.group_code"
					+ sb.toString() + " order by r.id  ) t";
			List<Map<String, Object>> list2 = dao.find(sql, list.toArray());
			count = Integer.parseInt(list2.get(0).get("c").toString());
		}
		 return count;
	}

	/**
	 * 数据库中
	 * 
	 * @param userName
	 * @return
	 */
	public Page<Map<String, Object>> getGroups(Map<String, String> params,PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			 
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
 				String group_code = params.get("group_code");
				sb.append(" and a.group_code = ? ");
				list.add(group_code);
			}
			
			if (StringUtils.isNotEmpty(params.get("real_flag")) && !"-1".equals(params.get("real_flag"))) {
 				String real_flag = params.get("real_flag");
				sb.append(" and p.real_flag = ? ");
				list.add(real_flag);
			}
			
			String specialName = params.get("specialName");
		    String specialCode = params.get("specialCode");
		    if(!"true".equals(specialName) && specialCode != null){
		    	sb.append(" and a.group_code <> ? ");
		    	list.add(specialCode);
		    }
      
		    // 商户名称查询
		    if (StringUtils.isNotEmpty(params.get("merchant"))) {
		    	/* String merchant_name = "%" + params.get("merchant") + "%";
	        	sb.append(" and (p.merchant_name like ? or p.merchant_short_name like ?  or p.merchant_no like ?) ");*/
		    	String merchant_name =params.get("merchant");
		    	sb.append(" and (p.merchant_name = ? or p.merchant_short_name = ?  or p.merchant_no = ?) ");
		    	list.add(merchant_name);
		    	list.add(merchant_name);
		    	list.add(merchant_name);
		    }
			 
		}
		String sql = "SELECT  r.id, p.merchant_no,p.merchant_name ,a.group_code ,a.group_name " +
				" from  pos_merchant p,trans_route_group a ,trans_route_group_merchant r  " +
				"where 1=1  and p.merchant_no=r.merchant_no " +
				"and r.group_code=a.group_code"
				+ sb.toString() + " order by r.id  ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	
	
	/**
	 * 集群中查询需转移的普通商户
	 * @param params  条件参数
	 * @author L J
	 * @param pageRequest 分页参数
	 * @return  查询结果集
	 */
	public Page<Map<String, Object>> getDistractGroups(Map<String, String> params,PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			//收单机构
			if(StringUtils.isNotBlank(params.get("acq_enname"))) {
				String acq_enname = params.get("acq_enname");
				sb.append(" and a.acq_no = ? ");
				list.add(acq_enname);
			}

			//集群编号
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
 					String group_code = params.get("group_code");
					sb.append(" and a.group_code = ? ");
					list.add(group_code);
			}
      
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and p.agent_no = ? ");
					list.add(agentNo);
				}
			}
			
			// 设备类型
			if (StringUtils.isNotEmpty(params.get("pos_type"))) {
				if (!params.get("pos_type").equals("-1")) {
					String pos_type = params.get("pos_type");
					sb.append(" and p.pos_type = ? ");
					list.add(pos_type);
				}
			}
			
			// 是否两清
			if (StringUtils.isNotEmpty(params.get("my_settle"))) {
				if (!params.get("my_settle").equals("-1")) {
					String my_settle = params.get("my_settle");
					sb.append(" and p.my_settle = ? ");
					list.add(my_settle);
				}
			}
			
			// 是否两清
			if (StringUtils.isNotEmpty(params.get("bag_settle"))) {
				if (!params.get("bag_settle").equals("-1")) {
					String bag_settle = params.get("bag_settle");
					sb.append(" and p.bag_settle = ? ");
					list.add(bag_settle);
				}
			}
			
			// 是否实名
			if (StringUtils.isNotEmpty(params.get("real_flag"))) {
				if (!params.get("real_flag").equals("-1")) {
					String real_flag = params.get("real_flag");
					sb.append(" and p.real_flag = ? ");
					list.add(real_flag);
				}
			}
			
			// 所属省份
			if (StringUtils.isNotEmpty(params.get("province"))) {
				if (!params.get("province").equals("-1")) {
					String province = params.get("province");
					sb.append(" and p.province = ? ");
					list.add(province);
				}
			}
			
			// 所属城市
			if (StringUtils.isNotEmpty(params.get("city"))) {
				if (!params.get("city").equals("-1")) {
					String city = params.get("city");
					sb.append(" and p.city = ? ");
					list.add(city);
				}
			}
			
			if(StringUtils.isNotEmpty(params.get("fee_type")) && !"-1".equals(params.get("fee_type"))){  //扣率
				if (params.get("fee_type").equals("RATIO")) {
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate")) && !"-1".equals(params.get("fee_rate"))){
						sb.append(" and pf.fee_rate=?");
						list.add(params.get("fee_rate"));
					}
				}else if(params.get("fee_type").equals("CAPPING")){ //封顶
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate"))  && !"-1".equals(params.get("fee_rate"))){
						String[]  fee_rate = params.get("fee_rate").toString().split("=");
						if(2 == fee_rate.length){ //封顶分别存的是3个字段
							sb.append(" and pf.fee_rate=?");
							//sb.append(" and pf.fee_cap_amount=?");
							sb.append(" and pf.fee_max_amount=?");
							list.add(fee_rate[0]);
							list.add(fee_rate[1]);
							//list.add(fee_rate[2]);
						}
					}
				}else if(params.get("fee_type").equals("LADDER")){  //阶梯
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate"))   && !"-1".equals(params.get("fee_rate"))){
						sb.append(" and pf.ladder_fee=?");
						list.add(params.get("fee_rate"));
					}
				}
			}
		}
		
		String sql = "SELECT  r.id,CONCAT(IFNULL(g.agent_name,''),' ', IFNULL(g.brand_type,'') ) as agent_name,p.pos_type,p.my_settle,p.bag_settle,p.real_flag,p.province,p.city,p.merchant_no,p.merchant_name ,a.group_code ,a.group_name " +
						 " from  pos_merchant p,trans_route_group a ,trans_route_group_merchant r,agent_info g, pos_merchant_fee pf  " +
						 " where 1=1  and p.merchant_no=r.merchant_no and r.group_code=a.group_code  and  p.agent_no=g.agent_no and pf.merchant_no=p.merchant_no " +
                           sb.toString() + " order by r.id  ";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	/**
	 * 查询所有需要转移的商户
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getDistractGroups(Map<String, String> params) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		
		if (null != params) {

			//收单机构
			if(StringUtils.isNotBlank(params.get("acq_enname"))) {
				String acq_enname = params.get("acq_enname");
				sb.append(" and a.acq_no = ? ");
				list.add(acq_enname);
			}

			//集群编号
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
				String group_code = params.get("group_code");
				sb.append(" and a.group_code = ? ");
				list.add(group_code);
			}
			
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and p.agent_no = ? ");
					list.add(agentNo);
				}
			}
			
			// 设备类型
			if (StringUtils.isNotEmpty(params.get("pos_type"))) {
				if (!params.get("pos_type").equals("-1")) {
					String pos_type = params.get("pos_type");
					sb.append(" and p.pos_type = ? ");
					list.add(pos_type);
				}
			}
			
			// 是否两清
			if (StringUtils.isNotEmpty(params.get("my_settle"))) {
				if (!params.get("my_settle").equals("-1")) {
					String my_settle = params.get("my_settle");
					sb.append(" and p.my_settle = ? ");
					list.add(my_settle);
				}
			}
			
			//是否钱包结算
			if (StringUtils.isNotEmpty(params.get("bag_settle"))) {
				if (!params.get("bag_settle").equals("-1")) {
					String bag_settle = params.get("bag_settle");
					sb.append(" and p.bag_settle = ? ");
					list.add(bag_settle);
				}
			}
			
			// 所属省份
			if (StringUtils.isNotEmpty(params.get("province"))) {
				if (!params.get("province").equals("-1")) {
					String province = params.get("province");
					sb.append(" and p.province = ? ");
					list.add(province);
				}
			}
			
			// 所属城市
			if (StringUtils.isNotEmpty(params.get("city"))) {
				if (!params.get("city").equals("-1")) {
					String city = params.get("city");
					sb.append(" and p.city = ? ");
					list.add(city);
				}
			}
			
			// 是否实名
			if (StringUtils.isNotEmpty(params.get("real_flag"))) {
				if (!params.get("real_flag").equals("-1")) {
					String real_flag = params.get("real_flag");
					sb.append(" and p.real_flag = ? ");
					list.add(real_flag);
				}
			}
			
			if(StringUtils.isNotEmpty(params.get("fee_type")) && !"-1".equals(params.get("fee_type"))){  //扣率
				if (params.get("fee_type").equals("RATIO")) {
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate")) && !"-1".equals(params.get("fee_rate"))){
						sb.append(" and pf.fee_rate=?");
						list.add(params.get("fee_rate"));
					}
				}else if(params.get("fee_type").equals("CAPPING")){ //封顶
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate"))  && !"-1".equals(params.get("fee_rate"))){
						String[]  fee_rate = params.get("fee_rate").toString().split("=");
						if(2 == fee_rate.length){ //封顶分别存的是3个字段
							sb.append(" and pf.fee_rate=?");
							//sb.append(" and pf.fee_cap_amount=?");
							sb.append(" and pf.fee_max_amount=?");
							list.add(fee_rate[0]);
							list.add(fee_rate[1]);
							//list.add(fee_rate[2]);
						}
					}
				}else if(params.get("fee_type").equals("LADDER")){  //阶梯
					sb.append(" and pf.fee_type=?");
					list.add(params.get("fee_type"));
					if(StringUtils.isNotEmpty(params.get("fee_rate"))   && !"-1".equals(params.get("fee_rate"))){
						sb.append(" and pf.ladder_fee=?");
						list.add(params.get("fee_rate"));
					}
				}
			}
		}
	
		String sql = "SELECT  r.id,CONCAT(IFNULL(g.agent_name,''),' ', IFNULL(g.brand_type,'') ) as agent_name ,p.pos_type,p.my_settle,p.bag_settle,p.merchant_no,p.merchant_name ,a.group_code ,a.group_name " +
						 " from  pos_merchant p,trans_route_group a ,trans_route_group_merchant r,agent_info g, pos_merchant_fee pf  " +
						 " where  p.merchant_no=r.merchant_no and r.group_code=a.group_code  and  p.agent_no=g.agent_no and pf.merchant_no=p.merchant_no " +
	                       sb.toString() + " order by r.id  ";
		return dao.find(sql, list.toArray());
	}
 
	
	

	
	/**
	 * 获取集群号
	 * @return
	 */
	public List<Map<String, Object>> getGroupList()  {
	     StringBuffer sql_sb = new StringBuffer(""); 
	     sql_sb.append("  select t.group_name,t.group_code from trans_route_group t");
	     List<Map<String, Object>> list =  dao.find(sql_sb.toString());
	     return list;
	 }
	
	
	 /**
	  * @author 王帅
	  * @date 2015年3月24日10:23:42
	  * @see 本方法用于集群转移操作，因转移集群会涉及二清问题，故此方法将以集群是否优质商户为标准，转移的集群将自动修改“商户”的是否优质商户信息与集群保持一致
	  * @param merchant_no 商户编号
	  * @param group_code 集群编号
	  * @param realName 操作人姓名
	  * @param my_settle 是否优质商户
	  * @throws SQLException
	  */
	 public void updateGroup(String merchant_no,String group_code, String realName,String my_settle, String bag_settle)throws SQLException{
		 log.info("GroupMerchantService updateGroup start...");
		 Connection conn = null;
		 try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			boolean checkModify = true;
			int modifyGroupCount = 0;
			String sql="update trans_route_group_merchant  set group_code=? where merchant_no=?";
			Object[] params = {group_code ,merchant_no};
			modifyGroupCount = dao.updateByTranscation(sql, params,conn);
			if(modifyGroupCount > 0){
				int modifyMerchantCount = 0;
				String modifyMerchantSQL = "update pos_merchant set bag_settle=?,my_settle=?  where merchant_no=?";
				Object[] modifyMerchant = {bag_settle, my_settle, merchant_no};
				modifyMerchantCount = dao.updateByTranscation(modifyMerchantSQL, modifyMerchant, conn);
				if(modifyMerchantCount > 0){
					int addLogCount = 0;
					String addLogSQL = "insert  into  trans_route_group_merchant_log( group_code,merchant_no,create_person,create_type,my_settle,bag_settle) values(?,?,?,'集群转移-转入',?,?)";
			    	Object[] params2 = {group_code ,merchant_no,realName,my_settle,bag_settle};
			    	addLogCount = dao.updateByTranscation(addLogSQL, params2,conn);
			    	if(addLogCount > 0){
			    		conn.commit();
			    		checkModify = false;
			    	}
				}
			}
			if(checkModify){
				conn.rollback();
			}
		} catch (Exception e) {
			log.error("集群转移发生异常 = GroupMerchantService updateGroup Exception = " + e.getMessage());
			conn.rollback();
		}finally{
			conn.close();
		}
		log.info("GroupMerchantService updateGroup start...");
	 }
	
	
	
	public int addSubmit(Map<String, String> params) throws SQLException {
		log.info("GroupMerchantService  addSubmit start...");
		Connection conn = null;
		int rowsuc = 0;
		try {
			if(null != params.get("my_settle") && null != params.get("bag_settle") && !"".equals(params.get("my_settle").toString())  && !"".equals(params.get("bag_settle").toString()) &&
					!"-1".equals(params.get("my_settle").toString()) && !"-1".equals(params.get("bag_settle").toString())){
				String group_code = params.get("group_code");
			    String merchant_no = params.get("merchant_no");
			    String insert_pos_terminal = "insert  into  trans_route_group_merchant( group_code,merchant_no) values(?,?)";
			    List<Object> listPosTerminal = new ArrayList<Object>();
			    listPosTerminal.add(group_code);
			    listPosTerminal.add(merchant_no);
			    conn = dao.getConnection();
				conn.setAutoCommit(false);
			    rowsuc = dao.updateByTranscation(insert_pos_terminal, listPosTerminal.toArray(),conn);
			    if(rowsuc > 0){
			    	String bag_settle = params.get("bag_settle").toString();
			    	String my_settle = params.get("my_settle").toString();
			    	int modifyMerchantCount = 0;
			    	String sql = "update pos_merchant set bag_settle=?, my_settle=? where merchant_no=?";
			    	modifyMerchantCount = dao.updateByTranscation(sql, new Object[]{bag_settle, my_settle, merchant_no},conn);
			    	if(modifyMerchantCount > 0){
			    		String create_person = params.get("create_person");
				    	 listPosTerminal.add(create_person);
				    	 listPosTerminal.add(my_settle);
				    	 listPosTerminal.add(bag_settle);
				    	int addLogCount = 0;
				    	String addLogSQL = "insert  into  trans_route_group_merchant_log( group_code,merchant_no,create_person,create_type,my_settle,bag_settle) values(?,?,?,'添加集群',?,?)";
				    	addLogCount = dao.updateByTranscation(addLogSQL, listPosTerminal.toArray(),conn);
				    	if(addLogCount > 0){
				    		conn.commit();
				    	}else{
				    		rowsuc = 0;
				    		conn.rollback();//回滚事务
				    	}
			    	}else{
			    		rowsuc = 0;//更新商户信息失败
			    		conn.rollback();//回滚事务
			    	}
			    }else{
			    	conn.rollback();//回滚事务
			    }
	    	}
		} catch (Exception e) {
			log.info("Exception GroupMerchantService ERROR = " + e.getMessage());
			conn.rollback();//回滚事务
			return rowsuc;
		}finally{
			conn.close();//关闭连接
		}
	    
	    log.info("GroupMerchantService  addSubmit start...");
	    return rowsuc;
  }
	
	
	//insert前1.检查商户是否存在。2.检查是否已经存在这个商户的group信息。.
	 public int addCheck_exists_route(Map<String, String> params) throws SQLException {
 	    String merchant_no = params.get("merchant_no");
	    List<Object> listPosTerminal = new ArrayList<Object>();
 	    listPosTerminal.add(merchant_no);
 	    
      String trans_route_group_merchant = " select count(*) count  from     trans_route_group_merchant where merchant_no=?  ";

      Map<String, Object> m1 = dao.findFirst(trans_route_group_merchant, listPosTerminal.toArray()) ;
	    int  ret=Integer.valueOf(String.valueOf(m1.get("count")));
	    return ret;
	  }
	 
	 

	  //insert前1.检查商户是否存在。2.检查是否已经存在这个商户的group信息。.
	   public int addCheck_exists_merchant(Map<String, String> params) throws SQLException {
	      String merchant_no = params.get("merchant_no");
	      List<Object> listPosTerminal = new ArrayList<Object>();
	      listPosTerminal.add(merchant_no);
	      String hava_merchant = " select count(*)  count  from     pos_merchant where merchant_no=?  ";
	      Map<String, Object> m1 = dao.findFirst(hava_merchant, listPosTerminal.toArray()) ;
	      int  ret=Integer.valueOf(String.valueOf(m1.get("count")));
	      return ret;
	    }
	   
	
	   
 
	/**
	 * @author 王帅
	 * @date 2015年3月20日13:53:15
	 * @see  根据集群普通商户表ID，将商户从该集群中删除，如要传入ID编号以及操作人真是姓名用以记录操作，如操作记录存储失败，将回滚操作并返回0，否则返回删除受影响的条数。
	 * @param groupID 集群普通商户ID编号
	 * @param realName 操作人
	 * @return 删除集群影响的条数
	 * @throws SQLException
	 */
	 public int delGroupMerchantNo(String groupID, String realName) throws SQLException {
		log.info("GroupMerchantService del START...");
		int rowsuc = 0;
		if(null != groupID && null != realName && !"".equals(groupID) && !"".equals(realName)){
			List<Object> listPosTerminal = new ArrayList<Object>();
	 	    listPosTerminal.add(groupID);
			String getGroupInfoSQL = "select merchant_no,group_code from trans_route_group_merchant t where t.id=?";
			List<Map<String, Object>> list = dao.find(getGroupInfoSQL, listPosTerminal.toArray());
			if(list != null && list.size() > 0 && null != list.get(0) && null != list.get(0).get("merchant_no") && null != list.get(0).get("group_code")){
				 Connection conn = null;
				 try {
					conn = dao.getConnection();
					conn.setAutoCommit(false);
					String updateRouteGroupSQL = "delete from trans_route_group_merchant where id=?";
					 rowsuc = dao.updateByTranscation(updateRouteGroupSQL, listPosTerminal.toArray(),conn);
					 if(rowsuc > 0){
						 List<String> addMonitorList = new ArrayList<String>();
						 int addMonitorCount = 0;
						 String addMonitorSQL = "insert  into  trans_route_group_merchant_log( group_code,merchant_no,create_person,create_type) values(?,?,?,'从集群删除')";
						 addMonitorList.add(list.get(0).get("group_code").toString()); //集群编号
						 addMonitorList.add(list.get(0).get("merchant_no").toString()); //商户编号
						 addMonitorList.add(realName); //操作人
						 addMonitorCount = dao.updateByTranscation(addMonitorSQL, addMonitorList.toArray(),conn);
						 if(addMonitorCount > 0){
							 conn.commit();
						 }else{
							 conn.rollback(); //添加记录失败，系统回滚操作确保数据安全
						 }
					 }
				} catch (Exception e) {
					log.error("GroupMerchantService del Exception = " + e.getMessage());
					conn.rollback();
					return rowsuc;
				}finally{
					conn.close();
				}
			}
		}
	    log.info("GroupMerchantService del END...");
	    return rowsuc;
	  }

	 /**
	  * 根据商户编号  删除所属集群信息
	  * @param params
	  * @return
	  * @throws SQLException
	  
	 public int delGroupByMerchantNo(Map<String, String> params) throws SQLException {
		    String merchant_no = params.get("merchant_no");
		    String insert_pos_terminal = " delete from    trans_route_group_merchant where merchant_no=?    ";
		    List<Object> listPosTerminal = new ArrayList<Object>();
	 	    listPosTerminal.add(merchant_no);
		    int rowsuc = dao.update(insert_pos_terminal, listPosTerminal.toArray());
		    return rowsuc;
		}*/
	 
 
}
