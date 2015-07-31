package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.eeepay.boss.domain.*;
import com.eeepay.boss.service.*;
import com.eeepay.boss.utils.*;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.encryptor.rsa.Base64Utils;
import com.eeepay.boss.encryptor.rsa.RSAUtils;
import com.eeepay.boss.enums.CurrencyType;
import com.eeepay.boss.enums.TransSource;
import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.enums.TransType;
import com.eeepay.boss.service.router.MerchantHandlingChargeService;
import com.eeepay.boss.utils.pub.Sms;

/**
 * 商户管理
 * 
 * @author zhanghw
 * 
 */
/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/mer")
public class MerchantController extends BaseController {

	@Resource
	private TransService transService;
	
	@Resource
	private MRCTransctionService mrcTransctionService;

	@Resource
	private MerchantService merchantService;

	@Resource
	private AcqMerchantService acqMerchantService;

	@Resource
	private BankCardService bankCardService;

	@Resource
	private AgentService agentService;

	@Resource
	private TerminalService terminalService;
	@Resource
	private MerchantHandlingChargeService merchantHandlingChargeService;

	@Resource
	private MobileChargeService mobileChargeService;

	@Resource
	private GroupMerchantService groupMerchantService;

	@Resource
	private PurseService purseService;
	
	@Resource
	private SZFSService szfsService;
	
	@Resource
	private UserGroupService userGroupService;

	@Resource
	private PosTypeService posTypeService;
	
	private static final Logger log = LoggerFactory.getLogger(MerchantController.class);
	
	@RequestMapping(value = "/getMerchantInfoByMerchantNo")
	public void getMerchantInfoByMerchantNo(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		log.info("MerchantController getMerchantInfoByMerchantNo START");
		Map<String, String> okMap = new HashMap<String, String>();
		String code = "1001";
		if(params != null){
			if(null != params.get("merchant_no") && !"".equals(params.get("merchant_no").toString())){
				Map<String, Object>  merchantMap = merchantService.getFirstMerchantInfoByMerchantNo(params.get("merchant_no").toString().trim());
				if(merchantMap != null){
					code = "1000";
					okMap.put("merchant_short_name", merchantMap.get("merchant_short_name").toString());
					okMap.put("agentNo", merchantMap.get("agent_no").toString());
				}else{
					code = "1002";
				}
			}
		}
		okMap.put("code",code);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		log.info("MerchantController getMerchantInfoByMerchantNo END");
	}
	
	private String posModelName(String pos_model){
		log.info("MerchantController posModelName START");
		String posModelName = pos_model;
		if(!StringUtil.isEmpty(posModelName)){
			Map<String, Object> posModelMap =  posTypeService.getPosModelName(pos_model);
			if(posModelMap != null && posModelMap.size() > 0){
				if(posModelMap.get("pos_model_name") != null && !"".equals(posModelMap.get("pos_model_name").toString())){
					posModelName = posModelMap.get("pos_model_name").toString();
				}
			}
		}
		log.info("MerchantController posModelName End");
		return posModelName;
	}
	
	private String posTypeName(String pos_type){
		log.info("MerchantController posTypeName START");
		String posTypeName = pos_type;
		Map<String, Object> posTypeMap =  posTypeService.getPosType(pos_type);
		if(posTypeMap != null && posTypeMap.size() > 0){
			if(posTypeMap.get("pos_type_name") != null && !"".equals(posTypeMap.get("pos_type_name").toString())){
				posTypeName = posTypeMap.get("pos_type_name").toString();
			}
		}
		log.info("MerchantController posTypeName End");
		return posTypeName;
	}
	
	@RequestMapping(value = "/getHlfAssembleFailReason")
	public void getHlfAssembleFailReason(HttpServletResponse response, HttpServletRequest request, @RequestParam String merchant_no){
		log.info("MerchantController getHlfAssembleFailReason START...");
		try {
			Map<String, Object> map = merchantService.getHlfAssembleFailReason(merchant_no);
			outJson(JSON.toJSONString(map), response);
		} catch (Exception e) {
			outText("Error！",response);
			log.error("MerchantController getHlfAssembleFailReason Error");
		}
		
		log.info("MerchantController getHlfAssembleFailReason End");
	}
	
	/**
	 * 好乐付自动绑定
	 * */
	@RequestMapping(value = "/hlfAssembleACQMerchant")
	public void hlfAssembleACQMerchant(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request){
		
		log.info("MerchantController hlfAssembleACQMerchant START...");
		JSONObject json = new JSONObject();
		
		try {
			int hlfAssembleCount = merchantService.getHLFAssembleMerchantNo(getUser().getRealName());
			json.put("status", hlfAssembleCount);
			outJson(json.toString(), response);
		} catch (Exception e) {
			log.error("MerchantController hlfAssembleACQMerchant Error");
		}
		log.info("MerchantController hlfAssembleACQMerchant End");
	}
	
	/**
	 * 好乐付同步商户信息
	 * @param params 请求参数
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/hlfSendMerchant")
	public void hlfSendMerchant(@RequestParam Map<String, String> params,  @RequestParam Long id,HttpServletResponse response2) {
		log.info("MerchantController hlfSendMerchant start...");
		JSONObject json2 = new JSONObject();
		try {
			Map<String, Object> map = merchantService.getMerDetail(id);
			if(map != null){
//				if(null != map.get("my_settle") && "0".equals(map.get("my_settle").toString())){
					HttpClient httpclient = new DefaultHttpClient();
					//请求处理页面  
					HttpPost httppost = new HttpPost(SysConfig.value("sendHLFMerchantInfo"));
					//创建待处理的表单域内容文本
					StringBody agentId  = new StringBody("440305");//map.get("agent_no").toString()); //固定代理商编号440305表示移付宝
					StringBody merchantType   = null;
					StringBody bankAccountType   =null;
					StringBody license   = new StringBody("");
					if("对公".equals(map.get("account_type").toString())){
						merchantType   = new StringBody("2");
						bankAccountType = new StringBody("2");
						if(null == map.get("bus_license_no") || "".equals(map.get("bus_license_no").toString())){
							json2.put("status", 2004);
							json2.put("message", "对公商户营业执照编号不允许为空！");
							outJson(json2.toString(), response2);
							return;
						}
						license   = new StringBody(map.get("bus_license_no").toString());
					}else{
						merchantType   = new StringBody("1");
						bankAccountType = new StringBody("1");
					}
					
					StringBody merchantName   = new StringBody(map.get("merchant_name").toString(),Charset.forName("UTF-8"));
					StringBody idcard   = new StringBody(map.get("id_card_no").toString());
					StringBody mobile   = new StringBody(map.get("mobile_username").toString());
					StringBody province   = new StringBody(map.get("province").toString(),Charset.forName("UTF-8"));
					StringBody city   = new StringBody(map.get("city").toString(),Charset.forName("UTF-8"));
					StringBody address   = new StringBody(map.get("address").toString(),Charset.forName("UTF-8"));
					StringBody bankName   = new StringBody(map.get("bank_name").toString(),Charset.forName("UTF-8"));
					StringBody bankUnionpayCode   = new StringBody(map.get("cnaps_no").toString());
			 		StringBody bankBranch   = new StringBody(map.get("bank_name").toString(),Charset.forName("UTF-8"));
					StringBody bankHolder   = new StringBody(map.get("account_name").toString(),Charset.forName("UTF-8"));
					StringBody bankAccount   = new StringBody(map.get("account_no").toString());
					StringBody mccCode   = new StringBody("8001");
					StringBody mccType   = null; //商户类型
					if(null != map.get("merchant_type") && !"".equals(map.get("merchant_type").toString())){
						if("5812".equals(map.get("merchant_type").toString())){
							mccType   = new StringBody("1");
						}else if("5331".equals(map.get("merchant_type").toString())){
							mccType   = new StringBody("2");
						}else if("5541".equals(map.get("merchant_type").toString())){
							mccType   = new StringBody("3");
						}else if("1520".equals(map.get("merchant_type").toString())){
							mccType   = new StringBody("4");
						}else if("5111".equals(map.get("merchant_type").toString())){
							mccType   = new StringBody("5");
						}else{
							mccType   = new StringBody("6");
						}
					}
					
					//根据商户编号得到商户费率
					Map<String, Object> merchantFee = merchantService.queryMerchantFee(map.get("merchant_no").toString());
					StringBody commissionType   = null;
					if(merchantFee != null){
						if(null != merchantFee.get("fee_type") && !"".equals(merchantFee.get("fee_type").toString())){
							if("RATIO".equals(merchantFee.get("fee_type").toString())){
								commissionType   = new StringBody("1");
							}else if("CAPPING".equals(merchantFee.get("fee_type").toString())){
								commissionType   = new StringBody("2");
							}
						}
						if(null== merchantFee.get("fee_rate")){
							json2.put("status", 2002);
							json2.put("message", "商户信息错误,费率为空！");
							outJson(json2.toString(), response2);
							return;
						}
						StringBody commission   = new StringBody(Integer.parseInt(new DecimalFormat("0").format(Double.parseDouble(merchantFee.get("fee_rate").toString())*10000))+"");
						
						StringBody yxbMerchantId   = new StringBody(map.get("merchant_no").toString());
						//对请求的表单域进行填充
						MultipartEntity reqEntity = new MultipartEntity();
						reqEntity.addPart("agentId", agentId);
						reqEntity.addPart("merchantType", merchantType);
						reqEntity.addPart("merchantName", merchantName);
						reqEntity.addPart("idcard", idcard);
						reqEntity.addPart("mobile", mobile);
						reqEntity.addPart("province", province);
						reqEntity.addPart("city", city);
						reqEntity.addPart("address", address);
						reqEntity.addPart("bankName", bankName);
						reqEntity.addPart("bankAccountType", bankAccountType);
						reqEntity.addPart("bankUnionpayCode", bankUnionpayCode);
						reqEntity.addPart("bankBranch", bankBranch);
						reqEntity.addPart("bankHolder", bankHolder);
						reqEntity.addPart("bankAccount", bankAccount);
						reqEntity.addPart("mccCode", mccCode);
						reqEntity.addPart("mccType", mccType);
						reqEntity.addPart("commissionType", commissionType);
						//当商户类型为批发类 且 扣率为封顶时则传入maxCommission值
						if("1520".equals(map.get("merchant_type").toString()) && merchantFee != null && null !=merchantFee.get("fee_type") && "CAPPING".equals(merchantFee.get("fee_type").toString())){
							reqEntity.addPart("maxCommission", new StringBody(merchantFee.get("fee_cap_amount").toString()));
						}
						reqEntity.addPart("commission", commission);
						reqEntity.addPart("license", license);
						reqEntity.addPart("yxbMerchantId", yxbMerchantId);
						//设置请求
						httppost.setEntity(reqEntity);
						//执行
						HttpResponse response = httpclient.execute(httppost);
						log.info("好乐付商户编号="+map.get("merchant_no").toString()+" HttpResponse  响应码= " + response.getStatusLine().getStatusCode());
						log.info("好乐付商户编号="+map.get("merchant_no").toString()+" HttpResponse  原因= " + response.getStatusLine().getReasonPhrase());
						//System.out.println(response.getStatusLine().getStatusCode());
						if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
							HttpEntity entity = response.getEntity();
							//显示内容
							if (entity != null) {
								//String a = EntityUtils.toString(entity);
								JSONObject json = JSONObject.fromObject(EntityUtils.toString(entity));
								if("100".equals(json.get("status").toString())){
									
									Map<String, Object> gmap = groupMerchantService.getGroupMerchantInfo(map.get("merchant_no").toString());
									int gmapSize = 0;
									if(gmap != null){
										gmapSize = gmap.size();
									}
									//System.out.println("gmap size = " + gmapSize);
									int addCount  = merchantService.updateMerchantSendHLFCode(map.get("merchant_no").toString(),response.getStatusLine().getStatusCode()+"",getUser().getRealName(),gmapSize);
									if(addCount > 0){
										outJson(json.toString(), response2);
									}else{
										json2.put("status", 2007);
										json2.put("message", "同步成功，修改否优质商户、否钱包结算、开启商户、从集群中删除操作失败，请手动修改!");
										outJson(json2.toString(), response2);
										return;
									}
								}else{
									outJson(json.toString(), response2);
									return;
								}
								//System.out.println(EntityUtils.toString(entity));
							}
							if (entity != null) {
								entity.consumeContent();
							}
						}else{
							json2.put("status", response.getStatusLine().getStatusCode());
							json2.put("message", "通讯错误");
							outJson(json2.toString(), response2);
							return;
						}
					}else{
						json2.put("status", 2003);
						json2.put("message", "同步失败，未查询到商户费率信息！");
						outJson(json2.toString(), response2);
						return;
					}
//				}else{
//					json2.put("status", 2001);
//					json2.put("message", "商户是否优质商户为是，不予同步!");
//					outJson(json2.toString(), response2);
//					return;
//				}
			}
		} catch (Exception e) {
			log.info("Exception MerchantController hlfSendMerchant ERROR = " + e.getMessage());
			e.printStackTrace();
			json2.put("status", 2000);
			json2.put("message", "系统错误,请重试！");
			outJson(json2.toString(), response2);
			return;
		}
		log.info("MerchantController hlfSendMerchant End");
	}
	
	
	@RequestMapping(value = "/transferAccountsExport")
	public void transferAccountsExport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request) {
		log.info("MerchantController transferAccountsExport strat ...");
		int row = 1; // 从第二行开始写
		int col = 0; // 从第一列开始写
		PageRequest page = new PageRequest(0, 79999);
		Page<Map<String, Object>>list=   merchantService.transferAccountsExport(params, page);
		OutputStream os = null;
		try {
			String fileName = "转账查询.xls"; 
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename="
					+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			
			Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/transferAccounts.xls"));
			WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
			WritableSheet ws = wwb.getSheet(0);
			Iterator<Map<String, Object>> it = list.iterator();
			while (it.hasNext()) {
				Map<String, Object> map = it.next();
				CardBin cardBinPayFor = new CardBin();
				CardBin cardBinPayee = new CardBin();
				String bankFor = "";
				String bankPayee = "";
				String payfor_account_no = "";
				String payee_account_no = "";
				if(map.get("payfor_account_no") != null && !"".equals(map.get("payfor_account_no").toString())){
					payfor_account_no = map.get("payfor_account_no").toString();
					cardBinPayFor= bankCardService.cardBin(map.get("payfor_account_no").toString());
					bankFor = cardBinPayFor.getBankName();
				}
				if(map.get("payee_account_no") != null && !"".equals(map.get("payee_account_no").toString())){
					payee_account_no = map.get("payee_account_no").toString();
					cardBinPayee= bankCardService.cardBin(map.get("payee_account_no").toString());
					bankPayee = cardBinPayee.getBankName();
				}
				ws.addCell(new Label(col++, row, map.get("order_no").toString())); //订单编号
				ws.addCell(new Label(col++, row, map.get("merchant_no").toString())); //商户编号
				ws.addCell(new Label(col++, row, map.get("amount").toString())); //转账金额
				ws.addCell(new Label(col++, row, map.get("agent_no").toString())); //代理商编号
				ws.addCell(new Label(col++, row, map.get("agent_name").toString())); //代理名称
				ws.addCell(new Label(col++, row, payfor_account_no)); //转账卡号
				
				ws.addCell(new Label(col++, row, bankFor)); //转账所属银行
				String id_card_no = "";
				if(map.get("id_card_no") != null && !"".equals(map.get("id_card_no").toString())){
					id_card_no = map.get("id_card_no").toString();
				}
				ws.addCell(new Label(col++, row, id_card_no)); //转账人身份证
				ws.addCell(new Label(col++, row, payee_account_no)); //收款银行账号
				
				ws.addCell(new Label(col++, row, bankPayee)); //收款所属银行
				ws.addCell(new Label(col++, row, map.get("fee").toString())); //手续费
				String payfor_account_name = "";
				if(map.get("payfor_account_name") != null && !"".equals(map.get("payfor_account_name").toString())){
					payfor_account_name = map.get("payfor_account_name").toString();
				}
				ws.addCell(new Label(col++, row, payfor_account_name)); //转账人姓名
				ws.addCell(new Label(col++, row, map.get("payee_account_name").toString())); //收款人姓名
				String order_status = "";
				if(map.get("order_status") != null && !"".equals(map.get("order_status").toString())){
					if("SUCCESS".equals(map.get("order_status").toString())){
						order_status = "成功";
					}else if("INIT".equals(map.get("order_status").toString())){
						order_status = "初始化";
					}else if("FAILED".equals(map.get("order_status").toString())){
						order_status = "失败";
					}else if("SENDORDER".equals(map.get("order_status").toString())){
						order_status = "已下单";
					}else {
						order_status = map.get("order_status").toString();
					}
				}
				ws.addCell(new Label(col++, row, order_status)); //转账状态
				ws.addCell(new Label(col++, row, map.get("create_time").toString())); //转账时间
				row++;
				col = 0;
			}
			wwb.write();
			wwb.close();
			wb.close();
			os.close();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("MerchantController transferAccountsExport End");
	}
	
	/**
	 * 转账详情
	 * @author 王帅
	 * @date 2015年1月22日14:05:46
	 * @see 根据转账ID查询转账详情信息
	 * @param model 返回转账详细信息
	 * @param params 转账ID
	 * @return 返回路径-转账详情界面
	 */
	@RequestMapping(value = "/transferAccountsDetail")
	public String transferAccountsDetail(final ModelMap model, @RequestParam String id){
		log.info("MerchantController transferAccountsDetail start ...");
		Map<String, Object>transferAccounts=   merchantService.transferAccountsDetail(id);
		CardBin cardBinFor = new CardBin();
		CardBin cardBinPayee = new CardBin();
		if(transferAccounts.get("payfor_account_no") != null && !"".equals(transferAccounts.get("payfor_account_no").toString())){
			cardBinFor= bankCardService.cardBin(transferAccounts.get("payfor_account_no").toString());
			transferAccounts.put("forBank", cardBinFor.getBankName());
		}
		
		if(transferAccounts.get("payee_account_no") != null && !"".equals(transferAccounts.get("payee_account_no").toString())){
			cardBinPayee= bankCardService.cardBin(transferAccounts.get("payee_account_no").toString());
			transferAccounts.put("payeeBank", cardBinPayee.getBankName());
		}
		
		model.put("params", transferAccounts);
		log.info("MerchantController transferAccountsDetail End");
		return "/merchant/transferAccountsDetail";
	}
	
	/**
	 * 转账查询
	 * @author 王帅
	 * @date 2015年1月21日15:31:46
	 * @see 转账查询
	 * @param model 返回数据集
	 * @param params 查询条件
	 * @param cpage 分页信息
	 * @return 返回路径
	 */
	@RequestMapping(value = "/transferAccountsQuery")
	public String transferAccountsQuery(final ModelMap model, @RequestParam Map<String, String> params,	@RequestParam(value = "p", defaultValue = "1") int cpage){
		log.info("MerchantController transferAccountsQuery start ...");
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		if(params.get("start_time") == null && params.get("end_time") == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String start_time = sdf.format(new Date())+" 00:00:00";
			String end_time = sdf.format(new Date())+" 23:59:59";
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		Page<Map<String, Object>>transferAccounts=   merchantService.transferAccountsQuery(params, page);
		model.put("p", cpage);
		model.put("list", transferAccounts);
		model.put("params", params);
		log.info("MerchantController transferAccountsQuery End");
		return "/merchant/transferAccounts";
	}
	
	// 交易查询
		@RequestMapping(value = "/trans")
		public String trans(final ModelMap model, 	@RequestParam Map<String, String> params,	@RequestParam(value = "p", defaultValue = "1") int cpage) {
			log.info("MerchantController trans start...");
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			Date currdate = new Date();
			List<Map<String, Object>> userList = userGroupService.getUserToGroupId(19,61);
			if (params.get("createTimeBegin") == null
					&& params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime +" 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}
			
			Page<Map<String, Object>> list = transService.getTrans(params, page);

			// Map<String, Object> totalMsg = transService.countTransInfo(params);
			model.put("p", cpage);
			model.put("currdate", DateUtils.format(currdate,"yyyy-MM-dd")+" 23:59:59");
			model.put("list", list);
			model.put("userList", userList);
			model.put("params", params);
			log.info("MerchantController trans End");
			return "/merchant/merchantTransQuery";
		}
	

	// 统计交易信息
	@RequestMapping("/countTransInfo")
	@ResponseBody
	public Map<String, Object> countTransInfo(
			@RequestParam Map<String, String> params) {
		return transService.countTransInfo(params);
	}

	// 统计快捷交易信息
	@RequestMapping("/countTransFast")
	@ResponseBody
	public Map<String, Object> countTransFast(
			@RequestParam Map<String, String> params) {
		return transService.countTransFast(params);
	}
	
	// 交易查询快捷
	@RequestMapping(value = "/transfast")
	public String transfast(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		 if (params.get("createTimeBegin") == null
		 && params.get("createTimeEnd") == null) {
			 Date date = new Date();
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			 String createTime = sdf.format(date);
			
			 String createTimeBegin = createTime + " 00:00:00";
			 String createTimeEnd = createTime +" 23:59:59";
			 params.put("createTimeBegin", createTimeBegin);
			 params.put("createTimeEnd", createTimeEnd);
		 }
		Page<Map<String, Object>> list = transService.getTransFastPat(params,
				page);
		model.put("p", cpage);
		model.put("list", list);
		Date currdate = new Date();
		model.put("currdate", DateUtils.format(currdate,"yyyy-MM-dd"));
		model.put("params", params);
		return "/merchant/merchantTransFastQuery";
	}

		// 快捷交易详情
		@RequestMapping(value = "/fastDetail")
		public String fastDetail(final ModelMap model, @RequestParam Long id) {

			Map<String, Object> params = new HashMap<String, Object>();

			Map<String, Object> transInfoMap = transService.queryFastTransInfoById(id);

	
			if (TransType.PURCHASE.toString().equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "消费");
			} else if ("BAG".equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "钱包充值");
			} else if ("MOBILE".equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "手机充值");
			} 

			CardBin cardBin = bankCardService.cardBin((String) transInfoMap
					.get("card_no"));

			params.put("bank_name", cardBin.getBankName());
			params.put("card_type", cardBin.getCardType());
			params.put("card_name", cardBin.getCardName());

			params.putAll(transInfoMap);
			model.put("params", params);
			
			List<Map<String, Object>> freezeLogs = transService.getTransFreezeLogs(id, "1");
			model.put("freezeLogs", freezeLogs);
			
