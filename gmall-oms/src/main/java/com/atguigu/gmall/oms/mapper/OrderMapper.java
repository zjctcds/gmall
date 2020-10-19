package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zjc
 * @email zjcmengli@163.com
 * @date 2020-10-19 21:20:58
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
