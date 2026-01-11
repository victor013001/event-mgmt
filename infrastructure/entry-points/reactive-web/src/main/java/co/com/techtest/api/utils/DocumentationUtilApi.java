package co.com.techtest.api.utils;

import co.com.techtest.api.dto.response.standardstructure.StandardResponse;
import co.com.techtest.model.util.enums.OperationType;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;

@UtilityClass
public class DocumentationUtilApi {

    private static final String EVENT_MGMT_MS_TAG = "eventmgmt";
    private static final String SUCCESS_DESCRIPTION = "Successful Operation";
    private static final String ERROR_DESCRIPTION = "Error Response";

    public static Consumer<Builder> document(OperationType operation, Class<?> clazz) {
        return ops -> ops.tag(EVENT_MGMT_MS_TAG)
                .operationId(operation.getKvRequest())
                .summary(operation.getNameRequest())
                .tags(new String[]{EVENT_MGMT_MS_TAG})
                .requestBody(requestBodyBuilder().implementation(clazz))
                .response(responseBuilder()
                        .responseCode("200")
                        .description(SUCCESS_DESCRIPTION)
                        .implementation(StandardResponse.class)
                )
                .response(responseBuilder()
                        .responseCode("400")
                        .description(ERROR_DESCRIPTION)
                        .implementation(StandardResponse.class)
                ).response(responseBuilder()
                        .responseCode("500")
                        .description(ERROR_DESCRIPTION)
                        .implementation(StandardResponse.class)
                );
    }
}
