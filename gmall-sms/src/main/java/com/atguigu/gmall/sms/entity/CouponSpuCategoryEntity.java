package com.atguigu.gmall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券分类关联
 * 
 * @author zjc
 * @email zjcmengli@163.com
 * @date 2020-09-21 20:46:03
 */
@Data
@TableName("sms_coupon_spu_category")
public class CouponSpuCategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 优惠券id
	 */
	private Long couponId;
	/**
	 * 产品分类id
	 */
	private Long categoryId;
	/**
	 * 产品分类名称
	 */
	private String categoryName;

}