			return "/merchant/fastTransDetail";
		}
	
	// 交易查询(移小宝)
	@RequestMapping(value = "/smBoxTrans")
	public String smBoxTrans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		String ids = (String) params.get("ids");

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime +" 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}
		Date currdate = new Date();
		params.put("transSource", "SMALLBOX_MOBOLE_PHONE");
		Page<Map<String, Object>> list = transService.getTrans(params, page);

		// Map<String, Object> totalMsg = transService.countTransInfo(params);
		model.put("p", cpage);
		model.put("list", list);
		model.put("currdate", DateUtils.format(currdate,"yyyy-MM-dd")+" 23:59:59");
		
		// model.put("totalMsg", totalMsg);
		model.put("params", params);
		return "/merchant/smboxTransQuery";
	}

	@RequestMapping(value = "/refundUpdate")
	public void refundUpdate(final ModelMap model,
			@RequestParam Map<String, String> params,
			HttpServletResponse response) {
		String id = (String) params.get("id");
		try {
			transService.updateStatus(Long.parseLong(id));
		} catch (NumberFormatException e) {
			outText("2", response);
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			outText("2", response);
			e.printStackTrace();
			return;
		}
		outText("1", response);
	}

	// 交易查询，导出excel,不是csv。zxm.
	@RequestMapping(value = "/export")
	public void export(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName = "交易查询" + sdf.format(new Date()) + "_" + random
				+ ".xls";

		// PageRequest page = new PageRequest(0, 10000);

		// Page<Map<String, Object>> list = transService.getTrans(params, page);
		// Map<String,Object> totalMsg = transService.countTransInfo(params);

		OutputStream os = null;
		try {
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename="
					+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			expordExcel(os, params, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 交易查询，导出excel,不是csv。zxm.
		@RequestMapping(value = "/export2")
		public void export2(@RequestParam Map<String, String> params,
				HttpServletResponse response, HttpServletRequest request) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);
			String fileName = "交易查询" + sdf.format(new Date()) + "_" + random
					+ ".xls";
			params.put("transSource", "SMALLBOX_MOBOLE_PHONE");

			// PageRequest page = new PageRequest(0, 10000);

			// Page<Map<String, Object>> list = transService.getTrans(params, page);
			// Map<String,Object> totalMsg = transService.countTransInfo(params);

			OutputStream os = null;
			try {
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				expordExcel(os, params, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	/**
	 * 导出Excel报表
	 * 
	 */
	private void expordExcel(OutputStream os, Map<String, String> params,
			String fileName) throws Exception {

		int row = 2; // 从第三行开始写
		int col = 0; // 从第一列开始写

		PageRequest page = new PageRequest(0, 65000);

		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime +" 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

		//Page<Map<String, Object>> list = transService.getTrans(params,page);

		Page<Map<String, Object>> list = transService.getTransForExport(params, page);

		//Map<String, Object> totalMsg = transService.countTransInfo(params);

		Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/MerTrans.xls"));

		WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
		WritableSheet ws = wwb.getSheet(0);
 
		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("agent_name"), "无")));
			ws.addCell(new Label(col++, row, String.valueOf(map.get("merchant_short_name"))));

			// <option value="PURCHASE" <c:out value="${params['transType'] eq
			// 'PURCHASE'?'selected':'' }"/>>消费</option>
			// <option value="PURCHASE_VOID" <c:out value="${params['transType']
			// eq
			// 'PURCHASE_VOID'?'selected':'' }"/>>消费撤销</option>
			// <option value="PURCHASE_REFUND" <c:out
			// value="${params['transType'] eq
			// 'PURCHASE_REFUND'?'selected':'' }"/>>退货</option>
			// <option value="REVERSED" <c:out value="${params['transType'] eq
			// 'REVERSED'?'selected':'' }"/>>冲正</option>
			// <option value="BALANCE_QUERY" <c:out value="${params['transType']
			// eq
			// 'BALANCE_QUERY'?'selected':'' }"/>>余额查询</option>
			//

			String trans_type = StringUtil.ifEmptyThen(map.get("trans_type"),
					"无");
			if ("PURCHASE".equals(trans_type)) {
				trans_type = "消费";
			} else if ("PURCHASE_VOID".equals(trans_type)) {
				trans_type = "消费撤销";
			} else if ("PURCHASE_REFUND".equals(trans_type)) {
				trans_type = "退货";
			} else if ("REVERSED".equals(trans_type)) {
				trans_type = "冲正";
			} else if ("BALANCE_QUERY".equals(trans_type)) {
				trans_type = "余额查询";
			} else {
				trans_type = "其他:" + trans_type;
				;
			}
			ws.addCell(new Label(col++, row, trans_type));
			String account_no = String.valueOf(map.get("account_no"));
			if (StringUtils.isNotEmpty(account_no)) {
				account_no = account_no.substring(0, 6)
						+ "*****"
						+ account_no.substring(account_no.length() - 4,
								account_no.length());
			} else {
				account_no = "";
			}
			ws.addCell(new Label(col++, row, account_no)); // 卡号

			String card_type = StringUtil
					.ifEmptyThen(map.get("card_type"), "无");
			if ("DEBIT_CARD".equals(card_type)) {
				card_type = "借记卡";
			} else if ("CREDIT_CARD".equals(card_type)) {
				card_type = "贷记卡";
			} else if ("PREPAID_CARD".equals(card_type)) {
				card_type = "预付卡";
			} else if ("SEMI_CREDIT_CARD".equals(card_type)) {
				card_type = "准贷记卡";
			} else if ("BUSINESS_CARD".equals(card_type)) {
				card_type = "公务卡";
			} else {
				card_type = "未知卡";
			}
			ws.addCell(new Label(col++, row, card_type));

			String trans_status = StringUtil.ifEmptyThen(
					map.get("trans_status"), "无");
			if ("INIT".equals(trans_status)) {
				trans_status = "初始化";
			} else if ("SUCCESS".equals(trans_status)) {
				trans_status = "已成功";
			} else if ("FAILED".equals(trans_status)) {
				trans_status = "已失败";
			} else if ("REVOKED".equals(trans_status)) {
				trans_status = "已撤销";
			} else if ("REFUND".equals(trans_status)) {
				trans_status = "已退货";
			} else if ("REVERSED".equals(trans_status)) {
				trans_status = "已冲正";
			} else if ("SETTLE".equals(trans_status)) {
				trans_status = "已结算";
			} else {
				trans_status = "其它:" + trans_status;
			}
			ws.addCell(new Label(col++, row, trans_status));

			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("mermcc"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("create_time"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("terminal_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_merchant_no"), "无")));
			
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("response_code"), "无")));//响应码
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("bank_name"), "无")));//发卡行
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("card_name"), "无")));//卡种
			
			String trans_source = StringUtil.ifEmptyThen(map.get("trans_source"), "无");//交易来源
			if(!StringUtil.isEmpty(trans_source)){
				trans_source = posModelName(trans_source);
			}
			ws.addCell(new Label(col++, row, trans_source));//卡种
			
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_cnname"), "无")));//机构名称
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_merchant_name"), "无")));//商户名称
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_reference_no"), "无")));//参考号
			
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_terminal_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("trans_time"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_fee"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("trans_amount"), "无")));
			row++;
			col = 0;
		}

		// settleBatchService.updateSettleFileName(settleBatchNo, fileName);
		wwb.write();
		wwb.close();
		wb.close();
		os.close();
	}

	// 退款
	@RequestMapping(value = "/refund")
	public String refund(final ModelMap model,
			@RequestParam Map<String, String> params) {
		model.put("params", params);
		return "/merchant/refundMerchant";
	}

	// 交易详情
	@RequestMapping(value = "/detail")
	public String transDetail(final ModelMap model, @RequestParam Long id) {

		Map<String, Object> params = new HashMap<String, Object>();

		Map<String, Object> transInfoMap = transService.queryTransInfoById(id);

		if (TransStatus.SUCCESS.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已成功");
		} else if (TransStatus.FAILED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已失败");
		} else if (TransStatus.INIT.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "初始化");
		} else if (TransStatus.REVOKED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已撤销");
		} else if (TransStatus.REFUND.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已退货");
		} else if (TransStatus.REVERSED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已冲正");
		}else if (TransStatus.FREEZED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已冻结");
		}
		

		if (TransType.PURCHASE.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "消费");
		} else if (TransType.PURCHASE_VOID.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "消费撤销");
		} else if (TransType.PURCHASE_REFUND.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "退货");
		} else if (TransType.REVERSED.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "冲正");
		} else if (TransType.BALANCE_QUERY.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "余额查询");
		}

		if (CurrencyType.CNY.toString().equals(
				(String) transInfoMap.get("currency_type"))) {
			transInfoMap.put("currency_type", "人民币");
		}

		Map<String, Object> merchantInfoMap = merchantService
				.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));

		Map<String, Object> acqMerchantInfoMap = acqMerchantService
				.queryAcqMerchantInfo((String) transInfoMap
						.get("acq_merchant_no"));

		CardBin cardBin = bankCardService.cardBin((String) transInfoMap
				.get("account_no"));

		String accountNo = (String) transInfoMap.get("account_no");
		/*String card_no = accountNo.substring(0, 6)
				+ "*****"
				+ accountNo.substring(accountNo.length() - 4,
						accountNo.length());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.#");*/
		Date create_time = (Date) (transInfoMap.get("create_time"));

		// String transId =
		// sdf.format(create_time)+StringUtil.stringFillLeftZero(String.valueOf(id),
		// 6);
		String transId = OrderUtil.buildOrderId(id, create_time);
		params.put("trans_id", transId);
		params.put("card_no", accountNo);
		params.put("bank_name", cardBin.getBankName());
		params.put("card_type", cardBin.getCardType());
		params.put("card_name", cardBin.getCardName());

		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		if (acqMerchantInfoMap != null) {
			params.putAll(acqMerchantInfoMap);
		}

		model.put("params", params);
		
		List<Map<String, Object>> freezeLogs = transService.getTransFreezeLogs(id, "0");
		model.put("freezeLogs", freezeLogs);
		
		return "/merchant/merchantTransDetail";
	}

	/**
	 * 根据商户编号，自动绑定机具、自动将商户状态变更为“正常”(录入机具数量超过20个不处理)
	 * 
	 * @param merchant_no
	 *            商户编号
	 * @return true：操作成功
	 */
	public boolean openMerchantAndTerminal(String merchant_no)
			throws SQLException {
		boolean flag = false;
		if (!"".equals(merchant_no)) {
			Map<String, Object> merchantInfo = merchantService
					.getMerchantInfoByMerchantNo(merchant_no); // 获取商户信息
			if (null != merchantInfo) { // 如商户信息存在，则验证该商户上传的设备类型、
				if (!"".equals(merchantInfo.get("terminal_no").toString())
						&& !"".equals(merchantInfo.get("agent_no").toString())) {
					String[] merchantTerminalInfo = merchantInfo
							.get("terminal_no").toString().split(";");
					if (merchantTerminalInfo.length < 21) { // 机具超过20个不处理
						StringBuffer querryConditions = new StringBuffer();
						for (int i = 0; i < merchantTerminalInfo.length; i++) {
							if (i == (merchantTerminalInfo.length - 1)) {
								querryConditions.append("'");
								querryConditions
										.append(merchantTerminalInfo[i]);
								querryConditions.append("'");
							} else {
								querryConditions.append("'");
								querryConditions
										.append(merchantTerminalInfo[i]);
								querryConditions.append("'");
								querryConditions.append(",");
							}
						}
						// 根据商户所属代理商编号和录入的SN 编号匹配所录入的机具有效性
						List<Map<String, Object>> terminalList = terminalService
								.checkTerminalByTerNo(
										querryConditions.toString(),
										merchantInfo.get("agent_no").toString());
						// 验证数量是否匹配
						if (terminalList.size() == merchantTerminalInfo.length) {
							// 根据商户编号绑定机具
							for (int j = 0; j < terminalList.size(); j++) {
								terminalService.setMerchantNo(
										merchant_no,
										Integer.parseInt(terminalList.get(j)
												.get("id").toString()),
										Integer.parseInt(merchantInfo.get(
												"agent_no").toString()));
							}
							// 组装商户开启条件 并 开启商户
							Map<String, String> params = new HashMap<String, String>();
							params.put("merchant_no", merchant_no);
							params.put("status", "1");
							// 开启商户状态
							int modifyMerchantCount = merchantService
									.merchantSetNormal(params);
							if (modifyMerchantCount > 0) {
								flag = true;
							}
						}
					}
				}
			}
		}
		return flag;
	}
	
	
	
	/**
	 * 龙宝注册通知
	 * @author swang
	 * @param params
	 * @see 商户所属代理商为龙宝(东莞吉庆鸿投资有限公司)，审核成功后，同步到龙宝
	 */
	public void longbaoInform(Map<String, String> params) {
		log.info("MerchantController longbaoInform start ...");
		Map<String, Object> map = new HashMap<String, Object>();
		String url = SysConfig.value("lb_registerInform_url");
		if(null != params && null != url && !"".equals(url)){
			try {
				String merchant_id =  params.get("id").toString();
				map.put("name", params.get("account_name").toString()); //params.get("link_name").toString());  //注册人的姓名
				map.put("phone",params.get("mobile_username").toString()); //params.get("phone").toString()); //注册人的手机号码
				map.put("email", params.get("email").toString()); //注册人的邮箱
				map.put("idcard", params.get("id_card_no").toString()); //注册人的身份证号码
				map.put("parent_phone", params.get("parent_phone").toString()); //推荐人的手机号码，（没有不填写或者写数字0）
				map.put("card_number", params.get("cnaps_no").toString());  //注册人的银行卡号
				map.put("bank_name", params.get("bank_name").toString());  //开户行名称，银行的中文名
				map.put("deposit", params.get("bank_name").toString()); //开户行地址(省市区+详细地址)
				//MD5后字符串   hmac=name+phone+email+idcard+card_number
				StringBuffer hmac = new StringBuffer(map.get("name").toString()); 
				hmac.append(map.get("phone").toString());
				hmac.append(map.get("email").toString());
				hmac.append(map.get("idcard").toString());
				hmac.append(map.get("card_number").toString());
				hmac.append("18a5eecf5d3a463f0ed0904cfb1912b9");
				System.out.println(Md5.md5Str(hmac.toString()));
				map.put("hmac", Md5.md5Str(hmac.toString()).toLowerCase());
				String msg = Http.send(url, map, "UTF-8");
				log.info("MerchantController longbaoInform Http Request SUCCESS  = " + msg);
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
				int count = merchantService.merchantNotifyAdd(map,"1");
				if(count > 0){
					log.info("MerchantController longbaoInform SUCCESS");
				}else{
					log.info("MerchantController longbaoInform FAIL");
				}
			} catch (Exception e) {
				log.error("MerchantController longbaoInform Exception = " + e);
				e.printStackTrace();
			}
			log.info("MerchantController longbaoInform end");
		}
	}

	// 商户审核提交
	@RequestMapping(value = "/checkDetailSubmit")
	public String checkDetailSubmit(final ModelMap model,	@RequestParam Map<String, String> params)  {
		log.info(" 1 MerchantController checkDetailSubmit start ...");
		try {
			boolean isSuccess = false;
			// 获取支付方式
			String pay_method = params.get("final_pay_method");
			pay_method = (null == pay_method || "".equals(pay_method)) ? "00"	: pay_method;
			params.put("pay_method", pay_method);
			Map<String, Object> merchant = merchantService.queryMerchantById(Long.valueOf(params.get("id")));
			log.info(" 2 MerchantController queryMerchantById start ...");
			String examinationMark = params.get("examinationMark");
			if ("success".equals(examinationMark)) {
				params.put("open_status", "5");
				merchantService.updatePosMerchant((String) merchant.get("merchant_no"), params);
				log.info(" 3 MerchantController updatePosMerchant start ...");
				merchantService.updatePosMerchantFee(	(String) merchant.get("merchant_no"), params);
				log.info(" 4 MerchantController updatePosMerchantFee start ...");
				merchantService.updatePosMerchantTransRule((String) merchant.get("merchant_no"), params);
				log.info(" 5 MerchantController updatePosMerchantTransRule start ...");
				List<Map<String, Object>> merchantUser = merchantService	.getMerchantUser(String.valueOf(merchant.get("merchant_no")));
				log.info(" 6 MerchantController getMerchantUser start ...");
				if (merchantUser.size() == 0) {
					//添加一个操作员。
					merchantService.merchantUserAdd((String) merchant.get("merchant_no"), getUser().getUserName(), params);
					log.info(" 7 MerchantController merchantUserAdd start ...");
				} 
				//得到所属代理商编号，验证是否为吉庆鸿，如果是，则同步吉庆鸿注册商户信息
				/*String agent_no_v = params.get("agent_no");
				if(null != agent_no_v && !"".equals(agent_no_v) && "6397".equals(agent_no_v)){
					longbaoInform(params);
				}*/
				isSuccess = true;
			} else {
				params.put("open_status", "3");
				merchantService.updatePosMerchantOpenStatus((String) merchant.get("merchant_no"), params);
				// 发送短信通知商户
				String projectRunFlag = SysConfig.value("projectRunFlag");
				String phoneNumber = merchant.get("mobile_username").toString();
				String examination_opinions = params.get("examination_opinions");
				//String content = "您提交的商户信息审核失败。审核意见:" + examination_opinions;
				//String content_xzf = "感谢您注册新支付，您提交的商户(" + merchant.get("merchant_name") + ")信息审核失败，失败原因为：" + examination_opinions;
				String merchantName = merchant.get("merchant_name").toString();
				String agentNo = merchant.get("agent_no").toString();
				String posType = merchant.get("pos_type").toString();
				if (!"test".equals(projectRunFlag)) {
					/*// 如果是移小宝发送审核失败原因
					String pos_type = params.get("pos_type");
					// 支付界不发送短信
					if (("3".equals(pos_type) || "4".equals(pos_type))	&& !"3124".equals(agentNo)) {
						String agentid = merchant.get("agentid").toString();
						String special_agent_zk = SysConfig.value("special_agent_zk");
						String special_agent_xzf = SysConfig.value("special_agent_xzf");
						String special_agent_cf = SysConfig.value("special_agent_cf");
						String special_agent_zl = SysConfig.value("special_agent_zl");
						String special_agent_qyb = SysConfig.value("special_agent_qyb");
						String special_agent_lb = SysConfig.value("special_agent_lb");
						String special_agent_wsy = SysConfig.value("special_agent_wsy");
						String special_agent_mjd = SysConfig.value("special_agent_mjd");
						String special_agent_xhtf = SysConfig.value("special_agent_xhtf");

						if (special_agent_zk.equals(agentid)) {
							Sms.sendMsgOem(phoneNumber, content, "[中宽支付]");
						} else if (special_agent_xzf.equals(agentid)) {
							Sms.sendMsgOem(phoneNumber, content_xzf, "[新支付]");
						} else if (special_agent_cf.equals(agentid)) {
							Sms.sendMsgOem(phoneNumber, content, "[诚付天下]");
						} else if (special_agent_zl.equals(agentid) 	|| special_agent_qyb.equals(agentid)	|| special_agent_lb.equals(agentid)
								|| special_agent_wsy.equals(agentid)
								|| special_agent_mjd.equals(agentid)
								|| special_agent_xhtf.equals(agentid)) {
							Sms.sendMsgOem(phoneNumber, content, "[支付随心]");
						} else {
							Sms.sendMsg(phoneNumber, content);
						}
					}

					// 新支付的超级刷发失败短信
					if("5".equals(pos_type) && SysConfig.value("special_agent_xzf").equals(merchant.get("agentid").toString())){
						Sms.sendMsgOem(phoneNumber, content_xzf, "[新支付]");
					}*/

					SmsUtil.sendSms(new SmsBean(phoneNumber, agentNo, posType, "0", new String[]{merchantName, examination_opinions}));
				}
				/*
				 * String add_type=params.get("add_type"); if("1".equals(add_type)){
				 * //删除审核失败的客户端进件类型商户资料 String id=merchant.get("id").toString();
				 * String merchant_no=merchant.get("merchant_no").toString();
				 * merchantService.merchantDel(id, merchant_no); }
				 */
				String pos_type = params.get("pos_type"); // 只有移联商通才有激活码
				if ("4".equals(pos_type)) {
					// 审核失败修改状态为已激活
					String keycode = params.get("keycode");
					merchantService.updatefaidCodeState(keycode);
				}
				isSuccess = false;
			}
			if (isSuccess) {
				// 如果注册成功，则将注册成功的商户添加到登陆用户中
				String mobileNo = (String) merchant.get("mobile_username");
				String realName = (String) merchant.get("account_name");
				String idcard = (String) merchant.get("id_card_no");
				String password = (String) merchant.get("mobile_password");
				String account_no = (String) merchant.get("account_no");
				String account_name = (String) merchant.get("account_name");
				String bank_name = (String) merchant.get("bank_name");
				String pos_type = params.get("pos_type"); // 只有移联商通才有激活码
				String merchant_no = (String) merchant.get("merchant_no");
				String parent_node = (String) merchant.get("parent_node");

				String sale_name = (String) merchant.get("sale_name");
				
				Map<String, String> tem = new HashMap<String, String>();
				tem.put("mobileNo", mobileNo);
				tem.put("password", password);
				tem.put("realName", realName);
				tem.put("idcard", idcard);
				tem.put("account_no", account_no);
				tem.put("account_name", account_name);
				tem.put("bank_name", bank_name);
				tem.put("pos_type", pos_type);
				tem.put("merchantNo", merchant_no);
				tem.put("parentNode", parent_node);

				tem.put("sale_name", sale_name);
				
				
				RunnableController rc = new RunnableController(merchant);
				new Thread(rc).start();
				log.info(" 8.0  MerchantController RunnableController start ...");
				
				if ("4".equals(pos_type)) {
					// 审核成功，更新激活状态为已使用，将商户编号更新到使用者
					String keycode = params.get("keycode");
					merchantService.updateSucessCodeState(keycode);
					log.info(" 8 MerchantController updateSucessCodeState start ...");
				}
				  /*龙宝接口同步*/
				if(merchant != null  && !"".equals(merchant.get("agent_no").toString()) && 
						("6397".equals(merchant.get("agent_no").toString()) || "9982".equals(merchant.get("agent_no").toString()))){
					if(!"".equals(merchant.get("belong_to_agent").toString())){
						Map<String, Object> belongMap = agentService.getAgentPhoneByAgentNo(merchant.get("belong_to_agent").toString());
						String agentiPhone = "0";
						if(belongMap != null){
							agentiPhone = belongMap.get("agent_link_tel").toString();
						}
						params.put("parent_phone", agentiPhone);
						longbaoInform(params);
					}
				}
				
			}
			merchantService.insertExaminationsLog((String) merchant.get("merchant_no"), getUser().getUserName(),params);
			log.info(" 9 MerchantController insertExaminationsLog start ...");
		} catch (Exception e) {
			log.info("Exception 10 MerchantController checkDetailSubmit =  " + e);
			e.printStackTrace();
			return "redirect:" + "/mer/checkQuery";
		}
		
		return "redirect:" + "/mer/checkQuery";
	}

	// 商户审核
	@RequestMapping(value = "/checkDetail")
	public String checkDetail(final ModelMap model, @RequestParam Long id)
			throws Exception {
		Map<String, Object> map = merchantService.queryMerchantInfoById(id);
		// 获得一级代理商编号进行显示
		String find_agent_no = (String) map.get("agent_no");
		Map<String, Object> amp = merchantService
				.findSaleNameByAgentNo(find_agent_no);
		if (amp != null && !"".equals(amp)) {
			map.put("self_sale_name", amp.get("self_sale_name"));
		}

		Object fee_rate = map.get("fee_rate");
		if (fee_rate != null && fee_rate.toString().length() > 0) {
			String feeRate = ((BigDecimal) map.get("fee_rate")).multiply(
					new BigDecimal("100")).setScale(4, RoundingMode.HALF_UP)
					+ "";
			map.put("fee_rate", feeRate);
		}

		Object ladder_fee = map.get("ladder_fee");
		if (ladder_fee != null && ladder_fee.toString().length() > 0) {
			if (ladder_fee.toString().indexOf("<") > 0) {
				String[] ladder = ladder_fee.toString().split("<");
				String ladder_min = ladder[0];
				String ladder_value = ladder[1];
				String ladder_max = ladder[2];

				map.put("ladder_min",
						new BigDecimal(ladder_min).movePointRight(2));
				map.put("ladder_value", ladder_value);
				map.put("ladder_max",
						new BigDecimal(ladder_max).movePointRight(2));
			}
		}
		String addType = map.get("add_type").toString();
		if ("1".equals(addType)) {
			String attachments = map.get("attachment").toString();
			String[] attachment = attachments.split(",");
			List<String> picList = new ArrayList<String>();
			for (int i = 0; i < attachment.length; i++) {
				String pic = attachment[i];
				picList.add(pic);
			}
			model.put("picList", picList);
		}
		// 获取集群信息
		List<Map<String, Object>> glist = groupMerchantService
				.getGroupInfoList();
		model.put("glist", glist);
		// 通过商户编号和激活码中的使用者相关联查询激活码
		String merchant_no = map.get("merchant_no").toString();
		Map<String, Object> list = merchantService
				.codeVerification(merchant_no);
		if (list != null) {
			map.put("keycode", list.get("keycode"));
		} else {
			map.put("keycode", "");
		}
		
		// 由于工行、交行和广州银行在结算中心暂时无法验证，如果是这三家银行卡，默认鹏元征信通道验证，结算中心验证不可用。
		CardBin cardBin = bankCardService.cardBin(map.get("account_no").toString());
		if(cardBin != null){
			String bankName = cardBin.getBankName();
			String icbcBankName = "工商银行";
			String bocomBankName = "交通银行";
			String gzBankName = "广州银行";
			if(bankName != null && (bankName.contains(icbcBankName) || bankName.contains(bocomBankName) || bankName.contains(gzBankName))){
				map.put("disSettleIdentity", true);
			}
		}
		
		model.put("params", map);
		return "/merchant/merchantCheckDetail";
	}

	// 商户审核查询
	@RequestMapping(value = "/checkQuery")
	public String checkQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = merchantService.queryCheckMerchant(
				params, page);
		List<Map<String, Object>> checkPerson = merchantService.getCheckerListForTag2();
		model.put("params", params);
		model.put("p", cpage);
		model.put("list", list);
		model.put("checkPerson", checkPerson);
		return "/merchant/merchantCheck";
	}
	
	
	/**
	 * 交易查询(POS)小票2
	 * @param id    交易记录id
	 * @param model
	 * @return
	 */
	@RequestMapping("/receiptReviewLoad2/{id}")
	// public String receiptReview(@@PathVariable("id") Long id, Model model) {
	public String receiptReview2(@PathVariable("id") Long id, ModelMap model) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> transInfoMap = transService.queryTransInfoById(id);
		Map<String, Object> merchantInfoMap = merchantService.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));
		Map<String, Object> acqMerchantInfoMap = acqMerchantService.queryAcqMerchantInfo((String) transInfoMap.get("acq_merchant_no"));
		CardBin cardBin = bankCardService.cardBin((String) transInfoMap.get("account_no"));
		String account_no = transInfoMap.get("account_no").toString();
		if(transInfoMap.get("is_iccard") != null && !"".equals(transInfoMap.get("is_iccard").toString())){
			if("0".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/S";
			}else if("1".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/I";
			}else{
				account_no += "/"+transInfoMap.get("is_iccard").toString();
			}
		}else{
			account_no += "/ ";
		}
		transInfoMap.put("account_no", account_no);
		Date create_time = (Date) (transInfoMap.get("create_time"));
		String transId = OrderUtil.buildOrderId(id, create_time);
		model.addAttribute("transId", transId);
		model.addAttribute("id", id);
		model.addAttribute("sysconfigPath", SysConfig.value("signPath"));
		model.addAttribute("reviewStatus", transInfoMap.get("review_status"));
		String signPath = SysConfig.value("signPath");
		/* 电子小票签名图片地址确定 */
		String signUrl = genImgUrl(transId, signPath);
		model.put("signUrl", signUrl);
		params.put("bank_nameT",cardBin.getBankName());
		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		params.putAll(acqMerchantInfoMap);
		model.put("params", params);
		return "/merchant/merchantSignReview2";
	}
	
	/**
	 * 交易查询(POS)小票2
	 * @param id    交易记录id
	 * @param model
	 * @return
	 */
	@RequestMapping("/addReceiptReviewLoad2/{id}")
	public void addReceiptReview2(@PathVariable("id") Long id, ModelMap model,HttpServletResponse response) {
		log.info("MerchantController addReceiptReview2 start ....");
		JSONObject json = new JSONObject();
		int addCount = 0;
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> transInfoMap = transService.queryTransInfoById(id);
		Map<String, Object> merchantInfoMap = merchantService.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));
		Map<String, Object> acqMerchantInfoMap = acqMerchantService.queryAcqMerchantInfo((String) transInfoMap.get("acq_merchant_no"));
		CardBin cardBin = bankCardService.cardBin((String) transInfoMap.get("account_no"));
		String account_no = transInfoMap.get("account_no").toString();
		if(transInfoMap.get("is_iccard") != null && !"".equals(transInfoMap.get("is_iccard").toString())){
			if("0".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/S";
			}else if("1".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/I";
			}else{
				account_no += "/"+transInfoMap.get("is_iccard").toString();
			}
		}else{
			account_no += "/ ";
		}
		transInfoMap.put("account_no", account_no);
		Date create_time = (Date) (transInfoMap.get("create_time"));
		String transId = OrderUtil.buildOrderId(id, create_time);
		params.put("trans_id", transId);
		model.addAttribute("id", id);
		model.addAttribute("sysconfigPath", SysConfig.value("signPath"));
		model.addAttribute("reviewStatus", transInfoMap.get("review_status"));
		params.put("create_person", getUser().getRealName());
		params.put("bank_nameT",cardBin.getBankName());
		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		params.putAll(acqMerchantInfoMap);
		addCount = transService.addTransReceipt(params);
		if(addCount > 0){
			log.info("MerchantController addReceiptReview2 SUCCESS");
		}else{
			log.info("MerchantController addReceiptReview2 FAIL");
		}
		log.info("MerchantController addReceiptReview2 End");
		json.put("result", addCount);
		outJson(json.toString(), response);
		//return "{\"result\":" + addCount + "}";
	}
	
	
	/**
	 * 商户审核统计详情查询
	 * @author swang
	 * @param model
	 * @param params
	 * @param cpage
	 * @return 
	 */
	@RequestMapping(value = "/merAuditingQueryDetail")
	public String merAuditingQueryDetail(final ModelMap model, @RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		
		if(null != params){
			if(StringUtil.isEmpty(params.get("detail_operator"))){
				return "/merchant/merAuditingQueryDetail";
			}
			
			if (StringUtil.isEmpty(params.get("detail_Start_time")) && StringUtil.isEmpty(params.get("detail_End_time"))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);

//				String createTimeBegin = createTime + " 00:00";
//				String createTimeEnd = createTime + " 23:59";
//				params.put("startQDTime", createTimeBegin);
//				params.put("endQDTime", createTimeEnd);
				params.put("startQDTime", createTime);
				params.put("endQDTime", createTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detail_Start_time"))){
				String startTime = params.get("detail_Start_time").toString();
				startTime += " 00:00:00";
				params.put("startQDTime", startTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detail_End_time"))){
				String end_time = params.get("detail_End_time").toString();
				end_time += " 23:59:59";
				params.put("endQDTime", end_time);
			}
		}
		
		Page<Map<String, Object>> list = merchantService.auditingQueryDetailPage(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/merchant/merAuditingQueryDetail";
	}

	/**
	 * 商户审核统计查询
	 * @param model
	 * @param params
	 * @param cpage
	 * @return
	 */
	@RequestMapping(value = "/merAuditingQuery")
	public String merAuditingQuery(final ModelMap model, @RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		
		if (StringUtil.isEmpty(params.get("start_time")) && StringUtil.isEmpty(params.get("end_time"))) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			params.put("start_time", createTime + " 00:00:00");
			params.put("end_time", createTime + " 23:59:59");
		}
		
		/*if(!StringUtil.isEmpty(params.get("start_time"))){
			String startTime = params.get("start_time").toString();
			startTime += " 00:00:00";
			params.put("start_time", startTime);
		}

		if(!StringUtil.isEmpty(params.get("end_time"))){
			String end_time = params.get("end_time").toString();
			end_time += " 23:59:59";
			params.put("end_time", end_time);
		}*/
		
//		merchantService.auditingQueryPage(params, page);
		Page<Map<String, Object>> list = merchantService.auditingQueryPage(params, page);
		List<Map<String, Object>> remain = merchantService.auditingRemainQuery();
		
		//手动插入审核剩余数
		for(Map<String, Object> map : list.getContent()){
			for(Map<String, Object> mapRemain : remain){
				String pos_type = map.get("pos_type") != null ? map.get("pos_type").toString() : "";
				String real_name = map.get("real_name") != null ? map.get("real_name").toString() : "";
				String pos_type1 = mapRemain.get("pos_type") != null ? mapRemain.get("pos_type").toString() : "";
				String checker = mapRemain.get("checker") != null ? mapRemain.get("checker").toString() : "";
				
				if(pos_type.equals(pos_type1) && real_name.equals(checker)){
					map.put("remain", mapRemain.get("remain"));
					continue;
				}
			}
		}
		
		List<Map<String, Object>> checker = merchantService.getCheckerListForTag2();
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		model.put("checker", checker);
		return "/merchant/merAuditingQuery";
	}
	
	/**
	 * 商户审核统计导出
	 * @param model
	 * @param params
	 * @param cpage
	 * @return
	 */
	@RequestMapping(value = "/merAuditingExport")
	public void merAuditingExport(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage, HttpServletResponse response, HttpServletRequest request) { 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String randomStr = StringUtil.stringFillLeftZero(""+random, 4);
		String fileName = "商户审核导出" + sdf.format(new Date()) + "_" + randomStr+ ".xls";
		
		OutputStream os = null;
		try {
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			merAuditingExportExcel(os, params, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void merAuditingExportExcel(OutputStream os, Map<String, String> params, String fileName) throws Exception {
		
		int row = 1; // 从第三行开始写
		int col = 0; // 从第一列开始写

		PageRequest page = new PageRequest(0, 29999);
		
		if(null != params){
			
			if (StringUtil.isEmpty(params.get("detail_Start_time")) && StringUtil.isEmpty(params.get("detail_End_time"))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);

//				String createTimeBegin = createTime + " 00:00";
//				String createTimeEnd = createTime + " 23:59";
//				params.put("startQDTime", createTimeBegin);
//				params.put("endQDTime", createTimeEnd);
				params.put("startQDTime", createTime);
				params.put("endQDTime", createTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detail_Start_time"))){
				String startTime = params.get("detail_Start_time").toString();
				startTime += " 00:00:00";
				params.put("startQDTime", startTime);
			}
			
			if(!StringUtil.isEmpty(params.get("detail_End_time"))){
				String end_time = params.get("detail_End_time").toString();
				end_time += " 23:59:59";
				params.put("endQDTime", end_time);
			}
		}
		
		Page<Map<String, Object>> list = merchantService.merAuditingExportExcel(params, page);
		
		Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/auditions.xls"));

		WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
		WritableSheet ws = wwb.getSheet(0);

//		String[] pos_type = {"","移联商宝","传统POS","移小宝","移联商通","超级刷"};
		Map<String, String> posTypes = posTypeService.getPosTypes();

		Iterator<Map<String, Object>> it = list.iterator();
		int index = 1;
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(index, "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("real_name"), "无")));//审核人
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("create_time"), "无")));// 日期
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("sale_name"), "无")));//销售名称
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("agent_name"), "无")));//代理商
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_short_name"), "无")));//商户名称
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_no"), "无")));//商户编号

			if(map.get("pos_type") == null || String.valueOf(map.get("pos_type")) == ""){
				ws.addCell(new Label(col++, row, "无"));
			} else {
				ws.addCell(new Label(col++, row, posTypeName(map.get("pos_type").toString())));
				//ws.addCell(new Label(col++, row, pos_type[Integer.parseInt(String.valueOf(map.get("pos_type")))]));
				//ws.addCell(new Label(col++, row, posTypes.get(String.valueOf(map.get("pos_type")))));
			} // 机型
			
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("terminal_count"), "无")));//台数
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("terminal_no"), "无")));//psn卡
			
			row++;
			index++;
			col = 0;
		}

		wwb.write();
		wwb.close();
		wb.close();
		os.close();
	}
	
	
	// 商户查询
		@RequestMapping(value = "/merQueryN")
		public String merQueryN(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {

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

			Page<Map<String, Object>> list = merchantService.getMerListN(params,
					page);
			List<Map<String, Object>> checker = merchantService.getCheckerListForTag2();
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			model.put("checker", checker);
			return "/merchant/merchantQueryNew";
		}
	
	

	// 商户查询
	@RequestMapping(value = "/merQuery")
	public String merQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

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

		Page<Map<String, Object>> list = merchantService.getMerList(params,
				page);
		List<Map<String, Object>> checker = merchantService.getCheckerListForTag2();
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		model.put("checker", checker);
		return "/merchant/merchantQuery";
	}

	// 未交易商户查询
	@RequestMapping(value = "/notransQuery")
	public String merNotransQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		System.out.println("总数量：" + params.size());
		Page<Map<String, Object>> list = merchantService.getMerNotransList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/merchant/merchantManager";
	}

	// 商户详情
	@RequestMapping(value = "/merDetail")
	public String merDetail(final ModelMap model, @RequestParam Long id)
			throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();

		Map<String, Object> merInfo = merchantService.getMerDetail(id);// 查询商户详情
		String belong_to_agent = (String) merInfo.get("belong_to_agent");
		System.out.println("belong_to_agent:" + belong_to_agent);
		String belongToAgentName = agentService.getAgentName(belong_to_agent);// 根据所属代理商编号查询代理商名称
		String trans_time_start = StringUtil.ifEmptyThen(
				merInfo.get("trans_time_start"), "");
		String trans_time_end = StringUtil.ifEmptyThen(
				merInfo.get("trans_time_end"), "");
		merInfo.put("trans_time_start_short", "".equals(trans_time_start) ? ""
				: trans_time_start.substring(11, 19));
		merInfo.put("trans_time_end_short", "".equals(trans_time_end) ? ""
				: trans_time_end.substring(11, 19));

		Map<String, Object> merchantInfoMap = merchantService
				.queryMerchantFee((String) merInfo.get("merchant_no"));
		Map<String, Object> merchantTransRuleMap = merchantService
				.merchantTransRuleQueryByMerchantNoToMap((String) merInfo
						.get("merchant_no"));

		Map<String, String> paramsShenhe = new HashMap<String, String>();
		paramsShenhe.put("merchant_no", (String) merInfo.get("merchant_no"));

		PageRequest page = new PageRequest(0, PAGE_NUMERIC);
		Page<Map<String, Object>> posMerchantShenheList = merchantService
				.merchantShenheListByMerchantno(paramsShenhe, page);

		// if("0".equals((String)merInfo.get("open_status"))){
		// merInfo.put("open_status", "关闭");
		// }else if("1".equals((String)merInfo.get("open_status"))){
		// merInfo.put("open_status", "正常");
		// }

		// if(FeeType.RATIO.toString().equals((String)merchantInfoMap.get("fee_type"))){
		// merchantInfoMap.put("fee_type", "比例");
		// }else
		// if(FeeType.SINGLE.toString().equals((String)merchantInfoMap.get("fee_type"))){
		// merchantInfoMap.put("fee_type", "单笔");
		// }else
		// if(FeeType.RATIOANDSINGLE.toString().equals((String)merchantInfoMap.get("fee_type"))){
		// merchantInfoMap.put("fee_type", "比例+单笔");
		// }else
		// if(FeeType.CAPPING.toString().equals((String)merchantInfoMap.get("fee_type"))){
		// merchantInfoMap.put("fee_type", "封顶");
		// }
		params.put("belong_to_agent_name", belongToAgentName);
		params.putAll(merInfo);
		params.putAll(merchantInfoMap);
		params.putAll(merchantTransRuleMap == null ? new HashMap<String, Object>()
				: merchantTransRuleMap);

		Object ladder_fee = merchantInfoMap.get("ladder_fee");
		if (ladder_fee != null && ladder_fee.toString().length() > 0) {
			if (ladder_fee.toString().indexOf("<") > 0) {
				String[] ladder = ladder_fee.toString().split("<");
				String ladder_min = ladder[0];
				String ladder_value = ladder[1];
				String ladder_max = ladder[2];

				params.put("ladder_min",
						new BigDecimal(ladder_min).movePointRight(2));
				params.put("ladder_value", ladder_value);
				params.put("ladder_max",
						new BigDecimal(ladder_max).movePointRight(2));
			}
		}

		String addType = merInfo.get("add_type").toString();
		if ("1".equals(addType)) {
			String attachments = merInfo.get("attachment").toString();
			String[] attachment = attachments.split(",");
			List<String> picList = new ArrayList<String>();
			for (int i = 0; i < attachment.length; i++) {
				String pic = attachment[i];
				picList.add(pic);
			}
			model.put("picList", picList);
		}

		// 通过商户编号和激活码中的使用者相关联查询激活码
		String merchant_no = merInfo.get("merchant_no").toString();
		Map<String, Object> list = merchantService
				.codeVerification(merchant_no);
		if (list != null) {
			params.put("keycode", list.get("keycode"));
		} else {
			params.put("keycode", "");
		}

		model.put("params", params);
		if (posMerchantShenheList != null) {
			model.put("posMerchantShenheList", posMerchantShenheList);
		}

		// 大商户列表
		List<Map<String, Object>> posAcqMerchantList = merchantService
				.getluyouMerNo(merInfo);
		if (posAcqMerchantList != null) {
			model.put("posAcqMerchantList", posAcqMerchantList);
		}
		
		//加载用于显示的客户端类型
		String appTypeName=merchantService.findAppPosTypeByAppNo((Integer) merInfo.get("app_no"));
		if(appTypeName==null){
			appTypeName="传统POS";
		}
		model.put("appTypeName", appTypeName);
		return "/merchant/merchantDetail";
	}

	// 修改页面
	@RequestMapping(value = "/merload")
	public String merload(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception {

		String id = params.get("id");
		// 商户审核失败，根据id跳转到新增商户界面进行修改
		if (id != null) {
			params.put("agent_no", getAgentNo());

			Map<String, Object> merchantMap = merchantService
					.merchantQueryById(Long.valueOf(id));
			log.info("--------------------------"+merchantMap.get("agent_lock"));
			// 获得一级代理商编号，对其进行关联查询所属自己销售--------start----------
			String find_agent_no = (String) merchantMap.get("agent_no");
			Map<String, Object> amp = merchantService
					.findSaleNameByAgentNo(find_agent_no);

			merchantMap.put("self_sale_name", amp.get("self_sale_name"));
			// -------------------------end-------------------------------

			String trans_time_start = StringUtil.ifEmptyThen(
					merchantMap.get("trans_time_start"), "");
			String trans_time_end = StringUtil.ifEmptyThen(
					merchantMap.get("trans_time_end"), "");
			merchantMap.put(
					"trans_time_start_short",
					"".equals(trans_time_start) ? "" : trans_time_start
							.substring(11, 19));
			merchantMap.put(
					"trans_time_end_short",
					"".equals(trans_time_end) ? "" : trans_time_end.substring(
							11, 19));

			Map<String, Object> posMerchantFeeMap = merchantService
					.merchantFeeQueryByMerchantNoToMap(merchantMap.get(
							"merchant_no").toString());
			Map<String, Object> posMerchantTransRuleMap = merchantService
					.merchantTransRuleQueryByMerchantNoToMap(merchantMap.get(
							"merchant_no").toString());

			// merchantMap.put("rate1", new
			// BigDecimal(posMerchantFeeMap.get("fee_rate").toString()).movePointRight(2));
			// merchantMap.put("rate2",
			// posMerchantFeeMap.get("fee_max_amount"));

			// 通过商户编号和激活码中的使用者相关联查询激活码
			String merchant_no = merchantMap.get("merchant_no").toString();
			int flagCount = getMerchantGroupInfo(merchant_no);
			merchantMap.put("flagCount", flagCount);
			Map<String, Object> list = merchantService
					.codeVerification(merchant_no);
			if (list != null) {
				merchantMap.put("keycode", list.get("keycode"));
			} else {
				merchantMap.put("keycode", "");
			}

			Object fee_rate = posMerchantFeeMap.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				merchantMap.put("rate1",
						new BigDecimal(posMerchantFeeMap.get("fee_rate")
								.toString()).movePointRight(2));
			}

			merchantMap.put("rate2", posMerchantFeeMap.get("fee_max_amount"));

			Object ladder_fee = posMerchantFeeMap.get("ladder_fee");
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];

					merchantMap.put("ladder_min",
							new BigDecimal(ladder_min).movePointRight(2));
					merchantMap.put("ladder_value", ladder_value);
					merchantMap.put("ladder_max",
							new BigDecimal(ladder_max).movePointRight(2));
				}
			}

			merchantMap.put("fee_type", posMerchantFeeMap.get("fee_type"));
			merchantMap.put("fee_cap_amount",
					posMerchantFeeMap.get("fee_cap_amount"));

			merchantMap.put("single_max_amount",
					posMerchantTransRuleMap.get("single_max_amount"));
			merchantMap.put("ed_max_amount",
					posMerchantTransRuleMap.get("ed_max_amount"));
			merchantMap.put("ed_card_max_items",
					posMerchantTransRuleMap.get("ed_card_max_items"));
			merchantMap.put("ed_card_max_amount",
					posMerchantTransRuleMap.get("ed_card_max_amount"));

			// 根据代理商编号查询代理商
			String agent_no = merchantMap.get("agent_no").toString();
			System.out.println("代理商编号：" + agent_no);
			Map<String, Object> agentMap = merchantService.getAgentNameByNo(agent_no);
			merchantMap.put("agent_name", agentMap.get("agent_name").toString());
			model.put("params", merchantMap);

			String addType = merchantMap.get("add_type").toString();
			if ("1".equals(addType)) {
				String attachments = merchantMap.get("attachment").toString();
				String[] attachment = attachments.split(",");
				List<String> picList = new ArrayList<String>();
				for (int i = 0; i < attachment.length; i++) {
					String pic = attachment[i];
					picList.add(pic);
				}
				model.put("picList", picList);
			}
			
			List<Map<String,Object>> appPosType=merchantService.findAppPosTypeAll();
			model.put("appPosTypes", appPosType);
		}

		return "/merchant/merchantInfo";
	}
	
	/**
	 * 根据商户编号获取当前商户所属普通集群信息
	 * @param merchant_no 商户编号
	 * @return 修改权限状态 1:忽视，2.无权修改 3. 权限验证通过
	 */
	public int getMerchantGroupInfo(String merchant_no){
		int flag = 1;
		if(null != SysConfig.value("3041groupMerchantManager") && !"".equals(SysConfig.value("3041groupMerchantManager"))){
			Map<String, Object> mgMap = groupMerchantService.getGroupMerchantInfo(merchant_no);
			if(null != mgMap && null != mgMap.get("group_code")){
				log.info("进入商户修改界面，系统查询出所属集群信息，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
				if(!"".equals(mgMap.get("group_code").toString())){
					String q_group_code = mgMap.get("group_code").toString();
					log.info("进入商户修改界面，系统查询出所属集群编号信息为："+q_group_code+"，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
					if("3041".equals(q_group_code)){
						log.info("进入商户修改界面，系统查询出所属集群编号信息为处理目标集群："+q_group_code+"，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
							if(SysConfig.value("3041groupMerchantManager").equals(getUser().getRealName())){
								flag = 3;
							}else{
								flag = 2;
								log.info("进入商户修改界面，系统查询出所属目标集群编号信息为："+q_group_code+"，登陆名称验证失败，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
							}
					}else{
						log.info("进入商户修改界面，系统查询出所属集群编号信息为："+q_group_code+"，非目标处理目标集群，忽视限制，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
					}
				}else{
					log.info("进入商户修改界面，系统查询出所属集群编号为空，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
				}
			}else{
				log.info("进入商户修改界面，系统未查询出所属集群信息，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
			}
		}else{
			log.info("系统未检测到3041集群信息限制信息，商户编号为：" + merchant_no+", 操作人：" + getUser().getRealName());
		}
		return flag;
	}
	
	// 锁定/解锁商户
	@RequestMapping(value = "/merLock")
	public void merLock(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		log.info("--------------------merLock---------------");
		String merchant_no = params.get("merchant_no");
		String lockStr = params.get("agent_lock");
		String agent_lock = "0";
		if("解锁".equals(lockStr)){
			agent_lock = "1";
		}
		log.info("-------lock------"+agent_lock+"-------操作---"+lockStr);
		String checker = getUser().getUserName();
		try {
			merchantService.updateMerchantAgentLock(merchant_no, agent_lock);
			merchantService.insertLockRecord(merchant_no, checker, lockStr,"成功");
		} catch (SQLException e) {
			log.info("----------解锁/锁定出错------------");
			e.printStackTrace();
		}
	}

	// 商户修改
		@RequestMapping(value = "/merUpdate")
		public String merUpdate(final ModelMap model, HttpServletRequest request,
				HttpServletResponse response,	@RequestParam Map<String, String> params) {
			log.info("MerchantContrller merUpdate 修改商户开始");
			String merchantNO;

			// 获取支付方式
			String pay_method = params.get("final_pay_method");
			pay_method = (null == pay_method || "".equals(pay_method)) ? "00"
					: pay_method;
			params.put("pay_method", pay_method);

			params.put("terminal_no", params.get("terminalNo").trim());
			params.put("trans_time_start_short", params.get("trans_time_start"));
			params.put("trans_time_end_short", params.get("trans_time_end"));
			
			try {

				String id = params.get("id");
				// 更新重复判断
				Map<String, String> map = new HashMap<String, String>();
				//map.put("merchant_name", params.get("merchant_name").trim());
				map.put("id", id);
				List<Map<String, Object>> merchantList = merchantService.merchantQuery(map);
				boolean flag = true;
				if(merchantList != null && merchantList.get(0).get("create_time") != null){
					if(!"".equals(merchantList.get(0).get("create_time").toString())){
						String create_time = merchantList.get(0).get("create_time").toString();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date s = sdf.parse(create_time);
							flag = s.before(sdf.parse("2015-02-09 00:00:00")); //2015-02-09日之后注册的商户修改时会验证身份证、结算银行卡号唯一性
						} catch (Exception e) {
							log.error("MerchantController merUpdate Exception ="+e.getMessage());
							model.put("flag", "0");
							model.put("errorMessage", "时间校验出现错误，请重试！");
							model.put("params", params);
							return "/merchant/merchantInfo";
						}
						
					}
				}
				/*if (merchantList != null && merchantList.size() > 0) {
					for (Map<String, Object> merchant : merchantList) {
						System.out.println("查询出来的商户id:" + merchant.get("id"));
						if (!id.equals(merchant.get("id").toString())) {
							model.put("flag", "0");
							model.put("errorMessage", "商户全称已存在");
							model.put("params", params);

							return "/merchant/merchantInfo";
						}
					}

				}

				map.clear();
				map.put("merchant_short_name", params.get("merchant_short_name").trim());
				map.put("app_no", params.get("app_no").trim());
				map.put("agent_no", params.get("agent_no").trim());
				merchantList = merchantService.merchantQuery(map);
				if (merchantList != null && merchantList.size() > 0) {
					for (Map<String, Object> merchant : merchantList) {
						if (!id.equals(merchant.get("id").toString())) {
							model.put("flag", "0");
							model.put("errorMessage", "商户简称已存在");
							model.put("params", params);

							return "/merchant/merchantInfo";
						}
					}

				}
				*/
				map.clear();
				map.put("mobile_username", params.get("mobile_username").trim());
				map.put("pos_type", params.get("pos_type").trim());
				map.put("app_no", params.get("app_no").trim());
				map.put("agent_no", params.get("agent_no").trim());
				merchantList = merchantService.merchantQuery(map);
				if (merchantList != null && merchantList.size() > 0) {
					for (Map<String, Object> merchant : merchantList) {
						if (!id.equals(merchant.get("id").toString())) {
							model.put("flag", "0");
							model.put("errorMessage", "登录手机号已存在");
							model.put("params", params);

							return "/merchant/merchantInfo";
						}
					}
				}
				String agent_no = params.get("agent_no");
				String pos_type = params.get("pos_type");
				/***
				 * 同一个身份证号、结算账号针对同一种设备类型（限移小宝、超级刷、移联商通）注册、系统只允许注册一次、不能多次注册
				 */
				if(!flag){
					if(
							"3".equals(pos_type)//移小宝
							|| "4".equals(pos_type)//移联商通
							|| "5".equals(pos_type)//超级刷
							|| ("2".equals(pos_type) && "8396".equals(agent_no)) //兴华通的传统POS
						){
						map.clear();
						map.put("id_card_no", params.get("id_card_no").toString());
						map.put("pos_type", params.get("pos_type").toString());
						map.put("agent_no", params.get("agent_no").toString());
						map.put("IA", "id_card");
						merchantList = merchantService.merchantQuery(map);
						if (merchantList != null && merchantList.size() > 0) {
							for (Map<String, Object> merchant : merchantList) {
								if (!id.equals(merchant.get("id").toString())) {
									model.put("flag", "0");
									model.put("errorMessage", "已经使用相同的身份证注册过该类型设备！");
									model.put("params", params);
									return "/merchant/merchantInfo";
								}
							}
						}
						map.clear();
						map.put("account_no", params.get("account_no").toString());
						map.put("pos_type", params.get("pos_type").toString());
						map.put("agent_no", params.get("agent_no").toString());
						map.put("IA", "account_no");
						merchantList = merchantService.merchantQuery(map);
						if (merchantList != null && merchantList.size() > 0) {
							for (Map<String, Object> merchant : merchantList) {
								if (!id.equals(merchant.get("id").toString())) {
									model.put("flag", "0");
									model.put("errorMessage", "已经使用相同的开户账号注册过该类型设备！");
									model.put("params", params);
									return "/merchant/merchantInfo";
								}
							}
						}
					}
				}
				
				// 检测代理商是否从属关系
				if (!merchantService.checkAgentExistsAndSatisfied(params.get("agent_no").trim(), params.get("belong_to_agent").trim())) {
					model.put("flag", "0");
					model.put("errorMessage", "代理商和所属代理商非从属关系");
					model.put("params", params);
					return "/merchant/merchantInfo";
				}

				merchantNO = params.get("merchant_no").trim();
				if (merchantNO == null) {
					model.put("flag", "0");
					model.put("errorMessage",
							"update fail:merchant_no must not be empty");
					model.put("params", params);
				} else {
					String attachment = params.get("attachment");
					String oldAttachment = params.get("oldAttachment");
					if (!oldAttachment.equals(attachment)) {
						FileUtils4AliYunOss.copyFile(attachment);
						params.put("attachment", attachment);
					}

					// 由代理商编号判断，如果不为空 则表示是拥有一级代理商修改权限，如过为空，则表示没有此权限，需根据商户名称去获取商户编号
//					String agent_no = (String) params.get("agent_no");
					if (agent_no == null || "".equals("agent_no")) {
						String agent_name = params.get("agent_name");
						if (agent_name != null && !"".equals("agent_name")) {
							System.out.println(agent_name);
							Map<String, Object> agentlist = merchantService
									.getAgentNoByNo(agent_name);
							params.put("agent_no", agentlist.get("agent_no")
									.toString());
						}
					}
					try {
						merchantService.merchantModify(params);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Exception MerchantContrller merUpdate 修改商户出现错误="+e.getMessage());
						model.put("flag", "0");
						model.put("errorMessage", "修改商户出现错误，请重试");
						model.put("params", params);
						return "/merchant/merchantInfo";
					}
					
					
					String bag_settle = params.get("bag_settle");
					String old_mobile_username = params.get("old_mobile_username").trim();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					// 同步到钱包
					Map<String, Object> map2= merchantService.getMerchantById(Long.valueOf(id));
					map2.put("oldMobileNo", old_mobile_username);
					String appNo = map2.get("app_no").toString().trim();
					String hmac = Md5.md5Str(old_mobile_username  + appNo + Constants.BAG_HMAC);
					map2.put("hmac", hmac);
					String xmlStr = Http.send(SysConfig.value("merModMobile2Bag"), map2, "UTF-8");
					if("1".equals(bag_settle)){
						try {
							DocumentBuilder db = dbf.newDocumentBuilder();
							Document doc = db.parse(new InputSource(new StringReader(xmlStr)));
							NodeList nl = doc.getElementsByTagName("msg");
							int length = nl.getLength();
							for(int i = 0; i < length; i++){
								Node n = nl.item(i);
								model.put("errorMessage", n.getFirstChild().getNodeValue());
							}
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e1) {
							e1.printStackTrace();
						}
					}
					
					
					params.put("merchant_no", merchantNO);
					PosMerchant posMerchant = merchantService
							.merchantQueryByMerchantNo(merchantNO);
					// PosMerchantFee posMerchantFee =
					// merchantService.merchantFeeQueryByMerchantNo(merchantNO);

//					String pos_type = (String) params.get("pos_type").trim(); // 设备类型
					String open_status = params.get("open_status").trim(); // 开通状态
					if ("4".equals(pos_type)) { // 移联商通绑定激活码
						// 绑定激活码
						String keycode = (String) params.get("keycode").trim();
						try {
							merchantService.bindkeycode(keycode, merchantNO,
									open_status);
						} catch (Exception e) {
							// e.printStackTrace();
							model.put("flag", "0");
							model.put("errorMessage", e.getMessage());
							model.put("params", params);
						}
					}
					model.put("params", params);
					model.put("posMerchant", posMerchant);
					// model.put("posMerchantFee", posMerchantFee);
					model.put("flag", "1");
					//model.put("errorMessage", "");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				model.put("flag", "0");
				model.put("errorMessage", e.getMessage());
				model.put("params", params);
			}
			log.info("MerchantContrller merUpdate 修改商户开始");
			return "/merchant/merchantInfo";
		}

	/*
	 * // 登录手机号验证
	 * 
	 * @RequestMapping(value = "/mobileVerification") public void
	 * mobileVerification(final ModelMap model, @RequestParam String
	 * id,HttpServletRequest request, HttpServletResponse response,
	 * 
	 * @RequestParam String mobile_username) throws Exception{ boolean isok =
	 * merchantService.merchantQuery(mobile_username, id); if (isok) {
	 * outText("1", response); }else{ outText("0", response); } }
	 */

	// 商户设置为 正常
	@RequestMapping(value = "/merSetNormal")
	public void merSetNormal(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		String merchantNO = params.get("merchant_no");
		// 打印传进来的商户名称start
		System.out.println("传入参数--商户名称：" + params.get("merchant_name"));
		// ------------------end

		String status = params.get("status");
		if ("".equals(status) || status == null) {
			status = "1";
		}
		String msg = "没有修改成功！";
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		if (merchantNO == null) {
			msg = "没有此商户!";
		} else {
			params.put("merchantNO", merchantNO);
			params.put("status", status);
			try {

				/*
				 * String httpParams ="merchantNo="+merchantNO; String
				 * tranStatus =
				 * Http.doGet("http://115.28.36.50:5780/mer/testquery.do",
				 * httpParams, "UTF-8", true).trim();
				 * 
				 * if(!"SUCCESS".equalsIgnoreCase(tranStatus)){
				 * 
				 * out.write(tranStatus); out.flush(); out.close(); return; }
				 */

				Map<String, Object> merInfo = merchantService.queryMerchantMsgByNo(merchantNO);
				String agentNo = merInfo.get("agent_no").toString();
				if (merInfo.get("terminal_no") != null	&& !"".equals(merInfo.get("terminal_no"))) {
					String terminalNos = merInfo.get("terminal_no").toString();
					//List<Map<String, Object>> list = terminalService.getPosTerminalByMno(merchantNO);
					//int merchantTerCount = list.size();

					// ---------------------------------机具分配代理商绑定商户-----------------------------------//
					if (!"0".equals(status)) {
						if (StringUtils.isNotEmpty(terminalNos)) {
							int updateCount = 0;
							String[] terminalNo = terminalNos.split(";");
							//if (merchantTerCount != terminalNo.length	&& merchantTerCount == 0) {
								//String allotBatch = GenSyncNo.getInstance().getNextPosAllotNo();
							if (terminalNo != null && terminalNo.length> 0) {
								for (String tno : terminalNo) {
									//验证当前机具分配的代理商与商户代理商是否一致
									List<Map<String, Object>> terList = terminalService.getTerminalByTerNo(tno.trim());
									if(terList != null && terList.size() > 0){
										if(terList.get(0).get("agent_no") != null && agentNo.equals(terList.get(0).get("agent_no").toString())){
											if("2".equals(terList.get(0).get("open_status").toString())){
												if(merchantNO.equals(terList.get(0).get("merchant_no").toString())){
													updateCount++;
												}else{
													msg = "开启失败,机具编号："+tno+"  已被同代理商下其他商户绑定!";
													out.write(msg);
													out.flush();
													out.close();
													return;
												}
											}else if("1".equals(terList.get(0).get("open_status").toString())){
												//int row = terminalService.setAgentAndBind(agentNo, tno.trim(), "MPOS-78",	allotBatch, merchantNO);
												int row = terminalService.setTerminalBind(merchantNO, Integer.parseInt(terList.get(0).get("id").toString()),Integer.parseInt(terList.get(0).get("agent_no").toString()));
												if(row > 0){
													String model = "NULL";
													if(terList.get(0).get("model") != null && "".equals(terList.get(0).get("model").toString())){
														model = terList.get(0).get("model").toString();
													}
													terminalService.saveAllotHistory(agentNo,"", terminalNo.length,model, getUser().getRealName());
												}
												updateCount = updateCount + row;
											}else{
												msg = "开启失败,机具编号："+tno+"  状态异常,请确认机具是否已分配当前商户所属代理商!";
												out.write(msg);
												out.flush();
												out.close();
												return;
											}
										}else{
											msg = "开启失败,机具编号："+tno+"  未分配到当前商户所属代理商!";
											out.write(msg);
											out.flush();
											out.close();
											return;
										}
										
									}else{
										msg = "开启失败,请检查商户机具编号："+tno+"  是否存在!";
										out.write(msg);
										out.flush();
										out.close();
										return;
									}
								}
								if (updateCount == 0) {
									// 未绑定机具
									msg = "未绑定成功机具，请检商户PSAM卡是否与系统机具对应。";
									out.write(msg);
									out.flush();
									out.close();
									return;
								}
								// 存记录
								//terminalService.saveAllotHistory(agentNo,allotBatch, terminalNo.length,"MPOS-78", getUser().getRealName());
								if (updateCount != terminalNo.length	&& updateCount != 0) {
									// 绑定部分机具
									msg = "绑定成功部分机具，请注意检查！";
									out.write(msg);
									out.flush();
									out.close();
									return;
								}
								
							}else{
								// 绑定部分机具
								msg = "开启失败，商户机具数量与PSAM卡号数量不符,请修改!";
								out.write(msg);
								out.flush();
								out.close();
								return;
							}
							if (updateCount != terminalNo.length) {
								// 绑定部分机具
								msg = "绑定成功部分机具，请注意检查！";
								out.write(msg);
								out.flush();
								out.close();
								return;
							}
						}else{
							msg = "开启商户失败,请输入正确的商户PASM卡号！";
							out.write(msg);
							out.flush();
							out.close();
							return;
						}
					}

					// ---------------------------------机具分配代理商绑定商户--------------------------------------//

				}

				int updateRows = merchantService.merchantSetNormal(params);
				if (updateRows == 1) {
					msg = "SUCCESS";
					if ("1".equals(status)) {

						// 发送短信通知商户
						String projectRunFlag = SysConfig.value("projectRunFlag");
						String phoneNumber = params.get("mobile_username");
						String merchant_name = params.get("merchant_name");
						String content = "您提交的商户" + merchant_name + "已经开通。";
						if (!"test".equals(projectRunFlag)) {
							SmsUtil.sendSms(new SmsBean(phoneNumber, agentNo, merInfo.get("pos_type").toString(), "1", new String[]{merchant_name}));
							/*// 移小宝发送下载地址
							String pos_type = merInfo.get("pos_type")
									.toString();
							String agentid = merInfo.get("agentid").toString();
							String special_agent_zk = SysConfig
									.value("special_agent_zk");
							String special_agent_xzf = SysConfig
									.value("special_agent_xzf");
							String special_agent_cf = SysConfig
									.value("special_agent_cf");
							String special_agent_zl = SysConfig
									.value("special_agent_zl");
							String special_agent_qyb = SysConfig
									.value("special_agent_qyb");
							String special_agent_lb = SysConfig
									.value("special_agent_lb");
							String special_agent_wsy = SysConfig
									.value("special_agent_wsy");
							String special_agent_mjd = SysConfig
									.value("special_agent_mjd");

							// 支付界不发送短信
							if ("3".equals(pos_type) && !"3124".equals(agentNo)) {
								content = "您提交的商户" + merchant_name + "已经开通。";
								if (special_agent_zk.equals(agentid)) {
									Sms.sendMsgOem(phoneNumber, content,
											"[中宽支付]");
								} else if (special_agent_xzf.equals(agentid)) {
									Sms.sendMsgOem(phoneNumber, content,
											"[新支付]");
								} else if (special_agent_cf.equals(agentid)) {
									Sms.sendMsgOem(phoneNumber, content,
											"[诚付天下]");
								} else if (special_agent_zl.equals(agentid)
										|| special_agent_qyb.equals(agentid)
										|| special_agent_lb.equals(agentid)
										|| special_agent_wsy.equals(agentid)
										|| special_agent_mjd.equals(agentid)) {
									Sms.sendMsgOem(phoneNumber, content,
											"[支付随心]");
								} else {
									Sms.sendMsg(phoneNumber, content);
								}
							}

							// 新支付的超级刷 发送开通短信
							if("5".equals(pos_type) && special_agent_xzf.equals(agentid)){
								Sms.sendMsgOem(phoneNumber, "您提交的商户" + merchant_name + "已经开通。", "[新支付]");
							}*/
						}
					}

				} else {
					msg = "设置出错！" + updateRows;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				msg = "修改状态失败！";
			}
			try {
				out.write(msg);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.flush();
				out.close();
			}
		}

	}

	// 商户状态设置为 冻结（冻结该商户的交易）
	@RequestMapping(value = "/merFreezeTrans")
	public void merFreezeTrans(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		String merchantNO = params.get("merchant_no");
		String msg = "没有修改成功！";
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		if (merchantNO == null) {
			msg = "没有此商户!";
		} else {
			try {
				int updateRows = merchantService.merFreeze(params);
				if (updateRows == 1) {
					msg = "已把商户设置为冻结！";
				} else {
					msg = "设置出错！" + updateRows;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				msg = "修改状态失败！";
			}
			try {
				out.write(msg);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.flush();
				out.close();
			}
		}

	}

	// 商户删除
	@RequestMapping(value = "/merDel")
	public void merDel(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		log.info("MerchantController merDel 删除商户开始。。。");
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			String id = params.get("id");
			String merchant_no = params.get("merchant_no");
			Long transCount = merchantService.queryMerTransCount(merchant_no);
			if (transCount > 0) {
				log.info("MerchantController merDel 删除商户错误,操作人"+getUser().getRealName()+",商户编号"+params.get("merchant_no").toString()+",错误信息=商户存在交易记录 " );
				okMap.put("msg", "ERROR");
				okMap.put("code", "2001");//existsTrans
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}
			merchantService.merchantDel(id, merchant_no);
			okMap.put("msg", "OK");
			String json = JSONObject.fromObject(okMap).toString();
			log.info("MerchantController merDel  删除商户操作完成，商户编号="+merchant_no+"，操作人="+getUser().getRealName());
			outJson(json, response);
		} catch (Exception e) {
			log.error("MerchantController merDel 删除商户异常,操作人"+getUser().getRealName()+",商户编号"+params.get("merchant_no").toString()+",异常信息= " + e.getMessage());
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}

	/*
	 * 手机充值查询
	 */
	@RequestMapping(value = "/mobiletrans")
	public String mobiletrans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
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

		Page<Map<String, Object>> list = merchantService.getMobileList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/merchant/mobiletrans";
	}

	/*
	 * 手机充值查询详情
	 */
	@RequestMapping(value = "/mobilePayDetail")
	public String mobiletransDetail(final ModelMap model, @RequestParam Long id) {
		Map<String, Object> list = merchantService.getMobileDetailList(id);
		model.put("params", list);
		return "/merchant/mobilePayDetail";
	}

	/*
	 * 信用卡还款查询
	 */
	@RequestMapping(value = "/creditTrans")
	public String creditTrans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

		Page<Map<String, Object>> list = merchantService.getCreditList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/merchant/creditTrans";
	}

	/*
	 * 信用卡还款详情
	 */
	@RequestMapping(value = "/creditTransDetail")
	public String creditTransDetail(final ModelMap model, @RequestParam Long id) {
		Map<String, Object> list = merchantService.getCreditDetail(id);
		model.put("params", list);
		return "/merchant/creditTransDetail";
	}

	// 交易审核查询
	@RequestMapping(value = "/transCherck")
	public String transCherck(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		String ids = (String) params.get("ids");

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

		Page<Map<String, Object>> list = transService.getTrans(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/merchant/merchantTransCheck";
	}

	/**
	 * 加载移小宝签名内容
	 * 
	 * @param id
	 *            交易记录id
	 * @param model
	 * @return
	 */
	@RequestMapping("/receiptReviewLoad/{id}")
	// public String receiptReview(@@PathVariable("id") Long id, Model model) {
	public String receiptReview(@PathVariable("id") Long id, ModelMap model) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> transInfoMap = transService.queryTransInfoById(id);
		Map<String, Object> merchantInfoMap = merchantService.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));
		CardBin cardBin = bankCardService.cardBin((String) transInfoMap.get("account_no"));
		String account_no =  transInfoMap.get("account_no").toString();
		if(transInfoMap.get("is_iccard") != null && !"".equals(transInfoMap.get("is_iccard").toString())){
			if("0".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/S";
			}else if("1".equals(transInfoMap.get("is_iccard").toString())){
				account_no += "/I";
			}else{
				account_no += "/"+transInfoMap.get("is_iccard").toString();
			}
		}else{
			account_no += "/ ";
		}
		transInfoMap.put("account_no", account_no);
		Date create_time = (Date) (transInfoMap.get("create_time"));
		String transId = OrderUtil.buildOrderId(id, create_time);
		model.addAttribute("transId", transId);
		model.addAttribute("id", id);
		model.addAttribute("sysconfigPath", SysConfig.value("signPath"));
		model.addAttribute("reviewStatus", transInfoMap.get("review_status"));
		String signUrl = "";
		String signPath = SysConfig.value("signPath");
		if(transInfoMap != null && transInfoMap.get("trans_source") != null && !"".equals(transInfoMap.get("trans_source").toString())
				&& "POS".equals(transInfoMap.get("trans_source").toString()) && transInfoMap.get("sign_img") != null && 
				!"".equals(transInfoMap.get("sign_img").toString())){
			Calendar currentCalendar = Calendar.getInstance();
			// 生成url在1小时候失效
			Date expiresDate = new Date(
					currentCalendar.getTime().getTime() * 3600 * 1000);
			signUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_SIGN_TUCKET,transInfoMap.get("sign_img").toString(), expiresDate);
		}else{
			signUrl = genImgUrl(transId, signPath);
		}
		
		/* 电子小票签名图片地址确定 */
		
		model.put("signUrl", signUrl);
		//model.put("cardBin",cardBin);
		//System.out.println("bank_nameT = " + cardBin.getBankName());
		params.put("bank_nameT",cardBin.getBankName());
		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		model.put("params", params);

		return "/merchant/merchantSignReview";
	}

	private String genImgUrl(String transId, String signPath) {
		String url1 = Constants.SIGN_URL1 + "/" + signPath + transId + ".png";
		String url2 = Constants.SIGN_URL2 + "/" + signPath + transId + ".png";
		String signUrl = null;
		if (urlExists(url1)) {
			signUrl = url1;
		} else if (urlExists(url2)) {
			signUrl = url2;
		} else {
			Calendar currentCalendar = Calendar.getInstance();
			// 生成url在1小时候失效
			Date expiresDate = new Date(
					currentCalendar.getTime().getTime() * 3600 * 1000);
			signUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_SIGN_TUCKET,
					transId + ".png", expiresDate);
		}
		return signUrl;
	}

	@RequestMapping("/stp")
	public String stp(@RequestParam String id, ModelMap model) {

		int flag = id.indexOf("|");
		String[] idts = null;
		if (flag > 0) {
			idts = id.split("\\|");
		} else {
			idts = id.split("_");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		Long idl = Long.parseLong(idts[0].toString());
		String tNo = idts[1].toString();
		Map<String, Object> transInfoMap = transService
				.queryTransInfoByIdAndmNo(idl, tNo);

		Map<String, Object> merchantInfoMap = merchantService
				.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));
		Date create_time = (Date) (transInfoMap.get("create_time"));

		String transId = OrderUtil.buildOrderId(idl, create_time);
		model.addAttribute("id", idl);
		model.addAttribute("reviewStatus", transInfoMap.get("review_status"));
		String signPath = SysConfig.value("signPath");
		String signUrl = genImgUrl(transId, signPath);
		model.put("signUrl", signUrl);
		params.put("agentId", merchantInfoMap.get("id"));
		params.put("agentParentId", merchantInfoMap.get("parent_id"));
		params.put("trans_id", transId);
		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		model.put("params", params);

		return "/merchant/ticketPreview";
	}

	private boolean urlExists(String url) {
		HttpURLConnection huc = null;
		try {
			URLConnection uc = new URL(url).openConnection();
			huc = (HttpURLConnection) uc;
			huc.setConnectTimeout(1000);
			// 不允许自动转发 302
			huc.setInstanceFollowRedirects(false);
			huc.connect();
			if (huc.getResponseCode() == 200) {
				return true;
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
			if (huc != null) {
				huc.disconnect();
			}
		}
		return false;
	}

	/**
	 * 审核移小宝签名
	 * 
	 * @param status 1 正常 ， 空字符串 、 null 、 2 待审核或者* ， 3 审核失败
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/receiptReview")
	@ResponseBody
	public void receiptReviewStatusChange(@RequestParam("status") String status, Model model,	@RequestParam("id") Long id,HttpServletResponse response) {
		log.info("MerchantController receiptReviewStatusChange 小票审核开始执行...");
		boolean result = true;
		JSONObject jsonObject = new JSONObject();
		// 对提交状态的合法性进行检查
		if (!"1".equals(status) && !"3".equals(status)) {
			status = "2";
		}
		try {
			int checkCount = transService.updateReviewStatus(id, status,getUser().getRealName());
			log.info("MerchantController receiptReviewStatusChange 小票审核执行成功，操作人："+getUser().getRealName()+",操作状态："+status+"，操作ID编号="+id+",操作结果："+checkCount);
			if(checkCount <= 0){
				result = false;
			}else{
				Map<String, Object>map = transService.queryTransInfoById(id);
				if(map != null){
					if(map.get("sign_check_time") != null){
						if(!"".equals(map.get("sign_check_time").toString())){
							jsonObject.accumulate("sign_check_time",  map.get("sign_check_time").toString());
						}
					}
					
					if(map.get("sign_check_person") != null){
						if(!"".equals(map.get("sign_check_person").toString())){
							jsonObject.accumulate("sign_check_person", map.get("sign_check_person").toString());
						}
					}
					
					if("3".equals(status)){
						if(map.get("merchant_no") != null){
							if(!"".equals(map.get("merchant_no").toString())){
								String merchant_no = map.get("merchant_no").toString();
								Map<String, Object>  map2 = merchantService.getMerchantInfoByMNo(merchant_no);
								String mobile_username = map2.get("mobile_username").toString();
								int sendCount = Sms.sendMsg(mobile_username, "您有一笔交易的签购单因持卡人签名不规范被审核驳回,请及时要求持卡人对该笔签购单使用正楷规范重签其中文全名,以免影响您该笔交易资金正常到账!");
								if(sendCount>0){
									log.info("小票签名审核失败发送短信，手机号为："+mobile_username);
								}
							}
						}
					}
					
				}
			}
		} catch (SQLException e) {
			log.error("SQLException MerchantController receiptReviewStatusChange 小票审核执行时发生SQL异常="+e);
			e.printStackTrace();
			result = false;
			jsonObject.accumulate("result", result);
			outJson(jsonObject.toString(), response);
		}catch (Exception e) {
			log.error("Exception MerchantController receiptReviewStatusChange 小票审核执行时发生未知异常=" + e);
			e.printStackTrace();
			jsonObject.accumulate("result", result);
			outJson(jsonObject.toString(), response);
		}
		jsonObject.accumulate("result", result);
		log.info("MerchantController receiptReviewStatusChange 小票审核执行完毕！");
		outJson(jsonObject.toString(), response);
	}

	/*
	 * 违章罚款
	 */
	@RequestMapping(value = "/wzfktrans")
	public String wzfktrans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
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

		Page<Map<String, Object>> list = merchantService.getWzfkList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/merchant/wzfktrans";
	}

	/*
	 * 违章罚款详情
	 */
	@RequestMapping(value = "/wzfkPayDetail")
	public String wzfkPayDetail(final ModelMap model, @RequestParam Long id) {
		Map<String, Object> list = merchantService.getWzfkDetailList(id);
		model.put("params", list);
		return "/merchant/wzfkPayDetail";
	}

	@RequestMapping("/merchantHandlingCharge")
	public String merchantHandlingCharge(
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage,
			Model model) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}
		Page<Map<String, Object>> list = merchantHandlingChargeService
				.findLossTransInfo(params, page);
		model.addAttribute("list", list);
		model.addAttribute("params", params);
		return "/merchant/handlingCharge";
	}

	@RequestMapping("/exportExcelMerchantHandlingChargeLost")
	public void exportExcelMerchantHandlingChargeLost(
			@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName = "亏损交易" + sdf.format(new Date()) + "_" + random
				+ ".xls";
		PageRequest page = new PageRequest(0, 9999);
		Page<Map<String, Object>> list = merchantHandlingChargeService
				.findLossTransInfo(params, page);
		try {
			response.setHeader("Content-disposition", "attachment;filename="
					+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			OutputStream os = response.getOutputStream();
			WritableWorkbook wwbook = Workbook.createWorkbook(os);
			// 创建工作博
			WritableSheet ws = wwbook.createSheet("亏损交易", 0);
			int c = 0;// 列
			int r = 0;// 行
			// 合并第一行的8列
			ws.mergeCells(0, 0, 7, 0);// 从第0,0单元格合并到7,0单元格
			// 添加标题
			Label labelTitle = cl(c, r++, "亏损交易");
			// 标题文字相关格式设置
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 18,
					WritableFont.BOLD, false);
			wf.setColour(Colour.RED);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			labelTitle.setCellFormat(wcf);
			ws.addCell(labelTitle);
			ws.mergeCells(0, 1, 7, 1);
			String description = "导出的数据为所有的亏损交易";
			ws.addCell(cl(c, r++, description));
			// 添加各列表头
			ws.addCell(cl(c++, r, "普通商户名称"));
			ws.addCell(cl(c++, r, "费率"));
			ws.addCell(cl(c++, r, "普通商户费用(元)"));
			ws.addCell(cl(c++, r, "收单机构商户名称"));
			ws.addCell(cl(c++, r, "费率"));
			ws.addCell(cl(c++, r, "收单商户费用(元)"));
			ws.addCell(cl(c++, r, "亏损金额(元)"));
			ws.addCell(cl(c++, r, "交易日期"));
			// 设置单元格宽度
			ws.setColumnView(0, 20);
			ws.setColumnView(1, 20);
			ws.setColumnView(2, 20);
			ws.setColumnView(3, 20);
			ws.setColumnView(4, 20);
			ws.setColumnView(5, 20);
			ws.setColumnView(6, 20);
			ws.setColumnView(7, 23);

			r++;// 下移到新行
			c = 0;// 列复位
			// 填充数据
			List<Map<String, Object>> contents = list.getContent();
			// 格式化小数
			DecimalFormat df = new DecimalFormat("0.00");
			FastDateFormat fastDateFormat = FastDateFormat
					.getInstance("yyyy年MM月dd日 HH:mm:ss");
			for (Map<String, Object> map : contents) {
				ws.addCell(cl(c++, r, map.get("merchant_name")));
				ws.addCell(cl(c++, r, map.get("merchant_rate")));
				ws.addCell(cl(c++, r, map.get("merchant_fee")));
				ws.addCell(cl(c++, r, map.get("acq_merchant_name")));
				ws.addCell(cl(c++, r, map.get("acq_merchant_rate")));
				ws.addCell(cl(c++, r, map.get("acq_merchant_fee")));
				// 计算损失的金额
				Double lostMoney = Double.valueOf(map.get("acq_merchant_fee")
						.toString())
						- Double.valueOf(map.get("merchant_fee").toString());
				ws.addCell(cl(c++, r, df.format(lostMoney)));
				if (map.get("trans_time") != null) {
					ws.addCell(cl(c++, r,
							fastDateFormat.format(map.get("trans_time"))));
				}
				r++;// 移动到下一行
				c = 0;// 列号复位
			}
			// 写入到输出流
			wwbook.write();
			// 释放资源
			wwbook.close();
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	private static Label cl(int c, int r, Object con) {
		return new Label(c, r, String.valueOf(con));
	}

	// 转入充值导入页面
	@RequestMapping(value = "/viewMobilePayImp")
	public String viewMobilePayImp(final ModelMap model,
			@RequestParam Map<String, String> params) {

		model.put("params", params);
		return "/merchant/mobilePayImp";
	}

	// 机具导入
	@RequestMapping(value = "/mobileImp")
	public void mobileImp(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();

		String strDirPath = request.getSession().getServletContext()
				.getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;

		try {
			int line = mobileChargeService.mobileExcImp(params, tmpPath);
			okMap.put("msg", "OK");
			okMap.put("line", line + "");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);

		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}

	// 商户测试
	@RequestMapping(value = "/testMerchant")
	public void testMerchant(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		String merchantNO = params.get("merchant_no");
		String msg = "";
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {

			String httpParams = "merchantNo=" + merchantNO;
			String tranStatus = Http.doGet(
					"http://115.28.36.50:5780/mer/testquery.do", httpParams,
					"UTF-8", true).trim();
			;
			msg = tranStatus.trim();
			out.write(tranStatus);
			out.flush();
			out.close();
			return;

		} catch (Exception e1) {
			e1.printStackTrace();
			msg = "测试异常！";
			out.write(msg);
			out.close();
		} finally {
			out.flush();
			out.close();
		}
	}

	// 手机密码重置
	@RequestMapping(value = "/changePassword")
	public void changePassword(final ModelMap model,
			@RequestParam String merchantNo, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		try {
			boolean falg = merchantService.changePassword(merchantNo);
			if (falg) {
				response.getWriter().write("1");
			} else {
				response.getWriter().write("0");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 身份证验证
	@RequestMapping(value = "/idcardVerification")
	public void idcardVerification(final ModelMap model,
			@RequestParam String account_name, @RequestParam String id_card_no,
			HttpServletResponse response, HttpServletRequest request,
			@RequestParam Map<String, String> params) throws Exception {
		try {
			account_name = (account_name == null) ? params.get("account_name")	: account_name;
			id_card_no = (id_card_no == null) ? params.get("id_card_no")	: id_card_no;
			System.out.println("姓名：" + account_name + " ；号码： " + id_card_no);
			int checkCount  = merchantService.checkMerchantIndenti(account_name, id_card_no);
			if(checkCount < 1 || checkCount > 2){
				Map<String, String> map = merchantService.cardVerificationNew(account_name,	id_card_no);
				map.put("createPerson", getUser().getRealName());
				map.put("bySystem", String.valueOf(1));
				merchantService.savePYidentiInfo(map);
				checkCount = Integer.parseInt(map.get("reportStatus"));
			}
			if (1 == checkCount) {
				response.getWriter().write("1");
			} else {
				response.getWriter().write("0");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// 身份证验证
		@RequestMapping(value = "/idcardVerification2")
		public void idcardVerification2(final ModelMap model,
				@RequestParam String account_name, @RequestParam String id_card_no,
				HttpServletResponse response, HttpServletRequest request,
				@RequestParam Map<String, String> params) throws Exception {
			try {
				account_name = (account_name == null) ? params.get("account_name")	: account_name;
				id_card_no = (id_card_no == null) ? params.get("id_card_no")	: id_card_no;
				String  hmac = params.get("hmac").toString();
				if(!hmac.equals(Md5.md5Str(id_card_no+"AB26A199894022AD"))){
					return ;
				}
				
				
				System.out.println("姓名：" + account_name + " ；号码： " + id_card_no);
				int checkCount  = merchantService.checkMerchantIndenti(account_name, id_card_no);
				if(checkCount < 1 || checkCount > 2){
					Map<String, String> map = merchantService.cardVerificationNew(account_name,	id_card_no);
					map.put("createPerson", getUser().getRealName());
					map.put("bySystem", String.valueOf(1));
					merchantService.savePYidentiInfo(map);
					checkCount = Integer.parseInt(map.get("reportStatus"));
				}
				if (1 == checkCount) {
					response.getWriter().write("1");
				} else {
					response.getWriter().write("0");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	/**
	 * 商户复审通过
	 * @author 王帅
	 * @date 2014年11月24日14:42:26
	 * @see 商户复审通过
	 * @param model
	 * @param id 商户ID编号
	 */
	@RequestMapping(value = "/checkRepeatSucess")
	public void checkRepeatSucess(final ModelMap model, HttpServletResponse response, @RequestParam Long id) {
		log.info("MerchantController checkRepeatSucess start ...");
		JSONObject json = new JSONObject();
		int statusOpen = 0; //失败
		if(id != null && id > 0){
			Map<String, Object> map = merchantService.getMerchantInfoById(id);
			if(map != null){
				 //商户信息存在，验证商户状态是否为等待复审,否则修改失败
				if(map.get("open_status") != null && !"".equals(map.get("open_status").toString()) && "8".equals(map.get("open_status").toString())){
					try {
						statusOpen = mrcTransctionService.checkRepeatSucess(id, getUser().getRealName(),map);
						//statusOpen = merchantService.merchantRepeatSF(String.valueOf(id), String.valueOf(1));
						if(statusOpen == 8){
							//修改成功
							statusOpen = 1;
						}else {
							//修改失败
							statusOpen = 2;
						}
						Map<String, String> mapLo = new HashMap<String, String>();
						mapLo.put("merchant_no", map.get("merchant_no").toString());
						mapLo.put("create_person", getUser().getRealName());
						mapLo.put("merchant_stastu", String.valueOf(1));
						mapLo.put("return_status", String.valueOf(statusOpen));
						merchantService.merchantRepeatLog(mapLo);
						log.info("MerchantController checkRepeatSucess Sucess statusOpen = " + statusOpen);
					} catch (Exception e) {
						log.info("ERROR MerchantController checkRepeatSucess  Exception = " + e);
						e.printStackTrace();
						//系统错误
						statusOpen = 3;
						json.put("statusOpen", statusOpen);
						outJson(json.toString(), response);
					}
				}else{
					statusOpen = 4;
					//商户信息已被修改
				}
			} else {
				//商户信息不存在
				statusOpen = 5;
			}
		}else{
			statusOpen = 30; //数据错误
		}
		log.info("MerchantController checkRepeatSucess end");
		json.put("statusOpen", statusOpen);
		outJson(json.toString(), response);
	}
	
	/**
	 * 商户复审不通过
	 * @author 王帅
	 * @date 2014年11月24日14:42:26
	 * @see 商户复审不通过
	 * @param model
	 * @param id 商户ID编号
	 */
	@RequestMapping(value = "/checkRepeatFail")
	public void checkRepeatFail(final ModelMap model, HttpServletResponse response, @RequestParam Long id,@RequestParam String failMsg) {
		log.info("MerchantController checkRepeatFail start ...");
		JSONObject json = new JSONObject();
		int statusOpen = 0; //失败
		if(id != null && id > 0){
			try {
				Map<String, Object> merchant= merchantService.getMerchantInfoById(id);
				if(merchant != null){
					statusOpen = mrcTransctionService.merchantRepeatFail(id, failMsg, getUser().getRealName(),merchant);
					if(statusOpen == 1){
						String projectRunFlag = SysConfig.value("projectRunFlag");
						String phoneNumber = merchant.get("mobile_username").toString();
						String examination_opinions = failMsg;
						String content = "您提交的商户信息审核失败。审核意见:" + examination_opinions;
						String agentNo = merchant.get("agent_no").toString();
						String pos_type = merchant.get("pos_type").toString();
						if (!"test".equals(projectRunFlag)) {
							// 如果是移小宝发送审核失败原因 支付界不发送短信
							if (("3".equals(pos_type) || "4".equals(pos_type) || "5".equals(pos_type))	&& !"3124".equals(agentNo)) {
									Sms.sendMsg(phoneNumber, content);
							}
						}
						if ("4".equals(pos_type)) {// 只有移联商通才有激活码
							// 审核失败修改状态为已激活
							String keycode = merchant.get("keycode").toString();
							merchantService.updatefaidCodeState(keycode);
						}
						Map<String, String> params = new HashMap<String, String>();
						int logCount = 3; //记录审核失败操作
						params.put("examination_opinions", failMsg);
						params.put("open_status", String.valueOf(logCount));
						merchantService.insertExaminationsLog((String) merchant.get("merchant_no"), getUser().getUserName(),params);
					}
				}else{
					log.info("MerchantController checkRepeatFail : 未查到商户信息 merchant_id = " + id);
				}
			} catch (Exception e) {
				log.info("Error MerchantController checkRepeatFail Exception = " + e);
				if(statusOpen == 1){
					statusOpen = 10;
				}else{
					statusOpen = 6;
				}
				json.put("statusOpen", statusOpen);
				outJson(json.toString(), response);
			}
		}else{
			statusOpen = 3; //数据错误
		}
		log.info("MerchantController checkRepeatFail end");
		json.put("statusOpen", statusOpen);
		outJson(json.toString(), response);
	}
	
	@RequestMapping(value = "/checkRepeatDetail")
	public String checkRepeatDetail(final ModelMap model, @RequestParam Long id) throws Exception {
		Map<String, Object> map = merchantService.getMerchantInfoById(id);
		if(map != null && map.get("attachment") != null && !"".equals(map.get("attachment").toString())){
			String[] attachmentArray = map.get("attachment").toString().split(",");
			model.put("attachmentArray", attachmentArray);
		}
		model.put("params", map);
		return "/merchant/merchantShowPhotoDetail";
	}
	
	/**
	 * 商户复核
	 * @param model 数据
	 * @param params 参数
	 * @param cpage 分页信息
	 * @return 商户复核界面
	 */
	@RequestMapping(value = "/checkRepeatQuery")
	public String checkRepeatQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		log.info("MerchantControoler checkRepeatQuery start ....");
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = merchantService.queryCheckRepeatMerchant(params, page);
		List<Map<String, Object>> checkPerson = merchantService.getCheckerListForTag2();
		model.put("params", params);
		model.put("p", cpage);
		model.put("list", list);
		model.put("checkPerson", checkPerson);
		log.info("MerchantControoler checkRepeatQuery End");
		return "/merchant/merchantRepeatCheck";
	}
	
	/**
	 * 
	 * 功能：账户校验（结算中心）
	 *    对公：校验  账户+姓名
	 *    对私：校验  账户+姓名+身份证号
	 *
	 * @param model
	 * @param params
	 */
  @RequestMapping(value = "/szfsAccountVerify")
  public void szfsAccountVerify(@RequestParam Long id,HttpServletResponse response) throws IOException {
    String msg = "";
    ErrorVO error=new ErrorVO();
//    Map<String, String> params = new HashMap<String, String>();
    
    Map<String, Object> map = merchantService.queryMerchantInfoById(id);
    String accNo = map.get("account_no").toString();
    String accName = map.get("account_name").toString();
    String identityId = map.get("id_card_no").toString();
    String accountType=map.get("account_type").toString();

    Map<String, String> paramsMap=new HashMap<String, String>();
    String verifyType="";
    String bankNo="";
    if ("对私".equals(accountType)) {
      verifyType="2";
      CardBin cardBin = bankCardService.cardBin(accNo);
      if(cardBin==null){
    	  msg = "账号错误";
          log.warn(msg);
          response.getWriter().write(msg);
          return;
      }
      bankNo = cardBin.getBankNo();
      if(StringUtil.isBlank(bankNo)){
    	  msg = "不支持的清算行";
          log.warn(msg);
          response.getWriter().write(msg);
          return;
      }
      //结算中心，需要将总行清算行号转换为深圳分行清算行号
      Map<String, Object> szBankNoMap=SZFSBankNoCache.getBankMap(bankNo);
      if (szBankNoMap==null) {
        msg = "不支持的清算行";
        log.warn(msg);
        response.getWriter().write(msg);
        return;
      }
      bankNo=(String)szBankNoMap.get("sz_bank_no");
    }else{
      verifyType="1";
      String bankName=(String)map.get("bank_name");
      if (StringUtil.isBlank(bankName)) {
        msg = "开户行为空，无法校验";
        log.warn(msg);
        response.getWriter().write(msg);
        return;
      }
      bankNo=szfsService.getEntpriseBankNo(bankName);
      if (StringUtil.isBlank(bankNo)) {
        msg = "开户行有误或者不支持的开户行，无法校验";
        log.warn(msg);
        response.getWriter().write(msg);
        return;
      }
    }
    
    paramsMap.put("channel", "BOSS");
    paramsMap.put("verifyType", verifyType);
    paramsMap.put("accNo", accNo);
    paramsMap.put("accName", accName);
    paramsMap.put("bankNo", bankNo);
    paramsMap.put("identityId", identityId);
    paramsMap.put("merchantId", Long.toString(id));
    
    BossUser user=(BossUser) SecurityUtils.getSubject().getSession().getAttribute("user");
    paramsMap.put("userId", ""+user.getId());
    paramsMap.put("userName", user.getRealName());
    
    error=szfsService.accountVerify(paramsMap);
    log.info("结算中心校验账户："+error.getErrCode()+"|"+error.getErrMsg());
    response.getWriter().write(error.getErrMsg());
    return;
    
//    if (StringUtil.isBlank(accNo)) {
//      msg = "账号为空，无法校验";
//      log.warn(msg);
//      response.getWriter().write(msg);
//      return;
//    }
//    if (StringUtil.isBlank(accountType)) {
//      msg = "账号类型为空，无法校验";
//      log.warn(msg);
//      response.getWriter().write(msg);
//      return;
//    }
//    if (StringUtil.isBlank(accName)) {
//      msg = "户名为空，无法校验";
//      log.warn(msg);
//      response.getWriter().write(msg);
//      return;
//    }
//    String bankNo="";
//    if ("对私".equals(accountType)) {
//      if (StringUtil.isBlank(identityId)) {
//        msg = "身份证号为空，无法校验";
//        log.warn(msg);
//        response.getWriter().write(msg);
//        return;
//      }
//      CardBin cardBin = bankCardService.cardBin(accNo);
//      bankNo = cardBin.getBankNo();
//      if (StringUtil.isBlank(bankNo)) {
//        msg = "账号异常，获得清算行失败，无法校验";
//        log.warn(msg);
//        response.getWriter().write(msg);
//        return;
//      }
//      Map<String, Object> szBankNoMap=SZFSBankNoCache.getBankMap(bankNo);
//      if (szBankNoMap==null) {
//        msg = "不支持的开户行，无法校验";
//        log.warn(msg);
//        response.getWriter().write(msg);
//        return;
//      }
//      bankNo=(String)szBankNoMap.get("sz_bank_no");
//    }else {
//      String bankName=(String)map.get("bank_name");
//      if (StringUtil.isBlank(bankName)) {
//        msg = "开户行为空，无法校验";
//        log.warn(msg);
//        response.getWriter().write(msg);
//        return;
//      }
//      bankNo=szfsService.getEntpriseBankNo(bankName);
//      if (StringUtil.isBlank(bankNo)) {
//        msg = "开户行有误或者不支持的开户行，无法校验";
//        log.warn(msg);
//        response.getWriter().write(msg);
//        return;
//      }
//    }
//    
//    String verifyType="";
//    String identityType="";
//    if ("对私".equals(accountType)) {
//      verifyType="2";
//      identityType="01";
//    }else {
//      verifyType="1";
//      identityType="";
//      identityId="";
//    }
//    
//    List<Map<String, Object>> list=szfsDbService.queryAccVerify(accNo,accName,identityType,identityId);
//    if (list!=null&&list.size()>0) {
//      String szfsStatus="";
//      String szfsId="";
//      for (Map<String, Object> szfsMap : list) {
//        String tempStatus=(String)szfsMap.get("status");
//        if ("1".equals(tempStatus)||"3".equals(tempStatus)) {
//          szfsStatus=tempStatus;
//          szfsId=szfsMap.get("id").toString();
//          break;
//        }
//      }
//      if ("1".equals(szfsStatus)) {       
//        msg="校验成功";
//        log.warn("账号:"+accNo+"已经做过账户校验，并且"+msg);
//        response.getWriter().write(msg);
//        return;
//      }else if ("3".equals(szfsStatus)) {
//        szfsError=szfsService.queryInfoTrans("ACCTVERIFY",szfsService.SZFS_OUT_ID+"619"+szfsId);
//        String returnStatus=szfsError.getStatus();
//        if ("true".equals(szfsError.getSuccess())) {
//          msg = "校验成功";
//          params.put("id", szfsId);
//          params.put("status", "1");
//          params.put("errCode", szfsError.getSuccess());
//          params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
//          params.put("szfsStatus", szfsError.getStatus());
//          params.put("szfsProcode", szfsError.getProCode());
//          params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
//          szfsDbService.updateTranserFileUpload(params);
//        } else {
//          if ("00".equals(returnStatus)||"04".equals(returnStatus)||"15".equals(returnStatus)||"98".equals(returnStatus)) {
//            msg = "银行正在处理中，请稍后再试";
//          }else if ("time_out".equals(returnStatus)&& "time_out".equals(returnStatus)) {
//            msg = "校验超时";
//          } else {
//            msg = "校验失败";
//            params.put("id", szfsId);
//            params.put("status", "2");
//            params.put("errCode", szfsError.getSuccess());
//            params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
//            params.put("szfsStatus", szfsError.getStatus());
//            params.put("szfsProcode", szfsError.getProCode());
//            params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
//            szfsDbService.updateTranserFileUpload(params);
//          }
//          msg += "[" + szfsError.getStatus() + "|" + szfsError.getProCode() + "|"+ szfsError.getRemark() + "]";
//        }
//        log.warn("账号:"+accNo+" 已经做过账户校验，主动查询："+msg);
//        response.getWriter().write(msg);
//        return;
//      }
//      
//    }
//    
//    params.clear();
//    params.put("merchantId", Long.toString(id));
//    params.put("channel", "BOSS");
//    params.put("verifyType", verifyType);
//    params.put("bankNo", bankNo);
//    params.put("currency", "CNY");
//    params.put("accNo", accNo);
//    params.put("accName", accName);
//    params.put("identityType", identityType);
//    params.put("identityId", identityId);
//    params.put("mobileNo", "");
//    params.put("momo", "");
//    params.put("verifyerId", "" + getUser().getId());
//    params.put("verifyerName", ((BossUser) SecurityUtils.getSubject().getSession().getAttribute("user")).getRealName());
//
//    String referenceNo = szfsDbService.insertAccountVerify(params);
//    if (StringUtil.isBlank(referenceNo)) {
//      referenceNo = new Date().getTime() + (int) (Math.random() * 100000000)+ Long.toString(id);
//    }
//
//    String status = "";
//    szfsError = szfsService.acctVerify(verifyType,referenceNo, bankNo, accNo,accName, identityType, identityId);
//    String returnStatus=szfsError.getStatus();
//    if ("true".equals(szfsError.getSuccess())) {
//      status = "1";
//      msg = "校验成功";
//    } else {
//      if ("00".equals(returnStatus)||"04".equals(returnStatus)||"15".equals(returnStatus)||"98".equals(returnStatus)) {
//        status = "3";
//        msg = "银行正在处理中，请稍后再试";
//      }else if ("time_out".equals(returnStatus)&& "time_out".equals(returnStatus)) {
//        status = "3";
//        msg = "校验超时";
//      } else {
//        status = "2";
//        msg = "校验失败";
//      }
//      msg += "[" + szfsError.getStatus() + "|" + szfsError.getProCode() + "|"+ szfsError.getRemark() + "]";
//    }
//
//    params.clear();
//    params.put("id", referenceNo);
//    params.put("status", status);
//    params.put("errCode", szfsError.getSuccess());
//    params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
//    params.put("szfsStatus", szfsError.getStatus());
//    params.put("szfsProcode", szfsError.getProCode());
//    params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
//    szfsDbService.updateTranserFileUpload(params);
//
//    log.warn(msg);
//    response.getWriter().write(msg);

  }

  
  
	// 查询验证激活码是否可用
	@RequestMapping(value = "/findKeyCode")
	public void findKeyCode(final ModelMap model, @RequestParam String keycode,
			HttpServletResponse response) throws Exception {
		Map<String, Object> okMap = new HashMap<String, Object>();
		Map<String, Object> temp = merchantService.findCode(keycode);
		if (temp.isEmpty()) {
			okMap.put("msg", "noexist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} else {
			Integer state = Integer.parseInt(temp.get("state").toString());
			String code_user = (String) temp.get("code_user");
			if (state == 1 && ("".equals(code_user) || code_user == null)) { // 状态为激活，使用者为空，激活码可用
				okMap.put("msg", "available");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			} else if (state == 3 && !"".equals(code_user)) { // 状态为已使用，使用者不为空，激活码不可用
				okMap.put("msg", "noavailable");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		}
	}

	// 查询验证是否为钱包用户
	@RequestMapping(value = "/bagVerification")
	public void bagVerification(final ModelMap model,
			@RequestParam String mobile_no, HttpServletResponse response)
			throws Exception {
		Map<String, Object> okMap = new HashMap<String, Object>();
		List<Map<String, Object>> temp = purseService.findBagUser(mobile_no);
		if (temp.size() > 0) {
			okMap.put("flag", "exist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} else {
			okMap.put("flag", "notExist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
	}
	
		/**
		 *  , method = RequestMethod.POST
		 * 这是一个对外公布的接口，
		 * 根据商户编号获取商户限额信息
		 * @author swang
		 * @param merchantNo 商户编号
		 * @param response
		 * @param request
		 * @throws Exception
		 */
		@RequestMapping(value = "/getMerchantFee")
		public void getMerchantFee(@RequestParam String merchantNo, @RequestParam String key, HttpServletResponse response,
				HttpServletRequest request) throws Exception {
			log.info("MerchantController getMerchantFee start ...");
			JSONObject  jsonObject = new JSONObject();
			String msg = "商户编号或秘钥不允许为空!";
			String res_code = "1001";
			if(null != merchantNo && !"".equals(merchantNo) && null != key && !"".equals(key)){
				boolean flag = true;
				if(merchantNo.length() < 8 || merchantNo.length() > 24){
					msg = "商户编号错误";
					flag = false;
				}
				if(key.length() != 32){
					msg = "商户编号错误";
					flag = false;
				}
				String vkey = merchantNo+"f228cd33dcef91f03598e3b731c8afa0"; //移付宝MD5=f228cd33dcef91f03598e3b731c8afa0
				String checkKey = Md5.md5Str(vkey).toLowerCase();
				if(!key.equals(checkKey)){
					msg = "密钥校验失败!";
					flag = false;
				}
			  if(flag){
					Map<String, Object>  merchantFee = merchantService.merchantTransRuleQueryByMerchantNoToMap(merchantNo);
					if(null != merchantFee && merchantFee.size() > 0){
						jsonObject.put("ed_max_amount", merchantFee.get("ed_max_amount")); //单日终端最大交易额
						jsonObject.put("single_max_amount", merchantFee.get("single_max_amount")); //终端单笔最大交易额
						jsonObject.put("ed_card_max_amount", merchantFee.get("ed_card_max_amount")); //单日单卡最大交易额
						jsonObject.put("ed_card_max_items", merchantFee.get("ed_card_max_items")); //单日终端单卡最大交易笔数
						msg = "";
						res_code = "1002";
					}else{
						res_code = "1003";
						msg = "未查询到对应的商户交易规则信息";
					}
			 }
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("MerchantController getMerchantFee success = " + jsonObject.toString());
			response.getWriter().write(jsonObject.toString());
		}
		
		//接口：上传图片将额度状态改为待提高
		@RequestMapping(value = "/sendMerchantFile")
		public void sendMerchantFile(@RequestParam Map<String, String> params,
				HttpServletResponse response, HttpServletRequest request)
				{
			log.info("MerchantController sendMerchantFile start ...---");
			JSONObject jsonObject = new JSONObject();
			String merchantNo = params.get("merchantNo");
			String key = params.get("key");
			String msg = "商户编号或秘钥不允许为空!";
			String res_code = "1001";
			
			PosMerchant posMerchant = merchantService.selectPosMerchant(merchantNo);

			if (null != merchantNo && !"".equals(merchantNo) && null != key
					&& !"".equals(key)) {
				boolean flag = true;
				if (merchantNo.length() < 8 || merchantNo.length() > 24) {
					msg = "商户编号错误";
					flag = false;
				}
				if (key.length() != 32) {
					msg = "商户编号错误";
					flag = false;
				}
				String vkey = merchantNo + "f228cd33dcef91f03598e3b731c8afa0"; // 移付宝MD5=f228cd33dcef91f03598e3b731c8afa0
				String checkKey = Md5.md5Str(vkey).toLowerCase();
				if (!key.equals(checkKey)) {
					msg = "密钥校验失败!";
					flag = false;
				}
				if(posMerchant==null){
					msg = "无此商户!";
					flag = false;
				}else if("2".equals(posMerchant.getOpenStatus())){
					msg = "该商户状态为额度提升!";
					flag = false;
				}
				

				if (flag) {
					// 保存多个文件名字
					String attachment = "";
					boolean isUploadFile = false;
					try {
						MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
						Map<String, MultipartFile> fileMap = mRequest.getFileMap();
						String fileName = null;
						for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap
								.entrySet().iterator(); it.hasNext();) {
							Map.Entry<String, MultipartFile> entry = it.next();
							MultipartFile mFile = entry.getValue();
							fileName = mFile.getOriginalFilename();
							System.out.println("fileName=" + fileName);
							if ("".equals(fileName)
									|| "nofilename".equals(fileName)) {
								continue;
							} else {
								isUploadFile = true;
							}
							int random = new Random().nextInt(100000);
							fileName = "c_" + DateUtils.getMessageTextTime() + ""
									+ random + ".jpg";
							System.out.println("fileName=" + fileName);
							if ("".endsWith(attachment)) {
								attachment = attachment + fileName;
							} else {
								attachment = attachment + "," + fileName;
							}
							InputStream stream = mFile.getInputStream();
							long fileLength = mFile.getSize();
							if (fileLength > 100000000) {
								res_code = "1003";
								msg = "文件太大了";
								
								System.out.println("文件太大了======");
							} else {
								System.out.println("上传成功开始");
								ALiYunOssUtil.saveFile(
										Constants.ALIYUN_OSS_ATTCH_TUCKET,
										fileName, stream);

								isUploadFile = true;
								res_code = "1002";
								msg = "上传成功";
								System.out.println("上传成功结束");
								//循环里最后一张图片
							}
						}

						attachment = attachment.endsWith(",") ? attachment.substring(0, attachment.length() - 1) : attachment;

						// 图片已经保存到阿里云了。。。。剩下王帅你来完善....
						// 是不是要把这个文件名保存在商户信息数据库的某个字段？？方便客服那边找到这个图片审核=。=！
						// attachment 是文件名字符串。。逗号隔开
						attachment = posMerchant.getAttachment() + "," + attachment;
						log.info("-----------文件名attachment----------"+attachment);
						merchantService.updateLiftStatus("1", attachment, merchantNo);
					} catch (Exception e) {
						e.printStackTrace();
						res_code = "1004";
						msg = "服务端异常";
					}
					if (!isUploadFile) {
						res_code = "1005";
						msg = "至少上传一张照片！申请失败";
					}
				}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);
			response.setContentType("text/json;charset=UTF-8");
			log.info("MerchantController getMerchantFee success = "
					+ jsonObject.toString());
			try {
				response.getWriter().write(jsonObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		//商户导出
		@RequestMapping(value="/merExport")
		public void merExport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request){

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);
			String randomStr = StringUtil.stringFillLeftZero(""+random, 4);
			String fileName = "商户导出" + sdf.format(new Date()) + "_" + randomStr+ ".xls";
			
			OutputStream os = null;
			try {
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				merExportExcel(os, params, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private void merExportExcel(OutputStream os, Map<String, String> params, String fileName) throws Exception {
			
			int row = 2; // 从第三行开始写
			int col = 0; // 从第一列开始写

			PageRequest page = new PageRequest(0, 29999);
			
			if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String createTimeBegin = createTime + " 00:00";
				String createTimeEnd = createTime + " 23:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}

			Page<Map<String, Object>> list = merchantService.getMerList(params, page);	

			Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/merchants.xls"));

			WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
			WritableSheet ws = wwb.getSheet(0);

			String[] open_statuss = {"商户关闭","正常","待审核","审核失败","冻结","机具绑定","初审"};
			String[] open_types = {"无","手动","自动"};
			
			Iterator<Map<String, Object>> it = list.iterator();
			int index = 1;
			while (it.hasNext()) {
				Map<String, Object> map = it.next();
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(index, "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("agent_name"), "无"))); //代理商名称
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_no"), "无"))); //商户编号
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_name"), "无"))); //商户全称
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_short_name"), "无"))); //商户简称
				String pos_type = "无";
				if(map.get("pos_type") != null && !"".equals(map.get("pos_type").toString())){
					pos_type = posTypeName(map.get("pos_type").toString());
				}
				ws.addCell(new Label(col++, row, pos_type)); //设备类型
				if(map.get("open_status") == null || String.valueOf(map.get("open_status")) == ""){
					ws.addCell(new Label(col++, row, "无"));
				} else {
					ws.addCell(new Label(col++, row, open_statuss[Integer.parseInt(String.valueOf(map.get("open_status")))]));
				}
				String real_flag = "";//是否实名
				String my_settle = "";//是否优质
				String bag_settle = ""; //是否钱包结算
				String freeze_status = "";//是否冻结
				if(map.get("real_flag") != null && !"".equals(map.get("real_flag").toString())){
					if("0".equals(map.get("real_flag").toString())){
						real_flag = "否";
					}else if("1".equals(map.get("real_flag").toString())){
						real_flag = "是";
					}else{
						real_flag = map.get("real_flag").toString();
					}
				}
				
				if(map.get("my_settle") != null && !"".equals(map.get("my_settle").toString())){
					if("0".equals(map.get("my_settle").toString())){
						my_settle = "否";
					}else if("1".equals(map.get("my_settle").toString())){
						my_settle = "是";
					}else{
						my_settle = map.get("my_settle").toString();
					}
				}
				
				if(map.get("bag_settle") != null && !"".equals(map.get("bag_settle").toString())){
					if("0".equals(map.get("bag_settle").toString())){
						bag_settle = "否";
					}else if("1".equals(map.get("bag_settle").toString())){
						bag_settle = "是";
					}else{
						bag_settle = map.get("bag_settle").toString();
					}
				}
				
				if(map.get("freeze_status") != null && !"".equals(map.get("freeze_status").toString())){
					if("0".equals(map.get("freeze_status").toString())){
						freeze_status = "未冻结";
					}else if("1".equals(map.get("freeze_status").toString())){
						freeze_status = "已冻结";
					}else{
						freeze_status = map.get("freeze_status").toString();
					}
				}
				ws.addCell(new Label(col++, row, real_flag));//是否实名
				ws.addCell(new Label(col++, row, my_settle));//是否优质
				ws.addCell(new Label(col++, row, bag_settle));//是否钱包结算
				ws.addCell(new Label(col++, row, freeze_status));//是否冻结
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("province"), "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("city"), "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("sale_address"), "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("create_time"), "无")));
				System.out.println(map.get("open_type"));
				if(map.get("open_type") == null || "".equals(map.get("open_type").toString())){
					ws.addCell(new Label(col++, row, "无"));
				} else {
					ws.addCell(new Label(col++, row, open_types[Integer.parseInt(map.get("open_type").toString())]));
				}
				
				row++;
				index++;
				col = 0;
			}

			wwb.write();
			wwb.close();
			wb.close();
			os.close();
		}
		
		/**
		 * 手机充值查询
		 */
		@RequestMapping(value = "/mobileflowtrans")
		public String mobileflowtrans(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage) {
			
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String createTimeBegin = createTime + " 00:00";
				String createTimeEnd = createTime + " 23:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}

			Page<Map<String, Object>> list = merchantService.getFlowRechargeList(params, page);

			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);

			return "/merchant/mobileflowtrans";
		}
		
		/**
		 * 手机流量充值查询详情
		 */
		@RequestMapping(value = "/flowRechargeDetail")
		public String flowRechargeDetail(final ModelMap model, @RequestParam Long id) {
			Map<String, Object> list = merchantService.getFlowRechargeDetail(id);
			model.put("params", list);
			return "/merchant/flowRechargeDetail";
		}
		
		/**
		 * 修改商户代理商次月生效触发方法
		 * @author 袁鹏
		 * */
		public void enableAgentChange(){
			log.info("==================修改商户代理商次月生效触发方法开始====================");
			
			merchantService.enableAgentChange();
			
			log.info("==================修改商户代理商次月生效触发方法结束====================");
		}
		
		/**
		 * 商户额度提升审核
		 * @author 袁鹏
		 * */
		@RequestMapping(value = "liftQuery")
		public String liftQuery(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage){
			
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			Page<Map<String, Object>> list = merchantService.queryLiftMerchant(params, page);
			List<Map<String, Object>> checkPerson = merchantService.getCheckerListForTag2();
			model.put("params", params);
			model.put("p", cpage);
			model.put("list", list);
			model.put("checkPerson", checkPerson);
			return "/merchant/merchantLiftQuery";
		}
		
		/**
		 * 商户额度提升详情
		 * @author 袁鹏
		 * */
		@RequestMapping(value = "liftDetail")
		public String liftDetail(final ModelMap model, @RequestParam Long id){
			
			Map<String, Object> map = merchantService.queryMerchantInfoById(id);
			// 获得一级代理商编号进行显示
			String find_agent_no = (String) map.get("agent_no");
			
			Map<String, Object> amp = null;
			try {
				amp = merchantService.findSaleNameByAgentNo(find_agent_no);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (amp != null && !"".equals(amp)) {
				map.put("self_sale_name", amp.get("self_sale_name"));
			}

			Object fee_rate = map.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				String feeRate = ((BigDecimal) map.get("fee_rate")).multiply(
						new BigDecimal("100")).setScale(4, RoundingMode.HALF_UP)
						+ "";
				map.put("fee_rate", feeRate);
			}

			Object ladder_fee = map.get("ladder_fee");
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];

					map.put("ladder_min",
							new BigDecimal(ladder_min).movePointRight(2));
					map.put("ladder_value", ladder_value);
					map.put("ladder_max",
							new BigDecimal(ladder_max).movePointRight(2));
				}
			}
			String addType = map.get("add_type").toString();
			if ("1".equals(addType)) {
				String attachments = map.get("attachment").toString();
				String[] attachment = attachments.split(",");
				List<String> picList = new ArrayList<String>();
				for (int i = 0; i < attachment.length; i++) {
					String pic = attachment[i];
					picList.add(pic);
				}
				model.put("picList", picList);
			}
			// 获取集群信息
			List<Map<String, Object>> glist = groupMerchantService.getGroupInfoList();
			model.put("glist", glist);
			// 通过商户编号和激活码中的使用者相关联查询激活码
			String merchant_no = map.get("merchant_no").toString();
			Map<String, Object> list = null;
			try {
				list = merchantService.codeVerification(merchant_no);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (list != null) {
				map.put("keycode", list.get("keycode"));
			} else {
				map.put("keycode", "");
			}
			model.put("params", map);
			
			return "/merchant/merchantLiftDetail";
		}
		
		@RequestMapping(value = "liftDetailSubmit")
		public String liftDetailSubmit(final ModelMap model, @RequestParam Map<String, String> params){
			log.info(" 1 MerchantController liftDetailSubmit start ...");
			try {
				String merchant_no = params.get("merchant_no");

				log.info(" 2 MerchantController liftPosMerchant start ...");
				merchantService.liftPosMerchant(merchant_no);
				
				if ("success".equals(params.get("examinationMark"))) {
					params.put("lift_status", "1");
					log.info(" 3 MerchantController liftPosMerchantTransRule start ...");
					merchantService.liftPosMerchantTransRule(merchant_no, params);
					
				} else {
					params.put("lift_status", "3");
					Map<String, Object> merchant = merchantService.getMerchantInfoByMerchantNo(merchant_no);
					// 发送短信通知商户
					String projectRunFlag = SysConfig.value("projectRunFlag");
					String phoneNumber = merchant.get("mobile_username").toString();
					String examination_opinions = params.get("examination_opinions");
					String content = "您提交的商户信息审核失败。审核意见:" + examination_opinions;
					String agentNo = merchant.get("agent_no").toString();
					if (!"test".equals(projectRunFlag)) {
						// 如果是移小宝发送审核失败原因
						String pos_type = params.get("pos_type");
						// 支付界不发送短信
						if (("3".equals(pos_type) || "4".equals(pos_type))	&& !"3124".equals(agentNo)) {
							String agentid = merchant.get("agentid").toString();
							String special_agent_zk = SysConfig.value("special_agent_zk");
							String special_agent_xzf = SysConfig.value("special_agent_xzf");
							String special_agent_cf = SysConfig.value("special_agent_cf");
							String special_agent_zl = SysConfig.value("special_agent_zl");
							String special_agent_qyb = SysConfig.value("special_agent_qyb");
							String special_agent_lb = SysConfig.value("special_agent_lb");
							String special_agent_wsy = SysConfig.value("special_agent_wsy");
							String special_agent_mjd = SysConfig.value("special_agent_mjd");
							String special_agent_xhtf = SysConfig.value("special_agent_xhtf");

							if (special_agent_zk.equals(agentid)) {
								Sms.sendMsgOem(phoneNumber, content, "[中宽支付]");
							} else if (special_agent_xzf.equals(agentid)) {
								Sms.sendMsgOem(phoneNumber, content, "[新支付]");
							} else if (special_agent_cf.equals(agentid)) {
								Sms.sendMsgOem(phoneNumber, content, "[诚付天下]");
							} else if (special_agent_zl.equals(agentid) 	|| special_agent_qyb.equals(agentid)	|| special_agent_lb.equals(agentid)
									|| special_agent_wsy.equals(agentid)
									|| special_agent_mjd.equals(agentid)
									|| special_agent_xhtf.equals(agentid)) {
								Sms.sendMsgOem(phoneNumber, content, "[支付随心]");
							} else {
								Sms.sendMsg(phoneNumber, content);
							}
						}
					}
				}
				
				merchantService.insertLiftLog(merchant_no, getUser().getUserName(),params);
				log.info(" 4 MerchantController insertLiftLog start ...");
			} catch (Exception e) {
				log.error("Exception MerchantController liftDetailSubmit =  " + e);
				return "redirect:" + "/mer/liftQuery";
			}
			return "redirect:" + "/mer/liftQuery";
		}
		
		/**
		 * 增值业务收款报表模版
		 * */
		@RequestMapping(value = "/incrementExport/{trans_type}")
		public void incrementExport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request,@PathVariable("trans_type") String trans_type){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);

			String trans_type_cn = "增值业务";
			String trans_filed = "mobile";
			String trans_itf = "增值业务接口";
			String trans_itf_merchant_no = "10000000";
			
			if("mobile".equals(trans_type)){
				trans_type_cn = "手机充值";
				trans_filed = "mobile";
				trans_itf = "年年卡手机话费充值";
				trans_itf_merchant_no = SysConfig.value("ka_merid");
			}else if("dataflow".equals(trans_type)){
				trans_type_cn = "手机流量充值";
				trans_filed = "mobile";
				trans_itf = "年年卡手机流量充值";
				trans_itf_merchant_no = SysConfig.value("ka_flow_merid");
			}else if("credit".equals(trans_type)){
				trans_type_cn = "信用卡还款";
				trans_filed = "credit_no";
				trans_itf = "银联公共缴费";
				trans_itf_merchant_no = SysConfig.value("yl_cid");
			}else if("traffic".equals(trans_type)){
				trans_type_cn = "车辆缴费";
				trans_filed = "carnumber";
				trans_itf = "车行易";
				trans_itf_merchant_no = SysConfig.value("userName_cheXingYi");
			}
			
			params.put("trans_type", trans_type);
			params.put("trans_type_cn", trans_type_cn);
			params.put("trans_filed", trans_filed);
			params.put("trans_itf", trans_itf);
			params.put("trans_itf_merchant_no", trans_itf_merchant_no);
			
			String fileName = trans_type_cn + sdf.format(new Date()) + "_" + random + ".xls"; 

			OutputStream os = null;
			try {
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				incrementExport(os, params, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private void incrementExport(OutputStream os, Map<String, String> params, String fileName) throws Exception {
			int row = 1; // 从第二行开始写
			int col = 0; // 从第一列开始写

			PageRequest page = new PageRequest(0, 29999);
			
			if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime + " 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}

			Page<Map<String, Object>> list = merchantService.getIncrementList(params, page);	

			Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/increment.xls"));

			WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
			WritableSheet ws = wwb.getSheet(0);

			Iterator<Map<String, Object>> it = list.iterator();
			int index = 1;
			while (it.hasNext()) {
				Map<String, Object> map = it.next();
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("agent_name"), "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_cnname"), "无")));
				ws.addCell(new Label(col++, row, "移付宝"));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("order_no"), "无")));
				
				String trans_status = (String) map.get("trans_status");
				if("INIT".equals(trans_status)){
					trans_status = "初始化";
				}else if("FAILED".equals(trans_status)){
					trans_status = "已失败";
				}else if("SUCCESS".equals(trans_status)){
					trans_status = "已成功";
				}else if("SENDORDER".equals(trans_status)){
					trans_status = "已下单";
				}else{
					trans_status = "无";
				}
				ws.addCell(new Label(col++, row, trans_status));
				
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_name"), "无")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_no"), "无")));
				ws.addCell(new Label(col++, row, params.get("trans_itf")));
				ws.addCell(new Label(col++, row, params.get("trans_itf_merchant_no")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("pay_order_no"), "无")));
				
				// 判断增值业务类型
				String trans_type = params.get("trans_type");
				if("dataflow".equals(trans_type)){
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("price"), "无")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("amount"), "无") +  "M"));
				}else{
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("amount"), "无")));
					ws.addCell(new Label(col++, row, "无"));
				}
				
				
				String status = (String) map.get("status");
				if("INIT".equals(status)){
					status = "初始化";
				}else if("FAILED".equals(status)){
					status = "已失败";
				}else if("SUCCESS".equals(status)){
					status = "已成功";
				}else if("SENDORDER".equals(status)){
					status = "已下单";
				}else{
					status = "无";
				}
				ws.addCell(new Label(col++, row, status));
				
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("create_time"), "无")));
				
				ws.addCell(new Label(col++, row, params.get("trans_type_cn")));
				ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get(params.get("trans_filed")), "无")));
				
				row++;
				index++;
				col = 0;
			}

			wwb.write();
			wwb.close();
			wb.close();
			os.close();
		}
		
		/**
		 * 将已冻结的状态改为成功的状态的定时任务
		 * @author lzj
		 * */
		public void freezeStatusChange(){
			log.info("-----------将已冻结的状态改为成功的状态方法start-------------");
			try {
				Date date = new Date();  
		        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");  
		        String nowDate = sf.format(date);
		        String nowTime = "00:10:30";  
				merchantService.freezeStatusChange(nowDate,nowTime);
			} catch (SQLException e) {
				e.printStackTrace(); 
			}
			log.info("-----------将已冻结的状态改为成功的状态方法end-------------");
		}
		
		/**
		 * 将已冻结的状态改为成功的状态的定时任务
		 * @author lzj
		 * */
		public void freezeStatusChangeNew(){
			log.info("-----------将已冻结的状态改为成功的状态方法start-------------");
			try {
				Date date = new Date();  
		        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");  
		        String nowDate = sf.format(date);
		        String nowTime = "00:10:30";  
				merchantService.freezeStatusChangeNew(nowDate,nowTime);
			} catch (SQLException e) {
				e.printStackTrace(); 
			}
			log.info("-----------将已冻结的状态改为成功的状态方法end-------------");
		}
		
		
		//冻结（POS||移小宝）弹窗
		@RequestMapping(value = "/transFreeze")
		public String transFreeze(final ModelMap model, @RequestParam Long id) {
			log.info("--------transFreeze------start-----");
			Map<String, Object> params = new HashMap<String, Object>();

			Map<String, Object> transInfoMap = transService.queryTransInfoById(id);

			if (TransStatus.SUCCESS.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已成功");
			} else if (TransStatus.FAILED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已失败");
			} else if (TransStatus.INIT.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "初始化");
			} else if (TransStatus.REVOKED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已撤销");
			} else if (TransStatus.REFUND.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已退货");
			} else if (TransStatus.REVERSED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已冲正");
			}else if (TransStatus.FREEZED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已冻结");
			}

			if (TransType.PURCHASE.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "消费");
			} else if (TransType.PURCHASE_VOID.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "消费撤销");
			} else if (TransType.PURCHASE_REFUND.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "退货");
			} else if (TransType.REVERSED.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "冲正");
			} else if (TransType.BALANCE_QUERY.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "余额查询");
			}

			if (CurrencyType.CNY.toString().equals(
					(String) transInfoMap.get("currency_type"))) {
				transInfoMap.put("currency_type", "人民币");
			}

			Map<String, Object> merchantInfoMap = merchantService
					.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));

			Map<String, Object> acqMerchantInfoMap = acqMerchantService
					.queryAcqMerchantInfo((String) transInfoMap
							.get("acq_merchant_no"));

			CardBin cardBin = bankCardService.cardBin((String) transInfoMap
					.get("account_no"));

			String accountNo = (String) transInfoMap.get("account_no");
			String card_no = accountNo.substring(0, 6)
					+ "*****"
					+ accountNo.substring(accountNo.length() - 4,
							accountNo.length());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.#");
			Date create_time = (Date) (transInfoMap.get("create_time"));

			// String transId =
			// sdf.format(create_time)+StringUtil.stringFillLeftZero(String.valueOf(id),
			// 6);
			String transId = OrderUtil.buildOrderId(id, create_time);
			params.put("trans_id", transId);
			params.put("card_no", card_no);
			params.put("bank_name", cardBin.getBankName());
			params.put("card_type", cardBin.getCardType());
			params.put("card_name", cardBin.getCardName());

			params.putAll(transInfoMap);
			params.putAll(merchantInfoMap);
			if (acqMerchantInfoMap != null) {
				params.putAll(acqMerchantInfoMap);
			}

			model.put("params", params);

			return "/merchant/merchantTransFreeze";
		}

		
		
		//钱包交易冻结（POS||移小宝）弹窗
		@RequestMapping(value = "/bagTransFreeze")
		public String bagTransFreeze(final ModelMap model, @RequestParam Long id) {
			log.info("--------bagtransFreeze------start-----");
			Map<String, Object> params = new HashMap<String, Object>();

			Map<String, Object> transInfoMap = transService.queryTransInfoById(id);

			if (TransStatus.SUCCESS.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已成功");
			} else if (TransStatus.FAILED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已失败");
			} else if (TransStatus.INIT.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "初始化");
			} else if (TransStatus.REVOKED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已撤销");
			} else if (TransStatus.REFUND.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已退货");
			} else if (TransStatus.REVERSED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已冲正");
			}else if (TransStatus.FREEZED.toString().equals(
					(String) transInfoMap.get("trans_status"))) {
				transInfoMap.put("trans_status", "已冻结");
			}

			if (TransType.PURCHASE.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "消费");
			} else if (TransType.PURCHASE_VOID.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "消费撤销");
			} else if (TransType.PURCHASE_REFUND.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "退货");
			} else if (TransType.REVERSED.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "冲正");
			} else if (TransType.BALANCE_QUERY.toString().equals(
					(String) transInfoMap.get("trans_type"))) {
				transInfoMap.put("trans_type", "余额查询");
			}

			if (CurrencyType.CNY.toString().equals(
					(String) transInfoMap.get("currency_type"))) {
				transInfoMap.put("currency_type", "人民币");
			}

			Map<String, Object> merchantInfoMap = merchantService
					.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));

			Map<String, Object> acqMerchantInfoMap = acqMerchantService
					.queryAcqMerchantInfo((String) transInfoMap
							.get("acq_merchant_no"));

			CardBin cardBin = bankCardService.cardBin((String) transInfoMap
					.get("account_no"));

			String accountNo = (String) transInfoMap.get("account_no");
			String card_no = accountNo.substring(0, 6)
					+ "*****"
					+ accountNo.substring(accountNo.length() - 4,
							accountNo.length());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.#");
			Date create_time = (Date) (transInfoMap.get("create_time"));

			// String transId =
			// sdf.format(create_time)+StringUtil.stringFillLeftZero(String.valueOf(id),
			// 6);
			String transId = OrderUtil.buildOrderId(id, create_time);
			params.put("trans_id", transId);
			params.put("card_no", card_no);
			params.put("bank_name", cardBin.getBankName());
			params.put("card_type", cardBin.getCardType());
			params.put("card_name", cardBin.getCardName());

			params.putAll(transInfoMap);
			params.putAll(merchantInfoMap);
			if (acqMerchantInfoMap != null) {
				params.putAll(acqMerchantInfoMap);
			}

			model.put("params", params);

			return "/merchant/bagTransFreeze";
		}
		
			// 快捷交易冻结
			@RequestMapping(value = "/fastTransFreeze")
			public String fastTransFreeze(final ModelMap model, @RequestParam Long id) {

				Map<String, Object> params = new HashMap<String, Object>();

				Map<String, Object> transInfoMap = transService.queryFastTransInfoById(id);

		
				if (TransType.PURCHASE.toString().equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "消费");
				} else if ("BAG".equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "钱包充值");
				} else if ("MOBILE".equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "手机充值");
				} 

				CardBin cardBin = bankCardService.cardBin((String) transInfoMap
						.get("card_no"));

				params.put("bank_name", cardBin.getBankName());
				params.put("card_type", cardBin.getCardType());
				params.put("card_name", cardBin.getCardName());

				params.putAll(transInfoMap);
				model.put("params", params);

				return "/merchant/fastTransFreeze";
			}
			
		// 钱包快捷交易冻结时  加载信息
		@RequestMapping(value = "/bagFastTransFreeze")
		public String bagFastTransFreeze(final ModelMap model, @RequestParam Long id) {

			Map<String, Object> params = new HashMap<String, Object>();

			Map<String, Object> transInfoMap = transService.queryFastTransInfoById(id);

	
			if (TransType.PURCHASE.toString().equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "消费");
			} else if ("BAG".equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "钱包充值");
			} else if ("MOBILE".equals(
					(String) transInfoMap.get("biz_name"))) {
				transInfoMap.put("biz_name", "手机充值");
			} 

			CardBin cardBin = bankCardService.cardBin((String) transInfoMap
					.get("card_no"));

			params.put("bank_name", cardBin.getBankName());
			params.put("card_type", cardBin.getCardType());
			params.put("card_name", cardBin.getCardName());

			params.putAll(transInfoMap);
			model.put("params", params);

			return "/merchant/bagFastTransFreeze";
		}
		
