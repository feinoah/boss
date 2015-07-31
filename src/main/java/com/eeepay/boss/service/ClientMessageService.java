package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

@Service
public class ClientMessageService {
 
	@Resource
	private Dao dao;
	
	/**
	 * 数据库中 查询手机客户端信息
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getClientMessageList(PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		String sql = "select id,is_continue_login,create_time,is_smallbox,is_dot,is_shang_bao,is_valid,is_delete from app_client_message where 1=1 and  is_delete=0  order by id desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	/**
	 * 新增客户端信息
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public int addMessage(Map<String, String> param) throws SQLException {
		    int is_continue_login = Integer.parseInt(param.get("is_continue_login"));
		    int is_smallbox = Integer.parseInt(param.get("is_smallbox"));
		    int is_dot = Integer.parseInt(param.get("is_dot"));
		    int is_shang_bao =Integer.parseInt(param.get("is_shang_bao"));
		 //   int is_valid = Integer.parseInt(param.get("is_valid"));
		    String msg = param.get("msg");
		    
		    String insert_sql = "insert into  app_client_message( is_continue_login,is_smallbox,is_dot,is_shang_bao,is_valid,msg,create_time) values(?,?,?,?,?,?,now()) ";
	
		    List<Object> listMessage = new ArrayList<Object>();
		    listMessage.add(is_continue_login);
		    listMessage.add(is_smallbox);
		    listMessage.add(is_dot);
		    listMessage.add(is_shang_bao);
		    listMessage.add(1);
		    listMessage.add(msg);
	
		    int rowsuc = dao.update(insert_sql, listMessage.toArray());
	        return rowsuc;
	  }
	

	/**
	 * 修改手机客户端信息
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int clientMessageModify(Map<String, String> param) throws SQLException{
	    int is_continue_login = Integer.parseInt(param.get("is_continue_login"));
	    int is_smallbox = Integer.parseInt(param.get("is_smallbox"));
	    int is_dot = Integer.parseInt(param.get("is_dot"));
	    int is_shang_bao =Integer.parseInt(param.get("is_shang_bao"));
	//    int is_valid = Integer.parseInt(param.get("is_valid"));
	    String msg = param.get("msg");
		String id=param.get("id");
		String sql="update app_client_message set is_continue_login=?,is_smallbox=?,is_dot=?,is_shang_bao=?,msg=? where id=?";
		int num=dao.update(sql,new Object[]{is_continue_login,is_smallbox,is_dot,is_shang_bao,msg,Long.parseLong(id)});
		return num;
	}
	
	
	/**
	 * 数据库中 查询详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> messageDetail(long id) throws SQLException{
		String sql="select * from app_client_message where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}

	 
    
	/**
	 * 数据库中 修改时查询
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getClientMessageById(long id) throws SQLException{
		String sql="select * from app_client_message where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	
	
	/**
	 * 手机客户端消息的开通禁用操作
	 * @param id 客户端消息id
	 * @param is_valid  客户端消息有效值
	 */
	public void updateMesStatus(String id,String is_valid) {
			try {
				
				boolean valid= Boolean.parseBoolean(is_valid);
				if (valid) {//禁用操作
					dao.update("update app_client_message set is_valid=? where id=?",
							new Object[]{0,id});
				}else {//开通操作
					dao.update("update app_client_message set is_valid=? where id=?",
							new Object[]{1,id});
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	
	/**
	 * 手机客户端消息的开通禁用操作
	 * @param id 客户端消息id
	 * @param is_valid  客户端消息有效值
	 */
	public void delClientMessage(String id,String is_delete) {
			try {
				boolean is_del= Boolean.parseBoolean(is_delete);
				if (is_del) {//禁用操作
					dao.update("update app_client_message set is_delete=? where id=?",
							new Object[]{0,id});
				}else {//开通操作
					dao.update("update app_client_message set is_delete=? where id=?",
							new Object[]{1,id});
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

	}
	
}
