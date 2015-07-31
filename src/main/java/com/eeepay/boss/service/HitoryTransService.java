package com.eeepay.boss.service;

import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.HistoryDao;
import com.eeepay.boss.utils.OrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/28.
 */
@Service
public class HitoryTransService {

    @Resource
    private Dao dao;

    @Resource
    private HistoryDao historyDao;

    // 组合条件查询交易数据
    public Page<Map<String, Object>> getTransHistory(Map<String, String> params,
                                                     final PageRequest pageRequest) {
        List<Object> list = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer();
        if (null != params) {

            // 订单id查询
            // 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
            if (StringUtils.isNotEmpty(params.get("id"))) {
                String idFromJsp = params.get("id");
                String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
                //String createTime = createTimeAndId[0];
                String id = createTimeAndId[1];

                String queryid = id;
                sb.append(" and t.id = ? ");
                list.add(queryid);
            }

            // 商户名称查询
            if (StringUtils.isNotEmpty(params.get("merchant"))) {
                String merchant_name = params.get("merchant");
                sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
                list.add(merchant_name);
                list.add(merchant_name);
                list.add(merchant_name);
            }

            // 代理商编号
            if (StringUtils.isNotEmpty(params.get("agentNo"))) {
                if (!params.get("agentNo").equals("-1")) {
                    String agentNo = params.get("agentNo");
                    sb.append(" and a.agent_no = ? ");
                    list.add(agentNo);
                }
            }

            // 代理商名称查询
            if (StringUtils.isNotEmpty(params.get("agentName"))) {
                String merchantName = params.get("agentName");
                sb.append(" and a.agent_name=?  ");
                list.add(merchantName);
            }

            // 收单商户
            if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
                String acqMerchantNo = params.get("acqMerchantNo");
                sb.append(" and t.acq_merchant_no =? ");
                list.add(acqMerchantNo);
            }

            // //
            // if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
            // String acqTerminalNo = params.get("acqTerminalNo");
            // sb.append(" and t.acq_terminal_no =? ");
            // list.add(acqTerminalNo);
            // }
            // // 终端号
            // if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
            // String terminalNo = params.get("terminalNo");
            // sb.append(" and t.terminal_no =? ");
            // list.add(terminalNo);
            // }

            // 卡号
            if (StringUtils.isNotEmpty(params.get("cardNo"))) {
                String cardNo = params.get("cardNo");
                sb.append(" and t.account_no = ? ");
                list.add(cardNo);
            }
            // 参考号
            if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
                String referenceNo = params.get("referenceNo");
                sb.append(" and t.acq_reference_no =? ");
                list.add(referenceNo);
            }

