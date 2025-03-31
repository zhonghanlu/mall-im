package cn.source.framework.config;

import cn.source.framework.config.properties.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * redis配置
 *
 * @author ruoyi
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Resource
    private RedisProperties redisProperties;

    @Resource
    private RedissonProperties redissonProperties;

    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisson() {
        String prefix = REDIS_PROTOCOL_PREFIX;
        if (redisProperties.isSsl()) {
            prefix = REDISS_PROTOCOL_PREFIX;
        }
        Config config = new Config();
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(JsonJacksonCodec.INSTANCE)
                .setTransportMode(redissonProperties.getTransportMode());

        RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
        if (Objects.nonNull(singleServerConfig)) {
            // 使用单机模式
            config.useSingleServer()
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setConnectTimeout(((Long) redisProperties.getTimeout().toMillis()).intValue())
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(StringUtils.isNotBlank(redisProperties.getPassword()) ? redisProperties.getPassword() : null)
                    .setTimeout(singleServerConfig.getTimeout())
                    .setRetryAttempts(singleServerConfig.getRetryAttempts())
                    .setRetryInterval(singleServerConfig.getRetryInterval())
                    .setSubscriptionsPerConnection(singleServerConfig.getSubscriptionsPerConnection())
                    .setClientName(singleServerConfig.getClientName())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionMinimumIdleSize(singleServerConfig.getSubscriptionConnectionMinimumIdleSize())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize())
                    .setDnsMonitoringInterval(singleServerConfig.getDnsMonitoringInterval());
        }
        // 集群配置方式 参考下方注释
        RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
        if (Objects.nonNull(clusterServersConfig)) {
            // 使用集群模式
            config.useClusterServers()
                    .setConnectTimeout(((Long) redisProperties.getTimeout().toMillis()).intValue())
                    .setPassword(StringUtils.isNotBlank(redisProperties.getPassword()) ? redisProperties.getPassword() : null)
                    .setTimeout(clusterServersConfig.getTimeout())
                    .setRetryAttempts(clusterServersConfig.getRetryAttempts())
                    .setRetryInterval(clusterServersConfig.getRetryInterval())
                    .setSubscriptionsPerConnection(clusterServersConfig.getSubscriptionsPerConnection())
                    .setClientName(clusterServersConfig.getClientName())
                    .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                    .setPingConnectionInterval(clusterServersConfig.getPingConnectionInterval())
                    .setSubscriptionConnectionMinimumIdleSize(clusterServersConfig.getSubscriptionConnectionMinimumIdleSize())
                    .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                    .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                    .setDnsMonitoringInterval(clusterServersConfig.getDnsMonitoringInterval())
                    .setFailedSlaveReconnectionInterval(clusterServersConfig.getFailedSlaveReconnectionInterval())
                    .setScanInterval(clusterServersConfig.getScanInterval())
                    .setReadMode(clusterServersConfig.getReadMode())
                    .setSubscriptionMode(clusterServersConfig.getSubscriptionMode())
                    .setNodeAddresses(redisProperties.getCluster().getNodes());
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.info("初始化 redis 配置");
        return redissonClient;
    }


    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }
}
