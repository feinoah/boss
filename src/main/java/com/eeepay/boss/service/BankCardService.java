package com.eeepay.boss.service;


import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.CardBin;
import com.eeepay.boss.utils.Dao;



/**
 * 银行卡卡bin查询等
 * 
 * @author dj
 * 
 */
@Service
public class BankCardService {

	@Resource
	private Dao dao;

	public CardBin cardBin(String cardNo) {
		String sql = "select * from pos_card_bin c  where  c.card_length = length(?) AND c.verify_code = left(?,  c.verify_length)  ";

		CardBin cb = dao.findFirst(CardBin.class, sql, new Object[] {
					cardNo, cardNo });
		if (null == cb) {
			cb = new CardBin();
		}
		cb.setCardNo(cardNo);
		return cb;		
	}
}
