package com.eeepay.boss.service.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;
@Service
public class MerchantHandlingChargeService  {
	@Resource
	private Dao dao;
//	public Page<Map<String,Object>> findMerchantHandlingCharge(PageRequest page) throws SQLException{
//		String sql="select * from pos_merchant";
//		Page<Map<String,Object>> data=dao.find(sql, new Object[]{}, page);
//		List<Map<String,Object>> list=data.getContent();
//		String feeSql="select * from pos_merchant_fee where merchant_no=?";
//		for (Map<String, Object> map : list) {
//			PosMerchant pm=new PosMerchant();
//			pm.setMerchantNo(map.get("merchant_no").toString());
//			if( map.get("real_flag")==null){
//				pm.setRealFlag(0);
//			}else{
//				pm.setRealFlag(new Integer(map.get("real_flag").toString()));
//			}
//			BigDecimal transAmount= new BigDecimal("10000");
//			TransRouteInfo tri=getAcqRoute(pm,transAmount, "0");
//			map.put("routerInfo", tri);
//			BigDecimal merchantFeeAmount = null;
//			String merchantRate = null;
//			PosMerchantFee merchantFee=dao.findFirst(PosMerchantFee.class, feeSql, map.get("merchant_no").toString());
//			if (merchantFee != null) {
//				FeeType feeType = merchantFee.getFeeType();
//				BigDecimal feeRate = merchantFee.getFeeRate();
//				BigDecimal feeCapAmount = merchantFee.getFeeCapAmount();
//				BigDecimal feeMaxAmount = merchantFee.getFeeMaxAmount();
//				BigDecimal feeSingBigDecimal = merchantFee.getFeeSingleAmount();
//				String ladderFee = merchantFee.getLadderFee();
//				if (FeeType.RATIO.equals(feeType)) {
//					merchantFeeAmount = transAmount
//							.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
//					merchantRate = feeRate.multiply(new BigDecimal("100"))
//							.setScale(4, RoundingMode.HALF_UP).toString()
//							+ "%";
//				} else if (FeeType.SINGLE.equals(feeType)) {
//					merchantFeeAmount = feeSingBigDecimal;
//				} else if (FeeType.RATIOANDSINGLE.equals(feeType)) {
//					merchantFeeAmount = transAmount
//							.multiply(feeRate).setScale(2, RoundingMode.HALF_UP)
//							.add(feeSingBigDecimal);
//				} else if (FeeType.CAPPING.equals(feeType)) {
//					if (feeCapAmount.compareTo(transAmount) <= 0) {
//						merchantFeeAmount = feeMaxAmount;
//					} else {
//						merchantFeeAmount =transAmount
//								.multiply(feeRate)
//								.setScale(2, RoundingMode.HALF_UP);
//					}
//					merchantRate = feeRate.multiply(new BigDecimal("100"))
//							.setScale(4, RoundingMode.HALF_UP).toString()
//							+ "% ~ " + feeMaxAmount;
//				} else if (FeeType.LADDER.equals(feeType)) {
//					String[] ladders = ladderFee.split("<");
//					BigDecimal ladderRateLow = new BigDecimal(ladders[0]);
//					BigDecimal ladderAmount = new BigDecimal(ladders[1]);
//					BigDecimal ladderRateHigh = new BigDecimal(ladders[2]);
//					if (transAmount.compareTo(ladderAmount) <= 0) {
//						merchantFeeAmount = transAmount
//								.multiply(ladderRateLow)
//								.setScale(2, RoundingMode.HALF_UP);
//					} else {
//						merchantFeeAmount =transAmount
//								.multiply(ladderRateHigh)
//								.setScale(2, RoundingMode.HALF_UP);
//					}
//					merchantRate = ladderFee;
//				}
//			}
//			map.put("merchantFeeAmount",merchantFeeAmount);
//			map.put("merchantRate", merchantRate);
//			BigDecimal acqMerchantFee = null;
//			String acqMerchantRate = null;
//			AcqMerchant acqMerchant=tri.getAcqMerchant();
//			FeeType feeType = acqMerchant.getFeeType();
//			BigDecimal feeRate = acqMerchant.getFeeRate();
//			BigDecimal feeCapAmount = acqMerchant.getFeeCapAmount();
//			BigDecimal feeMaxAmount = acqMerchant.getFeeMaxAmount();
//			BigDecimal feeSingBigDecimal = acqMerchant.getFeeSingleAmount();
//			String ladderFee = acqMerchant.getLadderFee();
//			if (FeeType.RATIO.equals(feeType)) {
//				acqMerchantFee = transAmount.multiply(feeRate).setScale(2,
//						RoundingMode.HALF_UP);
//				acqMerchantRate = feeRate.multiply(new BigDecimal("100"))
//						.setScale(4, RoundingMode.HALF_UP).toString()
//						+ "%";
//			} else if (FeeType.SINGLE.equals(feeType)) {
//				acqMerchantFee = feeSingBigDecimal;
//			} else if (FeeType.RATIOANDSINGLE.equals(feeType)) {
//				acqMerchantFee = transAmount.multiply(feeRate)
//						.setScale(2, RoundingMode.HALF_UP).add(feeSingBigDecimal);
//			} else if (FeeType.CAPPING.equals(feeType)) {
//				if (feeCapAmount.compareTo(transAmount) <= 0) {
//					acqMerchantFee = feeMaxAmount;
//				} else {
//					acqMerchantFee = transAmount.multiply(feeRate).setScale(2,
//							RoundingMode.HALF_UP);
//				}
//				acqMerchantRate = feeRate.multiply(new BigDecimal("100"))
//						.setScale(4, RoundingMode.HALF_UP).toString()
//						+ "% ~ " + feeMaxAmount;
//			} else if (FeeType.LADDER.equals(feeType)) {
//				String[] ladders = ladderFee.split("<");
//				BigDecimal ladderRateLow = new BigDecimal(ladders[0]);
//				BigDecimal ladderAmount = new BigDecimal(ladders[1]);
//				BigDecimal ladderRateHigh = new BigDecimal(ladders[2]);
//				if (transAmount.compareTo(ladderAmount) <= 0) {
//					acqMerchantFee =transAmount
//							.multiply(ladderRateLow)
//							.setScale(2, RoundingMode.HALF_UP);
//				} else {
//					acqMerchantFee = transAmount
//							.multiply(ladderRateHigh)
//							.setScale(2, RoundingMode.HALF_UP);
//				}
//				acqMerchantRate = ladderFee;
//			}
//			map.put("acqMerchantRate", acqMerchantRate);
//			map.put("acqMerchantFee", acqMerchantFee);
//			map.put("profit", acqMerchantFee.compareTo(merchantFeeAmount)<0);
//		}
//		return data;
//	}
	/**
	 * 查询亏损的交易
	 * @param page
	 * @return
	 */
	public Page<Map<String,Object>> findLossTransInfo(Map<String, String> params,PageRequest page){
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and ti.trans_time >=? ");
				list.add(createTimeBegin);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and ti.trans_time <=? ");
				list.add(createTimeEnd);
			}
		}
		
		String sql="select ti.*,pm.merchant_name,am.acq_merchant_name from trans_info ti join " +
				" pos_merchant pm on(pm.merchant_no=ti.merchant_no) join acq_merchant am on(am.acq_merchant_no=ti.acq_merchant_no) " +
				" where ti.merchant_fee<ti.acq_merchant_fee and ti.trans_status='SUCCESS' and ti.agent_no!='1001'" +sb.toString()+
				" order by ti.trans_time desc";
		//return 	dao.find(sql, new Object[]{}, page);
		return 	dao.find(sql,  list.toArray(), page);
	}
}
