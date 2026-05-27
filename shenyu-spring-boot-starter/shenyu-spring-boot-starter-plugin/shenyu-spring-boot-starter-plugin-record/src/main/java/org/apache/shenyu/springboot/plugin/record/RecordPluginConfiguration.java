package org.apache.shenyu.springboot.plugin.record;

import org.apache.shenyu.plugin.record.RecordPlugin;
import org.apache.shenyu.plugin.record.handler.RecordPluginDataHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecordPluginConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RecordPlugin recordPlugin() {
        return new RecordPlugin();
    }

    @Bean
    public RecordPluginDataHandler recordPluginDataHandler() {
        return new RecordPluginDataHandler();
    }
}
