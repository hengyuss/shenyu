package org.apache.shenyu.plugin.record.body;

import org.apache.shenyu.plugin.record.entity.ShenyuHttpRequestRecord;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;
import reactor.util.annotation.NonNull;

public class RecordServerHttpRequest<L extends ShenyuHttpRequestRecord> extends ServerHttpRequestDecorator {

    private final L record;
    private static final String RECORD_CONTEXT_KEY = "record_context_key";

    public RecordServerHttpRequest(final ServerHttpRequest delegate, final L record) {
        super(delegate);
        this.record = record;
    }


    @Override
    @NonNull
    public Flux<DataBuffer> getBody() {
        BodyWriter writer = new BodyWriter();
        return super.getBody().doOnNext(dataBuffer -> {
            try (DataBuffer.ByteBufferIterator bufferIterator = dataBuffer.readableByteBuffers()) {
                bufferIterator.forEachRemaining(byteBuffer -> writer.write(byteBuffer.asReadOnlyBuffer()));
            }
        }).doFinally(signal -> {
            String body = writer.output();
            record.setRequestBody(body);
        });
    }
}
