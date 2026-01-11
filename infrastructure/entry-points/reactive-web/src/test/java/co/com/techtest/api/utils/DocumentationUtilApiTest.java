package co.com.techtest.api.utils;

import co.com.techtest.model.util.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentationUtilApiTest {

    @Test
    void shouldDocumentOperationWithValidParameters() {
        Builder mockBuilder = mock(Builder.class);
        when(mockBuilder.tag(any())).thenReturn(mockBuilder);
        when(mockBuilder.operationId(any())).thenReturn(mockBuilder);
        when(mockBuilder.summary(any())).thenReturn(mockBuilder);
        when(mockBuilder.tags(any())).thenReturn(mockBuilder);
        when(mockBuilder.requestBody(any())).thenReturn(mockBuilder);
        when(mockBuilder.response(any())).thenReturn(mockBuilder);

        Consumer<Builder> result = DocumentationUtilApi.document(OperationType.CREATE_EVENT, String.class);

        assertNotNull(result);
        result.accept(mockBuilder);
    }

    @Test
    void shouldDocumentOperationWithNullClass() {
        Builder mockBuilder = mock(Builder.class);
        when(mockBuilder.tag(any())).thenReturn(mockBuilder);
        when(mockBuilder.operationId(any())).thenReturn(mockBuilder);
        when(mockBuilder.summary(any())).thenReturn(mockBuilder);
        when(mockBuilder.tags(any())).thenReturn(mockBuilder);
        when(mockBuilder.requestBody(any())).thenReturn(mockBuilder);
        when(mockBuilder.response(any())).thenReturn(mockBuilder);

        Consumer<Builder> result = DocumentationUtilApi.document(OperationType.CREATE_EVENT, null);

        assertNotNull(result);
        result.accept(mockBuilder);
    }
}
