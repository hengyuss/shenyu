package org.apache.shenyu.plugin.record;

import org.apache.shenyu.plugin.api.ShenyuPlugin;
import org.apache.shenyu.plugin.api.ShenyuPluginChain;
import org.apache.shenyu.plugin.record.body.RecordServerHttpRequest;
import org.apache.shenyu.plugin.record.body.RecordServerHttpResponse;
import org.apache.shenyu.plugin.record.entity.ShenyuHttpRequestRecord;
import org.apache.shenyu.plugin.record.utils.RecordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RecordPlugin implements ShenyuPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(RecordPlugin.class);

    private static final String REPLAY_MARK_HEADER = "X-Shenyu-Replay";

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, ShenyuPluginChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        ShenyuHttpRequestRecord record = new ShenyuHttpRequestRecord();
        record.setMethod(request.getMethod().name());
        record.setQueryParams(request.getURI().getQuery());
        record.setRequestUri(request.getURI().getPath());
        record.setRequestHeaders(RecordUtils.getHeaders(request.getHeaders()));


        RecordServerHttpRequest<ShenyuHttpRequestRecord> requestRecordRecordServerHttpRequest = new RecordServerHttpRequest<>(request, record);
        RecordServerHttpResponse<ShenyuHttpRequestRecord> shenyuRequestRecordRecordServerHttpResponse = new RecordServerHttpResponse<>(exchange.getResponse(), record);
        shenyuRequestRecordRecordServerHttpResponse.setExchange(exchange);
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(requestRecordRecordServerHttpRequest)
                .response(shenyuRequestRecordRecordServerHttpResponse)
                .build();


        return chain.execute(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
