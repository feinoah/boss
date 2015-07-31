package com.eeepay.boss.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

@Service
public class PosTypeService {
  @Resource
  private Dao dao;

  public Map<String, Object> getPosType(String posType){
	  
	  String sql = "select * from pos_type where pos_type = ? ";
	  if(dao == null){
		  dao = new Dao();
	  }
	  return dao.findFirst(sql,new Object[]{posType});
  }
  
  public Map<String, Object> getPosModelName(String posType){
	  
	  String sql = "select * from pos_type where pos_model = ? ";
	  if(dao == null){
		  dao = new Dao();
	  }
	  return dao.findFirst(sql,new Object[]{posType});
  }

	public Map<String, String> getPosTypes(){

		List<Map<String, Object>> list = dao.find("select pos_type_name, pos_type from pos_type");

		Map<String, String> posTypes = new HashMap<String, String>(10);

		if(list != null && list.size() > 0){
			for(Map<String, Object> map : list){
				posTypes.put(map.get("pos_type").toString(), map.get("pos_type_name").toString());
			}
		}

		return posTypes;
	}


}
