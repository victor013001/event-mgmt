package co.com.techtest.sqs.listener.handler.purchase;

import co.com.techtest.sqs.listener.processor.purchase.PurchaseProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import static co.com.techtest.model.util.enums.OperationType.PROCESS_PURCHASE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseHandlerTest {

    @Mock
    private PurchaseProcessor purchaseProcessor;

    private PurchaseHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PurchaseHandler(purchaseProcessor);
    }

    @Test
    void shouldHandleMessageSuccessfully() {
        Message message = Message.builder()
                .messageId("msg-123")
                .body("{\"ticketId\":\"ticket-123\"}")
                .build();

        when(purchaseProcessor.execute(any(Message.class), eq(PROCESS_PURCHASE)))
                .thenReturn(Mono.empty());

        Mono<Void> result = handler.handle(message);

        StepVerifier.create(result)
                .verifyComplete();

        verify(purchaseProcessor).execute(message, PROCESS_PURCHASE);
    }

    @Test
    void shouldHandleProcessorError() {
        Message message = Message.builder()
                .messageId("msg-123")
                .body("{\"ticketId\":\"ticket-123\"}")
                .build();

        when(purchaseProcessor.execute(any(Message.class), eq(PROCESS_PURCHASE)))
                .thenReturn(Mono.error(new RuntimeException("Processing error")));

        Mono<Void> result = handler.handle(message);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(purchaseProcessor).execute(message, PROCESS_PURCHASE);
    }
}