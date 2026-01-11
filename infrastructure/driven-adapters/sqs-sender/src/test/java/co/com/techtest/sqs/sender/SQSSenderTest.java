package co.com.techtest.sqs.sender;

import co.com.techtest.model.purchase.PurchaseParameter;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.sqs.sender.config.SQSSenderProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SQSSenderProperties properties;

    @Mock
    private SqsAsyncClient client;

    @InjectMocks
    private SQSSender sqsSender;

    @Test
    void shouldExecutePurchaseSuccessfully() {
        PurchaseParameter parameter = new PurchaseParameter("flow-123", "user-123", "ticket-123");

        when(properties.queueUrl()).thenReturn("http://localhost:4566/000000000000/test-queue");
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(SendMessageResponse.builder().build()));

        StepVerifier.create(sqsSender.executePurchase(parameter))
                .expectNext(parameter)
                .verifyComplete();
    }

    @Test
    void shouldHandleSqsException() {
        PurchaseParameter parameter = new PurchaseParameter("flow-123", "user-123", "ticket-123");

        when(properties.queueUrl()).thenReturn("http://localhost:4566/000000000000/test-queue");
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(SqsException.builder().build()));

        StepVerifier.create(sqsSender.executePurchase(parameter))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void shouldHandleGenericException() {
        PurchaseParameter parameter = new PurchaseParameter("flow-123", "user-123", "ticket-123");

        when(properties.queueUrl()).thenReturn("http://localhost:4566/000000000000/test-queue");
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Connection failed")));

        StepVerifier.create(sqsSender.executePurchase(parameter))
                .expectError(TechnicalException.class)
                .verify();
    }
}
