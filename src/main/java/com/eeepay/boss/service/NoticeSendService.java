package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.utils.Dao;

@Service
public class NoticeSendService {
    
	@Resource
	private Dao dao;
	
	

	/**
	 * 数据库中查询代理商通告信息
	 * @param params
	 * @param pageRequest
	 * @author LJ
	 * @return
	 */
	public Page<Map<String, Object>> getAgentNoticeQuery(Map<String, String> params,final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if(params!=null){
			// 代理商通告标题
			if (StringUtils.isNotEmpty(params.get("notice_title"))) {
				String notice_title = params.get("notice_title");
				sb.append(" and notice_title = ?");
				list.add(notice_title);
			}
			// 创建时间
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
		String sql = "select id,notice_title,notice_content,notice_is_valid,create_time from agent_notice_info where 1=1 "
				+sb.toString()+"order by id asc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	

	/**
	 * 新增代理商通告信息
	 * @param param
	 * @author LJ
	 * @return
	 * @throws SQLException
	 */
	public int addAgentNotice(Map<String, String> param) throws SQLException {
		    String notice_title = param.get("notice_title").toString();
		   // String notice_person = param.get("notice_person").toString();
		    String notice_content = param.get("notice_content").toString();
		   // String signature =param.get("signature").toString();
		    String attachment =param.get("attachment").toString();

		    String insert_sql = "insert into  agent_notice_info( notice_title,notice_content,notice_is_valid,attachment,create_time) values(?,?,?,?,now()) ";
	
		    List<Object> listMessage = new ArrayList<Object>();
		    if(StringUtils.isNotEmpty(notice_title)){
		    	 listMessage.add(notice_title);
		    }else{
		    	notice_title="通知";
		    	listMessage.add(notice_title);
		    }
		  /*  if(StringUtils.isNotEmpty(notice_person)){
		    	listMessage.add(notice_person+":");
		    }else{
		    	notice_person="尊敬的代理商及客户:";
		    	listMessage.add(notice_person);
		    }*/
		    listMessage.add(notice_content);
		    listMessage.add(1);
		 /*   if(StringUtils.isNotEmpty(signature)){
		    	listMessage.add(signature);
		    }else{
		    	signature="深圳市移付宝科技有限公司";
		    	listMessage.add(signature);
		    }*/
		    if(StringUtils.isNotEmpty(attachment)){
		    	listMessage.add(attachment);
		    }else{
		    	attachment="";
		    	listMessage.add(attachment);
		    }
		    int rowsuc = dao.update(insert_sql, listMessage.toArray());
	        return rowsuc;
	  }
	

	/**
	 * 修改代理商通告信息
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int agentNoticeModify(Map<String, String> params) throws SQLException{
	    String notice_title = params.get("notice_title").toString();
	   // String notice_person = params.get("notice_person").toString();
	    String notice_content = params.get("notice_content").toString();
	    //String signature =params.get("signature").toString();
	    String attachment = params.get("attachment").toString();
		String id=params.get("id");
		String sql="update agent_notice_info set notice_title=?,notice_content=?,attachment=? where id=?";
		int num=dao.update(sql,new Object[]{notice_title,notice_content,attachment,Long.parseLong(id)});
		return num;
	}
	
	
	/**
	 * 数据库中 查询详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> noticeDetail(long id) throws SQLException{
		String sql="select * from agent_notice_info where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
	//	System.out.println(map.get("notice_content"));
		return map;
	}
	
	
	/**
	 * 代理商通告信息的开通禁用操作
	 * @param id 客户端消息id
	 * @param is_valid  该条消息是否有效值
	 */
	public void updateNoticeStatus(String id,String notice_is_valid) {
			try {
				
				boolean valid= Boolean.parseBoolean(notice_is_valid);
				if (valid) {//禁用操作
					dao.update("update agent_notice_info set notice_is_valid=? where id=?",
							new Object[]{0,id});
				}else {//开通操作
					dao.update("update agent_notice_info set notice_is_valid=? where id=?",
							new Object[]{1,id});
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * 删除代理商通告信息
	 * @param id 客户端消息id
	 * @param is_valid  客户端消息有效值
	 */
	public int delNotice(long id) throws SQLException{
		String sql="delete from agent_notice_info where id=?";
		return dao.update(sql, id);
	}
	
}
