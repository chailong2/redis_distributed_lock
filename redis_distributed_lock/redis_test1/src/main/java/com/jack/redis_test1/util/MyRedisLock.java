package com.jack.redis_test1.util;

import cn.hutool.core.util.IdUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyRedisLock implements Lock {

    private StringRedisTemplate stringRedisTemplate;

    private String lockName;

    private String uuidValue;

    private long expiretime;


    public MyRedisLock(StringRedisTemplate stringRedisTemplate, String lockName, String UUID) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuidValue= UUID+":"+Thread.currentThread().getId();
        System.out.println(UUID);
        this.expiretime=30L;
    }

    @Override
    public void lock() {
        tryLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            tryLock(-1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (time == -1L) {
            String script=
                    "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then " +
                        "redis.call('hincrby',KEYS[1],ARGV[1],1) " +
                        "redis.call('expire',KEYS[1],ARGV[2]) " +
                        "return 1 " +
                    "else " +
                        "return 0 " +
                    "end ";
            while (!stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockName), uuidValue, String.valueOf(expiretime))) {
                //没有抢到锁，进行重试
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            resetExpire();
            return true;
        }
        return false;
    }

    private void resetExpire() {
        String scripts="if redis.call('HEXISTS', KEYS[1], ARGV[1]) == 1 then " +
                "return redis.call('expire',KEYS[1],ARGV[2]); " +
                "else " +
                "return 0 " +
                "end ";
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (stringRedisTemplate.execute(new DefaultRedisScript<>(scripts,Boolean.class), Arrays.asList(lockName),uuidValue,String.valueOf(expiretime))) {
                    resetExpire();
                }
            }
        },(this.expiretime * 1000/3));
    }

    @Override
    public void unlock() {
        String script="if redis.call('hexists', KEYS[1], ARGV[1]) == 0 then " +
                        "return nil " +
                      "elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0 then " +
                        "return redis.call('del', KEYS[1]) " +
                      "else " +
                        "return 0 " +
                      "end";
        Long flag = stringRedisTemplate.execute(new DefaultRedisScript<>(script,Long.class), Arrays.asList(lockName), uuidValue);
        if (flag == null) {
            throw  new RuntimeException("锁不存在");
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
