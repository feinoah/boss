package com.eeepay.boss.controller;

import com.eeepay.boss.domain.CardBin;
import com.eeepay.boss.enums.CurrencyType;
import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.enums.TransType;
import com.eeepay.boss.service.*;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.OrderUtil;
import com.eeepay.boss.utils.StringUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/5/28.
 */
@Controller
@RequestMapping(value = "/history")
public class HistoryTransController extends BaseController {
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private HitoryTransService hitoryTransService;
    @Resource
    private TransService transService;
    @Resource
    private MerchantService merchantService;

    @Resource
    private AcqMerchantService acqMerchantService;

    @Resource
    private BankCardService bankCardService;
    
    @Resource
	private PosTypeService posTypeService;

    private static final Logger log = LoggerFactory.getLogger(HistoryTransController.class);
    
    private String posModelName(String pos_model){
		log.info("HistoryTransController posModelName START");
		String posModelName = pos_model;
		if(!StringUtil.isEmpty(posModelName)){
			Map<String, Object> posModelMap =  posTypeService.getPosModelName(pos_model);
			if(posModelMap != null && posModelMap.size() > 0){
				if(posModelMap.get("pos_model_name") != null && !"".equals(posModelMap.get("pos_model_name").toString())){
					posModelName = posModelMap.get("pos_model_name").toString();
				}
			}
		}
		log.info("HistoryTransController posModelName End");
		return posModelName;
	}
    
    
    @RequestMapping(value = "transInfoHistory")
    public String transInfoHistory(ModelMap model ,@RequestParam Map<String,String> params,@RequestParam(value = "p",defaultValue = "1") int cpage){

        log.info("MerchantController transHistory start...");
        PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
        Date currdate = new Date();
        List<Map<String, Object>> userList = userGroupService.getUserToGroupId(19);
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

        Page<Map<String, Object>> list = hitoryTransService.getTransHistory(params, page);

        // Map<String, Object> totalMsg = transService.countTransInfo(params);
        model.put("p", cpage);
        model.put("currdate", DateUtils.format(currdate, "yyyy-MM-dd")+" 23:59:59");
        model.put("list", list);
        model.put("userList", userList);
        model.put("params", params);
        log.info("MerchantController transHistory End");
        return  "/merchant/merchantTransHistoryQuery";
    }

    @RequestMapping(value = "transHistoryExport")
    public void transHistoryExport(@RequestParam Map<String,String> params ,HttpServletResponse response,HttpServletRequest request){
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
            expordHistoryExcel(os, params, fileName);
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
    private void expordHistoryExcel(OutputStream os, Map<String, String> params,
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

        Page<Map<String, Object>> list = hitoryTransService.getTransHistory(params,//transService.getTransForExport(params,
                page);
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

            String trans_status = StringUtil.ifEmptyThen(map.get("trans_status"), "无");
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
           /* if (TransSource.MOBOLE_PHONE.toString().equals(trans_source)) {
                trans_source = "个人版";
            } else if (TransSource.COM_MOBILE_PHONE.toString().equals(trans_source)) {
                trans_source = "企业版";
            } else if (TransSource.POS.toString().equals(trans_source)) {
                trans_source = "POS";
            } else if (TransSource.SMALLBOX_MOBOLE_PHONE.toString().equals(trans_source)) {
                trans_source = "移小宝";
            } else if (TransSource.DOT.toString().equals(trans_source)) {
                trans_source = "点付宝";
            } else if (TransSource.NEW_LAND_ME30.toString().equals(trans_source)) {
                trans_source = "YPOS08";
            } else if (TransSource.NEW_LAND_ME31.toString().equals(trans_source)) {
                trans_source = "YPOS09";
            }*/
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

    // 统计交易信息
    @RequestMapping("/countTransHistoryInfo")
    @ResponseBody
    public Map<String, Object> countTransHistoryInfo(
            @RequestParam Map<String, String> params) {
        return hitoryTransService.countTransHistoryInfo(params);
    }

    // 交易详情
    @RequestMapping(value = "/historyDetail")
    public String transHistoryDetail(final ModelMap model, @RequestParam Long id) {

        Map<String, Object> params = new HashMap<String, Object>();

        Map<String, Object> transInfoMap = hitoryTransService.queryHistoryTransInfoById(id);

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
}
