package com.jack.redis_test2.service;

import cn.hutool.core.util.IdUtil;
import com.jack.redis_test2.util.MyRedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class InventoryService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String port = "8081";

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    public String sale() {
        String retMessage = "";
        Lock myRedisLock=distributedLockFactory.getDistributedLock("redis");
        myRedisLock.lock();
        try {
            //抢到了锁
            //1. 查询库存信息
            String inventory001 = stringRedisTemplate.opsForValue().get("inventory001");
            //2. 判断库存是否足够
            Integer inventory = inventory001 == null ? 0 : Integer.valueOf(inventory001);
            //3. 扣减库存
            if (inventory > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventory));
                retMessage = "成功卖出一个商品，剩余库存" + inventory;
                System.out.println(retMessage + "\t" + "服务器端口" + port);
            } else {
                retMessage = "商品库存不足";
            }
        } finally {
            myRedisLock.unlock();
        }
        return retMessage + "\t" + "服务器端口" + port;
    }
}
