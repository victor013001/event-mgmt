package co.com.techtest.sqs.listener.processor.purchase;

import co.com.techtest.model.util.enums.OperationType;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.usecase.ticket.UpdateTicketUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseProcessorTest {

    @Mock
    private UpdateTicketUseCase updateTicketUseCase;

    private PurchaseProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PurchaseProcessor(updateTicketUseCase);
    }

    @Test
    void shouldProcessPurchaseSuccessfully() {
        Message message = Message.builder()
                .body("{\"ticketId\":\"ticket-123\",\"flowId\":\"flow-123\"}")
                .build();

        when(updateTicketUseCase.updateTicketStatus("ticket-123", TicketStatus.SOLD))
                .thenReturn(Mono.empty());

        Mono<Void> result = processor.execute(message, OperationType.PROCESS_PURCHASE);

        StepVerifier.create(result)
                .verifyComplete();

        verify(updateTicketUseCase).updateTicketStatus("ticket-123", TicketStatus.SOLD);
    }

    @Test
    void shouldHandleBusinessException() {
        Message message = Message.builder()
                .body("{\"ticketId\":\"ticket-123\",\"flowId\":\"flow-123\"}")
                .build();

        BusinessException businessException = new BusinessException(TechnicalMessageType.ERROR_MS_INTERNAL_SERVER);
        when(updateTicketUseCase.updateTicketStatus("ticket-123", TicketStatus.SOLD))
                .thenReturn(Mono.error(businessException));

        Mono<Void> result = processor.execute(message, OperationType.PROCESS_PURCHASE);

        StepVerifier.create(result)
                .verifyComplete();

        verify(updateTicketUseCase).updateTicketStatus("ticket-123", TicketStatus.SOLD);
    }

    @Test
    void shouldHandleInvalidJsonMessage() {
        Message message = Message.builder()
                .body("invalid-json")
                .build();

        Mono<Void> result = processor.execute(message, OperationType.PROCESS_PURCHASE);

        StepVerifier.create(result)
                .expectError(TechnicalException.class)
                .verify();
    }
}
