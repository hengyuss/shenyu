package org.apache.shenyu.plugin.record.body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class BodyWriter {

    private static final Logger LOG = LoggerFactory.getLogger(BodyWriter.class);

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    private final WritableByteChannel channel = Channels.newChannel(stream);


    public void write(final ByteBuffer buffer) {
        if (!isClosed.get()) {
            try {
                channel.write(buffer);
            } catch (IOException e) {
                isClosed.compareAndSet(false, true);
                LOG.error("write buffer Failed.", e);
            }
        }
    }


    public boolean isEmpty() {
        return stream.size() == 0;
    }


    public int size() {
        return stream.size();
    }

    public String output() {
        if (isEmpty()) {
            return "";
        }
        try {
            isClosed.compareAndSet(false, true);
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error("Write failed: ", e);
            return "Write failed: " + e.getMessage();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.error("Close stream error: ", e);
            }
            try {
                channel.close();
            } catch (IOException e) {
                LOG.error("Close channel error: ", e);
            }
        }
    }

}
