package com.eeepay.boss.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.GroupService;
import com.eeepay.boss.service.TerminalService;

/**
 * 代理商管理
 * 
 * @author zhanghw
 * 
 */
@Controller
@RequestMapping(value = "/agent")
public class AgentController extends BaseController {

  @Resource
  private AgentService agentService;

  @Resource
  private TerminalService terminalService;
  
  @Resource
  private GroupService groupService;

  private static final Logger log = LoggerFactory
      .getLogger(AgentController.class);
  
  /**
   * 超级刷代理商查询(要独立出来，不允许与代理商混在一起)
   * @author 王帅
   * @param model  返回model
   * @param params 查询条件
   * @param cpage 分页信息
   * @return 返回超级刷代理商查询界面
   */
  @RequestMapping(value = "/agentQuerySPOS")
  public String agentQuerySPOS(final ModelMap model, @RequestParam
  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
  int cpage) {
	  log.info("AgentController agentQuerySPOS start ...");
    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
    if (params.size() == 110) {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String createTime = sdf.format(date);
      String createTimeBegin = createTime + " 00:00";
      String createTimeEnd = createTime + " 23:59";
      params.put("createTimeBegin", createTimeBegin);
      params.put("createTimeEnd", createTimeEnd);
    }

    Page<Map<String, Object>> list = agentService.getAgentSPOSList(params, page);
    agentService.checkIsAgentUsed(list);

    model.put("p", cpage);
    model.put("list", list);
    model.put("params", params);
    log.info("AgentController agentQuerySPOS end");
    return "/agent/agentQuerySPOS";
  }

 
  // 代理商查询
  @RequestMapping(value = "/agentQuery")
  public String agentQuery(final ModelMap model, @RequestParam
  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
  int cpage) {
    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
    if (params.size() == 110) {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String createTime = sdf.format(date);
      String createTimeBegin = createTime + " 00:00";
      String createTimeEnd = createTime + " 23:59";
      params.put("createTimeBegin", createTimeBegin);
      params.put("createTimeEnd", createTimeEnd);
    }

    Page<Map<String, Object>> list = agentService.getAgentList(params, page);
    agentService.checkIsAgentUsed(list);

    model.put("p", cpage);
    model.put("list", list);
    model.put("params", params);

    return "/agent/agentQuery";
  }

  // 增机
  @RequestMapping(value = "/appendTerminal")
  public String appendTerminal(final ModelMap model, @RequestParam
  Map<String, String> params) {
    model.put("agentNo", params.get("agentNo"));
    return "/terminal/appendTerminal";
  }
  
  /**
   * 超级刷增机
   * @author 王帅
   * @param model
   * @param params
   * @return
   */
 @RequestMapping(value = "/appendTerminalSPOS")
 public String appendTerminalSPOS(final ModelMap model, @RequestParam
 Map<String, String> params) {
   model.put("agentNo", params.get("agentNo"));
   return "/terminal/appendTerminalSPOS";
 }

