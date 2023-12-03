package com.example.springsecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@EnableJpaRepositories // Redis 를 사용한다고 명시해 주는 어노테이션
public class RedisConfig {// Redis 와의 연결 정보를 설정하고, Redis 데이터를 저장하고 조회하는데 사용되는 RedisTemplate 객체 클래스

    private final RedisProperties redisProperties; //Redis 서버와의 연결정보를 저장하는 객체. redis 의 host 와 port 를 yml 파일에서 가져온다.

    //RedisProperties 로 yml 에 저장한 host, post 를 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    //Serializer 설정으로 redis-cli 를 통해 직접 데이터를 조회살 수 있도록 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}
