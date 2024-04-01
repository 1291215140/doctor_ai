package com.example.doctorai.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;
/*
* 库0用来存储用户oepnid和临时token之间的映射
* 库1用来存储用户临时消息
* 库2用来存用户临时文件名字
*
*
*
*
* */
@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Async
    // 设置指定数据库的键值对
    public  void set(String key, String value, int dbIndex) {
        log.info("开始设置");
        // 创建一个新的连接工厂实例，并设置要连接的数据库索引
        RedisConnectionFactory targetConnectionFactory = createTargetConnectionFactory(dbIndex);
        // 切换到指定的数据库
        ((LettuceConnectionFactory) targetConnectionFactory).resetConnection();
        // 设置新的连接工厂
        this.redisTemplate.setConnectionFactory(targetConnectionFactory);
        // 设置键值对
        this.redisTemplate.opsForValue().set(key, value);
        log.info("完成设置");
    }
    // 获取指定数据库的键对应的值
    public String get(String key, int dbIndex) {
        // 创建一个新的连接工厂实例，并设置要连接的数据库索引
        RedisConnectionFactory targetConnectionFactory = createTargetConnectionFactory(dbIndex);
        // 切换到指定的数据库
        ((LettuceConnectionFactory) targetConnectionFactory).resetConnection();
        // 设置新的连接工厂
        this.redisTemplate.setConnectionFactory(targetConnectionFactory);
        // 获取键对应的值
        return this.redisTemplate.opsForValue().get(key);
    }

    // 获取指定数据库的所有键
    public Set<String> getAllKeys(int dbIndex) {
        // 创建一个新的连接工厂实例，并设置要连接的数据库索引
        RedisConnectionFactory targetConnectionFactory = createTargetConnectionFactory(dbIndex);
        // 切换到指定的数据库
        ((LettuceConnectionFactory) targetConnectionFactory).resetConnection();
        // 设置新的连接工厂
        this.redisTemplate.setConnectionFactory(targetConnectionFactory);
        // 获取所有键
        return this.redisTemplate.keys("*");
    }

    // 删除指定数据库的键值对
    public void del(String key, int dbIndex) {
        // 创建一个新的连接工厂实例，并设置要连接的数据库索引
        RedisConnectionFactory targetConnectionFactory = createTargetConnectionFactory(dbIndex);
        // 切换到指定的数据库
        ((LettuceConnectionFactory) targetConnectionFactory).resetConnection();
        // 设置新的连接工厂
        this.redisTemplate.setConnectionFactory(targetConnectionFactory);
        // 删除键值对
        this.redisTemplate.delete(key);
    }

    // 创建一个新的连接工厂实例，并设置要连接的数据库索引
    private RedisConnectionFactory createTargetConnectionFactory(int dbIndex) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
        lettuceConnectionFactory.setDatabase(dbIndex);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }
}
