package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zjc
 * @email zjcmengli@163.com
 * @date 2020-09-21 19:50:56
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<SkuAttrValueEntity> querySearchSkuAttrValuesByCidAndSkuId(Long cid, Long skuid);

    List<SaleAttrValueVo> querySaleAttrValuesBySpuId(Long spuId);

    String querySkuIdMappingSaleAttrValueBySpuId(Long spuId);
}

