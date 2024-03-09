package com.jack.redis_test1.service;

import cn.hutool.core.util.IdUtil;
import com.jack.redis_test1.util.MyRedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class DistributedLockFactory {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private String lockName;

    private String UUID;

    public DistributedLockFactory(){
        this.UUID= IdUtil.simpleUUID();
    }
    public Lock getDistributedLock(String lockType) {
        if (lockType == null) return null;
        if (lockType.equalsIgnoreCase("REDIS")) {
            this.lockName = "zzyyredisLock";
            return new MyRedisLock(stringRedisTemplate, lockName,UUID);
        } else if (lockType.equalsIgnoreCase("ZOOKEEPER")){
            this.lockName = "zzyZOOKEEPERLock";
            //TODO Zookeeper
        }
        return null;
    }
}
