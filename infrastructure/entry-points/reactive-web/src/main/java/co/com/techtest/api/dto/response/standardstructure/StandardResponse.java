package co.com.techtest.api.dto.response.standardstructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record StandardResponse<T>(String code, String message, String identifies, String date,
                                  T data, List<ErrorDetail> errors) {
}
