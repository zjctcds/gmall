package com.atguigu.gmall.index.service;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IndexService indexService;

    private static final String KEY_PREFIX = "index:cate:";

    public List<CategoryEntity> queryLvl1Categories() {
        ResponseVo<List<CategoryEntity>> responseVo = this.pmsClient.queryCategoriesByPid(0l);
        return responseVo.getData();
    }

    public List<CategoryEntity> queryLvl2WithSubsByPid(Long pid) {

        //查询缓存，命中就直接返回:一级分类id作为key,以方法返回值作为value
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json,CategoryEntity.class);
        }

        //没有命中，远程调用并放入缓存
        ResponseVo<List<CategoryEntity>> responseVo = this.pmsClient.queryCategoryLvl2WithSubsByPid(pid);
        List<CategoryEntity> categoryEntities = responseVo.getData();
        //这里已经解决了缓存穿透问题
        if (CollectionUtils.isEmpty(categoryEntities)){
            this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities),5, TimeUnit.MINUTES);
        }else {
            //这里解决缓存雪崩问题，要给缓存时间添加随机值
        this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities),90 + new Random().nextInt(5), TimeUnit.DAYS);
        }

        return categoryEntities;
    }


    public void testLock() throws InterruptedException {
        // 相当于setnx：只有redis中没有对应的key时，才会执行成功
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (!lock) {
            // 获取锁失败，重试
            Thread.sleep(20);
            this.testLock();
        } else {
            // this.redisTemplate.expire("lock", 3, TimeUnit.SECONDS);
            // 获取锁成功，执行业务逻辑
            String countString = this.redisTemplate.opsForValue().get("count");
            if (StringUtils.isBlank(countString)){
                this.redisTemplate.opsForValue().set("count", "1");
            }
            int count = Integer.parseInt(countString);
            this.redisTemplate.opsForValue().set("count", String.valueOf(++count));

            // 业务逻辑执行完成之后，释放锁
            // 判断是否是自己的锁，如果是自己的锁才能释放
            if (StringUtils.equals(uuid, this.redisTemplate.opsForValue().get("lock"))){
                // 判断完成之后，过期时间刚好到期，导致该锁自动释放。此时再去执行delete会导致误删
                this.redisTemplate.delete("lock");
            }
        }
    }
}