  // 增机
  @RequestMapping(value = "/appendTerminalSave")
  public void appendTerminalSave(final ModelMap model,
      HttpServletRequest request, HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {
    String agentNo = params.get("agentNo");
    String type = params.get("type");
    int count = Integer.valueOf(params.get("count"));
    Map<String, Integer> posType = new HashMap<String, Integer>();
    String posAllotBatch = GenSyncNo.getInstance().getNextPosAllotNo();
    posType.put(type, count);
    int f = terminalService.allotAgent(agentNo, posType, posAllotBatch,
        getUser());
    Map<String, String> okMap = new HashMap<String, String>();
    if (f > 100) {
      okMap.put("msg", "1");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    } else {
      okMap.put("msg", "0");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }

  // 代理商详情
  @RequestMapping(value = "/agentDetail")
  public String agentDetail(final ModelMap model, @RequestParam
  Long id, @RequestParam(value = "p", defaultValue = "1")
  int cpage) {
    // PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
    PageRequest page = new PageRequest(cpage - 1, Integer.MAX_VALUE);
    String url = SysConfig.value("uploadfiles");
    Map<String, Object> params = new HashMap<String, Object>();

    Map<String, Object> agentInfo = agentService.getAgentDetail(id);
    List<Map<String, Object>> agentFeeAndRule = agentService
        .getAgentFeeAndRuleDetail((String) agentInfo.get("agent_no"));
    String agent_no = agentInfo.get("agent_no").toString();
    Map<String, String> map = new HashMap<String, String>();
    map.put("agent_no", agent_no);

    Page<Map<String, Object>> list = terminalService.getPosAllotHistory(map,
        page);

    Map<String, Object> agent_settle_fee_Info= agentService.getAgent_agent_settle_fee(agent_no);
    
    if(agentInfo != null){

  		String commonDepositRate = agentInfo.get("common_deposit_rate") != null ? agentInfo.get("common_deposit_rate").toString() : "";
  		String overDepositRate = agentInfo.get("over_deposit_rate") != null ? agentInfo.get("over_deposit_rate").toString() : "";
  		String depositRate = agentInfo.get("deposit_rate") != null ? agentInfo.get("deposit_rate").toString() : "";
  		
  		agentInfo.put("common_deposit_rate",StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointRight(2).toString() : "");
  		agentInfo.put("over_deposit_rate",StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointRight(2).toString() : "");
  		agentInfo.put("deposit_rate",StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointRight(2).toString() : "");
      }
    
    
    params.putAll(agentInfo);
    if(agent_settle_fee_Info!=null){
    	params.putAll(agent_settle_fee_Info);
    }
    if (agentFeeAndRule != null&&agentFeeAndRule.size()>0) {
      // params.putAll(agentFeeAndRule);

/*      Object ladder_fee = agentFeeAndRule.get("ladder_fee");
      if (ladder_fee != null && ladder_fee.toString().length() > 0) {
        if (ladder_fee.toString().indexOf("<") > 0) {
          String[] ladder = ladder_fee.toString().split("<");
          String ladder_min = ladder[0];
          String ladder_value = ladder[1];
          String ladder_max = ladder[2];

          params.put("ladder_min", new BigDecimal(ladder_min).movePointRight(2));
          params.put("ladder_value", ladder_value);
          params.put("ladder_max", new BigDecimal(ladder_max).movePointRight(2));
        }
      }*/
    	
    	for (Iterator iterator = agentFeeAndRule.iterator(); iterator.hasNext();) {
			Map<String, Object> map2 = (Map<String, Object>) iterator.next();
			
			Object rule_type = map2.get("rule_type");
			if(rule_type!=null && "SMBOX".equals(rule_type.toString())){
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("smruleline" + (i + 1), ruleline);
					}
					
				}
			}else if(rule_type!=null && "ylst2".equals(rule_type.toString())){ //移联商通1分润
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("dto1ruleline" + (i + 1), ruleline);
					}
					
				}
			}else{
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("ruleline" + (i + 1), ruleline);
					}
					
				}
			}
			
		}
    	

    }
    model.put("params", params);
    model.put("url", url);

    model.put("p", cpage);
    model.put("list", list);

    return "/agent/agentDetail";
  }

  // 进入代理商录入input
  @RequestMapping(value = "/agentInput")
  public String agentInput(final ModelMap model, @RequestParam Map<String, String> params) {
	  //查询集群信息
	  List<Map<String, Object>> gpList =  groupService.getGroups(null);
	  model.put("gpList", gpList);
	  model.put("initpos", "0");
	  return "/agent/agentInput";
  }
  
  /**
   *查看超级刷代理商详情 
   * @author 王帅
   * @param model
   * @param id 超级刷代理商ID编号
   * @param cpage 
   * @return
   */
  @RequestMapping(value = "/agentDetailSPOS")
  public String agentDetailSPOS(final ModelMap model, @RequestParam
  Long id, @RequestParam(value = "p", defaultValue = "1")
  int cpage) {
	 log.info("AgentController agentDetailSPOS start ...");
    // PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
    PageRequest page = new PageRequest(cpage - 1, Integer.MAX_VALUE);
    String url = SysConfig.value("uploadfiles");
    Map<String, Object> params = new HashMap<String, Object>();

    Map<String, Object> agentInfo = agentService.getAgentDetail(id);
    List<Map<String, Object>> agentFeeAndRule = agentService
        .getAgentFeeAndRuleDetail((String) agentInfo.get("agent_no"));
    String agent_no = agentInfo.get("agent_no").toString();
    Map<String, String> map = new HashMap<String, String>();
    map.put("agent_no", agent_no);
    Page<Map<String, Object>> list = terminalService.getPosAllotHistory(map,
        page);
    Map<String, Object> agent_settle_fee_Info= agentService.getAgent_agent_settle_fee(agent_no);
    
    if(agentInfo != null){

  		String commonDepositRate = agentInfo.get("common_deposit_rate") != null ? agentInfo.get("common_deposit_rate").toString() : "";
  		String overDepositRate = agentInfo.get("over_deposit_rate") != null ? agentInfo.get("over_deposit_rate").toString() : "";
  		String depositRate = agentInfo.get("deposit_rate") != null ? agentInfo.get("deposit_rate").toString() : "";
  		
  		agentInfo.put("common_deposit_rate",StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointRight(2).toString() : "");
  		agentInfo.put("over_deposit_rate",StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointRight(2).toString() : "");
  		agentInfo.put("deposit_rate",StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointRight(2).toString() : "");
      }
    
    params.putAll(agentInfo);
    if(agent_settle_fee_Info!=null){
    	params.putAll(agent_settle_fee_Info);
    }
    if (agentFeeAndRule != null&&agentFeeAndRule.size()>0) {
    	for (Iterator iterator = agentFeeAndRule.iterator(); iterator.hasNext();) {
			Map<String, Object> map2 = (Map<String, Object>) iterator.next();
			Object rule_type = map2.get("rule_type");
			if(rule_type!=null && "SMBOX".equals(rule_type.toString())){
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("smruleline" + (i + 1), ruleline);
					}
					
				}
			}else if(rule_type!=null && "ylst2".equals(rule_type.toString())){ //移联商通1分润
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("dto1ruleline" + (i + 1), ruleline);
					}
					
				}
			}else{
				Object share_rule = map2.get("sharing_rule");
				if (share_rule != null && share_rule.toString().length() > 0) {
					String[] sharing_rule = share_rule.toString().split("\\|");
					for (int i = 0; i < sharing_rule.length; i++) {
						String[] ruleline = sharing_rule[i].split("~");
						params.put("ruleline" + (i + 1), ruleline);
					}
					
				}
			}
		}
    }
    model.put("params", params);
    model.put("url", url);
    model.put("p", cpage);
    model.put("list", list);
    log.info("AgentController agentDetailSPOS end");
    return "/agent/agentDetailSPOS";
  }
  
  
 /**
  * 进入超级刷代理商新增界面
  * @author 王帅
  * @param model 返回model
  * @param params 参数
  * @return 跳转到超级刷代理商新增界面
  */
  @RequestMapping(value = "/agentInputSPOS")
  public String agentInputSPOS(final ModelMap model, @RequestParam Map<String, String> params) {
	  log.info("AgentController agentInputSPOS start ...");
	  //查询集群信息
	  List<Map<String, Object>> gpList =  groupService.getGroups(null);
	  model.put("gpList", gpList);
	  model.put("initpos", "0");
	  log.info("AgentController agentInputSPOS end");
	  return "/agent/agentInputSPOS";
  }
  
  
  /**
   * 超级代理商信息
   * @author 王帅
   * @param request
   * @param response
   * @param params
   */
  @RequestMapping(value = "/agentAddSPOS")
  public void agentAddSPOS(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {
	  log.info("AgentController agentAddSPOS start ...");
	//去除参数前后空格
	paramsTrim(params);
	  
    Map<String, String> okMap = new HashMap<String, String>();

    String strDirPath = request.getSession().getServletContext().getRealPath(
        "/");
    String temp = SysConfig.value("uploadtemp");

    String uploadclientlogo = strDirPath + SysConfig.value("uploadclientlogo"); // 手机客户端的logo
    String uploadsyslogo = strDirPath + SysConfig.value("uploadsyslogo"); // 系统logo

    String fr_1_mix = params.get("fr_1_mix");
    // String savePath = strDirPath + uploadfiles;
    String tmpPath = strDirPath + temp;
    try {
      int defaultPassword = new Random().nextInt(10000 * 10000);
      String agentNo = GenSyncNo.getInstance().getNextAgentNo();
      String posAllotBatch = GenSyncNo.getInstance().getNextPosAllotNo();
      params.put("agentNo", agentNo);
      String clientLogo = params.get("clientLogo");
      String managerLogo = params.get("managerLogo");

      String agentPay = params.get("agentPay");
      String mixAmout = params.get("mixAmout");
      String agentRate = params.get("agentRate");

      String pic1 = FileUtils.copyFile(uploadclientlogo, tmpPath, "",
          clientLogo, clientLogo);
      String pic2 = FileUtils.copyFile(uploadsyslogo, tmpPath, "", managerLogo,
          managerLogo);
      if (agentPay == null || "".equals(agentPay)) {
        params.put("agentPay", "0");
      }
      if (mixAmout == null || "".equals(mixAmout)) {
        params.put("mixAmout", "0");
      }
      if (agentRate == null || "".equals(agentRate)) {
        params.put("agentRate", "0");
      } else {
        params.put("agentRate", (new BigDecimal(params.get("agentRate")
            .toString()).movePointLeft(2)).toString());
      }
      params.put("clientLogo", pic1);
      params.put("managerLogo", pic2);
      
      
      //-------获得相关节点-------------start-----------------------
       //由于是新增一级代理商，所以当前代理商编号为null,将其设置为0
		params.put("parent_node", "[0]");

		paramsTrim(params);
      
      //---------------------------------end-------------------------

      // 增加代理商的时间，把“分利润”和"扣费方式“等信息也同时添加进去。
      int rowsuc = agentService.agentInfoAddSPOS(params);

      if (rowsuc > 0) {
        String agentLinkMail = params.get("agentLinkMail");
        String defaultPasswordstr="eeepay"+defaultPassword;
        agentService.agentUserAdd(agentLinkMail, defaultPasswordstr + "", params
            .get("agentName"), agentNo, getUser().getUserName());
        // 构造pos机型号和数量
        Map<String, Integer> posType = new HashMap<String, Integer>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          if (key.indexOf("type-") > -1) {
            if (null != value && !value.equals("0")) {
              key = key.substring(key.indexOf("type-") + 5);
              posType.put(key, Integer.valueOf(value));
            }
          }
        }

        // 发送开通邮件
        MessageUtil.sendMail("移联支付超级刷代理商系统已开通", "网址：http://agent.eeepay.cn\n用户名："
            + agentLinkMail + "\n密码：" + defaultPasswordstr + "\n", agentLinkMail);
        terminalService.allotAgent(agentNo, posType, posAllotBatch, getUser());
        okMap.put("msg", "OK");
        String json = JSONObject.fromObject(okMap).toString();
        log.info("AgentController agentAddSPOS SUCCESS");
        outJson(json, response);
      }
    } catch (Exception e) {
     log.info("Exception AgentController agentAddSPOS = " + e);
      okMap.put("msg", "ERROR");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
      e.printStackTrace();
    }
  }
  

  // 代理商录入submit
  @RequestMapping(value = "/agentAdd")
  public void agentAdd(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {
	//去除参数前后空格
	paramsTrim(params);
	  
    Map<String, String> okMap = new HashMap<String, String>();

    String strDirPath = request.getSession().getServletContext().getRealPath(
        "/");
    String temp = SysConfig.value("uploadtemp");

    String uploadclientlogo = strDirPath + SysConfig.value("uploadclientlogo"); // 手机客户端的logo
    String uploadsyslogo = strDirPath + SysConfig.value("uploadsyslogo"); // 系统logo

    String fr_1_mix = params.get("fr_1_mix");
    // String savePath = strDirPath + uploadfiles;
    String tmpPath = strDirPath + temp;
    try {
      int defaultPassword = new Random().nextInt(10000 * 10000);
      String agentNo = GenSyncNo.getInstance().getNextAgentNo();
      String posAllotBatch = GenSyncNo.getInstance().getNextPosAllotNo();
      params.put("agentNo", agentNo);
      String clientLogo = params.get("clientLogo");
      String managerLogo = params.get("managerLogo");

      String agentPay = params.get("agentPay");
      String mixAmout = params.get("mixAmout");
      String agentRate = params.get("agentRate");

      String pic1 = FileUtils.copyFile(uploadclientlogo, tmpPath, "",
          clientLogo, clientLogo);
      String pic2 = FileUtils.copyFile(uploadsyslogo, tmpPath, "", managerLogo,
          managerLogo);
      if (agentPay == null || "".equals(agentPay)) {
        params.put("agentPay", "0");
      }
      if (mixAmout == null || "".equals(mixAmout)) {
        params.put("mixAmout", "0");
      }
      if (agentRate == null || "".equals(agentRate)) {
        params.put("agentRate", "0");
      } else {
        params.put("agentRate", (new BigDecimal(params.get("agentRate")
            .toString()).movePointLeft(2)).toString());
      }
      params.put("clientLogo", pic1);
      params.put("managerLogo", pic2);
      
      
      //-------获得相关节点-------------start-----------------------
       //由于是新增一级代理商，所以当前代理商编号为null,将其设置为0
		params.put("parent_node", "[0]");

		paramsTrim(params);
      
      //---------------------------------end-------------------------

      // 增加代理商的时间，把“分利润”和"扣费方式“等信息也同时添加进去。
      int rowsuc = agentService.agentInfoAdd(params);

      if (rowsuc > 0) {
        String agentLinkMail = params.get("agentLinkMail");
        String defaultPasswordstr="eeepay"+defaultPassword;
        agentService.agentUserAdd(agentLinkMail, defaultPasswordstr + "", params
            .get("agentName"), agentNo, getUser().getUserName());
        // 构造pos机型号和数量
        Map<String, Integer> posType = new HashMap<String, Integer>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          if (key.indexOf("type-") > -1) {
            if (null != value && !value.equals("0")) {
              key = key.substring(key.indexOf("type-") + 5);
              posType.put(key, Integer.valueOf(value));
            }
          }
        }

        // 发送开通邮件
        MessageUtil.sendMail("移联支付代理商系统已开通", "网址：http://agent.eeepay.cn\n用户名："
            + agentLinkMail + "\n密码：" + defaultPasswordstr + "\n", agentLinkMail);
        terminalService.allotAgent(agentNo, posType, posAllotBatch, getUser());

        okMap.put("msg", "OK");
        String json = JSONObject.fromObject(okMap).toString();
        outJson(json, response);
      }
    } catch (Exception e) {
      okMap.put("msg", "ERROR");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
      e.printStackTrace();
    }
  }
  
  
  /**
   * 进入超级刷代理商修改界面
   * @author 王帅
   * @param model
   * @param id
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/agentModloadSPOS")
  public String agentModloadSPOS(final ModelMap model, @RequestParam
  Long id) throws Exception {

    Map<String, Object> merInfo = agentService.getAgentInfoAndFeeAndFenrun(id);
    Map<String, Object> smboxShareRule = agentService.getSmboxShareRule(id);
    Map<String, Object> ylstShareRule = agentService.getylst2ShareRule(id);
    String isParent = (String.valueOf(agentService.isParent(id).get(
        "coutnAsParent")));
    if (merInfo.get("agent_rate") != null) {
      String agentRate = merInfo.get("agent_rate").toString();
      merInfo.put("agent_rate", (new BigDecimal(agentRate).movePointRight(2))
          .toString());
    }
   //代理商的分润信息.
    try {
    	
      if(merInfo != null){

      	String commonDepositRate = merInfo.get("common_deposit_rate") != null ? merInfo.get("common_deposit_rate").toString() : "";
      	String overDepositRate = merInfo.get("over_deposit_rate") != null ? merInfo.get("over_deposit_rate").toString() : "";
      	String depositRate = merInfo.get("deposit_rate") != null ? merInfo.get("deposit_rate").toString() : "";
      		
      	merInfo.put("common_deposit_rate",StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointRight(2).toString() : "");
      	merInfo.put("over_deposit_rate",StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointRight(2).toString() : "");
      	merInfo.put("deposit_rate",StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointRight(2).toString() : "");
      }
    	
      String rsharing_rule_AB[][] = null;
      String smrsharing_rule_AB[][] = null;
      String ylstsharing_rule_AB[][] = null;
      if (merInfo != null && merInfo.get("rsharing_rule") != null
          && (!"".equals(merInfo.get("rsharing_rule").toString()))) {
        String rsharing_rule = merInfo.get("rsharing_rule").toString();
        String rsharing_rule_A[] = rsharing_rule.split("\\|");
        rsharing_rule_AB = new String[rsharing_rule_A.length][];
        for (int i = 0; i < rsharing_rule_A.length; i++) {
          rsharing_rule_AB[i] = new String[rsharing_rule_A[i].split("~").length];
          for (int j = 0; j < rsharing_rule_A[i].split("~").length; j++) {
            rsharing_rule_AB[i][j] = rsharing_rule_A[i].split("~")[j];
          }
          rsharing_rule_AB[i][1] =new BigDecimal( rsharing_rule_AB[i][1]).movePointRight(2).toString();
        }
        
      }else{
    	  rsharing_rule_AB = new String[4][4];
          rsharing_rule_AB[0][0]="0";
          rsharing_rule_AB[0][1]="0";
          rsharing_rule_AB[0][2]="300";
          rsharing_rule_AB[0][3]="1";
          rsharing_rule_AB[1][0]="300";
          rsharing_rule_AB[1][1]="55";
          rsharing_rule_AB[1][2]="5000";
          rsharing_rule_AB[1][3]="1";
          rsharing_rule_AB[2][0]="5000";
          rsharing_rule_AB[2][1]="65";
          rsharing_rule_AB[2][2]="10000";
          rsharing_rule_AB[2][3]="1";
          rsharing_rule_AB[3][0]="10000";
          rsharing_rule_AB[3][1]="75";
          rsharing_rule_AB[3][2]="0";
          rsharing_rule_AB[3][3]="1";
      }
      
      if(smboxShareRule != null && smboxShareRule.get("sharing_rule")!= null
              && (!"".equals(smboxShareRule.get("sharing_rule").toString()))){
    	    String smrsharing_rule = smboxShareRule.get("sharing_rule").toString();
            String smrsharing_rule_A[] = smrsharing_rule.split("\\|");
            smrsharing_rule_AB = new String[smrsharing_rule_A.length][];
            for (int i = 0; i < smrsharing_rule_A.length; i++) {
            	smrsharing_rule_AB[i] = new String[smrsharing_rule_A[i].split("~").length];
              for (int j = 0; j < smrsharing_rule_A[i].split("~").length; j++) {
            	  smrsharing_rule_AB[i][j] = smrsharing_rule_A[i].split("~")[j];
              }
              smrsharing_rule_AB[i][1] =new BigDecimal( smrsharing_rule_AB[i][1]).movePointRight(2).toString();
            }
      } else {
        //小宝
        smrsharing_rule_AB = new String[4][4];
        smrsharing_rule_AB[0][0]="0";
        smrsharing_rule_AB[0][1]="0";
        smrsharing_rule_AB[0][2]="300";
        smrsharing_rule_AB[0][3]="1";
        smrsharing_rule_AB[1][0]="300";
        smrsharing_rule_AB[1][1]="55";
        smrsharing_rule_AB[1][2]="5000";
        smrsharing_rule_AB[1][3]="1";
        smrsharing_rule_AB[2][0]="5000";
        smrsharing_rule_AB[2][1]="65";
        smrsharing_rule_AB[2][2]="10000";
        smrsharing_rule_AB[2][3]="1";
        smrsharing_rule_AB[3][0]="10000";
        smrsharing_rule_AB[3][1]="100";
        smrsharing_rule_AB[3][2]="0";
        smrsharing_rule_AB[3][3]="1";
      }
      
      if(ylstShareRule != null && ylstShareRule.get("sharing_rule")!= null
              && (!"".equals(ylstShareRule.get("sharing_rule").toString()))){
    	    String ylstsharing_rule = ylstShareRule.get("sharing_rule").toString();
            String ylstsharing_rule_A[] = ylstsharing_rule.split("\\|");
            ylstsharing_rule_AB = new String[ylstsharing_rule_A.length][];
            for (int i = 0; i < ylstsharing_rule_A.length; i++) {
            	ylstsharing_rule_AB[i] = new String[ylstsharing_rule_A[i].split("~").length];
              for (int j = 0; j < ylstsharing_rule_A[i].split("~").length; j++) {
            	  ylstsharing_rule_AB[i][j] = ylstsharing_rule_A[i].split("~")[j];
              }
              ylstsharing_rule_AB[i][1] =new BigDecimal( ylstsharing_rule_AB[i][1]).movePointRight(2).toString();
            }
      } else {
        //移联商通
        ylstsharing_rule_AB = new String[4][4];
        ylstsharing_rule_AB[0][0]="0";
        ylstsharing_rule_AB[0][1]="0";
        ylstsharing_rule_AB[0][2]="300";
        ylstsharing_rule_AB[0][3]="1";
        ylstsharing_rule_AB[1][0]="300";
        ylstsharing_rule_AB[1][1]="80";
        ylstsharing_rule_AB[1][2]="5000";
        ylstsharing_rule_AB[1][3]="1";
        ylstsharing_rule_AB[2][0]="5000";
        ylstsharing_rule_AB[2][1]="80";
        ylstsharing_rule_AB[2][2]="10000";
        ylstsharing_rule_AB[2][3]="1";
        ylstsharing_rule_AB[3][0]="10000";
        ylstsharing_rule_AB[3][1]="80";
        ylstsharing_rule_AB[3][2]="0";
        ylstsharing_rule_AB[3][3]="1";
      }
      
      merInfo.put("ylstsharing_rule_chaifen", ylstsharing_rule_AB);
      merInfo.put("rsharing_rule_chaifen", rsharing_rule_AB);
      merInfo.put("smrsharing_rule_chaifen", smrsharing_rule_AB);
    } catch (Exception e) {
      e.printStackTrace();
      log.info("拆分代理商分润异常!", e);
    }

    
  //代理商的扣费费率。
    try {
      //agent_fee的阶梯字段，需要右边移动两位后，才展示到页面。
      String rsharing_rule_A[]  = null;
      if (merInfo.get("fladder_fee") != null
          && (!"".equals(merInfo.get("fladder_fee").toString()))) {
        String fladder_fee = merInfo.get("fladder_fee").toString();
        rsharing_rule_A = fladder_fee.split("<");
        rsharing_rule_A[0]= new BigDecimal(rsharing_rule_A[0]).movePointRight(2).toString();
        rsharing_rule_A[2]= new BigDecimal(rsharing_rule_A[2]).movePointRight(2).toString();
      } else {
        rsharing_rule_A = new String[3] ;
        rsharing_rule_A[0]="0";
        rsharing_rule_A[1]="0";
        rsharing_rule_A[2]="0";
      }
      merInfo.put("fladder_fee_chaifen", rsharing_rule_A);
      
    //agent_fee的 费率字段，需要右边移动两位后，才展示到页面。
      String ffee_rate="";
      if (merInfo.get("ffee_rate") != null
          && (!"".equals(merInfo.get("ffee_rate").toString()))) {
          ffee_rate = merInfo.get("ffee_rate").toString();
          ffee_rate= new BigDecimal(ffee_rate).movePointRight(2).toString();
          merInfo.put("ffee_rate", ffee_rate);
       }
      
    } catch (Exception e) {
      e.printStackTrace();
      log.info("拆分代理商扣费费率异常!", e);
    }
    
    
    model.put("params", merInfo);
    model.put("isParent", isParent);
	//查询所有集群
    List<Map<String, Object>> gpList =  groupService.getGroups(null);
	model.put("gpList", gpList);
    return "/agent/agentModifySPOS";
  }

  // 修改input页面。
  @RequestMapping(value = "/agentModload")
  public String agentModload(final ModelMap model, @RequestParam
  Long id) throws Exception {

    Map<String, Object> merInfo = agentService.getAgentInfoAndFeeAndFenrun(id);
    Map<String, Object> smboxShareRule = agentService.getSmboxShareRule(id);
    Map<String, Object> ylstShareRule = agentService.getylst2ShareRule(id);
    String isParent = (String.valueOf(agentService.isParent(id).get(
        "coutnAsParent")));
    if (merInfo.get("agent_rate") != null) {
      String agentRate = merInfo.get("agent_rate").toString();
      merInfo.put("agent_rate", (new BigDecimal(agentRate).movePointRight(2))
          .toString());
    }
   //代理商的分润信息.
    try {
    	
      if(merInfo != null){

  		String commonDepositRate = merInfo.get("common_deposit_rate") != null ? merInfo.get("common_deposit_rate").toString() : "";
  		String overDepositRate = merInfo.get("over_deposit_rate") != null ? merInfo.get("over_deposit_rate").toString() : "";
  		String depositRate = merInfo.get("deposit_rate") != null ? merInfo.get("deposit_rate").toString() : "";
  		
  		merInfo.put("common_deposit_rate",StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointRight(2).toString() : "");
  		merInfo.put("over_deposit_rate",StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointRight(2).toString() : "");
  		merInfo.put("deposit_rate",StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointRight(2).toString() : "");
      }
    	
    	
      String rsharing_rule_AB[][] = null;
      String smrsharing_rule_AB[][] = null;
      String ylstsharing_rule_AB[][] = null;
      if (merInfo != null && merInfo.get("rsharing_rule") != null
          && (!"".equals(merInfo.get("rsharing_rule").toString()))) {
        String rsharing_rule = merInfo.get("rsharing_rule").toString();
        String rsharing_rule_A[] = rsharing_rule.split("\\|");
        rsharing_rule_AB = new String[rsharing_rule_A.length][];
        for (int i = 0; i < rsharing_rule_A.length; i++) {
          rsharing_rule_AB[i] = new String[rsharing_rule_A[i].split("~").length];
          for (int j = 0; j < rsharing_rule_A[i].split("~").length; j++) {
            rsharing_rule_AB[i][j] = rsharing_rule_A[i].split("~")[j];
          }
          rsharing_rule_AB[i][1] =new BigDecimal( rsharing_rule_AB[i][1]).movePointRight(2).toString();
        }
        
      }else{
    	  rsharing_rule_AB = new String[4][4];
          rsharing_rule_AB[0][0]="0";
          rsharing_rule_AB[0][1]="0";
          rsharing_rule_AB[0][2]="300";
          rsharing_rule_AB[0][3]="1";
          rsharing_rule_AB[1][0]="300";
          rsharing_rule_AB[1][1]="55";
          rsharing_rule_AB[1][2]="5000";
          rsharing_rule_AB[1][3]="1";
          rsharing_rule_AB[2][0]="5000";
          rsharing_rule_AB[2][1]="65";
          rsharing_rule_AB[2][2]="10000";
          rsharing_rule_AB[2][3]="1";
          rsharing_rule_AB[3][0]="10000";
          rsharing_rule_AB[3][1]="75";
          rsharing_rule_AB[3][2]="0";
          rsharing_rule_AB[3][3]="1";
      }
      
      if(smboxShareRule != null && smboxShareRule.get("sharing_rule")!= null
              && (!"".equals(smboxShareRule.get("sharing_rule").toString()))){
    	    String smrsharing_rule = smboxShareRule.get("sharing_rule").toString();
            String smrsharing_rule_A[] = smrsharing_rule.split("\\|");
            smrsharing_rule_AB = new String[smrsharing_rule_A.length][];
            for (int i = 0; i < smrsharing_rule_A.length; i++) {
            	smrsharing_rule_AB[i] = new String[smrsharing_rule_A[i].split("~").length];
              for (int j = 0; j < smrsharing_rule_A[i].split("~").length; j++) {
            	  smrsharing_rule_AB[i][j] = smrsharing_rule_A[i].split("~")[j];
              }
              smrsharing_rule_AB[i][1] =new BigDecimal( smrsharing_rule_AB[i][1]).movePointRight(2).toString();
            }
      } else {
        //小宝
        smrsharing_rule_AB = new String[4][4];
        smrsharing_rule_AB[0][0]="0";
        smrsharing_rule_AB[0][1]="0";
        smrsharing_rule_AB[0][2]="300";
        smrsharing_rule_AB[0][3]="1";
        smrsharing_rule_AB[1][0]="300";
        smrsharing_rule_AB[1][1]="55";
        smrsharing_rule_AB[1][2]="5000";
        smrsharing_rule_AB[1][3]="1";
        smrsharing_rule_AB[2][0]="5000";
        smrsharing_rule_AB[2][1]="65";
        smrsharing_rule_AB[2][2]="10000";
        smrsharing_rule_AB[2][3]="1";
        smrsharing_rule_AB[3][0]="10000";
        smrsharing_rule_AB[3][1]="100";
        smrsharing_rule_AB[3][2]="0";
        smrsharing_rule_AB[3][3]="1";
      }
      
      if(ylstShareRule != null && ylstShareRule.get("sharing_rule")!= null
              && (!"".equals(ylstShareRule.get("sharing_rule").toString()))){
    	    String ylstsharing_rule = ylstShareRule.get("sharing_rule").toString();
            String ylstsharing_rule_A[] = ylstsharing_rule.split("\\|");
            ylstsharing_rule_AB = new String[ylstsharing_rule_A.length][];
            for (int i = 0; i < ylstsharing_rule_A.length; i++) {
            	ylstsharing_rule_AB[i] = new String[ylstsharing_rule_A[i].split("~").length];
              for (int j = 0; j < ylstsharing_rule_A[i].split("~").length; j++) {
            	  ylstsharing_rule_AB[i][j] = ylstsharing_rule_A[i].split("~")[j];
              }
              ylstsharing_rule_AB[i][1] =new BigDecimal( ylstsharing_rule_AB[i][1]).movePointRight(2).toString();
            }
      } else {
        //移联商通
        ylstsharing_rule_AB = new String[4][4];
        ylstsharing_rule_AB[0][0]="0";
        ylstsharing_rule_AB[0][1]="0";
        ylstsharing_rule_AB[0][2]="300";
        ylstsharing_rule_AB[0][3]="1";
        ylstsharing_rule_AB[1][0]="300";
        ylstsharing_rule_AB[1][1]="80";
        ylstsharing_rule_AB[1][2]="5000";
        ylstsharing_rule_AB[1][3]="1";
        ylstsharing_rule_AB[2][0]="5000";
        ylstsharing_rule_AB[2][1]="80";
        ylstsharing_rule_AB[2][2]="10000";
        ylstsharing_rule_AB[2][3]="1";
        ylstsharing_rule_AB[3][0]="10000";
        ylstsharing_rule_AB[3][1]="80";
        ylstsharing_rule_AB[3][2]="0";
        ylstsharing_rule_AB[3][3]="1";
      }
      
      merInfo.put("rsharing_rule_chaifen", rsharing_rule_AB);
      merInfo.put("smrsharing_rule_chaifen", smrsharing_rule_AB);
      merInfo.put("ylstsharing_rule_chaifen", ylstsharing_rule_AB);
    } catch (Exception e) {
      e.printStackTrace();
      log.info("拆分代理商分润异常!", e);
    }

    
  //代理商的扣费费率。
    try {
      //agent_fee的阶梯字段，需要右边移动两位后，才展示到页面。
      String rsharing_rule_A[]  = null;
      if (merInfo.get("fladder_fee") != null
          && (!"".equals(merInfo.get("fladder_fee").toString()))) {
        String fladder_fee = merInfo.get("fladder_fee").toString();
        rsharing_rule_A = fladder_fee.split("<");
        rsharing_rule_A[0]= new BigDecimal(rsharing_rule_A[0]).movePointRight(2).toString();
        rsharing_rule_A[2]= new BigDecimal(rsharing_rule_A[2]).movePointRight(2).toString();
      } else {
        rsharing_rule_A = new String[3] ;
        rsharing_rule_A[0]="0";
        rsharing_rule_A[1]="0";
        rsharing_rule_A[2]="0";
      }
      merInfo.put("fladder_fee_chaifen", rsharing_rule_A);
      
    //agent_fee的 费率字段，需要右边移动两位后，才展示到页面。
      String ffee_rate="";
      if (merInfo.get("ffee_rate") != null
          && (!"".equals(merInfo.get("ffee_rate").toString()))) {
          ffee_rate = merInfo.get("ffee_rate").toString();
          ffee_rate= new BigDecimal(ffee_rate).movePointRight(2).toString();
          merInfo.put("ffee_rate", ffee_rate);
       }
      
    } catch (Exception e) {
      e.printStackTrace();
      log.info("拆分代理商扣费费率异常!", e);
    }
    
    
    model.put("params", merInfo);
    model.put("isParent", isParent);
	//查询所有集群
    List<Map<String, Object>> gpList =  groupService.getGroups(null);
	model.put("gpList", gpList);
    return "/agent/agentModify";
  }

  // 代理商修改submit
  @RequestMapping(value = "/agentModify")
  public void agentModify(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {

    Map<String, String> okMap = new HashMap<String, String>();
    String strDirPath = request.getSession().getServletContext().getRealPath(
        "/");
    String temp = SysConfig.value("uploadtemp");

    String uploadclientlogo = strDirPath + SysConfig.value("uploadclientlogo"); // 手机客户端的logo
    String uploadsyslogo = strDirPath + SysConfig.value("uploadsyslogo"); // 系统logo

    // String uploadfiles = SysConfig.value("uploadfiles");
    // String savePath = strDirPath + uploadfiles;
    String tmpPath = strDirPath + temp;
    try {

      String clientLogo = params.get("clientLogo");
      String managerLogo = params.get("managerLogo");
      String oldClientLogo = params.get("oldClientLogo");
      String oldManagerLogo = params.get("oldManagerLogo");
        String agentName = params.get("agentName");
        String oldAgentName = params.get("old_agent_name");
        String agentNo = params.get("agentNo");

      if (!oldClientLogo.equals(clientLogo)) {
        String pic1 = FileUtils.copyFile(uploadclientlogo, tmpPath, "",
            clientLogo, clientLogo);
        String pic1Logo = SysConfig.value("agentaddress")
            + SysConfig.value("uploadclientlogo") + pic1;
        params.put("clientLogo", pic1Logo);
      }
      if (!oldManagerLogo.equals(managerLogo)) {
        String pic2 = FileUtils.copyFile(uploadsyslogo, tmpPath, "",
            managerLogo, managerLogo);
        String pic2Logo = SysConfig.value("agentaddress")
            + SysConfig.value("uploadsyslogo") + pic2;
        params.put("managerLogo", pic2Logo);
      }

      int rowsuc = agentService.agentInfoUpdate(params);
      if (rowsuc > 0) {
          // 判断是否有修改一级代理商名称，有修改择同步钱包
          if(agentName != null && !agentName.equals(oldAgentName)){

              Map<String, Object> map = new HashMap<String, Object>();
              map.put("agent_no", agentNo);
              map.put("agent_name", agentName);
              String hmac = Md5.md5Str(agentNo + agentName + Constants.BAG_HMAC);
              map.put("hmac", hmac);

              try {
                  Http.send(SysConfig.value("agentModName2Bag"), map, "UTF-8");
              }catch (Exception e1){
                  e1.printStackTrace();
                  log.error("修改一级代理商名称同步钱包失败");
              }
          }

        okMap.put("msg", "OK");
        String json = JSONObject.fromObject(okMap).toString();
        outJson(json, response);
      }
    } catch (Exception e) {
      okMap.put("msg", "ERROR");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
      e.printStackTrace();
    }
  }

  // 代理用户名查询验证
  @RequestMapping(value = "/agentUserCheck")
  public void agentUserCheck(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {

    Map<String, String> okMap = new HashMap<String, String>();
    String userName = params.get("userName");
    Map<String, Object> merInfo = agentService.getAgentUserByName(userName);
    if (merInfo != null) {
      if (merInfo.size() > 0) {
        okMap.put("msg", "exist");
        String json = JSONObject.fromObject(okMap).toString();
        outJson(json, response);
      }
    } else {
      okMap.put("msg", "noexist");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  
//超级刷代理用户名查询验证
 @RequestMapping(value = "/agentUserCheckSPOS")
 public void agentUserCheckSPOS(HttpServletRequest request,  HttpServletResponse response, @RequestParam
     Map<String, String> params) {
   Map<String, String> okMap = new HashMap<String, String>();
   String userName = params.get("userName");
   Map<String, Object> merInfo = agentService.getAgentUserByNameSPOS(userName);
   if (merInfo != null) {
     if (merInfo.size() > 0) {
       okMap.put("msg", "exist");
       String json = JSONObject.fromObject(okMap).toString();
       outJson(json, response);
     }
   } else {
     okMap.put("msg", "noexist");
     String json = JSONObject.fromObject(okMap).toString();
     outJson(json, response);
   }
 }
 
		//代理用户名查询验证
		@RequestMapping(value = "/agentNameCheckSPOS")
		public void agentNameCheckSPOS(HttpServletRequest request,  HttpServletResponse response, @RequestParam
		    Map<String, String> params) {
		  Map<String, String> okMap = new HashMap<String, String>();
		  // 增加判断 代理商名称
		  String agentName = params.get("agentName");
		  String agentNo = params.get("agentNo");
		  Map<String, Object> agentInfo = agentService.getAgentUserByAgentNameSPOS(agentName);
		  if (agentInfo != null && agentInfo.size() > 0) {
		    if (agentNo != null && !"".equals(agentNo)) {
		      String agent_no = (String) agentInfo.get("agent_no");
		      if (!agentNo.equals(agent_no)) {
		        okMap.put("msg", "exist");
		        String json = JSONObject.fromObject(okMap).toString();
		        outJson(json, response);
		        return;
		      } else {
		        okMap.put("msg", "noexist");
		        String json = JSONObject.fromObject(okMap).toString();
		        outJson(json, response);
		        return;
		      }
		    }
		    okMap.put("msg", "exist");
		    String json = JSONObject.fromObject(okMap).toString();
		    outJson(json, response);
		    return;
		  } else {
		    okMap.put("msg", "noexist");
		    String json = JSONObject.fromObject(okMap).toString();
		    outJson(json, response);
		  }
		}

  // 代理用户名查询验证
  @RequestMapping(value = "/agentNameCheck")
  public void agentNameCheck(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {

    Map<String, String> okMap = new HashMap<String, String>();
    // 增加判断 代理商名称
    String agentName = params.get("agentName");
    String agentNo = params.get("agentNo");
    Map<String, Object> agentInfo = agentService.getAgentUserByAgentName(agentName);
    if (agentInfo != null && agentInfo.size() > 0) {
      if (agentNo != null && !"".equals(agentNo)) {
        String agent_no = (String) agentInfo.get("agent_no");
        if (!agentNo.equals(agent_no)) {
          okMap.put("msg", "exist");
          String json = JSONObject.fromObject(okMap).toString();
          outJson(json, response);
          return;
        } else {
          okMap.put("msg", "noexist");
          String json = JSONObject.fromObject(okMap).toString();
          outJson(json, response);
          return;
        }
      }
      okMap.put("msg", "exist");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
      return;
    } else {
      okMap.put("msg", "noexist");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }

  // 代理商冻结
  @RequestMapping(value = "/agentFreeze")
  public void agentFreeze(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    int rowsuc = agentService.agentInfoFreeze(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 锁定代理商扣率
  @RequestMapping(value = "/agentLocked")
  public void agentLocked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("locked_status", "1");//锁定状态
    int rowsuc = agentService.agentInfoLocked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 锁定代理商扣率
  @RequestMapping(value = "/agentNotLocked")
  public void agentNotLocked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("locked_status", "0");//未锁定状态
    int rowsuc = agentService.agentInfoLocked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 审核代理商审核
  @RequestMapping(value = "/agentChecked")
  public void agentChecked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("checked_status", "1");//已审核状态
    int rowsuc = agentService.agentInfoChecked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      okMap.put("checked_status", "1");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 审核代理商分润比例
  @RequestMapping(value = "/agentShareChecked")
  public void agentShareChecked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("share_checked_status", "1");//已审核状态
    int rowsuc = agentService.agentShareChecked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      okMap.put("checked_status", "1");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 锁定代理商分润比例
  @RequestMapping(value = "/agentShareLocked")
  public void agentShareLocked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("share_lockeded_status", "1");//已锁定状态
    int rowsuc = agentService.agentShareLocked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      okMap.put("share_lockeded_status", "1");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 解锁代理商分润比例
  @RequestMapping(value = "/agentShareNotLocked")
  public void agentShareNotLocked(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) throws SQLException {

    Map<String, String> okMap = new HashMap<String, String>();
    params.put("share_lockeded_status", "0");//未锁定状态
    int rowsuc = agentService.agentShareLocked(params);
    if (rowsuc > 0) {
      okMap.put("msg", "OK");
      okMap.put("share_checked_status", "0");
      String json = JSONObject.fromObject(okMap).toString();
      outJson(json, response);
    }
  }
  // 代理商删除
  @RequestMapping(value = "/agentDel")
  public void agentDel(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {

    Map<String, String> okMap = new HashMap<String, String>();
    boolean flag = agentService.checkAgentCanDel(params);
    if (flag) {
      int rowsuc = agentService.agentInfoDel(params);
      if (rowsuc > 0) {
        okMap.put("msg", "OK");
        String json = JSONObject.fromObject(okMap).toString();
        outJson(json, response);
      }
    }
  }

  // 代理商查询
  @RequestMapping(value = "/agentUserSelect")
  public void agentUserSelect(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {
  
	    // Map<String, String> okMap = new HashMap<String, String>();
		log.info("/agentUserSelect");
	    List<Map<String, Object>> list = agentService.agentListSelect();
	    log.info("/agentUserSelect----------end");
	
	    JSONArray arr = JSONArray.fromObject(list);
	    String json = arr.toString();
	    outJson(json, response);
     
  }

  // 代理商商户查询
  @RequestMapping(value = "/agentMerchantSelect")
  public void agentMerchantSelect(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params) {
	    log.info("/start"+System.currentTimeMillis());
	    log.info("/start"+System.nanoTime());
	try{
	    String agentNo = (String) params.get("agentNo");
	    log.info("/agentMerchantSelect,agentNo="+agentNo);
	    List<Map<String, Object>> list = agentService.merchantSelect(agentNo);
	    log.info("/agentMerchantSelect--agentService.merchantSele");
	    log.info(String.valueOf(list.size()));
	    JSONArray arr = JSONArray.fromObject(list);
	    log.info("/sdfsdfsdfsdfsdfsdf");
	    String json = arr.toString();
	    log.info("/54756786575767");
	    outJson(json, response);
	    log.info("/54756786575767");
	    log.info("/end"+System.currentTimeMillis());
	    log.info("/end"+System.nanoTime());
	}catch( Exception e){
	      log.info("catch---");
		   LogException.logException(e, log);  
		   outJson("", response);
		   log.info("catch--outJson-");
	  }
  }

  //代理商查询
  @RequestMapping(value = "/agentSelect")
  public void agentSelect(HttpServletRequest request,
      HttpServletResponse response, @RequestParam
      Map<String, String> params,@RequestParam(value = "p", defaultValue = "1")
      int cpage) {
	String agentNo = params.get("agent_no");
    params.put("getChildByParentAgentno", agentNo);
    List<Map<String, Object>>  list = agentService.getAgentListForTag(params, new PageRequest(0,9999));
   
//    List<Map<String,Object>> maps = list.getContent();
    JSONArray arr = JSONArray.fromObject(list);
    String json = arr.toString();
    outJson(json, response);
  }
  	
	 @RequestMapping(value = "/transRuleList")
	 public String transRuleList(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage) {
		 
		 PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		 
		 Page<Map<String, Object>> list = agentService.getAgentTransRuleList(params, page);
		 
		 model.put("params", params);
		 model.put("list", list);
		 
		 return "/agent/transRuleList";
	 }
	 
	 @RequestMapping(value="/agentTransRuleInput")
	 public String agentTransRuleInput(final ModelMap model, @RequestParam Map<String, String> params){
		 return "/agent/transRuleInput";
	 }
	 
	 @RequestMapping(value = "/addTranRule")
	 public void addTranRule(final ModelMap model,HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> params) {
		 Map<String, String> okMap = new HashMap<String, String>();
		 
		 paramsTrim(params);
		 params.put("create_user", getUser().getRealName());
		 okMap.put("msg", "ERROR");
		 int row = agentService.addTransRule(params);
		 if(row>0){
			 okMap.put("msg", "OK");
		 }else{
			 okMap.put("msgDetail", "限额添加失败");
		 }
		 String json = JSONObject.fromObject(okMap).toString();
		 outJson(json, response);
	 }
	 
	 @RequestMapping(value = "/agentTransRuleDel")
	 public void agentTransRuleDel(final ModelMap model,HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> params) {
		 Map<String, String> okMap = new HashMap<String, String>();
		 int row = 0;
		 paramsTrim(params);
		 okMap.put("msg", "ERROR");
		 String id = params.get("id");
		 row = agentService.delTransRule(id);
		 if (row>0) {
			 okMap.put("msg", "OK");
			 okMap.put("msgDetail", "删除成功");
		 }else{
			 okMap.put("msgDetail", "删除失败");
		 }
		 
		 String json = JSONObject.fromObject(okMap).toString();
		 outJson(json, response);
	 }
	 
	 @RequestMapping(value = "/viewTransRule")
	 public String viewTransRule(final ModelMap model, @RequestParam String ids) {
		 
		 model.put("ids", ids);
		 return "agent/viewTransRule";
	 }
  
	 @RequestMapping(value="/transRuleModify")
	 public void transRuleModify(final ModelMap model, @RequestParam Map<String, String> params){
		 agentService.transRuleModify(params);
	 }
	 
}
