package org.apache.shenyu.plugin.record.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shenyu.plugin.base.utils.MediaTypeUtils;
import org.apache.shenyu.plugin.record.collector.RecordCollector;
import org.apache.shenyu.plugin.record.config.RecordCollectConfig;
import org.apache.shenyu.plugin.record.entity.ShenyuHttpRequestRecord;
import org.apache.shenyu.plugin.record.utils.RecordUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class RecordServerHttpResponse<L extends ShenyuHttpRequestRecord> extends ServerHttpResponseDecorator {

    private static final Logger LOG = LoggerFactory.getLogger(RecordServerHttpResponse.class);
    private final L record;

    private ServerWebExchange exchange;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RecordCollector recordCollector;


    public RecordServerHttpResponse(final ServerHttpResponse delegate, final L record, final RecordCollector recordCollector) {
        super(delegate);
        this.record = record;
        this.recordCollector = recordCollector;
    }

    public void setExchange(final ServerWebExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(appendResponse(body));
    }


    @NonNull
    private Flux<? extends DataBuffer> appendResponse(Publisher<? extends DataBuffer> body) {
        if (Objects.nonNull(getStatusCode())) {
            record.setStatus(getStatusCode().value());
        }
        record.setResponseHeaders(RecordUtils.getHeaders(getHeaders()));
        final MediaType mediaType = exchange.getResponse().getHeaders().getContentType();
        if (MediaTypeUtils.isByteType(mediaType)) {
            return Flux.from(body);
        }
        BodyWriter writer = new BodyWriter();
        return Flux.from(body).doOnNext(buffer -> {
            if (RecordUtils.isNotBinaryType(getHeaders())) {
                try (DataBuffer.ByteBufferIterator bufferIterator = buffer.readableByteBuffers()) {
                    bufferIterator.forEachRemaining(byteBuffer -> writer.write(byteBuffer.asReadOnlyBuffer()));
                }
            }
        }).doFinally(signal -> {
            String respBody = writer.output();
            record.setResponseBody(respBody);
            recordCollector.collect(record);
        });
    }

    private void saveRecordLocal() throws IOException {
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(record);
        Path path = Paths.get(RecordCollectConfig.INSTANCE.getRecordConfig().getStoragePath() + "test.json");

        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.write(path, json.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}
