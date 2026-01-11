package co.com.techtest.api.utils;

import co.com.techtest.api.dto.response.standardstructure.StandardResponse;
import co.com.techtest.model.util.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentationUtilApiTest {

    @Test
    void shouldCreateDocumentationConsumer() {
        Consumer<Builder> consumer = DocumentationUtilApi.document(OperationType.GET_TICKET, StandardResponse.class);

        assertNotNull(consumer);
    }

    @Test
    void shouldCreateDocumentationForAllOperationTypes() {
        for (OperationType operation : OperationType.values()) {
            Consumer<Builder> consumer = DocumentationUtilApi.document(operation, StandardResponse.class);
            assertNotNull(consumer);
        }
    }
}
