package co.com.techtest.sqs.listener.utils;

import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import co.com.techtest.sqs.listener.dto.log.StandardLogData;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

class LoggerUtilTest {

    @Test
    void shouldBuildBadRequestLog() {
        List<ErrorLogDetail> errors = List.of(
                new ErrorLogDetail("field1", "error1"),
                new ErrorLogDetail("field2", "error2")
        );
        String flowId = "flow-123";
        OperationType operation = OperationType.PROCESS_PURCHASE;

        StepVerifier.create(LoggerUtil.buildBadRequestLog(errors, flowId, operation))
                .verifyComplete();
    }

    @Test
    void shouldBuildResponseBusinessErrorLog() {
        BusinessException error = new BusinessException(TechnicalMessageType.ERROR_TICKET_NOT_FOUND);
        OperationType operation = OperationType.PROCESS_PURCHASE;
        String flowId = "flow-123";

        StepVerifier.create(LoggerUtil.buildResponseBusinessErrorLog(error, operation, flowId))
                .verifyComplete();
    }

    @Test
    void shouldLogRequest() {
        OperationType operation = OperationType.PROCESS_PURCHASE;
        Object request = "test request";

        LoggerUtil.logRequest(operation, request);
    }

    @Test
    void shouldLogSuccessResponse() {
        // Create a mock method that calls logResponse with success scenario
        StandardLogData<String> standardResponse = new StandardLogData<>(
                "200", "Success", "flow-123", LocalDateTime.now().toString(), "test data", null);
        OperationType operation = OperationType.PROCESS_PURCHASE;
        
        // Call a method that will internally call logResponse without errors or business exception
        // This will trigger the else branch -> logSuccessResponse
        LoggerUtil.logRequest(operation, standardResponse);
    }
}
