package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Dao;

/**
 * 交易查询，可以查询代理商及子代理商交易数据
 * 
 * @author dj
 */
@Service
public class LogService {
	@Resource
	private Dao dao;


	// 查询登陆日志
	public Page<Map<String, Object>> getLogList(Map<String, String> params,
			final PageRequest pageRequest) {

		List<Object> list = new ArrayList<Object>();

		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// id
			if (StringUtils.isNotEmpty(params.get("id"))) {

				String id = params.get("id");
				sb.append(" and id = ? ");
				list.add(id);

			}

			// sys_name
			if (StringUtils.isNotEmpty(params.get("sys_name"))) {
				// String sN = "%" + params.get("sN") + "%";
				String sys_name = params.get("sys_name");
				sb.append(" and sys_name= ? ");
				list.add(sys_name);
			}

			// user_id
			if (StringUtils.isNotEmpty(params.get("user_id"))) {

				String user_id = params.get("user_id");
				sb.append(" and user_id = ? ");
				list.add(user_id);

			}

			// user_name
			if (StringUtils.isNotEmpty(params.get("user_name"))) {

				String user_name = params.get("user_name");
				sb.append(" and user_name = ? ");
				list.add(user_name);

			}

			// location
			if (StringUtils.isNotEmpty(params.get("location"))) {

				String location = params.get("location");
				sb.append(" and location = ? ");
				list.add(location);

			}

			// login_ip
			if (StringUtils.isNotEmpty(params.get("login_ip"))) {

				String login_ip = params.get("login_ip");
				sb.append(" and login_ip = ? ");
				list.add(login_ip);

			}

			

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and last_login_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and last_login_time <=? ");
				list.add(createTimeEnd);
			}

			
		}

		String sql = "select * from sys_login_log where 1=1" + sb.toString()+" order by last_login_time desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	

	// 查询手机日志
		public Page<Map<String, Object>> getMobLogList(Map<String, String> params,
				final PageRequest pageRequest) {

			List<Object> list = new ArrayList<Object>();

			StringBuffer sb = new StringBuffer();
			if (null != params) {

				// ID
				if (StringUtils.isNotEmpty(params.get("ID"))) {

					String ID = params.get("ID");
					sb.append(" and ID = ? ");
					list.add(ID);

				}

				// user_name
				if (StringUtils.isNotEmpty(params.get("user_name"))) {
					// String sN = "%" + params.get("sN") + "%";
					String user_name = params.get("user_name");
					sb.append(" and user_name= ? ");
					list.add(user_name);
				}

				// seq_no
				if (StringUtils.isNotEmpty(params.get("seq_no"))) {

					String seq_no = params.get("seq_no");
					sb.append(" and seq_no = ? ");
					list.add(seq_no);

				}

				// psam_no
				if (StringUtils.isNotEmpty(params.get("psam_no"))) {

					String psam_no = params.get("psam_no");
					sb.append(" and psam_no = ? ");
					list.add(psam_no);

				}

				// trade_id
				if (StringUtils.isNotEmpty(params.get("trade_id"))) {

					String trade_id = params.get("trade_id");
					sb.append(" and trade_id = ? ");
					list.add(trade_id);

				}

				// platform
				if (StringUtils.isNotEmpty(params.get("platform"))) {

					String platform = params.get("platform");
					sb.append(" and platform = ? ");
					list.add(platform);

				}
				
				// content
				if (StringUtils.isNotEmpty(params.get("content"))) {
					
					String content = params.get("content");
					sb.append(" and content = ? ");
					list.add(content);
					
				}

				

				if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
					String createTimeBegin = params.get("createTimeBegin") + ":00";
					sb.append(" and create_time >=? ");
					list.add(createTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
					String createTimeEnd = params.get("createTimeEnd") + ":59";
					sb.append(" and create_time <=? ");
					list.add(createTimeEnd);
				}

				
			}

			String sql = "select * from mobile_request_log where 1=1" + sb.toString()+" order by create_time desc";
			return dao.find(sql, list.toArray(), pageRequest);
		}
		
		//根据ID查询mobrequest的详情
		public  Map<String,Object> queryMobRequest(Long id) 
		{
			List<Object> list = new ArrayList<Object>();
			list.add(id);
			String sql = "select  m.*  from mobile_request_log m where m.ID = ?";
			Map<String,Object> transInfo = dao.findFirst(sql, list.toArray());
			return transInfo;
		}
	
		
		//根据seq_no查询mobres
		public Map<String, Object> queryMobResponse(String seq_no){
			String sql = "select  *  from mobile_response_log  where seq_no = ?";
			return dao.findFirst(sql,seq_no);
		}

	
	// 查询收单机构请求日志
	public Page<Map<String, Object>> getAcqLogList(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("acqMerchant"))) {
			/*	sb.append(" and (l.acq_merchant_no = ? or m.acq_merchant_name like ?)");
				list.add(params.get("acqMerchant"));
				list.add("%"+params.get("acqMerchant")+"%");*/
				sb.append(" and (l.acq_merchant_no = ? or m.acq_merchant_name = ?)");
				list.add(params.get("acqMerchant"));
				list.add(params.get("acqMerchant"));

			}
			if (StringUtils.isNotEmpty(params.get("acq_terminal_no"))) {
			/*	sb.append(" and l.acq_terminal_no like ? ");
				list.add("%" + params.get("acq_terminal_no") + "%");*/
				sb.append(" and l.acq_terminal_no = ? ");
				list.add(params.get("acq_terminal_no"));

			}
			//  params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and l.create_time >=? ");
				list.add(createTimeBegin);
			}
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and l.create_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select l.id,l.acq_enname,l.mti,l.batch_no,l.serial_no,l.msg,l.create_time,l.acq_terminal_no,l.acq_merchant_no,m.acq_merchant_name "+
		"from acq_request_log l,acq_merchant m where l.acq_merchant_no=m.acq_merchant_no"+sb.toString()+" order by l.create_time desc";
		//String sql = "select * from acq_request_log where 1=1" + sb.toString()+" order by create_time desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	// 根据id查询收单机构请求日志详情
	public Map<String, Object> getAcqLogDetailById(Long id) {
		String sql = "select l.*,m.acq_merchant_name from acq_request_log l,acq_merchant m where l.acq_merchant_no=m.acq_merchant_no and l.id=?";
		return dao.findFirst(sql, id);
	}
	
	// 根据对应信息查询收单机构响应日志详情
	public Map<String, Object> getAcqLogDetailResp(Map<String, Object> params) {
		List<Object> list = new ArrayList<Object>();
		list.add(params.get("trans_code"));
		list.add(params.get("batch_no"));
		list.add(params.get("serial_no"));
		list.add(params.get("acq_merchant_no"));
		list.add(params.get("acq_terminal_no"));
		String sql = "select * from acq_response_log where trans_code=? and batch_no=? and "+
		"serial_no=? and acq_merchant_no=? and acq_terminal_no=?";
		return dao.findFirst(sql,list.toArray());
	}
	
	

  // 查询日志  来自数据库mobile_rquest_dev
  public Page<Map<String, Object>> getRequestDevList(Map<String, String> params,
      final PageRequest pageRequest) {
    List<Object> list = new ArrayList<Object>();
    StringBuffer sb = new StringBuffer();
    if (null != params) {

      // id
      if (StringUtils.isNotEmpty(params.get("id"))) {
        String id = params.get("id");
        sb.append(" and id = ? ");
        list.add(id);
      }
      
      // os
      if (StringUtils.isNotEmpty(params.get("os"))) {
        // String sN = "%" + params.get("sN") + "%";
        String os = params.get("os");
        sb.append(" and os= ? ");
        list.add(os);
      }
      
      // mobile
      if (StringUtils.isNotEmpty(params.get("mobile"))) {
         String mobile = params.get("mobile");
        sb.append(" and mobile= ? ");
        list.add(mobile);
      }

      if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
        String createTimeBegin = params.get("createTimeBegin") + ":00";
        sb.append(" and create_time >=? ");
        list.add(createTimeBegin);
      }

      if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
        String createTimeEnd = params.get("createTimeEnd") + ":59";
        sb.append(" and create_time <=? ");
        list.add(createTimeEnd);
      }
    }

    String sql = "select * from mobile_rquest_dev where 1=1" + sb.toString()+" order by create_time desc";
    return dao.find(sql, list.toArray(), pageRequest);
  }
  
  public void saveOperateLog(String remark) throws SQLException {
	  	BossUser bu = (BossUser) SecurityUtils.getSubject()
		.getPrincipal();
		String sql = "insert into user_operate_log(user_id,user_name,operate_time,remark) values(?,?,now(),?)";
		Object[] params = { bu.getId(), bu.getRealName(),remark};
		dao.update(sql, params);
	}
  
  	//查询用户操作日志
	public Page<Map<String, Object>> getOperateLogList(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("user_name"))) {

				String user_name = params.get("user_name");
				sb.append(" and user_name = ? ");
				list.add(user_name);

			}
			
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and operate_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and operate_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select * from user_operate_log where 1=1 "+sb.toString()
						+"order by operate_time desc";
		//String sql = "select * from acq_request_log where 1=1" + sb.toString()+" order by create_time desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	// 查询商户日志
		public Page<Map<String, Object>> getMerLogList(Map<String, String> params,
				final PageRequest pageRequest) {

			List<Object> list = new ArrayList<Object>();

			StringBuffer sb = new StringBuffer();
			if (null != params) {
				
				// 操作人查询
				if (StringUtils.isNotEmpty(params.get("operator"))) {
					/*String operator = "%" + params.get("operator") + "%";
					sb.append(" and ( oper_user_name like ?  or oper_user_id like ?) ");*/
					String operator =params.get("operator");
					sb.append(" and ( oper_user_name = ?  or oper_user_id = ?) ");
					list.add(operator);
					list.add(operator);
				}

				// 商户名称查询
				if (StringUtils.isNotEmpty(params.get("merchant"))) {
					/*String merchant_name = "%" + params.get("merchant") + "%";
					sb.append(" and (merchant_name like ? or merchant_short_name like ?  or merchant_no like ?) ");*/
					String merchant_name = params.get("merchant");
					sb.append(" and (merchant_name = ? or merchant_short_name = ?  or merchant_no = ?) ");
					list.add(merchant_name);
					list.add(merchant_name);
					list.add(merchant_name);
				}
				
				// oper_type
				if (StringUtils.isNotEmpty(params.get("opertype"))) {

					String opertype = params.get("opertype");
					sb.append(" and oper_type = ? ");
					list.add(opertype);

				}

				if (StringUtils.isNotEmpty(params.get("operTimeBegin"))) {
					String operTimeBegin = params.get("operTimeBegin") + ":00";
					sb.append(" and operate_time >=? ");
					list.add(operTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("operTimeEnd"))) {
					String operTimeEnd = params.get("operTimeEnd") + ":59";
					sb.append(" and operate_time <=? ");
					list.add(operTimeEnd);
				}
				
			}

			String sql = "select * from pos_merchant_log where 1=1" + sb.toString()+" order by operate_time desc";
				return dao.find(sql, list.toArray(), pageRequest);
		}
		
		// 根据id查询商户修改日志详情
		public Map<String, Object> getMerLogDetailById(String id) {
			String sql = "select l.* , a.agent_name from pos_merchant_log l left join agent_info a " +
					"on l.belong_to_agent=a.agent_no where l.id=? ";
			return dao.findFirst(sql, id);
		}
		
		// 查询代理商扣率日志
		public Page<Map<String, Object>> getAgentLogList(Map<String, String> params,
				final PageRequest pageRequest) {

			List<Object> list = new ArrayList<Object>();

			StringBuffer sb = new StringBuffer();
			if (null != params) {
				
				// 操作人查询
				if (StringUtils.isNotEmpty(params.get("operator"))) {
	/*				String operator = "%" + params.get("operator") + "%";
					sb.append(" and ( oper_user_name like ?  or oper_user_id like ?) ");*/
					String operator =params.get("operator");
					sb.append(" and ( oper_user_name = ?  or oper_user_id = ?) ");
					list.add(operator);
					list.add(operator);
				}

				if (StringUtils.isNotEmpty(params.get("operTimeBegin"))) {
					String operTimeBegin = params.get("operTimeBegin") + ":00";
					sb.append(" and create_time >=? ");
					list.add(operTimeBegin);

				}

				if (StringUtils.isNotEmpty(params.get("operTimeEnd"))) {
					String operTimeEnd = params.get("operTimeEnd") + ":59";
					sb.append(" and create_time <=? ");
					list.add(operTimeEnd);
				}

				
			}

			String sql = "select l.*,a.agent_name from agent_settle_fee_log l left join agent_info a on l.agent_no=a.agent_no where 1=1" + sb.toString()+" order by l.create_time desc";
				return dao.find(sql, list.toArray(), pageRequest);
		}
		
		// 根据id查询商户修改日志详情
		public Map<String, Object> getAgentLogDetailById(String id) {
			String sql = "select l.* , a.agent_name from agent_settle_fee_log l left join agent_info a " +
					"on l.agent_no=a.agent_no where l.id=? ";
			return dao.findFirst(sql, id);
		}
  
}