            // 手机号
            if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
                String mobile_username = params.get("mobile_username");
                sb.append(" and m.mobile_username =? ");
                list.add(mobile_username);
            }

            // 交易类型
            if (StringUtils.isNotEmpty(params.get("transType"))
                    && !"-1".equals(params.get("transType"))) {
                String transType = params.get("transType");
                sb.append(" and t.trans_type =? ");
                list.add(transType);
            }

            // 交易状态
            if (StringUtils.isNotEmpty(params.get("transStatus"))) {
                String transStatus = params.get("transStatus");
                if(transStatus.equals("FREEZED")){
                    sb.append(" and t.freeze_status =? ");
                    list.add("1");
                }else if (!"-1".equals(transStatus)) {
                    sb.append(" and t.trans_status =? ");
                    list.add(transStatus);
                }
            }


            // 冻结状态
            if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
                String transStatus = params.get("freezeStatus");
                if(!transStatus.equals("-1")){
                    sb.append(" and t.freeze_status =? ");
                    list.add(transStatus);
                }
            }

            // 卡类型
            if (StringUtils.isNotEmpty(params.get("cardType"))) {
                String cardType = params.get("cardType");
                if (!"-1".equals(cardType)) {
                    sb.append(" and t.card_type =? ");
                    list.add(cardType);
                }
            }

            // 交易来源
            if (StringUtils.isNotEmpty(params.get("transSource"))
                    && !"-1".equals(params.get("transSource"))) {
                String transSource = params.get("transSource");
                sb.append(" and t.trans_source =? ");
                list.add(transSource);
            }

            // 收单机构
            if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))
                    && !"-1".equals(params.get("ACQ_ENNAME"))) {
                String acq_enname = params.get("ACQ_ENNAME");
                sb.append(" and o.ACQ_ENNAME =? ");
                list.add(acq_enname);
            }

            // address

            if (StringUtils.isNotEmpty(params.get("address"))) {
                String address = "%" + params.get("address") + "%";
                sb.append(" and (ptil.address like ? or mrd.province like ? or mrd.city like ? or mrd.stree like ?)");
                list.add(address);
                list.add(address);
                list.add(address);
                list.add(address);
            }

            // params.put("createTimeBegin","2012-06-04 00:00:00");
            if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
                String createTimeBegin = params.get("transTimeBegin");
                sb.append(" and t.trans_time >=? ");
                list.add(createTimeBegin);
            }



            // sale_name
            if (StringUtils.isNotEmpty(params.get("sale_name"))) {
                String sale_name = params.get("sale_name");
                sb.append(" and (a.sale_name =? ");
                list.add(sale_name);
                sb.append(" or m.sale_name =?) ");
                list.add(sale_name);
            }

            if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
                String createTimeEnd = params.get("transTimeEnd");
                sb.append(" and t.trans_time <=? ");
                list.add(createTimeEnd);
            }


            // params.put("createTimeBegin","2012-06-04 00:00:00");
            if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
                String createTimeBegin = params.get("createTimeBegin");
                sb.append(" and t.create_time >=? ");
                list.add(createTimeBegin);
            }


            if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
                String createTimeBegin = params.get("createTimeEnd");
                sb.append(" and t.create_time <=? ");
                list.add(createTimeBegin);
            }
            // 电子小票
            if (StringUtils.isNotEmpty(params.get("review_status"))
                    && !"-1".equals(params.get("review_status"))) {
                String littleTreasure = params.get("review_status");
                if (littleTreasure.equals("2")) {
                    sb.append(" and (t.review_status=? or t.review_status is null) ");
                } else {
                    sb.append(" and t.review_status=? ");
                }

                list.add(littleTreasure);
            }

			/*String trans_amount = params.get("trans_amount");
			if (StringUtils.isNotEmpty(trans_amount)	&& trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount = ? ");
				list.add(new BigDecimal(trans_amount));
			}*/
            String checkTimeBegin = params.get("checkTimeBegin");
            if(StringUtils.isNotEmpty(checkTimeBegin)){
                sb.append(" and sign_check_time>=?");
                list.add(checkTimeBegin);
            }

            String checkTimeEnd = params.get("checkTimeEnd");
            if(StringUtils.isNotEmpty(checkTimeEnd)){
                sb.append(" and sign_check_time<=?");
                list.add(checkTimeEnd);
            }

            String checkPerson= params.get("checkPerson");
            if(StringUtils.isNotEmpty(checkPerson) && !"-1".equals(checkPerson)){
                sb.append(" and sign_check_person=?");
                list.add(checkPerson);
            }

            String start_trans_amount = params.get("start_trans_amount");
            if (StringUtils.isNotEmpty(start_trans_amount)	&& start_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
                sb.append(" and trans_amount >= ? ");
                list.add(new BigDecimal(start_trans_amount));
            }


            String end_trans_amount = params.get("end_trans_amount");
            if (StringUtils.isNotEmpty(end_trans_amount)	&& end_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
                sb.append(" and trans_amount <= ? ");
                list.add(new BigDecimal(end_trans_amount));
            }


        }
		/*
		 * String sql =
		 * "select m.merchant_short_name,m.mcc mermcc , m.mobile_username , " +
		 * "a.agent_name, " + " t.*" +
		 * " from trans_info t  ,pos_merchant m,agent_info a " +
		 * " where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no " +
		 * sb.toString() + " order by t.id desc";
		 */
