//package com.eeepay.boss.controller;
//
//import javax.annotation.Resource;
//
//import org.apache.commons.dbcp.BasicDataSource;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.eeepay.boss.utils.dbutils.DbMonitorGetConnection;
//
//
//@Controller
//@RequestMapping("db")
//public class DbPoolMonitorController {
//	@Resource(name="mainDataSource")
//	private BasicDataSource mainDataSource;
//	@Resource(name="bagDataSource")
//	private BasicDataSource bagDataSource;
//	@Resource(name="readOnlyDataSource")
//	private BasicDataSource readOnlyDataSource;
//	@RequestMapping("monitor")
//	public String monitor(Model model){
//		model.addAttribute("mainDataSource", mainDataSource);
//		model.addAttribute("bagDataSource", bagDataSource);
//		model.addAttribute("readOnlyDataSource", readOnlyDataSource);
//		model.addAttribute("connections", DbMonitorGetConnection.connections);
//		return "dbmonitor";
//	}
//}