//		//冻结交易
//		@RequestMapping(value = "/freezeTrans")
//		public void freezeTrans(HttpServletResponse response,@RequestParam Map<String, String> params) {
//			log.info("----冻结交易-----/freezeTrans----start----");
//			
//			if(params != null){
//				params.put("user",getUser().getUserName());
//				try {
//					log.info("----冻结交易Contrller--/freezeTrans---调用service开始---");
//					transService.updateTransInfoFreezed(params);
//					log.info("----冻结交易Contrller--/freezeTrans---调用service结束---");
//					outText("true", response);
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//					outText("false",response);
//				}
//			}
//			
//		}
//		
//		
		//冻结交易
		@RequestMapping(value = "/freezeTransNew")
		public void freezeTransNew(HttpServletResponse response,@RequestParam Map<String, String> params) {
			log.info("----冻结交易-----/freezeTrans----start----");
			
			if(params != null){
				params.put("user",getUser().getRealName());
				params.put("userId",getUser().getId()+"");
				try {
					log.info("----冻结交易Contrller--/freezeTrans---调用service开始---");
					transService.updateTransInfoFreezedNew(params);
					log.info("----冻结交易Contrller--/freezeTrans---调用service结束---");
					outText("true", response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					outText("false",response);
				}
			}
			
		}
		
//		//钱包交易冻结
//		@RequestMapping(value = "/bagFreezeTrans")
//		public void bagFreezeTrans(HttpServletResponse response,@RequestParam Map<String, String> params) {
//			log.info("----钱包冻结交易----/bagFreezeTrans----start----");
//			
//			if(params != null){
//				params.put("user",getUser().getUserName());
//				params.put("freezeDay", "0");
//				String channel = "";
//				String channelId = "";
//				if(!StringUtil.isBlank(params.get("fastId"))){
//					channel = "0";
//					channelId = params.get("fastId");
//				}else{
//					channel = "1";
//					channelId = params.get("id");
//				}
//				
//				try {
//					log.info("----钱包冻结交易Contrller--/bagFreezeTrans---调用service开始---");
//					
//						Map<String, Object> posMer = transService.getPosMerchant(params.get("merNo"));
//						String BAG_HMAC = Constants.BAG_HMAC;
//						String hmac = Md5.md5Str(posMer.get("mobile_username").toString()
//								+posMer.get("app_no").toString()+params.get("amount")+channel+channelId+"0"+BAG_HMAC);
//						Map<String, String> bagParam = new HashMap<String, String>();
//						bagParam.put("mobileNo", posMer.get("mobile_username").toString());
//						bagParam.put("appType", posMer.get("app_no").toString());
//						bagParam.put("amount", params.get("amount"));
//						bagParam.put("channel", channel);
//						bagParam.put("channelId",channelId);
//						bagParam.put("hmac", hmac);
//						bagParam.put("msg", params.get("freezeReson"));
//						bagParam.put("operation", "0");
//						
//						String retXml =  Http2.send(SysConfig.value("bagTransFreeze"), bagParam, "UTF-8");
//						
//						System.out.println(retXml);
//						Map<String, String> listMap = XmlUtil.parseXmlStrList(retXml);
//						String msg = listMap.get("msg");
//						String success = listMap.get("success");
//						if(success.equals("true")){
//							transService.updateTransInfoFreezed(params);
//						}
//						
//					
//					log.info("----冻结交易Contrller--/bagFreezeTrans---调用service结束---");
//					outText(msg, response);
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//					outText("系统异常。。。",response);
//				}
//			}
//			
//		}
//		
		
		
		
		//钱包交易冻结
		@RequestMapping(value = "/bagFreezeTransNew")
		public void bagFreezeTransNew(HttpServletResponse response,@RequestParam Map<String, String> params) {
			log.info("----钱包冻结交易----/bagFreezeTransNew----start----");
			
			if(params != null){
				params.put("user",getUser().getRealName());
				params.put("userId",getUser().getId()+"");
				params.put("freezeDay", "0");
				String channel = "";
				String channelId = "";
				if(!StringUtil.isBlank(params.get("fastId"))){
					channel = "0";
					channelId = params.get("fastId");
				}else{
					channel = "1";
					channelId = params.get("id");
				}
				
				try {
					log.info("----钱包冻结交易Contrller--/bagFreezeTrans---调用service开始---");
					
						Map<String, Object> posMer = transService.getPosMerchant(params.get("merNo"));
						String BAG_HMAC = Constants.BAG_HMAC;
						String hmac = Md5.md5Str(posMer.get("mobile_username").toString()
								+posMer.get("app_no").toString()+params.get("amount")+channel+channelId+"0"+getUser().getRealName()+BAG_HMAC);
						Map<String, String> bagParam = new HashMap<String, String>();
						bagParam.put("mobileNo", posMer.get("mobile_username").toString());
						bagParam.put("appType", posMer.get("app_no").toString());
						bagParam.put("amount", params.get("amount"));
						bagParam.put("channel", channel);
						bagParam.put("channelId",channelId);
						bagParam.put("hmac", hmac);
						bagParam.put("msg", params.get("freezeReson"));
						bagParam.put("operation", "0");
						bagParam.put("operater", getUser().getRealName());
						
						String retXml =  Http2.send(SysConfig.value("bagTransFreeze"), bagParam, "UTF-8");
						
						System.out.println(retXml);
						Map<String, String> listMap = XmlUtil.parseXmlStrList(retXml);
						String msg = listMap.get("msg");
						String success = listMap.get("success");
						if(success.equals("true")){
							transService.updateTransInfoFreezedNew(params);
						}
						
					
					log.info("----冻结交易Contrller--/bagFreezeTrans---调用service结束---");
					outText(msg, response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					outText("系统异常。。。",response);
				}
			}
			
		}
		
//		//解冻
//		@RequestMapping(value = "/transUnFreeze")
//		public void transUnFreeze(HttpServletResponse response,@RequestParam Map<String, String> params) {
//			log.info("----交易解冻controller----freezeTrans----start----");
//			
//			if(params != null){
//				params.put("user",getUser().getRealName());
//				params.put("userId",getUser().getId()+"");
//				try {
//					
//					log.info("----交易解冻controller----/freezeTrans----调用service开始----");
//					transService.updateTransInfoUnFreezed(params);
//					log.info("----交易解冻controller----/freezeTrans----调用service结束----");
//					outText("true", response);
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//					outText("false",response);
//				}
//			}
//		}
		
		
		
		//解冻
		@RequestMapping(value = "/transUnFreezeNew")
		public void transUnFreezeNew(HttpServletResponse response,@RequestParam Map<String, String> params) {
			log.info("----交易解冻controller----freezeTrans----start----");
			
			if(params != null){
				params.put("user",getUser().getRealName());
				params.put("userId",getUser().getId()+"");
				try {
					
					log.info("----交易解冻controller----/freezeTrans----调用service开始----");
					transService.updateTransInfoUnFreezedNew(params);
					log.info("----交易解冻controller----/freezeTrans----调用service结束----");
					outText("true", response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					outText("false",response);
				}
			}
		}

//		//钱包解冻
//		@RequestMapping(value = "/bagTransUnFreeze")
//		public void bagTransUnFreeze(HttpServletResponse response,@RequestParam Map<String, String> params) {
//			log.info("----交易解冻controller----freezeTrans----start----");
//			
//			if(params != null){
//				params.put("user",getUser().getUserName());
//				String channel = "";
//				String channelId = "";
//				String operation = "1";
//				if(!StringUtil.isBlank(params.get("fastId"))){
//					channel = "0";
//					channelId = params.get("fastId");
//				}else{
//					channel = "1";
//					channelId = params.get("id");
//				}
//				try {
//					
//
//					Map<String, Object> posMer = transService.getPosMerchant(params.get("merNo"));
//					String BAG_HMAC = Constants.BAG_HMAC;
//					String hmac = Md5.md5Str(posMer.get("mobile_username").toString()
//							+posMer.get("app_no").toString()+params.get("amount")+channel+channelId+operation+BAG_HMAC);
//					Map<String, String> bagParam = new HashMap<String, String>();
//					bagParam.put("mobileNo", posMer.get("mobile_username").toString());
//					bagParam.put("appType", posMer.get("app_no").toString());
//					bagParam.put("amount", params.get("amount"));
//					bagParam.put("channel", channel);
//					bagParam.put("channelId",channelId);
//					bagParam.put("hmac", hmac);
//					bagParam.put("operation", operation);
//					String retXml =  Http2.send(SysConfig.value("bagTransFreeze"), bagParam, "UTF-8");
//					
//					System.out.println(retXml);
//					Map<String, String> listMap = XmlUtil.parseXmlStrList(retXml);
//					String msg = listMap.get("msg");
//					String success = listMap.get("success");
//					if(success.equals("true")){
//						log.info("----交易解冻controller----/freezeTrans----调用service开始----");
//						transService.updateTransInfoUnFreezed(params);
//						log.info("----交易解冻controller----/freezeTrans----调用service结束----");
//							
//					}
//					outText(msg, response);
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//					outText("系统异常 。。 稍后再试",response);
//				}
//			}
//		}

		
		//钱包解冻
		@RequestMapping(value = "/bagTransUnFreezeNew")
		public void bagTransUnFreezeNew(HttpServletResponse response,@RequestParam Map<String, String> params) {
			log.info("----交易解冻controller----freezeTrans----start----");
			
			if(params != null){
				params.put("user",getUser().getRealName());
				params.put("userId",getUser().getId()+"");
				String channel = "";
				String channelId = "";
				String operation = "1";
				if(!StringUtil.isBlank(params.get("fastId"))){
					channel = "0";
					channelId = params.get("fastId");
				}else{
					channel = "1";
					channelId = params.get("id");
				}
				try {
					

					Map<String, Object> posMer = transService.getPosMerchant(params.get("merNo"));
					String BAG_HMAC = Constants.BAG_HMAC;
					String hmac = Md5.md5Str(posMer.get("mobile_username").toString()
							+posMer.get("app_no").toString()+params.get("amount")+channel+channelId+operation+getUser().getRealName()+BAG_HMAC);
					Map<String, String> bagParam = new HashMap<String, String>();
					bagParam.put("mobileNo", posMer.get("mobile_username").toString());
					bagParam.put("appType", posMer.get("app_no").toString());
					bagParam.put("amount", params.get("amount"));
					bagParam.put("channel", channel);
					bagParam.put("channelId",channelId);
					bagParam.put("hmac", hmac);
					bagParam.put("operation", operation);
					bagParam.put("operater", getUser().getRealName());
					String retXml =  Http2.send(SysConfig.value("bagTransFreeze"), bagParam, "UTF-8");
					
					System.out.println(retXml);
					Map<String, String> listMap = XmlUtil.parseXmlStrList(retXml);
					String msg = listMap.get("msg");
					String success = listMap.get("success");
					if(success.equals("true")){
						log.info("----交易解冻controller----/freezeTrans----调用service开始----");
						transService.updateTransInfoUnFreezedNew(params);
						log.info("----交易解冻controller----/freezeTrans----调用service结束----");
							
					}
					outText(msg, response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					outText("系统异常 。。 稍后再试",response);
				}
			}
		}
		//解冻时加载冻结人 冻结时间
		@RequestMapping(value = "/freezedInfo")
		public
		@ResponseBody
		Map<String, Object> freezedInfo(@RequestParam Map<String, String> params){
			log.info("==========加载冻结信息开始======freezedInfo---start---");
			Map<String, Object> map = transService.freezedInfo(params);
			log.info("==========加载冻结信息开始======freezedInfo---end---");
			return map;
		}
		
		//解冻时加载冻结人 冻结时间
		@RequestMapping(value = "/freezedInfoNew")
		public
		@ResponseBody
		Map<String, Object> freezedInfoNew(@RequestParam Map<String, String> params){
			log.info("==========加载冻结信息开始======freezedInfo---start---");
			Map<String, Object> map = transService.freezedInfoNew(params);
			log.info("==========加载冻结信息开始======freezedInfo---end---");
			return map;
		}
		

		@RequestMapping(value = "/merCheckStateExport")
		public void merCheckStateExport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);

			String fileName = "审核进度" + sdf.format(new Date()) + "_" + random + ".xls"; 
			OutputStream os = null;
			try {
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型

				int row = 1; // 从第二行开始写
				int col = 0; // 从第一列开始写

				PageRequest page = new PageRequest(0, 3000);
				
				if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
					Date date = new Date();
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					String createTime = sdf1.format(date);
					String createTimeBegin = createTime + " 00:00:00";
					String createTimeEnd = createTime + " 23:59:59";
					params.put("createTimeBegin", createTimeBegin);
					params.put("createTimeEnd", createTimeEnd);
				}

				Page<Map<String, Object>> list = merchantService.auditingQueryPage(params, page);	
				List<Map<String, Object>> remain = merchantService.auditingRemainQuery();
				
				//手动插入审核剩余数
				for(Map<String, Object> map : list.getContent()){
					for(Map<String, Object> mapRemain : remain){
						String pos_type = map.get("pos_type") != null ? map.get("pos_type").toString() : "";
						String real_name = map.get("real_name") != null ? map.get("real_name").toString() : "";
						String pos_type1 = mapRemain.get("pos_type") != null ? mapRemain.get("pos_type").toString() : "";
						String checker = mapRemain.get("checker") != null ? mapRemain.get("checker").toString() : "";
						
						if(pos_type.equals(pos_type1) && real_name.equals(checker)){
							map.put("remain", mapRemain.get("remain"));
							continue;
						}
					}
				}
				
				Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/merCheckStateExport.xls"));

				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);

				Iterator<Map<String, Object>> it = list.iterator();
				int index = 1;
				while (it.hasNext()) {
					Map<String, Object> map = it.next();
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(index, "无")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("real_name"), "无")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("pos_type_name"), "无")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("checked"), "0")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("remain"), "0")));
					ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("last_check_time"), "无")));
					
					row++;
					index++;
					col = 0;
				}

				wwb.write();
				wwb.close();
				wb.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		
		/**
		 * 额度修改查询
		 * @param model
		 * @param params
		 * @param cpage
		 * @return
		 */
		@RequestMapping(value = "/limitChangeQuery")
		public String limitChangeQuery(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage) {

			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

			if (StringUtil.isEmpty(params.get("create_time_begin")) && StringUtil.isEmpty(params.get("create_time_end"))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				params.put("create_time_begin", createTime);
				params.put("create_time_end", createTime);
			}
			
			if(!StringUtil.isEmpty(params.get("create_time_begin"))){
				String startTime = params.get("create_time_begin").toString();
				startTime += " 00:00:00";
				params.put("create_time_begin", startTime);
			}
			
			if(!StringUtil.isEmpty(params.get("create_time_end"))){
				String end_time = params.get("create_time_end").toString();
				end_time += " 23:59:59";
				params.put("create_time_end", end_time);
			}
			
			Page<Map<String, Object>> list = merchantService.getLimitChangeList(params, page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			
			return "/merchant/limitChangeQuery";
		}
		
		/**
		 * 限额修改初始化
		 * @return
		 */
		@RequestMapping(value = "/limitChangeLoad")
		public String limitChangeLoad(){
			return "/merchant/limitChangeLoad";
		}
		
		/**
		 * 限额修改添加
		 * @return
		 */
		@RequestMapping(value = "/limitChangeCreate")
		public String limitChangeCreate(final ModelMap model,	@RequestParam Map<String, String> params){
			
			merchantService.limitChangeCreate(params);
			return "redirect:" + "/mer/limitChangeQuery";
		}
		
		/**
		 * 限额修改详情
		 * @return
		 */
		@RequestMapping(value = "/limitChangeDetail")
		public String limitChangeDetail(final ModelMap model, @RequestParam Long id){
			
			Map<String, Object> map = merchantService.getLimitChangeById(id);
			model.put("params", map);
			return "/merchant/limitChangeDetail";
		}

		
		
		// 快捷交易详情
		@RequestMapping(value = "/fastAirOrderDetail")
		public String fastAirOrderDetail(final ModelMap model, @RequestParam Long id) {

			Map<String, Object> airOrderMap = null ;
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, Object> transInfoMap = transService.queryFastTransInfoById(id);
			
			Date dd = new Date();
			String yearStr = DateUtils.format(DateUtils.addDate(dd,1),"yyyy-MM-dd");
			String amount = transInfoMap.get("amount").toString();
			BigDecimal amountbg = new BigDecimal(amount);
			if(transInfoMap.get("air_id")==null || "".equals(transInfoMap.get("air_id").toString())){
				airOrderMap = transService.findFastAirOrderByAmount(amountbg);
			}else{
				airOrderMap = transService.findFastAirOrderById(transInfoMap.get("air_id").toString());
			}
			if(airOrderMap!=null){
				String airFlyTime = airOrderMap.get("flytime").toString();
				String airTime = yearStr+" "+airFlyTime.split("-")[0];
				params.put("airTime", airTime);
				params.putAll(airOrderMap);
			}
			
			CardBin cardBin = bankCardService.cardBin((String) transInfoMap
					.get("card_no"));
			params.put("bank_name", cardBin.getBankName());
			params.put("card_type", cardBin.getCardType());
			params.put("card_name", cardBin.getCardName());
			params.putAll(transInfoMap);
			model.put("params", params);

			return "/merchant/fastAirOrderDetail";
		}

		
		/**
		 * 实名认证
		 */
		@RequestMapping(value="/realNameAuthentication")
		public void realNameAuthentication(@RequestParam Long id,HttpServletResponse response){
			
			Map<String, Object> map = merchantService.queryMerchantInfoById(id);
		    
			String modulus = SysConfig.value("realNameAuthenticationModulus1") + SysConfig.value("realNameAuthenticationModulus2") + SysConfig.value("realNameAuthenticationModulus3");
			String exponent = SysConfig.value("realNameAuthenticationExponent");
			String secret_key = SysConfig.value("realNameAuthenticationSecretKey");
		    String merName = SysConfig.value("realNameAuthenticationMerName");
		    String merId = SysConfig.value("realNameAuthenticationMerId");
		    String url = SysConfig.value("realNameAuthenticationUrl");
		    log.info("===========modules:" + modulus + ",exponent:" + exponent + ",secret_key:" + secret_key + ",merName:" + merName + ",merId:" + merId + ",url:" + url + "===================");
		    
		    Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		    String orderTime = sdf.format(date);
		    
		    String orderNumber = createOrderNo();
		    String cardNum = map.get("account_no").toString();
		    String userName = map.get("account_name").toString();
		    String idCard = map.get("id_card_no").toString();
		    String phoneNum = map.get("mobile_username").toString();
		    
		    Map<String, String> treeMap = new TreeMap<String, String>();
		    treeMap.put("merName", merName);
		    treeMap.put("merId", merId);
		    treeMap.put("orderTime", orderTime);
		    treeMap.put("orderNumber", orderNumber);
		    treeMap.put("cardNum", cardNum);
		    treeMap.put("userName", userName);
		    treeMap.put("idCard", idCard);
		    treeMap.put("phoneNum", phoneNum);
		    
		    ErrorVO error=new ErrorVO();
		    try {
		    	log.info("=====================查询是否已经验证过=============================");
		    	Map<String, Object> record = merchantService.findRealNameAuthentication(treeMap);
		    	
		    	if(null != record && record.get("respCode") != null && !"".equals(record.get("respCode").toString())){
		    		String respCode = record.get("respCode").toString();
		    		
		    		error.setErrCode(respCode);
		    		if("00".equals(respCode)){
		    			error.setErrMsg("验证成功");
		    			log.info("===================已经验证过一次,并且是成功的====================");
		    		}else{
		    			error.setErrMsg("验证失败,错误码：" + respCode);
		    			log.info("===================已经验证过一次,但是是失败的====================");
		    		}
		    	}else{
		    		log.info("=====================没有验证过,插入实名查询记录开始=============================");
		    		log.info("=====================插入的数据:cardNum:" + cardNum + ",userName:" + userName + ",idCard:" + idCard + ",phoneNum:" + phoneNum + "=====================");
		    		int rows = merchantService.addRealNameAuthentication(treeMap);
					log.info("=====================没有验证过,插入实名查询记录结束=============================");
					
					log.info("=====================拼接报文并加密开始===============================");
					String searchUrl1 = "cardNum=" + cardNum + "&idCard=" + idCard + "&userName=" + userName + "&phoneNum=" + phoneNum;
					log.info("================RSA加密的报文 :" + searchUrl1 + "=====================");
					
					String paydata = Base64Utils.encode(RSAUtils.encryptByPublicKey(searchUrl1.getBytes("GBK"), modulus, exponent));
				    
					String searchUrl2 = "merId=" + merId + "&merName=" + merName + "&orderNumber=" + orderNumber + "&orderTime=" + orderTime + "&paydata=" + paydata;
				    
					String md5SecretKey = Md5.md5Str(secret_key, "GBK").toLowerCase();
				    
				    log.info("========================帐号加密的报文:" + searchUrl2 + " ==============================");
				    log.info("========================Md5 加密的Secret Key:" + md5SecretKey + " ==============================");
				    
				    String md5Signature = searchUrl2 + "&" + md5SecretKey;
				    log.info("========================Md5 加密的Signature:" + md5Signature + " ==============================");
					String signature = Md5.md5Str(md5Signature, "GBK").toLowerCase();
				    
				    String searchUrl = searchUrl2 + "&signature=" + signature;
				    log.info("=====================发送的请求报文参数" + searchUrl + "==============");
				    log.info("=====================拼接报文并加密结束===============================");
				    
				    log.info("=====================调用查询请求开始===============================");
				    String returnStr = new String(HttpsUtil.post(url, searchUrl, "GBK"));
				    log.info("=====================返回的字符串" + returnStr + "=================");
				    log.info("=====================调用查询请求结束===============================");

				    String respCode = "";
				    String respMsg = "";
				    if(StringUtils.isNotEmpty(returnStr) && returnStr.contains("respCode")){
				    	String[] strs = returnStr.split("&");
				    	respCode = strs[0].substring(strs[0].lastIndexOf("=") + 1);
				    	respMsg = strs[1].substring(strs[1].lastIndexOf("=") + 1);
				    }else{
				    	respCode = "-1";
				    	respMsg = returnStr;
				    }
				    log.info("=====================返回的字符串处理结果respCode:" + respCode + ",respMsg:" + respMsg + "=============================");
				    
				    error.setErrCode(respCode);
				    if("00".equals(respCode)){
				    	error.setErrMsg("验证成功");
				    }else{
				    	error.setErrMsg("验证失败,错误码：" + respCode);
				    }
				    log.info("=====================更新实名验证记录开始,orderNumber为"+orderNumber+"==========================");
				    merchantService.updateRealNameAuthentication(orderNumber, respCode, respMsg);
				    log.info("=====================更新实名验证记录结束==========================");
				    
		    	}
		    	PrintWriter pw = response.getWriter();
		    	pw.write(error.getErrMsg());
		    	pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		private String createOrderNo(){
			StringBuffer sb = new StringBuffer();
			Random r = new Random();
			for (int i = 0; i < 5; i++) {
				sb.append(r.nextInt(8999) + 1000);
			}
			String orderNo = System.currentTimeMillis() + sb.toString();
			return orderNo = orderNo.substring(15, orderNo.length());
		}
		
		
		/**
		 * 商户限额审核列表
		 * @param model
		 * @param params
		 * @param cpage
		 * @return
		 */
		@RequestMapping("/raiseCheckQuery")
		public String raiseCheckQuery(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage){
			
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

			if (StringUtil.isEmpty(params.get("create_time_begin")) && StringUtil.isEmpty(params.get("create_time_end"))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				params.put("create_time_begin", createTime + " 00:00:00");
				params.put("create_time_end", createTime + " 23:59:59");
			}

			Page<Map<String, Object>> list = merchantService.getRaiseCheckList(params, page);
			List<Map<String, Object>> checker = merchantService.getCheckerListForTag2();
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			model.put("checker", checker);
			
			return "/merchant/raiseCheckQuery";
		}
		
		/**
		 * 商户限额审核详情
		 * @param model
		 * @param id
		 * @return
		 */
		@RequestMapping(value="/raiseCheckDetail")
		public String raiseCheckDetail(final ModelMap model, @RequestParam Long id){
			
			Map<String, Object> raiseMap = merchantService.getMerchantRaise4CheckById(id);
			String merchantNo = raiseMap.get("merchant_no").toString();
			String agentNo = raiseMap.get("agent_no").toString();
			
			Map<String, Object> merchantMap = merchantService.queryMerchantInfoByMerchantNo(merchantNo);
			raiseMap.putAll(merchantMap);
			
			Map<String, Object> agentMap = null;
			try {
				agentMap = merchantService.findSaleNameByAgentNo(agentNo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (agentMap != null) {
				raiseMap.put("self_sale_name", raiseMap.get("self_sale_name"));
			}

			Object fee_rate = merchantMap.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				String feeRate = ((BigDecimal) merchantMap.get("fee_rate")).multiply(new BigDecimal("100")).setScale(4, RoundingMode.HALF_UP) + "";
				raiseMap.put("fee_rate", feeRate);
			}

			Object ladder_fee = merchantMap.get("ladder_fee");
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];

					raiseMap.put("ladder_min", new BigDecimal(ladder_min).movePointRight(2));
					raiseMap.put("ladder_value", ladder_value);
					raiseMap.put("ladder_max", new BigDecimal(ladder_max).movePointRight(2));
				}
			}
			
			String merchantRaiseCategory = SysConfig.value("merchantRaiseCategory");
			
			String[] categories = merchantRaiseCategory.split(",");
			
			List<Map<String, Object>> raiseDetails =  merchantService.getRaiseCheckDetailList(id);
			
			List<Map<String, Object>> opinions = merchantService.getRaiseCheckOpinionList(id);
			
			model.put("params", raiseMap);
			model.put("categories", categories);
			model.put("raiseDetails", raiseDetails);
			model.put("opinions", opinions);
			
			return "/merchant/raiseCheckDetail";
		}
		
		@RequestMapping(value="/raiseCheckInfo")
		public String raiseCheckInfo(final ModelMap model, @RequestParam Long id){
			
			Map<String, Object> raiseMap = merchantService.getMerchantRaise4CheckById(id);
			String merchantNo = raiseMap.get("merchant_no").toString();
			String agentNo = raiseMap.get("agent_no").toString();
			
			Map<String, Object> merchantMap = merchantService.queryMerchantInfoByMerchantNo(merchantNo);
			raiseMap.putAll(merchantMap);
			
			Map<String, Object> agentMap = null;
			try {
				agentMap = merchantService.findSaleNameByAgentNo(agentNo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (agentMap != null) {
				raiseMap.put("self_sale_name", raiseMap.get("self_sale_name"));
			}

			Object fee_rate = merchantMap.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				String feeRate = ((BigDecimal) merchantMap.get("fee_rate")).multiply(new BigDecimal("100")).setScale(4, RoundingMode.HALF_UP) + "";
				raiseMap.put("fee_rate", feeRate);
			}

			Object ladder_fee = merchantMap.get("ladder_fee");
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];

					raiseMap.put("ladder_min", new BigDecimal(ladder_min).movePointRight(2));
					raiseMap.put("ladder_value", ladder_value);
					raiseMap.put("ladder_max", new BigDecimal(ladder_max).movePointRight(2));
				}
			}
			
			String merchantRaiseCategory = SysConfig.value("merchantRaiseCategory");
			
			String[] categories = merchantRaiseCategory.split(",");
			
			List<Map<String, Object>> raiseDetails =  merchantService.getRaiseCheckDetailList(id);
			
			List<Map<String, Object>> opinions = merchantService.getRaiseCheckOpinionList(id);
			
			model.put("params", raiseMap);
			model.put("categories", categories);
			model.put("raiseDetails", raiseDetails);
			model.put("opinions", opinions);
			
			return "/merchant/raiseCheckInfo";
		}
		
		@RequestMapping(value="/raiseCheckDetailSubmit")
		public String raiseCheckDetailSubmit(final ModelMap model, @RequestParam Map<String, String> params){

			try {
				merchantService.raiseCheck(params);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return "redirect:" + "/mer/raiseCheckQuery";
		}
}