//		String sql = "select m.merchant_short_name,m.mcc mermcc , m.mobile_username ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,o.ACQ_CNNAME,o.ACQ_ENNAME,t.* "
//				+ "from acq_org o,trans_info t,pos_merchant m,agent_info a  "
//				+ "where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.acq_enname=o.ACQ_ENNAME "
//				+ sb.toString() + " order by t.id desc";
        String sql = "select m.merchant_short_name,m.mcc mermcc , m.mobile_username ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,o.ACQ_CNNAME,o.ACQ_ENNAME, "
                + " t.trans_type, t.card_type, t.account_no, t.trans_amount, t.trans_status, t.freeze_status, t.create_time, t.id, t.review_status, t.merchant_no, t.merchant_fee, t.bag_settle, t.merchant_settle_date, t.trans_source  "
                + " from history.trans_info t"
                + " left join pos_merchant m on t.merchant_no = m.merchant_no "
                + " left join agent_info a on m.agent_no = a.agent_no "
                + " left join acq_org o on t.acq_enname=o.acq_enname "
                + " where 1 = 1 "
                + sb.toString()
                + " order by t.id desc";
        return historyDao.find(sql, list.toArray(), pageRequest);
    }


    // 统计交易信息
    public Map<String, Object> countTransHistoryInfo(Map<String, String> params) {

        Map<String, Object> totalMsg = new HashMap<String, Object>();
        List<Object> list = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer();
        if (null != params) {

            // 订单id查询
            // 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
            if (StringUtils.isNotEmpty(params.get("id"))) {
                String idFromJsp = params.get("id");
                String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
                //String createTime = createTimeAndId[0];
                String id = createTimeAndId[1];

                String queryid = id;
                sb.append(" and t.id = ? ");
                list.add(queryid);
            }

            // 商户名称查询
            if (StringUtils.isNotEmpty(params.get("merchant"))) {
                String merchant_name = params.get("merchant");
                sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
                list.add(merchant_name);
                list.add(merchant_name);
                list.add(merchant_name);
            }

            // 代理商编号
            if (StringUtils.isNotEmpty(params.get("agentNo"))) {
                if (!params.get("agentNo").equals("-1")) {
                    String agentNo = params.get("agentNo");
                    sb.append(" and a.agent_no = ? ");
                    list.add(agentNo);
                }
            }

            // 代理商名称查询
            if (StringUtils.isNotEmpty(params.get("agentName"))) {
                String merchantName = params.get("agentName");
                sb.append(" and a.agent_name=?  ");
                list.add(merchantName);
            }

            // 收单商户
            if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
                String acqMerchantNo = params.get("acqMerchantNo");
                sb.append(" and t.acq_merchant_no =? ");
                list.add(acqMerchantNo);
            }

            // //
            // if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
            // String acqTerminalNo = params.get("acqTerminalNo");
            // sb.append(" and t.acq_terminal_no =? ");
            // list.add(acqTerminalNo);
            // }
            // // 终端号
            // if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
            // String terminalNo = params.get("terminalNo");
            // sb.append(" and t.terminal_no =? ");
            // list.add(terminalNo);
            // }

            // 卡号
            if (StringUtils.isNotEmpty(params.get("cardNo"))) {
                String cardNo = params.get("cardNo");
                sb.append(" and t.account_no = ? ");
                list.add(cardNo);
            }
            // 参考号
            if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
                String referenceNo = params.get("referenceNo");
                sb.append(" and t.acq_reference_no =? ");
                list.add(referenceNo);
            }

            // 手机号
            if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
                String mobile_username = params.get("mobile_username");
                sb.append(" and m.mobile_username =? ");
                list.add(mobile_username);
            }

            // 交易类型
            if (StringUtils.isNotEmpty(params.get("transType"))
                    && !"-1".equals(params.get("transType"))) {
                String transType = params.get("transType");
                sb.append(" and t.trans_type =? ");
                list.add(transType);
            }

            // 交易状态
            if (StringUtils.isNotEmpty(params.get("transStatus"))) {
                String transStatus = params.get("transStatus");
                if (!"-1".equals(transStatus)) {
                    sb.append(" and t.trans_status =? ");
                    list.add(transStatus);
                }
            }

            // 冻结状态
            if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
                String transStatus = params.get("freezeStatus");
                if(!transStatus.equals("-1")){
                    sb.append(" and t.freeze_status =? ");
                    list.add(transStatus);
                }
            }

            // 卡类型
            if (StringUtils.isNotEmpty(params.get("cardType"))) {
                String cardType = params.get("cardType");
                if (!"-1".equals(cardType)) {
                    sb.append(" and t.card_type =? ");
                    list.add(cardType);
                }
            }

            // 交易来源
            if (StringUtils.isNotEmpty(params.get("transSource"))
                    && !"-1".equals(params.get("transSource"))) {
                String transSource = params.get("transSource");
                sb.append(" and t.trans_source =? ");
                list.add(transSource);
            }

            // 收单机构
            if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))
                    && !"-1".equals(params.get("ACQ_ENNAME"))) {
                String acq_enname = params.get("ACQ_ENNAME");
                sb.append(" and o.ACQ_ENNAME =? ");
                list.add(acq_enname);
            }

            // address

            if (StringUtils.isNotEmpty(params.get("address"))) {
                String address = "%" + params.get("address") + "%";
                sb.append(" and (ptil.address like ? or mrd.province like ? or mrd.city like ? or mrd.stree like ?)");
                list.add(address);
                list.add(address);
                list.add(address);
                list.add(address);
            }

            // params.put("createTimeBegin","2012-06-04 00:00:00");
            if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
                String createTimeBegin = params.get("transTimeBegin");
                sb.append(" and t.trans_time >=? ");
                list.add(createTimeBegin);
            }

            // sale_name
            if (StringUtils.isNotEmpty(params.get("sale_name"))) {
                String sale_name = params.get("sale_name");
                sb.append(" and (a.sale_name =? ");
                list.add(sale_name);
                sb.append(" or m.sale_name =?) ");
                list.add(sale_name);
            }

            if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
                String createTimeEnd = params.get("transTimeEnd");
                sb.append(" and t.trans_time <=? ");
                list.add(createTimeEnd);
            }


            // params.put("createTimeBegin","2012-06-04 00:00:00");
            if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
                String createTimeBegin = params.get("createTimeBegin");
                sb.append(" and t.create_time >=? ");
                list.add(createTimeBegin);
            }


            if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
                String createTimeBegin = params.get("createTimeEnd");
                sb.append(" and t.create_time <=? ");
                list.add(createTimeBegin);
            }
            String checkTimeBegin = params.get("checkTimeBegin");
            if(StringUtils.isNotEmpty(checkTimeBegin)){
                sb.append(" and sign_check_time>=?");
                list.add(checkTimeBegin);
            }

            String checkTimeEnd = params.get("checkTimeEnd");
            if(StringUtils.isNotEmpty(checkTimeEnd)){
                sb.append(" and sign_check_time<=?");
                list.add(checkTimeEnd);
            }

            String checkPerson= params.get("checkPerson");
            if(StringUtils.isNotEmpty(checkPerson) && !"-1".equals(checkPerson)){
                sb.append(" and sign_check_person=?");
                list.add(checkPerson);
            }

            String start_trans_amount = params.get("start_trans_amount");
            if (StringUtils.isNotEmpty(start_trans_amount)	&& start_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
                sb.append(" and trans_amount >= ? ");
                list.add(new BigDecimal(start_trans_amount));
            }


            String end_trans_amount = params.get("end_trans_amount");
            if (StringUtils.isNotEmpty(end_trans_amount)	&& end_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
                sb.append(" and trans_amount <= ? ");
                list.add(new BigDecimal(end_trans_amount));
            }

            // 电子小票
            if (StringUtils.isNotEmpty(params.get("review_status"))
                    && !"-1".equals(params.get("review_status"))) {
                String littleTreasure = params.get("review_status");
                if (littleTreasure.equals("2")) {
                    sb.append(" and (t.review_status=? or t.review_status is null) ");
                } else {
                    sb.append(" and t.review_status=? ");
                }

                list.add(littleTreasure);
            }

            String trans_amount = params.get("trans_amount");
            if (StringUtils.isNotEmpty(trans_amount)
                    && trans_amount
                    .matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
                sb.append(" and trans_amount = ? ");
                list.add(new BigDecimal(trans_amount));
            }
        }


        String totalSql = "select count(t.id) total_count from history.trans_info t "+
                " left join pos_merchant m on t.merchant_no = m.merchant_no"+
                " left join  agent_info a on m.agent_no = a.agent_no "+
                " left join  acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1"
                + sb.toString() + " order by t.id desc";

        String totalPurSql = "select count(t.id) total_pur_count, sum(t.trans_amount) total_amount from history.trans_info t "+
                " left join  pos_merchant m on t.merchant_no = m.merchant_no"+
                " left join  agent_info a on m.agent_no = a.agent_no "+
                " left join  acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1"
                + sb.toString() + " order by t.id desc";

        String totalPurAmountSql = "select sum(t.trans_amount) total_pur_amount , sum(t.merchant_fee) total_mer_fee from history.trans_info t "+
                " left join pos_merchant m on t.merchant_no = m.merchant_no"+
                " left join  agent_info a on m.agent_no = a.agent_no "+
                " left join  acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1 "
                + sb.toString() + " order by t.id desc";

        Map<String, Object> totalCount = historyDao
                .findFirst(totalSql, list.toArray());
        Map<String, Object> totalPurCount =historyDao.findFirst(totalPurSql,
                list.toArray());
        // Map<String, Object> totalPrvCount = dao.find First(totalPrvSql,
        // list.toArray());
        // Map<String, Object> totalRefCount = dao.findFirst(totalRefSql,
        // list.toArray());
        Map<String, Object> totalPurAmount = historyDao.findFirst(totalPurAmountSql,
                list.toArray());

        if (totalCount != null) {
            if (totalCount.get("total_count") != null) {
                totalMsg.put("total_count", totalCount.get("total_count"));
            } else {
                totalMsg.put("total_count", 0);
            }
        }
        if (totalPurCount != null) {
            if (totalPurCount.get("total_pur_count") != null) {
                totalMsg.put("total_pur_count",
                        totalPurCount.get("total_pur_count"));
            } else {
                totalMsg.put("total_pur_count", 0);
            }
        }
        // if (totalPrvCount != null) {
        // if (totalPrvCount.get("total_prv_count") != null) {
        // totalMsg.put("total_prv_count",
        // totalPrvCount.get("total_prv_count"));
        // } else {
        // totalMsg.put("total_prv_count", 0);
        // }
        // }
        // if (totalRefCount != null) {
        // if (totalRefCount.get("total_ref_count") != null) {
        // totalMsg.put("total_ref_count",
        // totalRefCount.get("total_ref_count"));
        // } else {
        // totalMsg.put("total_ref_count", 0);
        // }
        // }
        if (totalPurAmount != null) {
            if (totalPurAmount.get("total_pur_amount") != null) {
                totalMsg.put("total_pur_amount",
                        totalPurAmount.get("total_pur_amount"));
            } else {
                totalMsg.put("total_pur_amount", "0.00");
            }

            if (totalPurAmount.get("total_mer_fee") != null) {
                totalMsg.put("total_mer_fee",
                        totalPurAmount.get("total_mer_fee"));
            } else {
                totalMsg.put("total_mer_fee", "0.00");
            }
        }

        return totalMsg;
    }

    // 交易详情查询
    public Map<String, Object> queryHistoryTransInfoById(Long id) {
        String sql = "select merchant_no,acq_reference_no, terminal_no, batch_no, serial_no, acq_merchant_no, acq_terminal_no, sign_check_person,sign_check_time,device_sn,sign_img,"
                + "acq_batch_no, acq_serial_no, account_no, ti.trans_type, trans_status,acq_auth_no, acq_response_code, currency_type, "
                + "trans_amount, merchant_rate, acq_merchant_rate, merchant_fee, acq_merchant_fee, trans_source, merchant_settle_date, "
                + "acq_settle_date, ti.create_time ,trans_time,review_status,ptil.address,CONCAT(mrd.province,mrd.city,mrd.stree)  mobile_address,is_iccard  from history.trans_info ti left join pos_trans_info_location ptil on ti.id"
                + "=ptil.trans_info_id left join mobile_rquest_dev mrd on ti.id=trim(both '0' from SUBSTRING(mrd.order_id from 9)) where ti.id =?";
        return historyDao.findFirst(sql, id);
    }
}
