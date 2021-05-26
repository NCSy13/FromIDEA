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
//    @Autowired
//    private RedissonDemo redissonDemo;

    @Autowired
    private DistributedLockHandler distributedLockHandler;
//
////@GetMapping("path2") 这个 path2 是url的第二层路径，路径1下面可能有多个路径2
//@GetMapping("/test")
//    public String test(String id){
//        redissonDemo.test(id);
//        return "test";
//    }@GetMapping("/test2")
//    public String test2(String id){
//        redissonDemo.test(id);
//        return "test2";
//    }
//
////测试2

@GetMapping("/test3")
    public String testDistributedLockHandler(){
        Lock lock = new Lock("lynn","min");
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
}
