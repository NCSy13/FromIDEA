package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//step2

//访问路径本机：http://localhost:8080/yanbin/test?id=1
//其他入口：ip:8080/yanbin/test

@RestController
//@RequestMapping("path1") 这个path1 是url的第一层路径， 可能会有多个第一层路径
@RequestMapping("/yanbin")
public class DemoController {

//测试1
    @Autowired
    private RedissonDemo redissonDemo;

    @Autowired
    private DistributedLockHandler distributedLockHandler;

//@GetMapping("path2") 这个 path2 是url的第二层路径，路径1下面可能有多个路径2
@GetMapping("/test")
    public String test(String id){
        redissonDemo.test(id);
        return "test";
    }@GetMapping("/test2")
    public String test2(String id){
        redissonDemo.test(id);
        return "test2";
    }

//测试2

//    通过这种方式创建的分布式锁存在以下问题：
//
//    高并发的情况下，如果两个线程同时进入循环，可能导致加锁失败。
//    SETNX 是一个耗时操作，因为它需要判断 Key 是否存在，因为会存在性能问题。
    @GetMapping("/test3")
    public String testDistributedLockHandler(){
    //创建锁
        Lock lock = new Lock("lynn","min");
        //尝试加锁
        if(distributedLockHandler.tryLock(lock)){
            try{
                //为了演示效果，睡眠5s
                System.out.println("方法执行");
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
            distributedLockHandler.releaseLock(lock);
        }
        return "第二种redis实现";
    }

    @Autowired
    private DistributedLocker distributedLocker;
//Redlock 是 Redis 官方推荐的一种方案，因此可靠性比较高。
    @GetMapping("/test4")
    public String test4()throws Exception{
            distributedLocker.lock("test",new AquiredLockWorker<Object>() {

            @Override
            public Object invokeAfterLockAquire() {
                try {
                    System.out.println("执行方法！");
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

        });
        return "Redisson Redlock 实现!";
    }
}
