/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.domain;

import java.util.Comparator;

import com.eeepay.hxb.pub.map.DataMap;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年10月27日
 */

public class HxbTransComparator implements Comparator<DataMap> {

  public int compare(DataMap map1, DataMap map2) {
    if (Integer.parseInt(map1.getString("seqNo")) > Integer.parseInt(map2
        .getString("seqNo"))) {
      return 1;
    } else {
      return -1;
    }
  }

}
