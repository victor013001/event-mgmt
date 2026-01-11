package co.com.techtest.sqs.listener;

import co.com.techtest.sqs.listener.handler.purchase.PurchaseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSProcessorTest {

    @Mock
    private PurchaseHandler purchaseHandler;

    private SQSProcessor sqsProcessor;

    @BeforeEach
    void setUp() {
        sqsProcessor = new SQSProcessor(purchaseHandler);
    }

    @Test
    void shouldProcessMessageSuccessfully() {
        Message message = Message.builder()
                .body("{\"ticketId\":\"123\"}")
                .build();

        when(purchaseHandler.handle(any(Message.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(sqsProcessor.apply(message))
                .verifyComplete();

        verify(purchaseHandler).handle(any(Message.class));
    }

    @Test
    void shouldHandleErrorFromPurchaseHandler() {
        Message message = Message.builder()
                .body("{\"ticketId\":\"123\"}")
                .build();

        when(purchaseHandler.handle(any(Message.class)))
                .thenReturn(Mono.error(new RuntimeException("Handler error")));

        StepVerifier.create(sqsProcessor.apply(message))
                .expectError(RuntimeException.class)
                .verify();

        verify(purchaseHandler).handle(any(Message.class));
    }
}