package com.eeepay.boss.service;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.MobileProduct;
import com.eeepay.boss.utils.Dao;

@Service
public class MobileProductService {
	@Resource
	private Dao dao;

	public void saveMobileProduct(MobileProduct mobile) throws SQLException{
		String sql="insert into mobile_product(prod_id,prod_content,prod_price,prod_isptype,prod_delaytimes,prod_provinceid,prod_type)" +
				"values(?,?,?,?,?,?,?)";
		dao.update(sql, new Object[]{mobile.getProdId(),mobile.getProdContent(),mobile.getProdPrice(),mobile.getProdIsptype(),mobile.getProdDelaytimes(),mobile.getProdProvinceid(),mobile.getProdType()});
	}
	
	public void deleteAllData() throws SQLException{
		String sql="delete from mobile_product";
		dao.update(sql);
	}

}
