package co.com.techtest.scheduler;

import co.com.techtest.usecase.orchestrator.TicketExpirationOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketExpirationSchedulerTest {

    @Mock
    private TicketExpirationOrchestrator orchestrator;

    private TicketExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new TicketExpirationScheduler(orchestrator);
    }

    @Test
    void shouldExpireTicketsSuccessfully() {
        when(orchestrator.expireReservedTickets())
                .thenReturn(Mono.empty());

        scheduler.expireTickets();

        verify(orchestrator).expireReservedTickets();
    }

    @Test
    void shouldHandleOrchestratorError() {
        when(orchestrator.expireReservedTickets())
                .thenReturn(Mono.error(new RuntimeException("Test error")));

        scheduler.expireTickets();

        verify(orchestrator).expireReservedTickets();
    }
}
