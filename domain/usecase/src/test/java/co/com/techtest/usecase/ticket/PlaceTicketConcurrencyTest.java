package co.com.techtest.usecase.ticket;

import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketParameter;
import co.com.techtest.model.ticket.TicketStatusEvent;
import co.com.techtest.model.ticket.gateway.TicketGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import co.com.techtest.model.util.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceTicketConcurrencyTest {

    @Mock
    private TicketGateway ticketGateway;

    private TicketUseCase ticketUseCase;

    @BeforeEach
    void setUp() {
        ticketUseCase = new TicketUseCase(ticketGateway);
    }

    @Test
    void shouldHandleConcurrentTicketPlacement() throws InterruptedException {
        String eventId = "event-123";
        String userId = "user-123";
        int quantity = 1;
        int numberOfThreads = 10;
        int successfulTransactions = 5;

        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        when(ticketGateway.saveTicket(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    if (successCount.get() < successfulTransactions) {
                        successCount.incrementAndGet();
                        return Mono.just(createTicket(eventId, userId, quantity));
                    } else {
                        failureCount.incrementAndGet();
                        return Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INVALID_CAPACITY));
                    }
                });

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    TicketParameter parameter = new TicketParameter("flow-123", userId, eventId, quantity);
                    StepVerifier.create(ticketUseCase.createTicket(parameter))
                            .expectNextCount(1)
                            .verifyComplete();
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(successfulTransactions, successCount.get());
        assertEquals(numberOfThreads - successfulTransactions, failureCount.get());
    }

    @Test
    void shouldPreventOverselling() {
        String eventId = "event-123";
        String userId = "user-123";
        int quantity = 10;

        when(ticketGateway.saveTicket(any(Ticket.class)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessageType.ERROR_MS_INVALID_CAPACITY)));

        TicketParameter parameter = new TicketParameter("flow-123", userId, eventId, quantity);
        StepVerifier.create(ticketUseCase.createTicket(parameter))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void shouldHandleMultipleConcurrentUsersForSameEvent() throws InterruptedException {
        String eventId = "event-123";
        int numberOfUsers = 5;
        int quantity = 2;

        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        AtomicInteger processedRequests = new AtomicInteger(0);

        when(ticketGateway.saveTicket(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    processedRequests.incrementAndGet();
                    Ticket ticket = invocation.getArgument(0);
                    return Mono.just(ticket);
                });

        for (int i = 0; i < numberOfUsers; i++) {
            final String userId = "user-" + i;
            executor.submit(() -> {
                try {
                    TicketParameter parameter = new TicketParameter("flow-123", userId, eventId, quantity);
                    StepVerifier.create(ticketUseCase.createTicket(parameter))
                            .expectNextCount(1)
                            .verifyComplete();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(numberOfUsers, processedRequests.get());
    }

    private Ticket createTicket(String eventId, String userId, int quantity) {
        return new Ticket(
                "ticket-" + System.nanoTime(),
                eventId,
                userId,
                quantity,
                TicketStatus.RESERVED,
                List.of(new TicketStatusEvent(TicketStatus.RESERVED, System.currentTimeMillis())),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
    }
}
