package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringRedisTest extends DemoApplicationTests {
//注入RedissonDemo
    @Autowired
    private RedissonDemo redissonDemo;

    @Test
    public void test() throws InterruptedException {
        //创建多个线程
        ThreadTest test1 = new ThreadTest("1");
        ThreadTest test2 = new ThreadTest("2");
        ThreadTest test3 = new ThreadTest("3");
        ThreadTest test4 = new ThreadTest("4");
        //多线程并发
        new Thread(test1,"thread 01").start();
        new Thread(test2,"thread 02").start();
        new Thread(test3,"thread 03").start();
        new Thread(test4,"thread 04").start();
    }

    class ThreadTest extends Thread {

        private String id;

        ThreadTest(){

        }

        ThreadTest(String id) {
            this.id = id;
        }

//Tread.start()方法会来调用run()方法
        @Override
        public void run() {
            redissonDemo.test(id);
        }
    }
}
