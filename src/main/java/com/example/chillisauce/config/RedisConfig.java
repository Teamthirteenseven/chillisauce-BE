package com.example.chillisauce.config;

import com.example.chillisauce.security.GrantedAuthorityDeserializer;
import com.example.chillisauce.security.GrantedAuthoritySerializer;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.spaces.dto.response.SpaceResponseDto;
import com.example.chillisauce.users.dto.response.UserListResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import org.springframework.security.core.GrantedAuthority;

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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<List<FloorResponseDto>> floorSerializer = new Jackson2JsonRedisSerializer<>
                (objectMapper.getTypeFactory().constructCollectionType(List.class, FloorResponseDto.class));
        floorSerializer.setObjectMapper(objectMapper);

        Jackson2JsonRedisSerializer<List<SpaceResponseDto>> spaceSerializer = new Jackson2JsonRedisSerializer<>
                (objectMapper.getTypeFactory().constructCollectionType(List.class, SpaceResponseDto.class));
        spaceSerializer.setObjectMapper(objectMapper);

        /* 성능테스트 2. 캐싱 / 논캐싱 비교*/
        Jackson2JsonRedisSerializer<UserListResponseDto> userSerializer = new Jackson2JsonRedisSerializer<>
                (objectMapper.getTypeFactory().constructType(UserListResponseDto.class));
        userSerializer.setObjectMapper(objectMapper);
        /* 성능테스트 2. 캐싱 / 논캐싱 비교*/


        Jackson2JsonRedisSerializer<UserDetailsImpl> userDetailsSerializer = new Jackson2JsonRedisSerializer<>
                (objectMapper.getTypeFactory().constructType(UserDetailsImpl.class));
        userDetailsSerializer.setObjectMapper(objectMapper);
        SimpleModule grantedAuthorityModule = new SimpleModule();
        grantedAuthorityModule.addSerializer(GrantedAuthority.class, new GrantedAuthoritySerializer());
        grantedAuthorityModule.addDeserializer(GrantedAuthority.class, new GrantedAuthorityDeserializer());
        objectMapper.registerModule(grantedAuthorityModule);

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory);

        builder.withCacheConfiguration("FloorResponseDtoList",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(60))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(floorSerializer))
        );

        builder.withCacheConfiguration("SpaceResponseDtoList",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(60))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(spaceSerializer))
        );

        /* 성능테스트 2. 캐싱 / 논캐싱 비교*/
        builder.withCacheConfiguration("UserResponseDtoList",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(60))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(userSerializer))
        );
        /* 성능테스트 2. 캐싱 / 논캐싱 비교*/

        builder.withCacheConfiguration("UserDetails",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(120))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(userDetailsSerializer))
        );

        return builder.build();
    }

}