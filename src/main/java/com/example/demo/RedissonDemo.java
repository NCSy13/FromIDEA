package com.example.demo;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedissonDemo {

    private static final String REDIS_CACHE_KEY = "ncs.id:";
    @Autowired
    private RedissonClient redissonClient;
//加了synchronized是同步锁，整个test方法执行完，再执行其他的test
//public synchronized void test(String id) {
    public void test (String id){
        System.out.println("id:" + id + "=======" + Thread.currentThread().getName() + " coming");

        RLock lock = redissonClient.getLock(REDIS_CACHE_KEY + id);

        try {
            //设置lock超时时间 2 秒
            lock.lock(2, TimeUnit.SECONDS);
            //以下是业务逻辑
            System.out.println("id:" + id + "=======" + Thread.currentThread().getName() + "is locked");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
            System.out.println("id:" + id + "=======" + Thread.currentThread().getName() + "is unlock");
        }
    }
}
