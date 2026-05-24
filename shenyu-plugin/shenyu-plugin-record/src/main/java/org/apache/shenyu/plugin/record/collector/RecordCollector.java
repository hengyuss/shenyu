package org.apache.shenyu.plugin.record.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shenyu.common.concurrent.MemorySafeTaskQueue;
import org.apache.shenyu.common.concurrent.ShenyuThreadFactory;
import org.apache.shenyu.common.concurrent.ShenyuThreadPoolExecutor;
import org.apache.shenyu.common.config.ShenyuConfig;
import org.apache.shenyu.common.constant.Constants;
import org.apache.shenyu.common.utils.Singleton;
import org.apache.shenyu.common.utils.ThreadUtils;
import org.apache.shenyu.plugin.record.config.RecordCollectConfig;
import org.apache.shenyu.plugin.record.entity.ShenyuHttpRequestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordCollector {

    private int bufferSize;

    private static final Logger LOG = LoggerFactory.getLogger(RecordCollector.class);

    private BlockingQueue<ShenyuHttpRequestRecord> bufferQueue;

    private final AtomicBoolean started = new AtomicBoolean(true);

    private long lastPushTime;

    private final ObjectMapper mapper = new ObjectMapper();

    public void start(){
        bufferSize = RecordCollectConfig.INSTANCE.getRecordConfig().getBufferQueueSize();
        bufferQueue = new LinkedBlockingDeque<>(bufferSize);
        ShenyuConfig config = Optional.ofNullable(Singleton.INST.get(ShenyuConfig.class)).orElse(new ShenyuConfig());
        final ShenyuConfig.SharedPool sharedPool = config.getSharedPool();
        ShenyuThreadPoolExecutor threadExecutor = new ShenyuThreadPoolExecutor(sharedPool.getCorePoolSize(),
                sharedPool.getMaximumPoolSize(), sharedPool.getKeepAliveTime(), TimeUnit.MILLISECONDS,
                new MemorySafeTaskQueue<>(Constants.THE_256_MB),
                ShenyuThreadFactory.create(config.getSharedPool().getPrefix(), true),
                new ThreadPoolExecutor.AbortPolicy());
        started.set(true);
        threadExecutor.execute(this::consume);
    }

    public void collect(final ShenyuHttpRequestRecord record) {
        if (Objects.isNull(record)) {
            return;
        }
        if (bufferQueue.size() < bufferSize){
            bufferQueue.add(record);
        }
    }

    private void consume() {
        while (started.get()) {
            int diffTimeMSForPush = 100;
            try {
                List<ShenyuHttpRequestRecord> records = new ArrayList<>();
                int barchSize = 100;
                processBufferQueue(bufferQueue, barchSize, records, diffTimeMSForPush, lastPushTime);
            } catch (Throwable t){
                LOG.error("ShenyuHttpRequestRecord collect log error", t);
                ThreadUtils.sleep(TimeUnit.MILLISECONDS, diffTimeMSForPush);
            }
        }
    }

    private void processBufferQueue(final BlockingQueue<ShenyuHttpRequestRecord> bufferQueue, final int barchSize,
                                    final List<ShenyuHttpRequestRecord> records, final int diffTimeMSForPush, final long lastPushTime) {
        int size = bufferQueue.size();
        long time = System.currentTimeMillis();
        long timeDiffMs = time - lastPushTime;
        if(size >= barchSize || timeDiffMs >= diffTimeMSForPush){
            bufferQueue.drainTo(records, barchSize);
            writeToFile(records);
            this.lastPushTime = time;
        } else {
            ThreadUtils.sleep(TimeUnit.MILLISECONDS, diffTimeMSForPush);
        }
    }

    private void writeToFile(List<ShenyuHttpRequestRecord> records) {
        try {
            RecordCollectConfig.RecordConfig config = RecordCollectConfig.INSTANCE.getRecordConfig();
            String baseDir = RecordCollectConfig.INSTANCE.getRecordConfig().getStoragePath();
            Path dirPath = Paths.get(baseDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            StringBuilder sb = new StringBuilder();
            for (ShenyuHttpRequestRecord record : records) {
                // 注意这里不要 pretty print，压缩成一行
                sb.append(mapper.writeValueAsString(record)).append(System.lineSeparator());
            }

            String fileName = "record-" + config.getTaskId() + ".jsonl";
            Path filePath = Paths.get(baseDir, fileName);

            Files.write(filePath, sb.toString().getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            LOG.info("[RecordPlugin] success save {} records", records.size());
        } catch (Exception e) {
            LOG.error("[RecordPlugin] write fail", e);
        }
    }


}
