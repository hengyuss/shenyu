package org.apache.shenyu.plugin.record.utils;

import com.google.common.collect.Sets;
import org.apache.shenyu.common.utils.JsonUtils;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordUtils {

    private static final Set<String> BINARY_TYPE_LIST = Sets.newHashSet("image", "multipart", "cbor",
            "octet-stream", "pdf", "javascript", "css");

    /**
     * judge whether is binary type.
     *
     * @param headers request or response header
     * @return whether binary type
     */
    public static boolean isNotBinaryType(final HttpHeaders headers) {
        return Optional.ofNullable(headers).map(HttpHeaders::getContentType)
                .map(contentType -> !BINARY_TYPE_LIST.contains(contentType.getType())
                        && !BINARY_TYPE_LIST.contains(contentType.getSubtype()))
                .orElse(true);
    }

    /**
     * get request header string.
     *
     * @param headers request headers
     * @return header string
     */
    public static Map<String, String> getHeaders(final HttpHeaders headers) {
        Map<String, String> map = headers.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(",", entry.getValue())));
        return map;
    }
}
