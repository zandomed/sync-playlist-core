package com.zandome.syncplaylist.shared.infra.http.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String message,
        String code,
        String path,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        Integer status,
        String error,
        List<FieldError> fieldErrors,
        Map<String, Object> details,
        String traceId
) {
    
    @Builder
    public record FieldError(
            String field,
            String message,
            Object rejectedValue,
            String code
    ) {}
}