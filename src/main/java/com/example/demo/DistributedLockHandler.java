package com.example.demo;
/*
spring-data-Redis为spring-data模块中对redis的支持部分，简称为“SDR”

利用StringRedisTemplate去实现一个分布式锁
 */
import org.apache.commons.lang.StringUtils;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


import java.util.concurrent.TimeUnit;
//import java.util.logging.Logger;

@Component
public class DistributedLockHandler {
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(DistributedLockHandler.class);
    private final static long LOCK_EXPIRE = 30 * 1000L;//单个业务持有锁时间30s，防止死锁 , 30s之后如果业务还没执行完，锁也会被释放
    private final static long LOCK_TRY_INTERVAL = 30L; // 默认30ms尝试一次
    private final static long LOCK_TRY_TIMEOUT = 2 * 1000L; //默认尝试20s
   // private int trytimes = 0;             //尝试次数

    @Autowired
    private StringRedisTemplate template;
    /**
     * 尝试获取全局锁
     *
     * @param lock 锁的名称
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(Lock lock) {
        return getLock(lock, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock    锁的名称
     * @param timeout 获取超时时间 单位ms
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(Lock lock, long timeout) {
        return getLock(lock, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock        锁的名称
     * @param timeout     获取锁的超时时间
     * @param tryInterval 多少毫秒尝试获取一次
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(Lock lock, long timeout, long tryInterval) {
        return getLock(lock, timeout, tryInterval, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取锁的超时时间
     * @param tryInterval    多少毫秒尝试获取一次
     * @param lockExpireTime 锁的过期
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
        return getLock(lock, timeout, tryInterval, lockExpireTime);
    }


    /**
     * 操作redis获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取的超时时间
     * @param tryInterval    多少ms尝试一次
     * @param lockExpireTime 获取成功后锁的过期时间
     * @return true 获取成功，false获取失败
     */
    public boolean getLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
        try {
            //判断锁的名字和值不为空
            if (StringUtils.isEmpty(lock.getName()) || StringUtils.isEmpty(lock.getValue())) {
                return false;
            }
            long startTime = System.currentTimeMillis();
            do{
                //通过锁的Name判断是否已经存在锁
                if (!template.hasKey(lock.getName())) {
                    ValueOperations<String, String> ops = template.opsForValue();
                    //设置变量值的过期时间
                    ops.set(lock.getName(), lock.getValue(), lockExpireTime, TimeUnit.MILLISECONDS);
                    return true;
                } else {//存在锁
                   // trytimes++;
                    //System.out.println(lock.getName()+"锁已经存在"+",失败第" + trytimes + "次");
                    System.out.println(lock.getName()+"锁已经存在");
                    //logger.debug("lock is exist!！！");
                }
                if (System.currentTimeMillis() - startTime > timeout) {//尝试超过了设定值之后直接跳出循环
                    System.out.println("锁超时");
                    return false;
                }

                Thread.sleep(tryInterval);
            }
            while (template.hasKey(lock.getName())) ;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            //logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void releaseLock(Lock lock) {
        if (!StringUtils.isEmpty(lock.getName())) {
            template.delete(lock.getName());
            System.out.println(lock.getName()+"锁已经释放");
        }
    }

}

