package com.eeepay.boss.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.eeepay.boss.domain.BossUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
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
public class GroupAcqMerchantService {
	@Resource
	private Dao dao;
	
	/**
	 * 根据集群编号 获取该集群下所有收单商户信息
	 * @author swang
	 * @param groupCode 集群编号
	 * @return  集群收单商户集合
	 */
	public List<Map<String, Object>> getGroupAcqMerchantListByCode(Map<String, String> params){
		List<Map<String, Object>> getGroupAcqMerchantList = new ArrayList<Map<String,Object>>();
		if(params != null){
			List<String> list = new ArrayList<String>();
			boolean flag = false;
			StringBuffer sql = new StringBuffer("select  r.id, r.acq_name,a.acq_cnname,r.acq_merchant_no,m.acq_merchant_name,r.group_code,");
			sql.append("CONCAT(IFNULL(i.agent_name,''),' ', IFNULL(i.brand_type,'') ) as agent_name ,g.group_name,r.last_use_time,r.status,p.merchant_no,");
			sql.append("p.merchant_name,p.province,p.city, p.address,p.pos_type,m.fee_type,m.fee_rate,m.fee_max_amount,");
			sql.append("f.fee_type as merchant_fee_type,f.fee_rate as merchant_fee_rate,f.fee_max_amount as merchant_fee_max_amount,m.merchant_rate,p.my_settle,p.bag_settle,p.real_flag");
			sql.append(" from  trans_route_group_rel r, acq_org a ,acq_merchant m,  trans_route_group g,pos_merchant p,agent_info i,pos_merchant_fee f ");
			sql.append(" where r.acq_name=a.acq_enname and p.merchant_no=f.merchant_no ");
			sql.append(" and  r.acq_merchant_no=m.acq_merchant_no and p.merchant_no=m.merchant_no and i.agent_no=p.agent_no and r.group_code=g.group_code ");
			if(StringUtils.isNotEmpty(params.get("group_code"))){
				sql.append(" and r.group_code = ? ");
				list.add(params.get("group_code"));
				flag = true;
			}
			
			if(StringUtils.isNotEmpty(params.get("acq_enname"))){
				sql.append(" and r.acq_name = ? ");
				list.add(params.get("acq_enname"));
				flag = true;
			}
			
			if(StringUtils.isNotEmpty(params.get("acq_merchant"))){
				sql.append(" and (m.acq_merchant_no = ? or m.acq_merchant_name = ?) ");
				list.add(params.get("acq_merchant"));
				list.add(params.get("acq_merchant"));
				flag = true;
			}
			
			sql.append("  order by r.id");
			if(flag){
				getGroupAcqMerchantList = dao.find(sql.toString(), list.toArray());
			}
			
		}
		return getGroupAcqMerchantList;
	}
	
