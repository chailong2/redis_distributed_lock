package com.jack.mybatis_plus;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //1. 定义一个常量
    public static final int _1W = 10000;
    //2. 定义Guava的初始容量
    public static final int SIZE = 100 * _1W;
    //3. 误判率，它越小误判的数量也就越少（思考：误判率是否是无限小？）
    public static double fpp = 0.03;
    //4. 创建布隆过滤器
    private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), SIZE, fpp);

    @Test
    public void testGUavaWithBloomFilter() {
        //1. 先让bloomfilter加入100万白名单数据
        for (int i = 0; i < SIZE; i++) {
            bloomFilter.put(i);
        }
        //2. 故意取10w个不在合法范围内的数据，进行误判率的演示
        ArrayList<Integer> list = new ArrayList<>(10 * _1W);
        //3. 验证
        for (int i = SIZE + 1; i <= SIZE + (10 * _1W); i++) {
            if(bloomFilter.mightContain(i)){
                System.out.println(i+"被误判了");
                list.add(i);
            }
        }
        System.out.println("误判总数量："+list.size());
    }
}
