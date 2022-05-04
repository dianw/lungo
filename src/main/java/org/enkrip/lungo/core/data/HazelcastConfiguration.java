package org.enkrip.lungo.core.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

@Configuration
public class HazelcastConfiguration {
    private static final String DEFAULT_FLAKE_ID_GEN_NAME = "defaultFlakeIdGen";

    @Bean
    @Primary
    public FlakeIdGenerator flakeIdGenerator(HazelcastInstance hzInstance) {
        return hzInstance.getFlakeIdGenerator(DEFAULT_FLAKE_ID_GEN_NAME);
    }

    @Bean
    public FlakeIdentifierGenerator defaultIdGenerator(FlakeIdGenerator flakeIdGenerator) {
        return new FlakeIdentifierGenerator(flakeIdGenerator);
    }
}