	/**
	 * 根据集群编号 统计该集群下所有收单商户记录数
	 * @param params 集群编号
	 * @return 该集群下所有收单商户统计数
	 */
	public int  getAcqMerchantByCode(Map<String, String> params) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		int count = 1;
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
 					String group_code = params.get("group_code");
					sb.append(" and r.group_code = ? ");
					list.add(group_code);
			}
			
			if (StringUtils.isNotEmpty(params.get("acq_enname"))) {
					String acq_enname = params.get("acq_enname");
				sb.append(" and r.acq_enname = ? ");
				list.add(acq_enname);
			}
			
			if (StringUtils.isNotEmpty(params.get("acq_merchant"))) {
					String acq_merchant = params.get("acq_merchant");
				sb.append(" and (m.acq_merchant_name = ? or m.acq_merchant_no=?)");
				list.add(acq_merchant);
				list.add(acq_merchant);
			}
		String sql = " select count(t.id) as c from ( select  r.id, r.acq_name,a.acq_cnname,r.acq_merchant_no,m.acq_merchant_name,r.group_code,g.group_name,r.last_use_time,r.status" +
				" from  trans_route_group_rel r, acq_org a ,acq_merchant m,  trans_route_group g" +
				"  where r.acq_name=a.acq_enname and r.acq_merchant_no=m.acq_merchant_no and r.group_code=g.group_code"
								+ sb.toString() + " order by r.id) t  ";
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
					sb.append(" and r.group_code = ? ");
					list.add(group_code);
			}
			
	     if (StringUtils.isNotEmpty(params.get("acq_enname"))) {
         String acq_enname = params.get("acq_enname");
         sb.append(" and r.acq_name = ? ");
         list.add(acq_enname);
     }
      
      // 商户名称查询
      if (StringUtils.isNotEmpty(params.get("acq_merchant"))) {
     /*   String acq_merchant = "%" + params.get("acq_merchant") + "%";
        sb.append(" and (m.acq_merchant_name like ?   or m.acq_merchant_no like ?) ");*/
        String acq_merchant =params.get("acq_merchant");
        sb.append(" and (m.acq_merchant_name = ?   or m.acq_merchant_no = ?) ");
        list.add(acq_merchant);
        list.add(acq_merchant);
       }
			 
		}
		String sql = "  select  r.id, r.acq_name,a.acq_cnname,r.acq_merchant_no,m.acq_merchant_name,r.group_code,g.group_name,r.last_use_time,r.status" +
				" from  trans_route_group_rel r, acq_org a ,acq_merchant m,  trans_route_group g" +
				"  where r.acq_name=a.acq_enname and r.acq_merchant_no=m.acq_merchant_no and r.group_code=g.group_code"
								+ sb.toString() + " order by r.id  ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

 
	public int addSubmit(Map<String, String> params) throws SQLException {

		String trans_route_group_rel = "insert into trans_route_group_rel( acq_name,acq_merchant_no,group_code) values(?,?,?)";
		String trans_route_group_rel_log = "insert into trans_route_group_rel_log(acq_name,acq_merchant_no,group_code,operate_time,oper_desc,oper_id,oper_name,oper_type) values(?,?,?,now(),?,?,?,?)";

		List<Object> list = new ArrayList<Object>();

		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);

			list.add(params.get("acq_enname"));
			list.add(params.get("acq_merchant_no"));
			list.add(params.get("group_code"));

			int rowsuc = dao.updateByTranscation(trans_route_group_rel, list.toArray(), conn);

			list.add("集群中新增收单商户");
			BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
			list.add(bu.getId());
			list.add(bu.getRealName());
			list.add(0);

			dao.updateByTranscation(trans_route_group_rel_log, list.toArray(), conn);

			conn.commit();
			return rowsuc;

		} catch (Exception e) {

			conn.rollback();
			e.printStackTrace();
			return 0;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

    }
	
	
	//insert前1.检查商户是否存在。2.检查trans_route_group_rel是否已经存在这个商户的 信息。.
	 public int addCheck_exists_rel(Map<String, String> params) throws SQLException {
 	    String acq_merchant_no = params.get("acq_merchant_no");
      String acq_enname = params.get("acq_enname");

	    List<Object> listPosTerminal = new ArrayList<Object>();
      listPosTerminal.add(acq_merchant_no);
      listPosTerminal.add(acq_enname);
 	    
      String trans_route_group_merchant = " select count(*) count  from     trans_route_group_rel where acq_merchant_no=?  and acq_name=?  ";

      Map<String, Object> m1 = dao.findFirst(trans_route_group_merchant, listPosTerminal.toArray()) ;
	    int  ret=Integer.valueOf(String.valueOf(m1.get("count")));
	    return ret;
	  }
	 
	 

	  //insert前1.检查商户是否存在。2.检查trans_route_group_rel是否已经存在这个商户的 信息。.
	   public int addCheck_exists_merchant(Map<String, String> params) throws SQLException {
       String acq_merchant_no = params.get("acq_merchant_no");
	      List<Object> listPosTerminal = new ArrayList<Object>();
	      listPosTerminal.add(acq_merchant_no);
	      String hava_merchant = " select count(*)  count  from     acq_merchant where acq_merchant_no=?  ";
	      Map<String, Object> m1 = dao.findFirst(hava_merchant, listPosTerminal.toArray()) ;
	      int  ret=Integer.valueOf(String.valueOf(m1.get("count")));
	      return ret;
	    }
	   
	   
 
	
	 public int del(Map<String, String> params) throws SQLException {
	 	String id = params.get("id");
	    String sql = " delete from trans_route_group_rel where id=? ";
		String sqlSel = "select acq_name,acq_merchant_no,group_code,last_use_time,status from trans_route_group_rel where id=?";
		String sqlLog = "insert into trans_route_group_rel_log(acq_name,acq_merchant_no,group_code,last_use_time,status,operate_time,oper_desc,oper_id,oper_name,oper_type)values(?,?,?,?,?,now(),?,?,?,?)";

		 Connection conn = null;
		 try {
			 conn = dao.getConnection();
			 conn.setAutoCommit(false);

			 Map<String, Object> map = dao.findFirst(sqlSel, id);
			 if(map != null){
				 List<Object> list = new ArrayList<Object>();

				 list.add(map.get("acq_name"));
				 list.add(map.get("acq_merchant_no"));
				 list.add(map.get("group_code"));
				 list.add(map.get("last_use_time"));
				 list.add(map.get("status"));

				 list.add("集群中删除收单商户");
				 BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				 list.add(bu.getId());
				 list.add(bu.getRealName());
				 list.add(1);

				 dao.updateByTranscation(sqlLog, list.toArray(), conn);

				 int rowsuc = dao.updateByTranscation(sql, new Object[]{id},conn);

				 conn.commit();
				 return rowsuc;
			 }

			 return 0;
		 } catch (Exception e) {

			 conn.rollback();
			 e.printStackTrace();
			 return 0;

		 } finally {
			 try {
				 conn.close();
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
	 }
	 
	  /**
	   * 修改
	   * @param merchantNo 商户编号
	   * @param status 0关闭1启用
	 * @throws SQLException 
	   */
	 public int changeAcqGroupMerchantStatus(String acqMerchantNo,int status) throws SQLException{

		 Connection conn = null;
		 try {
			 conn = dao.getConnection();
			 conn.setAutoCommit(false);

			 //不是开启的值，就给关闭的值，防止status出现 0 1之外的值
			 if(status!=1){
				 status=0;
			 }

			 String sqlSel = "select acq_name,acq_merchant_no,group_code,last_use_time,status from trans_route_group_rel where acq_merchant_no=?";
			 Map<String, Object> map = dao.findFirst(sqlSel, acqMerchantNo);
			 if(map != null){

				 List<Object> list = new ArrayList<Object>();

				 list.add(map.get("acq_name"));
				 list.add(map.get("acq_merchant_no"));
				 list.add(map.get("group_code"));
				 list.add(map.get("last_use_time"));
				 list.add(map.get("status"));

				 list.add("集群中修改收单商户");
				 BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				 list.add(bu.getId());
				 list.add(bu.getRealName());
				 list.add(2);

				 String sqlLog = "insert into trans_route_group_rel_log(acq_name,acq_merchant_no,group_code,last_use_time,status,operate_time,oper_desc,oper_id,oper_name,oper_type)values(?,?,?,?,?,now(),?,?,?,?)";
				 dao.updateByTranscation(sqlLog, list.toArray(), conn);

				 String sql="update trans_route_group_rel trgr set trgr.status=? where trgr.acq_merchant_no=?";
				 int rowsuc = dao.updateByTranscation(sql, new Object[]{status, acqMerchantNo},conn);

				 conn.commit();
				 return rowsuc;

			 }
			 return 0;

		 } catch (Exception e) {

			 conn.rollback();
			 e.printStackTrace();
			 return 0;
		 } finally {
			 try {
				 conn.close();
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
	 }
 
}
