package com.moviex.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Milvus Configuration
 * Configures the Milvus client connection and collection schemas
 */
@Slf4j
@Configuration
public class MilvusConfig {

    @Value("${milvus.host:localhost}")
    private String milvusHost;

    @Value("${milvus.port:19530}")
    private int milvusPort;

    public String getHost() {
        return milvusHost;
    }

    public int getPort() {
        return milvusPort;
    }

    /**
     * Create Milvus client bean
     * Uses gRPC protocol for communication with Milvus standalone
     */
    @Bean
    public MilvusServiceClient milvusClient() {
        try {
            log.info("Connecting to Milvus at {}:{}", milvusHost, milvusPort);
            
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(milvusHost)
                    .withPort(milvusPort)
                    .build();
            
            MilvusServiceClient client = new MilvusServiceClient(connectParam);
            
            log.info("Successfully connected to Milvus");
            return client;
        } catch (Exception e) {
            log.error("Failed to connect to Milvus", e);
            throw new RuntimeException("Failed to initialize Milvus client", e);
        }
    }
}
