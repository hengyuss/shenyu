package org.apache.shenyu.plugin.record.config;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class RecordCollectConfig {

    public final static RecordCollectConfig INSTANCE = new RecordCollectConfig();


    private RecordConfig recordConfig;

    public RecordConfig getRecordConfig() {
        return Optional.ofNullable(recordConfig).orElse(new RecordConfig());
    }

    public void setRecordConfig(RecordConfig recordConfig) {
        this.recordConfig = recordConfig;
    }

    public static class RecordConfig {

        private String storagePath = System.getProperty("user.home") + File.separator + "shenyu-records" + File.separator;

        private Integer maxBodySize = 524288;

        private int bufferQueueSize = 50000;

        private String taskId = "default-task";

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }


        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }

        public Integer getMaxBodySize() {
            return maxBodySize;
        }

        public void setMaxBodySize(Integer maxBodySize) {
            this.maxBodySize = maxBodySize;
        }

        public int getBufferQueueSize() {
            return bufferQueueSize;
        }

        public void setBufferQueueSize(int bufferQueueSize) {
            this.bufferQueueSize = bufferQueueSize;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RecordConfig that = (RecordConfig) o;
            return Objects.equals(storagePath, that.storagePath) &&
                    Objects.equals(maxBodySize, that.maxBodySize);
        }

        @Override
        public int hashCode() {
            return Objects.hash(storagePath, maxBodySize);
        }
    }
}
