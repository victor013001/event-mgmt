package co.com.techtest.sqs.listener.dto.log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record StandardLogData<T>(String code, String message, String identifies, String date,
                                 T data, List<ErrorLogDetail> errors) {
}
