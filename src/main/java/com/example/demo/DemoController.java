package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("path1") 这个path1 是url的第一层路径， 可能会有多个第一层路径
@RequestMapping("/yanbin")
public class DemoController {

    @Autowired
    private RedissonDemo redissonDemo;
//@GetMapping("path2") 这个 path2 是url的第二层路径，路径1下面可能有多个路径2
@GetMapping("/test")
    public String test(String id){
        redissonDemo.test(id);
        return "test";
    }
    @GetMapping("/test2")
    public String test2(String id){
        redissonDemo.test(id);
        return "test2";
    }
}
