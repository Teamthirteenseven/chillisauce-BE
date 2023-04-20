package com.example.chillisauce.config;

import com.example.chillisauce.spaces.dto.FloorResponseDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//java 8의 날짜/시간 지원모듈
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //날짜가 ISO-8601 형식의 문자열로 직렬화

        Jackson2JsonRedisSerializer<List<FloorResponseDto>> floorListSerializer = new Jackson2JsonRedisSerializer<> //객체를 직렬화
                (objectMapper.getTypeFactory().constructCollectionType(List.class, FloorResponseDto.class));
        floorListSerializer.setObjectMapper(objectMapper);

        Jackson2JsonRedisSerializer<List<SpaceResponseDto>> spaceListSerializer = new Jackson2JsonRedisSerializer<>
                (objectMapper.getTypeFactory().constructCollectionType(List.class, SpaceResponseDto.class));
        spaceListSerializer.setObjectMapper(objectMapper);

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory);
        //RedisCacheManagerBuilder를 생성합니다. 이때, 인자로 전달된 redisConnectionFactory를 사용하여 Redis와의 연결을 설정합니다.

        builder.withCacheConfiguration("FloorResponseDtoList",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))//TTL(Time To Live)을 1시간으로 설정
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(floorListSerializer))
        );

        builder.withCacheConfiguration("SpaceResponseDtoList",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(spaceListSerializer))
        );


        return builder.build();
    }

}