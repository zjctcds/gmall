package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author zjc
 * @email zjcmengli@163.com
 * @date 2020-10-13 19:13:41
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
