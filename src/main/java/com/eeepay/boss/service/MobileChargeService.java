package com.eeepay.boss.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.eeepay.boss.domain.MobileVO;
import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.Http2;
import com.eeepay.boss.utils.IncrementHttp;
import com.eeepay.boss.utils.KeyedDigestMD5;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.XMLChongzhi;
import com.eeepay.boss.utils.DateUtils;



/**
 * 高阳手机充值
 * 
 * @author wgd
 * 
 */
@Service
public class MobileChargeService {

	@Resource
	private Dao dao;
	
	private static final Logger logger = LoggerFactory
			.getLogger(MobileChargeService.class);

	/**
	 * 手机直冲
	 * 
	 * @param params
	 * @return
	 */
	public Map<String,String> chargeMobile(Map<String, String> params) {


		String prodid = params.get("prodid");
		String backurl = params.get("backurl");
		String returntype = params.get("returntype");
		String orderid = params.get("orderid");
		String mobilenum = params.get("mobilenum");
		String source = "esales";
		String mark = params.get("mark");
		String merchantkey = SysConfig.value("merchantKey");
		String agentid = SysConfig.value("agentid");
		String productUrl = SysConfig.value("directFillUrl");
		
		try {
			agentid=URLDecoder.decode(agentid, "GB2312") ;
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
		String sd = "prodid="+prodid+"&agentid="+agentid+"&backurl="+
		""+backurl+"&returntype="+returntype+"&orderid="+orderid+"" +
				"&mobilenum="+mobilenum+"&source=esales&mark="+mark+"&merchantKey="+merchantkey;

		
//		String sd="prodid=8096&agentid=test_agent_id_1&backurl=&returntype=2&orderid=111111111&mobilenum=13811528476&source=esales&mark=&merchantKey=111111";
//		System.out.println("===verifystring=="+KeyedDigestMD5.getKeyedDigest(sd, ""));
		
		Map<String, Object> sendParams = new HashMap<String, Object>();
		sendParams.put("prodid", prodid);
		sendParams.put("agentid", agentid);
		sendParams.put("backurl", backurl);
		sendParams.put("returntype", returntype);
		sendParams.put("orderid", orderid);
		sendParams.put("mobilenum", mobilenum);
		sendParams.put("source", source);
		try {
			sendParams.put("mark", URLDecoder.decode(mark,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sendParams.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
		
		logger.info("MobileAction|chargePhone|params|" + sd);
		String body = IncrementHttp.send(productUrl, sendParams,"GB2312");
		
		System.out.println("==body==="+body);
		String responseXMl="";
		try {
			responseXMl = URLDecoder.decode(body, "GB2312");
			logger.info("MobileAction|chargePhone|responseXMl|" + responseXMl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return XMLChongzhi.parsersXml(responseXMl);

	}
	
	
	
	/**
	 *  查询订单
	 * @param params
	 * @return
	 */
	public Map<String,String> selMobileOrder(Map<String, String> params) {

		String agentid = SysConfig.value("agentid");
		String backurl = params.get("backurl");
		String returntype = params.get("returntype");
		String orderid = params.get("orderid");
		String source = "esales";
		String merchantkey = SysConfig.value("merchantKey");
		String productUrl = SysConfig.value("directFillUrl");
		
		try {
			agentid=URLDecoder.decode(agentid, "GB2312") ;
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
		String sd = "agentid="+agentid+"&backurl="+""+backurl+"&returntype="+returntype+"&orderid="+orderid+"" +
				"&source=esales&merchantKey="+merchantkey;
		
		System.out.println("===verifystring=="+KeyedDigestMD5.getKeyedDigest(sd, ""));
		
		Map<String, Object> sendParams = new HashMap<String, Object>();
		sendParams.put("agentid", agentid);
		sendParams.put("backurl", backurl);
		sendParams.put("returntype", returntype);
		sendParams.put("orderid", orderid);
		sendParams.put("source", source);
		sendParams.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
		
		logger.info("MobileAction|chargePhone|params|" + sd);
		String body = IncrementHttp.send(productUrl, sendParams,"GB2312");
		
		System.out.println("==body==="+body);
		String responseXMl="";
		
		try {
			responseXMl = URLDecoder.decode(body, "UTF-8");
			logger.info("MobileAction|chargePhone|responseXMl|" + responseXMl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return XMLChongzhi.parsersXml(responseXMl);

	}
	
	
	//批量导入充值
	
	public int mobileExcImp(Map<String, String> params, String urlTemp)
			throws Exception {

		String fileName = params.get("excelFileName");
		String url = urlTemp + fileName;

//		List<Map<String, Object>> terminalLis = getTerminalList();
		int countLine = 0;
		try {
			// 构建Workbook对象, 只读Workbook对象
			// 直接从本地文件创建Workbook
			// 从输入流创建Workbook
			InputStream is = new FileInputStream(url);
			Workbook rwb = Workbook.getWorkbook(is);

			// 获取第一张Sheet表
			Sheet rs = (Sheet) rwb.getSheet(0);
			Cell c;


			for (int i = 1; i < rs.getRows(); i++) {
				c = ((Sheet) rs).getCell(0, i);
				String mobile = c.getContents();
				c = ((Sheet) rs).getCell(1, i);
				String price = c.getContents();
				
				
				if (!StringUtil.isEmpty(mobile) && !StringUtil.isEmpty(price)) {
				
					// 先判断是否支持该手机号，如果不支持，就不通讯，
					mobile = mobile.trim();
					price  = price.trim();
					String url_getMobileAddressByTaobao = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="
							+ mobile;
					String bodyString = Http2.sendXML("" + url_getMobileAddressByTaobao, "GBK");
	
					// 如果返回如果不是msg开头，说明淘宝有返回，则判断手机归属地。以msg开头，说明Http类没有通讯成功。就不知道是哪里的号码，也不去充值;
					if (bodyString.indexOf("msg=") < 0) {
						bodyString = bodyString.substring(bodyString.indexOf("{"),
								bodyString.length());
						MobileVO vo = JSONObject.parseObject(bodyString, MobileVO.class);
						String province = "";
						String catName = "";
						if (vo != null) {
							province = vo.getProvince();
							catName = vo.getCatName().substring(2);
							Map<String, Object> mobPrcMap = queryMobileProduct(Integer.parseInt(price),
											province, catName);
							
							
							if (mobPrcMap != null
									&& !"".equals(mobPrcMap.get("prod_id"))) {
	//							String amount = mobPrcMap.get("prod_price").toString();
								String prodid = mobPrcMap.get("prod_id").toString();
								String amount = price;                 //面额支付金额
								
								
								
								// 将订单插入数据库。
								String orderNo = createOrder(amount,"mobile", "110011011201958");
								mobileOrderAdd(orderNo, mobile, price, amount,
												TransStatus.INIT.toString(), "110011011201958");
								params.put("orderNo", orderNo);
								
								
								
								Map<String, String> mobPrcParams = new HashMap<String, String>();
								mobPrcParams.put("prodid", prodid);
								mobPrcParams.put("backurl", "");
								mobPrcParams.put("returntype", "2");
								mobPrcParams.put("orderid", orderNo);
								mobPrcParams.put("mobilenum", mobile);
								mobPrcParams.put("source", "esales");
								mobPrcParams.put("mark", "");
								
								
								//调用充值下单
								Map<String, String> resMap = chargeMobile(mobPrcParams);
								
								if (resMap == null) {
									params.put("status", "Error");
									logger.info("========订单" + orderNo + "手机充值下单失败========");
								} else {
									String resultno = resMap.get("resultno");
									Map<String, String> updateParams = new HashMap<String, String>();
									updateParams.put("orderid", orderNo);
									if ("0000".equals(resultno)) {
										params.put("status", "下单成功");
										updateParams.put("orderStatus",
												TransStatus.SENDORDER.toString());
										logger.info("========订单" + orderNo + "手机充值下单成功========");
									} else {
										params.put("status", "下单失败");
										updateParams.put("orderStatus",
												TransStatus.FAILED.toString());
										logger.info("========订单" + orderNo + "手机充值下单失败========");
									}
									updateOrder(updateParams);
								}
					
								countLine++;
	
							} else {
								// 将订单插入数据库。
								String orderNo = createOrder(price,"mobile", "110011011201958");
								mobileOrderAdd(orderNo, mobile, price, price,
												TransStatus.INIT.toString(), "110011011201958");
								logger.info("==== getMobPrc 无此产品，请返回重新选择 ====");
								
							}
						} else {
								logger.info("==== 解析失败 ====");
						}
					
				
					}
	
				}

			}
			// 当你完成对Excel电子表格数据的处理后，一定要使用close()方法来关闭先前创建的对象，
			// 以释放读取数据表的过程中所占用的内存空间，在读取大量数据时显得尤为重要。参考如下代码片段：
			// 操作完成时，关闭对象，释放占用的内存空间
			rwb.close();
			
		} catch (Exception e) {
			throw e;
		}


		return countLine;
	}

	
	//查询产品
	
	public Map<String,Object> queryMobileProduct(int price,String province,String catName) throws SQLException{
		String sql ="select * from mobile_product where prod_provinceid=? and prod_content=? and prod_isptype=? and prod_type=?";
		return dao.findFirst(sql, new Object[]{province,price,catName,"移动电话"});
	}
	
	


	// 创建订单
	public String createOrder(String transAmount, String transType,String merchantNo)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		for (int i = 0; i < 5; i++) {
			sb.append(r.nextInt(8999) + 1000);
		}
		String orderNo = System.currentTimeMillis() + sb.toString();
		orderNo = orderNo.substring(15, orderNo.length());
		orderNo = "IN" + orderNo;
		String sql = "insert into increment_order(order_no,trans_amount,trans_type,trans_status,merchant_no,create_time) values(?,?,?,?,?,?)";
		Object[] params = { orderNo, transAmount, transType,
				TransStatus.INIT.toString(),merchantNo, new Date() };
		if (dao.update(sql, params) > 0) {
			return orderNo;
		}
		return null;

	}
	
	// 保存手机充值记录
	public void mobileOrderAdd(String journal,String mobile,
			String price, String amount,String status,String merchantNo)
			throws SQLException {
		String sql = "insert into increment_mobile(journal,mobile,price,amount,status,merchant_no,create_time) values(?,?,?,?,?,?,?)";
		Object[] params = { journal, mobile, price, amount, status,merchantNo,new Date()};
		dao.update(sql, params);
	}

	public void updateOrder(Map<String, String> params) throws SQLException {
		
		String orderStatus = params.get("orderStatus");
		String orderNo = params.get("orderid");
		String tranTime = DateUtils.getMessageTextTime();
		String terminalNo = params.get("terminalNo");
		
		//更新手机充值订单表
		String sqlMobile = "update increment_mobile set trans_time=? , terminal_no=? , status=? " +
				" where journal=? ";
		dao.update(sqlMobile, new Object[] { tranTime, terminalNo,orderStatus ,orderNo });
		
	}
	
	
	public static void main(String[] args) {
		// String url = "IncrementHttp://pay.microit.com.cn:8040/recharge/mrd";
		// String url = "IncrementHttp://219.239.129.122:8040/recharge/mrd";
		
		  //获取产品id
/*		  Map<String,Object> paramss=new HashMap<String,Object>();
		  paramss.put("agentid", "test_agent_id_1");
		  paramss.put("source", "esales");
		  paramss.put("merchantKey", "111111");
		  String sd="agentid=test_agent_id_1&source=esales&merchantKey=111111";
//		  Map<String,Object> params=new HashMap<String,Object>();
		  paramss.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
		  String body = IncrementHttp.send("IncrementHttp://219.143.36.227:101/dealer/prodquery/directProduct.do", paramss, "UTF-8");
		  try {
			System.out.println(URLDecoder.decode(body,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		  //产品直冲
		  Map<String,Object> params=new HashMap<String,Object>();
		  String agentid = "测试中文";
//		  agentid = "%B2%E2%CA%D4%D6%D0%CE%C4";
		  String aa ="";
		  try {
			aa = URLDecoder.decode("测试中文", "GB2312") ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  params.put("prodid", "8096");
//		  params.put("agentid", "test_agent_id_1");
//		  params.put("agentid", aa);
		  params.put("agentid", agentid);
		  params.put("backurl", "");
		  params.put("returntype", "2");
		  params.put("orderid", "M20131219145641");
		  params.put("mobilenum", "13811528476");
		  params.put("source", "esales");
		  params.put("mark", "");
		  String sd="prodid=8096&agentid="+aa+"&backurl=&returntype=2&orderid=M20131219145641&mobilenum=13811528476&source=esales&mark=&merchantKey=111111";
		  System.out.println("======"+sd);
		  params.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
		  System.out.println("======"+KeyedDigestMD5.getKeyedDigest(sd, ""));
//		  String body = IncrementHttp.send("IncrementHttp://219.143.36.227:101/dealer/directfill/directFill.do", params, "UTF-8");
		  String body = IncrementHttp.send("IncrementHttp://219.143.36.227:101/dealer/directfill/directFill.do", params, "GB2312");
		  System.out.println(KeyedDigestMD5.getKeyedDigest(sd, ""));
		  System.out.println(body);

		  //查询订单
		  //1：正在处理 2充值成功 3、部分成功4充值失败
/*		  Map<String,Object> params=new HashMap<String,Object>();
		  params.put("agentid", "test_agent_id_1");
		  params.put("backurl", "");
		  params.put("returntype", "2");
		  params.put("orderid", "M20131217184436");
		  params.put("source", "esales");
		  String sd="agentid=test_agent_id_1&backurl=&returntype=2&orderid=M20131217184436&source=esales&merchantKey=111111";
		  params.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
		  String body = IncrementHttp.send("IncrementHttp://219.143.36.227:101/dealer/orderquery/directSearch.do", params, "UTF-8");
		  System.out.println(KeyedDigestMD5.getKeyedDigest(sd, ""));
		  System.out.println(body);*/
		  
		  
	}

}
